import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.formdev.flatlaf.FlatLightLaf;

public class James extends JFrame implements ActionListener, ComponentListener, ItemListener, ChangeListener
{
    JTabbedPane tabs = new JTabbedPane(); //Adds tabs

    //[Panel 1 StartVariables]
    JPanel panel1 = new JPanel(null);
    JLabel p1Title = new JLabel();
    AircraftInformationTable aircraft = new AircraftInformationTable(); //Brings in aircraft information
    JScrollPane aircraftInformation; //Adds scrollbar
    AircraftJobsTable aircraftJobsTable = new AircraftJobsTable(); //Brings in aircraft job information
    JScrollPane jobs; //Adds scrollbar
    String operationID = new String(); // Declared globally to be used once selected
    String[] aircraftID = new String[7]; //Aircraft information for passthrough
    //[Panel 1 EndVariables]

    //[Panel 2 StartVariables]
    JPanel panel2 = new JPanel(null);
    JLabel p2Title = new JLabel();
    Requirements operationsRequirements = new Requirements(); // brings in the first set of operation requirements
    JScrollPane orTable; //Adds scrollbar
    SkillCodes codeRequirements = new SkillCodes(); // brings in the second set of operation requirements
    JScrollPane codeTable; //Adds scrollbar
    int number_of_operations = 0; //global counters needed for buttons
    int number_of_skills = 0; //global counters needed for buttons
    JButton openPDF = new JButton(); // Opens files
    JButton openVideo = new JButton();// Opens files
    JButton openPPTX = new JButton();// Opens files
    JButton operationComplete = new JButton(); //Allows saving
    //[Panel 2 EndVariables]

    //[Completion StartVariables]
        JButton fileUploadButton = new JButton("Add files"); // Adding the save buttons
        Write writingClass = new Write(); // Writing based on new information

    //[Panel 3 EndVariables]

    //[StartFrameSettings

    /**
     * Initialises the GUI including setting up basic format and creating the two panels
     */
    public void startGUI()
    {
        FlatLightLaf.install();
        FlatLightLaf.setup();

        //sets up panel
        int width = 1000; //Default size
        int height = 600;

        tabs.addChangeListener(this);
        tabs.addComponentListener(this);

        //adds panels
        createPanel1();
        sizePanel1(width,height);
        tabs.add("Table", panel1); //adds the tab to the array of tabs
        createPanel2();
        sizePanel2(width,height);
        tabs.add("Operation", panel2);

        this.setLayout(new GridLayout(1,1)); //Sets basic software up
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Database");
        this.setSize(width,height);
        this.setForeground(new Color(-16777216));
        this.setBackground(new Color(-16777216));
        this.setMinimumSize(new Dimension(625, 575));
        this.setVisible(true);
        this.setResizable(true);
        this.add(tabs);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\Logo.png")); //Adds a logo
    }

    /**
     * Creates first panel including the two tables and title
     */
    public void createPanel1()
    {
        JTable aircraftTable = aircraft.createTable(); //Creates a table using the object
        aircraftTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                for(int i = 0;i< aircraftID.length;i++)
                {
                    aircraftID[i] = aircraftTable.getValueAt(aircraftTable.getSelectedRow(), i).toString(); //Gets selected value
                }
                aircraftJobsTable.addData(aircraftTable.getValueAt(aircraftTable.getSelectedRow(), 0).toString(),aircraftTable.getValueAt(aircraftTable.getSelectedRow(), 2).toString()); //Adds information to relevant tables
            }
        });
        aircraftInformation = new JScrollPane(aircraftTable); //Adds scroller

        JTable aJTable = aircraftJobsTable.createTable();//Creates a table using the object
        aJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                tabs.setSelectedIndex(1); // Moves to next page
                operationID = aJTable.getValueAt(aJTable.getSelectedRow(), 0).toString(); //Gets the operation ID
                String[] splitter = operationID.split("-"); //Splits to master operation ID rather than instance of the operation
                operationsRequirements.addData(splitter[0],"Blank"); //Runs an adddata method to add data to the table on tab 2
                codeRequirements.addData(splitter[0],"Blank"); //Runs an adddata method to add data to the table on tab 2
            }
        });
        jobs = new JScrollPane(aJTable); // Adds scroller

        p1Title.setOpaque(true);
        //t1_lbltitle.setFont(new Font("Arial", Font.PLAIN, 14));
        p1Title.setText("Aircraft Main Page"); //Adds title
    }

    /**
     * Resizes panel based on changes
     * @param width Panel width
     * @param height Panel height
     */
    public void sizePanel1(int width, int height)
    {
        //Sets sizes and locations and adds to the tab
        p1Title.setLocation(((width)/2)-50,(height)/50);
        p1Title.setSize(150,20);
        panel1.add(p1Title);

        aircraftInformation.setSize(width*4/10,height*3/4);
        aircraftInformation.setLocation((width)/20,(height)*1/16);
        panel1.add(aircraftInformation);

        jobs.setSize(width*4/10,height*3/4);
        jobs.setLocation((width*11/20),(height/16));
        panel1.add(jobs);
    }

    /**
     * Creates the second panels, including the table and buttons
     */
    public void createPanel2()
    {
        p2Title.setOpaque(true);
        p2Title.setText("Operation Details");

        JTable operations = operationsRequirements.createTable(); //Creates a table using the object
        TableModel operationsModel = operations.getModel(); // Needed to add a listener
        TableModelListener operationslistener = new TableModelListener () {
            // Whenever the information is changed it checks if all boxes are selected and sets a global variable based off this
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    for(int i = 0; i <operations.getRowCount(); i++)
                    {
                        String value = operations.getValueAt(i, 3).toString();
                        if(value.equals("true"))
                        {
                            number_of_operations= 1;
                        }
                        else
                        {
                            number_of_operations = 0;
                            break;
                        }
                    }
                    showOption(); //Runs method to ensure the tick boxes lead to the correct button response
                }
            }
        };
        operationsModel.addTableModelListener (operationslistener); //Adds the listener
        orTable = new JScrollPane(operations); // Adds the scroller

        JTable codes = codeRequirements.createTable(); //Creates a table using the object
        TableModel codeModel = codes.getModel();  // Needed to add a listener
        TableModelListener codeslistener = new TableModelListener () {
            // Whenever the information is changed it checks if all boxes are selected and sets a global variable based off this
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    for(int i = 0; i <codes.getRowCount(); i++)
                    {
                        String value = codes.getValueAt(i, 2).toString();
                        if(value.equals("true"))
                        {
                            number_of_skills = 1;
                        }
                        else
                        {
                            number_of_skills = 0;
                            break;
                        }
                    }
                    showOption();//Runs method to ensure the tick boxes lead to the correct button response
                }
            }
        };
        codeModel.addTableModelListener(codeslistener); //Adds the listener
        codeTable = new JScrollPane(codes); // Adds the scroller

        //sets appropriate text and button options to only show if the right boxes are selected
        openPDF.setText("Open PDF");
        openPDF.setVisible(false);
        openPDF.addActionListener(this);

        openVideo.setText("Open Video");
        openVideo.setVisible(false);
        openVideo.addActionListener(this);

        openPPTX.setText("Open PowerPoint");
        openPPTX.setVisible(false);
        openPPTX.addActionListener(this);

        operationComplete.setText("Operation Complete");
        operationComplete.setVisible(false);
        operationComplete.addActionListener(this);
        operationComplete.setBackground (Color.GREEN);
    }
    /**
     * Resizes panel based on changes
     * @param width Panel width
     * @param height Panel height
     */
    public void sizePanel2(int width, int height)
    {
        //Sets sizes and locations and adds to the tab
        p2Title.setLocation(((width)/2)-50,(height)/50);
        p2Title.setSize(150,20);
        panel2.add(p2Title);

        orTable.setSize(width*4/10,height*5/8);
        orTable.setLocation((width)/20,(height)*1/16);
        panel2.add(orTable);

        codeTable.setSize(width*4/10,height*5/8);
        codeTable.setLocation((width*11/20),(height/16));
        panel2.add(codeTable);

        openPDF.setSize(200,25);
        openPDF.setLocation(((width*1/4)-100),height*3/4);
        panel2.add(openPDF);

        openVideo.setSize(200,25);
        openVideo.setLocation(((width*1/2)-100),height*3/4);
        panel2.add(openVideo);

        openPPTX.setSize(200,25);
        openPPTX.setLocation(((width*3/4)-100),height*3/4);
        panel2.add(openPPTX);

        operationComplete.setSize(200,25);
        operationComplete.setLocation(((width*1/2)-100),height*53/64);
        panel2.add(operationComplete);
    }

    /**
     * Adds the completion panel including all the options such job completion, concessions, time taken, operators and files
     */
    public void completionPanel() {
        JPanel panelCompletion = new JPanel(null);
        panelCompletion.setPreferredSize(new Dimension(400,400));

        JLabel panelOptionTitle = new JLabel("Operation Complete?");
        String[] completeOptions = {"Yes","No","Yes with concessions"};

        JComboBox completeText = new JComboBox<>(completeOptions);
        JLabel concession = new JLabel("Concessions");
        JTextField concessionText = new JTextField();
        JLabel time = new JLabel("Time taken (HH:MM:SS)");
        JTextField timeText = new JTextField();
        JLabel operators = new JLabel("Select Operators");
        String[] columnNames = {"Operator ID", "Operator","Supported"};
        Object[][] operatorText = {{1,"Steve", false},{2,"Dave", false},{3,"Tasha", false},{4,"Simon", false}};
        JLabel fileUpload = new JLabel("Files");

        fileUploadButton.addActionListener(this);

        JTable operatorList = new JTable(new DefaultTableModel(operatorText, columnNames)
        {
            public boolean isCellEditable(int row, int column) {return column == 2;};
            public Class getColumnClass(int column) {
                //return Boolean.class
                return getValueAt(0, column).getClass();
            }
        });
        TableColumnModel columnModel = operatorList.getColumnModel();
        operatorList.getColumnModel().getColumn(2).setCellRenderer(new Requirements.CheckBoxRenderer());

        JScrollPane operatorTable = new JScrollPane(operatorList);

        panelCompletion.setLayout (new GridLayout (5, 2)); // 2 rows and 2 columns
        panelCompletion.add(panelOptionTitle);
        panelCompletion.add(completeText);
        panelCompletion.add(concession);
        panelCompletion.add(concessionText);
        panelCompletion.add(time);
        panelCompletion.add(timeText);
        panelCompletion.add(operators);
        panelCompletion.add(operatorTable);
        panelCompletion.add(fileUpload);
        panelCompletion.add(fileUploadButton);

        String[] options = {"OK", "Cancel"};
        int choice = JOptionPane.showOptionDialog (null, // parent component
                panelCompletion, // message
                "Completition Information", // title
                JOptionPane.DEFAULT_OPTION, // option type
                JOptionPane.QUESTION_MESSAGE, // message type
                null, // icon
                options, // options
                options[0]); // initial value
        if(choice == JOptionPane.YES_OPTION)
        {
            String[] optionOutput = {(String) completeText.getSelectedItem(), concessionText.getText(), timeText.getText()};
            String selectedOperators = "";
            for (int i = 0; i < operatorList.getRowCount(); i++) {
                String isThisTrue = operatorList.getValueAt(i, 2).toString();
                if (isThisTrue.equals("true")) {
                    selectedOperators = selectedOperators + (String) operatorList.getValueAt(i, 1) + "|";
                }
            }
            System.out.println(optionOutput[1]);
            writingClass.writeOperation(aircraftID,operationID, selectedOperators,optionOutput);
        }
    }

    /**
     * Method to show buttons when criteria are met
     */
    public void showOption()
    {
        if((number_of_operations == 1) && (number_of_skills == 1)) // if every tickbox is selected then the buttons show
        {
            openPDF.setVisible(true);
            openVideo.setVisible(true);
            openPPTX.setVisible(true);
            operationComplete.setVisible(true);
        }
        else
        {
            openPDF.setVisible(false);
            openVideo.setVisible(false);
            openPPTX.setVisible(false);
            operationComplete.setVisible(false);
        }
    }

    /**
     * Detects when event has happened and performs action. For this it is a button is selected and a file/panel is opened
     * @param event the event to be processed
     */
    public void actionPerformed(ActionEvent event)
    {
        if(event.getSource()==openPDF) //If button selected
        {
            String[] operationSplit = operationID.split("-");
            try {
                File pdfFile = new File("C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\Maintenance\\" + operationSplit[0] +"\\Instructions.pdf"); // Opens files
                if (pdfFile.exists())
                {
                    Desktop.getDesktop().open(pdfFile); // Goes to desktop and open files
                }
                else
                {
                    System.out.println("File does not exist!"); //Error catcher
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(event.getSource()==openVideo)
        {
            String[] operationSplit = operationID.split("-");
            try {
                File videoFile = new File("C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\Maintenance\\" + operationSplit[0] +"\\Instructions.webm");
                if (videoFile.exists())
                {
                    Desktop.getDesktop().open(videoFile);
                }
                else
                {
                    System.out.println("File does not exist!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(event.getSource()==openPPTX)
        {
            String[] operationSplit = operationID.split("-");
            try {
                File pptxFile = new File("C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\Maintenance\\" + operationSplit[0] +"\\Instructions.pptx");
                if (pptxFile.exists())
                {
                    Desktop.getDesktop().open(pptxFile);
                }
                else
                {
                    System.out.println("File does not exist!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(event.getSource()==operationComplete)
        {
            completionPanel();
        }
        if(event.getSource()==fileUploadButton)
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File folder = new File("C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\Aircraft\\"+aircraftID+"\\"+operationID);
                folder.mkdir();
                File selectedFile = fileChooser.getSelectedFile();
                Path sourcePath = selectedFile.toPath();
                System.out.println(selectedFile.getName());

                Path destinationPath = Path.of("C:\\Users\\James\\Documents\\University work\\GitHub\\JAMES\\readFiles\\Aircraft\\"+aircraftID+"\\"+operationID+"\\"+selectedFile.getName());
                try {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentShown(ComponentEvent e){}
    public void itemStateChanged(ItemEvent event) {}
    public void stateChanged(ChangeEvent changeEvent){}

    /**
     * Detects if the panel is resized and changes based on this
     * @param e the event to be processed
     */
    public void componentResized(ComponentEvent e)
    {
        //If it is resized then the pages are now resized
        int width = tabs.getWidth()+16;
        int height = tabs.getHeight()+38;
        panel1.removeAll();
        sizePanel1(width,height);
        panel2.removeAll();
        sizePanel2(width,height);
        revalidate();
        repaint();
    }
    public static void main(String[] args )
    {
        James gd = new James(); //Creates the code as an object and runs
        gd.startGUI();
    }
} 