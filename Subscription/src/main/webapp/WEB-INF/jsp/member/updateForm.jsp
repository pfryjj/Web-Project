<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- M-04 회원 정보 수정 화면 (USER-FUNC-04) --%>
<c:set var="pageTitle" value="회원 정보 수정" />
<c:set var="menu" value="my" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="container-narrow">
	<div class="page-head">
		<div>
			<h1>회원 정보 수정</h1>
			<p>아이디와 생년월일은 변경할 수 없습니다.</p>
		</div>
	</div>

	<div class="card">
		<%@ include file="/WEB-INF/jsp/include/errors.jspf" %>

		<form action="${ ctx }/member/update.do" method="post" class="form">
			<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">

			<div class="form-grid">
				<div class="field">
					<label for="id">아이디</label>
					<input type="text" id="id" value="${ fn:escapeXml(member.id) }" readonly>
				</div>
				<div class="field">
					<label for="birthDate">생년월일</label>
					<input type="text" id="birthDate" value="${ member.birthDate }" readonly>
				</div>

				<div class="field">
					<label for="name">이름<span class="req">*</span></label>
					<input type="text" id="name" name="name" value="${ fn:escapeXml(member.name) }" required maxlength="20">
				</div>
				<div class="field">
					<label for="phone">핸드폰 번호<span class="req">*</span></label>
					<input type="tel" id="phone" name="phone" value="${ fn:escapeXml(member.phone) }"
						   required maxlength="13" placeholder="010-1234-5678">
				</div>

				<div class="field span-2">
					<label for="email">이메일<span class="req">*</span></label>
					<input type="email" id="email" name="email" value="${ fn:escapeXml(member.email) }" required maxlength="100">
				</div>
				<div class="field span-2">
					<label for="address">주소<span class="req">*</span></label>
					<input type="text" id="address" name="address" value="${ fn:escapeXml(member.address) }" required maxlength="200">
				</div>

				<div class="field">
					<label for="newPassword">새 비밀번호</label>
					<input type="password" id="newPassword" name="newPassword" minlength="8" maxlength="20" autocomplete="new-password">
					<p class="hint">변경하지 않으려면 비워 두세요.</p>
				</div>
				<div class="field">
					<label for="newPasswordConfirm">새 비밀번호 확인</label>
					<input type="password" id="newPasswordConfirm" name="newPasswordConfirm" maxlength="20" autocomplete="new-password">
				</div>

				<div class="field span-2">
					<label for="currentPassword">현재 비밀번호<span class="req">*</span></label>
					<input type="password" id="currentPassword" name="currentPassword" required autocomplete="current-password">
					<p class="hint">본인 확인을 위해 현재 비밀번호를 입력해 주세요.</p>
				</div>
			</div>

			<div class="form-actions">
				<a href="${ ctx }/member/myPage.do" class="btn btn-ghost">취소</a>
				<button type="submit" class="btn btn-primary">저장</button>
			</div>
		</form>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
