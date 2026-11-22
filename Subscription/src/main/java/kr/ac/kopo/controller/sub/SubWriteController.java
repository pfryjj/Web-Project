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
 * 구독 서비스 등록 처리 (USER-FUNC-06)
 * 1. 입력값 추출 → 2. 세션의 회원 아이디를 VO 에 세팅 → 3. INSERT → 목록으로 redirect
 */
public class SubWriteController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		SubscriptionVO sub = SubRequestHelper.bind(request);
		sub.setMemberId(WebUtil.loginUser(request).getId());	// 작성자는 화면 값이 아닌 세션 값

		List<String> errors = SubscriptionValidator.validate(sub, true);
		if (!errors.isEmpty()) {
			// 예외 처리 : 필수 항목 누락 시 입력 안내 메시지 (입력값 유지)
			request.setAttribute("sub", sub);
			request.setAttribute("mode", "write");
			request.setAttribute("errors", errors);
			return "/WEB-INF/jsp/sub/form.jsp";
		}

		SubscriptionService subscriptionService = new SubscriptionServiceImpl();
		subscriptionService.register(sub);

		WebUtil.flash(request, "success", "'" + sub.getServiceName() + "' 구독이 등록되었습니다.");
		return "redirect:/sub/list.do";
	}
}
