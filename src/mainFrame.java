import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class mainFrame {
    JFrame mainwindow;
    public static JPanel pnl_center;
    JMenuBar mb;
    JMenu menu1, menuforms, menumaintenance;
    JMenuItem menu1_home, menuforms_requisitionslip, menu1_exit, menumaintenance_project,
    menuforms_canvass, menuforms_purchaseorder, menumaintenance_supplier;

    public mainFrame() throws Exception {
        mainwindow = new JFrame("Giovanni Construction - Purchasing and Inventory System");
        mainwindow.setBounds(10,0,1100,685);
        mainwindow.setLayout(new BorderLayout());

        HandleControlButton control = new HandleControlButton();

        mb = new JMenuBar();
        menu1 = new JMenu("Function");
        menuforms = new JMenu("Forms");
        menumaintenance = new JMenu("Maintenance");
        menu1_home = new JMenuItem("Home");
        menu1_exit = new JMenuItem("Exit");
        menuforms_requisitionslip = new JMenuItem("Requisition Slip");
        menumaintenance_project = new JMenuItem("Update Projects");
        menuforms_canvass = new JMenuItem("Canvass Sheet");
        menuforms_purchaseorder = new JMenuItem("Purchase Order");
        menumaintenance_supplier = new JMenuItem("Update Suppliers");

        menu1_home.addActionListener(control);
        menu1_exit.addActionListener(control);
        menuforms_requisitionslip.addActionListener(control);
        menumaintenance_project.addActionListener(control);
        menuforms_canvass.addActionListener(control);
        menuforms_purchaseorder.addActionListener(control);
        menumaintenance_supplier.addActionListener(control);

        menu1.add(menu1_home);
        menu1.addSeparator();
        menu1.add(menu1_exit);
        menuforms.add(menuforms_requisitionslip);
        menuforms.add(menuforms_canvass);
        menuforms.add(menuforms_purchaseorder);
        menumaintenance.add(menumaintenance_project);
        menumaintenance.add(menumaintenance_supplier);

        mb.add(menu1);

        if (form_login.userlevel == 1) {
            mb.add(menuforms);
            mb.add(menumaintenance);
        }

        pnl_center = new JPanel();
        pnl_center.setLayout(new BorderLayout());

        if (form_login.userlevel == 1)
            pnl_center.add(new panel_homeengineer(), BorderLayout.CENTER);
        else if (form_login.userlevel == 3)
            pnl_center.add(new panel_homemanager(), BorderLayout.CENTER);

        mainwindow.add(pnl_center, BorderLayout.CENTER);
        mainwindow.setJMenuBar(mb);
        mainwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainwindow.setVisible(true);
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if(source == menu1_home)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                if (form_login.userlevel == 1)
                    pnl_center.add(new panel_homeengineer(), BorderLayout.CENTER);
                else if (form_login.userlevel == 3) {
                    try {
                        pnl_center.add(new panel_homemanager(), BorderLayout.CENTER);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if(source == menuforms_requisitionslip)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                try {
                    pnl_center.add(new panel_requisitionslip(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if(source == menumaintenance_project)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                try {
                    pnl_center.add(new panel_projects(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if(source == menuforms_canvass)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();

                try {
                    pnl_center.add(new panel_canvasssheet(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if(source == menuforms_purchaseorder)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();


                try {
                    pnl_center.add(new panel_purchaseorder(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if(source == menumaintenance_supplier)
            {
                pnl_center.removeAll();
                pnl_center.repaint();
                pnl_center.revalidate();


                try {
                    pnl_center.add(new panel_suppliers(), BorderLayout.CENTER);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                pnl_center.repaint();
                pnl_center.revalidate();
            }

            if (source == menu1_exit) {
                System.exit(0);
            }
        }
    }
}
