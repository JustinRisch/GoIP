import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class GoIPLauncher {
	private static double version = 2;

	public static void main(String[] args) {

		checkVersion();
		
		JDialog jd = new JDialog();
		jd.setTitle("GoIP Launcher");
		jd.setBounds(100, 100, 300, 100);
		jd.setLocationRelativeTo(null);
		jd.add(new JLabel("Choose your role:"), BorderLayout.NORTH);

		JButton DMButton = new JButton("Dungeon Master");
		DMButton.addActionListener(e -> {
			jd.dispose();
			GoIPDM.main(args);
		});
		jd.add(DMButton, BorderLayout.CENTER);

		JButton PlayerButton = new JButton("Player");
		PlayerButton.addActionListener(e -> {
			jd.dispose();
			GoIPPlayer.main(args);
		});
		jd.add(PlayerButton, BorderLayout.SOUTH);
		jd.setVisible(true);
	}

	public static boolean checkVersion() {
		URL currentVersion;
		try {
			currentVersion = new URL("http://checkip.amazonaws.com");
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						currentVersion.openStream()));
		String result = in.readLine(); 
		System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
