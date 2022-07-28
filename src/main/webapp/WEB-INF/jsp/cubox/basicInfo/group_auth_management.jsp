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
						

						function fnClose(){
							$("input[name=fkind1]").val("");
							$("input[name=fkind2]").val("");
							$("input[name=fkind3]").val("");
							$("input[name=fvalue]").val("");
							$("input[name=fuseyn]").val("");
							

						}

						
						function fnAuthAddSave(){

							if(fnFormValueCheck ("authAddForm")) {
								var formData = $("#authAddForm").serializeFormJSON ();
								if(confirm("저장하시겠습니까?")){
									$.ajax({
										type:"POST",
										url:"<c:url value='/basicInfo/authAddSave.do' />",
										data:formData,
										dataType:'json',
										//timeout:(1000*30),
										success:function(returnData, status){
											if(returnData.authSaveCnt > 0) {
												location.reload();
											} else if (returnData.authSaveCnt < 0) {
												alert("코드가 중복됩니다.\n\n코드를 다시 확인하여 주십시오.");
												$("#fkind3").focus();
											} else{ alert("ERROR!");return;}
										}
									});
						        }
							}
						}

						function fnAuthChange(authorId, authorNm, authorDesc, sortOrdr){
							
							$("#txtEditAuthorNm").val(authorNm);
							$("#txtEditAuthorDesc").val(authorDesc);
							$("#txtEditSortOrdr").val(sortOrdr);
							$("#txtEditAuthorId").val(authorId);
						}

						
						function fnAuthEditSave(){
							if(fnFormValueCheck ("authEditForm")) {
								var formData = $("#authEditForm").serializeFormJSON ();
								if(confirm("수정하시겠습니까?")){
									$.ajax({
										type:"POST",
										url:"<c:url value='/basicInfo/authEditSave.do' />",
										data:formData,
										dataType:'json',
										//timeout:(1000*30),
										success:function(returnData, status){
											if(returnData.authEditCnt > 0) {
												location.reload();
											} else{ alert("ERROR!");return;}
										}
									});
						        }
							}

						}

					
						function fnGroupAuthUseynChangeSave(authorId, useyn){

							if(useyn == 'Y'){
								var confirmTxt = "사용안함으로 변경하시겠습니까?";
								var useyn2 = "N";
							}else{
								var confirmTxt = "사용중으로 변경하시겠습니까?";
								var useyn2 = "Y";
							}

							if(confirm(confirmTxt)){
								$.ajax({
									type:"POST",
									url:"<c:url value='/basicInfo/groupAuthUseynChangeSave.do' />",
									data:{
										"authorId": authorId,
										"useyn": useyn2
									},
									dataType:'json',
									//timeout:(1000*30),
									success:function(returnData, status){
										if(returnData.groupAuthCnt > 0) {
											location.reload();
										} else{ alert("ERROR!");return;}
									}
								});
					        }

						}
						
						</script>

						<div class="subheader">
							<div class="title-bar float_wrap wd100">
								<h1>권한 관리 </h1>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12">
								<div id="panel-1" class="panel">
                                    <div class="panel-container collapse show" style="">
                                        <div class="panel-content">
											<div class="float-right mb10">
                                                <button type="button" class="btn btn-sm btn-danger"  data-toggle="modal" data-target="#auth-add-modal" onclick="fnClose();">추가</button>
                                            </div>
                                            <div class="frame-wrap table-responsive text-nowrap">
                                                <table class="table m-0 table-bordered table-striped">
                                                	<colgroup>
						                                <col style="width:12%">
						                                <col style="">
						                                <col style="">
						                                <col style="">
						                                <col style="">
						                                <col style="width:12%">
						                                <col style="width:12%">
						                            </colgroup>
                                                    <thead>
														<tr>
															<th>권한</th>
															<th>권한 설명</th>
															<th>등록일</th>
                                                            <th>수정일</th>
                                                            <th>사용유무</th>
                                                            <th>편집</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${groupAuthList}" var="gaList" varStatus="status">
														<tr>
                                                            <td><c:out value="${gaList.authorNm}"/></td>
                                                            <td><c:out value="${gaList.authorDesc}"/></td>
                                                            <td><c:out value="${gaList.registDt}"/></td>
                                                            <td><c:out value="${gaList.modifyDt}"/></td>
                                                            <td>
                                                            	<c:if test="${gaList.useYn eq 'Y'}"><button type="button" class="btn btn-xs btn-info" onclick="fnGroupAuthUseynChangeSave('<c:out value="${gaList.authorId}"/>','<c:out value="${gaList.useYn}"/>');">사용중</button></c:if>
                                                            	<c:if test="${gaList.useYn eq 'N'}"><button type="button" class="btn btn-xs btn-light" onclick="fnGroupAuthUseynChangeSave('<c:out value="${gaList.authorId}"/>','<c:out value="${gaList.useYn}"/>');">사용안함</button></c:if>
                                                            </td>
                                                            <td><button type="button" class="btn btn-xs btn-secondary" data-toggle="modal" data-target="#account-editor-modal" onclick="fnAuthChange('${gaList.authorId}',  '${gaList.authorNm}', '${gaList.authorDesc}', '${gaList.sortOrdr}');">편집</button></td>
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

						<!-- modal 추가 팝업-->
				        <div class="modal fade" id="auth-add-modal" tabindex="-1" role="dialog" aria-hidden="true">
				            <div class="modal-dialog modal-dialog-centered" role="document">
				                <div class="modal-content">
				                    <div class="modal-header">
				                        <h5 class="modal-title">권한 추가</h5>
				                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="fnClose();">
				                            <span aria-hidden="true"><i class="fa fa-times"></i></span>
				                        </button>
				                    </div>
				                    <div class="modal-body">
				                    	<form id="authAddForm">
				                        <table class="table m-0 table-view">
				                            <colgroup>
				                                <col style="width:25%">
				                                <col style="width:*;">
				                                <col style="width:15%">
				                            </colgroup>
											<tbody>

				                             
				                               
												<tr>
												    <th>권한</th>
												    <td colspan="2">
												    	<input type="text" id="txtAuthorNm" name="txtAuthorNm" class="form-control" maxlength="200" placeholder="권한" check="text" checkName="권한">
												    </td>
												</tr>
				                                <tr>
				                                    <th>권한설명</th>
				                                    <td colspan="2">
				                                    	<input type="text" id="txtAuthorDesc" name="txtAuthorDesc" class="form-control" maxlength="200" placeholder="권한설명" check="text" checkName="권한설명">
				                                    </td>
				                                </tr>
				                                <tr>
				                                    <th>정렬순서</th>
				                                    <td colspan="2">
				                                    	<input type="text" id="txtSortOrdr" name="txtSortOrdr" class="form-control" maxlength="3" placeholder="정렬순서" onkeyup="fnRemoveChar(event)">
				                                    </td>
				                                </tr>
											</tbody>
				                        </table>
				                        </form>
				                    </div>
				                    <div class="modal-footer">
				                        <button type="button" class="btn btn-sm btn-primary" onclick="fnAuthAddSave();">저장</button>
				                        <button type="button" class="btn btn-sm btn-default" data-dismiss="modal" aria-label="Close" onclick="fnClose();">취소</button>
				                    </div>
				                </div>
				            </div>
				        </div>
				        <!-- modal 사용유무 팝업-->
				        <div class="modal fade" id="account-editor-modal" tabindex="-1" role="dialog" aria-hidden="true">
				            <div class="modal-dialog modal-dialog-centered" role="document">
				                <div class="modal-content">
				                    <div class="modal-header">
				                        <h5 class="modal-title">권한 편집</h5>
				                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
				                            <span aria-hidden="true"><i class="fa fa-times"></i></span>
				                        </button>
				                    </div>
				                    <div class="modal-body">
				                    	<form id="authEditForm">
					                    	
					                        <input type="hidden" id="txtEditAuthorId" name="txtEditAuthorId" />
					                        <table class="table m-0 table-view">

					                            <colgroup>
					                                <col style="width:25%">
					                                <col style="width:*;">
					                            </colgroup>
												<tbody>
					                                <tr>
													    <th>권한</th>
													    <td>
													    	<input type="text" id="txtEditAuthorNm" name="txtEditAuthorNm" class="form-control" maxlength="50" placeholder="권한">
													    </td>
													</tr>
													 <tr>
													    <th>권한 설명</th>
													    <td>
													    	<input type="text" id="txtEditAuthorDesc" name="txtEditAuthorDesc" class="form-control" maxlength="50" placeholder="권한 설명">
													    </td>
													</tr>

					                                <tr>
					                                    <th>정렬순서</th>
					                                    <td colspan="2">
					                                    	<input type="text" id="txtEditSortOrdr" name="txtEditSortOrdr" class="form-control" maxlength="3" placeholder="정렬순서" onkeyup="fnRemoveChar(event)">
					                                    </td>
					                                </tr>
												</tbody>
					                        </table>
				                        </form>
				                    </div>
				                    <div class="modal-footer">
				                        <button type="button" class="btn btn-sm btn-primary" onclick="fnAuthEditSave();">저장</button>
				                        <button type="button" class="btn btn-sm btn-default" data-dismiss="modal" aria-label="Close">취소</button>
				                    </div>
				                </div>
				            </div>
				        </div>