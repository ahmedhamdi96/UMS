package com.sumerge.program.managers;

import com.google.gson.Gson;
import com.sumerge.program.entities.Group;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GroupManager {
    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
    @EJB
    private AuditManager auditManager;
    private final static Logger LOGGER = Logger.getLogger(GroupManager.class);

    public Group createGroup(Group group, String author){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("createGroup");
        }
        Group merged_group =  entityManager.merge(group);
        auditManager.createAudit(author, "CREATE", new Gson().toJson(merged_group));
        return merged_group;
    }
}
