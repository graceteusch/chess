package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessObjectTest {

    @Test
    void createUser() throws DataAccessException {
        DataAccessObject db = new SqlDataAccess();
        var user = new UserData("joe", "password", "j@j.com");
        db.createUser(user);

        var gotUser = db.getUser(user.username());

        assertEquals(user.username(), gotUser.username());
        assertEquals(user.email(), gotUser.email());
    }


    @Test
    void getUser() {

    }


    @Test
    void clearUsers() throws DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        db.createUser(new UserData("joe", "password", "j@j.com"));
        db.clearUsers();
        assertNull(db.getUser("joe"));
    }
}