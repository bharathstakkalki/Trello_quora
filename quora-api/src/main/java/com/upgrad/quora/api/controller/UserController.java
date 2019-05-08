package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserBusinessService userBusinessService;

    /**
     * Method that implements the User Signup endpoint. This method gets the Object of type SignupUserRequest
     * and translates it into the UsersEntity object. Then it makes a call to the service method for creating
     * a new User. Once it receives the response it create the SignupUserResponse object that is to be sent in
     * the Response Body.
     * @param signupUserRequest
     * @return
     * @throws SignUpRestrictedException
     */
    @RequestMapping(path = "/user/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignUp(SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setUuid(UUID.randomUUID().toString());
        usersEntity.setFirstName(signupUserRequest.getFirstName());
        usersEntity.setLastName(signupUserRequest.getLastName());
        usersEntity.setUserName(signupUserRequest.getUserName());
        usersEntity.setPassword(signupUserRequest.getPassword());
        usersEntity.setAboutMe(signupUserRequest.getAboutMe());
        usersEntity.setContactNumber(signupUserRequest.getContactNumber());
        usersEntity.setEmail(signupUserRequest.getEmailAddress());
        usersEntity.setCountry(signupUserRequest.getCountry());
        usersEntity.setDob(signupUserRequest.getDob());
        usersEntity.setRole("nonadmin");

        UsersEntity createdUsersEntity = userBusinessService.createUser(usersEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(createdUsersEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
    }

}
