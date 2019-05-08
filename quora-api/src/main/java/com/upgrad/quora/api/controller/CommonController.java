package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    CommonBusinessService commonBusinessService;

    /**
     * Method that implements the User Profile Endpoint. This method gets the User UUID as path parameter in the uri
     * and authorization token in the Request Header and uses them to get User Profile Details.
     *
     * @param userUuid
     * @param authToken
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(path = "/userprofile/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserProfileDetails(@PathVariable("userId") String userUuid, @RequestHeader("authorization") String authToken) throws AuthorizationFailedException, UserNotFoundException {
        UsersEntity usersEntity = commonBusinessService.getUserDetails(userUuid, authToken);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(usersEntity.getFirstName())
                .lastName(usersEntity.getLastName()).userName(usersEntity.getUserName()).emailAddress(usersEntity.getEmail())
                .country(usersEntity.getCountry()).aboutMe(usersEntity.getAboutMe()).dob(usersEntity.getDob());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }

}

