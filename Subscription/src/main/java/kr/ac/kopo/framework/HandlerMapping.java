package kr.ac.kopo.framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kr.ac.kopo.controller.Controller;
import kr.ac.kopo.controller.MainController;
import kr.ac.kopo.controller.admin.AdminMemberDeleteController;
import kr.ac.kopo.controller.admin.AdminMemberDetailController;
import kr.ac.kopo.controller.admin.AdminMemberListController;
import kr.ac.kopo.controller.member.CheckIdController;
import kr.ac.kopo.controller.member.JoinController;
import kr.ac.kopo.controller.member.JoinFormController;
import kr.ac.kopo.controller.member.LoginController;
import kr.ac.kopo.controller.member.LoginFormController;
import kr.ac.kopo.controller.member.LogoutController;
import kr.ac.kopo.controller.member.MyPageController;
import kr.ac.kopo.controller.member.UpdateController;
import kr.ac.kopo.controller.member.UpdateFormController;
import kr.ac.kopo.controller.member.WithdrawController;
import kr.ac.kopo.controller.member.WithdrawFormController;
import kr.ac.kopo.controller.sub.SubDeleteController;
import kr.ac.kopo.controller.sub.SubDetailController;
import kr.ac.kopo.controller.sub.SubListController;
import kr.ac.kopo.controller.sub.SubRenewController;
import kr.ac.kopo.controller.sub.SubStatusController;
import kr.ac.kopo.controller.sub.SubUpdateController;
import kr.ac.kopo.controller.sub.SubUpdateFormController;
import kr.ac.kopo.controller.sub.SubWriteController;
import kr.ac.kopo.controller.sub.SubWriteFormController;

/**
 * URI ↔ Controller 매핑 (README.md 의 "URI 정보" 표와 1:1 대응)
 * 괄호 안은 기능명세서의 기능 ID
 */
public class HandlerMapping {

	private Map<String, Controller> mappings;
	private Set<String> postOnly;		// 데이터를 바꾸는 요청은 POST 로만 허용

	public HandlerMapping() {
		mappings = new HashMap<String, Controller>();
		postOnly = new HashSet<String>();

		// 메인 : 비로그인 → 로그인 화면, 회원 → 내 구독 목록, 관리자 → 회원 관리
		mappings.put("/main.do", new MainController());

		// ---------- 회원 (USER-FUNC-01 ~ 05, COMMON-FUNC-01) ----------
		mappings.put("/member/joinForm.do", new JoinFormController());
		post("/member/join.do", new JoinController());						// USER-FUNC-01
		mappings.put("/member/checkId.do", new CheckIdController());		// 아이디 중복확인(AJAX)
		mappings.put("/member/loginForm.do", new LoginFormController());
		post("/member/login.do", new LoginController());					// USER-FUNC-02, ADMIN-FUNC-01
		post("/member/logout.do", new LogoutController());					// COMMON-FUNC-01
		mappings.put("/member/myPage.do", new MyPageController());			// USER-FUNC-03
		mappings.put("/member/updateForm.do", new UpdateFormController());
		post("/member/update.do", new UpdateController());					// USER-FUNC-04
		mappings.put("/member/withdrawForm.do", new WithdrawFormController());
		post("/member/withdraw.do", new WithdrawController());				// USER-FUNC-05

		// ---------- 구독 관리 (USER-FUNC-06 ~ 13 + 결제일 갱신) ----------
		mappings.put("/sub/list.do", new SubListController());				// USER-FUNC-07, 11, 12
		mappings.put("/sub/writeForm.do", new SubWriteFormController());
		post("/sub/write.do", new SubWriteController());					// USER-FUNC-06
		mappings.put("/sub/detail.do", new SubDetailController());			// USER-FUNC-08
		mappings.put("/sub/updateForm.do", new SubUpdateFormController());
		post("/sub/update.do", new SubUpdateController());					// USER-FUNC-09
		post("/sub/delete.do", new SubDeleteController());					// USER-FUNC-10
		post("/sub/status.do", new SubStatusController());					// USER-FUNC-13
		post("/sub/renew.do", new SubRenewController());					// (신규 제안) 결제 일정 관리

		// ---------- 관리자 (ADMIN-FUNC-02, 03 + 회원 상세) ----------
		mappings.put("/admin/memberList.do", new AdminMemberListController());		// ADMIN-FUNC-02
		mappings.put("/admin/memberDetail.do", new AdminMemberDetailController());	// (누락 보완) User-004 관리자
		post("/admin/memberDelete.do", new AdminMemberDeleteController());			// ADMIN-FUNC-03
	}

	private void post(String uri, Controller controller) {
		mappings.put(uri, controller);
		postOnly.add(uri);
	}

	public Controller getController(String uri) {
		return mappings.get(uri);
	}

	public boolean isPostOnly(String uri) {
		return postOnly.contains(uri);
	}
}
