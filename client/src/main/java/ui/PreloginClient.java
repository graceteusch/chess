package ui;

import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class PreloginClient {
    private ReplState state = ReplState.LOGGEDOUT;
    private ServerFacade server;
    private String currUser = null;

    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to Chess. Type 'Help' to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Quit")) {
            //printPrompt();
            System.out.println("prompt/current state");
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                System.out.print(result);
            } catch (Throwable ex) {
                // if there input is invalid, this should be the error that is caught and message that is printed out to the user
                var msg = ex.toString();
                System.out.print(msg);
            }
        }
        System.out.println("Goodbye :(");
    }

    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                //case "login" -> login(params);
                case "quit" -> "quit";
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
            state = ReplState.LOGGEDIN;
            return String.format("You registered and logged in as %s.", currUser);
        }
        throw new ServerResponseException("To register, use the following format: Register <USERNAME> <PASSWORD> <EMAIL>");
    }


//    private String login(String... params) {
//        //Prompts the user to input login information.
//        // Calls the server login API to log in the user.
//        // When successfully logged in, the client should transition to the Postlogin UI.
//        if (params.length == 2) {
//            server.login();
//            ReplState state = ReplState.LOGGEDIN;
//            return "You signed in as [USERNAME].";
//        }
//        return "";
//    }


    private String help() {
        if (state == ReplState.LOGGEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - create an account!
                    - login - if you already registered, login to play chess!
                    - quit - exit chess
                    - help - list all possible commands
                    """;
        }
        return "";
    }
}
