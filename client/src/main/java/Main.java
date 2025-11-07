import chess.*;
import ui.PreloginClient;
import server.Server;

// heyou will still need to start your server using the Main.main function when you manually run your client.
public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        // start server
        Server server = new Server();
        int port = server.run(0);

        // run client
        String serverUrl = "http://localhost:" + port;
        PreloginClient client = new PreloginClient(serverUrl);
        client.run();

        // step server when REPL ends
        server.stop();
        System.out.println("Server stopped.");
    }
}