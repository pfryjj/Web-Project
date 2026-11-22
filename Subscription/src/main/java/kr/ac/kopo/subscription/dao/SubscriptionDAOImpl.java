package kr.ac.kopo.subscription.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import kr.ac.kopo.exception.DataAccessException;
import kr.ac.kopo.subscription.vo.CategoryStatVO;
import kr.ac.kopo.subscription.vo.SubSearchVO;
import kr.ac.kopo.subscription.vo.SubSummaryVO;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.ConnectionFactory;
import kr.ac.kopo.util.JDBCClose;

/**
 * ORACLE DB 에서 t_subscription 테이블 CRUD
 */
public class SubscriptionDAOImpl implements SubscriptionDAO {

	private static final String SELECT_COLUMNS =
			"select sno, member_id, service_name, category, amount, "
		  + "       to_char(start_date, 'YYYY-MM-DD')    as start_date, "
		  + "       to_char(next_pay_date, 'YYYY-MM-DD') as next_pay_date, "
		  + "       status, rating, memo, "
		  + "       to_char(reg_date, 'YYYY-MM-DD')      as reg_date, "
		  + "       trunc(next_pay_date) - trunc(sysdate) as days_left ";

	@Override
	public void insert(SubscriptionVO sub) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "insert into t_subscription(sno, member_id, service_name, category, amount, ";
			       sql += "                           start_date, next_pay_date, status, rating, memo) ";
			       sql += " values(seq_t_subscription_sno.nextval, ?, ?, ?, ?, ";
			       sql += "        to_date(?, 'YYYY-MM-DD'), to_date(?, 'YYYY-MM-DD'), ?, ?, ?) ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sub.getMemberId());
			pstmt.setString(2, sub.getServiceName());
			pstmt.setString(3, sub.getCategory());
			pstmt.setInt(4, sub.getAmount());
			pstmt.setString(5, emptyToNull(sub.getStartDate()));
			pstmt.setString(6, sub.getNextPayDate());
			pstmt.setString(7, sub.getStatus());
			setRating(pstmt, 8, sub.getRating());
			pstmt.setString(9, emptyToNull(sub.getMemo()));

			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("구독 등록 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt);
		}
	}

	/**
	 * 검색 조건에 따라 where 절을 동적으로 만든다.
	 * 값은 모두 ? 로 바인딩하고, 정렬 기준은 허용된 두 가지 중에서만 고른다.
	 */
	@Override
	public List<SubscriptionVO> findByCondition(SubSearchVO search) {

		List<SubscriptionVO> list = new ArrayList<>();
		List<Object> params = new ArrayList<>();

		// 주의 : select 절의 next_pay_date 는 to_char 문자열 별칭이므로
		//       정렬은 테이블 컬럼(s.next_pay_date, DATE 타입)으로 명확히 지정한다.
		String sql  = SELECT_COLUMNS;
		       sql += "  from t_subscription s ";
		       sql += " where member_id = ? ";
		params.add(search.getMemberId());

		if (!search.getKeyword().isEmpty()) {
			sql += "   and lower(service_name) like '%' || lower(?) || '%' escape '\\' ";
			params.add(escapeLike(search.getKeyword()));
		}
		if (!search.getCategory().isEmpty()) {
			sql += "   and category = ? ";
			params.add(search.getCategory());
		}
		if (!search.getStatus().isEmpty()) {
			sql += "   and status = ? ";
			params.add(search.getStatus());
		}
		if (search.getRating() != null) {
			sql += "   and rating = ? ";
			params.add(search.getRating());
		}

		if (SubSearchVO.SORT_RECENT.equals(search.getSort())) {
			sql += " order by s.sno desc ";
		} else {
			// 임박순 : 해지 완료는 맨 뒤로, 나머지는 다음 결제일이 빠른 순
			sql += " order by case when s.status = 'CANCELED' then 1 else 0 end, s.next_pay_date, s.sno desc ";
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionFactory().getConnection();
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(toSubscription(rs));
			}

		} catch (SQLException e) {
			throw new DataAccessException("구독 목록 조회 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}

		return list;
	}

	@Override
	public SubscriptionVO findBySno(int sno, String memberId) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = SELECT_COLUMNS;
			       sql += "  from t_subscription ";
			       sql += " where sno = ? and member_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sno);
			pstmt.setString(2, memberId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				return toSubscription(rs);
			}
			return null;

		} catch (SQLException e) {
			throw new DataAccessException("구독 상세 조회 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}
	}

	@Override
	public int update(SubscriptionVO sub) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "update t_subscription ";
			       sql += "   set service_name = ?, category = ?, amount = ?, ";
			       sql += "       start_date = to_date(?, 'YYYY-MM-DD'), ";
			       sql += "       next_pay_date = to_date(?, 'YYYY-MM-DD'), ";
			       sql += "       rating = ?, memo = ? ";
			       sql += " where sno = ? and member_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, sub.getServiceName());
			pstmt.setString(2, sub.getCategory());
			pstmt.setInt(3, sub.getAmount());
			pstmt.setString(4, emptyToNull(sub.getStartDate()));
			pstmt.setString(5, sub.getNextPayDate());
			setRating(pstmt, 6, sub.getRating());
			pstmt.setString(7, emptyToNull(sub.getMemo()));
			pstmt.setInt(8, sub.getSno());
			pstmt.setString(9, sub.getMemberId());

			return pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("구독 수정 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt);
		}
	}

	@Override
	public int delete(int sno, String memberId) {
		return executeUpdate("delete from t_subscription where sno = ? and member_id = ? ",
				"구독 삭제 실패", sno, memberId);
	}

	@Override
	public int updateStatus(int sno, String memberId, String status) {
		return executeUpdate("update t_subscription set status = ? where sno = ? and member_id = ? ",
				"구독 상태 변경 실패", status, sno, memberId);
	}

	@Override
	public int renewNextPayDate(int sno, String memberId) {
		// add_months : 1월 31일 + 1개월 = 2월 28(29)일 처럼 말일을 자동 보정
		return executeUpdate("update t_subscription set next_pay_date = add_months(next_pay_date, 1) "
				+ " where sno = ? and member_id = ? and status = 'ACTIVE' ",
				"결제일 갱신 실패", sno, memberId);
	}

	@Override
	public SubSummaryVO getSummary(String memberId) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "select count(*) as total_cnt, ";
			       sql += "       nvl(sum(case when status = 'ACTIVE' then 1 else 0 end), 0) as active_cnt, ";
			       sql += "       nvl(sum(case when status = 'CANCEL_PLANNED' then 1 else 0 end), 0) as planned_cnt, ";
			       sql += "       nvl(sum(case when status = 'CANCELED' then 1 else 0 end), 0) as canceled_cnt, ";
			       sql += "       nvl(sum(case when status = 'ACTIVE' then amount else 0 end), 0) as monthly_total, ";
			       sql += "       nvl(sum(case when status = 'ACTIVE' and next_pay_date >= trunc(sysdate) ";
			       sql += "                     and next_pay_date < trunc(sysdate) + 8 then 1 else 0 end), 0) as upcoming_cnt, ";
			       sql += "       nvl(sum(case when status = 'ACTIVE' and next_pay_date < trunc(sysdate) ";
			       sql += "                    then 1 else 0 end), 0) as overdue_cnt, ";
			       sql += "       nvl(sum(case when status = 'ACTIVE' and next_pay_date >= trunc(sysdate) ";
			       sql += "                     and next_pay_date < add_months(trunc(sysdate, 'MM'), 1) ";
			       sql += "                    then amount else 0 end), 0) as remaining_month ";
			       sql += "  from t_subscription ";
			       sql += " where member_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memberId);

			rs = pstmt.executeQuery();
			SubSummaryVO summary = new SubSummaryVO();
			if (rs.next()) {
				summary.setTotalCount(rs.getInt("total_cnt"));
				summary.setActiveCount(rs.getInt("active_cnt"));
				summary.setCancelPlannedCount(rs.getInt("planned_cnt"));
				summary.setCanceledCount(rs.getInt("canceled_cnt"));
				summary.setMonthlyTotal(rs.getLong("monthly_total"));
				summary.setUpcomingCount(rs.getInt("upcoming_cnt"));
				summary.setOverdueCount(rs.getInt("overdue_cnt"));
				summary.setRemainingThisMonth(rs.getLong("remaining_month"));
			}
			return summary;

		} catch (SQLException e) {
			throw new DataAccessException("구독 요약 조회 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}
	}

	@Override
	public List<CategoryStatVO> sumByCategory(String memberId) {

		List<CategoryStatVO> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionFactory().getConnection();

			String sql  = "select category, sum(amount) as total, count(*) as cnt ";
			       sql += "  from t_subscription ";
			       sql += " where member_id = ? and status = 'ACTIVE' ";
			       sql += " group by category ";
			       sql += " order by total desc ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memberId);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				CategoryStatVO stat = new CategoryStatVO();
				stat.setCategory(rs.getString("category"));
				stat.setTotal(rs.getLong("total"));
				stat.setCount(rs.getInt("cnt"));
				list.add(stat);
			}

		} catch (SQLException e) {
			throw new DataAccessException("카테고리별 지출 조회 실패", e);
		} finally {
			JDBCClose.close(conn, pstmt, rs);
		}

		return list;
	}

	// ------------------------------------------------------------------

	/** 단순 update/delete 공통 처리 */
	private int executeUpdate(String sql, String errorMessage, Object... params) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = new ConnectionFactory().getConnection();
			pstmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
			return pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException(errorMessage, e);
		} finally {
			JDBCClose.close(conn, pstmt);
		}
	}

	private SubscriptionVO toSubscription(ResultSet rs) throws SQLException {
		SubscriptionVO sub = new SubscriptionVO();
		sub.setSno(rs.getInt("sno"));
		sub.setMemberId(rs.getString("member_id"));
		sub.setServiceName(rs.getString("service_name"));
		sub.setCategory(rs.getString("category"));
		sub.setAmount(rs.getInt("amount"));
		sub.setStartDate(rs.getString("start_date"));
		sub.setNextPayDate(rs.getString("next_pay_date"));
		sub.setStatus(rs.getString("status"));
		int rating = rs.getInt("rating");
		sub.setRating(rs.wasNull() ? null : rating);	// NULL 컬럼은 getInt 가 0 을 돌려주므로 wasNull 확인
		sub.setMemo(rs.getString("memo"));
		sub.setRegDate(rs.getString("reg_date"));
		sub.setDaysLeft(rs.getInt("days_left"));
		return sub;
	}

	private void setRating(PreparedStatement pstmt, int index, Integer rating) throws SQLException {
		if (rating == null) {
			pstmt.setNull(index, Types.INTEGER);
		} else {
			pstmt.setInt(index, rating);
		}
	}

	private String emptyToNull(String value) {
		return (value == null || value.isEmpty()) ? null : value;
	}

	private static String escapeLike(String keyword) {
		return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}
}
