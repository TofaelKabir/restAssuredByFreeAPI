package d_api_stage;

import a_constants.Command;
import b_api_prep.GetAPIPrep;
import b_api_prep.PostAPIPrep;
import c_utils.DataPrep;
import c_utils.ExcelPrep;
import c_utils.JSONPrep;

import java.util.Map;

public class PostAPIStage implements Command {
    private DataPrep dataPrep = new DataPrep();
    private ExcelPrep excelPrep = new ExcelPrep();
    private JSONPrep jsonPrep = new JSONPrep();
    private GetAPIPrep getAPIPrep = new GetAPIPrep();
    private PostAPIPrep postAPIPrep = new PostAPIPrep();

    public String postAPIExecution() {
        String testStatus = null;
        if (dataPrep.flagStatus()) { //FROM DataPrep class, last method
            excelPrep.loadExcel();
            int counter = excelPrep.sheet.getLastRowNum();
            int firstRow = excelPrep.firstRow + 1;
            for (int x = firstRow; x <= counter; x++) {
                Map<String, String> data = excelPrep.dataTable(x);
                if (data.size() != 0) {
                    if (data.get(exec) != null && data.get(exec).equalsIgnoreCase(flag)) {
                        jsonPrep.loadOrgJson();
                        if(jsonPrep.payload != null) {
                            String quote = getAPIPrep.requestGetAPI(); // API get call here
                            for (String key : data.keySet()) {
                                jsonPrep.heatingSource(key, data.get(key));
                                jsonPrep.UMPDCoverage(key, data.get(key));
                                jsonPrep.setObject(key, data.get(key));
                            }
                            Object quoteNo = jsonPrep.jPath(quote, quoteNode);
                            jsonPrep.setObject(policyNode, String.valueOf(quoteNo));
                            int policyCol = excelPrep.getColumnNo(policyNode);
                            excelPrep.writeBackInExcel(x, policyCol, String.valueOf(quoteNo));
                            excelPrep.writeBackInExcel(x, excelPrep.getColumnNo(requestField), jsonPrep.payload);


                            String response = postAPIPrep.requestPostAPI(jsonPrep.payload);// API POST
                            excelPrep.writeBackInExcel(x, excelPrep.getColumnNo(responseField), response);
                            for(String result: dataPrep.resultFields()) {
                                Object out = jsonPrep.jPath(response, result);
                                int newCol = excelPrep.getColumnNo(result);
                                policyCol++;
                                if(newCol == 0) {
                                    excelPrep.writeBackInExcel(firstRow -1, policyCol , result);
                                    excelPrep.writeBackInExcel(x, policyCol, String.valueOf(out));
                                }else {
                                    excelPrep.writeBackInExcel(firstRow -1, newCol, result);
                                    excelPrep.writeBackInExcel(x, newCol, String.valueOf(out));
                                }

                            }
                            data.clear();
                        }
                        testStatus = "=======================================\n"+"*** "+success+" ***"+"\n=======================================";
                    }
                } else {
                    testStatus = excelError;
                }
            }
        } else {
            testStatus = error;
        }
        System.out.println(testStatus);
        return testStatus;
    }

}
