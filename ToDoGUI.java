import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ToDoGUI extends JFrame implements ActionListener {
    // Backend instance
    private TODOList todoList;

    // Frontend components
    private JTextField taskField;
    private JComboBox<Integer> priorityComboBox;
    private JTextArea outputArea;
    private JButton addButton;
    private JButton viewTasksButton;
    private JButton viewAllButton;
    private JButton completeButton;
    private JLabel statusLabel;

    public ToDoGUI() {
        // Initialize backend
        todoList = new TODOList();

        // Initialize frontend
        initializeGUI();
    }

    private void initializeGUI() {
        setupMainWindow();
        createComponents();
        setupLayout();
        setupEventHandlers();
        updateStatus();
    }

    private void setupMainWindow() {
        setTitle("To-Do App");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        // Create input components
        taskField = new JTextField(15);

        // Create priority combo box with values 1-5
        priorityComboBox = new JComboBox<>();
        updatePriorityComboBox();

        // Create buttons
        addButton = new JButton("Add Task");
        viewTasksButton = new JButton("View Tasks Left");
        viewAllButton = new JButton("View All Tasks");
        completeButton = new JButton("Mark Complete");

        // Create output area
        outputArea = new JTextArea(15, 40);
        outputArea.setEditable(false);

        // Create status label
        statusLabel = new JLabel("Ready to add tasks");
    }

    private void updatePriorityComboBox() {
        priorityComboBox.removeAllItems();
        List<Integer> availablePriorities = todoList.getAvailablePriorities();

        if (availablePriorities.isEmpty()) {
            priorityComboBox.addItem(0); // Show 0 if no priorities available
            priorityComboBox.setEnabled(false);
        } else {
            for (Integer priority : availablePriorities) {
                priorityComboBox.addItem(priority);
            }
            priorityComboBox.setEnabled(true);
        }
    }

    private void setupLayout() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Task Name:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(priorityComboBox);
        inputPanel.add(addButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewTasksButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(completeButton);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);

        // Combine input and button panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(statusPanel, BorderLayout.SOUTH);

        // Output area with scrolling
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Assemble main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Set content pane
        setContentPane(mainPanel);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(this);
        viewTasksButton.addActionListener(this);
        viewAllButton.addActionListener(this);
        completeButton.addActionListener(this);

        // Add Enter key support for task field
        taskField.addActionListener(e -> addTask());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Add Task":
                addTask();
                break;
            case "View Tasks Left":
                viewTasksLeft();
                break;
            case "View All Tasks":
                viewAllTasks();
                break;
            case "Mark Complete":
                markTaskComplete();
                break;
        }
    }

    private void addTask() {
        // Get user input from frontend
        String taskName = taskField.getText().trim();

        if (taskName.isEmpty()) {
            showError("Task name cannot be empty!");
            return;
        }

        if (priorityComboBox.getSelectedItem() == null ||
                priorityComboBox.getSelectedIndex() == -1) {
            showError("No available priorities! Complete some tasks first.");
            return;
        }

        int priority = (Integer) priorityComboBox.getSelectedItem();

        try {
            // Call backend (validation for priority range and duplicates happens in
            // Task.setPriority())
            todoList.addTask(taskName, priority);

            // Update frontend on success
            taskField.setText("");
            updatePriorityComboBox();
            updateOutput("Task added successfully!\n\n" + todoList.getIncompleteTasksAsString());
            updateStatus();
            taskField.requestFocus();

        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void viewTasksLeft() {
        // Show only incomplete tasks
        updateOutput(todoList.getIncompleteTasksAsString());
    }

    private void viewAllTasks() {
        // Show all tasks (including completed)
        updateOutput(todoList.getAllTasksAsString());
    }

    private void markTaskComplete() {
        List<Task> incompleteTasks = todoList.getIncompleteTasks();

        if (incompleteTasks.isEmpty()) {
            showError("No tasks available to mark as complete!");
            return;
        }

        // Create array of task descriptions for the dialog
        String[] taskOptions = new String[incompleteTasks.size()];
        for (int i = 0; i < incompleteTasks.size(); i++) {
            taskOptions[i] = incompleteTasks.get(i).toString();
        }

        // Show dialog for user to select task
        String selectedTask = (String) JOptionPane.showInputDialog(
                this,
                "Select a task to mark as complete:",
                "Mark Task Complete",
                JOptionPane.QUESTION_MESSAGE,
                null,
                taskOptions,
                taskOptions[0]);

        if (selectedTask != null) {
            // Find the selected task
            for (int i = 0; i < incompleteTasks.size(); i++) {
                if (incompleteTasks.get(i).toString().equals(selectedTask)) {
                    Task taskToComplete = incompleteTasks.get(i);
                    todoList.markTaskComplete(taskToComplete);

                    showInfo("Task marked as complete: " + taskToComplete.getName());
                    updatePriorityComboBox();
                    updateOutput(todoList.getIncompleteTasksAsString());
                    updateStatus();
                    break;
                }
            }
        }
    }

    private void updateStatus() {
        int total = todoList.getTaskCount();
        int left = todoList.getIncompleteTaskCount();
        statusLabel.setText("Tasks: " + total + " total, " + left + " left, " + (total - left) + " completed");
    }

    // Frontend helper methods
    private void updateOutput(String text) {
        outputArea.setText(text);
        outputArea.setCaretPosition(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ToDoGUI().setVisible(true);
        });
    }
}