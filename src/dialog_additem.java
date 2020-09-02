import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class dialog_additem extends JDialog {
    JTextField text_description;
    JLabel lbl_description, lbl_title, lbl_error;
    JButton btn_add;

    public dialog_additem()
    {
        setTitle("Add Stock");
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();

        lbl_title = new JLabel("Enter Item Details");
        lbl_description = new JLabel("Description :");
        text_description = new JTextField(20);
        btn_add = new JButton("Add Item");
        lbl_error = new JLabel();
        lbl_error.setForeground(Color.RED);

        btn_add.addActionListener(control);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //second row
        c.gridy = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_description, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_description, c);

        //third row
        c.gridy = 2;
        c.gridx = 1;
        add(btn_add, c);

        //4th row
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 2;
        add(lbl_error, c);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    class HandleControlButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source == btn_add) {
                dbconnect conn = new dbconnect();

                try {
                    if(conn.checkDuplicateitem(text_description.getText()))
                    {
                        conn.addStock(text_description.getText());
                        lbl_error.setText("Added " + text_description.getText() + " to stock list!");
                        conn.close();
                        dispose();
                    }
                    else
                    {
                        lbl_error.setText("Stock item already exists !");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
