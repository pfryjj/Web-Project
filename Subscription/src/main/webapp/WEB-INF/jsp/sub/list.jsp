<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
	S-02 내 구독 목록 화면
	  USER-FUNC-07 내 구독 목록 및 월 지출액 / USER-FUNC-11 서비스명·카테고리 검색
	  USER-FUNC-12 상태·별점별 조회 / USER-FUNC-13 상태 변경 / 결제일 갱신(Sub-009)
--%>
<c:set var="pageTitle" value="내 구독" />
<c:set var="menu" value="sub" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="page-head">
	<div>
		<h1>내 구독</h1>
		<p><c:out value="${ sessionScope.loginUser.name }" />님이 관리 중인 정기구독 서비스입니다.</p>
	</div>
	<a href="${ ctx }/sub/writeForm.do" class="btn btn-primary">+ 구독 등록</a>
</div>

<%-- ===== 지출 요약 ===== --%>
<div class="summary-grid">
	<div class="card stat stat-main">
		<div class="stat-label">월 고정 지출</div>
		<div class="stat-value num"><fmt:formatNumber value="${ summary.monthlyTotal }" pattern="#,##0" /><small>원</small></div>
		<div class="stat-sub">구독 중 ${ summary.activeCount }개 합계 · 연 <fmt:formatNumber value="${ summary.yearlyTotal }" pattern="#,##0" />원</div>
	</div>
	<div class="card stat">
		<div class="stat-label">이번 달 남은 결제</div>
		<div class="stat-value num"><fmt:formatNumber value="${ summary.remainingThisMonth }" pattern="#,##0" /><small>원</small></div>
		<div class="stat-sub">오늘부터 이번 달 말일까지</div>
	</div>
	<div class="card stat ${ summary.upcomingCount gt 0 ? 'stat-alert' : '' }">
		<div class="stat-label">7일 이내 결제 예정</div>
		<div class="stat-value num">${ summary.upcomingCount }<small>건</small></div>
		<div class="stat-sub">
			<c:choose>
				<c:when test="${ summary.overdueCount gt 0 }">
					<span class="stat-danger">결제일 지남 ${ summary.overdueCount }건 · 갱신 필요</span>
				</c:when>
				<c:otherwise>지난 결제일 없음</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="card stat">
		<div class="stat-label">등록한 구독</div>
		<div class="stat-value num">${ summary.totalCount }<small>개</small></div>
		<div class="stat-sub">해지 예정 ${ summary.cancelPlannedCount } · 해지 완료 ${ summary.canceledCount }</div>
	</div>
</div>

<c:if test="${ summary.totalCount gt 0 }">
	<div class="overview">
		<%-- 카테고리별 월 지출 (구독 중 기준) --%>
		<div class="card">
			<h2 class="section-title">카테고리별 월 지출</h2>
			<c:choose>
				<c:when test="${ empty categoryStats }">
					<p class="muted">'구독 중'인 서비스가 없습니다.</p>
				</c:when>
				<c:otherwise>
					<c:forEach var="stat" items="${ categoryStats }">
						<div class="cat-row">
							<span class="chip cat-${ stat.categoryCode }"><c:out value="${ stat.category }" /></span>
							<div class="bar"><div class="bar-fill cat-${ stat.categoryCode }" style="width: ${ stat.percent }%;"></div></div>
							<span class="amount num"><fmt:formatNumber value="${ stat.total }" pattern="#,##0" />원</span>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>

		<%-- 상태별 구독 수 (클릭하면 해당 상태로 필터) --%>
		<div class="card">
			<h2 class="section-title">상태별 구독</h2>
			<div class="status-summary">
				<a href="${ ctx }/sub/list.do?status=ACTIVE">
					<span class="badge status-ACTIVE">구독 중</span>
					<span class="count num">${ summary.activeCount }</span>
				</a>
				<a href="${ ctx }/sub/list.do?status=CANCEL_PLANNED">
					<span class="badge status-CANCEL_PLANNED">해지 예정</span>
					<span class="count num">${ summary.cancelPlannedCount }</span>
				</a>
				<a href="${ ctx }/sub/list.do?status=CANCELED">
					<span class="badge status-CANCELED">해지 완료</span>
					<span class="count num">${ summary.canceledCount }</span>
				</a>
			</div>
			<p class="hint" style="margin-top: 14px;">월 고정 지출은 '구독 중' 상태만 합산합니다. 상태를 바꾸면 바로 반영됩니다.</p>
		</div>
	</div>
</c:if>

<%-- ===== 검색 / 필터 (GET 방식 : 검색 조건이 주소에 남아 새로고침·뒤로가기 가능) ===== --%>
<form action="${ ctx }/sub/list.do" method="get" class="card filter-bar">
	<input type="search" name="keyword" class="kw" value="${ fn:escapeXml(search.keyword) }"
		   placeholder="서비스명 검색 (예: 넷플릭스)" maxlength="50" aria-label="서비스명 검색어">
	<select name="category" aria-label="카테고리">
		<option value="">전체 카테고리</option>
		<c:forEach var="cat" items="${ categories }">
			<option value="${ cat }" ${ cat eq search.category ? 'selected' : '' }>${ cat }</option>
		</c:forEach>
	</select>
	<select name="status" aria-label="구독 상태">
		<option value="">전체 상태</option>
		<c:forEach var="st" items="${ statusMap }">
			<option value="${ st.key }" ${ st.key eq search.status ? 'selected' : '' }>${ st.value }</option>
		</c:forEach>
	</select>
	<select name="rating" aria-label="별점">
		<option value="">전체 별점</option>
		<c:forEach var="i" begin="0" end="4">
			<option value="${ 5 - i }" ${ search.rating eq (5 - i) ? 'selected' : '' }>★ ${ 5 - i }점</option>
		</c:forEach>
	</select>
	<select name="sort" aria-label="정렬">
		<option value="payDate">결제일 임박순</option>
		<option value="recent" ${ search.sort eq 'recent' ? 'selected' : '' }>최근 등록순</option>
	</select>
	<div class="btn-row">
		<button type="submit" class="btn btn-primary">검색</button>
		<c:if test="${ search.filtered }">
			<a href="${ ctx }/sub/list.do" class="btn btn-ghost">초기화</a>
		</c:if>
	</div>
</form>

<%-- ===== 구독 목록 ===== --%>
<c:choose>
	<c:when test="${ summary.totalCount eq 0 }">
		<%-- USER-FUNC-07 예외 처리 --%>
		<div class="card empty-state">
			<strong>등록된 구독 서비스가 없습니다.</strong>
			이용 중인 구독을 등록하면 월 지출액과 다음 결제일을 한눈에 볼 수 있어요.<br>
			<a href="${ ctx }/sub/writeForm.do" class="btn btn-primary">첫 구독 등록하기</a>
		</div>
	</c:when>
	<c:when test="${ empty subList }">
		<%-- USER-FUNC-11, 12 예외 처리 --%>
		<div class="card empty-state">
			<strong>검색 결과가 없습니다.</strong>
			검색어나 필터 조건을 바꿔 보세요.
		</div>
	</c:when>
	<c:otherwise>
		<div class="result-info">
			<span>${ search.filtered ? '검색 결과' : '전체' } <strong>${ fn:length(subList) }</strong>건</span>
			<span>${ search.sort eq 'recent' ? '최근 등록순' : '다음 결제일 임박순' }</span>
		</div>

		<div class="sub-grid">
			<c:forEach var="sub" items="${ subList }">
				<article class="card sub-card cat-${ sub.categoryCode } ${ sub.status eq 'CANCELED' ? 'is-canceled' : '' }">
					<div class="sub-card-top">
						<span class="chip cat-${ sub.categoryCode }"><c:out value="${ sub.category }" /></span>
						<span class="badge status-${ sub.status }">${ sub.statusLabel }</span>
					</div>

					<div>
						<a class="sub-name" href="${ ctx }/sub/detail.do?sno=${ sub.sno }"><c:out value="${ sub.serviceName }" /></a>
						<div class="sub-amount num"><fmt:formatNumber value="${ sub.amount }" pattern="#,##0" />원 <small>/ 월</small></div>
					</div>

					<div class="sub-meta">
						<span>다음 결제 <strong class="num">${ sub.nextPayDate }</strong>
							<%@ include file="/WEB-INF/jsp/include/dday.jspf" %>
						</span>
						<%@ include file="/WEB-INF/jsp/include/stars.jspf" %>
					</div>

					<div class="sub-actions">
						<%-- USER-FUNC-13 : 상태를 고르면 바로 전송 --%>
						<form action="${ ctx }/sub/status.do" method="post" class="inline">
							<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
							<input type="hidden" name="sno" value="${ sub.sno }">
							<input type="hidden" name="returnQuery" value="${ fn:escapeXml(currentQuery) }">
							<select name="status" data-autosubmit aria-label="구독 상태 변경">
								<c:forEach var="st" items="${ statusMap }">
									<option value="${ st.key }" ${ st.key eq sub.status ? 'selected' : '' }>${ st.value }</option>
								</c:forEach>
							</select>
						</form>

						<div class="btn-row">
							<c:if test="${ sub.active and sub.daysLeft le 0 }">
								<form action="${ ctx }/sub/renew.do" method="post" class="inline">
									<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
									<input type="hidden" name="sno" value="${ sub.sno }">
									<input type="hidden" name="returnQuery" value="${ fn:escapeXml(currentQuery) }">
									<button type="submit" class="btn btn-success btn-sm" title="다음 결제일을 1개월 뒤로">결제 완료</button>
								</form>
							</c:if>
							<a href="${ ctx }/sub/detail.do?sno=${ sub.sno }" class="btn btn-ghost btn-sm">상세</a>
						</div>
					</div>
				</article>
			</c:forEach>
		</div>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
