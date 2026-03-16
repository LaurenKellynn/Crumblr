import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Lars Kellynn
 * CEN 3024C - Software Development 1
 * February 28, 2026
 * CrumblrApp.java
 * This class displays a menu to the user, requests a file path from the user, reads in the file path
 * and amends the contents based on the user adding, editing or deleting records via the methods they choose.
 */
public class CrumblrApp {

    private Path menuItemFile;

    public CrumblrApp(Path menuItemFile) {
        this.menuItemFile = menuItemFile;
    }

    /**
     * method: addMenuItemManually
     * parameters: Scanner for user input
     * return: Files, String
     * purpose: Calls the createMenuItem method to build the new menu item and adds the item
     * to the collection.
     */
    public void addMenuItemManually(Scanner userInput) throws IOException {
        int id = generateID();
        String newMenuEntry = System.lineSeparator() + id + "/" + createMenuItem(userInput);
        Files.writeString(menuItemFile, newMenuEntry, StandardOpenOption.APPEND);
    }

    /**
     * method: createMenuItem
     * parameters: Scanner for user input
     * return: String
     * purpose: Prompts the user for the new menu item's description, quantity,
     * date made, shelf life, calculates the expiration date, and prompts for any known allergies.
     * Then adds this as a string to the .txt file
     */
    private String createMenuItem(Scanner userInput) {
        String itemDescription;
        String itemQuantity;
        String dateMade;
        LocalDate date = null;
        String shelfLife;
        int life = 0;
        String expirationDate;
        String knownAllergens;

        String dateFormat = "MM-dd-yyyy"; //Defining the expected format of the dates.
        DateTimeFormatter formatter = null;

        //while loops to continue each question until the user's input is valid.
        while (true) {
            System.out.println("Please enter the menu item's description: ");
            itemDescription = userInput.nextLine();
            String errorMessage = "Invalid entry. The menu item's description must be between 1 - 30 characters long. ";
            if (itemDescription.isEmpty() || itemDescription.length() > 30) {
                System.out.println(errorMessage);
            } else {
                break;
            }
        }

        while (true) {
            System.out.println("Please enter the quantity of this menu item: ");
            itemQuantity = (userInput.nextLine());
            String errorMessage = "Invalid entry. The quantity must be between 0–999.";
            try {
                int quantity = Integer.parseInt(itemQuantity);

                if (quantity < 0 || quantity > 999) {
                    System.out.println(errorMessage);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }

        }

        while (true) {
            System.out.println("Please enter the date the menu item was made: ");
            dateMade = userInput.nextLine();
            String errorMessage = "Invalid entry. Please enter the date in the format 'MM-DD-YYYY'. ";
            try {
                formatter = DateTimeFormatter.ofPattern(dateFormat);
                date = LocalDate.parse(dateMade, formatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println(errorMessage);
            }
        }

        while (true) {
            System.out.println("Please enter the menu item's shelf life (in days): ");
            shelfLife = (userInput.nextLine());
            String errorMessage = "Invalid entry. Shelf life must be between 1 – 99 days.";
            try {
                life = Integer.parseInt(shelfLife);

                if (life < 1 || life > 99) {
                    System.out.println(errorMessage);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }

        //Custom action to calculate the expiration date
        LocalDate calculateExpirationDate = date.plusDays(life);
        expirationDate = calculateExpirationDate.format(formatter);
        System.out.println("The expiration date for " + itemDescription + " has been automatically calculated and entered as: " + expirationDate);

        while (true) {
            System.out.println("Please enter the menu item's known allergies. If none, please enter 'None': ");
            knownAllergens = userInput.nextLine();
            String errorMessage = "Invalid entry. The menu item's allergen list must be between 1 - 500 characters long. ";
            if (knownAllergens.isEmpty() || knownAllergens.length() > 500) {
                System.out.println(errorMessage);
            } else {
                break;
            }
        }

        //String joiner to add the string together separated by a slash
        StringJoiner sj = new StringJoiner("/");
        sj.add(itemDescription);
        sj.add(itemQuantity);
        sj.add(dateMade);
        sj.add(shelfLife);
        sj.add(expirationDate);
        sj.add(knownAllergens);

        return sj.toString();
    }

    /**
     * method: deleteMenuItem()
     * parameters: Scanner for user input
     * return: Files
     * purpose: reads in the .txt file to a string[], prompts the user for the menu item's description,
     * amends the string[], adds the new string to the .txt file.
     */
    public void deleteMenuItem(Scanner userInput) throws IOException {

        String listCollection = Files.readString(menuItemFile);
        String[] menuCollection = listCollection.split(System.lineSeparator());

        String id;
        boolean found = false;

        //while loop to continue until user input is valid
        while (!found) {
            System.out.println("Please type the ID of the menu item that you'd like to have removed: ");
            id = userInput.nextLine();

            if (id.isEmpty()) {
                System.out.println("Invalid entry. Please type the menu item that you'd like to have removed.");
                continue;
            }
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
            if (found) {
                System.out.println("Menu item deleted successfully!");
            } else {
                System.out.println("Menu item not found.");
            }
        }
        //Rebuilding the collection without the deleted entry
        StringBuilder newCollection = new StringBuilder();
        for (String s : menuCollection) {
            if (!s.isEmpty()) {
                newCollection.append(s).append(System.lineSeparator());
            }
        }
        //Writing back into the file as a single string
        String finalString = newCollection.toString();
        Files.writeString(menuItemFile, finalString);
    }

    /**
     * method: displayMenu
     * parameters: none
     * return: none
     * purpose: Displays the welcome screen and menu to the user
     */
    public void displayMenu() {
        System.out.println();
        System.out.println("Welcome to Crumblr!");
        System.out.println();
        System.out.println("What would you like to do?");
        System.out.println();
        System.out.println("1. View all menu items.");
        System.out.println("2. Add a new menu item.");
        System.out.println("3. Edit a menu item.");
        System.out.println("4. Delete a menu item.");
        System.out.println("5. Quit");
        System.out.println();
        System.out.println("Please choose option 1, 2, 3, 4, or 5: ");
    }

    /**
     * method: displayMenuItems
     * parameters: none
     * return: none
     * purpose: Displays the current contents of the Menu Item File.
     */
    public void displayMenuItems() throws IOException {
        System.out.println(Files.readString(menuItemFile));
        System.out.println();
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
     * method: updateMenuItem
     * parameters: Scanner for user input
     * return: none
     * purpose: To have the user enter the ID of the menu item they want to edit,
     * Find that item in the list, and prompts the user on which attribute they'd
     * like to edit.
     */
    public void updateMenuItem(Scanner userInput) throws IOException {

        String[] menuCollection = Files.readString(menuItemFile).split(System.lineSeparator());

        boolean found = false;

        while (!found) {
            System.out.println("Please type the ID of the menu item you would like to edit: ");
            String id = userInput.nextLine();

            for (int i = 0; i < menuCollection.length; i++) {
                if (menuCollection[i].isBlank()) continue;
                //Splitting so the user only needs to enter the description before / and not whole line.
                String[] fields = menuCollection[i].split("/");
                if (fields[0].equals(id)) {
                    System.out.println("Updating menu item: " + fields[1]);
                    System.out.println("1. Description");
                    System.out.println("2. Quantity");
                    System.out.println("3. Date Made");
                    System.out.println("4. Shelf Life");
                    System.out.println("5. Allergens");
                    System.out.println();

                    String userChoice = userInput.nextLine();

                    switch (userChoice) {
                        case "1":
                            while (true) {
                                System.out.println("Please enter the item's new description: ");
                                String description = userInput.nextLine();

                                if (description.isEmpty() || description.length() > 30) {
                                    System.out.println("Invalid entry. The menu item's description must be between 1 - 30 characters long.");
                                } else {
                                    fields[1] = description;
                                    break;
                                }
                            }
                            break;
                        case "2":
                            while (true) {
                                System.out.println("Please enter the item's new quantity: ");
                                String quantityInput = userInput.nextLine();

                                try {
                                    int quantity = Integer.parseInt(quantityInput);

                                    if (quantity < 0 || quantity > 999) {
                                        System.out.println("Invalid entry. Quantity must be between 0–999.");
                                    } else {
                                        fields[2] = quantityInput;
                                        break;
                                    }

                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid entry. Quantity must be a number.");
                                }
                            }
                            break;
                        case "3":
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

                            while (true) {
                                System.out.println("Please enter the item's new 'date made' (MM-DD-YYYY): ");
                                String dateInput = userInput.nextLine();

                                try {
                                    LocalDate.parse(dateInput, formatter);
                                    fields[3] = dateInput;
                                    break;
                                } catch (DateTimeParseException e) {
                                    System.out.println("Invalid entry. Please use MM-DD-YYYY format.");
                                }
                            }
                            break;

                        case "4":
                            while (true) {
                                System.out.println("Please enter the new shelf life of the item: ");
                                String shelfInput = userInput.nextLine();

                                try {
                                    int life = Integer.parseInt(shelfInput);

                                    if (life < 1 || life > 99) {
                                        System.out.println("Invalid entry. Shelf life must be between 1–99 days.");
                                    } else {
                                        fields[4] = shelfInput;
                                        break;
                                    }

                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid entry. Shelf life must be a number.");
                                }
                            }
                            break;

                        case "5":
                            while (true) {
                                System.out.println("Please enter the new list of allergens: ");
                                String allergens = userInput.nextLine();

                                if (allergens.isEmpty() || allergens.length() > 500) {
                                    System.out.println("Invalid entry. Must be between 1–500 characters.");
                                } else {
                                    fields[6] = allergens;
                                    break;
                                }
                            }
                            break;

                        default:
                            System.out.println("Please choose option 1, 2, 3, 4, or 5: ");
                            return;
                    }

                    //Updating the expiration date if the date made or the shelf life changes.
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    LocalDate dateMade = LocalDate.parse(fields[3], formatter);
                    int shelfLife = Integer.parseInt(fields[4]);
                    fields[5] = dateMade.plusDays(shelfLife).format(formatter);

                    menuCollection[i] = String.join("/", fields);
                    found = true;
                    System.out.println("Menu item updated successfully!");
                    break;
                }
            }
            if (!found) {
                System.out.println("Menu item not found.");
            }
        }
        //Rebuilding the file
        Files.writeString(menuItemFile, String.join(System.lineSeparator(), menuCollection));
    }
}
