package ui;

import model.AuthData;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;


public class PreloginClient implements Client {
    private ServerFacade server;
    private AuthData currUser = null;
    private Repl repl;

    public PreloginClient(ServerFacade server, Repl repl) {
        this.server = server;
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
            if (params[0].equals("null")) {
                return "You cannot use 'null' as a username. Please try a different username.";
            }
            if (params[1].equals("null")) {
                return "You cannot use 'null' as a password. Please try a different password.";
            }
            if (params[3].equals("null")) {
                return "You cannot use 'null' as an email. Please try a different email.";
            }
            currUser = server.register(newUser);
            // set the repl client to a Postlogin Client
            repl.setClient(new PostloginClient(server, repl, currUser));
            repl.setState(ReplState.LOGGEDIN);

            return String.format("You registered and logged in as %s. Type 'Help' to see what commands you can now use.", currUser.username());
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
            currUser = server.login(user);
            repl.setClient(new PostloginClient(server, repl, currUser));
            repl.setState(ReplState.LOGGEDIN);
            return String.format("You logged in as %s. Type 'Help' to see what commands you can now use.", currUser.username());
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
