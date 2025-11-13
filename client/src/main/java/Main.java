
import chess.*;
import server.Server;
import ui.Repl;

// you will still need to start your server using the Main.main function when you manually run your client.
public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        Server server = new Server();
        int port = server.run(0);
        String serverUrl = "http://localhost:" + port;

        // create Repl object and run it
        new Repl(serverUrl).run();
    }
}