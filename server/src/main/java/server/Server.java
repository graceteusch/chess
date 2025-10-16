package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccessObject;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import services.AlreadyTakenException;
import services.BadRequestException;
import services.UnauthorizedException;
import services.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService(new MemoryDataAccessObject());

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> clear(ctx));
        server.post("user", ctx -> register(ctx));
        server.post("session", ctx -> login(ctx));
        server.delete("session", ctx -> logout(ctx));

    }

    private void logout(Context ctx) {
        try {
            var serializer = new Gson();

            // get auth token from ctx header ???
            String authToken = ctx.header("authorization");

            // delete authData?
            userService.logout(authToken);
            ctx.status(200).result(serializer.toJson(Map.of()));
        } catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
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
        userService.clear();
        ctx.status(200).result();
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
