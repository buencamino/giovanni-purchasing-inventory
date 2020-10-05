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

public class panel_stockcard extends JPanel {
    JTable tbl_items;
    JLabel lbl_title;
    JButton btn_generate;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    String buff_itemid;

    public panel_stockcard() throws Exception {
        setLayout(new GridBagLayout());

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        lbl_title = new JLabel("Stock List");
        btn_generate = new JButton("Generate Stock Card");
        btn_generate.setEnabled(false);
        btn_generate.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("Stock #");
        headers.add("Stock Name");
        headers.add("Quantity");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getStockpermanent();
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
        sp.setPreferredSize(new Dimension(800,300));

        listselectionmodel = tbl_items.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_items.setSelectionModel(listselectionmodel);

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

                Object x;

                x = tbl_items.getValueAt(maxIndex, 0);
                buff_itemid = x.toString();
                btn_generate.setEnabled(true);
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("stock_id"));
            record.add(rset1.getString("description"));
            record.add(rset1.getString("quantity"));

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_generate) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                mainFrame.pnl_center.add(new panel_viewstockcard(buff_itemid), BorderLayout.CENTER);

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}
