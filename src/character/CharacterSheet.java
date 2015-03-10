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

import java.util.concurrent.atomic.AtomicInteger;

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
	public static boolean isLoaded = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CharacterSheet frame = new CharacterSheet("");
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
	

	private static final JMenuBar menubar = new JMenuBar();

	private static final JTextField[] stats = new JTextField[6];
	private static final JTextField[] tempMods = new JTextField[6];
	public static final JTextField[] mods = new JTextField[6];
	private static String[] labels = { "STR", "CON", "DEX", "INT", "WIS", "CHR" };
	{
		for (int i = 0; i < 6; i++) {
			mods[i] = new JTextField();
			mods[i].setText("");
		}
	}
	public CharacterSheet(String namer) {
		name = namer;
		
		if (name.equals("") || name.equalsIgnoreCase("mes"))
			this.setTitle("Character Sheet");
		else
			this.setTitle(name);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		setBounds(0, 0, 210, 200);
		setLocationRelativeTo(null);
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
			try {
				File SaveTo;
				if (fileChooser.getSelectedFile().toString().endsWith(".CSJ"))
					SaveTo = fileChooser.getSelectedFile();
				else
					SaveTo = new File(fileChooser.getSelectedFile().toString()
							+ ".CSJ");
				SaveTo.createNewFile();
				FileWriter fw = new FileWriter(SaveTo);
				fw.write("");
				for (int i = 0; i < 6; i++) {
					System.out.println(labels[i] + ":" + stats[i].getText());
					fw.append(labels[i] + ":" + stats[i].getText() + ":"
							+ tempMods[i].getText() + "\n");
				}
				fw.close();
			} catch (Exception error) {
				error.printStackTrace();
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
				AtomicInteger f = new AtomicInteger(0);
				read.lines().forEachOrdered(x -> {
					String[] values = x.split(":");
					if (values.length > 1 && !values[1].equals(""))
						stats[f.get()].setText(values[1]);
					if (values.length > 2 && !values[2].equals(""))
						tempMods[f.get()].setText(values[2]);
					f.incrementAndGet();
				});
				f.set(0);
				refresh();
				read.close();
			} catch (Exception error) {
				error.printStackTrace();
			}
		});
		menu.add(load);
		menubar.add(menu);
		this.add(menubar, BorderLayout.NORTH);
		JLabel top = new JLabel("Stat      Temp    Modifier");
		top.setBounds(43, 5, 200, 20);
		contentPane.add(top);
		Arrays.parallelSetAll(stats, e -> {
			stats[e] = new JTextField();
			stats[e].setBounds(40, 25 + 20 * e, 50, 20);
			stats[e].setColumns(10);
			contentPane.add(stats[e]);
			JLabel lblStr = new JLabel(labels[e] + ":");
			lblStr.setBounds(5, 25 + 20 * e, 33, 16);
			contentPane.add(lblStr);
			tempMods[e] = new JTextField();
			stats[e].setColumns(10);
			tempMods[e].setBounds(90, 25 + 20 * e, 50, 20);
			contentPane.add(tempMods[e]);

			mods[e].setBounds(140, 25 + 20 * e, 50, 20);
			mods[e].setEnabled(false);
			mods[e].setEditable(false);
			stats[e].addKeyListener(new JTAListener(mods[e], stats[e],
					tempMods[e]));
			tempMods[e].addKeyListener(new JTAListener(mods[e], stats[e],
					tempMods[e]));
			contentPane.add(mods[e]);
			return stats[e];
		});
		isLoaded=true;
	}

	public void refresh() {

		for (int i = 0; i < 6; i++) {
			try {
				Integer y = 0, x = (int) Integer.parseInt(stats[i].getText());
				if (!tempMods[i].getText().equals(""))
					y = (int) Integer.parseInt(tempMods[i].getText());
				mods[i].setText((int)Math.floor((x + y - 10) / 2) + "");
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
				self.setText((int)Math.floor((x + y - 10) / 2) + "");

			} catch (Exception x) {
				self.setText("");
			}

		}
	}
}
