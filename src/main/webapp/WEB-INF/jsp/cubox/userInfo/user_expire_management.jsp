<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">
function userSearch() {
	$("#srchPage").val("1");
	
	f = document.frmSearch;
	f.action = "/userInfo/userExpireMngmt.do"
	f.submit();
}

function pageSearch(page){
	$("#srchPage").val(page);
	
	f = document.frmSearch;
	f.action = "/userInfo/userExpireMngmt.do";
	f.submit();
}

$(function() {
	$(".title_tx").html("출입자 만료 관리");
	$("#reset").click(function(){
		$("#srchCond option:eq(0)").prop('selected', 'selected');
		$("#srchFutype option:eq(0)").prop('selected', 'selected');
		$("#srchFbioyn option:eq(0)").prop('selected', 'selected');
		$('.search_box input[type="checkbox"]').prop("checked",false).trigger('change');
		$("#srchFexp").prop("checked",true).trigger('change');
		$("#srchFunm").prop("checked",true).trigger('change');
		$("#srchCondWord").val("");
		$("#srchCfsdt").val("");
		$("#srchCfedt").val("");
	});

	$("#checkAll").click(function() {
		if($("#checkAll").prop("checked")){
			$("input[name=excelColumn]").prop("checked", true);
		}else{
			$("input[name=excelColumn]").prop("checked", false);
		}
	});

	$("#checkAllLst").click(function() {
		if($("#checkAllLst").prop("checked")){
			$("input[name=checkbx]").prop("checked", true);
		}else{
			$("input[name=checkbx]").prop("checked", false);
		}
	});
	

	$('#srchCfsdt').datetimepicker({
		timepicker:false,
		format:'Y-m-d'
	});
	
	$('#srchCfedt').datetimepicker({
		timepicker:false,
		format:'Y-m-d'
	});

	$("#srchRecPerPage").change(function(){
		userSearch();
	});
	
	$("#srchCondWord").keyup(function(e){if(e.keyCode == 13) userSearch();});
});

//체크된 사용자 만료처리
function fnChkUserExpire () {
	var tdArr = new Array();
	$.each($("input[name='checkbx']:checked"), function () {
		tdArr.push($(this).val());
	});

	var checkbox = $("input[name=checkbx]:checked");
	if(checkbox.length < 1) {
		alert("만료처리할 사용자를 선택하여 주십시오.");
		return;
	}
	if(fnIsEmpty(tdArr)) {
		alert("만료처리할 사용자가 확인되지 않습니다.");
		return;
	}

	//if(confirm("선택된 사용자의 자료를 만료처리하시겠습니까?\n\r사용자의 사진정보가 삭제되고 카드상태가 Expire로 변경됩니다.")) {
	if(confirm("선택된 사용자의 사진정보를 삭제하시겠습니까.")) {
		$.ajax({
			type:"POST",
			url:"/userInfo/chkUserExpire.do",
			data:{
				"userArr" : tdArr
			},
			traditional:true,
			dataType:'json',
			success: function(data){
				if(data.result == "Y"){
					location.reload();
				}else{
					alert("오류가 발생되었습니다. 다시 시도하여 주십시오.");
				}
			},
			error: function (request, status, error){
				alert("오류가 발생되었습니다. 다시 시도하여 주십시오.");
			}
		});
	}
}
	
function fnUserEditPopup(uid, partcd1, cdno) {
	$("#frmUserEditPop input[name='fuid']").val(uid);
	$("#frmUserEditPop input[name='fpartcd1']").val(partcd1);
	$("#frmUserEditPop input[name='sfcdno']").val(cdno);
	
	openPopup("", "winUserEditPopup", 930, 660);
	$("#frmUserEditPop").submit();
}

</script>
<!--검색박스 -->
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
	<input type="hidden" id="chkValueArray" name="chkValueArray" value=""/>
	<input type="hidden" id="chkTextArray" name="chkTextArray" value="" >
	<input type="hidden" id="fdownresn" name="fdownresn" value=""/>
	<input type="hidden" id="srchFexpireyn" name="srchFexpireyn" value="Y"/>
	<input type="hidden" id="srchPage" name="srchPage" value="${pagination.curPage}"/>
	<div class="search_box mb_20">
		<div class="search_in_bline">
			<div class="comm_search mr_20">
				<select name="srchCond" id="srchCond" size="1" class="w_100px input_com">
					<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
						<option value='<c:out value="${cCombo.siteId}"/>' <c:if test="${userInfoVO.srchCond eq cCombo.siteId}">selected</c:if>><c:out value="${cCombo.siteNm}"/></option>
					</c:forEach>
				</select>
			</div>
			<div class="ch_box  mr_20">
				<input type="checkbox" id="srchFuid" name="srchFuid" class="checkbox" value="fuid" <c:if test="${userInfoVO.srchFuid eq 'fuid'}">checked</c:if>>
				<label for="srchFuid"> FID</label>
				<input type="checkbox" id="srchFunm" name="srchFunm" class="checkbox" value="funm" <c:if test="${userInfoVO.srchFunm eq 'funm'}">checked</c:if>>
				<label for="srchFunm" class="ml_10"> 이름</label>
				<input type="checkbox" id="srchCardNum" name="srchCardNum" class="checkbox" value="cfcdno" <c:if test="${userInfoVO.srchCardNum eq 'cfcdno'}">checked</c:if>>
				<label for="srchCardNum" class="ml_10"> 카드번호</label>
			</div>
			<div class="comm_search mr_20">
				<input type="text" class="w_150px input_com" id="srchCondWord" name="srchCondWord" placeholder="FID/이름/카드번호" value='<c:out value="${userInfoVO.srchCondWord}"/>' />
			</div>
			
			<div class="comm_search mr_10">
				<label for="search-from-date" class="title">카드기간</label>
				<input type="text" id="srchCfsdt" name="srchCfsdt" class="input_datepicker w_130px fl" name="search-from-date" placeholder="카드시작일" value="${userInfoVO.srchCfsdt}" >
				<div class="sp_tx fl">~</div>
				<label for="search-to-date"></label><input type="text" id="srchCfedt" name="srchCfedt" class="input_datepicker w_130px  fl" name="search-to-date" placeholder="카드만료일" value="${userInfoVO.srchCfedt}" >
			</div>
		</div>
		<div class="search_in_bline">
			<div class="comm_search mr_20">
				<div class="title ">출입자타입</div>
				<select name="srchFutype" id="srchFutype" size="1" class="w_100px input_com">
					<option value="">전체</option>
					<c:forEach items="${userType}" var="uType" varStatus="status">
						<option value='<c:out value="${uType.fvalue}"/>' <c:if test="${uType.fvalue eq userInfoVO.srchFutype}">selected</c:if>><c:out value="${uType.fkind3}"/></option>
					</c:forEach>
				</select>
			</div>
			
			<div class="comm_search mr_20">
			<div class="title ">카드상태</div>
			<select name="srchFcardStatus" id="srchFcardStatus" size="1" class="w_100px input_com">
				<option value="">전체</option>
				<c:forEach items="${cardStatus}" var="code" varStatus="status">
					<option value='<c:out value="${code.fvalue}"/>' <c:if test="${code.fvalue eq userInfoVO.srchFutype}">selected</c:if>><c:out value="${code.fkind3}"/></option>
				</c:forEach>
			</select>
			</div>
			
			<div class="comm_search mr_20">
			<div class="title ">권한타입</div>
			<select name="srchAuthType" id="srchAuthType" size="1" class="w_100px input_com">
				<option value="">전체</option>
				<c:forEach items="${authType}" var="code" varStatus="status">
					<option value='<c:out value="${code.fvalue}"/>' <c:if test="${code.fvalue eq userInfoVO.srchAuthType}">selected</c:if>><c:out value="${code.fkind3}"/></option>
				</c:forEach>
			</select>
			</div>
			
			<div class="comm_search">
				<div class="title ">사진등록</div>
				<select name="srchFbioyn" id="srchFbioyn" size="1" class="w_100px input_com">
					<option value="">전체</option>
					<option value="Y" <c:if test="${userInfoVO.srchFbioyn eq 'Y'}">selected</c:if>>등록</option>
					<option value="N" <c:if test="${userInfoVO.srchFbioyn eq 'N'}">selected</c:if>>미등록</option>
				</select>
			</div>
			
			
			<div class="comm_search ml_60">
				<div class="search_btn2" title="검색" onclick="userSearch()"></div>
			</div>
			<div class="comm_search ml_65">
				<button type="button" class="comm_btn" id="reset" >초기화</button>
			</div>
		</div>
	</div>
	<div class="totalbox">
		<div class="txbox">
			<b class="fl mr_10">전체 : <c:out value="${pagination.totRecord}"/>건</b>
			<!-- 건수 -->
			<select name="srchRecPerPage" id="srchRecPerPage" class="input_com w_80px">
			<c:forEach items="${cntPerPage}" var="cntPerPage" varStatus="status">
				<option value='<c:out value="${cntPerPage.fvalue}"/>' <c:if test="${cntPerPage.fvalue eq userInfoVO.srchCnt}">selected</c:if>><c:out value="${cntPerPage.fkind3}"/></option>
			</c:forEach>
			</select>
		</div>
		<!-- 
		<div class="r_btnbox  mb_10">
			<button type="button" class="btn_middle color_basic" onclick="fnChkUserExpire()">사용자 만료 처리</button>
		</div> 
		-->
	</div>
</form>
<!--//검색박스 -->
<!--------- 목록--------->
<div class="com_box">
	<!--테이블 시작 -->
	<div class="tb_outbox">
		<table class="tb_list">
			<colgroup>
				<col width="11%" />
				<col width="11%" />
				<col width="7%" />
				<col width="11%" />
				<col width="11%" />
				<col width="11%" />
				<col width="11%" />
				<col width="11%" />
				<col width="11%" />
			</colgroup>		
			<thead id="thHead">
				<tr id="trUserMgt">
					<th>FID</th>
					<th>이름</th>
					<th>사진</th>
					<th>카드번호</th>
					<th>출입자타입</th>
					<th>카드상태</th>
					<th>권한타입</th>
					<th>출입시작일</th>
					<th>출입만료일</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${userInfoList == null || fn:length(userInfoList) == 0}">
						<tr>
							<td class="h_35px" colspan="9">조회 목록이 없습니다.</td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:forEach items="${userInfoList}" var="uList" varStatus="status">
							<tr>
								<td><a href="#none" onclick="fnUserEditPopup('<c:out value="${uList.fuid}"/>', '<c:out value="${uList.fpartcd1}"/>', '<c:out value="${uList.cfcdno}"/>');"><c:out value="${uList.fuid}"/></a></td>
								<td><a href="#none" onclick="fnUserEditPopup('<c:out value="${uList.fuid}"/>', '<c:out value="${uList.fpartcd1}"/>', '<c:out value="${uList.cfcdno}"/>');"><c:out value="${uList.funm}"/></a></td>
								<td>
								<c:choose>
									<c:when test="${uList.fbioyn eq 'Y'}">
										등록
									</c:when>
									<c:otherwise>
										-
									</c:otherwise>
								</c:choose>
								</td>
								<td><c:out value="${uList.cfcdno}"/></td>
								<td><c:out value="${uList.futypenm}"/></td>
								<td><c:out value="${uList.cfstatusnm}"/></td>
								<td><c:out value="${uList.fauthtypenm}"/></td>
								<td><c:out value="${uList.cfsdt}"/></td>
								<td><c:out value="${uList.cfedt}"/></td>
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

<form id="frmUserEditPop" name="frmUserEditPop" method="post" target="winUserEditPopup" action="/userInfo/userEditPopup.do">
	<input type="hidden" name="fuid">
	<input type="hidden" name="fpartcd1">
	<input type="hidden" name="sfcdno">
</form>