package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberService;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 로그인 처리 (USER-FUNC-02 + ADMIN-FUNC-01 을 하나의 로그인으로 통합)
 *
 * 요구사항 User-002 "로그인(회원, 관리자)" 은 하나의 요구사항이므로 화면도 하나로 두고,
 * 로그인 후 권한(role)에 따라 이동할 화면만 나눈다.
 *  - USER  → 내 구독 목록 (/sub/list.do)
 *  - ADMIN → 회원 관리   (/admin/memberList.do)
 */
public class LoginController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = WebUtil.param(request, "id").toLowerCase();
		String password = request.getParameter("password") == null ? "" : request.getParameter("password");

		MemberVO loginUser = null;
		if (!id.isEmpty() && !password.isEmpty()) {
			MemberService memberService = new MemberServiceImpl();
			loginUser = memberService.login(id, password);
		}

		if (loginUser == null) {
			// 예외 처리 : 아이디 없음 / 비밀번호 불일치 → 같은 메시지 (어느 쪽이 틀렸는지 알려주지 않음)
			request.setAttribute("errorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
			request.setAttribute("inputId", id);
			return "/WEB-INF/jsp/member/loginForm.jsp";
		}

		// 세션 고정(Session Fixation) 공격 방지 : 로그인 성공 시 세션 ID 를 새로 발급
		request.changeSessionId();

		HttpSession session = request.getSession();
		session.setAttribute(WebUtil.LOGIN_USER, loginUser);
		WebUtil.flash(request, "success", loginUser.getName() + "님, 환영합니다.");

		return loginUser.isAdmin() ? "redirect:/admin/memberList.do" : "redirect:/sub/list.do";
	}
}
