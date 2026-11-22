package kr.ac.kopo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Oracle DB 연결 (Mission-WEB-MVC 의 ConnectionFactory 와 같은 접속 정보)
 *
 * 변경점 : 접속 실패 시 null 을 반환하지 않고 SQLException 을 던진다.
 *         (null 이 반환되면 DAO 에서 NullPointerException 이 나서 원인 찾기가 어려움)
 */
public class ConnectionFactory {

	private static final String DRIVER   = "oracle.jdbc.OracleDriver";
	private static final String URL      = "jdbc:oracle:thin:@localhost:1521/FREE";
	private static final String USER     = "hr";
	private static final String PASSWORD = "hr";

	public Connection getConnection() throws SQLException {
		try {
			// 1단계 : 드라이버 로딩
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			throw new SQLException("Oracle JDBC 드라이버(WEB-INF/lib/ojdbc11.jar)를 찾을 수 없습니다.", e);
		}
		// 2단계 : DB 접속 후 Connection 객체 얻기
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
