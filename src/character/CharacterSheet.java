package character;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class CharacterSheet extends JFrame {

	private static final long serialVersionUID = 2870798817354394618L;
	private static final JPanel contentPane = new JPanel();
	private static String name = "";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CharacterSheet frame = new CharacterSheet();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CharacterSheet(String x) {
		name = x;
	}

	private JMenuBar menubar = new JMenuBar();

	private JTextField[] stats = new JTextField[6];
	private JTextField[] mods = new JTextField[6];
	private String[] labels = { "STR", "CON", "DEX", "INT", "WIS", "CHR" };

	public CharacterSheet() {
		if (name.equals(""))
			this.setTitle("Character Sheet");
		else
			this.setTitle(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		this.add(contentPane, BorderLayout.CENTER);
		JMenu menu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser("Save as...");

			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileFilter filter = new FileNameExtensionFilter("CSJ files only",
					"CSJ");
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.showSaveDialog(null);

			File SaveTo;
			if (fileChooser.getSelectedFile().toString().endsWith(".CSJ"))
				SaveTo = fileChooser.getSelectedFile();
			else
				SaveTo = new File(fileChooser.getSelectedFile().toString()
						+ ".CSJ");

			try {
				SaveTo.createNewFile();
				FileWriter fw = new FileWriter(SaveTo);
				fw.write("");
				for (int i = 0; i < 6; i++)
					fw.append(labels[i] + ": " + stats[i].getText() + "\n");
				fw.close();
			} catch (Exception error) {

			}
		});
		menu.add(save);
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser("Load");

			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			FileFilter filter = new FileNameExtensionFilter("CSJ files only",
					"CSJ");
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(filter);
			fileChooser.showOpenDialog(null);

			try {

				BufferedReader read = new BufferedReader(new FileReader(
						fileChooser.getSelectedFile()));
				read.lines().forEachOrdered(x -> System.out.println(x));
				read.close();
			} catch (Exception error) {

			}
		});
		menu.add(load);
		menubar.add(menu);
		this.add(menubar, BorderLayout.NORTH);

		Arrays.parallelSetAll(
				stats,
				e -> {
					stats[e] = new JTextField();
					stats[e].setBounds(40, 10 + 20 * e, 50, 20);
					stats[e].setColumns(10);
					contentPane.add(stats[e]);
					JLabel lblStr = new JLabel(labels[e] + ":");
					lblStr.setBounds(5, 10 + 20 * e, 33, 16);
					contentPane.add(lblStr);
					mods[e] = new JTextField();
					mods[e].setBounds(90, 10 + 20 * e, 50, 20);
					mods[e].setEnabled(false);
					mods[e].setEditable(false);
					JTAListener jtalist = c -> {
						try {
							mods[e].setText(""
									+ Math.floor((Integer.parseInt(stats[e]
											.getText()) - 10) / 2));
						} catch (Exception x) {
							mods[e].setText("");
						}
					};
					stats[e].addKeyListener(jtalist);
					contentPane.add(mods[e]);
					return stats[e];
				});

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
