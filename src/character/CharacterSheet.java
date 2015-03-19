package character;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class CharacterSheet extends JFrame {

    private static final long serialVersionUID = 2870798817354394618L;
    private static final JPanel contentPane = new JPanel();
    public static boolean isLoaded = false;

    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    CharacterSheet frame = new CharacterSheet("");
		    frame.setVisible(true);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */

    private static final JTextField[] stats = new JTextField[6];
    private static final JTextField[] tempMods = new JTextField[6];
    public static final JTextField[] mods = new JTextField[6];
    private static final String[] labels = { "STR", "DEX", "CON", "INT", "WIS",
	    "CHR" };
    private static final JMenuBar menubar = new CharacterMenuBar(labels, stats,
	    tempMods);

    public CharacterSheet(String namer) {

	// can adjust the whole screen up and down.
	int bump = 0;
	this.setTitle("Character Sheet");

	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	setResizable(false);
	setBounds(0, 0, 210, 200);
	setLocationRelativeTo(null);
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(null);
	this.add(contentPane, BorderLayout.CENTER);
	this.add(menubar, BorderLayout.NORTH);
	// yeah screw formatting! spaces all day!
	JLabel top = new JLabel("Stat      Temp    Modifier");
	top.setBounds(43, bump, 200, 20);
	contentPane.add(top);
	for (int e = 0; e < 6; e++) {
	    stats[e] = new JTextField();
	    stats[e].setBounds(40, bump += 20, 50, 20);
	    stats[e].setColumns(10);
	    contentPane.add(stats[e]);
	    JLabel lblStr = new JLabel(labels[e] + ":");
	    lblStr.setBounds(5, bump, 33, 16);
	    contentPane.add(lblStr);
	    tempMods[e] = new JTextField();
	    stats[e].setColumns(10);
	    tempMods[e].setBounds(90, bump, 50, 20);
	    contentPane.add(tempMods[e]);
	    mods[e] = new JTextField();
	    mods[e].setBounds(140, bump, 50, 20);
	    mods[e].setEnabled(false);
	    mods[e].setEditable(false);
	    stats[e].addKeyListener(new JTAListener(mods[e], stats[e],
		    tempMods[e]));
	    tempMods[e].addKeyListener(new JTAListener(mods[e], stats[e],
		    tempMods[e]));

	    contentPane.add(mods[e]);
	}
	isLoaded = true;
    }

    public static void refresh() {

	for (int i = 0; i < 6; i++) {
	    try {
		Integer y = 0, x = (int) Integer.parseInt(stats[i].getText());
		if (!tempMods[i].getText().equals(""))
		    y = (int) Integer.parseInt(tempMods[i].getText());
		mods[i].setText((int) Math.floor((x + y - 10) / 2) + "");
	    } catch (Exception x) {
		mods[i].setText("");
	    }
	}

    }

    class JTAListener implements KeyListener {
	JTextField a, b, self;

	public JTAListener(JTextField me, JTextField t, JTextField f) {
	    a = t;
	    b = f;
	    self = me;
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	    try {
		Integer y = 0, x = (int) Integer.parseInt(a.getText());
		if (!b.getText().equals(""))
		    y = (int) Integer.parseInt(b.getText());
		self.setText((int) Math.floor((x + y - 10) / 2) + "");

	    } catch (Exception x) {
		self.setText("");
	    }

	}
    }
}
