<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="../frame/sub/head.jsp" />
<script type="text/javascript" src="<c:url value='/js/faceComm.js?ver=<%=Math.random() %>'/>"></script>
<script type="text/javascript">
var cardDigit = Number("<spring:eval expression="@property['Globals.cardDigit']" />");//카드번호 생성 자릿수

$(document).ready(function() {
	<c:if test="${!empty sfcdno}">
		getAuthGroup('${sfcdno}');
	</c:if>
	
	<c:if test="${userInfo.fustatus eq 'D' || userInfo.fissyn eq 'Y'}">
	$("input").prop("readonly", true);
	$("option").attr("disabled", true);
	$("#startDate").prop("disabled", true);
	$("#expireDate").prop("disabled", true);
	$("#divFcdnoChange").css("display", "none");
	</c:if>
	
	//부모창의 새로고침/닫기/앞으로/뒤로
	$(opener).on('beforeunload', function() {
		 window.close();
	});	

	$("#popupNm").html("출입자 및 카드정보");
	
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

	//고유번호 입력 란
	$("#fuid").focus();
	
	//카드번호 변경시 
	$("#isFcdnoChange").on("click", function(){
		$("#newFcdno").val($("#fcdno").val()).attr("disabled", !$(this).is(":checked"));//기존카드번호 유지 및 신규카드번호 변경란 활성화 / 비활성화
	});
	
	$("#futype").change(function(){
		if($(this).val() == "3") {
			$("#fvisitnum").prop("disabled", false);
		} else {
			$("#fvisitnum").val("");
			$("#fvisitnum").prop("disabled", true);
		}
	});		
});

/*숫자 8자리*/
/* function fnvalichk (event) {
	alert(123);
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) return;
	else {
		var tVal = event.target.value;
		var regx = /^[0-9]{0,8}$/g;
		if(!fnIsEmpty(tVal) && !regx.test(tVal)) {
			tVal = tVal.replace(/[^0-9]/g,"");
			event.target.value = fnIsEmpty(tVal)?"":tVal.substring(0,8);
		}
	}
} */

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

					//canvas.setAttribute('width', image.height);
					//canvas.setAttribute('height', image.width);
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

function imgUpload() {
	var imgfuid = $("input[name=imgfuid]").val();
	var form = $('#frmDetail')[0];
	var formData = new FormData(form);
	formData.append('fuid', imgfuid);

	if($("#imageType").val() == "file") {
		console.log('===> image : attach file!!');
		formData.append("fileObj", $("#customFile")[0].files[0]);
	} else if($("#imageType").val() == "canvas") {
		console.log('===> image : draw canvas!!');
		var drawCanvas = document.getElementById('myCanvas');
		formData.append("imgUpload", drawCanvas.toDataURL('image/jpeg'));
	} else {
		alert("얼굴사진을 취득하거나 파일을 첨부하세요.");
		return;
	}

	$.ajax({
		url: "<c:url value='/userInfo/imgUserSave.do' />",
		data: formData,
		dataType:'json',
		processData: false,
		contentType: false,
		type: "POST",
		success: function(response){
			if(fnIsEmpty(response.rst)) {
				alert("저장에 실패하였습니다. 다시 시도하여 주십시오");
			} else {
				if (parseInt(response.rst) == -2){
					alert("jpg, jpeg, bmp, wbmp, png, gif 포맷만 사용 가능합니다.");
					
				} else if (parseInt(response.rst) == -1){
					alert("이미지 파일이 확인되지 않습니다. 다시 확인 후 첨부하여 주십시오.");
				} else {
					opener.userSearch();
				}
			}
			location.reload();
		},
		error: function (jqXHR){
			alert(jqXHR.responseText);
		}
	});
}

function userInfoSave_BAK(){

	var funm = $("#funm").val();
	var futype = $("#futype").val();					/*권한타입*/
	var fvisitnum = $("#fvisitnum").val();				/*권한타입*/
	var startDate = $("#startDate").val();				/*시작일*/
	var expireDate = $("#expireDate").val();			/*만료일*/
	var cfstatus = $("#cfstatus").val();				/*카드상태*/
	var fcdno = $("#fcdno").val();						/*카드번호*/
	var fpartnm2 = $("#fpartnm2").val();				/*부서*/
	var isFcdnoChange = ($("#isFcdnoChange").is(":checked")) ? "Y" : "N";/* 카드번호 변경 여부 */
	var newFcdno = $("#newFcdno").val();/*신규 카드 번호*/

	if(fnIsEmpty(funm)) {
		alert("이름을 입력하여 주십시오.");
		$("#funm").focus();
		return;
	}
	
	//카드번호 자릿수 체크
	if("Y" == isFcdnoChange){//카드번호를 변경할 경우
		if (newFcdno.length > 0 && newFcdno.length > cardDigit) {
			alert("카드번호를 자릿수를 확인하여 주십시오. (숫자 " + cardDigit + "자리)");
			$("#newFcdno").focus();
			return;
		}
	}
	
	if(startDate == "" || expireDate == ""){
		alert("시작일, 종료일을 입력하세요.");
		return;
	}

	//권한타입, 카드상태, 시작일, 종료일 변경사항 체크?
	if(confirm("출입자 정보를 저장하시겠습니까?")){
		$.ajax({
			type:"POST",
			url:"/userInfo/userInfoSave.do",
			data:{
				"fuid" : '<c:out value="${userInfo.fuid}"/>',
				"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>',
				"funm" : funm,
				"futype" : futype,
				"fvisitnum" : fvisitnum,
				"startDate": startDate,
				"expireDate": expireDate,
				"cfstatus": cfstatus,
				"fcdno": fcdno,
				"newFcdno" : newFcdno,
				"isFcdnoChange" : isFcdnoChange,
				"fpartnm2" : fpartnm2,
			},
			dataType:'json',
			//timeout:(1000*30),
			success : function(returnData, status){
				if( status == "success" ){
					if( !fnIsEmpty(returnData.uCnt) && parseInt(returnData.uCnt) > 0 ){
						opener.pageSearch();//opener.userSearch();
						alert("출입자정보를 저장하였습니다.");
						self.close();
					} else if(returnData.cardSaveCnt == -4){
						alert("카드번호가 중복되었습니다. 다시 입력하여 주십시오.\n현재 " + returnData.result.funm + "이(가) 사용중입니다.");
						$("#newFcdno").val("");
						$("#newFcdno").focus();
					} else if(returnData.cardSaveCnt == -11) {
						alert("방문객카드번호를 입력하여 주십시오.");
						$("#fvisitnum").focus();
					} else if(returnData.cardSaveCnt == -12) {
						alert("방문객카드번호가 중복되었습니다. 다시 입력하여 주십시오.");
						$("#fvisitnum").val("");
						$("#fvisitnum").focus();
					} else {
						if(!fnIsEmpty(returnData.message)) {
							alert(returnData.message);
						} else {
							alert("저장 중 오류가 발생했습니다. 다시 시도하여 주십시오.");
						}
						//location.reload();
					} 
				} else { 
					alert("ERROR!"); alert("오류가 발생했습니다. 다시 시도하여 주십시오."); return;
				}
			}
		});
	}
}

// 2021-04-20 사진,개인정보를 한번에 저장함.
function userInfoSave(){
	if(!fnFormValueCheck("frmDetail")) return;

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
	
	if(confirm("출입자정보를 저장하시겠습니까?")){
		var form = $('#frmDetail')[0];
		var formData = new FormData(form);
		// 개인정보
		formData.append('fuid', '<c:out value="${userInfo.fuid}"/>');
		formData.append('fpartcd1', '<c:out value="${userInfo.fpartcd1}"/>');
		formData.append('fauthtype', '<c:out value="${userInfo.fauthtype}"/>');
		formData.append('funm', $("#funm").val());
		formData.append('fcdno', fcdno);
		formData.append('futype', $("#futype").val());
		formData.append('fvisitnum', $("#fvisitnum").val());
		formData.append('startDate', $("#startDate").val());
		formData.append('expireDate', $("expireDate").val());
		formData.append('cfstatus', $("#cfstatus").val());
		formData.append('fpartnm2', $("#fpartnm2").val());
		formData.append('newFcdno', $("#newFcdno").val());
		formData.append('isFcdnoChange', (($("#isFcdnoChange").is(":checked")) ? "Y" : "N"));
		// 이미지
		if(!fnIsEmpty($("#imageType").val())) {
			if($("#imageType").val() == "file") {
				formData.append("fileObj", $("#customFile")[0].files[0]);
			} else if($("#imageType").val() == "canvas") {
				var drawCanvas = document.getElementById('myCanvas');
				formData.append("imgUpload", drawCanvas.toDataURL('image/jpeg'));
			}
		}
		
		$.ajax({
			type: "POST",
			url: "/userInfo/userInfoSaveNew.do",
			data: formData,
			dataType: 'json',
			//timeout:(1000*30),
			processData: false,
			contentType: false,
			success: function(returnData, status){
				if( status == "success" ){
					if( !fnIsEmpty(returnData.uCnt) && parseInt(returnData.uCnt) > 0 ){
						opener.pageSearch();//opener.userSearch();
						alert("출입자정보를 저장하였습니다.");
						self.close();
					} else if(returnData.cardSaveCnt == -4){
						alert("카드번호가 중복되었습니다. 다시 입력하여 주십시오.\n현재 " + returnData.result.funm + "이(가) 사용중입니다.");
						$("#newFcdno").val("");
						$("#newFcdno").focus();
					} else if(returnData.cardSaveCnt == -11) {
						alert("방문객카드번호를 입력하여 주십시오.");
						$("#fvisitnum").focus();
					} else if(returnData.cardSaveCnt == -12) {
						alert("방문객카드번호가 중복되었습니다. 다시 입력하여 주십시오.");
						$("#fvisitnum").val("");
						$("#fvisitnum").focus();
					} else {
						if(!fnIsEmpty(returnData.message)) {
							alert(returnData.message);
						} else {
							alert("저장 중 오류가 발생했습니다. 다시 시도하여 주십시오.");
						}
						//location.reload();
					} 
				} else { 
					alert("ERROR!"); alert("오류가 발생했습니다. 다시 시도하여 주십시오."); return;
				}
			}
		});
	}
}

function userInfoDel() {
	if(!confirm("출입자정보를 삭제하시겠습니까?")) return;
	
	$.ajax({
		type : "POST",
		url : "/userInfo/userInfoDel.do",
		data : {
			"fuid" : '<c:out value="${userInfo.fuid}"/>',
			"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>'
		},
		dataType : 'json',
		success : function(returnData, status){
			if(status == "success"){
				if(returnData.result == "success"){
					opener.pageSearch();//opener.userSearch();
					alert("출입자정보를 삭제하였습니다.");
					self.close();
				} else {
					if(!fnIsEmpty(returnData.message)) {
						alert(returnData.message);
					} else {
						alert("삭제 중 오류가 발생했습니다. 다시 시도하여 주십시오.");
					}
				} 
			} else { 
				alert("ERROR!"); alert("오류가 발생했습니다. 다시 시도하여 주십시오."); return;
			}
		}
	});
}

function userInfoReco() {
	if(!confirm("출입자정보를 복원하시겠습니까?")) return;
	
	$.ajax({
		type : "POST",
		url : "/userInfo/userInfoReco.do",
		data : {
			"fuid" : '<c:out value="${userInfo.fuid}"/>',
			"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>',
			"futype" : '<c:out value="${userInfo.futype}"/>'
		},
		dataType : 'json',
		success : function(returnData, status){
			if(status == "success"){
				if(returnData.result == "success"){
					opener.pageSearch();//opener.userSearch();
					alert("출입자정보를 복원하였습니다.");
					self.close();
				} else {
					if(!fnIsEmpty(returnData.message)) {
						alert(returnData.message);
					} else {
						alert("복원 중 오류가 발생했습니다. 다시 시도하여 주십시오.");
					}
				} 
			} else { 
				alert("ERROR!"); alert("오류가 발생했습니다. 다시 시도하여 주십시오."); return;
			}
		}
	});
}	

function userInfoDrop() {
	if(!confirm("출입자정보를 완전히 삭제하시겠습니까?")) return;
	
	$.ajax({
		type : "POST",
		url : "/logInfo/getGateLogTotCntByUser.do",
		data : {
			"fuid" : '<c:out value="${userInfo.fuid}"/>',
			"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>'
		},
		dataType : 'json',
		success : function(returnData, status){
			if(status == "success"){
				if(!fnIsEmpty(returnData.cnt)) {
					var cnt = parseInt(returnData.cnt);
					if(cnt == 0 ) {
						fnDropUser();
					} else if(cnt > 0) {
						alert("출입이력이 존재하여 영구삭제처리를 할 수 없습니다.");
					} else {
						alert("출입자 영구삭제 중 오류가 발생했습니다. 다시 시도하십시오.");
					}
				} else {
					alert("출입자 영구삭제 중 오류가 발생했습니다. 다시 시도하십시오.");
				}
			} else { 
				alert("ERROR!"); alert("오류가 발생했습니다. 다시 시도하십시오"); return;
			}
		}
	});
}

function fnDropUser() {
	if(!confirm("출입자정보를 완전히 삭제하시겠습니까?")) return;
	
	$.ajax({
		type : "POST",
		url : "/userInfo/userInfoDrop.do",
		data : {
			"fuid" : '<c:out value="${userInfo.fuid}"/>',
			"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>'
		},
		dataType : 'json',
		success : function(returnData, status){
			if(status == "success"){
				if(returnData.result == "success"){
					opener.pageSearch();//opener.userSearch();
					alert("출입자정보를 삭제하였습니다.");
					self.close();
				} else {
					if(!fnIsEmpty(returnData.message)) {
						alert(returnData.message);
					} else {
						alert("삭제 중 오류가 발생했습니다. 다시 시도하여 주십시오.");
					}
				} 
			} else { 
				alert("ERROR!"); alert("오류가 발생했습니다. 다시 시도하여 주십시오."); return;
			}
		}
	});	
}	

function getAuthGroup(fcdno) {
	//카드정보가져오기
	$.ajax({
		type:"POST",
		url:"<c:url value='/userInfo/getCardInfo.do' />",
		data:{
			"fcdno": fcdno,
			"fuid" : '<c:out value="${userInfo.fuid}"/>',
			"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>'
		},
		dataType:'json',
		//timeout:(1000*30),
		success:function(returnData, status){
			if(status == "success") {
				if(!fnIsEmpty(returnData.cardInfoVO)) {
					//alert(JSON.stringify(returnData.cardInfoVO))
					$("#startDate").val(returnData.cardInfoVO.fsdt);
					$("#expireDate").val(returnData.cardInfoVO.fedt);
					$("#fcdno").val(returnData.cardInfoVO.fcdno);
					$("#cfstatus").val(returnData.cardInfoVO.fstatus);
				}
			}else{ alert("ERROR!");return;}
		}
	});
}
</script>
<body>
<jsp:include page="../frame/sub/popup_top.jsp" />
<div class="popup_box">
<form id="frmDetail" name="frmDetail" method="post" enctype="multipart/form-data" onsubmit="return false;">
	<!--검색박스 -->
	<div class="search_box_popup mb_10">
		<div class="search_in">
			<div class="comm_search mr_m3">
				<div class="title">고유번호 : <c:out value="${userInfo.fuid}"/></div>
			</div>
		</div>
	</div>
	<!--//검색박스 -->
	<div class="box_w2 mt_20">
		<!--------- 목록--------->
		<div class="box_w2_1">
			<!--테이블 시작 -->
			<div class="com_box">
				<c:choose>
					<c:when test="${empty userBioInfo}">
						<img id="imgView" src="/img/photo_01.png" alt=""/>
					</c:when>
					<c:otherwise>
						<a href='/userInfo/getByteImageDown.do?fuid=<c:out value="${userInfo.fuid}"/>'>
							<img id="imgView" src='/userInfo/getByteImage.do?fuid=<c:out value="${userInfo.fuid}"/>' onerror="this.src='/img/photo_01.png'">
						</a>
					</c:otherwise>
				</c:choose>
				<canvas id="myCanvas" style="display:none;" width="320" height="288"></canvas>
				<input type="hidden" name="imgfuid" value='<c:out value="${userInfo.fuid}"/>' />
			</div>
			<!--  -->
			<c:if test="${userInfo.fustatus eq 'Y' && userInfo.fissyn eq 'N'}">
			<div class="totalbox2 mt_20">
				<div class="r_searhbox">
					<div class="custom-file comm_search mr_5">
						<input type="file" class="custom-file-input w_210px" id="customFile">
						<label class="custom-file-label" for="customFile">
							<div class="custom-file-fileName"></div>
						</label>
						<input type="hidden" id="imageType" name="imageType"/>
					</div>
					<div class="comm_search">
						<spring:eval expression="@property['Globals.photoShoot']" var="photoShoot"/>
						<c:if test="${photoShoot eq 'Y'}">
							<button type="button" class="btn_middle color_basic" id="btnSnapshot" onclick="fnDetectCall()">사진촬영</button>
						</c:if>
						<%-- 2021-04-20 사진,개인정보를 한번에 저장함. --%>
						<%-- <button type="button" class="btn_middle color_basic" style="padding: 0px 15px" onclick="imgUpload();">업로드</button> --%>
						<%-- fnDetectPop():사진촬영팝 / fnDetectCall():등록기 호출 --%>
					</div>
				</div>
			</div></c:if>
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
							<th rowspan="5" style="border-right: 1px solid #cecece">사용자정보</th>
							<th>이름 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="funm" name="funm" maxlength="50" value="${userInfo.funm}" class="w_150px input_com" check="text" checkName="이름">
							</td>
						</tr>
						<tr>
							<th>출입자타입 <span class="font-color_H">*</span></th>
							<td>
								<select name="futype" id="futype" size="1" class="w_150px input_com mr_5" onchange="getAuthGroup(this.value);">
									<c:forEach items="${userType}" var="uType" varStatus="status">
	                                    <option value='<c:out value="${uType.fvalue}"/>' <c:if test="${uType.fvalue eq userInfo.futype}">selected</c:if>><c:out value="${uType.fkind3}"/></option>
	                            	</c:forEach>
								</select>
								<input type="text" name="fvisitnum" id="fvisitnum" class="w_110px input_com onlyNumber" maxlength="3" placeholder="방문객카드번호" <c:if test="${userInfo.futype ne '3'}">disabled</c:if> value="${userInfo.fvisitnum}">
							</td>
						</tr>
						<tr>
							<th>부서</th>
							<td>
								<input type="text" id="fpartnm2" name="fpartnm2" maxlength="50" value="${userInfo.fpartnm2}" class="w_150px input_com">
							</td>
						</tr>
						<tr>
							<th>시작일 <span class="font-color_H">*</span></th>
							<td colspan="2">
      								<input type="text" class="input_datepicker w_150px fl" name="startDate" id="startDate" readonly="readonly" check="text" checkName="카드시작일">
							</td>
						</tr>
						<tr>
							<th>종료일 <span class="font-color_H">*</span></th>
							<td>
      								<input type="text" class="input_datepicker w_150px fl" name="expireDate" id="expireDate" readonly="readonly" check="text" checkName="카드종료일">
							</td>
						</tr>
						<tr>
							<th rowspan="3" style="border-right: 1px solid #cecece">카드정보</th>
							<th>카드번호</th>
							<td>
								<%-- <select name="fcdno" id="fcdno" class="form-control w_150px" onchange="getAuthGroup(this.value);">
                                    <c:forEach items="${cardInfoList}" var="cInfoList" varStatus="status">
                                    <option value='<c:out value="${cInfoList.fcdno}"/>' <c:if test="${cInfoList.fcdno eq sfcdno}">selected</c:if>><c:out value="${cInfoList.fcdno}"/></option>
                                    </c:forEach>
                                </select> --%>
                                <input type="text" id="newFcdno" name="newFcdno" maxlength="<spring:eval expression="@property['Globals.cardDigit']" />" class="w_150px input_com" value="${sfcdno}" onkeyup="fnvalichk(event)" disabled="disabled" style="float: left;" check="text" checkName="카드번호">
                                <input type="hidden" id="fcdno" name="fcdno" value="${sfcdno}">
								<div id="divFcdnoChange" class="ch_box mr_20" style="float: left;">
									<input type="checkbox" id="isFcdnoChange" name="isFcdnoChange" class="checkbox" value="Y">
									<label for="isFcdnoChange" class="ml_10"> 카드번호 변경</label>
								</div>
							</td>
						</tr>
						<tr>
							<th>카드상태 <span class="font-color_H">*</span></th>
							<td>
								<select name="cfstatus" id="cfstatus" class="form-control w_150px">
									<c:forEach items="${cardStatus}" var="cStatus" varStatus="status">
									<option value='<c:out value="${cStatus.fvalue}"/>'><c:out value="${cStatus.fkind3}"/></option>
									</c:forEach>
								</select>
							</td>
						</tr>
						
					</tbody>
				</table>
			</div>
			<!--버튼 -->
		   	<div class="right_btn mt_20">
		   	<c:if test="${userInfo.fissyn eq 'N'}">
		   	<c:if test="${userInfo.fustatus eq 'Y'}">
		        <button type="button" class="btn_middle color_basic" onclick="userInfoSave();">개인정보저장</button>
		        <button type="button" class="btn_middle color_gray" onclick="userInfoDel();">출입자삭제 </button>
			</c:if><c:if test="${userInfo.fustatus eq 'D'}">
				<button type="button" class="btn_middle color_basic" onclick="userInfoReco();">출입자복원</button>
				<c:if test="${sessionScope.loginVO.author_id eq '00001'}"><!-- 전체관리자 -->
				<button type="button" class="btn_middle color_gray" onclick="fnDropUser();">영구삭제 </button>
				</c:if>
			</c:if>				        
			</c:if>
		   	</div>
			<!--//버튼  -->
		</div>
		<!--------- //목록--------->
	</div>
</form>	
</div>
<jsp:include page="../frame/sub/tail.jsp" />
</body>