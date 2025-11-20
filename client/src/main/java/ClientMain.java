
import chess.*;
import ui.Repl;

// you will still need to start your server using the Main.main function when you manually run your client.
public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        String serverUrl = "http://localhost:3000";

        // create Repl object and run it
        new Repl(serverUrl).run();
    }
}