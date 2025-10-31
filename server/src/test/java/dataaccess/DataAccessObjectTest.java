package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
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
        var anotherTestAuth = new AuthData("random", "test");

        Collection<AuthData> expected = new ArrayList<>();
        expected.add(basicTestAuth);
        expected.add(anotherTestAuth);

        db.createAuth(basicTestAuth);
        db.createAuth(anotherTestAuth);

        // delete a nonexistent token
        db.deleteAuth("nonexistentToken");

        Collection<AuthData> actual = db.listAuths();
        assertEquals(expected, actual);
    }

    @Test
    void clearAuths() throws DataAccessException {
        db.createAuth(basicTestAuth);
        db.clearAuths();
        assertNull(db.getAuth(basicTestAuth.authToken()));
    }

    @Test
    void createGame() throws DataAccessException {
        var basicGame = new GameData(1, null, null, "testGame", new ChessGame());
        db.createGame(basicGame);

//        var createdGame = db.getGame(basicGame.gameID());
//
//        assertNotNull(createdGame);
//        assertEquals(basicGame.gameID(), createdGame.gameID());
//        assertEquals(basicGame.game(), createdGame.game());
    }

    @Test
    void createGameInvalid() throws DataAccessException {
        var basicGame = new GameData(1, null, null, "testGame", new ChessGame());
        // try to add a duplicate primary key
        db.createGame(basicGame);
        assertThrows(DataAccessException.class, () -> db.createGame(basicGame));
    }

    @Test
    void getGame() throws DataAccessException {
        var basicGame = new GameData(1, null, null, "testGame", new ChessGame());
        db.createGame(basicGame);

        var createdGame = db.getGame(basicGame.gameID());

        assertNotNull(createdGame);
        assertEquals(basicGame.gameID(), createdGame.gameID());
        assertEquals(basicGame.whiteUsername(), createdGame.whiteUsername());
        assertEquals(basicGame.blackUsername(), createdGame.blackUsername());
        assertEquals(basicGame.gameName(), createdGame.gameName());
        assertEquals(basicGame.game(), createdGame.game());
    }

    @Test
    void getGameInvalid() throws DataAccessException {
        var nonexistent = db.getGame(17);
        assertNull(nonexistent);
    }

    @Test
    void listGames() throws DataAccessException {
        var basicGame = new GameData(1, null, null, "testGame", new ChessGame());
        db.createGame(basicGame);
        var anotherGame = new GameData(2, "hello", "hi", "testGame", new ChessGame());
        db.createGame(anotherGame);

        var listOfGames = db.listGames();

        assertNotNull(listOfGames);
        assertEquals(2, listOfGames.size());
        assertTrue(listOfGames.contains(basicGame));
        assertTrue(listOfGames.contains(anotherGame));
    }

    @Test
    void listGamesInvalid() throws DataAccessException {
        // try to list games when there are none
        var listOfGames = db.listGames();
        assertTrue(listOfGames.isEmpty());
    }

    @Test
    void updateGame() throws DataAccessException {
        var basicGame = new GameData(1, null, null, "testGame", new ChessGame());
        db.createGame(basicGame);

        db.updateGame(basicGame.gameID(), "WHITE", "testUsername", null);

        var updatedGame = db.getGame(basicGame.gameID());

        assertNotNull(updatedGame.whiteUsername());
        assertEquals("testUsername", updatedGame.whiteUsername());
    }

    @Test
    void updateGameInvalid() throws DataAccessException {
        var basicGame = new GameData(1, null, null, "testGame", new ChessGame());
        db.createGame(basicGame);

        // try to insert an injection (?)

        // invalid player color
        assertThrows(DataAccessException.class, () -> db.updateGame(basicGame.gameID(), "GREEN", "testUsername", null));
    }


}