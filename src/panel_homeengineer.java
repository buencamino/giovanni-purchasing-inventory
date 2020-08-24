import javax.swing.*;
import java.awt.*;

public class panel_homeengineer extends JPanel {
    JLabel lbl_welcome;

    public panel_homeengineer()
    {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        lbl_welcome = new JLabel("Welcome back " + form_login.fullname + "!");

        add(lbl_welcome);
    }
}
