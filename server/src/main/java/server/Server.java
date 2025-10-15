package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccessObject;
import io.javalin.*;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import services.AlreadyTakenException;
import services.BadRequestException;
import services.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService(new MemoryDataAccessObject());

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));
    }

    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String requestJson = ctx.body(); // returns a string that represents the body - request JSON

            // handler should pass the RegisterRequest along to UserService (via its register function)
            UserData user = serializer.fromJson(requestJson, UserData.class); // get the actual request (saved as a RegisterRequest object)
            // change to user - just pass in UserData instead of RegisterRequest !
            AuthData registerResult = userService.register(user); // get a RegisterResult back from Service

//            // hard coding:
//            var response = Map.of("username", user.username(), "authToken", "xyz");
//            ctx.result(serializer.toJson(response));

            // serialize the RegisterResult back into a json
            ctx.result(serializer.toJson(registerResult));
        } catch (AlreadyTakenException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(403).result(msg);
        } catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
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
