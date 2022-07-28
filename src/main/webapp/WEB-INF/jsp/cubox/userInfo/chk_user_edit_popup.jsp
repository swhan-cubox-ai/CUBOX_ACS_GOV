<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<script type="text/javascript" src="<c:url value='/js/faceComm.js?ver=<%=Math.random() %>'/>"></script>
		
		<script type="text/javascript">
		$(function() {  				
			<c:if test="${!empty sfcdno}">
				getAuthGroup('${sfcdno}');
			</c:if>	
			
			$("#srchStartDate2").change(function(){ 
				var srchStartDt = $("#srchStartDate2").val().replace('-','').replace('-','');
				var srchExpireDt = $("#srchExpireDate2").val().replace('-','').replace('-','');
				if(srchStartDt > srchExpireDt && srchExpireDt != ""){
					alert("만료일보다 뒷 날짜를 선택할 수 없습니다.");
					$("#srchStartDate2").val('');
					$("#srchStartDate2").focus(); 
				}
			}); 
			
			$("#srchExpireDate2").change(function(){
				var srchStartDt = $("#srchStartDate2").val().replace('-','').replace('-','');
				var srchExpireDt = $("#srchExpireDate2").val().replace('-','').replace('-','');
				if(srchStartDt > srchExpireDt && srchExpireDt != ""){
					alert("시작일보다 앞 날짜를 선택할 수 없습니다.");
					$("#srchExpireDate2").val('');
					$("#srchExpireDate2").focus();
				}
			});
		}); 
		
		function getAuthGroup(fcdno) {			
			//카드정보가져오기
			$.ajax({ 
				type:"POST",  
				url:"<c:url value='/userInfo/getCardInfo.do' />", 
				data:{ 
					"fcdno": fcdno,
					"fuid" : '<c:out value="${userInfo.fuid}"/>',
					"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>'
				}, 
				dataType:'json', 
				//timeout:(1000*30), 
				success:function(returnData, status){
					if(status == "success") {
						if(returnData.cardInfoVO != null && returnData.cardInfoVO.length != 0){
							$("#datetimepicker3").datepicker('setDate',returnData.cardInfoVO.fsdt);
							$("#datetimepicker4").datepicker('setDate',returnData.cardInfoVO.fedt);						
							$("#cfcdnum").val(returnData.cardInfoVO.fcdnum);
							$("#cfstatus").val(returnData.cardInfoVO.fstatus);
						}							
					}else{ alert("ERROR!");return;}
				}
			});	 
		} 

		function userListInfoSave (pty){
			var msg = "출입자 정보를 일괄 수정하시겠습니까?";
			if(!fnIsEmpty(pty) && pty == "A") {				
				msg = "체크 된 사용자와 관련 없이 모든 사용자의 정보가 변경됩니다.\n\n계속 진행 하시겠습니까?";
			}			
			
			var fauthtype = $("#fauthtype").val();				/*권한타입*/
			var srchStartDate2 = $("#srchStartDate2").val();	/*시작일*/
			var srchExpireDate2 = $("#srchExpireDate2").val();	/*만료일*/
			var cfstatus = $("#cfstatus").val();				/*카드상태*/

			if(srchStartDate2 == "" || srchExpireDate2 == ""){ 
				alert("시작일, 종료일을 입력하세요.");
				return;
			}

			//권한타입, 카드상태, 시작일, 종료일 변경사항 체크?
			if(confirm(msg)){ 
				$.ajax({
					type:"POST",
					url:"/userInfo/chkUserInfoSave.do",
					data:{
						"fuList" : '${fuLst}',
						"fauthtype": fauthtype,
						"srchStartDate2": srchStartDate2,
						"srchExpireDate2": srchExpireDate2,
						"cfstatus": cfstatus,
						"fsvtype": pty
					},
					traditional:true,
					dataType:'json',  
					//timeout:(1000*30),
					success:function(returnData, status){
						if(status == "success") {
							if(!fnIsEmpty(returnData.uCnt) && parseInt(returnData.uCnt) > 0) alert("출입자정보를 수정하였습니다.");
							else alert("저장 중 오류가 발생되었습니다. 다시 시도하여 주십시오.");
							opener.location.reload();
							window.close();
						}else{ alert("ERROR!"); alert("오류가 발생되었습니다. 다시 시도하여 주십시오."); return;}
					}
				});
			}	 
		}	
		</script>
		
		<div class="modal-header">
            <h5 class="modal-title">출입자 및 카드정보 수정</h5>
            <button type="button" class="close" onclick="window.close()">
                <span aria-hidden="true"><i class="fa fa-times"></i></span>
            </button>
        </div>  
        <div class="modal-body">
            <ul class="nav nav-tabs" role="tablist"> 
                <li class="nav-item">
                    <a class="nav-link active" data-toggle="tab" href="#tab_borders_icons-1" role="tab"><i class="fa fa-user mr-1"></i> 출입자 정보</a>
                </li>
            </ul>     
            <div class="tab-content border border-top-0 p-3">
                <div class="tab-pane fade show active" id="tab_borders_icons-1" role="tabpanel">                         
                    <div class="d-flex align-items-stretch">    
                        <div class="flex-fill ml10">
                        	<div class="float-left mb10">
                        	<!-- 
                           		<button type="button" class="btn btn-sm btn-danger float-left"  onclick="userListInfoSave('A')">전체일괄수정</button>
	                           	<div class="datapicker_list float-left ml5" style="line-height: 35px;">↢ 체크된 사용자와 관련 없이 모든 사용자를 수정합니다.</div>
	                           	 -->
                            </div> 
                            <div class="float-right mb10">
                                <button type="button" class="btn btn-sm btn-dark" onclick="userListInfoSave('S');">출입자정보수정</button>
                            </div>
                            <table class="table m-0 table-view">
                                <colgroup>
                                    <col style="width:30%">
                                    <col style="width:70%;">
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <th>카드상태</th> 
                                        <td>
                                            <select name="cfstatus" id="cfstatus" class="form-control wd60">
												<c:forEach items="${cardStatus}" var="cStatus" varStatus="status"> 
												<option value='<c:out value="${cStatus.fvalue}"/>' <c:if test="${cStatus.fvalue eq 'Y'}">selected</c:if>><c:out value="${cStatus.fkind3}"/></option>
												</c:forEach>
											</select>
                                        </td>
                                    </tr>
									<tr>									    
									    <th>시작일</th>									    
									    <td>
									    	<div class='input-group date wd60' id='datetimepicker3'>
										    	<input type='text' id="srchStartDate2" name="srchStartDate2" class="search-word2 form-control2" />
		                                        <span class="input-group-addon">
		                                            <span class="glyphicon glyphicon-calendar"></span>
		                                        </span>
	                                        </div>
	                                    </td>
	                                </tr>
	                                <tr>  
	                                    <th>만료일</th>									    
									    <td>
									    	<div class='input-group date wd60' id='datetimepicker4'>
										    	<input type='text' id="srchExpireDate2" name="srchExpireDate2" class="search-word2 form-control2" />
		                                        <span class="input-group-addon">
		                                            <span class="glyphicon glyphicon-calendar"></span>
		                                        </span> 
	                                        </div>
	                                    </td>
									</tr> 
                                </tbody> 
                            </table>
                        </div>
                    </div> 
                </div>                
            </div>
        </div>
        <script type="text/javascript">		    
			$(window).load(function(){
				$(".nav-link").eq(0).addClass('active');
				$(".nav-link").eq(0).attr('aria-selected','true'); 
				$(".nav-link").eq(1).removeClass('active');
				$(".nav-link").eq(1).attr('aria-selected','false');
				setTimeout(function(){
					$("#tab_borders_icons-1").addClass('active show');
					$("#tab_borders_icons-2").removeClass('active show');
				},300);
			});
		</script>        