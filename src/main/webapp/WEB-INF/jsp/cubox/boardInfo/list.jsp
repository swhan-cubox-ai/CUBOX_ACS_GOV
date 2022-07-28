 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>

<script type="text/javascript">
$(function() {
	if('${bbsId}' == '00000000000000000001'){
		$(".title_tx").html("공지사항");
	}else if('${bbsId}' == '00000000000000000002'){
		$(".title_tx").html("Q&A");
	}
	
	$("#srchRecPerPage").change(function(){
		pageSearch("1");
	});	
});

function pageSearch(page) {
	if(!fnIsEmpty(page)) $("#srchPage").val(page);
	
	f = document.frmList;
	f.action = "/boardInfo/${bbsId}/list.do";
	showLoading();
	f.submit();
}

function cancel(){
	$("#attachFile").val("");
	$("#attachFileNm").val("");
	$("input[name=excelColumn]").prop("checked", false);
	$("#fzipdownresn").val("");
	$("input[name=fexdownresn]").val("");
	$("input[name=excelImgYn]").prop("checked", false);
	$("input[name=checkAll]").prop("checked", false);
	$("#checkAllLst").prop("checked", false);
	$("#hidExcelImgYn").val("");
	$("#hidFuLst").val("");
	$("input[name=checkbx]").prop("checked", false);
	$("#hidAllLst").val("");

	$("#zipDownloadPopup").PopupWindow("close");
	$("#excelAllDownloadPopup").PopupWindow("close");
	$("#excelSelDownloadPopup").PopupWindow("close");
}

function fnAdd() {
	//var form = $("form[id=frmList]");
	//$("input:hidden[id=hidAddTy]").val("A");
	//form.attr({"method":"post","action":"<c:url value='/boardInfo/add.do'/>"});
	//form.submit();
	f = document.frmSearch;
	f.action = "/boardInfo/${bbsId}/add.do";
	f.submit();
}

function fnDetail(nttId) {
	//var form = $("form[id=frmList]");
	//$("input:hidden[id=hidNttId]").val(nttId);
	//form.attr({"method":"post","action":"<c:url value='/boardInfo/detail.do'/>"});
	//form.submit();
	
	f = document.frmSearch;
	$("input:hidden[id=hidNttId]").val(nttId);
	f.action = "/boardInfo/${bbsId}/detail.do";
	f.submit();
}
	
</script>
<iframe name="ifm" id="ifm" frameborder="0" height="0" width="0" scrolling="no"></iframe>
<!-- progress bar -->
  <div id="progress_info" class="progress_info" style="display: none;">
      <div id="progress" class="progress" style="background-color: blue; width: 0px;"></div>
      <p id= "excelUploadingState" class="excelUploadingState"></p>
  </div>
<!--검색박스 -->
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
<input type="hidden" id="chkValueArray" name="chkValueArray" value=""/>
<input type="hidden" id="chkTextArray" name="chkTextArray" value="" >
<input type="hidden" id="fdownresn" name="fdownresn" value=""/>
<input type="hidden" id="hidExcelImgYn" name="hidExcelImgYn" value=""/>
<input type="hidden" id="hidAllLst" name="hidAllLst" value=""/>
<input type="hidden" id="hidFuLst" name="hidFuLst"/>
<input type="hidden" id="hidNttId" name="hidNttId">
<input type="hidden" id="hidbbsId" name="hidbbsId" value="${bbsId}">
</form>
<!--//검색박스 -->

<form id="frmList" name="frmList" method="post">
<input type="hidden" id="bbsId" name="bbsId" value="${bbsId}"/>
<input type="hidden" id="srchPage" name="srchPage" value="${pagination.curPage}"/>

<div class="totalbox">
	<div class="txbox">
		<b class="fl mr_10">전체 : <c:out value="${pagination.totRecord}"/>건</b>
		<!-- 건수 -->
		<select name="srchRecPerPage" id="srchRecPerPage" class="input_com w_80px">
		<c:forEach items="${cntPerPage}" var="cntPerPage" varStatus="status">
			<option value='<c:out value="${cntPerPage.fvalue}"/>' <c:if test="${cntPerPage.fvalue eq pagination.recPerPage}">selected</c:if>><c:out value="${cntPerPage.fkind3}"/></option>
		</c:forEach>
		</select>
	</div>
	<div class="r_btnbox  mb_10">
	<c:if test="${(bbsId eq '00000000000000000001' && sessionScope.loginVO.author_id eq '00001') || bbsId ne '00000000000000000001'}">
		<button type="button" class="btn_middle color_color1" id="btnGate" onclick="fnAdd();">신규등록</button>
	</c:if>
	</div>
</div>
<!--------- 목록--------->
<div class="com_box">
	<!--테이블 시작 -->
	  <div class="tb_outbox">
   		<table class="tb_list">
   			<colgroup>
	 			<col width="70px"/>
	 			<col width="650px"/>
	 			<c:if test="${option.fileAtchPosblAt eq 'Y'}"><col width="100px"/></c:if>
	 			<col width="100px"/>
	 			<col width="300px"/>
	 			<col width="100px"/>
 			</colgroup>
			<thead id="thHead">
				<tr id="trUserMgt">
					<th>번호</th>
					<th>제목</th>
					<c:if test="${option.fileAtchPosblAt eq 'Y'}"><th>첨부</th></c:if>
					<th>작성자</th>
					<th>등록일</th>
					<th>조회수</th>
				</tr>
			</thead>
			<tbody>
			<c:choose>
			<c:when test="${noticeList == null || fn:length(noticeList) == 0}">
				<tr>
					<th class="h_35px" colspan="6">조회 목록이 없습니다.</th>
				</tr>
			</c:when>
			<c:otherwise>
			<c:forEach items="${noticeList}" var="nList" varStatus="status">
				<tr>
					<td><c:out value="${nList.nttId}"/></td>
					<td style="text-align: left; padding-left:10px"><a href="javascript:void();" onclick="fnDetail('${nList.nttId}');"><c:out value="${nList.nttSj}"/></a></td>
					<c:if test="${option.fileAtchPosblAt eq 'Y'}">
					<td><c:if test="${nList.atchFileCnt > 0}"><img src="/img/icon_file2.png" alt=""></c:if></td>
					</c:if>
					<td><c:out value="${nList.registNm}"/></td>
					<td><c:out value="${nList.registDt}"/></td>
					<td><c:out value="${nList.inqireCo}"/></td>
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
</form>