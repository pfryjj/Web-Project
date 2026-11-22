package kr.ac.kopo.subscription.vo;

/**
 * 내 구독 목록 검색/필터 조건 (USER-FUNC-07, 11, 12)
 * 값이 비어 있으면 해당 조건은 적용하지 않는다.
 */
public class SubSearchVO {

	public static final String SORT_PAY_DATE = "payDate";	// 다음 결제일 임박순 (기본)
	public static final String SORT_RECENT   = "recent";	// 최근 등록순

	private String  memberId;
	private String  keyword  = "";		// 서비스명 검색어
	private String  category = "";
	private String  status   = "";
	private Integer rating;				// 1~5
	private String  sort     = SORT_PAY_DATE;

	/** 검색/필터가 하나라도 적용되었는지 → "검색 결과 없음" 문구 구분용 */
	public boolean isFiltered() {
		return !keyword.isEmpty() || !category.isEmpty() || !status.isEmpty() || rating != null;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword == null ? "" : keyword;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category == null ? "" : category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? "" : status;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = SORT_RECENT.equals(sort) ? SORT_RECENT : SORT_PAY_DATE;
	}
}
