import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class AircraftJobsTable extends TableClass
{
    //Initialises variables
    String[] headings = {"Operation ID", "Time required", "Needs doing in"};
    DefaultTableModel tableModel = new DefaultTableModel(headings,0);
    JScrollPane tableScroll;

    /**
     * Creates a table with 3 rows about the aircraft jobs
     * @return
     */
    @Override
    public JTable createTable() {
        JTable table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            } //Prevents editing
        };

        table.getTableHeader().setReorderingAllowed(false); //Prevents editing
        table.getTableHeader().setResizingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumnModel columnModel = table.getColumnModel(); //sets column rations
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //Centres columns
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        return table;
    }

    /**
     * Adds data to the table
     * @param filename filename of the aircraft
     * @param hours Hours the aircraft had been completed
     */
    @Override
    public void addData(String filename, String hours)
    {
        ArrayList<String[]> maintenance = jobCalculator(filename, hours); //Adds maintenance information to second table

        for(int i = 0; i<1;i++) //Removes table rows
        {
            try{
                tableModel.removeRow(i);
                i--;
            }
            catch(Exception exc){}
        }
        for(int i = 0;i<maintenance.size();i++) //Adds rows
        {
            tableModel.addRow(maintenance.get(i));
        }
    }

    /**
     * Calculates the jobs required for each aircraft including IDs and needed time
     * @param filename Aircraft number
     * @param hours Aircraft hours
     * @return
     */
    public ArrayList<String[]> jobCalculator(String filename, String hours)
    {
        ArrayList<String[]> maintenanceMaster = readClass.readingDataTable("MasterMaintenance.csv"); //Reads in maintenance information
        ArrayList<String[]> aircraftMaintenanceMaster = readClass.readingDataTable("Aircraft\\"+filename+"\\AircraftMaintenanceFH.csv"); //Reads in aircraft information
        ArrayList<String[]> maintenance = new ArrayList<>();

        for(int j = 1;j<aircraftMaintenanceMaster.size();j++) // For each aircraft operation
        {
            String[] aircraftJobRow = aircraftMaintenanceMaster.get(j);
            if(aircraftJobRow[5].equals("FALSE")) //Determines if operation was done
            {
                String[] longID = aircraftJobRow[0].split("-");
                String id = longID[0]; //Splits by ID to find maintenance ID
                for(int k = 1;k<maintenanceMaster.size();k++)
                {
                    String[] jobRow = maintenanceMaster.get(k); //gets maintenance ID
                    if(id.equals(jobRow[0])) //If ID found
                    {
                        if(Integer.parseInt(hours)>Integer.parseInt(aircraftJobRow[1]))
                        {
                            if(Integer.parseInt(hours)<Integer.parseInt(aircraftJobRow[3])) //Gets key information and adds to row
                            {
                                String[] row = new String[3];
                                row[0] = aircraftJobRow[0];
                                row[1] = jobRow[7];
                                row[2] = Integer.toString(Integer.parseInt(aircraftJobRow[3])-Integer.parseInt(hours));
                                maintenance.add(row);
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(null,"Aircraft has overflown","Information",1);
                            }
                        }
                    }
                }
            }
        }
        return maintenance;
    }
}
