package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import analysis.ErrorEvalution;

public class AnalysisFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final double width_ratio = 0.6, height_ratio = 0.6;
	private static final int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int frame_width = (int)(width_ratio * screen_width);
	private static final int frame_height = (int)(height_ratio * screen_height); 
	private static final String title_name = "분석 화면", xAxisName = "범위", yAxisName = "농도";
	private JPanel mainPanel, graphPanel, xlabelPanel, ylabelPanel;
	private JButton analysisButton;
	private ButtonActionListener listener;
	private ArrayList<ArrayList<Point2D.Double>> functions;
	
	public AnalysisFrame(int fileNumber, int dataNumber, ArrayList<ArrayList<Point2D.Double>> functions) {
		if(fileNumber == -1 && dataNumber == -1) setTitle(title_name + " ( 단체 분석 모드 )");
		else setTitle(title_name + " ( " + fileNumber + "번째 파일 : " + dataNumber + "번째 데이터 )");
		this.functions = functions;
		initPanel();
		listener = new ButtonActionListener();
		analysisButton = new JButton("분석");
		analysisButton.addActionListener(listener);
		add(analysisButton, BorderLayout.SOUTH);
		drawGraph();
		setLocation((screen_width - frame_width) / 2, (screen_height - frame_height) / 2);
		setPreferredSize(new Dimension(frame_width, frame_height));
		pack();
		dispose();
		setVisible(true);
	}
	
	private void initPanel() {
		mainPanel = new JPanel(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		xlabelPanel = new JPanel();
		xlabelPanel.add(new JLabel(xAxisName));
		mainPanel.add(xlabelPanel, BorderLayout.SOUTH);
		ylabelPanel = new VerticalLabelPanel(yAxisName);
		mainPanel.add(ylabelPanel, BorderLayout.WEST);
		graphPanel = new GraphDisplayPanel();
		mainPanel.add(graphPanel, BorderLayout.CENTER);
	}

	private void drawGraph() {
		for(int i=0; i<functions.size(); ++i) ((GraphDisplayPanel)graphPanel).add_graph(functions.get(i));
	}
	
	public int analysis(ArrayList<Point2D.Double> data)
	{
		double[] datat = new double[150];
		for(int i=0; i<data.size(); i++)
			datat[i] = data.get(i).y;
		ErrorEvalution evaluation = new ErrorEvalution();
		
		// input algorithm
		// -3 모터동작 오류
		// -2 삽입 오류
		// -1 샘플분주위치 오류
		// 0 정상
		return evaluation.evaluate(datat);
	}
	
	private class ButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(analysisButton)) {
				if(functions.size() < 1) return;
				boolean valid = true;	
				int index = 0;					
				if(functions.size() > 1) {			
					String indexString = JOptionPane.showInputDialog("몇 번째 데이터를 분석하시겠습니까?");
					if(!isNumber(indexString)) {		
						JOptionPane.showMessageDialog(null, "숫자로 입력해 주십시오!", "입력 오류", JOptionPane.ERROR_MESSAGE);
						valid = false;
					}
					else {
						index = Integer.parseInt(indexString);
						if(index < 1 || index > functions.size()) {
							JOptionPane.showMessageDialog(null, "데이터 번호 범위 초과!", "데이터 오류", JOptionPane.ERROR_MESSAGE);
							valid = false;
						}
						else --index;
					}
				}
				if(valid) {	
					int result = analysis(functions.get(index));
					switch(result) {
					case -3 : JOptionPane.showMessageDialog(null, "모터동작 오류 데이터", "결과", JOptionPane.ERROR_MESSAGE); break;
					case -2 : JOptionPane.showMessageDialog(null, "삽입 오류 데이터", "결과", JOptionPane.ERROR_MESSAGE);	break;
					case -1 : JOptionPane.showMessageDialog(null, "샘플분주위치 오류 데이터", "결과", JOptionPane.ERROR_MESSAGE); break;
					case 0 : JOptionPane.showMessageDialog(null, "정상 데이터", "결과", JOptionPane.INFORMATION_MESSAGE); break;
					}
				}
			}
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
