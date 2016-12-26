package com.vincent.core.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class GUILogAppender extends AppenderSkeleton {

	public static JTextPane text = new JTextPane();
	public static Document doc = text.getDocument();
	public static SimpleAttributeSet attr = new SimpleAttributeSet();

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLevel().equals(Level.DEBUG)) {
			return;
		}
		String s = now("HH:mm:ss.SSS") + "[" + event.getThreadName() + "]" + " | " + event.getMessage() + "\n";
		Color c = Color.WHITE;
		if (event.getLevel().equals(Level.ERROR)) {
			c = Color.RED;
		} else if (event.getLevel().equals(Level.WARN)) {
			c = Color.YELLOW;
		}
		synchronized (attr) {
			try {
				StyleConstants.setBackground(attr, c);
				doc.insertString(doc.getLength(), s, attr);
			} catch (BadLocationException e) {

			}
		}
	}

	private static String now(final String pattern) {
		try {
			return new SimpleDateFormat(pattern == null ? "HH:mm:ss" : pattern).format(new Date());
		} catch (Exception e) {

		}
		return pattern;
	}

}
