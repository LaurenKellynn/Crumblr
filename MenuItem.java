import java.time.LocalDate;

/**
 * Lars Kellynn
 * CEN 3024C - Software Development 1
 * February 28, 2026
 * MenuItem.java
 * This class creates the menu item as an object to be used by the other classes.
 */
public class MenuItem {

    private String description;
    private int quantity;
    private LocalDate dateMade;
    private int shelfLife;
    private LocalDate expirationDate;
    private String allergens;

    public MenuItem(String description, int quantity, LocalDate dateMade, int shelfLife, LocalDate expirationDate, String allergens) {
        this.description = description;
        this.quantity = quantity;
        this.dateMade = dateMade;
        this.shelfLife = shelfLife;
        this.expirationDate = expirationDate;
        this.allergens = allergens;
    }

    public String getDescription(){
        return description;
    }

    public String toFileString(){
        return description + "/" + quantity + "/" + dateMade + "/" + shelfLife + "/" + expirationDate + "/" + allergens;
    }
}
