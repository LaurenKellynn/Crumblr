import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOError;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CrumblrAppTest {

    //Create object to be tested
    @TempDir
    Path tempDir;

    @org.junit.jupiter.api.Test
    @DisplayName("Add Menu Item Test")
    void addMenuItemManuallyTest() throws IOException {
        //Using our own data to add menu item manually
        Path file = tempDir.resolve("Menu.txt");
        Files.createFile(file);

        CrumblrApp app = new CrumblrApp(file);

        String input = """
                Test Brownie
                10
                03-15-2025
                5
                Testing Allergens: Nuts
                """;

        Scanner scanner = new Scanner (input);
        app.addMenuItemManually(scanner);
        String expected = System.lineSeparator() + "1/Test Brownie/10/03-15-2025/5/03-20-2025/Testing Allergens: Nuts";
        String result = Files.readString(file);

        assertEquals(expected, result, "Error: The menu item was not added.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Delete Menu Item Test")
    void deleteMenuItemTest() throws IOException {
        Path file = tempDir.resolve("Menu.txt");

        String startingData =
                "1/Test Cookie/20/03-10-2026/5/03-15-2026/Gluten" + System.lineSeparator() +
                "2/Test Brownie/10/03-15-2026/5/03-20-2026/Nuts";

        Files.writeString(file, startingData);

        CrumblrApp app = new CrumblrApp(file);
        Scanner scanner = new Scanner ("1\n");
        app.deleteMenuItem(scanner);

        String expected = "2/Test Brownie/10/03-15-2026/5/03-20-2026/Nuts" + System.lineSeparator();
        String result = Files.readString(file);

        assertEquals(expected, result, "Error: The item was not deleted.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Update Menu Item's Description Test")
    void updateMenuItemDescriptionTest() throws IOException {
        Path file = tempDir.resolve("Menu.txt");

        String startingData =
                "1/Test Cookie/20/03-10-2026/5/03-15-2026/Gluten";

        Files.writeString(file, startingData);

        CrumblrApp app = new CrumblrApp(file);

        String input = """
                1
                1
                Test Chocolate Chip Cookie
                """;

        Scanner scanner = new Scanner (input);
        app.updateMenuItem(scanner);

        String expected = "1/Test Chocolate Chip Cookie/20/03-10-2026/5/03-15-2026/Gluten";
        String result = Files.readString(file);
        assertEquals(expected, result, "Error: The menu item's description was not updated.");

    }

    @org.junit.jupiter.api.Test
    @DisplayName("Update Menu Item's Quantity Test")
    void updateMenuItemQuantityTest() throws IOException {
        Path file = tempDir.resolve("Menu.txt");

        String startingData =
                "1/Test Cookie/20/03-10-2026/5/03-15-2026/Gluten";

        Files.writeString(file, startingData);

        CrumblrApp app = new CrumblrApp(file);

        String input = """
                1
                2
                10
                """;

        Scanner scanner = new Scanner (input);
        app.updateMenuItem(scanner);

        String expected = "1/Test Cookie/10/03-10-2026/5/03-15-2026/Gluten";
        String result = Files.readString(file);
        assertEquals(expected, result, "Error: The menu item's quantity was not updated.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Update Menu Item's Date Made / Expiration Date Test")
    void updateMenuItemDateMadeTest() throws IOException {
        Path file = tempDir.resolve("Menu.txt");

        String startingData =
                "1/Test Cookie/20/03-10-2026/5/03-15-2026/Gluten";

        Files.writeString(file, startingData);

        CrumblrApp app = new CrumblrApp(file);

        String input = """
                1
                3
                03-15-2026
                """;

        Scanner scanner = new Scanner (input);
        app.updateMenuItem(scanner);

        String expected = "1/Test Cookie/20/03-15-2026/5/03-20-2026/Gluten";
        String result = Files.readString(file);
        assertEquals(expected, result, "Error: The menu item's date made / expiration date was not updated.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Update Menu Item's Shelf Life / Expiration Date Test")
    void updateMenuItemShelfLifeTest() throws IOException {
        Path file = tempDir.resolve("Menu.txt");

        String startingData =
                "1/Test Cookie/20/03-10-2026/5/03-15-2026/Gluten";

        Files.writeString(file, startingData);

        CrumblrApp app = new CrumblrApp(file);

        String input = """
                1
                4
                10
                """;

        Scanner scanner = new Scanner (input);
        app.updateMenuItem(scanner);

        String expected = "1/Test Cookie/20/03-10-2026/10/03-20-2026/Gluten";
        String result = Files.readString(file);
        assertEquals(expected, result, "Error: The menu item's shelf life / expiration date was not updated.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Update Menu Item's Allergens Test")
    void updateMenuItemAllergensTest() throws IOException {
        Path file = tempDir.resolve("Menu.txt");

        String startingData =
                "1/Test Cookie/20/03-10-2026/5/03-15-2026/Gluten";

        Files.writeString(file, startingData);

        CrumblrApp app = new CrumblrApp(file);

        String input = """
                1
                5
                Nuts, Gluten
                """;

        Scanner scanner = new Scanner (input);
        app.updateMenuItem(scanner);

        String expected = "1/Test Cookie/20/03-10-2026/5/03-15-2026/Nuts, Gluten";
        String result = Files.readString(file);
        assertEquals(expected, result, "Error: The menu item's allergens were not updated.");
    }
}