package kr.ac.kopo.subscription.vo;

/**
 * 내 구독 요약 (목록 화면 상단 카드)
 *  - 월 고정 지출액 : '구독 중' 상태만 합산 (기능명세서 USER-FUNC-07)
 *  - 결제 일정      : 7일 이내 결제 예정, 결제일 지남, 이번 달 남은 결제 예정액 (요구사항 Sub-009)
 */
public class SubSummaryVO {

	private int  totalCount;			// 등록된 전체 구독 수
	private int  activeCount;
	private int  cancelPlannedCount;
	private int  canceledCount;
	private long monthlyTotal;			// 월 고정 지출액
	private int  upcomingCount;			// 오늘 ~ 7일 이내 결제 예정
	private int  overdueCount;			// 다음 결제일이 지났는데 갱신하지 않은 구독
	private long remainingThisMonth;	// 오늘 ~ 이번 달 말일 사이 결제 예정 금액

	/** 연간 환산 지출액 */
	public long getYearlyTotal() {
		return monthlyTotal * 12;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public int getCancelPlannedCount() {
		return cancelPlannedCount;
	}

	public void setCancelPlannedCount(int cancelPlannedCount) {
		this.cancelPlannedCount = cancelPlannedCount;
	}

	public int getCanceledCount() {
		return canceledCount;
	}

	public void setCanceledCount(int canceledCount) {
		this.canceledCount = canceledCount;
	}

	public long getMonthlyTotal() {
		return monthlyTotal;
	}

	public void setMonthlyTotal(long monthlyTotal) {
		this.monthlyTotal = monthlyTotal;
	}

	public int getUpcomingCount() {
		return upcomingCount;
	}

	public void setUpcomingCount(int upcomingCount) {
		this.upcomingCount = upcomingCount;
	}

	public int getOverdueCount() {
		return overdueCount;
	}

	public void setOverdueCount(int overdueCount) {
		this.overdueCount = overdueCount;
	}

	public long getRemainingThisMonth() {
		return remainingThisMonth;
	}

	public void setRemainingThisMonth(long remainingThisMonth) {
		this.remainingThisMonth = remainingThisMonth;
	}
}
