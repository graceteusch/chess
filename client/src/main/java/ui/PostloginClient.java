package ui;

import model.AuthData;
import model.UserData;

import java.util.Arrays;

public class PostloginClient implements Client {
    private ServerFacade server;
    private AuthData currUser;
    private Repl repl;

    public PostloginClient(ServerFacade server, Repl repl, AuthData auth) {
        this.server = server;
        this.repl = repl;
        this.currUser = auth;
    }

    @Override
    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "Quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

//    server.delete("session", ctx -> logout(ctx));
//    server.post("game", ctx -> createGame(ctx));
//    server.put("game", ctx -> joinGame(ctx));
//    server.get("game", ctx -> listGames(ctx));

//    private String register(String... params) throws Exception {
//        if (params.length == 3) {
//            var newUser = new UserData(params[0], params[1], params[2]);
//            AuthData auth = server.register(newUser);
//            currUser = newUser.username();
//            // set the repl client to a Postlogin Client
//            repl.setClient(new PostloginClient(server, repl));
//            repl.setState(ReplState.LOGGEDIN);
//            return String.format("You registered and logged in as %s.", currUser);
//        }
//        System.out.println("Invalid input");
//        throw new ServerResponseException("To register, please use the following format: Register <USERNAME> <PASSWORD> <EMAIL>");
//    }


    private String logout(String... params) {
        if (params.length == 0) {
            server.logout(currUser);
            repl.setClient(new PreloginClient(server, repl));
//            repl.setState(ReplState.LOGGEDIN);
//            return String.format("You registered and logged in as %s.", currUser);
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To logout, please use the following format: Logout");
    }

    private String observeGame(String... params) {
        return "";
    }

    private String listGames(String... params) {
        return "";
    }

    private String playGame(String... params) {
        return "";
    }

    private String createGame(String... params) {
        return "";
    }


    @Override
    public String help() {
        return """
                Commands you can use:
                - Create <NAME> - create a new chess game!
                - List - list all existing games
                - Join <GAME ID> <WHITE or BLACK> - join a chess game
                - Observe <GAME ID> - observe a chess game
                - Logout - logout of your account!
                - Help - list all possible commands!
                - Quit - exit chess :(
                """;
    }
}
