package com.example.file_storage_backend.entity;

import javax.persistence.*;

@Entity
public class FileInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(unique = true)
    String filename;
    Long size;
    String owner;
    Boolean pub;

    public FileInformation() {
    }

    public FileInformation(String filename, String owner, Long size, Boolean pub) {
        this.filename = filename;
        this.size = size;
        this.owner = owner;
        this.pub = pub;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPub() {
        return pub;
    }

    public void setPub(Boolean pub) {
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
}
