import java.io.*;
import java.lang.*;
import java.util.ArrayList;

public class Read
{
    /**
     * Reads a table based on a file name and returns it
     * @param filename filename of the relavant file
     * @return
     */
    public ArrayList<String[]> readingDataTable(String filename) {
        //Filereader
        ArrayList<String[]> aircraftData = new ArrayList<String[]>();
        String filepath = "C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\" + filename; // Centralised data location
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String tempString;
            while ((tempString = br.readLine()) != null)    /*Runs program if line isn't blank*/ {
                String[] splitData = tempString.split(","); //splits line into terms
                aircraftData.add(splitData);//adds data to the table
            }
            br.close();
        } catch (Exception exc) {
            System.out.println("READ error");
            exc.printStackTrace();
        }
        return aircraftData;
    }
}