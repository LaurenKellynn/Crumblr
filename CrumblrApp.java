import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    private Path menuItemFile;

    public CrumblrApp(Path menuItemFile) {
        this.menuItemFile = menuItemFile;
    }

    /**
     * method: addMenuItem
     * parameters: Strings from user input via GUI
     * return: Files, String
     * purpose: Validations for the new menu item being added by the user via the GUI.
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

        int id = generateID();

        String newItem = id + "/" +
                String.join("/", itemDescription, itemQuantity, dateMade, shelfLife, expirationDate, allergens);

        if (Files.exists(menuItemFile) && Files.size(menuItemFile) > 0) {
            newItem = System.lineSeparator() + newItem;
        }
        Files.writeString(menuItemFile, newItem, StandardOpenOption.APPEND);

        return expirationDate;
    }

    /**
     * method: deleteMenuItem()
     * parameters: String for the item's id
     * return: Files
     * purpose: This method holds the validation and logic for taking in the menu item's
     * id that the user would like to delete and then goes through the list to remove that string
     * via a for loop. It them writes back into the file the new string of menu items.
     */
    public void deleteMenuItem(String id) throws IOException {

        String[] menuCollection = Files.readString(menuItemFile).split(System.lineSeparator());

        boolean found = false;

        //For loop to go through the collection and if the user input matches,
        //replace with an empty line
        for (int i = 0; i < menuCollection.length; i++) {
            if (menuCollection[i].isBlank()) continue;
            //Splitting so the user only needs to enter the description before / and not whole line.
            String[] fields = menuCollection[i].split("/");
            if (fields[0].equals(id)) {
                menuCollection[i] = "";
                found = true;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Menu item not found.");
        }

        //Rebuilding the collection without the deleted entry
        StringBuilder newCollection = new StringBuilder();
        for (
                String s : menuCollection) {
            if (!s.isEmpty()) {
                if (newCollection.length() > 0) {
                    newCollection.append(System.lineSeparator());
                }
                newCollection.append(s);
            }
        }
        //Writing back into the file as a single string
        Files.writeString(menuItemFile, newCollection.toString());
    }

    /**
     * method: generateID
     * parameters: none
     * return: int
     * purpose: To add a unique id to each menu item entry
     */
    private int generateID() throws IOException {
        if (!Files.exists(menuItemFile)) return 1;

        int id = 0;

        for (String line : Files.readAllLines(menuItemFile)){
            if (line.isBlank()) continue;

            String [] fields = line.split("/");
            int itemId = Integer.parseInt(fields[0]);

            if (itemId > id){
                id = itemId;
            }
        }
        return id + 1;
    }

    /**
     * method: menuItemFile
     * parameters: None
     * return: File path
     * purpose: To hold and return the file path
     */
    public Path menuItemFile() {
        return menuItemFile;
    }

    /**
     * method: updateMenuItem
     * parameters: Strings from user input
     * return: none
     * purpose: To have the user enter the ID of the menu item they want to edit,
     * Find that item in the list, and prompts the user on which attribute they'd
     * like to edit. Writes back into the file.
     */
    public void updateMenuItem(String id, String choice, String newValue) throws IOException {

        newValue = newValue.trim();
        id = id.trim();

        String[] menuCollection = Files.readString(menuItemFile).split(System.lineSeparator());

        boolean found = false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        for (int i = 0; i < menuCollection.length; i++) {
            if (menuCollection[i].isBlank()) continue;
            //Splitting so the user only needs to enter the description before / and not whole line.
            String[] fields = menuCollection[i].split("/");
            if (fields[0].equals(id)) {

                switch (choice) {
                    case "1": //Updating Description
                        if (newValue.isEmpty() || newValue.length() > 30) {
                                throw new IllegalArgumentException("Invalid entry. The menu item's description must be between 1 - 30 characters long.");
                            }
                        fields[1] = newValue;
                        break;

                    case "2": //Updating Quantity
                        try {
                            int quantity = Integer.parseInt(newValue);
                            if (quantity < 0 || quantity > 999) {
                                throw new IllegalArgumentException("Invalid entry. Quantity must be between 0–999.");
                            }
                            fields[2] = newValue;
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid entry. Quantity must be a number.");
                        }
                        break;

                        case "3": //Updating the date made
                            try {
                                LocalDate.parse(newValue, formatter);
                                fields[3] = newValue;
                                } catch (DateTimeParseException e) {
                                    throw new IllegalArgumentException("Invalid entry. Please use MM-DD-YYYY format.");
                                }
                            break;

                        case "4": //Updating shelf life
                            try {
                                int life = Integer.parseInt(newValue);
                                    if (life < 1 || life > 99) {
                                        throw new IllegalArgumentException("Invalid entry. Shelf life must be between 1–99 days.");
                                    }
                                    fields[4] = newValue;
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException("Invalid entry. Shelf life must be a number.");
                            }
                            break;

                        case "5": //updating allergens
                            if (newValue.isEmpty() || newValue.length() > 500) {
                                throw new IllegalArgumentException("Invalid entry. Must be between 1–500 characters.");
                            }
                            fields[6] = newValue;
                            break;

                        default:
                            throw new IllegalArgumentException("Invalid selection.");
                    }

                    //Updating the expiration date if the date made or the shelf life changes.
                    LocalDate dateMade = LocalDate.parse(fields[3], formatter);
                    int shelfLife = Integer.parseInt(fields[4]);
                    fields[5] = dateMade.plusDays(shelfLife).format(formatter);

                    menuCollection[i] = String.join("/", fields);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Menu item not found.");
            }
        //Rebuilding the file
        Files.writeString(menuItemFile, String.join(System.lineSeparator(), menuCollection));
    }

    /**
     * method: validateTxtFile
     * parameters: File path
     * return: none
     * purpose: To validate that the file is not blank and is a .txt file
     */
    public static void validateTxtFile(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("No file selected.");
        }

        String fileName = filePath.getFileName().toString().toLowerCase();

        if (!fileName.endsWith(".txt")) {
            throw new IllegalArgumentException("Invalid file type. Please select a .txt file.");
        }
    }
}
