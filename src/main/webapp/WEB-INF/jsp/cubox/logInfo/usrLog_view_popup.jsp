<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<jsp:include page="../frame/sub/head.jsp" />
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<script type="text/javascript">
$(function(){
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "501px",
		scrolling: "yes"
	});	
	
	//초기화
	$("#reset").click(function(){
		$("#srchCondWord").val('');
		$("#srchTVCardNum").val('');
		$("input[name=srchSuccess]:checked").prop("checked", false);
		$("input[name=srchFail]:checked").prop("checked", false);

		//날짜, 시간
		$.ajax({
			type:"POST",
			 url:"<c:url value='/logInfo/getTime.do'/>",
			dataType:'json',
			success:function(returnData, status){
					if( status == "success" ) {
						if( returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0 ){
							$("#startDatetimepicker").val(returnData.ytDt+" "+returnData.fromTime);
							$("#endDatetimepicker").val(returnData.tdDt+" "+returnData.toTime);
						}
					} else {
						alert("ERROR!");
						return;
					}
			}
		});
	});

	//새로고침
	$("#refreshPage").click(function(){
		//날짜, 시간
		$.ajax({
			   type :"POST",
			    url :"<c:url value='/logInfo/usrPopReload.do'/>",
		   dataType :'json',
		    success : function(returnData, status){
						if(status == "success") {
							if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
								//$("#startDatetimepicker").val(returnData.ytDt+" "+returnData.fromTime);
								$("#endDatetimepicker").val(returnData.tdDt+" "+returnData.toTime);
							}
							usrLogSearch();
						} else {
							alert("ERROR!");
							return;
						}
					}
		});
	});

	modalPopup ("excelAllDownloadPopup", "전체 엑셀 다운로드", 300, 460);

	$("#excelAllDownload").on("click", function(event){
		$("#excelAllDownloadPopup").PopupWindow("open");
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

	//검색
	$("#btnSearch").click(function(){
		usrLogSearch();
	});

	//엑셀
	$("#excelDownA").click(function(){
		excelDownLoad();
	});
});

//form submit
function pageSearch(page) {
	$("#srchPage").val(page);
	
	f = document.frmSearch;
	f.action = "/logInfo/usrLogViewPopup.do";
	f.submit();
}

function addClickImg(fuid, fevttm){
	var srcImg = "/logInfo/getByteRealImage.do?fuid=" + fuid + "&fevttm=" + fevttm;
	$("#realimg").attr('src', srcImg);
}

//검색
function usrLogSearch(){

	var srchStartDate = $("input[name=srchStartDate]").val();
	var srchExpireDate = $("input[name=srchExpireDate]").val();

	if(srchStartDate > srchExpireDate){
		alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
		$("input[name=srchStartDate]").focus();
		return false;
	}
	
	$("#srchPage").val("1");
	
	f = document.frmSearch;
	f.action = "/logInfo/usrLogViewPopup.do"
	f.submit();
}

//엑셀다운로드
function excelDownLoad(){

	f = document.frmSearch;

	var rowDataArr = "";
	var rowTextArr = "";

	var checkbox = $("input[name=excelColumn]:checked");

	checkbox.each(function(i){
		var chkValue = $(this).val();
		var chkText = $(this).parent().text();

		if( i==0 ){
			rowDataArr = rowDataArr + chkValue + " as CELL" + (i+1);
			rowTextArr = rowTextArr + chkText;
		}else{
			rowDataArr = rowDataArr + ", " + chkValue + " as CELL" + (i+1);
			rowTextArr = rowTextArr + ", " + chkText;
		}
	});

	if( checkbox.length == 0 ){
		alert("한개이상 체크를 하셔야 다운로드 가능합니다.");
		return;
	} else {
		$("#excelDownA").attr("data-dismiss", "modal");
		$("#excelDownA").attr("aria-label", "Close");
	}

	$("#rowDataArr").val(rowDataArr);
	$("#rowTextArr").val(rowTextArr);

	cancel();

	f.action = "/logInfo/usrPopExcelDownload.do"
	f.submit();

}

function cancel(){
	$("input[name=excelColumn]").prop("checked", false);
	$("input[name=checkbx]").prop("checked", false);
	$("#checkAllA").prop("checked", false);
	$("#excelAllDownloadPopup").PopupWindow("close");
}

</script>
<body>


<form id="frmSearch"  name="frmSearch" method="post" onsubmit="return false;" >
<input type="hidden" name="fuid" value="${logInfoVO.fuid}"/>
<input type="hidden" name="funm" value="${userInfo.funm}"/>
<input type="hidden" id="rowDataArr" name="rowDataArr" value="" />
<input type="hidden" id="rowTextArr" name="rowTextArr" value="" />
<input type="hidden" id="srchPage" name="srchPage" value="${logInfoVO.srchPage}"/>
	
<div class="popup_head_box" style="display: list-item;">
	<span id="popupNm">[<c:if test="${userInfo.funm ne ''}"><c:out value="${userInfo.funm}"/></c:if>
						<c:if test="${userInfo.funm eq null}"><c:out value="${logInfoVO.fuid}"/></c:if>] ${title}</span>
	<div class="close">
		<a href="javascript:void(0);" onclick="window.close()"><img src="/img/close_icon.png" alt="닫기" class="mt_10" /></a>
	</div>
</div>
<div class="popup_box">
	<!--검색박스 -->
	<div class="search_box_popup mb_20">
		<div class="search_in_bline">
			<%-- 
			<div class="comm_search">
				<div class="title_text"><c:out value="${title}" />/<c:out value="${title}" /><c:if test="${title eq null}">${logInfoVO}</c:if>/ <c:if test="${title eq null}">${logInfoVO}</c:if> </div>
			</div> 
			--%>
			
			<c:if test="${sessionScope.loginVO.author_id ne '00009'}">
			<div class="comm_search  mr_20">
				<label  for="srchCondWord" class="title">단말기명</label>
				<input type="text" class="w_180px input_com" id="srchCondWord" name="srchCondWord" value='<c:out value="${logInfoVO.srchCondWord}"/>' placeholder="단말기명을 입력해 주세요">
			</div>
			</c:if>
			<div class="comm_search mr_20">
				<label  for="search-from-date" class="title">날짜</label>
				<input type="text" class="input_datepicker w_190px  fl" name="srchStartDate" id="startDatetimepicker" value="${logInfoVO.stDateTime}" placeholder="날짜,시간">
				<div class="sp_tx fl">~</div>
				<label  for="search-to-date"></label>
				<input type="text" class="input_datepicker w_190px  fl" name="srchExpireDate" id="endDatetimepicker" value="${logInfoVO.edDateTime}" placeholder="날짜,시간">
			</div>
			<div class="comm_search mr_60">
				<div class="title ">결과 :</div>
				<div class="ch_box">
					<input type="radio" id="srchSuccess" name="srchSuccess"  value="Y"   class="radioch mr_5" <c:if test="${logInfoVO.srchSuccess eq 'Y'}">checked</c:if>>
					<label for="srchSuccess"  class="mr_5">성공</label>
					<input type="radio" id="srchFail"    name="srchSuccess"  value="N"   class="radioch mr_5" <c:if test="${logInfoVO.srchSuccess eq 'N'}">checked</c:if>>
					<label for="srchFail"  class="mr_5">실패</label>
				</div>
			</div>
			<div class="comm_search">
				<div class="search_btn2" id="btnSearch"></div>
			</div>
			<div class="comm_search ml_5">
				<button type="button" class="comm_btn" id="reset">초기화</button>
			</div>
		</div>
		<!-- 검색조건 2줄시  
		<div class="search_in_bline">
		</div> 
		-->
	</div>
	<!--//검색박스 -->
	<!-- 가로 2칸 --->
	<div class="box_w2"><!-- mb_20-->
		<!--------- left--------->
		<div class="box_w2_1d">
			<!-- 가로 2칸 --->
			<div class="box_w2 mt_50">
				<!--------- 목록--------->
				<div class="box_w2_1g">
					<div class="com_box">
						<img src='/logInfo/getByteImage.do?fuid=<c:out value="${userInfo.fuid}"/>' alt="" onerror="this.src='/images/gateLogViewPopup.png'"  style="width:180px; height:200px; object-fit:cover;">
					</div>
					<div class="img_title"  style="width:180px;">SOURCE </div>
					<div class="com_box mt_20">
						<img src="" id="realimg" alt="" onerror="this.src='/images/gateLogViewPopup.png'"  style="width:190px; height: 210px; object-fit:cover;">
					</div>
					<div class="img_title" style="width:180px;">REAL </div>
				</div>
				<!--------- //목록--------->
				<!--------- 목록--------->
				<div class="box_w2_2g">
					<div class="com_box">
					<!--테이블 시작 -->
						<div class="tb_outbox">
							<table class="tb_write">
								<col width="70px"/>
								<col width=""/>
								<tbody>
									<tr>
										<th>이름</th>
										<td><c:out value="${userInfo.funm}"/></td>
									</tr>
									<tr>
										<th>권한타입</th>
										<td><c:out value="${userInfo.fauthtypenm}"/></td>
									</tr>
									<tr>
										<th>출입자타입</th>
										<td><c:out value="${userInfo.futypenm}"/></td>
									</tr>
									<tr>
										<th>회사</th>
										<td><c:out value="${userInfo.fpartnm1}"/></td>
									</tr>
									<tr>
										<th>부서</th>
										<td><c:out value="${userInfo.fpartnm2}"/></td>
									</tr>
									<tr>
										<th>직급</th>
										<td><c:out value="${userInfo.fpartnm3}"/></td>
									</tr>
									<tr>
										<th>휴대전화</th>
										<td><c:out value="${userInfo.hpNo}"/></td>
									</tr>
									<tr>
										<th>전화번호</th>
										<td><c:out value="${userInfo.ftel}"/></td>
									</tr>
									<tr>
										<th>차번호</th>
										<td><c:out value="${userInfo.fcarno}"/></td>
									</tr>                    
									<tr>
										<th>센터</th>
										<td><c:out value="${userInfo.siteNm}"/></td>
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
					<div class="r_btnbox  mb_10">
						<button type="button" class="btn_middle color_basic" id="refreshPage">새로고침</button>
						<button type="button" class="btn_excel" data-toggle="modal" id="excelAllDownload">엑셀 다운로드</button>
					</div>
				</div>
				<!--//버튼  -->
				<!--테이블 시작 -->
				<div class="tb_outbox ">
					<table class="tb_list"  id="TableID1" >
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
								<th>단말기명 </th>
								<th>카드번호</th>
								<th>결과 </th>
								<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
								<th>점수</th>
								</c:if>
								<th>권한타입</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach items="${userlogInfo}" var="userlog" varStatus="status">
							<tr>
								<td>${(pagination.totRecord - (pagination.totRecord-status.index)+1)  + ((pagination.curPage - 1)  *  pagination.recPerPage)}</td>
								<td><c:set var="fevttm" value="${fn:split(userlog.fevttm,'.')[0]}" /><c:out value="${fevttm}"/></td>
								<td><c:out value="${userlog.funm}"/></td>
								<td class="title"><a href="javascript:void(0);" onclick="addClickImg('<c:out value="${userlog.fuid}"/>','<c:out value="${userlog.fevttm}"/>')"><c:out value="${userlog.flname}"/></a></td>
								<td><c:out value="${userlog.fcdno}"/></td>
								<td><c:if test="${userlog.fevtcd ne '0' and userlog.fevtcd ne '39'}"><font class="font-color_H"><c:out value="${userlog.fvalue1}"/></font></c:if><c:if test="${userlog.fevtcd eq '0' or userlog.fevtcd eq '39'}"><c:out value="${userlog.fvalue1}"/></c:if></td>
								<c:if test="${sessionScope.loginVO.author_id eq '00001'}">
								<td><c:if test="${userlog.fevtcd ne '0' and userlog.fevtcd ne '39'}"><font class="font-color_H"><c:out value="${userlog.fmatchvalue}"/></font></c:if><c:if test="${userlog.fevtcd eq '0' or userlog.fevtcd eq '39'}"><c:out value="${userlog.fmatchvalue}"/></c:if></td>
								</c:if>
								<td><c:out value="${userlog.fvalue2}"/></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
				<!-- 페이징 -->
				<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
				<!-- /페이징 -->
			</div>
		<!--------- //목록--------->
		</div>
	<!--------- //right --------->
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
						<input type="checkbox" name="excelColumn" id="flname" value="flname" class="checkbox">
						<label for="syslogdeptnm2" class="ml_10">단말기명</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="fcdno" value="fcdno" class="checkbox">
						<label for="syslogdeptnm1" class="ml_10">카드번호</label>
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
</body>