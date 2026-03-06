package Database;
import java.sql.*;
import java.util.*;
public class JDBCEngine {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "9999";
    private static final String DB_NAME = "cadets_e3";
    private static final String DB_USER = "til_engelbrecht";
    private static final String DB_PASSWD = "aRLJP2Cso5tLAJD";

    private static final String JDBC_URL = "jdbc:postgresql://" + DB_HOST + ":"+ DB_PORT + "/"+ DB_NAME;

    private Connection connection;


    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()){
            connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWD);
            System.out.println("[INFO] DB-Verbindung hergestellt: "+ JDBC_URL);
        } else {
            System.out.println("[WARN] Es besteht bereits eine DB-Verbindung");
        }
    }

    public void disconnect() {
        if (connection != null){
            try {
                connection.close();
                System.out.println("[INFO] DB-Verbindung erfolgreich geschlossen");
            } catch (SQLException e) {
                System.err.println("[ERR] Fehler beim schließen der DB-Verbindung: "+e.getMessage() );
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void getAllEvents() throws SQLException {
        String sql = "SELECT * FROM event_table WHERE timestamp_rec <= 1522707048083354249";
        try(Statement stmt = getConnection().createStatement()){
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()){
                    System.out.println(rs.getString("operation"));
                }
            }
        }
    }
}
