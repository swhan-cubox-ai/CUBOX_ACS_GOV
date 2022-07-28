<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<jsp:include page="../inc/webEditor.jsp"/>
<spring:eval expression="@property['Globals.formatList']" var="formatList" /> 
<script type="text/javascript">
function fnSave(){
	oEditors.getById["txtWebEditor"].exec("UPDATE_CONTENTS_FIELD", []);
	if(fnFormValueCheck("addForm")){
		var formData = new FormData($("#addForm")[0]);
		
		$.ajax({
			url: "<c:url value='/boardInfo/addAction.do'/>",
			type: "POST",
			enctype: "multipart/form-data",
			data: formData,
			//dataType: "json",
			processData: false,
			contentType: false,
			success: function(data){
				console.log(data);
				if(data.result == "1"){
					location.href = "/boardInfo/${bbsId}/list.do";
				}else{
					alert(data.message);
				}
				
				return;
			},
			error: function (jqXHR){
				alert("저장에 실패했습니다.");
				return;
			}
		});
		//$('#addForm').attr('action', '<c:url value='/boardInfo/addAction.do'/>');
		//$('#addForm').submit();
	}
}

function fnList(){
	$('#addForm').attr('action', '<c:url value='/boardInfo/${bbsId}/list.do'/>');
	$('#addForm').submit();
}

$(function() {
	if('${bbsId}' == '00000000000000000001'){
		$(".title_tx").html("공지사항");
	}else if('${bbsId}' == '00000000000000000002'){
		$(".title_tx").html("Q&A");
	}
	
	<c:if test="${option.fileAtchPosblAt eq 'Y'}">
	$('#txtFiles').MultiFile({
		accept: '<c:forEach var="format" items="${formatList}" varStatus="status">${format}<c:if test="${!status.last}">|</c:if></c:forEach>',
		max: Number('${option.posblAtchFileNumber}'),
		list: '#fileList',
		STRING: {
			remove: "<img src='/img/icon_x.png' id='btnRemove'>",
			selected: 'Selecionado: $file',
			denied: '$ext 는(은) 업로드 할수 없는 파일확장자 입니다.',
			duplicate: '$file 는(은) 이미 추가된 파일입니다.'
		}
	});
	</c:if>
});


</script>
<form id="addForm" name="addForm" method="post" enctype="multipart/form-data">
<input type="hidden" id="hidNttId" name="hidNttId" value="${nttId}">
<input type="hidden" id="hidBbsId" name="hidBbsId" value="${bbsId}">
<div class="tb_01_box">
	<table class="tb_write_02 tb_write_p1">
		<col width="150px" />
		<col width="" />
		<tbody>
			<tr>
				<th>제목</th>
				<td>
					<input type="text" id="nttSj" name="nttSj" maxlength="50" size="50" value="${result.nttSj}" class="w_600px input_com">
				</td>
			</tr>
			<tr>
				<th>내용</th>
				<td>
					<textarea id="txtWebEditor" name="txtNttCn" style="width:100%; min-width:100%; display:none;">${result.nttCn}</textarea>	
				</td>
			</tr>
			<c:if test="${option.fileAtchPosblAt eq 'Y'}">
			<tr>
				<th>저장된 파일</th>
				<td>
				<iframe id="ifrFile" src="<c:url value='/file/fileListOwnerIframe.do'/>?ifr_id=ifrFile&atchFileId=${result.atchFileId}" scrolling="no" frameborder="0" width="100%" height="0px" ></iframe>
				</td>
			</tr>
			<tr>
				<th>첨부 파일</th>
				<td>
					<p style="line-height:30px;">사용가능한 확장자 : <c:forEach var="format" items="${formatList}" varStatus="status">${format}<c:if test="${!status.last}">, </c:if></c:forEach></p>
					<input type="file" id="txtFiles" name="txtFiles">
					<div id="fileList" class="file" style="line-height:30px;"></div>
				</td>
			</tr>
			</c:if>
		</tbody>	
	</table>
</div>
</form>

<div class="right_btn mt_20">
	<button id="btnClear" onClick="fnList();" class="btn_middle color_basic">목록</button>
	<button id="btnAdd" onClick="fnSave();" class="btn_middle color_basic">저장</button>
</div>
