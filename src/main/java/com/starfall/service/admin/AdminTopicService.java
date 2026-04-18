package com.starfall.service.admin;

import com.starfall.dao.admin.AdminTopicDao;
import com.starfall.entity.*;
import com.starfall.service.FileService;
import com.starfall.util.CodeUtil;
import com.starfall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class AdminTopicService {
    @Autowired
    private AdminTopicDao topicDao;
    @Autowired
    FileService fileService;
    @Autowired
    RedisUtil redisUtil;

    public ResultMsg findAllTopic(int page, String keyword) {
        keyword = "%" + keyword + "%";
        List<TopicOut> topics = topicDao.findAllTopic((page - 1) * 10, keyword);
        int count = topicDao.countTopic(keyword);
        return ResultMsg.success(topics, count);
    }

    @Transactional
    public ResultMsg addTopic(TopicOut topicOut) {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (topicDao.existTopicById(topicOut.getId()) == 0) {
            if (topicOut.getId().isEmpty()) {
                topicOut.setId(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6));
            }

            Topic topic = new Topic(
                    topicOut.getId(),
                    topicOut.getTitle(),
                    topicOut.getLabel(),
                    topicOut.getUser(),
                    null,
                    null,
                    date,
                    topicOut.getView(),
                    0,
                    topicOut.getVersion(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    topicOut.getDisplay(),
                    topicOut.getBelong(),
                    topicOut.getIsFirstPublic()
            );
            String filename = topicOut.getId() + ".md";
            String fileFolder = "user/" + topicOut.getUser() + "/topic/" + topicOut.getId();
            MultipartFile file = new MultipartFileImpl(topicOut.getContent(), filename);
            fileService.upload(file, fileFolder, filename);
            TopicItem topicItem = new TopicItem(topicOut.getId(), topicOut.getTopicTitle(), topicOut.getEnTitle(), topicOut.getSource(), topicOut.getAuthor(), topicOut.getLanguage(), topicOut.getAddress(), topicOut.getDownload(), fileFolder + "/" + filename);
            redisUtil.deleteBatchAsync("topic:*");
            int status1 = topicDao.addTopic(topic);
            int status2 = topicDao.addTopicItem(topicItem);
            return status1 + status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_ERROR");
    }

    @Transactional
    public ResultMsg updateTopic(TopicOut topicOut) {
        if (topicDao.existTopicById(topicOut.getId()) == 1) {
            TopicOut oldTopic = topicDao.findTopicById(topicOut.getId());
            Topic topic = new Topic(
                    topicOut.getId(),
                    topicOut.getTitle(),
                    topicOut.getLabel(),
                    topicOut.getUser(),
                    null,
                    null,
                    topicOut.getDate(),
                    topicOut.getView(),
                    0,
                    topicOut.getVersion(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    topicOut.getDisplay(),
                    topicOut.getBelong(),
                    topicOut.getIsFirstPublic()
            );
            String filename = topicOut.getId() + ".md";
            String fileFolder = "user/" + topicOut.getUser() + "/topic/" + topicOut.getId();
            MultipartFile file = new MultipartFileImpl(topicOut.getContent(), filename);
            if (!Objects.equals(oldTopic.getUser(), topicOut.getUser())) {
                fileService.upload(file, "user/" + oldTopic.getUser() + "/topic/" + oldTopic.getId(), filename);
                fileService.move("user/" + oldTopic.getUser() + "/topic/" + oldTopic.getId(), fileFolder);
            } else {
                fileService.upload(file, fileFolder, filename);
            }
            TopicItem topicItem = new TopicItem(topicOut.getId(), topicOut.getTopicTitle(), topicOut.getEnTitle(), topicOut.getSource(), topicOut.getAuthor(), topicOut.getLanguage(), topicOut.getAddress(), topicOut.getDownload(), fileFolder + "/" + filename);
            int status1 = topicDao.updateTopic(topic);
            int status2 = topicDao.updateTopicItem(topicItem);
            redisUtil.deleteBatchAsync("topic:*");
            return status1 + status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    @Transactional
    public ResultMsg deleteTopic(String id) {
        if (topicDao.existTopicById(id) == 1) {
            TopicOut topic = topicDao.findTopicById(id);
            int status1 = topicDao.deleteTopicItem(id);
            int status2 = topicDao.deleteTopic(id);
            topicDao.deleteTopicFileByTopicId(id);
            topicDao.deleteTopicGalleryByTopicId(id);
            fileService.removeFolder("user/" + topic.getUser() + "/topic/" + topic.getId());
            redisUtil.deleteBatchAsync("topic:*");
            return status1 + status2 == 2 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg findAllTopicComment(String id, int page, String keyword) {
        keyword = "%" + keyword + "%";
        List<Comment> topics = topicDao.findTopicCommentById(id, (page - 1) * 10, keyword);
        int count = topicDao.countTopicCommentById(id, keyword);
        return ResultMsg.success(topics, count);
    }

    public ResultMsg findAllTopicSelect(String keyword) {
        List<Topic> topics = topicDao.findAllTopicSelect("%" + keyword + "%", 10);
        return ResultMsg.success(topics);
    }

    @Transactional
    public ResultMsg addTopicComment(Comment comment) {
        if (topicDao.existComment(comment.getTopicId(), comment.getUser(), comment.getDate()) == 0) {
            int status = topicDao.addComment(comment);
            redisUtil.deleteBatchAsync("topic:*");
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_ERROR");
    }

    @Transactional
    public ResultMsg updateTopicComment(Comment comment) {
        if (topicDao.existComment(comment.getOldTopicId(), comment.getOldUser(), comment.getOldDate()) == 1) {
            int status = topicDao.updateComment(comment);
            redisUtil.deleteBatchAsync("topic:*");
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    @Transactional
    public ResultMsg deleteTopicComment(Comment comment) {
        if (topicDao.existComment(comment.getTopicId(), comment.getUser(), comment.getDate()) == 1) {
            int status = topicDao.deleteComment(comment);
            redisUtil.deleteBatchAsync("topic:*");
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg fndAllTopicLikeItem(int page, String keyword) {
        keyword = "%" + keyword + "%";
        return ResultMsg.success(topicDao.findAllTopicLikeItem((page - 1) * 10, keyword), topicDao.countTopic(keyword));
    }

    public ResultMsg findTopicItemById(String id, int page, String keyword) {
        keyword = "%" + keyword + "%";
        return ResultMsg.success(topicDao.findLikeItemByTopicId(id, (page - 1) * 10, keyword), topicDao.countLikeItemByTopicId(id, keyword));
    }

    @Transactional
    public ResultMsg addLikeLog(LikeLog likeLog) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(df);
        likeLog.setDate(date);
        redisUtil.deleteBatchAsync("topic:*");
        if (topicDao.existLikeItemOutStatus(likeLog.getTopicId(), likeLog.getUser()) == 0) {
            int status = topicDao.addLikeItem(likeLog);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        if (topicDao.existLikeItem(likeLog.getTopicId(), likeLog.getUser(), 0) == 1) {
            int status = topicDao.updateLikeItem(likeLog);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        if (topicDao.existLikeItem(likeLog.getTopicId(), likeLog.getUser(), likeLog.getStatus()) == 1) {
            return ResultMsg.error("LIKE_LOG_EXIST_ERROR");
        }
        int status = topicDao.updateLikeItem(likeLog);
        return status == 1 ? ResultMsg.warning("LIKE_STATUS_UPDATE") : ResultMsg.error("DATABASE_ERROR");
    }

    @Transactional
    public ResultMsg updateLikeLog(LikeLog likeLog) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = LocalDateTime.now().format(df);
        likeLog.setDate(date);
        if (topicDao.existLikeItemOutStatus(likeLog.getTopicId(), likeLog.getUser()) == 1) {
            int status = topicDao.updateLikeItem(likeLog);
            redisUtil.deleteBatchAsync("topic:*");
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg findAllUserCollection(String user, int page) {
        List<TopicCollection> topics = topicDao.findCollectByUser(user, (page - 1) * 10);
        return ResultMsg.success(topics, topicDao.countCollectByUser(user));
    }

    @Transactional
    public ResultMsg addUserCollection(Collection collection) {
        if (topicDao.existCollection(collection.getTopicId(), collection.getUser()) == 0) {
            LocalDateTime ldt = LocalDateTime.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date;
            try{
                date = LocalDateTime.parse(collection.getDate(), df).format(df);
            }catch (Exception e){
                date = ldt.format(df);
            }
            redisUtil.deleteBatchAsync("topic:*");
            int status = topicDao.addCollection(collection.getTopicId(), collection.getUser(),date);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("EXIST_ERROR");
    }

    @Transactional
    public ResultMsg deleteUserCollection(String id, String user) {
        if (topicDao.existCollection(id, user) == 1) {
            int status = topicDao.deleteCollection(user, id);
            redisUtil.deleteBatchAsync("topic:*");
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATABASE_ERROR");
        }
        return ResultMsg.error("NOT_EXIST_ERROR");
    }

    public ResultMsg findAllTopicGallery(String id) {
        List<TopicGallery> galleries = topicDao.findAllTopicGallery(id);
        return ResultMsg.success(galleries);
    }

    @Transactional
    public ResultMsg addTopicGallery(String id,String label,String user,String imgBase64) {
        int total = topicDao.countTopicGalleryByTopicId(id);
        if(total >= 10){
            return ResultMsg.error("GALLERY_FULL");
        }
        TopicOut topicOut = topicDao.findTopicById(id);
        LocalDateTime ldt = LocalDateTime.now();
        String galleryId = "tg"+ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6);
        String filename = galleryId+".png";
        String fileFolder = "user/"+topicOut.getUser()+"/topic/"+id+"/gallery/";
        MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(imgBase64),filename);
        ResultMsg uploadResult = fileService.upload(file,fileFolder,filename);
        if(uploadResult.getMsg().equals("SUCCESS")){
            TopicGallery topicGallery = new TopicGallery(galleryId,id,user,ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),fileFolder+filename,label);
            topicDao.insertTopicGallery(topicGallery);
            redisUtil.deleteBatchAsync("topic:*");
            return ResultMsg.success(topicGallery);
        }
        return ResultMsg.error("UPLOAD_ERROR");
    }

    @Transactional
    public ResultMsg deleteTopicGallery(String id) {
        TopicGallery topicGallery = topicDao.findTopicGalleryById(id);
        if (topicGallery != null) {
            int status = topicDao.deleteTopicGalleryById(id);
            if (status == 1) {
                fileService.removeFile(topicGallery.getPath());
                redisUtil.deleteBatchAsync("topic:*");
                return ResultMsg.success();
            }
            return ResultMsg.error("DELETE_ERROR");
        }
        return ResultMsg.error("NO_EXIST_GALLERY");
    }

    public ResultMsg findAllTopicFile(String id) {
        return ResultMsg.success(topicDao.findAllTopicFile(id));
    }

    @Transactional
    public ResultMsg addTopicFile(String id, String fileName, String fileLabel, String fileBase64) {
        int total = topicDao.countTopicFileByTopicId(id);
        if(total >= 3){
            return ResultMsg.error("FILE_FULL");
        }
        TopicOut topicOut = topicDao.findTopicById(id);
        if(topicOut == null){
            return ResultMsg.error("NO_EXIST_TOPIC");
        }
        TopicFile topicFile = TopicFile.parseTopicFileObj(fileName,id,fileLabel,topicOut.getUser(),fileBase64);
        String filename = topicFile.getId();
        String fileFolder = "user/"+topicOut.getUser()+"/topic/"+id+"/file/";
        MultipartFile file = new MultipartFileImpl(CodeUtil.getBase64Bytes(fileBase64),filename);
        fileService.upload(file,fileFolder,filename);
        int status = topicDao.insertTopicFile(topicFile);
        redisUtil.deleteBatchAsync("topic:*");
        return status == 1 ? ResultMsg.success(topicFile) : ResultMsg.error("UPLOAD_ERROR");
    }

    @Transactional
    public ResultMsg deleteTopicFile(String id) {
        TopicFile topicFile = topicDao.findTopicFileById(id);
        if (topicFile != null) {
            int status = topicDao.deleteTopicFileById(id);
            if (status == 1) {
                fileService.removeFile("user/"+topicFile.getUser()+"/topic/"+topicFile.getTopicId()+"/file/"+topicFile.getId());
                redisUtil.deleteBatchAsync("topic:*");
                return ResultMsg.success();
            }
            return ResultMsg.error("DELETE_ERROR");
        }
        return ResultMsg.error("NO_EXIST_GALLERY");
    }


}
