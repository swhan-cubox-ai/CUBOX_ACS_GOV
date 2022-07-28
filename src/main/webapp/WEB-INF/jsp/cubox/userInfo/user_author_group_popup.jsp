<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<jsp:include page="../frame/sub/head.jsp" />
<script>
$(document).ready(function() {
	$("#popupNm").html("사용자 권한 그룹");
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "350px",
		scrolling: "yes"
	});
	$("#TableID2").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "350px",
		scrolling: "yes"
	});
	$("#TableID3").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "350px",
		scrolling: "yes"
	});
	
	//부모창의 새로고침/닫기/앞으로/뒤로
	$(opener).on('beforeunload', function() {
		 window.close();
	});	

	$("#add_arrow").click(function() {
		$("input[name=chkCd]:checked").each(function(){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			tag = tag.replace("chkCd","chkUserCd");
			$("#tdUserGroup").append(tag);
		});

		var ckd = $("input[name=chkCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#delete_arrow").click(function(){
		$("input[name=chkUserCd]:checked").each(function(i){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			tag = tag.replace("chkUserCd","chkCd");
			$("#tbTotalGroup").append(tag);
		});

		var ckd = $("input[name=chkUserCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkUserCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#authCheckAll").click(function(){
		if($("#authCheckAll").prop("checked")){
			$("input[name=chkCd]").prop("checked", true);
		}else{
			$("input[name=chkCd]").prop("checked", false);
		}
	});

	$("#userAuthCheckAll").click(function(){
		if($("#userAuthCheckAll").prop("checked")){
			$("input[name=chkUserCd]").prop("checked", true);
		}else{
			$("input[name=chkUserCd]").prop("checked", false);
		}
	});
	
	getUserAuthGroup ('${userInfo.fuid}', '${userInfo.fpartcd1}');		
	modalPopup ("groupDetailPopup", "권한 그룹 상세", 500, 550);
});

function getUserAuthGroup ( fuId, siteId ) {
	//그룹권한 가져오기
	$.ajax({
		type :"POST",
		url :"<c:url value='/userInfo/getUserAuthGroupInfo.do'/>",
		data : { "selFuId": fuId, "selSiteId": siteId },
		dataType : 'json',
		//timeout:(1000*30),
		success : 
			function(returnData, status) {
				if( status == "success") {
					var setTotalMenuList = "";
					var setUserMenuList = "";
			
					if( returnData.totalGroupList != null && returnData.totalGroupList.length > 0 ) {
						for(var i=0; i < returnData.totalGroupList.length; i++ ){
							setTotalMenuList = setTotalMenuList + "<tr><td><input type='checkbox' name='chkCd' value='" + returnData.totalGroupList[i].authorGroupId + "'></td><td>" + returnData.totalGroupList[i].authorGroupId + "</td><td>" + returnData.totalGroupList[i].authorGroupNm +"</td><td><button type='button' class='btn_small color_basic' onclick='fnDetail(\""+returnData.totalGroupList[i].authorGroupId+"\",\""+returnData.totalGroupList[i].authorGroupNm+"\")' id='groupDetail'>상세</button></td></tr>";
						}
					}
			
					if( returnData.userGroupList != null && returnData.userGroupList.length > 0 ) {
						for(var i=0; i < returnData.userGroupList.length; i++ ){
							setUserMenuList = setUserMenuList + "<tr><td><input type='checkbox' name='chkUserCd' value='" + returnData.userGroupList[i].authorGroupId + "'></td><td>" + returnData.userGroupList[i].authorGroupId + "</td><td>" + returnData.userGroupList[i].authorGroupNm +"</td><td><button type='button' class='btn_small color_basic' onclick='fnDetail(\""+returnData.userGroupList[i].authorGroupId+"\",\""+returnData.userGroupList[i].authorGroupNm+"\")' id='groupDetail'>상세</button></td></tr>";
						}
					}
				$("#tbTotalGroup").html(setTotalMenuList);
				$("#tdUserGroup").html(setUserMenuList);
			} else { alert("ERROR!");return;}
		}
	});
}

//권한별 메뉴 저장
function saveAuthMenu() {
	var fuid = $("#hidFuid").val();
	var siteId = $("#hidSiteId").val();
	var groupArray= [];

	if( fnIsEmpty(fuid) || fnIsEmpty(siteId) ){
		alert("사용자 정보가 확인되지 않습니다. 해당 팝업을 종료후 다시 진행하여 주십시오.");
		return;
	}

	$("input[name=chkUserCd]").each(function(i){
		groupArray.push( $(this).val() );
	});
	
	if ( "${userInfo.futype}" != "3" && fnIsEmpty(groupArray) ){
		alert("권한그룹을 추가하세요.");
		return;
	}

	if(confirm("출입자에게 적용할 권한그룹을 저장하시겠습니까?")){
		$.ajax({
			type :"POST",
			url :"<c:url value='/userInfo/saveUserAuthGroup.do' />",
			data : {
				"fuid" : fuid,
				"funm" : "${userInfo.funm}",
				"siteId" : siteId,
				"groupArray" : groupArray
			},
			dataType :'json',
			traditional : true,
			//timeout:(1000*30),
			success : function(returnData, status) {
				if( status == "success" ) {
					alert("권한이 저장되었습니다");
					getUserAuthGroup(returnData.fuid, returnData.siteId );
				} else {
					alert("ERROR!");return;
				}
			}
		});
	}
}

function cancel(){
	$("#groupDetailPopup").PopupWindow("close");
}

function fnDetail(authorGroupId, authorGroupNm) {
	if(!fnIsEmpty(authorGroupId)) {
		$("#labGroupId").html("권한코드 : "+authorGroupId+" / 권한명 : "+authorGroupNm);
		
		//search
		$.ajax({
			type :"POST",
			url :"<c:url value='/authorInfo/getAuthorGroupDetail.do'/>",
			data : { "selAuthorGroupId": authorGroupId, "siteId" : $("#hidSiteId").val() },
			dataType : 'json',
			//timeout:(1000*30),
			success : 
				function(returnData, status) {
					if( status == "success") {
						var authorGroupDetailList = "";

						if( returnData.detailList != null && returnData.detailList.length > 0 ) {
							for(var i=0; i < returnData.detailList.length; i++ ) {
								authorGroupDetailList = authorGroupDetailList + "<tr><td>" + returnData.detailList[i].fgid + "</td><td>" + returnData.detailList[i].fvname +"</td><td>" + returnData.detailList[i].flname +"　</td></tr>";
							}
						}
						$("#tbGroupDtl").html(authorGroupDetailList);
						$("#groupDetailPopup").PopupWindow("open");
					} else { alert("ERROR!");return;}
				}
		});
	} else {
		alert("권한 정보가 확인되지 않습니다. 다시 확인하여 주십시오.");
	}
}
</script>
<body>
<jsp:include page="../frame/sub/popup_top.jsp" />
<div class="popup_box">
	<!--검색박스 -->
	<div class="search_box_popup mb_20">
		<div class="search_in_bline">
			<div class="comm_search mr">
				<input type="hidden" id="hidFuid" value="${userInfo.fuid}" />
				<input type="hidden" id="hidSiteId" value="${userInfo.fpartcd1}" />
				<div class="title">FID :</div>
				<div class="title_text">${userInfo.fuid}</div>
				<div class="title">이름 :</div>
				<div class="title_text">${userInfo.funm} (${userInfo.futypenm})</div>
				<div class="title">카드상태 :</div>
				<div class="title_text">${userInfo.cfstatusnm}<c:if test="${userInfo.fissyn eq 'Y'}"> (카드교부중)</c:if></div>
				<!-- (User:${userInfo.fustatus}/Card:${userInfo.cfstatus}/User&Card:${userInfo.cf_uc_status}/IssueYn:${userInfo.fissyn}) -->
			</div>
			<!-- <div class="comm_search ml_10">
				<button type="button" class="comm_btn">카드정보저장</button>
			</div> -->
		</div>
	</div>
	<!--//검색박스 -->
	<!-- 가로 3칸 --->
	<div class="box_w3 mb_20">
		<!--------- 전체 권한그룹  --------->
		<div class="box_w3_1">
			<div class="totalbox">
				<div class="title_s w_50p fl" style="margin-bottom:7px;">
					<img src="/img/title_icon1.png" alt="" />전체 권한 그룹
				</div>
				<!-- <div class="r_searhbox  mb_5">
					<div class="comm_search">
						<input type="text" class="w_200px input_com l_radius_no" placeholder="검색어를 입력해 주세요">
						<div class="search_btn"></div>
					</div>
				</div> -->
			</div>
			<!--테이블 시작 -->
			<div class="com_box">
				<div class="tb_outbox ">
					<table class="tb_list2" id="TableID1">
						<col width="10%" />
						<col width="25%" />
						<col width="45%" />
						<col width="20%" />
						<thead>
							<tr>
								<th><input type="checkbox" id="authCheckAll"></th>
								<th>권한 코드</th>
								<th>권한 명</th>
								<th>권한 상세</th>
							</tr>
						</thead>
						<tbody id="tbTotalGroup">
							<c:forEach items="${athorGroupList}" var="totList" varStatus="status">
								<tr>
									<td><input type="checkbox" name='chkCd' value='<c:out value="${totList.authorGroupId}"/>'></td>
									<td title="${totList.authorGroupDesc}"><c:out value="${totList.authorGroupId}"/></td>
									<td title="${totList.authorGroupDesc}"><a href="javascript:fnDetail();"><c:out value="${totList.authorGroupNm}"/></a></td>
									<td><button type="button" class="btn_small color_basic" data-toggle="modal" id="groupDetail">상세</button></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<!--------- //전체 권한그룹  --------->
		<!--------- 화살표 이동   --------->
		<c:if test="${userInfo.cf_uc_status eq 'Y' && userInfo.fissyn eq 'N'}">		
		<div class="box_w3_2">
			<div class="btn_box">
				<img src="/img/ar_r.png" alt="" id="add_arrow"/>
			</div>
			<div class="btn_box">
				<img src="/img/ar_l.png" alt="" id="delete_arrow"/>
			</div>
		</div>
		</c:if>
		<!--------- //화살표 이동   --------->
		<!--------- 카드에 적용될 권한그룹   --------->
		<div class="box_w3_3">
			<div class="totalbox">
				<div class="title_s w_300px fl">
					<img src="/img/title_icon1.png" alt="" />출입자에 적용될 권한그룹
				</div>
				<c:if test="${userInfo.cf_uc_status eq 'Y' && userInfo.fissyn eq 'N'}">
				<div class="right_btn">
					<button type="button" class="btn_middle color_basic" onclick="saveAuthMenu()">저장</button>
				</div>
				</c:if>
			</div>
			<!--테이블 시작 -->
			<div class="com_box mt_5">
				<div class="tb_outbox">
					<table class="tb_list2" id="TableID2">
						<col width="10%" />
						<col width="25%" />
						<col width="45%" />
						<col width="20%" />
						<thead>
							<tr>
								<th><input type="checkbox" id="userAuthCheckAll"></th>
								<th>권한 코드</th>
								<th>권한 명</th>
								<th>권한 상세</th>
							</tr>
						</thead>
						<tbody id="tdUserGroup"/>
					</table>

				</div>
			</div>
		</div>
		<!--------- //카드에 적용될 권한그룹--------->
	</div>
	<!-- //가로 3칸 --->
</div>

<!-- modal : groupDetailPopup -->
<div id="groupDetailPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup mb_20">
			<div class="search_in">
				<label style="font-size: 14px;" id="labGroupId"></label>
			</div>
		</div>
		<div class="com_box">
			<div class="tb_outbox">
				<table class="tb_list2" id="TableID3">
					<col width="25%" />
					<col width="35%" />
					<col width="40%" />
					<thead>
						<tr>
							<th>GID</th>
							<th>단말기코드</th>
							<th>단말기위치</th>
						</tr>
					</thead>
					<tbody id="tbGroupDtl"></tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<jsp:include page="../frame/sub/tail.jsp" />
</body>