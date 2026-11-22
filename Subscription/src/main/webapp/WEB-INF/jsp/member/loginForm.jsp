<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- M-02 로그인 화면 (회원·관리자 공용) = 비로그인 상태의 메인 화면 --%>
<c:set var="pageTitle" value="로그인" />
<c:set var="menu" value="login" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<section class="landing">
	<div class="landing-copy">
		<p class="eyebrow">정기구독관리 서비스</p>
		<h1>흩어진 구독,<br>한 곳에서 관리하세요.</h1>
		<p class="lead">
			영상·음악·쇼핑 멤버십까지 매달 빠져나가는 고정 지출과 다음 결제일을 한눈에 확인하고,
			잘 쓰지 않는 구독은 바로 정리하세요.
		</p>
		<ul class="feature-list">
			<li><span><strong>월 고정 지출</strong>구독 중인 서비스 금액을 자동으로 합산</span></li>
			<li><span><strong>결제 일정</strong>D-day 표시와 7일 이내 결제 예정 안내</span></li>
			<li><span><strong>상태 관리</strong>구독 중 · 해지 예정 · 해지 완료로 분류</span></li>
			<li><span><strong>검색</strong>서비스명, 카테고리, 만족도 별점으로 찾기</span></li>
		</ul>
	</div>

	<div class="card auth-card">
		<h2>로그인</h2>
		<p class="muted">회원과 관리자 모두 이곳에서 로그인합니다.</p>

		<%@ include file="/WEB-INF/jsp/include/errors.jspf" %>

		<form action="${ ctx }/member/login.do" method="post" class="form">
			<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
			<div class="field">
				<label for="loginId">아이디</label>
				<input type="text" id="loginId" name="id" value="${ fn:escapeXml(inputId) }"
					   required autofocus autocomplete="username">
			</div>
			<div class="field">
				<label for="loginPw">비밀번호</label>
				<input type="password" id="loginPw" name="password" required autocomplete="current-password">
			</div>
			<button type="submit" class="btn btn-primary btn-block">로그인</button>
		</form>

		<p class="auth-switch">아직 회원이 아니신가요? <a href="${ ctx }/member/joinForm.do">회원가입</a></p>
	</div>
</section>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
