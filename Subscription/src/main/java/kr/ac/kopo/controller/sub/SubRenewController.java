package kr.ac.kopo.controller.sub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.service.SubscriptionService;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 결제 완료 처리 : 다음 결제일 +1개월 (요구사항 Sub-009 "결제 일정 관리")
 *
 * 기능명세서에 Sub-009 에 대응하는 기능이 없어서 추가한 기능 (불일치 보고서 참고).
 * 결제일이 지난 구독의 "다음 결제일"을 손으로 고치지 않고 버튼 한 번으로 다음 달로 넘긴다.
 */
public class SubRenewController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int sno = SubRequestHelper.sno(request);
		String memberId = WebUtil.loginUser(request).getId();

		SubscriptionService subscriptionService = new SubscriptionServiceImpl();
		if (subscriptionService.renew(sno, memberId)) {
			SubscriptionVO sub = subscriptionService.getMySubscription(sno, memberId);
			WebUtil.flash(request, "success", "결제 완료 처리되었습니다. 다음 결제일 : " + sub.getNextPayDate());
		} else {
			WebUtil.flash(request, "error", "'구독 중' 상태인 본인 구독만 결제일을 갱신할 수 있습니다.");
		}
		return SubRequestHelper.backTo(request, sno);
	}
}
