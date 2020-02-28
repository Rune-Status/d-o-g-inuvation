package org.rspeer.ui;

import javax.swing.*;
import java.awt.*;

public final class Login extends JPanel {

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - RSPeer
    private JPanel panel1;
    private JFormattedTextField emailInput;
    private JButton loginButton;
    private JPasswordField passwordInput;
    private JLabel label1;
    private JLabel label2;
    private JLabel label4;
    private JLabel noAccount;

    public Login() {
        initComponents();
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JPasswordField getPasswordInput() {
        return passwordInput;
    }

    public JFormattedTextField getEmailInput() {
        return emailInput;
    }

    public JLabel getNoAccount() {
        return noAccount;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - RSPeer
        panel1 = new JPanel();
        emailInput = new JFormattedTextField();
        loginButton = new JButton();
        passwordInput = new JPasswordField();
        label1 = new JLabel();
        label2 = new JLabel();
        label4 = new JLabel();
        noAccount = new JLabel();

        //======== this ========


        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setMinimumSize(new Dimension(765, 503));
            panel1.setOpaque(false);

            //---- loginButton ----
            loginButton.setText("Login To RSPeer");
            loginButton.setMaximumSize(null);
            loginButton.setMinimumSize(null);
            loginButton.setPreferredSize(new Dimension(78, 60));

            //---- label1 ----
            label1.setText("Email Address");
            label1.setLabelFor(emailInput);

            //---- label2 ----
            label2.setText("Password");
            label2.setLabelFor(passwordInput);

            //---- label4 ----
            label4.setIcon(new ImageIcon(getClass().getResource("/logo.png")));

            //---- noAccount ----
            noAccount.setText("<html>No account? Click here to register.</></html>");
            noAccount.setForeground(new Color(58, 147, 255));
            noAccount.setFocusable(false);

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap(230, Short.MAX_VALUE)
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addComponent(noAccount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addGroup(panel1Layout.createParallelGroup()
                                                            .addComponent(emailInput)
                                                            .addGroup(panel1Layout.createSequentialGroup()
                                                                    .addGroup(panel1Layout.createParallelGroup()
                                                                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(label2, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(passwordInput, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE))
                                                                    .addGap(0, 0, Short.MAX_VALUE)))
                                                    .addGap(224, 224, 224))))
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(263, 263, 263)
                                    .addComponent(label4)
                                    .addGap(0, 277, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(36, 36, 36)
                                    .addComponent(label4)
                                    .addGap(31, 31, 31)
                                    .addComponent(label1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(emailInput, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(label2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(passwordInput, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(noAccount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(47, Short.MAX_VALUE))
            );
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
