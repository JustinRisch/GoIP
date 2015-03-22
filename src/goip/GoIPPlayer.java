package goip;

import java.awt.*;

import dice.DiceBag;
import dice.DiceRoll;

import javax.swing.*;

import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.text.DefaultCaret;

import character.CharacterSheet;
import Encryption.DecryptedWriter;
import Encryption.EncryptedReader;

import java.awt.Dialog.ModalExclusionType;

public final class GoIPPlayer {

    // communication variables
    public static DecryptedWriter out;
    public static EncryptedReader in;
    private static Socket transSocket;
    private static Socket playerListSocket;
    // some status variables likely set once or twice.
    public static boolean connected = false;
    private static String IP = "";
    static String me = "Me", lastMessage = "";
    // GUI components
    public final static CharacterSheet cs = new CharacterSheet("");
    private final static JFrame frame = new JFrame();
    private final static JPanel contentPane = new JPanel();
    private final static JTextField inputLine = new JTextField();
    public final static ChatArea chatArea = new ChatArea();

    private final static DefaultListModel<String> listModel = new DefaultListModel<String>();
    private final static JList<String> listPlayers = new JList<String>(
	    listModel);
    public final static ArrayList<DiceBag> dblist = new ArrayList<DiceBag>();
    private final static JMenuBar menubar = new PlayerMenu(dblist, false);
    private final static JLabel lblPlayerList = new JLabel("Player List");
    private final static JScrollPane scrollPane = new JScrollPane(listPlayers);
    private final static JScrollPane scrollPane2 = new JScrollPane(chatArea);
    private final static JButton btnRollD = new JButton("Dice Bag");
    private final static JButton btnCS = new JButton("C. Sheet");

    public static void main(String[] args) throws Exception {
	EventQueue.invokeLater(() -> {
	    new GoIPPlayer();
	    frame.setVisible(true);
	});

    }

    public GoIPPlayer() {
	initialize();
    }

    private boolean makeconnection() {
	try {
	    transSocket = new Socket(IP, 1813);
	    playerListSocket = new Socket(IP, 1813);

	    chatArea.setText(IP + " - ");
	    out = new DecryptedWriter(transSocket.getOutputStream(), true);
	    in = new EncryptedReader(new InputStreamReader(
		    transSocket.getInputStream()));
	    new playerListener(new EncryptedReader(new InputStreamReader(
		    playerListSocket.getInputStream()))).start();
	    new Ears(in).start();

	    return true;

	} catch (IOException e) {
	    chatArea.setText(IP
		    + " FAILED: Make sure your DM has his ports open and his server on. Hit enter to retry or type in a new IP.");
	    return false;
	} catch (Exception e) {
	    chatArea.setText("Well this is awkward. No idea why you can't connect.");
	    return false;
	}
    }

    private static String lastSent = "";

    // standard GUI initialization
    private void initialize() {
	frame.add(contentPane, BorderLayout.CENTER);
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    out.println("exit");
	    out.print((String) null); // severs connection
		out.close();
		// Needed to throw a run time exception to be able to close
		// it...
		// don't ask. I don't know.
		byte[] b = {};
		b[1] = 0;
		System.exit(1);
	    }));

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setResizable(false);
	frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
	frame.setTitle("GoIP Player");
	frame.setBounds(100, 100, 545, 300);
	frame.setLocationRelativeTo(null);
	contentPane.setLayout(null);

	inputLine.setBounds(7, 227, 422, 20);
	contentPane.add(inputLine);
	inputLine.setColumns(10);
	inputLine.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) { // when you hit enter
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		    inputLine.setText(lastSent);
		    break;
		case KeyEvent.VK_RIGHT:
		    inputLine.setText("");
		    break;
		case KeyEvent.VK_LEFT:
		    inputLine.setText("msg " + lastMessage + "  ");
		    break;
		case KeyEvent.VK_ENTER:
		    String input = inputLine.getText().trim();
		    // it appends what you wrote to the chat box
		    String[] params = input.split(" ");
		    switch (params[0].toLowerCase()) {
		    case "setname":
			if (params[1].equalsIgnoreCase("dm")) {
			    chatArea.append("Nice try.\n");
			    inputLine.setText("");
			    try {
				Desktop.getDesktop()
					.browse(new URI(
						"https://www.youtube.com/watch?feature=player_embedded&v=32kN5r-YZFY"));
			    } catch (Exception er) {
			    }
			    return;
			}
			me = params[1];
			break;
		    case "msg":
			lastMessage = params[1];
			chatArea.append("(To "
				+ params[1]
				+ "): "
				+ inputLine.getText().replace(
					"msg " + params[1], "") + "\n");
			break;
		    }

		    if (!input.equals(null) && !input.equals("")) {
			if (!connected) {
			    IP = input;
			    frame.setTitle("GoIP Player - " + IP);
			    connected = makeconnection();
			} else if (input.equalsIgnoreCase("reset")) {
			    try {
				out.println("reset");
				// cleaning up connections before remaking them
				if (!transSocket.isClosed())
				    transSocket.close();
				if (!playerListSocket.isClosed())
				    playerListSocket.close();

				in.close();
				out.close();
				makeconnection();
				chatArea.append("Disconnected.\n");
			    } catch (Exception q) {
				chatArea.setText("Failed to reconnect.");
			    }
			} else {
			    out.println(input.trim());
			}

			lastSent = input;
			inputLine.setText("");

		    }

		}
	    }
	});
	listPlayers.setBounds(369, 34, 91, 141);

	scrollPane.setBounds(433, 23, 102, 200);
	contentPane.add(scrollPane);
	((DefaultCaret) chatArea.getCaret())
		.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	chatArea.setEditable(false);
	chatArea.setBounds(10, 11, 422, 220);
	chatArea.setWrapStyleWord(true);
	chatArea.setLineWrap(true);
	listPlayers
		.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

	listPlayers.addListSelectionListener(e -> {
	    if (listPlayers.getSelectedValue() != null)
		inputLine.setText("msg " + listPlayers.getSelectedValue());
	    listPlayers.clearSelection();
	    inputLine.requestFocusInWindow();
	    inputLine.setCaretPosition(inputLine.getText().length());
	});
	listPlayers.setVisibleRowCount(-1);
	scrollPane2.setBounds(7, 7, 422, 216);
	scrollPane2
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	contentPane.add(scrollPane2);
	chatArea.setText("Type in the IP of your DM or use the dice bag to roll without connecting.\n");

	lblPlayerList.setBounds(437, -2, 98, 28);
	contentPane.add(lblPlayerList);
	btnCS.setBounds(439, 240, 91, 15);
	btnCS.addActionListener(e -> {
	    cs.setLocationRelativeTo(null);
	    cs.setVisible(true);
	});
	contentPane.add(btnCS);

	btnRollD.setBounds(439, 225, 91, 15);
	btnRollD.addActionListener(e -> {
	    final DiceBag db = new DiceBag(me);
	    setBehavior(db);
	    dblist.add(db);
	    db.setVisible(true);
	});
	frame.add(menubar, BorderLayout.NORTH);
	contentPane.add(btnRollD);
    }

    // handles all incoming messages.
    private static final class Ears extends Thread {
	private final EncryptedReader encryptedIN;

	public Ears(EncryptedReader in) {
	    this.encryptedIN = in;
	}

	@Override
	public void run() {
	    try {
		encryptedIN
			.lines()
			.map(e -> e.trim())
			.filter(fromServer -> !fromServer.equals("")
				&& !fromServer.equals("\n"))
			.forEach(
				fromServer -> {
				    if (lastSent.equalsIgnoreCase("ping")) {
					long results = System
						.currentTimeMillis()
						- Long.parseLong(fromServer);
					chatArea.append(results + "\n");
				    } else if (fromServer
					    .startsWith("DM: hyperlink ")) {
					try {
					    String link = fromServer
						    .substring("DM: hyperlink "
							    .length());

					    if (!(link.startsWith("https://") || link
						    .startsWith("http://")))
						link = "https://" + link;
					    Desktop.getDesktop().browse(
						    new URI(link));
					} catch (Exception e) {
					    System.out.println(fromServer);
					    e.printStackTrace();
					}

				    } else if (fromServer
					    .startsWith("(from DM) hyperlink ")) {
					try {
					    String link = fromServer
						    .substring("(from DM) hyperlink "
							    .length());

					    if (!(link.startsWith("https://") || link
						    .startsWith("http://")))
						link = "https://" + link;
					    Desktop.getDesktop().browse(
						    new URI(link));
					} catch (Exception e) {
					    System.out.println(fromServer);
					    e.printStackTrace();
					}

				    } else {
					chatArea.append(fromServer + "\n");
				    }
				});
	    } catch (Exception e) {
		// this blocks output on exception.
	    }

	}
    }

    public static void setBehavior(DiceBag db) {
	if (connected) {
	    db.setButtonBehavior(y -> {
		String z = db.localRoll();
		out.println("db~" + z.replace(me, ""));
		chatArea.append(z + "\n");
	    });
	    db.setStatButtonBehavior(z -> {
		String y = DiceRoll.statroll();
		chatArea.append(y + "\n");
		out.println("db~" + y);
	    });
	} else {
	    db.setStatButtonBehavior(z -> {
		String y = DiceRoll.statroll();
		chatArea.append(y + "\n");
	    });
	    db.setButtonBehavior(z -> chatArea.append(db.localRoll() + "\n"));
	}
    }

    // this thread handles incoming information for the player list object. Does
    // nothing else.
    private static final class playerListener extends Thread {
	private final EncryptedReader encryptedIn;

	public playerListener(EncryptedReader in) {
	    this.encryptedIn = in;
	}

	@Override
	public void run() {

	    try {
		encryptedIn
			.lines()
			.map(e -> e.replace("|", "~"))
			.forEach(
				fromServer -> {
				    listModel.removeAllElements();
				    Arrays.stream(fromServer.split("~"))
					    .forEach(
						    user -> listModel
							    .addElement(user
								    + "\n"));
				});
	    } catch (Exception e) {
		// this blocks output on exception.
		e.printStackTrace();
	    }
	}
    }
}
