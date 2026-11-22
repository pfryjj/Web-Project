package kr.ac.kopo.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.util.WebUtil;
import kr.ac.kopo.validator.MemberValidator;

/**
 * 아이디 중복 확인 (AJAX) - 회원가입 화면에서 fetch() 로 호출
 * JSP 로 forward 하지 않고 JSON 문자열을 직접 응답하므로 null 을 반환한다.
 *
 * 응답 예 : {"available":true,"message":"사용 가능한 아이디입니다."}
 * (최종 중복 검사는 가입 처리(JoinController)에서 한 번 더 한다)
 */
public class CheckIdController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = WebUtil.param(request, "id").toLowerCase();

		boolean available;
		String message;
		if (!MemberValidator.isValidId(id)) {
			available = false;
			message = "영문 소문자로 시작하는 4~20자의 영문 소문자·숫자만 사용할 수 있습니다.";
		} else if (new MemberServiceImpl().isIdAvailable(id)) {
			available = true;
			message = "사용 가능한 아이디입니다.";
		} else {
			available = false;
			message = "이미 사용 중인 아이디입니다.";
		}

		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write("{\"available\":" + available + ",\"message\":\"" + message + "\"}");
		return null;
	}
}
