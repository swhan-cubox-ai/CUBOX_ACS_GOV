<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script>
$(document).ready(function() {
	$(".title_tx").html("권한 그룹 관리");
	$("#TableID1").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "530px",
		scrolling: "yes"
	});
	$("#TableID2").chromatable({
		width: "100%", // specify 100%, auto, or a fixed pixel amount
		height: "530px",
		scrolling: "yes"
	});

	$("#add_arrow").click(function() {
		if(!fnChkCd ($("#selAuthorGroupId").val())) return;
		$("input[name=chkDeviceCd]:checked").each(function(){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			tag = tag.replace("chkDeviceCd","chkAuthorDeviceCd");

			$("#tdAuthorDevice").append(tag);
		});

		var ckd = $("input[name=chkDeviceCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkDeviceCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#delete_arrow").click(function() {
		if(!fnChkCd ($("#selAuthorGroupId").val())) return;
		$("input[name=chkAuthorDeviceCd]:checked").each(function(i){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			tag = tag.replace("chkAuthorDeviceCd","chkDeviceCd");

			$("#tbTotalDevice").append(tag);
		});

		var ckd = $("input[name=chkAuthorDeviceCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkAuthorDeviceCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#totalAuthCheckAll").click(function(){
		if($("#totalAuthCheckAll").prop("checked")){
			$("input[name=chkDeviceCd]").prop("checked", true);
		}else{
			$("input[name=chkDeviceCd]").prop("checked", false);
		}
	});

	$("#userAuthCheckAll").click(function(){
		if($("#userAuthCheckAll").prop("checked")){
			$("input[name=chkAuthorDeviceCd]").prop("checked", true);
		}else{
			$("input[name=chkAuthorDeviceCd]").prop("checked", false);
		}
	});
			
	modalPopup ("groupListPopup", "권한 그룹 목록", 850, 650);	//권한 그룹 목록
	modalPopup ("authorGroupPopup", "권한 그룹 추가", 430, 460);	//권한그룹 수정 및 생성			
});

function getAuthGroup ( authorId ) {
	
	//그룹권한 가져오기
	$.ajax({
			type :"POST",
			 url :"<c:url value='/authorInfo/getAuthorGroupInfo.do'/>",
			data : { "selAuthorId": authorId, "selPartcd1": $("#srchCond").val()},
		dataType : 'json',
		//timeout:(1000*30),
		 success : 
			 function(returnData, status) {
				  if( status == "success") {
					  var totalList = "";
					  var  authorCdList = "";
					//console.log(returnData.tbTotalDevice[0]);

					if( returnData.totalDeviceList != null && returnData.totalDeviceList.length > 0 ){
						for(var i=0; i < returnData.totalDeviceList.length; i++ ){
							totalList = totalList + "<tr><td><input type='checkbox' name='chkDeviceCd' value='" + returnData.totalDeviceList[i].fgid + "'></td><td>" + returnData.totalDeviceList[i].fgid + "</td><td>" + returnData.totalDeviceList[i].fvname + "</td><td>" + returnData.totalDeviceList[i].flname +"</td><td>"+ returnData.totalDeviceList[i].siteNm +"</td></tr>";
						}
					}

					if( returnData.authorDeviceList != null && returnData.authorDeviceList.length > 0 ){
						for(var i=0; i < returnData.authorDeviceList.length; i++ ){
							authorCdList = authorCdList + "<tr><td><input type='checkbox' name='chkAuthorDeviceCd' value='" + returnData.authorDeviceList[i].fgid + "'></td><td>" + returnData.authorDeviceList[i].fgid + "</td><td>" + returnData.authorDeviceList[i].fvname + "</td><td>" + returnData.authorDeviceList[i].flname +"</td><td>" + returnData.authorDeviceList[i].siteNm +"</td></tr>";
						}
					}
					//console.log(totalList);
					$("#tbTotalDevice").html(totalList);
					$("#tdAuthorDevice").html(authorCdList);

				} else { alert("ERROR!");return; }
			}
	});
}

//권한 그룹 추가
function addAuthorGroup () {
	cancel ();		
	//popup
	$(".popupwindow_titlebar_text").html("권한 그룹 등록");
	$("#fmAuthorGroup").find("#tdAuthorGroupId").html("자동발번");
	$("#authorGroupPopup").PopupWindow("open");
}

//권한그룹 수정
function updateAuthorGroup (pAuthorGroupId) {
	$(".popupwindow_titlebar_text").html("권한 그룹 편집");
	if(!fnChkCd (pAuthorGroupId)) return;	
	//search
	$.ajax({
			type :"POST",
			 url :"<c:url value='/authorInfo/getAuthorGroup.do'/>",
			data : { "selAuthorGroupId": pAuthorGroupId },
		dataType : 'json',
		//timeout:(1000*30),
		 success : 
			 function(returnData, status) {
				if( status == "success") {	
					if( !fnIsEmpty(returnData.detail) ) {
						$("#fmAuthorGroup").find("#hidAuthorGroupId").val(returnData.detail.authorGroupId);
						$("#fmAuthorGroup").find("#authorGroupNm").val(returnData.detail.authorGroupNm);
						$("#fmAuthorGroup").find("#authorGroupDesc").val(returnData.detail.authorGroupDesc);
						$("#fmAuthorGroup").find("#empYn").val(returnData.detail.empYn);
						$("#fmAuthorGroup").find("#visitYn").val(returnData.detail.visitYn);
						$("#fmAuthorGroup").find("#sortOrdr").val(returnData.detail.sortOrdr);				   			
						$("#fmAuthorGroup").find("#useYn").val(returnData.detail.useYn);
					}	
					$("#fmAuthorGroup").find("#tdAuthorGroupId").html(returnData.detail.authorGroupId);
					$("#authorGroupPopup").PopupWindow("open");
				} else { alert("ERROR!");return;}
			}
	});	
}

function saveAuthorGroup () {
	//save
	var aId 	= $("#fmAuthorGroup").find("#hidAuthorGroupId").val();
	var aNm 	= $("#fmAuthorGroup").find("#authorGroupNm").val();
	var aDesc 	= $("#fmAuthorGroup").find("#authorGroupDesc").val();
	var empYn	= $("#fmAuthorGroup").find("#empYn").val();
	var visitYn	= $("#fmAuthorGroup").find("#visitYn").val();
	var sort 	= $("#fmAuthorGroup").find("#sortOrdr").val();		
	var useYn 	= $("#fmAuthorGroup").find("#useYn").val();
	
	if(fnIsEmpty(aNm)) {
		alert("권한그룹명을 입력하세요.");
		$("#authorGroupNm").focus();
		return;
	}
	
	if(confirm("권한 그룹을 저장하시겠습니까?")) {
		$.ajax({
			type :"POST",
			 url :"<c:url value='/authorInfo/saveAuthorGroup.do'/>",
			data : { 
						"authorGroupId": aId,
						"authorGroupNm" : aNm,
						"authorGroupDesc" : aDesc,
						"empYn" : empYn,
						"visitYn" : visitYn,
						"sortOrdr" : sort,
						"useYn" : useYn
				   },
		dataType : 'json',
		//timeout:(1000*30),
		 success : 
			 function(returnData, status) {
				if( status == "success") {
					if(returnData.result > 0) {
						alert("권한그룹이 저장되었습니다");
						location.reload(); 
					} else {
						if(!fnIsEmpty(returnData.message)) {
							alert(returnData.message);
						} else {
							alert("권한그룹 저장 중 오류가 발생했습니다.");	
						}
					}
				} else { alert("ERROR!"); }
			}
		});
	}		
}

function cancel () {
	$("#fmAuthorGroup").find("#tdAuthorGroupId").html("");	
	$("#fmAuthorGroup").find("#hidAuthorGroupId").val("");
	$("#fmAuthorGroup").find("#authorGroupNm").val("");
	$("#fmAuthorGroup").find("#authorGroupDesc").val("");
	$("#fmAuthorGroup").find("#empYn").val("N");
	$("#fmAuthorGroup").find("#visitYn").val("N");
	$("#fmAuthorGroup").find("#sortOrdr").val("");
	$("#fmAuthorGroup").find("#useYn").val("Y");
	$("#authorGroupPopup").PopupWindow("close");
}

//권한 그룹별 장비 저장 
function saveAuthGroupDevice() {
	var authorGroupId = $("#selAuthorGroupId").val();
	if(!fnChkCd (authorGroupId)) return;
	
	var deviceArray= [];
	//console.log("authorGroupId:" + authorGroupId);

	$("input[name=chkAuthorDeviceCd]").each(function(i){
		deviceArray.push( $(this).val() );
	});
	
	if (fnIsEmpty(deviceArray)){
		alert("장비를 하나 이상 선택해주세요.");
		return;
	}

	if(confirm("권한그룹에 적용된 장비를 저장하시겠습니까?")){
		$.ajax({
			type : "POST",
			url : "<c:url value='/authorInfo/saveAuthorGroupDetail.do' />",
			data : {
				"authorGroupId" : authorGroupId, // 2021-03-16
				"siteId": $("#srchCond").val(),
				"deviceArray" : deviceArray
			},
			dataType :'json',
			traditional : true,
			//timeout:(1000*30),
			success : function(returnData, status) {
				if( returnData.result == "success" ){
					alert("권한그룹의 장비가 적용되었습니다");
					getAuthGroup (returnData.authorGroupId);
				} else {
					if(!fnIsEmpty(returnData.message)) {
						alert(returnData.message);
					} else {
						alert("ERROR!");
					}
					return;
				}
			}
		});
	}
}

function fnChkCd (chkcd) {
	if(fnIsEmpty(chkcd)) {
		alert("권한그룹을 선택하세요.");
		$("#selAuthorGroupId").focus();
		return false;
	} else {
		return true;
	}
}

function listAuthorGroup () {
	//search
	$.ajax({
			type :"POST",
			 url :"<c:url value='/authorInfo/getTotalAuthorGroup.do'/>",
			data : {},
		dataType : 'json',
		//timeout:(1000*30),
		 success : 
			 function(returnData, status) {
				var totalList = "";
				if( status == "success") {	
					if( returnData.list != null && returnData.list.length > 0 ) {
						for(var i=0; i < returnData.list.length; i++ ) {
							totalList += "<tr><td>" + returnData.list[i].authorGroupId + "</td><td>" 
							+ returnData.list[i].authorGroupNm + "</td><td>" 
							+ returnData.list[i].authorGroupDesc +"</td><td>" 
							+ returnData.list[i].empYn +"</td><td>" 
							+ returnData.list[i].visitYn +"</td><td>"
							+ returnData.list[i].sortOrdr +"</td><td>"
							+ (!fnIsEmpty(returnData.list[i].useYn) && returnData.list[i].useYn=="Y"?"<button type='button' class='btn_small color_basic' onclick='fnGroupUpdateUseAt(\""+returnData.list[i].authorGroupId+"\", \"N\")'>사용중</button>":"<button type='button' class='btn_small color_gray' onclick='fnGroupUpdateUseAt(\""+returnData.list[i].authorGroupId+"\", \"Y\")'>사용안함</button>")
							+"</td><td>"
							+"<button type='button' class='btn_small color_basic' onclick='updateAuthorGroup(\""+returnData.list[i].authorGroupId+"\")'>편집</button>"
							+"</td></tr>";
						}
					}	
					$("#tbAgroupList").html(totalList);
					$("#groupListPopup").PopupWindow("open");
				} else { alert("ERROR!");return;}
			}
	});		
}

function fnGroupUpdateUseAt (pAuthorGroupId, pUseYn) {		
	if(!fnChkCd (pAuthorGroupId)) return;	
	if(fnIsEmpty(pUseYn)) { alert("사용유무가 확인되지 않습니다."); return; }
	
	var sMsg = "사용유무를 ["+pUseYn+"]으로 변경하시겠습니까?";
	if(pUseYn == "N") {
		sMsg = "해당 권한 그룹에 속한 사용자의 권한을 삭제합니다!!\n" + sMsg;
	}		
	if(confirm(sMsg)) {
		$.ajax({
			type :"POST",
			 url :"<c:url value='/authorInfo/updateAuthorGroupUseYn.do'/>",
			data : {
					"siteId": $("#srchCond").val(), 
					"authorGroupId": pAuthorGroupId,
					"useYn" : pUseYn
				},
		dataType : 'json',
		//timeout:(1000*30),
		 success : 
			 function(returnData, status) {
				if( status == "success") {
					if(returnData.result > 0) {
						alert("수정 되었습니다.");
						location.reload(); 
					} else {
						if(!fnIsEmpty(returnData.message)) {
							alert(returnData.message);
						} else {
							alert("권한그룹 사용유무 변경 중 오류가 발생했습니다.");	
						}
					}
				} else { alert("ERROR!"); }
			}
		});
	}
}

function fnUserListPop() {
	$("#fmUserList input[name='hidPartCd1']").val($("#srchCond").val());	
	$("#fmUserList input[name='hidAuthorGroupId']").val($("#selAuthorGroupId").val());	
	$("#fmUserList").attr("action", "<c:url value='/authorInfo/authUserListPopup.do' />");
	$("#fmUserList").attr("target", "winUserListByAuthor");
	
	openPopup("", "winUserListByAuthor", 1200, 705);
	$("#fmUserList").submit();
}

</script>
<!--검색박스 -->
<div class="search_box_popup mb_20">
	<div class="search_in_bline">
		<div class="comm_search mr_20">
			<select name="srchCond" id="srchCond" size="1" class="w_120px input_com">
				<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
					<option value='<c:out value="${cCombo.siteId}"/>' <c:if test="${loginSiteId eq cCombo.siteId}">selected</c:if>><c:out value="${cCombo.siteNm}"/></option>
				</c:forEach>
			</select>
		</div>		
		<div class="comm_search mr">
			<div class="title">권한 그룹</div>
			<select name="selAuthorGroupId" id="selAuthorGroupId" size="1" class="w_200px input_com" onchange="getAuthGroup(this.value);">
				<option value="" selected>권한그룹을 선택하세요</option>
				<c:forEach items="${athorGroupList}" var="aGroupList" varStatus="status">
					<option value='<c:out value="${aGroupList.authorGroupId}"/>'><c:out value="${aGroupList.authorGroupNm}"/></option>
				</c:forEach>
			</select>				
			<button type="button" class="btn_middle color_basic ml_10" onclick="listAuthorGroup()">권한그룹 전체 보기</button>
			<button type="button" class="btn_middle color_basic ml_10" onclick="fnUserListPop()">권한그룹별 단말기/출입자 조회</button>
		</div>
	</div>
</div>
<!--//검색박스 -->
<!-- 가로 3칸 --->
<div class="box_w3 mb_20">
	<!--------- 전체 권한그룹  --------->
	<div class="box_w3_1">
		<div class="totalbox">
			<div class="title_s w_50p fl" style="margin-bottom:7px;">
				<img src="/img/title_icon1.png" alt="" />전체 장비
			</div>
		</div>
		<!--테이블 시작 -->
		<div class="com_box">
			<div class="tb_outbox ">
				<table class="tb_list2 tb_list2_2" id="TableID1">
					<col width="10%" />
					<col width="20%" />
					<col width="25%" />
					<col width="25%" />
					<col width="20%" />
					<thead>
						<tr>
							<th><input type="checkbox" id="totalAuthCheckAll"></th>
							<th>GID</th>
							<th>단말기코드</th>
							<th>단말기위치</th>
							<th>사이트</th>
						</tr>
					</thead>
					<tbody id="tbTotalDevice">
						<c:forEach items="${totalDeviceList}" var="tagList" varStatus="status">
							<tr>
								<td><input type="checkbox" name='chkDeviceCd' value='<c:out value="${tagList.fgid}"/>'></td>
								<td><c:out value="${tagList.fgid}"/></td>
								<td><c:out value="${tagList.fvname}"/></td>
								<td><c:out value="${tagList.flname}"/></td>
								<td><c:out value="${tagList.siteNm}"/></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<!--------- //전체 권한그룹  --------->
	<!--------- 화살표 이동   --------->
	<div class="box_w3_2">
		<div class="btn_box">
			<img src="/img/ar_r.png" alt="" id="add_arrow"/>
		</div>
		<div class="btn_box">
			<img src="/img/ar_l.png" alt="" id="delete_arrow"/>
		</div>
	</div>
	<!--------- //화살표 이동   --------->
	<!--------- 카드에 적용될 권한그룹   --------->
	<div class="box_w3_3">
		<div class="totalbox">
			<div class="title_s w_50p fl">
				<img src="/img/title_icon1.png" alt="" />그룹에 적용할 단말기
			</div>
			<div class="right_btn">
				<button type="button" class="btn_middle color_basic" onclick="saveAuthGroupDevice()">저장</button>
			</div>
		</div>
		<!--테이블 시작 -->
		<div class="com_box mt_5">

			<div class="tb_outbox">
				<table class="tb_list2 tb_list2_2" id="TableID2">
					<col width="10%" />
					<col width="20%" />
					<col width="25%" />
					<col width="25%" />
					<col width="20%" />
					<thead>
						<tr>
							<th><input type="checkbox" id="userAuthCheckAll"></th>
							<th>GID</th>
							<th>단말기코드</th>
							<th>단말기위치</th>
							<th>사이트</th>
						</tr>
					</thead>
					<tbody id="tdAuthorDevice"/>
				</table>

			</div>
		</div>
	</div>
	<!--------- //카드에 적용될 권한그룹--------->
</div>
<!-- //가로 3칸 --->

<form id="fmUserList" name="fmUserList" method="post">
	<input type="hidden" name="hidPartCd1">
	<input type="hidden" name="hidAuthorGroupId">
</form>

<!-- modal : authorGroupPopup -->
<div id="authorGroupPopup" class="example_content">
	<form id="fmAuthorGroup" name="fmAuthorGroup">
		<input type="hidden" id="hidAuthorGroupId" name="hidAuthorGroupId" />
		<input type="hidden" id="useYn" name="useYn" value="Y"/>
		<div class="popup_box">
			<!-- <div class="search_box_popup mb_20">
				<div class="search_in">
					<label style="font-size: 14px;" id="labGroupId">권한그룹 상세</label>
				</div>
			</div> -->
			<div class="com_box mb_20">
				<div class="tb_outbox">
					<table class="tb_write_02 tb_write_p1">							
						<col width="40%" />
						<col width="60%" />				
						<tbody>
							<tr>									
								<th>권한그룹코드</th>
								<td id="tdAuthorGroupId"></td>	
							</tr>
							<tr>							
								<th>권한그룹명 <span class="font-color_H">*</span></th>
								<td>
									<input type="text" id="authorGroupNm" name="authorGroupNm" class="w_200px input_com" maxlength="20"/>
								</td>
							</tr>
							<tr>	
								<th>권한그룹설명</th>
								<td>
									<input type="text" id="authorGroupDesc" name="authorGroupDesc" class="w_200px input_com" maxlength="50"/>
								</td>
							</tr>
							<tr>
								<th>직원기본권한여부</th>
								<td>
									<select id="empYn" name="empYn" class="w_80px">
										<option value="N">미사용</option>
										<option value="Y">사용</option>
									</select>
								</td>
							</tr>
							<tr>
								<th>방문기본권한여부</th>
								<td>
									<select id="visitYn" name="visitYn" class="w_80px">
										<option value="N">미사용</option>
										<option value="Y">사용</option>
									</select>
								</td>
							</tr>
							<tr>	
								<th>순서</th>
								<td>
									<input type="text" id="sortOrdr" name="sortOrdr" class="w_80px input_com" maxlength="5" onkeyup="fnRemoveChar(event)"/>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			
			<div class="r_btnbox">
				<div style="display: inline-block;">
					<button type="button" class="comm_btn mr_5" id="saveAuthorGroupBtn" onclick="saveAuthorGroup();">저장</button>
					<button type="button" class="bk_color comm_btn" title="취소" onclick="cancel();">취소</button>
				</div>
			</div>
			
		</div>
	</form>
</div>	

<!-- modal : groupListPopup -->
<div id="groupListPopup" class="example_content">
	<div class="popup_box">
		<div class="search_box_popup mb_20 fl">
			<div class="search_in">
				<label style="font-size: 14px;" id="">권한 그룹 목록</label>					
			</div>	
			<button id="gAddBtn" type="button" class="comm_btn mr_5 mt_5" onclick="addAuthorGroup()">추가</button>			
		</div>
		<div class="com_box">
			<div class="tb_outbox">
				<table class="tb_list2" id="TableID3">
					<col width="12%" />
					<col width="19%" />
					<col width="24%" />
					<col width="9%" />	
					<col width="9%" />
					<col width="7%" />		
					<col width="10%" />
					<col width="10%" />
					<thead>
						<tr>							
							<th>권한그룹 ID</th>
							<th>권한그룹 명</th>
							<th>권한그룹 설명</th>
							<th>직원<br>기본권한</th>
							<th>방문<br>기본권한</th>
							<th>순서</th>
							<th>사용유무</th>
							<th>편집</th>
						</tr>
					</thead>
					<tbody id="tbAgroupList"></tbody>
				</table>
			</div>
		</div>
	</div>
</div>