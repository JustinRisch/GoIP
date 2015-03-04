package goip;

import java.awt.Desktop;
import java.awt.EventQueue;

import dice.DiceBag;
import dice.DiceRoll;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.util.Arrays;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.text.DefaultCaret;

import Encryption.DecryptedWriter;
import Encryption.EncryptedReader;

import java.awt.Dialog.ModalExclusionType;

public final class GoIPPlayer {

	// communication variables
	private static DecryptedWriter out;
	private static EncryptedReader in;
	private static Socket transSocket;
	private static Socket playerListSocket;

	// GUI components
	private final static JFrame frame = new JFrame();;
	private final static JTextField inputLine = new JTextField();
	private final static ChatArea chatArea = new ChatArea();

	private final static DefaultListModel<String> listModel = new DefaultListModel<String>();
	private final static JList<String> listPlayers = new JList<String>(listModel);

	private final static JLabel lblPlayerList = new JLabel("Player List");
	private final static JScrollPane scrollPane = new JScrollPane(listPlayers);
	private final static JScrollPane scrollPane2 = new JScrollPane(chatArea);
	private final static JButton btnRollD = new JButton("Dice Bag");

	// some status variables likely set once or twice.
	private static boolean connected = false;
	private static String IP = "";
	private static String lastSent = "";
	private static String me = "Me";

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(() -> {
			new GoIPPlayer();
			frame.setVisible(true);
		});

	}

	private GoIPPlayer() {
		initialize();
	}

	private boolean makeconnection() {
		try {
			transSocket = null;
			in = null;
			transSocket = new Socket(IP, 1813);
			playerListSocket = new Socket(IP, 1813);
			chatArea.setText(IP + " - ");
			out = new DecryptedWriter(transSocket.getOutputStream(), true);
			in = new EncryptedReader(new InputStreamReader(
					transSocket.getInputStream()));
			new playerListener(new EncryptedReader(new InputStreamReader(
					playerListSocket.getInputStream()))).start();
			new Ears(transSocket, in).start();

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
	private void initialize() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("closing...");
			out.println("exit");
			out.print((String) null);
			// Needed to throw a run time exception to be able to close it...
			// don't ask. I don't know.
				byte[] b = {};
				b[1] = 0;
				System.exit(1);
			}));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.setTitle("GoIP Player");
		frame.setBounds(100, 100, 552, 282);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		inputLine.setBounds(7, 227, 422, 20);
		frame.getContentPane().add(inputLine);
		inputLine.setColumns(10);
		inputLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) { // when you hit enter
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					inputLine.setText(lastSent);
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
							return;
						}
						me = params[1];
						break;
					case "msg":
						chatArea.append("(To "
								+ params[1]
								+ "): "
								+ inputLine.getText().replace(
										"msg " + params[1], "") + "\n");
						break;
					case "cls":
						chatArea.setText("");
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
						} else
							out.println(input.trim());
						lastSent = input;
						inputLine.setText("");

					}
					break;
				}
			}
		});
		listPlayers.setBounds(369, 34, 91, 141);

		scrollPane.setBounds(433, 23, 102, 200);
		frame.getContentPane().add(scrollPane);
		((DefaultCaret) chatArea.getCaret())
				.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chatArea.setEditable(false);
		chatArea.setBounds(10, 11, 422, 220);
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);

		scrollPane2.setBounds(7, 7, 422, 216);
		scrollPane2
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane2);
		chatArea.setText("Type in the IP of your DM or use the dice bag to roll without connecting.\n");

		lblPlayerList.setBounds(437, -2, 98, 28);
		frame.getContentPane().add(lblPlayerList);

		btnRollD.setBounds(439, 226, 91, 23);
		btnRollD.addActionListener(e -> {
			final DiceBag db = new DiceBag(me);
			if (connected) {
				db.setButtonBehavior(y -> {
					String x = db.localRoll();
					out.println("db~" + x.replace(me, ""));
					chatArea.append(x + "\n");
				});
				db.setStatButtonBehavior(x -> {
					String y;
					chatArea.append(y = DiceRoll.statroll());
					out.println("db~" + y);
				});
			} else {
				db.setStatButtonBehavior(x -> chatArea.append(DiceRoll
						.statroll()));
				db.setButtonBehavior(z -> chatArea.append(db.localRoll() + "\n"));
			}
			db.setVisible(true);
		});
		frame.getContentPane().add(btnRollD);
	}

	// handles all incoming messages.
	private static final class Ears extends Thread {
		private final EncryptedReader in;

		public Ears(Socket listener, EncryptedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			try {
				in.lines()
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

	// this thread handles incoming information for the player list object. Does
	// nothing else.
	private static final class playerListener extends Thread {
		private final EncryptedReader in;

		public playerListener(EncryptedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			listPlayers
					.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

			listPlayers.setVisibleRowCount(-1);
			try {
				in.lines().map(e -> e.replace("|", "~")).forEach(
						fromServer -> {
							listModel.removeAllElements();
							Arrays.stream(fromServer.split("~")).forEach(
									user -> listModel.addElement(user+"\n"));
						});
			} catch (Exception e) {
				// this blocks output on exception.
				e.printStackTrace();
			}
		}
	}
}
