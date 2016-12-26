package com.vincent.core.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang3.StringUtils;

public class CheckBoxCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Font font;

	public CheckBoxCellRenderer(Font font) {
		this.font = font;
	}

	JCheckBox checkbox = new JCheckBox();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		checkbox.setFont(font);
		checkbox.setText(StringUtils.leftPad(String.valueOf(row + 1), 3, " "));
		checkbox.setSelected(Boolean.TRUE.equals(value));
		checkbox.setHorizontalAlignment(SwingConstants.CENTER);
		checkbox.setBackground(table.getBackground());
		return checkbox;
	}

}
