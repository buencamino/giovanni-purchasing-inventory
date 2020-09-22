import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class panel_requisitionslip extends JPanel {
    JLabel lbl_prepared, lbl_project, lbl_location, lbl_date, lbl_category, lbl_itemtitle, lbl_quantity, lbl_units, lbl_description, lbl_locationdata, lbl_deliverytype, lbl_purpose, lbl_enduser;
    JTextField text_purpose, text_enduser;
    JTextField text_quantity;
    public static JTextField text_description;
    JPanel pnl_center;
    JButton btn_additem, btn_delete, btn_submit, btn_search, btn_addstock;
    JScrollPane sp;
    JTable tbl_items;
    Vector<Vector<Object>> data;
    Vector<Object> vec;
    DefaultTableModel model;
    Vector<String> headers = new Vector<String>();
    ListSelectionModel listselectionmodel;
    int buffindex;
    JComboBox combo_category, combo_project, combo_delivery, combo_unit;
    String datenow, time;
    ResultSet rset;
    int selectedproject;


    public panel_requisitionslip() throws Exception {
        setLayout(new BorderLayout());

        buffindex = -1;
        pnl_center = new JPanel(new GridBagLayout());
        data = new Vector<Vector<Object>>();

        GridBagConstraints c = new GridBagConstraints();
        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        model = new DefaultTableModel();

        headers.add("Quantity");
        headers.add("Units");
        headers.add("Description");
        headers.add("End User");
        headers.add("Category");

        String[] categories = {"Construction Materials", "Office Supplies", "Spare Parts", "Equipment/Tools", "Fuel/Oil/Lubricants", "Meals", "Computer/I.T Supplies", "Insurance", "Medicines", "Fares", "Remmittance/Charges", "Accomodations/Hotels", "S.O.P/Solicitation", "Labor/Service Charges", "Document Solutions", "Cellphone Loads", "Freight And Handling", "Penalty Charges"};
        combo_category = new JComboBox(categories);

        String[] units = {"pc.", "pcs.", "kg.", "kgs.", "length", "lengths", "bags", "case", "box", "ream", "liter", "liters", "lot", "quart", "gallon", "set", "pail", "drum", "square meter", "cubic meter", "hour(s)", "day(s)", "minute(s)", "cm", "meter(s)", "feet/foot", "inches", "person(s)", "bag", "rolls", "rolls", "pack", "packs", "bar", "bars", "pair", "pairs", "sheet", "sheets", "bundle", "bundles", "trip", "trips", "batch", "bd. ft"};
        combo_unit = new JComboBox(units);

        dbconnect conn2 = new dbconnect();

        rset = conn2.getProjectlist();
        combo_project = new JComboBox();

        while (rset.next())
        {
            combo_project.addItem(rset.getString("project_name"));
        }

        combo_project.setSelectedIndex(-1);

        combo_project.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                selectedproject = combo_project.getSelectedIndex();
                int count = 0;

                try {
                    rset.first();
                    do
                    {
                        if (selectedproject == count)
                            lbl_locationdata.setText(rset.getString("location"));
                        count++;
                    } while (rset.next());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        String[] deliverytype = {"Pick Up", "Direct Purchase", "Delivered by Supplier"};
        combo_delivery = new JComboBox(deliverytype);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        lbl_date = new JLabel();
        lbl_date.setText("Date :  " + formatter.format(date));
        datenow = formatter.format(date);

        lbl_project = new JLabel("Project :");
        lbl_location = new JLabel("Location :");
        lbl_category = new JLabel("Category :");
        lbl_itemtitle = new JLabel("Enter Each Item Here");
        lbl_quantity = new JLabel("Quantity :");
        lbl_units = new JLabel("Units :");
        lbl_description = new JLabel("Description :");
        lbl_prepared = new JLabel("Prepared By : " + form_login.fullname);
        lbl_locationdata = new JLabel();
        lbl_deliverytype = new JLabel("Delivery Type :");
        lbl_purpose = new JLabel("Purpose :");
        lbl_enduser = new JLabel("End User :");

        text_description = new JTextField(30);
        text_description.setEditable(false);
        text_quantity = new JTextField(10);
        text_purpose = new JTextField(20);
        text_enduser = new JTextField(20);

        text_purpose.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_purpose.getText().length() >= 100 )
                    e.consume();
            }
        });

        text_enduser.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_enduser.getText().length() >= 100 )
                    e.consume();
            }
        });

        /*
        text_description.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_description.getText().length() >= 100 )
                    e.consume();
            }
        });
*/

        text_quantity.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE)) || text_quantity.getText().length() >= 30) {
                    getToolkit().beep();
                    e.consume();
                }
            }
        });

        btn_additem = new JButton("Add Item");
        btn_additem.addActionListener(control);
        btn_delete = new JButton("Delete Item");
        btn_delete.addActionListener(control);
        btn_submit = new JButton("Submit Requisition Slip");
        btn_submit.setPreferredSize(new Dimension(300,30));
        btn_submit.addActionListener(control);
        btn_search = new JButton("Search Stocks");
        btn_search.addActionListener(control);
        btn_addstock = new JButton("Add Stock");
        btn_addstock.addActionListener(control);
        btn_addstock.setEnabled(false);

        tbl_items = new JTable(model);
        tbl_items.setDefaultEditor(Object.class, null);

        //first row
        c.gridy = 0;
        c.gridx = 0;
        pnl_center.add(lbl_date, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_project, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 3;
        pnl_center.add(combo_project, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_location, c);

        c.gridx = 1;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(lbl_locationdata, c);

        //fourth row
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_purpose, c);

        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        pnl_center.add(text_purpose, c);

        //5th row
        c.gridy = 4;
        c.gridx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_deliverytype, c);

        c.gridx = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(combo_delivery, c);

        //sixth row
        c.gridy = 5;
        c.gridx = 0;
        pnl_center.add(lbl_itemtitle, c);

        //7th row
        c.gridy = 6;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_quantity, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(text_quantity, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_units, c);

        c.gridx = 3;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(combo_unit, c);

        //8th row
        c.gridy = 7;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_description, c);

        c.gridx = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(text_description, c);

        c.gridx = 3;
        c.gridwidth = 1;
        pnl_center.add(btn_search, c);

        c.gridx = 4;
        pnl_center.add(btn_addstock, c);

        //9th row
        c.gridwidth = 1;
        c.gridy = 8;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_category, c);

        c.gridx = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(combo_category, c);

        //10th row
        c.gridy = 9;
        c.gridx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        pnl_center.add(lbl_enduser, c);

        c.gridx = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(text_enduser, c);

        //11th row
        c.gridwidth = 1;
        c.gridy = 10;
        c.gridx = 2;
        pnl_center.add(btn_additem, c);

        c.gridx = 3;
        pnl_center.add(btn_delete, c);

        //12th row
        c.gridy = 11;
        c.gridx = 0;
        c.gridwidth = 5;
        sp = new JScrollPane(tbl_items);
        sp.setPreferredSize(new Dimension(600,250));
        pnl_center.add(sp, c);

        //13th row
        c.gridy = 12;
        c.gridx = 3;
        c.gridwidth = 2;
        pnl_center.add(lbl_prepared, c);

        //14th row
        c.gridy = 13;
        c.gridx = 2;
        c.gridwidth = 3;
        pnl_center.add(btn_submit, c);

        listselectionmodel = tbl_items.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_items.setSelectionModel(listselectionmodel);
        
        add(pnl_center, BorderLayout.CENTER);
    }

    class selectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();

            if(lsm.isSelectionEmpty())
            {
                System.out.println("nothing selected (message for error checking only)");
            }
            else
            {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                buffindex = maxIndex;
            }
        }
    }

    public void refreshVector(String quantity, String units, String description, String enduser, String category)
    {
        vec = new Vector<Object>();
        vec.add(quantity);
        vec.add(units);
        vec.add(description);
        vec.add(enduser);
        vec.add(category);

        data.addElement(vec);

        model.setDataVector(data, headers);
        model.fireTableDataChanged();
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_additem) {
                if ((!(text_quantity.getText().equals(""))) && (!(text_description.getText().equals(""))))
                    refreshVector(text_quantity.getText(), (String) combo_unit.getSelectedItem(), text_description.getText(), text_enduser.getText(), (String) combo_category.getSelectedItem());

                text_quantity.setText("");
                combo_unit.setSelectedIndex(0);
                text_description.setText("");
                text_enduser.setText("");
                combo_category.setSelectedIndex(0);
                btn_addstock.setEnabled(false);
            }

            if (source == btn_delete){
                if (buffindex != -1)
                data.remove(buffindex);
                model.setDataVector(data, headers);
                model.fireTableDataChanged();

                text_quantity.setText("");
                combo_unit.setSelectedIndex(0);
                text_description.setText("");
                text_enduser.setText("");
                combo_category.setSelectedIndex(0);

                buffindex = -1;
            }

            if (source == btn_submit)
            {
                dbconnect conn = new dbconnect();

                if (data.size() != 0)
                {

                    time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);
                    try {
                        conn.addRequisitionslip((String) combo_project.getSelectedItem(), lbl_locationdata.getText(), form_login.fullname, datenow, data, text_purpose.getText(), (String) combo_delivery.getSelectedItem(), time);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JDialog.setDefaultLookAndFeelDecorated(true);

                    int response = JOptionPane.showConfirmDialog(null, "Requisition slip submitted! Do you want to submit another requisition slip?", "Completed", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (response == JOptionPane.NO_OPTION) {
                        mainFrame.pnl_center.removeAll();
                        mainFrame.pnl_center.repaint();
                        mainFrame.pnl_center.revalidate();

                        mainFrame.pnl_center.add(new panel_homeengineer(), BorderLayout.CENTER);
                        mainFrame.pnl_center.repaint();
                        mainFrame.pnl_center.revalidate();
                    } else if (response == JOptionPane.YES_OPTION) {
                        mainFrame.pnl_center.removeAll();
                        mainFrame.pnl_center.repaint();
                        mainFrame.pnl_center.revalidate();

                        try {
                            mainFrame.pnl_center.add(new panel_requisitionslip(), BorderLayout.CENTER);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        mainFrame.pnl_center.repaint();
                        mainFrame.pnl_center.revalidate();

                    } else if (response == JOptionPane.CLOSED_OPTION) {
                        System.out.println("Closed window");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(pnl_center, "Error, no items entered!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (source == btn_search)
            {
                dialog_searchitem si = null;
                try {
                    si = new dialog_searchitem();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                si.setSize(new Dimension(500,500));
                si.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        if (text_description.getText().equals(""))
                            btn_addstock.setEnabled(true);
                        else
                            btn_addstock.setEnabled(false);
                    }
                });
                si.setLocationRelativeTo(null);
                si.setModal(true);
                si.setVisible(true);
            }

            if (source == btn_addstock)
            {
                dialog_additem ai = null;
                try {
                    ai = new dialog_additem();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                ai.setSize(new Dimension(450,200));
                ai.setLocationRelativeTo(null);
                ai.setModal(true);
                ai.setVisible(true);
            }
        }
    }
}
