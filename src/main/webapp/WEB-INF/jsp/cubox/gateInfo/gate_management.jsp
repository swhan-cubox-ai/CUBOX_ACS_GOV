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
<div class="sub_box">
	<!--타이틀 공통 --><!--타이틀 공통 -->
	<div class="title_box">
		<div class="title_tx">단말기 관리</div>
		<div class="title_time"><em>2018-02-06</em> Recent Updates </div>
		<div class="route">
			<div class="icon"><img src="./CUBOX2_files/icon_home.png" alt=""></div>
			HOME  &gt; 단말기 제어 관리 &gt; <em>단말기 관리</em> </div>
	</div>
	<!--//타이틀 공통 -->

	<!--검색박스 -->
	<div class="search_box mb_20">
		<div class="search_in">
			<div class="comm_search">
				<select name="" size="1" class="w_100px input_com mr_5">
					<option selected="">마곡 </option>
					<option>마곡</option>
				</select>
				<select name="" size="1" class="w_100px input_com  mr_5">
					<option selected="">선택 </option>
					<option>선택 </option>
				</select>
				<select name="" size="1" class="w_100px input_com  mr_5">
					<option selected="">지역 </option>
					<option>지역</option>
				</select>
				<input type="text" class="w_250px input_com l_radius_no" placeholder="검색어를 입력해 주세요">
				<div class="search_btn"></div>
			</div>
		</div>
	</div>
	<!--//검색박스 -->
	<!--------- 목록--------->
	<div class="com_box ">
		<div class="totalbox">
			<div class="r_btnbox  mb_10">
				<button type="button" class="btn_middle color_color1">KeepOpen</button>
				<button type="button" class="btn_middle color_basic">Open</button>
				<button type="button" class="btn_middle color_gray">Close</button>
			</div>
		</div>
		<!--버튼 -->

		<!--//버튼  -->
		<!--테이블 시작 -->
		<div class="tb_outbox">
			<table class="tb_list">
				<colgroup><col width="5%">
					<col width="12%">
					<col width="15%">
					<col width="15%">
					<col width="">
				</colgroup><thead>
			<tr>
				<th><input type="checkbox" name="" class="mt_5">
				</th>
				<th>GID</th>
				<th>단말기명 </th>
				<th>IP</th>
				<th>펌웨어버전 </th>
			</tr>
			</thead>
				<tbody>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="" class="mt_5"></td>
					<td>11100000</td>
					<td>데스트-더미리더기 </td>
					<td>192.168.20.30</td>
					<td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
				</tr>
				</tbody>
			</table>
		</div>
		<!--------- //목록--------->
		<!-- 페이징 -->
		<div class="pagebox "> <span class="ar1"><img src="./CUBOX2_files/al_icon3.png" alt=""></span> <span class="ar"><img src="./CUBOX2_files/al_icon2.png" alt=""></span> <span class="um on"> 1 </span> <span class="um">2</span> <span class="um">3</span> <span class="ar"><img src="./CUBOX2_files/ar_icon2.png" alt=""></span> <span class="ar1"><img src="./CUBOX2_files/ar_icon3.png" alt=""></span> </div>
		<!-- /페이징 -->
		<!--//본문시작 -->

	</div>
</div>