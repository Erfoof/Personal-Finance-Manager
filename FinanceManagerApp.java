import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class Expense {
    String category;
    double amount;
    String date;
    String notes;

    Expense(String category, double amount, String date, String notes) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }
}

class User {
    String username;
    String password;
    ArrayList<Expense> expenses = new ArrayList<>();
    java.util.Map<String, Double> budget = new java.util.HashMap<>();

    User(String username, String password) {
        this.username = username;
        this.password = password;
        initializeBudget();
    }

    private void initializeBudget() {
        // Initialize default budget values
        budget.put("Groceries", 0.0);
        budget.put("Utilities", 0.0);
        budget.put("Entertainment", 0.0);
        budget.put("Transportation", 0.0);
    }
}

public class FinanceManagerApp {
    private static ArrayList<User> users = new ArrayList<>();
    private static User currentUser;

    public static void main(String[] args) {
        // Create some sample users
        users.add(new User("user1", "password1"));
        users.add(new User("user2", "password2"));

        // Launch the login window
        SwingUtilities.invokeLater(LoginWindow::new);
    }

    static class LoginWindow extends JFrame implements ActionListener {
        private JTextField usernameField;
        private JPasswordField passwordField;

        LoginWindow() {
            super("Personal Finance Manager - Login");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(300, 150);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 2));

            JLabel usernameLabel = new JLabel("Username:");
            usernameField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(this);

            panel.add(usernameLabel);
            panel.add(usernameField);
            panel.add(passwordLabel);
            panel.add(passwordField);
            panel.add(new JLabel());
            panel.add(loginButton);

            add(panel);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            boolean authenticated = authenticateUser(username, password);
            if (authenticated) {
                currentUser = getUserByUsername(username);
                dispose(); // Close login window
                SwingUtilities.invokeLater(MainWindow::new); // Launch main application window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    static boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    static User getUserByUsername(String username) {
        for (User user : users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    static class MainWindow extends JFrame implements ActionListener {
        private JTextField categoryField, amountField, dateField, notesField;
        private JTextArea expenseTextArea;
        private JList<String> budgetList;

        MainWindow() {
            super("Personal Finance Manager - Welcome " + currentUser.username);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 400);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel(new BorderLayout());

            // Expense entry panel
            JPanel expensePanel = new JPanel(new GridLayout(5, 2));
            expensePanel.setBorder(BorderFactory.createTitledBorder("Add Expense"));

            JLabel categoryLabel = new JLabel("Category:");
            categoryField = new JTextField();
            JLabel amountLabel = new JLabel("Amount:");
            amountField = new JTextField();
            JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
            dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            JLabel notesLabel = new JLabel("Notes:");
            notesField = new JTextField();
            JButton addButton = new JButton("Add Expense");
            addButton.addActionListener(this);

            expensePanel.add(categoryLabel);
            expensePanel.add(categoryField);
            expensePanel.add(amountLabel);
            expensePanel.add(amountField);
            expensePanel.add(dateLabel);
            expensePanel.add(dateField);
            expensePanel.add(notesLabel);
            expensePanel.add(notesField);
            expensePanel.add(new JLabel());
            expensePanel.add(addButton);

            // Budget panel
            JPanel budgetPanel = new JPanel(new GridLayout(1, 2));
            budgetPanel.setBorder(BorderFactory.createTitledBorder("Budget"));
            DefaultListModel<String> budgetListModel = new DefaultListModel<>();
            budgetList = new JList<>(budgetListModel);
            updateBudgetList();
            budgetPanel.add(new JScrollPane(budgetList));

            // Expense text area
            expenseTextArea = new JTextArea(10, 30);
            expenseTextArea.setEditable(false);
            JScrollPane expenseScrollPane = new JScrollPane(expenseTextArea);
            expenseScrollPane.setBorder(BorderFactory.createTitledBorder("Expenses"));

            mainPanel.add(expensePanel, BorderLayout.NORTH);
            mainPanel.add(budgetPanel, BorderLayout.CENTER);
            mainPanel.add(expenseScrollPane, BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Add Expense")) {
                String category = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String date = dateField.getText();
                String notes = notesField.getText();
                currentUser.expenses.add(new Expense(category, amount, date, notes));
                updateExpenseTextArea();
                updateBudgetList();
                JOptionPane.showMessageDialog(this, "Expense added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void updateExpenseTextArea() {
            StringBuilder sb = new StringBuilder();
            for (Expense expense : currentUser.expenses) {
                sb.append("Category: ").append(expense.category).append(", Amount: $").append(expense.amount)
                        .append(", Date: ").append(expense.date).append(", Notes: ").append(expense.notes).append("\n");
            }
            expenseTextArea.setText(sb.toString());
        }

        private void updateBudgetList() {
            DefaultListModel<String> budgetListModel = (DefaultListModel<String>) budgetList.getModel();
            budgetListModel.clear();
            for (String category : currentUser.budget.keySet()) {
                budgetListModel.addElement(category + ": $" + currentUser.budget.get(category));
            }
        }
    }
}
