package kr.ac.kopo.controller.sub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.util.WebUtil;

/**
 * 구독 서비스 삭제 (USER-FUNC-10) - 상세 화면(S-03)의 삭제 버튼
 * 삭제 후 목록으로 이동하면 월 지출액이 다시 계산되어 표시된다.
 */
public class SubDeleteController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int sno = SubRequestHelper.sno(request);
		String memberId = WebUtil.loginUser(request).getId();

		if (new SubscriptionServiceImpl().remove(sno, memberId)) {
			WebUtil.flash(request, "success", "구독 내역이 삭제되었습니다.");
		} else {
			// 예외 처리 : 권한이 없거나 이미 삭제된 항목
			WebUtil.flash(request, "error", "삭제할 수 없습니다. 권한이 없거나 이미 삭제된 항목입니다.");
		}
		return "redirect:/sub/list.do";
	}
}
