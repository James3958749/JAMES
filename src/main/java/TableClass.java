import javax.swing.*;
import java.util.ArrayList;

public abstract class TableClass
{
    Read readClass = new Read();
    ArrayList<String[]> data = new ArrayList<String[]>();


    public abstract JTable createTable();
    {

    }
    public abstract void addData(String filename, String hours);
    {

    }

}
