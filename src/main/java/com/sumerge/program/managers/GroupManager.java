package com.sumerge.program.managers;

import com.google.gson.Gson;
import com.sumerge.program.entities.Group;

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


    public Group createGroup(Group group, String author){
        Group merged_group =  entityManager.merge(group);
        auditManager.createAudit(author, "CREATE", new Gson().toJson(merged_group));
        return merged_group;
    }
}
