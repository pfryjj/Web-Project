<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- M-01 회원가입 화면 (USER-FUNC-01) --%>
<c:set var="pageTitle" value="회원가입" />
<c:set var="menu" value="join" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="container-narrow">
	<div class="page-head">
		<div>
			<h1>회원가입</h1>
			<p>가입 후 내 구독 서비스를 등록하고 관리할 수 있습니다.</p>
		</div>
	</div>

	<div class="card">
		<%@ include file="/WEB-INF/jsp/include/errors.jspf" %>

		<form action="${ ctx }/member/join.do" method="post" class="form">
			<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">

			<div class="form-grid">
				<div class="field span-2">
					<label for="joinId">아이디<span class="req">*</span></label>
					<div class="input-group">
						<input type="text" id="joinId" name="id" value="${ fn:escapeXml(member.id) }"
							   required maxlength="20" pattern="[A-Za-z][A-Za-z0-9]{3,19}"
							   title="영문으로 시작하는 4~20자의 영문·숫자" autocomplete="username">
						<button type="button" id="checkIdBtn" class="btn btn-outline">중복 확인</button>
					</div>
					<p class="hint" id="idHint">영문 소문자로 시작하는 4~20자 (영문 소문자·숫자)</p>
				</div>

				<div class="field">
					<label for="password">비밀번호<span class="req">*</span></label>
					<input type="password" id="password" name="password" required minlength="8" maxlength="20"
						   autocomplete="new-password">
					<p class="hint">영문과 숫자를 포함한 8~20자</p>
				</div>
				<div class="field">
					<label for="passwordConfirm">비밀번호 확인<span class="req">*</span></label>
					<input type="password" id="passwordConfirm" name="passwordConfirm" required maxlength="20"
						   autocomplete="new-password">
				</div>

				<div class="field">
					<label for="name">이름<span class="req">*</span></label>
					<input type="text" id="name" name="name" value="${ fn:escapeXml(member.name) }"
						   required maxlength="20" autocomplete="name">
				</div>
				<div class="field">
					<label for="birthDate">생년월일<span class="req">*</span></label>
					<input type="date" id="birthDate" name="birthDate" value="${ fn:escapeXml(member.birthDate) }"
						   required min="1900-01-01">
				</div>

				<div class="field">
					<label for="phone">핸드폰 번호<span class="req">*</span></label>
					<input type="tel" id="phone" name="phone" value="${ fn:escapeXml(member.phone) }"
						   required maxlength="13" placeholder="010-1234-5678" autocomplete="tel">
				</div>
				<div class="field">
					<label for="email">이메일<span class="req">*</span></label>
					<input type="email" id="email" name="email" value="${ fn:escapeXml(member.email) }"
						   required maxlength="100" placeholder="name@example.com" autocomplete="email">
				</div>

				<div class="field span-2">
					<label for="address">주소<span class="req">*</span></label>
					<input type="text" id="address" name="address" value="${ fn:escapeXml(member.address) }"
						   required maxlength="200" autocomplete="street-address">
				</div>
			</div>

			<div class="form-actions">
				<a href="${ ctx }/member/loginForm.do" class="btn btn-ghost">취소</a>
				<button type="submit" class="btn btn-primary">가입하기</button>
			</div>
		</form>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
