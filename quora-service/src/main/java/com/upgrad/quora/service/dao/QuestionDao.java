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

    // This method persists or saves the question into the question table and then returns the QuestionEntity
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    // This method updates the question in the question table
    public QuestionEntity editQuestion(final QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
        return questionEntity;
    }

    // This method deletes the corresponding question entry from the database and returns the questionEntity
    public QuestionEntity deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
        return questionEntity;
    }

}
