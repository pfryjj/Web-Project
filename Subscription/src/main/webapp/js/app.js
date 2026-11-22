/*
 * 구독노트 공통 스크립트
 *  1) data-confirm 속성이 있는 폼 : 전송 전에 확인창
 *  2) data-autosubmit 속성이 있는 select : 값을 바꾸면 바로 전송 (상태 변경, 정렬 등)
 *  3) 회원가입 아이디 중복 확인 (AJAX - fetch)
 */
document.addEventListener('DOMContentLoaded', function () {

	const ctx = document.body.dataset.ctx || '';

	// 1) 삭제·탈퇴 등 되돌릴 수 없는 작업은 한 번 더 확인
	document.addEventListener('submit', function (e) {
		const message = e.target.dataset.confirm;
		if (message && !confirm(message)) {
			e.preventDefault();
		}
	});

	// 2) select 변경 시 자동 전송
	document.querySelectorAll('select[data-autosubmit]').forEach(function (select) {
		select.addEventListener('change', function () {
			select.form.submit();
		});
	});

	// 3) 아이디 중복 확인
	const idInput = document.querySelector('#joinId');
	const checkBtn = document.querySelector('#checkIdBtn');
	const idHint = document.querySelector('#idHint');

	if (idInput && checkBtn && idHint) {
		const defaultHint = idHint.textContent;
		const showHint = function (message, ok) {
			idHint.textContent = message;
			idHint.className = 'hint ' + (ok ? 'ok' : 'bad');
		};

		checkBtn.addEventListener('click', function () {
			const id = idInput.value.trim().toLowerCase();
			if (id === '') {
				showHint('아이디를 입력해 주세요.', false);
				idInput.focus();
				return;
			}

			fetch(ctx + '/member/checkId.do?id=' + encodeURIComponent(id))
				.then(function (res) { return res.json(); })
				.then(function (data) { showHint(data.message, data.available); })
				.catch(function () { showHint('중복 확인 중 오류가 발생했습니다.', false); });
		});

		// 아이디를 다시 고치면 확인 결과 초기화
		idInput.addEventListener('input', function () {
			idHint.textContent = defaultHint;
			idHint.className = 'hint';
		});

	}
});
