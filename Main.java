package dosageCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngineManager;

public class Main {

    private static Point initialClick;
    private static List<String> dosageHistory = new ArrayList<>();
    private static final String HISTORY_FILE = "dosageHistory.txt";
    private static Map<String, String> customMedicineLogic = new HashMap<>();

    public static void main(String[] args) {
        loadHistory();
        // Main frame
        JFrame frame = new JFrame("Medicine Dosage Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setUndecorated(true); // Remove window borders for a modern look
        frame.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        frame.setResizable(true); // Make the frame resizable

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Create a panel for the title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(0, 123, 255)); // Hospital-themed blue background
        titleBar.setPreferredSize(new Dimension(frame.getWidth(), 30));

        // Create minimize and exit buttons
        JButton minimizeButton = new JButton("\u2013"); // Unicode for minus sign
        JButton exitButton = new JButton("x"); // Unicode for multiplication sign (close button)

        // Style the buttons
        minimizeButton.setBackground(new Color(0, 123, 255));
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        minimizeButton.setFocusPainted(false);
        minimizeButton.setFont(new Font("Arial", Font.BOLD, 14));

        exitButton.setBackground(new Color(0, 123, 255));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        exitButton.setFocusPainted(false);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Add action listeners for the buttons
        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setState(Frame.ICONIFIED);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Add buttons to the title bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(minimizeButton);
        buttonPanel.add(exitButton);

        titleBar.add(buttonPanel, BorderLayout.EAST);

        // Add application title to the title bar
        JLabel titleLabel = new JLabel("Medicine Dosage Calculator");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);

        // Make the frame draggable
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                frame.getComponentAt(initialClick);
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                // get location of Window
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                frame.setLocation(X, Y);
            }
        });

        // Main panel with modern theme
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw modern background
                g.setColor(new Color(255, 255, 255, 200)); // Light background for hospital theme
                g.fillRoundRect(0, 0, getWidth(), getHeight() - 30, 0, 0); // Adjusted top border to align with title bar
                g.setColor(new Color(0, 123, 255));
                g.setFont(new Font("Arial", Font.BOLD, 24));
                // Adjusted position and size of the title
                FontMetrics fm = g.getFontMetrics();
                int titleWidth = fm.stringWidth("Medicine Dosage Calculator");
                g.drawString("Medicine Dosage Calculator", (getWidth() - titleWidth) / 2, 30);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20)); // Adjusted top border

        // Frame container for medicine name
        JPanel medicinePanel = new JPanel(new GridLayout(1, 4, 10, 10)); // Adjusted layout
        medicinePanel.setOpaque(false);
        JLabel medicineLabel = new JLabel("Select medicine:");
        medicineLabel.setForeground(new Color(0, 123, 255));
        DefaultComboBoxModel<String> medicineModel = new DefaultComboBoxModel<>(new String[]{"Paracetamol", "Ibuprofen", "Amoxicillin", "Aspirin", "Cetirizine", "Loratadine", "Metformin", "Omeprazole", "Simvastatin"});
        JComboBox<String> medicineDropdown = new JComboBox<>(medicineModel);

        JButton addMedicineButton = new JButton("Add Medicine");
        addMedicineButton.setBackground(new Color(0, 123, 255));
        addMedicineButton.setForeground(Color.WHITE);

        JButton removeMedicineButton = new JButton("Remove Medicine");
        removeMedicineButton.setBackground(new Color(0, 123, 255));
        removeMedicineButton.setForeground(Color.WHITE);

        // Add values to the medicine container
        medicinePanel.add(medicineLabel);
        medicinePanel.add(medicineDropdown);
        medicinePanel.add(addMedicineButton);
        medicinePanel.add(removeMedicineButton);

        // User input based on patient's info
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        inputPanel.setOpaque(false);
        
        JLabel weightLabel = new JLabel("Enter weight (kg):");
        weightLabel.setForeground(new Color(0, 123, 255));
        JTextField weightField = new JTextField();
        weightField.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
        weightField.setBackground(new Color(255, 255, 255, 150));
        weightField.setForeground(new Color(0, 123, 255));

        JLabel heightLabel = new JLabel("Enter height (cm):");
        heightLabel.setForeground(new Color(0, 123, 255));
        JTextField heightField = new JTextField();
        heightField.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
        heightField.setBackground(new Color(255, 255, 255, 150));
        heightField.setForeground(new Color(0, 123, 255));

        JLabel ageLabel = new JLabel("Enter age (years):");
        ageLabel.setForeground(new Color(0, 123, 255));
        JTextField ageField = new JTextField();
        ageField.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
        ageField.setBackground(new Color(255, 255, 255, 150));
        ageField.setForeground(new Color(0, 123, 255));

        JLabel intervalLabel = new JLabel("Enter medication interval (hours):");
        intervalLabel.setForeground(new Color(0, 123, 255));
        JTextField intervalField = new JTextField();
        intervalField.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
        intervalField.setBackground(new Color(255, 255, 255, 150));
        intervalField.setForeground(new Color(0, 123, 255));

        JLabel patientTypeLabel = new JLabel("Select patient type:");
        patientTypeLabel.setForeground(new Color(0, 123, 255));
        String[] patientTypes = {"Pediatric", "Geriatric"};
        JComboBox<String> patientTypeDropdown = new JComboBox<>(patientTypes);

        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setForeground(new Color(0, 123, 255));
        JLabel resultOutput = new JLabel("");
        resultOutput.setForeground(new Color(0, 123, 255));
        resultLabel.setVisible(false);
        resultOutput.setVisible(false);

        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(new Color(0, 123, 255));
        calculateButton.setForeground(Color.WHITE);

        JButton historyButton = new JButton("Show History");
        historyButton.setBackground(new Color(0, 123, 255));
        historyButton.setForeground(Color.WHITE);

        // Add user input values
        inputPanel.add(weightLabel);
        inputPanel.add(weightField);

        inputPanel.add(heightLabel);
        inputPanel.add(heightField);

        inputPanel.add(ageLabel);
        inputPanel.add(ageField);

        inputPanel.add(intervalLabel);
        inputPanel.add(intervalField);

        inputPanel.add(patientTypeLabel);
        inputPanel.add(patientTypeDropdown);

        inputPanel.add(resultLabel);
        inputPanel.add(resultOutput);

        inputPanel.add(calculateButton);
        inputPanel.add(historyButton); // Moved historyButton to be under calculateButton

        mainPanel.add(medicinePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Add medicine functionality
        addMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newMedicine = JOptionPane.showInputDialog(frame, "Enter new medicine name:");
                if (newMedicine != null && !newMedicine.trim().isEmpty()) {
                    String customLogic = JOptionPane.showInputDialog(frame, "Enter custom logic for " + newMedicine + " (e.g., weight * 0.1):");
                    if (customLogic != null && !customLogic.trim().isEmpty()) {
                        medicineModel.addElement(newMedicine.trim());
                        // Store custom logic for the new medicine
                        customMedicineLogic.put(newMedicine.trim(), customLogic.trim());
                    }
                }
            }
        });

        // Remove medicine functionality
        removeMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedMedicine = (String) medicineDropdown.getSelectedItem();
                if (selectedMedicine != null) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove " + selectedMedicine + "?", "Remove Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        medicineModel.removeElement(selectedMedicine);
                        customMedicineLogic.remove(selectedMedicine); // Remove custom logic if exists
                    }
                }
            }
        });

        // Calculation Logic
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String medicineName = (String) medicineDropdown.getSelectedItem();

                    // Parsing inputs
                    double weight = Double.parseDouble(weightField.getText());
                    double height = Double.parseDouble(heightField.getText());
                    int age = Integer.parseInt(ageField.getText());
                    double interval = Double.parseDouble(intervalField.getText());
                    String patientType = (String) patientTypeDropdown.getSelectedItem();

                    // Calculates dosage based on custom logic or default logic
                    double totalDose = 0;
                    if (customMedicineLogic.containsKey(medicineName)) {
                        String logic = customMedicineLogic.get(medicineName);
                        totalDose = evaluateCustomLogic(logic, weight, height, age); // Evaluate custom logic
                    } else {
                        if ("Pediatric".equals(patientType)) {
                            totalDose = (weight + height + age) * 0.05; // Example formula for pediatric
                        } else if ("Geriatric".equals(patientType)) {
                            totalDose = (weight + height + age) * 0.08; // Example formula for geriatric
                        }
                    }
                    double dosePerInterval = totalDose * (interval / 24);

                    // Store the result in history with date
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    String result = String.format("Date: %s\nMedicine: %s\nDose: %.2f mg per interval\n" +
                                                  "Age: %d\nWeight: %.2f kg Height: %.2f cm\nType: %s\n______________________\n", 
                                                  date, medicineName, dosePerInterval, age, weight, height, patientType);
                    dosageHistory.add(result);
                    saveHistory();

                    // Prints the result in a formatted way
                    String formattedResult = String.format("<html><body style=''>Medicine: %s<br/>Dose: %.2f mg per interval<br/>" +
                                                           "Age: %d<br/>Weight: %.2f kg<br/>Height: %.2f cm<br/>Type: %s</body></html>", 
                                                           medicineName, dosePerInterval, age, weight, height, patientType);
                    resultOutput.setText(formattedResult);
                    resultLabel.setVisible(true);
                    resultOutput.setVisible(true);
                } catch (NumberFormatException ex) {
                    // Handles invalid input
                    resultOutput.setText("Invalid input. Please enter a number.");
                    resultLabel.setVisible(true);
                    resultOutput.setVisible(true);
                }
            }
        });

        // Show history logic
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog historyDialog = new JDialog(frame, "Dosage History", true);
                historyDialog.setSize(600, 400);
                historyDialog.setLocationRelativeTo(frame);
                historyDialog.setLayout(new BorderLayout());

                JPanel historyPanel = new JPanel(new BorderLayout());
                historyPanel.setBackground(new Color(255, 255, 255));
                historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JTextArea historyTextArea = new JTextArea();
                historyTextArea.setEditable(false);
                historyTextArea.setBackground(new Color(245, 245, 245));
                historyTextArea.setForeground(new Color(0, 123, 255));
                historyTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
                for (String entry : dosageHistory) {
                    historyTextArea.append(entry + "\n");
                }

                JScrollPane scrollPane = new JScrollPane(historyTextArea);
                scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));

                JButton closeButton = new JButton("Close");
                closeButton.setBackground(new Color(0, 123, 255));
                closeButton.setForeground(Color.WHITE);
                closeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        historyDialog.dispose();
                    }
                });

                JButton clearButton = new JButton("Clear History");
                clearButton.setBackground(new Color(0, 123, 255));
                clearButton.setForeground(Color.WHITE);
                clearButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dosageHistory.clear();
                        saveHistory();
                        historyTextArea.setText("");
                    }
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(new Color(255, 255, 255));
                buttonPanel.add(clearButton);
                buttonPanel.add(closeButton);

                historyPanel.add(scrollPane, BorderLayout.CENTER);
                historyPanel.add(buttonPanel, BorderLayout.SOUTH);

                historyDialog.add(historyPanel);
                historyDialog.setVisible(true);
            }
        });

        frame.add(titleBar, BorderLayout.NORTH); // Add title bar to the frame
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Method to evaluate custom logic
    private static double evaluateCustomLogic(String logic, double weight, double height, int age) {
        // Simple evaluation logic (for demonstration purposes)
        logic = logic.replace("weight", String.valueOf(weight));
        logic = logic.replace("height", String.valueOf(height));
        logic = logic.replace("age", String.valueOf(age));
        try {
            return (double) new ScriptEngineManager().getEngineByName("JavaScript").eval(logic);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void loadHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dosageHistory.add(line);
            }
        } catch (IOException e) {
            // Handle exception or file not found
        }
    }

    private static void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String entry : dosageHistory) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            // Handle exception
        }
    }
}