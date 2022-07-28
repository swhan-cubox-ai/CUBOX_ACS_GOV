<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="../frame/sub/head.jsp" />
<script type="text/javascript" src="<c:url value='/js/faceComm.js?ver=<%=Math.random() %>'/>"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#popupNm").html("서버인증");

		$(".nav-link").eq(0).addClass('active');
		$(".nav-link").eq(0).attr('aria-selected','true');
		$(".nav-link").eq(1).removeClass('active');
		$(".nav-link").eq(1).attr('aria-selected','false');

		setTimeout(function(){
			$("#tab_borders_icons-1").addClass('active show');
			$("#tab_borders_icons-2").removeClass('active show');
		},300);
	});
</script>
<body>
<jsp:include page="../frame/sub/popup_top.jsp" />
<div class="popup_box">
	<div class="tab_box2 mb_20">
		<ul class="tabs" data-persist="true">
			<li>
				<a href="#view2_1"><img src="/img/teb_icon3_on.png" alt="" />서버 인증 이미지</a>
			</li>
		</ul>
	</div>
	<div class="tabcontents">
		<div id="view2_1">
			<div class="tab-content border border-top-0 p-3">
				<div class="tab-pane fade show active" id="tab_borders_icons-1" role="tabpanel">
					<div class="idchk_box">
						<dl class="check-after">
							<dt>요청 이미지</dt>
							<dd class="flex-grow-1"></dd>
							<dt>일치 이미지</dt>
							<dd class="flex-grow-1"></dd>
						</dl>
					</div>
					<div class="d-flex align-items-stretch">
						<div class="mt40">
							<img id="imgView" src='/apiInfo/getApiImage.do?srch_id=${srch_id}&img_type=A' onerror="this.src='/images/ncompare_live.png'" style="width: 450px; height: 400px; object-fit: cover; transform: scaleX(-1);">
							<img id="imgView" src='/apiInfo/getApiImage.do?srch_id=${srch_id}&img_type=B' onerror="this.src='/images/compare_origin.png'" style="width: 450px; height: 400px; object-fit: cover; ">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../frame/sub/tail.jsp" />
</body>