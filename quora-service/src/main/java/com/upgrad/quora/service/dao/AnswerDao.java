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


    //This method takes answer entity to be persisted in the database, stores the answer and other related parameters
    // in the answer table for a questionId and returns the created answer and other related parameters from answer table
    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }


    public AnswerEntity getAnswerByAnswerUuid(String answerUuid){
        try {
            AnswerEntity answerEntity = entityManager.createNamedQuery("getAnswerByAnswerUuid", AnswerEntity.class).setParameter("answerUuid", answerUuid).getSingleResult();
            return answerEntity;
        }catch (NoResultException nre){
            return null;
        }
    }


    //Method to update / edit the answer. Takes the answer to be updated and all other entity parameters and updates into the answer table in the database and
    //returns the updated record from answer table.
    public AnswerEntity editAnsContents(final AnswerEntity ansEditEntity){
        entityManager.merge(ansEditEntity);
        return ansEditEntity;
    }

    //Method removes/deletes the record from the database and returns the deleted record
    //takes the answerId checks for the database record and then removes it

    public AnswerEntity deleteAnswer(final AnswerEntity deleteAnsEntity){
        entityManager.remove(deleteAnsEntity);
        return deleteAnsEntity;
        }
    }


