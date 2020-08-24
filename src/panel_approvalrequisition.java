import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.*;
import java.sql.*;

public class panel_approvalrequisition extends JPanel {
    JPanel pnl_north, pnl_center, pnl_south;
    JTable tbl_pendingreq;
    JButton btn_view, btn_approve;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    Vector<Vector<Object>> data;
    Vector<Object> record;
    ResultSet rset, rsetreqinfo, rsetreqitems;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    String buff_reqid;

    public panel_approvalrequisition() throws Exception {
        setLayout(new BorderLayout());

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Req Num");
        headers.add("Project");
        headers.add("Prepared By");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getPendingreqlist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_pendingreq = new JTable(tablemodel);
        tbl_pendingreq.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_pendingreq);
        sp.setPreferredSize(new Dimension(600,100));

        listselectionmodel = tbl_pendingreq.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_pendingreq.setSelectionModel(listselectionmodel);

        btn_approve = new JButton("Approve Requisition Slip");
        btn_view = new JButton("View Requisition Slip");
        btn_view.addActionListener(control);
        btn_view.setPreferredSize(new Dimension(600,25));
        btn_view.setEnabled(false);
        btn_view.addActionListener(control);

        pnl_north = new JPanel(new GridBagLayout());
        pnl_center = new JPanel(new BorderLayout());
        pnl_south = new JPanel(new FlowLayout());

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

        pnl_south.add(btn_approve);

        add(pnl_north, BorderLayout.NORTH);
        add(pnl_center, BorderLayout.CENTER);
        add(pnl_south, BorderLayout.SOUTH);
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
                    rsetreqinfo = conn.getReqinfo(buff_reqid);

                    rsetreqinfo.first();
                    parameters.put("project", rsetreqinfo.getString("project"));
                    parameters.put("location", rsetreqinfo.getString("location"));
                    parameters.put("reqnum", buff_reqid);
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
                    rsetreqitems = conn2.getReqitems(buff_reqid);

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

                    pnl_center.add(new JRViewer(filledReport), BorderLayout.CENTER);
                    pnl_center.repaint();
                    pnl_center.revalidate();
                } catch (JRException jrException) {
                    jrException.printStackTrace();
                }

            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("requisition_num"));
            record.add(rset1.getString("project"));
            record.add(rset1.getString("preparedby"));

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

                x = tbl_pendingreq.getValueAt(maxIndex, 0);
                buff_reqid = x.toString();
                btn_view.setEnabled(true);
            }
        }
    }
}
