package kr.ac.kopo.controller.sub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.WebUtil;

/** 구독 수정 화면 (S-04) */
public class SubUpdateFormController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		int sno = SubRequestHelper.sno(request);
		String memberId = WebUtil.loginUser(request).getId();

		SubscriptionVO sub = new SubscriptionServiceImpl().getMySubscription(sno, memberId);
		if (sub == null) {
			WebUtil.flash(request, "error", "권한이 없습니다.");
			return "redirect:/sub/list.do";
		}

		request.setAttribute("sub", sub);
		request.setAttribute("mode", "update");
		return "/WEB-INF/jsp/sub/form.jsp";
	}
}
