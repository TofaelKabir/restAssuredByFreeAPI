package c_utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.jayway.jsonpath.JsonPath;
import a_constants.Constant;

public class JSONPrep implements Constant {
    public static String payload = null;
    private DataPrep dataPrep = new DataPrep();

//	public static void main(String[] args) {
//		loadOrgJson();
//		UMPDCoverage("P.UMPDCoverageCode", "2");
//		System.out.println(payload);
//	}

    public JSONObject loadOrgJson() {
        try {
            InputStream iStream = new FileInputStream((System.getProperty(findDir) + Constant.inputDir
                    + dataPrep.getConfigData().get(Fields.SAMPLE.toString())));
            JSONTokener jsonTokener = new JSONTokener(iStream);
            JSONObject object = new JSONObject(jsonTokener);
            payload = object.toString();
            return object;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public void setObject(String key, String value) {
        String lookUp = "\""+key+"\":\"";
        if (payload != null) {
            if (payload.contains(lookUp) && value.length() > 0) {
                String item = "";
                try {
                    String subStr = payload.substring(payload.indexOf(lookUp)+1).substring(0, payload.substring(payload.indexOf(lookUp)).lastIndexOf("\""));
                    int count = countDoubleQuote(subStr);
                    if(count > 3) {
                        String secondCut = subStr.substring(subStr.indexOf("\"")+3, subStr.length());
                        String finalCut = secondCut.substring(secondCut.indexOf("\""), secondCut.length());
                        item = "\"" + subStr.replace(finalCut, "");
                        //	System.out.println(item);
                    }else {
                        item = "\"" + subStr + "\"";
                    }
                }catch(Throwable e) {
                    System.err.println("Error for = \n"+ key + " : " + value);
                }
                String replaceItem = lookUp + value;
                if (item != null) {
                    payload = payload.replace(item, replaceItem);
                }
            }
        }
    }

    private int countDoubleQuote(String input) {
        int counter = 0;
        char[] charArr = input.toCharArray();
        for(char c:charArr) {
            if(c == '\"') {
                counter++;
                continue;
            }
        }
        return counter;
    }

    public void heatingSource(String key, String value) {
        String temp = "0";
        if(key.equalsIgnoreCase(Heating.BuiltInFireplace.toString())) {
            temp = Heating.BuiltInFireplace.getHeating()+"\":{\""+Heating.count.getHeating()+"\":\""+value;
            payload = payload.replace(Heating.BuiltInFireplace.getHeating()+"\":{\""+Heating.count.getHeating()+"\":\"0", temp);
        }else if(key.equalsIgnoreCase(Heating.FireplaceInsert.toString())) {
            temp = Heating.FireplaceInsert.getHeating()+"\":{\""+Heating.count.getHeating()+"\":\""+value;
            payload = payload.replace(Heating.FireplaceInsert.getHeating()+"\":{\""+Heating.count.getHeating()+"\":\"0", temp);
        }else if(key.equalsIgnoreCase(Heating.FreestandingStove.toString())) {
            temp = Heating.FreestandingStove.getHeating()+"\":{\""+Heating.count.getHeating()+"\":\""+value;
            payload = payload.replace(Heating.FreestandingStove.getHeating()+"\":{\""+Heating.count.getHeating()+"\":\"0", temp);
        }
        //	System.out.println(temp);
    }

    public void UMPDCoverage(String key, String value) {
        String temp = key.substring(0,1);
        if(key.contains(UMPD.UMPDCoverageCode.toString())) {
            if (temp.equalsIgnoreCase(UMPD.P.toString())) {
                String sub = payload.substring(payload.indexOf(UMPD.PolicyCoverage.toString()), payload.length());
                String sub1 = sub.substring(sub.indexOf(UMPD.UMPDCoverageCode.toString()), sub.length());
                String secondCut = sub1.substring(sub1.indexOf("\"")+3, sub1.length());
                sub1 = sub.replace(secondCut, "");
                temp = sub1.substring(0, sub1.lastIndexOf("\""))+"\""+value;
                payload = payload.replace(sub1, temp);
            }else if (temp.equalsIgnoreCase(UMPD.V.toString())) {
                String sub = payload.substring(payload.indexOf(UMPD.VehicleCoverage.toString()), payload.length());
                String sub1 = sub.substring(sub.indexOf(UMPD.UMPDCoverageCode.toString()), sub.length());
                String secondCut = sub1.substring(sub1.indexOf("\"")+3, sub1.length());
                sub1 = sub.replace(secondCut, "");
                temp = sub1.substring(0, sub1.lastIndexOf("\""))+"\""+value;
                payload = payload.replace(sub1, temp);
            }
        }
    }

    public String jPath(String json, String key) {
        List<Object> nodes = JsonPath.read(json, "."+key+"");
        return nodes.toString().replace("[", "").replace("]", "").replace("\"", "");
    }

    enum Heating{
        BuiltInFireplace("BuiltInFireplaceType"), FireplaceInsert("FireplaceInsertType"), FreestandingStove("FreestandingStoveType"), count("IsSelected");
        private String heating;
        public String getHeating() {
            return heating;
        }
        private Heating(String heating){
            this.heating = heating;
        }
    }

    enum UMPD{
        UMPDCoverageCode, VehicleCoverage, PolicyCoverage, V, P,
    }


}
