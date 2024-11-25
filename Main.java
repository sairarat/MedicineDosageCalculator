package dosageCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

	 public static void main(String[] args) {
	        // Main frame
	        JFrame frame = new JFrame("Medicine Dosage Calculator");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(550, 500);

	        // Frame container for medicine name
	        JPanel medicinePanel = new JPanel(new GridLayout(1, 3, 10, 10));
	        JLabel medicineLabel = new JLabel("Select medicine:");
	        String[] medicines = {"Paracetamol", "Ibuprofen", "Amoxicillin", "Aspirin", "Other"};
	        JComboBox<String> medicineDropdown = new JComboBox<>(medicines);
	        JTextField customMedicineField = new JTextField();
	        customMedicineField.setEnabled(false); // hidden unless option "others" is selected

	        // Add values to the medicine container
	        medicinePanel.add(medicineLabel);
	        medicinePanel.add(medicineDropdown);
	        medicinePanel.add(customMedicineField);

	        // user input based on patient's info
	        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
	        JLabel weightLabel = new JLabel("Enter weight (kg):");
	        JTextField weightField = new JTextField();

	        JLabel heightLabel = new JLabel("Enter height (cm):");
	        JTextField heightField = new JTextField();

	        JLabel ageLabel = new JLabel("Enter age (years):");
	        JTextField ageField = new JTextField();

	        JLabel intervalLabel = new JLabel("Enter medication interval (hours):");
	        JTextField intervalField = new JTextField();

	        JLabel resultLabel = new JLabel("Dose per interval:");
	        JLabel resultOutput = new JLabel("");

	        JButton calculateButton = new JButton("Calculate");

	        // Add user input values
	        mainPanel.add(weightLabel);
	        mainPanel.add(weightField);

	        mainPanel.add(heightLabel);
	        mainPanel.add(heightField);

	        mainPanel.add(ageLabel);
	        mainPanel.add(ageField);

	        mainPanel.add(intervalLabel);
	        mainPanel.add(intervalField);

	        mainPanel.add(calculateButton);
	        mainPanel.add(resultLabel);

	        
	        mainPanel.add(new JLabel()); 
	        mainPanel.add(resultOutput);

	        
	        frame.setLayout(new BorderLayout());
	        frame.add(medicinePanel, BorderLayout.NORTH);
	        frame.add(mainPanel, BorderLayout.CENTER);

	        // Hide visible option for medicine name when option other is selected
	        medicineDropdown.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                String selectedMedicine = (String) medicineDropdown.getSelectedItem();
	                if ("Other".equals(selectedMedicine)) {
	                    customMedicineField.setEnabled(true);
	                    customMedicineField.setText(""); 
	                } else {
	                    customMedicineField.setEnabled(false);
	                    customMedicineField.setText(""); 
	                }
	            }
	        });

	        // Calculation Logic
	        calculateButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                try {
	                    String medicineName = (String) medicineDropdown.getSelectedItem();
	                    if ("Other".equals(medicineName)) {
	                        medicineName = customMedicineField.getText().trim();
	                        if (medicineName.isEmpty()) {
	                            resultOutput.setText("Please enter the medicine name.");
	                            return;
	                        }
	                    }

	                    // Parsing inputs
	                    double weight = Double.parseDouble(weightField.getText());
	                    double height = Double.parseDouble(heightField.getText());
	                    int age = Integer.parseInt(ageField.getText());
	                    double interval = Double.parseDouble(intervalField.getText());

	                    // Calculates dosage and along with its interval per interval
	                    double totalDose = (weight + height + age) * 0.1; // Example formula
	                    double dosePerInterval = totalDose * (interval / 24);

	                    // Prints the result 
	                    resultOutput.setText(String.format("%s: %.2f mg per interval", medicineName, dosePerInterval));
	                } catch (NumberFormatException ex) {
	                    // Handles invalid input
	                    resultOutput.setText("Invalid input. Please enter numeric values.");
	                }
	            }
	            
	            //Goodluck sa inyo Hanna Girl <3
	        });
	        frame.setVisible(true);
	    }
	}