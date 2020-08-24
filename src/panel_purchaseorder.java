import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class panel_purchaseorder extends JPanel {
    JTable tbl_canvass;
    JScrollPane sp;
    JLabel lbl_selectedcanvass, lbl_title;
    ResultSet rset;
    Vector<Object> record;
    DefaultTableModel tablemodel;
    Vector<Vector<Object>> data;
    Vector<String> headers = new Vector<String>();
    ListSelectionModel listselectionmodel;
    String canvassnum, supplier1, supplier2, supplier3;
    JButton btn_next;
    JPanel pnl_entry1;

    public panel_purchaseorder() throws Exception {
        setLayout(new BorderLayout());

        pnl_entry1 = new JPanel(new GridBagLayout());

        data = new Vector<Vector<Object>>();

        tablemodel = new DefaultTableModel();
        headers.add("Canvass #");
        headers.add("Req #");
        headers.add("Supplier1");
        headers.add("Supplier2");
        headers.add("Supplier3");
        headers.add("Canvassed By");

        HandleControlButton control = new HandleControlButton();

        btn_next = new JButton("Next");
        btn_next.addActionListener(control);
        btn_next.setEnabled(false);
        lbl_selectedcanvass = new JLabel("No canvass sheet selected");
        lbl_title = new JLabel("Please choose a canvass sheet below to make purchase order form from");

        GridBagConstraints c = new GridBagConstraints();

        selectionHandler handler = new selectionHandler();

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getCanvass();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_canvass = new JTable(tablemodel);

        tbl_canvass.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_canvass);
        sp.setPreferredSize(new Dimension(900, 350));

        listselectionmodel = tbl_canvass.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_canvass.setSelectionModel(listselectionmodel);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_entry1.add(sp, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(5, 0, 50, 10);
        pnl_entry1.add(lbl_selectedcanvass, c);

        //fourth row
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(5, 0, 10, 10);
        pnl_entry1.add(btn_next, c);

        add(pnl_entry1, BorderLayout.CENTER);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getInt("canvass_id"));
            record.add(rset1.getInt("requisition_num"));
            record.add(rset1.getString("supplier1"));
            record.add(rset1.getString("supplier2"));
            record.add(rset1.getString("supplier3"));
            record.add(rset1.getString("canvassedby"));

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
                int maxIndex = lsm.getMaxSelectionIndex();

                String canvassedby;

                Object x, y, w, u, v;

                x = tbl_canvass.getValueAt(maxIndex, 0);
                canvassnum = x.toString();

                y = tbl_canvass.getValueAt(maxIndex, 5);
                canvassedby = y.toString();

                w = tbl_canvass.getValueAt(maxIndex, 2);
                supplier1 = w.toString();

                u = tbl_canvass.getValueAt(maxIndex, 3);
                supplier2 = u.toString();

                v = tbl_canvass.getValueAt(maxIndex, 4);
                supplier3 = v.toString();

                lbl_selectedcanvass.setText("Selected canvass sheet #" + canvassnum + " canvassed by " + canvassedby);

                btn_next.setEnabled(true);
            }
        }
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_next) {
                removeAll();
                repaint();
                revalidate();

                try {
                    add(new panel_purchaseorder2(canvassnum, supplier1, supplier2, supplier3), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                repaint();
                revalidate();
            }
        }
    }
}
