package kr.ac.kopo.subscription.service;

import java.util.List;

import kr.ac.kopo.subscription.vo.CategoryStatVO;
import kr.ac.kopo.subscription.vo.SubSearchVO;
import kr.ac.kopo.subscription.vo.SubSummaryVO;
import kr.ac.kopo.subscription.vo.SubscriptionVO;

public interface SubscriptionService {

	/** 구독 서비스 등록 (USER-FUNC-06) */
	void register(SubscriptionVO sub);

	/** 내 구독 목록 + 검색/필터 (USER-FUNC-07, 11, 12) */
	List<SubscriptionVO> getMySubscriptions(SubSearchVO search);

	/** 구독 상세 - 본인 것이 아니면 null (USER-FUNC-08) */
	SubscriptionVO getMySubscription(int sno, String memberId);

	/** 구독 정보 수정 - 본인 것이 아니면 false (USER-FUNC-09) */
	boolean modify(SubscriptionVO sub);

	/** 구독 삭제 - 권한 없음/이미 삭제됨이면 false (USER-FUNC-10) */
	boolean remove(int sno, String memberId);

	/** 구독 상태 변경 - 본인 것이 아니거나 잘못된 상태값이면 false (USER-FUNC-13) */
	boolean changeStatus(int sno, String memberId, String status);

	/** 다음 결제일 +1개월 (결제 일정 관리, 신규 제안 기능) */
	boolean renew(int sno, String memberId);

	/** 월 지출 요약 */
	SubSummaryVO getSummary(String memberId);

	/** 카테고리별 월 지출 + 비율 */
	List<CategoryStatVO> getCategoryStats(String memberId, long monthlyTotal);
}
