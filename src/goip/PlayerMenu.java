/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package goip;

import java.io.*;
import java.util.*;

import javax.swing.*;

import chooseFile.*;
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
		br.lines()
			.forEach(
				x -> {
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

    }
}
