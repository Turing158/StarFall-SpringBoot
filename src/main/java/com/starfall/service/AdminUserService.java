package com.starfall.service;

import com.starfall.dao.AdminHomeDao;
import com.starfall.dao.AdminMessageDao;
import com.starfall.dao.AdminTopicDao;
import com.starfall.dao.AdminUserDao;
import com.starfall.entity.MultipartFileImpl;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.SignIn;
import com.starfall.entity.User;
import com.starfall.util.AECSecureUtil;
import com.starfall.util.CodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class AdminUserService {
    @Autowired
    private AdminUserDao userDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private AdminTopicDao topicDao;
    @Autowired
    private AdminHomeDao homeDao;
    @Autowired
    private AdminMessageDao messageDao;


    public ResultMsg findAllUsersForSelect(String keyword) {
        List<User> users;
        int count;
        int num = 20;
        if(keyword == null || keyword.isEmpty()){
            users = userDao.findAllUser(num);
            count = userDao.countAllUser();
        }
        else{
            users = userDao.findAllUserByUserOrName("%"+keyword+"%",num);
            count = userDao.countAllUserByUserOrName("%"+keyword+"%");
        }
        List<User> newUsers = new ArrayList<>();
        users.forEach(user -> {
            User newUser = new User();
            newUser.setUser(user.getUser());
            newUser.setName(user.getName());
            newUsers.add(newUser);
        });
        return ResultMsg.success(newUsers,count);
    }

    public ResultMsg findAllUsers(int page,String keyword) {
        keyword = "%" + keyword + "%";
        List<User> users = userDao.findUserByPage((page-1)*10,keyword);
        users.forEach(user -> {
            user.setPassword("***");
        });
        return ResultMsg.success(users,userDao.countUser(keyword));
    }

    @Autowired
    AECSecureUtil aecSecureUtil;

    public ResultMsg insertUser(User user) {
        if(userDao.existUser(user.getUser()) == 0){
            if(userDao.existEmail(user.getEmail()) == 0){
                user.setAvatar("default.png");
                user.setPassword(aecSecureUtil.encrypt(user.getPassword()));
                int status = userDao.insertUser(user);
                return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
            }
            return ResultMsg.error("EMAIL_EXIST");
        }
        return ResultMsg.error("USER_EXIST");
    }


    public ResultMsg updateUser(User user,String oldUser,String oldEmail) {
        if(userDao.existUser(oldUser) == 1){
            if(Objects.equals(user.getUser(), oldUser) || userDao.existUser(user.getUser()) == 0){
                if(Objects.equals(user.getEmail(), oldEmail) || userDao.existEmail(user.getEmail()) == 0){
                    if(!user.getPassword().equals("******")){
                        user.setPassword(aecSecureUtil.encrypt(user.getPassword()));
                        userDao.updatePassword(user);
                    }
                    int status1 = userDao.updateUser(user);

                    return status1 == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
                }
                return ResultMsg.error("EMAIL_EXIST");
            }
            return ResultMsg.error("USER_EXIST");
        }
        return ResultMsg.error("USER_NOT_EXIST");
    }

    public ResultMsg deleteUser(String user) {
        if(userDao.existUser(user) == 1){
            int status = userDao.deleteUser(user);
            userDao.deleteSignInByUser(user);
            //删除所有有关主题的东西
            for (String topicId : topicDao.findAllTopicId(user)){
                topicDao.deleteTopicItemByTopicId(topicId);
            }
            topicDao.deleteTopicByUser(user);
            topicDao.deleteCommentByUser(user);
            topicDao.deleteLikeLogByUser(user);
            topicDao.deleteCollectionOnlyUser(user);
            topicDao.deleteTopicGalleryByUser(user);

            homeDao.deleteHomeTalkByUser(user);
            messageDao.deleteMessageByUser(user);
            fileService.removeFolder("user/"+user);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("USER_NOT_EXIST");
    }



    public ResultMsg updateAvatar(String user,String avatar){
        if(avatar.equals("default.png")){
            userDao.updateAvatar(user,"default.png");
            return ResultMsg.success("default.png");
        }
        User userObj = userDao.findUserByUser(user);
        String oldAvatar = userObj.getAvatar();
        String avatarOutHead = "data:image/png;base64,";
        if(avatar.startsWith(avatarOutHead)){
            avatar = avatar.substring(avatarOutHead.length());
        }
        byte[] bytes = Base64.getDecoder().decode(avatar);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        String avatarName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + CodeUtil.getCode(6);
        String fileName = avatarName + ".png";
        String folder = "user/"+user+"/avatar";
        MultipartFile file = new MultipartFileImpl(bytes,fileName);
        fileService.upload(file,folder,fileName);
        if(!oldAvatar.equals("default.png")){
            fileService.removeFile(oldAvatar);
        }
        userDao.updateAvatar(user,folder+"/"+fileName);
        return ResultMsg.success(fileName);
    }

    public ResultMsg findAllSignIn(int page,String keyword){
        keyword = "%" + keyword + "%";
        return ResultMsg.success(userDao.findSignInByPage((page-1)*10,keyword),userDao.countSignIn(keyword));
    }

    public ResultMsg appendSignIn(SignIn signIn){
        if(userDao.existSignIn(signIn.getUser(),signIn.getDate()) == 0){
            int status = userDao.insertSignIn(signIn);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("SIGN_IN_EXIST");
    }

    public ResultMsg updateSignIn(SignIn signIn){
        int status = userDao.updateSignIn(signIn);
        return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
    }


    public ResultMsg deleteSignIn(SignIn signIn){
        if(userDao.existSignIn(signIn.getUser(),signIn.getDate()) == 1){
            int status = userDao.deleteSignIn(signIn);
            return status == 1 ? ResultMsg.success() : ResultMsg.error("DATASOURCE_ERROR");
        }
        return ResultMsg.error("SIGN_IN_NOT_EXIST");
    }



}


