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

public class panel_home_viewcanvass extends JPanel {
    JLabel lbl_title;
    JButton btn_generate;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JTable tbl_records;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    String buff_canvassid;

    public panel_home_viewcanvass() throws Exception {
        setLayout(new GridBagLayout());

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        lbl_title = new JLabel("Available Canvass Sheets");
        btn_generate = new JButton("Generate Canvass Sheet");
        btn_generate.setEnabled(false);
        btn_generate.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("Canvass #");
        headers.add("Supplier 1");
        headers.add("Supplier 2");
        headers.add("Supplier 3");
        headers.add("Canvassed By");
        headers.add("Recommendations");

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

        tbl_records = new JTable(tablemodel);
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
        c.gridx = 1;
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

                x = tbl_records.getValueAt(maxIndex, 0);
                buff_canvassid = x.toString();
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

            record.add(rset1.getString("canvass_id"));
            record.add(rset1.getString("supplier1"));
            record.add(rset1.getString("supplier2"));
            record.add(rset1.getString("supplier3"));
            record.add(rset1.getString("canvassedby"));
            record.add(rset1.getString("recommendations"));

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

                try {
                    mainFrame.pnl_center.add(new panel_home_viewcanvass1(buff_canvassid), BorderLayout.CENTER);
                } catch (JRException jrException) {
                    jrException.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}
