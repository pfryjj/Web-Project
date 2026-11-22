package kr.ac.kopo.framework;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;

/**
 * Front Controller - 모든 *.do 요청을 받아서 알맞은 Controller 에게 위임한다.
 *
 * Mission-WEB-MVC 의 DispatcherServlet 에서 추가된 점
 *  1. 등록되지 않은 URI  → 404 응답 (기존: NullPointerException)
 *  2. POST 전용 URI 를 GET 으로 호출 → 405 응답 (삭제/수정 등을 링크 클릭만으로 실행하지 못하게)
 *  3. "redirect:" 접두어 지원 → PRG(Post-Redirect-Get) 패턴, 새로고침 시 중복 등록 방지
 *  4. null 반환 시 forward 하지 않음 → AJAX(JSON) 응답용
 */
@WebServlet(urlPatterns = "*.do", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

	private static final String REDIRECT_PREFIX = "redirect:";

	private HandlerMapping mappings;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		mappings = new HandlerMapping();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String context = request.getContextPath();
		String uri = request.getRequestURI().substring(context.length());

		System.out.println("[" + request.getMethod() + "] 요청 URI : " + uri);

		Controller control = mappings.getController(uri);
		if (control == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		if (mappings.isPostOnly(uri) && !"POST".equalsIgnoreCase(request.getMethod())) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		try {
			String callPage = control.handleRequest(request, response);

			if (callPage == null) {
				return;	// 컨트롤러가 응답을 직접 작성한 경우
			}

			if (callPage.startsWith(REDIRECT_PREFIX)) {
				String target = callPage.substring(REDIRECT_PREFIX.length());
				if (target.startsWith("/")) {
					target = context + target;
				}
				response.sendRedirect(target);
				return;
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher(callPage);
			dispatcher.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);	// web.xml 의 error-page(500) 로 이동
		}
	}
}
