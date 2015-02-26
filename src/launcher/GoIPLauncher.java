package launcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import goip.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GoIPLauncher {
	private final static JFrame jd = new JFrame();;
	private static final JButton updater = new JButton();
	private static double currentVersion = -1;
	private static final double version = 11;

	final public static void main(String[] args) {
		jd.setResizable(false);
		jd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			currentVersion = getCurrentVersion();
			jd.setTitle("GoIP v" + version / 10);

			jd.setBounds(100, 100, 300, 100);
			jd.setLocationRelativeTo(null);

			if (currentVersion == version) {

				jd.add(new JLabel(" Choose your role:"), BorderLayout.NORTH);
				JButton DMButton = new JButton("Dungeon Master");
				DMButton.addActionListener(e -> {
					try {
						GoIPDM.main(args);
						jd.dispose();
					} catch (Exception d) {
						jd.removeAll();
						jd.add(new JLabel(d.getMessage()));
						jd.revalidate();
						jd.repaint();
					}
				});
				jd.add(DMButton, BorderLayout.CENTER);

				JButton PlayerButton = new JButton("Player");
				PlayerButton.addActionListener(e -> {
					try {
						GoIPPlayer.main(args);
						jd.dispose();
					} catch (Exception d) {
						jd.removeAll();
						jd.add(new JLabel(d.getMessage()));
						jd.revalidate();
						jd.repaint();
					}
				});
				jd.add(PlayerButton, BorderLayout.SOUTH);
				jd.setVisible(true);
			} else {
				System.out.println(currentVersion);
				updater.setText("Update GoIP to " + currentVersion / 10);
				updater.addActionListener(GoIPLauncher::update);
				jd.add(updater);
				jd.setVisible(true);
			}

		} catch (Exception e) {
			jd.setTitle("GoIP version cannot be confirmed.");
		}
	}

	private final static void update(ActionEvent e) {
		JFileChooser f = new JFileChooser();
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		f.showSaveDialog(null);

		JLabel waitMessage = new JLabel("Please wait while it finishes");
		jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jd.setTitle("Updating Now...");
		jd.remove(updater);
		jd.add(waitMessage, BorderLayout.CENTER);
		jd.revalidate();
		jd.repaint();

		try {
			download(f.getSelectedFile() + "/GoIP v" + currentVersion / 10
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
		URL website = new URL(
				"https://dl.dropboxusercontent.com/u/11902673/GoIP%20Launcher.jar");
		Path jar = Paths.get(target);
		Files.copy(website.openStream(), jar,
				StandardCopyOption.REPLACE_EXISTING);
	}

	private final static int getCurrentVersion() {

		int version = -1;
		try {
			URL oracle = new URL(
					"https://dl.dropboxusercontent.com/u/11902673/version.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println((version = Integer.parseInt(inputLine)));
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

}
