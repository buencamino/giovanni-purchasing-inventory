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

public class panel_viewpurchaseorder extends JPanel {
    JPanel pnl_north, pnl_center;
    JTable tbl_purchaseorder;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset, rsetpurchaseinfo, rsetpurchaseitems;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    JButton btn_supplier1, btn_supplier2, btn_supplier3, btn_generate;
    String buff_purchaseid;
    Vector<Vector<Object>> data;
    Vector<Object> record;

    public panel_viewpurchaseorder() throws Exception {
        setLayout(new BorderLayout());

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("PO #");
        headers.add("Canvass #");
        headers.add("Project Name");
        headers.add("Prepared By");
        headers.add("Date");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getPurchaseorder();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_purchaseorder = new JTable(tablemodel);
        tbl_purchaseorder.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_purchaseorder);
        sp.setPreferredSize(new Dimension(900,100));

        listselectionmodel = tbl_purchaseorder.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_purchaseorder.setSelectionModel(listselectionmodel);

        /*
        btn_supplier1 = new JButton("View Supplier 1");
        btn_supplier1.addActionListener(control);
        btn_supplier1.setPreferredSize(new Dimension(300,25));
        btn_supplier1.setEnabled(false);

        btn_supplier2 = new JButton("View Supplier 2");
        btn_supplier2.addActionListener(control);
        btn_supplier2.setPreferredSize(new Dimension(300,25));
        btn_supplier2.setEnabled(false);

        btn_supplier3 = new JButton("View Supplier 3");
        btn_supplier3.addActionListener(control);
        btn_supplier3.setPreferredSize(new Dimension(300,25));
        btn_supplier3.setEnabled(false);
*/

        btn_generate = new JButton("Generate PO");
        btn_generate.addActionListener(control);
        btn_generate.setPreferredSize(new Dimension(900,25));
        btn_generate.setEnabled(false);

        pnl_north = new JPanel(new GridBagLayout());
        pnl_center = new JPanel(new BorderLayout());

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,0,25);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 3;
        pnl_north.add(sp, c);

        //second row

        /*
        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        pnl_north.add(btn_supplier1, c);

        c.gridx = 1;
        pnl_north.add(btn_supplier2, c);

        c.gridx = 2;
        pnl_north.add(btn_supplier3, c);
         */

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        pnl_north.add(btn_generate, c);

        add(pnl_north, BorderLayout.NORTH);
        add(pnl_center, BorderLayout.CENTER);
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("purchaseorder_id"));
            record.add(rset1.getString("canvass_id"));
            record.add(rset1.getString("projectname"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("date"));

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

                x = tbl_purchaseorder.getValueAt(maxIndex, 0);
                buff_purchaseid = x.toString();
                /*
                btn_supplier1.setEnabled(true);
                btn_supplier2.setEnabled(true);
                btn_supplier3.setEnabled(true);
                 */
                btn_generate.setEnabled(true);

            }
        }
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            /*
            if (source == btn_supplier1) {
                dbconnect conn3 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn3.getPurchaseorderitems(buff_purchaseid, "1");

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    String suppliername = null;

                    pnl_center.removeAll();
                    pnl_center.repaint();
                    pnl_center.revalidate();
                    dbconnect conn = new dbconnect();

                    ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

                    Map<String, Object> parameters = new HashMap<String, Object>();

                    dbconnect conn2 = new dbconnect();

                    try{
                        rsetpurchaseitems = conn2.getPurchaseorderitems(buff_purchaseid, "1");

                        while(rsetpurchaseitems.next()){
                            Map<String, Object> m = new HashMap<String, Object>();
                            m.put("quantity", rsetpurchaseitems.getString("quantity"));
                            m.put("unit", rsetpurchaseitems.getString("unit"));
                            m.put("description", rsetpurchaseitems.getString("description"));
                            m.put("unitprice", rsetpurchaseitems.getString("unitprice"));
                            m.put("amount", rsetpurchaseitems.getString("amount"));

                            dataSource.add(m);
                        }
                        rsetpurchaseitems.first();

                        suppliername = rsetpurchaseitems.getString("suppliername");
                        conn2.close();
                    }catch (Exception x){
                        System.out.println(x.getMessage());
                    }

                    try {
                        rsetpurchaseinfo = conn.getPurchaseorderinfo(buff_purchaseid);

                        rsetpurchaseinfo.first();
                        parameters.put("projectname", rsetpurchaseinfo.getString("projectname"));
                        parameters.put("suppliername", suppliername);
                        parameters.put("purchaseordernum", rsetpurchaseinfo.getString("purchaseorder_id"));
                        parameters.put("date", rsetpurchaseinfo.getString("newdate"));
                        parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));

                        conn.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
                    JasperReport report = null;

                    try {
                        //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
                        //JasperDesign dis = JRXmlLoader.load(url1);

                        report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_purchaseorder.jrxml"));
                        //report = JasperCompileManager.compileReport(dis);
                        JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

                        pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
                        pnl_center.repaint();
                        pnl_center.revalidate();
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 1st supplier for this purchase order!", "No 1st Supplier", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (source == btn_supplier2) {
                dbconnect conn2 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn2.getPurchaseorderitems(buff_purchaseid, "2");

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    String suppliername = null;

                    pnl_center.removeAll();
                    pnl_center.repaint();
                    pnl_center.revalidate();
                    dbconnect conn = new dbconnect();

                    ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

                    Map<String, Object> parameters = new HashMap<String, Object>();

                    try{
                        rsetpurchaseitems.first();
                        do{
                            Map<String, Object> m = new HashMap<String, Object>();
                            m.put("quantity", rsetpurchaseitems.getString("quantity"));
                            m.put("unit", rsetpurchaseitems.getString("unit"));
                            m.put("description", rsetpurchaseitems.getString("description"));
                            m.put("unitprice", rsetpurchaseitems.getString("unitprice"));
                            m.put("amount", rsetpurchaseitems.getString("amount"));

                            dataSource.add(m);
                        }while(rsetpurchaseitems.next());
                        rsetpurchaseitems.first();

                        suppliername = rsetpurchaseitems.getString("suppliername");
                        conn2.close();
                    }catch (Exception x){
                        System.out.println(x.getMessage());
                    }

                    try {
                        rsetpurchaseinfo = conn.getPurchaseorderinfo(buff_purchaseid);

                        rsetpurchaseinfo.first();
                        parameters.put("projectname", rsetpurchaseinfo.getString("projectname"));
                        parameters.put("suppliername", suppliername);
                        parameters.put("purchaseordernum", rsetpurchaseinfo.getString("purchaseorder_id"));
                        parameters.put("date", rsetpurchaseinfo.getString("newdate"));
                        parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));

                        conn.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
                    JasperReport report = null;

                    try {
                        //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
                        //JasperDesign dis = JRXmlLoader.load(url1);

                        report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_purchaseorder.jrxml"));
                        //report = JasperCompileManager.compileReport(dis);
                        JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

                        pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
                        pnl_center.repaint();
                        pnl_center.revalidate();
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 2nd supplier for this purchase order!", "No 2nd Supplier", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (source == btn_supplier3) {
                dbconnect conn2 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn2.getPurchaseorderitems(buff_purchaseid, "3");

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    String suppliername = null;

                    pnl_center.removeAll();
                    pnl_center.repaint();
                    pnl_center.revalidate();
                    dbconnect conn = new dbconnect();

                    ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

                    Map<String, Object> parameters = new HashMap<String, Object>();

                    try{
                        rsetpurchaseitems.first();
                        do{
                            Map<String, Object> m = new HashMap<String, Object>();
                            m.put("quantity", rsetpurchaseitems.getString("quantity"));
                            m.put("unit", rsetpurchaseitems.getString("unit"));
                            m.put("description", rsetpurchaseitems.getString("description"));
                            m.put("unitprice", rsetpurchaseitems.getString("unitprice"));
                            m.put("amount", rsetpurchaseitems.getString("amount"));

                            dataSource.add(m);
                        }while(rsetpurchaseitems.next());
                        rsetpurchaseitems.first();

                        suppliername = rsetpurchaseitems.getString("suppliername");
                        conn2.close();
                    }catch (Exception x){
                        System.out.println(x.getMessage());
                    }

                    try {
                        rsetpurchaseinfo = conn.getPurchaseorderinfo(buff_purchaseid);

                        rsetpurchaseinfo.first();
                        parameters.put("projectname", rsetpurchaseinfo.getString("projectname"));
                        parameters.put("suppliername", suppliername);
                        parameters.put("purchaseordernum", rsetpurchaseinfo.getString("purchaseorder_id"));
                        parameters.put("date", rsetpurchaseinfo.getString("newdate"));
                        parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));

                        conn.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
                    JasperReport report = null;

                    try {
                        //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
                        //JasperDesign dis = JRXmlLoader.load(url1);

                        report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_purchaseorder.jrxml"));
                        //report = JasperCompileManager.compileReport(dis);
                        JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

                        pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
                        pnl_center.repaint();
                        pnl_center.revalidate();
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 3rd supplier for this purchase order!", "No 3rd Supplier", JOptionPane.ERROR_MESSAGE);
                }
            }
            */

            if (source == btn_generate) {
                dbconnect conn2 = new dbconnect();
                int i;
                i = 0;

                try {
                    rsetpurchaseitems = conn2.getPurchaseorderitems(buff_purchaseid);

                    while (rsetpurchaseitems.next())
                    {
                        i++;
                    };
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (i > 0)
                {
                    String suppliername = null;

                    pnl_center.removeAll();
                    pnl_center.repaint();
                    pnl_center.revalidate();
                    dbconnect conn = new dbconnect();

                    ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

                    Map<String, Object> parameters = new HashMap<String, Object>();

                    try{
                        rsetpurchaseitems.first();
                        do{
                            Map<String, Object> m = new HashMap<String, Object>();
                            m.put("quantity", rsetpurchaseitems.getString("quantity"));
                            m.put("unit", rsetpurchaseitems.getString("unit"));
                            m.put("description", rsetpurchaseitems.getString("description"));
                            m.put("unitprice", rsetpurchaseitems.getString("unitprice"));
                            m.put("amount", rsetpurchaseitems.getString("amount"));

                            dataSource.add(m);
                        }while(rsetpurchaseitems.next());
                        rsetpurchaseitems.first();

                        suppliername = rsetpurchaseitems.getString("suppliername");
                        conn2.close();
                    }catch (Exception x){
                        System.out.println(x.getMessage());
                    }

                    try {
                        rsetpurchaseinfo = conn.getPurchaseorderinfo(buff_purchaseid);

                        rsetpurchaseinfo.first();
                        parameters.put("projectname", rsetpurchaseinfo.getString("projectname"));
                        parameters.put("suppliername", suppliername);
                        parameters.put("purchaseordernum", rsetpurchaseinfo.getString("purchaseorder_id"));
                        parameters.put("date", rsetpurchaseinfo.getString("newdate"));
                        parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));

                        conn.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
                    JasperReport report = null;

                    try {
                        //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
                        //JasperDesign dis = JRXmlLoader.load(url1);

                        report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_purchaseorder.jrxml"));
                        //report = JasperCompileManager.compileReport(dis);
                        JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

                        pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
                        pnl_center.repaint();
                        pnl_center.revalidate();
                    } catch (JRException jrException) {
                        jrException.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "There is no existing 3rd supplier for this purchase order!", "No 3rd Supplier", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
