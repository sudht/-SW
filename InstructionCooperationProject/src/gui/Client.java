package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
// import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fileProcessing.Data;
//import fileProcessing.GraphSaveLoad;

public class Client	extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final double width_ratio = 0.5, height_ratio = 0.5;
	private static final int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int frame_width = (int)(width_ratio * screen_width);
	private static final int frame_height = (int)(height_ratio * screen_height); 
	private static final String title_name = "Client"; 

	private static final String xAxisName = "Range", yAxisName = "Concentration";

	private boolean loadSuccess, normalCheck;
	private int analysisNumber;

	private JPanel graphPanel, graphDisplayPanel, xlabelPanel, ylabelPanel, analysisPanel, menuPanel;
	private JLabel xLabel;
	private JButton loadButton, analysisButton, submitButton, exitButton;
	private Data data;
	private static String IP;
	// private GraphSaveLoad<GraphDisplayPanel> gsl;

	public Client()	{
		super(title_name);
		loadSuccess = false;
		analysisNumber= 0;

		initAllPanel();

		setLocation((screen_width - frame_width) / 2, (screen_height - frame_height) / 2);
		setPreferredSize(new Dimension(frame_width, frame_height));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void initAllPanel()	{
		analysisPanel = new JPanel();
		add(analysisPanel, BorderLayout.EAST);

		menuPanel = new JPanel();
		add(menuPanel, BorderLayout.SOUTH);

		loadButton = new JButton("읽기");
		loadButton.addActionListener(new ButtonActionListener());
		menuPanel.add(loadButton);

		menuPanel.add(new JLabel("               "));

		analysisButton = new JButton("분석");
		analysisButton.addActionListener(new ButtonActionListener());
		analysisButton.setEnabled(false);
		menuPanel.add(analysisButton);

		menuPanel.add(new JLabel("               "));

		submitButton = new JButton("전송");
		submitButton.addActionListener(new ButtonActionListener());
		submitButton.setEnabled(false);
		menuPanel.add(submitButton);

		menuPanel.add(new JLabel("               "));

		exitButton = new JButton("종료");
		exitButton.addActionListener(new ButtonActionListener());
		menuPanel.add(exitButton);
		menuPanel.setBackground(Color.CYAN);

		new ButtonActionListener().setButton();
	}

	public static void main(String[] args) {
		// Scanner sc = new Scanner(System.in);
		// IP = sc.next();
		IP = "210.178.95.117";
		new Client();
		// sc.close();
	}

	public void load() {
		try {
			data = new Data();
			loadSuccess = true;		// 읽기가 성공 했을 때,
		} catch (Exception e) {
			this.setTitle(title_name + " - 읽기 실패");
			loadSuccess = false;
		}
	}

	public void analysis() {
		if(graphPanel != null) remove(graphPanel);
		graphPanel = new JPanel(new BorderLayout());

		xlabelPanel = new JPanel();
		xLabel = new JLabel(xAxisName);
		xlabelPanel.add(xLabel);
		graphPanel.add(xlabelPanel, BorderLayout.SOUTH);

		ylabelPanel = new VerticalLabelPanel(yAxisName);
		graphPanel.add(ylabelPanel, BorderLayout.WEST);

		graphDisplayPanel = new GraphDisplayPanel();
		graphPanel.add(graphDisplayPanel, BorderLayout.CENTER);
		add(graphPanel, BorderLayout.CENTER);
		revalidate(); repaint();

		GraphDisplayPanel gp = (GraphDisplayPanel) graphDisplayPanel;
		double[][][] dataValue = data.getData();

		for(int i=0; i<dataValue[analysisNumber][0].length; ++i) {
			ArrayList<Point2D.Double> testData = new ArrayList<Point2D.Double>();
			for(int j=0; j<dataValue[analysisNumber].length; ++j) {
				testData.add(new Point2D.Double(j, dataValue[analysisNumber][j][i]));
			}
			gp.add_graph(testData);
			setTitle(title_name + " - " + data.getTitleName(analysisNumber));
		}
		analysisNumber = ++analysisNumber >= dataValue.length ? 0 : analysisNumber;

		normalCheck = getTitle().contains("정상");
		if(normalCheck)
			JOptionPane.showMessageDialog(null, "정상 데이터", "결과", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(null, "오류 데이터", "결과", JOptionPane.ERROR_MESSAGE);
	}

	public void submit() {		// 전송 부분
		// GraphDisplayPanel 저장
		// graph_panel_data.txt파일을 전송하면 됩니다.
		// gsl = new GraphSaveLoad<GraphDisplayPanel>();
		// gsl.save((GraphDisplayPanel) graphDisplayPanel, false);

		String path = "C:/Users/data/";
		File dirFile = new File(path);
		File []fileList = dirFile.listFiles();

		BufferedInputStream bis = null;
		Socket sock = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;

		try {
			sock = new Socket(IP, 5000);

			dos = new DataOutputStream(sock.getOutputStream());
			dis = new DataInputStream(sock.getInputStream());

			dos.writeInt(fileList.length);

			int readSize = 0;

			for(int i=0; i<fileList.length; i++) {
				System.out.println("Start File Trans " + i);

				long transferSize = fileList[i].length();
				int bufSize = 1024;
				int totalReadCount = (int)transferSize / bufSize;
				int lastReadSize = (int)transferSize % bufSize;
				dos.writeUTF(fileList[i].getName());
				dos.writeLong(transferSize);

				bis = new BufferedInputStream(new FileInputStream(fileList[i]));
				byte[] buffer = new byte[bufSize];

				for(int k=0; k<totalReadCount; k++) {
					if((readSize = bis.read(buffer, 0, bufSize)) != -1) {
						dos.write(buffer, 0, readSize);
					}
				}

				if(lastReadSize > 0) {
					System.out.println("lastReadSize: " + lastReadSize);

					if((readSize = bis.read(buffer, 0, lastReadSize))!= -1) {
						System.out.println("readSize: " + readSize);
						dos.write(buffer, 0, readSize);
					}
				}
				long completeSize = dis.readLong();
				System.out.println(dis.readUTF() + " total: " + completeSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bis != null) bis.close();
				if(dos != null) dos.close();
				if(dis != null) dis.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	class ButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(loadButton)) {			// load 처리
				int result;
				result = JOptionPane.showConfirmDialog(null, "데이터를 읽어오시겠습니까?", "확인 메세지", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION) {
					load();
					setButton();
				}
			}
			else if(e.getSource().equals(analysisButton)) {	// analysis 처리
				int result;
				result = JOptionPane.showConfirmDialog(null, "데이터를 분석하시겠습니까?", "확인 메세지", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION) {
					analysis();
				}
				
			}
			else if(e.getSource().equals(submitButton)) {	// submit 처리
				int result;
				result = JOptionPane.showConfirmDialog(null, "데이터를 제출하시겠습니까?", "확인 메세지", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION) {
					submit();
				}
			}
			else if(e.getSource().equals(exitButton))	{	// exit 처리
				int result;
				result = JOptionPane.showConfirmDialog(null, "프로그램을 종료하시겠습니까?", "확인 메세지", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		}

		public void setButton() {
			if(loadSuccess) {
				analysisButton.setEnabled(true);
				submitButton.setEnabled(true);
			}
			else {
				analysisButton.setEnabled(false);
				submitButton.setEnabled(false);
			}
		}
	}
}
