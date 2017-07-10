package com.snailnlp.parse.viewer;

import javax.swing.SwingUtilities;

public class ParserView {

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ParserMain();
			}
		});
	}
}
