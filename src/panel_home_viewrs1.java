import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class panel_home_viewrs1 extends JPanel {
    ResultSet rsetreqinfo, rsetreqitems;

    public panel_home_viewrs1(String reqid)
    {
        setLayout(new BorderLayout());

        dbconnect conn = new dbconnect();

        ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

        Map<String, Object> parameters = new HashMap<String, Object>();

        try {
            rsetreqinfo = conn.getReqinfo(reqid);

            rsetreqinfo.first();
            parameters.put("project", rsetreqinfo.getString("project"));
            parameters.put("location", rsetreqinfo.getString("location"));
            parameters.put("reqnum", reqid);
            parameters.put("date", rsetreqinfo.getString("newdate"));
            parameters.put("preparedby", rsetreqinfo.getString("preparedby"));
            parameters.put("deliverytype", rsetreqinfo.getString("deliverytype"));
            parameters.put("purpose", rsetreqinfo.getString("purpose"));

            conn.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        dbconnect conn2 = new dbconnect();

        try{
            rsetreqitems = conn2.getReqitems(reqid);

            while(rsetreqitems.next()){
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("quantity", rsetreqitems.getInt("quantity"));
                m.put("units", rsetreqitems.getString("units"));
                m.put("description", rsetreqitems.getString("description"));
                m.put("category", rsetreqitems.getString("category"));
                m.put("enduser", rsetreqitems.getString("enduser"));

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

            report = JasperCompileManager.compileReport(getClass().getResourceAsStream("giovanni_requisitionslip.jrxml"));
            //report = JasperCompileManager.compileReport(dis);
            JasperPrint filledReport = JasperFillManager.fillReport(report, parameters, jrDatasource);

            add(new JRViewer(filledReport), BorderLayout.CENTER);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
