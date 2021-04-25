package c_utils;

import a_constants.Constant;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

// command+Alt+l to organize the code
// This class connect to the config.txt file for input the parameter
public class DataPrep implements Constant {
    //check point for can read the config file
    public boolean status = false;

    //Adding all the files in (files Array list)
    public ArrayList<File> getFiles() {
        ArrayList<File> files = new ArrayList<File>();
        File folder = new File(System.getProperty(findDir) + inputDir);
        for (File file : folder.listFiles()) { // return list of file
            if (!file.isDirectory()) { //check file or folder
                files.add(file);
            }
        }
        return files;
    }

    private ArrayList<Object> readConfig() {
        ArrayList<Object> lines = new ArrayList<Object>();
        if (getFiles().size() == 0) {
            lines.add(0);
        } else {
            for (File file : getFiles()) {
                try {
                    Path path = FileSystems.getDefault().getPath(file.getCanonicalPath()); //again define the path because exception can occur
                    if (file.getCanonicalPath().contains(properties)) {
                        lines.add(1); // file is present
                        lines.add(Files.readAllLines(path)); //Array.add(1); ArrayList.add("Environment = QA, File = Filename, Result = ");
                    }
                } catch (IOException e) {
                    lines.add(0);
                }
            }
        }
        return lines;
    }

    public HashMap<String, String> getConfigData() {
        HashMap<String, String> map = new HashMap<String, String>();

        if (!readConfig().get(0).toString().contains("0")) {
            status = true;
            String[] lines = readConfig().get(1).toString().split(",");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains("=")) {
                    String[] split = lines[i].split("=");
                    map.put(split[0].replace("[", "").trim(), split[1].replace("]", "").trim());
                }
            }
            if (map.size() == 0) {
                status = false;
            }
        }
        return map;
    }

    public String getEnvironement() {
        if (getConfigData().get(Fields.ENVIRONMENT.toString()).equalsIgnoreCase(preDev)) {
            return preDev;
        } else {
            return dev;
        }
    }

    public ArrayList<String> resultFields() {
        ArrayList<String> fields = new ArrayList<String>();
        String resultsFields = readConfig().get(1).toString();
        if (resultsFields.contains(Fields.RESULTS.toString())) {
            String evalFields = resultsFields.substring(resultsFields.indexOf(Fields.RESULTS.toString()), resultsFields.length() - 1);
            String breakingDown = evalFields.substring(evalFields.indexOf("="));
            String finalCut = breakingDown.substring(1, breakingDown.length());
            for (String s : finalCut.split(",")) {
                if (s.length() > 1) {
                    fields.add(s.trim());
                }
            }
        } else {
            fields.add("0");
        }
        return fields;
    }

    public String getProduct() {
        if (getConfigData().get(Fields.PRODUCT.toString()).equalsIgnoreCase(resourceA)) {
            return resourceA;
        } else {
            return resourceB;
        }
    }

    public boolean flagStatus() {
        getConfigData();
        return status;
    }


}
