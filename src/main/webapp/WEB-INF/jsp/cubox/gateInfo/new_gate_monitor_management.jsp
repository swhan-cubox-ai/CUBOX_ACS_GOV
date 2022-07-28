<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="euc-kr"%>
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
	floorSearch();
});

//
function getAreaList( value ){
	$.ajax({
		    type : "GET",
		     url : "<c:url value='/gateInfo/floorListToCenter.do' />",
		    data :{ "siteId": value },
		dataType : "json",
		 success : function(result){
			
				       $("#search_item_floor").find("option").remove();
			           $("#search_item_name").find("option").remove();
			           $("#search_item_name").append("<option value='0' selected>지역</option>");
			
			           if( result.floorListToCenter.length > 0 ){
				           $("#search_item_floor").append("<option value='0' selected>선택</option>");
				           $.each( result.floorListToCenter,
				               function(i){
					               $("#search_item_floor").append("<option value='" + result.floorListToCenter[i].code + "'>" + result.floorListToCenter[i].name + "</option>");
				           })
			           } else {
				           $("#search_item_center").val("0");
				           $("#search_item_floor").append("<option value='0' selected>건물</option>");
				          //alert("설정 된 건물이 없습니다. 다른 센터를 선택해주세요.");
			           }
		           }
	});
}

function floorSearch(){
	var   siteId = $("#search_item_center").val();
	var floorIdx = $("#search_item_floor").val();
	
	$("#floorImage").attr("src", "/file/displayImage.do?siteId="+siteId+"&floorIdx="+floorIdx);
	
	getGateListAndStatus();
}



function getGateListAndStatus() {
	
	
	var   siteId = $("#search_item_center").val();
	var   floor = $("#search_item_floor").val();
	
	var dragArea=$('#draggable');
	
	if($('.tooltip').length > 0){
		$('.tooltip').each(function(i){
			$(this).remove();
		})
	}
	dragArea.empty();
	
		$.ajax({
			type:"GET",
			url:"<c:url value='/gateInfo/newGateListToFloor.do' />",
			data:{
				"floor": floor,
				"siteId" : siteId
			},
			dataType: "json",
			success:function(result){
				$.each(result.gateInfoList, function(i) {
					var $element = $("<p class='point' id='" + result.gateInfoList[i].deviceId 
												+ "' title='"+ result.gateInfoList[i].deviceNm + "'/>"),
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
							console.log(quarterElement);
							
							$quarterElement.css({
								"background-color": result.gateInfoList[i][value.status] != 0 ? "rgba(0, 0, 255, 0.5)" : "rgba(0, 255, 0, 0.5)",
								"width": "50%",
								"height": "50%"
							});
							$element.append($quarterElement);
						})
					}
					
					$element.attr('select', false);
					$element.attr('deviceId', result.gateInfoList[i].deviceId);
					$element.attr('floor', result.gateInfoList[i].deviceFloor);
					
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
						"width" :  "20px",
						"height" : "20px",
						"border" : "2px solid #000000"
					}).offset({
						"left" : result.gateInfoList[i].xCoordinate * (900/750),
						"top" : result.gateInfoList[i].yCoordinate * (750/600)
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
			if(dragArea.children().length > 0){
				dragArea.children().each(function(i){
					list.push({
						"deviceId": $(this).attr('deviceId'),
						"xCoordinate": $(this).position().left/(900/750),
						"yCoordinate": $(this).position().top/(750/600)
					});
				});
				
				$.ajax({
					type:"GET",
					url:"<c:url value='/gateInfo/newGatePosition.do' />",
					dataType: "json",
					data: {
						"positionList": JSON.stringify(list)
					},
					traditional:true,
					success:function(result){
						alert('수정되었습니다.');
						floorSearch();
						
					}
				})	
			}
			else {
				alert('지역 이미지가 없어 위치를 수정할 수 없습니다.');
			}
		
	})
})

$(function(){
	$("#btn_gate_list_Position_reset").click(function() {
		var   floor = $("#search_item_floor").val();
		$.ajax({
			type:"GET",
			url:"<c:url value='/gateInfo/resetGatePosition.do' />",
			dataType: "json",
			data: {
				deviceFloor : floor
			},
			success:function(result){
				alert('초기화되었습니다.');
				floorSearch();
			}
		})
	})
})


</script>

<form id="frmSearch" name="frmSearch" method="post">

<input type="hidden" id="chkValueArray" name="chkValueArray" value=""/>
<input type="hidden" id="chkTextArray"  name="chkTextArray" value="" >
<input type="hidden" id="fdownresn" name="fdownresn" value=""/>
<input type="hidden" id="hidFloorIdx" name="hidFloorIdx" value=""/>

<div class="search_box mb_20">
	<div class="search_in">
		<div class="comm_search ml_10">
	    <div class="title">관리 지역</div>
			<select name="search_item_center" id="search_item_center" size="1" class="w_100px input_com mr_5" onchange="getAreaList(this.value)">
				<option value="0" selected disabled >센터</option>
				<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
					<option value='<c:out value="${cCombo.siteId}"/>'<c:if test="${searchItemCenter eq cCombo.siteId}">selected</c:if>>
						<c:out value="${cCombo.siteNm}" />
					</option>
				</c:forEach>
			</select>
    	</div>
    	
    	<div class="comm_search">
			<select name="search_item_floor" id="search_item_floor" size="1" class="w_100px input_com" onchange="floorSearch()">
				<option value="0" selected disabled >층수</option>
			</select>
    	</div>
    	<div class="comm_search ml_40">
			<div class="search_btn2" title="검색" onclick="floorSearch()"></div>
		</div>
	</div>
</div>
</form>

<div class="subheader" style="margin-bottom: 10px;">
	<div class="title-bar float_wrap wd100">
		<!-- /search-box -->
		<div class="com_box "> 
		<!--버튼 -->	  
		  <div class="totalbox">
			      <div class="r_btnbox  mb_10" > 
					 <button type="button" class="btn_middle color_color1">KeepOpen</button> 
					 <button type="button" class="btn_middle color_basic">Open</button> 
			  		 <button type="button" class="btn_middle color_gray">Close</button>
			  		 <button type="button" class="btn_middle color_basic" id="btn_gate_list_Position_update">저장</button>
			  		 <button type="button" class="btn_middle color_gray" id="btn_gate_list_Position_reset">초기화</button>
			</div>
		  </div>
		  <!--//버튼  --> 
	  
  </div>
	</div>
</div>
<div class="row">
	<div class="col-md-12">
		<div id="panel-1" class="panel">
			<div class="panel-container collapse show" style="width: 100%; height: 750px;">
				<div class="panel-content drag_area" id="drag_area" style="padding: 0;">
					<div class="drag_wrap" id="draggable"></div>
					<span class="img">
						<img alt="" width="900" height="750"	id="floorImage" src="#"/>
					</span>
				</div>
			</div>
		</div>
	</div>
</div>
</body>