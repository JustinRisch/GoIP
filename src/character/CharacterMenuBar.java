package character;

import java.io.*;
import java.util.concurrent.atomic.*;

import javax.swing.*;

import chooseFile.*;

@SuppressWarnings("serial")
public class CharacterMenuBar extends JMenuBar {
    public CharacterMenuBar(String[] labels, JTextField[] stats,
	    JTextField[] tempMods) {
	JMenu menu = new JMenu("File");
	JMenuItem save = new JMenuItem("Save");
	save.addActionListener(e -> {
	    try {
		File SaveTo = ChooseFile.saveFile("CSJ");
		if (!SaveTo.getName().endsWith("CSJ"))
		    SaveTo = new File(SaveTo.getAbsoluteFile() + ".CSJ");
		SaveTo.createNewFile();
		FileWriter fw = new FileWriter(SaveTo);
		fw.write("");
		for (int i = 0; i < 6; i++) {
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

	    try {

		BufferedReader read = new BufferedReader(new FileReader(
			ChooseFile.loadFile("CSJ")));

		AtomicInteger f = new AtomicInteger(0);
		read.lines().forEachOrdered(x -> {
		    String[] values = x.split(":");
		    switch (values.length) {
		    case 3:
			tempMods[f.get()].setText(values[2]);
		    case 2:
			stats[f.get()].setText(values[1]);
			break;
		    }
		    f.incrementAndGet();
		});
		CharacterSheet.refresh();
		read.close();
	    } catch (Exception error) {
		error.printStackTrace();
	    }
	});
	menu.add(load);
	this.add(menu);

    }
}
