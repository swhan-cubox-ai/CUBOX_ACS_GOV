<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="validator"	uri="http://www.springmodules.org/tags/commons-validator"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="aero.cubox.sample.service.vo.LoginVO"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">

$(function() {
	$(".title_tx").html("출입 실패 이력");
	//초기화
	$("#reset").click(function(){
		$("#srchCdNo").val('');
		$("#srchTVCardNum").val('');
		$("#cCombo option:eq(0)").prop('selected', 'selected');
		$("#srchFutype option:eq(0)").prop('selected', 'selected');
		$('.search_box input[type="checkbox"]').prop("checked", false);

		//날짜, 시간
		$.ajax({
			type:"POST",
			url:"<c:url value='/logInfo/getTime.do'/>",
			dataType:'json',
			success:function(returnData, status){
				if(status == "success") {
					if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
						$("input[name=srchStartDate]").val(returnData.ytDt + " " + returnData.fromTime);
						$("input[name=srchExpireDate]").val(returnData.tdDt + " " + returnData.toTime);
					}
				}else{
					alert("ERROR!");
					return;
				}
			}
		});
	});

	//모달창 닫기
	$("#cancel").click(function(){
		$("#log-center-add-modal").modal('hide');
	});

	//체크박스 전체
	$("#checkAllA").click(function(){
		if($("#checkAllA").prop("checked")){
			$("input[name=excelColumn]").prop("checked", true);
		}else{
			$("input[name=excelColumn]").prop("checked", false);
		}
	});
	
	//검색
	$(".search_btn2").click(function(){
		fileLogSearch();
	});
	
	$("#srchRecPerPage").change(function(){
		fileLogSearch();
	});	
	
	//모달창 닫기
	$("#cancel").click(function(){
		$("#center-add-modal").modal('hide');
	});
	
	//체크박스 전체
	$("#checkAllA").click(function(){

		if($("#checkAllA").prop("checked")){
			$("input[name=excelColumn]").prop("checked", true);
		}else{
			$("input[name=excelColumn]").prop("checked", false);
		}
	});

	modalPopup ("excelAllDownloadPopup", "엑셀 다운로드", 300, 470);

	$("#excelAllDownload").on("click", function(event){
		$("#excelAllDownloadPopup").PopupWindow("open");
	});
	
	$("#srchCdNo").keyup(function(e){if(e.keyCode == 13)  fileLogSearch(); });

});

//form submit
function pageSearch(page) {
	f = document.frmSearch;
	f.action = "/logInfo/logFailMngmt.do?srchPage=" + page;
	f.submit();
}

function fileLogSearch() {

	var srchStartDate = $("input[name=srchStartDate]").val();
	var srchExpireDate = $("input[name=srchExpireDate]").val();

	if(fnIsEmpty(srchStartDate)) {
		alert("시작일자를 입력하여 주십시오.");
		$("input[name=srchStartDate]").focus();
		return false;
	} else if(fnIsEmpty(srchExpireDate)) {
		alert("종료일자를 입력하여 주십시오.");
		$("input[name=srchExpireDate]").focus();
		return false;
	}

	f = document.frmSearch;

	if(srchStartDate > srchExpireDate){
		alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
		$("input[name=srchStartDate]").focus();
		return false;
	}

	//날짜 간격이 한달이 넘어갈 경우 경고창
	var sdt = new Date(srchStartDate);
	var smonth = sdt.getMonth() + 1 + 1;
	var sday = sdt.getDate();
	if (smonth.toString().length == 1) smonth = ('0' + smonth);
	if (sday.toString().length == 1) sday = ('0' + sday);
	var stdt = sdt.getFullYear() + smonth + sday;

	if(srchExpireDate.replace(/-/gi,"") > stdt) {
		if(!confirm("조회일자가 한달이상이면 조회속도가 느려질 수 있습니다.\n\n계속 진행하시겠습니까?")) {
			 $("input[name=srchStartDate]").focus();
			return;
		}
	}
	
	var str = "";
	$('input:checkbox[name="fsCardStatus"]').each(function() {
		if(this.checked) str += this.value + ",";
	});
	$("#srchCardStatus").val(str);
	

	f.action = "/logInfo/logFailMngmt.do"
	f.submit();
}

function excelDownLoad(){

	f = document.frmSearch;

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

	$("#rowDataArr").val(rowDataArr);
	$("#rowTextArr").val(rowTextArr.replaceAll(/\s+/g, ""));
	cancel();
	
	f.action = "/logInfo/failExcelDownload.do";
	f.submit();
}

function fnCardfailLogPop (fcdno, fuid, fvisitnm) {
	var frmPop = document.frmPopup;

	openPopup("",'winCardFailLogViewPopup', 1000, 670)

	frmPop.action = "/logInfo/cardFailLogViewPopup.do";;
	frmPop.target = "winCardFailLogViewPopup";
	frmPop.fuid.value = fuid;
	frmPop.fcdno.value = fcdno;
	frmPop.fvisitnm.value = fvisitnm
	frmPop.submit();
}

function cancel(){
	$("input[name=excelColumn]").prop("checked", false);
	$("input[name=checkbx]").prop("checked", false);
	$("#checkAllA").prop("checked", false);
	$("#excelAllDownloadPopup").PopupWindow("close");
}

function fnUsrLogPopup(fgid) {
	$("#frmUsrLogPop input[name='fuid']").val(fgid);
	openPopup("", "winUsrLogViewPopup", 1260, 770)
	frmUsrLogPop.submit();
}

</script>
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
<input type="hidden" id="rowDataArr" name="rowDataArr" value="">
<input type="hidden" id="rowTextArr" name="rowTextArr" value="">
<input type="hidden" id="reloadYn" name="reloadYn" value='<c:out value="${reloadYn}"/>'>
<input type="hidden" id="srchCardStatus" name="srchCardStatus" value="${logInfoVO.srchCardStatus}">

<div class="search_box mb_20">
	<div class="search_in_bline">
		<div class="comm_search mr_20">
			<!-- <div class="title" style="margin-right: 5px;" >전체:</div> -->
			<select name="srchCond" id="cCombo" class="w_100px input_com  mr_5">
				<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
				<option value='<c:out value="${cCombo.siteId}"/>' <c:if test="${logInfoVO.srchCond eq cCombo.siteId}">selected</c:if>><c:out value="${cCombo.siteNm}"/></option>
				</c:forEach>
			</select>
		</div>
		<div class="comm_search mr_20">
			 <div class="comm_search  mr_5" >
				<label  for="search-from-date" class="title">날짜</label>
				<input type="text" class="input_datepicker w_180px  fl" name="srchStartDate"  id="startDatetimepicker" value="${logInfoVO.srchStartDate}"  placeholder="날짜,시간">
				<div class="sp_tx fl">~</div>
				<label  for="search-to-date"></label>
				<input type="text" class="input_datepicker w_180px  fl" name="srchExpireDate" id="endDatetimepicker"   value="${logInfoVO.srchExpireDate}" placeholder="날짜,시간">
			</div>
		</div>
		<div class="comm_search mr_20">
			<div class="title ">출입자타입</div>
			<select name="srchFutype" id="srchFutype" size="1" class="w_120px input_com">
				<option value="">전체</option>
				<c:forEach items="${userType}" var="uType" varStatus="status">
				<option value='<c:out value="${uType.fvalue}"/>' <c:if test="${uType.fvalue eq logInfoVO.futype}">selected</c:if>><c:out value="${uType.fkind3}"/></option>
				</c:forEach>
			</select>
		</div>
		<div class="comm_search ml_40">
			<div class="search_btn2"></div>
		</div>
		<div class="comm_search ml_45">
			<button type="button" class="comm_btn" id="reset">초기화</button>
		</div>
	</div>
	<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
	<div class="search_in_bline">
		<div class="comm_search  mr_20">
			<div class="title ">카드번호</div>
			<input type="text" id="srchCdNo" name="srchCdNo" class="w_150px input_com" value='<c:out value="${logInfoVO.fcdno}"/>' placeholder="카드번호">
		</div>	
		<div class="comm_search">
			<div class="title ">카드상태</div>
			<div class="ch_box">
			<c:forEach items="${cardStatus}" var="code" varStatus="status">
				<input type="checkbox" id="fs${code.objName}" name="fsCardStatus" value="${code.fvalue}" class="checkbox" <c:if test="${fn:contains(logInfoVO.srchCardStatus, code.fvalue)}">checked</c:if>> <label for="fs${code.objName}" class="ml_10"> ${code.fkind3}</label>
			</c:forEach>
				<input type="checkbox" id="fsNone" name="fsNone" value="N" class="checkbox" <c:if test="${logInfoVO.fsNone eq 'N'}">checked</c:if>> <label for="fsNone" class="ml_10"> 상태값없음</label>
			</div>
		</div>
	</div>
	</c:if>	
</div>
<!--//검색박스 -->
<!--------- 목록--------->
<div class="com_box ">
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
			<button type="button" class="btn_excel" data-toggle="modal" id="excelAllDownload">엑셀 다운로드</button>
		</div>
	</div>
	<!--테이블 시작 -->
	<div class="tb_outbox">
		<table class="tb_list">
			<col width="50px"/>
			<col width=""/>
			<col width=""/>
			<col width=""/>
			<col width=""/>
			<col width=""/>
			<thead>
			<tr>
				<th>순번</th>
				<th>FID</th>
				<th>이름</th>
				<th>출입자타입</th>
				<th>카드번호</th>
				<th>카드상태</th>
				<th>실패횟수</th>
				<th>최근 실패일시</th>
				<!-- <th>사업명</th>
				<th>회사명</th>
				<th>부서</th>
				<th>차번호</th>
				<th>차번호2</th>
				<th>전화번호</th>
				<th>전화번호2</th>
				<th>출입증번호</th> -->
			</tr>
			</thead>
			<tbody>
			<c:if test="${logInfoList == null || fn:length(logInfoList) == 0}">
				<tr>
					<td class="h_35px" colspan="8">조회 목록이 없습니다.</td>
				</tr>
			</c:if>
			<c:forEach items="${logInfoList}" var="logList"	varStatus="status">
				<tr>
					<td>${(totalCnt - (totalCnt-status.index)+1)  + ( (currentPage - 1)  *  displayNum ) } </td>
					<td><a href="#none"	onclick="fnUsrLogPopup('<c:out value="${logList.fuid}"/>');"><c:out value="${logList.fuid}" /></a></td>
					<td><c:out value="${logList.funm}" /></td>
					<td><c:out value="${logList.fvalue2}" /></td>
					<td><c:out value="${logList.fcdno}" /></td>
					<td><c:out value="${logList.cfstatus}" /></td>
					<td><a href="#none"	onclick="fnCardfailLogPop('<c:out value="${logList.fcdno}"/>','<c:out value="${logList.fuid}"/>','<c:out value="${logList.fvisitnm}"/>')" style="font-weight: bold;"><c:out value="${logList.fvalue1}" /></a></td> <!-- 결과 -->
					<td><c:out value="${logList.fevttm}" /></td>
					<%-- <td style="text-align: left; padding-left:10px"><c:if test="${logList.fpartnm1 ne 'NULL'}"><c:out value="${logList.fpartnm1}" /></c:if></td>
					<td style="text-align: left; padding-left:10px"><c:if test="${logList.fpartnm2 ne 'NULL'}"><c:out value="${logList.fpartnm2}" /></c:if></td>
					<td style="text-align: left; padding-left:10px"><c:if test="${logList.fpartnm3 ne 'NULL'}"><c:out value="${logList.fpartnm3}" /></c:if></td>
					<td><c:out value="${logList.fpartcd3}" /></td>
					<td><c:out value="${logList.fcarno}" /></td>
					<td><c:if test="${logList.fpartcd2 ne '(null)'}"><c:out value="${logList.fpartcd2}" /></c:if></td>
					<td><c:out value="${logList.ftel}" /></td>
					<td>
						<c:out value="${logList.fvisitnm}" />
						<c:choose>
							<c:when test="${logList.futype=='5'||logList.futype=='6'||logList.futype=='7'||logList.futype=='8'}">방문 ${logList.fvisitnum}</c:when>
							<c:when test="${logList.futype=='9'}">임시 ${logList.fvisitnum}</c:when>
						</c:choose>
					</td> --%>
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

<form name="frmPopup" method="post">
<input type="hidden" name="fuid"/>
<input type="hidden" name="fcdno"/>
<input type="hidden" name="fvisitnm"/>
<input type="hidden" name="srchCond" value="<c:out value="${logInfoVO.srchCond}"/>"/>
<input type="hidden" name="srchStartDate" value="<c:out value="${logInfoVO.srchStartDate}"/>"/>
<input type="hidden" name="srchExpireDate" value="<c:out value="${logInfoVO.srchExpireDate}"/>"/>
<input type="hidden" name="fromTime" value="<c:out value="${logInfoVO.fromTime}"/>"/>
<input type="hidden" name="toTime" value="<c:out value="${logInfoVO.toTime}"/>"/>
</form>

<form id="frmUsrLogPop" name="frmUsrLogPop" method="post" target="winUsrLogViewPopup" action="/logInfo/usrFailLogViewPopup.do">
<input type="hidden" name="fuid">
<input type="hidden" name="srchStartDate" value="<c:out value="${logInfoVO.srchStartDate}"/>">
<input type="hidden" name="srchExpireDate" value="<c:out value="${logInfoVO.srchExpireDate}"/>">
</form>

<!-- modal : 전체 이미지 다운로드 -->
<div id="excelAllDownloadPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup" style="border-bottom: 0px">
			<div class="search_in">
				<input type="checkbox" id="checkAllA" name="checkAll" class="checkbox">
				<label for="checkAllA" class="ml_10">전체</label>
			</div>
		</div>
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<div class="mr_m3">
					<input type="checkbox" name="excelColumn" id=fuid value="fuid" class="checkbox">
					<label for="fcdno" class="ml_10">FID</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="funm" value="funm" class="checkbox">
					<label for="fcdno" class="ml_10">이름</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id=fcdno value="fvalue2" class="checkbox">
					<label for="fcdno" class="ml_10">출입자타입</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id=fcdno value="fcdno" class="checkbox">
					<label for="fcdno" class="ml_10">카드번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="cfstatus" value="cfstatus" class="checkbox">
					<label for="cfstatus" class="ml_10">카드상태</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fvalue1" value="fvalue1" class="checkbox">
					<label for="fvalue1" class="ml_10">실패횟수</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fevttm" value="fevttm" class="checkbox">
					<label for="fvalue2" class="ml_10">최근 실패일시</label>
				</div>
				<!-- <div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm1" value="fpartnm1" class="checkbox">
					<label for="fpartnm1" class="ml_10">사업명</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm2" value="fpartnm2" class="checkbox">
					<label for="fpartnm2" class="ml_10">회사명</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm3" value="fpartnm3" class="checkbox">
					<label for="fpartnm3" class="ml_10">부서</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartcd3" value="fpartcd3" class="checkbox">
					<label for="fpartcd3" class="ml_10">차번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fcarno" value="fcarno" class="checkbox">
					<label for="fcarno" class="ml_10">차번호2</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartcd2" value="fpartcd2" class="checkbox">
					<label for="fpartcd2" class="ml_10">전화번호1</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="ftel" value="ftel" class="checkbox">
					<label for="ftel" class="ml_10">전화번호2</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fvisitnm" value="fvisitnm" class="checkbox">
					<label for="fvisitnm" class="ml_10">출입증번호2</label>
				</div> -->
			</div>
		</div>
		<div class="c_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" id="excelDownA" onclick="excelDownLoad();">다운로드</button>
				<button type="button" class="bk_color comm_btn" title="취소" onclick="cancel();">취소</button>
			</div>
		</div>
	</div>
</div>