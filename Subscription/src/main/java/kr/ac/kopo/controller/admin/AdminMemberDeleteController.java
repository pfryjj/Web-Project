package kr.ac.kopo.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.member.service.MemberServiceImpl;
import kr.ac.kopo.util.WebUtil;

/**
 * 회원 강제 탈퇴 처리 (ADMIN-FUNC-03)
 * 해당 회원의 구독 데이터와 회원 정보를 트랜잭션으로 함께 삭제. 관리자 계정은 삭제 불가.
 */
public class AdminMemberDeleteController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String targetId = WebUtil.param(request, "id");

		if (new MemberServiceImpl().forceDelete(targetId)) {
			WebUtil.flash(request, "success", "회원 '" + targetId + "'의 계정과 구독 데이터가 삭제되었습니다.");
		} else {
			WebUtil.flash(request, "error", "삭제할 수 없는 회원입니다. (존재하지 않거나 관리자 계정)");
		}
		return "redirect:/admin/memberList.do";
	}
}
