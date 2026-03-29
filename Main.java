import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Lars Kellynn
 * CEN 3024C - Software Development 1
 * February 28, 2026
 * Main.java
 * This is the main class that will call the new GUI instance.
 */
public class Main {

    /**
     * method: Main
     * parameters: none
     * return: none
     * purpose: calls the new GUI instance
     */
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() ->{
            new GUI();
        });

    }
}