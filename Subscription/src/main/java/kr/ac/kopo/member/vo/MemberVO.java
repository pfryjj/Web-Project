package kr.ac.kopo.member.vo;

import java.io.Serializable;

/**
 * t_member 테이블 1행
 * 세션에 저장되므로 Serializable 구현 (톰캣 재시작 시 세션 직렬화)
 */
public class MemberVO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ROLE_USER  = "USER";
	public static final String ROLE_ADMIN = "ADMIN";

	private String id;
	private String password;	// 암호화된 값. 세션에 넣기 전에 null 로 지운다.
	private String name;
	private String email;
	private String phone;
	private String birthDate;	// yyyy-MM-dd
	private String address;
	private String role;
	private String regDate;		// yyyy-MM-dd
	private int    subCount;	// 관리자 회원목록 : 등록한 구독 수

	public MemberVO() {
	}

	/** EL 에서 ${ loginUser.admin } 으로 사용 */
	public boolean isAdmin() {
		return ROLE_ADMIN.equals(role);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public int getSubCount() {
		return subCount;
	}

	public void setSubCount(int subCount) {
		this.subCount = subCount;
	}

	@Override
	public String toString() {
		return "MemberVO [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone
				+ ", birthDate=" + birthDate + ", role=" + role + ", regDate=" + regDate + "]";
	}
}
