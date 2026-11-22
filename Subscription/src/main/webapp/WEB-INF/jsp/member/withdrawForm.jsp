<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- M-05 회원 탈퇴 화면 (USER-FUNC-05) --%>
<c:set var="pageTitle" value="회원 탈퇴" />
<c:set var="menu" value="my" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="container-narrow" style="max-width: 520px;">
	<div class="page-head">
		<div>
			<h1>회원 탈퇴</h1>
			<p>탈퇴하면 계정과 등록한 모든 구독 내역이 즉시 삭제되며 되돌릴 수 없습니다.</p>
		</div>
	</div>

	<div class="card">
		<%@ include file="/WEB-INF/jsp/include/errors.jspf" %>

		<form action="${ ctx }/member/withdraw.do" method="post" class="form"
			  data-confirm="정말 탈퇴하시겠습니까? 모든 구독 데이터가 삭제됩니다.">
			<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">

			<div class="field">
				<label for="password">비밀번호 확인<span class="req">*</span></label>
				<input type="password" id="password" name="password" required autocomplete="current-password" autofocus>
			</div>

			<label class="check">
				<input type="checkbox" required>
				<span>등록한 구독 내역이 모두 삭제되는 것을 확인했습니다.</span>
			</label>

			<div class="form-actions">
				<a href="${ ctx }/member/myPage.do" class="btn btn-ghost">취소</a>
				<button type="submit" class="btn btn-danger">탈퇴하기</button>
			</div>
		</form>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
