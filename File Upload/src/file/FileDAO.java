package file;

import java.sql.*;

public class FileDAO {

	private Connection con;
	
	public FileDAO() {
		String url="jdbc:oracle:thin:@localhost:1521:orcl"; /* 11g express edition은 orcl 대신 XE를 입력한다. */
		String userid="USERID";
		String pwd="USERPW";
		try { /* 드라이버를 찾는 과정 */
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println ("드라이버 로드 성공");
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		try { /* 데이터베이스를 연결하는 과정 */
			System.out.println ("데이터베이스 연결 준비 ...");
			con=DriverManager.getConnection(url, userid, pwd);
			System.out.println ("데이터베이스 연결 성공");
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
