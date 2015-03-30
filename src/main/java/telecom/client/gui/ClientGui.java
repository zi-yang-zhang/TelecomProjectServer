package telecom.client.gui;

import javax.swing.*;

/**
 * Created by robertzhang on 2015-03-30.
 */
public class ClientGui extends JFrame{
    private JButton button1;
    private JCheckBox checkBox1;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JTextPane textPane1;
    private JPanel rootPanel;

    public ClientGui(){
        setContentPane(rootPanel);
        pack();
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void init(){

    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientGui gui = new ClientGui();
                gui.setVisible(true);
            }
        });
    }
}
