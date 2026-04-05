package com.starfall.dao.redis;

import com.starfall.dao.TopicDao;
import com.starfall.entity.*;
import com.starfall.util.RedisUtil;
import com.starfall.util.VersionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TopicRedis {

    @Autowired
    TopicDao topicDao;
    @Autowired
    UserRedis userRedis;
    @Autowired
    RedisUtil redisUtil;

    int topicCachePage = 2;
    int topicPageSize = 30;
    int collectionCachePage = 4;
    int collectionPageSize = 20;
    int commentCachePage = 4;
    int commentPageSize = 10;
    int userTopicCachePage = 2;
    int userTopicPageSize = 20;

    final List<String> labelList = List.of(
            "服务端", "客户端", "模组", "插件", "材质包", "地图", "光影", "皮肤", "数据包",//资源标签
            "问答", "闲聊", "服务器推荐", "教程", "视频"//有话说标签
    );
    final List<String> versionList = List.of(//预设版本号
            "1.4.x",
            "1.5.x",
            "1.6.x",
            "1.7", "1.7.2", "1.7.10", "1.7.x",
            "1.8", "1.8.9", "1.8.x",
            "1.9", "1.9.4", "1.9.x",
            "1.10", "1.10.2", "1.10.x",
            "1.11", "1.11.2", "1.11.x",
            "1.12", "1.12.2", "1.12.x",
            "1.13", "1.13.1", "1.13.x",
            "1.14", "1.14.2", "1.14.4", "1.14.x",
            "1.15", "1.15.2", "1.15.x",
            "1.16", "1.16.5", "1.16.x",
            "1.17", "1.17.1", "1.17.x",
            "1.18", "1.18.2", "1.18.x",
            "1.19", "1.19.4", "1.19.x",
            "1.20", "1.20.1", "1.20.2", "1.20.6", "1.20.x",
            "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.8", "1.21.x"
    );

//    重要部分：缓存topic列表
    public Pair<List<Topic>,Integer> getRedisTopics(String belong, String label, String version, int page){
        log.info("getRedisTopics: belong={}, label={}, version={}, page={}",belong,label,version,page);
        if(page <= 0){
            page = 1;
        }
        if(!(belong.equals("talk") || belong.equals("resource"))){
            return Pair.of(List.of(),0);
        }
        if(label != null && version != null && !labelList.contains(label) && !versionList.contains(version)){
            return Pair.of(List.of(),0);
        }
        List<Topic> list;
        int count = 0;
//        未超过缓存页数限制，优先从缓存查询
        if(page <= topicCachePage){
//        两个都筛选
//            topic:list:resource/talk:double:每个标签:每个版本号/...:data
            if(label != null && version != null){
                if(redisUtil.hasKey("topic:list",belong,"double",label,version,"count")){
                    list = getHasKeyTopicWithPage(redisUtil.joinKey("topic:list",belong,"double",label,version,"data"),page);
                    count = getRedisTopicCount(belong,label,version,null);
                }
                else{
                    var cacheOrigin = topicDao.findAllTopicLabel(label,belong);
                    var cacheOriginWithFilter = cacheOrigin.stream().filter(t -> VersionUtil.match(version,t.getVersion())).toList();
                    var cache = cacheOriginWithFilter.subList(0, Math.min(cacheOriginWithFilter.size(), topicCachePage*topicPageSize));
                    list = redisUtil.paginateByPageNum(cache,page,topicPageSize);
                    redisUtil.set(redisUtil.joinKey("topic:list",belong,"double",label,version,"data"), cache, 1, TimeUnit.HOURS);
                    count = getRedisTopicCount(belong,label,version,cacheOriginWithFilter);
                }
            }
//        仅筛选标签
//            topic:list:resource/talk:label:每个标签/...:data
            else if(label != null){
                if(redisUtil.hasKey("topic:list",belong,"label",label,"data")){
                    list = getHasKeyTopicWithPage(redisUtil.joinKey("topic:list",belong,"label",label,"data"),page);
                    count = getRedisTopicCount(belong,label,version,null);
                }
                else{
                    var cache = topicDao.findAllTopicLabelLimit(label,belong,0,topicCachePage*topicPageSize);
                    list = redisUtil.paginateByPageNum(cache,page,topicPageSize);
                    redisUtil.set(redisUtil.joinKey("topic:list",belong,"label",label,"data"), cache,1, TimeUnit.HOURS);
                    count = getRedisTopicCount(belong,label,version,null);
                }
            }
//        仅筛选版本
//            topic:list:resource/talk:version:每个版本号/...:data
            else if(version != null){
                if(redisUtil.hasKey("topic:list",belong,"version",version,"data")){
                    list = getHasKeyTopicWithPage(redisUtil.joinKey("topic:list",belong,"version",version,"data"),page);
                    count = getRedisTopicCount(belong,label,version,null);
                }
                else{
                    var cacheOrigin = topicDao.findAllTopic(belong);
                    var cacheOriginWithFilter = cacheOrigin.stream().filter(t -> VersionUtil.match(version,t.getVersion())).toList();
                    var cache = cacheOriginWithFilter.subList(0, Math.min(cacheOriginWithFilter.size(), topicCachePage*topicPageSize));
                    list = redisUtil.paginateByPageNum(cache,page,topicPageSize);
                    redisUtil.set(redisUtil.joinKey("topic:list",belong,"version",version,"data"), cache, 1, TimeUnit.HOURS);
                    count = getRedisTopicCount(belong,label,version,cacheOriginWithFilter);
                }
            }
//        未筛选
//            topic:list:resource/talk:default:data
            else{
                if(redisUtil.hasKey("topic:list",belong,"default","data")){
                    list = getHasKeyTopicWithPage(redisUtil.joinKey("topic:list",belong,"default","data"),page);
                    count = getRedisTopicCount(belong,label,version,null);
                }
                else{
                    var cache = topicDao.findAllTopicLimit(belong,0,topicCachePage*topicPageSize);
                    list = redisUtil.paginateByPageNum(cache,page,topicPageSize);
                    redisUtil.set(redisUtil.joinKey("topic:list",belong,"default","data"), cache,1, TimeUnit.HOURS);
                    count = getRedisTopicCount(belong,label,version,null);
                }
            }
        }
//        超过缓存页数限制，直接从数据库查询
        else{
            if(label != null && version != null){
                var cache = topicDao.findAllTopicLabel(label,belong);
                var cacheWithFilter = cache.stream().filter(t -> VersionUtil.match(version,t.getVersion())).toList();
                list = redisUtil.paginateByPageNum(cacheWithFilter,page,topicPageSize);
                count = getRedisTopicCount(belong,label,version,cacheWithFilter);
            }
            else if(label != null){
                list = topicDao.findAllTopicLabelLimit(label,belong,0,(page-1)*topicPageSize);
                count = getRedisTopicCount(belong,label,version,null);
            }
            else if(version != null){
                var cache = topicDao.findAllTopic(belong);
                var cacheWithFilter = cache.stream().filter(t -> VersionUtil.match(version,t.getVersion())).toList();
                list = redisUtil.paginateByPageNum(cacheWithFilter,page,topicPageSize);
                count = getRedisTopicCount(belong,label,version,cacheWithFilter);
            }
            else{
                list = topicDao.findAllTopicLimit(belong,0,(page-1)*topicPageSize);
                count = getRedisTopicCount(belong,label,version,null);
            }
        }
        log.info("getRedisTopics: belong={}, label={}, version={}, page={} 返回：list:{},count:{}",belong,label,version,page,list,count);
        return Pair.of(list,count);
    }
    //辅助函数，根据key获取缓存列表并分页返回
    private List<Topic> getHasKeyTopicWithPage(String key,int page){
        var cache = redisUtil.getList(Topic.class,key);
        return redisUtil.paginateByPageNum(cache,page,collectionPageSize);
    }
    //辅助函数，根据key获取缓存列表的总数
    private int getRedisTopicCount(String belong, String label, String version,List<Topic> filterCache){
        int count = 0;
//        全筛选
        if(label != null && version != null){
            if(redisUtil.hasKey("topic:list",belong,"double",label,version,"count")){
                count = redisUtil.get(Integer.class,"topic:list",belong,"double",label,version,"count");
            }
            else{
                if(filterCache != null){
                    count = filterCache.size();
                }
                else{
                    var cache = topicDao.findAllTopicLabel(label,belong);
                    count = (int) cache.stream().filter(t -> VersionUtil.match(version,t.getVersion())).count();
                }
                redisUtil.set(redisUtil.joinKey("topic:list",belong,"double",label,version,"count"), count, 1, TimeUnit.HOURS);
            }
        }
//        仅筛选标签
        else if(label != null){
            if(redisUtil.hasKey("topic:list",belong,"label",label,"count")){
                count = redisUtil.get(Integer.class,"topic:list",belong,"label",label,"count");
            }
            else{
                count = topicDao.countTopicAllByLabel(label,belong);
                redisUtil.set(redisUtil.joinKey("topic:list",belong,"label",label,"count"), count, 1, TimeUnit.HOURS);
            }
        }
//        仅筛选版本
        else if(version != null){
            if(redisUtil.hasKey("topic:list",belong,"version",version,"count")){
                count = redisUtil.get(Integer.class,"topic:list",belong,"version",version,"count");
            }
            else{
                if(filterCache != null){
                    count = filterCache.size();
                }
                else{
                    var cache = topicDao.findAllTopic(belong);
                    count = (int) cache.stream().filter(t -> VersionUtil.match(version,t.getVersion())).count();
                }
                redisUtil.set(redisUtil.joinKey("topic:list",belong,"version",version,"count"), count, 1, TimeUnit.HOURS);
            }
        }
//        未筛选
        else{
            if(redisUtil.hasKey("topic:list",belong,"default","count")){
                count = redisUtil.get(Integer.class,"topic:list",belong,"default","count");
            }
            else{
                count = topicDao.countTopicAll(belong);
                redisUtil.set(redisUtil.joinKey("topic:list",belong,"default","count"), count, 1, TimeUnit.HOURS);
            }
        }
        return count;
    }

    //删除缓存的topic列表
    @Async
    public void deleteRedisTopics(String belong,String label,String version){
        //先清除没有筛选的键
        redisUtil.deleteAsync("topic:list",belong,"default:data");
        redisUtil.deleteAsync("topic:list",belong,"default","count");
        //再清除仅标签筛选的键
        redisUtil.deleteAsync("topic:list",belong,"label",label,"data");
        redisUtil.deleteAsync("topic:list",belong,"label",label,"count");
//        特殊特殊，版本号可能匹配多个版本，所以需要先查询出所有匹配的版本号，再逐个清除仅版本筛选的键
        versionList.stream().filter(v -> VersionUtil.match(v,version)).toList().forEach(v -> {
            redisUtil.deleteAsync("topic:list",belong,"version",v,"data");
            redisUtil.deleteAsync("topic:list",belong,"version",v,"count");
        });
        //最后清除双重筛选的键
        redisUtil.deleteAsync("topic:list",belong,"double",label,version,"data");
        redisUtil.deleteAsync("topic:list",belong,"double",label,version,"count");
    }

    //更新首页第一个topic
    @Async
    public void updateHomeFirstTopic(Topic topic, String... key){
        if(redisUtil.hasKey(key)){
            List<Topic> list = redisUtil.getList(Topic.class,key);
            list.add(0,topic);
            list.remove(list.size()-1);
            redisUtil.set(redisUtil.joinKey(key), list);
        }
    }

    public TopicOut getRedisTopicOut(String id){
        TopicOut topic;
        String key = redisUtil.joinKey("topic:cache",id,"info");
        if(redisUtil.hasKey(key)){
            topic = redisUtil.get(key, TopicOut.class);
        }
        else{
            topic = topicDao.findTopicInfoById(id);
            redisUtil.set(key, topic,1, TimeUnit.HOURS);
        }
        return topic;
    }

    //设置缓存的topic详细内容
    @Async
    public void setRedisTopicOut(TopicOut topic){
        String key = redisUtil.joinKey("topic:cache",topic.getId(),"info");
        if(redisUtil.hasKey(key)){
            redisUtil.set(key, topic);
        }
    }

    public LikeLog getRedisLikeLog(String id, String user){
        LikeLog like;
        String key = redisUtil.joinKey("topic:like:cache",id,user);
        if(redisUtil.hasKey(key)){
            like = redisUtil.get(key, LikeLog.class);
        }
        else{
            like = topicDao.findLikeByTopicAndUser(id,user);
            redisUtil.set(key, like,1,TimeUnit.HOURS);
        }
        return like;
    }

    public int setRedisLike(String id,String user,LikeLog likeObj,int like,String date){
        String key = redisUtil.joinKey("topic:like:cache",id,user);
        if(redisUtil.hasKey(key)){
            if(likeObj == null){
                likeObj = getRedisLikeLog(id,user);
            }
            likeObj.setStatus(like);
            likeObj.setDate(date);
            redisUtil.set(key, likeObj);
        }
        return like;
    }

    public int getRedisLikeCount(String id){
        int count = 0;
        String key = redisUtil.joinKey("topic:like:count",id);
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key, Integer.class);
        }
        else{
            count = topicDao.findLikeTotalByTopic(id);
            redisUtil.set(key, count,1,TimeUnit.HOURS);
        }
        return count;
    }

    public void setRedisLikeCount(String id,boolean like) {
        String key = redisUtil.joinKey("topic:like:count",id);
        if(redisUtil.hasKey(key)){
            redisUtil.incOrDec(like, key);
        }
    }

    public List<Topic> getRedisCollectionTopics(String user, int page){
        String key = redisUtil.joinKey("topic:collection",user,"cache");
        if (redisUtil.hasKey(key) && page <= collectionCachePage) {
            List<Topic> redisList = redisUtil.getList(key, Topic.class);
            return redisUtil.paginateByPageNum(redisList, page, collectionPageSize);
        } else {
            if(page <= collectionCachePage){
                var cache = topicDao.findCollectByUser(user,0,collectionCachePage*collectionPageSize);
                redisUtil.set(key,cache,1,TimeUnit.HOURS);
                return redisUtil.paginateByPageNum(cache, page, collectionPageSize);
            }
        }
        return (page-1)*collectionPageSize >= getRedisCollectionCount(user)
                ? new ArrayList<>()
                : topicDao.findCollectByUser(user,(page-1)*collectionPageSize, collectionPageSize);
    }

    //添加缓存的topic到收藏列表
    @Async
    public void addRedisCollectionTopics(String user,Topic topic){
        String key = redisUtil.joinKey("topic:collection",user,"cache");
        if(redisUtil.hasKey(key)){
            List<Topic> redisList = redisUtil.getList(key, Topic.class);
            redisList.add(0,topic);
            if(redisList.size() > collectionCachePage*collectionPageSize){
                redisList.remove(redisList.size()-1);
            }
            redisUtil.set(key,redisList);
        }
    }

    //删除缓存的topic收藏
    @Async
    public void removeRedisCollectionTopics(String user,String topicId){
        String key = redisUtil.joinKey("topic:collection",user,"cache");
        if(redisUtil.hasKey(key)){
            List<Topic> redisList = redisUtil.getList(key, Topic.class);
            redisList.removeIf(topic -> topic.getId().equals(topicId));
            if(getRedisCollectionCount(user) >= collectionCachePage * collectionPageSize){
                var add = topicDao.findCollectByUser(user,collectionCachePage * collectionPageSize-1,1);
                if(!add.isEmpty()){
                    redisList.add(add.get(0));
                }
            }
            redisUtil.set(key,redisList);
        }
    }

    public int getRedisCollectionCount(String user){
        int count = 0;
        String key = redisUtil.joinKey("topic:collection",user,"count");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key, Integer.class);
        }
        else{
            count = topicDao.countCollectByUser(user);
            redisUtil.set(key,count,1,TimeUnit.HOURS);
        }
        return count;
    }

    public void setRedisCollectionCount(String user,boolean increment) {
        String key = redisUtil.joinKey("topic:collection",user,"count");
        if (redisUtil.hasKey(key)) {
            redisUtil.incOrDec(increment, key);
        }
    }

    public Collection getRedisCollection(String user, String topicId){
        Collection collection;
        String key = redisUtil.joinKey("topic:collection",user,"mapper",topicId);
        if(redisUtil.hasKey(key)){
            collection = redisUtil.get(key,Collection.class);
        }
        else{
            collection = topicDao.findCollection(user,topicId);
            redisUtil.set(key,collection,1,TimeUnit.HOURS);
        }
        return collection;
    }

    //添加缓存的topic收藏映射
    public void addRedisCollection(Collection collection){
        String key = redisUtil.joinKey("topic:collection",collection.getUser(),"mapper",collection.getTopicId());
        if(redisUtil.hasKey(key)){
            redisUtil.set(key,collection);
        }
    }

    //删除缓存的topic收藏映射
    public void removeRedisCollection(String user,String topicId){
        String key = redisUtil.joinKey("topic:collection",user,"mapper",topicId);
        if(redisUtil.hasKey(key)){
            redisUtil.delete(key);
        }
    }

    //获取缓存的topic收藏数量
    public int getRedisCollectionCountOnTopic(String id){
        int count = 0;
        String key = redisUtil.joinKey("topic:cache",id,"collection:count");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key,Integer.class);
        }
        else{
            count = topicDao.findCollectionTotalById(id);
            redisUtil.set(key,count,1,TimeUnit.HOURS);
        }
        return count;
    }

    //设置缓存的topic收藏数量
    public void setRedisCollectionCountOnTopic(String id,boolean increment) {
        String key = redisUtil.joinKey("topic:cache",id,"collection:count");
        if (redisUtil.hasKey(key)) {
            redisUtil.incOrDec(increment, key);
        }
    }

    //获取缓存的topic图片
    public List<TopicGallery> getRedisTopicGallery(String id){
        String key = redisUtil.joinKey("topic:cache",id,"gallery:data");
        if(redisUtil.hasKey(key)){
            return redisUtil.getList(TopicGallery.class,key);
        }
        else{
            var topicGalleryList = topicDao.findTopicGalleryByTopicId(id);
            redisUtil.set(key,topicGalleryList,1,TimeUnit.HOURS);
            return topicGalleryList;
        }
    }

    //设置缓存的topic图片
    public void setRedisTopicGallery(TopicGallery topicGallery,boolean increment){
        String key = redisUtil.joinKey("topic:cache",topicGallery.getTopicId(),"gallery:data");
        if(redisUtil.hasKey(key)){
            var list = redisUtil.getList(TopicGallery.class,key);
            if(increment){
                list.add(topicGallery);
            }
            else{
                list.removeIf(gallery -> gallery.getId().equals(topicGallery.getId()));
            }
            redisUtil.set(key,list);
        }
    }

    //获取缓存的topic图片数量
    public int getRedisTopicGalleryCount(String id){
        String key = redisUtil.joinKey("topic:cache",id,"gallery:count");
        if(redisUtil.hasKey(key)){
            return redisUtil.get(key,Integer.class);
        }
        else{
            var topicGalleryList = topicDao.findTopicGalleryByTopicId(id);
            redisUtil.set(key,topicGalleryList.size(),1,TimeUnit.HOURS);
            return topicGalleryList.size();
        }
    }

    //设置缓存的topic图片数量
    public void setRedisTopicGalleryCount(String id,boolean increment){
        String key = redisUtil.joinKey("topic:cache",id,"gallery:count");
        if(redisUtil.hasKey(key)){
            redisUtil.incOrDec(increment, key);
        }
    }

    //获取缓存的topic文件
    public List<TopicFile> getRedisTopicFiles(String id){
        String key = redisUtil.joinKey("topic:cache",id,"file:data");
        if(redisUtil.hasKey(key)){
            return redisUtil.getList(TopicFile.class,key);
        }
        else{
            var topicFileList = topicDao.findTopicFilesByTopicId(id);
            redisUtil.set(key,topicFileList,1,TimeUnit.HOURS);
            return topicFileList;
        }
    }

    //设置缓存的topic文件
    public void setRedisTopicFiles(TopicFile topicFile,boolean increment){
        String key = redisUtil.joinKey("topic:cache",topicFile.getTopicId(),"file:data");
        if(redisUtil.hasKey(key)){
            List<TopicFile> list = redisUtil.getList(TopicFile.class,key);
            if(increment){
                list.add(topicFile);
            }
            else{
                list.removeIf(file -> file.getId().equals(topicFile.getId()));
            }
            redisUtil.set(key,list);
        }
    }

    //获取缓存的topic文件数量
    public int getRedisTopicFilesCount(String id){
        String key = redisUtil.joinKey("topic:cache",id,"file:count");
        if(redisUtil.hasKey(key)){
            return redisUtil.get(key, Integer.class);
        }
        else{
            var topicFileList = topicDao.findTopicFilesByTopicId(id);
            redisUtil.set(key,topicFileList.size(),1,TimeUnit.HOURS);
            return topicFileList.size();
        }
    }

    //设置缓存的topic文件数量
    public void setRedisTopicFilesCount(String id,boolean increment) {
        String key = redisUtil.joinKey("topic:cache",id,"file:count");
        if (redisUtil.hasKey(key)) {
            redisUtil.incOrDec(increment,key);
        }
    }

    //获取缓存的topic评论
    public List<CommentVO> getRedisComments(String id,int page){
        String key = redisUtil.joinKey("topic:cache",id,"comment:data");
        List<CommentVO> comments;
        if(page <= commentCachePage){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(CommentVO.class,key);
                comments = redisUtil.paginateByPageNum(cache,page,commentCachePage);
            }
            else{
                var cache = topicDao.findCommentByTopicId(id,0,commentCachePage*commentPageSize);
                redisUtil.set(key,cache,1,TimeUnit.HOURS);
                comments = redisUtil.paginateByPageNum(cache,page,commentCachePage);
            }
        }
        else{
            comments = topicDao.findCommentByTopicId(id,(page-1)*commentPageSize,commentPageSize);
        }
        return comments;
    }

    //设置缓存的topic评论
    @Async
    public void setRedisComments(CommentVO comment,boolean increment,int count){
        String key = redisUtil.joinKey("topic:cache",comment.getTopicId(),"comment:data");
        if(redisUtil.hasKey(key)){
            List<CommentVO> list = redisUtil.getList(CommentVO.class,key);
            boolean isChange = false;
            if(increment){
                if(count < commentCachePage * commentPageSize){
                    User user = userRedis.findRedisUser(comment.getUser());
                    UserPersonalized personalized = userRedis.findRedisUserPersonalized(comment.getUser());
                    comment.setName(user.getName());
                    comment.setAvatar(user.getAvatar());
                    comment.setLevel(user.getLevel());
                    comment.setExp(user.getExp());
                    comment.setSignature(personalized.getSignature());
                    list.add(comment);
                    isChange = true;
                }
            }
            else{
                isChange = list.removeIf(c -> c.getDate().equals(comment.getDate()) && c.getUser().equals(comment.getUser()));
                if(isChange){
                    var add = topicDao.findCommentByTopicId(comment.getTopicId(),commentCachePage*commentPageSize-1,1);
                    if(!add.isEmpty()){
                        list.add(add.get(0));
                    }
                }
            }
            if(isChange){
                redisUtil.set(key,list);
            }
        }
    }

    //获取缓存的topic评论数量
    public int getRedisCommentsCount(String id){
        int count = 0;
        String key = redisUtil.joinKey("topic:cache",id,"comment:count");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key, Integer.class);
        }
        else{
            count = topicDao.findCommentCountByTopicId(id);
            redisUtil.set(key,count,1,TimeUnit.HOURS);
        }
        return count;
    }

    //设置缓存的topic评论数量
    public void setRedisCommentsCount(String id,boolean increment){
        String key = redisUtil.joinKey("topic:cache",id,"comment:count");
        if (redisUtil.hasKey(key)) {
            redisUtil.incOrDec(increment,key);
        }
    }

    //获取缓存的topic评论顶部
    public List<CommentVO> getRedisTopComment(String id){
        String key = redisUtil.joinKey("topic:cache",id,"comment:top");
        List<CommentVO> topComments;
        if(redisUtil.hasKey(key)){
            topComments = redisUtil.getList(CommentVO.class,key);
        }
        else{
            topComments = topicDao.findCommentTopByTopicId(id);
            redisUtil.set(key,topComments,1,TimeUnit.HOURS);
        }
        return topComments;
    }

    //设置缓存的topic评论顶部
    public void setRedisTopComment(String id){
        String key = redisUtil.joinKey("topic:cache",id,"comment:top");
        if(redisUtil.hasKey(key)){
            redisUtil.delete(key);
        }
    }

    //获取缓存的用户topic
    public List<Topic> getRedisUserTopics(String user,int page,boolean self){
        List<Topic> list;
        String key = redisUtil.joinKey("topic:user",user,"self",String.valueOf(self),"data");
        if(page <= userTopicCachePage){
            if(redisUtil.hasKey(key)){
                var cache = redisUtil.getList(key,Topic.class);
                list = redisUtil.paginateByPageNum(cache,page,userTopicPageSize);
            }
            else{
                var cache = self
                        ? topicDao.findTopicByUser(user,0,userTopicCachePage*userTopicPageSize)
                        : topicDao.findTopicByUserWhereDisplay(user,0,userTopicCachePage*userTopicPageSize);
                redisUtil.set(key,cache,1,TimeUnit.HOURS);
                list = redisUtil.paginateByPageNum(cache,page,userTopicPageSize);
            }
        }
        else{
            list = self
                    ? topicDao.findTopicByUser(user,(page-1)*userTopicPageSize,userTopicPageSize)
                    : topicDao.findTopicByUserWhereDisplay(user,(page-1)*userTopicPageSize,userTopicPageSize);
        }
        return list;
    }

    //设置缓存的用户topic
    @Async
    public void setRedisUserTopic(String user){
        redisUtil.delete("topic:user",user,"self:true:data");
        redisUtil.delete("topic:user",user,"self:false:data");
    }

    //获取缓存的用户topic数量
    public int getRedisUserTopicsCount(String user,boolean self){
        int count = 0;
        String key = redisUtil.joinKey("topic:user",user,"self",String.valueOf(self),"count");
        if(redisUtil.hasKey(key)){
            count = redisUtil.get(key, Integer.class);
        }
        else{
            count = self
                    ? topicDao.findTopicTotalByUser(user)
                    : topicDao.findTopicTotalByUserWhereDisplay(user);
            redisUtil.set(key,count,1,TimeUnit.HOURS);
        }
        return count;
    }

    //设置缓存的用户topic数量
    public void setRedisUserTopicCount(String user,boolean increment,Topic topic){
        String key = redisUtil.joinKey("topic:user",user,"self:true:count");
        if(topic != null && redisUtil.hasKey(key)){
            redisUtil.incOrDec(increment,key);
        }
        redisUtil.delete("topic:user",user,"self:false:count");
    }
}
