import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_bank extends JPanel {
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset, rsetbanks;
    JTable tbl_banks;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JTextField text_accountname, text_accountnum, text_bankname;
    JLabel lbl_accountname, lbl_accountnum, lbl_list, lbl_title, lbl_bankname;
    JButton btn_add, btn_update;
    String buff_bankid;

    public panel_bank() throws Exception {
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Bank #");
        headers.add("Bank Name");
        headers.add("Account Name");
        headers.add("Account Num");

        dbconnect conn = new dbconnect();

        try {
            rset = conn.getBanklist();
            refreshTable(rset);
            conn.close();
        } catch (Exception j) {
            throw j;
        }

        tbl_banks = new JTable(tablemodel);
        tbl_banks.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_banks);
        sp.setPreferredSize(new Dimension(1000, 300));

        listselectionmodel = tbl_banks.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_banks.setSelectionModel(listselectionmodel);

        lbl_list = new JLabel("Company Bank List");
        lbl_title = new JLabel("Input Company Bank Details");
        lbl_accountname = new JLabel("Account Name :");
        lbl_accountnum = new JLabel("Account Number :");
        lbl_bankname = new JLabel("Bank Name :");

        text_accountname = new JTextField(40);
        text_accountnum = new JTextField(30);
        text_bankname = new JTextField(40);

        text_accountname.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_accountname.getText().length() >= 100)
                    e.consume();
            }
        });

        text_accountnum.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_accountnum.getText().length() >= 100)
                    e.consume();
            }
        });

        text_bankname.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_bankname.getText().length() >= 100)
                    e.consume();
            }
        });

        btn_add = new JButton("Add Bank Details");
        btn_update = new JButton("Update Bank");
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
        add(lbl_bankname, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_bankname, c);

        //third row
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_accountname, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_accountname, c);

        //fourth row
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_accountnum, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_accountnum, c);

        //5th row
        c.gridx = 2;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        add(btn_add, c);

        //6th row
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

            record.add(rset1.getString("bank_id"));
            record.add(rset1.getString("bankname"));
            record.add(rset1.getString("accountname"));
            record.add(rset1.getString("accountnum"));

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class selectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if (lsm.isSelectionEmpty()) {
                System.out.println("nothing selected");
            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                Object x;

                x = tbl_banks.getValueAt(maxIndex, 0);
                buff_bankid = x.toString();
                btn_update.setEnabled(true);
            }
        }
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {

                if (!(text_bankname.getText().equals("") && text_accountname.getText().equals("") && text_accountnum.getText().equals(""))) {
                    dbconnect conn = new dbconnect();

                    int i = 0;

                    try {
                        rsetbanks = conn.checkDuplicatebank(text_bankname.getText(), text_accountname.getText(), text_accountnum.getText());
                        while (rsetbanks.next())
                            i++;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    if (i == 0)
                    {
                        try {
                            conn.addBank(text_bankname.getText(), text_accountname.getText(), text_accountnum.getText());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        text_bankname.setText("");
                        text_accountnum.setText("");
                        text_accountname.setText("");

                        dbconnect conn2 = new dbconnect();

                        try {
                            rset = conn2.getBanklist();
                            refreshTable(rset);
                            conn2.close();
                        } catch (Exception j) {
                            System.out.println(j.getMessage());
                        }
                    } else if (i > 0)
                    {
                        JOptionPane.showMessageDialog(this, "Bank and information already exists!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
