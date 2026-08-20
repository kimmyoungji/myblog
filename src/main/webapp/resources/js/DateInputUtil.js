var DateInputUtil = (function () {

    /*
     * =====================================================================
     * DateInputUtil
     * =====================================================================
     *
     * 날짜 입력 전용 유틸리티.
     *
     * [핵심 개념]
     *
     * 1. 사용자가 실제로 입력한 값(raw)과
     *    화면에 보여주는 값(text)을 분리해서 관리한다.
     *
     *    예)
     *      raw  : 20265
     *      text : 2026-05
     *
     *    raw는 사용자가 실제로 입력한 숫자만 보관한다.
     *    '-', 자동으로 추가되는 '0' 등은 raw에 포함하지 않는다.
     *
     *
     * 2. sourceMap은 화면의 각 문자가
     *    raw의 몇 번째 문자에서 왔는지를 기록한다.
     *
     *    예)
     *      raw       : 2 0 2 6 5
     *      raw index : 0 1 2 3 4
     *
     *      text      : 2 0 2 6 - 0 5
     *      sourceMap : 0 1 2 3 null null 4
     *
     *    null은 사용자가 입력한 문자가 아니라
     *    화면 표시를 위해 자동으로 추가된 문자라는 의미다.
     *
     *    이 정보는 화면상의 커서 위치와
     *    raw상의 위치를 서로 변환할 때 사용한다.
     *
     *
     * 3. 화면에 보이는 input.value를 직접 편집하지 않고,
     *    항상 raw를 먼저 수정한 뒤 render()를 통해 화면을 다시 그린다.
     *
     *    입력
     *      ↓
     *    raw 수정
     *      ↓
     *    createDisplayState()
     *      ↓
     *    YYYY-MM-DD 형태로 화면 출력
     *
     * =====================================================================
     */


    /*
     * raw 값을 화면 표시용 상태 객체로 변환한다.
     *
     * 반환값 예)
     *
     * createDisplayState('20265')
     *
     * {
     *     raw: '20265',
     *     text: '2026-05',
     *     sourceMap: [0, 1, 2, 3, null, null, 4]
     * }
     *
     * sourceMap은 화면의 각 문자가 raw의 어느 위치에서 왔는지를 나타낸다.
     */
    function createDisplayState(rawValue) {

        /*
         * 숫자가 아닌 문자는 제거하고 최대 8자리까지만 사용한다.
         *
         * YYYYMMDD = 최대 8자리
         */
        var raw = String(rawValue == null ? '' : rawValue)
            .replace(/\D/g, '')
            .slice(0, 8);


        /*
         * chars
         *   화면에 출력할 문자 배열
         *
         * sourceMap
         *   chars의 각 문자가 raw의 몇 번째 문자에서 왔는지 기록
         */
        var chars = [];
        var sourceMap = [];


        /*
         * raw에 실제로 존재하는 문자를 화면에 추가한다.
         *
         * 예)
         * raw = '20265'
         *
         * addRaw(4)
         *
         * chars     → '5' 추가
         * sourceMap → 4 추가
         */
        function addRaw(index) {
            chars.push(raw.charAt(index));
            sourceMap.push(index);
        }


        /*
         * 화면 표시를 위해 자동 생성한 문자를 추가한다.
         *
         * '-', 자동으로 보완한 '0' 등이 해당한다.
         *
         * raw에는 존재하지 않는 문자이므로
         * sourceMap에는 null을 기록한다.
         */
        function addFormat(char) {
            chars.push(char);
            sourceMap.push(null);
        }


        /*
         * 1~4자리
         *
         * 그대로 표시한다.
         *
         * 예)
         * 2    → 2
         * 20   → 20
         * 202  → 202
         * 2026 → 2026
         */
        if (raw.length <= 4) {

            for (var i = 0; i < raw.length; i++) {
                addRaw(i);
            }
        }


        /*
         * 5자리
         *
         * YYYYM → YYYY-0M
         *
         * 예)
         * 20265 → 2026-05
         *
         * 여기서 '-'와 월 앞의 '0'은
         * 사용자가 입력하지 않은 화면 표시용 문자다.
         */
        else if (raw.length === 5) {

            for (var i = 0; i < 4; i++) {
                addRaw(i);
            }

            addFormat('-');
            addFormat('0');
            addRaw(4);
        }


        /*
         * 6자리
         *
         * YYYYMM → YYYY-MM
         *
         * 예)
         * 202608 → 2026-08
         */
        else if (raw.length === 6) {

            for (var i = 0; i < 4; i++) {
                addRaw(i);
            }

            addFormat('-');
            addRaw(4);
            addRaw(5);
        }


        /*
         * 7자리
         *
         * YYYYMMd → YYYY-MM-0d
         *
         * 예)
         * 2026085 → 2026-08-05
         */
        else if (raw.length === 7) {

            for (var i = 0; i < 4; i++) {
                addRaw(i);
            }

            addFormat('-');
            addRaw(4);
            addRaw(5);

            addFormat('-');
            addFormat('0');
            addRaw(6);
        }


        /*
         * 8자리
         *
         * YYYYMMDD → YYYY-MM-DD
         *
         * 예)
         * 20260814 → 2026-08-14
         */
        else {

            for (var i = 0; i < 4; i++) {
                addRaw(i);
            }

            addFormat('-');
            addRaw(4);
            addRaw(5);

            addFormat('-');
            addRaw(6);
            addRaw(7);
        }


        return {
            raw: raw,
            text: chars.join(''),
            sourceMap: sourceMap
        };
    }


    /*
     * =====================================================================
     * 화면 커서 위치 → raw 위치 변환
     * =====================================================================
     *
     * 화면에는 '-', 자동 생성된 '0' 등이 존재하지만
     * raw에는 이러한 문자가 존재하지 않는다.
     *
     * 따라서 input.selectionStart 값을
     * raw의 위치로 바로 사용할 수 없다.
     *
     * 예)
     *
     * text : 2026-0|5
     * raw  : 2026|5
     *
     * 화면상의 위치는 6이지만
     * 실제 raw상의 위치는 4이다.
     *
     * sourceMap에서 null이 아닌 문자만 세어서
     * raw 위치를 계산한다.
     */
    function getRawPosition(state, displayPosition) {

        var count = 0;

        for (var i = 0; i < displayPosition; i++) {

            /*
             * null이 아니라면
             * 실제 raw에서 온 문자이므로 카운트한다.
             */
            if (state.sourceMap[i] != null) {
                count++;
            }
        }

        return count;
    }


    /*
     * =====================================================================
     * raw 위치 → 화면 커서 위치 변환
     * =====================================================================
     *
     * raw를 수정한 뒤 화면을 다시 그리면
     * '-' 또는 자동 '0'이 새로 생길 수 있다.
     *
     * 따라서 raw 기준 커서 위치를
     * 다시 화면 기준 위치로 변환해야 한다.
     *
     * 예)
     *
     * raw : 20265|
     *
     * 화면을 다시 그리면
     *
     * text : 2026-05|
     */
    function getDisplayPosition(state, rawPosition) {

        /*
         * raw의 마지막 위치라면
         * 화면에서도 맨 마지막에 커서를 둔다.
         */
        if (rawPosition >= state.raw.length) {
            return state.text.length;
        }


        /*
         * sourceMap에서 해당 raw 위치를 가진
         * 화면상의 index를 찾는다.
         */
        for (var i = 0; i < state.sourceMap.length; i++) {

            if (state.sourceMap[i] === rawPosition) {
                return i;
            }
        }


        /*
         * 찾지 못한 경우 안전하게 화면 끝으로 이동
         */
        return state.text.length;
    }


    /*
     * =====================================================================
     * raw 값을 기준으로 input 화면을 다시 그린다.
     * =====================================================================
     *
     * 이 유틸리티에서는 input.value를 직접 부분 수정하지 않는다.
     *
     * 항상
     *
     * raw 변경
     *   ↓
     * render()
     *   ↓
     * 화면 전체 재생성
     *
     * 방식으로 처리한다.
     */
    function render(input, rawValue, rawCursor, triggerInput) {
		
		console.log("render: ")
		console.log("input: ", input);
		console.log("rawCursor: ", rawCursor);
		console.log("triggerInput: ", triggerInput);
		

        /*
         * raw 값을 화면 표시용 상태로 변환
         */
        var state = createDisplayState(rawValue);


        /*
         * input DOM 객체에 현재 상태를 저장한다.
         *
         * 외부에서 실제 값이 필요한 경우에는
         *
         * DateInputUtil.getValue(input)
         *
         * 을 통해 state.raw를 반환한다.
         */
        input._dateInputState = state;


        /*
         * 사용자에게 보여줄 값
         */
        input.value = state.text;


        /*
         * raw 기준 커서 위치가 전달된 경우
         * 화면 기준 위치로 변환해서 커서를 복원한다.
         */
        if (rawCursor != null) {

            var cursor =
                getDisplayPosition(state, rawCursor);

            input.setSelectionRange(cursor, cursor);
        }


        /*
         * beforeinput에서 기본 입력 동작을 preventDefault() 했기 때문에
         * 브라우저의 기본 input 이벤트가 발생하지 않는다.
         *
         * 필요할 경우 직접 input 이벤트를 발생시킨다.
         *
         * 예)
         *
         * input.addEventListener('input', function () {
         *     DateInputUtil.getValue(this);
         * });
         */
        if (triggerInput) {

            input.dispatchEvent(
                new Event('input', { bubbles: true })
            );
        }
    }


    /*
     * =====================================================================
     * 외부에서 전달된 날짜 값을 화면 표시용 형태로 정규화한다.
     * =====================================================================
     *
     * DB 또는 기존 데이터에서 여러 날짜 형식이 전달될 수 있으므로
     * DateInputUtil에서 사용할 수 있는 형태로 맞춘다.
     *
     * 지원 예)
     *
     * 26.02       → 2026-02
     * 2026        → 2026
     * 202608      → 2026-08
     * 2026.08     → 2026-08
     * 2026-08     → 2026-08
     * 20260814    → 2026-08-14
     * 2026.08.14  → 2026-08-14
     * 2026-08-14  → 2026-08-14
     */
    function normalize(value) {

        if (!value) {
            return '';
        }

        value = String(value).trim();

        /*
         * 비교하기 쉽도록 숫자만 추출
         */
        var digits = value.replace(/\D/g, '');


        /*
         * 26.02 → 2026-02
         *
         * 숫자는 4자리이지만 원본에 '.', '-' 등
         * 숫자가 아닌 문자가 포함되어 있으면 YY.MM 형태로 판단한다.
         */
        if (digits.length === 4 && /\D/.test(value)) {

            return '20'
                + digits.slice(0, 2)
                + '-'
                + digits.slice(2, 4);
        }


        /*
         * YYYY
         *
         * 예)
         * 2026
         */
        if (digits.length === 4) {
            return digits;
        }


        /*
         * YYYYMM
         *
         * 예)
         * 202608
         * 2026.08
         * 2026-08
         *
         * 모두 YYYY-MM 형태로 통일
         */
        if (digits.length === 6) {

            return digits.slice(0, 4)
                + '-'
                + digits.slice(4, 6);
        }


        /*
         * YYYYMMDD
         *
         * 모두 YYYY-MM-DD 형태로 통일
         */
        if (digits.length === 8) {

            return digits.slice(0, 4)
                + '-'
                + digits.slice(4, 6)
                + '-'
                + digits.slice(6, 8);
        }


        /*
         * 위 규칙에 해당하지 않는 경우 원본 유지
         */
        return value;
    }


    /*
     * =====================================================================
     * input에 날짜 입력 기능을 적용한다.
     * =====================================================================
     *
     * 사용 예)
     *
     * var input = document.querySelector('#dateInput');
     * DateInputUtil.bind(input);
     *
     * bind 이후에는
     *
     * - 숫자 입력
     * - Backspace
     * - Delete
     * - 영역 선택 후 삭제
     * - 붙여넣기
     *
     * 를 DateInputUtil이 직접 처리한다.
     */
    function bind(input) {


        /*
         * -------------------------------------------------------------
         * 초기값 설정
         * -------------------------------------------------------------
         *
         * input에 기존 value가 존재할 수 있으므로
         * 먼저 normalize한 뒤 DateInputUtil 상태를 만든다.
         */
        var normalized = normalize(input.value);

        input._dateInputState =
            createDisplayState(
                normalized.replace(/\D/g, '')
            );

        input.value =
            input._dateInputState.text;


        /*
         * -------------------------------------------------------------
         * 키보드 입력 처리
         * -------------------------------------------------------------
         *
         * beforeinput을 사용하는 이유:
         *
         * 브라우저가 실제 input.value를 변경하기 전에
         * 입력/삭제 동작을 가로채기 위해서다.
         *
         * 기본 동작은 preventDefault()로 막고,
         * raw를 직접 수정한 뒤 render()한다.
         */
        input.addEventListener(
            'beforeinput',
            function (e) {

                var state =
                    input._dateInputState;


                /*
                 * 화면 기준 selection 위치
                 *
                 * 선택 영역이 없다면 start === end
                 */
                var start =
                    input.selectionStart;
				
				console.log("input.selectionStart: ", input.selectionStart);
					
                var end =
                    input.selectionEnd;

				console.log("input.selectionEnd: ", input.selectionEnd);
					
                /*
                 * 화면상의 selection 위치를
                 * 실제 raw 위치로 변환
                 */
                var rawStart =
                    getRawPosition(
                        state,
                        start
                    );

                var rawEnd =
                    getRawPosition(
                        state,
                        end
                    );


                /*
                 * =====================================================
                 * 숫자 입력
                 * =====================================================
                 */
                if (e.inputType === 'insertText') {

                    /*
                     * 브라우저가 input.value를 직접 수정하지 못하도록 한다.
                     */
                    e.preventDefault();


                    /*
                     * 입력 문자에서 숫자만 사용한다.
                     *
                     * 숫자가 아닌 문자 입력은 무시한다.
                     */
                    var inserted =
                        String(
                            e.data == null
                                ? ''
                                : e.data
                        )
                        .replace(/\D/g, '');


                    if (!inserted) {
                        return;
                    }


                    /*
                     * 현재 선택 영역을 입력된 숫자로 교체한다.
                     *
                     * 선택 영역이 없다면 rawStart === rawEnd이므로
                     * 현재 커서 위치에 새 숫자가 삽입된다.
                     */
                    var raw =
                        state.raw.slice(
                            0,
                            rawStart
                        )
                        + inserted
                        + state.raw.slice(
                            rawEnd
                        );


                    /*
                     * YYYYMMDD 최대 8자리
                     */
                    raw = raw.slice(0, 8);


                    /*
                     * 입력된 숫자 바로 뒤에
                     * 커서를 위치시키기 위한 raw 기준 위치
                     */
                    var cursor =
                        Math.min(
                            rawStart
                                + inserted.length,
                            raw.length
                        );
					console.log("cursor: ", cursor);
					console.log("rawStart + insered.length: ", rawStart + inserted.length);
					console.log("raw.length: ", raw.length);

                    /*
                     * 변경된 raw를 기준으로
                     * 화면과 커서를 다시 그린다.
                     */
                    render(
                        input,
                        raw,
                        cursor,
                        true
                    );

                    return;
                }


                /*
                 * =====================================================
                 * Backspace
                 * =====================================================
                 */
                if (
                    e.inputType
                    === 'deleteContentBackward'
                ) {

                    e.preventDefault();

                    var raw = state.raw;


                    /*
                     * 선택 영역이 있는 경우
                     *
                     * 선택된 raw 구간 전체를 삭제
                     */
                    if (rawStart !== rawEnd) {

                        raw =
                            raw.slice(
                                0,
                                rawStart
                            )
                            + raw.slice(
                                rawEnd
                            );

                        render(
                            input,
                            raw,
                            rawStart,
                            true
                        );

                        return;
                    }


                    /*
                     * 현재 커서 바로 앞 문자가
                     * 화면 표시용 문자라면 삭제하지 않는다.
                     *
                     * 예)
                     *
                     * 2026-|05
                     *      ↑
                     *
                     * '-' 및 자동 생성된 '0'은
                     * raw에 존재하지 않으므로 직접 삭제하지 않는다.
                     */
                    if (
                        start > 0
                        && state.sourceMap[
                            start - 1
                        ] == null
                    ) {
                        return;
                    }


                    /*
                     * 실제 raw의 바로 앞 숫자를 삭제
                     */
                    if (rawStart > 0) {

                        raw =
                            raw.slice(
                                0,
                                rawStart - 1
                            )
                            + raw.slice(
                                rawStart
                            );

                        render(
                            input,
                            raw,
                            rawStart - 1,
                            true
                        );
                    }

                    return;
                }


                /*
                 * =====================================================
                 * Delete
                 * =====================================================
                 *
                 * Backspace와 동일하지만
                 * 현재 커서 뒤의 문자를 삭제한다.
                 */
                if (
                    e.inputType
                    === 'deleteContentForward'
                ) {

                    e.preventDefault();

                    var raw = state.raw;


                    /*
                     * 선택 영역이 있다면
                     * 해당 영역 전체 삭제
                     */
                    if (rawStart !== rawEnd) {

                        raw =
                            raw.slice(
                                0,
                                rawStart
                            )
                            + raw.slice(
                                rawEnd
                            );

                        render(
                            input,
                            raw,
                            rawStart,
                            true
                        );

                        return;
                    }


                    /*
                     * 현재 위치가 화면 표시용 문자라면
                     * Delete로 직접 삭제하지 않는다.
                     */
                    if (
                        state.sourceMap[start]
                            == null
                        && start
                            < state.sourceMap.length
                    ) {
                        return;
                    }


                    /*
                     * 현재 raw 위치의 숫자 삭제
                     */
                    if (
                        rawStart
                        < raw.length
                    ) {

                        raw =
                            raw.slice(
                                0,
                                rawStart
                            )
                            + raw.slice(
                                rawStart + 1
                            );

                        render(
                            input,
                            raw,
                            rawStart,
                            true
                        );
                    }
                }
            }
        );


        /*
         * -------------------------------------------------------------
         * 붙여넣기 처리
         * -------------------------------------------------------------
         *
         * 예)
         *
         * 2026-08-14
         * 2026.08.14
         * 20260814
         *
         * 어떤 형태로 붙여넣더라도 숫자만 추출해서 처리한다.
         */
        input.addEventListener(
            'paste',
            function (e) {

                e.preventDefault();


                /*
                 * 클립보드 문자열 가져오기
                 */
                var text =
                    (
                        e.clipboardData
                        || window.clipboardData
                    )
                    .getData('text');


                /*
                 * 날짜 구분자를 제거하고 숫자만 사용
                 */
                var inserted =
                    text.replace(/\D/g, '');


                if (!inserted) {
                    return;
                }


                var state =
                    input._dateInputState;


                /*
                 * 현재 selection 영역을
                 * raw 위치로 변환
                 */
                var rawStart =
                    getRawPosition(
                        state,
                        input.selectionStart
                    );

                var rawEnd =
                    getRawPosition(
                        state,
                        input.selectionEnd
                    );


                /*
                 * 선택된 영역을 붙여넣은 숫자로 교체
                 */
                var raw =
                    state.raw.slice(
                        0,
                        rawStart
                    )
                    + inserted
                    + state.raw.slice(
                        rawEnd
                    );


                /*
                 * 최대 YYYYMMDD 8자리
                 */
                raw = raw.slice(0, 8);


                render(
                    input,
                    raw,

                    /*
                     * 붙여넣은 값 바로 뒤에 커서 배치
                     */
                    Math.min(
                        rawStart
                            + inserted.length,
                        raw.length
                    ),

                    true
                );
            }
        );
    }


    /*
     * =====================================================================
     * 외부 데이터로 input 값을 설정한다.
     * =====================================================================
     *
     * 사용 예)
     *
     * DateInputUtil.setValue(
     *     input,
     *     '2026.08.14'
     * );
     *
     * 화면:
     *     2026-08-14
     *
     * 내부 raw:
     *     20260814
     *
     * setValue는 프로그래밍 방식으로 값을 설정하는 기능이므로
     * input 이벤트는 별도로 발생시키지 않는다.
     */
    function setValue(input, value) {

        var normalized =
            normalize(value);

        var digits =
            normalized.replace(/\D/g, '');

        render(
            input,
            digits
        );
    }


    /*
     * =====================================================================
     * input의 실제 저장용 값을 반환한다.
     * =====================================================================
     *
     * 화면 값:
     *     2026-08-14
     *
     * 반환 값:
     *     20260814
     *
     * 화면에 자동 추가된 '-', '0' 등은 반환하지 않는다.
     *
     * 예)
     *
     * 화면     : 2026-05
     * 실제 raw : 20265
     *
     * DateInputUtil.getValue(input)
     *     → '20265'
     */
    function getValue(input) {

        if (!input._dateInputState) {
            return '';
        }

        return input._dateInputState.raw;
    }


    /*
     * 외부에 공개할 함수만 반환한다.
     *
     * createDisplayState,
     * getRawPosition,
     * getDisplayPosition,
     * render
     *
     * 등은 DateInputUtil 내부 구현용 함수이므로 외부에 노출하지 않는다.
     */
    return {
        bind: bind,
        setValue: setValue,
        getValue: getValue,
        normalize: normalize
    };

})();