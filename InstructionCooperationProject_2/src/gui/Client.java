package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Client extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final double width_ratio = 0.5, height_ratio = 0.5;
	private static final int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int frame_width = (int)(width_ratio * screen_width);
	private static final int frame_height = (int)(height_ratio * screen_height); 
	private static final String title_name = "Client";
	private static final double default_value = -1.0;	
	private static final String scanEnd = "#### END ####";
	private static final String xAxisName = "범위", yAxisName = "농도";
	private JPanel mainPanel, graphPanel, xlabelPanel, ylabelPanel;
	private JLabel xLabel;
	private JMenuBar menubar;
	private JMenu normal, analysis;
	private JMenuItem fileOpen, graphInit, exit, uniAnalysis, multiAnalysis;
	private JFileChooser fileChooser;
	private MenuItemActionListener listener;
	private ArrayList<Integer> fileDataCount;
	
	public Client()	{
		super(title_name);
		
		initObjects();
		initComponents();
		
		setLocation((screen_width - frame_width) / 2, (screen_height - frame_height) / 2);
		setPreferredSize(new Dimension(frame_width, frame_height));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void initObjects() {
		listener = new MenuItemActionListener();
		fileDataCount = new ArrayList<Integer>();
	}
	
	private void initComponents() {		
		initPanel();
		initMenuBar();
	}

	private void initPanel() {
		mainPanel = new JPanel(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		xlabelPanel = new JPanel();
		xLabel = new JLabel(xAxisName);
		xlabelPanel.add(xLabel);
		mainPanel.add(xlabelPanel, BorderLayout.SOUTH);
		ylabelPanel = new VerticalLabelPanel(yAxisName);
		mainPanel.add(ylabelPanel, BorderLayout.WEST);
		graphPanel = new GraphDisplayPanel();
		mainPanel.add(graphPanel, BorderLayout.CENTER);
	}

	private void initMenuBar() {
		menubar = new JMenuBar();
		add(menubar, BorderLayout.NORTH);
		normal = new JMenu("일반");
		menubar.add(normal);
		fileOpen = new JMenuItem("파일 열기");
		fileOpen.addActionListener(listener);
		normal.add(fileOpen);
		graphInit = new JMenuItem("그래프 초기화");
		graphInit.addActionListener(listener);
		normal.add(graphInit);
		exit = new JMenuItem("종료");
		exit.addActionListener(listener);
		normal.add(exit);
		analysis = new JMenu("분석");
		menubar.add(analysis);
		uniAnalysis = new JMenuItem("단일 분석");
		uniAnalysis.addActionListener(listener);
		analysis.add(uniAnalysis);
		multiAnalysis = new JMenuItem("단체 분석");
		multiAnalysis.addActionListener(listener);
		analysis.add(multiAnalysis);
	}

	private class MenuItemActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object selected = e.getSource();
			if(selected.equals(fileOpen)) {				
				fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
				fileChooser.setMultiSelectionEnabled(false);							
				if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)	{
					ArrayList<ArrayList<Point2D.Double>> result = getData(fileChooser.getSelectedFile());
					if(result != null) for(int i=0; i<result.size(); ++i) ((GraphDisplayPanel)graphPanel).add_graph(result.get(i));
				}
				else JOptionPane.showMessageDialog(null, "csv 파일이 선택되지 않았습니다.", "파일 열기 오류", JOptionPane.ERROR_MESSAGE);	
			} else if(selected.equals(graphInit)) {		
				remove(mainPanel); fileDataCount = new ArrayList<Integer>();
				initPanel(); revalidate(); repaint();
			} else if(selected.equals(exit))			
				System.exit(0);
			else if(selected.equals(uniAnalysis)) {	
				JTextField fileNumberTextField = new JTextField();
				JTextField dataNumberTextField = new JTextField();
				ArrayList<ArrayList<Point2D.Double>> data = new ArrayList<ArrayList<Point2D.Double>>();
				int confirm = JOptionPane.showConfirmDialog(null, new Object[] {
					    "파일 번호:", fileNumberTextField,
					    "데이터 번호:", dataNumberTextField
					}, "단일 데이터 입력", JOptionPane.OK_CANCEL_OPTION);
				if (confirm == JOptionPane.OK_OPTION) {	
					if(!isNumber(fileNumberTextField.getText()) || !isNumber(dataNumberTextField.getText()))
						JOptionPane.showMessageDialog(null, "숫자로 입력해 주십시오!", "입력 오류", JOptionPane.ERROR_MESSAGE);
					else {
						int fileNumber = Integer.parseInt(fileNumberTextField.getText());
						int dataNumber = Integer.parseInt(dataNumberTextField.getText());
						if(fileNumber < 1 || fileNumber > fileDataCount.size())	
							JOptionPane.showMessageDialog(null, "파일 번호 범위 초과!", "파일 오류", JOptionPane.ERROR_MESSAGE);
						else if(dataNumber < 1 || dataNumber > fileDataCount.get(fileNumber-1)) 
							JOptionPane.showMessageDialog(null, "데이터 번호 범위 초과!", "데이터 오류", JOptionPane.ERROR_MESSAGE);
						else {
							int index = 0;
							for(int i=1; i<fileNumber; ++i) index += fileDataCount.get(i-1);
							index += dataNumber-1;
							data.add(((GraphDisplayPanel)graphPanel).get_function(index));
							new AnalysisFrame(fileNumber, dataNumber, data);
						}
					}
				}
			} else if(selected.equals(multiAnalysis)) {
				JTextField fileNumberTextField = new JTextField();
				JTextField dataNumberTextField = new JTextField();
				ArrayList<ArrayList<Point2D.Double>> data = new ArrayList<ArrayList<Point2D.Double>>();
				int conti = 0;
				do {
					int confirm = JOptionPane.showConfirmDialog(null, new Object[]  {
							"파일 번호:", fileNumberTextField,
						    "데이터 번호:", dataNumberTextField
						}, "추가 데이터 입력", JOptionPane.OK_CANCEL_OPTION);
					if (confirm == JOptionPane.OK_OPTION) {
						if(!isNumber(fileNumberTextField.getText()) || !isNumber(dataNumberTextField.getText()))	// 숫자가 아닌 데이터가 있을 때,
							JOptionPane.showMessageDialog(null, "숫자로 입력해 주십시오!", "입력 오류", JOptionPane.ERROR_MESSAGE);
						else {
							int fileNumber = Integer.parseInt(fileNumberTextField.getText());
							int dataNumber = Integer.parseInt(dataNumberTextField.getText());
							if(fileNumber < 1 || fileNumber > fileDataCount.size())							// 파일 번호 범위 초과
								JOptionPane.showMessageDialog(null, "파일 번호 범위 초과!", "파일 오류", JOptionPane.ERROR_MESSAGE);
							else if(dataNumber < 1 || dataNumber > fileDataCount.get(fileNumber-1))		// 데이터 번호 범위 초과
								JOptionPane.showMessageDialog(null, "데이터 번호 범위 초과!", "데이터 오류", JOptionPane.ERROR_MESSAGE);
							else {
								int index = 0;
								for(int i=1; i<fileNumber; ++i)
									index += fileDataCount.get(i-1);
								index += dataNumber-1;
								data.add(((GraphDisplayPanel)graphPanel).get_function(index));
							}
						}
					}
					conti = JOptionPane.showConfirmDialog(null, "데이터를 더 추가하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
				}while(conti == JOptionPane.OK_OPTION);	
				if(data.size() > 0)	new AnalysisFrame(-1, -1, data);
			}
		}
	}

	private ArrayList<ArrayList<Point2D.Double>> getData(File file) {
		try {
			ArrayList<ArrayList<Point2D.Double>> tempData = new ArrayList<ArrayList<Point2D.Double>>();	
			Scanner input = new Scanner(file);
			String[] words = input.nextLine().split(",");
			int columnCount = words.length;
			double[] xPointer = new double[columnCount];
			Arrays.fill(xPointer, 1.0);	
			for(int i=0; i<columnCount; ++i) tempData.add(new ArrayList<Point2D.Double>());
			boolean blankLine = false, scan = false;
			while(input.hasNextLine()) {
				String line = input.nextLine();
				words = line.split(",");
				if(!scan && !blankLine && (line.equals("") || words.length == 0)) {
					blankLine = true;
					continue;
				}
				if(blankLine) {												
					scan = true;
					blankLine = false;
					continue;
				}
				if(scan && words.length > 0 && words[0].equals(scanEnd)) break;
				if(scan) {
					for(int i=0; i<words.length; ++i) {
						if(isNumber(words[i])) tempData.get(i).add(new Point2D.Double(xPointer[i], Double.parseDouble(words[i])));
						else tempData.get(i).add(new Point2D.Double(xPointer[i], default_value));
						++xPointer[i];
					}
				}
			}
			input.close();
			fileDataCount.add(columnCount);
			return tempData;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private boolean isNumber(String word) {
		try {
			Double.parseDouble(word);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
