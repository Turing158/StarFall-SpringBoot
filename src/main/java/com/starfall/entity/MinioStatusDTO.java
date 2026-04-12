package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioStatusDTO {
    private boolean connected;
    private String errorMessage;
    private String timestamp;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BucketInfo {
        private String name;
        private ZonedDateTime creationDate;
        private long totalObjects;
        private long totalSize;
        private List<ObjectInfo> objects;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ObjectInfo {
        private String name;
        private long size;
        private ZonedDateTime lastModified;
        private String etag;
        private String contentType;
        private Map<String, String> userMetadata;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ObjectPageResult {
        private List<MinioStatusDTO.ObjectInfo> objects;
        private String nextStartAfter;// 下一页的起始标记
        private boolean hasNext;
        private int totalCount;// 可选：总对象数（需额外统计）
    }
}
