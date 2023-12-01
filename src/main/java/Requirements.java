import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class Requirements extends TableClass
{
    String[] headings = {"ID", "Name", "Amount","Tickbox"}; //Sets up headings
    DefaultTableModel tableModel = new DefaultTableModel(headings,0);

    /**
     * Creates the table with 4 rows
     * @return
     */
    @Override
    public JTable createTable()
    {
        JTable table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            } //Prevents editing excluding the tickbox
            public Class getColumnClass(int column) {
                //return Boolean.class
                return getValueAt(0, column).getClass(); //gets values when required
            }
        };
        table.getColumnModel().getColumn(3).setCellRenderer(new CheckBoxRenderer());

        table.getTableHeader().setReorderingAllowed(false); //Prevents reordering
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//Ensures single selection
        table.getTableHeader().setResizingAllowed(false);//Prevents column changing
        TableColumnModel columnModel = table.getColumnModel(); //Sets column ratios
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(25);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //Centres columns
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        return table;
    }

    /**
     * Tracks the status of tick boxes
     */
    static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

        public CheckBoxRenderer() {
            // Set the horizontal alignment to center
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // Get the boolean value from the model
            boolean checked = (Boolean) value;

            // Set the selected state of the checkbox according to the value
            setSelected(checked);

            // Set the background color of the checkbox according to the value
            if (checked) {
                setBackground(Color.GREEN); // green for true
            } else {
                setBackground(Color.RED); // red for false
            }

            // Return this checkbox as the component to render
            return this;
        }

    }

    /**
     * Adds data to a table based on the aircraft information
     * @param filename Filename of aircraft
     * @param hours Flight hours of aircraft
     */
    @Override
    public void addData(String filename, String hours)
    {
        ArrayList<String[]> totalList = jobCalculator(filename, hours); //runs object to get relevant skill codes

        Object[][] data = new Object[totalList.size()][4]; //create a new object
        for(int i = 0;i<totalList.size();i++)
        {
            String[] row = totalList.get(i); //Adds data to object
            data[i][0] = row[0];
            data[i][1]  = row[1];
            data[i][2]  = row[2];
            data[i][3]  = false;
        }
        for(int i = 0; i<1;i++)
        {
            try{
                tableModel.removeRow(i);//removes the data
                i--;
            }
            catch(Exception exc){}
        }

        for (Object[] row : data) {
            tableModel.addRow(row); // adds the data back
        }


    }

    /**
     * Calculates the information about the selected job
     * @param filename Filename of aircraft
     * @param hours Flight Hours of aircraft
     * @return
     */
    public ArrayList<String[]> jobCalculator(String filename, String hours)
    {

        ArrayList<String[]> totalList = new ArrayList<String[]>();


        ArrayList<String[]> maintenanceMaster = readClass.readingDataTable("MasterMaintenance.csv"); //Reads in from master sheet
        for (int i = 0; i < maintenanceMaster.size(); i++)
        {
            String[] aircraftJobRow = maintenanceMaster.get(i);
            if (aircraftJobRow[0].equals(filename)) //searches for filename requested and measures if operation matches
            {
                ArrayList<String[]> toolData = readClass.readingDataTable("Maintenance\\Tool.csv"); //reads the tool codes
                String[] splitTool = aircraftJobRow[8].split("\\|"); //splits the tool codes
                for(int j = 0;j<splitTool.length;j++)
                {
                    String[] id = splitTool[j].split("x"); //splits code by x
                    if(Integer.parseInt(id[0])>0) //checks that ID exists
                    {

                        for(int k = 0;k<toolData.size();k++)
                        {

                            String[] tool = toolData.get(k); //Adds the tool codes
                            if(tool[0].equals(id[0]))
                            {
                                String[] row = new String[3];
                                row[0] = id[0];
                                row[1] = tool[1];
                                row[2] = id[1];
                                totalList.add(row);
                            }
                        }
                    }
                }
                ArrayList<String[]> equipmentData = readClass.readingDataTable("Maintenance\\Equipment.csv"); //reads the equipment codes
                String[] splitEquipment = aircraftJobRow[9].split("\\|"); //splits the equipment codes
                for(int j = 0;j<splitEquipment.length;j++)
                {
                    String[] id = splitEquipment[j].split("x");  //splits code by x.
                    if(Integer.parseInt(id[0])>0) //checks that ID exists
                    {

                        for(int k = 0;k<equipmentData.size();k++)
                        {

                            String[] equipment = equipmentData.get(k);//Adds the equipment codes
                            if(equipment[0].equals(id[0]))
                            {
                                String[] row = new String[3];
                                row[0] = id[0];
                                row[1] = equipment[1];
                                row[2] = "";
                                totalList.add(row);
                            }
                        }
                    }
                }
                ArrayList<String[]> partData = readClass.readingDataTable("Maintenance\\Part.csv"); //reads the part codes
                String[] splitPart = aircraftJobRow[10].split("\\|"); //splits the part codes
                for(int j = 0;j<splitPart.length;j++)
                {
                    String[] id = splitPart[j].split("x"); //splits code by x
                    if(Integer.parseInt(id[0])>0)//checks that ID exists
                    {

                        for(int k = 0;k<partData.size();k++)
                        {

                            String[] part = partData.get(k);//Adds the part codes
                            if(part[0].equals(id[0]))
                            {
                                String[] row = new String[3];
                                row[0] = id[0];
                                row[1] = part[1];
                                row[2] = id[1];
                                totalList.add(row);
                            }
                        }
                    }
                }
            }
        }
        return totalList;
    }
}
