import javax.swing.*;
import java.io.IOException;

/**
 * Created by jwenzel on 22.02.17.
 */
public class PasswordPrompt {

    public static String readPassword(String prompt)
            throws IOException {

        JPasswordField pf = new JPasswordField();
        int res = JOptionPane.showConfirmDialog(null, pf, prompt, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            return new String(pf.getPassword());
        } else return null;
    }
}
