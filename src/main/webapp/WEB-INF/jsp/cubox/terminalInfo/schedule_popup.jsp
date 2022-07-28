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
	function fnScheduleSave () {
		if(fnFormValueCheck("fmScheduleInfo")) {
			var formData = $("#fmScheduleInfo").serialize ();
			$.ajax({
				url: "<c:url value='/terminalInfo/scheduleSave.do' />",
				data: formData,
				dataType:'json',
				traditional:true,
				type: "POST",
				success: function(response){
					if(response.saveCnt > 0){
						alert("저장되었습니다.");
						opener.location.reload();
						window.close();
					}else{
						alert("저장에 실패했습니다.");
						return;
					}
				},
				error: function (jqXHR){
					alert("저장에 실패했습니다.");
					return;
				}
			});
		}
	}
	function fnDuplCheck () {
		$.ajax({
			url: "<c:url value='/terminalInfo/duplScheduleNmCheck.do' />",
			data: {"fnm":$("#fnm").val()},
			dataType:'json',
			traditional:true,
			type: "POST",
			success: function(response){
				if(response.duplCnt > 0){
					alert("중복되는 이름입니다. 이름을 변경하여 주십시오.");
					$("#fnm").focus();
				}else{
					$("#fsidx").val("0");
					alert("사용가능한 이름입니다.");
				}
			},
			error: function (jqXHR){
				alert("저장에 실패했습니다.");
				return;
			}
		});
	}
	function fnTimeChng(event) {
		event = event || window.event;
		var keyID = (event.which) ? event.which : event.keyCode;
		if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) return;
		else {
			var tVal = event.target.value;
			var regx = /^(2[0-3]|[01][0-9]):([0-5][0-9])$/g;
			if(!regx.test(tVal)) {
				event.target.value = "";
				alert("12:00 형식으로 입력하여 주십시오.");
			}
		}
	}
</script>
		<div class="modal-header">
            <h5 class="modal-title">단말기 스케줄</h5>
            <button type="button" class="close" onclick="window.close()">
                <span aria-hidden="true"><i class="fa fa-times"></i></span>
            </button>
        </div>
        <form id="fmScheduleInfo" name="fmScheduleInfo" method="post">
	        <div class="modal-body">
	            <div class="idchk_box">
	                <p class="d-flex m-0">
	                	<input type="hidden" id="fsidx" name="fsidx" value="${gateShTypeList.fsidx}" check="text" checkName="중복체크확인"/>
	                	<c:choose>
	                		<c:when test="${gateShTypeList.fsidx != null && gateShTypeList.fsidx ne ''}">
	                			<input type="text" class="form-control3" id="fnm" name="fnm" value="${gateShTypeList.fnm}" readonly="readonly"/>
	                		</c:when>
	                		<c:otherwise>
	                			<input type="text" class="form-control3" id="fnm" name="fnm" check="text" checkName="스케줄명"/>
	                			<button type="button" class="btn btn-sm btn-secondary ml10 wd100p" onclick="fnDuplCheck()">중복체크</button>
	                		</c:otherwise>
	                	</c:choose>
	                </p>
	            </div>
	            <table class="table m-0 table-bordered table-striped">
	                <colgroup>
	                    <col style="width:15%">
	                    <col style="width:42.5%">
	                    <col style="width:42.5%">
	                </colgroup>
	                <thead>
	                    <tr>
	                        <th>Date</th>
	                        <th>Lock Schedule</th>
	                        <th>KeepOpen Schedule</th>
	                    </tr>
	                </thead>
	                <tbody>
	                    <tr>
	                    	<c:set var="fmo01" value="${fn:split(gateShTypeList.fmo,'|')[0]}" />
	                    	<c:set var="fmo02" value="${fn:split(gateShTypeList.fmo,'|')[3]}" />
	                    	<c:set var="fmo01_1" value="${fn:split(fmo01,':')[1]}" />
	                    	<c:set var="fmo01_2" value="${fn:split(fmo01,':')[2]}" />
	                    	<c:set var="fmo01_3" value="${fn:split(fmo01,':')[3]}" />
	                    	<c:set var="fmo01_4" value="${fn:split(fmo01,':')[4]}" />
	                    	<c:set var="fmo02_1" value="${fn:split(fmo02,':')[1]}" />
	                    	<c:set var="fmo02_2" value="${fn:split(fmo02,':')[2]}" />
	                    	<c:set var="fmo02_3" value="${fn:split(fmo02,':')[3]}" />
	                    	<c:set var="fmo02_4" value="${fn:split(fmo02,':')[4]}" />
	                        <th>월</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fmo1_1" name="fmo1_1" value="${fmo01_1}:${fmo01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fmo1_2" name="fmo1_2" value="${fmo01_3}:${fmo01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fmo2_1" name="fmo2_1" value="${fmo02_1}:${fmo02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fmo2_2" name="fmo2_2" value="${fmo02_3}:${fmo02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="ftu01" value="${fn:split(gateShTypeList.ftu,'|')[0]}" />
	                    	<c:set var="ftu02" value="${fn:split(gateShTypeList.ftu,'|')[3]}" />
	                    	<c:set var="ftu01_1" value="${fn:split(ftu01,':')[1]}" />
	                    	<c:set var="ftu01_2" value="${fn:split(ftu01,':')[2]}" />
	                    	<c:set var="ftu01_3" value="${fn:split(ftu01,':')[3]}" />
	                    	<c:set var="ftu01_4" value="${fn:split(ftu01,':')[4]}" />
	                    	<c:set var="ftu02_1" value="${fn:split(ftu02,':')[1]}" />
	                    	<c:set var="ftu02_2" value="${fn:split(ftu02,':')[2]}" />
	                    	<c:set var="ftu02_3" value="${fn:split(ftu02,':')[3]}" />
	                    	<c:set var="ftu02_4" value="${fn:split(ftu02,':')[4]}" />
	                        <th>화</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ftu1_1" name="ftu1_1" value="${ftu01_1}:${ftu01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ftu1_2" name="ftu1_2" value="${ftu01_3}:${ftu01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ftu2_1" name="ftu2_1" value="${ftu02_1}:${ftu02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ftu2_2" name="ftu2_2" value="${ftu02_3}:${ftu02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="fwe01" value="${fn:split(gateShTypeList.fwe,'|')[0]}" />
	                    	<c:set var="fwe02" value="${fn:split(gateShTypeList.fwe,'|')[3]}" />
	                    	<c:set var="fwe01_1" value="${fn:split(fwe01,':')[1]}" />
	                    	<c:set var="fwe01_2" value="${fn:split(fwe01,':')[2]}" />
	                    	<c:set var="fwe01_3" value="${fn:split(fwe01,':')[3]}" />
	                    	<c:set var="fwe01_4" value="${fn:split(fwe01,':')[4]}" />
	                    	<c:set var="fwe02_1" value="${fn:split(fwe02,':')[1]}" />
	                    	<c:set var="fwe02_2" value="${fn:split(fwe02,':')[2]}" />
	                    	<c:set var="fwe02_3" value="${fn:split(fwe02,':')[3]}" />
	                    	<c:set var="fwe02_4" value="${fn:split(fwe02,':')[4]}" />
	                        <th>수</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fwe1_1" name="fwe1_1" value="${fwe01_1}:${fwe01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fwe1_2" name="fwe1_2" value="${fwe01_3}:${fwe01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fwe2_1" name="fwe2_1" value="${fwe02_1}:${fwe02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fwe2_2" name="fwe2_2" value="${fwe02_3}:${fwe02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="fth01" value="${fn:split(gateShTypeList.fth,'|')[0]}" />
	                    	<c:set var="fth02" value="${fn:split(gateShTypeList.fth,'|')[3]}" />
	                    	<c:set var="fth01_1" value="${fn:split(fth01,':')[1]}" />
	                    	<c:set var="fth01_2" value="${fn:split(fth01,':')[2]}" />
	                    	<c:set var="fth01_3" value="${fn:split(fth01,':')[3]}" />
	                    	<c:set var="fth01_4" value="${fn:split(fth01,':')[4]}" />
	                    	<c:set var="fth02_1" value="${fn:split(fth02,':')[1]}" />
	                    	<c:set var="fth02_2" value="${fn:split(fth02,':')[2]}" />
	                    	<c:set var="fth02_3" value="${fn:split(fth02,':')[3]}" />
	                    	<c:set var="fth02_4" value="${fn:split(fth02,':')[4]}" />
	                        <th>목</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fth1_1" name="fth1_1" value="${fth01_1}:${fth01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fth1_2" name="fth1_2" value="${fth01_3}:${fth01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fth2_1" name="fth2_1" value="${fth02_1}:${fth02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fth2_2" name="fth2_2" value="${fth02_3}:${fth02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="ffr01" value="${fn:split(gateShTypeList.ffr,'|')[0]}" />
	                    	<c:set var="ffr02" value="${fn:split(gateShTypeList.ffr,'|')[3]}" />
	                    	<c:set var="ffr01_1" value="${fn:split(ffr01,':')[1]}" />
	                    	<c:set var="ffr01_2" value="${fn:split(ffr01,':')[2]}" />
	                    	<c:set var="ffr01_3" value="${fn:split(ffr01,':')[3]}" />
	                    	<c:set var="ffr01_4" value="${fn:split(ffr01,':')[4]}" />
	                    	<c:set var="ffr02_1" value="${fn:split(ffr02,':')[1]}" />
	                    	<c:set var="ffr02_2" value="${fn:split(ffr02,':')[2]}" />
	                    	<c:set var="ffr02_3" value="${fn:split(ffr02,':')[3]}" />
	                    	<c:set var="ffr02_4" value="${fn:split(ffr02,':')[4]}" />
	                        <th>금</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ffr1_1" name="ffr1_1" value="${ffr01_1}:${ffr01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ffr1_2" name="ffr1_2" value="${ffr01_3}:${ffr01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ffr2_1" name="ffr2_1" value="${ffr02_1}:${ffr02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ffr2_2" name="ffr2_2" value="${ffr02_3}:${ffr02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="fsa01" value="${fn:split(gateShTypeList.fsa,'|')[0]}" />
	                    	<c:set var="fsa02" value="${fn:split(gateShTypeList.fsa,'|')[3]}" />
	                    	<c:set var="fsa01_1" value="${fn:split(fsa01,':')[1]}" />
	                    	<c:set var="fsa01_2" value="${fn:split(fsa01,':')[2]}" />
	                    	<c:set var="fsa01_3" value="${fn:split(fsa01,':')[3]}" />
	                    	<c:set var="fsa01_4" value="${fn:split(fsa01,':')[4]}" />
	                    	<c:set var="fsa02_1" value="${fn:split(fsa02,':')[1]}" />
	                    	<c:set var="fsa02_2" value="${fn:split(fsa02,':')[2]}" />
	                    	<c:set var="fsa02_3" value="${fn:split(fsa02,':')[3]}" />
	                    	<c:set var="fsa02_4" value="${fn:split(fsa02,':')[4]}" />
	                        <th>토</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fsa1_1" name="fsa1_1" value="${fsa01_1}:${fsa01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fsa1_2" name="fsa1_2" value="${fsa01_3}:${fsa01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fsa2_1" name="fsa2_1" value="${fsa02_1}:${fsa02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fsa2_2" name="fsa2_2" value="${fsa02_3}:${fsa02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="fsu01" value="${fn:split(gateShTypeList.fsu,'|')[0]}" />
	                    	<c:set var="fsu02" value="${fn:split(gateShTypeList.fsu,'|')[3]}" />
	                    	<c:set var="fsu01_1" value="${fn:split(fsu01,':')[1]}" />
	                    	<c:set var="fsu01_2" value="${fn:split(fsu01,':')[2]}" />
	                    	<c:set var="fsu01_3" value="${fn:split(fsu01,':')[3]}" />
	                    	<c:set var="fsu01_4" value="${fn:split(fsu01,':')[4]}" />
	                    	<c:set var="fsu02_1" value="${fn:split(fsu02,':')[1]}" />
	                    	<c:set var="fsu02_2" value="${fn:split(fsu02,':')[2]}" />
	                    	<c:set var="fsu02_3" value="${fn:split(fsu02,':')[3]}" />
	                    	<c:set var="fsu02_4" value="${fn:split(fsu02,':')[4]}" />
	                        <th>일</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fsu1_1" name="fsu1_1" value="${fsu01_1}:${fsu01_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fsu1_2" name="fsu1_2" value="${fsu01_3}:${fsu01_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="fsu2_1" name="fsu2_1" value="${fsu02_1}:${fsu02_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="fsu2_2" name="fsu2_2" value="${fsu02_3}:${fsu02_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="ff101" value="${fn:split(gateShTypeList.ff1,'|')[0]}" />
	                    	<c:set var="ff102" value="${fn:split(gateShTypeList.ff1,'|')[3]}" />
	                    	<c:set var="ff101_1" value="${fn:split(ff101,':')[1]}" />
	                    	<c:set var="ff101_2" value="${fn:split(ff101,':')[2]}" />
	                    	<c:set var="ff101_3" value="${fn:split(ff101,':')[3]}" />
	                    	<c:set var="ff101_4" value="${fn:split(ff101,':')[4]}" />
	                    	<c:set var="ff102_1" value="${fn:split(ff102,':')[1]}" />
	                    	<c:set var="ff102_2" value="${fn:split(ff102,':')[2]}" />
	                    	<c:set var="ff102_3" value="${fn:split(ff102,':')[3]}" />
	                    	<c:set var="ff102_4" value="${fn:split(ff102,':')[4]}" />
	                        <th>공휴일1</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ff11_1" name="ff11_1" value="${ff101_1}:${ff101_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ff11_2" name="ff11_2" value="${ff101_3}:${ff101_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ff12_1" name="ff12_1" value="${ff102_1}:${ff102_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ff12_2" name="ff12_2" value="${ff102_3}:${ff102_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="ff201" value="${fn:split(gateShTypeList.ff2,'|')[0]}" />
	                    	<c:set var="ff202" value="${fn:split(gateShTypeList.ff2,'|')[3]}" />
	                    	<c:set var="ff201_1" value="${fn:split(ff201,':')[1]}" />
	                    	<c:set var="ff201_2" value="${fn:split(ff201,':')[2]}" />
	                    	<c:set var="ff201_3" value="${fn:split(ff201,':')[3]}" />
	                    	<c:set var="ff201_4" value="${fn:split(ff201,':')[4]}" />
	                    	<c:set var="ff202_1" value="${fn:split(ff202,':')[1]}" />
	                    	<c:set var="ff202_2" value="${fn:split(ff202,':')[2]}" />
	                    	<c:set var="ff202_3" value="${fn:split(ff202,':')[3]}" />
	                    	<c:set var="ff202_4" value="${fn:split(ff202,':')[4]}" />
	                        <th>공휴일2</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ff21_1" name="ff21_1" value="${ff201_1}:${ff201_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ff21_2" name="ff21_2" value="${ff201_3}:${ff201_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ff22_1" name="ff22_1" value="${ff202_1}:${ff202_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ff22_2" name="ff22_2" value="${ff202_3}:${ff202_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>
	                    <tr>
	                    	<c:set var="ff301" value="${fn:split(gateShTypeList.ff3,'|')[0]}" />
	                    	<c:set var="ff302" value="${fn:split(gateShTypeList.ff3,'|')[3]}" />
	                    	<c:set var="ff301_1" value="${fn:split(ff301,':')[1]}" />
	                    	<c:set var="ff301_2" value="${fn:split(ff301,':')[2]}" />
	                    	<c:set var="ff301_3" value="${fn:split(ff301,':')[3]}" />
	                    	<c:set var="ff301_4" value="${fn:split(ff301,':')[4]}" />
	                    	<c:set var="ff302_1" value="${fn:split(ff302,':')[1]}" />
	                    	<c:set var="ff302_2" value="${fn:split(ff302,':')[2]}" />
	                    	<c:set var="ff302_3" value="${fn:split(ff302,':')[3]}" />
	                    	<c:set var="ff302_4" value="${fn:split(ff302,':')[4]}" />
	                        <th>공휴일3</th>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ff31_1" name="ff31_1" value="${ff301_1}:${ff301_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ff31_2" name="ff31_2" value="${ff301_3}:${ff301_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                        <td>
	                            <p class="d-flex justify-content-between m-0">
	                                <input type="text" class="form-control3 wd49" id="ff32_1" name="ff32_1" value="${ff302_1}:${ff302_2}" onchange="fnTimeChng(event)" maxlength="5"/>
	                                <input type="text" class="form-control3 wd49" id="ff32_2" name="ff32_2" value="${ff302_3}:${ff302_4}" onchange="fnTimeChng(event)" maxlength="5"/>
	                            </p>
	                        </td>
	                    </tr>

	                </tbody>
	            </table>
	        </div>
        </form>
        <div class="modal-footer pt10">
            <button type="button" class="btn btn-sm btn-primary" onclick="fnScheduleSave()">저장</button>
            <button type="button" class="btn btn-sm btn-default" onclick="window.close()">취소</button>
        </div>
