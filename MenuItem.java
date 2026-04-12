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

    /**
     * Constructs a MenuItem with all required attributes.
     *
     * @param description description of the item
     * @param quantity quantity available
     * @param dateMade date the item was made
     * @param shelfLife shelf life (in days)
     * @param expirationDate calculated expiration date
     * @param allergens known allergen information
     */
    public MenuItem(String description, int quantity, LocalDate dateMade, int shelfLife, LocalDate expirationDate, String allergens) {
        this.description = description;
        this.quantity = quantity;
        this.dateMade = dateMade;
        this.shelfLife = shelfLife;
        this.expirationDate = expirationDate;
        this.allergens = allergens;
    }

    /**
     * Returns the description of the menu item.
     *
     * @return item description
     */
    public String getDescription(){
        return description;
    }

    /**
     * Converts the menu item into a formatted string for file storage.
     * Fields are separated by "/"
     *
     * @return formatted string representation of the menu item
     */
    public String toFileString(){
        return description + "/" + quantity + "/" + dateMade + "/" + shelfLife + "/" + expirationDate + "/" + allergens;
    }
}
