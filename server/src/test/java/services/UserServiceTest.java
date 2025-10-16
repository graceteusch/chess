package services;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import dataaccess.MemoryDataAccessObject;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerValid() throws BadRequestException, AlreadyTakenException, DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        var user = new UserData("joe", "password", "j@j.com");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }


    @Test
    void registerInvalidUsername() throws BadRequestException, AlreadyTakenException, DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        var nullUser = new UserData(null, "password", "j@j.com");
        var userService = new UserService(db);
        assertThrows(BadRequestException.class, () -> userService.register(nullUser));

        var user = new UserData("joe", "password", "j@j.com");
        userService.register(user);
        assertThrows(AlreadyTakenException.class, () -> userService.register(user));
    }

    @Test
    void clear() throws BadRequestException, AlreadyTakenException, DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        var userService = new UserService(db);
        var user = new UserData("joe", "password", "j@j.com");
        userService.register(user);

        userService.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void loginValid() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        DataAccessObject db = new MemoryDataAccessObject();
        var userService = new UserService(db);
        var user = new UserData("joe", "password", "j@j.com");
        userService.register(user);
        var authData = userService.login(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void loginInvalid() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        DataAccessObject db = new MemoryDataAccessObject();
        var userService = new UserService(db);
        var registerUser = new UserData("joe", "password", "j@j.com");
        userService.register(registerUser);
        var loginUser = new UserData("joe", "wrongPassword", "j@j.com");
        assertThrows(UnauthorizedException.class, () -> userService.login(loginUser));
    }


    @Test
    void logoutValid() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        var userService = new UserService(db);
        var user = new UserData("joe", "password", "j@j.com");
        userService.register(user);
        var authData = userService.login(user);

        // log out
        userService.logout(authData.authToken());
        // check if auth data is now nonexistent (actually got removed)
        assertNull(db.getAuth(authData.authToken()));
    }

}