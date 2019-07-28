package com.sumerge.program.managers;

import com.sumerge.program.entities.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Stateless
public class UserManager {

    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
}
