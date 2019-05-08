package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;

import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    //This is get request resting all the answer to a particular question.
    //Method takes the uuid of the question and gets all the answer of the same question.
    //The method return listof answerDetailsResponse. The method uses AnswerBusinessService to get the listof answer.
    //The method also takes the authorization and passes it to service layer for authentication.
    //All the exception are handled and return with the code & message as required.

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>>getAllAnswerToQuestion(@PathVariable(value = "questionId")final String questionUuid, @RequestHeader(value = "authorization")final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> answerEntities = answerBusinessService.getAllAnswerToQuestion(questionUuid,authorization);
        List<AnswerDetailsResponse> answerDetailsResponsesList = new LinkedList<>();

        //This loop iterates through the list and add the uuid,question  and answercontent to the answerDetailsResponse.
        //This is later added to the answerDetailsResponseList to return to the client.
        for(AnswerEntity answerEntity:answerEntities){
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).questionContent(answerEntity.getQuestion().getContent()).answerContent(answerEntity.getAns());
            answerDetailsResponsesList.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponsesList, HttpStatus.OK);
    }

    //Endpoint to create an answer to question. Any user can access this end point
    //This method takes answerRequest in JSON model format, questionId whose answer is to be created and authorization details
    //And passes it to service class.
    //It returns the createdAnswer record in the JSON response model format from the answer table along with the http status code
    //Also it handles exceptions and sends appropriate error codes and messages
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable(value = "questionId")final String questionUuid,
                                                       @RequestHeader(value = "authorization")final String authorization) throws InvalidQuestionException, AuthorizationFailedException {

        //This code will transform JSON request model to entity
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());

        AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity, questionUuid, authorization);

        final AnswerResponse createdAnswerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(createdAnswerResponse, HttpStatus.CREATED);

    }


    //This end point is for editing an answer.
    //It allows editing only to the answer owner
    //This method returns the edited answer in the form of JSON response model AnswerEditResponse with Http status
    //Method takes JSON request model from client, path variable answerId and the authorization and passes it to service layer for authentication.
    //Also it handles AuthorizationFailedExceptions & AnswerNotFoundException and returns them with appropriate messages & codes

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable(value = "answerId") final String answerId,
                                                                @RequestHeader(value = "authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        //This code transforms JSON model to entity model
        AnswerEntity ansEditEntity = new AnswerEntity();
        ansEditEntity.setUuid(UUID.randomUUID().toString());
        ansEditEntity.setAns(answerEditRequest.getContent());
        ansEditEntity.setDate(ZonedDateTime.now());

        AnswerEntity ansEdited = answerBusinessService.editAnsContents(ansEditEntity, answerId, authorization);

        final AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(ansEdited.getUuid()).status("ANSWER EDITED");

        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

}
