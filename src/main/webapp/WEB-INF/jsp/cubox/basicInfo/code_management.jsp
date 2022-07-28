<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">
	$(function() {
		$(".title_tx").html("코드 관리");
		
		modalPopup("addCodePopup", "코드 추가", 500, 460);
		modalPopup("editCodePopup", "코드 수정", 500, 410);
		
		$("#btnAddCode").on("click", function(event){
			fnClose();
			$("#addCodePopup").PopupWindow("open");
		});
		
		$("#btnInit").click(function(){
			$("#searchFkind1").val("");
			$("#searchFkind2").val("");
			$("#searchCode").val("");
			$("#searchCodeName").val("");
			$("#srchUseYn").val("");
		});
		
		$("#btnAddClose").click(function(){
			$("#addCodePopup").PopupWindow("close");	
		});
		
		$("#btnEditClose").click(function(){
			fnClose();			
			$("#editCodePopup").PopupWindow("close");	
		});			
	});

	function codeSearch() {
		f = document.frmSearch;
		f.action = "/basicInfo/commcodeMngmt.do";
		f.submit();
	}	

	function fnClose(){
		$("input[name=tdFkind1]").text("");
		$("input[name=tdFkind2]").text("");
		
		$("select[name=fkind1]").val("");
		$("#fkind2").find("option").remove();
		$("#fkind2").append("<option value=''>선택</option>");
		$("#fkind2_txt").val("");
		$("#fkind2_txt").attr("readonly", true);
		$("#fkind2_txt").removeAttr("check");
		$("#fkind2_txt").removeAttr("checkName");

		$("input[name=fkind3]").val("");
		$("input[name=fkind3_org]").val("");
		$("input[name=fvalue]").val("");
		$("input[name=fuseyn]").val("");
		$("input[name=forder]").val("");
	}	
	
	//코드 추가
	function fnCommCodeAddSave() {
		if(fnFormValueCheck ("frmAdd")) {
			var formData = $("#frmAdd").serializeFormJSON ();
			if(confirm("저장하시겠습니까?")){
				$.ajax({
					type:"POST",
					url:"<c:url value='/basicInfo/commcodeAddSave.do' />",
					data:formData,
					dataType:'json',
					//timeout:(1000*30),
					success:function(returnData, status){
						if(returnData.codeSaveCnt > 0) {
							location.reload();
						} else if (returnData.codeSaveCnt < 0) {
							alert("코드가 중복됩니다.\n\n코드를 다시 확인하여 주십시오.");
							$("#fkind3").focus();
						} else { alert("ERROR!");return;}
					}
				});
			}
		}
	}
	
	function fnCommCodeInfoChange(fkind1, fkind2, fkind3, fvalue, fuseyn, forder){
		$("#tdFkind1").text(fkind1);
		$("#tdFkind2").text(fkind2);
		$("#fkind1_e").val(fkind1);
		$("#fkind2_e").val(fkind2);
		$("#fkind3_e").val(fkind3);
		$("#fkind3_org").val(fkind3);
		$("#fvalue_e").val(fvalue);
		$("#fuseyn_e").val(fuseyn);
		$("#forder_e").val(forder);
		
		$("#editCodePopup").PopupWindow("open");
	}
	
	//코드편집
	function fnCommCodeEditSave(){
		if(fnFormValueCheck ("frmEdit")) {
			var formData = $("#frmEdit").serializeFormJSON ();
			if(confirm("수정하시겠습니까?")){
				$.ajax({
					type:"POST",
					url:"<c:url value='/basicInfo/commcodeEditSave.do' />",
					data:formData,
					dataType:'json',
					//timeout:(1000*30),
					success:function(returnData, status){
						if(returnData.codeSaveCnt > 0) {
							location.reload();
						} else{ alert("ERROR!");return;}
					}
				});
			}
		}
	}
	
	//코드사용유무변경
	function fnCommCodeUseynChangeSave(fkind1, fkind2, fkind3, fuseyn){
	
		if(fuseyn == 'Y'){
			var confirmTxt = "사용안함으로 변경하시겠습니까?";
			var fuseyn2 = "N";
		}else{
			var confirmTxt = "사용중으로 변경하시겠습니까?";
			var fuseyn2 = "Y";
		}
	
		if(confirm(confirmTxt)){
			$.ajax({
				type:"POST",
				url:"<c:url value='/basicInfo/commCodeUseynChangeSave.do' />",
				data:{
					"fkind1": fkind1,
					"fkind2": fkind2,
					"fkind3": fkind3,
					"fuseyn": fuseyn2
				},
				dataType:'json',
				//timeout:(1000*30),
				success:function(returnData, status){
					if(returnData.codeSaveCnt > 0) {
						location.reload();
					} else{ alert("ERROR!");return;}
				}
			});
		}
	}
	
	function fnChangeKind2(obj) {
		if(!fnIsEmpty(obj.value) && obj.value == "txt") {
			$("#fkind2_txt").attr("readonly", false);
			$("#fkind2_txt").attr("check","text");
			$("#fkind2_txt").attr("checkName","구분2(입력)");
		} else {
			$("#fkind2_txt").val("");
			$("#fkind2_txt").attr("readonly", true);
			$("#fkind2_txt").removeAttr("check");
			$("#fkind2_txt").removeAttr("checkName");
		}
	}
	
	function fnGetKind2(obj, gb) {
		$.ajax({
			type:"post",
			url:"<c:url value='/basicInfo/getCodeFkind2List2.do' />",
			data:{
				"fkind1": obj.value
			},
			dataType:"json",
			success:function(result){
				if(gb == 'M') {
					$("#searchFkind2").find("option").remove();
					$("#searchFkind2").append("<option value=''>구분2</option>");
					
					if(result.codeFkind2List.length > 0){
						$.each(result.codeFkind2List, function(i){
							$("#searchFkind2").append("<option value='" + result.codeFkind2List[i].fkind2 + "'>" 
									+ result.codeFkind2List[i].fkind2 + "</option>");
						});
					}
				} else if(gb == 'P'){
					$("#fkind2").find("option").remove();
					$("#fkind2").append("<option value=''>선택</option>");
					
					if(result.codeFkind2List.length > 0){
						$.each(result.codeFkind2List, function(i){
							$("#fkind2").append("<option value='" + result.codeFkind2List[i].fkind2 + "'>" 
									+ result.codeFkind2List[i].fkind2 + "</option>");
						});
						$("#fkind2").append('<option value="txt">직접입력</option>');
					}
				}
			}
		});
	}
	
	
</script>

<!--검색박스 -->
<div class="search_box mb_20">
<form name="frmSearch" id="frmSearch" method="post">
	<div class="search_in_bline">
		<div class="comm_search mr_5">
			<select name="searchFkind1" id="searchFkind1" size="1" class="w_150px input_com  mr_5" onchange="fnGetKind2(this, 'M');">
				<option value=''>구분1</option>
				<c:forEach items="${codeFkind1List}" var="fkind1" varStatus="status">
				<option value='<c:out value="${fkind1.fkind1}"/>' <c:if test="${searchFkind1 eq fkind1.fkind1}" >selected</c:if>><c:out value="${fkind1.fkind1}"/></option>
				</c:forEach>
			</select>
			<select name="searchFkind2" id="searchFkind2" size="1" class="w_220px input_com">
				<option value=''>구분2</option>
				<c:forEach items="${codeFkind2List}" var="fkind2" varStatus="status">
				<option value='<c:out value="${fkind2.fkind2}"/>' <c:if test="${searchFkind2 eq fkind2.fkind2}" >selected</c:if>><c:out value="${fkind2.fkind2}"/></option>
				</c:forEach>
			</select>
		</div>
		<div class="comm_search  mr_5">
			<input type="text" name="searchCode" id="searchCode" class="w_200px input_com" value='<c:out value="${searchCode}"/>' placeholder="코드를 입력해 주세요.">
		</div>
		<div class="comm_search  mr_5">
			<input type="text" name="searchCodeName" id="searchCodeName" class="w_200px input_com" value='<c:out value="${searchCodeName}"/>' placeholder="코드명을 입력해 주세요.">
		</div>
		<div class="comm_search  ml_10">
	    <div class="title">사용여부</div>
		<select name="srchUseYn" id="srchUseYn" size="1" class="w_100px input_com">
			<option value="ALL" selected disabled>전체</option>
            <option value="Y" <c:if test="${srchUseYn eq 'Y'}">selected</c:if>>사용</option>
            <option value="N" <c:if test="${srchUseYn eq 'N'}">selected</c:if>>사용안함</option>
		</select>
    	</div>
		<div class="comm_search ml_40">
			<div class="search_btn2" onclick="codeSearch();"></div>
		</div>
		<div class="comm_search ml_45">
			<button type="button" class="comm_btn" id="btnInit">초기화</button>
		</div>
	</div>
</form>	
</div>
<!--//검색박스 -->

<!--------- 목록--------->
<div class="com_box">
	<div class="totalbox">
		<div class="txbox"><b class="fl mr_10">전체 : ${fn:length(codeFullList)}건</b></div>
		<div class="r_btnbox  mb_10"> 
			<button type="button" class="btn_middle color_basic" id="btnAddCode">추가</button>
		</div>
	</div>
	<!--버튼 -->
	<!--테이블 시작 -->
	<div class="tb_outbox">
		<table class="tb_list">
			<colgroup>
				<col width="12%"/>
				<col/>
				<col/>
				<col/>
				<col/>
				<col/>
				<col/>
			</colgroup>
			<thead>
				<tr>
					<th>구분1</th>
					<th>구분2</th>
					<th>코드</th>
					<th>코드명</th>
					<th>정렬순서 </th>
					<th>사용유뮤</th>
					<th>편집</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${codeFullList == null || fn:length(codeFullList) == 0}">
				<tr>
					<td class="h_35px" colspan="7">조회 목록이 없습니다.</td>
				</tr>
				</c:if>			
				<c:forEach items="${codeFullList}" var="codeList" varStatus="status">
				<tr>
				<td><c:out value="${codeList.fkind1}"/></td>
					<td><c:out value="${codeList.fkind2}"/></td>
					<td><c:out value="${codeList.fkind3}"/></td>
					<td><c:out value="${codeList.fvalue}"/></td>
					<td><c:out value="${codeList.forder}"/></td>
					<td><c:if test="${codeList.fuseyn eq 'Y'}"><button type="button" class="btn_small color_basic" onclick="fnCommCodeUseynChangeSave('<c:out value="${codeList.fkind1}"/>','<c:out value="${codeList.fkind2}"/>','<c:out value="${codeList.fkind3}"/>','<c:out value="${codeList.fuseyn}"/>');">사용중</button></c:if>
						<c:if test="${codeList.fuseyn eq 'N'}"><button type="button" class="btn_small color_gray" onclick="fnCommCodeUseynChangeSave('<c:out value="${codeList.fkind1}"/>','<c:out value="${codeList.fkind2}"/>','<c:out value="${codeList.fkind3}"/>','<c:out value="${codeList.fuseyn}"/>');">사용안함</button></c:if></td>
					<td><button type="button" class="btn_small color_basic" data-toggle="modal" data-target="#account-editor-modal" onclick="fnCommCodeInfoChange('${codeList.fkind1}', '${codeList.fkind2}','${codeList.fkind3}','${codeList.fvalue}', '${codeList.fuseyn}', '${codeList.forder}');">편집</button></td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	
	<!-- modal : 등록 -->
	<div id="addCodePopup" class="example_content">
	<form name="frmAdd" id="frmAdd" method="post">
		<div class="popup_box">
			<!--테이블 시작 -->
			<div class="tb_outbox mb_20">
				<table class="tb_write_02 tb_write_p1">
					<col width="25%" />
					<col width="75%" />
					<tbody>
						<tr>
							<th>구분1 <span class="font-color_H">*</span></th>
							<td>
								<select name="fkind1" id="fkind1" class="input_com w_100p" check="text" checkName="구분1" onchange="fnGetKind2(this, 'P');">
									<option value=''>선택</option>
									<c:forEach items="${codeFkind1List}" var="fkind1" varStatus="status">
									<option value='<c:out value="${fkind1.fkind1}"/>'><c:out value="${fkind1.fkind1}"/></option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<th>구분2 <span class="font-color_H">*</span></th>
							<td>
								<select name="fkind2" id="fkind2" class="w_100p input_com" check="text" checkName="구분2" onchange="fnChangeKind2(this)">
									<option value=''>선택</option>
									<%-- <c:forEach items="${authorList}" var="author" varStatus="status">
									<option value='<c:out value="${author.authorId}"/>'><c:out value="${author.authorNm}"/></option>
									</c:forEach> --%>
								</select>
							</td>
						</tr>
						<tr>
							<th>구분2(입력)</th>
							<td>
								<input type="text" id="fkind2_txt" name="fkind2_txt" class="w_100p input_com" maxlength="100" readonly="readonly" placeholder="구분2에서 직접입력을 선택하면 활성화됩니다." />
							</td>
						</tr>
						<tr>
							<th>코드 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="fkind3" name="fkind3" class="w_100p input_com" maxlength="200" placeholder="코드" check="text" checkName="코드" />
							</td>
						</tr>
						<tr>
							<th>코드명 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="fvalue" name="fvalue" class="w_100p input_com" maxlength="200" placeholder="코드명" check="text" checkName="코드명" />
							</td>
						</tr>
						<tr>
							<th>정렬순서 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="forder" name="forder" class="w_100p input_com" maxlength="5" placeholder="정렬순서" check="text" checkName="정렬순서" onkeyup="fnRemoveChar(event)" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!--버튼 -->
			<div class="r_btnbox">
				<div style="display: inline-block;">
					<button type="button" class="comm_btn mr_5" onclick="fnCommCodeAddSave();">등록</button>
					<button type="button" class="bk_color comm_btn mr_5" id="btnAddClose">취소</button>
				</div>
			</div>
			<!--//버튼  -->
		</div>
	</form>
	</div>
	
	<!-- modal : 수정 -->
	<div id="editCodePopup" class="example_content">
	<form name="frmEdit" id="frmEdit" method="post">
		<input type="hidden" id="fkind1_e" name="fkind1">
		<input type="hidden" id="fkind2_e" name="fkind2">
		<input type="hidden" id="fkind3_org" name="fkind3_org">
		<input type="hidden" id="fuseyn_e" name="fuseyn">
		<div class="popup_box">
			<!--테이블 시작 -->
			<div class="tb_outbox mb_20">
				<table class="tb_write_02 tb_write_p1">
					<col width="25%" />
					<col width="75%" />
					<tbody>
						<tr>
							<th>구분1</th>
							<td id="tdFkind1"></td>
						</tr>
						<tr>
							<th>구분2</th>
							<td id="tdFkind2"></td>
						</tr>
						<tr>
							<th>코드 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="fkind3_e" name="fkind3" class="w_100p input_com" maxlength="200" placeholder="코드" check="text" checkName="코드" />
							</td>
						</tr>
						<tr>
							<th>코드명 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="fvalue_e" name="fvalue" class="w_100p input_com" maxlength="200" placeholder="코드명" check="text" checkName="코드명" />
							</td>
						</tr>
						<tr>
							<th>정렬순서 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" id="forder_e" name="forder" class="w_100p input_com" maxlength="5" placeholder="정렬순서" check="text" checkName="정렬순서" onkeyup="fnRemoveChar(event)" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!--버튼 -->
			<div class="r_btnbox">
				<div style="display: inline-block;">
					<button type="button" class="comm_btn mr_5" onclick="fnCommCodeEditSave();">수정</button>
					<button type="button" class="bk_color comm_btn mr_5" id="btnEditClose">취소</button>
				</div>
			</div>
			<!--//버튼  -->
		</div>
	</form>
	</div>