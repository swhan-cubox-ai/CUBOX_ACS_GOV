<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">
$(function(){
	$(".title_tx").html("서버인증 이력");
	$("#startDate").change(function(){
		var startDate = $("#startDate").val().replace('-','').replace('-','');
		var expireDate = $("#expireDate").val().replace('-','').replace('-','');
		if(startDate > expireDate && expireDate != ""){
			alert("완료일보다 뒷 날짜를 선택할 수 없습니다.");
			$("#startDate").val('');
			$("#startDate").focus();
		}
	});

	$("#expireDate").change(function(){
		var startDate = $("#startDate").val().replace('-','').replace('-','');
		var expireDate = $("#expireDate").val().replace('-','').replace('-','');
		if(startDate > expireDate && startDate != ""){
			alert("시작일보다 앞 날짜를 선택할 수 없습니다.");
			$("#expireDate").val('');
			$("#expireDate").focus();
		}
	});
	
	$("#expireDate").keyup(function(e){if(e.keyCode == 13)  apiLogSearch(); });
	
	$("#srchRecPerPage").change(function(){
		apiLogSearch();
	});	
});

function apiLogSearch(){
	$("#srchPage").val("1");
	f = document.frmSearch;
	f.action = "/apiInfo/apiInfoMngmt.do"
	f.submit();
}

function pageSearch(page){
	$("#srchPage").val(page);
	f = document.frmSearch;
	f.action = "/apiInfo/apiInfoMngmt.do";
	f.submit();
}
</script>
<!--검색박스 -->
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
<input type="hidden" id="srchPage" name="srchPage" value="${pagination.curPage}"/>
<div class="search_box mb_20">
	<div class="search_in_bline">
		<div class="comm_search mr_20">
			<label for="search-from-date" class="title">날짜</label>
			<input type="text" class="input_datepicker w_150px fl" name="startDate" id="startDate" placeholder="시작일" readonly="readonly" value="${json.start_dt}">
			<div class="sp_tx fl">~</div>
			<label for="search-to-date"></label>
			<input type="text" class="input_datepicker w_150px fl" name="expireDate" id="expireDate" placeholder="종료일" readonly="readonly" value="${json.end_dt}">
		</div>
		<div class="comm_search ml_20">
			<div class="search_btn2" title="검색" onclick="apiLogSearch()"></div>
		</div>
	</div>
</div>
<div class="totalbox">
	<div class="txbox">
		<b class="fl mr_10">전체 : <c:out value="${pagination.totRecord}"/>건</b>
		<!-- 건수 -->
		<select name="srchRecPerPage" id="srchRecPerPage" class="input_com w_80px">
		<c:forEach items="${cntPerPage}" var="cntPerPage" varStatus="status">
			<option value='<c:out value="${cntPerPage.fvalue}"/>' <c:if test="${cntPerPage.fvalue eq json.paging_cnt}">selected</c:if>><c:out value="${cntPerPage.fkind3}"/></option>
		</c:forEach>
		</select>
	</div>
</div>		
</form>
<!--//검색박스 -->
<!--------- 목록--------->
<div class="com_box">
	<!--테이블 시작 -->
	<div class="tb_outbox">
		<table class="tb_list">
			<col style="width:10%">
			<col style="width:10%">
			<col style="width:20%">
			<col style="width:10%">
			<col style="width:10%">
			<col style="width:20%">
			<col style="width:20%">
			<thead id="thead">
				<tr id="trUserMgt">
					<th>검색방식</th>
					<th>검색유형</th>
					<th>일치성명</th>
					<th>일치점수</th>
					<th>품질점수</th>
					<th>검색시작일시</th>
					<th>검색종료일시</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${matchLog == null || fn:length(matchLog) == 0}">
						<tr>
							<td class="h_35px" colspan="11">조회 목록이 없습니다.</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach items="${matchLog}" var="result" varStatus="status">
							<tr>
								<td><a style="cursor: pointer;" onclick="openPopup('/apiInfo/apiInfoPopup.do?srch_id=${result.SRCH_ID}','',1000,680)"><c:out value="${result.SRCH_SE}"/></a></td>
								<td>
								<c:choose>
									<c:when test="${result.SRCH_TY eq 'M'}">1:N</c:when>
									<c:when test="${result.SRCH_TY eq 'V'}">1:1</c:when>
									<c:otherwise>-</c:otherwise>
								</c:choose>
								</td>
								<td><c:out value="${result.MTCH_FACE_NM}"/></td>
								<td>
								<c:choose>
									<c:when test="${result.MTCH_SCO > 7000}">
										<b style="color:blue">${result.MTCH_SCO}</b>
									</c:when>
									<c:when test="${result.MTCH_SCO > 4000}">
										<b style="color:green">${result.MTCH_SCO}</b>
									</c:when>
									<c:otherwise>
										<b style="color:red">${result.MTCH_SCO}</b>
									</c:otherwise>
								</c:choose>
								</td>
								<td> ${result.MTCH_FACE_QLITY_SCO}</td>
								<td> ${result.SRCH_BEGIN_DT}</td>
								<td> ${result.SRCH_END_DT}</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>
	<!--------- //목록--------->
	<!-- paging -->
	<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
</div>

