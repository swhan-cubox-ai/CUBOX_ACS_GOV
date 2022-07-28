<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<body>
<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
	<script type="text/javascript">
	
$(function() {
	$(".title_tx").html("지역 관리");
});

$(function(){ 
	$("#item_floor > dd > span > button").click(function(){
		$("#item_floor > dd > span > button").removeClass("btn-dark").addClass("btn-light");
		$(this).removeClass("btn-light").addClass("btn-dark");
	 	$("#fptid").val($(this).val());
	 	
	 	console.log($(this).val());
	 	
	 	getGateListAndStatus();
	 	
	});
});

function getGateListAndStatus() {
	var dragArea=$('#draggable');
	
	if($('.tooltip').length > 0){
		$('.tooltip').each(function(i){
			$(this).remove();
		})
	}
	dragArea.empty();
	
		$.ajax({
			type:"GET",
			url:"<c:url value='/gateInfo/gateListToFloor.do' />",
			data:{
				"fptid": $('#fptid').val()
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
					}).css({
						"width" : result.gateInfoList[i].xCoordinate * (900/750),
						"height" : result.gateInfoList[i].yCoordinate * (750/600),
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

$(function(){
	$("#btn_gate_list_Position_update").click(function() {
		var dragArea=$('#draggable');
		var list= [];
		
		if($("#fptid").val() != null && $('#fptid').val() != 0){
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

<div class="subheader" style="margin-bottom: 10px;">
	<div class="title-bar float_wrap wd100">
		<!-- search-box -->
		<div class="board-search-box d-flex" style="width: 800px">
			<input type="hidden" name="fptid" id="fptid" />
			<button class="btn btn-sm btn-danger ml5" id="btn_gate_list_Position_update">수정</button>
		</div>
		<!-- /search-box -->
	</div>
</div>
<div class="row">
	<div class="col-md-12">
		<div id="panel-1" class="panel">
			<div class="panel-content">
				<div class="state-exam mt30 mr200">
					<dl class="search_item_floor" id="item_floor">
						
						<dt>본사</dt>
						<dd>
							<span><button type="button" class="btn btn-xs btn-light" onclick="" value="11100">전체</button></span>
						</dd>
					</dl>
				</div>
			</div>
			<div class="panel-container collapse show"
				style="width: 100%; height: 750px;">
				<div class="panel-content drag_area" id="drag_area"
					style="padding: 0;">
					<div class="drag_wrap" id="draggable"></div>
					<span class="img"><img alt="" width="900" height="750"
						id="floorImage" src="/images/floor_plan/113000001.JPG" /></span>
				</div>
			</div>
		</div>
	</div>
</div>
</body>