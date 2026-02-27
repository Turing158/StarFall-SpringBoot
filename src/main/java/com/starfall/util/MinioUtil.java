package com.starfall.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.starfall.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MinioUtil {
    @Autowired
    private MinioConfig prop;

    @Resource
    private MinioClient minioClient;

    public void checkBucket(String bucketName) throws Exception {
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    public String upload(String folder, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException();
        }
        String fileName = IdUtil.simpleUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = folder + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + fileName;
        try {
            checkBucket(prop.getBucketName());
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(prop.getBucketName()).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }

    public String upload(String folder,String fileName, MultipartFile file) {
        String filePath = folder + "/" + fileName;
        try {
            checkBucket(prop.getBucketName());
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(prop.getBucketName()).object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }

    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException();
        }
        String fileName = IdUtil.simpleUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + fileName;
        try {
            checkBucket(prop.getBucketName());
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(prop.getBucketName()).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }

    public String uploadFile(String bucketName, String fileName, MultipartFile multipartFile) throws IOException {
        String url = "";
        try (InputStream inputStream = multipartFile.getInputStream()) {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                checkBucket(bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                String READ_WRITE = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(READ_WRITE).build());
            }
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, multipartFile.getSize(), -1)
                    .contentType(multipartFile.getContentType()).build()
            );
            //路径获取
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).
                    method(Method.GET).build());
            url = url.substring(0, url.indexOf('?'));
            //常规访问路径获取
            return url;
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public String preview(String fileName) {
        GetPresignedObjectUrlArgs build =
                new GetPresignedObjectUrlArgs()
                        .builder()
                        .bucket(prop.getBucketName())
                        .object(fileName)
                        .method(Method.GET)
                        .build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean remove(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(prop.getBucketName()).object(fileName).build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteFolder(String folderPath) {
        try {
            String normalizedPath = normalizeFolderPath(folderPath);
            List<DeleteObject> objectsToDelete = getObjectsInFolderWithDeleteObject(prop.getBucketName(), normalizedPath);
            if (objectsToDelete.isEmpty()) {
                System.out.println("Folder is empty or does not exist: " + normalizedPath);
                return true;
            }
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(prop.getBucketName())
                            .objects(objectsToDelete)
                            .build()
            );
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                System.err.println("Error deleting object: " + error.objectName() +
                        " - " + error.message());
            }
            System.out.println("Successfully deleted folder: " + normalizedPath +
                    " with " + objectsToDelete.size() + " objects");
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting folder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean moveFolder(String sourceFolder, String targetFolder) {
        try {
            // 规范化文件夹路径
            String normalizedSource = normalizeFolderPath(sourceFolder);
            String normalizedTarget = normalizeFolderPath(targetFolder);

            System.out.println("Moving folder from: " + normalizedSource + " to: " + normalizedTarget);

            // 获取源文件夹下的所有对象
            List<Item> sourceObjects = getObjectsInFolderWithItem(prop.getBucketName(), normalizedSource);

            if (sourceObjects.isEmpty()) {
                System.out.println("Source folder is empty or does not exist: " + normalizedSource);
                return false;
            }

            // 复制所有对象到新位置
            boolean copySuccess = copyObjects(prop.getBucketName(), sourceObjects, normalizedSource, normalizedTarget);

            if (copySuccess) {
                // 删除源对象
                boolean deleteSuccess = deleteObjects(prop.getBucketName(), sourceObjects);

                if (deleteSuccess) {
                    System.out.println("Successfully moved folder: " + normalizedSource +
                            " to: " + normalizedTarget +
                            " (" + sourceObjects.size() + " objects)");
                    return true;
                } else {
                    System.err.println("Failed to delete source objects after copying");
                    // 可以考虑回滚操作，删除已复制的目标对象
                    rollbackCopy(prop.getBucketName(), sourceObjects, normalizedSource, normalizedTarget);
                    return false;
                }
            } else {
                System.err.println("Failed to copy objects to target location");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error moving folder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制对象到新位置
     */
    private boolean copyObjects(String bucketName, List<Item> sourceObjects,
                                String sourcePrefix, String targetPrefix) {
        try {
            for (Item item : sourceObjects) {
                String sourceObjectName = item.objectName();
                String targetObjectName = sourceObjectName.replace(sourcePrefix, targetPrefix);

                // 执行复制操作
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(targetObjectName)
                                .source(
                                        CopySource.builder()
                                                .bucket(bucketName)
                                                .object(sourceObjectName)
                                                .build())
                                .build()
                );

                System.out.println("Copied: " + sourceObjectName + " → " + targetObjectName);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error copying objects: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除源对象
     */
    private boolean deleteObjects(String bucketName, List<Item> objectsToDelete) {
        try {
            List<DeleteObject> objectNames = new ArrayList<>();
            for (Item item : objectsToDelete) {
                objectNames.add(new DeleteObject(item.objectName()));
            }

            // 批量删除
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objectNames)
                            .build()
            );

            // 检查是否有删除错误
            boolean hasErrors = false;
            for (Result<io.minio.messages.DeleteError> result : results) {
                io.minio.messages.DeleteError error = result.get();
                if (error != null) {
                    System.err.println("Error deleting object: " + error.objectName());
                    hasErrors = true;
                }
            }

            return !hasErrors;
        } catch (Exception e) {
            System.err.println("Error deleting objects: " + e.getMessage());
            return false;
        }
    }

    /**
     * 回滚操作：删除已复制的目标对象
     */
    private void rollbackCopy(String bucketName, List<Item> sourceObjects,
                              String sourcePrefix, String targetPrefix) {
        try {
            List<DeleteObject> objectsToDelete = new ArrayList<>();

            for (Item item : sourceObjects) {
                String sourceObjectName = item.objectName();
                String targetObjectName = sourceObjectName.replace(sourcePrefix, targetPrefix);
                objectsToDelete.add(new DeleteObject(targetObjectName));
            }

            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objectsToDelete)
                            .build()
            );

            System.out.println("Rollback completed: deleted copied objects in target location");
        } catch (Exception e) {
            System.err.println("Error during rollback: " + e.getMessage());
        }
    }

    private List<Item> getObjectsInFolderWithItem(String bucketName, String folderPath) throws Exception {
        List<Item> objects = new ArrayList<>();

        ListObjectsArgs listArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(folderPath)
                .recursive(true)
                .build();

        Iterable<Result<Item>> results = minioClient.listObjects(listArgs);

        for (Result<Item> result : results) {
            objects.add(result.get());
        }

        return objects;
    }

    private List<DeleteObject> getObjectsInFolderWithDeleteObject(String bucketName, String folderPath) throws Exception {
        List<DeleteObject> objects = new ArrayList<>();

        ListObjectsArgs listArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(folderPath)
                .recursive(true)
                .build();

        Iterable<Result<Item>> results = minioClient.listObjects(listArgs);

        for (Result<Item> result : results) {
            Item item = result.get();
            objects.add(new DeleteObject(item.objectName()));
        }

        return objects;
    }


    private String normalizeFolderPath(String folderPath) {
        if (folderPath == null || folderPath.trim().isEmpty()) {
            return "";
        }

        String normalized = folderPath.trim();
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }

        return normalized;
    }


}

