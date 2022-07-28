<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
$(function() {
	$(".title_tx").html("단말기 관리");
	$("#checkAll").click(function(){
		if($("#checkAll").prop("checked")){
			$("input[name=checkbx]").prop("checked", true);
		}else{
			$("input[name=checkbx]").prop("checked", false);
		}
	});

	$("#btnGate").on("click", function(event){
		$("#fgid").val("");
		$("#flname").val("");
		$("#fvname").val("");
		$("#fip01").val("");
		$("#fip02").val("");
		$("#fip03").val("");
		$("#fip04").val("");
		$("#fauthtype2").val("");
		$("#ftmtype").val("");
		$("#fsiteId").val("");
		
        $("#add-gate-modal").PopupWindow("open");
    });
	
	modalPopup ("add-gate-modal", "단말기 추가", 600, 600);
	modalPopup ("edit-gate-modal", "단말기 편집", 600, 400);

	$("#btnFileZipPw").on("click", function(event) {
        $("#center-zippw-modal").PopupWindow("open");
        $("#fzipdownpw").focus();
    });

	$("#btnAddClose").click(function(){
		$("#add-gate-modal").PopupWindow("close");	
	});
	
	$("#btnEditClose").click(function(){
		$("#txtEditSiteNm").val("");
		$("#txtEditSiteDesc").val("");
		$("#txtEditSortOrdr").val("");
		$("#txtEditSiteId").val("");
		
		$("#edit-gate-modal").PopupWindow("close");	
	});
});


function fnSearch () {
	f = document.frmSearch;
	f.action = "/deviceInfo/deviceMngmt.do";
	f.submit();
}

function pageSearch(page) {
	f = document.frmSearch;
	f.action = "/deviceInfo/deviceMngmt.do?srchPage=" + page;//"/userInfo/userMngmt.do?srchPage=" + page;
	showLoading();
	f.submit();
}

function fnCommValidation () {
	//단말기 체크박스 확인
	var tdArr = new Array();
	var rData = {};
	var checkbox = $("input[name=checkbx]:checked");
	if(checkbox.length < 1) {
		alert("전송할 단말기를 체크하여 주십시오.");
		return false;
	}
	return true;
}

function fnTableData () {
	var tdArr = new Array();
	var checkbox = $("input[name=checkbx]:checked");
	checkbox.each(function(i) {
		var tr = checkbox.parent().parent().eq(i);
		var td = tr.children();

		var fgid = td.eq(1).text();
		var flname = td.eq(2).text();
		var fip = td.eq(3).text();
		var fdf = td.eq(4).text();
		var fversion = td.eq(10).text();

		var rData = {};
		rData.fgid = fgid;
		rData.flname = flname;
		rData.fip = fip;
		rData.fdf = fdf;
		rData.fversion = fversion;
		tdArr.push(rData);
	});
	return tdArr;
}



function fnTerminalShMsgPop () {
	if(fnCommValidation ()) {
		$("#account-editor-modal").modal();
	}
}





function fnTerminalMatchTypeChange (deviceId, deviceNm, sObj, sVal) {
	if(confirm("[ "+ deviceNm + " ]의 인증방식을 [ "+  sObj.options[sObj.selectedIndex].text + " ]방식으로 \n\r변경하시겠습니까?")) {

		//if(!fnIsEmpty(sVal) && sVal == "Server") sVal = "Server";
		//else sVal = "Client";

		$.ajax({
			url: "<c:url value='/deviceInfo/updateDeviceMatchType.do' />",
			data: {	"deviceId":deviceId, "value":sObj.value},
			dataType:'json',
			traditional:true,
			type: "POST",
			success: function(response){
				if(!fnIsEmpty(response.rstCnt) && parseInt(response.rstCnt) > 0){
					//alert("인증방식이 변경되었습니다.");
					location.reload();
				}else if(parseInt(response.rstCnt) < 0){
					alert("변경에 실패했습니다.");
					return;
				}else{
					alert("변경에 실패했습니다.");
					return;
				}
			},
			error: function (jqXHR){
				alert("변경에 실패했습니다.");
				return;
			}
		});
	}
}

function fnTerminalAuthTypeChange (deviceId, deviceNm, sObj, sOrg) {
	if(confirm("[ "+ deviceNm + " ]의 출입인증방식을 [ "+ sObj.options[sObj.selectedIndex].text + " ]방식으로 \n\r변경하시겠습니까?")) {
		$.ajax({
			url: "<c:url value='/deviceInfo/updateAuthTypeChange.do' />",
			data: {	"deviceId":deviceId, "value":sObj.value},
			dataType:'json',
			traditional:true,
			type: "POST",
			success: function(response){
				if(!fnIsEmpty(response.rstCnt) && parseInt(response.rstCnt) > 0){
					//alert("인증방식이 변경되었습니다.");
					location.reload();
				}else if(parseInt(response.rstCnt) < 0){
					alert("변경에 실패했습니다.");
					return;
				}else{
					alert("변경에 실패했습니다.");
					return;
				}
			},
			error: function (jqXHR){
				alert("변경에 실패했습니다.");
				return;
			}
		});
		
	} else {
		sObj.value = sOrg;
	}
}


function cancel(){
	$("#fzipdownpw").val("");
	$("#center-zippw-modal").PopupWindow("close");
}

function fnClose(){
	$("#add-gate-modal").PopupWindow("close");
}

function fnGateAddSave () {
	if(!fnFormValueCheck("frmAdd")) return;

	var deviceNm = $("#deviceNm").val();
	var fip01 = $("#fip01").val();
	var fip02 = $("#fip02").val();
	var fip03 = $("#fip03").val();
	var fip04 = $("#fip04").val();
	
	var accAuthType = $("#accAuthType2").val();
	var faceAuthType = $("#faceAuthType2").val();
	var siteId = $("#siteId").val();
	var deviceFloor = $("#deviceFloor2").val();
	
	showLoading();
	$.ajax({
		url: "<c:url value='/deviceInfo/saveDeviceInfo.do' />",
		data: {
			"deviceNm": deviceNm,
			"fip01":fip01,
			"fip02":fip02,
			"fip03":fip03,
			"fip04": fip04,
			"accAuthType": accAuthType,
			"faceAuthType": faceAuthType,
			"siteId" : siteId,
			"deviceFloor" : deviceFloor
		},
		dataType:'json',
		traditional:true,
		type: "POST",
		success:function(returnData, status){
			if(status == "success") {
				if(returnData.result == "success") {
					location.reload();
					//$("#siteAddPopup").PopupWindow("close");
				} else {
					alert(returnData.message);
					hideLoading();
					$("#fvname").focus();
				}
			} else { 
				alert("ERROR!");
				return;
			}
		}
	});
}

function fnGateInfoChange(deviceId, deviceNm, floorDesc, fip, deviceFloor){
	$("#editDeviceId").html(deviceId);
	$("#editDeviceNm").html(deviceNm);
	$("#hidDeviceId").val(deviceId);
	//$("#flname2").val(flname);
	$("#editDeviceDesc").val(floorDesc);
	
	//$("#fauthtype3").val(fli);
	//$("#ftmtype2").val(flh);
	$("#editDeviceFloor").val(deviceFloor);
	
	if(!fnIsEmpty(fip)) {
		var fip2 = fip.split('.');
		$("#fip012").val(fip2[0]);
		$("#fip022").val(fip2[1]);
		$("#fip032").val(fip2[2]);
		$("#fip042").val(fip2[3]);
	}
	$("#edit-gate-modal").PopupWindow("open");
	
}

function fnGateChangeSave() {
	if(!fnFormValueCheck("frmEdit")) return;

	var deviceId = $("#hidDeviceId").val();
	var flname = $("#flname2").val();
	var fvname = $("#fvname2").val();
	var fauthtype = $("#fauthtype3").val();
	var ftmtype = $("#ftmtype2").val();
	
	var editDeviceDesc = $("#editDeviceDesc").val();
	var editDeviceFloor = $("#editDeviceFloor").val();
	
	var fip01 = $("#fip012").val();
	var fip02 = $("#fip022").val();
	var fip03 = $("#fip032").val();
	var fip04 = $("#fip042").val();

	if(confirm("저장하시겠습니까?")){
		$.ajax({
			type:"POST",
			url:"<c:url value='/deviceInfo/updateDeviceInfo.do' />",
			data:{
				"deviceId": deviceId,
				"flname": flname,
				"fvname":fvname,
				"fauthtype":fauthtype,
				"ftmtype":ftmtype,
				"fip01": fip01,
				"fip02": fip02,
				"fip03": fip03,
				"fip04": fip04,
				"editDeviceDesc" : editDeviceDesc,
				"editDeviceFloor" : editDeviceFloor
			},
			dataType:'json',
			//timeout:(1000*30),
			success:function(returnData, status){
				if(status == "success") {
					location.reload();
					//$("#siteAddPopup").PopupWindow("close");
				}else{ alert("ERROR!");return;}
			}
		});
    }
}


function fnvalichk (event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) return;
	else {
		var tVal = event.target.value;
		var regx = /^[0-9]{0,3}$/g;
		if(!fnIsEmpty(tVal) && !regx.test(tVal)) {
			tVal = tVal.replace(/[^0-9]/g,"");
			event.target.value = fnIsEmpty(tVal)?"":tVal.substring(0,3);
		}
	}
}

function fnfgidchk (event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) return;
	else {
		var tVal = event.target.value;
		var regx = /^[0-9]{0,10}$/g;
		if(!fnIsEmpty(tVal) && !regx.test(tVal)) {
			tVal = tVal.replace(/[^0-9]/g,"");
			event.target.value = fnIsEmpty(tVal)?"":tVal.substring(0,10);
		}
	}
}



//gid 체크
function fnGidCheck(){
	var fgid = $("#fgid").val();
	if(!fnIsEmpty(fgid) && fgid.length > 1 && fgid.length < 20) {
		$.ajax({
			type:"POST",
			url:"<c:url value='/basicInfo/fgidDplctCnfirm.do' />",
			data:{
				"checkId": fgid
			},
			dataType:'json',
			//timeout:(1000*30),
			success:function(returnData, status){
				if(status == "success") {
					if(returnData.usedCnt > 0 ){
						//사용할수 없는 아이디입니다.
						$("#idConfirm").html("<font color='#ff5a00'>사용할 수 없는 아이디 입니다.</font>");
					}else{
						//사용가능한 아이디입니다.
						$("#idConfirm").html("<font color='#009fc0'>사용가능한 아이디 입니다.</font>");
						$("#fgid").attr("readonly", true);
						$("#idCheck").val("Y");
					}
				} else { alert("ERROR!");return;}
			}
		});
		
	} else {
		if( fnIsEmpty(fsiteid) ) {
			alert("아이디를 입력하세요.");
		} else {
			alert("아이디를 5자이상 입력하세요.");
		}
		$("#fsiteid").focus();
	}
}

//계정사용유무변경
function fnSiteFuseynChangeSave(deviceId, useYn) {
	var useYn2 = "";
	var confirmTxt = "";
	if(!fnIsEmpty(useYn) && useYn == 'Y'){
		confirmTxt = "사용안함으로 변경하시겠습니까?";
		useYn2 = "N";
	}else{
		confirmTxt = "사용중으로 변경하시겠습니까?";
		useYn2 = "Y";
	}

	if(confirm(confirmTxt)){
		$.ajax({
			type:"POST",
			url:"<c:url value='/deviceInfo/updateDeviceUse.do' />",
			data:{
				"deviceId": deviceId,
				"useYn": useYn2
			},
			dataType:'json',
			//timeout:(1000*30),
			success:function(returnData, status){
				if(status == "success") {
					location.reload();
				}else{ alert("ERROR!");return;}
			}
		});
    }
}
</script>
<form id="frmSearch" name="frmSearch" method="post">
<input type="hidden" id="chkValueArray" name="chkValueArray" value=""/>
<input type="hidden" id="chkTextArray" name="chkTextArray" value="" >
<input type="hidden" id="fdownresn" name="fdownresn" value=""/>
<div class="search_box mb_20">
	<div class="search_in">
		<div class="comm_search">
			<select name="search_item_center" id="search_item_center" size="1" class="w_150px input_com mr_5" onchange="getAreaList(this.value)">
				<option value="0" selected disabled >센터</option>
				<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
					<option value='<c:out value="${cCombo.siteId}"/>'<c:if test="${searchItemCenter eq cCombo.siteId}">selected</c:if>>
						<c:out value="${cCombo.siteNm}" />
					</option>
				</c:forEach>
			</select>
			<input type="text" id="srchCondWord" name="srchCondWord" class="w_250px input_com l_radius_no" placeholder="단말기 이름/상세를 입력해 주세요" value='<c:out value="${srchCondWord}"/>'>
			<div class="search_btn" onclick="fnSearch();"></div>
		</div>
		<div class="comm_search ml_10">
	    <div class="title">출입인증 방식</div>
			<select name="searchAccAuthType" id="searchAccAuthType" size="1" class="w_100px input_com">
				<option value="0" selected disabled >전체</option>
				<c:forEach items="${authCombo}" var="auth" varStatus="status">
					<option value='<c:out value="${auth.fkind3}"/>' <c:if test="${searchAccAuthType eq auth.fkind3}">selected</c:if>><c:out value="${auth.fvalue}" /></option>
				</c:forEach>
	        </select>
    	</div>
    	
    	<div class="comm_search ml_10">
	    <div class="title">얼굴인증 방식</div>
			<select name="searchFaceAuthType" id="searchFaceAuthType" size="1" class="w_100px input_com">
				<option value="0" selected disabled >전체</option>
				<c:forEach items="${certCombo}" var="cert" varStatus="status">
				<option value='<c:out value="${cert.fkind3}"/>' <c:if test="${searchFaceAuthType eq cert.fkind3}">selected</c:if>><c:out value="${cert.fvalue}" /></option>
				</c:forEach>
			</select>
    	</div>
    	
    	<div class="comm_search ml_10">
	    <div class="title">층 위치</div>
		<select name="searchFloor" id="searchFloor" size="1" class="w_100px input_com mr_5">
			<option value="0" selected disabled >전체</option>
			<c:forEach items="${floorCombo}" var="cCombo" varStatus="status">
				<option value='<c:out value="${cCombo.idx}"/>'<c:if test="${searchFloor eq cCombo.idx}">selected</c:if>>
					<c:out value="${cCombo.floorNm}" />
				</option>
			</c:forEach>
		</select>
    	</div>
    	
    	<div class="comm_search  ml_10">
	    <div class="title">사용여부</div>
		<select name="srchUseYn" id="srchUseYn" size="1" class="w_100px input_com">
			<option value="ALL" selected disabled>전체</option>
            <option value="Y" <c:if test="${srchUseYn eq 'Y'}">selected</c:if>>사용</option>
            <option value="N" <c:if test="${srchUseYn eq 'N'}">selected</c:if>>사용안함</option>
		</select>
    	</div>
	</div>
</div>
<!--//검색박스 -->
<!--------- 목록--------->
<div class="com_box ">
	<div class="totalbox" style="width: 50%;">
		<div class="r_btnbox  mb_10">
		</div>
	</div>
	<div class="r_btnbox  mb_10">
		<button type="button" class="btn_middle color_basic" id="btnGate">추가</button>
	</div>
	<!--버튼 -->

	<!--//버튼  -->
	<!--테이블 시작 -->
	<div class="tb_outbox">
		<table class="tb_list">
			<col width="" />
			<col width="" />
			<col width="" />
			<col width="" />
			<col width="" />
			<col width="11%" />
			<col width="10%" />
			<col width="30%" />
			<col width="" />
			<col width="" />
			<thead>
				<tr>
					<th>Device ID</th>
					<th>단말기이름</th>
					<th>설치 위치(floor)</th>
					<th>단말기상세</th>
					<th>IP</th>
					<th>출입인증방식</th>
					<th>얼굴인증방식</th>
					<th>펌웨어버전</th>
					<th>사용유무</th>
					<th>편집</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${gateList}" var="gList" varStatus="status">
					<tr>
						<td><c:out value="${gList.deviceId}" /></td>
						<td><c:out value="${gList.deviceNm}" /></td>
						
						<td>
							<c:set var = "floor" value = "${gList.deviceFloor}" />
							<c:choose>
								<c:when test = "${fn : contains(floor, '-')}">
									지하 <c:out value="${fn:replace(floor, '-', '')}"/> 층
								</c:when>
								<c:otherwise>
									<c:if test="${not empty floor}"> 
										<c:out value="${gList.deviceFloor}"/> 층
									</c:if>
								</c:otherwise>
							</c:choose>
						</td>
						
						<td><c:out value="${gList.deviceDesc}" /></td>
						<td><c:out value="${gList.deviceIp}" /></td>
						<td style="padding: 7px 10px">
							<select name="aauthtype" id="aauthtype" size="1" class="w_100p input_com" onchange="fnTerminalAuthTypeChange('${gList.deviceId}', '${gList.deviceNm}', this, '${gList.deviceNm}')">
							<c:forEach items="${authCombo}" var="auth" varStatus="status">
								<option value='<c:out value="${auth.fkind3}"/>' <c:if test="${gList.accAuthType eq auth.fkind3}">selected</c:if>><c:out value="${auth.fvalue}" /></option>
							</c:forEach>
	                        </select>
						</td>
						<td style="padding: 7px 10px">
							<select name="fauthtype" id="fauthtype" size="1" class="w_100p input_com" onchange="fnTerminalMatchTypeChange('${gList.deviceId}','${gList.deviceNm}',this, '${gList.deviceNm}')">
								<c:forEach items="${certCombo}" var="cert" varStatus="status">
								<option value='<c:out value="${cert.fkind3}"/>' <c:if test="${gList.faceAuthType eq cert.fkind3}">selected</c:if>><c:out value="${cert.fvalue}" /></option>
								</c:forEach>
							</select>
						</td>
						<td><c:out value="${gList.deviceVersion}" /></td>
						<td>
							<c:if test="${gList.useYn eq 'Y'}"><button type="button" class="btn_small color_basic" onclick="fnSiteFuseynChangeSave('<c:out value="${gList.deviceId}"/>','<c:out value="${gList.useYn}"/>');">사용중</button></c:if>
							<c:if test="${gList.useYn eq 'N'}"><button type="button" class="btn_small color_gray" onclick="fnSiteFuseynChangeSave('<c:out value="${gList.deviceId}"/>','<c:out value="${gList.useYn}"/>');">사용안함</button></c:if>
						</td>
						<td><button type="button" class="btn_small color_basic" data-toggle="modal" onclick="fnGateInfoChange('<c:out value="${gList.deviceId}"/>','<c:out value="${gList.deviceNm}"/>','<c:out value="${gList.deviceDesc}"/>','<c:out value="${gList.deviceIp}"/>','<c:out value="${gList.deviceFloor}"/>')">편집</button></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<!--------- //목록--------->
	<!-- 페이징 -->
	<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
	<!-- /페이징 -->
</div>
</form>

<!-- modal : 등록 -->
<div id="add-gate-modal" class="example_content">
<form id="frmAdd" name="frmAdd" method="post">
	<div class="popup_box">
		<!--테이블 시작 -->
		<div class="tb_outbox mb_20">
			<table class="tb_write_02 tb_write_p1">
				<col width="30%" />
				<col width="70%" />
				<tbody>
					<tr>
						<th>단말기 코드 <span class="font-color_H">*</span></th>
						<td>
							<input type="text" id="deviceNm" name="deviceNm" maxlength="20" class="w_190px input_com" check="text" checkName="단말기 코드"/>
						</td>
					</tr>
					<tr>
						<th>단말기 위치(floor)</th>
						<td>
							<select name="deviceFloor2" id="deviceFloor2" size="1" class="w_100px input_com mr_5">
								<option value="0" selected disabled >전체</option>
								<c:forEach items="${floorCombo}" var="fCombo" varStatus="status">
									<option value='<c:out value="${fCombo.idx}"/>'><c:out value="${fCombo.floorNm}" /></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>단말기 설명<span class="font-color_H">*</span></th>
						<td>
							<input type="text" id="deviceDesc" name="deviceDesc" maxlength="20" class="w_190px input_com" check="text" checkName="단말기 위치"/>
						</td>
					</tr>
					<tr>
						<th>IP <span class="font-color_H">*</span></th>
						<td>
							<input type="text" id="fip01" name="fip01" maxlength="3" class="w_70px input_com fl" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
							<input type="text" id="fip02" name="fip02" maxlength="3" class="w_70px input_com fl ml_5" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
							<input type="text" id="fip03" name="fip03" maxlength="3" class="w_70px input_com fl ml_5" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
							<input type="text" id="fip04" name="fip04" maxlength="3" class="w_70px input_com fl ml_5" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
						</td>
					</tr>
					<tr>
						<th>출입인증방식 <span class="font-color_H">*</span></th>
						<td>
							<select name="accAuthType2" id="accAuthType2" size="1" class="w_190px input_com" check="text" checkName="출입인증방식">
								<option value="">선택</option>
								<c:forEach items="${authCombo}" var="auth" varStatus="status">
								<option value='<c:out value="${auth.fkind3}"/>'><c:out value="${auth.fvalue}" /></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>얼굴인증방식 <span class="font-color_H">*</span></th>
						<td>
							<select name="faceAuthType2" id="faceAuthType2" size="1" class="w_190px input_com" check="text" checkName="얼굴인증방식">
								<option value="">선택</option>
								<c:forEach items="${certCombo}" var="cert" varStatus="status">
								<option value='<c:out value="${cert.fkind3}"/>'><c:out value="${cert.fvalue}" /></option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>사이트 <span class="font-color_H">*</span></th>
						<td>
							<select name="siteId" id="siteId" size="1" class="w_190px input_com" check="text" checkName="사이트">
								<option value="">선택</option>
								<c:forEach items="${siteCombo}" var="cCombo" varStatus="status">
								<option value='<c:out value="${cCombo.siteId}"/>'><c:out value="${cCombo.siteNm}" /></option>
								</c:forEach>
							</select>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<!--버튼 -->
	   	<div class="r_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" onclick="fnGateAddSave();">저장</button>
				<button type="button" class="bk_color comm_btn mr_5" id="btnAddClose">취소</button>
			</div>
		</div>
		<!--//버튼  -->
	</div>
</form>
</div>

<!-- modal : 편집 -->
<div id="edit-gate-modal" class="example_content">
<form id="frmEdit" name="frmEdit" method="post">
	<div class="popup_box">
		<!--테이블 시작 -->
		<div class="tb_outbox mb_20">
			<input type="hidden" id="hidDeviceId" name="hidDeviceId"/>
			<input type="hidden" id="fvname2" name="fvname2"/>
			<table class="tb_write_02 tb_write_p1">
				<col width="25%" />
				<col width="75%" />
				<tbody>
				<tr>
					<th>Device Id</th>
					<td id="editDeviceId"></td>
				</tr>
				<tr>
					<th>단말기 코드 <span class="font-color_H">*</span></th>
					<td id="editDeviceNm"></td>
				</tr>
				<tr>
					<th>단말기 상세</th>
					<td>
						<input type="text" id="editDeviceDesc" name="editDeviceDesc" maxlength="50" class="w_190px input_com" check="text" checkName="단말기 위치" />
					</td>
				</tr>
				<tr>
					<th>단말기 위치(floor)</th>
					<td>
						<select name="editDeviceFloor" id="editDeviceFloor" size="1" class="w_100px input_com mr_5">
							<option value="0" selected disabled >전체</option>
							<c:forEach items="${floorCombo}" var="fCombo" varStatus="status">
								<option value='<c:out value="${fCombo.floor}"/>'><c:out value="${fCombo.floorNm}" /></option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<th>IP <span class="font-color_H">*</span></th>
					<td>
						<input type="text" id="fip012" name="fip012" maxlength="3" class="w_70px input_com fl" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
						<input type="text" id="fip022" name="fip022" maxlength="3" class="w_70px input_com fl ml_5" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
						<input type="text" id="fip032" name="fip032" maxlength="3" class="w_70px input_com fl ml_5" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
						<input type="text" id="fip042" name="fip042" maxlength="3" class="w_70px input_com fl ml_5" onkeyup="fnvalichk(event)" check="text" checkName="IP"/>
					</td>
				</tr>
				
			</tbody>
			</table>
		</div>
		<!--버튼 -->
	   	<div class="r_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" onclick="fnGateChangeSave();">저장</button>
				<button type="button" class="bk_color comm_btn mr_5" id="btnEditClose">취소</button>
			</div>
		</div>
		<!--//버튼  -->
	</div>
</form>
</div>