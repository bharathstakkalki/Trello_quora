package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@SuppressWarnings("JpaQueryApiInspection")
@Repository
public class AdminDao {

    @PersistenceContext
    private EntityManager entityManager;

    //This method is created to fetch the user details from the user table using uuid and delete.
    //This method is only called from the adminBussinessService once authorization of requesting user is complete.
    //This method returns the user that it fetched snd deleted to the businessservice layer.
    //It has name query created which fetches the user details using uuid.if the user doesnt
    // not exist then the exception are caught and null is return to the service class.

    public UsersEntity deleteUser(final String uuid) {

        try {
            UsersEntity user = entityManager.createNamedQuery("userByUuid", UsersEntity.class).setParameter("uuid", uuid).getSingleResult();
            entityManager.remove(user);
            return user;
        } catch (NoResultException nre) {
            return null;
        }

    }

    //This method is used to get the auth token of the logged in user or the requesting user so that he is authorised by matching the accesstoken.
    //Method uses a name query to fetch user auth details from userauth table using accesstoken.
    //If the details doesnt exist then it throws exception which is caught and a null value is returned to the calling business class.

    public UserAuthEntity getAuthToken(final String authorizationToken ){

        try{
            UserAuthEntity userAuthEntity = entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class).setParameter("accessToken",authorizationToken).getSingleResult();
            return userAuthEntity;
        }catch(NoResultException nre){
            return null;
        }
    }

}
