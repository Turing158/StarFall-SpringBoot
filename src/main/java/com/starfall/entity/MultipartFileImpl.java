package com.starfall.entity;

import com.starfall.util.CodeUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultipartFileImpl implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public MultipartFileImpl(String content) {
        this("file", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS"))+ CodeUtil.getCode(8), "text/plain", content);
    }

    public MultipartFileImpl(String content, String filename) {
        this("file", filename, "text/plain", content);
    }

    public MultipartFileImpl(byte[] content, String filename){
        this.name = "file";
        this.originalFilename = filename;
        this.contentType = "application/octet-stream";
        this.content = content;
    }

    public MultipartFileImpl(String name, String originalFilename, String contentType, String content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
