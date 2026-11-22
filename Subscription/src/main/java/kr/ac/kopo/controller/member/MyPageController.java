package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberService;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.subscription.service.SubscriptionService;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.util.WebUtil;

/**
 * 마이페이지 - 회원 상세 정보 조회 (USER-FUNC-03, M-03)
 * 1. 세션에서 로그인 회원 아이디 추출 → 2. DB 에서 최신 회원 정보 조회
 */
public class MyPageController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = WebUtil.loginUser(request).getId();

		MemberService memberService = new MemberServiceImpl();
		MemberVO member = memberService.getMember(id);

		if (member == null) {
			// 관리자가 강제 탈퇴시킨 경우 등 : 세션을 정리하고 로그인 화면으로
			request.getSession().invalidate();
			WebUtil.flash(request, "error", "회원 정보를 찾을 수 없습니다. 다시 로그인해 주세요.");
			return "redirect:/member/loginForm.do";
		}

		request.setAttribute("member", member);
		if (!member.isAdmin()) {
			SubscriptionService subscriptionService = new SubscriptionServiceImpl();
			request.setAttribute("summary", subscriptionService.getSummary(id));
		}
		return "/WEB-INF/jsp/member/myPage.jsp";
	}
}
