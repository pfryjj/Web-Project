package kr.ac.kopo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 모든 컨트롤러가 구현하는 인터페이스 (Mission-WEB-MVC 와 동일)
 *
 * 반환값 규칙 (DispatcherServlet 이 해석)
 *  - "/WEB-INF/jsp/xxx.jsp"   : 해당 JSP 로 forward
 *  - "redirect:/sub/list.do"  : 해당 URI 로 sendRedirect (컨텍스트 경로는 자동으로 붙음)
 *  - null                     : 컨트롤러가 응답을 직접 작성함 (AJAX/JSON 응답 등)
 */
public interface Controller {

	String handleRequest(HttpServletRequest request, HttpServletResponse response)
				throws Exception;
}
