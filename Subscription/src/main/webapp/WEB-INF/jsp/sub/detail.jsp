<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
	S-03 구독 상세 화면
	  USER-FUNC-08 상세 조회 / USER-FUNC-10 삭제 / USER-FUNC-13 상태 변경 / 결제일 갱신(Sub-009)
--%>
<c:set var="pageTitle" value="${ sub.serviceName }" />
<c:set var="menu" value="sub" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<p style="margin: 0 0 14px;"><a href="${ ctx }/sub/list.do" class="muted">← 내 구독 목록</a></p>

<div class="detail-layout">
	<section class="card">
		<div class="detail-head">
			<div>
				<span class="chip cat-${ sub.categoryCode }"><c:out value="${ sub.category }" /></span>
				<h1><c:out value="${ sub.serviceName }" /></h1>
			</div>
			<span class="badge status-${ sub.status }">${ sub.statusLabel }</span>
		</div>

		<dl class="info-list">
			<dt>월 결제 금액</dt>
			<dd class="num">
				<strong style="font-size: 20px;"><fmt:formatNumber value="${ sub.amount }" pattern="#,##0" />원</strong>
				<span class="muted">(연 환산 <fmt:formatNumber value="${ sub.amount * 12 }" pattern="#,##0" />원)</span>
			</dd>

			<dt>다음 결제일</dt>
			<dd class="num">${ sub.nextPayDate } <%@ include file="/WEB-INF/jsp/include/dday.jspf" %></dd>

			<dt>구독 시작일</dt>
			<dd class="num">${ empty sub.startDate ? '-' : sub.startDate }</dd>

			<dt>카테고리</dt>
			<dd><c:out value="${ sub.category }" /></dd>

			<dt>만족도</dt>
			<dd><%@ include file="/WEB-INF/jsp/include/stars.jspf" %></dd>

			<dt>메모</dt>
			<dd>
				<c:choose>
					<c:when test="${ empty sub.memo }"><span class="muted">-</span></c:when>
					<c:otherwise><div class="memo-box"><c:out value="${ sub.memo }" /></div></c:otherwise>
				</c:choose>
			</dd>

			<dt>등록일</dt>
			<dd class="num">${ sub.regDate }</dd>
		</dl>

		<div class="form-actions" style="justify-content: space-between; margin-top: 28px;">
			<%-- USER-FUNC-10 삭제 --%>
			<form action="${ ctx }/sub/delete.do" method="post" class="inline"
				  data-confirm="'${ fn:escapeXml(sub.serviceName) }' 구독 내역을 삭제할까요? 삭제 후에는 되돌릴 수 없습니다.">
				<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
				<input type="hidden" name="sno" value="${ sub.sno }">
				<button type="submit" class="btn btn-danger">삭제</button>
			</form>
			<div class="btn-row">
				<a href="${ ctx }/sub/list.do" class="btn btn-ghost">목록</a>
				<a href="${ ctx }/sub/updateForm.do?sno=${ sub.sno }" class="btn btn-primary">수정</a>
			</div>
		</div>
	</section>

	<aside>
		<%-- USER-FUNC-13 구독 상태 변경 --%>
		<div class="card side-card">
			<h3>구독 상태 변경</h3>
			<div class="segmented">
				<c:forEach var="st" items="${ statusMap }">
					<form action="${ ctx }/sub/status.do" method="post" class="inline">
						<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
						<input type="hidden" name="sno" value="${ sub.sno }">
						<input type="hidden" name="status" value="${ st.key }">
						<input type="hidden" name="from" value="detail">
						<c:choose>
							<c:when test="${ st.key eq sub.status }">
								<button type="button" class="btn btn-outline current" disabled>${ st.value } <span>현재 상태</span></button>
							</c:when>
							<c:otherwise>
								<button type="submit" class="btn btn-outline">${ st.value } <span class="muted">변경하기 →</span></button>
							</c:otherwise>
						</c:choose>
					</form>
				</c:forEach>
			</div>
			<p class="hint" style="margin-top: 12px;">월 고정 지출에는 '구독 중' 상태만 합산됩니다.</p>
		</div>

		<%-- 결제 일정 관리 (요구사항 Sub-009) --%>
		<div class="card side-card">
			<h3>결제 일정</h3>
			<c:choose>
				<c:when test="${ sub.active }">
					<p class="muted" style="margin: 0 0 12px; font-size: 14px;">
						이번 달 결제가 끝났다면 다음 결제일을 한 달 뒤로 넘기세요.
					</p>
					<form action="${ ctx }/sub/renew.do" method="post">
						<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
						<input type="hidden" name="sno" value="${ sub.sno }">
						<input type="hidden" name="from" value="detail">
						<button type="submit" class="btn btn-success btn-block">결제 완료 · 다음 결제일 +1개월</button>
					</form>
				</c:when>
				<c:otherwise>
					<p class="muted" style="margin: 0; font-size: 14px;">'구독 중' 상태일 때만 결제일을 갱신할 수 있습니다.</p>
				</c:otherwise>
			</c:choose>
		</div>
	</aside>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
