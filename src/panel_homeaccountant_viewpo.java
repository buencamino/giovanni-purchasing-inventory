import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class panel_homeaccountant_viewpo extends JPanel {
    JLabel lbl_title, lbl_bankname;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset, rsetbanks;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JTable tbl_records;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    JButton btn_generatevoucher, btn_generate, btn_updatebank;
    String buff_poid, selectedterms;
    JPanel pnl_bankdetails;
    JComboBox combo_bankname;

    public panel_homeaccountant_viewpo() throws Exception {
        setLayout(new GridBagLayout());

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();
        GridBagConstraints c = new GridBagConstraints();

        pnl_bankdetails = new JPanel(new GridBagLayout());
        lbl_bankname = new JLabel("Bank Account Name :");
        btn_updatebank = new JButton("Update Bank Info");
        btn_updatebank.addActionListener(control);

        combo_bankname = new JComboBox();

        dbconnect conn2 = new dbconnect();

        rsetbanks = conn2.getBanklist();

        while (rsetbanks.next())
        {
            combo_bankname.addItem(rsetbanks.getString("bankname") + " " + rsetbanks.getString("accountnum"));
        }

        combo_bankname.setSelectedIndex(-1);

        Border border_bankdetails = BorderFactory.createTitledBorder("Enter Bank Details");
        pnl_bankdetails.setBorder(border_bankdetails);
        pnl_bankdetails.setPreferredSize(new Dimension(400,200));

        //bank details panel
        //1st row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 0, 25);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_bankdetails.add(lbl_bankname, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_bankdetails.add(combo_bankname, c);

        //2nd row
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_bankdetails.add(btn_updatebank, c);

        pnl_bankdetails.setVisible(false);


        lbl_title = new JLabel("Approved Purchase Orders");

        btn_generatevoucher = new JButton("Generate Voucher");

        btn_generatevoucher.setEnabled(false);
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
        headers.add("Terms");
        headers.add("Bank Chosen");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getPurchaseorder();
            refreshTable(rset);
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

        c.gridx = 1;
        c.gridheight = 3;
        add(pnl_bankdetails, c);

        //4th row
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        add(btn_generatevoucher, c);
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

            dbconnect conn = new dbconnect();
            String terms = null;

            terms = conn.checkVouchertype(rset1.getString("purchaseorder_id"));
            conn.close();

            record.add(terms);

            if (rset.getInt("bankchosen") != 0)
            {
                String bankname = null, bankid;

                bankid = rset.getString("bankchosen");

                rsetbanks.first();

                do{
                    if (bankid.equals(rsetbanks.getString("bank_id")))
                    {
                        bankname = rsetbanks.getString("bankname");
                    }
                }while (rsetbanks.next());

                record.add(bankname);
            }
            else if (rset.getInt("bankchosen") == 0)
            {
                record.add("None");
            }

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

                Object x, y, z;
                String terms1 = null;

                x = tbl_records.getValueAt(maxIndex, 0);
                buff_poid = x.toString();
                btn_generate.setEnabled(true);
                btn_generatevoucher.setEnabled(true);

                y = tbl_records.getValueAt(maxIndex, 5);
                terms1 = y.toString();

                if (terms1.equals("Check"))
                {
                    pnl_bankdetails.setVisible(true);
                    selectedterms = terms1;
                }
                else if (terms1.equals("Cash"))
                {
                    pnl_bankdetails.setVisible(false);
                    selectedterms = terms1;
                }
            }
        }
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_generate) {

            }

            if (source == btn_updatebank) {
                String bankid = null;
                int selectedbank;

                dbconnect conn = new dbconnect();

                selectedbank = combo_bankname.getSelectedIndex();
                int count = 0;

                try {
                    rsetbanks.first();
                    do
                    {
                        if (selectedbank == count){
                            bankid = rsetbanks.getString("bank_id");
                            break;
                        }

                        count++;
                    } while (rsetbanks.next());

                    conn.updatePobank(bankid, buff_poid);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
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
                    j.printStackTrace();
                }
            }

            if (source == btn_generatevoucher) {
                if (selectedterms.equals("Check"))
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    try {
                        mainFrame.pnl_center.add(new panel_home_viewcheckvoucher(buff_poid), BorderLayout.CENTER);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else if (selectedterms.equals("Cash"))
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
