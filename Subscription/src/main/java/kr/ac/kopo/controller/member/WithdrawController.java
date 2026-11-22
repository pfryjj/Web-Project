package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberService;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 회원 탈퇴 처리 (USER-FUNC-05)
 * 1. 비밀번호로 본인 확인 → 2. 구독 데이터 + 회원 정보 삭제(트랜잭션) → 3. 세션 만료
 */
public class WithdrawController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		MemberVO loginUser = WebUtil.loginUser(request);
		String password = request.getParameter("password") == null ? "" : request.getParameter("password");

		if (loginUser.isAdmin()) {
			request.setAttribute("errorMsg", "관리자 계정은 탈퇴할 수 없습니다.");
			return "/WEB-INF/jsp/member/withdrawForm.jsp";
		}

		MemberService memberService = new MemberServiceImpl();
		if (password.isEmpty() || !memberService.withdraw(loginUser.getId(), password)) {
			// 예외 처리 : 비밀번호 불일치 → 탈퇴 실패 안내
			request.setAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
			return "/WEB-INF/jsp/member/withdrawForm.jsp";
		}

		request.getSession().invalidate();		// 세션 만료 처리
		WebUtil.flash(request, "info", "회원 탈퇴가 완료되었습니다. 그동안 이용해 주셔서 감사합니다.");
		return "redirect:/main.do";
	}
}
