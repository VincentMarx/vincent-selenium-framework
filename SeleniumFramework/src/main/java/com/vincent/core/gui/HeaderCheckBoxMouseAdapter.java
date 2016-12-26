package com.vincent.core.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class HeaderCheckBoxMouseAdapter extends MouseAdapter {
	private JTable table;
	private JCheckBox checkbox;

	public HeaderCheckBoxMouseAdapter(JTable table, JCheckBox checkbox) {
		this.table = table;
		this.checkbox = checkbox;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		TableColumnModel columnModel = table.getColumnModel();
		int viewColumn = columnModel.getColumnIndexAtX(e.getX());
		int modelColumn = table.convertColumnIndexToModel(viewColumn);
		if (modelColumn == 0) {
			checkbox.setSelected(!checkbox.isSelected());
			TableModel m = table.getModel();
			Boolean selected = checkbox.isSelected();
			for (int i = 0; i < table.getRowCount(); i++) {
				int row = table.convertRowIndexToModel(i);
				if (!selected.equals(m.getValueAt(row, 0))) {
					m.setValueAt(selected, row, 0);
				}
			}
		}
	}
}
