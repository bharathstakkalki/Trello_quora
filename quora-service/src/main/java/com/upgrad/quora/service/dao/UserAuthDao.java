package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;


    //This method is used to get the auth token of the logged in user or the requesting user so that he is authorised by matching the accesstoken.
    //Method uses a name query to fetch user auth details from userauth table using accesstoken.
    //If the details doesnt exist then it throws exception which is caught and a null value is returned to the calling business class.
    public UserAuthEntity getAuthToken(final String authorizationToken) {

        try {
            UserAuthEntity userAuthEntity = entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken", authorizationToken).getSingleResult();
            return userAuthEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method is used to persist the User Authorization Token i.e. JWT in the database.
     *
     * @param userAuthEntity
     * @return UserAuthEntity Object
     */
    public UserAuthEntity createAuthToken(UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    /**
     * DAO Method to update the UserAuthEntity object with the logout time in the database.
     *
     * @param userAuthEntity
     * @return UserAuthEntity Object
     */
    public UserAuthEntity logOut(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
        return userAuthEntity;
    }
}
