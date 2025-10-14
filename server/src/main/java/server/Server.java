package server;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import services.UserService;
import services.requests.RegisterRequest;
import services.results.RegisterResult;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));
    }

    private void register(Context ctx) {
        // handler function
        var serializer = new Gson();
        String requestJson = ctx.body(); // returns a string that represents the body - request JSON
        var request = serializer.fromJson(requestJson, RegisterRequest.class); // get the actual request (saved as a RegisterRequest object)

        // handler should pass the RegisterRequest along to UserService (via its register function)
        RegisterResult registerResult = userService.register(request); // get a RegisterResult back from Service

        // for now - just hardcoding an authToken
        // var response = Map.of("username", request.get("username"), "authToken", "xyz"); // normally service should create the authToken

        // serialize the RegisterResult back into a json
        ctx.result(serializer.toJson(registerResult));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
