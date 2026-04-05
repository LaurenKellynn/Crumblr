import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.FlowView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;

/**
 * Lars Kellynn
 * CEN 3024C - Software Development 1
 * March 12, 2026
 * GUI.java
 * This class holds all the GUI elements.
 */
public class GUI extends JFrame {

    private CrumblrApp app;
    private JTextArea displayArea;
    private ImageIcon icon;

    public GUI () {

        setTitle("Crumblr");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        //Custom Icon
        try (var is = getClass().getResourceAsStream("/CupcakeIcon.png")) {
            if (is != null) {
                Image image = ImageIO.read(is);
                this.icon = new ImageIcon(image);
                setIconImage(image);
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar.getTaskbar().setIconImage(image);
                }
            } else {
                System.out.println("Icon not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JTextField urlField = new JTextField("jdbc:mysql://localhost:3306/Crumblr");
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] message = {
                "Database URL:", urlField,
                "Username:", userField,
                "Password:", passField
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Connect to Database",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                app = new CrumblrApp(
                        urlField.getText(),
                        userField.getText(),
                        new String(passField.getPassword())
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage());
                System.exit(0);
            }
        } else {
            System.exit(0);
        }

        //Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeMessage = new JLabel("Welcome to Crumblr! Your Bakery Management System.");
        headerPanel.add(welcomeMessage);
        headerPanel.setBackground(Color.pink);
        add(headerPanel, BorderLayout.NORTH);

        //Menu Item Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        //Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton veiwMenuItemsButton = new JButton("View All Menu Items");
        JButton addMenuItemsButton = new JButton("Add a New Menu Item");
        JButton updateMenuItemsButton = new JButton("Edit a Menu Item");
        JButton deleteMenuItemsButton = new JButton("Delete a Menu Item");

        buttonPanel.add(Box.createVerticalStrut(10));

        buttonPanel.add(veiwMenuItemsButton);
        buttonPanel.add(addMenuItemsButton);
        buttonPanel.add(updateMenuItemsButton);
        buttonPanel.add(deleteMenuItemsButton);
        buttonPanel.setBackground(Color.PINK);
        add(buttonPanel, BorderLayout.WEST);

        Dimension buttonSize = new Dimension(200, 50);
        JButton[] buttons = {
                veiwMenuItemsButton,
                addMenuItemsButton,
                updateMenuItemsButton,
                deleteMenuItemsButton
        };
        for (JButton btn : buttons) {
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        //Button Actions
        veiwMenuItemsButton.addActionListener(e -> loadFile());
        addMenuItemsButton.addActionListener(e -> {
            JTextField itemDescription = new JTextField();
            JTextField itemQuantity = new JTextField();
            JTextField dateMade = new JTextField();
            JTextField shelfLife = new JTextField();
            JTextField allergens = new JTextField();

            Object[] addItemMessage = {
                    "Menu Item's Description:", itemDescription,
                    "Menu Item's Quantity:", itemQuantity,
                    "Date that the Menu Item was made:", dateMade,
                    "Shelf life (in days) of the Menu Item:", shelfLife,
                    "Known allergens:", allergens
            };
            int addItemOption = JOptionPane.showConfirmDialog(
                    this,
                    addItemMessage,
                    "Add New Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    icon);
            setIconImage(icon.getImage());
            if (addItemOption == JOptionPane.OK_OPTION){
                try {

                    String expirationDate = app.addMenuItem(itemDescription.getText(),
                            itemQuantity.getText(),
                            dateMade.getText(),
                            shelfLife.getText(),
                            allergens.getText()
                    );
                    loadFile();
                    JOptionPane.showMessageDialog(
                            this,
                            "The menu item has been added successfully! " +
                            "The expiration date was automatically calculated to be: " + expirationDate + ".",
                            "Crumblr",
                            JOptionPane.PLAIN_MESSAGE,
                            icon);
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });
        deleteMenuItemsButton.addActionListener(e -> {
            String id = (String) JOptionPane.showInputDialog(
                    this,
                    "Please enter the ID of the menu item that you would like to delete:",
                    "Crumblr",
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    null,
                    null);
            setIconImage(icon.getImage());

            if (id != null) {
                try {
                    app.deleteMenuItem(id);
                    loadFile();

                    JOptionPane.showMessageDialog(
                            this,
                            "The menu item has been deleted successfully!",
                            "Crumblr",
                            JOptionPane.PLAIN_MESSAGE,
                            icon
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });
        updateMenuItemsButton.addActionListener(e ->{
            String id = (String) JOptionPane.showInputDialog(
                    this,
                    "Please enter the ID of the menu item you would like to edit:",
                    "Crumblr",
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    null,
                    null);
            setIconImage(icon.getImage());

            if (id == null || !id.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID number.");
                return;
            }

            String[] fields = {"Description", "Quantity", "Date Made", "Shelf Life", "Allergens"};
            JComboBox<String> comboBox = new JComboBox<>(fields);

            int updateOption = JOptionPane.showConfirmDialog(
                    this,
                    comboBox,
                    "Select field to update",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    icon
            );
            if (updateOption != JOptionPane.OK_OPTION) return;

            String field = (String) comboBox.getSelectedItem();
            String fieldCode;

            switch (field) {
                case "Description" -> fieldCode = "1";
                case "Quantity" -> fieldCode = "2";
                case "Date Made" -> fieldCode = "3";
                case "Shelf Life" -> fieldCode = "4";
                case "Allergens" -> fieldCode = "5";
                default -> {
                    JOptionPane.showMessageDialog(this, "Invalid selection.");
                    return;
                }
            }

            String newValue = (String) JOptionPane.showInputDialog(
                    this,
                    "Enter the new value for " + field + ":",
                    "Crumblr",
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    null,
                    null);
            if (newValue == null) return;

            newValue = newValue.trim();

            if (field.equals("Quantity") && !newValue.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid entry. Quantity must be between 0–999.");
                return;
            }

            if (field.equals("Date Made")) {
                try {
                    java.time.format.DateTimeFormatter formatter =
                            java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    java.time.LocalDate.parse(newValue, formatter);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid entry. Please use MM-DD-YYYY format.");
                    return;
                }
            }

            if (field.equals("Shelf Life") && !newValue.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid entry. Shelf life must be between 1–99 days.");
                return;
            }
            try {
               app.updateMenuItem(id, fieldCode, newValue);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        loadFile();
        setVisible(true);
    }
    /**
     * method: loadFile
     * parameters: none
     * return: none
     * purpose: Selects the data from the database to be viewed in the gui.
     */
    private void loadFile() {
        try {
            StringBuilder content = new StringBuilder();
            String sql = "SELECT * FROM menu";

            try (java.sql.Statement stmt = app.getConnection().createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    content.append(
                                    rs.getInt("id")).append(" / ")
                            .append(rs.getString("description")).append(" / ")
                            .append(rs.getInt("quantity")).append(" / ")
                            .append(rs.getDate("date_made")).append(" / ")
                            .append(rs.getInt("shelf_life")).append(" / ")
                            .append(rs.getDate("expiration_date")).append(" / ")
                            .append(rs.getString("allergens"))
                            .append("\n");
                }
            }
            displayArea.setText(content.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data.");
        }
    }
}