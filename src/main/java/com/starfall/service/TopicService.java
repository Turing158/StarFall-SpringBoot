package com.starfall.service;

import cn.hutool.dfa.SensitiveUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.starfall.Exception.ServiceException;
import com.starfall.annotation.RequireRole;
import com.starfall.dao.TopicDao;
import com.starfall.dao.UserDao;
import com.starfall.dao.redis.UserRedis;
import com.starfall.entity.*;
import com.starfall.dao.redis.TopicRedis;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class TopicService {
    @Autowired
    TopicDao topicDao;
    @Autowired
    UserDao userDao;
    @Autowired
    TopicRedis topicRedis;
    @Autowired
    UserRedis userRedis;
    @Autowired
    FileService fileService;
    @Autowired
    SearchService searchService;
    @Autowired
    UserInteractionService userInteractionService;
    @Autowired
    UserNoticeService userNoticeService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CodeUtil codeUtil;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    ClamAvUtil clamAvUtil;

    private final int topicSize = 30;

    public Pair<List<Topic>,Integer> findAllTopic(int page,String label,String version,String belong){
        if (page <= 0){
            page = 1;
        }
        if((label.isBlank() || label.equals("全部")) && (version.isBlank() || version.equals("全部"))){
            label = null;
            version = null;
        }
        else if(label.isBlank() || label.equals("全部")){
            label = null;
        }
        else if(version.isBlank() || version.equals("全部")){
            version = null;
        }
        return topicRedis.getRedisTopics(belong,label,version,page);
    }


    public TopicOut getTopicInfo(String token,String id){
        TopicOut topic = topicRedis.getRedisTopicOut(id);
        if(topic == null){
            throw new ServiceException("ID_ERROR","主题:"+id+"不存在！");
        }
        //为了获取最大经验值
        topic.setMaxExp(Exp.getMaxExp(topic.getLevel()));
        if (topic.getDisplay() != 1){
            Claims claims = jwtUtil.parseToken(token);
            String user = (String) claims.get("USER");
            String role = (String) claims.get("ROLE");
//            检测用户是否有权限查看该主题，逻辑为
//            如果 用户是该主题发布者 或者
//            主题被待整改 需要 管理权限
//            或 （主题为资源主题时 需要 资源版主权限）
//            或 （主题为有话说主题时 需要 有话说版主权限）
//            则可以查看该主题
            if (!(topic.getUser().equals(user)
                    || (topic.getDisplay() == -1 &&
                    (role.equals("admin")
                            || (topic.getBelong().equals("resource") && role.equals("resource_moderator"))
                            || (topic.getBelong().equals("talk") && role.equals("talk_moderator"))
                    )))){
                throw new ServiceException("NOT_PUBLIC","主题:"+id+"不是公开主题！");
            }

        }
        else{
            if(token != null){
                int oldView = topic.getView();
                topicDao.updateTopicView(oldView+1,id);
                topic.setView(oldView+1);
            }
        }
        return topic;
    }


    public Pair<List<Topic>,Integer> findAllTopicByUser(int page,String user,String token){
        if(token == null){
            return Pair.of(
                    topicRedis.getRedisUserTopics(user,page,false),
                    topicRedis.getRedisUserTopicsCount(user,false)
            );
        }
        String fromUser = jwtUtil.getTokenField(token,"USER");

        return Pair.of(
                topicRedis.getRedisUserTopics(user,page,fromUser.equals(user)),
                topicRedis.getRedisUserTopicsCount(user,fromUser.equals(user))
        );
    }

    public ResultMsg getLike(String topicId,String token) {
        String user = jwtUtil.getTokenField(token,"USER");
        LikeLog like = topicRedis.getRedisLikeLog(topicId,user);
        if(like != null && like.getStatus() == 1){
            return ResultMsg.warning("IS_LIKE",topicRedis.getRedisLikeCount(topicId));
        }
        else if(like != null && like.getStatus() == 2){
            return ResultMsg.warning("IS_DISLIKE");
        }
        return ResultMsg.error("NOT_LIKE");
    }

    public ResultMsg like(String topicId,String token,int like){
        String user = jwtUtil.getTokenField(token,"USER");
        LocalDateTime ldt = LocalDateTime.now();
        String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        LikeLog likeObj = topicRedis.getRedisLikeLog(topicId,user);
        if(likeObj != null){
            if(likeObj.getStatus() != like){
                topicRedis.setRedisLike(topicId,user,likeObj,like,date);
                topicDao.updateLikeStateByTopicAndUser(topicId,user,like,date);
                topicRedis.setRedisLikeCount(topicId,like == 1);
                return like == 1
                        ? ResultMsg.warning("UPDATE_LIKE",topicRedis.getRedisLikeCount(topicId))
                        : ResultMsg.warning("UPDATE_LIKE");
            }
            topicRedis.setRedisLike(topicId,user,likeObj,0,date);
            topicRedis.setRedisLikeCount(topicId,false);
            topicDao.updateLikeStateByTopicAndUser(topicId,user,0,date);
            return ResultMsg.warning("ALREADY_LIKE");
        }
        topicRedis.setRedisLikeCount(topicId,like == 1);
        likeObj = new LikeLog(topicId,user,like,date);
        topicRedis.setRedisLike(topicId,user,likeObj,like,date);
        topicDao.insertLike(likeObj);
        return like == 1
                ? ResultMsg.warning("LIKE_SUCCESS",topicRedis.getRedisLikeCount(topicId))
                : ResultMsg.warning("LIKE_SUCCESS");
    }


    public ResultMsg findCommentByTopicId(String id,int page){
        List<CommentVO> list = topicRedis.getRedisComments(id,page);
        return ResultMsg.success(list,topicRedis.getRedisCommentsCount(id));
    }

    public ResultMsg appendComment( String topicId, String token, String content, String code){
        String user = jwtUtil.getTokenField(token,"USER");
        if(codeUtil.checkCode(code)){
            LocalDateTime ldt = LocalDateTime.now();
            String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
            var sennsitiveList = SensitiveUtil.getFoundAllSensitive(content);
            if(!sennsitiveList.isEmpty()){
                throw new ServiceException("SENSITIVE_ERROR","包含敏感词"+sennsitiveList);
            }
            int count = topicRedis.getRedisCommentsCount(topicId);
            topicDao.insertComment(topicId,user,date,content,0);
            topicRedis.setRedisCommentsCount(topicId,true);
            topicRedis.updateHomeFirstTopic(topicRedis.getRedisTopicOut(topicId).parseTopic(),"topic","firstCommentTopic");
            count++;
            topicRedis.setRedisComments(new CommentVO(topicId,user,date,content,0),true,count);
            topicDao.updateTopicComment(count,date,topicId);
            if(redisUtil.hasKey("topic:cache:"+topicId)){
                TopicOut topic = redisUtil.get("topic:cache:"+topicId,TopicOut.class);
                topic.setComment(count);
                redisUtil.set("topic:cache:"+topicId,topic);
            }
            return ResultMsg.success(count);
        }
        return ResultMsg.error("CODE_ERROR");
    }


    public ResultMsg deleteComment(String id,String token,String date){
        String user = jwtUtil.getTokenField(token,"USER");
        int count = topicRedis.getRedisCommentsCount(id);
        int status1 = topicDao.deleteComment(id,user,date);
        topicRedis.setRedisCommentsCount(id,false);
        count--;
        topicRedis.setRedisComments(new CommentVO(id,user,date,null,0),false,count);
        if(redisUtil.hasKey("topic:cache",id)){
            TopicOut topic = redisUtil.get(redisUtil.joinKey("topic:cache",id), TopicOut.class);
            topic.setComment(count);
            redisUtil.set(redisUtil.joinKey("topic:cache",id), topic);
        }
        int status2 = topicDao.updateTopicComment(count,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"),id);
        return status1+status2 == 2 ? ResultMsg.success(count) : ResultMsg.error("DELETE_ERROR");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultMsg appendTopic(String token, TopicDTO topicDTO){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        int level = userObj.getLevel();
        if(level < 5){
            return ResultMsg.error("LEVEL_ERROR");
        }
        if(topicDao.countTopicTotalByDateAndUser(user) >= 10){
            return ResultMsg.error("APPEND_DAY_MAX_ERROR");
        }
        if(codeUtil.checkCode(topicDTO.getCode())){
            ContentUtil.checkContentSensitive(topicDTO);
            LocalDateTime ldt = LocalDateTime.now();
            String id = dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS")+ CodeUtil.getCode(6);
            String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
            //清除版本号问题
            if(topicDTO.getVersion() == null || topicDTO.getVersion().isEmpty() || topicDTO.getVersion().isBlank()){
                topicDTO.setVersion("");
            }
            if (topicDTO.getDisplay() != 0 && topicDTO.getDisplay() != 1){
                topicDTO.setDisplay(0);
            }
            Topic topic = new Topic(
                    id,
                    topicDTO.getTitle(),
                    topicDTO.getLabel(),
                    user,
                    userObj.getName(),
                    userObj.getAvatar(),
                    date,
                    0,
                    0,
                    topicDTO.getVersion(),
                    dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"),
                    topicDTO.getDisplay(),
                    topicDTO.getBelong(),
                    topicDTO.getDisplay() == 1 ? 1 : 0
            );
            topicDao.insertTopic(topic);
            String filename = id+".md";
            String fileFolder = "user/"+user+"/topic/"+id;
            MultipartFile file = new MultipartFileImpl(topicDTO.getContent(),filename);
            try{
                fileService.upload(file,fileFolder,filename);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new ServiceException("UPLOAD_ERROR","上传文件失败");
            }
            topicRedis.deleteRedisTopics(topic.getBelong(),topic.getLabel(),topic.getVersion());
            topicRedis.setRedisUserTopicCount(user,true,topic);
            topicRedis.setRedisUserTopic(user);
            TopicItem topicItem = new TopicItem(
                    id,
                    topicDTO.getTopicTitle(),
                    topicDTO.getEnTitle(),
                    topicDTO.getSource(),
                    topicDTO.getAuthor(),
                    topicDTO.getLanguage(),
                    topicDTO.getAddress(),
                    topicDTO.getDownload(),
                    fileFolder + "/" + filename
            );
            topicDao.insertTopicItem(topicItem);
            int addExp = 0;
            if(topicDTO.getDisplay() == 1){
                topicRedis.updateHomeFirstTopic(topic,"topic","firstRefreshTopic");
                topicRedis.updateHomeFirstTopic(topic,"topic","firstPublicTopic");
                addExp = addExpFunc(token,100,200,userObj);
            }
            topicRedis.setRedisTopicOut(new TopicOut(topic,topicItem,userObj,userRedis.findRedisUserPersonalized(user)));
            searchService.saveTopic(
                    new Search(
                            id,
                            topicDTO.getTitle(),
                            topicDTO.getLabel(),
                            topicDTO.getBelong(),
                            topicDTO.getTopicTitle(),
                            topicDTO.getEnTitle(),
                            ContentUtil.parseContent(topicDTO.getContent()),
                            LocalDate.parse(date.split(" ")[0]),
                            ldt,
                            user,
                            userObj.getName(),
                            topicDTO.getDisplay()
                    )
            );
            return ResultMsg.success(id,addExp);
        }
        return ResultMsg.error("CODE_ERROR");
    }

    public ResultMsg isPromiseToEditTopic(String token,String id){
        String user = jwtUtil.getTokenField(token,"USER");
        TopicOut topic = topicRedis.getRedisTopicOut(id);
        if(topic == null){
            return ResultMsg.error("NULL_ERROR");
        }
        if(user.equals(topic.getUser())){
            return ResultMsg.success();
        }
        return ResultMsg.error("REJECT");
    }


    public ResultMsg findTopicInfoToEdit(String token,String id){
        ResultMsg r = isPromiseToEditTopic(token,id);
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        TopicOut topicOut = topicRedis.getRedisTopicOut(id);
        //为了获取最大经验值
        topicOut.setMaxExp(Exp.getMaxExp(topicOut.getLevel()));
        if(topicOut == null){
            return ResultMsg.error("NULL_ERROR");
        }
        return ResultMsg.success(topicOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultMsg updateTopic(String token, TopicDTO topicDTO){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        ResultMsg r = isPromiseToEditTopic(token, topicDTO.getId());
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        if(codeUtil.checkCode(topicDTO.getCode())){
            TopicOut data = topicRedis.getRedisTopicOut(topicDTO.getId());
            if (data == null){
                return ResultMsg.error("NULL_ERROR");
            }
            ContentUtil.checkContentSensitive(topicDTO);
            LocalDateTime ldt = LocalDateTime.now();
            String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
            TopicOut topicOut = topicRedis.getRedisTopicOut(topicDTO.getId());
            int isFirstPublic = 1;
            boolean isPublicExp = false;
            if(topicOut.getDisplay() != -1){
                if (topicDTO.getDisplay() != 0 && topicDTO.getDisplay() != 1){
                    topicDTO.setDisplay(0);
                }
                if (topicOut.getIsFirstPublic() == 0){
                    if(topicDTO.getDisplay() == 0){
                        isFirstPublic = 0;
                    }
                    else{
                        isPublicExp = true;
                    }
                }
            }
            else{
                topicDTO.setDisplay(-1);
            }
            Topic topic = new Topic(
                    topicDTO.getId(),
                    topicDTO.getTitle(),
                    topicDTO.getLabel(),
                    user,
                    userObj.getName(),
                    userObj.getAvatar(),
                    isPublicExp ? date : topicOut.getDate(),
                    topicOut.getView(),
                    topicOut.getComment(),
                    topicDTO.getVersion(),
                    date,
                    topicDTO.getDisplay(),
                    topicDTO.getBelong(),
                    isFirstPublic
            );
            String filename = topicDTO.getId()+".md";
            String fileFolder = "user/"+user+"/topic/"+ topicDTO.getId();
            MultipartFile file = new MultipartFileImpl(topicDTO.getContent(),filename);
            try{
                fileService.upload(file,fileFolder,filename);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("UPLOAD_ERROR");
            }
            TopicItem topicItem = new TopicItem(
                    topicDTO.getId(),
                    topicDTO.getTopicTitle(),
                    topicDTO.getEnTitle(),
                    topicDTO.getSource(),
                    topicDTO.getAuthor(),
                    topicDTO.getLanguage(),
                    topicDTO.getAddress(),
                    topicDTO.getDownload(),
                    fileFolder + "/" + filename
            );
            int status1 = topicDao.updateTopicExpectCommentAndView(topic);
            if(topicDTO.getDisplay() == 1){
                topicRedis.updateHomeFirstTopic(topic,"topic","firstRefreshTopic");
            }
            int addExp = 0;
            if(isPublicExp){
                addExp = addExpFunc(token,100,200,userObj);
                topicRedis.updateHomeFirstTopic(topic,"topic","firstPublicTopic");
            }
            int status2 = topicDao.updateTopicItem(topicItem);
            if (data.getDisplay() != -1){
                topicDao.updateTopicDisplay(topicDTO.getDisplay(), topicDTO.getId());
            }
            var topicOutUpdated = new TopicOut(topic,topicItem,userObj,userRedis.findRedisUserPersonalized(user));
//            优先清除旧的缓存，避免更新后用户访问到旧数据
            topicRedis.deleteRedisTopics(data.getBelong(),data.getLabel(),data.getVersion());
            topicRedis.setRedisUserTopicCount(user,true,null);
            topicRedis.setRedisUserTopic(user);
//            根据标签和版本号是否改变来决定是否清除相关列表缓存，减少不必要的缓存清除
            if(!data.getLabel().equals(topicDTO.getLabel()) || !data.getVersion().equals(topicDTO.getVersion())){
                topicRedis.deleteRedisTopics(topic.getBelong(),topic.getLabel(),topic.getVersion());
            }
            topicRedis.setRedisTopicOut(topicOutUpdated);
            searchService.saveTopic(new Search(
                    topicDTO.getId(),
                    topicDTO.getTitle(),
                    topicDTO.getLabel(),
                    topicDTO.getBelong(),
                    topicDTO.getTopicTitle(),
                    topicDTO.getEnTitle(),
                    ContentUtil.parseContent(topicDTO.getContent()),
                    LocalDate.parse(topic.getDate().split(" ")[0]),
                    ldt,
                    userObj.getUser(),
                    userObj.getName(),
                    topic.getDisplay()
            ));
            return (status1+status2) == 2 ? ResultMsg.success(topicDTO.getId(),addExp) : ResultMsg.error("UPDATE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultMsg deleteTopic(String token,String id) {
        ResultMsg r = isPromiseToEditTopic(token, id);
        if (r.getMsg().equals("REJECT")) {
            return r;
        }
        String user = jwtUtil.getTokenField(token,"USER");
        TopicOut topicOut = topicRedis.getRedisTopicOut(id);
        int status1 = topicDao.deleteTopicItem(id);
        int status2 = topicDao.deleteTopic(id);
        topicDao.deleteTopicGalleryById(id);
        topicDao.deleteCommentByTopicId(id);
//        使用通配符删除缓存
        redisUtil.deleteBatch(redisUtil.joinKey("topic:cache",id,"*"));
        topicRedis.deleteRedisTopics(topicOut.getBelong(),topicOut.getLabel(),topicOut.getVersion());
        Topic topic = topicOut.parseTopic();
        topicRedis.setRedisUserTopicCount(user,false,topic);
        topicRedis.setRedisUserTopic(user);
        topicDao.deleteLikeLog(id);
        fileService.removeFolder("user/"+user+"/topic/"+id);
        searchService.deleteById(id);
        return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DELETE_ERROR");
    }

    @Autowired
    ElasticsearchOperations elasticsearch;

    public ResultMsg searchTopic(String key,String classification,int page){
        var sensitiveWords = SensitiveUtil.getFoundAllSensitive(key);
        if(!sensitiveWords.isEmpty()){
            throw new RuntimeException("SENSITIVE_ERROR: " + sensitiveWords);
        }
        if(page <= 0){
            page = 1;
        }
        String[] fields = getSearchFields(classification);
        List<HighlightField> highlightFields = new ArrayList<>();
        for (String field : fields) {
            highlightFields.add(new HighlightField(field));
        }

        HighlightParameters parameters = HighlightParameters.builder()
                .withPreTags("<hk>")
                .withPostTags("</hk>")
                .build();

        // 创建Highlight对象
        Highlight highlight = new Highlight(parameters, highlightFields);
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    b.must(m -> m.term(t -> t.field("status").value(1)));
                    BoolQuery.Builder searchBool = new BoolQuery.Builder();
                    for (String field : fields) {
                        searchBool.should(s -> s.wildcard(w ->
                                w.field(field).value("*" + key + "*")
                        ));

                        searchBool.should(s -> s.match(m ->
                                m.field(field).query(key)
                        ));
                    }
                    b.should(s -> s.bool(searchBool.build()._toQuery().bool()));
                    b.minimumShouldMatch("1");
                    return b;
                }))
                .withSort(Sort.by(Sort.Direction.DESC, "refreshTime"))
                .withPageable(PageRequest.of(page-1, 10))
                .withHighlightQuery(new HighlightQuery(highlight,Search.class))
                .build();

        SearchHits<Search> searchHits = elasticsearch.search(query, Search.class);

        List<Search> result = searchHits.get().map(hit -> {
            Search content = hit.getContent();
            Map<String, List<String>> highlightResult = hit.getHighlightFields();
            boolean parseContent = true;
            // 如果有高亮字段，用高亮内容替换原始内容
            if (highlightResult != null && !highlightResult.isEmpty()) {
                for (String field : fields) {
                    List<String> highlights = highlightResult.get(field);
                    if (highlights != null && !highlights.isEmpty()) {
                        if(field.equals("content")){
                            parseContent = false;
                        }
                        String truncatedText = ContentUtil.truncateTextAroundKeyword(highlights.get(0), key, 200);
                        ContentUtil.setFieldByReflection(content, field, truncatedText);

                    }
                }
            }
            if(parseContent){
                content.setContent(ContentUtil.truncateTextAroundKeyword(content.getContent(),null,200));
            }
            return content;
        }).toList();
        return ResultMsg.success(result, (int) searchHits.getTotalHits());
    }

    @Transactional
    public void adjustTopicDisplay(String id,String reason,int display,String token){
        adjustTopicDisplay(id,reason,display,token,null);
    }

    @Transactional
    @RequireRole({"admin","resource_moderator","talk_moderator"})
    public void adjustTopicDisplay(String id,String reason,int display,String token,String noticeId){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        if(userObj == null){
            throw new ServiceException("NO_EXIST_USER","用户不存在");
        }
        TopicOut topic = topicRedis.getRedisTopicOut(id);
        if(topic == null){
            throw new ServiceException("NO_EXIST_TOPIC","主题不存在");
        }
        if (!(display == -1 || display == 1) ||
                !(userObj.getRole().equals("admin")
                || (topic.getBelong().equals("resource") && userObj.getRole().equals("resource_moderator"))
                || (topic.getBelong().equals("talk") && userObj.getRole().equals("talk_moderator"))
                )){
            throw new ServiceException("NO_PERMISSION","没有权限执行此操作");
        }
        if(noticeId == null && topic.getDisplay() == display){
            throw new ServiceException("DONT_DO","不允许这样操作主题");
        }
        var notice = userNoticeService.findUserNoticeById(noticeId);
        if(topic.getDisplay() == -1 && notice != null){
            LocalDateTime ldt = LocalDateTime.parse(notice.getCreateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //判断5分钟之后，才能再次对主题进行审核，防止审核人员恶意反复审核
            if(ldt.plusMinutes(5).isAfter(LocalDateTime.now())){
                throw new ServiceException("NOT_AFTER_5_MIN","请仔细审核，5分钟内不能再次审核该帖子");
            }
        }
        if(topic.getDisplay() != display){
            TopicOut topicOut = topicRedis.getRedisTopicOut(id);
            topicDao.updateTopicDisplay(display,id);
            topicOut.setDisplay(display);
            topicRedis.setRedisTopicOut(topicOut);
            Search search = new Search();
            search.setId(id);
            search.setStatus(display);
            search.setRefreshTime(LocalDateTime.now());
            searchService.saveTopic(search);
        }
        if(noticeId != null){
            userNoticeService.updateTopicNoticeActionHandle(notice,user);
        }
        TopicNoticeAction topicNoticeAction = new TopicNoticeAction(id,topic.getTitle(),display,user,reason,false);
        userNoticeService.insertNotice(topic.getUser(),UserNoticeType.topic,
                "帖子状态有更新",JsonOperate.toJson(topicNoticeAction,false));
    }

    @Transactional
    public void topicRectificationComplete(String noticeId,String topicId,String token){
        log.info("topicRectificationComplete({},{})",noticeId,topicId);
        String user = jwtUtil.getTokenField(token,"USER");
        TopicOut topic = topicRedis.getRedisTopicOut(topicId);
        if(topic == null){
            throw new ServiceException("NO_EXIST_TOPIC","帖子不存在");
        }
        if(!topic.getUser().equals(user)){
            throw new ServiceException("NO_PERMISSION","没有权限执行此操作");
        }
        LocalDateTime refreshLDT = LocalDateTime.parse(topic.getRefresh(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if(refreshLDT.plusDays(1).isBefore(LocalDateTime.now())){
            throw new ServiceException("NOT_AFTER_1_DAY","帖子超过1天未编辑，若已整改编辑，请在完成编辑后1天内并完成整改");
        }
        var notice = userNoticeService.findUserNoticeById(noticeId);
        LocalDateTime noticeLDT = LocalDateTime.parse(notice.getCreateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if(noticeLDT.plusMinutes(5).isAfter(LocalDateTime.now())){
            throw new ServiceException("NOT_BEFORE_5_MIN","请修改帖子后，5分钟后再完成整改");

        }
        userNoticeService.updateTopicNoticeActionHandle(notice,user);

        if(notice == null || !notice.getUser().equals(user)){
            throw new ServiceException("NO_EXIST_NOTICE","通知不存在");
        }
        var action = JsonOperate.toObject(notice.getAction(), TopicNoticeAction.class);
        if(action.isHandle()){
            throw new ServiceException("ALREADY_HANDLE","已处理过该通知");
        }
        var newAction = new TopicNoticeAction(topic.getId(),topic.getTitle(),0,null,null,false);
        userNoticeService.insertNotice(action.getOperator(),UserNoticeType.topic,"主题帖已修改通知",JsonOperate.toJson(newAction,false));
    }

    public ResultMsg findCollectStatus(String id,String token){
        String user = jwtUtil.getTokenField(token,"USER");
        if (topicRedis.getRedisCollection(user,id) != null){
            return ResultMsg.success(true,topicRedis.getRedisCollectionCountOnTopic(id));
        }
        return ResultMsg.success(false);
    }

    public Pair<List<Topic>,Integer> findAllCollection(int page, String token){
        String user = jwtUtil.getTokenField(token,"USER");
        UserPersonalized userPersonalized = userRedis.findRedisUserPersonalized(user);
        if (userPersonalized == null){
            throw new ServiceException("NO_EXIST_USER","不存在该用户");
        }
        return findUserCollections(user,page);
    }

    private Pair<List<Topic>,Integer> findUserCollections(String user,int page){
        List<Topic> topics = topicRedis.getRedisCollectionTopics(user,page);
        int count = topicRedis.getRedisCollectionCount(user);
        return Pair.of(topics,count);
    }

    @Transactional
    public ResultMsg setCollectionStatus(String id,String token){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        if (userObj != null){
            Collection collection = topicRedis.getRedisCollection(user,id);
            int status;
            String msg;
            log.info("setCollectionStatus({})",collection);
            if (collection != null && collection.getDate() != null && !collection.getDate().isEmpty()){
                topicRedis.setRedisCollectionCount(user,false);
                topicRedis.setRedisCollectionCountOnTopic(id,false);
                status = topicDao.deleteCollect(user,id);
                topicRedis.removeRedisCollectionTopics(user,id);
                topicRedis.removeRedisCollection(user,id);

                msg = "CANCEL";
            }
            else{
                topicRedis.setRedisCollectionCount(user,true);
                topicRedis.setRedisCollectionCountOnTopic(id,true);
                collection = new Collection(id,user,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
                status = topicDao.appendCollect(collection);
                topicRedis.addRedisCollectionTopics(user,getTopicInfo(token,id).parseTopic());
                topicRedis.addRedisCollection(collection);
                msg = "COLLECT";
            }
            return status == 1 ? ResultMsg.success(msg,topicRedis.getRedisCollectionCountOnTopic(id)) : ResultMsg.error("ERROR");
        }
        return ResultMsg.error("NO_EXIST_USER");
    }

    public Pair<List<Topic>,Integer> findOtherCollection(String user,int page){
        UserPersonalized userPersonalized = userRedis.findRedisUserPersonalized(user);
        if(userPersonalized == null){
            throw new ServiceException("USER_NOT_EXIST","用户不存在");
        }
        if(userPersonalized.getShowCollection() != 1){
            throw new ServiceException("HIDE_COLLECTION","用户隐藏了收藏夹");
        }
        return findUserCollections(user,page);
    }

    public List<Topic> findFirstPublicTopic(){
        List<Topic> topics;
        if(redisUtil.hasKey("topic:firstPublicTopic")){
            topics = redisUtil.get("topic:firstPublicTopic",List.class);
        }
        else{
            topics = topicDao.findFirstPublicTopic();
            redisUtil.set("topic:firstPublicTopic",topics, 1, TimeUnit.DAYS);
        }
        return topics;
    }

    public List<Topic> findFirstRefreshTopic(){
        List<Topic> topics;
        if(redisUtil.hasKey("topic:firstRefreshTopic")){
            topics = redisUtil.get("topic:firstRefreshTopic",List.class);
        }
        else{
            topics = topicDao.findFirstRefreshTopic();
            redisUtil.set("topic:firstRefreshTopic",topics, 1, TimeUnit.DAYS);
        }
        return topics;
    }

    public List<Topic> findFirstCommentTopic(){
        List<Topic> topics;
        if(redisUtil.hasKey("topic:firstCommentTopic")){
            topics = redisUtil.get("topic:firstCommentTopic",List.class);
        }
        else{
            topics = topicDao.findFirstCommentTopic();
            redisUtil.set("topic:firstCommentTopic",topics, 1, TimeUnit.DAYS);
        }
        return topics;
    }

    public ResultMsg findTopicGallery(String id){
        return ResultMsg.success(topicDao.findTopicGalleryByTopicId(id),topicRedis.getRedisTopicGalleryCount(id));
    }

    public ResultMsg uploadTopicGallery(String id,String label,String imgBase64,String token){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        ResultMsg r = isPromiseToEditTopic(token,id);
        if(userObj != null){
            if(r.getMsg().equals("REJECT")){
                return r;
            }
            int total = topicRedis.getRedisTopicGalleryCount(id);
            //等级制度：普通用户只能上传5张图片，3级用户可以上传8张图片，5及以上用户可以上传10张图片
            if(userObj.getLevel() < 3 && total >= 5 || userObj.getLevel() >= 3 && userObj.getLevel() < 5 && total >= 8 || userObj.getLevel() >= 5 && total >= 10){
                return ResultMsg.error("GALLERY_FULL");
            }
            LocalDateTime ldt = LocalDateTime.now();
            String galleryId = "tg"+ dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
            String filename = galleryId+".png";
            String fileFolder = "user/"+user+"/topic/"+id+"/gallery/";
            MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(imgBase64),filename);
            boolean isInfected = clamAvUtil.scanFile(file);
            if(!isInfected){
                return ResultMsg.error("INFECTED");
            }
            ResultMsg uploadResult = fileService.upload(file,fileFolder,filename);
            if(uploadResult.getMsg().equals("SUCCESS")){
                TopicGallery topicGallery = new TopicGallery(galleryId,id,user,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"),fileFolder+filename,label);
                topicDao.insertTopicGallery(topicGallery);
                topicRedis.setRedisTopicGalleryCount(id,true);
                topicRedis.setRedisTopicGallery(topicGallery,true);
                return ResultMsg.success(topicGallery);
            }
            return ResultMsg.error("UPLOAD_ERROR");
        }
        return ResultMsg.error("NO_LOGIN");
    }

    public ResultMsg deleteTopicGallery(String id,String topicId,String token) {
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        ResultMsg r = isPromiseToEditTopic(token, topicId);
        if (userObj != null) {
            if (r.getMsg().equals("REJECT")) {
                return r;
            }
            var topicGalleries = topicRedis.getRedisTopicGallery(topicId);
            TopicGallery topicGallery = topicGalleries.stream().filter(g -> g.getId().equals(id)).findFirst().orElse(null);
            if (topicGallery != null) {
                int status = topicDao.deleteTopicGalleryById(id);
                topicRedis.setRedisTopicGalleryCount(topicId,false);
                topicRedis.setRedisTopicGallery(topicGallery,false);
                if (status == 1) {
                    fileService.removeFile(topicGallery.getPath());
                    return ResultMsg.success();
                }
                return ResultMsg.error("DELETE_ERROR");
            }
            return ResultMsg.error("NO_EXIST_GALLERY");
        }
        return ResultMsg.error("NO_LOGIN");
    }

    public ResultMsg topTopicComment(CommentVO commentVO, String token){
        ResultMsg r = isPromiseToEditTopic(token, commentVO.getTopicId());
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        int count = topicDao.findCommentTopCountByTopicId(commentVO.getTopicId());
        if(commentVO.getWeight() != 0 && count >= 3){
            return ResultMsg.error("TOP_FULL");
        }
        Comment comment = topicDao.findCommentByUserAndTopicIdAndDate(commentVO.getUser(), commentVO.getTopicId(), commentVO.getDate());
        int status = 0;
        boolean isTop = comment.getWeight() != 0;
        if(comment.getWeight() != 0){
            status = topicDao.updateCommentWeight(commentVO.getTopicId(), commentVO.getUser(), commentVO.getDate(),0);
        }
        else{
            status = topicDao.updateCommentWeight(commentVO.getTopicId(), commentVO.getUser(), commentVO.getDate(), commentVO.getWeight());
        }
        topicRedis.setRedisTopComment(commentVO.getTopicId());
        return status == 1 ? ResultMsg.success(isTop) : ResultMsg.error("ERROR");
    }

    public List<CommentVO> findTopComment(String id){
        return topicRedis.getRedisTopComment(id);
    }

    public ResultMsg findTopicFiles(String topicId){
        return ResultMsg.success(topicRedis.getRedisTopicFiles(topicId),topicRedis.getRedisTopicFilesCount(topicId));
    }

    public ResultMsg uploadTopicFile(String id, String fileName, String fileLabel, String fileBase64, String token){
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        if(userObj != null) {
            ResultMsg r = isPromiseToEditTopic(token, id);
            if (r.getMsg().equals("REJECT")) {
                return r;
            }
            int total = topicRedis.getRedisTopicFilesCount(id);
            if (total >= 3) {
                return ResultMsg.error("FILE_FULL");
            }
            TopicFile topicFile = TopicFile.parseTopicFileObj(fileName,id,fileLabel,user,fileBase64);
            String filename = topicFile.getId();
            String fileFolder = "user/"+user+"/topic/"+id+"/file/";
            MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(fileBase64),filename);
            boolean isInfected = clamAvUtil.scanFile(file);
            if(!isInfected){
                return ResultMsg.error("INFECTED");
            }
            fileService.upload(file,fileFolder,filename);
            topicDao.insertTopicFile(topicFile);
            topicRedis.setRedisTopicFilesCount(id,true);
            topicRedis.setRedisTopicFiles(topicFile,true);
            return ResultMsg.success(topicFile);
        }
        return ResultMsg.error("NO_LOGIN");
    }

    public ResultMsg deleteTopicFile(String id,String topicId,String token) {
        String user = jwtUtil.getTokenField(token,"USER");
        User userObj = userRedis.findRedisUser(user);
        ResultMsg r = isPromiseToEditTopic(token, topicId);
        if (userObj != null) {
            if (r.getMsg().equals("REJECT")) {
                return r;
            }
            var cached = topicRedis.getRedisTopicFiles(topicId);
            TopicFile topicFile = cached.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
            if (topicFile != null) {
                fileService.removeFile("user/" + user + "/topic/" + topicId + "/file/" + topicFile.getId());
                int status = topicDao.deleteTopicFileById(id);
                topicRedis.setRedisTopicFilesCount(topicId,false);
                topicRedis.setRedisTopicFiles(topicFile,false);
                return status == 1 ? ResultMsg.success() : ResultMsg.error("DELETE_ERROR");
            }
            return ResultMsg.error("NO_EXIST_FILE");
        }
        return ResultMsg.error("NO_LOGIN");
    }


    //辅助函数，不作为接口使用
    private String[] getSearchFields(String classification) {
        switch (classification) {
            case "作者":
                return new String[]{"name"};
            case "主题标题":
                return new String[]{"title", "topicTitle", "enTitle"};
            case "主题内容":
                return new String[]{"content"};
            default:
                return new String[]{"content", "title", "topicTitle", "enTitle", "name"};
        }
    }

    @Transactional
    public int addExpFunc(String token,int minExp,int maxExp,User user){
        int level = user.getLevel();
        int addExp = 0;
        Random r = new Random();
        addExp = r.nextInt(maxExp)+minExp;
        int exp = user.getExp() + addExp;
        int expDiff = Exp.checkAndLevelUp(exp,level);
        if(expDiff >= 0){
            exp = expDiff;
            level++;
        }
        String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
        userDao.updateExp(user.getUser(),exp,level,date);
        user.setExp(exp);
        user.setLevel(level);
        user.setUpdateTime(date);
        userRedis.setRedisUser(user.getUser(),user);
        UserVO userVO = user.toUserVO();
        userVO.setMaxExp(Exp.getMaxExp(level));
        redisUtil.set("onlineUser:"+token, userVO);
        return addExp;
    }

}
