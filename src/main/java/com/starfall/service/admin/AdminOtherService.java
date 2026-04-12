package com.starfall.service.admin;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starfall.entity.MinioStatusDTO;
import com.starfall.entity.NacosStatusDTO;
import com.starfall.entity.RedisStatusDTO;
import com.starfall.util.RedisUtil;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminOtherService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private NacosConfigManager nacosConfigManager;
    @Autowired(required = false)
    private NacosDiscoveryProperties discoveryProperties;
    @Autowired
    private Environment environment;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Value("${spring.cloud.nacos.config.server-addr:127.0.0.1:8848}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.config.username:}")
    private String username;

    @Value("${spring.cloud.nacos.config.password:}")
    private String password;

    public RedisStatusDTO findRedisStatus() {
        RedisStatusDTO redisStatusDTO = new RedisStatusDTO();

        try {
            String pong = (String) redisTemplate.execute(
                    (RedisCallback<String>) RedisConnectionCommands::ping
            );
            redisStatusDTO.setPing(pong);
            redisStatusDTO.setConnected("PONG".equals(pong));
            // 获取服务器信息
            Properties serverInfo = (Properties) redisTemplate.execute(
                    (RedisCallback<Object>) connection -> connection.info("server")
            );
            redisStatusDTO.setRedisVersion(serverInfo.getProperty("redis_version"));
            redisStatusDTO.setOs(serverInfo.getProperty("os"));
            redisStatusDTO.setUptimeInDays(serverInfo.getProperty("uptime_in_days"));

            // 获取内存信息
            Properties memoryInfo = (Properties) redisTemplate.execute(
                    (RedisCallback<Object>) connection -> connection.info("memory")
            );

            redisStatusDTO.setUsedMemoryHuman(memoryInfo.getProperty("used_memory_human"));
            redisStatusDTO.setTotalKeys(Math.toIntExact(stringRedisTemplate.execute(RedisConnection::dbSize)));

            // 获取统计信息
            Properties statsInfo = (Properties) redisTemplate.execute(
                    (RedisCallback<Object>) connection -> connection.info("stats")
            );

            redisStatusDTO.setTotalConnectionsReceived(statsInfo.getProperty("total_connections_received"));
            redisStatusDTO.setTotalCommandsProcessed(statsInfo.getProperty("total_commands_processed"));
            redisStatusDTO.setInstantaneousOpsPerSec(statsInfo.getProperty("instantaneous_ops_per_sec"));

            // 获取客户端信息
            Properties clientsInfo = (Properties) redisTemplate.execute(
                    (RedisCallback<Object>) connection -> connection.info("clients")
            );

            redisStatusDTO.setConnectedClients(clientsInfo.getProperty("connected_clients"));
            redisStatusDTO.setBlockedClients(clientsInfo.getProperty("blocked_clients"));

        } catch (Exception e) {
            redisStatusDTO.setConnected(false);
            redisStatusDTO.setError(e.getMessage());
        }

        return redisStatusDTO;
    }

    public NacosStatusDTO findNacosFullStatus() {
        NacosStatusDTO dto = new NacosStatusDTO();
        dto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // 1. 连接状态
        dto.setConnectionStatus(findNacosConnectionStatus());

        // 2. 配置信息
        dto.setConfigurationInfo(findNacosConfigurationInfo());

        // 3. 服务发现信息
        dto.setServiceDiscoveryInfo(findNacosServiceDiscoveryInfo());

        return dto;
    }

    private NacosStatusDTO.NacosConnectionStatus findNacosConnectionStatus() {
        NacosStatusDTO.NacosConnectionStatus status = new NacosStatusDTO.NacosConnectionStatus();
        boolean configOk = false;
        boolean discoveryOk = false;

        if (nacosConfigManager != null) {
            try {
                ConfigService configService = nacosConfigManager.getConfigService();
                // 尝试获取一个不存在的配置来测试连通性（不会抛异常，但可以判断服务是否可用）
                configService.getConfig("test-connection", "DEFAULT_GROUP", 1000);
                configOk = true;
            } catch (NacosException e) {
                configOk = false;
            }
            status.setConfigServerAddr(nacosConfigManager.getNacosConfigProperties().getServerAddr());
        }

        if (discoveryProperties != null) {
            try {
                NamingService namingService = discoveryProperties.namingServiceInstance();
                namingService.getServicesOfServer(1, 1);
                discoveryOk = true;
            } catch (NacosException e) {
                discoveryOk = false;
            }
            status.setDiscoveryServerAddr(discoveryProperties.getServerAddr());
            status.setNamespace(discoveryProperties.getNamespace());
            status.setGroup(discoveryProperties.getGroup());
        }

        status.setConfigServerConnected(configOk);
        status.setDiscoveryServerConnected(discoveryOk);
        return status;
    }

    private NacosStatusDTO.ConfigurationInfo findNacosConfigurationInfo() {
        NacosStatusDTO.ConfigurationInfo configInfo = new NacosStatusDTO.ConfigurationInfo();

        String group = environment.getProperty("spring.cloud.nacos.config.group", "DEFAULT_GROUP");
        configInfo.setGroup(group);

        Map<String, String> allRawConfigs = getAllConfigsInGroup(group);
        configInfo.setAllConfigs(allRawConfigs);
        return configInfo;
    }

    private Map<String, String> getAllConfigsInGroup(String group) {
        Map<String, String> result = new HashMap<>();
        List<String> dataIds = listDataIdsByGroup(group);
        for (String dataId : dataIds) {
            String content = getConfigContent(dataId, group);
            if (content != null) {
                result.put(dataId, content);
            }
        }
        return result;
    }
    private List<String> listDataIdsByGroup(String group) {
        List<String> dataIds = new ArrayList<>();
        int pageNo = 1;
        int pageSize = 100;
        boolean hasMore = true;

        while (hasMore) {
            String url = UriComponentsBuilder.fromHttpUrl(nacosServerAddr.startsWith("http") ? nacosServerAddr : "http://" + nacosServerAddr)
                    .path("/nacos/v1/cs/configs")
                    .queryParam("dataId", "")
                    .queryParam("group", group)
                    .queryParam("pageNo", pageNo)
                    .queryParam("pageSize", pageSize)
                    .queryParam("search", "accurate")
                    .build(false)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            if (username != null && !username.isEmpty()) {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                headers.set("Authorization", authHeader);
            }

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode pageItems = root.get("pageItems");
                    if (pageItems != null && pageItems.isArray()) {
                        for (JsonNode item : pageItems) {
                            String dataId = item.get("dataId").asText();
                            dataIds.add(dataId);
                        }
                    }
                    int totalCount = root.get("totalCount").asInt();
                    hasMore = (pageNo * pageSize) < totalCount;
                    pageNo++;
                } catch (Exception e) {
                    throw new RuntimeException("解析配置列表失败", e);
                }
            } else {
                throw new RuntimeException("获取配置列表失败，HTTP状态码: " + response.getStatusCode());
            }
        }
        return dataIds;
    }

    private String getConfigContent(String dataId, String group) {
        String url = UriComponentsBuilder.fromHttpUrl(nacosServerAddr.startsWith("http") ? nacosServerAddr : "http://" + nacosServerAddr)
                .path("/nacos/v1/cs/configs")
                .queryParam("dataId", dataId)
                .queryParam("group", group)
                .build(false)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        if (username != null && !username.isEmpty()) {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
        }

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    private NacosStatusDTO.ServiceDiscoveryInfo findNacosServiceDiscoveryInfo() {
        NacosStatusDTO.ServiceDiscoveryInfo discoveryInfo = new NacosStatusDTO.ServiceDiscoveryInfo();

        if (discoveryProperties == null) {
            discoveryInfo.setAllServiceNames(Collections.emptyList());
            discoveryInfo.setServiceDetails(Collections.emptyMap());
            discoveryInfo.setTotalServiceCount(0);
            return discoveryInfo;
        }

        try {
            NamingService namingService = discoveryProperties.namingServiceInstance();
            // 获取所有服务名（分页，一次性获取足够多）
            ListView<String> services = namingService.getServicesOfServer(1, Integer.MAX_VALUE);
            List<String> serviceNames = services.getData();
            discoveryInfo.setAllServiceNames(serviceNames);
            discoveryInfo.setTotalServiceCount(serviceNames.size());

            // 获取每个服务的实例详情
            Map<String, List<NacosStatusDTO.ServiceInstanceDetail>> detailsMap = new HashMap<>();
            for (String serviceName : serviceNames) {
                List<Instance> instances = namingService.getAllInstances(serviceName);
                List<NacosStatusDTO.ServiceInstanceDetail> instanceDetails = instances.stream()
                        .map(this::convertInstance)
                        .collect(Collectors.toList());
                detailsMap.put(serviceName, instanceDetails);
            }
            discoveryInfo.setServiceDetails(detailsMap);

        } catch (NacosException e) {
            discoveryInfo.setAllServiceNames(Collections.emptyList());
            discoveryInfo.setServiceDetails(Collections.emptyMap());
            discoveryInfo.setTotalServiceCount(0);
        }
        return discoveryInfo;
    }

    private NacosStatusDTO.ServiceInstanceDetail convertInstance(Instance instance) {
        NacosStatusDTO.ServiceInstanceDetail detail = new NacosStatusDTO.ServiceInstanceDetail();
        detail.setInstanceId(instance.getInstanceId());
        detail.setIp(instance.getIp());
        detail.setPort(instance.getPort());
        detail.setHealthy(instance.isHealthy());
        detail.setEnabled(instance.isEnabled());
        detail.setWeight(instance.getWeight());
        detail.setMetadata(instance.getMetadata());
        detail.setServiceName(instance.getServiceName());
        return detail;
    }

    public MinioStatusDTO findMinioFullStatus() {
        MinioStatusDTO status = new MinioStatusDTO();
        status.setTimestamp(ZonedDateTime.now().toString());
        try {
            // 1. 测试连接
            minioClient.listBuckets(); // 若抛出异常则连接失败
            status.setConnected(true);

        } catch (Exception e) {
            status.setConnected(false);
            status.setErrorMessage(e.getMessage());
        }
        return status;
    }

    public MinioStatusDTO.ObjectPageResult listObjectsByPage(String bucketName, String startAfter,int page, int pageSize) {
        List<MinioStatusDTO.ObjectInfo> objects = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .startAfter(startAfter)
                        .maxKeys(pageSize)          // 单次请求最大数量，但迭代器可能仍会继续
                        .recursive(true)
                        .build()
        );

        String nextMarker = null;
        int totalCount = 0;

        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                MinioStatusDTO.ObjectInfo obj = convertItem(item, bucketName);
                objects.add(obj);
                nextMarker = item.objectName();
            } catch (Exception e) {
                log.error("遍历对象失败: {}", e.getMessage());
            }
            totalCount++;
        }
        List<MinioStatusDTO.ObjectInfo> objectsResult = redisUtil.paginateByPageNum(objects, page, pageSize);
        MinioStatusDTO.ObjectPageResult pageResult = new MinioStatusDTO.ObjectPageResult();
        pageResult.setObjects(objectsResult);
        pageResult.setNextStartAfter(nextMarker);
        // 若取到的数量等于 pageSize 且 nextMarker 不为空，则认为有下一页
        pageResult.setHasNext(objectsResult.size() == pageSize && nextMarker != null);
        pageResult.setTotalCount(totalCount);
        return pageResult;
    }

    private MinioStatusDTO.ObjectInfo convertItem(Item item, String bucketName) {
        MinioStatusDTO.ObjectInfo objInfo = new MinioStatusDTO.ObjectInfo();
        objInfo.setName(item.objectName());
        objInfo.setSize(item.size());
        objInfo.setLastModified(item.lastModified());
        objInfo.setEtag(item.etag());

        // 如果需要获取更详细的元数据（如 Content-Type、用户自定义元数据），可以额外调用 statObject
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(item.objectName())
                            .build()
            );
            objInfo.setContentType(stat.contentType());
            objInfo.setUserMetadata(stat.userMetadata());
        } catch (Exception e) {
            // 获取元数据失败时，可设置默认值或忽略
            objInfo.setContentType("application/octet-stream");
            objInfo.setUserMetadata(Collections.emptyMap());
        }

        return objInfo;
    }
}
