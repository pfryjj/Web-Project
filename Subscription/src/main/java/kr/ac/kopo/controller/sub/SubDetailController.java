package kr.ac.kopo.controller.sub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.service.SubscriptionService;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 구독 상세 조회 (USER-FUNC-08, S-03)
 * 구독 번호(sno) + 세션 회원 아이디로 조회 → 타인의 구독 번호를 넣으면 조회되지 않음
 */
public class SubDetailController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int sno = SubRequestHelper.sno(request);
		String memberId = WebUtil.loginUser(request).getId();

		SubscriptionService subscriptionService = new SubscriptionServiceImpl();
		SubscriptionVO sub = subscriptionService.getMySubscription(sno, memberId);

		if (sub == null) {
			// 예외 처리 : 존재하지 않는 번호 / 타인의 구독 → 권한 오류 안내 후 목록으로
			WebUtil.flash(request, "error", "권한이 없거나 존재하지 않는 구독입니다.");
			return "redirect:/sub/list.do";
		}

		request.setAttribute("sub", sub);
		return "/WEB-INF/jsp/sub/detail.jsp";
	}
}
