<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="../frame/sub/head.jsp" />
<script type="text/javascript">
$(function(){
	//부모창의 새로고침/닫기/앞으로/뒤로
	$(opener).on('beforeunload', function() {
		 window.close();
	});	
	
	modalPopup("excelUserDownPopup", "엑셀다운로드", 400, 520);
	modalPopup("excelGateDownPopup", "엑셀다운로드", 400, 400);
	
	$("#excelUserDown").on("click", function(event){
		$("#excelUserDownPopup").PopupWindow("open");
	});
	$("#excelGateDown").on("click", function(event){
		$("#excelGateDownPopup").PopupWindow("open");
	});
	
	$("input[name=checkAllG]").click(function() {
		if($(this).prop("checked")){
			$("input[name=excelColumnG]").prop("checked", true);
		}else{
			$("input[name=excelColumnG]").prop("checked", false);
		}
	});	
	$("input[name=checkAll]").click(function() {
		if($(this).prop("checked")){
			$("input[name=excelColumn]").prop("checked", true);
		}else{
			$("input[name=excelColumn]").prop("checked", false);
		}
	});
	
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "394px",
		scrolling: "yes"
	});
	$("#TableID2").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "394px",
		scrolling: "yes"
	});
});

function userSearch(){
	if(fnIsEmpty($("#hidAuthorGroupId").val())) {
		alert("권한그룹을 선택하세요.");
		return;
	}	

	frmSearch.action = "/authorInfo/authUserListPopup.do";
	frmSearch.submit();
}

function excelGateDown(){
	if("${fn:length(glist)}" == "0") {
		alert("엑셀 다운로드할 자료가 없습니다.");
		return;
	}
	
	var chkValueArray= "";
	var chkTextArray= "";
	$("input[name=excelColumnG]:checked").each(function(i){ 
		var chkValue = $(this).val();
		var chkText = $(this).parent().text();
		
		if(i == 0){
			chkValueArray = chkValueArray + chkValue + " as CELL" + (i+1);
			chkTextArray = chkTextArray + chkText;
		}else{
			chkValueArray = chkValueArray + "," + chkValue + " as CELL" + (i+1);
			chkTextArray = chkTextArray + "," + chkText;
		}		
	}); 
	
	if(chkValueArray.length == 0){
		alert("한 개 이상 체크를 하셔야 엑셀로 다운로드가 가능합니다.g");
		return;
	} else {
		$("#download").attr("data-dismiss", "modal");
		$("#download").attr("aria-label", "Close");
	}
	
	$("#chkValueArray").val(chkValueArray);
	$("#chkTextArray").val(chkTextArray.replaceAll(/\s+/g, ""));
	
	$("#hidAuthorGroupNm").val($("#hidAuthorGroupId option:checked").text());
	frmSearch.action = "/authorInfo/authGateListExcel.do";
	frmSearch.submit();
	cancel();
} 

function excelUserDown(){
	if("${fn:length(ulist)}" == "0") {
		alert("엑셀 다운로드할 자료가 없습니다.");
		return;
	}
	
	var chkValueArray= "";
	var chkTextArray= "";
	$("input[name=excelColumn]:checked").each(function(i){ 
		var chkValue = $(this).val();
		var chkText = $(this).parent().text();
		
		if(i == 0){
			chkValueArray = chkValueArray + chkValue + " as CELL" + (i+1);
			chkTextArray = chkTextArray + chkText;
		}else{
			chkValueArray = chkValueArray + "," + chkValue + " as CELL" + (i+1);
			chkTextArray = chkTextArray + "," + chkText;
		}		
	}); 
	
	if(chkValueArray.length == 0){
		alert("한 개 이상 체크를 하셔야 엑셀로 다운로드가 가능합니다.");
		return;
	} else {
		$("#download").attr("data-dismiss", "modal");
		$("#download").attr("aria-label", "Close");
	}
	
	$("#chkValueArray").val(chkValueArray);
	$("#chkTextArray").val(chkTextArray.replaceAll(/\s+/g, ""));
	
	$("#hidAuthorGroupNm").val($("#hidAuthorGroupId option:checked").text());
	frmSearch.action = "/authorInfo/authUserListExcel.do";
	frmSearch.submit();
	cancel2();
} 

function cancel(){
	$("input[name=checkAllG]").prop("checked", false);
	$("input[name=excelColumnG]").prop("checked", false);
	$("#excelGateDownPopup").PopupWindow("close");
}

function cancel2(){
	$("input[name=checkAll]").prop("checked", false);
	$("input[name=excelColumn]").prop("checked", false);
	$("#excelUserDownPopup").PopupWindow("close");
}
</script>
<body>
<div class="popup_head_box" style="display: list-item;">
	<span id="popupNm">권한그룹별 단말기/출입자 목록</span>
	<div class="close">
		<a href="javascript:void(0);" onclick="window.close()"><img src="/img/close_icon.png" alt="닫기" class="mt_10" /></a>
	</div>
</div>
<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
<input type="hidden" id="hidPartCd1" name="hidPartCd1" value="${param.hidPartCd1}">
<input type="hidden" id="hidAuthorGroupNm" name="hidAuthorGroupNm">
<input type="hidden" id="chkValueArray" name="chkValueArray">
<input type="hidden" id="chkTextArray" name="chkTextArray">
<div class="popup_box">
	<!--검색박스 -->
	<div class="search_box mb_20">
		<div class="search_in">
			<div class="comm_search">
				<div class="title">CUBOX</div>
				<div class="title">권한 그룹</div>
				<select name="hidAuthorGroupId" id="hidAuthorGroupId" size="1" class="w_200px input_com" onchange="userSearch();">
					<option value="" selected disabled>선택</option>
				<c:forEach items="${auth}" var="aGroupList" varStatus="status">
					<option value='<c:out value="${aGroupList.authorGroupId}"/>' <c:if test="${aGroupList.authorGroupId eq param.hidAuthorGroupId}">selected</c:if>><c:out value="${aGroupList.authorGroupNm}"/></option>
				</c:forEach>
				</select>
				<div class="search_btn2" onclick="userSearch();"></div>
			</div>
		</div>
	</div>
	<div class="tab_box2 mb_20">
		<ul class="tabs" data-persist="false">
			<li><a href="#view1"><img src="/img/teb_icon1_on.png" alt=""/>단말기 목록</a></li>
			<li><a href="#view2"><img src="/img/teb_icon3_on.png" alt=""/>출입자 목록</a></li>
		</ul>
	</div>	
	<div class="tabcontents">
	<div id="view1">
	<!--//검색박스 -->
	<div class="com_box">
		<div class="totalbox">
			<div class="txbox">전체 : ${fn:length(glist)}건</div>
			<div class="r_btnbox mb_10">
				<button type="button" class="btn_excel" data-toggle="modal" id="excelGateDown">엑셀다운로드</button>
			</div>
		</div>	
		<!--테이블 시작 -->
		<div class="tb_outbox">
			<table class="tb_list2" id="TableID1">
				<colgroup>
					<col width="15%"/>
					<col width="15%"/>
					<col width="15%"/>
					<col width="15%"/>
					<col width="40%"/>
				</colgroup>
				<thead>
					<tr>
						<th>GID</th>
						<th>단말기코드</th>
						<th>단말기위치</th>
						<th>IP</th>
						<th>펌웨어버전</th>
					</tr>
				</thead>
				<tbody>
				<c:choose>
				<c:when test="${fn:length(glist) == 0}">
					<tr>
						<td class="h_35px" colspan="5">조회 목록이 없습니다.</td>
					</tr>
				</c:when>
				<c:otherwise>
				<c:forEach items="${glist}" var="glist" varStatus="status">
					<tr>
						<td><c:out value="${glist.fgid}" /></td>
						<td><c:out value="${glist.fvname}" /></td>
						<td><c:out value="${glist.flname}" /></td>
						<td><c:out value="${glist.fip}" /></td>
						<td><c:out value="${glist.fversion}" /></td>
					</tr>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</tbody>
			</table>
		</div>
	</div>
	<!--------- //목록--------->
	</div>
	<div id="view2">
	<!--//검색박스 -->
	<div class="com_box">
		<div class="totalbox">
			<div class="txbox">전체 : ${fn:length(ulist)}건</div>
			<div class="r_btnbox mb_10">
				<button type="button" class="btn_excel" data-toggle="modal" id="excelUserDown">엑셀다운로드</button>
			</div>
		</div>	
		<!--테이블 시작 -->
		<div class="tb_outbox">
			<table class="tb_list2" id="TableID2">
				<colgroup>
					<col width="10%"/>
					<col width="10%"/>
					<col width="15%"/>
					<col width="10%"/>
					<col width="12%"/>
					<col width="12%"/>
					<col width="11%"/>
					<col width="10%"/>
					<col width="10%"/>
				</colgroup>		
				<thead>
					<tr>
						<th>고유번호</th>
						<th>성명</th>
						<th>부서명</th>
						<th>직급</th>
						<th>휴대전화</th>
						<th>전화번호</th>
						<th>차번호</th>
						<th>기타1</th>
						<th>기타2</th>
					</tr>
				</thead>
				<tbody>
				<c:choose>
				<c:when test="${fn:length(ulist) == 0}">
					<tr>
						<td class="h_35px" colspan="9">조회 목록이 없습니다.</td>
					</tr>
				</c:when>
				<c:otherwise>
				<c:forEach items="${ulist}" var="ulist" varStatus="status">
					<tr>
						<td><c:out value="${ulist.fuid}" /></td>
						<td><c:out value="${ulist.funm}" /></td>
						<td><c:out value="${ulist.fpartnm2}" /></td>
						<td><c:out value="${ulist.fpartnm3}" /></td>
						<td><c:out value="${ulist.hp_no}" /></td>
						<td><c:out value="${ulist.ftel}" /></td>
						<td><c:out value="${ulist.fcarno}" /></td>
						<td><c:out value="${ulist.fetc1}" /></td>
						<td><c:out value="${ulist.fetc2}" />&nbsp;</td>
					</tr>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</tbody>
			</table>
		</div>
	</div>
	<!--------- //목록--------->
	</div>	
	</div>
</div>
</form>

<div id="excelGateDownPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup" style="border-bottom: 0px">
			<div class="search_in">
				<input type="checkbox" id="checkAllG" name="checkAllG" class="checkbox">
				<label for="checkAllG" class="ml_10"> 전체</label>
			</div>
		</div>
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<div class="mr_m3">
					<input type="checkbox" name="excelColumnG" id="fgid" value="fgid" class="checkbox">
					<label for="fgid" class="ml_10"> GID</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumnG" id="fvname" value="fvname" class="checkbox">
					<label for="fvname" class="ml_10"> 단말기코드</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumnG" id="flname" value="flname" class="checkbox">
					<label for="flname" class="ml_10"> 단말기위치</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumnG" id="fip" value="fip" class="checkbox">
					<label for="fip" class="ml_10"> IP</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumnG" id="fversion" value="fversion" class="checkbox">
					<label for="fversion" class="ml_10"> 펌웨어</label>
				</div>
			</div>
		</div>
		<div class="c_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" onclick="excelGateDown();">다운로드</button>
				<button type="button" class="comm_btn bk_color" onclick="cancel();">취소</button>
			</div>
		</div>
	</div>
</div>

<div id="excelUserDownPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup" style="border-bottom: 0px">
			<div class="search_in">
				<input type="checkbox" id="checkAll" name="checkAll" class="checkbox">
				<label for="checkAll" class="ml_10"> 전체</label>
			</div>
		</div>
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<div class="mr_m3">
					<input type="checkbox" name="excelColumn" id="fuid" value="fuid" class="checkbox">
					<label for="fuid" class="ml_10"> 고유번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="funm" value="funm" class="checkbox">
					<label for="funm" class="ml_10"> 이름</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm2" value="fpartnm2" class="checkbox">
					<label for="fpartnm2" class="ml_10"> 부서명</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fpartnm3" value="fpartnm3" class="checkbox">
					<label for="fpartnm3" class="ml_10"> 직급</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="hp_no" value="hp_no" class="checkbox">
					<label for="hp_no" class="ml_10"> 휴대전화</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="ftel" value="ftel" class="checkbox">
					<label for="ftel" class="ml_10"> 전화번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fcarno" value="fcarno" class="checkbox">
					<label for="fcarno" class="ml_10"> 차번호</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fetc1" value="fetc1" class="checkbox">
					<label for="fetc1" class="ml_10"> 기타1</label>
				</div>
				<div class="mr_m3 mt_15">
					<input type="checkbox" name="excelColumn" id="fetc2" value="fetc2" class="checkbox">
					<label for="fetc2" class="ml_10"> 기타2</label>
				</div>
			</div>
		</div>
		<div class="c_btnbox">
			<div style="display: inline-block;">
				<button type="button" class="comm_btn mr_5" onclick="excelUserDown();">다운로드</button>
				<button type="button" class="comm_btn bk_color" onclick="cancel2();">취소</button>
			</div>
		</div>
	</div>
</div>
</body>