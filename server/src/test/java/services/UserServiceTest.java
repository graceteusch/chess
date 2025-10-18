package services;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import dataaccess.MemoryDataAccessObject;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static DataAccessObject db;
    private static UserData basicTestUser;
    private static UserService userService;
    private static GameService gameService;

    @BeforeAll
    public static void init() {
        db = new MemoryDataAccessObject();
        basicTestUser = new UserData("joe", "password", "j@j.com");
        userService = new UserService(db);
        gameService = new GameService(db);
    }

    @Test
    void registerValid() throws BadRequestException, AlreadyTakenException, DataAccessException {
        var authData = userService.register(basicTestUser);

        assertNotNull(authData);
        assertEquals(basicTestUser.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }


    @Test
    void registerInvalidUsername() throws BadRequestException, AlreadyTakenException, DataAccessException {
        // bad request
        var nullUser = new UserData(null, "password", "j@j.com");
        assertThrows(BadRequestException.class, () -> userService.register(nullUser));

        // already taken (register same person twice)
        userService.register(basicTestUser);
        assertThrows(AlreadyTakenException.class, () -> userService.register(basicTestUser));
    }

    @Test
    void clear() throws BadRequestException, AlreadyTakenException, DataAccessException {
        userService.register(basicTestUser);

        userService.clear();
        // user shouldn't exist
        assertNull(db.getUser("joe"));
    }

    @Test
    void loginValid() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);
        assertNotNull(authData);
        assertEquals(basicTestUser.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void loginInvalid() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        // register a user
        userService.register(basicTestUser);
        // login user with wrong password
        var loginUser = new UserData("joe", "wrongPassword", "j@j.com");
        assertThrows(UnauthorizedException.class, () -> userService.login(loginUser));
    }


    @Test
    void logoutValid() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        // register and login user
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // log out
        userService.logout(authData.authToken());
        // check if auth data is now nonexistent (actually got removed)
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void logoutInvalid() throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        // register and login a user
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // invalid log out
        String fakeAuthToken = "fake";

        assertThrows(UnauthorizedException.class, () -> userService.logout(fakeAuthToken));

        // check if auth data is still existent (shouldn't have been removed)
        assertNotNull(db.getAuth(authData.authToken()));
    }

    @Test
    void createGameValid() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        // register and login
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // create a game
        int testGameID = gameService.createGame(authData.authToken(), "Test Game");

        // get game from the db
        GameData game = db.getGame(testGameID);

        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
        assertEquals(testGameID, game.gameID());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void createGameInvalid() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        // register and login
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // bad request - null game name
        assertThrows(BadRequestException.class, () -> gameService.createGame(authData.authToken(), null));

        // unauthorized - authToken isn't valid
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("fakeAuthToken", "Test Game"));
    }

    @Test
    void joinGameValid() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        // register and login
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // create a game
        int testGameID = gameService.createGame(authData.authToken(), "Test Game");

        // join a game - white username
        gameService.joinGame(authData.authToken(), "WHITE", testGameID);
        GameData gameJoined = db.getGame(testGameID);

        assertNotNull(gameJoined);
        assertEquals(basicTestUser.username(), gameJoined.whiteUsername());
        assertNull(gameJoined.blackUsername());
        assertEquals("Test Game", gameJoined.gameName());
    }

    @Test
    void joinGameInvalid() throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
        // register and login
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // create a game
        int testGameID = gameService.createGame(authData.authToken(), "Test Game");

        // bad request - player color is wrong
        assertThrows(BadRequestException.class, () -> gameService.joinGame(authData.authToken(), "BLUE", testGameID));
        // bad request - testGameID is nonexistent
        assertThrows(BadRequestException.class, () -> gameService.joinGame(authData.authToken(), "WHITE", 500));
        // unauthorized - incorrect authToken
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("fakeAuthToken", "WHITE", testGameID));
    }

    @Test
    void listGamesValid() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        // register and login
        userService.register(basicTestUser);
        var authData = userService.login(basicTestUser);

        // create a game
        int testGameID = gameService.createGame(authData.authToken(), "Test Game");

        // join a game - white username
        gameService.joinGame(authData.authToken(), "WHITE", testGameID);
        GameData gameJoined = db.getGame(testGameID);

        // create game 2
        int testGameID_2 = gameService.createGame(authData.authToken(), "Test Game");
        // same gameid because I don't actually generate a random num !!

        // list one game
        Collection<GameData> gameList = gameService.listGames(authData.authToken());
        assertNotNull(gameList);
        assertEquals(2, gameList.size());
    }

}