package kr.ac.kopo.member.service;

import java.util.List;

import kr.ac.kopo.member.vo.MemberVO;

public interface MemberService {

	/** 아이디 사용 가능 여부 (중복이 아니면 true) */
	boolean isIdAvailable(String id);

	/** 회원가입 - 평문 비밀번호를 암호화해서 저장. 아이디 중복이면 false (USER-FUNC-01) */
	boolean join(MemberVO member);

	/** 로그인 - 성공 시 비밀번호를 지운 회원 정보, 실패 시 null (USER-FUNC-02, ADMIN-FUNC-01) */
	MemberVO login(String id, String rawPassword);

	/** 회원 상세 (비밀번호 제외) - 없으면 null (USER-FUNC-03) */
	MemberVO getMember(String id);

	/** 본인 확인 */
	boolean checkPassword(String id, String rawPassword);

	/** 회원 정보 수정 - newRawPassword 가 null 이면 비밀번호는 그대로 (USER-FUNC-04) */
	void updateMember(MemberVO member, String newRawPassword);

	/** 회원 탈퇴 - 비밀번호가 맞고 일반 회원일 때만 삭제 (USER-FUNC-05) */
	boolean withdraw(String id, String rawPassword);

	/** 회원 목록/검색 (ADMIN-FUNC-02) */
	List<MemberVO> searchMembers(String searchType, String keyword);

	/** 관리자 강제 탈퇴 - 관리자 계정은 삭제 불가 (ADMIN-FUNC-03) */
	boolean forceDelete(String targetId);
}
