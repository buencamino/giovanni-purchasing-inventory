import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class panel_home_viewpo1 extends JPanel {
    ResultSet rsetpurchaseitems, rsetpurchaseinfo;
    JPanel pnl_center, pnl_south;
    JButton btn_back;

    public panel_home_viewpo1(String purchaseid) throws JRException {
        HandleControlButton control = new HandleControlButton();

        setLayout(new BorderLayout());

        pnl_center = new JPanel(new BorderLayout());
        pnl_south = new JPanel(new FlowLayout());

        btn_back = new JButton("Back");
        btn_back.addActionListener(control);

        String suppliername = null;
        dbconnect conn2 = new dbconnect();
        int i;
        i = 0;

        try {
            rsetpurchaseitems = conn2.getPurchaseorderitems(purchaseid);

            while (rsetpurchaseitems.next()) {
                i++;
            }
            ;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (i > 0) {
            removeAll();
            repaint();
            revalidate();
            dbconnect conn = new dbconnect();

            ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

            Map<String, Object> parameters = new HashMap<String, Object>();

            try {
                rsetpurchaseitems.first();
                do {
                    Map<String, Object> m = new HashMap<String, Object>();
                    m.put("quantity", rsetpurchaseitems.getString("quantity"));
                    m.put("unit", rsetpurchaseitems.getString("unit"));
                    m.put("description", rsetpurchaseitems.getString("description"));
                    m.put("unitprice", rsetpurchaseitems.getString("unitprice"));
                    m.put("amount", rsetpurchaseitems.getString("amount"));

                    dataSource.add(m);
                } while (rsetpurchaseitems.next());
                rsetpurchaseitems.first();

                suppliername = rsetpurchaseitems.getString("suppliername");
                conn2.close();
            } catch (Exception x) {
                System.out.println(x.getMessage());
            }

            try {
                rsetpurchaseinfo = conn.getPurchaseorderinfo(purchaseid);

                rsetpurchaseinfo.first();
                parameters.put("projectname", rsetpurchaseinfo.getString("projectname"));
                parameters.put("suppliername", suppliername);
                parameters.put("purchaseordernum", rsetpurchaseinfo.getString("purchaseorder_id"));
                parameters.put("date", rsetpurchaseinfo.getString("newdate"));
                parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));
                parameters.put("time", rsetpurchaseinfo.getString("timerecorded"));

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
                repaint();
                revalidate();
            } catch (JRException jrException) {
                jrException.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "There is no existing 3rd supplier for this purchase order!", "No 3rd Supplier", JOptionPane.ERROR_MESSAGE);
        }

        pnl_south.add(btn_back);

        add(pnl_center, BorderLayout.CENTER);
        add(pnl_south, BorderLayout.SOUTH);
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_back) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();


                try {
                    mainFrame.pnl_center.add(new panel_home_viewpo(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }

}
