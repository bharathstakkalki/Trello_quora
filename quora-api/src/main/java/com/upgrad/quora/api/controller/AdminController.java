package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//AdminController is created for the delete user request.
// Only admin can delete a user.All the corresponding exception are handled and message are shown as per the requirement.


@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    //This Controller method is called when request pattern is /admin/user/{userId}.It is a delete request.
    //This method deletes the user with coreesponding uuid from the database.
    //This method throws exception when the user is not available or the user doesnt have right to access this api.
    //For the Task of deleting the method uses adminBusinesssService and passes the authorization token for authorization and uuid for find thing user and deleting the user.
    //Once the user is deleted it return a response msg and deleted user uuid.

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable (value = "userId") final String uuid, @RequestHeader("authorization")final String authorization) throws UserNotFoundException, AuthorizationFailedException {

        final UsersEntity deletedUser = adminBusinessService.deleteUser(uuid,authorization);
       UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deletedUser.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);

    }


}
