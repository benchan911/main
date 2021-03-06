//@@author namiwa

package planner.util.crawler;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import planner.logic.exceptions.planner.ModBadRequestStatus;
import planner.logic.exceptions.planner.ModFailedJsonException;
import planner.logic.modules.cca.Cca;
import planner.logic.modules.TaskList;
import planner.logic.modules.module.ModuleInfoDetailed;
import planner.logic.modules.module.ModuleTask;
import planner.util.logger.PlannerLogger;
import planner.util.storage.Storage;

public class JsonWrapper {

    private Gson gson;
    private RequestData requestsData;
    private final String listFile = "data/modsListData.json";
    private final String listDetailedFile = "data/modsDetailedListData.json";
    private final String userModuleFile = "data/userData.json";
    private final String userCcaFile = "data/ccaListData.json";
    private final String academicYear = "2019-2020";

    public enum Requests {
        DETAILED, SUMMARY
    }

    /**
     * Constructor for JsonWrapper to access module information.
     */
    public JsonWrapper() {
        gson = new Gson();
        requestsData = new RequestData();
    }

    private HashMap<String, ModuleInfoDetailed> getMapFromList(List<ModuleInfoDetailed> modsList) {
        HashMap<String, ModuleInfoDetailed> ret = new HashMap<>();
        for (ModuleInfoDetailed temp : modsList) {
            String modCode = temp.getModuleCode();
            ret.put(modCode, temp);
        }
        return ret;
    }

    /**
     * Updating detailed module list file in data folder.
     * @param academicYear Academic Year input by user.
     * @param store Storage object to write files.
     * @throws ModBadRequestStatus If the user's status return from API call is not 200 (success).
     */
    public void runRequests(String academicYear, Storage store) throws ModBadRequestStatus {
        store.setDataPath(Paths.get(listDetailedFile));
        if (store.getDataPathExists()) {
            return;
        }
        requestsData.storeModData(requestsData.requestModuleListDetailed(academicYear), store);
    }

    /**
     * Converts the stored json file into a list of ModuleInfoDetailed objects.
     * @return a list of ModuleInfoDetailed objects, null if it fails to parse.
     */
    private List<ModuleInfoDetailed> getModuleListDetailedObject() {
        try {
            JsonReader reader = new JsonReader(new FileReader(listDetailedFile));
            Type listType = new TypeToken<List<ModuleInfoDetailed>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            PlannerLogger.log(e);
        } catch (IOException ei) {
            System.out.println(Arrays.toString(ei.getStackTrace()));
            PlannerLogger.log(ei);
        }
        return new ArrayList<>();
    }

    /**
     * Overloaded function to generate runtime file from resources instead of query file from NUSMODS.
     * @param set Flag to run code.
     * @param store Storage Object for file check.
     * @return HashMap of Module code mapped to corresponding ModuleInfoDetailed.
     * @throws ModFailedJsonException If the user's status return from API call is not 200 (success).
     */
    public HashMap<String, ModuleInfoDetailed> getModuleDetailedMap(boolean set, Storage store)
            throws ModFailedJsonException {
        if (set && store.getDataPathExists()) {
            return getModuleDetailedMap();
        } else {
            InputStream in = this.getClass().getResourceAsStream("/data/modsDetailedListData.json");
            Type listType = new TypeToken<List<ModuleInfoDetailed>>(){}.getType();
            InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
            List<ModuleInfoDetailed> modsList = gson.fromJson(inputStreamReader, listType);
            return getMapFromList(modsList);
        }
    }

    /**
     * Main helper function to obtained HashMap of detailed info from modsDetailedListData.json.
     * @return HashMap with module code as the key and ModuleInfoSummary object as the value.
     * @throws ModFailedJsonException If the previous call to getModuleListObject() returns null.
     */
    public HashMap<String, ModuleInfoDetailed> getModuleDetailedMap() throws ModFailedJsonException {
        List<ModuleInfoDetailed> modsList = getModuleListDetailedObject();
        if (modsList.size() == 0) {
            throw new ModFailedJsonException();
        }
        return getMapFromList(modsList);
    }


    /**
     * Stores the current state of the taskList into a json file.
     * @param tasksList List of module tasks.
     * @param store object which handles file storing.
     */
    public void storeTaskListAsJson(TaskList<ModuleTask> tasksList, Storage store) {
        String jsonString = gson.toJson(tasksList);
        List<String> stringsList = requestsData.getResponseList(jsonString);
        store.setDataPath(Paths.get(userModuleFile));
        store.writeModsData(stringsList);
    }

    /**
     * Stores the current state of the ccaList into a json file.
     */
    public void storeCcaListAsJson(List<Cca> ccaList, Storage store) {
        String jsonString = gson.toJson(ccaList);
        List<String> stringsList = requestsData.getResponseList(jsonString);
        store.setDataPath(Paths.get(userCcaFile));
        store.writeModsData(stringsList);
    }

    /**
     * Returns taskList after reading json file.
     * @return List of tasks of the read was successful, null if otherwise.
     */
    public TaskList<ModuleTask> readJsonTaskList(Storage store) {
        try {
            store.setDataPath(Paths.get(userModuleFile));
            if (store.getDataPathExists()) {
                JsonReader reader = new JsonReader(new FileReader(userModuleFile));
                Type listType = new TypeToken<TaskList>() {}.getType();
                return gson.fromJson(reader, listType);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            PlannerLogger.log(e);
        } catch (IOException ei) {
            System.out.println(Arrays.toString(ei.getStackTrace()));
            PlannerLogger.log(ei);
        }
        return new TaskList<>();
    }

    /**
     * Returns ccaList after reading json file.
     * @return List of ccas of the read was successful, null if otherwise.
     */
    public TaskList<Cca> readJsonCcaList(Storage store) {
        try {
            store.setDataPath(Paths.get(userCcaFile));
            if (store.getDataPathExists()) {
                JsonReader reader = new JsonReader(new FileReader(userCcaFile));
                Type listType = new TypeToken<TaskList>() {
                }.getType();
                return gson.fromJson(reader, listType);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            PlannerLogger.log(e);
        } catch (IOException ei) {
            System.out.println(Arrays.toString(ei.getStackTrace()));
            PlannerLogger.log(ei);
        }
        return new TaskList<>();
    }

}
