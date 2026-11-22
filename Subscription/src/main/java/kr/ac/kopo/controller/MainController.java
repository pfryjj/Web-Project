package kr.ac.kopo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 메인 : 로그인 상태에 따라 알맞은 첫 화면으로 보낸다.
 *  - 비로그인 → 로그인 화면 (기능명세서 : "메인(로그인) 화면")
 *  - 회원     → 내 구독 목록 (USER-FUNC-02 사후조건)
 *  - 관리자   → 회원 관리   (ADMIN-FUNC-01 사후조건)
 */
public class MainController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		MemberVO loginUser = WebUtil.loginUser(request);

		if (loginUser == null) {
			return "/WEB-INF/jsp/member/loginForm.jsp";
		}
		if (loginUser.isAdmin()) {
			return "redirect:/admin/memberList.do";
		}
		return "redirect:/sub/list.do";
	}
}
