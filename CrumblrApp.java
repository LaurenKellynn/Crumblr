import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lars Kellynn
 * CEN 3024C - Software Development 1
 * February 28, 2026
 * CrumblrApp.java
 * This class holds all the methods for the GUI to utilize.
 */
public class CrumblrApp {

    private Connection conn;
    public CrumblrApp(String url, String user, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found.");
        }
        this.conn = DriverManager.getConnection(url, user, password);
    }

    /**
     * method: addMenuItem
     * parameters: Strings from user input via GUI
     * return: String
     * purpose: Validations for the new menu item being added by the user via the GUI.
     *
     * @return
     */
    public String addMenuItem(String itemDescription, String itemQuantity, String dateMade, String shelfLife, String allergens) throws IOException {
        if (itemDescription.isEmpty() || itemDescription.length() > 30) {
            throw new IllegalArgumentException("Invalid entry. The menu item's description must be between 1 - 30 characters long.");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(itemQuantity);
            if (quantity < 0 || quantity > 999) {
                throw new IllegalArgumentException("Invalid entry. The quantity must be between 0–999.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid entry. The quantity must be between 0–999.");
        }

        //Defining the expected format of the dates.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate date;
        try {
            date = LocalDate.parse(dateMade, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid entry. Please enter the date in the format 'MM-DD-YYYY'. ");
        }

        int lifeDays;
        try {
            lifeDays = Integer.parseInt(shelfLife);
            if (lifeDays < 1 || lifeDays > 99) {
                throw new IllegalArgumentException("Invalid entry. Shelf life must be between 1 – 99 days.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid entry. Shelf life must be between 1 – 99 days.");
        }

        //Custom action to calculate the expiration date
        String expirationDate = date.plusDays(lifeDays).format(formatter);

        if (allergens.isEmpty() || allergens.length() > 500) {
            throw new IllegalArgumentException("Invalid entry. The menu item's allergen list must be between 1 - 500 characters long.");
        }
        String sql = "INSERT INTO menu (description, quantity, date_made, shelf_life, expiration_date, allergens) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemDescription);
            stmt.setInt(2, quantity);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setInt(4, lifeDays);
            stmt.setDate(5, java.sql.Date.valueOf(date.plusDays(lifeDays)));
            stmt.setString(6, allergens);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return expirationDate;
    }

    /**
     * method: deleteMenuItem()
     * parameters: String for the item's id
     * return: None
     * purpose: This method runs the sql statement to delete the requested menu item from the database.
     */
    public void deleteMenuItem(String id) throws IOException {
        String sql = "DELETE FROM menu WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Menu item not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * method: getConnection()
     * parameters: None
     * return: Sql database connection
     * purpose: This method connects to the database
     */
    public java.sql.Connection getConnection() {
        return conn;
    }

    /**
     * method: updateMenuItem
     * parameters: Strings from user input
     * return: none
     * purpose: To have the user enter the ID of the menu item they want to edit,
     * Uses sql to update the menu item in the database.
     */
    public void updateMenuItem(String id, String choice, String newValue) throws IOException {
        newValue = newValue.trim();
        id = id.trim();
        String column;

        switch (choice) {
            case "1":
                if (newValue.isEmpty() || newValue.length() > 30) {
                    throw new IllegalArgumentException("Invalid entry. The menu item's description must be between 1 - 30 characters long.");
                }
                column = "description";
                break;

            case "2":
                try {
                    int q = Integer.parseInt(newValue);
                    if (q < 0 || q > 999) throw new Exception();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid entry. Quantity must be between 0–999.");
                }
                column = "quantity";
                break;

            case "3":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    LocalDate.parse(newValue, formatter);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid entry. Please use MM-DD-YYYY format.");
                }
                column = "date_made";
                break;

            case "4":
                try {
                    int life = Integer.parseInt(newValue);
                    if (life < 1 || life > 99) throw new Exception();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid entry. Shelf life must be between 1–99 days.");
                }
                column = "shelf_life";
                break;

            case "5":
                if (newValue.isEmpty() || newValue.length() > 500) {
                    throw new IllegalArgumentException("Invalid entry. Must be between 1–500 characters.");
                }
                column = "allergens";
                break;

            default:
                throw new IllegalArgumentException("Invalid selection.");
        }

        String sql = "UPDATE menu SET " + column + " = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (column.equals("quantity") || column.equals("shelf_life")) {
                stmt.setInt(1, Integer.parseInt(newValue));
            } else if (column.equals("date_made")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                stmt.setDate(1, java.sql.Date.valueOf(LocalDate.parse(newValue, formatter)));
            } else {
                stmt.setString(1, newValue);
            }

            stmt.setInt(2, Integer.parseInt(id));
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Menu item not found.");
            }

            //Recalculate the expiration date
            if (column.equals("date_made") || column.equals("shelf_life")) {
                String updateExp = "UPDATE menu " +
                        "SET expiration_date = DATE_ADD(date_made, INTERVAL shelf_life DAY) " +
                        "WHERE id = ?";
                try (PreparedStatement expStmt = conn.prepareStatement(updateExp)) {
                    expStmt.setInt(1, Integer.parseInt(id));
                    expStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
