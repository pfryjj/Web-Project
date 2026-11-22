<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
	S-01 구독 등록 화면 (USER-FUNC-06) / S-04 구독 수정 화면 (USER-FUNC-09)
	두 화면은 입력 항목이 거의 같으므로 하나의 JSP 를 mode(write | update) 로 구분해서 사용
	(구독 상태는 등록할 때만 입력, 이후 변경은 목록·상세의 상태 변경 기능 USER-FUNC-13 사용)
--%>
<c:set var="isUpdate" value="${ mode eq 'update' }" />
<c:set var="pageTitle" value="${ isUpdate ? '구독 정보 수정' : '구독 서비스 등록' }" />
<c:set var="menu" value="${ isUpdate ? 'sub' : 'write' }" />
<%@ include file="/WEB-INF/jsp/include/head.jspf" %>

<div class="container-narrow">
	<div class="page-head">
		<div>
			<h1>${ pageTitle }</h1>
			<p>${ isUpdate ? '결제 금액, 결제일, 별점, 메모 등을 수정할 수 있습니다.' : '이용 중인 구독 서비스 정보를 입력해 주세요.' }</p>
		</div>
	</div>

	<div class="card">
		<%@ include file="/WEB-INF/jsp/include/errors.jspf" %>

		<form action="${ ctx }/sub/${ isUpdate ? 'update' : 'write' }.do" method="post" class="form">
			<input type="hidden" name="_csrf" value="${ sessionScope.csrfToken }">
			<c:if test="${ isUpdate }">
				<input type="hidden" name="sno" value="${ sub.sno }">
			</c:if>

			<div class="form-grid">
				<div class="field span-2">
					<label for="serviceName">서비스명<span class="req">*</span></label>
					<input type="text" id="serviceName" name="serviceName" value="${ fn:escapeXml(sub.serviceName) }"
						   required maxlength="50" placeholder="예: 넷플릭스" autofocus>
				</div>

				<div class="field">
					<label for="category">카테고리<span class="req">*</span></label>
					<select id="category" name="category" required>
						<option value="">선택하세요</option>
						<c:forEach var="cat" items="${ categories }">
							<option value="${ cat }" ${ cat eq sub.category ? 'selected' : '' }>${ cat }</option>
						</c:forEach>
					</select>
				</div>
				<div class="field">
					<label for="amount">월 결제 금액(원)<span class="req">*</span></label>
					<input type="number" id="amount" name="amount" value="${ sub.amount }"
						   required min="0" max="10000000" step="1" placeholder="17000">
				</div>

				<div class="field">
					<label for="startDate">구독 시작일</label>
					<input type="date" id="startDate" name="startDate" value="${ fn:escapeXml(sub.startDate) }">
				</div>
				<div class="field">
					<label for="nextPayDate">다음 결제일<span class="req">*</span></label>
					<input type="date" id="nextPayDate" name="nextPayDate" value="${ fn:escapeXml(sub.nextPayDate) }" required>
				</div>

				<c:if test="${ not isUpdate }">
					<div class="field span-2">
						<label for="status">구독 상태<span class="req">*</span></label>
						<select id="status" name="status" required>
							<c:forEach var="st" items="${ statusMap }">
								<option value="${ st.key }" ${ st.key eq sub.status ? 'selected' : '' }>${ st.value }</option>
							</c:forEach>
						</select>
					</div>
				</c:if>

				<div class="field span-2">
					<span>만족도 별점</span>
					<div class="rating-input">
						<label><input type="radio" name="rating" value="" ${ empty sub.rating ? 'checked' : '' }> 평가 안 함</label>
						<c:forEach var="r" begin="1" end="5">
							<label><input type="radio" name="rating" value="${ r }" ${ sub.rating eq r ? 'checked' : '' }> ★ ${ r }</label>
						</c:forEach>
					</div>
				</div>

				<div class="field span-2">
					<label for="memo">메모</label>
					<textarea id="memo" name="memo" maxlength="500" placeholder="요금제, 할인 정보, 해지 방법 등"><c:out value="${ sub.memo }" /></textarea>
				</div>
			</div>

			<div class="form-actions">
				<c:choose>
					<c:when test="${ isUpdate }">
						<a href="${ ctx }/sub/detail.do?sno=${ sub.sno }" class="btn btn-ghost">취소</a>
						<button type="submit" class="btn btn-primary">수정 완료</button>
					</c:when>
					<c:otherwise>
						<a href="${ ctx }/sub/list.do" class="btn btn-ghost">취소</a>
						<button type="submit" class="btn btn-primary">등록</button>
					</c:otherwise>
				</c:choose>
			</div>
		</form>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/include/foot.jspf" %>
