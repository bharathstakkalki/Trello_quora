package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;
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

import java.util.LinkedList;
import java.util.List;

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

    //This end point is to delete an answer with permissions that only answer owner OR admin can delete the answer
    //Method takes answerID and authorization and passes it to the service class
    //It gives you back the deleted record from the answer table in the form of JSON model format along with the httpstatus
    //It also handles the exceptions and returns the generated error codes and messages

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
         public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable(value = "answerId")final String answerId, @RequestHeader("authorization")final String authorization)throws AuthorizationFailedException, AnswerNotFoundException {
            final AnswerEntity deletedAns = answerBusinessService.deleteAnswer(answerId, authorization);
            AnswerDeleteResponse  deletedAnsResponse = new AnswerDeleteResponse().id(deletedAns.getUuid()).status("ANSWER DELETED");
            return new ResponseEntity<AnswerDeleteResponse>(deletedAnsResponse,HttpStatus.OK);
    }

}
