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
		}); 	
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