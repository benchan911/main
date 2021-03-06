package planner.logic.modules.legacy.task;

import java.util.Objects;

public class Task {

    /**
     * task is the string value of the task name.
     * done is the active status of the task.
     * dateTime is the date and time information if the task requires.
     */
    private String task;
    private Boolean done;

    /**
     * Constructor to Task class.
     * @param task User's input of the desired task.
     */
    public Task(String task) {
        this.task = task.trim();
        this.done = false;
    }

    public void setTaskDone() {
        done = true;
    }

    public String getName() {
        return task;
    }

    /**
     * Function to be used to when writing to the file.
     * @return Returns a string containing task name and done status.
     */
    public String writingFile() {
        return task
                + "|"
                + (isDone() ? "1" : "0");
    }

    @Override
    public String toString() {
        String completed = (done) ? "[taken] " : "[not taken] "; // \u2713, \u2717
        return completed + task;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Task)) {
            return false;
        }
        Task otherTask = (Task) other;
        return otherTask.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, done);
    }

    public boolean isDone() {
        return this.done;
    }

    public String type() {
        return "task";
    }

    public String getTaskToLowerCase() {
        return this.task.toLowerCase();
    }
}
