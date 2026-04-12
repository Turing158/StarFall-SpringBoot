package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NacosStatusDTO {
    private NacosConnectionStatus connectionStatus;
    private ConfigurationInfo configurationInfo;
    private ServiceDiscoveryInfo serviceDiscoveryInfo;
    private String timestamp;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NacosConnectionStatus {
        private boolean configServerConnected;
        private boolean discoveryServerConnected;
        private String configServerAddr;
        private String discoveryServerAddr;
        private String namespace;
        private String group;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfigurationInfo {
        private String group;
        private Map<String, String> allConfigs; // 所有配置（包括本地和 Nacos）的原始内容
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ServiceDiscoveryInfo {
        private List<String> allServiceNames;// 所有服务名
        private Map<String, List<ServiceInstanceDetail>> serviceDetails; // 每个服务的实例详情
        private int totalServiceCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ServiceInstanceDetail {
        private String instanceId;
        private String ip;
        private int port;
        private boolean healthy;
        private boolean enabled;
        private double weight;
        private Map<String, String> metadata;
        private String serviceName;
    }
}
