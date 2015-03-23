package launcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import goip.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public final class GoIPLauncher {
	// GUI members
	private static final JFrame jd = new JFrame();;
	private static final JButton updater = new JButton();
	private static final JLabel roleLabel = new JLabel(" Choose your role:");
	private static final JButton DMButton = new JButton("Dungeon Master");
	private static final JButton PlayerButton = new JButton("Player");
	private static final JFileChooser fileChooser = new JFileChooser();
	private static final JLabel waitMessage = new JLabel(
			"Please wait while it finishes");

	// version members
	private static String currentVersion;
	private static final String version = currentVersion = "1.6.4";

	final public static void main(String[] args) {

		jd.setResizable(false);
		jd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jd.setBounds(100, 100, 300, 100);
		try {
			currentVersion = getCurrentVersion();
			jd.setTitle("GoIP v" + version);

			jd.setLocationRelativeTo(null);

			if (currentVersion.equals(version)) {
				setButtons();

			} else {
				updater.setText("Update GoIP!");
				updater.addActionListener(GoIPLauncher::update);
				jd.add(updater);
				jd.setVisible(true);
			}

		} catch (Exception e) {
			jd.setTitle("GoIP version cannot be confirmed.");
			setButtons();
		}
	}

	private static final void setButtons() {
		jd.add(roleLabel, BorderLayout.NORTH);

		DMButton.addActionListener(e -> {
			try {
				GoIPDM.main(new String[0]);
				jd.dispose();
			} catch (Exception d) {
				jd.removeAll();
				jd.add(new JLabel(d.getMessage()));
				jd.revalidate();
				jd.repaint();
			}
		});
		jd.add(DMButton, BorderLayout.WEST);

		PlayerButton.addActionListener(e -> {
			try {
				GoIPPlayer.main(new String[0]);
				jd.dispose();
			} catch (Exception d) {
			}
		});
		jd.add(PlayerButton, BorderLayout.CENTER);

		updater.setText("Force update");
		updater.addActionListener(GoIPLauncher::update);
		updater.setVisible(true);
		jd.add(updater, BorderLayout.SOUTH);
		jd.setVisible(true);

	}

	private final static void update(ActionEvent e) {
		jd.remove(roleLabel);
		jd.remove(DMButton);
		jd.remove(PlayerButton);

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showSaveDialog(null);

		jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jd.setTitle("Updating Now...");
		jd.remove(updater);
		jd.add(waitMessage, BorderLayout.CENTER);
		jd.revalidate();
		jd.repaint();

		try {
			download(fileChooser.getSelectedFile() + "/GoIP v" + currentVersion
					+ ".jar");
			jd.setTitle("Complete!");
			jd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jd.remove(waitMessage);
			jd.add(new JLabel("Your download is complete. Please run that jar."),
					BorderLayout.CENTER);

			jd.revalidate();
			jd.repaint();
		} catch (Exception err) {
			jd.setTitle("Sorry - cannot write file");
			jd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jd.remove(waitMessage);
			jd.add(new JLabel("Unable to update."), BorderLayout.CENTER);
			jd.revalidate();
			jd.repaint();
		}

	}

	private final static void download(String target) throws IOException {
		Files.copy(
				new URL(
						"https://dl.dropboxusercontent.com/u/11902673/GoIP%20Launcher.jar")
						.openStream(), Paths.get(target),
				StandardCopyOption.REPLACE_EXISTING);

	}

	private final static String getCurrentVersion() throws IOException {
		String subversion = "-1";
		URL oracle = new URL(
				"https://dl.dropboxusercontent.com/u/11902673/GoIPversion.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				oracle.openStream()));

		subversion = (in.readLine()).trim();
		in.close();
		return subversion;
	}
}
