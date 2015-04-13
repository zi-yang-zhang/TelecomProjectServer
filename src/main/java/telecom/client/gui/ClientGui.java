package telecom.client.gui;

import telecom.protocol.RequestType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * Created by robertzhang on 2015-03-30.
 */

/**
 * GUI implementation of client side, container for multiple client instances
 */
public class ClientGui extends JFrame {

    private JCheckBox leakyBucketCheckbox;
    private JRadioButton burstyTypeRadioButton;
    private JRadioButton constantRateRadioButton;
    private JPanel rootPanel;

    private ButtonGroup radioButtonGroup;

    private JButton addConnectionButton;
    private JPanel contentPanel;
    private ArrayList<AbstractMap.SimpleEntry<String,ConnectionPanel>> connectionPanels;
    private int ID;


    public ClientGui(){
        super("Bandwidth Tester Client");
        ID = 0;
        setContentPane(rootPanel);
        connectionPanels  = new ArrayList<>();
        init();
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void init(){
        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(constantRateRadioButton);
        radioButtonGroup.add(burstyTypeRadioButton);
        burstyTypeRadioButton.setSelected(true);
        addConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.setLayout(new BoxLayout(contentPanel, 1));
                ID++;
                AbstractMap.SimpleEntry entry = new AbstractMap.SimpleEntry<>(ID,new ConnectionPanel(ID, getMode()));
                connectionPanels.add(entry);
                for(AbstractMap.SimpleEntry panelEntry:connectionPanels){
                    contentPanel.add((ConnectionPanel)panelEntry.getValue());
                }
                contentPanel.repaint();
                contentPanel.revalidate();

                pack();


            }
        });
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

    private RequestType getMode(){
        if(leakyBucketCheckbox.isSelected()){
            return constantRateRadioButton.isSelected()?RequestType.ConstantBitRateWithLeakyBucket:RequestType.BurstyWithLeakyBucket;
        }else{
            return constantRateRadioButton.isSelected()?RequestType.ConstantBitRate:RequestType.Bursty;
        }
    }

    /**
     * Removes the entry from client instance list
     * @param ID
     */
    public void removeEntry(String ID){
        for(AbstractMap.SimpleEntry panelEntry:connectionPanels){
            if(panelEntry.getKey().equals(Integer.valueOf(ID))){
                connectionPanels.remove(panelEntry);
                this.ID--;
                pack();
                break;
            }
        }
    }

}
