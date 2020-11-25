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
import java.sql.SQLException;
import java.util.Vector;

public class panel_manageusers extends JPanel {
    JLabel lbl_title, lbl_currentusers;
    public static JLabel lbl_status;
    JTable tbl_currentusers;
    JButton btn_add, btn_modify, btn_delete;
    Vector<String> headers = new Vector<String>();
    DefaultTableModel tablemodel;
    ResultSet rset;
    JScrollPane sp;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ListSelectionModel listselectionmodel;
    String buff_userid, buff_username, buff_fullname, buff_password, buff_accounttype;

    public panel_manageusers() throws Exception {
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();
        GridBagConstraints c = new GridBagConstraints();
        selectionHandler handler = new selectionHandler();

        lbl_title = new JLabel("User Maintenance");
        lbl_currentusers = new JLabel("Current Users");
        lbl_status = new JLabel();

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

        //5th row
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(25,0,0,10);
        add(lbl_status, c);

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
                dialog_updateuser si = null;
                try {
                    si = new dialog_updateuser(buff_username, buff_fullname, buff_password, buff_accounttype);
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

                btn_modify.setEnabled(false);
                btn_delete.setEnabled(false);
            }

            if (source == btn_delete)
            {
                int response = JOptionPane.showConfirmDialog(null, "Do you really want to delete user " + buff_username + "?", "Delete User", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.NO_OPTION) {

                } else if (response == JOptionPane.YES_OPTION) {
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

                    lbl_status.setText("Deleted user " + buff_username + " from database!");
                } else if (response == JOptionPane.CLOSED_OPTION) {
                    System.out.println("Closed window");
                }
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

                Object x, y, z, w;

                x = tbl_currentusers.getValueAt(maxIndex, 0);
                buff_userid = x.toString();

                y = tbl_currentusers.getValueAt(maxIndex, 1);
                buff_username = y.toString();

                z = tbl_currentusers.getValueAt(maxIndex, 2);
                buff_fullname = z.toString();

                w = tbl_currentusers.getValueAt(maxIndex, 3);
                buff_accounttype = w.toString();

                try {
                    int i = 0;

                    dbconnect conn4 = new dbconnect();
                    ResultSet rset1 = null;

                    rset1 = conn4.getUserlist();

                    while (rset1.next() && i <= maxIndex)
                    {
                        buff_password = rset1.getString("password");
                        i++;
                    }

                    conn4.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                System.out.println(buff_password);
                btn_delete.setEnabled(true);
                btn_modify.setEnabled(true);
            }
        }
    }
}
