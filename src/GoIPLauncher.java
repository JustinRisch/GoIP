import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class GoIPLauncher {
	public static void main(String[] args) {

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

}
