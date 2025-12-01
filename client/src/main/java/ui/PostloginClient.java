package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.Integer.parseInt;

public class PostloginClient implements Client {
    private ServerFacade server;
    private AuthData currUser;
    private Repl repl;
    private ArrayList<GameData> lastListedGames;
    private WebSocketFacade webSocket;

    public PostloginClient(ServerFacade server, Repl repl, AuthData auth) {
        this.server = server;
        this.repl = repl;
        this.currUser = auth;
        this.webSocket = new WebSocketFacade(server.getServerUrl());
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
                case "join" -> playGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "Quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

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
            if (name.equals("null")) {
                return "You cannot use 'null' as a game name. Please try a different name.";
            }
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
            lastListedGames = new ArrayList<>(games);
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
                    whiteUsername = "________";
                }
                if (blackUsername == null) {
                    blackUsername = "________";
                }
                response += String.format("%d. Game Name: %s, White Player: %s, Black Player: %s %n",
                        index++, game.gameName(), whiteUsername, blackUsername);

            }
            return "Games: " + "\n" + response;
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To list games, please use the following format: List");
    }

    private String playGame(String... params) {
        if (lastListedGames == null) {
            throw new ServerResponseException("Before joining a game, please use the 'List' command to see available games and their numbers");
        }
        if (lastListedGames.isEmpty()) {
            throw new ServerResponseException("There are currently no games. Please use the 'Create <GAME NAME>' command to make a new game.");
        }
        if (params.length == 2) {
            String gameNumber = params[0];
            String color = params[1];
            int gameNum;
            try {
                gameNum = parseInt(gameNumber);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input");
                throw new ServerResponseException("Please make sure the <GAME NUMBER> is a number (1, 15, etc.)");
            }
            if (gameNum < 1 || gameNum > lastListedGames.size()) {
                throw new ServerResponseException("Please make sure the <GAME NUMBER> is a current valid game. " +
                        "To see available games and their numbers, use 'list'.");
            }
            // save the user color - if it isn't BLACK or WHITE, return invalid input
            if (!color.equalsIgnoreCase("BLACK") && !color.equalsIgnoreCase("WHITE")) {
                throw new ServerResponseException("Please make sure the <TEAM COLOR> is either WHITE or BLACK.");
            }

            // get the correct/corresponding game from the lastListedGames list
            GameData joiningGame = lastListedGames.get(gameNum - 1);
            // get that game's actual gameID (not just the list number)
            int actualID = joiningGame.gameID();

            // call the server facade join/play game function
            server.joinGame(actualID, color, currUser);

            // call the websocket facade join game function (which sends a connect message)
            webSocket.joinGame(actualID, color, currUser);

            // set the repl client to a Gameplay Client
            repl.setClient(new GameplayClient(server, repl, currUser, webSocket));
            repl.setState(ReplState.GAMEPLAY);

            // give: playerColor and gameID
            // get back: nothing?? —> should it give back a game object?
            System.out.printf("You successfully joined game #%d as the %s player.%n", gameNum, color);
            return "";
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To join a game, please use the following format: Join <GAME NUMBER> <TEAM COLOR - WHITE or BLACK>");
    }

    private String observeGame(String... params) {
        if (lastListedGames == null) {
            throw new ServerResponseException("Before joining a game, please use the 'List' command to see available games and their numbers");
        }
        if (lastListedGames.isEmpty()) {
            throw new ServerResponseException("There are currently no games. Please use the 'Create <GAME NAME>' command to make a new game.");
        }
        if (params.length == 1) {
            String gameNumber = params[0];
            int gameNum;
            try {
                gameNum = parseInt(gameNumber);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input");
                throw new ServerResponseException("Please make sure the <GAME NUMBER> is a number (1, 15, etc.)");
            }
            if (gameNum < 1 || gameNum > lastListedGames.size()) {
                throw new ServerResponseException("Please make sure the <GAME NUMBER> is a current valid game." +
                        " To see available games and their numbers, use 'list'.");
            }

            // get the correct/corresponding game from the lastListedGames list
            GameData joiningGame = lastListedGames.get(gameNum - 1);
            // get that game's actual gameID (not just the list number)
            int actualID = joiningGame.gameID();

            // call the websocket facade join game function (which sends a connect message)
            webSocket.joinGame(actualID, "observer", currUser);

            // set the repl client to a Gameplay Client
            repl.setClient(new GameplayClient(server, repl, currUser, webSocket));
            repl.setState(ReplState.GAMEPLAY);

            System.out.printf("You are now observing game #%d.%n", gameNum);
        }
        System.out.println("Invalid input");
        throw new ServerResponseException("To observe a game, please use the following format: Observe <GAME NUMBER>");
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
