import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class panel_home_viewcashvoucher extends JPanel {
    Date date;
    String datenow, suppliername, s, project, timenow;
    ResultSet rsetpurchaseitems, rsetpurchaseinfo, rsetsum, rset, rsetproject;
    int voucherid;

    public panel_home_viewcashvoucher(String poid)
    {
        setLayout(new BorderLayout());

        date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        datenow = formatter.format(date);

        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String timenow = dateFormat.format(new Date()).toString();

        removeAll();
        repaint();
        revalidate();

        dbconnect conn2 = new dbconnect();

        try {
            rsetpurchaseitems = conn2.getPurchaseorderitems(poid);
            rsetproject = conn2.getProject(poid);
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
            rsetproject.first();

            suppliername = rsetpurchaseitems.getString("suppliername");
            project = rsetproject.getString("projectname");
            conn2.close();
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }

        try {
            rsetpurchaseinfo = conn.getPurchaseorderinfo(poid);
            rsetsum = conn.getSum(poid);

            rsetpurchaseinfo.first();
            rsetsum.first();

            Float num = rsetsum.getFloat("sum");
            int left = (int)Math.floor(num);
            int right = (int)Math.floor((num-left)*100.0f);

            s = "IN WORDS: " + EnglishNumberToWords.convert(left) + " and " + EnglishNumberToWords.convert(right) + " cents only";

            dbconnect conn3 = new dbconnect();
            int i = 0;

            rset = conn3.checkDuplicatevouchercash(poid);

            while (rset.next())
                i++;

            if (i == 0)
            {
                String time;
                time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);

                conn3.addVouchercash(poid, datenow, suppliername, project, rsetpurchaseinfo.getString("preparedby"), time);
                rset = conn3.checkDuplicatevouchercash(poid);
                rset.first();
                voucherid = rset.getInt("vouchercash_id");
            }
            else if (i > 0)
            {
                rset.first();
                voucherid = rset.getInt("vouchercash_id");
            }
            
            conn3.close();

            parameters.put("voucherid", voucherid);
            parameters.put("date", datenow);
            parameters.put("suppliername", suppliername);
            parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));
            parameters.put("numtowords", s);
            parameters.put("project", project);
            parameters.put("time", timenow);

            conn.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        JRDataSource jrDatasource = new JRBeanCollectionDataSource(dataSource);
        JasperReport report = null;

        try {
            //InputStream url1 = getClass().getResourceAsStream("./giovanni_requisitionslip.jrxml");
            //JasperDesign dis = JRXmlLoader.load(url1);

            report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_vouchercash.jrxml"));
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
