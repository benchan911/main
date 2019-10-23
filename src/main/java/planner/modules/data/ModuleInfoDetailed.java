package planner.modules.data;

public class ModuleInfoDetailed {

    private String moduleCode = "";
    private String title = "";
    private String description = "";
    private String moduleCredit = "";
    private String department = "";
    private String faculty = "";
    private String preclusion = "";
    private Attributes attributes = new Attributes();
    private ExamInfo[] semesterData;


    public String getModuleCode() {
        return moduleCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getModuleCredit() {
        return Double.parseDouble(moduleCredit);
    }

    public String getDepartment() {
        return department;
    }

    public String getFaculty() {
        return faculty;
    }


    public String getPreclusion() {
        return preclusion;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public ExamInfo[] getSemesterData() {
        return semesterData;
    }

    @Override
    public String toString() {
        return "ModuleCode:"
                + getModuleCode()
                + ", MC:"
                + getModuleCredit()
                + ", SU:"
                + getAttributes().isSu();
    }
}
