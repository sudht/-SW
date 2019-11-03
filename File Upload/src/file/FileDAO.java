package file;

import java.sql.*;

public class FileDAO {

	private Connection con;
	
	public FileDAO() {
		String url="jdbc:oracle:thin:@localhost:1521:orcl"; /* 11g express edition�� orcl ��� XE�� �Է��Ѵ�. */
		String userid="rmdrkdgh";
		String pwd="rkdgh5909";
		try { /* ����̹��� ã�� ���� */
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println ("����̹� �ε� ����");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		try { /* �����ͺ��̽��� �����ϴ� ���� */
			System.out.println ("�����ͺ��̽� ���� �غ� ...");
			con=DriverManager.getConnection(url, userid, pwd);
			System.out.println ("�����ͺ��̽� ���� ����");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int upload(String fileName, String fileRealName) {
		String SQL = "INSERT INTO FILET VALUES (?, ?)";
		try {
			PreparedStatement pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, fileName);
			pstmt.setString(2, fileRealName);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
