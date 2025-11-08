package ui;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class PostloginClient implements Client {
    private ServerFacade server;
    private AuthData currUser;
    private Repl repl;
    private Collection<GameData> lastListedGames;

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
            repl.setState(ReplState.LOGGEDOUT);
            return "You have been logged out.";
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To logout, please use the following format: Logout");
    }

    private String createGame(String... params) {
        if (params.length == 1) {
            String name = params[0];
            var newGame = new GameData(null, null, null, name, null);
            server.createGame(currUser, newGame);
            return String.format("You created a chess game named %s.", name);
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To create a game, please use the following format: Create <GAME NAME>");
    }

    private String listGames(String... params) {
        if (params.length == 0) {
            Collection<GameData> games = server.listGames(currUser);
            lastListedGames = games;
            // convert games to a string
            if (games.isEmpty()) {
                return "There are currently no games. Use Create <GAME NAME> to make a game.";
            }
            var response = "";
            var index = 1;
            for (GameData game : games) {
                String whiteUsername = game.whiteUsername();
                String blackUsername = game.blackUsername();
                if (whiteUsername == null) {
                    whiteUsername = "NO PLAYER";
                }
                if (blackUsername == null) {
                    blackUsername = "NO PLAYER";
                }
                response += String.format("%d. Game Name: %s, White Player: %s, Black Player: %s %n", index++, game.gameName(), whiteUsername, blackUsername);

            }
            return "Games: " + "\n" + response;
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To list games, please use the following format: List");
    }

    private String observeGame(String... params) {
        return "";
    }

    private String playGame(String... params) {
        return "";
    }

    @Override
    public String help() {
        return """
                Commands you can use:
                - Create <GAME NAME> - create a new chess game!
                - List - list all existing games
                - Join <GAME ID> <WHITE or BLACK> - join a chess game
                - Observe <GAME ID> - observe a chess game
                - Logout - logout of your account!
                - Help - list all possible commands!
                - Quit - exit chess :(
                """;
    }
}
