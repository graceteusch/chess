package ui;

import java.util.Arrays;

public class PostloginClient implements Client {
    public PostloginClient(ServerFacade server, Repl repl) {

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
