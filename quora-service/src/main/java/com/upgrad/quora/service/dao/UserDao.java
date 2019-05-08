package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {


    @PersistenceContext
    private EntityManager entityManager;


    //This method is created to fetch the user details from the user table using uuid.
    //This method is only called from the Bussiness Service once authorization of requesting user is complete.
    //This method returns the user that it fetched and to the businessservice layer.
    //It has name query created which fetches the user details using uuid.if the user doesnt
    // not exist then the exception are caught and null is return to the service class.

    public UsersEntity getUser(final String uuid) {
        try {
            UsersEntity user = entityManager.createNamedQuery("userByUuid", UsersEntity.class).setParameter("uuid", uuid).getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method fetches the User Entity Object having Username property as the given parameter by calling
     * the corresponding Named Query
     *
     * @param username
     * @return
     */
    public UsersEntity getUserByUsername(final String username) {
        try {
            UsersEntity user = entityManager.createNamedQuery("userByUsername", UsersEntity.class).setParameter("username", username).getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method fetches the User Entity Object having email property as the given parameter by calling
     * the corresponding Named Query
     *
     * @param email
     * @return
     */
    public UsersEntity getUserByEmail(final String email) {
        try {
            UsersEntity user = entityManager.createNamedQuery("userByEmail", UsersEntity.class).setParameter("email", email).getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method simply persists the received UsersEntity object in the database and returns the persisted
     * object.
     *
     * @param usersEntity
     * @return
     */
    public UsersEntity createUser(UsersEntity usersEntity) {
        entityManager.persist(usersEntity);
        return usersEntity;
    }


}
