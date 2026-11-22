package kr.ac.kopo.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.member.vo.MemberVO;

/**
 * 컨트롤러에서 반복되는 작업 모음
 */
public class WebUtil {

	public static final String LOGIN_USER = "loginUser";

	/** 파라미터를 앞뒤 공백 제거해서 반환 (없으면 빈 문자열 → NullPointerException 방지) */
	public static String param(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		return value == null ? "" : value.trim();
	}

	/** 정수 파라미터 (비어 있거나 숫자가 아니면 null) - "12,900" 처럼 콤마가 있어도 허용 */
	public static Integer intParam(HttpServletRequest request, String name) {
		String value = param(request, name).replace(",", "");
		if (value.isEmpty()) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** 세션에 저장된 로그인 회원 (비로그인이면 null) */
	public static MemberVO loginUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (MemberVO) session.getAttribute(LOGIN_USER);
	}

	/**
	 * 플래시 메시지 : redirect 후 "다음 화면"에서 한 번만 보여줄 안내 문구
	 * head.jspf 에서 출력하고 바로 세션에서 지운다.
	 * @param type success | error | info
	 */
	public static void flash(HttpServletRequest request, String type, String message) {
		HttpSession session = request.getSession();
		session.setAttribute("flashType", type);
		session.setAttribute("flashMsg", message);
	}
}
