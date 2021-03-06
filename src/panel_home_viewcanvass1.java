import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class panel_home_viewcanvass1 extends JPanel {
    ResultSet rsetcanvassinfo, rsetcanvassitems;
    JPanel pnl_center, pnl_south;
    JButton btn_back;

    public panel_home_viewcanvass1(String canvassid) throws JRException {
        HandleControlButton control = new HandleControlButton();

        setLayout(new BorderLayout());

        pnl_center = new JPanel(new BorderLayout());
        pnl_south = new JPanel(new FlowLayout());

        btn_back = new JButton("Back");
        btn_back.addActionListener(control);

        dbconnect conn = new dbconnect();

        ArrayList<Map<String, ?>> dataSource = new ArrayList<Map<String, ?>>();

        Map<String, Object> parameters = new HashMap<String, Object>();

        try {
            rsetcanvassinfo = conn.getCanvassinfo(canvassid);

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
            rsetcanvassitems = conn2.getCanvassitems(canvassid);

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

            //JRViewer viewreport = new JRViewer(filledReport);
            //remove entire toolbar
            //viewreport.remove(0);
            //remove only save button
            //((JPanel)viewreport.getComponent(0)).remove(0);
            //doesn't work
            //((JPanel)viewreport.getComponent(0)).remove(1);
            //pnl_center.add(viewreport, BorderLayout.CENTER);

            pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
        } catch (JRException e) {
            e.printStackTrace();
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
                    mainFrame.pnl_center.add(new panel_home_viewcanvass(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}
