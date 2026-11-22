package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;

/** 회원 탈퇴 화면 (M-05) */
public class WithdrawFormController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "/WEB-INF/jsp/member/withdrawForm.jsp";
	}
}
