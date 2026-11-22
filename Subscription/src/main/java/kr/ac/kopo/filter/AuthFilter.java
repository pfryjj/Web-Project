package kr.ac.kopo.filter;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 인증·인가 필터 (Sec-001 사용자 인증 및 세션 관리 / 유스케이스의 «include» 사용자 인증)
 *
 * 컨트롤러마다 "로그인했는지" 검사 코드를 넣지 않고, 컨트롤러 실행 "전"에 한 곳에서 검사한다.
 *  - 공개 URI         : 누구나 접근
 *  - 비로그인 전용 URI : 로그인 상태로 접근하면 메인으로 (회원가입·로그인 사전조건 = 비로그인 상태)
 *  - /admin/**        : 관리자만 (GET 은 메인으로 리다이렉트, POST 는 403 에러 페이지)
 *  - 그 외            : 로그인 필요 → 로그인 화면으로 리다이렉트
 *
 * 등록 위치 : web.xml (필터 실행 순서를 보장하려면 어노테이션보다 web.xml 이 확실함)
 */
public class AuthFilter implements Filter {

	private static final Set<String> PUBLIC_URIS = Set.of(
			"/main.do",
			"/member/logout.do",
			"/member/checkId.do");

	private static final Set<String> GUEST_ONLY_URIS = Set.of(
			"/member/loginForm.do",
			"/member/login.do",
			"/member/joinForm.do",
			"/member/join.do");

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String context = request.getContextPath();
		String uri = request.getRequestURI().substring(context.length());
		MemberVO loginUser = WebUtil.loginUser(request);

		if (PUBLIC_URIS.contains(uri)) {
			chain.doFilter(request, response);
			return;
		}

		if (GUEST_ONLY_URIS.contains(uri)) {
			if (loginUser != null) {
				response.sendRedirect(context + "/main.do");
				return;
			}
			chain.doFilter(request, response);
			return;
		}

		// 여기부터는 로그인이 필요한 URI
		if (loginUser == null) {
			WebUtil.flash(request, "info", "로그인이 필요한 서비스입니다. 로그인해 주세요.");
			response.sendRedirect(context + "/member/loginForm.do");
			return;
		}

		if (uri.startsWith("/admin/") && !loginUser.isAdmin()) {
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				// ADMIN-FUNC-02 : 관리자 권한이 없는 사용자 → 메인 페이지로 강제 리다이렉트
				WebUtil.flash(request, "error", "관리자만 접근할 수 있는 메뉴입니다.");
				response.sendRedirect(context + "/main.do");
			} else {
				// ADMIN-FUNC-03 : 관리자 권한 미보유 → 요청 차단 및 에러 페이지
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
			return;
		}

		chain.doFilter(request, response);
	}
}
