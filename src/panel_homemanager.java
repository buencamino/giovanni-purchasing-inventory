import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class panel_homemanager extends JPanel {
    JPanel pnl_notifications, pnl_west;
    JLabel lbl_approvalreq, lbl_countreq;
    JButton btn_viewreq, btn_viewcanvass, btn_viewpurchaseorder;
    ResultSet rset;

    public panel_homemanager() throws Exception {
        rset = null;

        setLayout(new GridLayout(0,2));

        HandleControlButton control = new HandleControlButton();

        pnl_notifications = new JPanel();
        pnl_notifications.setLayout(new GridBagLayout());
        pnl_west = new JPanel();

        lbl_approvalreq = new JLabel("Pending requisition slips for approval ");
        lbl_countreq = new JLabel();

        btn_viewreq = new JButton("View");
        btn_viewreq.setPreferredSize(new Dimension(75, 20));
        btn_viewreq.addActionListener(control);
        btn_viewcanvass = new JButton("View Canvass Sheets");
        btn_viewcanvass.addActionListener(control);
        btn_viewpurchaseorder = new JButton("View Purchase Orders");
        btn_viewpurchaseorder.addActionListener(control);

        GridBagConstraints c = new GridBagConstraints();

        setLabels();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,0,25);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_notifications.add(lbl_approvalreq, c);

        c.gridx = 1;
        pnl_notifications.add(lbl_countreq, c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_START;
        pnl_notifications.add(btn_viewreq, c);

        //second row
        c.gridx = 2;
        c.gridy = 1;
        pnl_notifications.add(btn_viewcanvass, c);

        //third row
        c.gridx = 2;
        c.gridy = 2;
        pnl_notifications.add(btn_viewpurchaseorder, c);

        add(pnl_west);
        add(pnl_notifications);
    }

    private void setLabels() throws Exception {
        Integer count;

        dbconnect conn = new dbconnect();

        rset = conn.getPendingrequisitioncount();

        rset.first();
        count = rset.getInt(1);

        if (count == 0) {
            lbl_countreq.setForeground(Color.GRAY);
            btn_viewreq.setEnabled(false);
        }
        else if (count > 0)
        {
            lbl_countreq.setForeground(new Color(12,173,76));
            btn_viewreq.setEnabled(true);
        }

        lbl_countreq.setText(String.valueOf(count));
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_viewreq) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_approvalrequisition(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }

            if (source == btn_viewcanvass) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_viewcanvass(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }

            if (source == btn_viewpurchaseorder) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_viewpurchaseorder(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}
