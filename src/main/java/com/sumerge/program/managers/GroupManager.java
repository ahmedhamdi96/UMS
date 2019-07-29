package com.sumerge.program.managers;

import com.sumerge.program.entities.Group;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GroupManager {

    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;

    public Group createGroup(Group group){
        return entityManager.merge(group);
    }
}
