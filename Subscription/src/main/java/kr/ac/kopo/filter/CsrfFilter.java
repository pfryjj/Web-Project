package kr.ac.kopo.filter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ac.kopo.util.WebUtil;

/**
 * CSRF(사이트 간 요청 위조) 방어 필터 (Sec-002 웹 취약점 방어)
 *
 * 공격 예 : 로그인한 회원이 악성 사이트를 열면, 그 사이트가 몰래
 *          <form action="http://localhost:8080/Subscription-MVC/sub/delete.do" method="post"> 를 전송
 *          → 브라우저는 세션 쿠키를 자동으로 붙이므로 회원 모르게 구독이 삭제됨
 *
 * 방어 : 세션마다 예측 불가능한 토큰을 만들고, 우리 사이트의 모든 POST 폼에 hidden 으로 넣는다.
 *        POST 요청의 토큰이 세션 토큰과 다르면 거부한다. (악성 사이트는 토큰 값을 알 수 없음)
 *
 * JSP 사용법 : <input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
 */
public class CsrfFilter implements Filter {

	public static final String TOKEN_NAME = "csrfToken";
	public static final String PARAM_NAME = "_csrf";

	private static final SecureRandom RANDOM = new SecureRandom();

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession();
		String sessionToken = (String) session.getAttribute(TOKEN_NAME);
		if (sessionToken == null) {
			sessionToken = newToken();
			session.setAttribute(TOKEN_NAME, sessionToken);
		}

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			String requestToken = request.getParameter(PARAM_NAME);
			if (!isSame(sessionToken, requestToken)) {
				String context = request.getContextPath();
				String uri = request.getRequestURI().substring(context.length());

				// 세션 만료 후 로그아웃을 누른 경우 : 이미 로그아웃 상태이므로 조용히 메인으로 (COMMON-FUNC-01 예외처리)
				if (!"/member/logout.do".equals(uri)) {
					WebUtil.flash(request, "error", "요청이 만료되었거나 올바르지 않습니다. 다시 시도해 주세요.");
				}
				response.sendRedirect(context + "/main.do");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private static String newToken() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private static boolean isSame(String a, String b) {
		if (a == null || b == null) {
			return false;
		}
		return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
	}
}
