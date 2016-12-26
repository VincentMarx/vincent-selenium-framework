package com.vincent.core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vincent.core.execution.Context;
import com.vincent.core.execution.ExecutionService;
import com.vincent.core.testcase.TestCase;

public class Console {
	private static Log log = LogFactory.getLog(Console.class);

	private JFrame frame = new JFrame("Automation Framework");
	private Font font = new Font("Courier New", 0, 12);

	private String[] testCaseTableHeaders = new String[] { "SN", "Test Case Name", "Runs", "Status" };
	private TestTableModel testCaseModel;
	private JTable testCaseTable;
	private JCheckBox selectAllCase = new JCheckBox();

	private String[] testStepTableHeaders = new String[] { "SN", "Test Step Name", "Status" };
	private TestTableModel testStepModel;
	private JTable testStepTable;
	private JCheckBox selectAllStep = new JCheckBox();
	// TODO may be this variable is no need.

	private JMenu mnTest = new JMenu("Test");
	private JMenuItem mnNew = new JMenuItem("New Test");
	private JMenuItem mnRctTest = new JMenuItem("Recent Test");
	private JButton btnNewRun = new JButton("New Run");
	private JButton btnStop = new JButton("Stop");
	private JButton btnPause = new JButton("Pause");
	private JButton btnResume = new JButton("Resume");
	private JButton btnRefresh = new JButton("Refresh");

	private JButton btnRunStep = new JButton("Run Step By Step");
	private JButton btnRunAllStep = new JButton("Run");
	private JButton btnStopStep = new JButton("Stop");
	private JProgressBar progressBar = new JProgressBar();
	private ExecutionService service;

	public Console() {
		log.info("init console UI");
		frame.setSize(900, 700);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		testCaseModel = new TestTableModel(testCaseTableHeaders);

		testCaseTable = new JTable(testCaseModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(final MouseEvent e) {
				int column = this.columnAtPoint(e.getPoint());
				return super.getToolTipText();
			}

		};

		DefaultCellEditor cbEditor = new CheckBoxCellEditor(font);
		DefaultTableCellRenderer cbRenderer = new CheckBoxCellRenderer(font);
		DefaultTableCellRenderer caseHeaderRenderer = new CheckBoxCellHeaderRenderer(selectAllCase);
		MouseAdapter caseAdapter = new HeaderCheckBoxMouseAdapter(testCaseTable, selectAllCase);
		testCaseTable.getColumnModel().getColumn(0).setCellEditor(cbEditor);
		testCaseTable.getColumnModel().getColumn(0).setCellRenderer(cbRenderer);
		testCaseTable.getColumn("SN").setHeaderRenderer(caseHeaderRenderer);
		testCaseTable.getColumn("Status").setCellRenderer(new HighlightCellRenderer());
		testCaseTable.getTableHeader().addMouseListener(caseAdapter);
		testCaseTable.getSelectionModel().addListSelectionListener(new TestCaseTableModelListener());

		testStepModel = new TestTableModel(testStepTableHeaders);
		testStepTable = new JTable(testStepModel);
		DefaultTableCellRenderer stepHeaderRenderer = new CheckBoxCellHeaderRenderer(selectAllStep);
		MouseAdapter stepAdapter = new HeaderCheckBoxMouseAdapter(testStepTable, selectAllStep);
		testStepTable.getColumnModel().getColumn(0).setCellEditor(cbEditor);
		testStepTable.getColumnModel().getColumn(0).setCellRenderer(cbRenderer);
		testStepTable.getColumn("SN").setHeaderRenderer(stepHeaderRenderer);
		testStepTable.getColumn("Status").setCellRenderer(new HighlightCellRenderer());
		testStepTable.getTableHeader().addMouseListener(stepAdapter);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(500);

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(mainSplitPane);

		JScrollPane logPane = new JScrollPane();
		logPane.setViewportView(GUILogAppender.text);
		GUILogAppender.text.setEditable(false);
		splitPane.setBottomComponent(logPane);

		mainSplitPane.setDividerLocation(600);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setDividerSize(8);

		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel leftPane = new JPanel(new BorderLayout());
		JPanel rightPane = new JPanel(new BorderLayout());
		mainSplitPane.setLeftComponent(leftPane);
		mainSplitPane.setRightComponent(rightPane);

		JScrollPane testCasePane = new JScrollPane();
		testCasePane.setViewportView(testCaseTable);

		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(mnTest);
		mnTest.add(mnNew);
		mnTest.addSeparator();
		mnTest.add(mnRctTest);
		// TODO: launch from history
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		mnNew.addActionListener(new NewMenuItemListener());

		// test case tool bar
		JToolBar testCaseToolBar = new JToolBar();
		setPreferDimension(btnNewRun);
		setPreferDimension(btnStop);
		testCaseToolBar.add(btnNewRun);
		testCaseToolBar.add(btnStop);
		btnStop.setVisible(false);

		testCaseToolBar.addSeparator();
		setPreferDimension(btnPause);
		setPreferDimension(btnResume);
		testCaseToolBar.add(btnPause);
		testCaseToolBar.add(btnResume);
		testCaseToolBar.addSeparator();
		testCaseToolBar.add(btnRefresh);
		btnResume.setVisible(false);
		btnPause.setEnabled(false);
		testCaseToolBar.addSeparator();

		leftPane.add(testCaseToolBar, BorderLayout.NORTH);
		leftPane.add(testCasePane, BorderLayout.CENTER);
		leftPane.add(progressBar, BorderLayout.SOUTH);
		progressBar.setVisible(true);
		progressBar.setStringPainted(true);

		// right pane, step actions
		JScrollPane testStepPane = new JScrollPane();
		testStepPane.setViewportView(testStepTable);
		JToolBar testStepToolBar = new JToolBar("Actions");
		rightPane.add(testStepToolBar, BorderLayout.NORTH);
		rightPane.add(testStepPane, BorderLayout.CENTER);
		setPreferDimension(btnRunStep);
		setPreferDimension(btnRunAllStep);
		setPreferDimension(btnStopStep);
		testStepToolBar.add(btnRunStep);
		testStepToolBar.addSeparator();
		testStepToolBar.add(btnRunAllStep);
		testStepToolBar.addSeparator();
		testStepToolBar.add(btnStopStep);
		btnStopStep.setVisible(false);

		resizeColumnWidth(this.testCaseTable);
		resizeColumnWidth(this.testStepTable);

		this.btnNewRun.addActionListener(new NewRunListener());
		this.btnStop.addActionListener(new StopListener());
		this.btnPause.addActionListener(new PauseListener());
		this.btnResume.addActionListener(new ResumeListener());
		this.btnRefresh.addActionListener(new RefreshListener());

		this.btnRunAllStep.addActionListener(new RunAllStepListener());
		this.btnStopStep.addActionListener(new StopListener());
	}

	class TestCaseTableModelListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			int rowIndex = testCaseTable.getSelectedRow();
			if (rowIndex == -1) {
				return;
			}
			rowIndex = testCaseTable.convertRowIndexToModel(rowIndex);
			TestCase tc = service.getTestSet().get(rowIndex);
			selectTestCase(tc);
		}
	}

	private void selectTestCase(TestCase tc) {
		testStepModel.setTestList(tc.getTestSteps());
		testStepModel.fireTableDataChanged();
		resizeColumnWidth(testStepTable);
	}

	class NewMenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Console.this.service = new ExecutionService();
			new NewTestDialog(Console.this.frame, "New Test", true, Console.this.service);
			Console.this.testCaseModel.setTestList(service.getTestSet());
			Console.this.testCaseModel.fireTableDataChanged();
			resizeColumnWidth(Console.this.testCaseTable);
		}
	}

	// for test case pane
	class NewRunListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Console.this.service == null || Console.this.service.getTestSet() == null
					|| Console.this.service.getTestSet().size() == 0) {
				JOptionPane.showMessageDialog(Console.this.frame, "Please load test data file first.");
				return;
			}
			Thread execThread = new Thread(new Runnable() {
				@Override
				public void run() {
					Console.this.service.executeTestSet();
				}
			});
			Console.this.service.setTotal(0);
			Console.this.service.setStop(false);
			Thread guiThread = new Thread(new GUIManager(execThread, GUIManager.RUN_TEST_SET));
			execThread.start();
			guiThread.start();
		}
	}

	class PauseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Console.this.service.setPause(true);
			Console.this.btnPause.setVisible(false);
			Console.this.btnResume.setVisible(true);
		}
	}

	class RefreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Console.this.service == null || Console.this.service.getTestSet() == null
					|| Console.this.service.getTestSet().size() == 0) {
				JOptionPane.showMessageDialog(Console.this.frame, "Please load test data file first.");
				return;
			}
			Console.this.service.refresh();
			Console.this.testCaseModel.setTestList(service.getTestSet());
			Console.this.testCaseModel.fireTableDataChanged();
		}

	}

	class ResumeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Console.this.service.setPause(false);
			Console.this.btnPause.setVisible(true);
			Console.this.btnResume.setVisible(false);
		}
	}

	class StopListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Console.this.service.setStop(true);
		}
	}

	// for test step panel

	class RunAllStepListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final Context context = new Context();
			int selectedRow = Console.this.testCaseTable.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}
			int rowInModel = Console.this.testCaseTable.convertRowIndexToModel(selectedRow);
			final TestCase testCase = Console.this.service.getTestSet().get(rowInModel);
			Thread execThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Console.this.service.executeTestCase(testCase, context);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			});
			Thread guiThread = new Thread(new GUIManager(execThread, GUIManager.RUN_TEST_SET));
			execThread.start();
			guiThread.start();
		}

	}

	class GUIManager implements Runnable {
		Thread execThread;
		String runMode;

		public GUIManager(Thread execThread, String runMode) {
			this.execThread = execThread;
			this.runMode = runMode;
		}

		public static final String RUN_TEST_SET = "RunTestSet";
		public static final String RUN_TEST_CASE = "RunTestCase";
		public static final String RUN_TEST_CASE_STEP_BY_STEP = "RunTestCaseStepByStep";

		@Override
		public void run() {
			if (RUN_TEST_SET.equalsIgnoreCase(runMode)) {
				Console.this.btnNewRun.setVisible(false);
				Console.this.btnStop.setVisible(true);
				Console.this.btnPause.setEnabled(true);

				Console.this.btnRunStep.setEnabled(false);
				Console.this.btnRunAllStep.setEnabled(false);
				Console.this.btnStopStep.setEnabled(false);
				Console.this.btnRefresh.setEnabled(false);

				Console.this.progressBar.setMaximum(service.getTotal());

			} else if (RUN_TEST_CASE.equalsIgnoreCase(runMode)) {
				Console.this.btnNewRun.setEnabled(false);
				Console.this.btnRunStep.setEnabled(false);
				Console.this.btnRunAllStep.setEnabled(false);
				Console.this.btnStopStep.setEnabled(true);
			} else if (RUN_TEST_CASE_STEP_BY_STEP.equalsIgnoreCase(runMode)) {
				Console.this.btnNewRun.setEnabled(false);
				Console.this.btnRunStep.setEnabled(false);
				Console.this.btnRunAllStep.setEnabled(false);
				Console.this.btnStopStep.setEnabled(false);
			}

			int caseSelectedRow = Console.this.testCaseTable.getSelectedRow();
			int stepSelectedRow = Console.this.testStepTable.getSelectedRow();
			Console.this.testCaseTable.setEnabled(false);
			Console.this.testStepTable.setEnabled(false);

			while (this.execThread != null && this.execThread.isAlive()) {
				if (RUN_TEST_SET.equalsIgnoreCase(runMode)) {
					Console.this.progressBar.setValue(Console.this.service.getExecuted());
					Console.this.progressBar.setString("Executing ... " + Console.this.service.getExecuted() + " / "
							+ Console.this.service.getTotal());
					Console.this.testCaseModel.fireTableDataChanged();
					if (Console.this.service.getCurTestCaseIdx() != -1) {
						try {
							int rowIndex = Console.this.testCaseTable
									.convertRowIndexToView(Console.this.service.getCurTestCaseIdx());
							// if (rowIndex !=
							// Console.this.testCaseTable.getSelectedRow()) {
							Console.this.testCaseTable.setRowSelectionInterval(rowIndex, rowIndex);
							// }
						} catch (IndexOutOfBoundsException e) {
							log.info("The test case that is beening executed is not in Test Case table view.");
						}

					}
				}
				if (Console.this.service.getCurStepIdx() != -1) {
					try {
						int rowIndex = Console.this.testStepTable
								.convertRowIndexToView(Console.this.service.getCurStepIdx());
						// if (rowIndex !=
						// Console.this.testStepTable.getSelectedRow()) {
						Console.this.testStepTable.setRowSelectionInterval(rowIndex, rowIndex);
						// }
					} catch (IndexOutOfBoundsException e) {
						log.info("The test step that is beening executed is not in Test Step table view.");
					}

				}
				Console.this.testStepModel.fireTableDataChanged();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}

			Console.this.btnNewRun.setVisible(true);
			Console.this.btnStop.setVisible(false);
			Console.this.btnPause.setEnabled(false);
			Console.this.btnRefresh.setEnabled(true);

			Console.this.btnRunStep.setEnabled(true);
			Console.this.btnRunAllStep.setEnabled(true);
			Console.this.btnStopStep.setEnabled(false);

			if (RUN_TEST_SET.equalsIgnoreCase(runMode)) {
				Console.this.progressBar.setValue(Console.this.service.getExecuted());
				Console.this.progressBar.setString("Execution Ended " + Console.this.service.getExecuted() + " / "
						+ Console.this.service.getTotal());
			}

			if (caseSelectedRow != -1) {
				Console.this.testCaseTable.setRowSelectionInterval(caseSelectedRow, caseSelectedRow);
			}
			if (stepSelectedRow != -1) {
				Console.this.testStepTable.setRowSelectionInterval(stepSelectedRow, stepSelectedRow);
			}
			Console.this.testCaseTable.setEnabled(true);
			Console.this.testStepTable.setEnabled(true);
		}

	}

	private void resizeColumnWidth(final JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 50;
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private void setPreferDimension(final Component comp) {
		Dimension dimension = new Dimension(100, 23);
		comp.setPreferredSize(dimension);
		comp.setMaximumSize(dimension);
		comp.setMinimumSize(dimension);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Console console = new Console();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		});

	}

}
