<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
		<script>
			function pageSearch(page){
				if(typeof page == "undefined"){
					page = 1;
				}
				
				f = document.frmSearch;
				f.action = "/gateInfo/gateUserPopup.do?srchPage=" + page 
											+ "&userName=" + $("input[name=userName]").val()  
											+"&centerCode=" + ${centerCode} + "&gid=" + ${gateInfo.gid};
				f.submit();
			}
			
			function userSearch() {
				var searchUserName = $("input[name=userName]").val();
				
				if(typeof searchUserName == "undefined" || searchUserName.trim() =='' || searchUserName == null) {
					alert('출입자명을 입력하세요.');
					$("input[name=userName]").val('');
					return false;
				}
				else {
					this.pageSearch();
				}
			}
			
			function resetSearch(){
				$("input[name=userName]").val('');
				this.pageSearch();
			}
		</script>
		
		<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false">
		<div class="modal-header">
            <h5 class="modal-title">${gateInfo.name}</h5>
            <button type="button" class="close" onclick="window.close()">
                <span aria-hidden="true"><i class="fa fa-times"></i></span>
            </button>
        </div>
        <div class="modal-body">
        	<div class="title-bar float_wrap wd100">
				<div class="idchk_box date-search-box mt0">
					<div class="search_box d-flex">
                      		<input placeholder="출입자명을 입력해주세요" id="userName" name="userName" value='<c:out value="${gateInfo.unm}"/>' class="search-word form-control" type="search">
                      		<button class="bbs-search-btn" title="검색" onclick="userSearch();"><i class="fa fa-search"></i></button>
                    		<button class="btn btn-dark ml5" id="reset" onclick="resetSearch();">초기화</button>
            		</div>
				</div>
			</div>
            <div class="d-flex align-items-center">
                <div class="flex-fill">
               		<div class="float-left mb10 mt10">
						<b style="display:block;line-height:30px;">출입 가능 인원 : <c:out value="${pagination.totRecord}" />명</b>
					</div>
                    <table class="table m-0 table-bordered table-striped">
                        <colgroup>
                            <col style="width:15%">
                            <col style="width:20%">
                            <col style="width:22%">
                            <col style="width:23%">
                            <col style="width:20%">
                        </colgroup>
                        <thead>
                            <tr>
                                <th>UID</th>
                                <th>이름</th>
                                <th>파트1</th>
                                <th>파트2</th>
                                <th>파트3</th>
                            </tr>
                        </thead>
                        <tbody>
                        	 <c:forEach items="${userList}" var="userList" varStatus="status">
								<tr>
									<td><c:out value="${userList.fuid}"/></td>
									<td><c:out value="${userList.funm}"/></td> 
									<td><c:out value="${userList.fpartnm1}"/></td>
									<td><c:out value="${userList.fpartnm2}"/></td>
									<td><c:out value="${userList.fpartnm3}"/></td>
								</tr>
							</c:forEach>
                        </tbody>
                    </table>
                    <div class="frame-wrap" style="margin-top:20px">
              			<jsp:include page="/WEB-INF/jsp/cubox/common/pagination2.jsp" flush="false"/>
      				</div>
                </div>
            </div>
          </div>
          </form>