import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class form_login {
    public static int userlevel, userid;
    public static String fullname;
    JFrame frame_login;
    JLabel lbl_login, lbl_username, lbl_password;
    JTextField text_username;
    JPasswordField text_password;
    JButton btn_login;
    ResultSet rset;

    public form_login()
    {
        frame_login = new JFrame("Log-in");
        frame_login.setBounds(300,150,350,200);
        frame_login.setLayout(new GridBagLayout());

        HandleControlButton control = new HandleControlButton();

        lbl_login = new JLabel("Sign In");
        lbl_username = new JLabel("Username :");
        lbl_password = new JLabel("Password :");
        text_username = new JTextField(20);
        text_password = new JPasswordField(20);
        btn_login = new JButton("Log In");
        btn_login.addActionListener(control);

        GridBagConstraints c = new GridBagConstraints();

        //first row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(5,0,0,10);
        frame_login.add(lbl_login, c);

        //second row
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;
        frame_login.add(lbl_username, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        frame_login.add(text_username, c);

        //third row
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        frame_login.add(lbl_password, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        frame_login.add(text_password, c);

        //fourth row
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        frame_login.add(btn_login, c);

        frame_login.setResizable(false);
        frame_login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame_login.setVisible(true);
    }

    class HandleControlButton extends Component implements ActionListener
    {
        public void actionPerformed (ActionEvent e)
        {
            Object source = e.getSource();

            if(source == btn_login)
            {
                if (!(text_username.getText().equals("") || text_password.getPassword().equals("")))
                {
                    dbconnect conn = new dbconnect();

                    try {
                        rset = conn.verifyCredentials(text_username.getText(), String.valueOf(text_password.getPassword()));

                        if (rset.next())
                        {
                            rset.first();
                            userlevel = rset.getInt("level");
                            userid = rset.getInt("userid");
                            fullname = rset.getString("fullname");

                            mainFrame x = new mainFrame();
                            frame_login.dispose();
                            frame_login.setVisible(false);
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Invalid username/password", "Not Found", JOptionPane.ERROR_MESSAGE);
                        }

                        conn.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}
