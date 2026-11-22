<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- A-02 회원 관리 화면 : 전체 회원 목록 조회 및 검색 (ADMIN-FUNC-02) / 강제 탈퇴 (ADMIN-FUNC-03) --%>
<c:set var="pageTitle" value="회원 관리" />
<c:set var="menu" value="admin" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="page-head">
	<div>
		<h1>회원 관리</h1>
		<p>${ empty keyword ? '가입된 전체 회원' : '검색된 회원' } <strong>${ fn:length(memberList) }</strong>명</p>
	</div>
</div>

<form action="${ ctx }/admin/memberList.do" method="get" class="search-bar">
	<select name="searchType" aria-label="검색 조건">
		<option value="id" ${ searchType eq 'id' ? 'selected' : '' }>아이디</option>
		<option value="name" ${ searchType eq 'name' ? 'selected' : '' }>이름</option>
		<option value="email" ${ searchType eq 'email' ? 'selected' : '' }>이메일</option>
	</select>
	<input type="search" name="keyword" value="${ fn:escapeXml(keyword) }" placeholder="검색어를 입력하세요" maxlength="50">
	<button type="submit" class="btn btn-primary">검색</button>
	<c:if test="${ not empty keyword }">
		<a href="${ ctx }/admin/memberList.do" class="btn btn-ghost">전체 보기</a>
	</c:if>
</form>

<div class="card table-wrap">
	<table class="table">
		<thead>
			<tr>
				<th>아이디</th>
				<th>이름</th>
				<th>이메일</th>
				<th>핸드폰 번호</th>
				<th>가입일</th>
				<th class="right">구독 수</th>
				<th>구분</th>
				<th>관리</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${ empty memberList }">
					<tr>
						<td colspan="8" class="empty-state">
							${ empty keyword ? '가입된 회원이 없습니다.' : '일치하는 회원이 없습니다.' }
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach var="m" items="${ memberList }">
						<tr>
							<td><a href="${ ctx }/admin/memberDetail.do?id=${ m.id }"><c:out value="${ m.id }" /></a></td>
							<td><c:out value="${ m.name }" /></td>
							<td><c:out value="${ m.email }" /></td>
							<td class="nowrap"><c:out value="${ m.phone }" /></td>
							<td class="nowrap num">${ m.regDate }</td>
							<td class="right num">${ m.subCount }</td>
							<td><span class="badge role-${ m.role }">${ m.admin ? '관리자' : '회원' }</span></td>
							<td class="nowrap">
								<a href="${ ctx }/admin/memberDetail.do?id=${ m.id }" class="btn btn-ghost btn-sm">상세</a>
								<c:if test="${ not m.admin }">
									<form action="${ ctx }/admin/memberDelete.do" method="post" class="inline"
										  data-confirm="'${ m.id }' 회원을 강제 탈퇴시킬까요? 회원의 구독 데이터도 모두 삭제됩니다.">
										<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
										<input type="hidden" name="id" value="${ m.id }">
										<button type="submit" class="btn btn-danger btn-sm">강제 탈퇴</button>
									</form>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
