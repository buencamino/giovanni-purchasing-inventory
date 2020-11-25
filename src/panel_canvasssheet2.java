import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Vector;

public class panel_canvasssheet2 extends JPanel {
    JScrollPane sp1;
    JTable tbl_itemlist;
    JLabel lbl_itemlist, lbl_itemdetails, lbl_quantity1, lbl_quantity1a, lbl_unit1, lbl_amount1, lbl_amount1a,
            lbl_supplier1, lbl_terms1,
    lbl_supplier2, lbl_terms2, lbl_quantity2, lbl_quantity2a, lbl_unit2, lbl_amount2, lbl_amount2a,
    lbl_supplier3, lbl_terms3, lbl_quantity3, lbl_quantity3a, lbl_unit3, lbl_amount3, lbl_amount3a;
    JTextField text_unit1, text_unit2, text_unit3;
    Vector<String> headers = new Vector<String>();
    DefaultTableModel tablemodel;
    JButton btn_update, btn_submit;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ResultSet rset;
    ListSelectionModel listselectionmodel;
    Integer i;
    String suppliera, supplierb, supplierc, termsa, termsb, termsc, reqnum, reco, time, datenow;

    public panel_canvasssheet2(String buff_reqnum, String supplier1, String supplier2, String supplier3, String terms1, String terms2, String terms3, String recommendations) throws Exception {
        setLayout(new GridBagLayout());

        reqnum = buff_reqnum;

        reco = recommendations;
        suppliera = supplier1;
        supplierb = supplier2;
        supplierc = supplier3;

        termsa = terms1;
        termsb = terms2;
        termsc = terms3;

        if (supplier2.equals(""))
            termsb = "N/A";
        if (supplier3.equals(""))
            termsc = "N/A";

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        GridBagConstraints c = new GridBagConstraints();

        data = new Vector<Vector<Object>>();

        lbl_itemlist = new JLabel("Item List");
        lbl_itemdetails = new JLabel("No item selected yet");
        text_unit1 = new JTextField(10);
        text_unit2 = new JTextField(10);
        text_unit3 = new JTextField(10);
        lbl_quantity1 = new JLabel("Quantity :");
        lbl_unit1 = new JLabel("Unit Price :");
        lbl_amount1 = new JLabel("Amount :");
        lbl_supplier1 = new JLabel("Supplier 1 - " + supplier1);
        lbl_terms1 = new JLabel("Terms - " + terms1);
        btn_update = new JButton("Update Item");
        lbl_quantity1a = new JLabel();
        lbl_amount1a = new JLabel("0.0");
        btn_submit = new JButton("Submit Canvass Form");

        lbl_itemdetails.setForeground(new Color(0, 119, 240));
        btn_submit.setEnabled(false);
        text_unit1.setEnabled(false);
        text_unit2.setEnabled(false);
        text_unit3.setEnabled(false);
        btn_update.setEnabled(false);
        btn_update.addActionListener(control);
        btn_submit.addActionListener(control);

        text_unit1.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == '.') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE)) || text_unit1.getText().length() >= 30) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        text_unit1.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                calculate();
            }
            public void removeUpdate(DocumentEvent e) {
                calculate();
            }
            public void insertUpdate(DocumentEvent e) {
                calculate();
            }

            public void calculate()
            {
                Double x, y, total;

                if (text_unit1.getText().equals("") || text_unit3.getText().equals("."))
                    lbl_amount1a.setText("0");
                else
                {
                    x = Double.parseDouble(text_unit1.getText());
                    y = Double.parseDouble(lbl_quantity1a.getText());
                    total = x * y;

                    DecimalFormat df = new DecimalFormat("#.##");
                    total = Double.valueOf(df.format(total));

                    lbl_amount1a.setText(String.valueOf(total));
                }
            }
        });

        text_unit2.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == '.') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE)) || text_unit2.getText().length() >= 30) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        text_unit2.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                calculate();
            }
            public void removeUpdate(DocumentEvent e) {
                calculate();
            }
            public void insertUpdate(DocumentEvent e) {
                calculate();
            }

            public void calculate()
            {
                Double x, y, total;

                if (text_unit2.getText().equals("") || text_unit3.getText().equals("."))
                    lbl_amount2a.setText("0");
                else
                {
                    x = Double.parseDouble(text_unit2.getText());
                    y = Double.parseDouble(lbl_quantity2a.getText());
                    total = x * y;

                    DecimalFormat df = new DecimalFormat("#.##");
                    total = Double.valueOf(df.format(total));

                    lbl_amount2a.setText(String.valueOf(total));
                }
            }
        });

        text_unit3.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) || (c == '.') ||
                        (c == KeyEvent.VK_DELETE)) || text_unit3.getText().length() >= 30) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        text_unit3.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                calculate();
            }
            public void removeUpdate(DocumentEvent e) {
                calculate();
            }
            public void insertUpdate(DocumentEvent e) {
                calculate();
            }

            public void calculate()
            {
                Double x, y, total;

                if (text_unit3.getText().equals("") || text_unit3.getText().equals("."))
                    lbl_amount3a.setText("0");
                else
                {
                    x = Double.parseDouble(text_unit3.getText());
                    y = Double.parseDouble(lbl_quantity3a.getText());
                    total = x * y;

                    DecimalFormat df = new DecimalFormat("#.##");
                    total = Double.valueOf(df.format(total));

                    lbl_amount3a.setText(String.valueOf(total));
                }
            }
        });

        lbl_quantity2 = new JLabel("Quantity :");
        lbl_unit2 = new JLabel("Unit Price :");
        lbl_amount2 = new JLabel("Amount :");
        if (supplier2.equals(""))
            lbl_supplier2 = new JLabel("Supplier 2 - N/A");
        else
            lbl_supplier2 = new JLabel("Supplier 2 - " + supplier2);
        lbl_terms2 = new JLabel("Terms - " + termsb);
        lbl_quantity2a = new JLabel();
        lbl_amount2a = new JLabel("0.0");

        lbl_quantity3 = new JLabel("Quantity :");
        lbl_unit3 = new JLabel("Unit Price :");
        lbl_amount3 = new JLabel("Amount :");
        if (supplier3.equals(""))
            lbl_supplier3 = new JLabel("Supplier 3 - N/A");
        else
            lbl_supplier3 = new JLabel("Supplier 3 - " + supplier3);
        lbl_terms3 = new JLabel("Terms - " + termsc);
        lbl_quantity3a = new JLabel();
        lbl_amount3a = new JLabel("0.0");

        tablemodel = new DefaultTableModel();
        headers.add("Item #");
        headers.add("Quantity");
        headers.add("Description");
        headers.add("Sup1 Unit Price");
        headers.add("Amount1");
        headers.add("Sup2 Unit Price");
        headers.add("Amount2");
        headers.add("Sup3 Unit Price");
        headers.add("Amount3");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getReqitems(buff_reqnum);
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_itemlist = new JTable(tablemodel)
        {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);

                if (getSelectedRow() == row) {
                    if (value == null || value.equals("0.0") || value.equals("0")) {
                        comp.setBackground(new Color(199,62,62));
                    } else {
                        comp.setBackground(new Color(191,187,187));
                    }
                } else {
                    if (value == null || value.equals("0.0") || value.equals("0")) {
                        comp.setBackground(new Color(245,120,120));
                    } else {
                        comp.setBackground(Color.white);
                    }
                    //comp.setBackground(Color.white);
                }
                return comp;
            }
        };
        tbl_itemlist.getColumnModel().getColumn(2).setCellRenderer(new WordWrapCellRenderer());
        tbl_itemlist.setDefaultEditor(Object.class, null);
        sp1 = new JScrollPane(tbl_itemlist);
        sp1.setPreferredSize(new Dimension(900, 350));

        listselectionmodel = tbl_itemlist.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_itemlist.setSelectionModel(listselectionmodel);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_itemlist, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 6;
        c.insets = new Insets(5, 0, 30, 10);
        add(sp1, c);

        //third row
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 6;
        c.insets = new Insets(5, 0, 0, 10);
        add(lbl_itemdetails, c);

        //fourth row
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 3;
        add(lbl_supplier1, c);

        c.gridx = 2;
        add(lbl_supplier2, c);

        c.gridx = 4;
        add(lbl_supplier3, c);

        //5th row
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(5, 0, 0, 10);
        add(lbl_terms1, c);

        c.gridx = 2;
        add(lbl_terms2, c);

        c.gridx = 4;
        add(lbl_terms3, c);

        //6th row
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_quantity1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_quantity1a, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_quantity2, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_quantity2a, c);

        c.gridx = 4;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_quantity3, c);

        c.gridx = 5;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_quantity3a, c);

        //7th row
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_unit1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 60);
        add(text_unit1, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(5, 0, 0, 10);
        add(lbl_unit2, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 60);
        add(text_unit2, c);

        c.gridx = 4;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(5, 0, 0, 10);
        add(lbl_unit3, c);

        c.gridx = 5;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 60);
        add(text_unit3, c);

        //8th row
        c.gridx = 0;
        c.gridy = 7;
        c.insets = new Insets(5, 0, 0, 10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_amount1, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_amount1a, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_amount2, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_amount2a, c);

        c.gridx = 4;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_amount3, c);

        c.gridx = 5;
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_amount3a, c);

        //9th row
        c.gridx = 5;
        c.gridy = 8;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(btn_update, c);

        //10th row
        c.gridx = 3;
        c.gridy = 9;
        add(btn_submit, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("item_id"));
            record.add(rset1.getString("quantity"));
            record.add(rset1.getString("description"));
            record.add("0.0");
            record.add("0.0");
            record.add("0.0");
            record.add("0.0");
            record.add("0.0");
            record.add("0.0");

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    static class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
        WordWrapCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_update) {
                updateTable(i, text_unit1.getText(), lbl_amount1a.getText(), text_unit2.getText(), lbl_amount2a.getText(), text_unit3.getText(), lbl_amount3a.getText());
                btn_submit.setEnabled(true);
                text_unit1.setText("");
                text_unit2.setText("");
                text_unit3.setText("");
            }

            if(source == btn_submit) {
                dbconnect conn = new dbconnect();

                time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                Date date = new Date();
                datenow = formatter.format(date);

                try {
                    conn.addCanvasssheet(reqnum, suppliera, supplierb, supplierc, termsa, termsb, termsc, data, form_login.fullname, reco, time, datenow);

                    double amt1 = 0, amt2 = 0, amt3 = 0;

                    for (i = 0; i < data.size(); i++)
                    {
                        amt1 += Double.parseDouble(data.get(i).get(4).toString());
                        amt2 += Double.parseDouble(data.get(i).get(6).toString());
                        amt3 += Double.parseDouble(data.get(i).get(8).toString());
                    }

                    double arramount[] = new double[]{amt1, amt2, amt3};

                    System.out.println("1:" + amt1 + " 2:" + amt2 + " 3:" +amt3);

                    int supplierchosen = getMin(arramount);

                    System.out.println(supplierchosen);

                    conn.addPurchaseorder(reqnum, form_login.fullname, suppliera, supplierb, supplierc, data, datenow, supplierchosen, time);
                    conn.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                JOptionPane.showMessageDialog(this, "Canvass sheet submitted!", "Information Complete", JOptionPane.INFORMATION_MESSAGE);

                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                mainFrame.pnl_center.add(new panel_homeengineer(), BorderLayout.CENTER);

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
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
                int maxIndex = lsm.getMaxSelectionIndex();

                String itemnum, description, quantity;

                Object x, y, z;

                x = tbl_itemlist.getValueAt(maxIndex, 0);
                itemnum = x.toString();

                y = tbl_itemlist.getValueAt(maxIndex, 2);
                description = y.toString();

                z = tbl_itemlist.getValueAt(maxIndex, 1);
                quantity = z.toString();

                i = maxIndex;
                lbl_itemdetails.setText("Item #" + itemnum + " - " + description);

                text_unit1.setEnabled(true);

                if (supplierb.equals(""))
                    text_unit2.setEnabled(false);
                else
                    text_unit2.setEnabled(true);

                if (supplierc.equals(""))
                    text_unit3.setEnabled(false);
                else
                    text_unit3.setEnabled(true);

                lbl_quantity1a.setText(quantity);
                lbl_quantity2a.setText(quantity);
                lbl_quantity3a.setText(quantity);

                btn_update.setEnabled(true);
            }
        }
    }

    public void updateTable(int y, String unit1, String amount1, String unit2, String amount2, String unit3, String amount3)
    {
        if (unit1.equals(""))
            unit1 = "0.0";
        if (unit2.equals(""))
            unit2 = "0.0";
        if (unit3.equals(""))
            unit3 = "0.0";
        data.get(y).set(3, unit1);
        data.get(y).set(4, amount1);
        data.get(y).set(5, unit2);
        data.get(y).set(6, amount2);
        data.get(y).set(7, unit3);
        data.get(y).set(8, amount3);

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();

        tbl_itemlist.getColumnModel().getColumn(2).setCellRenderer(new WordWrapCellRenderer());
    }

    public static int getMin(double[] inputArray){
        int supplierchosen = 1;

        double minValue = inputArray[0];
        for(int i=1;i<inputArray.length;i++){
            if(inputArray[i] < minValue){
                minValue = inputArray[i];
                supplierchosen = i + 1;
            }
        }
        return supplierchosen;
    }
}
