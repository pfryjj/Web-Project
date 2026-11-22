package kr.ac.kopo.member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.kopo.exception.DataAccessException;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.ConnectionFactory;
import kr.ac.kopo.util.JDBCClose;

/**
 * ORACLE DB 에서 t_member 테이블 CRUD
 * 모든 SQL 은 PreparedStatement(? 바인딩)로 작성 → SQL Injection 방어 (Sec-002)
 */
public class MemberDAOImpl implements MemberDAO {

	private static final String SELECT_COLUMNS =
			"select id, password, name, email, phone, "
		  + "       to_char(birth_date, 'YYYY-MM-DD') as birth_date, "
		  + "       address, role, to_char(reg_date, 'YYYY-MM-DD') as reg_date ";

	@Override
	public boolean insert(MemberVO member) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "insert into t_member(id, password, name, email, phone, birth_date, address, role) ";
			       sql += " values(?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD'), ?, 'USER') ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPassword());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getEmail());
			pstmt.setString(5, member.getPhone());
			pstmt.setString(6, member.getBirthDate());
			pstmt.setString(7, member.getAddress());

			pstmt.executeUpdate();
			return true;

		} catch (SQLIntegrityConstraintViolationException e) {
			// ORA-00001 : 동시에 같은 아이디로 가입한 경우
			return false;
		} catch (SQLException e) {
			throw new DataAccessException("회원 등록 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt);
		}
	}

	@Override
	public MemberVO findById(String id) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = SELECT_COLUMNS;
			       sql += "  from t_member ";
			       sql += " where id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				MemberVO member = toMember(rs);
				member.setPassword(rs.getString("password"));
				return member;
			}
			return null;

		} catch (SQLException e) {
			throw new DataAccessException("회원 조회 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}
	}

	@Override
	public boolean existsById(String id) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql = "select count(*) from t_member where id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;

		} catch (SQLException e) {
			throw new DataAccessException("아이디 중복 확인 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}
	}

	@Override
	public int update(MemberVO member, String encodedPassword) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "update t_member ";
			       sql += "   set name = ?, email = ?, phone = ?, address = ? ";
			if (encodedPassword != null) {
			       sql += "     , password = ? ";
			}
			       sql += " where id = ? ";

			pstmt = conn.prepareStatement(sql);
			int idx = 1;
			pstmt.setString(idx++, member.getName());
			pstmt.setString(idx++, member.getEmail());
			pstmt.setString(idx++, member.getPhone());
			pstmt.setString(idx++, member.getAddress());
			if (encodedPassword != null) {
				pstmt.setString(idx++, encodedPassword);
			}
			pstmt.setString(idx++, member.getId());

			return pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("회원 정보 수정 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt);
		}
	}

	/**
	 * 트랜잭션 : 구독 삭제 + 회원 삭제가 "둘 다 성공"하거나 "둘 다 취소"되어야 한다.
	 * (중간에 오류가 나서 구독만 지워지고 회원은 남는 상황 방지)
	 */
	@Override
	public int deleteWithSubscriptions(String id) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = new ConnectionFactory().getConnection();
			conn.setAutoCommit(false);		// 트랜잭션 시작

			pstmt = conn.prepareStatement("delete from t_subscription where member_id = ? ");
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			pstmt.close();

			pstmt = conn.prepareStatement("delete from t_member where id = ? ");
			pstmt.setString(1, id);
			int cnt = pstmt.executeUpdate();

			conn.commit();					// 모두 성공 → 확정
			return cnt;

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();		// 하나라도 실패 → 전부 취소
				} catch (SQLException re) {
					re.printStackTrace();
				}
			}
			throw new DataAccessException("회원 삭제 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt);
		}
	}

	@Override
	public List<MemberVO> findAll(String searchType, String keyword) {

		List<MemberVO> memberList = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 검색 컬럼은 사용자 입력을 그대로 SQL 에 넣지 않고 "허용 목록"에서만 고른다
		String column = null;
		if (keyword != null && !keyword.isEmpty()) {
			switch (searchType) {
				case "name":  column = "m.name";  break;
				case "email": column = "m.email"; break;
				default:      column = "m.id";
			}
		}

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "select m.id, m.name, m.email, m.phone, ";
			       sql += "       to_char(m.birth_date, 'YYYY-MM-DD') as birth_date, ";
			       sql += "       m.address, m.role, to_char(m.reg_date, 'YYYY-MM-DD') as reg_date, ";
			       sql += "       (select count(*) from t_subscription s where s.member_id = m.id) as sub_count ";
			       sql += "  from t_member m ";
			if (column != null) {
			       sql += " where lower(" + column + ") like '%' || lower(?) || '%' escape '\\' ";
			}
			       sql += " order by m.reg_date desc, m.id ";

			pstmt = conn.prepareStatement(sql);
			if (column != null) {
				pstmt.setString(1, escapeLike(keyword));
			}

			rs = pstmt.executeQuery();
			while (rs.next()) {
				MemberVO member = toMember(rs);
				member.setSubCount(rs.getInt("sub_count"));
				memberList.add(member);
			}

		} catch (SQLException e) {
			throw new DataAccessException("회원 목록 조회 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}

		return memberList;
	}

	/** LIKE 검색에서 % _ 를 일반 문자로 취급 (예: "100%" 검색) */
	static String escapeLike(String keyword) {
		return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}

	private MemberVO toMember(ResultSet rs) throws SQLException {
		MemberVO member = new MemberVO();
		member.setId(rs.getString("id"));
		member.setName(rs.getString("name"));
		member.setEmail(rs.getString("email"));
		member.setPhone(rs.getString("phone"));
		member.setBirthDate(rs.getString("birth_date"));
		member.setAddress(rs.getString("address"));
		member.setRole(rs.getString("role"));
		member.setRegDate(rs.getString("reg_date"));
		return member;
	}
}
