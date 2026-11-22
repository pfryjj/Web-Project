<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
	관리자 - 회원 상세 정보 (요구사항 User-004 의 관리자 부분)
	유스케이스 다이어그램·기능명세서에서 누락되어 보완한 화면
--%>
<c:set var="pageTitle" value="회원 상세" />
<c:set var="menu" value="admin" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="container-narrow">
	<p style="margin: 0 0 14px;"><a href="${ ctx }/admin/memberList.do" class="muted">← 회원 목록</a></p>

	<div class="page-head">
		<div>
			<h1><c:out value="${ member.name }" /> <span class="muted" style="font-size: 18px;">(<c:out value="${ member.id }" />)</span></h1>
			<p>가입일 ${ member.regDate }</p>
		</div>
		<span class="badge role-${ member.role }">${ member.admin ? '관리자' : '일반 회원' }</span>
	</div>

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
		</dl>

		<c:if test="${ not member.admin }">
			<div class="form-actions" style="margin-top: 24px;">
				<form action="${ ctx }/admin/memberDelete.do" method="post" class="inline"
					  data-confirm="'${ member.id }' 회원을 강제 탈퇴시킬까요? 회원의 구독 데이터도 모두 삭제됩니다.">
					<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
					<input type="hidden" name="id" value="${ member.id }">
					<button type="submit" class="btn btn-danger">강제 탈퇴</button>
				</form>
			</div>
		</c:if>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
