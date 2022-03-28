package com.example.file_storage_backend.dto;

public class FileInformationDto {
    String filename;
    Long size;
    String owner;
    Boolean pub;

    public FileInformationDto(String filename, Long size, String owner, Boolean pub) {
        this.filename = filename;
        this.size = size;
        this.owner = owner;
        this.pub = pub;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getPub() {
        return pub;
    }

    public void setPub(Boolean pub) {
        this.pub = pub;
    }
}
