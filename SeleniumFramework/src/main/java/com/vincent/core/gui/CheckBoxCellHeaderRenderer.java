package com.vincent.core.gui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CheckBoxCellHeaderRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JCheckBox checkbox;

	public CheckBoxCellHeaderRenderer(final JCheckBox checkbox) {
		this.checkbox = checkbox;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component component = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		JLabel l = (JLabel) component;
		l.setIcon(new CheckBoxIcon(checkbox));
		return l;
	}

}
