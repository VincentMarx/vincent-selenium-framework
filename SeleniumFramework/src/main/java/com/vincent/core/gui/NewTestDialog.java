package com.vincent.core.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vincent.core.config.Config;
import com.vincent.core.execution.ExecutionService;

public class NewTestDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(NewTestDialog.class);

	private final JPanel pane = new JPanel();
	private JComboBox<String> dataFile = new JComboBox<String>();
	private JTextField outputFolder = new JTextField();
	private JFileChooser fc = new JFileChooser();
	private JButton btnChooseFile = new JButton("...");

	private ExecutionService service;

	public NewTestDialog(JFrame owner, String title, boolean modal, ExecutionService service) {
		super(owner, title, modal);
		this.service = service;

		setResizable(false);
		setBounds(100, 100, 650, 130);
		getContentPane().setLayout(new BorderLayout());
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pane, BorderLayout.CENTER);

		JLabel lDataFile = new JLabel("Data File: ");
		JLabel lOutput = new JLabel("Output Folder: ");

		JPanel dataFilePane = new JPanel();
		dataFilePane.setLayout(new BoxLayout(dataFilePane, BoxLayout.LINE_AXIS));
		dataFilePane.add(Box.createHorizontalGlue());
		Dimension dimension = new Dimension(480, 23);
		dataFile.setMaximumSize(dimension);
		dataFile.setMinimumSize(dimension);
		dataFile.setPreferredSize(dimension);
		dataFilePane.add(dataFile);
		dataFilePane.add(btnChooseFile);

		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);

		SequentialGroup hg = gl.createSequentialGroup();
		hg.addGroup(gl.createParallelGroup().addComponent(lDataFile).addComponent(lOutput));
		hg.addGroup(gl.createParallelGroup().addComponent(dataFilePane).addComponent(outputFolder));
		gl.setHorizontalGroup(hg);

		SequentialGroup vg = gl.createSequentialGroup();
		vg.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(lDataFile).addComponent(dataFilePane));
		vg.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(lOutput).addComponent(outputFolder));
		gl.setVerticalGroup(vg);

		dataFile.setEditable(true);
		outputFolder.setEditable(true);

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				String fileName = f.getName().toLowerCase();
				if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "Excel files: xls, xlsx, xlsm";
			}

		});

		btnChooseFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int state = fc.showOpenDialog(NewTestDialog.this);
				if (state == JFileChooser.APPROVE_OPTION) {
					File file = NewTestDialog.this.fc.getSelectedFile();
					dataFile.setSelectedItem(file.getPath());
				}
			}
		});

		JPanel btnPane = new JPanel();
		btnPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(btnPane, BorderLayout.SOUTH);

		JButton btnOK = new JButton("OK");
		btnOK.setActionCommand("OK");
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("Cancel");
		btnPane.add(btnOK);
		btnPane.add(btnCancel);

		btnOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOK_actionPerformed();
			}

		});

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				NewTestDialog.this.dispose();
			}

		});

		scanFile();

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	private void btnOK_actionPerformed() {
		String dataFilePath = (String) dataFile.getSelectedItem();
		String outputPath = outputFolder.getText();
		if (StringUtils.isBlank(dataFilePath)) {
			JOptionPane.showMessageDialog(this, "Please select a file.");
			return;
		}
		if (!new File(dataFilePath).exists()) {
			JOptionPane.showMessageDialog(this, "File does not exist.");
			return;
		}
		service.init(dataFilePath, outputPath);
		if (service.getTestSet() == null || service.getTestSet().size() == 0) {
			JOptionPane.showMessageDialog(this,
					"No test case found in data file.  \nPlease check if you select a correct file.");
			return;
		}
		this.dispose();
	}

	private void scanFile() {
		File inputPath = new File(Config.dataFileFolder);
		if (inputPath.exists()) {
			File[] dataFiles = inputPath.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					String fileName = name.toLowerCase();
					if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) {
						return true;
					}
					return false;
				}
			});

			for (File file : dataFiles) {
				String path = file.getPath();
				path = StringUtils.substringAfter(path, Config.dataFileFolder);
				path = Config.dataFileFolder + path;
				dataFile.addItem(path);
			}
		}
		outputFolder.setText(Config.outputRootPath);
	}
}
