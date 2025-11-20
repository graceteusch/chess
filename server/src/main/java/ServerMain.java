import chess.*;
import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(3000);
        System.out.println("♕ 240 Chess Server");
    }
}

