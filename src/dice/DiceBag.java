package dice;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;

import character.CharacterSheet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

@SuppressWarnings("serial")
public final class DiceBag extends JFrame {

    private final JTextField[] j = new JTextField[7];
    private final JCheckBox[] useStat = new JCheckBox[6];
    private final JTextField cd = new JTextField(10);
    private final JTextField dc = new JTextField();
    private final JTextField add = new JTextField();
    private final JButton btnRoll = new JButton("Roll!");
    private final JButton statbutt = new JButton("4d6 Best 3");
    private final JPanel contentPane = new JPanel();
    private final JTextField NoteBox = new JTextField();
    private final JPanel buttonPane = new JPanel();
    private final JCheckBox showRollBox = new JCheckBox();

    private final String name;

    private static final String[] labels = { "d100", "d20", "d12", "d10", "d8",
	    "d6", "d4" };
    private static String[] stats = { "STR", "DEX", "CON", "INT", "WIS", "CHR" };

    public boolean getShowRoll() {
	return !showRollBox.isSelected();
    }

    public void setStatButtonBehavior(ActionListener e) {
	statbutt.addActionListener(e);
    }

    public static void main(String[] args) {
	try {
	    DiceBag db = new DiceBag("DM");
	    db.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    db.setVisible(true);
	    db.setButtonBehavior(e -> System.out.println(db.localRoll()));
	} catch (Exception e) {
	}
    }

    public void setButtonBehavior(ActionListener e) {
	btnRoll.addActionListener(e);
    }

    public DiceBag(String name) {
	this.name = name;
	this.setTitle("Dice Bag");
	int width = 230, height = 355;

	if (!CharacterSheet.isLoaded)
	    height -= 50; //doesn't show checkbuttons if they aren't available.
	this.setBounds(100, 100, width, height);
	this.setMaximumSize(new Dimension(width, height));
	this.setMinimumSize(new Dimension(width, 60));

	this.setLocationRelativeTo(null);

	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	this.add(contentPane, BorderLayout.CENTER);

	contentPane.setLayout(null);
	if (this.name.equals("DM")) {
	    showRollBox.setBounds(2, 195, 23, 20);
	    contentPane.add(showRollBox);
	    JLabel shower = new JLabel("Hide roll?");
	    shower.setBounds(27, 195, 70, 20);
	    contentPane.add(shower);
	}
	if (CharacterSheet.isLoaded)
	    Arrays.setAll(stats,
		    i -> {
			useStat[i] = new JCheckBox();
			useStat[i].setBounds(50 * (i / 2),
				215 + (25 * (i % 2)), 35, 25);

			JLabel temp = new JLabel(stats[i]);
			temp.setBounds(25 + 50 * (i / 2), 215 + (25 * (i % 2)),
				35, 25);
			contentPane.add(useStat[i]);
			contentPane.add(temp);
			return stats[i];
		    });
	Arrays.setAll(j, i -> {
	    j[i] = new JTextField();
	    j[i].setHorizontalAlignment(SwingConstants.RIGHT);
	    j[i].setBounds(5, i * 24, 86, 20);
	    j[i].setColumns(10);
	    contentPane.add(j[i]);
	    JButton button = new JButton("+");
	    button.setFocusable(false);
	    button.setBounds(132, 24 * i, 46, 23);
	    button.addActionListener(new ButtonListener(j[i], 1));
	    contentPane.add(button);

	    button = new JButton("-");
	    button.setFocusable(false);
	    button.setBounds(179, 24 * i, 46, 23);
	    button.addActionListener(new ButtonListener(j[i], -1));
	    contentPane.add(button);
	    JLabel lblD = new JLabel(labels[i]);
	    lblD.setBounds(98, 2 + 24 * i, 46, 14);
	    contentPane.add(lblD);
	    return j[i];
	});

	cd.setHorizontalAlignment(SwingConstants.RIGHT);
	cd.setBounds(5, 168, 86, 20);
	contentPane.add(cd);

	buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	this.add(buttonPane, BorderLayout.SOUTH);
	buttonPane.setLayout(new BorderLayout());

	btnRoll.setFont(new Font("Arial Black", Font.BOLD, 11));
	btnRoll.setFocusCycleRoot(true);
	btnRoll.setBounds(5, 188, 91, 23);
	// the behavior of the roll button is added by the DM / Player programs.
	buttonPane.add(btnRoll, BorderLayout.EAST);
	this.getRootPane().setDefaultButton(btnRoll);
	dc.setHorizontalAlignment(SwingConstants.LEFT);
	dc.setBounds(134, 168, 91, 20);
	contentPane.add(dc);
	dc.setColumns(10);

	JLabel lblD_7 = new JLabel("d");
	lblD_7.setBounds(98, 168, 11, 14);
	contentPane.add(lblD_7);

	add.setHorizontalAlignment(SwingConstants.LEFT);
	add.setBounds(134, 192, 86, 20);
	contentPane.add(add);
	add.setColumns(10);

	JLabel label = new JLabel("+");
	label.setBounds(121, 195, 11, 14);
	contentPane.add(label);

	NoteBox.setText("Description of Bag");
	NoteBox.setHorizontalAlignment(SwingConstants.CENTER);
	NoteBox.setToolTipText("Put a description of the box here! (eg: Sword Damage)");
	NoteBox.setBounds(5, 1, 180, 20);
	JTAListener jtalist = e -> this.setTitle(NoteBox.getText());
	NoteBox.addKeyListener(jtalist);
	this.add(NoteBox, BorderLayout.NORTH);
	NoteBox.setColumns(10);

	statbutt.setFocusable(false);
	statbutt.setBounds(5, 260, 215, 16);
	buttonPane.add(statbutt, BorderLayout.WEST);
    }

    public final String localRoll() {
	StringBuilder temp = new StringBuilder("");
	for (int i = 0; i < 7; i++) {
	    if (!j[i].getText().trim().equals(""))
		temp.append(j[i].getText().trim() + labels[i].trim() + " ");
	}

	if (!dc.getText().trim().equals(""))
	    temp.append(" " + cd.getText().trim() + "d" + dc.getText().trim());
	String adder = add.getText().trim();
	if (!adder.equals(""))
	    if (adder.startsWith("-"))
		temp.append(" " + adder);
	    else
		temp.append(" +" + adder);

	if (!this.name.equals("DM"))
	    for (int i = 0; i < 6; i++) {

		if (!useStat[i].isSelected())
		    continue;

		adder = CharacterSheet.mods[i].getText();
		if (!adder.equals(""))
		    if (adder.startsWith("-"))
			temp.append(" " + adder);
		    else
			temp.append(" +" + adder);
	    }

	String note = NoteBox.getText().trim();
	if (!note.equalsIgnoreCase("Description of Bag") && !note.equals(""))
	    return DiceRoll.roll(temp.toString(), note);
	else
	    return DiceRoll.roll(temp.toString(), name);
    }

    class ButtonListener implements ActionListener {
	final JTextField JTF;// where the button should get it's info from.
	final int increment;// how much it should increment the number in that
			    // JTF

	public ButtonListener(JTextField JTF, int increment) {
	    this.JTF = JTF;
	    this.increment = increment;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Integer x;
	    try {
		x = new Integer(JTF.getText());
	    } catch (Exception err) {
		x = 0;
	    }
	    x += increment;
	    if (x < 0)
		x = 0;
	    JTF.setText(x.toString());

	}

    }

    interface JTAListener extends KeyListener {
	@Override
	default public void keyPressed(KeyEvent e) {
	}

	@Override
	default public void keyTyped(KeyEvent e) {
	}
    }
}
