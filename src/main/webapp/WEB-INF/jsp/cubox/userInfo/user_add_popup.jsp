<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="../frame/sub/head.jsp" />
<style>
	/* 카드 입력란 */
	input#fcdno::placeholder{
		color: red;
		font-weight: bold;
	}
</style>
<script type="text/javascript" src="<c:url value='/js/faceComm.js?ver=<%=Math.random() %>'/>"></script>
<script type="text/javascript">
var fidDigit = Number("<spring:eval expression="@property['Globals.fidDigit']" />");//고유번호 생성 자릿수
var cardDigit = Number("<spring:eval expression="@property['Globals.cardDigit']" />");//카드번호 생성 자릿수

$(document).ready(function() {
	$("#popupNm").html("신규등록");

	//부모창의 새로고침/닫기/앞으로/뒤로
	$(opener).on('beforeunload', function() {
		 window.close();
	});
	
	// 숫자만 입력가능
	$(".onlyNumber").keyup(function(event){
		if (!(event.keyCode >=37 && event.keyCode<=40)) { //방향키
			var inputVal = $(this).val();
			$(this).val(inputVal.replace(/[^0-9]/gi,''));
		}
	});

	$("#startDate").change(function() {
		var srchStartDt = $("#startDate").val().replace('-','').replace('-','');
		var srchExpireDt = $("#expireDate").val().replace('-','').replace('-','');
		if(srchStartDt > srchExpireDt && srchExpireDt != ""){
			alert("만료일보다 뒷 날짜를 선택할 수 없습니다.");
			$("#startDate").val('');
			$("#startDate").focus();
		}
	});

	$("#expireDate").change(function(){
		var srchStartDt = $("#startDate").val().replace('-','').replace('-','');
		var srchExpireDt = $("#expireDate").val().replace('-','').replace('-','');
		if(srchStartDt > srchExpireDt && srchExpireDt != ""){
			alert("시작일보다 앞 날짜를 선택할 수 없습니다.");
			$("#expireDate").val('');
			$("#expireDate").focus();
		}
	});
	
	$("#futype").change(function(){
		if($(this).val() == "3") {
			$("#fvisitnum").prop("disabled", false);
			$("#fvisitnum").focus();
		} else {
			$("#fvisitnum").val("");
			$("#fvisitnum").prop("disabled", true);
		}
	});		

	//고유번호 입력 란
	$("#fuid").focus();
});

/* 고유번호 자릿수 체크 */
function fnvalichk2 (event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) return;
	else {
		var tVal = event.target.value;
		var regx = /^[0-9]*$/g;
		if(!fnIsEmpty(tVal) && !regx.test(tVal)) {
			tVal = tVal.replace(/[^0-9]/g,"");
			event.target.value = fnIsEmpty(tVal)?"":tVal.substring(0, fidDigit);
		}
	}
}

function fnDetectCall () {
	showLoading ();
	$.ajax({
		url: "/userInfo/detectCallImg.do",
		data: "",
		processData: false,
		contentType: false,
		type: "POST",
		success: function(response) {
			hideLoading();
			//$("#imgView").attr("src",response);
			if(!fnIsEmpty(response) && !fnIsEmpty(response.rst) && response.rst == 1) {
				$("#myCanvas").show();
				$("#imgView").hide();
				$("#imageType").val("canvas");
				var image = new Image();
				image.onload = function() {
					//alert(image.width + "," + image.height);
					var canvas = document.getElementById('myCanvas');
					canvas.setAttribute('width', 640);
					canvas.setAttribute('height', 576);
					var scale = Math.max(canvas.width / image.width, canvas.height / image.height);
					//var scale = Math.min(canvas.width / image.width, canvas.height / image.height);
					var x = (canvas.width / 2) - (image.width / 2) * scale;
				    var y = (canvas.height / 2) - (image.height / 2) * scale;
					var ctx =  canvas.getContext('2d');
					ctx.drawImage(image, x, y, image.width * scale, image.height * scale);
				};
				image.src = response.imageString;
			} else {
				alert("사진이 확인되지 않습니다. 다시 시도하여 주십시오.");
			}
		},
		error: function (jqXHR){
			alert("사진촬영에 실패했습니다. 다시 시도하여 주십시오.");
		}
	});
}

//신규저장
function userInfoSave () {
	var fuid = $("#fuid").val();						/*고유번호*/
	var funm = $("#funm").val();						/*이름*/
	var futype = $("#futype").val();					/*권한타입*/
	var fvisitnum = $("#fvisitnum").val();				/*방문객카드번호*/
	var startDate = $("#startDate").val();				/*시작일*/
	var expireDate = $("#expireDate").val();			/*만료일*/
	var cfstatus = $("#cfstatus").val();				/*카드상태*/
	var fcdno = $("#fcdno").val();						/*카드번호*/
	var fpartnm2 = $("#fpartnm2").val();				/*부서*/
	
	if(fnIsEmpty(fuid) || fuid.length != fidDigit) {
		alert("고유번호를 입력하여 주십시오. (" + fidDigit + "자리)");
		$("#fuid").focus();
		return;
	}
	
	if(fnIsEmpty(funm)) {
		alert("이름을 입력하여 주십시오.");
		$("#funm").focus();
		return;
	}
	
	if($("#futype").val() == "3") { //방문객카드
		if(fnIsEmpty($("#fvisitnum").val())) {
			alert("출입자타입이 방문객카드인 경우, 방문객카드번호는 필수입력 항목입니다.");
			$("#fvisitnum").focus();
			return;	
		} else {
			var str = Number($("#fvisitnum").val());
			if(str == 0) {
				alert("방문객카드번호를 정확하게 입력하세요.");
				$("#fvisitnum").focus();
				return;
			}
		}
	}	
	
	if(fnIsEmpty(startDate) || fnIsEmpty(expireDate)) {
		alert("시작일, 종료일을 입력하세요.");
		$("#startDate").focus();
		return;
	}
	
	if(confirm("출입자 정보를 저장하시겠습니까?")) {
		var imgty = $("#imageType").val();
		var form = $('#frmDetail')[0];
		var formData = new FormData(form);
		formData.append('fuid', fuid);
		formData.append('funm', funm);
		formData.append('fcdno', fcdno);
		formData.append('futype', futype);
		formData.append('fvisitnum', fvisitnum);
		formData.append('srchStartDate2', startDate);
		formData.append('srchExpireDate2', expireDate);
		formData.append('cfstatus', cfstatus);
		formData.append('fpartnm2', fpartnm2);

		if(!fnIsEmpty(imgty) && imgty == "file") {
			formData.append("fileObj", $("#customFile")[0].files[0]);
		} else if(!fnIsEmpty(imgty) && imgty == "canvas") {
			var drawCanvas = document.getElementById('myCanvas');
			formData.append("imgUpload", drawCanvas.toDataURL('image/jpeg'));
		}
		$.ajax({
			url: "<c:url value='/userInfo/addUserInfoSave.do' />",
			data: formData,
			dataType:'json',
			processData: false,
			contentType: false,
			type: "POST",
			beforeSend : function(xhr, opts) {},
			success: function(response) {
				if(response.cardSaveCnt > 0) {
					alert("저장되었습니다.");
					opener.userSearch();
					window.close();
				} else if(response.cardSaveCnt == -1) {
					alert("고유번호는 숫자 " + fidDigit + "자리 입니다. 다시 입력하여 주십시오.");
					$("#fuid").val("");
					$("#fuid").focus();
				} else if(response.cardSaveCnt == -2) {
					alert("고유번호가 중복되었습니다. 다시 입력하여 주십시오.");
					$("#fuid").val("");
					$("#fuid").focus();
				} else if(response.cardSaveCnt == -4){
					alert("카드번호가 중복되었습니다. 다시 입력하여 주십시오.\n현재 " + response.result.funm + "이(가) 사용중입니다.");
					$("#fcdno").val("");
					$("#fcdno").focus();
				} else if(response.cardSaveCnt == -11) {
					alert("방문객카드번호를 입력하여 주십시오.");
					$("#fvisitnum").focus();
				} else if(response.cardSaveCnt == -12) {
					alert("방문객카드번호가 중복되었습니다. 다시 입력하여 주십시오.");
					$("#fvisitnum").val("");
					$("#fvisitnum").focus();					
				} else{
					alert("저장에 실패했습니다.");
				}
			},
			error: function (jqXHR){
				alert("저장에 실패했습니다."); //alert(jqXHR.responseText);
				return;
			}
		});
	}
}
</script>
<body>
<jsp:include page="../frame/sub/popup_top.jsp" />
<div class="popup_box">
	<!-- <div class="tab_box2 mb_20">
		<ul class="tabs" data-persist="true">
			<li>
				<a href="#view2_1"><img src="/img/teb_icon3_on.png" alt="" />출입자 정보</a>
			</li>
		</ul>
	</div> -->
	<div class="tabcontents">
		<div id="view2_1">
			<!--검색박스 -->
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<div class="comm_search mr_m3">
						<div class="title">* 고유번호</div>
						<input type="text" id="fuid" name="fuid" maxlength="<spring:eval expression="@property['Globals.fidDigit']" />" class="w_200px input_com" onkeyup="fnvalichk2(event)">
					</div>
					<!-- <div class="comm_search mr_5">
						<button type="button" class="comm_btn">중복체크</button>
					</div> -->
				</div>
			</div>
			<!--//검색박스 -->

			<div class="box_w2 mt_20">
				<!--------- 목록--------->
				<div class="box_w2_1">
					<!--테이블 시작 -->
					<form id="frmDetail" name="frmDetail" method="post" enctype="multipart/form-data" onsubmit="return false;">
						<div class="com_box">
							<c:choose>
								<c:when test="${empty userBioInfo}">
									<img id="imgView" src="/img/photo_01.png" alt=""/>
								</c:when>
								<c:otherwise>
									<a href='/userInfo/getByteImageDown.do?fuid=<c:out value="${userInfo.fuid}"/>'>
										<img id="imgView" src='/userInfo/getByteImage.do?fuid=<c:out value="${userInfo.fuid}"/>' onerror="this.src='/img/photo_01.jpg'">
									</a>
								</c:otherwise>
							</c:choose>
							<canvas id="myCanvas" style="display:none;" width="320" height="288"></canvas>
							<input type="hidden" name="imgfuid" value='<c:out value="${userInfo.fuid}"/>' />
						</div>
						<!--  -->
						<div class="totalbox2 mt_20">
							<div class="r_searhbox">
								<div class="custom-file comm_search mr_5">
									<input type="file" class="custom-file-input w_210px" id="customFile">
									<label class="custom-file-label" for="customFile">
										<div class="custom-file-fileName"></div>
									</label>
									<input type="hidden" id="imageType" name="imageType"/>
								</div>
								
								<spring:eval expression="@property['Globals.photoShoot']" var="photoShoot"/>
								<c:if test="${photoShoot eq 'Y'}">
									<div class="comm_search">
										<button type="button" class="btn_middle color_basic" id="btnSnapshot" onclick="fnDetectCall()">사진 촬영</button>
										<!-- fnDetectPop():사진촬영팝 / fnDetectCall():등록기 호출 -->
									</div>
								</c:if>
							</div>
						</div>
					</form>
				</div>
				<!--------- 목록--------->
				<!--------- 목록--------->
				<div class="box_w2_2">
					<!--테이블 시작 -->
					<div class="tb_outbox">
						<table class="tb_write_02 tb_write_p1">
							<col width="15%" />
							<col width="25%" />
							<col width="65%" />
							<tbody>
								<tr>
									<th rowspan="5"  style="border-right: 1px solid #cecece">사용자정보</th>
									<th>이름 <span class="font-color_H">*</span></th>
									<td>
										<input type="text" id="funm" name="funm" maxlength="50" value="" class="w_150px input_com">
									</td>
								</tr>
								<tr>
									<th>출입자타입 <span class="font-color_H">*</span></th>
									<td>
										<select name="futype" id="futype" size="1" class="w_150px input_com mr_5" onchange="getAuthGroup(this.value);">
										<c:forEach items="${userType}" var="uType" varStatus="status">
		                                    <option value='<c:out value="${uType.fvalue}"/>'><c:out value="${uType.fkind3}"/></option>
		                            	</c:forEach>
										</select>
										<input type="text" name="fvisitnum" id="fvisitnum" class="w_110px input_com onlyNumber" maxlength="3" placeholder="방문객카드번호" disabled>
									</td>
								</tr>
								<tr>
									<th>부서</th>
									<td>
										<input type="text" id="fpartnm2" name="fpartnm2" maxlength="50" class="w_150px input_com">
									</td>
								</tr>
								<tr>
									<th>시작일 <span class="font-color_H">*</span></th>
									<td>
        								<input type="text" class="input_datepicker w_150px" name="startDate" id="startDate" readonly="readonly" placeholder="">
									</td>
								</tr>
								<tr>
									<th>종료일 <span class="font-color_H">*</span></th>
									<td>
        								<input type="text" class="input_datepicker w_150px" name="expireDate" id="expireDate" readonly="readonly" placeholder="">
									</td>
								</tr>
								<tr>
									<th rowspan="2" style="border-right: 1px solid #cecece">카드정보</th>
									<th>카드번호</th>
									<td>
										<!-- <span class="ml10">자동 발번</span> -->
										<input type="text" id="fcdno" name="fcdno" maxlength="<spring:eval expression="@property['Globals.cardDigit']" />"  class="w_150px input_com" placeholder="미입력시 자동발번" onkeyup="fnvalichk(event)">
									</td>
								</tr>
								<tr>
									<th>카드상태 <span class="font-color_H">*</span></th>
									<td>
										<select name="cfstatus" id="cfstatus" size="1" class="w_150px input_com" onchange="getAuthGroup(this.value);">
											<c:forEach items="${cardStatus}" var="cStatus" varStatus="status">
												<option value='<c:out value="${cStatus.fvalue}"/>' <c:if test="${cStatus.fvalue eq 'Y'}">selected</c:if>><c:out value="${cStatus.fkind3}"/></option>
											</c:forEach>
										</select>
									</td>
								</tr>
								
							</tbody>
						</table>
					</div>
					<!--버튼 -->
				   	<div class="right_btn mt_20">
				        <button type="button" class="btn_middle color_basic" onclick="userInfoSave();">저장 </button>
				        <!-- <button type="button" class="btn_middle color_gray">취소 </button> -->
				   	</div>
					<!--//버튼  -->
				</div>
				<!--------- //목록--------->
			</div>
		</div>
	</div>
</div>
<jsp:include page="../frame/sub/tail.jsp" />
</body>