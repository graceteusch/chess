package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));
    }

    private void register(Context ctx) {
        // handler function
        var serializer = new Gson();
        String requestJson = ctx.body(); // returns a string that represents the body - request JSON
        var request = serializer.fromJson(requestJson, Map.class); // get the actual request (saved as a map)

        // insert here - call to the service and register

        // for now - just hardcoding an authToken
        var response = Map.of("username", request.get("username"), "authToken", "xyz"); // normally service should create the authToken

        // serialize the response object you just created
        ctx.result(serializer.toJson(response));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
