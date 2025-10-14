package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccessObject;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import services.UserService;
import services.requests.RegisterRequest;

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
        // handler function
        var serializer = new Gson();
        String requestJson = ctx.body(); // returns a string that represents the body - request JSON
        var request = serializer.fromJson(requestJson, RegisterRequest.class); // get the actual request (saved as a RegisterRequest object)
        // change to user - just pass in UserData instead of RegisterRequest !

        // handler should pass the RegisterRequest along to UserService (via its register function)
        var registerResult = userService.register(request); // get a RegisterResult back from Service

        // serialize the RegisterResult back into a json
        ctx.result(serializer.toJson(registerResult));
        // try catch block in here
//        try {
//        }
//    } catch (Exception ex){
//        var msg = String.format("{}") // create exception class and put status code in there
//        ctx.status(403).result();
//    }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
