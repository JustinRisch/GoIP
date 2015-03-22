package chooseFile;

import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

public abstract class ChooseFile {
    public static File saveFile(String ext) {
	JFileChooser fileChooser = new JFileChooser("Save as...");
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	FileFilter filter = new FileNameExtensionFilter(ext, ext);
	fileChooser.setAcceptAllFileFilterUsed(false);
	fileChooser.setFileFilter(filter);
	fileChooser.showSaveDialog(null);
	return fileChooser.getSelectedFile();
    }

    public static File loadFile(String ext) {
	JFileChooser fileChooser = new JFileChooser("Load File...");
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	FileFilter filter = new FileNameExtensionFilter(ext, ext);
	fileChooser.setFileFilter(filter);
	fileChooser.setAcceptAllFileFilterUsed(false);
	fileChooser.showOpenDialog(null);
	return fileChooser.getSelectedFile();
    }
}
