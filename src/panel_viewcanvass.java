import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class panel_viewcanvass extends JPanel {
    JPanel pnl_north, pnl_center;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ResultSet rset, rsetcanvassinfo, rsetcanvassitems;
    JTable tbl_canvass;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    JButton btn_view;
    String buff_canvassid;

    public panel_viewcanvass() throws Exception {
        setLayout(new BorderLayout());

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Canvass #");
        headers.add("RS #");
        headers.add("Supplier1");
        headers.add("Supplier2");
        headers.add("Supplier3");

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
        sp.setPreferredSize(new Dimension(600,100));

        listselectionmodel = tbl_canvass.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_canvass.setSelectionModel(listselectionmodel);

        btn_view = new JButton("View Canvass Sheet");
        btn_view.addActionListener(control);
        btn_view.setPreferredSize(new Dimension(600,25));
        btn_view.setEnabled(false);
        btn_view.addActionListener(control);

        pnl_north = new JPanel(new GridBagLayout());
        pnl_center = new JPanel(new BorderLayout());

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,0,25);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_north.add(sp, c);

        //second row
        c.gridy = 1;
        c.gridx = 0;
        pnl_north.add(btn_view, c);

        add(pnl_north, BorderLayout.NORTH);
        add(pnl_center, BorderLayout.CENTER);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("canvass_id"));
            record.add(rset1.getString("requisition_num"));
            record.add(rset1.getString("supplier1"));
            record.add(rset1.getString("supplier2"));
            record.add(rset1.getString("supplier3"));

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

                Object x;

                x = tbl_canvass.getValueAt(maxIndex, 0);
                buff_canvassid = x.toString();
                btn_view.setEnabled(true);
            }
        }
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_view) {

                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();
                dbconnect conn = new dbconnect();

                ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

                Map<String, Object> parameters = new HashMap<String, Object>();

                try {
                    rsetcanvassinfo = conn.getCanvassinfo(buff_canvassid);

                    rsetcanvassinfo.first();
                    parameters.put("supplier1", rsetcanvassinfo.getString("supplier1"));
                    parameters.put("supplier2", rsetcanvassinfo.getString("supplier2"));
                    parameters.put("supplier3", rsetcanvassinfo.getString("supplier3"));
                    parameters.put("terms1", rsetcanvassinfo.getString("terms1"));
                    parameters.put("terms2", rsetcanvassinfo.getString("terms2"));
                    parameters.put("terms3", rsetcanvassinfo.getString("terms3"));
                    parameters.put("canvassedby", rsetcanvassinfo.getString("canvassedby"));
                    parameters.put("recommendations", rsetcanvassinfo.getString("recommendations"));

                    conn.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                dbconnect conn2 = new dbconnect();

                try{
                    rsetcanvassitems = conn2.getCanvassitems(buff_canvassid);

                    while(rsetcanvassitems.next()){
                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("item_id", rsetcanvassitems.getInt("item_id"));
                        m.put("quantity", rsetcanvassitems.getString("quantity"));
                        m.put("description", rsetcanvassitems.getString("description"));
                        m.put("unit1", rsetcanvassitems.getString("unit1"));
                        m.put("amount1", rsetcanvassitems.getString("amount1"));
                        m.put("unit2", rsetcanvassitems.getString("unit2"));
                        m.put("amount2", rsetcanvassitems.getString("amount2"));
                        m.put("unit3", rsetcanvassitems.getString("unit3"));
                        m.put("amount3", rsetcanvassitems.getString("amount3"));

                        dataSource.add(m);
                    }
                    conn2.close();
                }catch (Exception x){
                    System.out.println(x.getMessage());
                }

                JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
                JasperReport report = null;

                try {
                    //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
                    //JasperDesign dis = JRXmlLoader.load(url1);

                    report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_canvasssheet.jrxml"));
                    //report = JasperCompileManager.compileReport(dis);
                    JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

                    pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
                    pnl_center.repaint();
                    pnl_center.revalidate();
                } catch (JRException jrException) {
                    jrException.printStackTrace();
                }
            }
        }
    }
}
