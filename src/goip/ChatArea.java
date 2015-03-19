package goip;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ChatArea extends JTextArea {
    private StringList text = new StringList();
    int max = 50;

    @Override
    public void append(String str) {
	text.add(str);
	if (text.size() > max) {
	    text.remove(0);
	}
	super.setText(text.toString());
    }
}
