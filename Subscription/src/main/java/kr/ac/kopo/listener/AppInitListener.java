package kr.ac.kopo.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import kr.ac.kopo.subscription.vo.SubscriptionVO;

/**
 * 서버(웹 애플리케이션) 시작 시 한 번 실행
 * 모든 JSP 에서 공통으로 쓰는 코드 목록을 application 영역에 등록한다.
 *   ${ applicationScope.categories } / ${ categories } : 카테고리 목록
 *   ${ statusMap }                                    : 구독 상태 코드 → 표시명
 */
@WebListener
public class AppInitListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext application = sce.getServletContext();
		application.setAttribute("categories", SubscriptionVO.CATEGORIES);
		application.setAttribute("statusMap", SubscriptionVO.STATUS_MAP);
		System.out.println("[구독노트] 공통 코드 등록 완료");
	}
}
