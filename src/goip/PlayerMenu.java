package goip;

import java.io.*;
import java.util.*;

import javax.swing.*;

import chooseFile.*;
import dice.*;

public class PlayerMenu extends JMenuBar {

    static JMenuItem item;
    static JMenu menu;
    static boolean isDM;

    public PlayerMenu(ArrayList<DiceBag> dblist, boolean isDM) {
	this.isDM = isDM;
	menu = new JMenu("File");
	item = new JMenuItem("Save Dice Bags...");
	item.addActionListener(e -> {
	    try {
		File SaveTo = ChooseFile.saveFile("dbag");
		if (!SaveTo.getName().endsWith("dbag"))
		    SaveTo = new File(SaveTo.getAbsoluteFile() + ".dbag");
		SaveTo.createNewFile();
		FileWriter fw = new FileWriter(SaveTo);
		fw.write("");
		dblist.stream().forEach(x -> {
		    try {
			fw.append(x.stringForSave() + "\n");
		    } catch (Exception err) {
		    }
		});
		fw.close();
	    } catch (Exception error) {
		error.printStackTrace();
	    }
	});
	menu.add(item);
	item = new JMenuItem("Load Dice Bags...");
	item.addActionListener(e -> {
	    try {
		File LoadFrom = ChooseFile.loadFile("dbag");
		BufferedReader br = new BufferedReader(new FileReader(LoadFrom));
		br.lines().forEach(x -> {
		    DiceBag db;
		    if (isDM) {
			db = new DiceBag("DM");
			GoIPDM.setBehavior(db);
		    } else {
			db = new DiceBag("Player");
			GoIPPlayer.setBehavior(db);
		    }
		    db.setValues(x);
		    db.setVisible(true);
		});
		br.close();
	    } catch (Exception error) {
		error.printStackTrace();
	    }
	});
	menu.add(item);
	item = new JMenuItem("Exit");
	item.addActionListener(e -> {
	    System.exit(0);
	});
	menu.add(item);
	this.add(menu);
	menu = new JMenu("Help");
	item = new JMenuItem("Controls");
	item.addActionListener(e -> {
	    showHelp("Controls", helpControls);
	    help.setSize(475, 100);
	});
	menu.add(item);
	item = new JMenuItem("Commands");
	item.addActionListener(e -> {
	    showHelp("Commands", helpCommands);
	    help.setSize(275, 135);
	});
	menu.add(item);
	this.add(menu);
    }

    private static JDialog help;
    static final String helpCommands = "literal, (optional), [mandatory]\n"
	    + "ping\n" + "msg [Player] [your message to the player]\n"
	    + "(bc) [message to broadcast]\n" + "setname [new name]\n"
	    + "exit\nreset";
    static final String helpControls = "Press enter to broadcast a message.\n"
	    + "Click on a player in the playerlist to send a message to that player.\n"
	    + "Press left arrow to send another message to the last person you messaged.\n"
	    + "Press right to clear your message line.\n";

    public static void showHelp(String title, String message) {
	help = new JDialog();
	help.setContentPane(Box.createVerticalBox());
	help.setResizable(false);
	help.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	help.setTitle(title);
	help.setLocationRelativeTo(null);
	for (String x : message.split("\n")) {
	    help.add(new JLabel(x));
	}
	help.setVisible(true);
    }
}
