<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">
$(function() {
	$(".title_tx").html("출입이력 보관설정");
	
	$(".onlyNumber").keyup(function(event){
		if (!(event.keyCode >=37 && event.keyCode<=40)) { //방향키
			var inputVal = $(this).val();
			$(this).val(inputVal.replace(/[^0-9]/gi,''));
		}
	});
	
	$("#btnNew").click(function(){
		$("#keep_seq").val("");
		$("#startDate").val("");
		$("#endDate").val("");
		$("#keep_unit").val("");
		$("#keep_num").val("");
		$("#use_yn").val("Y");
	});
});

function fnEdit(seq, sdt, edt, unit, num, useyn) {
	$("#keep_seq").val(seq);
	$("#startDate").val(sdt);
	$("#endDate").val(edt);
	$("#keep_unit").val(unit);
	$("#keep_num").val(num);
	$("#use_yn").val(useyn);
}

function fnSave() {
	if(!fnFormValueCheck("frmDetail")) return;
	
	if(!confirm("저장하시겠습니까?")) return;
	
	var formData = $("form[id=frmDetail]").serialize();
	$.ajax({
		url: "<c:url value='/basicInfo/saveLogSetting.do'/>",
		type: "POST",
		data: formData,
		dataType: "json",
		success: function(returnData){
			if(returnData.result == "success") {
				alert("저장에 성공했습니다.");
				location.reload();
			} else {
				if(!fnIsEmpty(returnData.message)) {
					alert(returnData.message);	
				} else {
					alert("저장 중 오류가 발생했습니다. 다시 실행하십시오.");
				}
			}
		},
		error: function(jqXHR){
			alert("저장에 실패했습니다.");
			return;
		}
	});
}

</script>

<form name="frmDetail" id="frmDetail" method="post">
<div class="box_w2">
	<!--------- left--------->
	<div class="box_w2_1h">
		<!--------- 목록--------->
		<div class="com_box ">
			<!--버튼 -->
			<div class="r_btnbox  mb_10">
				<button type="button" class="btn_middle color_basic" id="btnNew">신규</button>
			</div>
			<!--//버튼  -->
			<!--테이블 시작 -->
			<div class="tb_outbox ">
				<table class="tb_list">
					<colgroup>
						<col width="20%">
						<col width="20%">
						<col width="17%">
						<col width="17%">
						<col width="13%">
						<col width="13%">
					</colgroup>
					<thead>
						<tr>
							<th>기준시작일자</th>
							<th>기준종료일자</th>
							<th>날짜수</th>
							<th>날짜단위</th>							
							<th>사용여부</th>
							<th>편집</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${setList == null || fn:length(setList) == 0}">
						<tr>
							<td class="h_35px" colspan="6">조회 목록이 없습니다.</td>
						</tr>
						</c:if>
						<c:forEach items="${setList}" var="list" varStatus="status">
						<tr>
							<td><c:out value="${list.keep_sdt}"/></td>
							<td><c:out value="${list.keep_edt}"/></td>
							<td><c:out value="${list.keep_num}"/></td>							
							<td><c:if test="${list.keep_unit eq 'year'}">년</c:if>
								<c:if test="${list.keep_unit eq 'month'}">월</c:if>
								<c:if test="${list.keep_unit eq 'day'}">일</c:if></td>
							<td><c:if test="${list.use_yn eq 'Y'}">사용중</c:if>
								<c:if test="${list.use_yn eq 'N'}">사용안함</c:if></td>
							<td><button type="button" class="btn_small color_basic" data-toggle="modal" onclick="fnEdit('<c:out value="${list.keep_seq}"/>', '<c:out value="${list.keep_sdt}"/>', '<c:out value="${list.keep_edt}"/>', '<c:out value="${list.keep_unit}"/>', '<c:out value="${list.keep_num}"/>', '<c:out value="${list.use_yn}"/>');">편집</button></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		<!--------- //right --------->
		</div>
		<!--------- //목록--------->	
	</div>
	<!--------- //left --------->
	<!--------- right --------->
	<div class="box_w2_2h">
		<div class="com_box">
			<!--버튼 -->
			<div class="r_btnbox  mb_10">
				<button type="button" class="btn_middle color_basic" onclick="fnSave()">저장</button>
			</div>		
			<!--테이블 시작 -->
			<div class="tb_outbox">
				<table class="tb_write_02 tb_write_p1">
					<colgroup>
						<col width="50%">
						<col width="50%">
					</colgroup>				
					<tbody>
						<tr>
							<th>기준시작일자 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" name="startDate" id="startDate" class="input_datepicker w_100p" readonly check="text" checkName="기준시작일자"/>
								<input type="hidden" name="keep_seq" id="keep_seq"/>
							</td>
						</tr>
						<tr>
							<th>기준종료일자 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" name="endDate" id="endDate" class="input_datepicker w_100p" readonly check="text" checkName="기준종료일자"/>
							</td>
						</tr>
						<tr>
							<th>날짜수 <span class="font-color_H">*</span></th>
							<td>
								<input type="text" name="keep_num" id="keep_num" class="input_com w_100p onlyNumber" maxlength="3" check="text" checkName="날짜수"/>
							</td>
						</tr>
						<tr>
							<th>날짜단위 <span class="font-color_H">*</span></th>
							<td>
								<select name="keep_unit" id="keep_unit" class="input_com w_100p" check="text" checkName="날짜단위">
									<option value="" disabled selected>선택</option>
									<option value="year">년</option>
									<option value="month">월</option>
									<option value="day">일</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>사용여부 <span class="font-color_H">*</span></th>
							<td>
								<select name="use_yn" id="use_yn" class="input_com w_100p" >
									<option value="Y">사용</option>
									<option value="N">사용안함</option>
								</select>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>		
	</div>
	<!--------- //light --------->
</div>
</form>
