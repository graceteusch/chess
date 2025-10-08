# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram Progress

Finished ListGames and CreateGame Diagrams: 
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDp6gAxJCcGCsyg8mA6SwwDmzMSYHQo4AAa0lAxgmyQYHiCoGnJgwAQao48pQAA80RoYDJtLT+XcEeSVPUpVAeZh7YiVC7KjdnjAFGaUMALe0NegAKJWlTYAgFH1VaiUAPwZDmeqBJzBYbjObqYCMhbLGNQbx1Q1TJUmsMR+XydXoKFmTimHlO9QZv0oepoHwIBBkkT++5dvn0kBq9Ge9kDHncx1TgX3Yz1BQcDha6XaUdUCkTld01S1Gfh9EKHz6jHAG-xJeYSenwUbkPbkMP7321XhzUAJIYLWCAwJQVY6nqBpaPIJqrj0MG6PoYj2r6bqGKUrpptW176u0EAtmgcCzvkaBxgmSaFKhqa3JhlTFNmMAAKxOE4BZjEWqglvM9RjBWVb1EB6LvKaYGVtAbb2ugHDPiezp0TRSLkD4q4AGZDgg8oRhwKA7rhj77n2GYwk81bYmieJqEOWAmXCvbVMGrxGkqSy8cCyz3nhBGtjAbltkG8L3AxGD1OErHsYqny+cs7ljJ58T4YRrkxW20mmOlpheL4ATQOwjIxCKcAxtIcAKDAAAyEBZIUwXMFhNT1M0bRdL0BjqKRBaRSgay-P8HBXAFgq2Y5XU9fofw7NCjx2fVSkIFV4oYpV1UEkSYCkkZx40quDJMvOXXLttr7rsKMBihKnoynKNYiXMKZ-hqu6wLq+o3caHAQGoMBoBAzDWmiKb2i+8mze630aYDinjvVwaXZ9qgAHK-XGaLkSgiakZDg0KbVOZOAAjOxnHcWWfHidWCrTA+0BIAAXrpklgR2wM9gpfb1G83qbYG6H1CA8QoCA6pNONfULrWcxjVsOykr+aqaiL0sAjA2Qq2gIDQCi4BY+h9nYYJ6ua4LYCKxNAJoxjybUdjQVZiFMC5oTowcfyJO8fx0Ac1T+o0-TuxXFJHbcw6R38nzs4oPpd7ftoh28sdlTvluekx-I93yzAQkgWJEEvdB2hwaeCHaHoBg62OGEwzhD6JegxGXqRFuUeXtG2yUYD1CxbHO8Tpbu+TBvCdMoHgRJAdMzJLNrqD-bKWpGladuulfvqXO6wpw3Vst4oZKo1mYJvesNTATkS-M0X7CC8W12gyX7ANDmBfRdsdzAYXd6ft3n35V8195t8X0uO2GSGVPDeD8P4LwKB0AxDiIkKBMDlq+CwLVQUAVGrSBjOVGM7QYzdB6G1VQHVvYJX-gedM9xN4vGvv-KasIKE8wrvUea9hkFLSqsg1aah1oHiPFSOS9IjAoG4FeB80cvKETjt2NcidToZBmBAGgK8DKwWujQwi6d-zKJvt9X6MAqCmiQDJaiU8j7Bn0jyaQ8MkZgBRjkJumNraPwzLjB2BMiauz7uWAelN4q+wZuPNKpi2aP3qGQFSdJ1LDnlCiBRNAU6r0Ms4nGL9QrhVGFcNKGUYCZXAQEFEO5-DYHFJqcqaIYAAHElQaFQVXRqFScH4PsEqYY6j0DkPhJUKhJ82loDoaZNBvMYDIByFUosS00RcJJLw8c-DQ6CJGWAMZahxGkMkbJeZMihSinFAklRwBZRaT-houWWiLEFw+l9H6f0bQ5EhsEmeA4IZOIrmY6uiT5BWLUDYuxtj4zo2bi81uz9254ydoWTxPFvECXtD4Eh-j-YpiCQIrZ75zo7lNKBZZGgHmMMPGDcJ89okwFUr4CU2KYATJyDACAqlKlKifJtSh01gxlJyLvfeh8Qn6xPssZpRYywNHGPylAAFpBlnxuEYIgQQSbHiHqFAnpOR7G+MkUA6olUuUWN8EVCMtVjChDAToD9sIuNSW-dJIw+XVMFcKpUYqJVSplcsOVCrNWfG1SCNVQt3U8QNSCXV+rDXGtMFk9KWUIEcAAOxuCcCgJwMQYzBDgEVAAbPACO9K5g5NqXi4MTUOhNJaSQm+BZA1zBNbRLpLLqwjF6WsctKB+kzTxUpC8cgUDLIxPXDtyypk8ODlPXaYAo69KkauN8p10XKKukciR7TTmPXObBS5AprlgVuVgExKKj5KUHMOFuR46kzu0F8xGyNN0OKtqhG2ILGKOw8cWLxZMYW+OplAOmASkVB3XnM+OYcYDtvRF2+tMBG3joTtsj8O5lkyjzmBpUers2PHBsON61SNn-tZvVIgBKIn8iiZpE0S8YMMoLhiEVYqYAAF4YCSulRtdezL6HVh7cBpUHKEA2RrW8l4YxKPivqPRwIlaKFt0Yu-diAnHXSsyR2UBEaAiWGEfNTYsCkAJDAMp4cEA1MACkIDiizYYfw3r1RFBfoMnlzRmQtR6CK1pxz0AFmwAgYAymoBwAgPNKADb7XSDkyAxTkCoDufU5prwYXEDhlgMAbArnCB5AKDmyzx6GiYOwbg-BxhgGYCAA

10/06/25 Updated Sequence Diagram: https://sequencediagram.org/index.html#initialData=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-jeYxqwTgvBBDjDthkkAA


Initial draft (halfway completed): https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-mIHDLAYA2AbmEDyAUOx-jeaHKwTgvBBDjC5MwEAA


