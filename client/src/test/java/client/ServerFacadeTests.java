package client;

import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import ui.ServerResponseException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDB() {
        facade.clear();
    }


    @Test
    public void register() {
        var user = new UserData("testUser", "pass", "test@email.com");
        var auth = facade.register(user);

        assertNotNull(auth);
        assertEquals(user.username(), auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    public void registerNegative() {
        var user = new UserData(null, "pass", "test@email.com");
        assertThrows(ServerResponseException.class, () -> facade.register(user));
    }

    @Test
    public void login() {
        var user = new UserData("test", "pass", "test@email.com");
        facade.register(user);

        var auth = facade.login(user);
        assertNotNull(auth);
        assertEquals(user.username(), auth.username());
        assertNotNull(auth.authToken());
    }

    // TODO: write negative login test!

    @Test
    public void logout() {
        var user = new UserData("test", "pass", "test@email.com");
        facade.register(user);

        var auth = facade.login(user);

        assertDoesNotThrow(() -> facade.logout(auth));
    }

    @Test
    public void logoutNegative() {
        var user = new UserData("test", "pass", "test@email.com");
        facade.register(user);

        var auth = facade.login(user);

        var fakeAuth = new AuthData("fakeToken", "test");

        assertThrows(ServerResponseException.class, () -> facade.logout(fakeAuth));
    }

    @Test
    public void createGame() {
        var user = new UserData("test", "pass", "test@email.com");
        facade.register(user);
        var auth = facade.login(user);

        var game = new GameData(null, null, null, "testGame", null);

        int gameID = facade.createGame(auth, game);
        assertNotNull(gameID);
    }

    @Test
    public void createGameNegative() {
        var user = new UserData("test", "pass", "test@email.com");
        facade.register(user);
        var auth = facade.login(user);

        var game = new GameData(null, null, null, "testGame", null);
        var invalidAuth = new AuthData("fakeAuth", "test");
        assertThrows(ServerResponseException.class, () -> facade.createGame(invalidAuth, game));


        var invalidGame = new GameData(null, null, null, null, null);
        assertThrows(ServerResponseException.class, () -> facade.createGame(auth, invalidGame));
    }

    // TODO: write unit tests for createGame, listGames, joinGame

    
}
