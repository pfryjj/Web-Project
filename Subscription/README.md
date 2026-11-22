# 구독노트 (Subscription-MVC) — 정기구독관리 사이트

Mission-WEB-MVC 에서 만든 **직접 구현한 MVC 프레임워크(DispatcherServlet + HandlerMapping + Controller)** 구조 그대로
회원·구독·관리자 기능을 구현한 프로젝트입니다. (Java 21 · Tomcat 11 · Oracle 23ai Free · JSP/JSTL 3.0)

요구사항 불일치 검출 보고서 : https://claude.ai/artifact/PE8ejR8p4nFoW55KrqpFS4
(코드 주석의 `INC-xx`, `USER-FUNC-xx` 는 보고서·기능명세서의 ID 입니다)

---

## 1. 실행 방법

1. **프로젝트 가져오기** : 압축을 풀고 Eclipse 에서 `File → Import → General → Existing Projects into Workspace`
   → `Subscription-MVC` 폴더 선택 (web-workspace 폴더 안에 두고 가져오면 편합니다)
2. **DB 만들기** : SQL Developer 에서 hr 계정으로 접속 → `sql/subscription.sql` 열기 → 전체 실행(F5)
3. **서버 등록** : Servers 탭 → `Tomcat v11.0 Server at localhost` 우클릭 → Add and Remove → `Subscription-MVC` 추가 → Start
4. **접속** : http://localhost:8080/Subscription-MVC/

| 계정 | 아이디 / 비밀번호 | 비고 |
| --- | --- | --- |
| 관리자 | `admin` / `admin1234` | 회원 관리 화면으로 이동 |
| 회원 | `user01` / `user1234` | 샘플 구독 6개 (결제일은 SQL 실행일 기준) |

- DB 접속 정보가 다르면 `kr.ac.kopo.util.ConnectionFactory` 의 URL·계정만 바꾸면 됩니다.
- Oracle 21c XE 이하라면 `subscription.sql` 맨 위의 `DROP ... IF EXISTS` 3줄을 지우고 실행하세요.
- 라이브러리(ojdbc11, JSTL 3.0)는 `WEB-INF/lib` 에 들어 있습니다. (Mission-WEB-MVC 와 같은 파일)

---

## 2. 요청 처리 흐름

```
브라우저
  │  *.do 요청
  ▼
AuthFilter ──(비로그인/권한 없음)──▶ 로그인 화면·메인으로 redirect, 또는 403
  ▼
CsrfFilter ──(POST 인데 토큰 불일치)──▶ 메인으로 redirect
  ▼
DispatcherServlet ── HandlerMapping 에서 URI 로 Controller 찾기 (없으면 404, POST 전용을 GET 으로 부르면 405)
  ▼
XxxController ──▶ XxxService ──▶ XxxDAO ──▶ Oracle (t_member, t_subscription)
  │
  └─ 반환값 "/WEB-INF/jsp/...jsp" → forward
           "redirect:/sub/list.do" → sendRedirect (PRG 패턴)
           null                    → 컨트롤러가 직접 응답 (AJAX JSON)
```

```
src/main/java/kr/ac/kopo
 ├─ framework     DispatcherServlet, HandlerMapping
 ├─ controller    Controller(인터페이스), MainController
 │   ├─ member    회원가입·로그인·로그아웃·마이페이지·수정·탈퇴 (11개)
 │   ├─ sub       구독 목록·등록·상세·수정·삭제·상태변경·결제일갱신 (9개 + SubRequestHelper)
 │   └─ admin     회원 목록·상세·강제탈퇴 (3개)
 ├─ member        vo / dao / service
 ├─ subscription  vo / dao / service
 ├─ filter        AuthFilter(인증·인가), CsrfFilter(요청 위조 방어)
 ├─ listener      AppInitListener (카테고리·상태 코드를 application 영역에 등록)
 ├─ validator     MemberValidator, SubscriptionValidator (서버 측 입력값 검증)
 ├─ util          ConnectionFactory, JDBCClose, PasswordUtil, WebUtil
 └─ exception     DataAccessException
src/main/webapp
 ├─ WEB-INF/jsp   member/ sub/ admin/ error/ include/(head·foot·errors·dday·stars .jspf)
 ├─ css/style.css, js/app.js, index.jsp
sql/subscription.sql
```

---

## 3. URI 정보

| 기능 ID | 기능 | URI | 방식 | Controller | 응답 |
| --- | --- | --- | --- | --- | --- |
| - | 메인(로그인 상태별 분기) | /main.do | GET | MainController | member/loginForm.jsp 또는 redirect |
| USER-FUNC-01 | 회원가입 화면 | /member/joinForm.do | GET | JoinFormController | member/joinForm.jsp |
| USER-FUNC-01 | 회원가입 처리 | /member/join.do | POST | JoinController | redirect:/member/loginForm.do |
| - | 아이디 중복 확인 (AJAX) | /member/checkId.do | GET | CheckIdController | JSON |
| USER-FUNC-02, ADMIN-FUNC-01 | 로그인 화면 (회원·관리자 공용) | /member/loginForm.do | GET | LoginFormController | member/loginForm.jsp |
| USER-FUNC-02, ADMIN-FUNC-01 | 로그인 처리 | /member/login.do | POST | LoginController | redirect:/sub/list.do 또는 /admin/memberList.do |
| COMMON-FUNC-01 | 로그아웃 | /member/logout.do | POST | LogoutController | redirect:/main.do |
| USER-FUNC-03 | 마이페이지 | /member/myPage.do | GET | MyPageController | member/myPage.jsp |
| USER-FUNC-04 | 회원 정보 수정 화면 | /member/updateForm.do | GET | UpdateFormController | member/updateForm.jsp |
| USER-FUNC-04 | 회원 정보 수정 처리 | /member/update.do | POST | UpdateController | redirect:/member/myPage.do |
| USER-FUNC-05 | 회원 탈퇴 화면 | /member/withdrawForm.do | GET | WithdrawFormController | member/withdrawForm.jsp |
| USER-FUNC-05 | 회원 탈퇴 처리 | /member/withdraw.do | POST | WithdrawController | redirect:/main.do |
| USER-FUNC-07, 11, 12 | 내 구독 목록·검색·필터 | /sub/list.do | GET | SubListController | sub/list.jsp |
| USER-FUNC-06 | 구독 등록 화면 | /sub/writeForm.do | GET | SubWriteFormController | sub/form.jsp |
| USER-FUNC-06 | 구독 등록 처리 | /sub/write.do | POST | SubWriteController | redirect:/sub/list.do |
| USER-FUNC-08 | 구독 상세 | /sub/detail.do?sno= | GET | SubDetailController | sub/detail.jsp |
| USER-FUNC-09 | 구독 수정 화면 | /sub/updateForm.do?sno= | GET | SubUpdateFormController | sub/form.jsp |
| USER-FUNC-09 | 구독 수정 처리 | /sub/update.do | POST | SubUpdateController | redirect:/sub/detail.do |
| USER-FUNC-10 | 구독 삭제 | /sub/delete.do | POST | SubDeleteController | redirect:/sub/list.do |
| USER-FUNC-13 | 구독 상태 변경 | /sub/status.do | POST | SubStatusController | redirect: 목록 또는 상세 |
| (신규, Sub-009) | 결제 완료 → 다음 결제일 +1개월 | /sub/renew.do | POST | SubRenewController | redirect: 목록 또는 상세 |
| ADMIN-FUNC-02 | 회원 목록·검색 | /admin/memberList.do | GET | AdminMemberListController | admin/memberList.jsp |
| (보완, User-004) | 회원 상세 (관리자) | /admin/memberDetail.do?id= | GET | AdminMemberDetailController | admin/memberDetail.jsp |
| ADMIN-FUNC-03 | 회원 강제 탈퇴 | /admin/memberDelete.do | POST | AdminMemberDeleteController | redirect:/admin/memberList.do |

---

## 4. 사용한 개념

**수업에서 배운 개념** (Mission-WEB-MVC 와 같은 방식)
- Front Controller(DispatcherServlet `*.do`) + HandlerMapping(HashMap) + Controller 인터페이스
- Service / DAO / VO 계층, 인터페이스 + Impl
- JDBC 5단계, ConnectionFactory, JDBCClose, PreparedStatement, 시퀀스(`seq_t_subscription_sno`)
- JSP + EL + JSTL (`c:forEach`, `c:if`, `c:choose`, `c:out`), 공유 영역(request/session/application), HttpSession 로그인

**새로 사용한 개념** (필요해서 추가한 것, 각 파일 주석에 설명)

| 개념 | 왜 필요한가 | 보는 곳 |
| --- | --- | --- |
| `redirect:` 반환 + PRG 패턴 | 등록 후 새로고침해도 중복 등록되지 않게 | DispatcherServlet |
| 서블릿 필터 + web.xml 실행 순서 | 로그인·관리자 검사를 컨트롤러마다 반복하지 않게 (유스케이스의 «include» 사용자 인증) | AuthFilter, web.xml |
| CSRF 토큰 | 다른 사이트가 몰래 삭제·수정 요청을 보내는 공격 방어 | CsrfFilter, 모든 POST 폼 |
| POST 전용 URI | 링크 클릭·주소 입력만으로 삭제되지 않게 (GET 이면 405) | HandlerMapping `post(...)` |
| 비밀번호 해시 (PBKDF2 + salt) | 비밀번호 암호화 저장 (Sec-002), JDK 기본 기능만 사용 | PasswordUtil |
| 세션 ID 재발급 | 로그인 시 세션 고정 공격 방지 | LoginController `changeSessionId()` |
| 트랜잭션 (commit / rollback) | 탈퇴 시 구독 삭제와 회원 삭제가 함께 성공하거나 함께 취소 | MemberDAOImpl.deleteWithSubscriptions |
| 동적 SQL + 정렬 화이트리스트, LIKE 이스케이프 | 검색 조건이 있을 때만 where 절 추가, SQL 인젝션 방지 | SubscriptionDAOImpl.findByCondition |
| 서버 측 유효성 검증 | HTML required 는 우회 가능하므로 서버에서 다시 검사 | validator 패키지 |
| 플래시 메시지 | redirect 후 다음 화면에 안내 문구 한 번 표시 | WebUtil.flash, head.jspf |
| 리스너 (@WebListener) | 서버 시작 시 공통 코드(카테고리·상태)를 한 번 등록 | AppInitListener |
| JSP 정적 include (.jspf), WEB-INF 아래 JSP | 공통 상단/하단 재사용, JSP 직접 접근 차단 | WEB-INF/jsp/include |
| `fmt:formatNumber`, `fn:escapeXml` | 금액 콤마 표시, 입력값 출력 시 XSS 방지 | sub/list.jsp 등 |
| AJAX (fetch) + JSON 응답 | 페이지 이동 없이 아이디 중복 확인 | CheckIdController, js/app.js |
| error-page | 403·404·405·500 을 안내 화면으로 | web.xml, error/error.jsp |

---

## 5. 요구사항 불일치를 구현에서 처리한 방식 (요약)

- 구독 수정·삭제 : 요구사항명세서 Sub-004·005 기준으로 구현 (정의서의 Sub-004 누락, Sub-005 오기 — INC-01)
- 관리자 회원 상세 조회 화면 추가 (유스케이스·기능명세서 누락 — INC-02)
- 로그인 화면 하나 + 권한별 이동 (INC-03, INC-10)
- 결제 일정 관리 : D-day, 7일 이내 결제 예정, 이번 달 남은 결제, 결제 완료 버튼 (기능명세서에 Sub-009 대응 기능 없음 — INC-04)
- 회원가입에 '이름' 추가 → 관리자가 아이디·이름·이메일로 검색 (INC-06)
- 월 고정 지출은 기능명세서대로 '구독 중'만 합산 (INC-09)
- 회원 정보 수정 : 현재 비밀번호(필수) + 새 비밀번호(선택) (INC-16)

전체 18건과 근거는 보고서를 참고하세요.

---

## 6. 검증한 내용과 남은 확인 사항

- Java 소스 전체 컴파일 (경고 0건)
- DB 없이 실행 가능한 동작 46건 자동 테스트 통과 : 필터의 로그인·관리자·CSRF 처리, 404·405, redirect 경로,
  입력값 검증, 잘못된 쿼리스트링 차단 등
- JSP 12개 정적 검사 통과 : JSTL 태그·속성 이름, EL 프로퍼티(getter 존재), 링크·폼의 URI 매핑, POST 폼의 CSRF 토큰
- 실제 Tomcat + Oracle 환경에서는 실행해 보지 못했습니다. 처음 실행할 때 아래 순서로 확인해 주세요.
  1. `subscription.sql` 실행 후 마지막 SELECT 두 개에 회원 2명, 구독 6건이 나오는지
  2. `user01` 로그인 → 목록 상단에 월 고정 지출 50,690원이 나오는지
  3. 검색창에 한글(예: 넷플릭스) 입력 후 검색이 되는지
