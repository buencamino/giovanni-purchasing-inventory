import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Vector;
import java.sql.*;

public class panel_purchaseorder2 extends JPanel {
    JRadioButton radio_supplier1, radio_supplier2, radio_supplier3;
    JTable tbl_items;
    JPanel pnl_entry, pnl_supplier1, pnl_supplier2, pnl_supplier3;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    Vector<Object> record;
    Vector<Vector<Object>> data;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    JLabel lbl_title, lbl_selecteditem, lbl_quantity1, lbl_unit1, lbl_amount1,
    lbl_quantity1a, lbl_unit1a, lbl_amount1a,
    lbl_quantity2, lbl_unit2, lbl_amount2,
    lbl_quantity2a, lbl_unit2a, lbl_amount2a,
    lbl_quantity3, lbl_unit3, lbl_amount3,
    lbl_quantity3a, lbl_unit3a, lbl_amount3a, lbl_supplierchosen;
    JButton btn_update, btn_submit;
    //int i;
    String suppliera, supplierb, supplierc, canvassid, datenow;
    int supplierchosen;

    public panel_purchaseorder2(String canvassnum, String supplier1, String supplier2, String supplier3) throws Exception {
        setLayout(new BorderLayout());

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        java.util.Date date = new Date();
        datenow = formatter.format(date);

        canvassid = canvassnum;
        suppliera = supplier1;
        supplierb = supplier2;
        supplierc = supplier3;

        HandleControlButton control = new HandleControlButton();

        pnl_entry = new JPanel(new GridBagLayout());
        pnl_supplier1 = new JPanel(new GridBagLayout());
        pnl_supplier2 = new JPanel(new GridBagLayout());
        pnl_supplier3 = new JPanel(new GridBagLayout());
        lbl_title = new JLabel("Please choose an item/product below to pick supplier");
        lbl_selecteditem = new JLabel("No item selected");
        btn_update = new JButton("Update Item");
        btn_update.addActionListener(control);
        btn_submit = new JButton("Confirm Purchase Order");
        btn_submit.addActionListener(control);
        btn_submit.setEnabled(false);

        radio_supplier1 = new JRadioButton("Choose Supplier 1");
        radio_supplier2 = new JRadioButton("Choose Supplier 2");
        radio_supplier3 = new JRadioButton("Choose Supplier 3");
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(radio_supplier1);
        bgroup.add(radio_supplier2);
        bgroup.add(radio_supplier3);
        /*
        radio_supplier1.setEnabled(false);
        radio_supplier2.setEnabled(false);
        radio_supplier3.setEnabled(false);
        btn_update.setEnabled(false);
        */

        lbl_quantity1 = new JLabel("Quantity :");
        lbl_unit1 = new JLabel("Unit Price :");
        lbl_amount1 = new JLabel("Amount :");
        lbl_quantity1a = new JLabel("0.0");
        lbl_unit1a = new JLabel("0.0");
        lbl_amount1a = new JLabel("0.0");

        lbl_quantity2 = new JLabel("Quantity :");
        lbl_unit2 = new JLabel("Unit Price :");
        lbl_amount2 = new JLabel("Amount :");
        lbl_quantity2a = new JLabel("0.0");
        lbl_unit2a = new JLabel("0.0");
        lbl_amount2a = new JLabel("0.0");

        lbl_quantity3 = new JLabel("Quantity :");
        lbl_unit3 = new JLabel("Unit Price :");
        lbl_amount3 = new JLabel("Amount :");
        lbl_quantity3a = new JLabel("0.0");
        lbl_unit3a = new JLabel("0.0");
        lbl_amount3a = new JLabel("0.0");

        lbl_supplierchosen = new JLabel();

        GridBagConstraints c = new GridBagConstraints();

        Border border_supplier1 = BorderFactory.createTitledBorder(supplier1);
        pnl_supplier1.setBorder(border_supplier1);
        pnl_supplier1.setPreferredSize(new Dimension(300,200));

        Border border_supplier2 = BorderFactory.createTitledBorder(supplier2);
        pnl_supplier2.setBorder(border_supplier2);
        pnl_supplier2.setPreferredSize(new Dimension(300,200));

        Border border_supplier3 = BorderFactory.createTitledBorder(supplier3);
        pnl_supplier3.setBorder(border_supplier3);
        pnl_supplier3.setPreferredSize(new Dimension(300,200));

        if (supplier2.equals(""))
            pnl_supplier2.setVisible(false);
        if (supplier3.equals(""))
            pnl_supplier3.setVisible(false);

        /*
        //pnl_supplier1 components
        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 25, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier1.add(lbl_quantity1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 25);
        pnl_supplier1.add(lbl_quantity1a, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier1.add(lbl_unit1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_supplier1.add(lbl_unit1a, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier1.add(lbl_amount1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_supplier1.add(lbl_amount1a, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier1.add(radio_supplier1, c);

        //pnl_supplier2 components
        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(5, 25, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier2.add(lbl_quantity2, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 25);
        pnl_supplier2.add(lbl_quantity2a, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier2.add(lbl_unit2, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_supplier2.add(lbl_unit2a, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier2.add(lbl_amount2, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_supplier2.add(lbl_amount2a, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier2.add(radio_supplier2, c);

        //pnl_supplier3 components
        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(5, 25, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier3.add(lbl_quantity3, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 25);
        pnl_supplier3.add(lbl_quantity3a, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier3.add(lbl_unit3, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_supplier3.add(lbl_unit3a, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier3.add(lbl_amount3, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_supplier3.add(lbl_amount3a, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_supplier3.add(radio_supplier3, c);

*/

        //panel supplier 1
        c.gridx = 0;
        c.gridy = 0;
        pnl_supplier1.add(radio_supplier1, c);

        //panel supplier 2
        pnl_supplier2.add(radio_supplier2, c);

        //panel supplier 3
        pnl_supplier3.add(radio_supplier3, c);

        data = new Vector<Vector<Object>>();

        tablemodel = new DefaultTableModel();
        headers.add("Item #");
        headers.add("Quantity");
        headers.add("Description");
        headers.add("Unit 1");
        headers.add("Amount 1");
        headers.add("Unit 2");
        headers.add("Amount 2");
        headers.add("Unit 3");
        headers.add("Amount 3");
        headers.add("Supplier Chosen");

        //selectionHandler handler = new selectionHandler();

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getCanvassitems(canvassnum);
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_items = new JTable(tablemodel)
        {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);

                if (getSelectedRow() == row) {
                    if (value == null || value.equals("No Supplier")) {
                        comp.setBackground(new Color(114,114,114));
                    }
                    else if (value.equals("Supplier 1"))
                    {
                        comp.setBackground(new Color(222,85,85));
                    }
                    else if (value.equals("Supplier 2"))
                    {
                        comp.setBackground(new Color(128,136,229));
                    }
                    else if (value.equals("Supplier 3"))
                    {
                        comp.setBackground(new Color(116,192,112));
                    }
                    else {
                        comp.setBackground(new Color(196,196,196));
                    }
                } else {
                    if (value == null || value.equals("No Supplier")) {
                        comp.setBackground(new Color(175,175,175));
                    }
                    else if (value.equals("Supplier 1"))
                    {
                        comp.setBackground(new Color(229,155,155));
                    }
                    else if (value.equals("Supplier 2"))
                    {
                        comp.setBackground(new Color(184,187,231));
                    }
                    else if (value.equals("Supplier 3"))
                    {
                        comp.setBackground(new Color(164,194,162));
                    }
                    else {
                        comp.setBackground(Color.white);
                    }
                }
                return comp;
            }
        };

        tbl_items.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_items);
        sp.setPreferredSize(new Dimension(900, 200));

        /*
        listselectionmodel = tbl_items.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_items.setSelectionModel(listselectionmodel);
*/

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry.add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry.add(sp, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.insets = new Insets(5, 0, 30, 20);
        pnl_entry.add(lbl_selecteditem, c);

        //fourth row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.insets = new Insets(5, 0, 10, 20);
        pnl_entry.add(pnl_supplier1, c);

        c.gridx = 1;
        pnl_entry.add(pnl_supplier2, c);

        c.gridx = 2;
        pnl_entry.add(pnl_supplier3, c);

        //5th row
        c.gridx = 2;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_entry.add(btn_update, c);

        c.gridx = 1;
        pnl_entry.add(lbl_supplierchosen, c);

        //6th row
        c.gridx = 1;
        c.gridy = 5;
        pnl_entry.add(btn_submit, c);

        add(pnl_entry, BorderLayout.CENTER);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getInt("item_id"));
            record.add(rset1.getInt("quantity"));
            record.add(rset1.getString("description"));
            record.add(rset1.getDouble("unit1"));
            record.add(rset1.getDouble("amount1"));
            record.add(rset1.getDouble("unit2"));
            record.add(rset1.getDouble("amount2"));
            record.add(rset1.getDouble("unit3"));
            record.add(rset1.getDouble("amount3"));
            record.add("No Supplier");

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    /*
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
                int maxIndex = lsm.getMaxSelectionIndex();

                i = maxIndex;

                String description, quantity, unit1, amount1, unit2, amount2, unit3, amount3;

                Object x, y, w, u, v, m, n, o;

                x = tbl_items.getValueAt(maxIndex, 2);
                description = x.toString();

                y = tbl_items.getValueAt(maxIndex, 1);
                quantity = y.toString();

                w = tbl_items.getValueAt(maxIndex, 3);
                unit1 = w.toString();

                u = tbl_items.getValueAt(maxIndex, 4);
                amount1 = u.toString();

                v = tbl_items.getValueAt(maxIndex, 5);
                unit2 = v.toString();

                m = tbl_items.getValueAt(maxIndex, 6);
                amount2 = m.toString();

                n = tbl_items.getValueAt(maxIndex, 7);
                unit3 = n.toString();

                o = tbl_items.getValueAt(maxIndex, 8);
                amount3 = o.toString();

                lbl_quantity1a.setText(quantity);
                lbl_unit1a.setText(unit1);
                lbl_amount1a.setText(amount1);

                lbl_quantity2a.setText(quantity);
                lbl_unit2a.setText(unit2);
                lbl_amount2a.setText(amount2);

                lbl_quantity3a.setText(quantity);
                lbl_unit3a.setText(unit3);
                lbl_amount3a.setText(amount3);

                lbl_selecteditem.setText("Selected item : " + description);

                radio_supplier1.setEnabled(true);
                radio_supplier2.setEnabled(true);
                radio_supplier3.setEnabled(true);
                btn_update.setEnabled(true);
            }
        }
    }
*/

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_update) {
                if (radio_supplier1.isSelected())
                    supplierchosen = 1;
                else if (radio_supplier2.isSelected())
                    supplierchosen = 2;
                else if (radio_supplier3.isSelected())
                    supplierchosen = 3;
                else
                    supplierchosen = 0;

                updateTable(supplierchosen);

                if (supplierchosen != 0)
                    lbl_supplierchosen.setText("You have chosen supplier #" + supplierchosen + ".");
                else
                    lbl_supplierchosen.setText("You did not choose a supplier.");

                radio_supplier1.setSelected(false);
                radio_supplier2.setSelected(false);
                radio_supplier3.setSelected(false);
                btn_submit.setEnabled(true);
            }

            if (source == btn_submit) {
                dbconnect conn = new dbconnect();
                boolean withchosen;

                withchosen = true;

                for(int i = 0; i < data.size(); i++) {
                    if (data.get(i).get(9).toString().equals("No Supplier"))
                    {
                        withchosen = false;
                        break;
                    }
                }

                if (withchosen)
                {
                    String time;
                    time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);

                    try {
                        conn.addPurchaseorder(canvassid, form_login.fullname, suppliera, supplierb, supplierc, data, datenow, supplierchosen, time);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(this, "Purchase order submitted!", "Information Complete", JOptionPane.INFORMATION_MESSAGE);

                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    mainFrame.pnl_center.add(new panel_homeengineer(), BorderLayout.CENTER);

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Please choose a supplier before submitting!", "Incomplete Information", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void updateTable(Integer supplier)
    {
        String tempsupplier = null;

        if (supplier != 0)
            tempsupplier = "Supplier " + supplier;
        else
            tempsupplier = "No Supplier";

        for (int i = 0; i < data.size(); i++)
        {
            data.get(i).set(9, tempsupplier);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }
}
