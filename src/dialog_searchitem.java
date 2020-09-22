import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.Vector;

public class dialog_searchitem extends JDialog {
    JButton btn_search, btn_select;
    JTextField text_search;
    JTable tbl_stocks;
    JScrollPane sp;
    Vector<String> headers = new Vector<String>();
    Vector<Vector<Object>> data;
    Vector<Object> record;
    DefaultTableModel tablemodel;
    ListSelectionModel listselectionmodel;
    String buff_description, buff_stockid;
    ResultSet rset;

    public dialog_searchitem() throws Exception {
        setLayout(new GridBagLayout());
        setTitle("Stock Search");

        HandleControlButton control = new HandleControlButton();
        selectionHandler handler = new selectionHandler();

        tablemodel = new DefaultTableModel();
        headers.add("Stock ID");
        headers.add("Stock Name");

        text_search = new JTextField(25);
        btn_search = new JButton("Search");
        btn_search.addActionListener(control);
        btn_select = new JButton("Select Item");
        btn_select.setPreferredSize(new Dimension(350, 25));
        btn_select.setEnabled(false);
        btn_select.addActionListener(control);

        text_search.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_search.getText().length() >= 100 )
                    e.consume();
            }
        });

        dbconnect conn = new dbconnect();

        try
        {
            rset = conn.getStocklist();
            refreshTable(rset);
            conn.close();
        }
        catch (Exception j)
        {
            throw j;
        }

        tbl_stocks = new JTable(tablemodel);
        sp = new JScrollPane(tbl_stocks);
        sp.setPreferredSize(new Dimension(350,300));

        listselectionmodel = tbl_stocks.getSelectionModel();
        listselectionmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listselectionmodel.addListSelectionListener(handler);
        tbl_stocks.setSelectionModel(listselectionmodel);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_START;
        add(text_search, c);

        c.gridx = 1;
        add(btn_search, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        add(sp, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        add(btn_select, c);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_search) {
                dbconnect conn = new dbconnect();

                ResultSet rset3 = null;

                try {
                    rset3 = conn.getStocksearch(text_search.getText());
                    refreshTable(rset3);
                    conn.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            if (source == btn_select) {
                panel_requisitionslip.text_description.setText(buff_description);
                panel_requisitionslip.stockid = buff_stockid;
                dispose();
            }
        }
    }

    public void refreshTable(ResultSet rset1) throws Exception
    {
        data = new Vector<Vector<Object>>();

        while(rset1.next())
        {
            record = new Vector<Object>();

            record.add(rset1.getString("stock_id"));
            record.add(rset1.getString("description"));

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

                Object x, y;

                x = tbl_stocks.getValueAt(maxIndex, 1);
                buff_description = x.toString();

                y = tbl_stocks.getValueAt(maxIndex, 0);
                buff_stockid = y.toString();
                btn_select.setEnabled(true);
            }
        }
    }
}
