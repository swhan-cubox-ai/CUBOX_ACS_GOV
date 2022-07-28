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
						function getAreaList(value) {
							if(value != '0'){
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
										$("#search_item_floor").append("<option value='0' selected disabled>지역</option>");
										
										if(result.areaListToCenter.length > 0){
											$("#search_item_area").append("<option value='0' selected disabled>선택</option>");
											$.each(result.areaListToCenter, function(i){
												$("#search_item_area").append("<option value='" + result.areaListToCenter[i].code + "'>" 
														+ result.areaListToCenter[i].name + "</option>");
											})
										}
										else {
											$("#search_item_center").val("0");
											$("#search_item_area").append("<option value='0' selected disabled>건물</option>");
											$("#btn_gate_list_Status_refresh").attr('disabled', true);
											alert("설정 된 건물이 없습니다. 다른 센터를 선택해주세요.");
										}
									}
								});
							}
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
										$("#search_item_floor").append("<option value='0' selected disabled>선택</option>");
										$("#search_item_floor").val('0');
										$.each(result.floorListToArea, function(i){
											$("#search_item_floor").append("<option value='" + result.floorListToArea[i].code + "'>" 
													+ result.floorListToArea[i].name + "</option>");
										})	
									}
									else {
										$("#search_item_floor").append("<option value='0' selected disabled>지역</option>");
										alert("설정 된 지역이 없습니다. 다른 건물을 선택해주세요.");
									}
								}
							}); 
						}
						
						function bindImageAndGateStatus() {
							var floorImage = "<c:url value='/images/map/" + $("#search_item_floor").val() + ".jpg'></c:url>";
							console.log("floorImage : " + floorImage);
							$('#floorImage').attr("src", floorImage)
							.on("error", function(){
								$('#floorImage').unbind('error');
								$('#floorImage').attr('src', '/images/map/no-img2.png');
							});
			
							getGateListAndStatus();	
						}
						
						function searchBtnValidation() {
							if($('#search_item_center').val() == null){
								$('#btn_gate_list_Status').attr('disabled', true);
								$('#btn_gate_list_Status_refresh').attr('disabled', true);
								$('#floorImage').attr('src','');
								alert("센터를 선택해주세요.");
								return false;
							}else if($('#search_item_area').val() == null){
								$('#btn_gate_list_Status').attr('disabled', true);
								$('#btn_gate_list_Status_refresh').attr('disabled', true);
								$('#floorImage').attr('src','');
								alert("건물을 선택해주세요.");
								return false;
							}else if($('#search_item_floor').val() == null){
								$('#btn_gate_list_Status').attr('disabled', true);
								$('#btn_gate_list_Status_refresh').attr('disabled', true);
								$('#floorImage').attr('src','');
								alert("지역를 선택해주세요.");
								return false;
							}else{
								$('#btn_gate_list_Status').attr('disabled', false);
								return true;
							}
						}
						
						function getGateListAndStatus() {
							var dragArea=$('#draggable');
							
							if($('.tooltip').length > 0){
								$('.tooltip').each(function(i){
									$(this).remove();
								})
							}
							dragArea.empty();
							if(searchBtnValidation()){
								$.ajax({
									type:"GET",
									url:"<c:url value='/gateInfo/gateListToFloor.do' />",
									data:{
										"fptid": $('#search_item_floor').val()
									},
									dataType: "json",
									success:function(result){
										$.each(result.gateInfoList, function(i) {
											var $element = $("<p class='point' id='" + result.gateInfoList[i].gid 
																		+ "' title='"+ result.gateInfoList[i].name + "'/>"),
												$positionStatusList = [
													{ "position" : "left-top", "status": "terminalStatus" }, 
													{ "position" : "right-top", "status": "doorStatus" }, 
													{ "position" : "left-bottom", "status": "coverStatus" }, 
													{ "position" : "right-bottom", "status": "openStatus" }],
												$positionStatusMap = $.makeArray($positionStatusList);
												$quarterElementList = [];
											
											if(result.gateInfoList[i].aliveDiff > 190 || result.gateInfoList[i].aliveDiff == null) {
												$element.css({
													"background-color": "rgba(255, 0, 0, 0.5)"
												})
											}
											else {
												$.map($positionStatusMap, function(value, index){
													var $quarterElement = $("<span class='" + value.position + "'></span>");
													
													$quarterElement.css({
														"background-color": result.gateInfoList[i][value.status] != 0 ? "rgba(0, 0, 255, 0.5)" : "rgba(0, 255, 0, 0.5)",
														"width": "50%",
														"height": "50%"
													});
													$element.append($quarterElement);
												})
											}
											
											$element.attr('select', false);
											$element.attr('tid', result.gateInfoList[i].tid);
											
											$element.click(function() {
												if($element.attr('select') == "false"){
													$element.css({
														"border": "3px solid #0000FF"
													});
													$element.attr('select', true);
													$("input[name=CheckFtid][value='" + $element.attr('id')  +"']").attr('checked', true);
												}
												else{
													$element.css({
														"border": "2px solid #000000"
													});
													$element.attr('select', false);
													$("input[name=CheckFtid][value='" + $element.attr('id')  +"']").attr('checked', false);
												}
											}).tooltip({
												delay:100
											}).css({
												"width" : result.gateInfoList[i].vw * (900/750),
												"height" : result.gateInfoList[i].vh * (750/600),
												"border" : "2px solid #000000"
											}).offset({
												"left" : result.gateInfoList[i].vx * (900/750),
												"top" : result.gateInfoList[i].vy * (750/600)
											});
											
											dragArea.append($element);
											$element.draggable({
												containment: ".drag_area"
											});
										})	
											
										$('#btn_gate_list_Status').attr('disabled', true);
										$('#btn_gate_list_Status_refresh').attr('disabled', false);
									}
								});
							}
						}
						
						function onGateEvent(operation, value) {
							var dragArea=$('#draggable'),
								operationList = [];
							
							if(dragArea.children().length > 0){
								dragArea.children().each(function(i){
									if($(this).attr('select') == 'true'){
										operationList.push($(this).attr('id'));
									}
								});
								
								if(operationList.length > 0){
									var gateControlConfirm = confirm("선택한 단말기에 대해 제어하시겠습니까?");
									if(gateControlConfirm == true){
										$.ajax({
											type:"POST",
											url:"<c:url value='/gateInfo/gateOperation.do' />",
											data:{
												"centerCode": $('#search_item_center').val(),
												"operationList": operationList,
												"operationCode": operation,
												"value": value
											},
											traditional:true,
											dataType: "json",
											success:function(result){
												alert('제어 명령을 완료하였습니다.');
												getGateListAndStatus();
											}
										});
									}
									else {
										alert('취소되었습니다.');
									}
								}
								else {
									alert('제어할 단말기를 선택해 주세요.');
								}
							}
							else {
								alert('지역을 검색해주세요.');
							}
						}
						
						var threadRefresh;
						
						function autoRefresh(me) {
							var dragArea=$('#draggable'),
								intervalSecond = $("#refresh_interval_second"),
								refreshIcon = $("#auto_refresh_icon");
							
							if(dragArea.children().length > 0){
								if(me.value == "false"){
									refreshIcon.removeClass('fa-play').addClass('fa-pause');
									me.value = true;
									threadRefresh = setInterval(function(){this.bindImageAndGateStatus()}, intervalSecond.val() * 1000);
									alert(intervalSecond.val() +  "초마다 자동 새로고침이 시작됩니다.");
								}
								else {
									refreshIcon.removeClass('fa-pause').addClass('fa-play');
									me.value = false;
									clearInterval(threadRefresh);
									alert("자동 새로고침이 중지되었습니다.");
								}
							}
							else {
								alert('지역을 검색해주세요.');
							}
						}
						
						$(function(){
							$("#btn_gate_list_Position_update").click(function() {
								var dragArea=$('#draggable');
								var list= [];
								
								if($("#search_item_floor").val() != null && $('#search_item_floor').val() != 0){
									if(dragArea.children().length > 0){
										dragArea.children().each(function(i){
											list.push({
												"tid": $(this).attr('tid'),
												"vx": $(this).position().left/(900/750),
												"vy": $(this).position().top/(750/600)
											});
										});
										
										$.ajax({
											type:"GET",
											url:"<c:url value='/gateInfo/gatePosition.do' />",
											dataType: "json",
											data: {
												"positionList": JSON.stringify(list)
											},
											traditional:true,
											success:function(result){
												alert('수정되었습니다.');
												$("#btn_gate_list_Status_refresh").click();
											}
										})	
									}
									else {
										alert('지역 이미지가 없어 위치를 수정할 수 없습니다.');
									}
								}
								else {
									alert('지역을 검색해 주세요.');
								}
							})
						})
						</script>
						
						<div class="subheader" style="margin-bottom:10px;">
							<div class="title-bar float_wrap wd100">
								<h1>지역 관리</h1>
                                <!-- search-box -->
                                <div class="board-search-box d-flex" style="width:800px">
                                	<label class="form-control2 wd150p" style="background-color:#e5e5e5; text-align:center; font-weight:bold; color:black;">자동 새로고침(초)</label>
                               		<input class="form-control2 ml5" type="number" id="refresh_interval_second" value="5" min="5" max="180" style="width:80px;"/>
									<button class="btn btn-sm btn-dark ml5 mr60" id="btn_auto_refresh" title="Auto Refresh" onclick="autoRefresh(this)" value="false"><i class="fa fa-play" id="auto_refresh_icon"></i></button>
									<select name="search_item_center" id="search_item_center" class="form-control ml5 wd120" onchange="getAreaList(this.value)">
										<option value="0" selected disabled >센터</option>
                                        <c:forEach items="${centerInfoList}" var="centerInfoList" varStatus="status">
                                        <c:if test="${centerInfoList.fkind3 !='10'}">
                                        	<c:if test="${loginInfo.fkind3 == '10'}">
                                        		<option value='<c:out value="${centerInfoList.fkind3}"/>'><c:out value="${centerInfoList.fvalue}"/></option>
                                        	</c:if>
                                        	<c:if test="${loginInfo.fkind3 != '10'}">
                                        		<c:if test="${loginInfo.fkind3 eq centerInfoList.fkind3}">
                                        			<option value='<c:out value="${centerInfoList.fkind3}"/>'><c:out value="${centerInfoList.fvalue}"/></option>
                                        		</c:if>
                                        	</c:if>
                                        </c:if>
                                        </c:forEach>
                                    </select>
                                    <select name="search_item_area" id="search_item_area" class="form-control ml5 wd120" onchange="getFloorList(this.value)">
                                        <option value="0">건물</option>
                                    </select>
                                    <select name="search_item_floor" id="search_item_floor" class="form-control ml5 wd120" onchange="searchBtnValidation()">
                                        <option value="0">지역</option>
                                    </select>
                                    <button class="btn btn-sm btn-dark ml5" title="검색" onclick="bindImageAndGateStatus();" id="btn_gate_list_Status" disabled><i class="fa fa-search"></i></button>
                                    <button class="btn btn-sm btn-primary ml5" onclick="getGateListAndStatus();" id="btn_gate_list_Status_refresh" disabled>새로고침</button>
                                    <button class="btn btn-sm btn-danger ml5" id="btn_gate_list_Position_update">수정</button>
								</div>
								<!-- /search-box -->
							</div>
						</div>
						<div class="row">
							<div class="col-md-12">
								<div id="panel-1" class="panel">
									<div class="panel-content"> 
	                                     <!-- div class="float-left mt10 ml10">
	                                         <button type="button" class="btn btn-sm btn-danger" onclick="onGateEvent('CMD_TERMINAL_LOCK',1)">Lock</button>
	                                         <button type="button" class="btn btn-sm btn-secondary" onclick="onGateEvent('CMD_TERMINAL_LOCK',0)">UnLock</button>
	                                         <button type="button" class="btn btn-sm btn-primary" onclick="onGateEvent('CMD_DELETE_ALL_USER',0)">Reset User</button>
	                                     </div-->
	                                     <div class="float-right mt10 mr10">
	                                     	<button type="button" class="btn btn-sm btn-primary" onclick="onGateEvent('CMD_TERMINAL_KOPEN',1)">KeepOpen</button>
	                                         <button type="button" class="btn btn-sm btn-danger" onclick="onGateEvent('CMD_TERMINAL_KOPEN',0)" >Open</button>
	                                         <button type="button" class="btn btn-sm btn-secondary" onclick="onGateEvent('CMD_TERMINAL_KOPEN',2)">Close</button>
	                                     </div>
									<div class="state-exam mt30">
										<ul class="">
											<li class="d-flex">
												<p class="point d-flex">
	                                                <span class="left-top red"></span>
	                                                <span class="right-top red"></span>
	                                                <span class="left-bottom red"></span>
	                                                <span class="right-bottom red"></span>
	                                            </p>
												<span class="explain-txt">연결끊김</span>
											</li>
											<li class="d-flex">
												<p class="point d-flex">
	                                                <span class="left-top on"></span>
	                                                <span class="right-top on"></span>
	                                                <span class="left-bottom on"></span>
	                                                <span class="right-bottom on"></span>
	                                            </p>
												<span class="explain-txt">닫힘</span>
											</li>
											<li class="d-flex">
												<p class="point d-flex">
	                                                <span class="left-top blue"></span>
	                                                <span class="right-top on"></span>
	                                                <span class="left-bottom on"></span>
	                                                <span class="right-bottom on"></span>
	                                            </p>
												<span class="explain-txt">덮개 개방</span>
											</li>
											<li class="d-flex">
												<p class="point d-flex">
	                                                <span class="left-top on"></span>
	                                                <span class="right-top blue"></span>
	                                                <span class="left-bottom on"></span>
	                                                <span class="right-bottom on"></span>
	                                            </p>
												<span class="explain-txt">문 개방</span>
											</li>
											<li class="d-flex">
												<p class="point d-flex">
	                                                <span class="left-top on"></span>
	                                                <span class="right-top on"></span>
	                                                <span class="left-bottom on"></span>
	                                                <span class="right-bottom blue"></span>
	                                            </p>
												<span class="explain-txt">도어락 개방</span>
											</li>
											<li class="d-flex">
												<p class="point d-flex">
	                                                <span class="left-top on"></span>
	                                                <span class="right-top on"></span>
	                                                <span class="left-bottom blue"></span>
	                                                <span class="right-bottom on"></span>
	                                            </p>
												<span class="explain-txt">단말기 해체</span>
											</li>
										</ul>
									</div>
								</div>
                                    <div class="panel-container collapse show" style="width:100%; height:750px;">
                                        <div class="panel-content drag_area" id="drag_area" style="padding:0;">
                                            <div class="drag_wrap" id="draggable">
                                            </div>
                                            <span class="img"><img alt="" width="900" height="750" id="floorImage" src=""/></span>
                                        </div>
                                    </div>
                                </div>
							</div>
						</div>