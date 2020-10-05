import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_enduser extends JPanel{
    JTable tbl_enduser;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset, rsetenduser;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JLabel lbl_list, lbl_title, lbl_enduser;
    JTextField text_enduser;
    JButton btn_add, btn_update;

    public panel_enduser() throws Exception {
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();
        //selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("End User #");
        headers.add("End User Name");

        dbconnect conn = new dbconnect();

        try {
            rset = conn.getEnduserlist();
            refreshTable(rset);
            conn.close();
        } catch (Exception j) {
            throw j;
        }

        tbl_enduser = new JTable(tablemodel);
        tbl_enduser.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_enduser);
        sp.setPreferredSize(new Dimension(1000, 300));

        TableColumn a = tbl_enduser.getColumnModel().getColumn(1);
        a.setPreferredWidth(600);

        /*
        listselectionmodel = tbl_checker.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_checker.setSelectionModel(listselectionmodel);
*/

        lbl_list = new JLabel("End User List");
        lbl_title = new JLabel("Input End User Details");
        lbl_enduser = new JLabel("End User Name :");

        text_enduser = new JTextField(40);

        text_enduser.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_enduser.getText().length() >= 100)
                    e.consume();
            }
        });

        btn_add = new JButton("Add Watchman/Checker");
        btn_update = new JButton("Update Watchman/Checker");
        btn_update.setEnabled(false);

        btn_add.addActionListener(control);
        btn_update.addActionListener(control);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 1;
        c.gridy = 0;
        add(lbl_title, c);

        //second row
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_enduser, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_enduser, c);

        //3rd row
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(btn_add, c);

        //4th row
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_list, c);

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
        add(btn_update, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception {
        data = new Vector<Vector<Object>>();

        while (rset1.next()) {
            record = new Vector<Object>();

            record.add(rset1.getString("enduser_id"));
            record.add(rset1.getString("endusername"));

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {

                if (!(text_enduser.getText().equals(""))) {
                    dbconnect conn = new dbconnect();

                    int i = 0;

                    try {
                        rsetenduser = conn.checkDuplicateenduser(text_enduser.getText());
                        while (rsetenduser.next())
                            i++;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    if (i == 0)
                    {
                        try {
                            conn.addEnduser(text_enduser.getText());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        text_enduser.setText("");

                        dbconnect conn2 = new dbconnect();

                        try {
                            rset = conn2.getEnduserlist();
                            refreshTable(rset);
                            conn2.close();
                        } catch (Exception j) {
                            System.out.println(j.getMessage());
                        }

                        TableColumn a = tbl_enduser.getColumnModel().getColumn(1);
                        a.setPreferredWidth(600);
                    } else if (i > 0)
                    {
                        JOptionPane.showMessageDialog(this, "End user already exists!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
