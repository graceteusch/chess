package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Types.NULL;


public class SqlDataAccess implements DataAccessObject {

    public SqlDataAccess() throws DataAccessException {
        setupDatabase(); //
    }

    private final String[] createTablesStatements = {
            // user data table
            """
            CREATE TABLE IF NOT EXISTS  userdata (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            // auth data table
            """
            CREATE TABLE IF NOT EXISTS  authdata (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            // game data table
            """
            CREATE TABLE IF NOT EXISTS  gamedata (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256),
              `game` LONGTEXT,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void setupDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : createTablesStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to setup database", ex);
        }
    }

    private String hashUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private boolean verifyUserPassword(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private String readHashedPasswordFromDatabase(String username) {

        return "";
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        preparedStatement.setString(i + 1, p);
                    }
//                    else if (param instanceof Integer p) {
//                        preparedStatement.setInt(i + 1, p);
//                    } else if (param instanceof PetType p) {
//                        preparedStatement.setString(i + 1, p.toString());
//                    }
                    else if (param == null) {
                        preparedStatement.setNull(i + 1, NULL);
                    }
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update database", ex);
        }
    }

    private Object executeQuery(String statement, Object... params) {

        return null;
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        // store username password and email in the userdata table
        var statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        var password = hashUserPassword(u.password());
        executeUpdate(statement, u.username(), password, u.email());
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username.matches("[a-zA-Z]+")) {
            var statement = "SELECT username, password, email FROM userdata WHERE username = ?";
            try (Connection conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.setString(1, username);
                    try (var resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return readUserData(resultSet);
                        }
                    }
                }
            } catch (SQLException ex) {
                throw new DataAccessException("Unable to update database", ex);
            }
        }
        return null;
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var email = rs.getString("email");
        return new UserData(username, null, email);
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE userdata";
        executeUpdate(statement);
    }


    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        // store auth token and username in the auth data table
        var statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM authdata WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return readAuthData(resultSet);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update database", ex);
        }
        return null;
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM authdata WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clearAuths() throws DataAccessException {
        var statement = "TRUNCATE authdata";
        executeUpdate(statement);
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public boolean isColorTaken(int gameID, String playerColor) {
        return false;
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) {

    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE gamedata";
        executeUpdate(statement);
    }
}
