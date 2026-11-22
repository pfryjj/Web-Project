package kr.ac.kopo.validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kr.ac.kopo.subscription.vo.SubscriptionVO;

/**
 * 구독 입력값 서버 측 유효성 검증 (USER-FUNC-06, 09)
 */
public class SubscriptionValidator {

	public static final int MAX_AMOUNT = 10_000_000;

	/**
	 * @param checkStatus 등록 화면에서는 상태도 입력받으므로 true, 수정 화면은 false
	 *                    (상태 변경은 USER-FUNC-13 에서 별도로 처리)
	 */
	public static List<String> validate(SubscriptionVO sub, boolean checkStatus) {
		List<String> errors = new ArrayList<>();

		if (sub.getServiceName() == null || sub.getServiceName().isEmpty()) {
			errors.add("서비스명을 입력해 주세요.");
		} else if (sub.getServiceName().length() > 50) {
			errors.add("서비스명은 50자 이하로 입력해 주세요.");
		}

		if (!SubscriptionVO.CATEGORIES.contains(sub.getCategory())) {
			errors.add("카테고리를 선택해 주세요.");
		}

		if (sub.getAmount() == null) {
			errors.add("월 결제 금액을 숫자로 입력해 주세요.");
		} else if (sub.getAmount() < 0 || sub.getAmount() > MAX_AMOUNT) {
			errors.add("월 결제 금액은 0원 이상 10,000,000원 이하로 입력해 주세요.");
		}

		LocalDate next = null;
		if (sub.getNextPayDate() == null || sub.getNextPayDate().isEmpty()) {
			errors.add("다음 결제일을 입력해 주세요.");
		} else {
			next = MemberValidator.parseDate(sub.getNextPayDate());
			if (next == null) {
				errors.add("다음 결제일이 올바른 날짜가 아닙니다.");
			}
		}

		if (sub.getStartDate() != null && !sub.getStartDate().isEmpty()) {
			LocalDate start = MemberValidator.parseDate(sub.getStartDate());
			if (start == null) {
				errors.add("구독 시작일이 올바른 날짜가 아닙니다.");
			} else if (next != null && next.isBefore(start)) {
				errors.add("다음 결제일은 구독 시작일과 같거나 이후여야 합니다.");
			}
		}

		if (checkStatus && !SubscriptionVO.STATUS_MAP.containsKey(sub.getStatus())) {
			errors.add("구독 상태를 선택해 주세요.");
		}

		if (sub.getRating() != null && (sub.getRating() < 1 || sub.getRating() > 5)) {
			errors.add("별점은 1~5점 중에서 선택해 주세요.");
		}

		if (sub.getMemo() != null && sub.getMemo().length() > 500) {
			errors.add("메모는 500자 이하로 입력해 주세요.");
		}

		return errors;
	}
}
