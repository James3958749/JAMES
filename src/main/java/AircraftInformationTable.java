import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class AircraftInformationTable extends TableClass
{
    // Initiates variables
    String[] headings = {"ID","Aircraft", "Flight Hours", "Calendar Days", "Time in", "Time required","Jobs"}; //Table headings
    DefaultTableModel tableModel = new DefaultTableModel(headings,0);
    JScrollPane tableScroll;
    ArrayList<String[]> aircraftData = readClass.readingDataTable("AircraftInDepot.csv"); //Reads from abstract
    String[] row = new String[3];
    AircraftJobsTable aJTable = new AircraftJobsTable();

    /**
     * Creates a table with 7 columns about the key aircraft information
     * @return
     */
    @Override
    public JTable createTable() {
        JTable table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            } // prevents editing
        };

        table.getTableHeader().setReorderingAllowed(false); //Prevents editing
        table.getTableHeader().setResizingAllowed(false);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //Ensures only one cell can be edited

        TableColumnModel columnModel = table.getColumnModel(); //Sets column widths
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(50);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //Centres columns
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        jobsCalculator(); //Determines what aircraft jobs are needed
        addData("BLANK","Blank"); //Adds the data to the table

        return table;//tableScroll;
    }

    /**
     * Calculates the amount of jobs required for each aircraft based on the excel files
     */
    public void jobsCalculator()
    {
        ArrayList<String[]> maintenanceMaster = readClass.readingDataTable("MasterMaintenance.csv"); //Reads in the maintenance data
        for(int i = 1;i<aircraftData.size();i++) //for each aircraft
        {
            int counter = 0; //counts the jobs
            String[] aircraftInformationRow = aircraftData.get(i); //gets the aircraft information
            String filename = aircraftInformationRow[0]; //Starts with the name

            ArrayList<String[]> aircraftMaintenanceMaster = readClass.readingDataTable("Aircraft\\"+filename+"\\AircraftMaintenanceFH.csv"); //Reads in the flight hour operations
            for(int j = 1;j<aircraftMaintenanceMaster.size();j++) //For each one
            {
                String[] aircraftJobRow = aircraftMaintenanceMaster.get(j); //Gets the row
                if(aircraftJobRow[5].equals("FALSE")) //if the operation has not been completed
                {
                    String[] longID = aircraftJobRow[0].split("-"); //splits the operationID for the aircraft to just the operationID
                    for(int k = 1;k<maintenanceMaster.size();k++)  //finds the operation information
                    {
                        String[] jobRow = maintenanceMaster.get(k); //gets the operation information
                        if(longID[0].equals(jobRow[0])) //if the ID matchs
                        {
                            if(Integer.parseInt(aircraftInformationRow[2])>Integer.parseInt(aircraftJobRow[1])) //checks the aircraft is above the operation minimum
                            {
                                if(Integer.parseInt(aircraftInformationRow[2])<Integer.parseInt(aircraftJobRow[3])) //Increases the counter if the operation can be done
                                {
                                    counter = counter + 1;
                                }
                                else //Displays a warning if the aircraft has overflown the maximum limit
                                {
                                    JOptionPane.showMessageDialog(null,"Aircraft has overflown","Information",1);
                                }
                            }
                        }
                    }
                }
            }
            aircraftInformationRow[6] = Integer.toString(counter); // Adds the counter
            aircraftData.set(i,aircraftInformationRow); //Changes the number
        }
    }

    /**
     * Adds data to the table
     * @param filename (Not used - only used as in abstract)
     * @param hours (Not used - only used as in abstract)
     */
    @Override
    public void addData(String filename, String hours)
    {
        for(int i = 0; i<1;i++) //Removes all rows
        {
            try{
                tableModel.removeRow(i);
                i--;
            }
            catch(Exception exc){}
        }
        for(int i = 1;i<aircraftData.size();i++)//Adds each row to the table
        {
            String[] row = aircraftData.get(i);
            tableModel.addRow(row);
        }
    }

}