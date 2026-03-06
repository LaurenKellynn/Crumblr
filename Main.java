import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Lars Kellynn
 * CEN 3024C - Software Development 1
 * February 28, 2026
 * Main.java
 * This is the main class that will stay in a while loop, calling the display Menu method and prompting the user.
 * Only exiting when the user chooses.
 */
public class Main {

    /**
     * method: Main
     * parameters: none
     * return: none
     * purpose: calls the displayMenu method in a while loop,
     * gets the users input for the menu and triggers that method chosen with
     * conditional statements
     */
    public static void main(String[] args) throws IOException {

        //Path to choose the file from the user. If the file doesn't exist, we will create one.
        Scanner userFile = new Scanner(System.in);
        System.out.println("Please enter the full path to the text file.");
        System.out.println("If the file does not exist, we will create one for you.");
        System.out.print("File path: ");
        Path menuItemFile = Path.of(userFile.nextLine());
        if (!Files.exists(menuItemFile)) {
            Files.createFile(menuItemFile);
        }

        CrumblrApp begin = new CrumblrApp(menuItemFile);

        Scanner userInput = new Scanner(System.in);
        int option = 0;

        while (option != 5) {
            begin.displayMenu();

            try {
                option = Integer.parseInt(userInput.nextLine());
            } catch (NumberFormatException e) {
                option = 0;
            }

            //if statements to trigger the method the user chooses
            if (option == 1) {
                begin.displayMenuItems();
            } else if (option == 2) {
                begin.addMenuItemManually(userInput);
            } else if (option == 3) {
                begin.updateMenuItem(userInput);
            } else if (option == 4) {
                begin.deleteMenuItem(userInput);
            } else {
                if (option != 5) {
                    System.out.println("Please make a valid selection: Options 1, 2, 3, 4 or 5.");
                }
            }
        }
    }

}
