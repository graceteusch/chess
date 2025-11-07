import chess.*;
import ui.PreloginClient;
import ui.ServerFacade;

//you will still need to start your server using the Main.main function when you manually run your client.
public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");


        String serverUrl = "http://localhost:" + "8080";
        PreloginClient client = new PreloginClient(serverUrl);
        client.run();
    }
}