<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<jsp:include page="../frame/sub/head.jsp" />
<style>
	/* 카드 입력란 */
	input#fcdno::placeholder{
		color: #C10909;
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
	
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "350px",
		scrolling: "yes"
	});
	$("#TableID2").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "350px",
		scrolling: "yes"
	});
	$("#TableID3").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "350px",
		scrolling: "yes"
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
	
	$("#add_arrow").click(function() {
		$("input[name=chkCd]:checked").each(function(){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			tag = tag.replace("chkCd","chkUserCd");
			$("#tdUserGroup").append(tag);
		});

		var ckd = $("input[name=chkCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#delete_arrow").click(function(){
		$("input[name=chkUserCd]:checked").each(function(i){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			tag = tag.replace("chkUserCd","chkCd");
			$("#tbTotalGroup").append(tag);
		});

		var ckd = $("input[name=chkUserCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkUserCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#authCheckAll").click(function(){
		if($("#authCheckAll").prop("checked")){
			$("input[name=chkCd]").prop("checked", true);
		}else{
			$("input[name=chkCd]").prop("checked", false);
		}
	});

	$("#userAuthCheckAll").click(function(){
		if($("#userAuthCheckAll").prop("checked")){
			$("input[name=chkUserCd]").prop("checked", true);
		}else{
			$("input[name=chkUserCd]").prop("checked", false);
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

	modalPopup ("groupDetailPopup", "권한 그룹 상세", 500, 550);

});

function fnTabChange(gb) {
	if(gb == 'user') {
		$('.tabs li:last').removeClass('selected');
		$('#view2').css('display', 'none');		
		$('.tabs li:first').addClass('selected');
		$('#view1').css('display', 'block');
	} else if(gb == 'card') {
		$('.tabs li:first').removeClass('selected');
		$('#view1').css('display', 'none');
		$('.tabs li:last').addClass('selected');
		$('#view2').css('display', 'block');
	}
}

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

// 신규저장
function fnAddUser() {
	//if(!fnFormValueCheck("frmDetail")) return;
	
	/* if(fnIsEmpty($("#fuid").val())) {
		alert("고유번호은(는) 필수입력 항목입니다.");
		fnTabChange('user');
		$("#fuid").focus();
		return;
	} */
	if(fnIsEmpty($("#funm").val())) {
		alert("이름은(는) 필수입력 항목입니다.");
		fnTabChange('user');
		$("#funm").focus();
		return;
	}
	if($("#futype").val() == "3") { //방문객카드
		if(fnIsEmpty($("#fvisitnum").val())) {
			alert("출입자타입이 방문객카드인 경우, 방문객카드번호는 필수입력 항목입니다.");
			fnTabChange('user');
			$("#fvisitnum").focus();
			return;
		} else {
			var str = Number($("#fvisitnum").val());
			if(str == 0) {
				alert("방문객카드번호를 정확하게 입력하세요.");
				fnTabChange('user');
				$("#fvisitnum").focus();
				return;
			}
		}
	}
	/* if(fnIsEmpty($("#fpartnm1").val())) {
		alert("회사은(는) 필수입력 항목입니다.");
		fnTabChange('user');
		$("#fpartnm1").focus();
		return;
	}
	if(fnIsEmpty($("#fpartnm2").val())) {
		alert("부서은(는) 필수입력 항목입니다.");
		fnTabChange('user');
		$("#fpartnm2").focus();
		return;
	}
	if(fnIsEmpty($("#hp_no").val())) {
		alert("휴대전화은(는) 필수입력 항목입니다.");
		fnTabChange('user');
		$("#hp_no").focus();
		return;
	} else {
		if(!isCelluar($("#hp_no").val())) {
			alert("휴대전화 형식에 맞지 않습니다.");
			fnTabChange('user');
			$("#hp_no").focus();
			return;
		}
	} */
	if(!fnIsEmpty($("#hp_no").val()) && !isCelluar($("#hp_no").val())) {
		alert("휴대전화 형식에 맞지 않습니다.");
		fnTabChange('user');
		$("#hp_no").focus();
		return;
	}	
	if(!fnIsEmpty($("#ftel").val()) && !isCall($("#ftel").val())) {
		alert("전화번호 형식에 맞지 않습니다.");
		fnTabChange('user');
		$("#ftel").focus();
		return;
	}
	if(fnIsEmpty($("#startDate").val())) {
		alert("카드시작일은(는) 필수입력 항목입니다.");
		fnTabChange('card');
		$("#startDate").focus();
		return;
	}
	if(fnIsEmpty($("#expireDate").val())) {
		alert("카드만료일은(는) 필수입력 항목입니다.");
		fnTabChange('card');
		$("#expireDate").focus();
		return;
	}

	// 권한그룹
	var arr = [];
	$("input[name=chkUserCd]").each(function(i){
		arr.push( $(this).val() );
	});
	
	if($("#futype").val() != "3" && fnIsEmpty(arr)) {
		alert("권한그룹을 추가하세요.");
		fnTabChange('card');
		return;
	}
	
	if(confirm("출입자 정보를 저장하시겠습니까?")) {
		var form = $('#frmDetail')[0];
		var formData = new FormData(form);

		// 출입자
		formData.append('funm', $("#funm").val());
		formData.append('futype', $("#futype").val());
		formData.append('fvisitnum', $("#fvisitnum").val());
		formData.append('fpartnm1', $("#fpartnm1").val());
		formData.append('fpartnm2', $("#fpartnm2").val());
		formData.append('fpartnm3', $("#fpartnm3").val());
		formData.append('ftel', $("#ftel").val());
		formData.append('fcarno', $("#fcarno").val());
		formData.append('hp_no', $("#hp_no").val());
		// 카드, 권한그룹
		formData.append('fcdno', $("#fcdno").val());
		formData.append('fstatus', $("#fstatus").val());
		formData.append('fsdt', $("#startDate").val());
		formData.append('fedt', $("#expireDate").val());
		formData.append("arraryGroup", arr);
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
			url: "<c:url value='/userInfo/addUserInfoNew.do' />",
			type: "post",
			data: formData,
			dataType: 'json',
			processData: false,
			contentType: false,
			beforeSend : function(xhr, opts) {},
			success: function(response) {
				if(response.cardSaveCnt > 0) {
					alert("저장되었습니다.");
					<c:if test="${sessionScope.loginVO.author_id ne '00008'}">
					opener.userSearch();
					self.close();					
					</c:if><c:if test="${sessionScope.loginVO.author_id eq '00008'}">
					fnListUser();
					</c:if>					
				} else if(response.cardSaveCnt == -4){
					alert("카드번호가 중복되었습니다. 다시 입력하여 주십시오.\n현재 " + response.result.funm + "이(가) 사용중입니다.");
					$("#fcdno").val("");
					$("#fcdno").focus();
				} else if(response.cardSaveCnt == -2) {
					alert("고유번호가 중복되었습니다. 다시 입력하여 주십시오.");
					$("#fuid").val("");
					$("#fuid").focus();
				} else if(response.cardSaveCnt == -11) {
					alert("방문객카드번호를 입력하여 주십시오.");
					fnTabChange('user');
					$("#fvisitnum").focus();
				} else if(response.cardSaveCnt == -12) {
					alert("방문객카드번호가 중복되었습니다. 다시 입력하여 주십시오.");
					fnTabChange('user');
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

function fnListUser() {
	$("#hidSortName").val("fregdt");
	$("#hidSortNum").val("2");
	frmList.action = "<c:url value='/userInfo/userMngmt2.do'/>";
	frmList.submit();
}

function fnDetail(authorGroupId, authorGroupNm) {
	if(!fnIsEmpty(authorGroupId)) {
		$("#labGroupId").html("권한코드 : "+authorGroupId+" / 권한명 : "+authorGroupNm);
		
		//search
		$.ajax({
			type :"POST",
			url :"<c:url value='/authorInfo/getAuthorGroupDetail.do'/>",
			data : { "selAuthorGroupId": authorGroupId, "siteId" : "${sessionScope.loginVO.site_id}" },
			dataType : 'json',
			//timeout:(1000*30),
			success : 
				function(returnData, status) {
					if( status == "success") {
						var authorGroupDetailList = "";
						if( returnData.detailList != null && returnData.detailList.length > 0 ) {
							for(var i=0; i < returnData.detailList.length; i++ ){
								authorGroupDetailList = authorGroupDetailList + "<tr><td>" + returnData.detailList[i].fgid + "</td><td>" + returnData.detailList[i].fvname +"</td><td>" + returnData.detailList[i].flname +"　</td></tr>";
							}
						}
					$("#tbGroupDtl").html(authorGroupDetailList);
					$("#groupDetailPopup").PopupWindow("open");
				} else { alert("ERROR!");return;}
			}
		});
	}
}
</script>
<body>
<div class="popup_head_box" style="display: list-item;"> <span id="popupNm"></span>
<c:if test="${sessionScope.loginVO.author_id ne '00008'}">
  	<div class="close">
  		<a href="javascript:void()" onclick="javascript:window.close()"><img src="/img/close_icon.png" alt="닫기" class="mt_10"/></a>
  	</div>
</c:if>  	
</div>
<div class="popup_box">
	<div class="tab_box2 mb_20">
		<ul class="tabs" data-persist="false">
			<li><a href="#view1"><img src="/img/teb_icon1_on.png" alt=""/>출입자 정보</a></li>
			<li><a href="#view2"><img src="/img/teb_icon3_on.png" alt=""/>카드 정보</a></li>
		</ul>
	</div>
	<div class="tabcontents">
		<form id="frmDetail" name="frmDetail" method="post" enctype="multipart/form-data" onsubmit="return false;">
		<div id="view1">
			<div class="box_w2 mt_20">
				<!--------- 목록--------->
				<div class="box_w2_1">
					<!--테이블 시작 -->
					<div class="com_box">
						<img id="imgView" src="/img/photo_01.png" alt=""/>
						<canvas id="myCanvas" style="display:none;" width="320" height="288"></canvas>
						<input type="hidden" name="imgfuid"/>
					</div>
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
				</div>
				<!--------- 목록--------->
				<!--------- 목록--------->
				<div class="box_w2_2">
					<!--테이블 시작 -->
					<div class="tb_outbox">
						<table class="tb_write_02 tb_write_p1">
							<col width="17%" />
							<col width="30%" />
							<col width="17%" />
							<col width="36%" />
							<tbody>
								<%-- <tr>
									<th>고유번호 <span class="font-color_H">*</span></th>
									<td><input type="text" id="fuid" name="fuid" maxlength="<spring:eval expression="@property['Globals.fidDigit']" />" class="w_100p input_com" onkeyup="fnvalichk2(event)"></td>
									<td></td>
									<td></td>
								</tr> --%>
								<tr>
									<th>이름 <span class="font-color_H">*</span></th>
									<td><input type="text" id="funm" name="funm" maxlength="30" value="" class="w_100p input_com" check="text" checkName="성명"></td>
									<th>출입자타입 <span class="font-color_H">*</span></th>
									<td>
										<select name="futype" id="futype" size="1" class="w_55p input_com mr_5" check="text" checkName="출입자타입">
										<c:forEach items="${userType}" var="uType" varStatus="status">
											<option value='<c:out value="${uType.fvalue}"/>'><c:out value="${uType.fkind3}"/></option>
										</c:forEach>
										</select>
										<input type="text" name="fvisitnum" id="fvisitnum" class="w_40p input_com onlyNumber" maxlength="3" placeholder="방문객카드번호" disabled>
									</td>
								</tr>
								<tr>
									<th>회사</th>
									<td><input type="text" id="fpartnm1" name="fpartnm1" maxlength="100" class="w_100p input_com" ></td>
									<th>휴대전화</th>
									<td><input type="text" id="hp_no" name="hp_no" maxlength="13" class="w_100p input_com"  placeholder="예) 010-1234-5678"></td>
								</tr>
								<tr>
									<th>부서 </th>
									<td><input type="text" id="fpartnm2" name="fpartnm2" maxlength="100" class="w_100p input_com"></td>
									<th>전화번호</th>
									<td><input type="text" id="ftel" name="ftel" maxlength="13" class="w_100p input_com"  placeholder="예) 02-1234-5678"></td>
								</tr>
								<tr>
									<th>직급</th>
									<td><input type="text" id="fpartnm3" name="fpartnm3" maxlength="100" class="w_100p input_com"></td>
									<th>차번호</th>
									<td><input type="text" id="fcarno" name="fcarno" maxlength="100" class="w_100p input_com"></td>
								</tr>
								<tr>
									<th>기타1</th>
									<td colspan="3"><input type="text" id="fetc1" name="fetc1" maxlength="200" class="w_100p input_com"></td>
								</tr>
								<tr>
									<th>기타2</th>
									<td colspan="3"><input type="text" id="fetc2" name="fetc2" maxlength="200" class="w_100p input_com"></td>
								</tr>
							</tbody>
						</table>
					</div>
					<!--버튼 -->
					<%-- khlee_save_user <div class="right_btn mt_20">
						<button type="button" class="btn_middle color_basic" onclick="userInfoSave();">저장 </button>
						<button type="button" class="btn_middle color_gray">취소 </button>
					</div> --%>
					<!--//버튼  -->
				</div>
				<!--------- //목록--------->
			</div>
		</div>
		<div id="view2">
			<!--검색박스 -->
			<div class="search_box_popup mb_10">
				<div class="search_in">
					<div class="comm_search mr_20">
						<div class="title">카드번호 <span class="font-color_H">*</span></div>
						<input type="text" id="fcdno" name="fcdno" maxlength="<spring:eval expression="@property['Globals.cardDigit']" />"  class="w_142px input_com" placeholder="미입력시 자동발번" onkeyup="fnvalichk(event)">
					</div>
					<%-- <div class="comm_search mr_10">
						<button type="button"  class="comm_btn">중복체크</button>
					</div>
					<div class="comm_search mr_20">
						<button type="button" class="comm_btn color_gray">초기화</button>
					</div> --%>
					<div class="comm_search mr_20">
						<label  for="search-from-date" class="title">시작일 <span class="font-color_H">*</span></label>
						<input type="text" class="input_datepicker w_142px" name="startDate" id="startDate" readonly="readonly" check="text" checkName="카드시작일">
					</div>
					<div class="comm_search mr_20">
						<label  for="search-to-date" class="title">만료일 <span class="font-color_H">*</span></label>
						<input type="text" class="input_datepicker w_142px" name="expireDate" id="expireDate" readonly="readonly" check="text" checkName="카드만료일">
					</div>
					<div class="comm_search mr_20">
						<label  for="search-to-date" class="title">카드상태 <span class="font-color_H">*</span></label>
						<select name="fstatus" id="fstatus" size="1" class="w_142px input_com">
							<c:forEach items="${cardStatus}" var="cStatus" varStatus="status">
								<option value='<c:out value="${cStatus.fvalue}"/>' <c:if test="${cStatus.fvalue eq 'Y'}">selected</c:if>><c:out value="${cStatus.fkind3}"/></option>
							</c:forEach>
						</select>
					</div>
				</div>
			</div>
			<!--//검색박스 --> 	
			<div class="box_w3">
				<!--------- 전체 권한그룹  --------->
				<div class="box_w3_1">
					<div class="totalbox">
						<div class="title_s w_50p fl" style="margin-bottom:7px;">
							<img src="/img/title_icon1.png" alt="" />전체 권한 그룹
						</div>
						<!-- <div class="r_searhbox  mb_5">
							<div class="comm_search">
								<input type="text" class="w_200px input_com l_radius_no" placeholder="검색어를 입력해 주세요">
								<div class="search_btn"></div>
							</div>
						</div> -->
					</div>
					<!--테이블 시작 -->
					<div class="com_box">
						<div class="tb_outbox ">
							<table class="tb_list2" id="TableID1">
								<col width="10%" />
								<col width="25%" />
								<col width="45%" />
								<col width="20%" />
								<thead>
									<tr>
										<th><input type="checkbox" id="authCheckAll"></th>
										<th>권한 코드</th>
										<th>권한 명</th>
										<th>권한 상세</th>
									</tr>
								</thead>
								<tbody id="tbTotalGroup">
									<c:forEach items="${authList}" var="totList" varStatus="status">
										<tr>
											<td><input type="checkbox" name='chkCd' value='<c:out value="${totList.authorGroupId}"/>'></td>
											<td><c:out value="${totList.authorGroupId}"/></td>
											<td><c:out value="${totList.authorGroupNm}"/></td>
											<td>
												<button type="button" class="btn_small color_basic" data-toggle="modal" onclick="fnDetail('${totList.authorGroupId}', '${totList.authorGroupNm}')" id="groupDetail">상세</button>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<!--------- //전체 권한그룹  --------->
				<!--------- 화살표 이동   --------->
				<div class="box_w3_2">
					<div class="btn_box">
						<img src="/img/ar_r.png" alt="" id="add_arrow"/>
					</div>
					<div class="btn_box">
						<img src="/img/ar_l.png" alt="" id="delete_arrow"/>
					</div>
				</div>
				<!--------- //화살표 이동   --------->
				<!--------- 카드에 적용될 권한그룹   --------->
				<div class="box_w3_3">
					<div class="totalbox">
						<div class="title_s w_300px fl">
							<img src="/img/title_icon1.png" alt="" />출입자에 적용될 권한그룹
						</div>
						<%-- khlee_auth_save <div class="right_btn">
							<button type="button" class="btn_middle color_basic" onclick="saveAuthMenu()">저장</button>
						</div> --%>
					</div>
					<!--테이블 시작 -->
					<div class="com_box mt_5">
						<div class="tb_outbox">
							<table class="tb_list2" id="TableID2">
								<col width="10%" />
								<col width="25%" />
								<col width="45%" />
								<col width="20%" />
								<thead>
									<tr>
										<th><input type="checkbox" id="userAuthCheckAll"></th>
										<th>권한 코드</th>
										<th>권한 명</th>
										<th>권한 상세</th>
									</tr>
								</thead>
								<tbody id="tdUserGroup"/>
							</table>
						</div>
					</div>
				</div>
				<!--------- //카드에 적용될 권한그룹--------->
			</div>
			<!-- //가로 3칸 --->
		</div>
	</form>	
	</div>
	<div class="right_btn mt_20"> 
		<button type="button" class="btn_middle color_basic" onclick="fnAddUser()">저장 </button>
		<c:if test="${sessionScope.loginVO.author_id eq '00008'}">
		<button type="button" class="btn_middle color_basic" onclick="fnListUser()">출입자목록 </button>
		</c:if>
	</div>
</div>

<form id="frmList" name="frmList" method="post" target="_self">
<input type="hidden" id="hidSortName" name="hidSortName">
<input type="hidden" id="hidSortNum" name="hidSortNum">
</form>

<!-- modal : groupDetailPopup -->
<div id="groupDetailPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<label style="font-size: 14px;" id="labGroupId"></label>
			</div>
		</div>
		<div class="com_box">
			<div class="tb_outbox">
				<table class="tb_list2" id="TableID3">
					<col width="25%" />
					<col width="35%" />
					<col width="40%" />
					<thead>
						<tr>
							<th>GID</th>
							<th>단말기코드</th>
							<th>단말기위치</th>
						</tr>
					</thead>
					<tbody id="tbGroupDtl"></tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<jsp:include page="../frame/sub/tail.jsp" />
</body>