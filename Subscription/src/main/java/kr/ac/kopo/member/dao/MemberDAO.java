package kr.ac.kopo.member.dao;

import java.util.List;

import kr.ac.kopo.member.vo.MemberVO;

public interface MemberDAO {

	/** 회원 저장 (password 는 이미 암호화된 값). 아이디가 중복(PK 위반)이면 false */
	boolean insert(MemberVO member);

	/** 아이디로 회원 조회 (암호화된 비밀번호 포함) - 없으면 null */
	MemberVO findById(String id);

	/** 아이디 중복 여부 */
	boolean existsById(String id);

	/** 이름·이메일·연락처·주소 수정. encodedPassword 가 null 이 아니면 비밀번호도 변경 */
	int update(MemberVO member, String encodedPassword);

	/** 회원의 구독 데이터와 회원 정보를 하나의 트랜잭션으로 삭제. 삭제된 회원 수 반환 */
	int deleteWithSubscriptions(String id);

	/**
	 * 회원 목록 (관리자)
	 * @param searchType id | name | email
	 * @param keyword    비어 있으면 전체 조회
	 */
	List<MemberVO> findAll(String searchType, String keyword);
}
