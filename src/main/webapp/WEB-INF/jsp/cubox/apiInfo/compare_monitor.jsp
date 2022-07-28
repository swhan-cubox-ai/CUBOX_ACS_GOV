<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript"  src="/js/sockjs.js"></script>
<script type="text/javascript"  src="/js/reconnecting-websocket.min.js"></script>

<script>
	var sock = null;
	var message = {};

	$(document).ready(function(){
		$(".title_tx").html("서버인증 모니터링");
		chatSock = new ReconnectingWebSocket("ws://${ws_ip}/websocket",null, {debug: true, reconnectInterval: 3000});
		chatSock.onopen = function() {
			message={};
			message.whoami = "listpage";
			chatSock.send(JSON.stringify(message));
		};
		chatSock.onclose = function() {
		};
		chatSock.onmessage = function(evt) {
			var msg = evt.data;
			var data = JSON.parse(msg);
			if(data != null) {
				var tx = data["tx"];
				var score = data["score"];
				var fname = data["name"];
				var sDate = data["date"];

				//score = Number(score);

				$("#divDate").html("인증일시 : "+sDate);

				var resultText = ""

				if(tx != null) {
					var orgImageBase64 = data["orgImageBase64"];
					var left_image = document.getElementById('left_image');
					var right_image = document.getElementById('right_image');
					if(orgImageBase64 != null) {
						left_image.src = 'data:image/bmp;base64,'+orgImageBase64;
					}else{
						left_image.src = "/images/no-img.jpg";
					}

					var trgImageBase64 = data["trgImageBase64"];
					if(score > 7000){
						resultText = "인증대상 : "+fname+" ( 점수 :  <font color=blue>"+ score +"</font>)";
						if(trgImageBase64 != null) {
							right_image.src = 'data:image/bmp;base64,'+trgImageBase64;
						}else{
							right_image.src = "/images/no-img.jpg";
						}
					}else if(score > 4000){
						resultText = "인증대상 : "+fname+" ( 점수 :  <font color=green>"+ score +"</font>)";
						if(trgImageBase64 != null) {
							right_image.src = 'data:image/bmp;base64,'+trgImageBase64;
						}else{
							right_image.src = "/images/no-img.jpg";
						}
					}else{
						resultText = "인증대상 : 점수미달 ( 점수 :  <font color=red>"+ score +"</font>)";
						right_image.src = "/images/no-img.jpg";
					}
				}
				$("#divText").html(resultText);
			}
		};
	});
</script>

<div class="search_box mb_20">
	<div class="search_in_bline">
		<div class="comm_search mr_20">
			<div class="mb_5"><img class="mr_10" src="/img/title_icon2.png" alt="">게이트가 서버인증으로 설정되었을때 일치된 사용자에 대한 정보를 출력합니다.</div>
		</div>
	</div>
</div>
<div class="search_box mb_20">
	<div class="search_in_bline">
		<div class="ml_10 title_box mt_10 mb_10">
			<div class="title_time">
				<em id="divDate"></em>
			</div>
		</div>
	</div>
	<div class="search_in_bline">
		<div class="fl">
			<img id="left_image" src="/images/compare_origin.png" style="width:450px;height:450px;"/>
		</div>
		<div class='fl ml_10'>
			<img id="right_image" src="/images/compare_live.png" style="width:450px;height:450px;"/>
		</div>
	</div>
	<div class="search_in_bline">
		<div class="ml_10 mt_10 mb_15">
			<div class="match_text" id="divText"></div>
		</div>
	</div>
</div>
