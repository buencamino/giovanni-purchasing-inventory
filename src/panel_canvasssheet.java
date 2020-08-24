import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Vector;

public class panel_canvasssheet extends JPanel {
    JPanel pnl_entry1;
    JLabel lbl_titlereq, lbl_titlesupplier, lbl_supplier1, lbl_supplier2, lbl_supplier3, lbl_selectedreq,
    lbl_terms1, lbl_terms2, lbl_terms3, lbl_recommendations;
    JScrollPane sp;
    JTable tbl_requisitionlist;
    JButton btn_next;
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    Vector<String> headers = new Vector<String>();
    DefaultTableModel tablemodel;
    ListSelectionModel listselectionmodel;
    String buff_reqnum;
    JComboBox combo_terms1, combo_terms2, combo_terms3, combo_supplier1, combo_supplier2, combo_supplier3,
    combo_recommendations;

    public panel_canvasssheet() throws Exception {
        setLayout(new BorderLayout());

        buff_reqnum = "-1";

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        String[] terms = {"Cash", "Check"};
        combo_terms1 = new JComboBox(terms);
        combo_terms2 = new JComboBox(terms);
        combo_terms3 = new JComboBox(terms);

        String[] reco = {"For refund to FGCR", "For refund to OCR II", "For purchase", "For canvass only"};
        combo_recommendations = new JComboBox(reco);

        dbconnect conn2 = new dbconnect();

        rset = conn2.getSupplierlist();
        combo_supplier1 = new JComboBox();
        combo_supplier2 = new JComboBox();
        combo_supplier3 = new JComboBox();

        while (rset.next())
        {
            combo_supplier1.addItem(rset.getString("suppliername"));
            combo_supplier2.addItem(rset.getString("suppliername"));
            combo_supplier3.addItem(rset.getString("suppliername"));
        }

        combo_supplier1.setSelectedIndex(-1);
        combo_supplier2.setSelectedIndex(-1);
        combo_supplier3.setSelectedIndex(-1);

        lbl_terms1 = new JLabel("Terms :");
        lbl_terms2 = new JLabel("Terms :");
        lbl_terms3 = new JLabel("Terms :");
        lbl_titlereq = new JLabel("Please choose a requisition slip below");
        pnl_entry1 = new JPanel(new GridBagLayout());
        lbl_titlesupplier = new JLabel("Choose suppliers here");
        lbl_supplier1 = new JLabel("Supplier Name 1 :");
        lbl_supplier2 = new JLabel("Supplier Name 2 :");
        lbl_supplier3 = new JLabel("Supplier Name 3 :");
        lbl_selectedreq = new JLabel("No requisition slip selected yet");
        btn_next = new JButton("Next");
        btn_next.setEnabled(false);
        lbl_recommendations = new JLabel("Recommendations :");

        btn_next.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("RS #");
        headers.add("Project Name");
        headers.add("Location");
        headers.add("Prepared By");
        headers.add("Purpose");
        headers.add("Date");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getPendingreqlist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_requisitionlist = new JTable(tablemodel);
        tbl_requisitionlist.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_requisitionlist);
        tbl_requisitionlist.setDefaultEditor(Object.class, null);
        sp.setPreferredSize(new Dimension(800, 200));

        listselectionmodel = tbl_requisitionlist.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_requisitionlist.setSelectionModel(listselectionmodel);

        GridBagConstraints c = new GridBagConstraints();

        //pnl_entry1 elements
        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(lbl_titlereq, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 5;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(sp, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        c.insets = new Insets(5, 0, 50, 10);
        pnl_entry1.add(lbl_selectedreq, c);

        //fourth row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_titlesupplier, c);

        //5th row
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_supplier1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_supplier1, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_terms1, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_terms1, c);

        //6th row
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_supplier2, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_supplier2, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_terms2, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_terms2, c);

        //7th row
        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_supplier3, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_supplier3, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_terms3, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_terms3, c);

        //8th row
        c.gridy = 7;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry1.add(lbl_recommendations, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(combo_recommendations, c);

        //9th row;
        c.gridx = 3;
        c.gridy = 8;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(btn_next, c);

        add(pnl_entry1, BorderLayout.CENTER);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_next) {
                String supplier1 = null, supplier2 = null, supplier3 = null;

                if (combo_supplier1.getSelectedItem() == null)
                    supplier1 = "";
                else
                    supplier1 = combo_supplier1.getSelectedItem().toString();

                if (combo_supplier2.getSelectedItem() == null)
                    supplier2 = "";
                else
                    supplier2 = combo_supplier2.getSelectedItem().toString();

                if (combo_supplier3.getSelectedItem() == null)
                    supplier3 = "";
                else
                    supplier3 = combo_supplier3.getSelectedItem().toString();

                if (!(supplier1.equals("") && supplier2.equals("") && supplier3.equals("")))
                {
                    try {
                        removeAll();
                        repaint();
                        revalidate();

                        add(new panel_canvasssheet2(buff_reqnum, supplier1, supplier2, supplier3, combo_terms1.getSelectedItem().toString(), combo_terms2.getSelectedItem().toString(), combo_terms3.getSelectedItem().toString(), combo_recommendations.getSelectedItem().toString()), BorderLayout.CENTER);

                        repaint();
                        revalidate();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(pnl_entry1, "Error, please enter at least one supplier!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("requisition_num"));
            record.add(rset1.getString("project"));
            record.add(rset1.getString("location"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("purpose"));
            record.add(rset1.getDate("date").toString());

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

                String reqnum, project, preparedby, date;

                Object x, y, z, w;

                x = tbl_requisitionlist.getValueAt(maxIndex, 0);
                reqnum = x.toString();

                y = tbl_requisitionlist.getValueAt(maxIndex, 1);
                project = y.toString();

                z = tbl_requisitionlist.getValueAt(maxIndex, 3);
                preparedby = z.toString();

                w = tbl_requisitionlist.getValueAt(maxIndex, 5);
                date = w.toString();

                buff_reqnum = reqnum;
                btn_next.setEnabled(true);
                lbl_selectedreq.setText("Selected requisition #" + reqnum + " for project " + project + " prepared by " + preparedby + " on " + date);
            }
        }
    }
}