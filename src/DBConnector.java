import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    static public boolean initialisePostgres() {
        // Set up postgres drivers
        System.setProperty("jdbc.drivers", "org.postgresql.Driver");

        // Get postgres driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");

            return false;
        }

        System.out.println("PostgreSQL driver registered.");

        return true;
    }

    static public Connection connectDatabase() {
        // Connection object to be returned
        Connection dbConn = null;

        // Name of accessed database
        String dbName = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/dxr714";

        // Connect to database
        try {
            dbConn = DriverManager.getConnection(dbName, "dxr714", "swqiv5rrco");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return dbConn;
    }
}
