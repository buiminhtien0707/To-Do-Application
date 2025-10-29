public class Task {
    private String name;
    private int priority;
    private boolean completed;
    
    public Task(String name, int priority) {
        this.name = name;
        setPriority(priority);
        this.completed = false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be between 1 and 5");
        }
        this.priority = priority;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public void markComplete() {
        this.completed = true;
    }
    
    @Override
    public String toString() {
        String status = completed ? "✓ COMPLETED" : "Pending";
        return String.format("Priority %d: %s [%s]", priority, name, status);
    }
}