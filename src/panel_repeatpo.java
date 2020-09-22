import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_repeatpo extends JPanel {
    JTable tbl_po;
    JLabel lbl_items, lbl_title;
    JScrollPane sp;
    JButton btn_next;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ListSelectionModel listselectionmodel;
    String buff_poid, suppliername;

    public panel_repeatpo() throws Exception {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        lbl_title = new JLabel("Please choose which PO you want to copy");
        lbl_items = new JLabel("Items in chosen Purchase Order will be displayed here.");

        btn_next = new JButton("Next");
        btn_next.setPreferredSize(new Dimension(400, 30));
        btn_next.setEnabled(false);
        btn_next.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("PO #");
        headers.add("Prepared By");
        headers.add("Project Name");
        headers.add("Supplier");
        headers.add("Date");
        headers.add("Time");

        dbconnect conn = new dbconnect();
        try
        {
            rset = conn.getPurchaseorder();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_po = new JTable(tablemodel);
        tbl_po.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_po);
        sp.setPreferredSize(new Dimension(800, 300));

        listselectionmodel = tbl_po.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_po.setSelectionModel(listselectionmodel);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_START;
        add(sp, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        add(lbl_items, c);

        //fourth row
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(15,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_next, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();
        dbconnect conn = new dbconnect();
        String suppliername = null;

        while(rset1.next())
        {
            record = new Vector<Object>();

            suppliername = conn.getSuppliername(rset1.getString("supplierchosen"), rset1.getString("canvass_id"));

            record.add(rset1.getString("purchaseorder_id"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("projectname"));
            record.add(suppliername);
            record.add(rset1.getString("newdate"));
            record.add(rset1.getString("timerecorded"));

            data.addElement(record);
        }

        conn.close();
        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_next) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_repeatpo2(buff_poid, suppliername), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }

    class selectionHandler implements ListSelectionListener
    {
        ResultSet rsetitems = null;

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

                Object x, y;

                x = tbl_po.getValueAt(maxIndex, 0);
                buff_poid = x.toString();
                btn_next.setEnabled(true);

                y = tbl_po.getValueAt(maxIndex, 3);
                suppliername = y.toString();

                lbl_items.setText("Items included : ");

                dbconnect conn = new dbconnect();

                try {
                    rsetitems = conn.getPurchaseorderitems(buff_poid);

                    while (rsetitems.next())
                    {
                        lbl_items.setText(lbl_items.getText() + rsetitems.getString("description") + " | ");
                    }
                    conn.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
