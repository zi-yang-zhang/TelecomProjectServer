package telecom.client.gui;

import telecom.client.core.Client;
import telecom.server.protocol.RequestType;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by robertzhang on 2015-04-12.
 */
public class ConnectionPanel extends JPanel implements DataReceiverListener{
    private JLabel receiveRateLabel;
    private JLabel totalDataLabel;
    private JTextArea receiveRateText;
    private JTextArea totalDataText;
    private JLabel connectionName;
    private JButton clearButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel connectionTypeLabel;
    private JButton removeButton;
    private JLabel connectionLabel;
    private JLabel connectionType;
    private RequestType requestType;
    private Client client;
    private ClientReceiverGuiWorker receiverGuiWorker;
    private UpdateGuiWorker updateGuiWorker;



    public ConnectionPanel(int connectionNumber,RequestType requestType){
        this.requestType = requestType;
        connectionName.setText(String.valueOf(connectionNumber));
        connectionTypeLabel.setText(requestType.name());

        client = new Client();
        client.setMode(requestType);

        client.setServer("localhost", 5000);
        client.registerListener(this);

        init();

        System.out.println("panel created");


    }


    public void init(){
        this.setSize(300, 200);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new LineBorder(Color.black));
        FlowLayout subPanelLayout = new FlowLayout();
        subPanelLayout.setAlignment(FlowLayout.LEADING);
        JPanel connectionNamePanel = new JPanel();
        connectionNamePanel.setLayout(subPanelLayout);
        connectionNamePanel.add(connectionLabel);
        connectionNamePanel.add(connectionName);
        this.add(connectionNamePanel);

        JPanel connectionTypePanel = new JPanel();
        connectionTypePanel.setLayout(subPanelLayout);
        connectionTypePanel.add(connectionTypeLabel);
        connectionTypePanel.add(connectionType);
        this.add(connectionTypePanel);
        receiveRateText  = new JTextArea(1,10);
        JPanel receiveRatePanel = new JPanel();
        receiveRatePanel.setLayout(subPanelLayout);
        receiveRatePanel.add(receiveRateLabel);
        receiveRatePanel.add(receiveRateText);
        this.add(receiveRatePanel);
        totalDataText   = new JTextArea(1,10);
        JPanel totalDataPanel = new JPanel();
        totalDataPanel.setLayout(subPanelLayout);
        totalDataPanel.add(totalDataLabel);
        totalDataPanel.add(totalDataText);
        this.add(totalDataPanel);

        connectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        disconnectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        removeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(connectButton);
        this.add(disconnectButton);

        this.add(clearButton);
        this.add(removeButton);

        disconnectButton.setEnabled(false);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connect();
                } catch (IOException e1) {
                    connectionTypeLabel.setText("Connection refused");
                }
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    disconnect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receiveRateText.setText("");
                totalDataText.setText("");
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parent = getParent();
                ((ClientGui) getTopLevelAncestor()).removeEntry(connectionName.getText());
                parent.remove(ConnectionPanel.this);
                parent.validate();
                parent.repaint();

            }
        });

    }


    public void connect() throws IOException {

        client.connect();
        client.sendCommand();
        receiverGuiWorker = new ClientReceiverGuiWorker(client);
        updateGuiWorker = new UpdateGuiWorker(client);
        updateGuiWorker.execute();
        receiverGuiWorker.execute();
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
        connectionTypeLabel.setText(requestType.name() + ": Connected");

    }
    public void disconnect() throws IOException {
        receiverGuiWorker.terminate();
        receiverGuiWorker.cancel(true);
        updateGuiWorker.terminate();
        updateGuiWorker.cancel(true);
        connectionTypeLabel.setText(requestType.name() + ": Connection closed");
        disconnectButton.setEnabled(false);
        connectButton.setEnabled(true);

    }
    @Override
    public void onDataReceive(double dataLength) {
        DecimalFormat df = new DecimalFormat("#.00");
        receiveRateText.setText(df.format(dataLength) + "Byte/s");
        totalDataText.setText(client.getTotalBytes() + "Bytes");
    }


    @Override
    public void onClientClosed() {
        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
