<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<%-- <jsp:include page="../inc/webEditor.jsp"/> --%>
<style>
 .board{
 	border: 1px solid #ccc;
 	}
 }
</style>
<script type="text/javascript">
document.addEventListener("DOMContentLoaded", function(){
	fnCmntList();
});

$(function() {
	if('${bbsId}' == '00000000000000000001'){
		$(".title_tx").html("공지사항");
	}else if('${bbsId}' == '00000000000000000002'){
		$(".title_tx").html("Q&A");
	}
});

function fnUpdate(){
	f = document.addForm;
	f.action = "/boardInfo/${bbsId}/add.do";
	f.submit();
}
	
function fnList(){
	$('#addForm').attr('action', '<c:url value='/boardInfo/${bbsId}/list.do'/>');
	$('#addForm').submit();
}
	
function fnCmntAdd(){
	if(fnIsEmpty($("#txtCmntCn").val())) {
		alert("덧글 내용을 입력하세요.");
		return;
	}
	
	if(confirm("덧글을 작성하시겠습니까?")){ 
		var queryString =  $('#frmCmnt').serialize();
		$.ajax({
			type : "post",
			url  : "/boardInfo/cmntAddAjax.do",
			data : queryString,
			dataType :'json', 
			success  : function(data, status ){
				if(data.result == "Y"){
					 fnCmntList();
					 $("#txtCmntCn").val("");
				}
			}
		});
	}
}

function fnCmntList(){
	var queryString =  $('#frmCmnt').serialize();
	
	$.ajax({
		type : "post",
		url  : "/boardInfo/cmntListAjax.do",
		data : queryString,
		dataType :'json', 
		success:function(returnData, status){
			if(status == "success") {
				innerHtml = "";
				for(var i = 0; i < returnData.list.length; i++){
					cmntData = returnData.list[i];

					innerHtml += "<div class=\"comment_box\">";
					innerHtml += "	<div class=\"user_tx\">";
					innerHtml += "		<em>"+ cmntData.registNm +"</em> "+ cmntData.registDt+"";
					innerHtml += "		<div class=\"btnbox\">";
					if("${fsiteId}" == cmntData.registId){
						innerHtml += "<a href=\"javascript:fnCmntUpdate('"+ cmntData.commentNo +"');\" id=\"btnCmnUpdateUse\">수정</a>";
						innerHtml += "<a href=\"javascript:void(0);\" id=\"btnCmnUpdateCancel\" name=\"btnCmnUpdateCancel\" data-id=\""+ cmntData.CMNT_SN +"\" style=\"display:none\">수정취소</a>";
						innerHtml += "<a href=\"javascript:fnCmntDelete('"+ cmntData.commentNo +"');\" id=\"btnCmnDelete\">삭제</a> <br>";
					}
					innerHtml += "		</div>";
					innerHtml += "	</div>";
					innerHtml += "	<div class=\"divCmnt\" id=\"divCmnt\" name=\"divCmnt\" data-id=\""+ cmntData.commentNo +"\">"+ cmntData.commentCn.replace(/(?:\r\n|\r|\n)/g, '<br />') +"</div>";
					innerHtml += "</div>";
				}
				$("#tbCmntList").html(innerHtml);
				
			}else{ alert("ERROR!");return;}
		}
	});
}
	
function fnCmntDelete(cmntCn){
	if(confirm("덧글을 삭제하시겠습니까?")){ 
		$("input:hidden[id=hidcmntNo]").val(cmntCn);	
		var queryString =  $('#frmCmnt').serialize();
		$.ajax({
			type : "post",
			url  : "/boardInfo/cmntDisableAjax.do",
			data : queryString,
			dataType :'json', 
			success  : function(data, status ){
				if(data.result == "Y"){
					 fnCmntList();
					 
				}
			}
		});
	}
}

function btnCmntUpdate(){
	if(confirm("덧글을 수정하시겠습니까?")){
		var UpdateCmntCn = $("input[name=txtUpdateCmntCn]").val();
		$("input:hidden[id=hidtxtUpdateCmntCn]").val(UpdateCmntCn);
		
		var queryString =  $('#frmCmnt').serialize();
		$.ajax({
			type : "post",
			url  : "/boardInfo/cmntUpdateAjax.do",
			data : queryString,
			dataType :'json', 
			success  : function(data, status ){
				if(data.result == "Y"){
					 fnCmntList();
					 
				}
			}
		});
	}
}
	
function fnCmntUpdate(cmntCn){
	$("input:hidden[id=hidcmntNo]").val(cmntCn);
	var queryString =  $('#frmCmnt').serialize();

	console.log(queryString);
	$.ajax({
		type : "post",
		url  : "/boardInfo/cmntDetailAjax.do",
		data : queryString,
		dataType :'json', 
		success:function(returnData, status){
			if(status == "success") {
				$("div[name='divCmnt'][data-id="+ cmntCn +"]").attr("class","inputbox");

				innerHtml = "";
				innerHtml += "<textarea id=\"txtUpdateCmntCn\" name=\"txtUpdateCmntCn\" class=\"intx\">"+ returnData.detail.commentCn +"</textarea>";
				innerHtml += "<input type=\"button\" class=\"savebtn\" value=\"수정\" id=\"btnCmnUpdate\" onclick=\"btnCmntUpdate();\">";

				$("a[name='btnCmnUpdateUse'][data-id="+ cmntCn +"]").hide();
				$("a[name='btnCmnUpdateCancel'][data-id="+ cmntCn +"]").show();
				$("div[name='divCmnt'][data-id="+ cmntCn +"]").html(innerHtml);
			}else{
				alert("진행중 오류가 발생하였습니다.");
			}
		}
	});
}

function fnDelete() {
	if(!confirm("삭제하시겠습니까?")) return;
	
	$.ajax({
		type : "post",
		url  : "/boardInfo/delAction.do",
		data : {"hidNttId" : "${nttId}", "hidbbsId" : "${bbsId}"},
		dataType :'json', 
		success  : function(data, status){
			if(data.result == "Y"){
				 location.href = "/boardInfo/${bbsId}/list.do";
			} else {
				alert("삭제 중 오류가 발생하였습니다.");
			}
		}
	});	
}
</script>

<form id="addForm" name="addForm" method="post" enctype="multipart/form-data">
<input type="hidden" name="hidNttId" value="${nttId}">
<input type="hidden" id="hidAddTy" name="hidAddTy" value="${hidAddTy}">
<input type="hidden" name="hidbbsId" value="${bbsId}">

<div class="tb_01_box">
	<table class="tb_write_02 tb_write_p1 board">
		<col width="15%" />
		<col width="20%" />
		<col width="15%" />
		<col width="20%" />
		<col width="15%" />
		<col width="15%" />
		<tbody>
			<tr>
				<th height="25">제목</th>
				<td colspan="5" class="line">
					${result.nttSj}
				</td>
			</tr>		
			<tr>
				<th height="25">작성자</th>
				<td class="line">${result.registNm}</td>
				<th>작성일시</th>
				<td>${result.registDt}</td>
				<th>조회 수</th>
				<td>${result.inqireCo}</td>
			</tr>
			<c:if test="${option.fileAtchPosblAt eq 'Y'}">
				<tr>
					<th height="25">첨부파일</th>
					<td colspan="5" class="line">
						<iframe id="ifrFile" src="<c:url value='/file/fileListIframe.do'/>?ifr_id=ifrFile&atchFileId=${result.atchFileId}" scrolling="no" frameborder="0" width="100%" height="0px" ></iframe>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="6">
					<c:out escapeXml="false" value="${fn:replace(result.nttCn,crcn,br)}"/>
				</td>
			</tr>
		</tbody>
	</table>
</div>
<div class="table_view_text" style="padding:25px;">
</div>
</form>
<c:if test="${result.replyAt eq 'Y'}">
<form id="frmCmnt" name="frmCmnt" method="post">
<input type="hidden" name="hidNttId" value="${nttId}">
<input type="hidden" name="hidbbsId" value="${bbsId}">
<input type="hidden" id="hidcmntNo" name="hidcmntNo">
<input type="hidden" id="hidtxtUpdateCmntCn" name="hidtxtUpdateCmntCn">
<div class="bbs_re">
	<div id="tbCmntList">
	</div>
	<div class="comment_re">
		<div class="inputbox">
			<textarea id="txtCmntCn" name="txtCmntCn" class="intx" check="text" checkName="덧글 내용" maxlength="200"></textarea>
			<input type="button" class="savebtn" value="확인" onclick="fnCmntAdd();" id="btnCmnAdd">
		</div>
	</div>
</div>
</form>
</c:if>

<div class="right_btn mt_20">
	<button id="btnClear" onClick="fnList();" class="btn_middle color_basic">목록</button>
	<c:if test="${sessionScope.loginVO.fsiteid eq result.registId || sessionScope.loginVO.author_id eq '00001'}"> 
	<button id="btnAdd" onClick="fnUpdate();" class="btn_middle color_basic">수정</button>
	<button id="btnAdd" onClick="fnDelete();" class="btn_middle color_gray">삭제</button>
	</c:if>
</div>
