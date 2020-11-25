import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.sql.*;

public class panel_home_viewrs extends JPanel {
    JLabel lbl_title;
    JScrollPane sp;
    JTable tbl_records;
    Vector<Object> record;
    Vector<String> headers = new Vector<String>();
    ListSelectionModel listselectionmodel;
    DefaultTableModel tablemodel;
    Vector<Vector<Object>> data;
    ResultSet rset;
    String buff_reqid;
    JButton btn_generate, btn_back;

    public panel_home_viewrs() throws Exception {
        setLayout(new GridBagLayout());

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();

        lbl_title = new JLabel("Available Requisition Slips");
        btn_generate = new JButton("Generate Requisition Slip");
        btn_generate.setEnabled(false);
        btn_generate.addActionListener(control);

        btn_back = new JButton("Back");
        btn_back.addActionListener(control);

        tablemodel = new DefaultTableModel();
        headers.add("Req #");
        headers.add("Project");
        headers.add("Prepared By");
        headers.add("Date");
        headers.add("Purpose");
        headers.add("Delivery Type");

        dbconnect conn = new dbconnect();

        try {
            rset = conn.getPendingreqlist();
            refreshTable(rset);
            conn.close();
        } catch (Exception j) {
            throw j;
        }

        tbl_records = new JTable(tablemodel);
        sp = new JScrollPane(tbl_records);
        sp.setPreferredSize(new Dimension(800, 300));

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

        c.gridx = 0;
        add(btn_back, c);
    }

    class selectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if (lsm.isSelectionEmpty()) {
                System.out.println("nothing selected");
            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                Object x;

                x = tbl_records.getValueAt(maxIndex, 0);
                buff_reqid = x.toString();
                btn_generate.setEnabled(true);
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception {
        data = new Vector<Vector<Object>>();

        while (rset1.next()) {
            record = new Vector<Object>();

            record.add(rset1.getString("requisition_num"));
            record.add(rset1.getString("project"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("date"));
            record.add(rset1.getString("purpose"));
            record.add(rset1.getString("deliverytype"));

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

                mainFrame.pnl_center.add(new panel_home_viewrs1(buff_reqid), BorderLayout.CENTER);

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }

            if (source == btn_back) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();


                mainFrame.pnl_center.add(new panel_homeengineer(), BorderLayout.CENTER);

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}
