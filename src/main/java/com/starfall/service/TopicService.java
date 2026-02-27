package com.starfall.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.starfall.dao.TopicDao;
import com.starfall.dao.UserDao;
import com.starfall.entity.*;
import com.starfall.entity.Collection;
import com.starfall.util.*;
import io.jsonwebtoken.Claims;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class TopicService {
    @Autowired
    TopicDao topicDao;
    @Autowired
    UserDao userDao;
    @Autowired
    FileService fileService;
    @Autowired
    SearchService searchService;
    @Autowired
    UserInteractionService userInteractionService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    CodeUtil codeUtil;
    @Autowired
    DateUtil dateUtil;



    private final int topicSize = 30;

    public ResultMsg findAllTopic(int page,String label,String version,String belong){
        List<Topic> list = null;
        int num = 0;
        if (page <= 0){
            page = 1;
        }
        if (label == null || version == null || label.isBlank() || version.isBlank()){
            list = topicDao.findAllTopicLimit30((page-1)*topicSize,belong);
            num = topicDao.findTopicTotal(belong);
            return ResultMsg.success(list,num);
        }
        if(label.equals("全部") && version.equals("全部")){
            list = topicDao.findAllTopicLimit30((page-1)*topicSize,belong);
            num = topicDao.findTopicTotal(belong);
        }
        else if(label.equals("全部")){
            list = new ArrayList<>();
            for (Topic item : topicDao.findAllTopic(belong)) {
                if (VersionUtil.match(version,item.getVersion())){
                    num++;
                    if (list.size() <= topicSize && num > (page-1)*topicSize){
                        list.add(item);
                    }
                }
            }
        }
        else if(version.equals("全部")){
            list = topicDao.findAllTopicLabelLimit30((page-1)*topicSize,label,belong);
            num = topicDao.findTopicTotalByLabel(label,belong);
        }
        else {
            list = new ArrayList<>();
            for (Topic item: topicDao.findAllTopicLabel(label,belong)) {
                if (VersionUtil.match(version,item.getVersion())){
                    num++;
                    if (list.size() < topicSize && num > (page-1)*topicSize){
                        list.add(item);
                    }
                }
            }
        }
        return ResultMsg.success(list,num);
    }


    public ResultMsg getTopicInfo(String token,String id){
        TopicOut topic = topicDao.findTopicInfoById(id);
        if(topic != null){
            //为了获取最大经验值
            topic.setMaxExp(Exp.getMaxExp(topic.getLevel()));
            if (topic.getDisplay() != 1){
                Claims claims = JwtUtil.parseJWT(token);
                String user = (String) claims.get("USER");
                String role = (String) claims.get("ROLE");
                if (user.equals(topic.getUser())){
                    return ResultMsg.success(topic);
                }
                else if (topic.getDisplay() == 0 &&
                        (role.equals("admin")
                                || (topic.getBelong().equals("resource") && role.equals("resource_moderator"))
                                || (topic.getBelong().equals("talk") && role.equals("talk_moderator"))
                        )){
                    return ResultMsg.success(topic);
                }
                return ResultMsg.error("ID_ERROR");
            }
            else{
                if(token != null){
                    int oldView = topic.getView();
                    topicDao.updateTopicView(oldView+1,id);
                    topic.setView(oldView+1);
                }
                return ResultMsg.success(topic);
            }
        }
        return ResultMsg.error("ID_ERROR");
    }


    public ResultMsg findAllTopicByUser(int page,String user,String token){
        List<Topic> list;
        int num = topicDao.findTopicTotalByUser(user);;
        if(token == null){
            list = topicDao.findTopicByUserWhereDisplay((page-1)*10,user);
        }
        else{
            Claims claims = JwtUtil.parseJWT(token);
            String fromUser = (String) claims.get("USER");
            if (fromUser.equals(user)){
                list = topicDao.findTopicByUser((page-1)*10,user);

            }
            else{
                list = topicDao.findTopicByUserWhereDisplay((page-1)*10,user);
                num = topicDao.findTopicTotalByUserWhereDisplay(user);
            }
        }
        return ResultMsg.success(list,num);
    }

    public ResultMsg getLike(String topicId,String token) {
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        LikeLog like = topicDao.findLikeByTopicAndUser(topicId,user);
        if(like != null && like.getStatus() == 1){
            return ResultMsg.warning("IS_LIKE",topicDao.findLikeTotalByTopic(topicId));
        }
        else if(like != null && like.getStatus() == 2){
            return ResultMsg.warning("IS_DISLIKE");
        }
        return ResultMsg.error("NOT_LIKE");
    }

    public ResultMsg like(String topicId,String token,int like){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        LocalDateTime ldt = LocalDateTime.now();
        String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
        LikeLog likeObj = topicDao.findLikeByTopicAndUser(topicId,user);
        if(likeObj != null){
            if(likeObj.getStatus() != like){
                topicDao.updateLikeStateByTopicAndUser(topicId,user,like,date);
                if(like == 1){
                    return ResultMsg.warning("UPDATE_LIKE",topicDao.findLikeTotalByTopic(topicId));
                }
                return ResultMsg.warning("UPDATE_LIKE");
            }
            topicDao.updateLikeStateByTopicAndUser(topicId,user,0,date);
            return ResultMsg.warning("ALREADY_LIKE");
        }
        topicDao.insertLike(topicId,user,like,date);
        if(like == 1){
            return ResultMsg.warning("LIKE_SUCCESS",topicDao.findLikeTotalByTopic(topicId));
        }
        return ResultMsg.warning("LIKE_SUCCESS");
    }


    public ResultMsg findCommentByTopicId(String id,int page){
        List<CommentOut> list = topicDao.findCommentByTopicId(id,(page-1)*10);
        for (CommentOut item : list){
            item.setExp(Exp.getMaxExp(item.getLevel()));
        }
        return ResultMsg.success(list,topicDao.findCommentCountByTopicId(id));
    }

    public ResultMsg appendComment( String topicId, String token, String content, String code){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if(codeUtil.checkCode(code)){
            LocalDateTime ldt = LocalDateTime.now();
            String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
            topicDao.insertComment(topicId,user,date,content,0);
            int count = topicDao.findCommentCountByTopicId(topicId);
            topicDao.updateTopicComment(count,date,topicId);
            return ResultMsg.success(count);
        }
        return ResultMsg.error("CODE_ERROR");
    }


    public ResultMsg deleteComment(String id,String token,String date){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        int status1 = topicDao.deleteComment(id,user,date);
        int count = topicDao.findCommentCountByTopicId(id);
        int status2 = topicDao.updateTopicComment(count,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"),id);
        return status1+status2 == 2 ? ResultMsg.success(count) : ResultMsg.error("DELETE_ERROR");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultMsg appendTopic(String token, TopicIn topicIn){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        int level = userObj.getLevel();
        if(level < 5){
            return ResultMsg.error("LEVEL_ERROR");
        }
        if(topicDao.countTopicTotalByDateAndUser(user) >= 10){
            return ResultMsg.error("APPEND_DAY_MAX_ERROR");
        }
        if(codeUtil.checkCode(topicIn.getCode())){
            LocalDateTime ldt = LocalDateTime.now();
            String id = dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS")+ CodeUtil.getCode(6);
            String date = dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss");
            if (topicIn.getDisplay() != 0 && topicIn.getDisplay() != 1){
                topicIn.setDisplay(0);
            }
            topicDao.insertTopic(
                    id,
                    topicIn.getTitle(),
                    topicIn.getLabel(),
                    user,
                    date,
                    topicIn.getVersion(),
                    dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"),
                    topicIn.getDisplay(),
                    topicIn.getBelong(),
                    topicIn.getDisplay() == 1 ? 1 : 0
            );
            String filename = id+".md";
            String fileFolder = "user/"+user+"/topic/"+id;
            MultipartFile file = new MultipartFileImpl(topicIn.getContent(),filename);
            try{
                fileService.upload(file,fileFolder,filename);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("UPLOAD_ERROR");
            }
            topicDao.insertTopicItem(
                    id,
                    topicIn.getTopicTitle(),
                    topicIn.getEnTitle(),
                    topicIn.getSource(),
                    topicIn.getAuthor(),
                    topicIn.getLanguage(),
                    topicIn.getAddress(),
                    topicIn.getDownload(),
                    fileFolder + "/" + filename
            );
            int addExp = 0;
            if(topicIn.getDisplay() == 1){
                addExp = addExpFunc(token,100,200,userObj);
            }
            searchService.saveTopic(
                    new Search(
                            id,
                            topicIn.getTitle(),
                            topicIn.getLabel(),
                            topicIn.getBelong(),
                            topicIn.getTopicTitle(),
                            topicIn.getEnTitle(),
                            ContentUtil.parseContent(topicIn.getContent()),
                            LocalDate.parse(date),
                            ldt,
                            user,
                            userObj.getName(),
                            topicIn.getDisplay()
                    )
            );
            return ResultMsg.success(id,addExp);
        }
        return ResultMsg.error("CODE_ERROR");
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
        userDao.updateExp(user.getUser(),exp,level,dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
        UserDTO userDTO = user.toUserDTO();
        userDTO.setExp(exp);
        userDTO.setLevel(level);
        userDTO.setMaxExp(Exp.getMaxExp(level));
        redisUtil.set("onlineUser:"+token, userDTO);
        return addExp;
    }

    public ResultMsg isPromiseToEditTopic(String token,String id){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        String topicUser = topicDao.findTopicUserById(id);
        if(user.equals(topicUser)){
            return ResultMsg.success();
        }
        return ResultMsg.error("REJECT");
    }


    public ResultMsg findTopicInfoToEdit(String token,String id){
        ResultMsg r = isPromiseToEditTopic(token,id);
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        TopicOut topicOut = topicDao.findTopicInfoById(id);
        //为了获取最大经验值
        topicOut.setMaxExp(Exp.getMaxExp(topicOut.getLevel()));
        if(topicOut == null){
            return ResultMsg.error("NULL_ERROR");
        }
        return ResultMsg.success(topicOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultMsg updateTopic(String token,TopicIn topicIn){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        ResultMsg r = isPromiseToEditTopic(token,topicIn.getId());
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        if(codeUtil.checkCode(topicIn.getCode())){
            LocalDateTime ldt = LocalDateTime.now();
            String date = dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss");
            TopicOut topicOut = topicDao.findTopicInfoById(topicIn.getId());
            if (topicIn.getDisplay() != 0 && topicIn.getDisplay() != 1){
                topicIn.setDisplay(0);
            }
            int isFirstPublic = 1;
            boolean isPublicExp = false;
            if (topicOut.getIsFirstPublic() == 0){
                if(topicIn.getDisplay() == 0){
                    isFirstPublic = 0;
                }
                else{
                    isPublicExp = true;
                }
            }
            Topic topic = new Topic(
                    topicIn.getId(),
                    topicIn.getTitle(),
                    topicIn.getLabel(),
                    user,
                    null,
                    null,
                    isPublicExp ? date : topicOut.getDate(),
                    topicOut.getView(),
                    topicOut.getComment(),
                    topicIn.getVersion(),
                    date,
                    topicIn.getDisplay(),
                    topicIn.getBelong(),
                    isFirstPublic
            );
            String filename = topicIn.getId()+".md";
            String fileFolder = "user/"+user+"/topic/"+topicIn.getId();
            MultipartFile file = new MultipartFileImpl(topicIn.getContent(),filename);
            try{
                fileService.upload(file,fileFolder,filename);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("UPLOAD_ERROR");
            }
            TopicItem topicItem = new TopicItem(
                    topicIn.getId(),
                    topicIn.getTopicTitle(),
                    topicIn.getEnTitle(),
                    topicIn.getSource(),
                    topicIn.getAuthor(),
                    topicIn.getLanguage(),
                    topicIn.getAddress(),
                    topicIn.getDownload(),
                    fileFolder + "/" + filename
            );
            TopicOut data = topicDao.findTopicInfoById(topicIn.getId());
            if (data == null){
                return ResultMsg.error("NULL_ERROR");
            }

            int addExp = 0;
            if(isPublicExp){
                addExp = addExpFunc(token,100,200,userObj);
            }
            int status1 = topicDao.updateTopicExpectCommentAndView(topic);
            int status2 = topicDao.updateTopicItem(topicItem);
            if (data.getDisplay() != -1){
                topicDao.updateTopicDisplay(topicIn.getDisplay(),topicIn.getId());
            }
            searchService.saveTopic(new Search(
                    topicIn.getId(),
                    topicIn.getTitle(),
                    topicIn.getLabel(),
                    topicIn.getBelong(),
                    topicIn.getTopicTitle(),
                    topicIn.getEnTitle(),
                    ContentUtil.parseContent(topicIn.getContent()),
                    LocalDate.parse(topic.getDate()),
                    ldt,
                    userObj.getUser(),
                    userObj.getName(),
                    topic.getDisplay()
            ));
            return (status1+status2) == 2 ? ResultMsg.success(topicIn.getId(),addExp) : ResultMsg.error("UPDATE_ERROR");
        }
        return ResultMsg.error("CODE_ERROR");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultMsg deleteTopic(String token,String id) {
        ResultMsg r = isPromiseToEditTopic(token, id);
        if (r.getMsg().equals("REJECT")) {
            return r;
        }
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        int status1 = topicDao.deleteTopicItem(id);
        int status2 = topicDao.deleteTopic(id);
        topicDao.deleteTopicGalleryById(id);
        topicDao.deleteCommentByTopicId(id);
        topicDao.deleteLikeLog(id);
        fileService.removeFolder("user/"+user+"/topic/"+id);
        searchService.deleteById(id);
        return status1+status2 == 2 ? ResultMsg.success() : ResultMsg.error("DELETE_ERROR");
    }

    @Autowired
    ElasticsearchOperations elasticsearch;

    public ResultMsg searchTopic(String key,String classification,int page){
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
                        System.out.println(field);
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

    public ResultMsg adjustTopicDisplay(String id,String reason,int display,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if (userObj != null){
            if (display == -1 || display == 1){
                TopicOut topic = topicDao.findTopicInfoById(id);
                if (userObj.getRole().equals("admin")
                        || (topic.getBelong().equals("resource") && userObj.getRole().equals("resource_moderator"))
                        || (topic.getBelong().equals("talk") && userObj.getRole().equals("talk_moderator"))
                ){
                    topicDao.updateTopicDisplay(display,id);
                    Search search = new Search();
                    search.setId(id);
                    search.setStatus(display);
                    search.setRefreshTime(LocalDateTime.now());
                    searchService.saveTopic(search);
                    TopicNoticeAction topicNoticeAction = new TopicNoticeAction(id,topic.getTitle(),display,user,reason);
                    userInteractionService.insertNotice(topic.getUser(),UserNoticeType.topic,
                            "帖子状态有更新",JsonOperate.toJson(topicNoticeAction,false));
//                    messageService.SendMessage(token,topic.getUser(),
//                            "您的帖子<a href=\"/topic/detail/"+topic.getId()+"\" target=\"_blank\">"+topic.getTitle()+"</a>已被 <a href=\"/personal/other/"+userObj.getUser()+"\" target=\"_blank\">"+userObj.getName()+"</a> 设置为"+(display == -1 ? " <span style=\"color: darkred;\">待整改</span>" : " <span style=\"color: darkgreen;\">已发布</span>")+" 状态。<br/>原因："+reason );
                    return ResultMsg.success();
                }
            }
            return ResultMsg.error("NO_PERMISSION");
        }
        return ResultMsg.error("NO_EXIST_USER");
    }

    public ResultMsg findCollectStatus(String id,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        if (topicDao.existCollection(user,id) > 0){
            return ResultMsg.success(true,topicDao.findCollectionTotalById(id));
        }
        return ResultMsg.success(false);
    }

    public ResultMsg findAllCollection(int page,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if (userObj != null){
            List<Topic> topics = topicDao.findCollectByUser(user,(page - 1)*20);
            return  ResultMsg.success(topics);
        }
        return ResultMsg.error("NO_EXIST_USER");
    }

    public ResultMsg setCollectionStatus(String id,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if (userObj != null){
            Collection collection = topicDao.findCollection(user,id);
            int status;
            String msg;
            if (collection != null){
                status = topicDao.deleteCollect(user,id);
                msg = "CANCEL";
            }
            else{
                status = topicDao.appendCollect(user,id, dateUtil.getDateTimeByFormat("yyyy-MM-dd HH:mm:ss"));
                msg = "COLLECT";
            }
            return status == 1 ? ResultMsg.success(msg,topicDao.findCollectionTotalById(id)) : ResultMsg.error("ERROR");
        }
        return ResultMsg.error("NO_EXIST_USER");
    }

    public ResultMsg findFirstPublicTopic(){
        return ResultMsg.success(topicDao.findFirstPublicTopic());
    }

    public ResultMsg findFirstRefreshTopic(){
        return ResultMsg.success(topicDao.findFirstRefreshTopic());
    }

    public ResultMsg findFirstCommentTopic(){
        return ResultMsg.success(topicDao.findFirstCommentTopic());
    }

    public ResultMsg findTopicGallery(String id){
        return ResultMsg.success(topicDao.findTopicGalleryByTopicId(id));
    }

    public ResultMsg uploadTopicGallery(String id,String label,String imgBase64,String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        ResultMsg r = isPromiseToEditTopic(token,id);
        if(userObj != null){
            if(r.getMsg().equals("REJECT")){
                return r;
            }
            int total = topicDao.countTopicGalleryByTopicId(id);
            if(total >= 10){
                return ResultMsg.error("GALLERY_FULL");
            }
            LocalDateTime ldt = LocalDateTime.now();
            String galleryId = "tg"+ dateUtil.getDateTimeByFormat(ldt,"yyyyMMddHHmmssSSSS") + CodeUtil.getCode(6);
            String filename = galleryId+".png";
            String fileFolder = "user/"+user+"/topic/"+id+"/gallery/";
            MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(imgBase64),filename);
            ResultMsg uploadResult = fileService.upload(file,fileFolder,filename);
            if(uploadResult.getMsg().equals("SUCCESS")){
                TopicGallery topicGallery = new TopicGallery(galleryId,id,user,dateUtil.getDateTimeByFormat(ldt,"yyyy-MM-dd HH:mm:ss"),fileFolder+filename,label);
                topicDao.insertTopicGallery(topicGallery);
                return ResultMsg.success(topicGallery);
            }
            return ResultMsg.error("UPLOAD_ERROR");
        }
        return ResultMsg.error("NO_LOGIN");
    }

    public ResultMsg deleteTopicGallery(String id,String topicId,String token) {
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        ResultMsg r = isPromiseToEditTopic(token, topicId);
        if (userObj != null) {
            if (r.getMsg().equals("REJECT")) {
                return r;
            }
            TopicGallery topicGallery = topicDao.findTopicGalleryById(id);
            if (topicGallery != null) {
                int status = topicDao.deleteTopicGalleryById(id);
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

    public ResultMsg topTopicComment(CommentOut commentOut,String token){
        System.out.println(commentOut);
        ResultMsg r = isPromiseToEditTopic(token, commentOut.getTopicId());
        if(r.getMsg().equals("REJECT")){
            return r;
        }
        int count = topicDao.findCommentTopCountByTopicId(commentOut.getTopicId());
        if(commentOut.getWeight() != 0 &&count >= 3){
            return ResultMsg.error("TOP_FULL");
        }
        Comment comment = topicDao.findCommentByUserAndTopicIdAndDate(commentOut.getUser(),commentOut.getTopicId(),commentOut.getDate());
        if(comment.getWeight() != 0){
            int status = topicDao.updateCommentWeight(commentOut.getTopicId(),commentOut.getUser(),commentOut.getDate(),0);
            return status == 1 ? ResultMsg.success(false) : ResultMsg.error("ERROR");
        }
        else{
            int status = topicDao.updateCommentWeight(commentOut.getTopicId(),commentOut.getUser(),commentOut.getDate(),commentOut.getWeight());
            return status == 1 ? ResultMsg.success(true) : ResultMsg.error("ERROR");
        }
    }

    public ResultMsg findTopicFiles(String topicId){
        System.out.println(topicId);
        return ResultMsg.success(topicDao.findTopicFilesByTopicId(topicId));
    }

    public ResultMsg uploadTopicFile(String id, String fileName, String fileLabel, String fileBase64, String token){
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(userObj != null) {
            String topicUser = topicDao.findTopicUserById(id);
            if (!user.equals(topicUser)) {
                return ResultMsg.error("REJECT");
            }
            int total = topicDao.countTopicFileByTopicId(id);
            if (total >= 3) {
                return ResultMsg.error("FILE_FULL");
            }
            TopicFile topicFile = TopicFile.parseTopicFileObj(fileName,id,fileLabel,user,fileBase64);
            String filename = topicFile.getId();
            String fileFolder = "user/"+user+"/topic/"+id+"/file/";
            MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(fileBase64),filename);
            fileService.upload(file,fileFolder,filename);
            topicDao.insertTopicFile(topicFile);
            return ResultMsg.success(topicFile);
        }
        return ResultMsg.error("NO_LOGIN");
    }

    public ResultMsg deleteTopicFile(String id,String topicId,String token) {
        Claims claims = JwtUtil.parseJWT(token);
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        ResultMsg r = isPromiseToEditTopic(token, topicId);
        if (userObj != null) {
            if (r.getMsg().equals("REJECT")) {
                return r;
            }
            TopicFile topicFile = topicDao.findTopicFileById(id);
            if (topicFile != null) {
                fileService.removeFile("user/" + user + "/topic/" + topicId + "/file/" + topicFile.getId());
                int status = topicDao.deleteTopicFileById(id);
                return status == 1 ? ResultMsg.success() : ResultMsg.error("DELETE_ERROR");
            }
            return ResultMsg.error("NO_EXIST_FILE");
        }
        return ResultMsg.error("NO_LOGIN");
    }
}
