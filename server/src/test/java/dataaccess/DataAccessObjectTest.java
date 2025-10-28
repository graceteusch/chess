package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessObjectTest {
    private static DataAccessObject db;
    private static UserData basicTestUser;
    private static AuthData basicTestAuth;

    @BeforeEach
    public void init() throws DataAccessException {
        db = new SqlDataAccess();
        basicTestUser = new UserData("joe", "password", "j@j.com");
        basicTestAuth = new AuthData("fakeAuthToken", "joe");
        db.clearUsers();
        db.clearAuths();
        db.clearGames();
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
        db.createAuth(basicTestAuth);

        var createdAuth = db.getAuth(basicTestAuth.authToken());

        assertEquals(basicTestAuth.username(), createdAuth.username());
        assertEquals(basicTestAuth.authToken(), createdAuth.authToken());
    }

    @Test
    void createAuthInvalid() throws DataAccessException {
        // try to add a duplicate primary key
        db.createAuth(basicTestAuth);
        assertThrows(DataAccessException.class, () -> db.createAuth(basicTestAuth));

        // try to add null values
        var nullAuth = new AuthData(null, "joe");
        assertThrows(DataAccessException.class, () -> db.createAuth(nullAuth));
    }

    @Test
    void getAuth() throws DataAccessException {
        db.createAuth(basicTestAuth);
        var createdAuth = db.getAuth(basicTestAuth.authToken());


        assertNotNull(createdAuth);
        assertEquals(basicTestAuth.authToken(), createdAuth.authToken());
        assertEquals(basicTestAuth.username(), createdAuth.username());
    }

    @Test
    void getAuthInvalid() throws DataAccessException {
        var nonexistent = db.getAuth("nonexistentAuth");
        assertNull(nonexistent);
    }

    @Test
    void deleteAuth() throws DataAccessException {
        db.createAuth(basicTestAuth);
        db.deleteAuth(basicTestAuth.authToken());
        assertNull(db.getAuth(basicTestAuth.authToken()));
    }

    @Test
    void deleteAuthInvalid() throws DataAccessException {
//        var anotherTestAuth = new AuthData("random", "test");
//
//        List<AuthData> expected = new ArrayList<>();
//        expected.add(basicTestAuth);
//        expected.add(anotherTestAuth);
//
//        db.createAuth(basicTestAuth);
//        db.createAuth(anotherTestAuth);
//
//        db.deleteAuth("nonexistentToken");
//
//        List<AuthData> actual = db.listAuths();
//        assertPetCollectionEqual(expected, actual);
    }

    @Test
    void clearAuths() throws DataAccessException {
        db.createAuth(basicTestAuth);
        db.clearAuths();
        assertNull(db.getAuth(basicTestAuth.authToken()));
    }


}