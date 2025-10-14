package services;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import services.requests.LoginRequest;
import services.requests.LogoutRequest;
import services.requests.RegisterRequest;
import services.results.LoginResult;
import services.results.LogoutResult;
import services.results.RegisterResult;

import java.util.UUID;

public class UserService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public UserService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // generate a token when a user registers or logs in
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        // TODO: throw specific exceptions (like AlreadyTakenException) depending on the error
        // TODO: work on how the handler will take in the Register Result / how to determine the correct HTTP code (200, 403, etc.)
        // TODO: implement the DAO classes
        // TODO: unit tests to make sure this actually works properly
        try {
            // create userDAO object
            // call the userDAO object's getUser function
            // get back UserData
            UserData userData = userDAO.getUser(username);
            if (userData != null) {
                // if data is NOT null, then return a failure response
                return new RegisterResult(null, null, "[403] Error: already taken");
            } else if (username == null) {
                // return a failure response
                return new RegisterResult(null, null, "[400] Error: bad request");
            }

            // if data is null
            // call the userDAO object's createUser function
            // create a new UserData object using the given fields and pass it to createUser
            userDAO.insertUser(new UserData(username, password, email));

            // generate an authToken
            String authToken = generateToken();
            // call the authDAO object's createAuth function
            authDAO.createAuth(new AuthData(authToken, username));

            return new RegisterResult(username, authToken, "[200] Success");


            // return a success result
        } catch (DataAccessException e) {
            var message = "Error: ";
            // return fail result
            return new RegisterResult(null, null, "[500] Error: (description of error)");
        }
    }

//    public LoginResult login(LoginRequest loginRequest) {
//
//    }
//
//    public LogoutResult logout(LogoutRequest logoutRequest) {
//
//    }
}