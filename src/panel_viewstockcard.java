import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class panel_viewstockcard extends JPanel {
    ResultSet rsetstock, rsetstocktransaction;
    String begbalance;

    public panel_viewstockcard(String itemid)
    {
        setLayout(new BorderLayout());

        removeAll();
        repaint();
        revalidate();

        dbconnect conn2 = new dbconnect();

        try {
            rsetstock = conn2.getStockinfo(itemid);
            rsetstocktransaction = conn2.getStocktransaction(itemid);
            begbalance = conn2.getStockbegbalance(itemid);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

        Map<String, Object> parameters = new HashMap<String, Object>();

        try {
            rsetstocktransaction.first();
            do {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("date", rsetstocktransaction.getString("date"));
                m.put("drnum", rsetstocktransaction.getString("drnum"));
                m.put("receivedby", rsetstocktransaction.getString("receivedby"));
                m.put("quantityin", rsetstocktransaction.getString("quantityin"));
                m.put("issuedto", rsetstocktransaction.getString("issuedto"));
                m.put("quantityout", rsetstocktransaction.getString("quantityout"));
                m.put("balance", rsetstocktransaction.getString("balance"));
                m.put("conformedby", rsetstocktransaction.getString("conformedby"));
                m.put("purpose", rsetstocktransaction.getString("purpose"));

                dataSource.add(m);
            } while (rsetstocktransaction.next());
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }

        try {
            rsetstock.first();
            parameters.put("description", rsetstock.getString("description"));
            parameters.put("beginningbalance", begbalance);

            conn2.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
        JasperReport report = null;

        try {
            //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
            //JasperDesign dis = JRXmlLoader.load(url1);

            report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_stockcard.jrxml"));
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
