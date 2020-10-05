import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_viewcompletepo extends JPanel {
    JTable tbl_po;
    Vector<String> headers = new Vector<String>();
    DefaultTableModel tablemodel;
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ListSelectionModel listselectionmodel;
    JScrollPane sp, sp1;
    JLabel lbl_title, lbl_image;
    String buff_poid, terms;
    ImageIcon img_display;
    JButton btn_viewvoucher;

    public panel_viewcompletepo() throws Exception {
        setLayout(new GridBagLayout());

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();
        GridBagConstraints c = new GridBagConstraints();

        tablemodel = new DefaultTableModel();
        headers.add("PO #");
        headers.add("Prepared By");
        headers.add("Project Name");
        headers.add("Supplier");
        headers.add("Date");
        headers.add("Time");
        headers.add("Status");
        headers.add("terms");
        headers.add("Image");

        dbconnect conn = new dbconnect();
        try {
            rset = conn.getCompletepo();
            refreshTable(rset);
            conn.close();
        } catch (Exception j) {
            throw j;
        }

        tbl_po = new JTable(tablemodel);
        tbl_po.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_po);
        sp.setPreferredSize(new Dimension(800, 150));

        listselectionmodel = tbl_po.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_po.setSelectionModel(listselectionmodel);

        Border blackborder = BorderFactory.createLineBorder(Color.BLACK, 2);

        lbl_title = new JLabel("Please choose a completed purchase order below");

        img_display = new ImageIcon(new ImageIcon("src/berry.png").getImage().getScaledInstance(450,300, Image.SCALE_DEFAULT));
        lbl_image = new JLabel(img_display);
        //lbl_image.setPreferredSize(new Dimension(800,400));
        //lbl_image.setBorder(blackborder);
        sp1 = new JScrollPane(lbl_image);
        sp1.setPreferredSize(new Dimension(800,400));
        sp1.setBorder(blackborder);
        btn_viewvoucher = new JButton("View Voucher");
        btn_viewvoucher.setPreferredSize(new Dimension(800, 25));
        btn_viewvoucher.addActionListener(control);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(5, 0, 0, 25);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        add(sp, c);

        //3rd row
        c.gridx = 0;
        c.gridy = 2;
        add(sp1, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        add(btn_viewvoucher, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception {
        data = new Vector<Vector<Object>>();
        dbconnect conn = new dbconnect();
        String suppliername = null;

        while (rset1.next()) {
            record = new Vector<Object>();

            suppliername = conn.getSuppliername(rset1.getString("supplierchosen"), rset1.getString("canvass_id"));

            record.add(rset1.getString("purchaseorder_id"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("projectname"));
            record.add(suppliername);
            record.add(rset1.getString("newdate"));
            record.add(rset1.getString("timerecorded"));
            record.add("Complete");

            dbconnect conn2 = new dbconnect();

            terms = conn2.checkVouchertype(rset1.getString("purchaseorder_id"));
            conn2.close();

            record.add(terms);
            record.add(rset1.getString("imagepath"));

            data.addElement(record);
        }

        conn.close();
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

                Object x, y, z;
                String terms1 = null;

                x = tbl_po.getValueAt(maxIndex, 0);
                buff_poid = x.toString();

                y = tbl_po.getValueAt(maxIndex, 8);


                //ImageIcon img = new ImageIcon(new ImageIcon(y.toString()).getImage().getScaledInstance(450,300, Image.SCALE_DEFAULT));
                ImageIcon img = new ImageIcon(new ImageIcon(y.toString()).getImage());
                lbl_image.setIcon(img);
            }
        }
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btn_viewvoucher) {
                if (terms.equals("Check"))
                {
                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    try {
                        mainFrame.pnl_center.add(new panel_home_viewcheckvoucher(buff_poid, ""), BorderLayout.CENTER);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else if (terms.equals("Cash"))
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
