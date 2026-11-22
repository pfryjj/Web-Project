package kr.ac.kopo.subscription.vo;

/**
 * 카테고리별 월 지출 (구독 중 상태 기준)
 */
public class CategoryStatVO {

	private String category;
	private long   total;
	private int    count;
	private int    percent;		// 월 고정 지출액 대비 비율(%) - Service 에서 계산

	/** CSS 클래스용 카테고리 코드 : ${ stat.categoryCode } */
	public String getCategoryCode() {
		return SubscriptionVO.categoryCodeOf(category);
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
}
