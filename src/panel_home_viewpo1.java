import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class panel_home_viewpo1 extends JPanel {
    ResultSet rsetpurchaseitems, rsetpurchaseinfo;

    public panel_home_viewpo1(String purchaseid, String suppliernum) throws JRException {
        setLayout(new BorderLayout());

        String suppliername = null;

        dbconnect conn = new dbconnect();

        ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

        Map<String, Object> parameters = new HashMap<String, Object>();

        dbconnect conn2 = new dbconnect();

        try {
            rsetpurchaseitems = conn2.getPurchaseorderitems(purchaseid, suppliernum);

            while (rsetpurchaseitems.next()) {
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
            add(new JRViewer(filledReport), BorderLayout.CENTER);
        } catch (JRException e) {
            e.printStackTrace();
        }

    }
}
