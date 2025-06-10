import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UniEatsCafeSystem {
    private JFrame frame;
    private JPanel mainPanel, loginPanel, menuPanel, itemDetailPanel, headerPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Map<String, FoodItem> foodItems;
    private FoodItem currentlySelected;
    private JLabel userGreetingLabel;

    private Connection connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/form", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Database connection failed: " + e.getMessage());
            return null;
        }
    }

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(255, 165, 0);
    private final Color SECONDARY_COLOR = new Color(220, 220, 220);
    private final Color ACCENT_COLOR = new Color(255, 215, 0);
    private final Color TEXT_COLOR = new Color(50, 50, 50);
    private final Color BACKGROUND_COLOR = Color.WHITE;

    public UniEatsCafeSystem() {
        initializeFoodItems();
        createMainFrame();
        showLoginScreen();
    }

    private void initializeFoodItems() {
        foodItems = new HashMap<>();
        foodItems.put("burger", new FoodItem("Classic Burger", "Juicy beef patty with lettuce, tomato, and our special sauce served with fries", 12.99, "burger.png"));
        foodItems.put("pizza", new FoodItem("Margherita Pizza", "Traditional pizza with San Marzano tomato sauce, fresh mozzarella, and basil", 14.99, "pizza.png"));
        foodItems.put("salad", new FoodItem("Caesar Salad", "Crisp romaine lettuce with homemade Caesar dressing, parmesan, and garlic croutons", 9.99, "salad.png"));
        foodItems.put("pasta", new FoodItem("Spaghetti Carbonara", "Al dente spaghetti with creamy egg sauce, pancetta, and pecorino cheese", 13.99, "pasta.png"));
        foodItems.put("dessert", new FoodItem("Chocolate Lava Cake", "Warm chocolate cake with a molten center, served with vanilla ice cream", 7.99, "dessert.png"));
    }

    private void createMainFrame() {
        frame = new JFrame("UniEats Café");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(1000, 700));

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2,
                (screenSize.height - frame.getHeight()) / 2);
    }

    private void showLoginScreen() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBackground(BACKGROUND_COLOR);

        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(BACKGROUND_COLOR);
        logoPanel.setBorder(new EmptyBorder(50, 0, 50, 0));

        JLabel logoLabel = new JLabel("UNI EATS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        logoLabel.setForeground(PRIMARY_COLOR);
        logoPanel.add(logoLabel);

        // Login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(20, 300, 50, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel loginTitle = new JLabel("Welcome Back!", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(loginTitle, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Sign In");
        styleButton(loginButton, PRIMARY_COLOR, Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        JLabel signupPrompt = new JLabel("Don't have an account? Sign up", SwingConstants.CENTER);
        signupPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signupPrompt.setForeground(PRIMARY_COLOR);
        gbc.gridy = 4;
        formPanel.add(signupPrompt, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (!username.isEmpty() && !password.isEmpty()) {
                showMainMenu(username);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Please enter both username and password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        loginPanel.add(logoPanel, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);

        frame.setContentPane(loginPanel);
        frame.setVisible(true);
    }

    private void showMainMenu(String username) {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        createHeader(username);
        createMenuContent();
        createItemDetailPanel();

        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void createHeader(String username) {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        // Logo
        JLabel logoLabel = new JLabel("UNI EATS");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);

        // User greeting
        userGreetingLabel = new JLabel("Welcome, " + username);
        userGreetingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userGreetingLabel.setForeground(Color.WHITE);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        navPanel.setOpaque(false);

        JButton menuButton = new JButton("Menu");
        styleButton(menuButton, Color.WHITE, PRIMARY_COLOR);

        JButton ordersButton = new JButton("My Orders");
        styleButton(ordersButton, Color.WHITE, PRIMARY_COLOR);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, Color.WHITE, PRIMARY_COLOR);

        logoutButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            showLoginScreen();
        });

        navPanel.add(menuButton);
        navPanel.add(ordersButton);
        navPanel.add(logoutButton);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(userGreetingLabel, BorderLayout.CENTER);
        headerPanel.add(navPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMenuContent() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Menu title
        JLabel menuTitle = new JLabel("Our Menu", SwingConstants.CENTER);
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        menuTitle.setForeground(TEXT_COLOR);
        menuTitle.setBorder(new EmptyBorder(30, 0, 20, 0));
        contentPanel.add(menuTitle, BorderLayout.NORTH);

        // Food items grid
        JPanel itemsGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        itemsGrid.setBackground(BACKGROUND_COLOR);
        itemsGrid.setBorder(new EmptyBorder(0, 30, 30, 30));

        for (String key : foodItems.keySet()) {
            FoodItem item = foodItems.get(key);
            JPanel itemCard = createItemCard(item);
            itemsGrid.add(itemCard);
        }

        // Wrap grid in scroll pane
        JScrollPane scrollPane = new JScrollPane(itemsGrid);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createItemCard(FoodItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(175, 175));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Image label
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(175, 125));

        // Try to load image
        try {
            InputStream imgStream = getClass().getResourceAsStream("/" + item.imagePath);
            if (imgStream != null) {
                ImageIcon originalIcon = new ImageIcon(ImageIO.read(imgStream));
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        175, 125, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                throw new IOException("Image not found: " + item.imagePath);
            }
        } catch (Exception e) {
            // Fallback if image fails to load
            imageLabel.setOpaque(true);
            imageLabel.setBackground(SECONDARY_COLOR);
            imageLabel.setText(item.name);
            imageLabel.setForeground(TEXT_COLOR);
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        // Item info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel priceLabel = new JLabel(String.format("$%.2f", item.price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);

        // Add click listener
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showItemDetails(item);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });

        return card;
    }

    private void createItemDetailPanel() {
        itemDetailPanel = new JPanel(new BorderLayout(10, 20));
        itemDetailPanel.setPreferredSize(new Dimension(350, 0));
        itemDetailPanel.setBorder(new EmptyBorder(30, 20, 30, 30));
        itemDetailPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(itemDetailPanel, BorderLayout.EAST);
    }

    private void showItemDetails(FoodItem item) {
        currentlySelected = item;
        itemDetailPanel.removeAll();
        itemDetailPanel.setLayout(new BorderLayout(10, 20));

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(245, 245, 245));

        // Item name field (editable)
        JTextField nameField = new JTextField(item.name);
        nameField.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameField.setBorder(BorderFactory.createTitledBorder("Item Name"));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height));

        // Item description area (editable)
        JTextArea descArea = new JTextArea(item.description, 4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(BorderFactory.createTitledBorder("Description"));
        descScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, descScrollPane.getPreferredSize().height));

        // Price field (editable)
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(item.price, 0.01, 1000.0, 0.5));
        JSpinner.NumberEditor priceEditor = new JSpinner.NumberEditor(priceSpinner, "$#,##0.00");
        priceSpinner.setEditor(priceEditor);
        priceSpinner.setBorder(BorderFactory.createTitledBorder("Price"));
        priceSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, priceSpinner.getPreferredSize().height));

        // Image path field (editable)
        JTextField imagePathField = new JTextField(item.imagePath);
        imagePathField.setBorder(BorderFactory.createTitledBorder("Image Filename"));
        imagePathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, imagePathField.getPreferredSize().height));

        // Add to Menu button
        JButton addButton = new JButton("Add to Menu");
        styleButton(addButton, PRIMARY_COLOR, Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newDesc = descArea.getText().trim();
            double newPrice = (Double) priceSpinner.getValue();
            String newImagePath = imagePathField.getText().trim();

            if (!newName.isEmpty() && !newDesc.isEmpty() && !newImagePath.isEmpty()) {
                // Update existing item or add new one
                String key = newName.toLowerCase().replaceAll("\\s+", "_");
                foodItems.put(key, new FoodItem(newName, newDesc, newPrice, newImagePath));

                JOptionPane.showMessageDialog(frame,
                        "Menu item updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refresh the menu
                showMainMenu(userGreetingLabel.getText().replace("Welcome, ", ""));
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Please fill in all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        addButton = new JButton("Add to Menu");
        styleButton(addButton, PRIMARY_COLOR, Color.WHITE);

        JButton removeButton = new JButton("Remove Item");
        styleButton(removeButton, Color.RED.darker(), Color.WHITE);

        addButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newDesc = descArea.getText().trim();
            double newPrice = (Double) priceSpinner.getValue();
            String newImagePath = imagePathField.getText().trim();

            if (!newName.isEmpty() && !newDesc.isEmpty() && !newImagePath.isEmpty()) {
                String key = newName.toLowerCase().replaceAll("\\s+", "_");
                foodItems.put(key, new FoodItem(newName, newDesc, newPrice, newImagePath));

                try (Connection con = connectToDatabase()) {
                    if (con != null) {
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO menu_items (name, description, price, image_path) VALUES (?, ?, ?, ?)"
                        );
                        ps.setString(1, newName);
                        ps.setString(2, newDesc);
                        ps.setDouble(3, newPrice);
                        ps.setString(4, newImagePath);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Database insert failed: " + ex.getMessage());
                }

                JOptionPane.showMessageDialog(frame, "Menu item added successfully!");
                showMainMenu(userGreetingLabel.getText().replace("Welcome, ", ""));
            }
        });

        removeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove this item?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String key = item.name.toLowerCase().replaceAll("\\s+", "_");
                foodItems.remove(key);

                try (Connection con = connectToDatabase()) {
                    if (con != null) {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM menu_items WHERE name = ?");
                        ps.setString(1, item.name);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Database deletion failed: " + ex.getMessage());
                }

                JOptionPane.showMessageDialog(frame, "Menu item removed successfully!");
                showMainMenu(userGreetingLabel.getText().replace("Welcome, ", ""));
            }
        });

        formPanel.add(addButton);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(removeButton);

        // Add components to form
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(descScrollPane);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(priceSpinner);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(imagePathField);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(addButton);

        itemDetailPanel.add(formPanel, BorderLayout.CENTER);
        itemDetailPanel.revalidate();
        itemDetailPanel.repaint();
    }

    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UniEatsCafeSystem());
    }

    private class FoodItem {
        String name, description, imagePath;
        double price;

        FoodItem(String name, String description, double price, String imagePath) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.imagePath = imagePath;
        }
    }
}
