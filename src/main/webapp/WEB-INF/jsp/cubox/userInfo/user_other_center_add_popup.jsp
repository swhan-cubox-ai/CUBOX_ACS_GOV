<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<% response.setHeader("Cache-Control", "no-cache"); %>
<% response.setHeader("Pragma", "no-cache"); %>
<% response.setDateHeader("Expires", 0); %>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />

		<script type="text/javascript">		
		function otherCenterUserInfoSearch(){
			var srchFunmWord = $("#srchFunmWord").val();
			if(srchFunmWord == ""){
				alert("출입자 이름을 입력하세요.");
				$("#srchFunmWord").focus();
				return;
			}		
			console.log("srchFunmWord : "+ srchFunmWord);
			$.ajax({
				type:"POST",
				url:"<c:url value='/userInfo/otherCenterUserInfoSearch.do'/>",
				data: {
				   "srchFunmWord" : srchFunmWord
				},
				dataType:'json',  
				success:function(returnData, status){
					if(status == "success") {
						var userInfoList = "";
						for(var i=0; i < returnData.userInfoList.length; i++ ){
							userInfoList 	=	userInfoList 
											+ 	"<tr>" 
											+ 	"	<td><input type='checkbox' name='fuidCheck' value='" + returnData.userInfoList[i].fuid + "|" + returnData.userInfoList[i].cfcdno + "|" + returnData.userInfoList[i].fpartcd1 + "'></td>" 
											+ 	"	<td>" + returnData.userInfoList[i].fuid 		+ 	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].funm 		+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].cfcdno 		+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].fpartnm1 	+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].fpartnm2 	+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].fpartnm3 	+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].fpartcd2 	+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].ftel 		+	"</td>" 
											+ 	"	<td>" + returnData.userInfoList[i].fpartcdnm1 	+	"</td>" 
											+ 	"</tr>";	
						}
						$("#userInfoList").html(userInfoList);
					}else{ alert("ERROR!");return;}
				}
			});
		}
		
		function authSearch(){
			var fcdno = $("#fcdno").val();
			var srchCondWord = $("#srchCondWord").val();
						
			console.log("srchCondWord : "+ srchCondWord);
			$.ajax({
				type:"POST",
				url:"<c:url value='/userInfo/newTotalAuthGroup.do'/>",
				data: {
				   "srchCondWord" : srchCondWord
				},
				dataType:'json',  
				success:function(returnData, status){
					if(status == "success") {
						var totalAuthGroupList = "";
						for(var i=0; i < returnData.totalAuthGroup.length; i++ ){
							totalAuthGroupList = totalAuthGroupList + "<tr><td><input type='checkbox' name='totalFtid' value='" + returnData.totalAuthGroup[i].ftid + "'></td><td>" + returnData.totalAuthGroup[i].ftid + "</td><td>" + returnData.totalAuthGroup[i].fitemnm +"</td></tr>";
						}
						$("#totalAuthGroup").html(totalAuthGroupList);
					}else{ alert("ERROR!");return;}
				}
			});
		}
		 
		$(function(){  			 
			$("#add_arrow").click(function(){ 
								 
				$("input[name=totalFtid]:checked").each(function(){
					var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
					//$("#totalAuthGroup").$(this).closest("tr").remove();
					tag = tag.replace("totalFtid","userFtid");
					
					$("#userAuthGroup").append(tag);
				});
				
				var ckd = $("input[name=totalFtid]:checked").length;
				for(var i=ckd-1; i>-1; i--){
					$("input[name=totalFtid]:checked").eq(i).closest("tr").remove();
				}
			});
			
			$("#delete_arrow").click(function(){
								
				$("input[name=userFtid]:checked").each(function(i){
					var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
					//$("#totalAuthGroup").$(this).closest("tr").remove();
					tag = tag.replace("userFtid","totalFtid");
					
					$("#totalAuthGroup").append(tag);
				});
				
				var ckd = $("input[name=userFtid]:checked").length;
				for(var i=ckd-1; i>-1; i--){
					$("input[name=userFtid]:checked").eq(i).closest("tr").remove();
				}
			});
			
			$("#totalAuthCheckAll").click(function(){
				if($("#totalAuthCheckAll").prop("checked")){
					$("input[name=totalFtid]").prop("checked", true);
				}else{
					$("input[name=totalFtid]").prop("checked", false);
				} 
			});
			
			$("#userAuthCheckAll").click(function(){
				if($("#userAuthCheckAll").prop("checked")){
					$("input[name=userFtid]").prop("checked", true);
				}else{
					$("input[name=userFtid]").prop("checked", false);
				}
			});
		}); 
		
		function otherCenterUserImpSave(){
			var fuidCheckArray= [];
			$("input[name=fuidCheck]:checked").each(function(i){
				fuidCheckArray.push($(this).val());
			});
			
			console.log("fuidCheckArray : " + fuidCheckArray);
			
			if(fuidCheckArray.length == 0){
				alert("출입자를 선택하세요.");
				$("#fcdno").focus();
				return; 
			}
			
			var authArray= [];
			$("input[name=userFtid]").each(function(i){
				authArray.push($(this).val());
			});
			
			if(authArray.length == 0){
				alert("권한 그룹을 추가하세요.");  
				return;
			}
			
			console.log("authArray : " + authArray);
			 
			if(confirm("저장하시겠습니까?")){ 
				$.ajax({
					type:"POST",
					url:"<c:url value='/userInfo/otherCenterUserImpSave.do' />",
					data: {
						"fuidCheckArray" : fuidCheckArray,
						"authArray" : authArray
					},
					dataType:'json', 
					traditional:true,
					//timeout:(1000*30),
					success:function(returnData, status){
						if(status == "success") {
							alert("저장되었습니다.");
							location.reload();
							//getAuthGroup(returnData.fcdno);	
						}else{ alert("ERROR!");return;}
					}
				});
			}	
		} 
		 
		</script> 
		<div class="modal-header">
            <h5 class="modal-title">타센터출입자가져오기</h5>
            <button type="button" class="close" onclick="window.close()">
                <span aria-hidden="true"><i class="fa fa-times"></i></span>
            </button>
        </div>  
        <div class="modal-body">  
        	<div class="border p-3">
				<div class="d-flex align-items-tops position-relative">
					<div class="p-2 wd100">
						<div class="d-flex pop-search">
							<h2 class="h2_type1">출입자</h2>
							<div class="d-flex">
								<input placeholder="출입자 이름을 입력해주세요" id="srchFunmWord" name="srchFunmWord" class="search-word form-control" type="search" onkeypress="if(event.keyCode==13){otherCenterUserInfoSearch();}">
								<button class="bbs-search-btn" title="검색" onclick="otherCenterUserInfoSearch();"><i class="fa fa-search"></i></button>
							</div>
						</div>
						<table class="table m-0 table-bordered table-striped"> 
							<colgroup> 
								<col style="width:5%">
								<col style="width:10%">
								<col style="width:10%">
								<col style="width:10%">
								<col style="width:11%">
								<col style="width:11%">
								<col style="width:11%">
								<col style="width:11%">
								<col style="width:11%">
								<col style="width:10%">
							</colgroup> 
							<thead>
								<tr> 
									<th>선택</th>
									<th>FID</th>
									<th>이름</th>
									<th>카드번호</th>
									<th>파트1</th>
									<th>파트2</th>
									<th>파트3</th>
									<th>전화번호1</th>
									<th>전화번호2</th>
									<th>센터</th>
								</tr>
							</thead>
						</table> 
						<div class="tbody-scroll ht240">
							<table class="table m-0 table-bordered table-striped">
								<colgroup> 
									<col style="width:5%">
									<col style="width:10%">
									<col style="width:10%">
									<col style="width:10%">
									<col style="width:11%">
									<col style="width:11%">
									<col style="width:11%">
									<col style="width:11%">
									<col style="width:11%">
									<col style="width:10%">
								</colgroup> 
								<tbody id="userInfoList">
								</tbody>
							</table>
						</div>  
					</div> 
 
				</div> 
			</div>
			<div class="border p-3 mt15">
				<div class="d-flex align-items-tops position-relative">
					<div class="p-2 wd45">
						<div class="d-flex pop-search">
							<h2 class="h2_type1">전체 권한 그룹</h2>
							<div class="d-flex">
								<input placeholder="권한그룹명을 입력해주세요" id="srchCondWord" name="srchCondWord" class="search-word form-control" type="search" onkeypress="if(event.keyCode==13){authSearch();}">
								<button class="bbs-search-btn" title="검색" onclick="authSearch();"><i class="fa fa-search"></i></button>
							</div>
						</div>
						<table class="table m-0 table-bordered table-striped">
							<colgroup> 
								<col style="width:13%">
								<col style="width:22%">
								<col style="width:65%">
							</colgroup> 
							<thead>
								<tr>
									<th><input type="checkbox" id="totalAuthCheckAll"> 전체</th>
									<th>FID</th>
									<th>그룹명</th>
								</tr>
							</thead>
						</table>
						<div class="tbody-scroll ht240">
							<table class="table m-0 table-bordered table-striped">
								<colgroup>
									<col style="width:13%">
									<col style="width:22%">
									<col style="width:65%">
								</colgroup>
								<tbody id="totalAuthGroup">
									<c:forEach items="${totalAuthGroupList}" var="tagList" varStatus="status">
									<tr>
										<td><input type="checkbox" name='totalFtid' value='<c:out value="${tagList.ftid}"/>'></td>
										<td><c:out value="${tagList.ftid}"/></td>
										<td><c:out value="${tagList.fitemnm}"/></td>
									</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>  
					</div> 
					<div class="p-2 mt100"> 
						<button class="btn btn-ms btn-danger" id="add_arrow">추가<span class="fa fa-angle-double-right ml-2"></span></button>
						<hr>
						<button class="btn btn-ms btn-secondary" id="delete_arrow"><span class="fa fa-angle-double-left mr-2"></span>삭제</button>
					</div>
					<div class="p-2 wd45">
						<div class="d-flex justify-content-between align-items-center">
							<h2 class="h2_type1">적용될 권한 그룹(*)</h2>
						</div>
						<table class="table m-0 table-bordered table-striped">
							<colgroup>
								<col style="width:13%">
								<col style="width:22%">
								<col style="width:65%"> 
							</colgroup>
							<thead>
								<tr>
									<th><input type="checkbox" id="userAuthCheckAll"> 전체</th>
									<th>FID</th>
									<th>그룹명</th>
								</tr>
							</thead>
						</table>
						<div class="tbody-scroll ht240">
							<table class="table m-0 table-bordered table-striped">
								<colgroup>
									<col style="width:13%">
									<col style="width:22%">
									<col style="width:65%">
								</colgroup>
								<tbody id="userAuthGroup"></tbody>
							</table>
						</div>
					</div> 
				</div> 
			</div>
        </div>  
        <div class="modal-footer"> 
            <button type="button" class="btn btn-sm btn-primary" onclick="otherCenterUserImpSave();">저장</button>
            <button type="button" class="btn btn-sm btn-default" onclick="window.close();">취소</button>
        </div>  
              
        