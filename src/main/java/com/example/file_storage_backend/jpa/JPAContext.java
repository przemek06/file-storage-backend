package com.example.file_storage_backend.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAContext {
    private final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("file-upload");

    public static EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
}
