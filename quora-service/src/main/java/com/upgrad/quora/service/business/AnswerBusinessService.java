package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//This is service class for AnswerController.

@Service
public class AnswerBusinessService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    UserAuthDao userAuthDao;


    //This method takes the question uuid and authorization token as the parameter from the AnswerController method getAllAnswerToQuestion.
    //This method return the list of AnswerEntity.
    //The authorizationToken is used to authorise the access by using UserAuthDao Method getAuthToken and are checked for the required condition.
    //If the condition are passed then the Question is fetched from the Question table using QuestionDao.
    //If the Question exists then Answer list is fetched using AnswerDao Method getAllAnswerToQuestion by passing the particular question.

    public List<AnswerEntity> getAllAnswerToQuestion(final String questionUuid,final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if (userAuthEntity == null){//Chekcing if user is not signed in
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if (userAuthEntity.getLogoutAt() != null){//checking if user is signed out
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");
        }

        QuestionEntity questionEntity = questionDao.getQuestionByQuestionUuid(questionUuid);

        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }
        //Returning the List of AnswerEntity to the calling method.
        List<AnswerEntity> answerEntities = answerDao.getAllAnswerToQuestion(questionEntity);
        return answerEntities;
    }

    //This method is used to check different conditions before the record is actually deleted from database
    //It takes tha answerId and the authorization parameters to perform various checks
    //And returns the deleted record to the controller class
    //Also if conditions are not favorable throws exceptions

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerUuid, final String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {

        //Code checks whether the answer to be deleted is exist in the database OR not??
        AnswerEntity answerEntity = answerDao.getAnswerByAnswerUuid(answerUuid);
        if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

       //Checks whether user is signed in..??
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {    //checking if user is signed out
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        //check for user willing to delete the answer is admin OR the answer owner??

        String role = userAuthEntity.getUser().getRole();
        if (role.equals("admin") || (answerEntity.getUser().equals(userAuthEntity.getUser()))) {
            AnswerEntity deletedAns = answerDao.deleteAnswer(answerEntity);
            return deletedAns;
        }
        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
    }

}
