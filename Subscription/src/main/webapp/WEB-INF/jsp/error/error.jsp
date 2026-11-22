<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- 공통 에러 페이지 (web.xml 의 error-page 설정으로 이동) --%>
<c:set var="code" value="${ pageContext.errorData.statusCode }" />
<c:set var="pageTitle" value="오류" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="card error-page">
	<div class="error-code">${ code }</div>
	<c:choose>
		<c:when test="${ code eq 403 }">
			<h1>접근 권한이 없습니다.</h1>
			<p>관리자만 사용할 수 있는 기능이거나, 본인의 데이터가 아닙니다.</p>
		</c:when>
		<c:when test="${ code eq 404 }">
			<h1>페이지를 찾을 수 없습니다.</h1>
			<p>주소가 정확한지 확인해 주세요.</p>
		</c:when>
		<c:when test="${ code eq 405 }">
			<h1>허용되지 않은 요청입니다.</h1>
			<p>등록·수정·삭제 기능은 화면의 버튼으로만 실행할 수 있습니다.</p>
		</c:when>
		<c:otherwise>
			<h1>일시적인 오류가 발생했습니다.</h1>
			<p>잠시 후 다시 시도해 주세요. 계속되면 DB 연결 상태(서버 로그)를 확인해 주세요.</p>
		</c:otherwise>
	</c:choose>
	<a href="${ ctx }/main.do" class="btn btn-primary">메인으로</a>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
