package client;

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
}
