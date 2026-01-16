package main;

import javax.swing.UIManager;
import view.WelcomeFrame;

public class Main {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // continue with default if Nimbus fails
        }

        java.awt.EventQueue.invokeLater(() -> {
            WelcomeFrame wf = new WelcomeFrame();
            wf.setLocationRelativeTo(null); // optional, not against guidelines
            wf.setVisible(true);
        });
    }
}
