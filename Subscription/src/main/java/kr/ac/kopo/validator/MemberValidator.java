package kr.ac.kopo.validator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kr.ac.kopo.member.vo.MemberVO;

/**
 * 회원 입력값 서버 측 유효성 검증 (USER-FUNC-01, 04 / Sec-002 비정상 입력값 방어)
 *
 * HTML 의 required, pattern 속성은 사용자가 개발자도구로 지울 수 있으므로
 * 서버에서 반드시 한 번 더 검사한다. 오류 메시지 목록을 반환하고, 비어 있으면 통과.
 */
public class MemberValidator {

	private static final Pattern ID_PATTERN       = Pattern.compile("^[a-z][a-z0-9]{3,19}$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,20}$");
	private static final Pattern EMAIL_PATTERN    = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
	private static final Pattern PHONE_PATTERN    = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");

	/** 회원가입 */
	public static List<String> validateJoin(MemberVO m, String passwordConfirm) {
		List<String> errors = new ArrayList<>();

		if (m.getId().isEmpty()) {
			errors.add("아이디를 입력해 주세요.");
		} else if (!ID_PATTERN.matcher(m.getId()).matches()) {
			errors.add("아이디는 영문 소문자로 시작하는 4~20자의 영문 소문자·숫자 조합이어야 합니다.");
		}

		validateNewPassword(m.getPassword(), passwordConfirm, errors);

		if (m.getBirthDate().isEmpty()) {
			errors.add("생년월일을 입력해 주세요.");
		} else {
			LocalDate birth = parseDate(m.getBirthDate());
			if (birth == null || birth.isAfter(LocalDate.now()) || birth.isBefore(LocalDate.of(1900, 1, 1))) {
				errors.add("생년월일이 올바르지 않습니다.");
			}
		}

		validateProfile(m, errors);
		return errors;
	}

	/** 회원 정보 수정 (newPassword 가 비어 있으면 비밀번호는 변경하지 않음) */
	public static List<String> validateUpdate(MemberVO m, String currentPassword,
											  String newPassword, String newPasswordConfirm) {
		List<String> errors = new ArrayList<>();

		if (currentPassword.isEmpty()) {
			errors.add("본인 확인을 위해 현재 비밀번호를 입력해 주세요.");
		}
		if (!newPassword.isEmpty() || !newPasswordConfirm.isEmpty()) {
			validateNewPassword(newPassword, newPasswordConfirm, errors);
		}
		validateProfile(m, errors);
		return errors;
	}

	/** 이름·연락처·이메일·주소 (가입/수정 공통) */
	private static void validateProfile(MemberVO m, List<String> errors) {
		if (m.getName().isEmpty()) {
			errors.add("이름을 입력해 주세요.");
		} else if (m.getName().length() > 20) {
			errors.add("이름은 20자 이하로 입력해 주세요.");
		}

		if (m.getPhone().isEmpty()) {
			errors.add("핸드폰 번호를 입력해 주세요.");
		} else if (!PHONE_PATTERN.matcher(m.getPhone()).matches()) {
			errors.add("핸드폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)");
		} else {
			m.setPhone(normalizePhone(m.getPhone()));
		}

		if (m.getEmail().isEmpty()) {
			errors.add("이메일을 입력해 주세요.");
		} else if (m.getEmail().length() > 100 || !EMAIL_PATTERN.matcher(m.getEmail()).matches()) {
			errors.add("이메일 형식이 올바르지 않습니다.");
		}

		if (m.getAddress().isEmpty()) {
			errors.add("주소를 입력해 주세요.");
		} else if (m.getAddress().length() > 200) {
			errors.add("주소는 200자 이하로 입력해 주세요.");
		}
	}

	private static void validateNewPassword(String password, String confirm, List<String> errors) {
		if (password.isEmpty()) {
			errors.add("비밀번호를 입력해 주세요.");
		} else if (!PASSWORD_PATTERN.matcher(password).matches()) {
			errors.add("비밀번호는 영문과 숫자를 포함한 8~20자여야 합니다.");
		} else if (!password.equals(confirm)) {
			errors.add("비밀번호 확인이 일치하지 않습니다.");
		}
	}

	/** 아이디 형식만 검사 (AJAX 중복확인용) */
	public static boolean isValidId(String id) {
		return id != null && ID_PATTERN.matcher(id).matches();
	}

	/** 01012345678 → 010-1234-5678 */
	static String normalizePhone(String phone) {
		String digits = phone.replace("-", "");
		int mid = digits.length() == 11 ? 7 : 6;
		return digits.substring(0, 3) + "-" + digits.substring(3, mid) + "-" + digits.substring(mid);
	}

	static LocalDate parseDate(String value) {
		try {
			return LocalDate.parse(value);	// yyyy-MM-dd
		} catch (DateTimeParseException e) {
			return null;
		}
	}
}
