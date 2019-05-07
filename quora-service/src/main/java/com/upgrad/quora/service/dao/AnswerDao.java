package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    public AnswerEntity getAnswerByAnswerUuid(String answerUuid){
        try {
            AnswerEntity answerEntity = entityManager.createNamedQuery("getAnswerByAnswerUuid", AnswerEntity.class).setParameter("answerUuid", answerUuid).getSingleResult();
            return answerEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public AnswerEntity editAnsContents(AnswerEntity ansEditEntity){
        entityManager.merge(ansEditEntity);
        return ansEditEntity;
    }

}

