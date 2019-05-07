package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable(value = "questionId")final String questionUuid,
                                                       @RequestHeader(value = "authorization")final String authorization) throws InvalidQuestionException, AuthorizationFailedException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity, questionUuid, authorization);

        final AnswerResponse createdAnswerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(createdAnswerResponse, HttpStatus.CREATED);

    }


}
