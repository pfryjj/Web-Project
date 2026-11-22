package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.util.WebUtil;

/** 회원 정보 수정 화면 (M-04) */
public class UpdateFormController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = WebUtil.loginUser(request).getId();
		request.setAttribute("member", new MemberServiceImpl().getMember(id));
		return "/WEB-INF/jsp/member/updateForm.jsp";
	}
}
