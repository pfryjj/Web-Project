<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- M-03 마이페이지 - 회원 상세 정보 조회 (USER-FUNC-03) --%>
<c:set var="pageTitle" value="마이페이지" />
<c:set var="menu" value="my" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="container-narrow">
	<div class="page-head">
		<div>
			<h1>${ member.admin ? '내 정보' : '마이페이지' }</h1>
			<p>가입 정보를 확인하고 수정할 수 있습니다.</p>
		</div>
		<div class="btn-row">
			<a href="${ ctx }/member/updateForm.do" class="btn btn-primary">정보 수정</a>
		</div>
	</div>

	<c:if test="${ not empty summary }">
		<div class="summary-grid summary-3">
			<div class="card stat">
				<div class="stat-label">등록한 구독</div>
				<div class="stat-value num">${ summary.totalCount }<small>개</small></div>
			</div>
			<div class="card stat">
				<div class="stat-label">구독 중</div>
				<div class="stat-value num">${ summary.activeCount }<small>개</small></div>
			</div>
			<div class="card stat">
				<div class="stat-label">월 고정 지출</div>
				<div class="stat-value num"><fmt:formatNumber value="${ summary.monthlyTotal }" pattern="#,##0" /><small>원</small></div>
			</div>
		</div>
	</c:if>

	<div class="card">
		<dl class="info-list">
			<dt>아이디</dt>
			<dd><c:out value="${ member.id }" /></dd>
			<dt>이름</dt>
			<dd><c:out value="${ member.name }" /></dd>
			<dt>생년월일</dt>
			<dd>${ member.birthDate }</dd>
			<dt>핸드폰 번호</dt>
			<dd><c:out value="${ member.phone }" /></dd>
			<dt>이메일</dt>
			<dd><c:out value="${ member.email }" /></dd>
			<dt>주소</dt>
			<dd><c:out value="${ member.address }" /></dd>
			<dt>가입일</dt>
			<dd>${ member.regDate }</dd>
			<dt>회원 구분</dt>
			<dd><span class="badge role-${ member.role }">${ member.admin ? '관리자' : '일반 회원' }</span></dd>
		</dl>
	</div>

	<c:if test="${ not member.admin }">
		<p style="text-align: right; margin-top: 14px;">
			<a href="${ ctx }/member/withdrawForm.do" class="muted" style="font-size: 14px;">회원 탈퇴</a>
		</p>
	</c:if>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
