package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessObjectTest {
    private static DataAccessObject db;
    private static UserData basicTestUser;

    @BeforeEach
    public void init() throws DataAccessException {
        db = new SqlDataAccess();
        basicTestUser = new UserData("joe", "password", "j@j.com");
    }

    @Test
    void createUser() throws DataAccessException {
        db.createUser(basicTestUser);

        var createdUser = db.getUser(basicTestUser.username());

        assertEquals(basicTestUser.username(), createdUser.username());
        assertEquals(basicTestUser.email(), createdUser.email());
    }


    @Test
    void getUser() {

    }


    @Test
    void clearUsers() throws DataAccessException {
        db.createUser(new UserData("joe", "password", "j@j.com"));
        db.clearUsers();
        assertNull(db.getUser("joe"));
    }
}