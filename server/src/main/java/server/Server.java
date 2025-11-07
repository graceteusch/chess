package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessObject;
import dataaccess.SqlDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.*;
import service.JoinGameRequest;

import java.util.Collection;
import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        //var dataAccess = new MemoryDataAccessObject();
        SqlDataAccess dataAccess = null;
        try {
            dataAccess = new SqlDataAccess();
        } catch (DataAccessException ex) {
            System.err.println(ex.getMessage());
        }
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> clear(ctx));
        server.post("user", ctx -> register(ctx));
        server.post("session", ctx -> login(ctx));
        server.delete("session", ctx -> logout(ctx));
        server.post("game", ctx -> createGame(ctx));
        server.put("game", ctx -> joinGame(ctx));
        server.get("game", ctx -> listGames(ctx));

    }

    private void listGames(Context ctx) {
        try {
            var serializer = new Gson();
            // get auth token from ctx header
            String authToken = ctx.header("authorization");
            Collection<GameData> allGames = gameService.listGames(authToken);
            var listGamesResponse = Map.of("games", allGames);
            ctx.status(200).result(serializer.toJson(listGamesResponse));
        } catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    // Verifies that the specified game exists and adds the caller as the requested color to the game.
    private void joinGame(Context ctx) {
        try {
            // Body	{ "playerColor":"WHITE/BLACK", "gameID": 1234 }
            var serializer = new Gson();
            String authToken = ctx.header("authorization");
            String requestJson = ctx.body();
            JoinGameRequest game = serializer.fromJson(requestJson, JoinGameRequest.class);


            gameService.joinGame(authToken, game.playerColor(), game.gameID());

            ctx.status(200).result(serializer.toJson(Map.of()));
        } catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (AlreadyTakenException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(403).result(msg);
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void createGame(Context ctx) {
        try {
            var serializer = new Gson();
            String authToken = ctx.header("authorization");
            String requestJson = ctx.body();
            GameData game = serializer.fromJson(requestJson, GameData.class);

            int gameID = gameService.createGame(authToken, game.gameName());

            ctx.status(200).result(serializer.toJson(Map.of("gameID", gameID)));
        } catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void logout(Context ctx) {
        try {
            var serializer = new Gson();
            // get auth token from ctx header
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.status(200).result(serializer.toJson(Map.of()));
        } catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void login(Context ctx) {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body();
            UserData user = serializer.fromJson(requestJson, UserData.class);

            AuthData loginResult = userService.login(user);

            // hardcoding:
            // AuthData registerResult = new AuthData("authToken", user.username());
            ctx.status(200).result(serializer.toJson(loginResult));
        } catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void clear(Context ctx) {
        try {
            userService.clear();
            ctx.status(200).result();
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body(); // returns a string that represents the body - request JSON

            // handler should pass the RegisterRequest along to UserService (via its register function)
            UserData user = serializer.fromJson(requestJson, UserData.class); // get the actual request (saved as a RegisterRequest object)
            // change to user - just pass in UserData instead of RegisterRequest !
            AuthData registerResult = userService.register(user); // get a RegisterResult back from Service

            // serialize the RegisterResult back into a json
            ctx.status(200).result(serializer.toJson(registerResult));
        } catch (AlreadyTakenException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(403).result(msg);
        } catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch (DataAccessException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
