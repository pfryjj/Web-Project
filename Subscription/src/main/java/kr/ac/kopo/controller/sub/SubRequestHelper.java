package kr.ac.kopo.controller.sub;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.kopo.subscription.vo.SubscriptionVO;
import kr.ac.kopo.util.WebUtil;

/**
 * 구독 컨트롤러들이 함께 쓰는 기능
 */
class SubRequestHelper {

	/** 목록으로 돌아갈 때 붙일 쿼리스트링 허용 문자 (줄바꿈 등 헤더 조작 문자 차단) */
	private static final Pattern SAFE_QUERY = Pattern.compile("^[\\w%=&.+\\-]*$");

	private SubRequestHelper() {
	}

	/** 등록·수정 폼의 입력값 → SubscriptionVO */
	static SubscriptionVO bind(HttpServletRequest request) {
		SubscriptionVO sub = new SubscriptionVO();
		sub.setServiceName(WebUtil.param(request, "serviceName"));
		sub.setCategory(WebUtil.param(request, "category"));
		sub.setAmount(WebUtil.intParam(request, "amount"));
		sub.setStartDate(WebUtil.param(request, "startDate"));
		sub.setNextPayDate(WebUtil.param(request, "nextPayDate"));
		sub.setStatus(WebUtil.param(request, "status"));
		sub.setRating(WebUtil.intParam(request, "rating"));
		sub.setMemo(WebUtil.param(request, "memo"));
		return sub;
	}

	/** 구독 번호 파라미터 (없거나 숫자가 아니면 -1 → 조회 결과 없음으로 처리됨) */
	static int sno(HttpServletRequest request) {
		Integer sno = WebUtil.intParam(request, "sno");
		return sno == null ? -1 : sno;
	}

	/**
	 * 상태 변경·결제일 갱신 후 돌아갈 화면
	 *  from=detail → 상세 화면, 그 외 → 목록 화면(보던 검색 조건 유지)
	 */
	static String backTo(HttpServletRequest request, int sno) {
		if ("detail".equals(request.getParameter("from"))) {
			return "redirect:/sub/detail.do?sno=" + sno;
		}
		String query = WebUtil.param(request, "returnQuery");
		if (!query.isEmpty() && SAFE_QUERY.matcher(query).matches()) {
			return "redirect:/sub/list.do?" + query;
		}
		return "redirect:/sub/list.do";
	}
}
