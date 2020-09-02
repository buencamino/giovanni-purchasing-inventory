import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class panel_homeengineer extends JPanel {
    JLabel lbl_welcome;
    JPanel pnl_top, pnl_center;
    JLabel lbl_title, lbl_rs, lbl_canvass, lbl_po;
    JButton btn_viewrs, btn_viewcanvass, btn_viewpo;

    public panel_homeengineer() {
        HandleControlButton control = new HandleControlButton();

        pnl_top = new JPanel();
        pnl_center = new JPanel();

        lbl_title = new JLabel("View Records");
        lbl_rs = new JLabel("Requisition Slips");
        lbl_canvass = new JLabel("Canvass Sheets");
        lbl_po = new JLabel("Purchase Orders");

        btn_viewrs = new JButton("View");
        btn_viewcanvass = new JButton("View");
        btn_viewpo = new JButton("View");

        btn_viewrs.setPreferredSize(new Dimension(100, 15));
        btn_viewcanvass.setPreferredSize(new Dimension(100, 15));
        btn_viewpo.setPreferredSize(new Dimension(100, 15));
        btn_viewrs.addActionListener(control);
        btn_viewcanvass.addActionListener(control);
        btn_viewpo.addActionListener(control);

        setLayout(new BorderLayout());
        pnl_top.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lbl_welcome = new JLabel("Welcome back " + form_login.fullname + "!");

        pnl_top.add(lbl_welcome);

        pnl_center.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 0, 20, 25);
        c.anchor = GridBagConstraints.LINE_START;
        pnl_center.add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 0, 0, 25);
        pnl_center.add(lbl_rs, c);

        c.gridx = 1;
        pnl_center.add(btn_viewrs, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        pnl_center.add(lbl_canvass, c);

        c.gridx = 1;
        pnl_center.add(btn_viewcanvass, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        pnl_center.add(lbl_po, c);

        c.gridx = 1;
        pnl_center.add(btn_viewpo, c);

        add(pnl_top, BorderLayout.NORTH);
        add(pnl_center, BorderLayout.CENTER);
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_viewrs) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_home_viewrs(), BorderLayout.CENTER);
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
                    mainFrame.pnl_center.add(new panel_home_viewcanvass(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }

            if (source == btn_viewpo) {
                mainFrame.pnl_center.removeAll();
                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();

                try {
                    mainFrame.pnl_center.add(new panel_home_viewpo(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                mainFrame.pnl_center.repaint();
                mainFrame.pnl_center.revalidate();
            }
        }
    }
}