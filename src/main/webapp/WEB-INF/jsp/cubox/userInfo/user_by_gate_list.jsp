<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">
$(function() {
	$(".title_tx").html("단말기별 출입자");	
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "440px",
		scrolling: "yes"
	});
	
	$('#srchFromDt').datetimepicker({
		timepicker:false,
		format:'Y-m-d'
	});
	$('#srchToDt').datetimepicker({
		timepicker:false,
		format:'Y-m-d'
	});
	
	$("#btnSearch").click(function(){
		userSearch();
	});
	
	$("#srchRecPerPage").change(function(){
		userSearch();
	});	

	//excel download
	$("#excelDownload").on("click", function(event){
		<c:if test="${userInfoList == null || fn:length(userInfoList) == 0}">
		alert("엑셀 다운로드할 자료가 없습니다.");
		return;
		</c:if>		
		
		$("#excelDownloadPopup").PopupWindow("open");
	});
	
	//excel download
	/* $("#authList").on("click", function(event){
		$("#gateListPopup").PopupWindow("open");
	}); */	

	$("input[name=checkAll]").click(function() {
		if( $(this).prop("checked") ){
			$("input[name=excelColumn]").prop("checked", true);
		} else {
			$("input[name=excelColumn]").prop("checked", false);
		}
	});
	
	$("input[name=chkGateAll]").click(function() {
		if( $(this).prop("checked") ){
			$("input[name=chkGate]").prop("checked", true);
		} else {
			$("input[name=chkGate]").prop("checked", false);
		}
	});

	//modal popup set
	modalPopup ("excelDownloadPopup", "엑셀 다운로드", 400, 550);
	modalPopup ("gateListPopup", "단말기 검색", 600, 705);

	//초기화
	$("#reset").click(function() {
		$("#srchGateId").val("");
		$("#srchGateNm").val("");
		$("#srchNm").val("");
		$("#srchCdNo").val("");
		$("#srchFromDt").val("");
		$("#srchToDt").val("");
		$("#srchFutype").val("");
		$('.search_box input[type="checkbox"]').prop("checked",false).trigger('change');
		$('.search_box input[type="radio"]').prop("checked",false).trigger('change');
	});	
	
	$("#srchNm").keyup(function(e){if(e.keyCode == 13) userSearch();});
	$("#srchCdNo").keyup(function(e){if(e.keyCode == 13) userSearch();});
	
	$("#srchGateName").keyup(function(e){if(e.keyCode == 13) fnGatePopup('B');});
});

function userSearch() {
	var str = "";
	$('input:checkbox[name="fsCardStatus"]').each(function() {
		if(this.checked) str += this.value + ",";
	});

	if(fnIsEmpty($("#srchGateNm").val()) && fnIsEmpty($("#srchNm").val()) && fnIsEmpty($("#srchCdNo").val())
		&& fnIsEmpty($("#srchFromDt").val()) && fnIsEmpty($("#srchToDt").val())
		&& fnIsEmpty($("#srchFutype").val()) && fnIsEmpty(str) && !$("#fsNone").is(":checked")) {
		alert("검색조건을 입력하세요.");
		return;
	}

	if(!fnIsEmpty($("#srchFromDt").val()) && !fnIsEmpty($("#srchToDt").val())) {
		if($("#srchFromDt").val() > $("#srchToDt").val()){
			alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
			$("input[name=srchFromDt]").focus();
			return;
		}	
	}	

	$("#srchCardStatus").val(str);
	
	$("#srchPage").val("1");
	frmSearch.action = "/userInfo/userListByGate.do";
	frmSearch.submit();
}

//form submit
function pageSearch(page) {
	$("#srchPage").val(page);
	frmSearch.action = "/userInfo/userListByGate.do";
	frmSearch.submit();
}

function excelDownLoad() {
	var rowDataArr = "";
	var rowTextArr = "";
	var checkbox = $("input[name=excelColumn]:checked");
	checkbox.each(function(i){
		var chkValue = $(this).val();
		var chkText = $(this).parent().text();
		if(i==0){
			rowDataArr = rowDataArr + chkValue + " as CELL" + (i+1);
			rowTextArr = rowTextArr + chkText;
		}else{
			rowDataArr = rowDataArr + ", " + chkValue + " as CELL" + (i+1);
			rowTextArr = rowTextArr + ", " + chkText;
		}
	});
	if(checkbox.length == 0){
		alert("한개이상 체크를 하셔야 다운로드 가능합니다.");
		return;
	}else{
		$("#download").attr("data-dismiss", "modal");
		$("#download").attr("aria-label", "Close");
	}
	$("#chkValueArray").val(rowDataArr);chkValueArray
	$("#chkTextArray").val(rowTextArr.replaceAll(/\s+/g, ""));

	var str = "";
	$('input:checkbox[name="fsCardStatus"]').each(function() {
		if(this.checked) str += this.value + ",";
	});
	$("#srchCardStatus").val(str);
	
	frmSearch.action = "/userInfo/userListByGateExcel.do"
	frmSearch.submit();
	
	cancel();
}

function cancel(){
	$("input[name=excelColumn]").prop("checked", false);
	$("#checkAll").prop("checked", false);
	$("#excelDownloadPopup").PopupWindow("close");
}

function fnGatePopup(gb) {
	var str = "";
	if(gb == "B") {
		str = $("#srchGateName").val();
	}

	//단말기그룹 search
	$.ajax({
		type: "POST",
		url: "<c:url value='/terminalInfo/getGateList.do'/>",
		data: {srchGateName : str},
		dataType: 'json',
		timeout: (1000*30),
		success: 
			function(returnData, status) {
				var strHtml = "";
				if( status == "success") {
					if( returnData.list != null && returnData.list.length > 0 ) {
						for(var i = 0 ; i < returnData.list.length ; i++ ) {
							strHtml += "<tr><td>"
									+ "<input type='checkbox' name='chkGate' value='"+ returnData.list[i].fgid +"' class='mb_5'>"
									+ "</td><td>"
									+ returnData.list[i].flname
									+ "</td><td>"
									+ returnData.list[i].fuseyn									
									+ "</td></tr>";
						}
					}
					$("#tbGateList").html(strHtml);
					if(gb == "A") {
						$("#gateListPopup").PopupWindow("open");
					}
				} else { alert("ERROR!");return;}
			}
	});
}

function selGate() {
	var gateId = "";
	var gateNm = "";
	$('input:checkbox[name="chkGate"]').each(function() {
		if(this.checked) {
			gateId += $(this).val() + ",";
			gateNm += $(this).parent().parent().eq(0)[0].cells[1].textContent + ",";
		}
	});
	$("#srchGateId").val(gateId);
	$("#srchGateNm").val(gateNm);
	cancel2();
}

function cancel2() {
	$("input[name=chkGateAll]").prop("checked", false);
	$("input[name=chkGate]").prop("checked", false);
	$("#gateListPopup").PopupWindow("close");
}
</script>

<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
<input type="hidden" id="srchCardStatus" name="srchCardStatus" value="${param.srchCardStatus}">
<input type="hidden" id="srchPage" name="srchPage" value="${pagination.curPage}">
<input type="hidden" id="chkValueArray" name="chkValueArray">
<input type="hidden" id="chkTextArray" name="chkTextArray">
<!--검색박스 -->
<div class="search_box mb_20">
	<div class="search_in_bline">
		<div class="comm_search mr_30">
			<input type="text" id="srchGateNm" name="srchGateNm" class="w_200px input_com" readonly placeholder="단말기명 " value='<c:out value="${param.srchGateNm}"/>'>
			<input type="hidden" id="srchGateId" name="srchGateId" value='<c:out value="${param.srchGateId}"/>'>
		</div>
		<div class="comm_search">
			<div class="search_btn2" onclick="fnGatePopup('A')"></div>
		</div>
		<div class="comm_search mr_10 ml_10">
			<input type="text" id="srchNm" name="srchNm" class="w_120px input_com" placeholder="출입자명 " value='<c:out value="${param.srchNm}"/>'>
		</div>
		<div class="comm_search mr_10">
			<input type="text" id="srchCdNo" name="srchCdNo" class="w_120px input_com" placeholder="카드번호 " value='<c:out value="${param.srchCdNo}"/>'>
		</div>
		<div class="ch_box  mr_20">
			<input type="checkbox" id="srchFpartnm1" name="srchFpartnm1" class="checkbox" value="fpartnm1" <c:if test="${param.srchFpartnm1 eq 'fpartnm1'}">checked</c:if>>
			<label for="srchFpartnm1"> 회사</label>
			<input type="checkbox" id="srchFpartnm2" name="srchFpartnm2" class="checkbox" value="fpartnm2" <c:if test="${param.srchFpartnm2 eq 'fpartnm2'}">checked</c:if>>
			<label for="srchFpartnm2" class="ml_10"> 부서</label>
			<input type="checkbox" id="srchFpartnm3" name="srchFpartnm3" class="checkbox" value="fpartnm3" <c:if test="${param.srchFpartnm3 eq 'fpartnm3'}">checked</c:if>>
			<label for="srchFpartnm3" class="ml_10"> 직급</label>
		</div>
		<div class="comm_search mr_20">
			<input type="text" class="w_150px input_com" id="srchPartWord" name="srchPartWord" placeholder="회사/부서/직급" value='<c:out value="${param.srchPartWord}"/>' />
		</div>
		<div class="comm_search mr_10">
			<label for="search-from-date" class="title">카드기간</label>
			<input type="text" id="srchFromDt" name="srchFromDt" class="input_datepicker w_130px fl" name="search-from-date" placeholder="카드시작일" value='<c:out value="${param.srchFromDt}"/>'>
			<div class="sp_tx fl">~</div>
			<label for="search-to-date"></label><input type="text" id="srchToDt" name="srchToDt" class="input_datepicker w_130px  fl" name="search-to-date" placeholder="카드만료일" value='<c:out value="${param.srchToDt}"/>'>
		</div>
	</div>		
	<div class="search_in_bline">
		<div class="comm_search mr_20">
			<div class="title ">출입자타입</div>
			<select name="srchFutype" id="srchFutype" size="1" class="w_120px input_com">
				<option value="">전체</option>
				<c:forEach items="${userType}" var="uType" varStatus="status">
					<option value='<c:out value="${uType.fvalue}"/>' <c:if test="${uType.fvalue eq param.srchFutype}">selected</c:if>><c:out value="${uType.fkind3}"/></option>
				</c:forEach>
			</select>
		</div>	
		<div class="comm_search mr_20">
			<div class="title">카드상태</div>
			<div class="ch_box">
			<c:forEach items="${cardStatus}" var="code" varStatus="status">
				<input type="checkbox" id="fs${code.objName}" name="fsCardStatus" value="${code.fvalue}" class="checkbox" <c:if test="${fn:contains(param.srchCardStatus, code.fvalue)}">checked</c:if>> <label for="fs${code.objName}" class="ml_10"> ${code.fkind3}</label>
			</c:forEach>
				<input type="checkbox" id="fsNone" name="fsNone" value="N" class="checkbox" <c:if test="${param.fsNone eq 'N'}">checked</c:if>> <label for="fsNone" class="ml_10"> 상태값없음</label>
			</div>
		</div>
		<div class="comm_search ml_60">
			<div class="search_btn2" title="검색" id="btnSearch"></div>
		</div>
		<div class="comm_search ml_65">
			<button type="button" class="comm_btn" id="reset">초기화</button>
		</div>
	</div>
</div>
<div class="totalbox">
	<div class="txbox">
		<b class="fl mr_10">전체 : <c:out value="${pagination.totRecord}" />건</b>
		<!-- 건수 -->
		<select name="srchRecPerPage" id="srchRecPerPage" class="input_com w_80px">
		<c:forEach items="${cntPerPage}" var="cntPerPage" varStatus="status">
			<option value='<c:out value="${cntPerPage.fvalue}"/>' <c:if test="${cntPerPage.fvalue eq pagination.recPerPage}">selected</c:if>><c:out value="${cntPerPage.fkind3}"/></option>
		</c:forEach>
		</select>
	</div>
	<div class="r_btnbox  mb_10">
		<button type="button" class="btn_excel" data-toggle="modal" id="excelDownload">엑셀다운로드</button>
	</div>
</div>
</form>
<!--//검색박스 -->
<!--------- 목록--------->
<!--테이블 시작 -->
<div class="tb_outbox">
	<table class="tb_list">
		<colgroup>
			<col width="11%" />
			<col width="12%" />
			<col width="12%" />
			<col width="9%" />
			<col width="8%" />
			<col width="11%" />
			<col width="11%" />
			<col width="8%" />
			<col width="9%" />
			<col width="9%" />
		</colgroup>
		<thead>
			<tr>
				<th>단말기위치</th>
				<th>출입자명</th>
				<th>카드번호</th>
				<th>출입자타입</th>
				<th>카드상태</th>
				<th>회사</th>
				<th>부서</th>
				<th>직급</th>
				<th>카드시작일</th>
				<th>카드만료일</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${userInfoList == null || fn:length(userInfoList) == 0}">
			<tr>
				<td class="h_35px"  style="border-right:none;" colspan="10">조회 목록이 없습니다.</td>
			</tr>
		</c:if>
		<c:forEach items="${userInfoList}" var="ulist" varStatus="status">
			<tr>
				<td><c:out value="${ulist.flname}" /></td>
				<td><c:out value="${ulist.funm}" /></td>
				<td><c:out value="${ulist.fcdno}" /></td>
				<td><c:out value="${ulist.futypenm}" /></td>
				<td><c:out value="${ulist.card_status}" /></td>
				<td><c:out value="${ulist.fpartnm1}" /></td>
				<td><c:out value="${ulist.fpartnm2}" /></td>
				<td><c:out value="${ulist.fpartnm3}" /></td>
				<td><c:out value="${ulist.fsdt}" /></td>
				<td><c:out value="${ulist.fedt}" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<!--------- //목록--------->
<!-- 페이징 -->
<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
<!-- /페이징 -->
<!--//본문시작 -->

<!-- modal : 엑셀 다운로드 -->
<div class="popup_box" id="excelDownloadPopup">
	<div class="search_box_popup" style="border-bottom: 0px">
		<div class="search_in">
			<input type="checkbox" id="checkAllA" name="checkAll" class="checkbox">
			<label for="checkAllA" class="ml_10"> 전체</label>
		</div>
	</div>
	<div class="search_box_popup mb_20">
		<div class="search_in">
			<div class="mr_m3">
				<input type="checkbox" name="excelColumn" id="flname" value="flname" class="checkbox">
				<label for="flname" class="ml_10"> 단말기명</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="funm" value="funm" class="checkbox">
				<label for="funm" class="ml_10"> 출입자명</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="fcdno" value="fcdno" class="checkbox">
				<label for="fcdno" class="ml_10"> 카드번호</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="futypenm" value="futypenm" class="checkbox">
				<label for="futypenm" class="ml_10"> 출입자타입</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="card_status" value="card_status" class="checkbox">
				<label for="card_status" class="ml_10"> 카드상태</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="fpartnm1" value="fpartnm1" class="checkbox">
				<label for="fpartnm1" class="ml_10"> 회사</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="fpartnm2" value="fpartnm2" class="checkbox">
				<label for="fpartnm2" class="ml_10"> 부서</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="fpartnm3" value="fpartnm3" class="checkbox">
				<label for="fpartnm3" class="ml_10"> 직급</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="fsdt" value="fsdt" class="checkbox">
				<label for="fsdt" class="ml_10"> 카드시작일</label>
			</div>
			<div class="mr_m3 mt_15">
				<input type="checkbox" name="excelColumn" id="fedt" value="fedt" class="checkbox">
				<label for="fedt" class="ml_10"> 카드만료일</label>
			</div>
		</div>
	</div>
	<div class="c_btnbox">
		<div style="display: inline-block;">
			<button type="button" class="comm_btn mr_5" onclick="excelDownLoad();">다운로드</button>
			<button type="button" class="comm_btn bk_color" onclick="cancel();">취소</button>
		</div>
	</div>
</div>

<!-- modal : 단말기 검색  -->
<div class="popup_box" id="gateListPopup">
	<!--검색박스 -->
	<div class="search_box mb_20">
		<div class="search_in">
			<div class="comm_search mr_50">
				<div class="title">단말기명</div>
				<input type="text" name="srchGateName" id="srchGateName" class="w_200px input_com mr_40" value="${srchGateName}">
				<div class="search_btn2" onclick="fnGatePopup('B');"></div>
			</div>
		</div>
	</div>
	<div class="com_box mb_20">
		<div class="tb_outbox">
			<table class="tb_list2" id="TableID1">
				<col width="15%" />
				<col width="70%" />
				<col width="15%" />
				<thead>
					<tr>
						<th><input type="checkbox" id="chkGateAll" name="chkGateAll" class="mt_5"><label for="chkGateAll" class="ml_5">전체</label></th>
						<th>단말기명</th>
						<th>사용여부</th>
					</tr>
				</thead>
				<tbody id="tbGateList"></tbody>
			</table>
		</div>
	</div>
	<div class="c_btnbox">
		<div style="display: inline-block;">
			<button type="button" class="comm_btn mr_5" onclick="selGate();">선택</button>
			<button type="button" class="comm_btn bk_color" onclick="cancel2();">취소</button>
		</div>
	</div>		
</div>
