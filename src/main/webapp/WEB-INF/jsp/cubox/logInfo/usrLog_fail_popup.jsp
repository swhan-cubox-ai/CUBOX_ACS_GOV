<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<jsp:include page="../frame/sub/head.jsp" />
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<script type="text/javascript">

	//form submit
	function pageSearch(page) {
		f = document.frmSearch;
		f.action = "/logInfo/cardFailLogViewPopup.do?srchPage=" + page;
		f.submit();
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
		f.action = "/logInfo/cardFailLogViewPopup.do"
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

		f.action = "/logInfo/cardFailPopExcelDownload.do"
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
	<input type="hidden" name="funm" value="${logInfoVO.funm}"/>
	<input type="hidden" name="fcdno" value="${logInfoVO.fcdno}"/>
	<input type="hidden" name="srchCond" value="${logInfoVO.srchCond}"/>
	<input type="hidden" name="stDateTime" value="${logInfoVO.stDateTime}"/>
	<input type="hidden" name="edDateTime" value="${logInfoVO.edDateTime}"/>
	<input type="hidden" id="rowDataArr" name="rowDataArr" value="" />
	<input type="hidden" id="rowTextArr" name="rowTextArr" value="" />
	<div class="popup_head_box" style="display: list-item;">
		<span id="popupNm">카드 실패 이력</span>
		<div class="close">
			<a href="javascript:void(0);" onclick="window.close()"><img src="/img/close_icon.png" alt="닫기" class="mt_10" /></a>
		</div>
	</div>
	<div class="popup_box">
		<!--검색박스 -->
		<div class="search_box_popup mb_20">
			<div class="search_in_bline">
				<div class="comm_search">
					<div class="title_text">카드번호 : <c:out value="${title}" /></div>
				</div>
			</div>
		</div>
				
		<div class="row">
			<div class="col-md-12">
				<div id="panel-1" class="panel">
					<div class="panel-container collapse show" style="">
						<div class="panel-content">
							<div class="d-flex align-items-top">
								<div class="flex-fill ml20">
									<div class="float-left mb10"><b style="display:block;line-height:30px;">전체 : <c:out value="${pagination.totRecord}" />건</b></div>
									<div class="tb_outbox ">
										<table class="tb_list">
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
											<tbody>
											<c:forEach items="${userlogInfo}" var="userlog" varStatus="status">
												<tr>
													<td><c:set var="fevttm" value="${fn:split(userlog.fevttm,'.')[0]}" /><c:out value="${fevttm}"/></td>
													<td><c:out value="${userlog.funm}"/></td>
													<td class="title"><c:out value="${userlog.flname}"/></td>
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
	<!-- 페이징 -->
	<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
	<!-- /페이징 -->
	
	<!-- modal 추가 팝업 -->
	  <!--   
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
	    -->
</form>
<jsp:include page="../frame/sub/tail.jsp" />