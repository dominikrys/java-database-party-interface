import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class DBPopulator {
    public static void main(String[] args) {
        // Set up postgreSQL
        if (!DBConnector.initialisePostgres()) {
            System.out.println("Couldn't initiliase postgreSQL, quitting.");
        }

        // Connect to database
        Connection dbConn = DBConnector.connectDatabase();

        if (dbConn != null) {
            System.out.println("Database accessed!");

            // Code to use database - all in one try catch as this program just has to complete fully
            try {
                // Drop tables if they exist
                dropTables(dbConn);

                // Create new tables after they've been dropped
                createNewTables(dbConn);

                // Populate Venue with 11 real values and 100 synthetic
                populateVenue(dbConn);

                // Populate Menu with 11 real values and 100 synthetic
                populateMenu(dbConn);

                // Populate Entertainment with 11 real values and 100 synthetic
                populateEntertainment(dbConn);

                // Populate Party with 11 real values and 1000 synthetic
                populateParty(dbConn);

                // Print message that all tasks have been completed
                System.out.println("Completed! Closing connection..");

                dbConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Failed to make connection");
        }
    }

    // Method for populating party table
    private static void populateParty(Connection dbConn) throws SQLException {
        // Print message to user to show progress
        System.out.println("Populating Party table...");

        // Real values
        PreparedStatement realValuesParty = dbConn.prepareStatement(
                "INSERT INTO Party VALUES" +
                        "(1, '60th Birthday', 2, 1, 4, 2000, '2020-08-10 11:00:00', 70)," +
                        "(2, 'High School finishing Party', 3, 2, 1, 2000, '2021-01-11 20:00:00', 200)," +
                        "(3, 'Retirement Party', 5, 3, 2, 3000, '2019-02-12 18:00:00', 40)," +
                        "(4, '6th Birthday', 3, 4, 3, 1000, '2021-03-13 14:30:00', 15)," +
                        "(5, 'Club night', 8, 5, 4, 5000, '2023-04-14 22:00:00', 300)," +
                        "(6, 'Baby shower', 9, 6, 5, 4000, '2022-05-15 17:00:00', 100)," +
                        "(7, 'Graduation party', 10, 7, 6, 2000, '2019-06-16 18:30:00', 60)," +
                        "(8, 'Promotion party', 2, 8, 7, 2100, '2021-11-15 13:00:00', 65)," +
                        "(9, 'Work dinner party', 1, 9, 8, 1500, '2020-12-13 20:45:00', 150)," +
                        "(10, 'Tea party', 4, 10, 9, 1200, '2019-09-11 20:30:00', 10)," +
                        "(11, 'Surprise birthday party', 6, 11, 10, 1200, '2019-08-23 16:00:00', 20);"
        );

        realValuesParty.executeUpdate();

        // Close statement
        realValuesParty.close();

        // Choose random values to populate table with
        for (int i = 12; i < 1020; i++) {
            // Create timestamp strimp
            String timeStampString = "'" +
                    (new Random().nextInt(10) + 2018) + "-" +
                    (new Random().nextInt(2) + 10) + "-" +
                    (new Random().nextInt(30) + 1) + " " +
                    (new Random().nextInt(5) + 11) + ":" +
                    (new Random().nextInt(45) + 10) + ":" +
                    (new Random().nextInt(45) + 10) +
                    "'";
            PreparedStatement tempStatement = dbConn.prepareStatement(
                    "INSERT INTO Party VALUES" +
                            "(" + i + ", 'Party<" + i + ">'," +
                            (new Random().nextInt(100) + 1) + ", " +
                            (new Random().nextInt(100) + 1) + ", " +
                            (new Random().nextInt(100) + 1) + ", " +
                            (new Random().nextInt(8000) + 5000) + ", " +
                            timeStampString + ", " +
                            (new Random().nextInt(400) + 1) +
                            ");"
            );

            tempStatement.executeUpdate();

            // Close statement
            tempStatement.close();
        }

        // Print message to user to show progress
        System.out.println("Party table populated!");
    }

    // Method for populating entertainment table
    private static void populateEntertainment(Connection dbConn) throws SQLException {
        // Print message to user to show progress
        System.out.println("Populating Entertainment table...");

        // Real values
        PreparedStatement realValuesEntertainment = dbConn.prepareStatement(
                "INSERT INTO Entertainment VALUES" +
                        "(1, 'Live band', 500)," +
                        "(2, 'Mime', 250)," +
                        "(3, 'Clown', 350)," +
                        "(4, 'Live DJ', 400)," +
                        "(5, 'Magician', 440)," +
                        "(6, 'Live singer', 250)," +
                        "(7, 'Stand up comedy', 600)," +
                        "(8, 'Mariachi band', 700)," +
                        "(9, 'Live MC', 150)," +
                        "(10, 'Lights show', 400)," +
                        "(11, 'Cooking class', 500);"
        );

        realValuesEntertainment.executeUpdate();

        // Close statement
        realValuesEntertainment.close();

        // Choose random values to populate table with
        for (int i = 12; i < 120; i++) {
            PreparedStatement tempStatement = dbConn.prepareStatement(
                    "INSERT INTO Entertainment VALUES" +
                            "(" + i + ", 'Entertainment<" + i + ">'," + (new Random().nextInt(1000) + 1) + ");"
            );

            tempStatement.executeUpdate();

            // Close statement
            tempStatement.close();
        }

        // Print message to user to show progress
        System.out.println("Entertainment table populated!");
    }

    // Method for populating menu table
    private static void populateMenu(Connection dbConn) throws SQLException {
        // Print message to user to show progress
        System.out.println("Populating Menu table...");

        // Real values
        PreparedStatement realValuesMenu = dbConn.prepareStatement(
                "INSERT INTO Menu VALUES" +
                        "(1, 'Seafood', 8)," +
                        "(2, 'Mediterranean', 12)," +
                        "(3, 'American', 7)," +
                        "(4, 'Italian', 13)," +
                        "(5, 'Jamaican', 12)," +
                        "(6, 'British', 6.70)," +
                        "(7, 'Mexican', 12.50)," +
                        "(8, 'Indian', 10)," +
                        "(9, 'Chinese', 8.0)," +
                        "(10, 'Japanese', 13)," +
                        "(11, 'South African', 12);"
        );

        realValuesMenu.executeUpdate();

        // Close statement
        realValuesMenu.close();

        // Choose random values to populate table with
        for (int i = 12; i < 120; i++) {
            PreparedStatement tempStatement = dbConn.prepareStatement(
                    "INSERT INTO Menu VALUES" +
                            "(" + i + ", 'Menu<" + i + ">'," + (new Random().nextInt(20) + 1) + ");"
            );

            tempStatement.executeUpdate();

            // Close statement
            tempStatement.close();
        }

        // Print message to user to show progress
        System.out.println("Menu table populated!");
    }

    // Method for populating venue table
    private static void populateVenue(Connection dbConn) throws SQLException {
        // Print message to user to show progress
        System.out.println("Populating Venue table...");

        // Real values
        PreparedStatement realValuesVenue = dbConn.prepareStatement(
                "INSERT INTO Venue VALUES" +
                        "(1, 'Tudor Place', 500)," +
                        "(2, 'Town Hall', 1000)," +
                        "(3, 'Bowling Alley', 500)," +
                        "(4, 'Youth Club', 200)," +
                        "(5, 'Bingo Club', 250)," +
                        "(6, 'Retirement Home', 150)," +
                        "(7, 'Town College', 200)," +
                        "(8, 'Local School', 450)," +
                        "(9, 'Bar', 300)," +
                        "(10, 'Nightclub', 700)," +
                        "(11, 'Victoria Palace', 900);"
        );

        realValuesVenue.executeUpdate();

        // Close statement
        realValuesVenue.close();

        // Choose random values to populate table with
        for (int i = 12; i < 120; i++) {
            PreparedStatement tempStatement = dbConn.prepareStatement(
                    "INSERT INTO Venue VALUES" +
                            "(" + i + ", 'Venue<" + i + ">'," + (new Random().nextInt(1000) + 1) + ");"
            );

            tempStatement.executeUpdate();

            // Close statement
            tempStatement.close();
        }

        // Print message to user to show progress
        System.out.println("Venue table populated!");
    }

    // Method for creating new tables
    private static void createNewTables(Connection dbConn) throws SQLException {
        // Print message to user to show progress
        System.out.println("Creating new empty tables...");

        // Statement for creating Venue table
        PreparedStatement createVenueTable = dbConn.prepareStatement(
                "CREATE TABLE Venue" +
                        "(vid INTEGER UNIQUE," +
                        "name TEXT NOT NULL," +
                        "venuecost NUMERIC NOT NULL," +
                        "PRIMARY KEY (vid)," +
                        "CHECK (venuecost >= 0 ))"
        );

        // Statement for creating Menu table
        PreparedStatement createMenuTable = dbConn.prepareStatement(
                "CREATE TABLE Menu" +
                        "(mid INTEGER UNIQUE," +
                        "description TEXT NOT NULL," +
                        "costprice NUMERIC NOT NULL," +
                        "PRIMARY KEY (mid)," +
                        "CHECK (costprice >= 0 ))"
        );

        // Statement for creating Entertainment table
        PreparedStatement createEntertainmentTable = dbConn.prepareStatement(
                "CREATE TABLE Entertainment" +
                        "(eid INTEGER UNIQUE," +
                        "description TEXT NOT NULL," +
                        "costprice NUMERIC NOT NULL," +
                        "PRIMARY KEY (eid)," +
                        "CHECK (costprice >= 0 ))"
        );

        // Statement for creating Party table
        PreparedStatement createPartyTable = dbConn.prepareStatement(
                "CREATE TABLE Party" +
                        "(pid INTEGER UNIQUE," +
                        "name TEXT NOT NULL," +
                        "mid INTEGER NOT NULL," +
                        "vid INTEGER NOT NULL," +
                        "eid INTEGER NOT NULL," +
                        "price NUMERIC NOT NULL," +
                        "timing TIMESTAMP NOT NULL," +
                        "numberofguests INTEGER NOT NULL," +
                        "PRIMARY KEY (pid)," +
                        "FOREIGN KEY (mid) REFERENCES Menu(mid)" +
                        "ON DELETE CASCADE " +
                        "ON UPDATE CASCADE," +
                        "FOREIGN KEY (vid) REFERENCES Venue(vid)" +
                        "ON DELETE CASCADE " +
                        "ON UPDATE CASCADE," +
                        "FOREIGN KEY (eid) REFERENCES Entertainment(eid)" +
                        "ON DELETE CASCADE " +
                        "ON UPDATE CASCADE," +
                        "CHECK (price >=0 )," +
                        "CHECK (numberofguests >= 0));"
        );


        // Create new tables
        createVenueTable.executeUpdate();
        createMenuTable.executeUpdate();
        createEntertainmentTable.executeUpdate();
        createPartyTable.executeUpdate();

        // Close statements
        createVenueTable.close();
        createMenuTable.close();
        createEntertainmentTable.close();
        createPartyTable.close();

        // Print message to user to show progress
        System.out.println("Empty tables created!");
    }

    // Method to remove any existing tables
    private static void dropTables(Connection dbConn) throws SQLException {
        // Print message to user to show progress
        System.out.println("Removing existing tables...");

        // Drop tables if they exist
        PreparedStatement dropPartyTable = dbConn.prepareStatement(
                "DROP TABLE IF EXISTS Party, Menu, Entertainment, Venue"
        );

        dropPartyTable.executeUpdate();

        // Close statement
        dropPartyTable.close();

        // Print message to user to show progress
        System.out.println("Any existing tables removed!");
    }
}
