/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package goip;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

import dice.*;

/**
 */
public class PlayerMenu extends JMenuBar {

    static JMenuItem item;
    static JMenu menu;
    static boolean isDM;

    public PlayerMenu(ArrayList<DiceBag> dblist, boolean isDM) {
	this.isDM = isDM;
	menu = new JMenu("File");
	item = new JMenuItem("Save Dice Bags...");

	item.addActionListener(e -> {
	    JFileChooser fileChooser = new JFileChooser("Save as...");

	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    FileFilter filter = new FileNameExtensionFilter("dbag",
		    "dbag");
	    fileChooser.setAcceptAllFileFilterUsed(false);
	    fileChooser.setFileFilter(filter);
	    fileChooser.showSaveDialog(null);
	    try {
		File SaveTo;
		if (fileChooser.getSelectedFile().toString().endsWith(".dbag"))
		    SaveTo = fileChooser.getSelectedFile();
		else
		    SaveTo = new File(fileChooser.getSelectedFile().toString()
			    + ".dbag");
		SaveTo.createNewFile();
		FileWriter fw = new FileWriter(SaveTo);
		fw.write("");
		dblist.stream().forEach(x -> {
		    System.out.println(x.stringForSave());
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
	    JFileChooser fileChooser = new JFileChooser("Load File...");

	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    FileFilter filter = new FileNameExtensionFilter("dbag files only",
		    "dbag");
	    fileChooser.setFileFilter(filter);
	    fileChooser.setAcceptAllFileFilterUsed(false);
	    fileChooser.showOpenDialog(null);
	    try {
		File LoadFrom = fileChooser.getSelectedFile();
		BufferedReader br = new BufferedReader(new FileReader(LoadFrom));
		br.lines().forEach(
			x -> {
			    System.out.println(x);
			    DiceBag db;
			    if (isDM) {
				db = new DiceBag("DM");
				db.setButtonBehavior(y -> {
				    GoIPDM.chatArea.append(db.localRoll()
					    + "\n");
				    if (db.getShowRoll())
					GoIPDM.broadcast(" rolled a "
						+ db.localRoll().substring(3));
				});
				db.setStatButtonBehavior(z -> GoIPDM.chatArea
					.append(DiceRoll.statroll() + "\n"));
			    } else {
				db = new DiceBag("Player");
				if (GoIPPlayer.connected) {
					db.setButtonBehavior(y -> {
					    String z = db.localRoll();
					    GoIPPlayer.out.println("db~" + z.replace(GoIPPlayer.me, ""));
					    GoIPPlayer.chatArea.append(z + "\n");
					});
					db.setStatButtonBehavior(z -> {
					    String y = DiceRoll.statroll();
					    GoIPPlayer.chatArea.append(y + "\n");
					    GoIPPlayer.out.println("db~" + y);
					});
				    } else {
					db.setStatButtonBehavior(z -> {
					    String y = DiceRoll.statroll();
					    GoIPPlayer.chatArea.append(y+"\n");
					});
					db.setButtonBehavior(z -> GoIPPlayer.chatArea.append(db.localRoll() + "\n"));
				    }
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

    }
}
