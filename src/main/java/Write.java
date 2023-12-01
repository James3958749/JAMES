import java.util.ArrayList;
import java.io.*;


public class Write
{
    String[][] fileKey = {{"F", "FH"}, {"C", "CD"}}; // example given of translate
    Read readClass = new Read();

    /**
     * Writes data to a file
     * @param aircraftID Aircraft name
     * @param operationID Operation name
     * @param selectedOperators Operators are selected
     * @param optionOutput Ensures the ok button has been selected
     */
    public void writeOperation(String[] aircraftID, String operationID, String selectedOperators, String[] optionOutput)
    {
        char[] operationIDChar = operationID.toCharArray();
        String key = new String();
        for(int i = 0; i <fileKey.length;i++) //Gets the right file key
        {
            if(operationIDChar[0]==fileKey[i][0].charAt(0))
            {
                key = fileKey[i][1];
            }
        }
        System.out.println("Aircraft\\"+aircraftID[0]+"\\AircraftMaintenance"+key+".csv");
        ArrayList<String[]> oldText = readClass.readingDataTable("Aircraft\\"+aircraftID[0]+"\\AircraftMaintenance"+key+".csv"); //gets old data
        String[] newRow = new String[9];
        for(int i = 0;i<oldText.size();i++)
        {
            String[] textRow = oldText.get(i); //gets relevant row
            if(operationID.equals(textRow[0])) // compares ID
            {
                if(key.equals("FH")) //data will depend on key
                    newRow[4] = aircraftID[2];
                else if (key.equals("CD"))
                {
                    newRow[4] = aircraftID[2];
                }
                else
                {
                    newRow[4] = "N/A";
                }
                newRow[5] = "TRUE";
                newRow[0] = textRow[0]; //Adds data in the required order
                newRow[1] = textRow[1];
                newRow[2] = textRow[2];
                newRow[3] = textRow[3];
                newRow[6] = optionOutput[1];
                newRow[7] = optionOutput[2];
                newRow[8] = selectedOperators;
                oldText.set(i,newRow);
            }

        }

        try {
            // Create a FileWriter and a PrintWriter
            String filename = "C:\\Users\\James\\Documents\\University work\\Dissertation\\Github\\readFiles\\Aircraft\\"+aircraftID[0]+"\\AircraftMaintenance"+key+"New.csv"; //Adds to new to not affect current data
            FileWriter fw = new FileWriter(filename);
            PrintWriter pw = new PrintWriter(fw);

            // Loop through the list and write each String array as a row
            for (String[] array : oldText) {
                // Create a StringBuilder to construct the row
                StringBuilder sb = new StringBuilder();
                // Loop through the array and append each element with a comma
                for (String element : array) {
                    sb.append(element).append(",");
                }
                // Remove the last comma and add a new line
                sb.deleteCharAt(sb.length() - 1).append("\n");
                // Write the row to the file
                pw.write(sb.toString());
            }

            // Close the PrintWriter and the FileWriter
            pw.close();
            fw.close();

            System.out.println("File written successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
