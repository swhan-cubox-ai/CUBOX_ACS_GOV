<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<jsp:include page="../frame/sub/head.jsp" />
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<script type="text/javascript">
$(function(){
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "502px",
		scrolling: "yes"
	});	
	
	//초기화
	$("#reset").click(function(){
		$("#srchCondWord").val('');
		$("input[name=srchSuccess]:checked").prop("checked", false);
		$("input[name=srchFail]:checked").prop("checked", false);
		$("input[name=fsActive]:checked").prop('checked', false);
		$("input[name=fsDelete]:checked").prop('checked', false);
		$("input[name=fsExpired]:checked").prop('checked', false);
		$("input[name=fsLost]:checked").prop('checked', false);
		$("input[name=fsWait]:checked").prop('checked', false);
		$("input[name=fsNone]:checked").prop('checked', false);

		//날짜, 시간
		$.ajax({
			type:"POST",
			url :"<c:url value='/logInfo/getTime.do'/>",
			dataType:'json',
			success:function(returnData, status){
					if(status == "success") {
						if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
							$("#startDatetimepicker").val(returnData.ytDt +" "+ returnData.fromTime);
							$("#endDatetimepicker").val(returnData.tdDt+" "+ returnData.toTime);
						}
					} else {
						alert("ERROR!");
						return;
					}
			}
		});
	});

	//검색
	$("#btnSearch").click(function(){
		gateLogSearch();
	});

	//새로고침
	$("#refreshPage").click(function(){
		//날짜, 시간
		$.ajax({
			type:"POST",
			url:"<c:url value='/logInfo/gatePopReload.do'/>",
			dataType:'json',
			success:function(returnData, status){
					if(status == "success") {
						if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
							//$("#startDatetimepicker").val(returnData.ytDt+" "+returnData.fromTime);
							$("#endDatetimepicker").val(returnData.tdDt+" "+returnData.toTime);
						}
						gateLogSearch();
					} else {
						alert("ERROR!");
						return;
					}
			}
		});
	});

	//모달창 닫기
	$("#cancle").click(function(){
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

	modalPopup ("excelAllDownloadPopup", "엑셀 다운로드", 300, 460);

	$("#excelAllDownload").on("click", function(event){
		$("#excelAllDownloadPopup").PopupWindow("open");
	});
});

//form submit
function pageSearch(page) {
	$("#srchPage").val(page);

	f = document.frmSearch;
	f.action = "/logInfo/gateLogViewPopup.do";
	f.submit();
}

function viewUserInfo(fuid, fcdno){

	var srcImg = "/logInfo/getByteImage.do?fuid=" + fuid;
	$("#sourceImg").attr('src', srcImg);

	$.ajax({
		type:"POST",
		url :"<c:url value='/logInfo/gateLogUserInfo.do' />",
		data:{
			"fuid": fuid ,
			"fcdno" : fcdno
		},
		dataType:'json',
		success:function(returnData, status){
				if(status == "success") {
					var strHtml = "";
					if( returnData.userInfo !=null && returnData.userInfo.length != 0 ){
						strHtml = "<tr><th>이름</th><td>" + returnData.userInfo.funm + "</td></tr><tr><th>권한타입</th><td>" + returnData.userInfo.fauthtypenm + "</td></tr>"
								+ "<tr><th>출입자타입</th><td>" + returnData.userInfo.futypenm + "</td></tr><tr><th>회사</th><td>" + returnData.userInfo.fpartnm1 + "</td></tr>"
								+ "<tr><th>부서</th><td>" + returnData.userInfo.fpartnm2 + "</td></tr><tr><th>직급</th><td>" + returnData.userInfo.fpartnm3  + "</td></tr>"
								+ "<tr><th>휴대번호</th><td>" + returnData.userInfo.hpNo + "</td></tr><tr><th>전화번호</th><td>" + returnData.userInfo.ftel  + "</td></tr>"
								+ "<tr><th>차번호</th><td>" + returnData.userInfo.fcarno  + "</td></tr><tr><th>카드시작일</th><td>" + returnData.userInfo.cfsdt + "</td></tr>"
								+ "<tr><th>카드만료일</th><td>" + returnData.userInfo.cfedt  + "</td></tr><tr><th>센터</th><td>" + returnData.userInfo.siteNm + "</td></tr>";
					}else{
						strHtml = "<tr><th>이름</th><td></td></tr><tr><th>권한타입</th><td></td></tr>"
								+ "<tr><th>출입자타입</th><td></td></tr><tr><th>회사</th><td></td></tr>"
								+ "<tr><th>부서</th><td></td></tr><tr><th>직급</th><td></td></tr>"
								+ "<tr><th>휴대번호</th><td></td></tr><tr><th>전화번호</th><td></td></tr>"
								+ "<tr><th>차번호</th><td></td></tr><tr><th>카드시작일</th><td></td></tr>"
								+ "<tr><th>카드만료일</th><td></td></tr><tr><th>센터</th><td></td></tr>";
					}
					$("#userInfo").html(strHtml);
				}else{
					alert("ERROR!");
					return;
			}
		}
	});
}

function gateLogSearch(){
	var srchStartDate = $("input[name=srchStartDate]").val();
	var srchExpireDate = $("input[name=srchExpireDate]").val();

	if(srchStartDate > srchExpireDate){
		alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
		$("input[name=srchStartDate]").focus();
		return false;
	}

	$("#srchPage").val("1");

	f = document.frmSearch;
	f.action = "/logInfo/gateLogViewPopup.do"
	f.submit();
}

function excelDownload(){

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
		$("#excelDownA").attr("data-dismiss","modal");
		$("#excelDownA").attr("aria-label","Close");
	}

	$("#rowDataArr").val(rowDataArr);
	$("#rowTextArr").val(rowTextArr);
	cancel();
	f.action = "/logInfo/gatePopExcelDownload.do"
	f.submit();
}

function cancel(){
	$("input[name=excelColumn]").prop("checked", false);
	$("input[name=checkbx]").prop("checked", false);
	$("#checkAllA").prop("checked", false);
	$("#excelAllDownloadPopup").PopupWindow("close");
}

</script>

<form id="frmSearch"  name="frmSearch" method="post" onsubmit="return false;" >
<input type="hidden" id="fgid" name="fgid" value='<c:out value="${logInfoVO.fgid}"/>'/>
<input type="hidden" id="flname" name="flname" value='<c:out value="${title}"/>'/>
<input type="hidden" id="rowDataArr" name="rowDataArr" value="" />
<input type="hidden" id="rowTextArr" name="rowTextArr" value="" />
<input type="hidden" id="srchPage" name="srchPage" value="${logInfoVO.srchPage}" />
<div class="popup_head_box" style="display: list-item;">
	<span id="popupNm">[<c:out value="${title}" />] 단말기별 출입이력 </span>
	<div class="close">
		<a href="javascript:void(0);" onclick="window.close()"><img src="/img/close_icon.png" alt="닫기" class="mt_10" /></a>
	</div>
</div>
<div class="popup_box">
<!--검색박스 -->
	<div class="search_box_popup mb_20">
		<div class="search_in_bline">
			<%-- <div class="comm_search">
				<div class="title ">단말기 위치 :</div>
				<div class="title_text">[<c:out value="${title}" />]</div>
			</div> --%>
			<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
			<div class="comm_search  mr_20">
				<label  for="srchCondWord" class="title">출입자이름</label>
				<input type="text" class="w_180px input_com" id="srchCondWord" name="srchCondWord" value='<c:out value="${logInfoVO.srchCondWord}"/>' placeholder="출입자이름을 입력해 주세요">
			</div>
			</c:if>
			<div class="comm_search mr_20">
				<label  for="search-from-date" class="title">날짜</label>
				<input type="text" class="input_datepicker w_190px  fl" name="srchStartDate" id="startDatetimepicker" value="${logInfoVO.stDateTime}" placeholder="날짜,시간">
				<div class="sp_tx fl">~</div>
				<label  for="search-to-date"></label>
				<input type="text" class="input_datepicker w_190px  fl" name="srchExpireDate" id="endDatetimepicker" value="${logInfoVO.edDateTime}" placeholder="날짜,시간">
			</div>
		</div>
		<div class="search_in_bline">
			<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
			<div class="comm_search mr_40">
				<div class="title ">카드상태 :</div>
				<div class="ch_box">
					<input type="checkbox"   id="fsActive"  name="fsActive"  value="Y" class="checkbox" <c:if test="${logInfoVO.fsActive eq 'Y'}">checked</c:if> >
					<label for="fsActive"    class="ml_10">ACTIVE</label>
					<input type="checkbox"   id="fsDelete"  name="fsDelete"  value="D" class="checkbox" <c:if test="${logInfoVO.fsDelete eq 'D'}">checked</c:if> >
					<label for="fsDelete"    class="ml_10">DELETE</label>
					<input type="checkbox"   id="fsExpired" name="fsExpired" value="E" class="checkbox" <c:if test="${logInfoVO.fsExpired eq 'E'}">checked</c:if> >
					<label for="fsExpired"   class="ml_10">EXPIRED</label>
					<input type="checkbox"   id="fsLost"    name="fsLost"    value="L" class="checkbox" <c:if test="${logInfoVO.fsLost eq 'L'}">checked</c:if> >
					<label for="fsLost"      class="ml_10">LOST</label>
					<input type="checkbox"   id="fsWait"    name="fsWait"    value="W" class="checkbox" <c:if test="${logInfoVO.fsWait eq 'W'}">checked</c:if> >
					<label for="fsWait"      class="ml_10">WAIT</label>
					<input type="checkbox"   id="fsNone"    name="fsNone"    value="N" class="checkbox" <c:if test="${logInfoVO.fsNone eq 'N'}">checked</c:if> >
					<label for="fsNone"      class="ml_10">상태값없음</label>
				</div>
			</div>
			</c:if>
			<div class="comm_search mr_60">
				<div class="title ">결과 :</div>
				<div class="ch_box">
					<input type="radio" id="srchSuccess" name="srchSuccess"  value="Y"   class="radioch mr_5" <c:if test="${logInfoVO.srchSuccess eq 'Y'}">checked</c:if>>
					<label for="srchSuccess"  class="mr_5"> 성공</label>
					<input type="radio" id="srchFail"    name="srchSuccess"  value="N"   class="radioch mr_5" <c:if test="${logInfoVO.srchSuccess eq 'N'}">checked</c:if>>
					<label for="srchFail"  class="mr_5"> 실패</label>
				</div>
			</div>
			<div class="comm_search">
				<div class="search_btn2" id="btnSearch"></div>
			</div>
			<div class="comm_search ml_5">
				<button type="button" class="comm_btn" id="reset">초기화</button>
			</div>
		</div>
	</div>
	<!--//검색박스 -->
	<!-- 가로 2칸 --->
	<div class="box_w2">
		<!--------- left--------->
		<div class="box_w2_1d">
		<!-- 가로 2칸 --->
			<div class="box_w2 mt_50">
				<!--------- 목록--------->
				<div class="box_w2_1g">
					<div class="com_box">
						<img src="" id="sourceImg" alt="" onerror="this.src='/images/gateLogViewPopup.png'"  width="180" height="200"  style=" object-fit:cover; transform:scaleX(-1); ">
					</div>
					<div class="img_title" style="width:180px;">SOURCE</div>
				</div>
				<!--------- //목록--------->
				<!--------- 목록--------->
				<div class="box_w2_2g">
					<div class="com_box">
					<!--테이블 시작 -->
						<div class="tb_outbox">
							<table class="tb_write">
								<tbody id="userInfo">
									<tr>
										<th>이름</th>
										<td></td>
									</tr>
									<tr>
										<th>권한타입</th>
										<td></td>
									</tr>
									<tr>
										<th>출입자타입</th>
										<td></td>
									</tr>
									<tr>
										<th>회사</th>
										<td></td>
									</tr>
									<tr>
										<th>부서</th>
										<td></td>
									</tr>
									<tr>
										<th>직급</th>
										<td></td>
									</tr>
									<tr>
										<th>휴대전화</th>
										<td></td>
									</tr>
									<tr>
										<th>전화번호</th>
										<td></td>
									</tr>
									<tr>
										<th>차번호</th>
										<td></td>
									</tr>
									<tr>
										<th>카드시작일</th>
										<td></td>
									</tr>
									<tr>
										<th>카드만료일</th>
										<td></td>
									</tr>
									<tr>
										<th>센터</th>
										<td></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<!--------- //목록--------->
			</div>
			<!-- //가로 2칸 --->
		</div>
		<!--------- //left --------->
		<!--------- right --------->
		<div class="box_w2_2d">
			<!--------- 목록--------->
			<div class="com_box ">
				<!--버튼 -->
				<div class="totalbox">
					<div class="txbox">전체 :<c:out value="${pagination.totRecord}" />건 </div>
					<div class="r_btnbox mb_10">
						<button type="button" class="btn_middle color_basic" id="refreshPage">새로고침 </button>
						<button type="button" class="btn_excel" data-toggle="modal" id="excelAllDownload">엑셀 다운로드</button>
					</div>
				</div>
				<!--//버튼  -->
				<!--테이블 시작 -->
				<div class="tb_outbox ">
					<table class="tb_list" id="TableID1" >
						<colgroup>
							<col width="6%" />
							<col width="22%" />
							<col width="11%" />
							<col width="17%" />
							<col width="14%" />
							<col width="13%" />
							<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
							<col width="6%" />
							</c:if>
							<col width="11%" />
						</colgroup>
						<thead>
							<tr>
								<th>순번</th>
								<th>시간 </th>
								<th>이름 </th>
								<th>카드번호 </th>
								<th>카드상태</th>
								<th>결과 </th>
								<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
								<th>점수</th>
								</c:if>
								<th>권한타입</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach items="${gateLogInfoList}" var="gatelog" varStatus="status">
							<tr>
								<td>${(pagination.totRecord - (pagination.totRecord-status.index)+1)  + ((pagination.curPage - 1)  *  pagination.recPerPage)}</td>
								<td><c:set var="fevttm" value="${fn:split(gatelog.fevttm,'.')[0]}" /><c:out value="${fevttm}"/></td>
								<td><a href="#" onclick="viewUserInfo('<c:out value="${gatelog.fuid}"/>', '<c:out value="${gatelog.fcdno}"/>')"><c:out value="${gatelog.funm}"/></a></td>
								<td><c:out value="${gatelog.fcdno}"/></td>
								<td><c:out value="${gatelog.cfstatus}"/></td>
								<td><c:if test="${gatelog.fevtcd ne '0' and gatelog.fevtcd ne '39'}"><font class="font-color_H"><c:out value="${gatelog.fvalue1}"/></font></c:if><c:if test="${gatelog.fevtcd eq '0' or gatelog.fevtcd eq '39'}"><c:out value="${gatelog.fvalue1}"/></c:if></td>
								<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
								<td><c:if test="${gatelog.fevtcd ne '0' and gatelog.fevtcd ne '39'}"><font class="font-color_H"><c:out value="${gatelog.fmatchvalue}"/></font></c:if><c:if test="${gatelog.fevtcd eq '0' or gatelog.fevtcd eq '39'}"><c:out value="${gatelog.fmatchvalue}"/></c:if></td>
								</c:if>
								<td><c:out value="${gatelog.fvalue2}"/></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
		<!--------- //right --------->
		<!-- 페이징 -->
		<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
		<!-- /페이징 -->        
			</div>
	<!--------- //목록--------->
		</div>
	</div>
</div>
<!-- //가로 2칸 --->
</form>
<jsp:include page="../frame/sub/tail.jsp" />

<!-- modal : 전체 이미지 다운로드 -->
<div id="excelAllDownloadPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup" style="border-bottom: 0px">
			<div class="search_in">
				<input type="checkbox" id="checkAllA" name="checkAll" class="checkbox">
				<label for="checkAllA" class="ml_10"> 전체</label>
			</div>
		</div>
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<div class="mr_m3">
					<input type="checkbox" name="excelColumn" id="fevttm" value="fevttm" class="checkbox">
					<label for="fevttm" class="ml_10">시간</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="funm" value="funm" class="checkbox">
					<label for="fsiteid" class="ml_10">이름</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fcdno" value="fcdno" class="checkbox">
					<label for="syslogdeptnm1" class="ml_10">카드번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="cfstatus" value="cfstatus" class="checkbox">
					<label for="syslogdeptnm2" class="ml_10">카드상태</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fvalue1" value="fvalue1" class="checkbox">
					<label for="fsyscodenm" class="ml_10">결과</label>
				</div>
				<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fmatchvalue" value="fmatchvalue" class="checkbox">
					<label for="fmatchvalue" class="ml_10">점수</label>
				</div>
				</c:if>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fvalue2" value="fvalue2" class="checkbox">
					<label for="fcnntip" class="ml_10">권한타입</label>
				</div>
			</div>
		</div>
		<div class="c_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" id="excelDownA" onclick="excelDownload();">다운로드</button>
				<button type="button" class="bk_color comm_btn" title="취소" onclick="cancel();">취소</button>
			</div>
		</div>
	</div>
</div>
