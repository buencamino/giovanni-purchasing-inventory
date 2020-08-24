import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.Vector;


public class panel_suppliers extends JPanel {
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    String buff_supplierid;
    JTable tbl_suppliers;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    JLabel lbl_suppliername, lbl_title, lbl_list;
    JTextField text_suppliername;
    JButton btn_add, btn_delete;
    Vector<Vector<Object>> data;
    Vector<Object> record;


    public panel_suppliers() throws Exception {
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Supplier #");
        headers.add("Supplier Name");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getSupplierlist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_suppliers = new JTable(tablemodel);
        TableColumn a = tbl_suppliers.getColumnModel().getColumn(1);
        a.setPreferredWidth(600);
        tbl_suppliers.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_suppliers);
        sp.setPreferredSize(new Dimension(1000,300));

        listselectionmodel = tbl_suppliers.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_suppliers.setSelectionModel(listselectionmodel);

        lbl_suppliername = new JLabel("Supplier Name :");
        lbl_list = new JLabel("Supplier List");
        lbl_title = new JLabel("Input Supplier Details");

        text_suppliername = new JTextField(30);

        text_suppliername.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_suppliername.getText().length() >= 100 )
                    e.consume();
            }
        });


        btn_add = new JButton("Add Supplier");
        btn_delete = new JButton("Delete Supplier");
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
        add(lbl_suppliername, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_suppliername, c);

        //third row
        c.gridx = 2;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        add(btn_add, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_list, c);

        //5th row
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        add(sp, c);

        //6th row
        c.gridx = 2;
        c.gridy = 5;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_delete, c);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {

                if (!(text_suppliername.getText().equals("")))
                {
                    dbconnect conn = new dbconnect();

                    try {
                        conn.addSupplier(text_suppliername.getText());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    text_suppliername.setText("");

                    dbconnect conn2 = new dbconnect();

                    try
                    {
                        rset = conn2.getSupplierlist();
                        refreshTable(rset);
                        conn2.close();
                    }
                    catch (Exception j)
                    {
                        System.out.println(j.getMessage());
                    }

                    TableColumn a = tbl_suppliers.getColumnModel().getColumn(1);
                    a.setPreferredWidth(600);
                }

            }

            if (source == btn_delete)
            {
                dbconnect conn3 = new dbconnect();

                try {
                    conn3.deleteSupplier(buff_supplierid);
                    conn3.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dbconnect conn2 = new dbconnect();

                try
                {
                    rset = conn2.getSupplierlist();
                    refreshTable(rset);
                    conn2.close();
                }
                catch (Exception j)
                {
                    System.out.println(j.getMessage());
                }

                TableColumn a = tbl_suppliers.getColumnModel().getColumn(1);
                a.setPreferredWidth(600);

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

            record.add(rset1.getString("supplier_id"));
            record.add(rset1.getString("suppliername"));

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

                x = tbl_suppliers.getValueAt(maxIndex, 0);
                buff_supplierid = x.toString();
                btn_delete.setEnabled(true);
            }
        }
    }

}
