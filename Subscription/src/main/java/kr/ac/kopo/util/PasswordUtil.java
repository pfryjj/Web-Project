package kr.ac.kopo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 비밀번호 암호화 (Sec-002) - JDK 기본 제공 PBKDF2 사용, 외부 라이브러리 불필요
 *
 * 저장 형식 : pbkdf2$반복횟수$솔트(Base64)$해시(Base64)
 *  - 솔트(salt) : 회원마다 다른 난수 → 같은 비밀번호라도 저장값이 달라짐 (레인보우 테이블 방어)
 *  - 반복횟수   : 해시를 일부러 느리게 만들어 무차별 대입 공격 비용을 높임
 *
 * 비밀번호는 "복호화"하지 않는다. 로그인 시 입력값을 같은 방식으로 해시해서 비교한다.
 */
public class PasswordUtil {

	private static final String ALGORITHM  = "PBKDF2WithHmacSHA256";
	private static final int    ITERATIONS = 310000;
	private static final int    SALT_BYTES = 16;
	private static final int    KEY_BITS   = 256;

	private static final SecureRandom RANDOM = new SecureRandom();

	/** 회원가입·비밀번호 변경 시 : 평문 → 저장용 문자열 */
	public static String hash(String rawPassword) {
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS);

		Base64.Encoder enc = Base64.getEncoder();
		return "pbkdf2$" + ITERATIONS + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(hash);
	}

	/** 로그인·본인확인 시 : 입력한 평문과 DB 저장값 비교 */
	public static boolean matches(String rawPassword, String stored) {
		if (rawPassword == null || stored == null) {
			return false;
		}
		String[] parts = stored.split("\\$");
		if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
			return false;
		}
		try {
			int iterations = Integer.parseInt(parts[1]);
			byte[] salt     = Base64.getDecoder().decode(parts[2]);
			byte[] expected = Base64.getDecoder().decode(parts[3]);
			byte[] actual   = pbkdf2(rawPassword.toCharArray(), salt, iterations);
			// 비교 시간이 일정한 메서드 사용 (타이밍 공격 방지)
			return MessageDigest.isEqual(expected, actual);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
			return factory.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("비밀번호 암호화 실패", e);
		}
	}

	/** sql/subscription.sql 의 초기 계정 해시를 만들 때 사용 : java PasswordUtil admin1234 */
	public static void main(String[] args) {
		for (String pw : args) {
			System.out.println(pw + " => " + hash(pw));
		}
	}
}
