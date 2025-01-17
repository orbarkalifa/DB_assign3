// Or Bar Califa 318279429
// Daniel Fradkin 316410885

import java.sql.*;
import java.util.Scanner;
import java.sql.PreparedStatement;

public class Ex1 {
    String connectionURL = "";
    // jdbc:mysql://localhost:3306/sakila
    // Change the connection string according to your db server address and port
    Connection con = null;
    Scanner input = new Scanner(System.in);

    public void setUpConnection() {
        while (con == null && connectionURL.isEmpty()) {
            try {
                System.out.println("Enter database URL in the following format: jdbc:mysql://localhost:3306/sakila");
                connectionURL = input.nextLine();

                // Validate the URL format if necessary
                if (connectionURL.trim().isEmpty()) {
                    System.out.println("The connection URL cannot be empty. Please try again.");
                    continue;
                }

                // Load MySQL driver (for MySQL 8.x and above)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // TODO: check what to do with password for root
                con = DriverManager.getConnection(connectionURL, "root", "root");

                System.out.println("Connection successful!");

            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
                e.printStackTrace();  // For debugging, you can remove in production
                connectionURL = "";  // Reset URL to try again
                System.out.println("Please enter the correct URL.");
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();  // For debugging, you can remove in production
            }
        }
    }
    public void addMovie() {
        Scanner input = new Scanner(System.in);

        // Ask for required fields
        System.out.println("Enter movie title:");
        String title = input.nextLine();

        int defualt_language_id = 1;



        // Prepare SQL insert query
        String sql = "INSERT INTO film (title, language_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, defualt_language_id);

            // Execute the query
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Movie added successfully!");
            } else {
                System.out.println("Failed to add the movie.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addActor() {
        Scanner input = new Scanner(System.in);

        // Ask for required fields
        System.out.println("Enter actor first name:");
        String firstName = input.nextLine();

        System.out.println("Enter actor last name:");
        String lastName = input.nextLine();

        // Prepare SQL insert query
        String sql = "INSERT INTO actor (first_name, last_name) VALUES (?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);

            // Execute the query
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Actor added successfully!");
            } else {
                System.out.println("Failed to add the actor.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addActorToMovie(int movieId, int actorId) {
        String sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, actorId);

            // Execute the query
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Actor added to movie successfully!");
            } else {
                System.out.println("Failed to add actor to movie.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void executeUserQuery() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your SQL query:");
        String query = input.nextLine();

        try (Statement stmt = con.createStatement()) {
            // Determine the type of query based on the first word
            String queryType = query.trim().split(" ")[0].toUpperCase();

            if ("SELECT".equals(queryType)) {
                // Handle SELECT queries
                ResultSet rs = stmt.executeQuery(query);

                // Print result
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                int counter = 0;
                while (rs.next()) {
                    counter++;
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }

                System.out.println("Number of rows returned: " + counter);
            } else {
                // Handle UPDATE, INSERT, DELETE queries
                int rowsAffected = stmt.executeUpdate(query);
                System.out.println("Query executed successfully. Rows affected: " + rowsAffected);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }
    public void showMenu() {
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1 - Add Movie");
            System.out.println("2 - Add Actor");
            System.out.println("3 - Add Actor to Movie");
            System.out.println("4 - Execute Custom SQL Query");
            System.out.println("5 - Execute Prepared Statement");
            System.out.println("6 - Exit");

            int choice = input.nextInt();
            input.nextLine();  // Consume newline left-over

            switch (choice) {
                case 1:
                    addMovie();
                    break;
                case 2:
                    addActor();
                    break;
                case 3:
                    System.out.println("Enter movie ID:");
                    int movieId = input.nextInt();
                    System.out.println("Enter actor ID:");
                    int actorId = input.nextInt();
                    addActorToMovie(movieId, actorId);
                    break;
                case 4:
                    executeUserQuery();
                    break;
                case 5:
                    // Add prepared statement queries (e.g., searchMoviesByTitle)
                    // Display query options to the user
                    System.out.println("Choose a query to execute:");
                    System.out.println("a. Search for movies by a keyword in the title");
                    System.out.println("b. Search for movies featuring an actor by name");
                    System.out.println("c. Search for movies in a specific language");
                    System.out.println("d. Search for movies with exactly X actors");
                    System.out.println("e. Search for English movies without Robert De Niro");
                    System.out.println("f. Count movies featuring an actor by name");

                    String Qchoice = input.nextLine();

                    try {
                        PreparedStatement pstmt;
                        ResultSet rs;

                        switch (Qchoice) {
                            case "a":
                                System.out.println("Enter a keyword to search for in movie titles:");
                                String keyword = input.nextLine();
                                pstmt = con.prepareStatement("SELECT * FROM film WHERE title LIKE ?");
                                pstmt.setString(1, "%" + keyword + "%");
                                break;

                            case "b":
                                System.out.println("Enter the actor's name:");
                                String actorName = input.nextLine();
                                pstmt = con.prepareStatement("SELECT film.* FROM film INNER JOIN film_actor ON film.film_id = film_actor.film_id INNER JOIN actor ON film_actor.actor_id = actor.actor_id WHERE actor.first_name = ? OR actor.last_name = ?");
                                pstmt.setString(1, actorName);
                                pstmt.setString(2, actorName);
                                break;

                            case "c":
                                System.out.println("Enter the language:");
                                String language = input.nextLine();
                                pstmt = con.prepareStatement("SELECT * FROM film INNER JOIN language ON film.language_id = language.language_id WHERE language.name = ?");
                                pstmt.setString(1, language);
                                break;

                            case "d":
                                System.out.println("Enter the exact number of actors:");
                                int numActors = input.nextInt();
                                input.nextLine(); // Consume newline
                                pstmt = con.prepareStatement("SELECT * FROM film WHERE film_id IN (SELECT film_id FROM film_actor GROUP BY film_id HAVING COUNT(actor_id) = ?)");
                                pstmt.setInt(1, numActors);
                                break;

                            case "e":
                                pstmt = con.prepareStatement(("SELECT film.* FROM film WHERE language_id = (SELECT language_id FROM language WHERE name = 'English') AND film_id NOT IN (SELECT film_id FROM film_actor INNER JOIN actor ON film_actor.actor_id = actor.actor_id WHERE actor.first_name = 'Robert' AND actor.last_name = 'De Niro')"));
                                break;

                            case "f":
                                System.out.println("Enter the actor's name:");
                                String actor = input.nextLine();
                                pstmt = con.prepareStatement("SELECT COUNT(*) AS movie_count FROM film_actor INNER JOIN actor ON film_actor.actor_id = actor.actor_id WHERE actor.first_name = ? OR actor.last_name = ?");
                                pstmt.setString(1, actor);
                                pstmt.setString(2, actor);
                                break;

                            default:
                                System.out.println("Invalid choice. Returning to menu.");
                                return;
                        }

                        // Execute the query and display results
                        rs = pstmt.executeQuery();

                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        // Print column names
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(metaData.getColumnName(i) + "\t");
                        }
                        System.out.println();

                        // Print rows
                        int rowCount = 0;
                        while (rs.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                System.out.print(rs.getString(i) + "\t");
                            }
                            System.out.println();
                            rowCount++;
                        }

                        if (rowCount == 0) {
                            System.out.println("No results found.");
                        } else {
                            System.out.println("Rows returned: " + rowCount);
                        }

                    } catch (SQLException e) {
                        System.out.println("An error occurred while executing the query.");
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

}