import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class dialog_adduser extends JDialog {
    JLabel lbl_title, lbl_fullname, lbl_username, lbl_password, lbl_repassword, lbl_accounttype, lbl_error;
    JTextField text_fullname, text_username, text_password, text_repassword;
    JComboBox combo_accounttype;
    JButton btn_add, btn_cancel;

    public dialog_adduser()
    {
        setTitle("Add User");
        setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();

        GridBagConstraints c = new GridBagConstraints();

        lbl_title = new JLabel("User Information");
        lbl_fullname = new JLabel("Full Name :");
        lbl_username = new JLabel("Username :");
        lbl_password = new JLabel("Password :");
        lbl_repassword = new JLabel("Re-enter password :");
        lbl_accounttype = new JLabel("Account Type :");
        lbl_error = new JLabel();
        lbl_error.setForeground(Color.RED);

        text_fullname = new JTextField(20);
        text_username = new JTextField(20);
        text_password = new JTextField(20);
        text_repassword = new JTextField(20);

        text_fullname.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_fullname.getText().length() >= 50 )
                    e.consume();
            }
        });

        text_username.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_username.getText().length() >= 50 )
                    e.consume();
            }
        });

        text_password.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_password.getText().length() >= 50 )
                    e.consume();
            }
        });

        text_repassword.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (text_repassword.getText().length() >= 50 )
                    e.consume();
            }
        });

        combo_accounttype = new JComboBox();
        combo_accounttype.addItem("Engineer/Purchaser");
        combo_accounttype.addItem("Accountant");
        combo_accounttype.addItem("Project Manager");

        btn_add = new JButton("Add User");
        btn_cancel = new JButton("Cancel");
        btn_add.setPreferredSize(new Dimension(100, 25));
        btn_cancel.setPreferredSize(new Dimension(90, 25));

        btn_add.addActionListener(control);
        btn_cancel.addActionListener(control);

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,0,20,10);
        c.anchor = GridBagConstraints.LINE_START;
        add(lbl_title, c);

        //2nd row
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5,0,0,10);
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_fullname, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_fullname, c);

        //3rd row
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_username, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_username, c);

        //4th row
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_password, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_password, c);

        //5th row
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_repassword, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(text_repassword, c);

        //6th row
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.LINE_END;
        add(lbl_accounttype, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(combo_accounttype, c);

        //7th row
        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(20,0,20,10);
        add(btn_add, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(btn_cancel, c);

        //8th row
        c.gridx = 0;
        c.gridy = 7;
        c.insets = new Insets(5,0,0,10);
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
                    if(conn.checkDuplicateuser(text_username.getText()) && text_password.getText().equals(text_repassword.getText()))
                    {
                        conn.addUser(text_fullname.getText(), text_username.getText(), text_password.getText(), combo_accounttype.getSelectedItem().toString());
                        conn.close();
                        dispose();
                    }
                    else if (conn.checkDuplicateuser(text_username.getText()) == false)
                    {
                        lbl_error.setText("Username already exists!");
                        conn.close();
                    }
                    else if (!(text_password.getText().equals(text_repassword.getText())))
                    {
                        lbl_error.setText("Passwords entered do not match!");
                        conn.close();
                    }
                    else
                    {
                        lbl_error.setText("Username already exists and passwords entered do not match!");
                        conn.close();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            if (source == btn_cancel)
            {
                dispose();
            }
        }
    }
}
