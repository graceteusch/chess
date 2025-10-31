package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final DataAccessObject dataAccess;

    public UserService(DataAccessObject dataAccess) {
        this.dataAccess = dataAccess;
    }

    // generate a token when a user registers or logs in
    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private String hashUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private boolean verifyUserPassword(String username, String providedClearTextPassword) throws DataAccessException {
        // read the previously hashed password from the database
        var user = dataAccess.getUser(username);
        var hashedPassword = user.password();

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
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
        var hashed = hashUserPassword(user.password());
        dataAccess.createUser(new UserData(username, hashed, email));

        // generate an authToken
        String authToken = generateAuthToken();
        // call the authDAO object's createAuth function
        dataAccess.createAuth(new AuthData(authToken, username));

        return new AuthData(authToken, username);
    }

    public void clear() throws DataAccessException {
        dataAccess.clearUsers();
        dataAccess.clearAuths();
        dataAccess.clearGames();
    }


    public AuthData login(UserData user) throws BadRequestException, UnauthorizedException, DataAccessException {
        String username = user.username();
        String password = user.password();

        // 400: bad request
        if (username == null || password == null) {
            throw new BadRequestException("bad request");
        }

        // find user with dataAccess
        // 401: unauthorized
        if (dataAccess.getUser(username) == null) {
            throw new UnauthorizedException("unauthorized");
        }

        UserData currUser = dataAccess.getUser(username);

        // check password
        // 401: unauthorized
        if (!verifyUserPassword(username, password)) {
            throw new UnauthorizedException("unauthorized");
        }
        //if (!currUser.password().equals(password)) { throw new UnauthorizedException("unauthorized");}

        // generate authToken
        String authToken = generateAuthToken();

        // create AuthData
        dataAccess.createAuth(new AuthData(authToken, username));

        return new AuthData(authToken, username);
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }


}

