package kr.ac.kopo.controller.member;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberService;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.WebUtil;
import kr.ac.kopo.validator.MemberValidator;

/**
 * 회원 정보 수정 처리 (USER-FUNC-04)
 *
 * 기능명세서 "비밀번호 빈값 전송 시 수정 거부"의 해석 (불일치 보고서 참고)
 *  - 현재 비밀번호 : 필수 (본인 확인용, 비어 있으면 수정 거부)
 *  - 새 비밀번호   : 선택 (비워 두면 기존 비밀번호 유지)
 */
public class UpdateController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		MemberVO loginUser = WebUtil.loginUser(request);
		MemberService memberService = new MemberServiceImpl();

		MemberVO member = new MemberVO();
		member.setId(loginUser.getId());		// 아이디는 화면 값이 아니라 세션 값을 사용 (다른 회원 정보 수정 방지)
		member.setName(WebUtil.param(request, "name"));
		member.setPhone(WebUtil.param(request, "phone"));
		member.setEmail(WebUtil.param(request, "email"));
		member.setAddress(WebUtil.param(request, "address"));

		String currentPassword    = nvl(request.getParameter("currentPassword"));
		String newPassword        = nvl(request.getParameter("newPassword"));
		String newPasswordConfirm = nvl(request.getParameter("newPasswordConfirm"));

		List<String> errors = MemberValidator.validateUpdate(member, currentPassword, newPassword, newPasswordConfirm);
		if (errors.isEmpty() && !memberService.checkPassword(member.getId(), currentPassword)) {
			errors.add("현재 비밀번호가 일치하지 않습니다.");
		}

		if (!errors.isEmpty()) {
			MemberVO original = memberService.getMember(member.getId());
			member.setBirthDate(original.getBirthDate());	// 화면 표시용 (수정 불가 항목)
			member.setRegDate(original.getRegDate());
			request.setAttribute("member", member);
			request.setAttribute("errors", errors);
			return "/WEB-INF/jsp/member/updateForm.jsp";
		}

		memberService.updateMember(member, newPassword.isEmpty() ? null : newPassword);

		// 세션 내 회원 정보 갱신 (상단에 표시되는 이름 등)
		request.getSession().setAttribute(WebUtil.LOGIN_USER, memberService.getMember(member.getId()));

		WebUtil.flash(request, "success", "회원 정보가 수정되었습니다.");
		return "redirect:/member/myPage.do";
	}

	private String nvl(String value) {
		return value == null ? "" : value;
	}
}
