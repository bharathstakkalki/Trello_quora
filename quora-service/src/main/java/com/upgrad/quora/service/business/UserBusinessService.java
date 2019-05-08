package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    UserAuthDao userAuthDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Service Method to implement the Create User Functionality. It takes the UsersEntity Object as input
     * and checks if the given User name or the given email exists in the database or not. If it does
     * it throws SignUpRestrictedException with respective message.
     * @param usersEntity - Type UsersEntity
     * @return UserEntity - After persisting in Database
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(UsersEntity usersEntity) throws SignUpRestrictedException {
        UsersEntity usersEntityByName = userDao.getUserByUsername(usersEntity.getUserName());
        if(userDao.getUserByUsername(usersEntity.getUserName())!=null){
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        if(userDao.getUserByEmail(usersEntity.getEmail())!= null){
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

            String[] encryptedPassword = passwordCryptographyProvider.encrypt(usersEntity.getPassword());
            usersEntity.setSalt(encryptedPassword[0]);
            usersEntity.setPassword(encryptedPassword[1]);
            userDao.createUser(usersEntity);
            return usersEntity;

    }

    /**
     * The Service method used for Authenticating the user with given Username and Password. It checks if Username
     * and password is correct or not, if not it gives Unauthorized Exception.
     * @param username
     * @param password
     * @return Object of Type UserAuthEntity
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticateUser(String username, String password) throws AuthenticationFailedException {
        UsersEntity user = userDao.getUserByUsername(username);
        if(user == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password, user.getSalt());
        if(encryptedPassword.equals(user.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUser(user);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
            userAuthEntity.setLoginAt(now);
            userAuthEntity.setExpiresAt(expiresAt);
            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthDao.createAuthToken(userAuthEntity);

            return userAuthEntity;
        }else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }
}
