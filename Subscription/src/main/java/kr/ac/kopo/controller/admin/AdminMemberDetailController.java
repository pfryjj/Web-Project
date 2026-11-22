package kr.ac.kopo.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.subscription.service.SubscriptionServiceImpl;
import kr.ac.kopo.util.WebUtil;

/**
 * 관리자 - 특정 회원 상세 정보 조회
 *
 * 요구사항 User-004 "회원 상세정보(회원, 관리자)" / 요구사항명세 "관리자는 특정 회원의 상세 정보를 조회"
 * → 유스케이스 다이어그램과 기능명세서 모두에서 누락되어 있던 기능 (불일치 보고서 참고)
 * 구독 내역 자체는 개인 정보이므로 건수와 월 지출액 요약만 보여준다.
 */
public class AdminMemberDetailController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = WebUtil.param(request, "id");
		MemberVO member = new MemberServiceImpl().getMember(id);

		if (member == null) {
			WebUtil.flash(request, "error", "존재하지 않는 회원입니다.");
			return "redirect:/admin/memberList.do";
		}

		request.setAttribute("member", member);
		request.setAttribute("summary", new SubscriptionServiceImpl().getSummary(id));
		return "/WEB-INF/jsp/admin/memberDetail.jsp";
	}
}
