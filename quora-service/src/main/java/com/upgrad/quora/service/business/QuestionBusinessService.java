package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//This is service class for QuestionController.

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;


    //This method takes the authorization to authorize the request.
    //The access token is sent to getAuthToken in userAuthDao to get userAuthEntity.
    //This entity is checked to authorise. If the parameter are right then questionentities list is created using questionDao.
    //All the related exception are handled.
    public List<QuestionEntity> getAllQuestions(final String authorizationToken)throws AuthorizationFailedException{
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if(userAuthEntity == null){//Checking if user is not signed in.
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt() != null){//Checking if user is logged out.
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        //Returning the list of questionEntities.
        List<QuestionEntity> questionEntities = questionDao.getAllQuestions();
        return questionEntities;
    }

    //This method takes the authorization to authorize the request and uuid of the user to get the user detail,
    // using this detail it fetches the question of the user.
    //The access token is sent to getAuthToken in userAuthDao to get userAuthEntity.
    //This entity is checked to authorise.
    // If the parameter are right then user details are fetched using uuid and using that questions are fetched from the database using QuestionDao.
    // Then questionentities list is created using questionDao.
    //All the related exception are handled.
    public List<QuestionEntity> getAllQuestionsByUser(final String uuid,final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if (userAuthEntity == null){//Chekcing if user is not signed in
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if (userAuthEntity.getLogoutAt() != null){//checking if user is signed out
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }

        UsersEntity usersEntity = userDao.getUser(uuid);
        if (usersEntity == null){//Checking if user exists.
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }

        //Returning the list of questionEntities.
        List<QuestionEntity> questionEntities = questionDao.getAllQuestionsByUser(usersEntity);
        return questionEntities;
    }

    /*
        The createQuestion() recieves the question content contained in the QuestionEntity object and tries to persist it in the database.
        Care has been taken to check if the authorized user is performing the operation of question creation - i.e., only if the
        authorization token passed is present in the database and if the user is logged in (i.e., the logout_at field of the user in the
        user_auth table is not null can he proceed to create the question.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorizationToken);

        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        UsersEntity usersEntity = userAuthEntity.getUser();
        questionEntity.setUser(usersEntity);
        QuestionEntity createdQuestion = questionDao.createQuestion(questionEntity);
        return createdQuestion;
    }

    // This method checks if the question uuid provided by is valid and hence updates the corresponding entry in the database
    // with the modified content. Exceptions arising due to User not being signed in or Question not existing in the database
    // have been appropriately handled. Care has also been taken to restrict only the owners of the question to edit/update it
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final QuestionEntity questionEntity, final String questionUuid, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getAuthToken(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if (userAuthEntity.getLogoutAt() != null ) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        QuestionEntity questionToBeEdited = questionDao.getQuestionByQuestionUuid(questionUuid);
        UsersEntity usersEntity = userAuthEntity.getUser();
        if(questionToBeEdited == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        else if (questionToBeEdited.getUser() != usersEntity) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionToBeEdited.setDate(questionEntity.getDate());
        questionToBeEdited.setContent(questionEntity.getContent());
        return questionDao.editQuestion(questionToBeEdited);
    }

}
