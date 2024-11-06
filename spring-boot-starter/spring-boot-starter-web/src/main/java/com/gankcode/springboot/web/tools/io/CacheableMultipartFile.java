package com.gankcode.springboot.web.tools.io;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;


public class CacheableMultipartFile implements MultipartFile {

    private final MultipartFile origin;

    @Getter
    private final File cache;

    public CacheableMultipartFile(MultipartFile origin, File cache) {
        this.origin = origin;
        this.cache = cache;
    }

    @Override
    public String getName() {
        return origin == null ? null : origin.getName();
    }

    @Override
    public String getOriginalFilename() {
        return origin == null ? null : origin.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        return origin == null ? null : origin.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return origin == null || origin.isEmpty();
    }

    @Override
    public long getSize() {
        return origin == null ? 0 : origin.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return origin == null ? null : origin.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (origin == null) {
            return null;
        }
        if (cache == null) {
            return origin.getInputStream();
        }
        return new CacheableFileInputStream(origin.getInputStream(), cache);
    }

    @Override
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        transferTo(dest.toPath());
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        MultipartFile.super.transferTo(dest);
    }
}
