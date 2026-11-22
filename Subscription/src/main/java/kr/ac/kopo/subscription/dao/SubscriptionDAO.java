package kr.ac.kopo.subscription.dao;

import java.util.List;

import kr.ac.kopo.subscription.vo.CategoryStatVO;
import kr.ac.kopo.subscription.vo.SubSearchVO;
import kr.ac.kopo.subscription.vo.SubSummaryVO;
import kr.ac.kopo.subscription.vo.SubscriptionVO;

/**
 * t_subscription CRUD
 *
 * 데이터 소유권(Data-003) : 조회·수정·삭제 SQL 에 항상 "and member_id = ?" 조건을 붙인다.
 * → 다른 회원의 구독 번호(sno)를 주소창에 넣어도 0건 처리된다.
 */
public interface SubscriptionDAO {

	void insert(SubscriptionVO sub);

	List<SubscriptionVO> findByCondition(SubSearchVO search);

	/** 본인 구독 1건 - 없거나 타인 소유면 null */
	SubscriptionVO findBySno(int sno, String memberId);

	/** 서비스명·카테고리·금액·시작일·다음결제일·별점·메모 수정. 수정된 행 수 반환 */
	int update(SubscriptionVO sub);

	int delete(int sno, String memberId);

	int updateStatus(int sno, String memberId, String status);

	/** '구독 중'인 구독의 다음 결제일을 1개월 뒤로 갱신 */
	int renewNextPayDate(int sno, String memberId);

	SubSummaryVO getSummary(String memberId);

	List<CategoryStatVO> sumByCategory(String memberId);
}
