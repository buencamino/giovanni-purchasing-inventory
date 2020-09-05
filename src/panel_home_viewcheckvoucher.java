import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.Date;

public class panel_home_viewcheckvoucher extends JPanel {
    String suppliername = null;
    ResultSet rsetpurchaseitems, rsetpurchaseinfo, rsetsupplierinfo;
    String datenow;
    Date date;

    public panel_home_viewcheckvoucher(String poid)
    {
        setLayout(new BorderLayout());

        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        datenow = formatter.format(date);

        removeAll();
        repaint();
        revalidate();

        dbconnect conn2 = new dbconnect();

        try {
            rsetpurchaseitems = conn2.getPurchaseorderitems(poid);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        dbconnect conn = new dbconnect();

        ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

        Map<String, Object> parameters = new HashMap<String, Object>();

        try {
            rsetpurchaseitems.first();
            do {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("description", rsetpurchaseitems.getString("description"));
                m.put("amount", rsetpurchaseitems.getDouble("amount"));

                dataSource.add(m);
            } while (rsetpurchaseitems.next());
            rsetpurchaseitems.first();

            suppliername = rsetpurchaseitems.getString("suppliername");
            conn2.close();
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }

        try {
            rsetpurchaseinfo = conn.getPurchaseorderinfo(poid);
            rsetsupplierinfo = conn.getSupplierinfo(suppliername);

            rsetpurchaseinfo.first();
            rsetsupplierinfo.first();
            parameters.put("date", datenow);
            parameters.put("suppliername", suppliername);
            parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));
            parameters.put("accountname", rsetsupplierinfo.getString("accountname"));
            parameters.put("accountnum", rsetsupplierinfo.getString("accountnum"));

            conn.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
        JasperReport report = null;

        try {
            //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
            //JasperDesign dis = JRXmlLoader.load(url1);

            report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_vouchercheck.jrxml"));
            //report = JasperCompileManager.compileReport(dis);
            JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

            add(new JRViewer(filledReport), BorderLayout.CENTER);
            repaint();
            revalidate();
        } catch (JRException jrException) {
            jrException.printStackTrace();
        }

    }
}
