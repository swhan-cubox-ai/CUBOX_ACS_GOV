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

		function userInfoSave(){
			var fauthtype = $("#fauthtype").val();				/*권한타입*/
			var fcdno = $("#fcdno").val(); 						/*카드번호*/
			var srchStartDate2 = $("#srchStartDate2").val();	/*시작일*/
			var srchExpireDate2 = $("#srchExpireDate2").val();	/*만료일*/
			var cfstatus = $("#cfstatus").val();				/*카드상태*/

			if(srchStartDate2 == "" || srchExpireDate2 == ""){
				alert("시작일, 종료일을 입력하세요.");
				return;
			}

			//권한타입, 카드상태, 시작일, 종료일 변경사항 체크?
			if(confirm("출입자 정보를 저장하시겠습니까?")){
				$.ajax({
					type:"POST",
					url:"/userInfo/userInfoSave.do",
					data:{
						"fuid" : '<c:out value="${userInfo.fuid}"/>',
						"fpartcd1" : '<c:out value="${userInfo.fpartcd1}"/>',
						"fauthtype": fauthtype,
						"fcdno": fcdno,
						"srchStartDate2": srchStartDate2,
						"srchExpireDate2": srchExpireDate2,
						"cfstatus": cfstatus
					},
					dataType:'json',
					//timeout:(1000*30),
					success:function(returnData, status){
						if(status == "success") {
							if(!fnIsEmpty(returnData.uCnt) && parseInt(returnData.uCnt) > 0) alert("개인정보를 저장하였습니다.");
							else alert("저장 중 오류가 발생되었습니다. 다시 시도하여 주십시오.");
							location.reload();
						}else{ alert("ERROR!"); alert("오류가 발생되었습니다. 다시 시도하여 주십시오."); return;}
					}
				});
			}
		}

		function imgUpload() {
			/* var afile = $("#customFile").val();
			if(afile == ""){
				alert("사진을 첨부해주세요.");
				$("#customFile").focus();
				return;
			}
			var fileExt = afile.slice(afile.lastIndexOf(".")+1).toLowerCase();
			if(fileExt != "jpg" && fileExt != "jpeg") {
				alert("jpg/jpeg 이미지파일만 업로드 가능합니다.");
				$("#customFileNm").val("");
				$("#customFile").val("");
				return;
			}  */

			var imgfuid = $("input[name=imgfuid]").val();
			var form = $('#frmDetail')[0];
			var formData = new FormData(form);
			formData.append('fuid', imgfuid);

			if($("#imageType").val() == "file") {
				console.log('===> image : attach file!!');
				formData.append("fileObj", $("#customFile")[0].files[0]);
			} else if($("#imageType").val() == "canvas") {
				console.log('===> image : draw canvas!!');
				var drawCanvas = document.getElementById('myCanvas');
				formData.append("imgUpload", drawCanvas.toDataURL('image/jpeg'));
			} else {
				alert("얼굴사진을 취득하거나 파일을 첨부하세요.");
				return;
			}

			$.ajax({
				url: "<c:url value='/userInfo/imgUserSave.do' />",
				data: formData,
				dataType:'json',
				processData: false,
				contentType: false,
				type: "POST",
				success: function(response){
					if(fnIsEmpty(response.rst)) {
						alert("저장에 실패하였습니다. 다시 시도하여 주십시오");
					} else {
						if (parseInt(response.rst) == -2)
							alert("jpg, jpeg, bmp, wbmp, png, gif 포맷만 사용 가능합니다.");
						else if (parseInt(response.rst) == -1)
							alert("이미지 파일이 확인되지 않습니다. 다시 확인 후 첨부하여 주십시오.");
					}
					location.reload();
				},
				error: function (jqXHR){
					alert(jqXHR.responseText);
				}
			});
		}
		function fnDetectCall () {
			showLoading ();
			$.ajax({
				url: "/userInfo/detectCallImg.do",
				data: "",
				processData: false,
				contentType: false,
				type: "POST",
				success: function(response) {
					hideLoading();
					//$("#imgView").attr("src",response);
					if(!fnIsEmpty(response) && !fnIsEmpty(response.rst) && response.rst == 1) {
						$("#myCanvas").show();
						$("#imgView").hide();
						$("#imageType").val("canvas");
						var image = new Image();
						image.onload = function() {
							//alert(image.width + "," + image.height);
							var canvas = document.getElementById('myCanvas');
							canvas.setAttribute('width', 640);
							canvas.setAttribute('height', 576);
							var scale = Math.max(canvas.width / image.width, canvas.height / image.height);
							//var scale = Math.min(canvas.width / image.width, canvas.height / image.height);
							var x = (canvas.width / 2) - (image.width / 2) * scale;
						    var y = (canvas.height / 2) - (image.height / 2) * scale;
							var ctx =  canvas.getContext('2d');
							ctx.drawImage(image, x, y, image.width * scale, image.height * scale);

							//canvas.setAttribute('width', image.height);
							//canvas.setAttribute('height', image.width);
						};
						image.src = response.imageString;
					} else {
						alert("사진이 확인되지 않습니다. 다시 시도하여 주십시오.");
					}
				},
				error: function (jqXHR){
					alert("사진촬영에 실패했습니다. 다시 시도하여 주십시오.");
					/***/
					//alert(jqXHR.responseText);
				}
			});
			//$("#imgView").attr("src",'/userInfo/detectCallImg.do');
			/* $.ajax({
			    crossOrigin : true,
				url: "http://172.16.190.160:6770/v1/reqSVC",
				data: {"tx":"FACE_DETECT"},
				dataType:'json',
				//processData: false,
				ontentType: "application/json; charset=UTF-8",
				type: "POST",
				success: function(response) {
					alert(JSON.stringify(response));
				},
				error: function (jqXHR){
					alert(jqXHR.responseText);
				}
			}); */

			/* $.ajax({
			    url : "http://172.16.190.160:6770/v1/reqSVC",
			    dataType : "jsonp",
			    jsonp : "callback",
			    success: function(response) {
					alert(JSON.stringify(response));
				},
				error: function (jqXHR){
					alert(jqXHR.responseText);
				}
			}); */
		}
		</script>

		<div class="modal-header">
            <h5 class="modal-title">출입자 및 카드정보</h5>
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
                    <div class="idchk_box">
                        <dl class="check-after">
                            <dt>고유번호</dt>
                            <dd class="flex-grow-1"><c:out value="${userInfo.fuid}"/></dd>
                        </dl>
                    </div>
                    <div class="d-flex align-items-stretch">
                        <div class="mt40">
                            <c:if test="${empty userBioInfo}">
                        		<img id="imgView" src='/userInfo/getByteImage.do?fuid=<c:out value="${userInfo.fuid}"/>' onerror="this.src='/images/no-img-user.jpg'" style="width:320px; height:288px; object-fit:cover; transform:scaleX(-1); ">
                        	</c:if>
                        	<c:if test="${!empty userBioInfo}">
                        		<a href='/userInfo/getByteImageDown.do?fuid=<c:out value="${userInfo.fuid}"/>'><img id="imgView" src='/userInfo/getByteImage.do?fuid=<c:out value="${userInfo.fuid}"/>' onerror="this.src='/images/no-img-user.jpg'" style="width:320px; height:288px; object-fit:cover; transform:scaleX(-1); "></a>
                        	</c:if>
                        	<canvas id="myCanvas" style="width:320px; height:288px; display:none;" width="320" height="288"></canvas>
                        	<div class="d-flex mb3" >
                                <form id="frmDetail" name="frmDetail" method="post" enctype="multipart/form-data" onsubmit="return false;">
		                        	<input type="hidden" name="imgfuid" value='<c:out value="${userInfo.fuid}"/>' />
		                        	<div class="custom-file mt10" style="z-index:0; width: 180px;">
										<input type="file" class="custom-file-input" id="customFile">
										<label class="custom-file-label" for="customFile"></label>
										<input type="hidden" id="imageType" name="imageType"/>
									</div>
								</form>
								<button type="button" id="btnSnapshot" class="btn btn-sm btn-dark ml5 mt10" onclick="fnDetectCall()" style="height:37px; z-index: 10">사진촬영</button> <!-- fnDetectPop() -->
                                <button type="button" class="btn btn-sm btn-primary flex-fill ml5 mt10" style="height:37px; z-index: 20" onclick="imgUpload();">업로드</button>
                            </div>
                        </div>
                        <div class="flex-fill ml10">
                            <div class="float-right mb10">
                                <button type="button" class="btn btn-sm btn-dark" onclick="userInfoSave();">개인정보저장</button>
                            </div>
                            <table class="table m-0 table-view">
                                <colgroup>
                                	<col style="width:8%">
                                    <col style="width:13%">
                                    <col style="width:27%;">
                                </colgroup>
                                <tbody>
                                    <tr>
                                    	<th rowspan="2" style="border-right: 1px solid #cecece">사용자정보</th>
                                        <th>이름</th>
                                        <td>
                                        	<c:out value="${userInfo.funm}"/>
                                        </td>
                                    </tr>
                                    <tr>
										<th>출입자타입</th>
                                        <td>
                                        	<c:forEach items="${userType}" var="uType" varStatus="status">
                                        		<c:if test="${userInfo.futype eq uType.fvalue}">
                                        			${uType.fkind3}
                                        		</c:if>
                                        	</c:forEach>
                                        </td>
                                    </tr>
                                    <tr>
                                    	<th rowspan="4" style="border-right: 1px solid #cecece">카드정보</th>
                                        <th>카드번호</th>
                                        <td>
                                        	<select name="fcdno" id="fcdno" class="form-control wd60" onchange="getAuthGroup(this.value);">
			                                    <c:forEach items="${cardInfoList}" var="cInfoList" varStatus="status">
			                                    <option value='<c:out value="${cInfoList.fcdno}"/>' <c:if test="${cInfoList.fcdno eq sfcdno}">selected</c:if>><c:out value="${cInfoList.fcdno}"/></option>
			                                    </c:forEach>
			                                </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>카드상태</th>
                                        <td>
                                            <select name="cfstatus" id="cfstatus" class="form-control wd60">
												<c:forEach items="${cardStatus}" var="cStatus" varStatus="status">
												<option value='<c:out value="${cStatus.fvalue}"/>'><c:out value="${cStatus.fkind3}"/></option>
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
        <!-- div class="modal-footer">
            <button type="button" class="btn btn-sm btn-primary">저장</button>
            <button type="button" class="btn btn-sm btn-default">취소</button>
        </div-->