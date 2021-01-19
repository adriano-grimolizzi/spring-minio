package com.example.uploadingfiles.model;

import org.springframework.data.annotation.Id;

public class Metadata {

    @Id
    public String id;

    public String fileName;
    public String contentType;

    public Metadata() {
    }

    public Metadata(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return String.format(
                "Metadata[id=%s, fileName='%s', type='%s']",
                id, fileName, contentType);
    }

}