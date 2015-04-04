package telecom.client.gui;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by robertzhang on 2015-04-03.
 */
public class TextAreaOutputStream extends OutputStream {
    private JTextArea textArea;
    public TextAreaOutputStream(JTextArea textArea){
        this.textArea = textArea;
    }
    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char) b));
    }
}
