<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<script type="text/javascript" src="/js/jquery-3.3.1.min.js"></script>
<script>
function fnSave() {
	var form = $('#frmUser')[0];
	var formData = new FormData(form);
	
	formData.append("funm", $("#funm").val());
	
	var files = $("#fimg")[0].files;
	if(files.length > 0) {
		formData.append("fileObj", $("#fimg")[0].files[0]);	
	}
	
	$.ajax({
		url: "/service/addUser.do",
		type: "post",
		data: formData,
		dataType: 'json',
		processData: false,
		contentType: false,
		beforeSend : function(xhr, opts){},
		success: function(data) {
			alert(data);
			console.log(data);			
			alert(data.message);
		},
		error: function(jqXHR){
			alert(jqXHR.responseText);
		}
	});
}

function fnDelete() {
	$.ajax({
		url: "/service/delUser.do",
		type: "post",
		data: {"fuid" : $("#fuid").val()},
		dataType: 'json',
		//processData: false,
		//contentType: false,
		beforeSend : function(xhr, opts){},
		success: function(data) {
			alert(data.message);
		},
		error: function(jqXHR){
			alert(jqXHR.responseText);
		}
	});
}

</script>
</head>
<body>
<h2>외부에서 출입자 등록 및 삭제 Test</h2>
<form id="frmUser" name="frmUser" method="post" enctype="multipart/form-data" onsubmit="return false;">
<h3>출입자 등록</h3>
이름 : <input type="text" id="funm" name="funm">
<button type="button" onclick="fnSave();">저장</button>
<br>
등록이미지 : <input type="file" id="fimg" name="fimg">
<br><br>
<h3>출입자 삭제</h3>
FUID : <input type="text" id="fuid" name="fuid">
<button type="button" onclick="fnDelete();" >삭제</button>
</form>
<br>
<h3>엑셀 업로드</h3>
<form id="frmExcel" name="frmExcel" method="post" enctype="multipart/form-data" onsubmit="return false;">
엑셀파일 : <input type="file" id="excelfile" name="excelfile">
<button type="button" onclick="fnUpload();">업로드</button>
</form>
<script>a
function fnUpload() {
	var form = $('#frmExcel')[0];
	var formData = new FormData(form);
	
	var files = $("#excelfile")[0].files;
	if(files.length > 0) {
		formData.append("fileObj", $("#excelfile")[0].files[0]);	
	}
	
	$.ajax({
		url: "/service/excelUpload.do",
		type: "post",
		data: formData,
		dataType: 'json',
		processData: false,
		contentType: false,
		beforeSend : function(xhr, opts){},
		success: function(data) {
			alert(data.message);
		},
		error: function(jqXHR){
			alert(jqXHR.responseText);
		}
	});
}
</script>
</body>
</html>