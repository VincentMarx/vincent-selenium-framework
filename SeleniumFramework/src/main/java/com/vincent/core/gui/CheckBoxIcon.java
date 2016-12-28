package com.vincent.core.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

/**
 * <p>a check box inside an icon, to display a check box in the header of a JTable</p>
 */
public class CheckBoxIcon implements Icon {
	private final JCheckBox checkbox;

	CheckBoxIcon(JCheckBox checkbox) {
		this.checkbox = checkbox;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		SwingUtilities.paintComponent(g, checkbox, (Container) c, x, y, getIconWidth(), getIconHeight());

	}

	@Override
	public int getIconWidth() {
		return checkbox.getPreferredSize().width;
	}

	@Override
	public int getIconHeight() {
		return checkbox.getPreferredSize().height;
	}
}