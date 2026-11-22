package kr.ac.kopo.controller.sub;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.service.SubscriptionService;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.WebUtil;
import kr.ac.kopo.validator.SubscriptionValidator;

/**
 * 구독 정보 수정 처리 (USER-FUNC-09)
 * 세션 아이디와 구독 등록자 아이디가 같을 때만 UPDATE (where sno = ? and member_id = ?)
 */
public class SubUpdateController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		SubscriptionVO sub = SubRequestHelper.bind(request);
		sub.setSno(SubRequestHelper.sno(request));
		sub.setMemberId(WebUtil.loginUser(request).getId());

		List<String> errors = SubscriptionValidator.validate(sub, false);
		if (!errors.isEmpty()) {
			request.setAttribute("sub", sub);
			request.setAttribute("mode", "update");
			request.setAttribute("errors", errors);
			return "/WEB-INF/jsp/sub/form.jsp";
		}

		SubscriptionService subscriptionService = new SubscriptionServiceImpl();
		if (!subscriptionService.modify(sub)) {
			// 예외 처리 : 본인 데이터가 아닌 항목 수정 시도
			WebUtil.flash(request, "error", "권한이 없습니다.");
			return "redirect:/sub/list.do";
		}

		WebUtil.flash(request, "success", "구독 정보가 수정되었습니다.");
		return "redirect:/sub/detail.do?sno=" + sub.getSno();
	}
}
