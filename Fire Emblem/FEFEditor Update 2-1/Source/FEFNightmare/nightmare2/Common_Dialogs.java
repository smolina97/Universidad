/*
 *  Nightmare 2.0 - General purpose file editor
 *
 *  Copyright (C) 2009 Hextator,
 *  hectorofchad (AIM) hectatorofchad@sbcglobal.net (MSN)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3
 *  as published by the Free Software Foundation
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  <Description> Convenience class for abusing JOptionPane
 */

package nightmare2;

import java.awt.FileDialog;
import java.io.File;

import javax.swing.JOptionPane;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Common_Dialogs {
	public static void showGenericErrorDialog(final String message) {
		/**
		 * This had to be removed due to Java version issues;
		 * it now prints to the standard out
		javax.swing.JTextArea eTextArea =
			new javax.swing.JTextArea();
		eTextArea.setText(message + "\n");
		eTextArea.setRows(16);
		eTextArea.setColumns(48);
		eTextArea.setEditable(false);
		javax.swing.JScrollPane eScrollPane =
			new javax.swing.JScrollPane(eTextArea);
		JOptionPane.showMessageDialog(
			null, eScrollPane, "Error",
			JOptionPane.ERROR_MESSAGE
		);
		**/
		System.out.println(message);
	}

	public static <E extends Exception> void showCatchErrorDialog(E e) {
		if (e == null) return;
		StackTraceElement[] stackTrace = e.getStackTrace();
		StackTraceElement exceptSource = stackTrace[0];
		String methodName = exceptSource.getClassName()
			+ "." + exceptSource.getMethodName();
		String exceptMessage = e.getMessage();
		if (exceptMessage == null) exceptMessage = "Unknown exception";
		exceptMessage = "\t" + exceptMessage.replaceAll("\n", "\n\t");
		String message = methodName + ":\n" + exceptMessage;
		showGenericErrorDialog(message);
	}

	public static void showGenericInformationDialog(String message) {
		JOptionPane.showMessageDialog(
			null, message, "Notice",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	public static int showYesNoDialog(String message) {
		return JOptionPane.showConfirmDialog(
			null, message, "Input",
			JOptionPane.YES_NO_OPTION
		);
	}

	private static File fileHelper(String title, int mode, Window owner) {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files", "*");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Compressed Bin Files (*.bin.lz)", "*.bin.lz");
		chooser.getExtensionFilters().addAll(allFilter, extFilter);
		chooser.setTitle(title);
		if(mode == FileDialog.LOAD)
			return chooser.showOpenDialog(owner);
		else if(mode == FileDialog.SAVE)
			return chooser.showSaveDialog(owner);
		else
			return null;
	}

	public static File showOpenFileDialog(String what, Window owner) {
		return fileHelper("Select " + what + " for opening", FileDialog.LOAD, owner);
	}

	public static File showSaveFileDialog(String what, Window owner) {
		return fileHelper("Select path to save " + what,FileDialog.SAVE, owner);
	}
}
