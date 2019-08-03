package com.sumerge.program.managers;


import com.google.gson.Gson;
import com.sumerge.program.entities.Group;
import com.sumerge.program.models.GroupAdminModel;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class GroupManager {
    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
    @EJB
    private AuditManager auditManager;
    private final static Logger LOGGER = Logger.getLogger(GroupManager.class);

    public List<Group> readAllGroups(){
        LOGGER.debug("readAllGroups: ENTER");

        TypedQuery<Group> query =
                entityManager.createNamedQuery("Group.selectAllGroups", Group.class);

        List<Group> groups =  query.getResultList();
        LOGGER.debug("readAllGroups: EXIT");
        return groups;
    }

    public List<Group> readAllGroupsAdmin(){
        LOGGER.debug("readAllGroupsAdmin: ENTER");

        TypedQuery<Group> query =
                entityManager.createNamedQuery("Group.selectAllGroupsAdmin", Group.class);

        List<Group> groups = query.getResultList();
        LOGGER.debug("readAllGroupsAdmin: EXIT");
        return groups;
    }

    public Group readGroup(Integer groupId){
        LOGGER.debug("readGroup: ENTER");

        Group group = entityManager.find( Group.class, groupId);
        if (group == null || !group.getActive()){
            LOGGER.debug("readGroup: EXIT");
            return null;
        }

        LOGGER.debug("readGroup: EXIT");
        return group;
    }

    public Group readGroupByName(String name){
        LOGGER.debug("readGroupByName: ENTER");

        TypedQuery<Group> query =
                entityManager.createNamedQuery("Group.selectByName", Group.class).setParameter("name", name);
        try{
            Group group = query.getSingleResult();
            LOGGER.debug("readGroupByName: EXIT");
            return group;
        } catch (NoResultException noResultException){
            LOGGER.debug(noResultException.getMessage());
            LOGGER.debug("readGroupByName: EXIT");
            return null;
        }
    }

    @Transactional
    public Group createGroup(Group group, String author){
        LOGGER.debug("createGroup: ENTER");

        if (group.getName()!= null){
            if (readGroupByName(group.getName())!=null){
                LOGGER.debug("createGroup: EXIT");
                return null;
            }
        }

        Group merged_group =  entityManager.merge(group);
        auditManager.createAudit(author, "CREATE", "GROUP", new Gson().toJson(new GroupAdminModel(merged_group)));
        LOGGER.debug("createGroup: EXIT");
        return merged_group;
    }


    @Transactional
    public String deleteGroup(Integer groupId, String author){
        LOGGER.debug("deleteGroup: ENTER");

        Group group = entityManager.find( Group.class, groupId);
        if (group == null || !group.getActive()){
            LOGGER.debug("deleteGroup: EXIT");
            return null;
        }
        group.setUsers(null);
        group.setActive(false);
        group.setName(group.getName()+"_DELETED");
        Group merged_group =  entityManager.merge(group);
        auditManager.createAudit(author, "DELETE", "GROUP", new Gson().toJson(new GroupAdminModel(merged_group)));

        LOGGER.debug("deleteGroup: EXIT");
        return "Group Deleted Successfully.";
    }
}
