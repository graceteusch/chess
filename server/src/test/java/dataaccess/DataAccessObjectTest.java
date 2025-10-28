package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessObjectTest {
    private static DataAccessObject db;
    private static UserData basicTestUser;
    private static UserService userService;

    @BeforeEach
    public void init() throws DataAccessException {
        db = new SqlDataAccess();
        basicTestUser = new UserData("joe", "password", "j@j.com");
        userService = new UserService(db);
    }


    @Test
    void createUser() throws DataAccessException {
        db.createUser(basicTestUser);

        var createdUser = db.getUser(basicTestUser.username());

        assertEquals(basicTestUser.username(), createdUser.username());
        assertEquals(basicTestUser.email(), createdUser.email());
    }

    @Test
    void createUserInvalid() throws DataAccessException {
        // try to add a duplicate primary key
        db.createUser(basicTestUser);
        assertThrows(DataAccessException.class, () -> db.createUser(basicTestUser));

        // try to add null values
        var invalidTestUser = new UserData(null, null, null);
        assertThrows(DataAccessException.class, () -> db.createUser(invalidTestUser));
    }


    @Test
    void getUser() throws DataAccessException {
        db.createUser(basicTestUser);
        var createdUser = db.getUser(basicTestUser.username());


        assertNotNull(createdUser);
        assertEquals(basicTestUser.username(), createdUser.username());
        assertEquals(basicTestUser.email(), createdUser.email());
    }

    @Test
    void getUserInvalid() throws DataAccessException {
        var createdUser = db.getUser("nonexistentUsername");
        assertNull(createdUser);
    }


    @Test
    void clearUsers() throws DataAccessException {
        db.createUser(new UserData("joe", "password", "j@j.com"));
        db.clearUsers();
        assertNull(db.getUser("joe"));
    }

    @Test
    void createAuth() throws DataAccessException {
        var testAuth = new AuthData("fakeAuthToken", "joe");
        db.createAuth(testAuth);

        var createdAuth = db.getAuth(testAuth.authToken());

        assertEquals(testAuth.username(), createdAuth.username());
        assertEquals(testAuth.authToken(), createdAuth.authToken());
    }


}