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
 * 회원가입 처리 (USER-FUNC-01)
 * 1. 입력값 추출 → 2. 유효성 검증 → 3. 아이디 중복 검사 → 4. 비밀번호 암호화 후 INSERT
 */
public class JoinController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		MemberVO member = new MemberVO();
		member.setId(WebUtil.param(request, "id").toLowerCase());
		member.setPassword(request.getParameter("password") == null ? "" : request.getParameter("password"));
		member.setName(WebUtil.param(request, "name"));
		member.setBirthDate(WebUtil.param(request, "birthDate"));
		member.setPhone(WebUtil.param(request, "phone"));
		member.setEmail(WebUtil.param(request, "email"));
		member.setAddress(WebUtil.param(request, "address"));
		String passwordConfirm = request.getParameter("passwordConfirm") == null ? "" : request.getParameter("passwordConfirm");

		List<String> errors = MemberValidator.validateJoin(member, passwordConfirm);

		MemberService memberService = new MemberServiceImpl();
		if (errors.isEmpty() && !memberService.isIdAvailable(member.getId())) {
			errors.add("이미 사용 중인 아이디입니다.");
		}
		if (errors.isEmpty() && !memberService.join(member)) {
			errors.add("이미 사용 중인 아이디입니다.");	// 동시에 같은 아이디로 가입한 경우
		}

		if (!errors.isEmpty()) {
			// 예외 처리 : 오류 메시지와 함께 가입 페이지 유지 (입력값은 그대로, 비밀번호만 비움)
			member.setPassword(null);
			request.setAttribute("member", member);
			request.setAttribute("errors", errors);
			return "/WEB-INF/jsp/member/joinForm.jsp";
		}

		// 사후 조건 : 로그인 페이지로 이동
		WebUtil.flash(request, "success", "회원가입이 완료되었습니다. 로그인해 주세요.");
		return "redirect:/member/loginForm.do";
	}
}
