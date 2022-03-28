package com.example.file_storage_backend.repository;

import com.example.file_storage_backend.entity.FileInformation;
import com.example.file_storage_backend.jpa.JPAContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped
public class FileRepository {

    final String FILE_NAME_ATTR_NAME = "filename";
    final String OWNER_ATTR_NAME = "owner";
    final String IS_PUBLIC_ATTR_NAME = "pub";

    EntityManager entityManager;

    @PostConstruct
    public void init(){
        entityManager= JPAContext.getEntityManager();
    }

    public boolean addFile(String filename, String username, Long size, Boolean pub){
        FileInformation information = new FileInformation(filename, username, size, pub);
        if(existsByName(filename)) return false;
        return makeTransaction(information);
    }

    private synchronized boolean makeTransaction(FileInformation information){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(information);
            commitTransactionIfActive(transaction);
            return true;
        } catch (EntityExistsException|RollbackException exception){
            return false;
        }
    }

    private void commitTransactionIfActive(EntityTransaction transaction){
        if(transaction.isActive()){
            transaction.commit();
        }
    }

    private boolean existsByName(String name){
        return getByName(name)!=null;
    }

    public FileInformation getByName(String name){
        List<FileInformation> optionalResult = getAllByOneAttr(FILE_NAME_ATTR_NAME, name);
        if(optionalResult.isEmpty()) return null;
        return optionalResult.get(0);
    }

    public List<FileInformation> getAllByOwner(String owner){
        return getAllByOneAttr(OWNER_ATTR_NAME, owner);
    }

    public List<FileInformation> getAllByPublic(Boolean pub){
        return getAllByOneAttr(IS_PUBLIC_ATTR_NAME, pub);
    }

    private List<FileInformation> getAllByOneAttr(String attributeName, Object requiredValue){
        CriteriaBuilder cb = createCriteriaBuilder();
        CriteriaQuery<FileInformation> cq = createCriteriaQuery(cb);
        Root<FileInformation> fileInfo = extractRoot(cq);
        cq.select(fileInfo).where(cb.equal(fileInfo.get(attributeName), requiredValue));
        return entityManager.createQuery(cq).getResultList();
    }

    private CriteriaBuilder createCriteriaBuilder(){
        return entityManager.getCriteriaBuilder();
    }

    private CriteriaQuery<FileInformation> createCriteriaQuery(CriteriaBuilder cb){
        return cb.createQuery(FileInformation.class);
    }

    private Root<FileInformation> extractRoot(CriteriaQuery<FileInformation> cq){
        return cq.from(FileInformation.class);
    }
}
