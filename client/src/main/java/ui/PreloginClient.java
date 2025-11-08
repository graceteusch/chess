package ui;

import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class PreloginClient implements Client {
    private ServerFacade server;
    private String currUser = null;
    private Repl repl;

    public PreloginClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "Quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws Exception {
        if (params.length == 3) {
            var newUser = new UserData(params[0], params[1], params[2]);
            AuthData auth = server.register(newUser);
            currUser = newUser.username();
            // set the repl client to a Postlogin Client
            repl.setClient(new PostloginClient(server, repl));
            repl.setState(ReplState.LOGGEDIN);
            return String.format("You registered and logged in as %s.", currUser);
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To register, please use the following format: Register <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String login(String... params) {
        // Prompts the user to input login information.
        // Calls the server login API to log in the user.
        // When successfully logged in, the client should transition to the Postlogin UI.
        if (params.length == 2) {
            var user = new UserData(params[0], params[1], null);
            AuthData auth = server.login(user);
            repl.setClient(new PostloginClient(server, repl));
            repl.setState(ReplState.LOGGEDIN);
            currUser = auth.username();
            return String.format("You logged in as %s.", currUser);
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To log in, please use the following format: Login <USERNAME> <PASSWORD>");
    }


    public String help() {
        return """
                Commands you can use:
                - Register <USERNAME> <PASSWORD> <EMAIL> - create an account!
                - Login - if you already registered, login to play chess!
                - Quit - exit chess :(
                - Help - list all possible commands!
                """;
    }
}
