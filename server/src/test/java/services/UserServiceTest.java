package services;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import dataaccess.MemoryDataAccessObject;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() throws BadRequestException, AlreadyTakenException, DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        var user = new UserData("joe", "password", "j@j.com");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
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
    void login() {
    }
}