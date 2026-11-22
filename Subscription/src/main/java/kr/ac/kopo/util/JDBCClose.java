package kr.ac.kopo.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 5단계 : DB 자원 반납 (Mission-WEB-MVC 의 JDBCClose 에 ResultSet 버전 추가)
 */
public class JDBCClose {

	public static void close(Connection conn, Statement stmt) {
		close(conn, stmt, null);
	}

	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
