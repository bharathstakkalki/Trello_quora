package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


//This Controller class deals with all the request related to question.

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionBusinessService questionBusinessService;


    //This method gets all the question stored in the data base.
    // There is only requirement that the user has to be signed in user with valid accesstoken.
    // This method returns a List of questionDetailsResponse as there would be no of questions stored in the database.
    //All the exception arising and as required has been handled.

    @RequestMapping(method = RequestMethod.GET,path = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new LinkedList<>();//list is created to return.

        //This loop iterates through the list and the question uuid and content to the questionDetailResponse.
        //This is later added to the questionDetailsResponseList to return to the client.
        for(QuestionEntity questionEntity:questionEntities){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

    //This method is used to get all the question posted by the user it uses uuid to get user details and then get user question.
    // There is only requirement that the user has to be signed in user with valid accesstoken.
    // This method returns a List of questionDetailsResponse as there would be no of questions stored in the database.
    //All the exception arising and as required has been handled.
    @RequestMapping(method = RequestMethod.GET,path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable(value = "userId")final String uuid,@RequestHeader(value = "authorization")final String authorization) throws UserNotFoundException, AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionBusinessService.getAllQuestionsByUser(uuid,authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new LinkedList<>();//list is created to return.

        //This loop iterates through the list and the question uuid and content to the questionDetailResponse.
        //This is later added to the questionDetailsResponseList to return to the client.
        for(QuestionEntity questionEntity:questionEntities){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

    // This Request handler method is used to handle Http requests of Post type. It receives the question entered by the user,
    // creates the QuestionEntity object and correspondingly calls the createQuestion() in QuestionBusinessService class
    // Exceptions arising due to scenarios when a user who isn't signed in, or has signed out tries to create a question has been handled
    @RequestMapping(method = RequestMethod.POST, path="/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());
        QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return  new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    // This Request handler method is used to handle Http requests of Put type. It receives the content that needs to be edited,
    // the uuid of the question to be edited and the Authorization details (in the header) pertaining to the logged in user.
    // A QuestionEntity object is created that stored the details present in the QuestionEditRequest and is sent to editQuestion() in
    // QuestionBusinessService class to update the corresponding record in the database
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{question_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuesttion(final QuestionEditRequest questionEditRequest, @PathVariable(value = "question_id") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        QuestionEntity editedQuestion = questionBusinessService.editQuestion(questionEntity, questionId, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);

    }


}
