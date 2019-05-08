package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;


    //This method gets the question using the questionuuid.
    public QuestionEntity getQuestionByQuestionUuid(String questionUuid) {
        try {
            QuestionEntity questionEntity = entityManager.createNamedQuery("getQuestionByQuestionUuid", QuestionEntity.class).setParameter("questionUuid", questionUuid).getSingleResult();
            return questionEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    //This Method gets all the question in the database.
    //Returns a list of Question.
    public List<QuestionEntity> getAllQuestions(){ List<QuestionEntity> questionEntities = entityManager.createNamedQuery("getAllQuestions",QuestionEntity.class).getResultList();
        return questionEntities;
    }


    //This Method Gets all the question posted by a user using user as the parameter.
    //Returns a list of Question.
    public List<QuestionEntity> getAllQuestionsByUser(final UsersEntity usersEntity) {
        List<QuestionEntity> questionEntities = entityManager.createNamedQuery("getAllQuestionsByUser",QuestionEntity.class).setParameter("userEntity",usersEntity).getResultList();
        return questionEntities;
    }
}
