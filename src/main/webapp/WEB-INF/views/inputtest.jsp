<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>dinguru</title>
    <script src="${pageContext.request.contextPath}/resources/js/DateInputUtil.js"></script>
  </head>

  <body>
  	<div style="padding: 1rem;">
  		<h3>DB 조회 데이터 예시</h3>
  		<ul id="exList">
  			<li data-value="26.08">26.08</li>
  			<li data-value="2026">2026</li>
  			<li data-value="2026.08">2026.08</li>
  			<li data-value="2026-08">2026-08</li>
  			<li data-value="2026.08.16">2026.08.16</li>
  			<li data-value="2026-08-16">2026-08-16</li>
  		</ul>
  	</div>
  	<div style="padding: 1rem;">
  		<h3>화면 입/출력 예시</h3>
  		<input
		    type="text"
		    id="dateInput"
		    maxlength="10"
		    inputmode="numeric"
		    placeholder="YYYY-MM-DD"
		    autocomplete="off"
		/>
  	</div>
  	<div style="padding: 1rem;">
  		<h3>DB 저장 형태</h3>
  		<b id="resultStr"></b>
  	</div>
  	
    <script>
	    var dateInput = document.querySelector('#dateInput');
	    var resultStrEl = document.querySelector('#resultStr');
	
	    DateInputUtil.bind(dateInput);
	
	    dateInput.addEventListener('input', function () {
	        resultStrEl.textContent =
	            DateInputUtil.getValue(this);
	    });  
	    
	    document.querySelectorAll('#exList li').forEach(function (li) {
	        li.addEventListener('click', function () {
	            DateInputUtil.setValue(
	                dateInput,
	                li.dataset.value
	            );

	            resultStrEl.textContent =
	                DateInputUtil.getValue(dateInput);
	        });
	    });
	    
	    dateInput.addEventListener("change", function (e) {
			console.log("change event: ", e);
	    })
  	</script>
  </body>
  
</html>
