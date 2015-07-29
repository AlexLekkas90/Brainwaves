package main.java.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class CustomDocument extends PlainDocument {
	private int charLimit;

	public CustomDocument(int charLimit) {
		this.charLimit = charLimit;
	}

	public void insertString(int offset, String s, AttributeSet attr)
			throws BadLocationException {
		if (s == null) {
			return;
		} else if ((getLength() + s.length()) <= charLimit) {
			super.insertString(offset, s, attr);
		}
	}
}
