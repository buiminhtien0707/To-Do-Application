import java.util.ArrayList;
import java.util.List;

public class TODOList {
    private List<Task> tasks;

    public TODOList() {
        tasks = new ArrayList<>();
    }

    // Add a new task with validation
    public boolean addTask(String taskName, int priority) {
        if (taskName == null || taskName.trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }

        // Check for duplicate priority
        if (isPriorityTaken(priority)) {
            throw new IllegalArgumentException("Priority " + priority + " is already taken by another task");
        }

        // Priority validation is handled by Task.setPriority()
        Task newTask = new Task(taskName.trim(), priority);
        tasks.add(newTask);
        return true;
    }

    // Check if priority is already taken by an incomplete task
    private boolean isPriorityTaken(int priority) {
        for (Task task : tasks) {
            if (task.getPriority() == priority && !task.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    // Get available priorities (1-5 excluding taken ones)
    public List<Integer> getAvailablePriorities() {
        List<Integer> available = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            if (!isPriorityTaken(i)) {
                available.add(i);
            }
        }
        return available;
    }

    // Get incomplete tasks only
    public List<Task> getIncompleteTasks() {
        List<Task> incomplete = new ArrayList<>();
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                incomplete.add(task);
            }
        }
        return incomplete;
    }

    // Get all tasks (including completed)
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    // Mark task as complete by task object
    public boolean markTaskComplete(Task task) {
        if (task != null && tasks.contains(task)) {
            task.markComplete();
            return true;
        }
        return false;
    }

    // Get incomplete tasks as formatted string
    public String getIncompleteTasksAsString() {
        List<Task> incomplete = getIncompleteTasks();
        if (incomplete.isEmpty()) {
            return "No tasks left! All tasks are completed.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Tasks Left (").append(incomplete.size()).append("):\n\n");
        for (int i = 0; i < incomplete.size(); i++) {
            sb.append((i + 1) + ". " + incomplete.get(i).toString() + "\n");
        }
        return sb.toString();
    }

    // Get all tasks as formatted string
    public String getAllTasksAsString() {
        if (tasks.isEmpty()) {
            return "No tasks in the list.";
        }

        StringBuilder sb = new StringBuilder();
        int completedCount = 0;
        for (Task task : tasks) {
            if (task.isCompleted())
                completedCount++;
        }

        sb.append("All Tasks (").append(tasks.size()).append(" total, ")
                .append(completedCount).append(" completed, ")
                .append(tasks.size() - completedCount).append(" left):\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1) + ". " + tasks.get(i).toString() + "\n");
        }
        return sb.toString();
    }

    // Get task count
    public int getTaskCount() {
        return tasks.size();
    }

    // Get incomplete task count
    public int getIncompleteTaskCount() {
        return getIncompleteTasks().size();
    }

    // Remove task by index
    public boolean removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            return true;
        }
        return false;
    }

    // Clear all tasks
    public void clearAllTasks() {
        tasks.clear();
    }
}