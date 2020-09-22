import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    int selected;
    double unitprice;
    JButton btn_update, btn_confirm;
    String buff_poid, datenow, suppliername;

    public panel_repeatpo2(String poid, String supplier) throws Exception {
        setLayout(new GridBagLayout());

        buff_poid = poid;
        suppliername = supplier;

        GridBagConstraints c = new GridBagConstraints();
        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        tablemodel = new DefaultTableModel();
        headers.add("Item #");
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
        text_quantity = new JTextField(20);
        btn_update = new JButton("Update Quantity");
        btn_confirm = new JButton("Confirm Purchase Order");
        btn_confirm.setPreferredSize(new Dimension(300, 30));
        btn_update.addActionListener(control);
        btn_confirm.addActionListener(control);

        text_quantity.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == '.') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE)) || text_quantity.getText().length() >= 30) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.insets = new Insets(5,0,25,10);
        add(sp, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_quantity, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_quantity, c);

        c.gridx = 2;
        add(btn_update, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 3;
        c.insets = new Insets(35,0,0,10);
        add(btn_confirm, c);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("item_id"));
            record.add(rset1.getString("description"));
            record.add(rset1.getString("unit"));
            record.add(rset1.getString("unitprice"));
            record.add(0);
            record.add(0);

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
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

                Object x;

                selected = maxIndex;

                x = tbl_items.getValueAt(maxIndex, 3);
                unitprice = Double.parseDouble(x.toString());

                btn_update.setEnabled(true);
                btn_confirm.setEnabled(true);
            }
        }
    }

    public void updateTable(int y, String quantity)
    {
        double amount;

        data.get(selected).set(4, quantity);

        amount = unitprice * Double.parseDouble(quantity);
        data.get(selected).set(5, amount);

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_update) {
                updateTable(selected, text_quantity.getText());
                text_quantity.setText("");
            }

            if (source == btn_confirm) {
                dbconnect conn = new dbconnect();

                boolean complete;

                complete = true;

                for(int i = 0; i < data.size(); i++) {
                    if (data.get(i).get(4).toString().equals("0"))
                    {
                        complete = false;
                        break;
                    }
                }

                if (complete)
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    java.util.Date date = new Date();
                    datenow = formatter.format(date);
                    String time;
                    time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);

                    try {
                        conn.addRepeatpo(buff_poid, data, form_login.fullname, datenow, time, suppliername);
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
                    JOptionPane.showMessageDialog(this, "Some items are missing quantity!", "Incomplete Information", JOptionPane.ERROR_MESSAGE);

                }

            }
        }
    }
}
