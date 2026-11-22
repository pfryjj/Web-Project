package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.util.WebUtil;

/**
 * 로그아웃 (COMMON-FUNC-01) - 회원·관리자 공통, 상단 메뉴의 버튼(POST)
 * 비로그인 상태에서 호출해도 오류 없이 메인으로 이동
 */
public class LogoutController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);
		boolean wasLoggedIn = WebUtil.loginUser(request) != null;

		if (session != null) {
			session.invalidate();	// 세션 정보 제거
		}
		if (wasLoggedIn) {
			WebUtil.flash(request, "info", "로그아웃되었습니다.");	// 새 세션에 안내 문구 저장
		}
		return "redirect:/main.do";
	}
}
