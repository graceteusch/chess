package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessObjectTest {

    @Test
    void createUser() throws DataAccessException {
        DataAccessObject db = new MemoryDataAccessObject();
        var user = new UserData("joe", "password", "j@j.com");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
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