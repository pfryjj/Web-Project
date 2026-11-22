package kr.ac.kopo.controller.sub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.util.WebUtil;

/**
 * 구독 상태 변경 (USER-FUNC-13) - 목록(S-02)·상세(S-03) 화면 공용
 * 구독 중 / 해지 예정 / 해지 완료 → 월 지출액은 '구독 중'만 합산되므로 자동 반영됨
 */
public class SubStatusController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int sno = SubRequestHelper.sno(request);
		String status = WebUtil.param(request, "status");
		String memberId = WebUtil.loginUser(request).getId();

		if (new SubscriptionServiceImpl().changeStatus(sno, memberId, status)) {
			WebUtil.flash(request, "success",
					"구독 상태가 '" + SubscriptionVO.STATUS_MAP.get(status) + "'(으)로 변경되었습니다.");
		} else {
			// 예외 처리 : 타인의 구독 / 잘못된 상태값 → 요청 차단
			WebUtil.flash(request, "error", "권한이 없거나 올바르지 않은 요청입니다.");
			return "redirect:/sub/list.do";
		}
		return SubRequestHelper.backTo(request, sno);
	}
}
