package kr.ac.kopo.exception;

/**
 * DAO 에서 발생한 SQLException 을 감싸는 예외 (RuntimeException)
 *
 * 기존 게시판 DAO 는 catch 에서 printStackTrace() 만 하고 넘어가서
 * DB 오류가 나도 화면에서는 "성공"으로 보이는 문제가 있었다.
 * 예외를 다시 던지면 DispatcherServlet → web.xml error-page(500) 로 이동한다.
 */
public class DataAccessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
