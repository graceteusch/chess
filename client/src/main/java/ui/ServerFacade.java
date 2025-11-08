package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import server.Server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    // register
    public AuthData register(UserData user) throws ServerResponseException {
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }


    // login - server response returns authdata (?)
    public AuthData login(UserData user) throws ServerResponseException {
        var request = buildRequest("POST", "/session", user);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // clear
    public void clear() throws ServerResponseException {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    // logout
    public void logout(AuthData user) throws ServerResponseException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", user.authToken())
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    // createGame


    // joinGame


    // listGames

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServerResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ServerResponseException(500, ex.getMessage());
        }


    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ServerResponseException.fromJson(status, body);
            }
            throw new ServerResponseException(status, "Server threw error with no message: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

