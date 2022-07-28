<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="utf-8">
	<title>CUBOX 보안관제시스템</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no, user-scalable=no, minimal-ui">
	<!-- base css -->
	<%-- <link href="/css/import.css" rel="stylesheet" media="screen, print"/> --%>
	<link href="/lib/font-awesome/css/font-awesome.css" rel="stylesheet" />
</head>

<body style="text-align:center; margin-top:50px;">
	<div id="wrap">
		<div class="error-wrap">
			<!-- h1><a href="#"><img src="/images/logo_t.png" alt=""></a></h1-->
			<div class="error">
				<span class="img"><img src="/images/error_img.png" alt=""></span>
				<h2>페이지에 오류가 있습니다.</h2>
				<p>찾으시려는 페이지는 변경되었거나, 현재 사용할 수 없는 페이지입니다.<br>관리자에게 문의하세요.</p>
			</div>
			<div class="error-button txt_center">
				<button type="button" class="btn btn-lg btn-primary" onclick="location.href='/index.do'">메인페이지 바로가기</button>
			</div>
		</div>
	</div>
</body>
</html>
