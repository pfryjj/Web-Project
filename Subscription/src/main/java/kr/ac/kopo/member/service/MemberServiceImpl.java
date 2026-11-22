package kr.ac.kopo.member.service;

import java.util.List;

import kr.ac.kopo.member.dao.MemberDAO;
import kr.ac.kopo.member.dao.MemberDAOImpl;
import kr.ac.kopo.member.vo.MemberVO;
import kr.ac.kopo.util.PasswordUtil;

public class MemberServiceImpl implements MemberService {

	private MemberDAO memberDao;

	public MemberServiceImpl() {
		memberDao = new MemberDAOImpl();
	}

	@Override
	public boolean isIdAvailable(String id) {
		return !memberDao.existsById(id);
	}

	@Override
	public boolean join(MemberVO member) {
		if (memberDao.existsById(member.getId())) {
			return false;
		}
		member.setPassword(PasswordUtil.hash(member.getPassword()));
		return memberDao.insert(member);
	}

	@Override
	public MemberVO login(String id, String rawPassword) {
		MemberVO member = memberDao.findById(id);
		if (member == null || !PasswordUtil.matches(rawPassword, member.getPassword())) {
			return null;	// 아이디 없음 / 비밀번호 불일치를 구분하지 않는다 (아이디 추측 방지)
		}
		member.setPassword(null);	// 세션에는 비밀번호를 넣지 않는다
		return member;
	}

	@Override
	public MemberVO getMember(String id) {
		MemberVO member = memberDao.findById(id);
		if (member != null) {
			member.setPassword(null);
		}
		return member;
	}

	@Override
	public boolean checkPassword(String id, String rawPassword) {
		MemberVO member = memberDao.findById(id);
		return member != null && PasswordUtil.matches(rawPassword, member.getPassword());
	}

	@Override
	public void updateMember(MemberVO member, String newRawPassword) {
		String encoded = (newRawPassword == null) ? null : PasswordUtil.hash(newRawPassword);
		memberDao.update(member, encoded);
	}

	@Override
	public boolean withdraw(String id, String rawPassword) {
		MemberVO member = memberDao.findById(id);
		if (member == null || member.isAdmin()) {
			return false;
		}
		if (!PasswordUtil.matches(rawPassword, member.getPassword())) {
			return false;
		}
		return memberDao.deleteWithSubscriptions(id) == 1;
	}

	@Override
	public List<MemberVO> searchMembers(String searchType, String keyword) {
		return memberDao.findAll(searchType, keyword);
	}

	@Override
	public boolean forceDelete(String targetId) {
		MemberVO target = memberDao.findById(targetId);
		if (target == null || target.isAdmin()) {
			return false;
		}
		return memberDao.deleteWithSubscriptions(targetId) == 1;
	}
}
