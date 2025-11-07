package ui;

import com.google.gson.Gson;

import java.util.HashMap;

public class ServerResponseException extends RuntimeException {
    private int statusCode;

    public ServerResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    // overloading constructor for client errors (no status codes)
    public ServerResponseException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public int getStatusCode() {
        return statusCode;
    }

    // creates an error from a json
    public static ServerResponseException fromJson(int httpStatusCode, String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = map.get("message").toString();
        return new ServerResponseException(httpStatusCode, message);
    }


}
