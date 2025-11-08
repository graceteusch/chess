import chess.*;
import ui.PreloginClient;
import server.Server;
import ui.Repl;

// you will still need to start your server using the Main.main function when you manually run your client.
public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        // start server
        Server server = new Server();
        int port = server.run(0);

        // run client
        String serverUrl = "http://localhost:" + port;

        // create Repl object
        new Repl(serverUrl).run();

        // stop server when REPL ends
        server.stop();
        System.out.println("Server stopped.");
    }
}