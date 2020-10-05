import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.Date;

public class panel_home_viewcheckvoucher extends JPanel {
    String suppliername = null;
    ResultSet rsetpurchaseitems, rsetpurchaseinfo, rsetsupplierinfo, rset, rsetbankinfo, rsetsum;
    String datenow, accountname, accountnum, bankname, bankaccountname, bankaccountnum, s, timenow;
    int voucherid;
    Date date;

    public panel_home_viewcheckvoucher(String poid, String checknum) throws Exception {
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
            rsetsum = conn.getSum(poid);

            rsetpurchaseinfo.first();
            rsetsupplierinfo.first();
            rsetsum.first();

            rsetbankinfo = conn.getBankinfo(rsetpurchaseinfo.getString("bankchosen"));

            rsetbankinfo.first();

            Float num = rsetsum.getFloat("sum");
            int left = (int)Math.floor(num);
            int right = (int)Math.floor((num-left)*100.0f);

            s = "The amount of " + EnglishNumberToWords.convert(left) + " and " + EnglishNumberToWords.convert(right) + " cents only";

            accountname = rsetsupplierinfo.getString("accountname");
            accountnum = rsetsupplierinfo.getString("accountnum");
            bankname = rsetbankinfo.getString("bankname");
            bankaccountname = rsetbankinfo.getString("accountname");
            bankaccountnum = rsetbankinfo.getString("accountnum");

            dbconnect conn3 = new dbconnect();
            int i = 0;

            rset = conn3.checkDuplicatevouchercheck(poid);

            while (rset.next())
                i++;

            if (i == 0)
            {
                String time;
                time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);

                conn3.addVouchercheck(poid, datenow, suppliername, accountname, accountnum, time, checknum);
                rset = conn3.checkDuplicatevouchercheck(poid);
                rset.first();
                voucherid = rset.getInt("vouchercheck_id");
            }
            else if (i > 0)
            {
                rset.first();
                voucherid = rset.getInt("vouchercheck_id");
            }

            conn3.close();

            parameters.put("voucherid", voucherid);
            parameters.put("date", datenow);
            parameters.put("suppliername", suppliername);
            parameters.put("preparedby", rsetpurchaseinfo.getString("preparedby"));
            parameters.put("bankname", bankname);
            parameters.put("bankaccountname", bankaccountname);
            parameters.put("bankaccountnum", bankaccountnum);
            parameters.put("accountname", accountname);
            parameters.put("accountnum", accountnum);
            parameters.put("ponum", poid);
            parameters.put("numtowords", s);
            parameters.put("time", timenow);
            parameters.put("checknum", rsetpurchaseinfo.getString("checknum"));

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
