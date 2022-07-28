<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="validator"
	uri="http://www.springmodules.org/tags/commons-validator"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="aero.cubox.sample.service.vo.LoginVO"%>
<jsp:include page="../frame/sub/head.jsp" />
<script type="text/javascript">
	//form submit
	function pageSearch(page) {
		f = document.frmSearch;
		f.action = "/logInfo/downloadLogPopup.do?srchPage=" + page;
		f.submit();
	}

	//검색
	function usrLogSearch() {
		var srchStartDate = $("input[name=srchStartDate]").val();
		var srchExpireDate = $("input[name=srchExpireDate]").val();
		f = document.frmSearch;
		if(srchStartDate > srchExpireDate){
			alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
			$("input[name=srchStartDate]").focus();
			return false;
		}
		f.action = "/logInfo/downloadLogPopup.do"
		f.submit();
	}

	$(function() {
		$("#popupNm").html("다운로드 이력");

		$("#TableID1").chromatable({
			width: "100%", // specify 100%, auto, or a fixed pixel amount
			height: "362px",
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
				data:{

				},
				dataType:'json',
				success:function(returnData, status){
						if(status == "success") {
							if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
								$("#fromTime").val(returnData.fromTime);
								$("#toTime").val(returnData.toTime);
								$("#srchStartDate").val(returnData.ytDt);
								$("#srchExpireDate").val(returnData.tdDt);
							}

						}else{
							alert("ERROR!");
							return;
					}
				}
			});
		});
	});
</script>
<jsp:include page="../frame/sub/popup_top.jsp" />
<div class="popup_box">
	<!--검색박스 -->
	<div class="mb_20">
		<b class="mr_10">전체 : <c:out value="${pagination.totRecord}"/>건</b>
	</div>

	<form id="frmSearch"  name="frmSearch" method="post" onsubmit="return false;" >
		<div class="com_box">
			<!--테이블 시작 -->
			<div class="tb_outbox">
				<table class="tb_list2 tbd_scroll" id="TableID1">
					<col style="width:20%">
					<col style="">
					<col style="width:15%">
					<col style="width:30%">
					<thead>
						<tr>
							<th>IP</th>
					        <th>다운로드 시간</th>
					        <th>다운로드타입</th>
					        <th>다운로드사유</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${downloadLogVO}" var="downlog" varStatus="status">
							<tr>
								<td><c:out value="${downlog.fip}" /></td>
								<td><c:out value="${downlog.fregdt}" /></td>
								<td>
									<c:choose>
										<c:when test="${downlog.fdwnflg eq 'Z'}">출입자 이미지</c:when>
										<c:when test="${downlog.fdwnflg eq 'E'}">출입자 엑셀</c:when>
										<c:otherwise>${downlog.fdwnflg}</c:otherwise>
									</c:choose>
								</td>
								<td><c:out value="${downlog.fresn}"/></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<%-- <div class="tb_outbox tbody-scroll ht355">
				<table class="tb_list2">
					<col style="width:20%">
					<col style="width:20%">
					<col style="width:15%">
					<col style="width:22%">
				</table>
			</div> --%>
		</div>
	</form>
	<!--------- //목록--------->
	<!-- paging -->
	<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
</div>
<jsp:include page="../frame/sub/tail.jsp" />