import net.sf.jasperreports.engine.export.Grid;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.Vector;
import java.sql.*;

public class panel_projects extends JPanel {
    JLabel lbl_projectname, lbl_location, lbl_available, lbl_title, lbl_ntpdate;
    JTextField text_projectname, text_location;
    JFormattedTextField text_ntpdate;
    JButton btn_add, btn_delete;
    JTable tbl_projects;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ResultSet rset;
    String buff_projectid;

    public panel_projects() throws Exception {
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Project #");
        headers.add("Project Name");
        headers.add("Location");
        headers.add("NTP Date");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getProjectlist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_projects = new JTable(tablemodel);
        TableColumn a = tbl_projects.getColumnModel().getColumn(1);
        a.setPreferredWidth(500);
        tbl_projects.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_projects);
        sp.setPreferredSize(new Dimension(1000,300));

        listselectionmodel = tbl_projects.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_projects.setSelectionModel(listselectionmodel);

        lbl_projectname = new JLabel("Project Name :");
        lbl_location = new JLabel("Location :");
        lbl_available = new JLabel("Available Projects");
        lbl_title = new JLabel("Input Project Details");
        lbl_ntpdate = new JLabel("Project NTP Date :");

        text_projectname = new JTextField(30);
        text_location = new JTextField(30);

        MaskFormatter mask = null;
        try
        {
            mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');

        }
        catch (ParseException k)
        {
            k.printStackTrace();
        }

        text_ntpdate = new JFormattedTextField(mask);
        text_ntpdate.setPreferredSize(new Dimension(110, 25));

        text_projectname.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_projectname.getText().length() >= 100 )
                    e.consume();
            }
        });

        text_location.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_location.getText().length() >= 100 )
                    e.consume();
            }
        });

        btn_add = new JButton("Add Project");
        btn_delete = new JButton("Delete Project");
        btn_delete.setEnabled(false);

        btn_add.addActionListener(control);
        btn_delete.addActionListener(control);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 1;
        c.gridy = 0;
        add(lbl_title, c);

        //second row
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_projectname, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_projectname, c);

        //third row
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_location, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_location, c);

        //fourth row
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_ntpdate, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_ntpdate, c);

        //fifth row
        c.gridx = 2;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        add(btn_add, c);

        //6th row
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_available, c);

        //7th row
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 3;
        add(sp, c);

        //8th row
        c.gridx = 2;
        c.gridy = 7;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_delete, c);

    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {

                if (!(text_projectname.getText().equals("") || text_location.getText().equals("") || text_ntpdate.getText().equals("__/__/____")))
                {
                    dbconnect conn = new dbconnect();

                    try {
                        conn.addProject(text_projectname.getText(), text_location.getText(), text_ntpdate.getText());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    text_projectname.setText("");
                    text_location.setText("");
                    text_ntpdate.setText("");

                    dbconnect conn2 = new dbconnect();

                    try
                    {
                        rset = conn2.getProjectlist();
                        refreshTable(rset);
                        conn2.close();
                    }
                    catch (Exception j)
                    {
                        System.out.println(j.getMessage());
                    }

                    TableColumn a = tbl_projects.getColumnModel().getColumn(1);
                    a.setPreferredWidth(500);
                }

            }

            if (source == btn_delete)
            {
                dbconnect conn3 = new dbconnect();

                try {
                    conn3.deleteProject(buff_projectid);
                    conn3.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dbconnect conn2 = new dbconnect();

                try
                {
                    rset = conn2.getProjectlist();
                    refreshTable(rset);
                    conn2.close();
                }
                catch (Exception j)
                {
                    System.out.println(j.getMessage());
                }

                TableColumn a = tbl_projects.getColumnModel().getColumn(1);
                a.setPreferredWidth(500);

                btn_delete.setEnabled(false);
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("project_id"));
            record.add(rset1.getString("project_name"));
            record.add(rset1.getString("location"));
            record.add(rset1.getDate("ntpdate").toString());

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class selectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if(lsm.isSelectionEmpty())
            {
                System.out.println("nothing selected");
            }
            else
            {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                Object x;

                x = tbl_projects.getValueAt(maxIndex, 0);
                buff_projectid = x.toString();
                btn_delete.setEnabled(true);
            }
        }
    }
}
