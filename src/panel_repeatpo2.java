import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_repeatpo2 extends JPanel {
    JTable tbl_items;
    JLabel lbl_quantity;
    JTextField text_quantity;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ListSelectionModel listselectionmodel;
    DefaultTableModel tablemodel;
    JScrollPane sp;
    int quantity;
    double amount;

    public panel_repeatpo2(String poid) throws Exception {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        tablemodel = new DefaultTableModel();
        headers.add("Description");
        headers.add("Unit");
        headers.add("Unit Price");
        headers.add("Quantity");
        headers.add("Amount");

        dbconnect conn = new dbconnect();
        try
        {
            rset = conn.getPurchaseorderitems(poid);
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_items = new JTable(tablemodel);
        tbl_items.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_items);
        sp.setPreferredSize(new Dimension(800, 300));

        listselectionmodel = tbl_items.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_items.setSelectionModel(listselectionmodel);

        lbl_quantity = new JLabel("Quantity :");
        text_quantity = new JTextField(30);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.insets = new Insets(5,0,25,10);
        add(sp, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_START;
        add(sp, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("description"));
            record.add(rset1.getString("unit"));
            record.add(rset1.getString("unitprice"));
            record.add(0);
            record.add(0);

            data.addElement(record);
        }

        quantity = 0;
        amount = 0;

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }
}
