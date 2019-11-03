import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DBInterface {
    public static void main(String[] args) {
        // Set up postgreSQL
        if (!DBConnector.initialisePostgres()) {
            System.out.println("Couldn't initiliase postgreSQL, quitting.");

            System.exit(-1);
        }

        // Connect to database
        Connection dbConn = DBConnector.connectDatabase();

        if (dbConn != null) {
            System.out.println("Database accessed!");

            // Running flag
            boolean running = true;

            // Display help message
            System.out.println("Welcome to the database interface. Please enter one of the following:\n" +
                    "p: Produce a report for a party\n" +
                    "m: Produce report for menu item\n" +
                    "i: Insert a new party into the database\n" +
                    "quit: end program"
            );

            // Initialise buffered reader for user input
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            // Try block to catch IO exceptions from BufferedReader
            try {
                // Loop, asking for user input
                while (running) {
                    // Message for user to know where to input data
                    System.out.println("\nEnter one of p, m, i or quit):");

                    // Get user input
                    String userInput = bufferedReader.readLine().toLowerCase();

                    switch (userInput) {
                        case "p":
                            // Call method to handle party query
                            try {
                                handlePartyQuery(dbConn, bufferedReader);
                            } catch (SQLException e) {
                                System.out.println("Exception in the party query method!");
                                e.printStackTrace();
                            }

                            break;
                        case "m":
                            // Call method to handle menu query
                            try {
                                handleMenuQuery(dbConn, bufferedReader);
                            } catch (SQLException e) {
                                System.out.println("Exception in the menu query method!");
                                e.printStackTrace();
                            }

                            break;
                        case "i":
                            // Call method to handle party inserting
                            try {
                                handleInsertParty(dbConn, bufferedReader);
                            } catch (SQLException e) {
                                System.out.println("Exception in the party parrty insertion method!");
                                e.printStackTrace();
                            }

                            break;
                        case "quit":
                            // End the program
                            System.out.println("Quitting...");
                            running = false;

                            break;
                        default:
                            // Notify user that an invalid command has been entered
                            System.out.println("Invalid  command entered. Please enter one of \"1\", \"2\", \"3\" or \"quit\"");

                            break;
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close connection to database
                    dbConn.close();

                    // close buffered reader
                    bufferedReader.close();
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            // Notify user that a connection has not beed made
            System.out.println("Failed to make connection");
        }
    }

    // Method to handle party query
    private static void handlePartyQuery(Connection dbConn, BufferedReader bufferedReader) throws IOException, SQLException {
        System.out.println("Enter Party ID to generate report for: ");

        // Get party ID
        String pid = bufferedReader.readLine();

        // Query to get The pid, The party’s name,, The mid, The vid, The eid, The quoted price, The date and time, And number of guests
        PreparedStatement partyQuery = dbConn.prepareStatement(
                "SELECT Party.pid AS \"Party ID\"," +
                        "Party.name AS \"Party name\"," +
                        "Venue.name AS \"Venue name\"," +
                        "Menu.description as \"Menu description\"," +
                        "Entertainment.description AS \"Entertainment description\"," +
                        "Party.numberofguests AS \"Number of guests\"," +
                        "Party.price AS \"Price charged\"," +
                        "Venue.venuecost + Entertainment.costprice + Menu.costprice*Party.numberofguests AS \"Total cost price\"," +
                        "Party.price - (Venue.venuecost + Entertainment.costprice + Menu.costprice*Party.numberofguests) AS \"Net profit\" " +
                        "FROM Party, Venue, Menu, Entertainment " +
                        "WHERE Menu.mid = Party.mid AND Entertainment.eid = Party.eid AND Venue.vid = Party.vid AND Party.pid = ? ;"
        );

        // Try to inject pid to statement, if not possible then notify user
        try {
            partyQuery.setInt(1, Integer.parseInt(pid));
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input! Please enter an integer next time");
            partyQuery.close();
            return;
        }

        // Execute the query
        ResultSet rs = null;
        try {
            rs = partyQuery.executeQuery();
        } catch (SQLException e) {
            // Notify user if incorrect input
            System.out.println("SQL error, please try again");
            rs.close();
            partyQuery.close();
            return;
        }

        // Go through result set and print out data
        boolean emptyResultSet = true;

        while (rs.next()) {
            emptyResultSet = false;

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if (i > 1) System.out.print("\n"); // Print new line if not first
                System.out.print(rs.getMetaData().getColumnName(i) + ": " + rs.getString(i));
            }
        }

        // If nothing in result set, notify user that the entered pid isn't stored
        if (emptyResultSet) {
            System.out.println("Entered party ID not in the table!");
        } else {
            // Print a new line for aesthetic purposes
            System.out.println();
        }

        // Close statement and result set
        rs.close();
        partyQuery.close();
    }

    // Method to handle menu query
    private static void handleMenuQuery(Connection dbConn, BufferedReader bufferedReader) throws IOException, SQLException {
        System.out.println("Enter Menu ID to generate report for: ");

        // Get menu ID
        String mid = bufferedReader.readLine();

        // Query to get The menu’s id and description, The menu’s cost price, The number of times the menu was used – the number of parties and the total number of guests
        PreparedStatement menuQuery = dbConn.prepareStatement(
                "SELECT Menu.mid AS \"Menu ID\"," +
                        "Menu.costprice AS \"Menu cost\"," +
                        "COUNT(Party.mid) AS \"Number of parties used in\"," +
                        "SUM(Party.numberofguests) AS \"Number of guests used by\" " +
                        "FROM Menu, Party " +
                        "WHERE Party.mid = Menu.mid AND Menu.mid = ? " +
                        "GROUP BY menu.mid;");

        // Try to inject mid to statement, if not possible then notify user
        try {
            menuQuery.setInt(1, Integer.parseInt(mid));
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input! Please enter an integer next time");
            menuQuery.close();
            return;
        }

        ResultSet rs = null;

        // Execute the query
        try {
            rs = menuQuery.executeQuery();
        } catch (SQLException e) {
            // Notify user if incorrect input
            System.out.println("Incorrect input! Please enter an integer next time");
            menuQuery.close();
            rs.close();
            return;
        }

        // Go through result set and print out data
        boolean emptyResultSet = true;
        while (rs.next()) {
            emptyResultSet = false;

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                if (i > 1) System.out.print("\n"); // Print new line if not first
                System.out.print(rs.getMetaData().getColumnName(i) + ": " + rs.getString(i));
            }
        }

        // If no result set, menu wasn't in the table, so notify user
        if (emptyResultSet) {
            System.out.println("Entered menu ID not in the table!");
        } else {
            // Print a new line for aesthetic purposes
            System.out.println();
        }

        // Close statement and result set
        menuQuery.close();
        rs.close();
    }

    // Method to handle party insertion
    private static void handleInsertParty(Connection dbConn, BufferedReader bufferedReader) throws SQLException, IOException {
        // Prepare statement to insert party according to given values
        PreparedStatement insertParty = dbConn.prepareStatement(
                "INSERT INTO PARTY VALUES" +
                        "(?, ?, ?, ?, ?, ?, ?, ?);"
        );

        // Inject party ID according to user input
        boolean correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter Party ID to insert (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setInt(1, Integer.parseInt(newUserInput));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not an integer)! Please try again");
            }
        }

        // Inject party name according to user input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter Party name (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setString(2, newUserInput);
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not a string)! Please try again");
            }
        }

        // Inject menu ID according to user input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter Menu ID for party (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setInt(3, Integer.parseInt(newUserInput));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not an integer)! Please try again");
            }
        }

        // Inject venue ID according to user input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter Venue ID for party (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setInt(4, Integer.parseInt(newUserInput));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not an integer)! Please try again");
            }
        }

        // Inject entertainment ID according to user input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter Entertainment ID for party (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setInt(5, Integer.parseInt(newUserInput));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not an integer)! Please try again");
            }
        }

        // Inject price according to user input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter price for party (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setDouble(6, Double.parseDouble(newUserInput));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not an decimal)! Please try again");
            }
        }

        // Inject time accordng to input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter time for party in the form \"YYYY-MM-DD HH:MM\" (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    // Set format for date
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm" +
                            "");

                    // Insert date and time into correct format
                    LocalDateTime formatDateTime = LocalDateTime.parse(newUserInput, formatter);

                    // Set time and date
                    insertParty.setTimestamp(7, Timestamp.valueOf(formatDateTime));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Incorrect input (not correct format)! Please try again");
            }
        }

        // Inject number of guests accordng to input
        correctInput = false; // Flag for whether input is correct
        while (!correctInput) {
            try {
                System.out.println("Enter number of guests for party (or input \'quit\' to quit): ");

                // Get input
                String newUserInput = bufferedReader.readLine();

                // Check if user doesn't want to quit
                if (!newUserInput.toLowerCase().equals("quit")) {
                    insertParty.setInt(8, Integer.parseInt(newUserInput));
                    correctInput = true;
                } else {
                    insertParty.close();
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input (not an integer)! Please try again");
            }
        }

        // Insert party into table
        try {
            insertParty.executeUpdate();
        } catch (SQLException e) {
            switch (e.getSQLState()) {
                case ("23505"): // State when party already stored at the specified ID
                    System.out.println("There's already a party stored at the entered ID, can't insert.");
                    insertParty.close();
                    break;
                case ("42601"): // State when incorrectly formatted data entered
                    System.out.println("Tried to insert incorrect data, please double check and try again.");
                    insertParty.close();
                    break;
                case ("22003"): // State when party with data that violated constraints inserted
                    System.out.println("Tried to insert a party with valid data, except some parts were not within constraints.");
                    insertParty.close();
                    break;
                default: // For all other exception
                    System.out.println("An exception has occurred, check the message below and try again double checking your values");
                    System.out.println(e.getMessage());
                    insertParty.close();
                    break;
            }
            return;
        }

        // Notify user the party has been inserted
        System.out.println("Party inserted!");

        // Close statement
        insertParty.close();
    }

}
