package planner.logic.exceptions.legacy;

public class ModEmptyListException extends ModException {

    private String type;

    public ModEmptyListException(String type) {
        this.type = type;
    }

    public ModEmptyListException() {
        this("tasks");
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "There are no " + this.type + " in the list!";
    }
}
