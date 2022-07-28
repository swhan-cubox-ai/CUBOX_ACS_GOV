<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="validator"	uri="http://www.springmodules.org/tags/commons-validator"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="aero.cubox.sample.service.vo.LoginVO"%>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<style type="text/css">
	.mainOverflow {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		width: 200px;
		text-align: left;
	}
</style>
<script type="text/javascript">

	//form submit
	function pageSearch(page) {
		f = document.frmSearch;
		f.action = "/terminalInfo/terminalLogMngmt.do?srchPage=" + page;
		f.submit();
	}

	function terminalLogSearch() {

		var srchStartDate = $("input[name=startDate]").val();
		var srchExpireDate = $("input[name=endDate]").val();

		if(fnIsEmpty(srchStartDate)) {
			alert("시작일자를 입력하여 주십시오.");
			$("input[name=startDate]").focus();
			return false;
		} else if(fnIsEmpty(srchExpireDate)) {
			alert("종료일자를 입력하여 주십시오.");
			$("input[name=endDate]").focus();
			return false;
		}

		f = document.frmSearch;

		if(srchStartDate > srchExpireDate){
			alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
			$("input[name=startDate]").focus();
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
				 $("input[name=startDate]").focus();
				return;
			}
		}

		f.action = "/terminalInfo/terminalLogMngmt.do"
		f.submit();

	}

	function excelDownLoad(){

		f = document.frmSearch;

		var rowDataArr = "";
		var rowTextArr = "";

		var checkbox = $("input[name=colChk]:checked");

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
		$("#rowTextArr").val(rowTextArr);

		f.action = "/terminalInfo/terminalLogExcelDownload.do";
		f.submit();

	}

	function getAreaList(value) {
		var strcval = '${terSchvo.searchItemArea}';
		if(value != '0'){
			$.ajax({
				type:"GET",
				url:"<c:url value='/gateInfo/areaListToCenter.do' />",
				data:{
					"fptid": value
				},
				dataType: "json",
				success:function(result){

					$("#search_item_area").find("option").remove();
					$("#search_item_floor").find("option").remove();
					$("#search_item_floor").append("<option value='0' selected disabled>지역</option>");

					if(result.areaListToCenter.length > 0){
						$("#search_item_area").append("<option value=''>선택</option>");
						$.each(result.areaListToCenter, function(i){
							$("#search_item_area").append("<option value='" + result.areaListToCenter[i].code + "' "
									+ (strcval==result.areaListToCenter[i].code.toString()?"selected":"")
									+ "> "
									+ result.areaListToCenter[i].name + "</option>");
						});
					}
					else {
						$("#search_item_center").val("0");
						$("#search_item_area").append("<option value='0' selected disabled>건물</option>");
						$("#btn_gate_list_Status_refresh").attr('disabled', true);
						alert("설정 된 건물이 없습니다. 다른 센터를 선택해주세요.");
					}

					if(!fnIsEmpty(strcval)) getFloorList (strcval);
				}
			});
		}
	}

	function getFloorList(value) {
		var strcval = '${terSchvo.searchItemFloor}';
		$.ajax({
			type:"GET",
			url:"<c:url value='/gateInfo/floorListToArea.do' />",
			data:{
				"fptid": value
			},
			dataType: "json",
			success:function(result){
				$("#search_item_floor").find("option").remove();

				if(result.floorListToArea.length > 0){
					$("#search_item_floor").append("<option value=''>선택</option>");
					$("#search_item_floor").val('0');
					$.each(result.floorListToArea, function(i){
						$("#search_item_floor").append("<option value='" + result.floorListToArea[i].code + "' "
								+ (strcval==result.floorListToArea[i].code.toString()?"selected":"")
								+ "> "
								+ result.floorListToArea[i].name + "</option>");
					})
				}
				else {
					$("#search_item_floor").append("<option value='0' selected disabled>지역</option>");
					//alert("설정 된 지역이 없습니다. 다른 건물을 선택해주세요.");
				}
			}
		});
	}

	$(function() {

		//초기화
		$("#reset").click(function(){
			$("#srchFtx").val("");
			$("#cCombo option:eq(0)").prop('selected', 'selected');
			$("#srchFtx option:eq(0)").prop('selected', 'selected');
			$("#srchFstat option:eq(0)").prop('selected', 'selected');
			$("#search_item_area option:eq(0)").prop('selected', 'selected');
			$("#search_item_floor option:eq(0)").prop('selected', 'selected');
			$("input:checkbox").prop("checked", false);

			//날짜, 시간
			$.ajax({
				type:"POST",
				url:"<c:url value='/logInfo/getTime.do'/>",
				dataType:'json',
				success:function(returnData, status){
						if(status == "success") {
							if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
								$("#startTime").val(returnData.fromTime);
								$("#endTime").val(returnData.toTime);
								$("#startDate").val(returnData.ytDt);
								$("#endDate").val(returnData.tdDt);
							}

						}else{
							alert("ERROR!");
							return;
					}
				}
			});

		});

		//모달창 닫기
		$("#cancle").click(function(){
			$("#log-center-add-modal").modal('hide');
		});

		//체크박스 전체
		$("#checkAll").click(function(){
			if($("#checkAll").prop("checked")){
				$("input[name=colChk]").prop("checked", true);
			}else{
				$("input[name=colChk]").prop("checked", false);
			}
		});

		if(!fnIsEmpty('${terSchvo.srchCond}') && '${terSchvo.srchCond}'!='10') getAreaList ('${terSchvo.srchCond}');

	});

</script>
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
	<input type="hidden" id="rowDataArr" name="rowDataArr" value="">
	<input type="hidden" id="rowTextArr" name="rowTextArr" value="">
	<input type="hidden" id="reloadYn" name="reloadYn" value='<c:out value="${reloadYn}"/>'>
	<div class="subheader">
		<div class="title-bar float_wrap wd100">
			<h1>단말기제어이력</h1>
			<!-- search-box -->
			<div class="date-search-box panel">
				<div class="idchk_box date-search-box mt10">
	                <div class="d-flex ml10">
	                    <div class="search_box d-flex">
	                        <select id="cCombo" name="srchCond" class="form-control" onchange="getAreaList(this.value)">
								<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
	                            	<option value='<c:out value="${cCombo.siteId}"/>' <c:if test="${terSchvo.srchCond eq cCombo.siteId}">selected</c:if>><c:out value="${cCombo.siteNm}"/></option>
		                        </c:forEach>
							</select>
							<select name="search_item_area" id="search_item_area" class="form-control ml5 wd150p" onchange="getFloorList(this.value)">
				                <option value="0">건물</option>
				            </select>
				            <select name="search_item_floor" id="search_item_floor" class="form-control ml5 wd150p" onchange="searchBtnValidation()">
				                <option value="0">지역</option>
				            </select>
	                    </div>
	                    <div class="datapicker_list">
	                        <div class="form-group">
	                            <div class='input-group date ml10' id='datetimepicker6'>
	                                <label for="">날짜</label>
	                                <input type='text' id="startDate" name="startDate" class="search-word2 form-control wd100p" value="${terSchvo.startDate}" />
	                                <span class="input-group-addon">
	                                    <span class="glyphicon glyphicon-calendar"></span>
	                                </span>
	                            </div>
	                        </div>
	                        <div class="form-group mo-ml10">
                         		<div class='input-group time ml5'>
	                    			<label for="">시/분</label>
                       				<input type='time' id="startTime" name="startTime" class="form-control" value="${terSchvo.startTime}" />
                       			</div>
	                		</div>
	                        <div class="form-group mo-ml10">
	                            <div class='input-group date' id='datetimepicker7'>
	                                <label for="">~</label>
	                                <input type='text' id="endDate" name="endDate" class="search-word2 form-control wd100p" value="${terSchvo.endDate}"/>
	                                <span class="input-group-addon">
	                                    <span class="glyphicon glyphicon-calendar"></span>
	                                </span>
	                            </div>
	                        </div>
                         	<div class="form-group mo-ml10">
                         		<div class='input-group time'>
	                    			<label for="">시/분</label>
                       				<input type='time' id="endTime" name="endTime" class="form-control"  value="${terSchvo.endTime}" />
                       			</div>
	                		</div>
	                    </div>
	                </div>
	                <span class="hr-dashed-line-d"></span>
	                <div class="d-flex" id="chkDiv1">
	                    <div class="search_box d-flex ml10">
	                        <input placeholder="단말기명을 입력해주세요" id="srchFlnam" name="srchFlnam" class="search-word2 form-control wd180p" type="search" value='<c:out value="${terSchvo.srchFlnam}"/>'>
	                    	<%-- <select id="srchFtx" name="srchFtx" class="form-control ml5" style="max-width: 250px;">
	                        	<option value="">== 전송구분을 선택하세요 ==</option>
								<option value='CMD_SEND_MESSAGE' <c:if test="${terSchvo.srchFtx eq 'CMD_SEND_MESSAGE'}">selected</c:if>>CMD_SEND_MESSAGE</option>
								<option value='CMD_SET_TERMINAL_OPTION' <c:if test="${terSchvo.srchFtx eq 'CMD_SET_TERMINAL_OPTION'}">selected</c:if>>CMD_SET_TERMINAL_OPTION</option>
							</select> --%>
							<select id="srchFtxnm" name="srchFtxnm" class="form-control ml5" style="max-width: 250px;">
	                        	<option value="">== 전송종류를 선택하세요 ==</option>
								<!--
								<option value='Schedule' <c:if test="${terSchvo.srchFtxnm eq 'Schedule'}">selected</c:if>>Schedule</option>
								<option value='Message' <c:if test="${terSchvo.srchFtxnm eq 'Message'}">selected</c:if>>Message</option>
								<option value='Lock' <c:if test="${terSchvo.srchFtxnm eq 'Lock'}">selected</c:if>>Lock</option>
								<option value='UnLock' <c:if test="${terSchvo.srchFtxnm eq 'UnLock'}">selected</c:if>>UnLock</option>
								<option value='Reset User' <c:if test="${terSchvo.srchFtxnm eq 'Reset User'}">selected</c:if>>Reset User</option>
								-->
								<option value='KeepOpen' <c:if test="${terSchvo.srchFtxnm eq 'KeepOpen'}">selected</c:if>>KeepOpen</option>
								<option value='Open' <c:if test="${terSchvo.srchFtxnm eq 'Open'}">selected</c:if>>Open</option>
								<option value='Close' <c:if test="${terSchvo.srchFtxnm eq 'Close'}">selected</c:if>>Close</option>

							</select>
							<select id="srchFstat" name="srchFstat" class="form-control ml5" style="max-width: 200px;">
	                        	<option value="">== 상태를 선택하세요 ==</option>
								<option value='Q' <c:if test="${terSchvo.srchFstat eq 'Q'}">selected</c:if>>대기</option>
								<option value='U' <c:if test="${terSchvo.srchFstat eq 'U'}">selected</c:if>>완료</option>
								<option value='E' <c:if test="${terSchvo.srchFstat eq 'E' || terSchvo.srchFstat eq 'F'}">selected</c:if>>오류</option>
							</select>
	                    </div>
	                    <button class="bbs-search-btn" title="검색" onclick="terminalLogSearch()"><i class="fa fa-search"></i></button>
                        <button type="button" class="btn btn-dark ml5" id="reset">초기화</button>
	                </div>
	            </div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div id="panel-1" class="panel">
				<div class="panel-container collapse show">
					<div class="panel-content">
						<div class="float-left mb10">
							<div class="d-flex align-items-center">
								<b style="display:block;line-height:30px;" class="mr20">전체 : <c:out value="${pagination.totRecord}" />건</b>
							</div>
						</div>
						<div class="float-right mb10">
                            <button type="button" class="btn btn-sm btn-warning" data-toggle="modal" data-target="#log-center-add-modal"><span class="fa fa-download mr-1"></span>엑셀 다운로드</button>
                        </div>
						<div class="frame-wrap table-responsive text-nowrap">
							<table class="table m-0 table-bordered table-striped">
								<thead>
									<tr>
										<th>전송일자</th>
										<th>GID</th>
										<th>단말기명</th>
										<th>IP</th>
										<th>전송구분</th>
										<th>전송정보</th>
										<th>전송값</th>
										<th>전송종류</th>
										<th>상태</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${terminalLogInfo}" var="logList"	varStatus="status">
										<tr>
											<td style="max-width: 20%"><c:out value="${logList.fsidx}" /></td>
											<td><c:out value="${logList.fgid}" /></td>
											<td style="text-align: left; padding-left: 10px;"><c:out value="${logList.flnam}" /></td>
											<td><c:out value="${logList.fip}" /></td>
											<td style="text-align: left; padding-left: 10px;"><c:out value="${logList.ftx}" /></td>
											<td><c:out value="${logList.fex}" /></td>
											<td>
												<div class="mainOverflow" title="${logList.fvalue}">
													<c:out value="${logList.fvalue}" />
												</div>
											</td>
											<td><c:out value="${logList.ftxnm}" /></td>
											<td><c:out value="${logList.fsstatusnm}" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
						<div class="frame-wrap">
							<jsp:include page="/WEB-INF/jsp/cubox/common/pagination2.jsp" flush="false" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- modal 추가 팝업 -->
    <div class="modal fade" id="log-center-add-modal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">엑셀다운로드</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"><i class="fa fa-times"></i></span>
                    </button>
                </div>
                <div class="modal-body">
                	 <ul>
						<li><input type="checkbox" id="checkAll">전체</li>
					</ul>
                    <div class="check-list" style="height: 300px;">
                        <ul>
                            <li><input type="checkbox" name="colChk" value="fsidx">전송일자</li>
                            <li><input type="checkbox" name="colChk" value="fgid">GID</li>
                            <li><input type="checkbox" name="colChk" value="flnam">단말기명</li>
                            <li><input type="checkbox" name="colChk" value="fip">ip</li>
                            <li><input type="checkbox" name="colChk" value="ftx">전송구분</li>
                            <li><input type="checkbox" name="colChk" value="fex">전송정보</li>
                            <li><input type="checkbox" name="colChk" value="fvalue">전송값</li>
                            <li><input type="checkbox" name="colChk" value="ftxnm">전송종류</li>
                            <li><input type="checkbox" name="colChk" value="fsstatusnm">상태</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" id="download" class="btn btn-sm btn-primary" onclick="excelDownLoad();">다운로드</button>
                    <button type="button" id="cancle" class="btn btn-sm btn-default">취소</button>
                </div>
            </div>
        </div>
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