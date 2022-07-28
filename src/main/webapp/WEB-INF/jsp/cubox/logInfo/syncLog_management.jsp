<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script>
	$(function(){
		$(".title_tx").html("동기화 이력");
		$(document).on('click','.search_btn',function(){
			syncLogSearch();
		});
	});

	//form submit
	function pageSearch(page) {

		f = document.frmSearch;
		f.action = "/logInfo/syncLogMngmt.do?srchPage=" + page;
		f.submit();

	}

	function syncLogSearch(){

		var srchStartDate = $("input[name=startDate]").val();
		var srchExpireDate = $("input[name=endDate]").val();
		var code = $("#fjobcd option:selected").val();
		var result = $("#frsltyn option:selected").val();

		f = document.frmSearch;

		if(srchStartDate > srchExpireDate){
			alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
			$("input[name=srchStartDate]").focus();
			return false;
		}

		f.action = "/logInfo/syncLogMngmt.do"
		f.submit();
	}
</script>

<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
	<input type="hidden" id="rowDataArr" name="rowDataArr" value="">
	<input type="hidden" id="rowTextArr" name="rowTextArr" value="">
	<input type="hidden" id="reloadYn"   name="reloadYn"   value='<c:out value="${reloadYn}"/>'>

<div class="search_box mb_20">
  <div class="search_in_bline">
    <div class="comm_search mr_20">
        <label  for="search-from-date" class="title">검색 일시</label>
        <input type="text" class="input_datepicker w_200px fl" name="startDate" id="startDate" placeholder="날짜"  value="<c:out value='${map.fsdt}'/>">
		<div class="sp_tx fl">~</div>
		<label  for="search-to-date"></label>
        <input type="text" class="input_datepicker w_200px fl" name="endDate"   id="endDate"   placeholder="날짜" value="<c:out value='${map.fedt}'/>">
    </div>

    <div class="comm_search mr_40 mt_1">
        <div class="title" style="margin-right: 5px;" >분류코드:</div>
        <select name="fjobcd" id="fjobcd" class="w_100px input_com  mr_5">
            <option value=''>선택</option>
            <option value='USER' <c:if test="${map.code eq 'USER'}">selected</c:if>>직원</option>
            <option value='VISIT' <c:if test="${map.code eq 'VISIT'}">selected</c:if>>방문객</option>
            <option value='FACE' <c:if test="${map.code eq 'FACE'}">selected</c:if>>얼굴</option>
            <option value='ZIP' <c:if test="${map.code eq 'ZIP'}">selected</c:if>>사진압축</option>
        </select>
	    <div class="title" style="margin-right: 5px;" >결과:</div>
	    <select name="frsltyn" id="frsltyn" class="w_100px input_com  mr_5">
            <option value=''>선택</option>
            <option value='Y' <c:if test="${map.result eq 'Y'}">selected</c:if>>성공</option>
            <option value='N' <c:if test="${map.result eq 'N'}">selected</c:if>>실패</option>
        </select>
    </div>
    <div class="comm_search">
        <div class="search_btn" ></div>
    </div>
  </div>
</div>

<!--//검색박스 -->
  <!--------- 목록--------->
<div class="com_box ">
    <div class="totalbox">
        <div class="txbox">전체 : <c:out value="${pagination.totRecord}"/> 건</div>
    </div>
    <!--버튼 -->

    <!--//버튼  -->
    <!--테이블 시작 -->
    <div class="tb_outbox">
      <table class="tb_list">
        <col width="16%"/>
        <col width="34%"/>
        <col width="34%"/>
        <col width="16%"/>
        <thead>
          <tr>
            <th>분류코드</th>
            <th>동기화 시작일시</th>
            <th>동기화 끝난일시</th>
            <th>동기화 결과</th>
          </tr>
        </thead>
        <tbody>
			<c:forEach items="${schdulList}" var="schdulList"	varStatus="status">
				<tr>
					<td>
						<c:choose>
							<c:when test="${schdulList.fjobcd eq 'USER'}">
								직원
							</c:when>
							<c:when test="${schdulList.fjobcd eq 'VISIT'}">
								방문객
							</c:when>
							<c:when test="${schdulList.fjobcd eq 'ZIP'}">
								사진압축
							</c:when>
							<c:when test="${schdulList.fjobcd eq 'FACE'}">
								얼굴
							</c:when>
						</c:choose>
					</td>
					<td><c:out value="${schdulList.fsdt}" /></td>
					<td><c:out value="${schdulList.fedt}" /></td>
					<td>
						<c:choose>
							<c:when test="${schdulList.frsltyn eq 'Y'}">
								성공
							</c:when>
							<c:when test="${schdulList.frsltyn eq 'N'}">
								실패
							</c:when>
						</c:choose>
					</td>
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
