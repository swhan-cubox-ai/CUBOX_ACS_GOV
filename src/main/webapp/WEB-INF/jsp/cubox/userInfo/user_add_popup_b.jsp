<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"      	uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="validator" 	uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    	uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="aero.cubox.sample.service.vo.LoginVO" %>
<% response.setHeader("Cache-Control", "no-cache"); %>
<% response.setHeader("Pragma", "no-cache"); %>
<% response.setDateHeader("Expires", 0); %>
<c:set var="showURI" value="${requestScope['javax.servlet.forward.servlet_path']}" />
<script type="text/javascript" src="<c:url value='/js/faceComm.js?ver=<%=Math.random() %>'/>"></script>

		<script type="text/javascript">		
		function authSearch(){
			var fcdno = $("#fcdno").val();
			var srchCondWord = $("#srchCondWord").val();
						
			console.log("srchCondWord : "+ srchCondWord);
			$.ajax({
				type:"POST",
				url:"<c:url value='/userInfo/newTotalAuthGroup.do'/>",
				data: {
				   "srchCondWord" : srchCondWord
				},
				dataType:'json',  
				success:function(returnData, status){
					if(status == "success") {
						var totalAuthGroupList = "";
						for(var i=0; i < returnData.totalAuthGroup.length; i++ ){
							totalAuthGroupList = totalAuthGroupList + "<tr><td><input type='checkbox' name='totalFtid' value='" + returnData.totalAuthGroup[i].ftid + "'></td><td>" + returnData.totalAuthGroup[i].ftid + "</td><td>" + returnData.totalAuthGroup[i].fitemnm +"</td></tr>";
						}
						$("#totalAuthGroup").html(totalAuthGroupList);
					}else{ alert("ERROR!");return;}
				}
			});
		}
		 
		$(function(){  			 
			$("#add_arrow").click(function(){ 
								 
				$("input[name=totalFtid]:checked").each(function(){
					var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
					//$("#totalAuthGroup").$(this).closest("tr").remove();
					tag = tag.replace("totalFtid","userFtid");
					
					$("#userAuthGroup").append(tag);
				});
				
				var ckd = $("input[name=totalFtid]:checked").length;
				for(var i=ckd-1; i>-1; i--){
					$("input[name=totalFtid]:checked").eq(i).closest("tr").remove();
				}
			});
			
			$("#delete_arrow").click(function(){
								
				$("input[name=userFtid]:checked").each(function(i){
					var tag = "<tr>" + $(this).closest("tr").html() + "</tr>";
					//$("#totalAuthGroup").$(this).closest("tr").remove();
					tag = tag.replace("userFtid","totalFtid");
					
					$("#totalAuthGroup").append(tag);
				});
				
				var ckd = $("input[name=userFtid]:checked").length;
				for(var i=ckd-1; i>-1; i--){
					$("input[name=userFtid]:checked").eq(i).closest("tr").remove();
				}
			});
			
			$("#totalAuthCheckAll").click(function(){
				if($("#totalAuthCheckAll").prop("checked")){
					$("input[name=totalFtid]").prop("checked", true);
				}else{
					$("input[name=totalFtid]").prop("checked", false);
				} 
			});
			
			$("#userAuthCheckAll").click(function(){
				if($("#userAuthCheckAll").prop("checked")){
					$("input[name=userFtid]").prop("checked", true);
				}else{
					$("input[name=userFtid]").prop("checked", false);
				}
			});
			 			
			$("#cfusdt").change(function(){ 
				var srchStartDt = $("#cfusdt").val().replace('-','').replace('-','');
				var srchExpireDt = $("#cfuedt").val().replace('-','').replace('-','');
				if(srchStartDt > srchExpireDt && srchExpireDt != ""){
					alert("??????????????? ??? ????????? ????????? ??? ????????????.");
					$("#cfusdt").val('');
					$("#cfusdt").focus(); 
				}
			}); 
			
			$("#cfuedt").change(function(){
				var srchStartDt = $("#cfusdt").val().replace('-','').replace('-','');
				var srchExpireDt = $("#cfuedt").val().replace('-','').replace('-','');
				if(srchStartDt > srchExpireDt && srchExpireDt != ""){
					alert("??????????????? ??? ????????? ????????? ??? ????????????.");
					$("#cfuedt").val('');
					$("#cfuedt").focus();
				}
			});
		}); 
		
		/*
		//?????????????????? ????????????
		$(document).ready(function(){
        	var fileTarget = $('.filebox .upload_hidden');
        	$(document).on('change', '.filebox .upload_hidden', function(){ // ?????? ????????????
        		var filename = ""; 
        		
        		if($(this)[0].files.length == 0){
        			filename = "";
        		}else{
        			filename = $(this)[0].files[0].name;
        		}
        		// ????????? ????????? ??????
        		$(this).siblings('.upload_name').val(filename);
        	});
        });
		*/
		
		function cdnoDuplCheck(){
			var addfcdno = $("#addfcdno").val();
			
			if(addfcdno == ""){ 
				alert("??????????????? ???????????????.");
				$("#addfcdno").focus();
				return;
			}

			$.ajax({
				type:"POST",
				url:"/userInfo/getCdnoCnt.do",
				data:{
					"addfcdno": addfcdno
				},
				dataType:'json',  
				//timeout:(1000*30),  
				success:function(returnData, status){ 
					if(status == "success") { 
						if(returnData.cdnoCnt > 0){
							alert("?????? ????????? ???????????????.\n??????????????? ?????? ??????????????????.");
							$("#addfcdno").focus();
							return;
						}else{
							if(confirm("??????????????? ???????????????. \n????????? ?????????????????????????")){ 
								$("#cdnoDuplChk").val("Y");
								$("#addfcdno").attr("readonly", true);
								$("#cdnoDuplchkBtn").attr("disabled", true);
							}
						}					
					}else{ alert("ERROR!");return;}
				}
			});		
		} 
		
		function newUserInfoSave(){
			var funm = $("#funm").val(); 
			var fauthtype = $("#fauthtype").val();
			var fustatus = $("#fustatus").val();
			var fpartnm1 = $("#fpartnm1").val();
			var fpartnm2 = $("#fpartnm2").val();
			var fpartnm3 = $("#fpartnm3").val();
			var fpartcd2 = $("#fpartcd2").val();
			var ftel = $("#ftel").val();
			var addfcdno = $("#addfcdno").val();
			var cdnoDuplChk =  $("#cdnoDuplChk").val();
			var cfcdnum = $("#cfcdnum").val();
			var cfusdt = $("#cfusdt").val();
			var cfuedt = $("#cfuedt").val();
			var authArray= [];
			$("input[name=userFtid]").each(function(i){ 
				authArray.push($(this).val());  
			});
			console.log(authArray.length);
			if(funm == ""){alert("????????? ???????????????."); tabChange("user"); $("#funm").focus(); return;}
			if(fauthtype == ""){alert("??????????????? ???????????????."); tabChange("user"); $("#fauthtype").focus(); return;}
			if(addfcdno == ""){alert("??????????????? ???????????????."); tabChange("card"); $("#addfcdno").focus(); return;}
			if(cdnoDuplChk != "Y"){alert("???????????? ??????????????? ?????????."); tabChange("card"); $("#cdnoDuplChk").focus(); return;}
			//if(cfcdnum == ""){alert("????????????????????? ???????????????."); tabChange("card"); $("#cfcdnum").focus(); return;}
			if(cfusdt == ""){alert("?????? ???????????? ???????????????."); tabChange("card"); $("#cfusdt").focus(); return;}
			if(cfuedt == ""){alert("?????? ???????????? ???????????????."); tabChange("card"); $("#cfuedt").focus(); return;}
			if(cfuedt == ""){alert("?????? ???????????? ???????????????."); tabChange("card"); $("#cfuedt").focus(); return;}
			if(authArray.length == 0){alert("?????? ????????? ???????????????."); tabChange("card"); return;}

			var form = $('#fmNewUserInfo')[0]; 
			var formData = new FormData(form);
			formData.append('funm', funm);
			formData.append('fauthtype', fauthtype);
			formData.append('fustatus', fustatus);
			formData.append('fpartnm1', fpartnm1);
			formData.append('fpartnm2', fpartnm2);
			formData.append('fpartnm3', fpartnm3);
			formData.append('fpartcd2', fpartcd2);
			formData.append('ftel', ftel);
			formData.append('addfcdno', addfcdno);
			formData.append('cfcdnum', cfcdnum);
			formData.append('cfusdt', cfusdt);
			formData.append('cfuedt', cfuedt);
			formData.append('authArray', authArray);

			if($("#imageType").val() == "file") {
				formData.append("fileObj", $("#customFile")[0].files[0]);
			} else if($("#imageType").val() == "canvas") {
				var drawCanvas = document.getElementById('myCanvas');
				formData.append("imgUpload", drawCanvas.toDataURL('image/jpeg'));
			}


			$.ajax({
				url: "<c:url value='/userInfo/newUserInfoSave.do' />",
				data: formData,
				dataType:'json',
				processData: false, 
				contentType: false,
				type: "POST", 
				success: function(response){
					//location.reload();
					if(response.cardSaveCnt > 0){
						alert("?????????????????????.");
						opener.parent.userSearch();
						window.close();
					}else{
						alert("????????? ??????????????????.");
						return;
					}
				},
				error: function (jqXHR){
					alert("????????? ??????????????????.");
					return;
					//alert(jqXHR.responseText);
				}
			});		
		}
		
		function resetBtn(){
			$("#cdnoDuplChk").val("N");
			$("#addfcdno").removeAttr("readonly");  
			$("#cdnoDuplchkBtn").removeAttr("disabled");
			
		} 
		
		function tabChange(obj){
			if(obj == "user"){
				$(".nav-link").eq(0).addClass('active');
				$(".nav-link").eq(0).attr('aria-selected','true'); 
				$(".nav-link").eq(1).removeClass('active');
				$(".nav-link").eq(1).attr('aria-selected','false');
				$("#tab_borders_icons-1").addClass('active show');
				$("#tab_borders_icons-2").removeClass('active show');
			}else{
				$(".nav-link").eq(0).removeClass('active');
				$(".nav-link").eq(0).attr('aria-selected','false'); 
				$(".nav-link").eq(1).addClass('active');
				$(".nav-link").eq(1).attr('aria-selected','true');
				$("#tab_borders_icons-1").removeClass('active show');
				$("#tab_borders_icons-2").addClass('active show');
			}
		}
		 
		function futypeChange(futype){
			switch(futype){
				case '5':	
					$("#fvisitnum").removeAttr("readonly"); 	
					break; 
				case '6':	
					$("#fvisitnum").removeAttr("readonly"); 	
					break;
				case '7':	
					$("#fvisitnum").removeAttr("readonly"); 	
					break;
				case '8':	
					$("#fvisitnum").removeAttr("readonly"); 	
					break;
				case '9':	
					$("#fvisitnum").removeAttr("readonly"); 	
					break;
				default:
					$("#fvisitnum").attr("readonly", true);
				
			}
		}
		</script> 
		<div class="modal-header">
            <h5 class="modal-title">????????? ??? ?????? ????????????</h5>
            <button type="button" class="close" onclick="window.close()">
                <span aria-hidden="true"><i class="fa fa-times"></i></span>
            </button>
        </div>  
        <div class="modal-body"> 
        	<form id="fmNewUserInfo" name="fmNewUserInfo" method="post" enctype="multipart/form-data" onsubmit="return false;">
            <ul class="nav nav-tabs" role="tablist"> 
                <li class="nav-item">
                    <a class="nav-link active" data-toggle="tab" href="#tab_borders_icons-1" role="tab" ><i class="fa fa-user mr-1"></i> ????????? ??????</a>
                </li>
                <li class="nav-item"> 
                    <a class="nav-link" data-toggle="tab" href="#tab_borders_icons-2" role="tab" ><i class="fa fa-id-card"></i> ???????????? </a>
                </li>
            </ul>

            <div class="tab-content border border-top-0 p-3">
                <div class="tab-pane fade active show" id="tab_borders_icons-1" role="tabpanel">

                    <div class="d-flex align-items-stretch">    
                        <div class="mt8"> 
                            <img id="imgView" src="/images/no-img.jpg" alt="" style="width:320px; height:288px;">

                        	<canvas id="myCanvas" style="width:320px; height:288px; display:none;" width="320" height="288"></canvas>
                        	<div class="d-flex mb3" >
	                        	<div class="custom-file mt10" style="z-index:0;"> 									
									<input type="file" class="custom-file-input" id="customFile">
									<label class="custom-file-label" for="customFile"></label>
									<input type="hidden" id="imageType" name="imageType"/>
								</div>
								<button type="button" id="btnSnapshot" class="btn btn-sm btn-dark ml5 mt10" onclick="fnDetectPop()" style="height:37px; z-index:10;">????????????</button>
                            </div>                            
                        </div>

                        <div class="flex-fill ml10">
                            <table class="table m-0 table-view">
                                <colgroup>
                                    <col style="width:16%">
                                    <col style="width:34%;">
                                    <col style="width:16%">
                                    <col style="width:34%;">
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <th>??????(*)</th>
                                        <td colspan="3"><input type="text" id="funm" name="funm" maxlength="15" class="form-control" /></td>
                                    </tr>
                                    <tr>
                                        <th>????????????(*)</th>
                                        <td>
                                            <select name="fauthtype" id="fauthtype" class="form-control">
                                            	<c:forEach items="${authType}" var="aType" varStatus="status"> 
                                                <option value='<c:out value="${aType.fvalue}"/>' <c:if test="${userInfo.fauthtype eq aType.fvalue}">selected</c:if>><c:out value="${aType.fkind3}"/></option>
												</c:forEach>
                                            </select>
                                        </td>
                                        <th>???????????????</th> 
                                         <td>
                                            <select name="fustatus" id="fustatus" class="form-control">
                                            	<option value=''>??????</option>
                                            	<c:forEach items="${userStatus}" var="uStatus" varStatus="status"> 
                                                <option value='<c:out value="${uStatus.fvalue}"/>' ><c:out value="${uStatus.fkind3}"/></option>
												</c:forEach>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>?????????</th>
                                        <td><input type="text" id="fpartnm1" name="fpartnm1" placeholder="?????????" class="form-control" /></td>
                                        <th>??????</th>
                                        <td><input type="text" id="fpartnm2" name="fpartnm2" placeholder="?????????" class="form-control" /></td>
                                    </tr>
                                    <tr>
                                        <th>??????</th>
                                        <td colspan="3"><input type="text" id="fpartnm3" name="fpartnm3" placeholder="??????" class="form-control" /></td>           
                                    </tr>
                                    <tr>
                                        <th>????????????</th>
                                        <td colspan="3"><input type="text" id="fpartcd2" name="fpartcd2" class="form-control" /></td>           
                                    </tr>
                                    <tr>
                                        <th>????????????</th>
                                        <td colspan="3"><input type="text" id="ftel" name="ftel" class="form-control" /></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div> 
                </div> 
                <div class="tab-pane fade" id="tab_borders_icons-2" role="tabpanel">
                    <div class="idchk_box date-search-box mt5">
                        <div class="d-flex justify-content-between">
	                        <dl class="check-after">
	                            <dt>????????????(*)</dt>
	                            <dd class="flex-grow-1">	 
	                            	<input type="text" id="addfcdno" name="addfcdno" class="form-control" />
	                            	<input type="hidden" id="cdnoDuplChk" name="cdnoDuplChk" />
	                            </dd>
	                            <dd>
	                            	<button type="button" class="btn btn-ms btn-dark" id="cdnoDuplchkBtn" onclick="cdnoDuplCheck();">????????????</button>
	                            	<button type="button" class="btn btn-ms btn-secondary" onclick="resetBtn();">?????????</button>
	                            </dd>
	                        </dl>
	                        <dl class="check-after">
								<dt>??????????????????(*)</dt>
							   	<dd class="flex-grow-1"><input type="text" id="cfcdnum" name="cfcdnum" class="form-control wd100p"></dd>
							</dl>
							<div class="d-flex">
                                <div class="form-group">
                                    <div class='input-group date' id='datetimepicker3'>
                                        <label for="" class="d-flex align-items-center mr10">?????????(*)</label>
                                        <input type='text' id="cfusdt" name="cfusdt" class="search-word2 form-control2 wd100p" />
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span>
                                    </div> 
                                </div> 
                                <div class="form-group mo-ml10">
                                    <div class='input-group date' id='datetimepicker4'>
                                        <label for="" class="d-flex align-items-center mr10">?????????(*)</label>
                                        <input type='text' id="cfuedt" name="cfuedt" class="search-word2 form-control2 wd100p" />
                                        <span class="input-group-addon">
                                            <span class="glyphicon glyphicon-calendar"></span>
                                        </span> 
                                    </div>
                                </div>  
                            </div>
	              		</div>	    
                    </div>
                    <div class="d-flex align-items-tops position-relative">
                        <div class="p-2 wd45">
                             
                            <div class="d-flex pop-search">
		                        <h2 class="h2_type1">?????? ?????? ??????</h2>
		                        <div class="d-flex">
		                           <input placeholder="???????????? ??????????????????" id="srchCondWord" name="srchCondWord" class="search-word form-control" type="search" onkeypress="if(event.keyCode==13){authSearch();}">
		                            <button class="bbs-search-btn" title="??????" onclick="authSearch();"><i class="fa fa-search"></i></button>
								</div>
		                    </div>
                            <table class="table m-0 table-bordered table-striped">
                                <colgroup> 
                                    <col style="width:13%">
                                    <col style="width:22%">
                                    <col style="width:65%">
                                </colgroup> 
                                <thead>
                                    <tr>
                                        <th><input type="checkbox" id="totalAuthCheckAll"> ??????</th>
                                        <th>FID</th>
                                        <th>?????????</th>
                                    </tr>
                                </thead>
                            </table>
                            <div class="tbody-scroll ht240">
                                <table class="table m-0 table-bordered table-striped">
                                <colgroup>
                                    <col style="width:13%">
                                    <col style="width:22%">
                                    <col style="width:65%">
                                </colgroup>
                                <tbody id="totalAuthGroup">
                                    <c:forEach items="${totalAuthGroupList}" var="tagList" varStatus="status">
                                    <tr>
                                        <td><input type="checkbox" name='totalFtid' value='<c:out value="${tagList.ftid}"/>'></td>
                                        <td><c:out value="${tagList.ftid}"/></td>
                                        <td><c:out value="${tagList.fitemnm}"/></td>
                                    </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            </div>  
                        </div> 
                        <div class="p-2 mt100">
                            <button class="btn btn-ms btn-danger" id="add_arrow">??????<span class="fa fa-angle-double-right ml-2"></span></button>
                            <hr>
                            <button class="btn btn-ms btn-secondary" id="delete_arrow"><span class="fa fa-angle-double-left mr-2"></span>??????</button>
                        </div>
                        <div class="p-2 wd45">
                            <div class="d-flex justify-content-between align-items-center">
                                <h2 class="h2_type1">????????? ????????? ?????? ??????(*)</h2>
                            </div>
                            <table class="table m-0 table-bordered table-striped">
                                <colgroup>
                                    <col style="width:13%">
                                    <col style="width:22%">
                                    <col style="width:65%">
                                </colgroup>
                                <thead>
                                    <tr>
                                        <th><input type="checkbox" id="userAuthCheckAll"> ??????</th>
                                        <th>FID</th>
                                        <th>?????????</th>
                                    </tr>
                                </thead>
                            </table>
                            <div class="tbody-scroll ht240">
                                <table class="table m-0 table-bordered table-striped">
                                <colgroup>
                                    <col style="width:13%">
                                    <col style="width:22%">
                                    <col style="width:65%">
                                </colgroup>
                                <tbody id="userAuthGroup">
                                    
                                </tbody>
                            </table>
                            </div>
                        </div> 
                    </div> 
                </div>  
            </div>  
  			</form>
        </div>  
        <div class="modal-footer"> 
            <button type="button" class="btn btn-sm btn-primary" onclick="newUserInfoSave();">??????</button>
            <button type="button" class="btn btn-sm btn-default" onclick="window.close();">??????</button>
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
			},200);
		});
		</script>