package kr.ac.kopo.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.util.WebUtil;

/**
 * 전체 회원 목록 조회 및 검색 (ADMIN-FUNC-02, A-02)
 * 관리자 권한 확인은 AuthFilter 가 먼저 처리한다.
 * 검색 조건 : 아이디 / 이름 / 이메일 (요구사항명세 User-008 "아이디나 이름 등", 기능명세서 "아이디, 이메일 등")
 */
public class AdminMemberListController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String searchType = WebUtil.param(request, "searchType");
		if (!searchType.equals("name") && !searchType.equals("email")) {
			searchType = "id";
		}
		String keyword = WebUtil.param(request, "keyword");
		if (keyword.length() > 50) {
			keyword = keyword.substring(0, 50);
		}

		request.setAttribute("memberList", new MemberServiceImpl().searchMembers(searchType, keyword));
		request.setAttribute("searchType", searchType);
		request.setAttribute("keyword", keyword);
		return "/WEB-INF/jsp/admin/memberList.jsp";
	}
}
