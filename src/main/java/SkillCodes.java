import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class SkillCodes extends TableClass
{
    String[] headings = {"ID", "Code","Tickbox"}; // Sets up headings
    DefaultTableModel tableModel = new DefaultTableModel(headings,0);
    /**
     * Creates the table with 4 rows
     * @return
     */
    @Override
    public JTable createTable() //Sets up table
    {
        JTable table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            } //Prevents editing of the table excluding the tickbox
            public Class getColumnClass(int column) {
                //return Boolean.class
                return getValueAt(0, column).getClass(); //Gets selected rows
            }
        };
        table.getTableHeader().setReorderingAllowed(false); //Prevents reordering
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //Ensures single selection
        table.getTableHeader().setResizingAllowed(false); //Prevents column changing
        TableColumnModel columnModel = table.getColumnModel(); //Sets column ratios
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(50);
        columnModel.getColumn(2).setPreferredWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //Centres columns
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(new Requirements.CheckBoxRenderer());

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
        ArrayList<String[]> codeList = jobCalculator(filename, hours); //runs object to get relevant skill codes

        Object[][] data = new Object[codeList.size()][3]; //create a new object
        for(int i = 0;i<codeList.size();i++)
        {
            String[] row = codeList.get(i); //Adds data to object
            data[i][0] = row[0];
            data[i][1]  = row[1];
            data[i][2]  = false;
        }
        for(int i = 0; i<1;i++)
        {
            try{
                tableModel.removeRow(i); //removes the data
                i--;
            }
            catch(Exception exc){}
        }

        for (Object[] row : data)  // adds the data back
        {
            tableModel.addRow(row);
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
        ArrayList<String[]> codeList = new ArrayList<String[]>();
        ArrayList<String[]> maintenanceMaster = readClass.readingDataTable("MasterMaintenance.csv"); //Reads in from master sheet
        for (int i = 0; i < maintenanceMaster.size(); i++)
        {
            String[] aircraftJobRow = maintenanceMaster.get(i);
            if (aircraftJobRow[0].equals(filename)) //searches for filename requested and measures if operation matches
            {
                ArrayList<String[]> codeData = readClass.readingDataTable("Maintenance\\SkillCodes.csv"); //reads the skill codes
                String[] splitCode = aircraftJobRow[11].split("\\|"); //splits the skill codes

                for(int j = 0;j<splitCode.length;j++)
                {
                    String[] id = splitCode[j].split("x"); //splits code by x. There so code is the same as equipment/tool requirements
                    if(Integer.parseInt(id[0])>0) //checks that ID exists
                    {
                        for(int k = 0;k<codeData.size();k++)
                        {
                            String[] skillCode = codeData.get(k); //Adds the skill codes
                            if(skillCode[0].equals(id[0]))
                            {
                                String[] row = new String[2];
                                row[0] = id[0];
                                row[1] = skillCode[1];
                                codeList.add(row);
                            }
                        }
                    }
                }
            }
        }
        return codeList;
    }
}
