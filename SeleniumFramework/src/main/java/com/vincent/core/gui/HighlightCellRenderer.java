package com.vincent.core.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.vincent.core.testcase.Status;

public class HighlightCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (value != null) {
			Status status = (Status) value;
			switch (status) {
			case Running:
				setBackground(Color.CYAN);
				break;
			case Awaiting:
				setBackground(Color.GRAY);
				break;
			case Done:
				setBackground(Color.WHITE);
				break;
			case Pass:
				setBackground(Color.GREEN);
				break;
			case Warning:
				setBackground(Color.ORANGE);
				break;
			case Fail:
				setBackground(Color.RED);
				break;
			case Stop:
				setBackground(Color.CYAN);
				break;
			case Fatal:
				setBackground(Color.RED);
				break;
			default:
				setBackground(Color.WHITE);
				break;
			}
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
