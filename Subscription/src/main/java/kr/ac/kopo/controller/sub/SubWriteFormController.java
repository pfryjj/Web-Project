package kr.ac.kopo.controller.sub;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.subscription.vo.SubscriptionVO;

/** 구독 등록 화면 (S-01) */
public class SubWriteFormController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 입력 편의를 위한 기본값 : 오늘 시작, 한 달 뒤 결제, 구독 중
		LocalDate today = LocalDate.now();
		SubscriptionVO sub = new SubscriptionVO();
		sub.setStartDate(today.toString());
		sub.setNextPayDate(today.plusMonths(1).toString());
		sub.setStatus(SubscriptionVO.STATUS_ACTIVE);

		request.setAttribute("sub", sub);
		request.setAttribute("mode", "write");
		return "/WEB-INF/jsp/sub/form.jsp";
	}
}
