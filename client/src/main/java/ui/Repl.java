package ui;

import java.util.Scanner;

public class Repl {
    private ServerFacade server;
    private ReplState state;
    private Client client;

    public Repl(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.state = ReplState.LOGGEDOUT;
        this.client = new PreloginClient(server, this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setState(ReplState state) {
        this.state = state;
    }


    public void run() {
        System.out.println("Welcome to Chess. Type 'Help' to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Quit")) {
            //printPrompt();
            System.out.print("[" + state + "] >>>> ");
            String line = scanner.nextLine();
            try {
                result = client.evaluate(line);
                System.out.println(result);
            } catch (Throwable ex) {
                // if there input is invalid, this should be the error that is caught and message that is printed out to the user
                var msg = ex.toString();
                System.out.println(msg);
            }
        }
        System.out.println("Goodbye :(");
    }
}

