package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<QuestionEntity> getAllQuestions(){


        List<QuestionEntity> questionEntities = entityManager.createNamedQuery("getAllQuestions",QuestionEntity.class).getResultList();
        return questionEntities;
    }

    public List<QuestionEntity> getAllQuestionsByUser(final UsersEntity usersEntity) {
        List<QuestionEntity> questionEntities = entityManager.createNamedQuery("getAllQuestionsByUser",QuestionEntity.class).setParameter("userEntity",usersEntity).getResultList();
        return questionEntities;
    }
}
