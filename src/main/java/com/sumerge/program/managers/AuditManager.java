package com.sumerge.program.managers;

import com.sumerge.program.entities.Audit;
import org.apache.log4j.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.Date;

@Stateless
public class AuditManager {
    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
    private final static Logger LOGGER = Logger.getLogger(AuditManager.class);

    public void createAudit(String author, String action, String entity){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("createAudit");
        }
        Audit audit = new Audit(author, action, new Timestamp(new Date().getTime()), entity);
        entityManager.persist(audit);
    }
}
