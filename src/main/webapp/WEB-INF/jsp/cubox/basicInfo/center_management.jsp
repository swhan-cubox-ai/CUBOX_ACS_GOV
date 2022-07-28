<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
						
						<c:if test="${sessionScope.loginVO.fkind3 ne '10'}">
						<script type="text/javascript">
							alert("권한이 없습니다.");
							location.href = "/index.do";
						</script>
						</c:if> 
						
						
						<script type="text/javascript">
						function fnCenterAddClose(){
							$("input[name=fvalue]").val("");
						} 
						
						function fnCenterAddSave(){
							var fvalue = $("input[name=fvalue]").val();
							if(fvalue == ""){alert ("센터명을 입력하세요."); $("input[name=fvalue]").focus(); return;}
							
							if(confirm("저장하시겠습니까?")){
								$.ajax({
									type:"POST",
									url:"<c:url value='/basicInfo/centerAddSave.do' />",
									data:{
										"fvalue": fvalue
									},
									dataType:'json',
									//timeout:(1000*30),
									success:function(returnData, status){
										if(status == "success") {
											location.reload();
										}else{ alert("ERROR!");return;}
									}
								});
					        }
							
						}
						
						function fnFuseynChange(fkind3, fuseyn){
							$("#fuseyn").val(fuseyn);
							$("#fkind3").val(fkind3);
						}
						
						function fnFuseynChangeSave(){
							var fkind3 = $("#fkind3").val();
							var fuseyn = $("#fuseyn").val();
							
							if(confirm("저장하시겠습니까?")){
								$.ajax({
									type:"POST",
									url:"<c:url value='/basicInfo/fuseynChangeSave.do' />",
									data:{
										"fkind3": fkind3,
										"fuseyn": fuseyn
									},
									dataType:'json',
									//timeout:(1000*30),
									success:function(returnData, status){
										if(status == "success") {
											location.reload();
										}else{ alert("ERROR!");return;}
									}
								});
					        }
							
						}
						
						$(document).ready(function() {
							
						});
						</script>
						
						<div class="subheader"> 
							<div class="title-bar float_wrap">
								<h1>센터 관리</h1>
							</div>
						</div> 						
						<div class="row">
							<div class="col-md-12">
								<div id="panel-1" class="panel">
                                    <div class="panel-container collapse show" style="">
                                        <div class="panel-content">
											<div class="float-right mb10">
                                                <button type="button" class="btn btn-sm btn-danger"  data-toggle="modal" data-target="#center-add-modal" onclick="fnCenterAddClose();">추가</button>
                                            </div>
                                            <div class="frame-wrap table-responsive text-nowrap">
                                                <table class="table m-0 table-bordered table-striped">                                     
                                                    <thead>
														<tr>
															<th>센터코드</th>
															<th>센터명</th>
															<th>사용유무</th> 
															<th>입력일</th>
															<th>편집</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${centerInfoList}" var="cList" varStatus="status">
														<tr>
                                                            <td><c:out value="${cList.fkind3}"/></td>
                                                            <td><c:out value="${cList.fvalue}"/></td>
                                                            <td><c:if test="${cList.fuseyn eq 'Y'}">사용중</c:if><c:if test="${cList.fuseyn eq 'N'}">사용안함</c:if></td>
                                                            <td><c:out value="${cList.fregdt}"/></td>
                                                            <td><button type="button" class="btn btn-xs btn-secondary" data-toggle="modal" data-target="#center-use-or-not" onclick="fnFuseynChange('<c:out value="${cList.fkind3}"/>','<c:out value="${cList.fuseyn}"/>')">편집</button></td>
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
				        <div class="modal fade" id="center-add-modal" tabindex="-1" role="dialog" aria-hidden="true">
				            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
				                <div class="modal-content">
				                    <div class="modal-header">
				                        <h5 class="modal-title">센터추가</h5>
				                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="fnCenterAddClose();">
				                            <span aria-hidden="true"><i class="fa fa-times"></i></span>
				                        </button>
				                    </div>
				                    <div class="modal-body">
				                        <table class="table m-0 table-view">
											<tbody>
												<tr>
				                                    <th>센터명</th>
				                                    <td><input type="text" id="fvalue" name="fvalue" class="form-control" placeholder="센터명" onkeydown="javascript:if(event.keyCode==13){centerAddSave();}"></td>
				                                </tr>
											</tbody>
				                        </table>
				                    </div>
				                    <div class="modal-footer">
				                        <button type="button" class="btn btn-sm btn-primary" onclick="fnCenterAddSave();">저장</button>
				                        <button type="button" class="btn btn-sm btn-default" data-dismiss="modal" aria-label="Close" onclick="fnCenterAddClose();">취소</button>
				                    </div>
				                </div>
				            </div>
				        </div>
				        <!-- modal 사용유무 팝업-->
				        <div class="modal fade" id="center-use-or-not" tabindex="-1" role="dialog" aria-hidden="true">
				            <div class="modal-dialog modal-sm modal-dialog-centered" role="document">
				                <div class="modal-content">
				                    <div class="modal-header">
				                        <h5 class="modal-title">사용유무 편집</h5>
				                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
				                            <span aria-hidden="true"><i class="fa fa-times"></i></span>
				                        </button>
				                    </div>
				                    <div class="modal-body">
				                        <table class="table m-0 table-view">
											<tbody>
												<tr>
				                                    <th>사용유무</th>
				                                    <td>
				                                        <select name="fuseyn" id="fuseyn" class="wd100 form-control">
				                                            <option value="Y">사용중</option>
				                                            <option value="N">사용안함</option>
				                                        </select>
				                                        <input type="hidden" id="fkind3" value=""/>
				                                    </td>
				                                </tr>
											</tbody>
				                        </table>
				                    </div>
				                    <div class="modal-footer">
				                        <button type="button" class="btn btn-sm btn-primary" onclick="fnFuseynChangeSave();">저장</button>
				                        <button type="button" class="btn btn-sm btn-default" data-dismiss="modal" aria-label="Close">취소</button> 
				                    </div>
				                </div>
				            </div>
				        </div>