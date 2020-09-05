import net.sf.jasperreports.engine.JRException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_home_viewpo extends JPanel {
    JLabel lbl_title;
    JButton btn_generate1, btn_generate2, btn_generate3, btn_approved, btn_cancelled, btn_generate, btn_generatevoucher;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JTable tbl_records;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    String buff_poid;
    ResultSet rsetpurchaseitems;

    public panel_home_viewpo() throws Exception {
        setLayout(new GridBagLayout());

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        lbl_title = new JLabel("Available Purchase Orders");
        /*
        btn_generate1 = new JButton("Generate PO Supplier 1");
        btn_generate2 = new JButton("Generate PO Supplier 2");
        btn_generate3 = new JButton("Generate PO Supplier 3");
        */

        btn_approved = new JButton("Approved by Management");
        btn_cancelled = new JButton("Cancelled/Disapproved");
        btn_generatevoucher = new JButton("Generate Voucher");
        /*
        btn_generate1.setEnabled(false);
        btn_generate2.setEnabled(false);
        btn_generate3.setEnabled(false);
        */

        btn_approved.setEnabled(false);
        btn_cancelled.setEnabled(false);
        btn_generatevoucher.setEnabled(false);
        /*
        btn_generate1.addActionListener(control);

        btn_generate2.addActionListener(control);
        btn_generate3.addActionListener(control);
        */
        btn_approved.addActionListener(control);
        btn_cancelled.addActionListener(control);
        btn_generate = new JButton("Generate PO");
        btn_generate.setEnabled(false);
        btn_generate.addActionListener(control);
        btn_generatevoucher.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("PO #");
        headers.add("Prepared By");
        headers.add("Date");
        headers.add("Project Name");
        headers.add("Status");

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

        tbl_records = new JTable(tablemodel);
        tbl_records.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_records);
        sp.setPreferredSize(new Dimension(800,300));

        listselectionmodel = tbl_records.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_records.setSelectionModel(listselectionmodel);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 0, 25);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        add(sp, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(btn_generate, c);

        /*
        c.gridx = 1;
        add(btn_generate2, c);

        c.gridx = 2;
        add(btn_generate3, c);
*/

        //fourth row
        c.gridx = 0;
        c.gridy = 3;
        add(btn_approved, c);

        c.gridx = 1;
        add(btn_cancelled, c);

        //5th row
        c.gridx = 0;
        c.gridy = 4;
        add(btn_generatevoucher, c);

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

                Object x, y;

                x = tbl_records.getValueAt(maxIndex, 0);
                buff_poid = x.toString();
                btn_generate.setEnabled(true);
                //btn_generate2.setEnabled(true);
                //btn_generate3.setEnabled(true);
                btn_approved.setEnabled(true);

                y = tbl_records.getValueAt(maxIndex, 4);
                if (y.toString().equals("Pending"))
                    btn_generatevoucher.setEnabled(false);
                else if (y.toString().equals("Approved"))
                    btn_generatevoucher.setEnabled(true);
                else if (y.toString().equals("Cancelled"))
                    btn_generatevoucher.setEnabled(false);

            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("purchaseorder_id"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("date"));
            record.add(rset1.getString("projectname"));
            if (rset1.getInt("approved") == 0)
                record.add("Pending");
            else if (rset1.getInt("approved") == 1)
                record.add("Approved");
            else if (rset1.getInt("approved") == 2)
                record.add("Cancelled");

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
/*
            if (source == btn_generate1) {
                dbconnect conn2 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn2.getPurchaseorderitems(buff_poid, "1");

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    try {
                        mainFrame.pnl_center.add(new panel_home_viewpo1(buff_poid, "1"), BorderLayout.CENTER);
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 1st supplier for this purchase order!", "No 1st Supplier", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (source == btn_generate2) {

                dbconnect conn2 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn2.getPurchaseorderitems(buff_poid, "2");

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    try {
                        mainFrame.pnl_center.add(new panel_home_viewpo1(buff_poid, "2"), BorderLayout.CENTER);
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 2nd supplier for this purchase order!", "No 2nd Supplier", JOptionPane.ERROR_MESSAGE);
                }

            }

            if (source == btn_generate3) {
                dbconnect conn2 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn2.getPurchaseorderitems(buff_poid, "3");

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    try {
                        mainFrame.pnl_center.add(new panel_home_viewpo1(buff_poid, "3"), BorderLayout.CENTER);
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 3rd supplier for this purchase order!", "No 3rd Supplier", JOptionPane.ERROR_MESSAGE);
                }
            }
            */

            if (source == btn_generate)
            {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_home_viewpo1(buff_poid), BorderLayout.CENTER);
                } catch (JRException jrException) {
                    jrException.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }

            if (source == btn_approved)
            {
                dbconnect conn = new dbconnect();

                try {
                    conn.setApprovepo(buff_poid);
                    conn.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dbconnect conn2 = new dbconnect();

                try
                {
                    rset = conn2.getPurchaseorder();
                    refreshTable(rset);
                    conn2.close();
                }
                catch (Exception j)
                {
                    System.out.println(j.getMessage());
                }

                btn_generatevoucher.setEnabled(true);
            }

            if (source == btn_generatevoucher)
            {
                dbconnect conn = new dbconnect();
                String type = null;

                try {
                    type = conn.checkVouchertype(buff_poid);
                    conn.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (type.equals("Check"))
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    mainFrame.pnl_center.add(new panel_home_viewcheckvoucher(buff_poid), BorderLayout.CENTER);

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else if (type.equals("Cash"))
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    mainFrame.pnl_center.add(new panel_home_viewcashvoucher(buff_poid), BorderLayout.CENTER);

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
            }
        }
    }
}
