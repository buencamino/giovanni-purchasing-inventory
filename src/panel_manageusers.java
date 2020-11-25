import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_manageusers extends JPanel {
    JLabel lbl_title, lbl_currentusers;
    JTable tbl_currentusers;
    JButton btn_add, btn_modify, btn_delete;
    Vector<String> headers = new Vector<String>();
    DefaultTableModel tablemodel;
    ResultSet rset;
    JScrollPane sp;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ListSelectionModel listselectionmodel;
    String buff_userid;

    public panel_manageusers() throws Exception {
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();
        GridBagConstraints c = new GridBagConstraints();
        selectionHandler handler = new selectionHandler();

        lbl_title = new JLabel("User Maintenance");
        lbl_currentusers = new JLabel("Current Users");

        btn_add = new JButton("Add User");
        btn_modify = new JButton("Modify User");
        btn_delete = new JButton("Delete User");

        btn_modify.setEnabled(false);
        btn_delete.setEnabled(false);

        btn_add.setPreferredSize(new Dimension(300, 25));
        btn_modify.setPreferredSize(new Dimension(300, 25));
        btn_delete.setPreferredSize(new Dimension(300, 25));

        btn_add.addActionListener(control);
        btn_modify.addActionListener(control);
        btn_delete.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("ID #");
        headers.add("Username");
        headers.add("Full Name");
        headers.add("User Level");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getUserlist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_currentusers = new JTable(tablemodel);
        tbl_currentusers.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_currentusers);
        sp.setPreferredSize(new Dimension(1000,225));

        listselectionmodel = tbl_currentusers.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_currentusers.setSelectionModel(listselectionmodel);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,30,10);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5,0,0,10);
        add(lbl_currentusers, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        add(sp, c);
        c.insets = new Insets(5,0,20,10);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.insets = new Insets(5,0,0,40);
        add(btn_add, c);

        c.gridx = 1;
        add(btn_modify, c);

        c.gridx = 2;
        add(btn_delete, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("userid"));
            record.add(rset1.getString("username"));
            record.add(rset1.getString("fullname"));

            if (rset1.getString("level").equals("1"))
                record.add("Engineer/Purchaser");
            else if (rset1.getString("level").equals("2"))
                record.add("Accountant");
            else if (rset1.getString("level").equals("3"))
                record.add("Project Manager");

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {
                dialog_adduser si = null;
                try {
                    si = new dialog_adduser();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                si.setSize(new Dimension(450,350));
                si.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        dbconnect conn2 = new dbconnect();

                        try
                        {
                            rset = conn2.getUserlist();
                            refreshTable(rset);
                            conn2.close();
                        }
                        catch (Exception j)
                        {
                            System.out.println(j.getMessage());
                        }
                    }
                });

                si.setLocationRelativeTo(null);
                si.setModal(true);
                si.setVisible(true);
            }

            if (source == btn_modify) {


            }

            if (source == btn_delete)
            {
                dbconnect conn3 = new dbconnect();

                try {
                    conn3.deleteUser(buff_userid);
                    conn3.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dbconnect conn2 = new dbconnect();

                try
                {
                    rset = conn2.getUserlist();
                    refreshTable(rset);
                    conn2.close();
                }
                catch (Exception j)
                {
                    System.out.println(j.getMessage());
                }

                btn_delete.setEnabled(false);
                btn_modify.setEnabled(false);
            }
        }
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

                x = tbl_currentusers.getValueAt(maxIndex, 0);
                buff_userid = x.toString();
                btn_delete.setEnabled(true);
                btn_modify.setEnabled(true);
            }
        }
    }
}
