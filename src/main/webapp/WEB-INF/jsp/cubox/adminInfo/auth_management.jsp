<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script>
$(document).ready(function() {
	$(".title_tx").html("권한별 메뉴 관리");
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

	$("#add_arrow").click(function(){
		$("input[name=chkMenuCd]:checked").each(function(){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			//$("#tbTotalMenu").$(this).closest("tr").remove();
			tag = tag.replace("chkMenuCd","chkUserMenuCd");

			$("#tdUserMenu").append(tag);
		});

		var ckd = $("input[name=chkMenuCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkMenuCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#delete_arrow").click(function(){
		$("input[name=chkUserMenuCd]:checked").each(function(i){
			var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
			//$("#tbTotalMenu").$(this).closest("tr").remove();
			tag = tag.replace("chkUserMenuCd","chkMenuCd");

			$("#tbTotalMenu").append(tag);
		});

		var ckd = $("input[name=chkUserMenuCd]:checked").length;
		for(var i=ckd-1; i>-1; i--){
			$("input[name=chkUserMenuCd]:checked").eq(i).closest("tr").remove();
		}
	});

	$("#totalAuthCheckAll").click(function(){
		if($("#totalAuthCheckAll").prop("checked")){
			$("input[name=chkMenuCd]").prop("checked", true);
		}else{
			$("input[name=chkMenuCd]").prop("checked", false);
		}
	});

	$("#userAuthCheckAll").click(function(){
		if($("#userAuthCheckAll").prop("checked")){
			$("input[name=chkUserMenuCd]").prop("checked", true);
		}else{
			$("input[name=chkUserMenuCd]").prop("checked", false);
		}
	});

});

function getAuthGroup( authorId ){
	console.log("권한 구분");
	//그룹권한 가져오기
	$.ajax({
		    type :"POST",
		     url :"<c:url value='/adminfo/getAuthMenuInfo.do'/>",
            data : { "selAuthorId": authorId },
		dataType : 'json',
		//timeout:(1000*30),
			 success : function(returnData, status){
                       if( status == "success") {
                           var totalMenuList = "";
                           var  userMenuList = "";
							//console.log(returnData.tbTotalMenu[0]);

							if( returnData.totalMenuList != null && returnData.totalMenuList.length > 0 ){
								for(var i=0; i < returnData.totalMenuList.length; i++ ){
									totalMenuList = totalMenuList + "<tr><td><input type='checkbox' name='chkMenuCd' value='" + returnData.totalMenuList[i].menu_code + "'></td><td>" + returnData.totalMenuList[i].menu_cl_nm + "</td><td>" + returnData.totalMenuList[i].menu_nm +"</td></tr>";
								}
							}

							if( returnData.userMenuList != null && returnData.userMenuList.length > 0 ){
								for(var i=0; i < returnData.userMenuList.length; i++ ){
									userMenuList = userMenuList + "<tr><td><input type='checkbox' name='chkUserMenuCd' value='" + returnData.userMenuList[i].menu_code + "'></td><td>" + returnData.userMenuList[i].menu_cl_nm + "</td><td>" + returnData.userMenuList[i].menu_nm +"</td></tr>";
								}
							}
							//console.log(tbTotalMenuList);
							$("#tbTotalMenu").html(totalMenuList);
							$("#tdUserMenu").html(userMenuList);

						} else { alert("ERROR!");return; }
		}
	});
}

//권한별 메뉴 저장
function saveAuthMenu(){
	var authorId = $("#selAuthorId").val();
	var menuArray= [];

	if( "" == authorId ){
		alert("권한을 선택해주세요.");
		return;
	}

	console.log("authorId:" + authorId);

	$("input[name=chkUserMenuCd]").each(function(i){
		menuArray.push( $(this).val() );
	});
	
	if ("" == menuArray){
		alert("권한을 하나 이상 선택해주세요.");
		return;
	}

	if(confirm("권한에 적용된 메뉴를 저장하시겠습니까?")){
		$.ajax({
			type :"POST",
			 url :"<c:url value='/menuInfo/saveUserAuthMenuGroup.do' />",
			data : {
				       "authorId" : authorId
				    , "menuArray" : menuArray
			       },
		dataType :'json',
	 traditional : true,
			//timeout:(1000*30),
		 success : function(returnData, status){
				       if( status == "success" ){
                           alert("권한이 저장되었습니다");
                           getAuthGroup(returnData.authorId);
                       } else {
                    	   alert("ERROR!");return;
                       }
			}
		});
	}
}

function groupSearch(){
	var srchCondWord = $("#srchCondWord").val();
	console.log("srchCondWord : "+ srchCondWord);
	$.ajax({
		    type :"POST",
             url :"<c:url value='/authInfo/AuthGroupSearch.do'/>",
            data : {
                     "srchCondWord" : srchCondWord
                   },
   		dataType :'json',
		 success : function(returnData, status){
				           if(status == "success") {
                           var authGroupList = "";
                           for(var i = 0; i < returnData.authGroup.length; i ++){
                               authGroupList = authGroupList + "<tr><td><input type='checkbox' name='chkMenuCd' value='"
                                              + returnData.authGroup[i].ftid + "'></td><td>"
                                              + returnData.authGroup[i].ftid + "</td><td>"
                                              + returnData.authGroup[i].fitemnm +"</td></tr>";
       							$("#tbTotalMenu").html(authGroupList);
				           }
                       } else {
                    	   alert("ERROR!");return;
                       }
		          }
	});
}

</script>
	<!--검색박스 -->
<div class="search_box_popup mb_20">
	<div class="search_in_bline">
		<div class="comm_search mr">
			<div class="title">권한 구분</div>
			<select name="selAuthorId" id="selAuthorId" size="1" class="w_200px input_com" onchange="getAuthGroup(this.value);">
				<option selected>권한을 선택하세요</option>
				<c:forEach items="${authorList}" var="aInfoList" varStatus="status">
					<option value='<c:out value="${aInfoList.authorId}"/>'<c:if test="${aInfoList.authorId eq sfcdno}">selected</c:if>><c:out value="${aInfoList.authorNm}"/></option>
				</c:forEach>
			</select>
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
				<img src="/img/title_icon1.png" alt="" />전체 메뉴
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
					<col width="30%" />
					<col width="60%" />
					<thead>
						<tr>
							<th><input type="checkbox" id="totalAuthCheckAll"></th>
							<th>메뉴분류</th>
							<th>메뉴명</th>
						</tr>
					</thead>
					<tbody id="tbTotalMenu">
						<c:forEach items="${totalMenuList}" var="tagList" varStatus="status">
							<tr>
								<td><input type="checkbox" name='chkMenuCd' value='<c:out value="${tagList.menu_code}"/>'></td>
								<td><c:out value="${tagList.menu_cl_nm}"/></td>
								<td><c:out value="${tagList.menu_nm}"/></td>
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
				<img src="/img/title_icon1.png" alt="" />권한에 적용될 메뉴
			</div>
			<div class="right_btn">
				<button type="button" class="btn_middle color_basic" onclick="saveAuthMenu()">저장</button>
			</div>
		</div>
		<!--테이블 시작 -->
		<div class="com_box mt_5">

			<div class="tb_outbox">
				<table class="tb_list2" id="TableID2">
					<col width="10%" />
					<col width="30%" />
					<col width="60%" />
					<thead>
						<tr>
							<th><input type="checkbox" id="userAuthCheckAll"></th>
							<th>메뉴분류</th>
							<th>메뉴명</th>
						</tr>
					</thead>
					<tbody id="tdUserMenu"/>
				</table>

			</div>
		</div>
	</div>
	<!--------- //카드에 적용될 권한그룹--------->
</div>
<!-- //가로 3칸 --->
