import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class panel_homeaccountant extends JPanel {
    JPanel pnl_notifications, pnl_west;
    JButton btn_viewpurchaseorder;

    public panel_homeaccountant() {

        setLayout(new GridLayout(0, 2));

        HandleControlButton control = new HandleControlButton();

        pnl_notifications = new JPanel();
        pnl_notifications.setLayout(new GridBagLayout());
        pnl_west = new JPanel();

        btn_viewpurchaseorder = new JButton("View Purchase Orders");
        btn_viewpurchaseorder.addActionListener(control);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 0, 25);
        c.anchor = GridBagConstraints.LINE_END;
        pnl_notifications.add(btn_viewpurchaseorder, c);

        add(pnl_west);
        add(pnl_notifications);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btn_viewpurchaseorder) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_homeaccountant_viewpo(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}