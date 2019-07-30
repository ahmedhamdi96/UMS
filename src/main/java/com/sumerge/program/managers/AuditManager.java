package com.sumerge.program.managers;

import com.sumerge.program.entities.Audit;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.Date;

@Stateless
public class AuditManager {

    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;

    public void createAudit(String author, String action, String entity){
        Audit audit = new Audit(author, action, new Timestamp(new Date().getTime()), entity);
        entityManager.persist(audit);
    }
}
