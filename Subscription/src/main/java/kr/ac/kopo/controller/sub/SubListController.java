package kr.ac.kopo.controller.sub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.service.SubscriptionService;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.subscription.vo.SubSearchVO;
import kr.ac.kopo.subscription.vo.SubSummaryVO;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 내 구독 목록 화면 (S-02) - 기능 3개가 같은 화면을 사용
 *  - USER-FUNC-07 : 내 구독 목록 및 월 지출액 조회
 *  - USER-FUNC-11 : 서비스명 및 카테고리 검색
 *  - USER-FUNC-12 : 구독 상태 및 별점별 조회
 */
public class SubListController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String memberId = WebUtil.loginUser(request).getId();

		// 검색 조건 : 허용된 값이 아니면 무시 (비정상 입력값 방어)
		SubSearchVO search = new SubSearchVO();
		search.setMemberId(memberId);

		String keyword = WebUtil.param(request, "keyword");
		search.setKeyword(keyword.length() > 50 ? keyword.substring(0, 50) : keyword);

		String category = WebUtil.param(request, "category");
		if (SubscriptionVO.CATEGORIES.contains(category)) {
			search.setCategory(category);
		}

		String status = WebUtil.param(request, "status");
		if (SubscriptionVO.STATUS_MAP.containsKey(status)) {
			search.setStatus(status);
		}

		Integer rating = WebUtil.intParam(request, "rating");
		if (rating != null && rating >= 1 && rating <= 5) {
			search.setRating(rating);
		}

		search.setSort(WebUtil.param(request, "sort"));

		SubscriptionService subscriptionService = new SubscriptionServiceImpl();
		SubSummaryVO summary = subscriptionService.getSummary(memberId);

		request.setAttribute("search", search);
		request.setAttribute("subList", subscriptionService.getMySubscriptions(search));
		request.setAttribute("summary", summary);
		request.setAttribute("categoryStats", subscriptionService.getCategoryStats(memberId, summary.getMonthlyTotal()));
		// 상태 변경·결제일 갱신 후 같은 검색 조건의 목록으로 돌아오기 위해 현재 쿼리스트링 보관
		request.setAttribute("currentQuery", request.getQueryString() == null ? "" : request.getQueryString());

		return "/WEB-INF/jsp/sub/list.jsp";
	}
}
