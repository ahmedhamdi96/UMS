package com.sumerge.program.managers;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GroupManager {

    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
}
