package telecom.client.gui;

import telecom.client.core.Client;
import telecom.server.protocol.RequestType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * Created by robertzhang on 2015-03-30.
 */
public class ClientGui extends JFrame implements DataReceiverListener{
    private JButton connectButton;
    private JCheckBox leakyBucketCheckbox;
    private JRadioButton burstyTypeRadioButton;
    private JRadioButton constantRateRadioButton;
    private JTextArea receiveRateText;
    private JTextArea totalDataText;
    private JPanel rootPanel;
    private JButton disconnectButton;
    private JTextArea contentTextArea;
    private JButton clearButton;
    private JScrollPane contentScrollPanel;
    private ButtonGroup radioButtonGroup;
    private Client client;
    private TextAreaOutputStream outputStream;
    private ClientReceiverGuiWorker receiverGuiWorker;
    private UpdateGuiWorker updateGuiWorker;
    private JScrollBar sb;
    private JLabel contentLabel;
    private JLabel totalDataLabel;
    private JLabel receiveRateLabel;

    public ClientGui(){
        client = new Client();
        client.setServer("localhost", 5000);
        client.registerListener(this);
        outputStream = new TextAreaOutputStream(contentTextArea);
        setContentPane(rootPanel);
        pack();
        init();
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        System.setOut(new PrintStream(outputStream, true));

    }

    public void init(){
        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(constantRateRadioButton);
        radioButtonGroup.add(burstyTypeRadioButton);
        burstyTypeRadioButton.setSelected(true);
        disconnectButton.setEnabled(false);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setMode(getMode());
                try {
                    client.connect();
                    client.sendCommand();
                    receiverGuiWorker = new ClientReceiverGuiWorker(client);
                    updateGuiWorker = new UpdateGuiWorker(client);
                    updateGuiWorker.execute();
                    receiverGuiWorker.execute();
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    receiverGuiWorker.terminate();
                    receiverGuiWorker.cancel(true);
                    updateGuiWorker.terminate();
                    updateGuiWorker.cancel(true);
                    contentTextArea.append("Connection closed\n");
                    disconnectButton.setEnabled(false);
                    connectButton.setEnabled(true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentTextArea.setText("");
                receiveRateText.setText("");
                totalDataText.setText("");
            }
        });
        sb = contentScrollPanel.getVerticalScrollBar();


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

    @Override
    public void onDataReceive(double dataLength) {
        DecimalFormat df = new DecimalFormat("#.00");
        receiveRateText.setText(df.format(dataLength) + "Byte/s");
        totalDataText.setText(client.getTotalBytes() + "Bytes");
    }

    @Override
    public void onDataReceive(byte[] data) {
        System.out.println(data);
        sb.setValue(sb.getMaximum());
    }
}
