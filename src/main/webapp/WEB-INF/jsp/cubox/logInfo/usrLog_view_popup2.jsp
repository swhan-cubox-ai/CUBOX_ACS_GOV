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

	//form submit
	function pageSearch(page) {
		
		f = document.frmSearch;
		
		f.action = "/logInfo/usrLogViewPopup2.do?srchPage=" + page;
		f.submit();
	
	}
	
	function addClickImg(fuid, fevttm){
		var srcImg = "/logInfo/getByteRealImage.do?fuid=" + fuid + "&fevttm=" + fevttm;
		$("#realimg").attr('src', srcImg);
	}
	
	//검색
	function usrLogSearch(){
		
		var srchStartDate = $("input[name=srchStartDate]").val();
		var srchExpireDate = $("input[name=srchExpireDate]").val();
		
		f = document.frmSearch;
		
		if(srchStartDate > srchExpireDate){
			alert('시작일이 만료일보다 큽니다. 다시 입력해 주세요');
			$("input[name=srchStartDate]").focus();
			return false;
		}
		
		f.action = "/logInfo/usrLogViewPopup2.do"
		f.submit();
	}
	
	//엑셀다운로드
	function excelDownLoad(){
		
		f = document.frmSearch;
		
		var rowDataArr = "";
		var rowTextArr = "";
		
		var checkbox = $("input[name=colChk]:checked");
		
		checkbox.each(function(i){			
			var chkValue = $(this).val();
			var chkText = $(this).parent().text();
			
			if(i==0){
				rowDataArr = rowDataArr + chkValue + " as CELL" + (i+1);
				rowTextArr = rowTextArr + chkText;
			}else{
				rowDataArr = rowDataArr + ", " + chkValue + " as CELL" + (i+1);
				rowTextArr = rowTextArr + ", " + chkText;
			}		
		});
		
		if(checkbox.length == 0){
			alert("한개이상 체크를 하셔야 다운로드 가능합니다.");
			return;
		}else{
			$("#download").attr("data-dismiss", "modal");
			$("#download").attr("aria-label", "Close");
		}
		
		$("#rowDataArr").val(rowDataArr);
		$("#rowTextArr").val(rowTextArr);
		
		f.action = "/logInfo/usrPopExcelDownload.do"
		f.submit();
		
	} 

	$(function(){
		
		//초기화
		$("#reset").click(function(){
			$("#srchCondWord").val('');
			$("#srchTVCardNum").val('');
			$("input[name=srchSuccess]:checked").prop("checked", false);
			$("input[name=srchFail]:checked").prop("checked", false);
			
			//날짜, 시간
			$.ajax({
				type:"POST", 
				url:"<c:url value='/logInfo/getTime.do'/>",
				data:{
					
				},
				dataType:'json',
				success:function(returnData, status){
						if(status == "success") {
							if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
								$("#fromTime").val(returnData.fromTime);
								$("#toTime").val(returnData.toTime);
								$("#srchStartDate").val(returnData.ytDt);
								$("#srchExpireDate").val(returnData.tdDt);
							}
							
						}else{ 
							alert("ERROR!");
							return;
					}
				}
			});
		});
		
		//새로고침
		$("#refreshPage").click(function(){
			//날짜, 시간
			$.ajax({
				type:"POST", 
				url:"<c:url value='/logInfo/usrPopReload2.do'/>",
				dataType:'json',
				success:function(returnData, status){
						if(status == "success") {
							if(returnData.fromTime != null && returnData.fromTime.length != 0 && returnData.toTime != null && returnData.toTime.length != 0 && returnData.ytDt != null && returnData.ytDt.length != 0 && returnData.tdDt != null && returnData.tdDt.length != 0){
								$("#srchStartDate").val(returnData.ytDt);
								$("#srchExpireDate").val(returnData.tdDt);
								$("#fromTime").val(returnData.fromTime);
								$("#toTime").val(returnData.toTime);
							}
							usrLogSearch();
						}else{ 
							alert("ERROR!");
							return;
					}
				}
			});
		});
		
		//모달창 닫기
		$("#cancle").click(function(){
			$("#center-add-modal").modal('hide');
		});
		
		//체크박스 전체
		$("#checkAll").click(function(){
			if($("#checkAll").prop("checked")){
				$("input[name=colChk]").prop("checked", true);
			}else{
				$("input[name=colChk]").prop("checked", false);
			}
		});
		
	});
	
</script>
<form id="frmSearch"  name="frmSearch" method="post" onsubmit="return false;" >
<input type="hidden" name="fuid" value="${logInfoVO.fuid}"/>
<input type="hidden" id="rowDataArr" name="rowDataArr" value="" />
<input type="hidden" id="rowTextArr" name="rowTextArr" value="" />
 	<div class="modal-header">
 	<h5 class="modal-title"><c:out value="${title}"/></h5>
	    <button type="button" class="close" onclick="window.close()">
	        <span aria-hidden="true"><i class="fa fa-times"></i></span>
	    </button>
	</div>
	<div class="modal-body">
		<div class="subheader">
       		<!-- search-box -->
			<div class="title-bar float_wrap wd100">
				<div class="date-search-box panel" style="margin-top:0px !important;">
					<div class="idchk_box date-search-box mt10">
		                <div class="d-flex ml10">
		                    <div class="search_box d-flex">
		                        <input placeholder="검색어를 입력해주세요" id="srchCondWord" name="srchCondWord" class="search-word2 form-control" type="search" value="${logInfoVO.srchCondWord}"/>
				            </div>
		                    <div class="datapicker_list">
		                        <div class="form-group">
		                            <div class='input-group date' id='datetimepicker6'>
		                                <label for="">날짜</label>
		                                <input type='text' id="srchStartDate" name="srchStartDate" class="search-word2 form-control wd100p" value="${logInfoVO.srchStartDate}" />
		                                <span class="input-group-addon">
		                                    <span class="glyphicon glyphicon-calendar"></span>
		                                </span>
		                            </div>
		                        </div>
		                        <div class="form-group mo-ml10">
	                         		<div class='input-group time'>
		                    			<label for="">시/분</label>
	                       				<input type='time' id="fromTime" name="fromTime" class="form-control" value="${logInfoVO.fromTime}" />
	                       			</div>
	                			</div>
		                        <div class="form-group mo-ml10">
		                            <div class='input-group date' id='datetimepicker7'>
		                                <label for="">~</label>
		                                <input type='text' id="srchExpireDate" name="srchExpireDate" class="search-word2 form-control wd100p" value="${logInfoVO.srchExpireDate}"/>
		                                <span class="input-group-addon">
		                                    <span class="glyphicon glyphicon-calendar"></span>
		                                </span>
		                            </div>
		                        </div>
		                        <div class="form-group mo-ml10">
	                         		<div class='input-group time'>
		                    			<label for="">시/분</label>
	                       				<input type='time' id="toTime" name="toTime" class="form-control" value="${logInfoVO.toTime}" />
	                       			</div>
	                			</div>
		               		</div>
		               		<div class="search_box d-flex">
		                        <div class='input-group d-flex align-items-center ml10 mr10'>
	                        		<label for="" class="mr10">결과</label>
	                            	<label for="srchSuccess" class="chk-label"><input type="checkbox" id="srchSuccess" name="srchSuccess" value="Y" <c:if test="${logInfoVO.srchSuccess eq 'Y'}">checked</c:if>>성공</label>
	                            	<label for="srchFail" class="chk-label"><input type="checkbox" id="srchFail" name="srchFail" value="Y" <c:if test="${logInfoVO.srchFail eq 'Y'}">checked</c:if>>실패</label>
	                       		</div>
	                    	</div>
		               		<button class="bbs-search-btn" title="검색" onclick="usrLogSearch()"><i class="fa fa-search"></i></button>
	                        <button type="button" class="btn btn-dark ml5" id="reset">초기화</button>
				        </div>
					</div>
				</div>
			</div>
		</div>
	<div class="row">
		<div class="col-md-12">
			<div id="panel-1" class="panel">
				<div class="panel-container collapse show" style="">
					<div class="panel-content">
			            <div class="d-flex align-items-top"> 
			                <div class="flex-fill d-flex align-items-stretch mt42" style="min-width:380px;">
			                    <div class="face-wrap">
									<div class="source-pic">
										<span class="img">
			                            	<img src='/logInfo/getByteImage.do?fuid=<c:out value="${userInfo.fuid}"/>' alt="" onerror="this.src='/images/no-img.jpg'"  style="width:160px; height:160px; object-fit:cover;">
			                            </span>
			                            <span class="text">source</span>
			                        </div>
			                        <div class="real-pic mt5" id="realpic">
										<span class="img">
											<img src="" id="realimg" alt="" onerror="this.src='/images/no-img.jpg'"  style="width:160px; object-fit:cover;"> 
										</span>
										<span class="text">real</span>
			                        </div>
			                    </div>
			                    <div class="flex-fill wd100p">
									<table class="table m-0 table-view lh22" style="min-height:390px;">
			                            <tbody>
			                                <tr>
			                                    <th>이름</th>
			                                    <td><c:out value="${userInfo.funm}"/></td>
			                                </tr>
			                               	<tr>
			                                    <th>권한타입</th>
			                                    <td><c:out value="${userInfo.fkind3}"/></td>
			                              	</tr> 
			                                <tr>
			                                    <th>차번호</th>
			                                    <td><c:out value="${userInfo.fpartcd3}"/></td>
			                                </tr>
			                                <tr>
			                                    <th>차번호2</th>
			                                    <td><c:out value="${userInfo.fcarno}"/></td>
			                                </tr>
			                                <tr>
			                                    <th>사업명</th>
			                                    <td><c:out value="${userInfo.fpartnm1}"/></td>
			                                </tr>
			                                <tr>
			                                    <th>회사명</th>
			                                    <td><c:out value="${userInfo.fpartnm2}"/></td>
			                                </tr>
			                                <tr>
			                                    <th>부서</th>
			                                    <td><c:out value="${userInfo.fpartnm3}"/></td>
			                                </tr>
			                                <tr>
			                                    <th>전화번호</th>
			                                    <td><c:out value="${userInfo.fpartcd2}"/></td>
			                                </tr>
			                                 <tr>
			                                    <th>전화번호2</th>
			                                    <td><c:out value="${userInfo.ftel}"/></td>
			                                </tr>
			                                <tr>
			                                    <th>센터</th>
			                                    <td><c:out value="${userInfo.fvalue}"/></td>
			                                </tr>
			                            </tbody>
			                        </table>
			                    </div>
			                </div>
			                <div class="flex-fill ml20">
			                		<div class="float-left mb10">
										<b style="display:block;line-height:30px;">전체 : <c:out value="${pagination.totRecord}" />건</b>
									</div>
			                		<div class="float-right mb10">
                           				<button type="button" class="btn btn-sm btn-warning" data-toggle="modal" data-target="#center-add-modal"><span class="fa fa-download mr-1"></span>엑셀 다운로드</button>
                            			<button type="button" class="btn btn-sm btn-primary" id="refreshPage">새로고침</button>
                        			</div>
				                    <table class="table m-0 table-bordered table-striped">
				                        <colgroup>
				                            <col style="width:27%">
				                            <col style="width:19%">
				                            <col style="width:22%">
				                            <col style="width:16%">
				                            <col style="width:16%">
				                        </colgroup>
				                        <thead>
				                            <tr>
				                                <th>시간</th>
				                                <th>이름</th>
				                                <th>단말기명</th>
				                                <th>카드번호</th>
				                                <th>결과</th>
				                                <th>권한타입</th>
				                            </tr>
				                        </thead>
				                    </table>
			                    	<div class="tbody-scroll ht355">
				                        <table class="table m-0 table-bordered table-striped">
				                          	<colgroup>
				                                <col style="width:27%">
				                                <col style="width:19%">
				                                <col style="width:22%">
				                                <col style="width:16%">
				                                <col style="width:16%">
				                          	</colgroup>
				                            <tbody>
					                            <c:forEach items="${userlogInfo}" var="userlog" varStatus="status">
					                                <tr>
					                                    <td><c:set var="fevttm" value="${fn:split(userlog.fevttm,'.')[0]}" /><c:out value="${fevttm}"/></td>
					                                    <td><c:out value="${userlog.funm}"/></td>
					                                    <td class="title"><a href="#none" onclick="addClickImg('<c:out value="${userlog.fuid}"/>','<c:out value="${userlog.fevttm}"/>')"><c:out value="${userlog.flname}"/></a></td> 
					                                    <td><c:out value="${userlog.fcdno}"/></td>
					                                    <td><c:out value="${userlog.fvalue1}"/></td>
														<td><c:out value="${userlog.fvalue2}"/></td>
					                                </tr>
					                            </c:forEach>
				                            </tbody>
				                        </table>
			                    	</div>
			                	</div>
			            	</div>
			        	</div>
			    	</div>
		    	</div>
			</div>
		</div>
	</div>
	<div class="frame-wrap">
		<jsp:include page="/WEB-INF/jsp/cubox/common/pagination2.jsp" flush="false" />
	</div>
	<!-- modal 추가 팝업 -->
    <div class="modal fade" id="center-add-modal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">엑셀다운로드</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"><i class="fa fa-times"></i></span>
                    </button>
                </div>
                <div class="modal-body">
                	<ul>
						<li><input type="checkbox" id="checkAll">전체</li>
					</ul>
                    <div class="check-list">
                        <ul>
                            <li><input type="checkbox" name="colChk" value="fevttm">시간</li>
                            <li><input type="checkbox" name="colChk" value="funm">이름</li>
                            <li><input type="checkbox" name="colChk" value="flname">단말기명</li>
                            <li><input type="checkbox" name="colChk" value="fcdno">카드번호</li>
                            <li><input type="checkbox" name="colChk" value="fvalue1">결과</li>
                            <li><input type="checkbox" name="colChk" value="fvalue2">권한타입</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" id="download" class="btn btn-sm btn-primary" onclick="excelDownLoad();">다운로드</button>
                    <button type="button" id="cancle" class="btn btn-sm btn-default">취소</button>
                </div>
            </div>
        </div>
    </div>
</form>