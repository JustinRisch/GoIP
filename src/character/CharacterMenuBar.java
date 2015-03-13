/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package character;

import java.io.*;
import java.util.concurrent.atomic.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

/**
 */
public class CharacterMenuBar extends JMenuBar {
    public CharacterMenuBar(String[] labels, JTextField[] stats,
	    JTextField[] tempMods) {
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

		read.close();
	    } catch (Exception error) {
		error.printStackTrace();
	    }
	});
	menu.add(load);
	this.add(menu);

    }
}
