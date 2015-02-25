package goip;

import java.awt.EventQueue;
import dice.DiceBag;
import dice.DiceRoll;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.util.Optional;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JButton;

import Encryption.DecryptedWriter;
import Encryption.EncryptedReader;

import java.awt.Dialog.ModalExclusionType;

public final class GoIPPlayer {
	public static Thread listenUp;
	public static DecryptedWriter out = null;
	public static GoIPPlayer window;
	private static JFrame frmGoIPPlayer;
	private static JTextField inputLine;
	private static JTextArea chatArea;
	public static JTextArea listPlayers;
	private static boolean connected = false;
	private static String IP = "";
	private static String lastSent = "";
	private static Socket transSocket;
	private static Socket playerListSocket;
	private static EncryptedReader in;
	private JButton btnRollD;
	private String me = "Me";


	public static void main(String[] args) throws Exception{
		EventQueue.invokeLater(() -> {
				window = new GoIPPlayer();
				frmGoIPPlayer.setVisible(true);
		});

	}

	public GoIPPlayer() {
		initialize();
	}

	boolean makeconnection() {
		try {
			transSocket = null;
			in = null;
			transSocket = new Socket(IP, 1813);
			playerListSocket = new Socket(IP, 1813);
			chatArea.setText(IP + " - ");
			out = new DecryptedWriter(transSocket.getOutputStream(), true);
			in = new EncryptedReader(new InputStreamReader(
					transSocket.getInputStream()));
			new Thread(new playerListener(new EncryptedReader(
					new InputStreamReader(playerListSocket.getInputStream()))))
					.start();
			new Thread(new Ears(transSocket, in)).start();

			return true;
		} catch (UnknownHostException e) {
			chatArea.setText("Host Unknown Exception.. yeah you should probably google that.");
			return false;
		} catch (IOException e) {
			chatArea.setText(IP
					+ " FAILED: Make sure your DM has his ports open and his server on. Hit enter to retry or type in a new IP.");
			return false;
		} catch (Exception e) {
			chatArea.setText("Well this is awkward. No idea why you can't connect.");
			return false;
		}
	}

	// standard GUI initialization
	void initialize() {
		frmGoIPPlayer = new JFrame();
		frmGoIPPlayer.setResizable(false);
		frmGoIPPlayer
				.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frmGoIPPlayer.setTitle("GoIP Player");
		frmGoIPPlayer.setBounds(100, 100, 552, 282);
		frmGoIPPlayer.setLocationRelativeTo(null);
		frmGoIPPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGoIPPlayer.getContentPane().setLayout(null);

		inputLine = new JTextField();
		inputLine.setBounds(7, 227, 422, 20);
		frmGoIPPlayer.getContentPane().add(inputLine);
		inputLine.setColumns(10);
		inputLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) { // when you hit enter
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String input = inputLine.getText().trim();
					// it appends what you wrote to the chat box
					String[] params = input.split(" ");
					if (!input.contains("setname")
							&& params[0].equalsIgnoreCase("msg")) {
						// avoid annoying double messages from commands
						// and responses.

						chatArea.append(me + ": " + inputLine.getText() + "\n");
					} else if (params[0].equalsIgnoreCase("setname")
							&& params.length > 1) {
						me = params[1];
					} else if (params[0].equalsIgnoreCase("msg")
							&& input.split(" ").length > 1) {
						chatArea.append("To " + params[1] + ": "
								+ inputLine.getText() + "\n");
					}

					if (!input.equals(null) && !input.equals("")) {
						if (!connected) {
							if (input.equalsIgnoreCase("LAN")) {
								chatArea.setText("Feature Removed due to strange complications. Type in IP");
							} else {
								IP = input;
								frmGoIPPlayer.setTitle("GoIP Player - " + IP);
								connected = makeconnection();
							}
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
							} catch (Exception q) {
								chatArea.setText("Failed to reconnect.");
							}
						} else
							out.println(input.trim());
						lastSent = input;
						inputLine.setText("");

					}
				}
			}
		});
		listPlayers = new JTextArea();
		listPlayers.setTabSize(3);
		listPlayers.setEditable(false);
		listPlayers.setBounds(369, 34, 91, 141);
		JScrollPane scrollPane = new JScrollPane(listPlayers);
		scrollPane.setBounds(433, 23, 102, 200);
		frmGoIPPlayer.getContentPane().add(scrollPane);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setBounds(10, 11, 422, 220);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);

		JScrollPane scrollPane2 = new JScrollPane(chatArea);
		scrollPane2.setBounds(7, 7, 422, 216);
		scrollPane2
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frmGoIPPlayer.getContentPane().add(scrollPane2);
		chatArea.setText("Type in the IP of your DM or use the dice bag to roll without connecting.\n");

		JLabel lblPlayerList = new JLabel("Player List");
		lblPlayerList.setBounds(437, -2, 98, 28);
		frmGoIPPlayer.getContentPane().add(lblPlayerList);

		btnRollD = new JButton("Dice Bag");
		btnRollD.setBounds(439, 226, 91, 23);
		btnRollD.setToolTipText("Do it. I dare you.");
		btnRollD.addActionListener(e -> {
			final DiceBag db = new DiceBag();
			if (connected) {
				db.btnRoll.addActionListener(y -> {
					String temp = "roll " + db.j[0].getText().trim() + "d100 "
							+ db.j[1].getText().trim() + "d20 "
							+ db.j[2].getText().trim() + "d12 "
							+ db.j[3].getText().trim() + "d10 "
							+ db.j[4].getText().trim() + "d8 "
							+ db.j[5].getText().trim() + "d6 ";
					temp += db.j[6].getText().trim() + "d4";
					if (!db.dc.getText().trim().equals(""))
						temp += " " + db.cd.getText().trim() + "d"
								+ db.dc.getText().trim();
					if (!db.add.getText().trim().equals(""))
						temp += " +" + db.add.getText().trim();
					out.println(temp);
				});
			} else {
				db.statbutt.addActionListener(x -> chatArea.append(DiceRoll
						.statroll()));
				db.btnRoll.addActionListener(z -> {
					String temp = "roll " + db.j[0].getText().trim() + "d100 "
							+ db.j[1].getText().trim() + "d20 "
							+ db.j[2].getText().trim() + "d12 "
							+ db.j[3].getText().trim() + "d10 "
							+ db.j[4].getText().trim() + "d8 "
							+ db.j[5].getText().trim() + "d6 "
							+ db.j[6].getText().trim() + "d4";
					if (!db.dc.getText().trim().equals(""))
						temp += " " + db.cd.getText().trim() + "d"
								+ db.dc.getText().trim();
					if (!db.add.getText().trim().equals(""))
						temp += " +" + db.add.getText().trim();
					String note = db.NoteBox.getText().trim();
					if (!note.equalsIgnoreCase("Description of Bag"))
						chatArea.append(note + ":"
								+ DiceRoll.roll(temp.split(" ")) + "\n");
					else
						chatArea.append("You "
								+ DiceRoll.roll(temp.split(" ")).trim() + "\n");
				});
			}

			db.setVisible(true);
		});
		frmGoIPPlayer.getContentPane().add(btnRollD);
	}

	// handles all incoming messages.
	static class Ears implements Runnable {
		private EncryptedReader in;

		public Ears(Socket listener, EncryptedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			Optional<String> from = null;

			try {
				while ((from = Optional.ofNullable(in.readLine())).isPresent()) {
					// keyDecrypting and trimming input.
					from.map(e -> e.trim())
							.filter(fromServer -> !fromServer.equals("")
									&& !fromServer.equals("\n"))
							.ifPresent(
									fromServer -> {
										if (!lastSent.equalsIgnoreCase("ping"))
											chatArea.append(fromServer + "\n");
										else {
											long results = System
													.currentTimeMillis()
													- Long.parseLong(fromServer);
											chatArea.append(results + "\n");
										}
									});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	// this thread handles incoming information for the player list object. Does
	// nothing else.
	static class playerListener implements Runnable {
		private EncryptedReader in;

		public playerListener(EncryptedReader in) { // the constructor that
			// should always be used
			this.in = in;
		}

		@Override
		public void run() {
			Optional<String> from;
			try {
				while ((from = Optional.ofNullable(in.readLine())).isPresent()) {
					from.filter(
							fromServer -> !fromServer.equals("")
									&& !fromServer.equals("\n")).ifPresent(
							fromServer -> listPlayers.setText(fromServer
									.replace("|", "\n")));
				}
			} catch (Exception e) {
				chatArea.append("");
				e.printStackTrace();
			}
		}
	}
}
