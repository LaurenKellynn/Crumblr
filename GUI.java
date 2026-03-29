import javax.swing.*;
import javax.swing.text.FlowView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

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
        URL iconURL = getClass().getResource("/CupcakeIcon.png");
        if (iconURL != null){
            icon = new ImageIcon(iconURL);
            Image image = icon.getImage();
            setIconImage(image);
            if (Taskbar.isTaskbarSupported()) {
                Taskbar.getTaskbar().setIconImage(image);
            }
        } else {
            System.out.println("Icon not found");
        }

        //For the user to choose the file path
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Please select the file that holds the current menu");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                //Validate the file is a .txt file
                CrumblrApp.validateTxtFile(file.toPath());
                app = new CrumblrApp(file.toPath());
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage());
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

            Object[] message = {
                    "Menu Item's Description:", itemDescription,
                    "Menu Item's Quantity:", itemQuantity,
                    "Date that the Menu Item was made:", dateMade,
                    "Shelf life (in days) of the Menu Item:", shelfLife,
                    "Known allergens:", allergens
            };
            int option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Add New Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    icon);
            setIconImage(icon.getImage());
            if (option == JOptionPane.OK_OPTION){
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

            String field = (String) JOptionPane.showInputDialog(
                    this,
                    "Select field to update:\n" +
                            "1: The menu item's description.\n" +
                            "2: The menu item's quantity\n" +
                            "3. The date the menu item was made\n" +
                            "4. The shelf life of the menu item.\n" +
                            "5. The list of allergens for the menu item",
                    "Crumblr",
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    null,
                    null
            );
            if (field == null) return;

            if (!field.matches("[1-5]")) {
                JOptionPane.showMessageDialog(this, "Please choose options 1-5.");
                return;
            }

            String newValue = (String) JOptionPane.showInputDialog(
                    this,
                    "Enter the new value for this field:",
                    "Crumblr",
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    null,
                    null);
            if (newValue == null) return;

            newValue = newValue.trim();

            if (field.equals("2") && !newValue.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid entry. Quantity must be between 0–999.");
                return;
            }

            if (field.equals("3")) {
                try {
                    java.time.format.DateTimeFormatter formatter =
                            java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy");
                    java.time.LocalDate.parse(newValue, formatter);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid entry. Please use MM-DD-YYYY format.");
                    return;
                }
            }

            if (field.equals("4") && !newValue.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid entry. Shelf life must be between 1–99 days.");
                return;
            }

            try {
               app.updateMenuItem(id, field, newValue);
            } catch (IOException ex) {
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
     * purpose: Loads the file into a string for it to be displayed by the GUI
     */
    private void loadFile() {
        try {
            String content = Files.readString(app.menuItemFile());
            displayArea.setText(content);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file.");
        }
    }
}