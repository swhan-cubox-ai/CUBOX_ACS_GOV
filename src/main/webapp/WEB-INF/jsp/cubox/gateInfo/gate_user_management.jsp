<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
						<script type="text/javascript">
						function pageSearch(page){
							if(typeof page == "undefined"){
								page = 1;
							}
						
							f = document.frmSearch;
							f.action = "/gateInfo/gateUserMngmt.do?srchPage=" + page; 
							f.submit();
						}
						
						function userSearch() {
							var searchUserName = $("input[name=userName]").val(),
								tableList = $("#userListTable tbody");
							
							if(tableList.children().length > 0){
								if(typeof searchUserName == "undefined" || searchUserName.trim() =='' || searchUserName == null) {
									alert('출입자명을 입력하세요.');
									$("input[name=userName]").val('');
									return false;
								}
								else {
									this.pageSearch();	
								}
							}
							else {
								alert('단말기 별 사용자를 검색해주세요.');
							}
						}
						
						function resetSearch(){
							$("input[name=userName]").val('');
							this.pageSearch();
						}
						
						function gateUserSearch(){
							if($("input[name=chkField]:checked").length > 0){
								var gateList = [];
								
								$("input[name=chkField]:checked").each(function(){
									gateList.push($(this).val());
								})
								$("#gateList").val(gateList);
								this.pageSearch();
							}
							else {
								alert('단말기를 선택해주세요.');
							}
						}
						
						function getAreaList(value) {
							
							$.ajax({
								type:"GET",
								url:"<c:url value='/gateInfo/areaListToCenter.do' />",
								data:{
									"fptid": value
								},
								dataType: "json",
								success:function(result){
									
									$("#search_item_area").find("option").remove();
									$("#search_item_floor").find("option").remove();
									$("#search_item_floor").append("<option value='0' selected>지역</option>");
									
									if(result.areaListToCenter.length > 0){
										$("#search_item_area").append("<option value='0' selected>선택</option>");
										$.each(result.areaListToCenter, function(i){
											$("#search_item_area").append("<option value='" + result.areaListToCenter[i].code + "'>" 
													+ result.areaListToCenter[i].name + "</option>");
										})
									}
									else {
										$("#search_item_center").val("0");
										$("#search_item_area").append("<option value='0' selected>건물</option>");
										//alert("설정 된 건물이 없습니다. 다른 센터를 선택해주세요.");
									}
								}
							});
							
						}
						
						function getFloorList(value) {
							$.ajax({
								type:"GET",
								url:"<c:url value='/gateInfo/floorListToArea.do' />",
								data:{
									"fptid": value
								},
								dataType: "json",
								success:function(result){
									$("#search_item_floor").find("option").remove();
									
									if(result.floorListToArea.length > 0){
										$("#search_item_floor").append("<option value='0' >선택</option>");
										$("#search_item_floor").val('0');
					
										$.each(result.floorListToArea, function(i){
											$("#search_item_floor").append("<option value='" + result.floorListToArea[i].code + "'>" 
													+ result.floorListToArea[i].name + "</option>");
										})	
									}
									else {
										$("#search_item_floor").append("<option value='0' selected>지역</option>");
										//alert("설정 된 지역이 없습니다. 다른 건물을 선택해주세요.");
									}
								}
							}); 
						}
						
						function getGateList(){
							var gateListTable = $('#gate_list_table tbody');
							
							var searchItemCenter = $('#search_item_center').val();
							var searchItemArea = $('#search_item_area').val();
							var searchItemFloor = $('#search_item_floor').val();
														
							if(this.searchBtnValidation()){
							
								
								if(gateListTable.children().length > 0) {
									gateListTable.empty();
								}
								
								$.ajax({
									type:"POST",
									url:"<c:url value='/gateInfo/gateListToFloor2.do' />",
									data:{
										"searchItemCenter": searchItemCenter,
										"searchItemArea": searchItemArea,
										"searchItemFloor": searchItemFloor
										
									},
									dataType: "json",
									success:function(result){
										if(result.gateInfoList.length > 0){
											$.each(result.gateInfoList, function(i){
												gateListTable.append("<tr><td><input type='checkbox' name='chkField' value='" + result.gateInfoList[i].gid + "'></td><td>" 
														+ result.gateInfoList[i].name + "</td></tr>");
											})
											
											$('#gate-list-modal').modal('show');
											
										}
										else {
											alert('지역에 단말기가 없습니다.');
										}
									}
								});
							}
						}
						
						function searchBtnValidation() {
							if($('#search_item_center').val() == "0"){
								//$('#search_item_gate_user_list').attr('disabled', true);
								alert("센터를 선택해주세요.");
								return false;
							}else{
								$("input[name=userName]").val('');
								//$('#search_item_gate_user_list').attr('disabled', false);
								return true;
							}
							
							/*
							else if($('#search_item_area').val() == null){
								$('#search_item_gate_user_list').attr('disabled', true);
								alert("건물을 선택해주세요.");
								return false;
							}else if($('#search_item_floor').val() == null){
								$('#search_item_gate_user_list').attr('disabled', true);
								alert("지역를 선택해주세요.");
								return false;
							}else{
								$("input[name=userName]").val('');
								$('#search_item_gate_user_list').attr('disabled', false);
								return true;
							}
							*/
						}						
						
						function excelDownLoad() {
							f = document.frmSearch;
							
							var chkValueArray= "",
								chkTextArray= "",
								tableList = $("#userListTable tbody");
							
							if(tableList.children().length > 0){
								$("input[name=excelColumn]:checked").each(function(i){ 
									var chkValue = $(this).val();
									var chkText = $(this).parent().text();
									
									if(i==0){
										chkValueArray = chkValueArray + chkValue + " as CELL" + (i+1);
										chkTextArray = chkTextArray + chkText;
									}else{
										chkValueArray = chkValueArray + "," + chkValue + " as CELL" + (i+1);
										chkTextArray = chkTextArray + "," + chkText;
									}		
								}); 
								
								if(chkValueArray.length == 0){
									alert("한개 이상 체크를 하셔야 엑셀로 다운로드가 가능합니다.");
									return;
								}else{
									$("#download").attr("data-dismiss", "modal");
									$("#download").attr("aria-label", "Close");
								}
								$("#chkValueArray").val(chkValueArray); 
								$("#chkTextArray").val(chkTextArray);
								
								f.action = "/gateInfo/excelDownload.do"
								f.submit();	
							}
							else {
								alert("단말기 별 출입자를 검색하셔야 다운로드가 가능합니다.");
							}
						}
						
						$(function(){
							$("#cancle").click(function(){
								$("#gate-user-excel-modal").modal('hide');
							});
							
							$("#gate_list_cancle").click(function(){
								$("#gate-list-modal").modal('hide');
							});
							
							$("#checkAll").click(function(){
								if($("#checkAll").prop("checked")){
									$("input[name=excelColumn]").prop("checked", true);
								}else{
									$("input[name=excelColumn]").prop("checked", false);
								} 
							});
							
							$("#checkAllGate").click(function(){
								if($("#checkAllGate").prop("checked")){
									$("input[name=chkField]").prop("checked", true);
								}else{
									$("input[name=chkField]").prop("checked", false);
								} 
							});
						});
						</script>
						<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
						<input type="hidden" id="chkValueArray" name="chkValueArray" value=""/>
						<input type="hidden" id="chkTextArray" name="chkTextArray" value="" >
						<input type="hidden" id="gateList" name="gateList" value='<c:out value="${gateList}"/>' >
						<div class="subheader">
							<div class="title-bar float_wrap wd100">
								<h1>단말기별 출입자</h1>
								<div class="board-search-box d-flex" style="width:1000px">
							        <select name="search_item_center" id="search_item_center" class="form-control wd120" onchange="getAreaList(this.value)">
										<option value="0" selected>센터</option>
                                        	<c:forEach items="${centerInfoList}" var="centerInfoList" varStatus="status">
	                                        	<c:if test="${centerInfoList.fkind3 !='10'}">
	                                        		<c:if test="${loginInfo.fkind3 == '10'}">
	                                        			<option value='<c:out value="${centerInfoList.fkind3}"/>' <c:if test="${centerInfoList.fkind3 == searchItemCenter}" >selected</c:if> ><c:out value="${centerInfoList.fvalue}"/></option>
	                                        		</c:if>
	                                        		<c:if test="${loginInfo.fkind3 != '10'}">
	                                        			<c:if test="${loginInfo.fkind3 eq centerInfoList.fkind3}">
	                                        				<option value='<c:out value="${centerInfoList.fkind3}"/>' <c:if test="${centerInfoList.fkind3 == searchItemCenter}" >selected</c:if> ><c:out value="${centerInfoList.fvalue}"/></option>
	                                       				</c:if>
	                                       			</c:if>
	                                        	</c:if>
                                        	</c:forEach>
                                    </select>
                                    <select name="search_item_area" id="search_item_area" class="form-control ml5 wd120" style="max-width:130px;" onchange="getFloorList(this.value)">
                                        <option value="0" selected>건물</option>
                                        <c:forEach items="${areaListToCenter}" var="areaListToCenterList" varStatus="status">
                                        <option value='<c:out value="${areaListToCenterList.code}"/>' <c:if test="${areaListToCenterList.code == searchItemArea}" >selected</c:if> ><c:out value="${areaListToCenterList.name}"/></option>
                                        </c:forEach>
                                    </select>
                                    <select name="search_item_floor" id="search_item_floor" class="form-control ml5 wd120" style="max-width:130px;" onchange="searchBtnValidation()">
                                        <option value="0" selected>지역</option>
                                        <c:forEach items="${floorListToArea}" var="floorListToAreaList" varStatus="status">
                                        <option value='<c:out value="${floorListToAreaList.code}"/>' <c:if test="${floorListToAreaList.code == searchItemFloor}" >selected</c:if> ><c:out value="${floorListToAreaList.name}"/></option>
                                        </c:forEach>
                                    </select>
                                    <button class="btn btn-sm btn-dark ml5" id="search_item_gate_user_list" title="검색" onclick="getGateList()"><i class="fa fa-search"></i></button>
								</div>
                                <!-- search-box -->
								<div class="date-search-box panel">
                                    <div class="d-flex ml10">
                                    	<div class="search_box d-flex ">
                                    		<input placeholder="출입자명을 입력해주세요" id="userName" name="userName" value='<c:out value="${gateInfo.unm}"/>' class="search-word form-control" style="width:250px;" type="search">
                                    		<button class="bbs-search-btn" title="검색" onclick="userSearch();"><i class="fa fa-search"></i></button>
			                    			<button class="btn btn-dark ml5" id="reset" onclick="resetSearch();">초기화</button>
                                    	</div>
                                    </div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12">
								<div id="panel-1" class="panel">
                                    <div class="panel-container collapse show" style="">
                                        <div class="panel-content">
                                        	<div class="float-left mb10">
                                                <b style="display:block;line-height:30px;">출입 가능 인원 : <c:out value="${pagination.totRecord}"/>건</b> 
                                            </div>
                                            <div class="float-right mb10">
                                            	<button type="button" class="btn btn-sm btn-warning" data-toggle="modal" data-target="#gate-user-excel-modal"><span class="fa fa-download mr-1"></span> 엑셀 다운로드</button>
                                            </div>
                                            <div class="frame-wrap table-responsive text-nowrap">
                                                <table class="table m-0 table-bordered table-striped"  id="userListTable">
                                                    <thead> 
														<tr>
															<th>단말기명</th>
															<th>카드번호</th>
                                                            <th>UID</th>
							                                <th>이름</th>
							                                <th>사업명</th>
							                                <th>회사명</th>
							                                <th>부서</th>
                                                        </tr>
													</thead>
													<tbody>
														 <c:forEach items="${userList}" var="userList" varStatus="status">
															<tr>
																<td><c:out value="${userList.fgname}"/></td>
																<td><c:out value="${userList.cfcdno}"/></td>
																<td><c:out value="${userList.fuid}"/></td>
																<td><c:out value="${userList.funm}"/></td> 
																<td><c:out value="${userList.fpartnm1}"/></td>
																<td><c:out value="${userList.fpartnm2}"/></td>
																<td><c:out value="${userList.fpartnm3}"/></td>
															</tr>
														</c:forEach>
													</tbody>
                                                </table>
                                            </div>
                                            <div class="frame-wrap">
                                                <jsp:include page="/WEB-INF/jsp/cubox/common/pagination2.jsp" flush="false" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
							</div>
						</div>
						</form>
						<!-- modal 다운로드 팝업-->
						 <div class="modal fade" id="gate-list-modal" tabindex="-1" role="dialog" aria-hidden="true">
				            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
				                <div class="modal-content" style="width:500px; height:520px;">
				                    <div class="modal-header">
				                        <h5 class="modal-title">단말기 리스트</h5>
				                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="close();">
				                            <span aria-hidden="true"><i class="fa fa-times"></i></span>
				                        </button>
				                    </div>
				                    <div class="modal-body" style="width:500px; height:500px;">
				                    	<div class="check-list" style="height:380px;"> 
					                        <table class="table m-0 table-bordered table-striped" id="gate_list_table">
						                       <colgroup>
						                           <col style="width:10%;">
						                           <col style="width:55%;">
						                       </colgroup>
					                           <thead> 
													<tr>
						                              <th><input type="checkbox" id="checkAllGate"> 전체</th>
						                              <th>단말기명</th>
						                          	</tr>
												</thead>
												<tbody>
												</tbody>
					                        </table>
				                        </div>
				                    </div>
				                    <div class="modal-footer">
				                        <button type="button" id="select" class="btn btn-sm btn-primary" onclick="gateUserSearch()">조회</button>
                    					<button type="button" id="gate_list_cancle" class="btn btn-sm btn-default">취소</button>
				                    </div>
				                </div>
				            </div>
				        </div>
						<!-- modal 다운로드 팝업-->
						 <div class="modal fade" id="gate-user-excel-modal" tabindex="-1" role="dialog" aria-hidden="true">
				            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
				                <div class="modal-content">
				                    <div class="modal-header">
				                        <h5 class="modal-title">엑셀다운로드</h5>
				                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="cancel();">
				                            <span aria-hidden="true"><i class="fa fa-times"></i></span>
				                        </button>
				                    </div>
				                    <div class="modal-body">
				                        <ul>
											<li><input type="checkbox" id="checkAll">전체</li>
										</ul>
				                        <div class="check-list"> 
				                            <ul>
				                            	<li><input type="checkbox" name="excelColumn" value="fgname">단말기명</li>
				                            	<li><input type="checkbox" name="excelColumn" value="cfcdno">카드번호</li>
				                                <li><input type="checkbox" name="excelColumn" value="fuid">UID</li>
				                                <li><input type="checkbox" name="excelColumn" value="funm">출입자명</li>
				                                <li><input type="checkbox" name="excelColumn" value="fpartnm1">사업명</li>
				                                <li><input type="checkbox" name="excelColumn" value="fpartnm2">회사명</li>
				                                <li><input type="checkbox" name="excelColumn" value="fpartnm3">부서</li>
				                            </ul>
				                        </div>
				                    </div>
				                    <div class="modal-footer">
				                        <button type="button" id="download" class="btn btn-sm btn-primary" onclick="excelDownLoad();">다운로드</button>
                    					<button type="button" id="cancle" class="btn btn-sm btn-default">취소</button>
				                    </div>
				                </div>
				            </div>
				        </div>