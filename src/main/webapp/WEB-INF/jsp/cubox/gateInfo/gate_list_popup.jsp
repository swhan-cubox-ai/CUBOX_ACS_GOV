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
		function onGateUserList() {
			var gateList = [];
			//window.close();
			$("input[name=checkFtid]:checked").each(function(i){ 
				gateList.push($(this).val());
			});
			
			$.ajax({
				type:"GET",
				url:"<c:url value='/gateInfo/gateUserList.do' />",
				data:{
					"gateList": gateList
				},
				dataType: "json",
				traditional:true,
				success:function(result){
					
				}
			}); 
		}
		
		$(function(){
			$("#checkAll").click(function(){
				if($("#checkAll").prop("checked")){
					$("input[name=checkFtid]").prop("checked", true);
				}else{
					$("input[name=checkFtid]").prop("checked", false);
				} 
			});
		});
		</script>
		<div class="modal-header">
            <h5 class="modal-title">단말기 리스트</h5>
            <button type="button" class="close" onclick="window.close()">
                <span aria-hidden="true"><i class="fa fa-times"></i></span>
            </button>
        </div>
        <div class="modal-body">
            <div class="d-flex align-items-center">
                <div class="p-2 flex-fill">
                    <div class="tbody-scroll ht400">
                       <table class="table m-0 table-bordered table-striped" id="gate_list_table">
	                       <colgroup>
	                           <col style="width:10%;">
	                           <col style="width:55%;">
	                       </colgroup>
                           <thead> 
								<tr>
	                              <th><input type="checkbox" id="checkAll"> 전체</th>
	                              <th>단말기명</th>
	                          </tr>
							</thead>
							<tbody>
								<c:forEach items="${gateList}" var="gateList" varStatus="status">
								<tr>
									<td><input type="checkbox" name="checkFtid" value='<c:out value="${gateList.gid}"/>'></td>
									<td><c:out value="${gateList.name}"/></td> 
								</tr>
								</c:forEach>
							</tbody>
                        </table>
                    </div>
                </div>                
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-sm btn-primary" onclick="onGateUserList()">조회</button>
            <button type="button" class="btn btn-sm btn-default" onclick="window.close()">취소</button>
        </div>