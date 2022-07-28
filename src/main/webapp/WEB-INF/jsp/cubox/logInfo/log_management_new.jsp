<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="validator"	uri="http://www.springmodules.org/tags/commons-validator"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="aero.cubox.sample.service.vo.LoginVO"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<style>
.fa {
    display: inline-block;
    font: normal normal normal 14px/1 FontAwesome;
    font-size: inherit;
    text-rendering: auto;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}
</style>
<script type="text/javascript">
$(function() {
	$(".title_tx").html("출입 이력");

	//modal popup set
	modalPopup ("excelAllDownloadPopup", "엑셀 다운로드", 400, 682);

	//excel all download
	$("#excelAllDownload").on("click", function(event){
		$("#excelAllDownloadPopup").PopupWindow("open");
	});

	//자동새로고침 증가
	$("#btnRefreshIncrease").click(function(){
		if(parseInt($("#intervalSecond").val()) < 999) {
			$("#intervalSecond").val( parseInt($("#intervalSecond").val())+1);	
		}		
	});

	$("input[name=checkAll]").click(function() {
		if( $(this).prop("checked") ){
			$("input[name=excelColumn]").prop("checked", true);
		} else {
			$("input[name=excelColumn]").prop("checked", false);
		}
	});

	var threadRefresh;
	if('<c:out value="${reloadYn}" />' == 'Y'){
		threadRefresh = setInterval(function(){this.reload()}, <c:out value="${intervalSecond}" /> * 1000);
	} else {
		clearInterval(threadRefresh);
	}
	
	// 숫자만 입력가능
	$(".onlyNumber").keyup(function(event){
		if (!(event.keyCode >=37 && event.keyCode<=40)) {
			var inputVal = $(this).val();
			$(this).val(inputVal.replace(/[^0-9]/gi,''));
		}
	});

	//모달창 닫기
	$("#cancle").click(function(){
		$("#log-center-add-modal").modal('hide');
	});

	//검색
	$(".search_btn2").click(function(){
		logSearch();
	});
	
	$("#srchRecPerPage").change(function(){
		logSearch();
	});
	
	//초기화
	$("#reset").click(function() {
		$("#srchCondWord").val('');
		$("#cCombo option:eq(0)").prop('selected', 'selected');
		$('.search_box input[type="checkbox"]').prop("checked",false).trigger('change');
		$('.search_box input[type="radio"]').prop("checked",false).trigger('change');
		$("input:checkbox[name='funm']").prop("checked", true).trigger('change');
		$("input:checkbox[id='fsActive']").prop("checked", true).trigger('change');
		
		//날짜, 시간
		$.ajax({
			type:"POST",
			url:"<c:url value='/logInfo/getTime.do'/>",
			dataType:'json',
			success:function(returnData, status){
				if(status == "success") {
					if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
						$("#startDatetimepicker").val(returnData.ytDt+" "+returnData.fromTime);
						$("#endDatetimepicker").val(returnData.tdDt+" "+returnData.toTime);
					}
				}else{
					alert("ERROR!");
					return;
				}
			}
		});
	});	

	//체크박스 전체
	$("#checkAll").click(function(){
		if($("#checkAll").prop("checked")){
			$("input[name=colChk]").prop("checked", true);
		}else{
			$("input[name=colChk]").prop("checked", false);
		}
	});
	
	$("#srchCondWord").keyup(function(e){if(e.keyCode == 13)  logSearch(); });

	//sort
	var msort = {"1":"▲", "2":"▼"};
	$("#trLog th").click(function() {
		var thid = $(this).prop("id");
		if(!fnIsEmpty(thid)) {
			thid = thid.replace("th","");
			var sort = $("#sort"+thid).val();
			var chngSort = "";
			switch(sort) {
				case "2" :
					chngSort = "";
					break;
				case "1" :
					chngSort = "2";
					break;
				default :
					chngSort = "1";
					break;
			}
			$("#trUserMgt th input[type=hidden]").val("");
			$("#trUserMgt th span").html("");
			$("#sort"+thid).val(chngSort);
			$("#sp"+thid).html(fnIsEmpty(msort[chngSort])?"":msort[chngSort]);
			if(fnIsEmpty(chngSort)) {
				$("#hidSortName").val("");
				$("#hidSortNum").val("");
			} else {
				$("#hidSortName").val(thid);
				$("#hidSortNum").val(chngSort);
			}
			pageSearch("${pagination.curPage}");
		}
	});

	//sort 설정
	if(!fnIsEmpty($("#hidSortName").val())) {
		var hidsortnm = $("#hidSortName").val();
		var hidsortnum = $("#hidSortNum").val();
		$("#sort"+hidsortnm).val(hidsortnum);
		$("#sp"+hidsortnm).html(fnIsEmpty(msort[hidsortnum])?"":msort[hidsortnum]);
	}
});

//form submit
function pageSearch(page) {
	$("#srchPage").val(page);
	
	f = document.frmSearch;
	f.action = "/logInfo/logMngmt2.do";
	f.submit();
}

function logSearch() {
	var srchStartDate = $("input[name=srchStartDate]").val();
	var srchExpireDate = $("input[name=srchExpireDate]").val();
	if(srchStartDate > srchExpireDate){
		alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
		$("input[name=srchStartDate]").focus();
		return false;
	}
	
	var str = "";
	$('input:checkbox[name="fsCardStatus"]').each(function() {
		if(this.checked) str += this.value + ",";
	});
	$("#srchCardStatus").val(str);
	
	$("#srchPage").val("1");
	
	f = document.frmSearch;
	f.action = "/logInfo/logMngmt2.do"
	f.submit();
}

function excelDownLoad() {
	f = document.frmSearch;
	var rowDataArr = "";
	var rowTextArr = "";
	var checkbox = $("input[name=excelColumn]:checked");
	checkbox.each(function(i) {
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
	$("#rowDataArr").val(rowDataArr);
	$("#rowTextArr").val(rowTextArr.replaceAll(/\s+/g, ""));
	
	// 이미지 포함여부
	if($('#excelImgYn').is(':checked')) {
		$("#hidImgYn").val("Y");
	} else {
		$("#hidImgYn").val("N");
	}
	
	f.action = "/logInfo/excelDownload.do"
	f.submit();
	
	cancel();
}

//새로고침
function reload(){
	//날짜, 시간
	$.ajax({
		type:"POST",
		url:"<c:url value='/logInfo/getTime.do'/>",
		dataType:'json',
		success:function(returnData, status){
			if(status == "success") {
				if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
					//$("#startDatetimepicker").val(returnData.ytDt+" "+returnData.fromTime);
					$("#endDatetimepicker").val(returnData.tdDt+" "+returnData.toTime);
				}
				logSearch();
			}else{
				alert("ERROR!");
				return;
			}
		}
	});
}

function autoRefresh(value) {

	var reloadYn = $("#reloadYn");
	var autoRefreshBtn = $("#autoRefreshBtn");
	var autoRefreshIcon = $("#autoRefreshIcon");

	if(value == "N"){
		reloadYn.val("Y");
		alert($("#intervalSecond").val() +  "초마다 자동 새로고침이 시작됩니다.");
		reload();
	} else {
		reloadYn.val("N");
		alert("자동 새로고침이 중지되었습니다.");
		reload();
	}
}

function cancel(){
	$("#checkAll").prop("checked", false);
	$("input[name=excelColumn]").prop("checked", false);
	$("input[name=excelImgYn]").prop("checked", false);
	$("#excelAllDownloadPopup").PopupWindow("close");
}

function fnGateLogPopup(fgid) {
	$("#frmGateLogPop input[name='fgid']").val(fgid);
	openPopup("","winGateLogViewPopup", 1260, 835)
	frmGateLogPop.submit();
}

function fnUsrLogPopup(fuid) {
	$("#frmUsrLogPop input[name='fuid']").val(fuid);
	openPopup("","winUsrLogViewPopup", 1260, 770)
	frmUsrLogPop.submit();
}
</script>
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
<input type="hidden" id="hidImgYn" name="hidImgYn" value="">
<input type="hidden" id="rowDataArr" name="rowDataArr" value="">
<input type="hidden" id="rowTextArr" name="rowTextArr" value="">
<input type="hidden" id="reloadYn" name="reloadYn" value='<c:out value="${reloadYn}"/>'>
<input type="hidden" id="hidSortName" name="hidSortName" value="<c:out value="${logInfoVO.hidSortName}"/>"/>
<input type="hidden" id="hidSortNum" name="hidSortNum" value="<c:out value="${logInfoVO.hidSortNum}"/>"/>
<input type="hidden" id="srchPage" name="srchPage" value="${pagination.curPage}"/>
<input type="hidden" id="srchCardStatus" name="srchCardStatus" value="${logInfoVO.srchCardStatus}">
<!--검색박스 -->
<div class="search_box mb_20">
	<div class="search_in_bline">
		<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
		<div class="comm_search mr_20">
			<select id="cCombo" name="srchCond" size="1" class="w_100px input_com">
				<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
					<option value='<c:out value="${cCombo.siteId}"/>' <c:if test="${logInfoVO.srchCond eq cCombo.siteId}">selected</c:if>><c:out value="${cCombo.siteNm}" /></option>
				</c:forEach>
			</select>
		</div>
		</c:if>
		<div class="comm_search  mr_20">
			<label for="search-from-date" class="title">날짜</label>
			<input type="text" class="input_datepicker w_180px fl" name="srchStartDate" id="startDatetimepicker" value="${logInfoVO.srchStartDate}" placeholder="날짜,시간">
			<div class="sp_tx fl">~</div>
			<label for="search-to-date"></label>
			<input type="text" class="input_datepicker w_180px fl" name="srchExpireDate" id="endDatetimepicker" value="${logInfoVO.srchExpireDate}" placeholder="날짜,시간">
		</div>
		<div class="comm_search mr_60">
			<div class="title ">결과</div>
			<div class="ch_box">
				<input type="radio" id="srchSuccess" name="srchSuccess" value="Y" class="radioch mr_5" <c:if test="${logInfoVO.srchSuccess eq 'Y'}">checked</c:if>> <label for="srchSuccess" class="mr_5"> 성공</label>
				<input type="radio" id="srchFail" name="srchSuccess" value="N" class="radioch mr_5" <c:if test="${logInfoVO.srchSuccess eq 'N'}">checked</c:if>> <label for="srchFail" class="mr_5"> 실패</label>
			</div>
		</div>
		<div class="comm_search ">
			<div class="search_btn2 mr_5"></div>
		</div>
		<div class="comm_search">
			<button type="button" class="comm_btn" id="reset">초기화</button>
		</div>
	</div>
	<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
	<div class="search_in_bline">
		<div class="comm_search mr_20">
			<div class="title ">출입자타입</div>
			<select name="srchUserType" id="srchUserType" size="1" class="w_110px input_com">
				<option value="">전체</option>
				<c:forEach items="${userType}" var="code" varStatus="status">
					<option value='<c:out value="${code.fvalue}"/>' <c:if test="${code.fvalue eq logInfoVO.srchUserType}">selected</c:if>><c:out value="${code.fkind3}"/></option>
				</c:forEach>
			</select>
		</div>			
		<div class="ch_box  mr_20">
			<input type="checkbox" id="flname" name="flname" value="flname" class="checkbox" <c:if test="${logInfoVO.flname eq 'flname'}">checked</c:if>> <label for="flname" > 단말기명</label>
			<input type="checkbox" id="fid" name="fid" value="fuid" class="checkbox" <c:if test="${logInfoVO.fuid eq 'fuid'}">checked</c:if>> <label for="fid" class="ml_10"> FID</label> 
			<input type="checkbox" id="srchFunm" name="funm" value="funm" class="checkbox" <c:if test="${logInfoVO.funm eq 'funm'}">checked</c:if>> <label for="srchFunm" class="ml_10"> 이름</label> 
			<input type="checkbox" id="srchCardNum" name="srchCardNum" value="fcdno" class="checkbox" <c:if test="${logInfoVO.srchCardNum eq 'fcdno'}">checked</c:if>> <label for="srchCardNum" class="ml_10"> 카드번호</label>
		</div>
		<div class="comm_search mr_20">
			<input type="text" id="srchCondWord" name="srchCondWord" class="w_170px input_com" placeholder="단말기명/FID/이름/카드번호" value='<c:out value="${logInfoVO.srchCondWord}"/>'>
		</div>
		<div class="ch_box mr_20">
			<input type="checkbox" id="srchFpartnm1" name="srchFpartnm1" class="checkbox" value="fpartnm1" <c:if test="${logInfoVO.srchFpartnm1 eq 'fpartnm1'}">checked</c:if>>
			<label for="srchFpartnm1"> 회사</label>
			<input type="checkbox" id="srchFpartnm2" name="srchFpartnm2" class="checkbox" value="fpartnm2" <c:if test="${logInfoVO.srchFpartnm2 eq 'fpartnm2'}">checked</c:if>>
			<label for="srchFpartnm2" class="ml_10"> 부서</label>
		</div>
		<div class="comm_search mr_20">
			<input type="text" class="w_142px input_com" id="srchPartWord" name="srchPartWord" placeholder="회사/부서" value='<c:out value="${logInfoVO.srchPartWord}"/>' />
		</div>	
	</div>
	<div class="search_in_bline">
		<div class="comm_search mr_40">
			<div class="title ">카드상태</div>
			<div class="ch_box">
			<c:forEach items="${cardStatus}" var="code" varStatus="status">
				<input type="checkbox" id="fs${code.objName}" name="fsCardStatus" value="${code.fvalue}" class="checkbox" <c:if test="${fn:contains(logInfoVO.srchCardStatus, code.fvalue)}">checked</c:if>> <label for="fs${code.objName}" class="ml_10"> ${code.fkind3}</label>
			</c:forEach>
				<input type="checkbox" id="fsNone" name="fsNone" value="N" class="checkbox" <c:if test="${logInfoVO.fsNone eq 'N'}">checked</c:if>> <label for="fsNone" class="ml_10"> 상태값없음</label>
			</div>
		</div>
		<div class="comm_search mr_20">
			<div class="title ">권한타입</div>
			<select name="srchAuthType" id="srchAuthType" size="1" class="w_142px input_com">
				<option value="">전체</option>
				<c:forEach items="${authType}" var="code" varStatus="status">
					<option value='<c:out value="${code.fkind3}"/>' <c:if test="${code.fkind3 eq logInfoVO.srchAuthType}">selected</c:if>><c:out value="${code.fvalue}"/></option>
				</c:forEach>
			</select>
		</div>
	</div>
	</c:if>	
</div>
<div class="totalbox">
	<div class="txbox">
		<b class="fl mr_10">전체 : <c:out value="${pagination.totRecord}" />건</b>
		<!-- 건수 -->
		<select name="srchRecPerPage" id="srchRecPerPage" class="input_com w_80px">
		<c:forEach items="${cntPerPage}" var="cntPerPage" varStatus="status">
			<option value='<c:out value="${cntPerPage.fvalue}"/>' <c:if test="${cntPerPage.fvalue eq logInfoVO.srchCnt}">selected</c:if>><c:out value="${cntPerPage.fkind3}"/></option>
		</c:forEach>
		</select>
	</div>
	<div class="r_btnbox  mb_10">
		<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
		<div class="comm_search mr_5">
			<div class="title ">자동새로고침(초)</div>
			<input type="text" class="w_90px input_com onlyNumber" id="intervalSecond" name="intervalSecond" value="${intervalSecond}" maxlength="3" placeholder="">
			<div class="plus_btn" id="btnRefreshIncrease"></div>
		</div>
		<button type="button" value='<c:out value="${reloadYn}"/>' onclick="autoRefresh(this.value)" class="btn_middle color_gray mr_20" style="margin-right: 20px;">
			<c:choose>
				<c:when test="${reloadYn eq 'Y'}">정지</c:when>
				<c:otherwise>시작 </c:otherwise>
			</c:choose>
		</button>
		<button type="button" id="reloadPage" onclick="reload();" class="btn_middle color_basic">새로고침</button>
		</c:if>
		<button type="button" class="btn_excel" data-toggle="modal" id="excelAllDownload">엑셀다운로드</button>
	</div>
</div>
</form>
<!--//검색박스 -->
<!--------- 목록--------->
<!--테이블 시작 -->
<div class="tb_outbox">
	<table class="tb_list">
		<colgroup>
			<col width="4%" />
			<col width="14%"/>
			<col width="10%"/>
			<col width="7%"/>
			<col width="13%"/>
			<col width="7%"/>
			<col width="7%"/>
			<col width="8%"/>
			<col width="10%"/>
			<col width="7%"/>
			<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
			<col width="6%"/>
			</c:if>
			<col width="7%"/>
		</colgroup>
		<thead id="thHead">
			<tr id="trLog">
				<th>순번 </th>
				<th id="thfevttm">
					<input type="hidden" id="sortfevttm" name="thsortnm" />
					시간 
					<span id="spfevttm" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thflname">
					<input type="hidden" id="sortflname" name="thsortnm" />
					단말기 위치
					<span id="spflname" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfunm">
					<input type="hidden" id="sortfunm" name="thsortnm" />
					이름
					<span id="spfunm" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfcdno">
					<input type="hidden" id="sortfcdno" name="thsortnm" />
					카드번호
					<span id="spfcdno" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfutype">
					<input type="hidden" id="sortfutype" name="thsortnm" />
					출입자타입
					<span id="spfutype" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfcstatus">
					<input type="hidden" id="sortfcstatus" name="fcstatus" />
					카드상태
					<span id="spfcstatus" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfpartnm1">
					<input type="hidden" id="sortfpartnm1" name="thsortnm" />
					회사
					<span id="spfpartnm1" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfpartnm2">
					<input type="hidden" id="sortfpartnm2" name="thsortnm" />
					부서
					<span id="spfpartnm2" style="float: right; margin-right: 5px;"></span>
				</th>
				<th id="thfvalue1">
					<input type="hidden" id="sortfvalue1" name="thsortnm" />
					결과
					<span id="spfvalue1" style="float: right; margin-right: 5px;"></span>
				</th>
				<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
				<th id="thfmatchvalue">
					<input type="hidden" id="sortfmatchvalue" name="thsortnm" />
					점수
					<span id="spfmatchvalue" style="float: right; margin-right: 5px;"></span>
				</th>
				</c:if>				
				<th id="thfvalue2">
					<input type="hidden" id="sortfvalue2" name="thsortnm" />
					권한타입
					<span id="spfvalue2" style="float: right; margin-right: 5px;"></span>
				</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${logInfoList == null || fn:length(logInfoList) == 0}">
			<tr>
				<td class="h_35px" colspan="12">조회 목록이 없습니다.</td>
			</tr>
		</c:if>
		<c:forEach items="${logInfoList}" var="logList"	varStatus="status">
			<tr>
				<td>${(totalCnt - (totalCnt-status.index)+1)  + ( (currentPage - 1)  *  displayNum ) } </td>
				<td><c:set var="fevttm" value="${fn:split(logList.fevttm,'.')[0]}" /><c:out value="${fevttm}"/></td>
				<td><a href="#none" onclick="fnGateLogPopup('<c:out value="${logList.fgid}"/>')"><c:out value="${logList.flname}" /></a></td>
				<td><a href="#none" onclick="fnUsrLogPopup('<c:out value="${logList.fuid}"/>')"><c:out value="${logList.funm}" /></a></td>				
				<td><c:out value="${logList.fcdno}" /></td>
				<td><c:out value="${logList.futypenm}" /></td>
				<td><c:out value="${logList.fcstatusnm}" /></td>
				<td><c:out value="${logList.fpartnm1}" /></td>
				<td><c:out value="${logList.fpartnm2}" /></td>
				<td><c:if test="${logList.fevtcd ne '0' and logList.fevtcd ne '39'}"><font class="font-color_H"><c:out value="${logList.fvalue1}" /></font></c:if>
					<c:if test="${logList.fevtcd eq '0' or logList.fevtcd eq '39'}"><c:out value="${logList.fvalue1}" /></c:if></td>
				<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
				<td><c:if test="${logList.fevtcd ne '0' and logList.fevtcd ne '39'}"><font class="font-color_H"><c:out value="${logList.fmatchvalue}" /></font></c:if>
					<c:if test="${logList.fevtcd eq '0' or logList.fevtcd eq '39'}"><c:out value="${logList.fmatchvalue}" /></c:if></td>
				</c:if>				
				<td><c:out value="${logList.fvalue2}" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<form id="frmGateLogPop" name="frmGateLogPop" method="post" target="winGateLogViewPopup" action="/logInfo/gateLogViewPopup.do">
	<input type="hidden" name="fgid">
	<input type="hidden" name="srchStartDate" value="<c:out value="${logInfoVO.srchStartDate}"/>">
	<input type="hidden" name="srchExpireDate" value="<c:out value="${logInfoVO.srchExpireDate}"/>">
</form>
<form id="frmUsrLogPop" name="frmUsrLogPop" method="post" target="winUsrLogViewPopup" action="/logInfo/usrLogViewPopup.do">
	<input type="hidden" name="fuid">
	<input type="hidden" name="srchStartDate" value="<c:out value="${logInfoVO.srchStartDate}"/>">
	<input type="hidden" name="srchExpireDate" value="<c:out value="${logInfoVO.srchExpireDate}"/>">
</form>
<!--------- //목록--------->
<!-- 페이징 -->
<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
<!-- /페이징 -->
<!--//본문시작 -->
<!-- modal : 전체 이미지 다운로드 -->
<div id="excelAllDownloadPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup" style="border-bottom: 0px">
			<div class="search_in">
				<input type="checkbox" id="checkAll" name="checkAll" class="checkbox">
				<label for="checkAll" class="ml_10">전체</label>
			</div>
		</div>
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<div class="mr_m3">
					<input type="checkbox" name="excelColumn" id="fevttm" value="fevttm" class="checkbox">
					<label for="fevttm" class="ml_10">시간</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="flname2" value="flname" class="checkbox">
					<label for="flname2" class="ml_10">단말기명</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fuid" value="fuid" class="checkbox">
					<label for="fuid" class="ml_10">FID</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="funm" value="funm" class="checkbox">
					<label for="funm" class="ml_10">이름</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fcdno" value="fcdno" class="checkbox">
					<label for="fcdno" class="ml_10">카드번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="futype" value="futypenm" class="checkbox">
					<label for="futype" class="ml_10">출입자타입</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fcstatusnm" value="fcstatusnm" class="checkbox">
					<label for="fcstatusnm" class="ml_10">카드상태</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm1" value="fpartnm1" class="checkbox">
					<label for="fpartnm1" class="ml_10">회사</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm2" value="fpartnm2" class="checkbox">
					<label for="fpartnm2" class="ml_10">부서</label>
				</div>				
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fvalue1" value="fvalue1" class="checkbox">
					<label for="fvalue1" class="ml_10">결과</label>
				</div>
				<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fmatchvalue" value="fmatchvalue" class="checkbox">
					<label for="fmatchvalue" class="ml_10">점수</label>
				</div>
				</c:if>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fvalue2" value="fvalue2" class="checkbox">
					<label for="fvalue2" class="ml_10">권한타입</label>
				</div>
			</div>
		</div>
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<input type="checkbox" name="excelImgYn" id="excelImgYn" value="excelImgYn" class="checkbox">
				<label for="excelImgYn" class="ml_10"> 이미지 추가 여부</label>
			</div>
		</div>
		<div class="c_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" onclick="excelDownLoad();">다운로드</button>
				<button type="button" class="bk_color comm_btn" onclick="cancel();">취소</button>
			</div>
		</div>
	</div>
</div>

