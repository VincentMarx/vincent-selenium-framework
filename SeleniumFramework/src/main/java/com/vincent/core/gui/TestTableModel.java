package com.vincent.core.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.vincent.core.testcase.Test;

public class TestTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// test case list, or test step list
	private List<?> testList;
	private String[] columnNames;

	public TestTableModel(String[] columnNames) {
		this.columnNames = columnNames;
	}

	@Override
	public int getRowCount() {
		return testList == null ? 0 : testList.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		Test testItem = (Test) testList.get(row);
		return testItem.getValueAt(column);
	}

	@Override
	public String getColumnName(final int column) {
		String columnName = null;
		if (column < columnNames.length) {
			columnName = columnNames[column];
		} else {
			columnName = super.getColumnName(column);
		}
		return columnName;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0) {
			Test testItem = (Test) testList.get(row);
			testItem.setChecked(Boolean.TRUE.equals(aValue));
		}
	}

	public List<?> getTestList() {
		return testList;
	}

	public void setTestList(List<?> testList) {
		this.testList = testList;
	}

}
