--------------------------------------------------------------------------------
-- 구독노트 (정기구독관리) DB 스크립트
--   대상 : Oracle Database 23ai Free (localhost:1521/FREE), 계정 hr/hr
--   실행 : SQL Developer 에서 이 파일 전체 선택 → [스크립트 실행(F5)]
--
--   ※ "DROP TABLE IF EXISTS" 는 23ai 부터 지원합니다.
--     21c XE 이하 버전이면 아래 DROP 3줄을 지우고 실행하세요. (처음 실행이면 지울 필요 없음)
--------------------------------------------------------------------------------

DROP TABLE IF EXISTS t_subscription PURGE;
DROP TABLE IF EXISTS t_member PURGE;
DROP SEQUENCE IF EXISTS seq_t_subscription_sno;

--------------------------------------------------------------------------------
-- 회원 (Data-001)
--   password : PBKDF2 해시 문자열 (평문 저장 X, Sec-002)
--   name     : 요구사항(User-001) 가입 항목에는 없지만 회원 검색(User-008) "이름" 조건을 위해 추가
--   role     : USER(회원) / ADMIN(관리자)
--   VARCHAR2(n CHAR) : 한글 1글자 = 3바이트이므로 "글자 수" 기준으로 길이를 지정
--------------------------------------------------------------------------------
CREATE TABLE t_member (
    id          VARCHAR2(20)        CONSTRAINT pk_t_member PRIMARY KEY,
    password    VARCHAR2(200)       NOT NULL,
    name        VARCHAR2(20 CHAR)   NOT NULL,
    email       VARCHAR2(100)       NOT NULL,
    phone       VARCHAR2(13)        NOT NULL,
    birth_date  DATE                NOT NULL,
    address     VARCHAR2(200 CHAR)  NOT NULL,
    role        VARCHAR2(10)        DEFAULT 'USER' NOT NULL,
    reg_date    DATE                DEFAULT SYSDATE NOT NULL,
    CONSTRAINT ck_t_member_role CHECK (role IN ('USER', 'ADMIN'))
);

--------------------------------------------------------------------------------
-- 구독 (Data-002, Data-003)
--   member_id     : 회원과 연결 (FK) → 로그인 회원 본인의 구독만 조회·수정·삭제
--   amount        : 월 결제 금액(원)
--   next_pay_date : 다음 결제 예정일
--   status        : ACTIVE(구독 중) / CANCEL_PLANNED(해지 예정) / CANCELED(해지 완료)
--   rating        : 만족도 별점 1~5 (미평가 NULL)
--------------------------------------------------------------------------------
CREATE TABLE t_subscription (
    sno            NUMBER(10)          CONSTRAINT pk_t_subscription PRIMARY KEY,
    member_id      VARCHAR2(20)        NOT NULL,
    service_name   VARCHAR2(50 CHAR)   NOT NULL,
    category       VARCHAR2(20 CHAR)   NOT NULL,
    amount         NUMBER(10)          NOT NULL,
    start_date     DATE,
    next_pay_date  DATE                NOT NULL,
    status         VARCHAR2(20)        DEFAULT 'ACTIVE' NOT NULL,
    rating         NUMBER(1),
    memo           VARCHAR2(500 CHAR),
    reg_date       DATE                DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_t_subscription_member FOREIGN KEY (member_id) REFERENCES t_member(id),
    CONSTRAINT ck_t_subscription_amount CHECK (amount >= 0),
    CONSTRAINT ck_t_subscription_status CHECK (status IN ('ACTIVE', 'CANCEL_PLANNED', 'CANCELED')),
    CONSTRAINT ck_t_subscription_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE SEQUENCE seq_t_subscription_sno NOCACHE;

-- 내 구독 목록은 항상 member_id 로 조회하고 다음 결제일 순으로 정렬하므로 복합 인덱스 생성
CREATE INDEX idx_t_subscription_member ON t_subscription(member_id, next_pay_date);

--------------------------------------------------------------------------------
-- 초기 데이터
--   관리자 : admin  / admin1234
--   회원   : user01 / user1234
--   (비밀번호 해시는 kr.ac.kopo.util.PasswordUtil 의 main() 으로 생성한 값)
--   ※ 실제 서비스라면 관리자 비밀번호는 반드시 바꿔야 합니다.
--------------------------------------------------------------------------------
INSERT INTO t_member(id, password, name, email, phone, birth_date, address, role)
VALUES ('admin',
        'pbkdf2$310000$lA0baGpIhiFLwT6CUv0DFw==$4gpgxuFHLs7yLaphp4wIgL3ur5oKpI/Y4fP0Zx2P7r4=',
        '관리자', 'admin@subnote.kr', '010-0000-0000', DATE '1990-01-01', '서울특별시 마포구', 'ADMIN');

INSERT INTO t_member(id, password, name, email, phone, birth_date, address, role)
VALUES ('user01',
        'pbkdf2$310000$NxwqaF1v1O7Y4EJ5K5+Osg==$Wy2Vo+XSJ1sSqyZqEE1CIRfWVB14ZdYpI2vIjeMWEDk=',
        '홍길동', 'hong@example.com', '010-1234-5678', DATE '1998-05-15', '서울특별시 성동구 왕십리로 1', 'USER');

-- 결제일은 실행한 날(SYSDATE) 기준으로 만들어서 D-day 표시를 바로 확인할 수 있게 함
INSERT INTO t_subscription(sno, member_id, service_name, category, amount, start_date, next_pay_date, status, rating, memo)
VALUES (seq_t_subscription_sno.NEXTVAL, 'user01', '넷플릭스', '영상', 17000,
        ADD_MONTHS(TRUNC(SYSDATE), -14) + 2, TRUNC(SYSDATE) + 2, 'ACTIVE', 4, '스탠다드 요금제. 가족 공유 중');

INSERT INTO t_subscription(sno, member_id, service_name, category, amount, start_date, next_pay_date, status, rating, memo)
VALUES (seq_t_subscription_sno.NEXTVAL, 'user01', '유튜브 프리미엄', '영상', 14900,
        ADD_MONTHS(TRUNC(SYSDATE), -6) + 9, TRUNC(SYSDATE) + 9, 'ACTIVE', 5, NULL);

INSERT INTO t_subscription(sno, member_id, service_name, category, amount, start_date, next_pay_date, status, rating, memo)
VALUES (seq_t_subscription_sno.NEXTVAL, 'user01', '멜론', '음악', 10900,
        ADD_MONTHS(TRUNC(SYSDATE), -3) - 1, TRUNC(SYSDATE) - 1, 'ACTIVE', 3, '연말 할인 이벤트 확인하기');

INSERT INTO t_subscription(sno, member_id, service_name, category, amount, start_date, next_pay_date, status, rating, memo)
VALUES (seq_t_subscription_sno.NEXTVAL, 'user01', '쿠팡 와우', '쇼핑', 7890,
        ADD_MONTHS(TRUNC(SYSDATE), -20) + 15, TRUNC(SYSDATE) + 15, 'ACTIVE', 4, NULL);

INSERT INTO t_subscription(sno, member_id, service_name, category, amount, start_date, next_pay_date, status, rating, memo)
VALUES (seq_t_subscription_sno.NEXTVAL, 'user01', 'ChatGPT Plus', 'IT', 29000,
        ADD_MONTHS(TRUNC(SYSDATE), -2) + 5, TRUNC(SYSDATE) + 5, 'CANCEL_PLANNED', 2, '이번 결제 후 해지 예정');

INSERT INTO t_subscription(sno, member_id, service_name, category, amount, start_date, next_pay_date, status, rating, memo)
VALUES (seq_t_subscription_sno.NEXTVAL, 'user01', '밀리의 서재', '생활', 9900,
        ADD_MONTHS(TRUNC(SYSDATE), -8), ADD_MONTHS(TRUNC(SYSDATE), -1), 'CANCELED', 2, '잘 안 봐서 해지함');

COMMIT;

-- 확인
SELECT id, name, role, reg_date FROM t_member;
SELECT sno, member_id, service_name, category, amount, next_pay_date, status, rating FROM t_subscription ORDER BY next_pay_date;
