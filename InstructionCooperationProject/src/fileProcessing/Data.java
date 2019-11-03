package fileProcessing;

import java.io.File;
import java.util.Scanner;

public class Data {
	
	// private static final String file_path = "data/";
	private static final String file_path = "C:/Users/data/";
	private static final String[] file_name = {"error_sample.csv", "error_insert.csv", "error_moter.csv", "normal_CRP.csv",
												"normal_Cort.csv", "normal_DIM.csv", "normal_P4.csv", "normal_T4.csv"};
	private static final String[] title_name = {"샘플분주위치오류", "삽입오류", "모터동작 오류", "정상_CRP",
												"정상_Cort", "정상_DIM", "정상_P4", "정상_T4"};
	private static final int[] scanStartRow = {7, 7, 7, 6, 6, 6, 6, 6};
	private static final int[] scanEndRow = {156, 156, 156, 155, 155, 155, 155, 155};
	private static final String[] scanStartCol = {"A", "A", "A", "A", "A", "A", "A", "A"};
	private static final String[] scanEndCol = {"X", "K", "A", "IJ", "AP", "L", "DZ", "BD"};
	
	private int dataCount;
	private double[][][] data;
	
	public Data() throws Exception {
		dataCount = file_name.length;
		data = new double[dataCount][][];
		for(int i=0; i<dataCount; ++i)
			data[i] = new double[scanEndRow[i] - scanStartRow[i] + 1][alphabetToNumber(scanEndCol[i]) - alphabetToNumber(scanStartCol[i]) + 1];
		read_data();
	}
	
	public void read_data() throws Exception {
		for(int i=0; i<file_name.length; ++i) {
			Scanner input = new Scanner(new File(file_path + file_name[i]));
			int rowIndex = 1;			// 1로 설정할 것
			while(input.hasNextLine()) {
				String line = input.nextLine();
				if(rowIndex >= scanStartRow[i] && rowIndex <= scanEndRow[i]) {
					String[] words = line.split(",");
					for(int j=0; j<words.length; ++j) {
						if(isNumber(words[j])) 
							data[i][rowIndex - scanStartRow[i]][j] = Double.parseDouble(words[j]);
						else		// Value가 존재하지 않는 값 처리
							data[i][rowIndex - scanStartRow[i]][j] = Double.NaN;
					}
				}
				++rowIndex;
			}
			input.close();
		}
	}
	
	public boolean isNumber(String word) {
		try {
			Double.parseDouble(word);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public int alphabetToNumber(String word) {
		if(word == null || word.equals("") || word.equals(" "))
			return 0;
		final int alphabetCount = 26;
		int wordNumber = 0;
		for(int i=0; i<word.length(); ++i)
			wordNumber += (word.charAt(i) - 'A' + 1) * Math.pow(alphabetCount, word.length()-i-1);
		return wordNumber;
	}
	
	public double[][][] getData() {
		return data;
	}
	
	public String getTitleName(int index) {
		return title_name[index];
	}
}
