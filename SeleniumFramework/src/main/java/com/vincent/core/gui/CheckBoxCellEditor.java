package com.vincent.core.gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;

public class CheckBoxCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	private Font font;

	public CheckBoxCellEditor(Font font) {
		super(new JCheckBox());
		this.font = font;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JCheckBox checkbox = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		checkbox.setFont(font);
		checkbox.setText(StringUtils.leftPad(String.valueOf(row + 1), 3, " "));
		checkbox.setSelected(Boolean.TRUE.equals(value));
		checkbox.setHorizontalAlignment(SwingConstants.CENTER);
		checkbox.setBackground(table.getBackground());
		return checkbox;
	}

}
