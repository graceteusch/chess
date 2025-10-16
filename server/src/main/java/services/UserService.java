package services;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccessObject dataAccess;

    public UserService(DataAccessObject DAO) {
        this.dataAccess = DAO;
    }

    // generate a token when a user registers or logs in
    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    // return AuthData and take UserData instead of using RegisterRequest/Results !!
    public AuthData register(UserData user) throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = user.username();
        String password = user.password();
        String email = user.email();

        // 403: already taken
        if (dataAccess.getUser(username) != null) {
            throw new AlreadyTakenException("already taken");
        }

        // 400: bad request
        if (username == null || password == null || email == null) {
            throw new BadRequestException("bad request");
        }

        // if data is null
        // call the userDAO createUser function
        // create a new UserData object using the given fields and pass it to createUser
        dataAccess.createUser(new UserData(username, password, email));

        // generate an authToken
        String authToken = generateAuthToken();
        // call the authDAO object's createAuth function
        dataAccess.createAuth(new AuthData(authToken, username));

        return new AuthData(authToken, username);
    }

    public void clear() {
        dataAccess.clearUsers();
        dataAccess.clearAuths();
        dataAccess.clearGames();
    }


    public AuthData login(UserData user) {
        String username = user.username();
        String password = user.password();

        // find user with dataAccess
        UserData currUser = dataAccess.getUser(username);

        // check password
        if (!currUser.password().equals(password)) {
            // throw error
        }

        // generate authToken
        String authToken = generateAuthToken();

        // create AuthData
        dataAccess.createAuth(new AuthData(authToken, username));

        return new AuthData(authToken, username);


//        // 403: already taken
//        if (dataAccess.getUser(username) != null) {
//            throw new AlreadyTakenException("already taken");
//        }
//
//        // 400: bad request
//        if (username == null || password == null || email == null) {
//            throw new BadRequestException("bad request");
//        }


    }
}

