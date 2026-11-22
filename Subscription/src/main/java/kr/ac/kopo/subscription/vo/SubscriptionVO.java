package kr.ac.kopo.subscription.vo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * t_subscription 테이블 1행
 */
public class SubscriptionVO {

	// ---------- 코드 정의 (AppInitListener 가 application 영역에 등록 → JSP 에서 사용) ----------

	/** 카테고리 (요구사항 Sub-006 : 영상, 음악, 쇼핑, IT 등) */
	public static final List<String> CATEGORIES = List.of("영상", "음악", "쇼핑", "IT", "생활", "기타");

	public static final String STATUS_ACTIVE         = "ACTIVE";
	public static final String STATUS_CANCEL_PLANNED = "CANCEL_PLANNED";
	public static final String STATUS_CANCELED       = "CANCELED";

	/** 구독 상태 코드 → 화면 표시명 (요구사항 Sub-007, Sub-008) */
	public static final Map<String, String> STATUS_MAP;
	static {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(STATUS_ACTIVE, "구독 중");
		map.put(STATUS_CANCEL_PLANNED, "해지 예정");
		map.put(STATUS_CANCELED, "해지 완료");
		STATUS_MAP = Collections.unmodifiableMap(map);
	}

	// ---------- 컬럼 ----------

	private int     sno;
	private String  memberId;
	private String  serviceName;
	private String  category;
	private Integer amount;			// 월 결제 금액(원). 입력 오류 시 null 이 될 수 있어 Integer
	private String  startDate;		// yyyy-MM-dd (선택)
	private String  nextPayDate;	// yyyy-MM-dd (필수)
	private String  status;
	private Integer rating;			// 1~5, 미평가는 null
	private String  memo;
	private String  regDate;
	private int     daysLeft;		// 다음 결제일까지 남은 일수 (SQL 에서 계산, 음수면 지남)

	public SubscriptionVO() {
	}

	/** 화면 표시용 상태명 : ${ sub.statusLabel } */
	public String getStatusLabel() {
		String label = STATUS_MAP.get(status);
		return label == null ? status : label;
	}

	/** CSS 클래스용 카테고리 코드 : ${ sub.categoryCode } */
	public String getCategoryCode() {
		return categoryCodeOf(category);
	}

	/** 카테고리명 → CSS 클래스용 영문 코드 (cat-video, cat-music ...) */
	public static String categoryCodeOf(String category) {
		if (category == null) {
			return "etc";
		}
		switch (category) {
			case "영상": return "video";
			case "음악": return "music";
			case "쇼핑": return "shopping";
			case "IT":   return "it";
			case "생활": return "life";
			default:     return "etc";
		}
	}

	public boolean isActive() {
		return STATUS_ACTIVE.equals(status);
	}

	public int getSno() {
		return sno;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getNextPayDate() {
		return nextPayDate;
	}

	public void setNextPayDate(String nextPayDate) {
		this.nextPayDate = nextPayDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public int getDaysLeft() {
		return daysLeft;
	}

	public void setDaysLeft(int daysLeft) {
		this.daysLeft = daysLeft;
	}

	@Override
	public String toString() {
		return "SubscriptionVO [sno=" + sno + ", memberId=" + memberId + ", serviceName=" + serviceName
				+ ", category=" + category + ", amount=" + amount + ", startDate=" + startDate
				+ ", nextPayDate=" + nextPayDate + ", status=" + status + ", rating=" + rating + "]";
	}
}
