import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.sql.*;
import java.util.Date;

public class panel_completepo extends JPanel {
    JTable tbl_po;
    DefaultTableModel tablemodel;
    Vector<String> headers = new Vector<String>();
    ResultSet rset;
    JScrollPane sp;
    ListSelectionModel listselectionmodel;
    Vector<Vector<Object>> data;
    Vector<Object> record;
    JLabel lbl_title, lbl_title1, lbl_path, lbl_drnum, lbl_checker;
    JButton btn_browse, btn_complete;
    String path, buff_poid;
    JTextField text_drnum;
    JComboBox combo_checker;

    public panel_completepo() throws Exception {
        setLayout(new GridBagLayout());

        buff_poid = "";
        path = "";

        selectionHandler handler = new selectionHandler();
        HandleControlButton control = new HandleControlButton();
        GridBagConstraints c = new GridBagConstraints();

        lbl_title = new JLabel("Please choose a purchase order");
        lbl_title1 = new JLabel("Attach scanned image/deposit slip");
        lbl_path = new JLabel("File Chosen : ");
        lbl_drnum = new JLabel ("DR No./Rec. No. :");
        lbl_checker = new JLabel("Watchman/Checker :");

        btn_browse = new JButton("Browse");
        btn_complete = new JButton("Attach Image and Complete Purchase Order");
        btn_complete.setPreferredSize(new Dimension(500, 25));
        btn_browse.addActionListener(control);
        btn_complete.addActionListener(control);

        text_drnum = new JTextField(10);

        text_drnum.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_drnum.getText().length() >= 100 )
                    e.consume();
            }
        });

        dbconnect conn2 = new dbconnect();

        rset = conn2.getCheckerlist();
        combo_checker = new JComboBox();

        while (rset.next())
        {
            combo_checker.addItem(rset.getString("checkername"));
        }

        combo_checker.setSelectedIndex(-1);

        btn_browse.setEnabled(false);
        btn_complete.setEnabled(false);

        tablemodel = new DefaultTableModel();
        headers.add("PO #");
        headers.add("Prepared By");
        headers.add("Project Name");
        headers.add("Terms");
        headers.add("Date");
        headers.add("Time");
        headers.add("Status");

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getIncompletepo();
            refreshTable(rset);
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_po = new JTable(tablemodel);
        tbl_po.setDefaultEditor(Object.class, null);
        sp = new JScrollPane(tbl_po);
        sp.setPreferredSize(new Dimension(800,300));

        listselectionmodel = tbl_po.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_po.setSelectionModel(listselectionmodel);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(5, 0, 0, 25);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        add(sp, c);

        //3rd row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_drnum, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_drnum, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_checker, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(combo_checker, c);

        //5th row
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 4;
        c.insets = new Insets(20, 0, 0, 25);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title1, c);

        //6th row
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 1;
        c.insets = new Insets(5, 0, 0, 25);
        add(btn_browse, c);

        c.gridx = 1;
        add(lbl_path, c);

        //7th row
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 3;
        c.insets = new Insets(30, 0, 0, 25);
        c.anchor = GridBagConstraints.CENTER;
        add(btn_complete, c);
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

                Object x, y, z;
                String terms1 = null;

                x = tbl_po.getValueAt(maxIndex, 0);
                buff_poid = x.toString();

                btn_browse.setEnabled(true);
                btn_complete.setEnabled(true);
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next()) {
            record = new Vector<Object>();

            record.add(rset1.getString("purchaseorder_id"));
            record.add(rset1.getString("preparedby"));
            record.add(rset1.getString("projectname"));

            dbconnect conn = new dbconnect();
            String terms = null;

            terms = conn.checkVouchertype(rset1.getString("purchaseorder_id"));
            conn.close();

            record.add(terms);

            record.add(rset1.getString("newdate"));
            record.add(rset1.getString("timerecorded"));

            if (rset1.getInt("complete") == 0)
                record.add("Incomplete");
            else if (rset1.getInt("complete") == 1)
                record.add("Complete");

            data.addElement(record);
        }

        tablemodel.setDataVector(data, headers);
        tablemodel.fireTableDataChanged();
    }

    class HandleControlButton extends Component implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btn_browse) {
                String userDirLocation = System.getProperty("user.dir");
                //String userDirLocation = System.getenv("SystemDrive");
                File userDir = new File(userDirLocation);

                JFileChooser fc = new JFileChooser(userDir);

                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.showOpenDialog(this);

                File f = fc.getSelectedFile();
                path = f.getAbsolutePath();
                path = path.replace('\\', '/');
                //path = path + "/UYTITBACKUP_" + date + ".sql";
                lbl_path.setText("File Chosen : " + path);
                btn_complete.setEnabled(true);
            }

            if (source == btn_complete) {
                if (!(!(path.substring(path.length() - 4).equals(".jpg")) || path.equals("") || buff_poid.equals("")))
                {
                    String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
                    String newpath = System.getProperty("user.dir") + "/" + date + "PO" + buff_poid + "scan.jpg";
                    newpath = newpath.replace('\\', '/');

                    File originalfile = new File(path);
                    File destinationfile = new File(newpath);

                    try {
                        Files.copy(originalfile.toPath(), destinationfile.toPath());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    dbconnect conn2 = new dbconnect();

                    try {
                        conn2.Completepo(buff_poid, newpath, text_drnum.getText(), combo_checker.getSelectedItem().toString());
                        conn2.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(this, "Purchase order completed", "Information Submitted", JOptionPane.INFORMATION_MESSAGE);

                    mainFrame.pnl_center.removeAll();
                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();

                    mainFrame.pnl_center.add(new panel_homeaccountant(), BorderLayout.CENTER);

                    mainFrame.pnl_center.repaint();
                    mainFrame.pnl_center.revalidate();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Please attach a file to store in database/choose a valid .jpg file.", "Incomplete Information", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
