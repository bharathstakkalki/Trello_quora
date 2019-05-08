package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;


    //This Method gets all the answer stores for a particular Question using question as the parameter.
    //Returns a list of answer.
    public List<AnswerEntity> getAllAnswerToQuestion(QuestionEntity questionEntity){
        List<AnswerEntity> answerEntities = entityManager.createNamedQuery("getAllAnswerToQuestion",AnswerEntity.class).setParameter("question",questionEntity).getResultList();
        return answerEntities;
    }

    //This method takes answer entity to be persisted in the database, stores the answer and other related parameters
    // in the answer table for a questionId and returns the created answer and other related parameters from answer table
    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }
}
