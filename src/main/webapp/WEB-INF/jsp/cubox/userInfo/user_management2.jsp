 <%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<body>
	<script type="text/javascript">
		var threadRefresh;
		var msort = {"1":"▲", "2":"▼"};

		function userSearch() {
			f = document.frmSearch;
			f.action = "/userInfo/userMngmt.do";
			f.submit();
		}

		function pageSearch(page) {
			f = document.frmSearch;
			f.action = "/userInfo/userMngmt.do?srchPage=" + page;
			showLoading();
			f.submit();
		}

		$(function() {
			$(".title_tx").html("출입자 관리");
			$("#reset").click(function() {
				$("#srchCond option:eq(0)").prop('selected', 'selected');
				$("#srchFbioyn").val('ALL');
				$('.search_box input[type="checkbox"]').prop("checked",false).trigger('change');
				$("#srchFexp").prop("checked",true).trigger('change');
				$("#srchFunm").prop("checked",true).trigger('change');
				$("#srchCondWord").val("");
			});

			$("input[name=checkAll]").click(function() {
				if($(this).prop("checked")){
					$("input[name=excelColumn]").prop("checked", true);
				}else{
					$("input[name=excelColumn]").prop("checked", false);
				}
			});

			$("#checkAllLst").click(function() {
				if($("#checkAllLst").prop("checked")){
					$("input[name=checkbx]").prop("checked", true);
				}else{
					$("input[name=checkbx]").prop("checked", false);
				}
			});

			$("#srchRecPerPage").change(function(){
				userSearch();
			});

			$("#trUserMgt th").click(function() {
				var thid = $(this).prop("id");
				if(!fnIsEmpty(thid)) {
					thid = thid.replace("th","");
					var sort = $("#sort"+thid).val();
					var chngSort = "";
					switch(sort) {
						case "2" :
							break;
						case "1" :
							chngSort = "2";
							break;
						default :
							chngSort = "1";
							break;
					}
					$("#trUserMgt th input[type=hidden]").val("");
					$("#trUserMgt th span").html("");
					$("#sort"+thid).val(chngSort);
					$("#sp"+thid).html(fnIsEmpty(msort[chngSort])?"":msort[chngSort]);
					$("#hidSortName").val(thid);
					$("#hidSortNum").val(chngSort);
					pageSearch("${pagination.curPage}");
				}
			});

			//sort 설정
			if(!fnIsEmpty($("#hidSortName").val ())) {
				var hidsortnm = $("#hidSortName").val ();
				var hidsortnum = $("#hidSortNum").val ();
				$("#sort"+hidsortnm).val(hidsortnum);
				$("#sp"+hidsortnm).html(fnIsEmpty(msort[hidsortnum])?"":msort[hidsortnum]);
			}

			//modal popup set
			modalPopup ("excelAllDownloadPopup", "전체 엑셀 다운로드", 400, 670);
			modalPopup ("excelSelDownloadPopup", "선택 엑셀 다운로드", 400, 670);
			modalPopup ("zipDownloadPopup", "출입자 이미지 전체 다운로드", 400, 290);

			//excel all download
			$("#excelAllDownload").on("click", function(event){
	            $("#excelAllDownloadPopup").PopupWindow("open");
	        });
			$("#excelSelDownload").on("click", function(event){
	            $("#excelSelDownloadPopup").PopupWindow("open");
	        });

			$("#zipDownload").on("click", function(event){
	            $("#zipDownloadPopup").PopupWindow("open");
	            $("#fzipdownresn").focus();
	        });

			//search
			if('${userInfoVO.srchFuid}' == 'fuid') $('#srchFuid').prop("checked",true).trigger('change');
			if('${userInfoVO.srchFunm}' == 'funm') $('#srchFunm').prop("checked",true).trigger('change');
			if('${userInfoVO.srchCardNum}' == 'cfcdno') $('#srchCardNum').prop("checked",true).trigger('change');
		});

		//파일추가하기 스크립트
		$(document).ready(function() {
	    	var fileTarget = $('.filebox .upload_hidden');
	    	$(document).on('change', '.filebox .upload_hidden', function(){ // 값이 변경되면
	    		var filename = "";
	    		//if(window.FileReader){ // modern browser
	    			//filename = $(this)[0].files[0].name;
	    		//} else { // old IE
	    			//filename = $(this).val().split('/').pop().split('\\').pop(); // 파일명만 추출
	    		//}
	    		if($(this)[0].files.length == 0){
	    			filename = "";
	    		}else{
	    			filename = $(this)[0].files[0].name;
	    		}
	    		// 추출한 파일명 삽입
	    		$(this).siblings('.upload_name').val(filename);
	    	});
	    });

		function cancel(){
			$("#attachFile").val("");
			$("#attachFileNm").val("");
			$("input[name=excelColumn]").prop("checked", false);
			$("#fzipdownresn").val("");
			$("input[name=fexdownresn]").val("");
			$("input[name=excelImgYn]").prop("checked", false);
			$("input[name=checkAll]").prop("checked", false);
			$("#checkAllLst").prop("checked", false);
			$("#hidExcelImgYn").val("");
			$("#hidFuLst").val("");
			$("input[name=checkbx]").prop("checked", false);
			$("#hidAllLst").val("");

			$("#zipDownloadPopup").PopupWindow("close");
			$("#excelAllDownloadPopup").PopupWindow("close");
			$("#excelSelDownloadPopup").PopupWindow("close");
		}

		function excelUpload(){
			var afile = $("#attachFile").val();
			if(afile == ""){
				alert("엑셀파일을 첨부해주세요.");
				$("#attachFile").focus();
				return;
			}

			var fileExt = afile.slice(afile.lastIndexOf(".")+1).toLowerCase();

			if(fileExt != "xls" && fileExt != "xlsx"){
				alert("엑셀파일만 업로드 가능합니다.");
				$("#attachFileNm").val("");
				$("#attachFile").val("");
				return;
			}

			showLoading();
			var formData = new FormData(document.getElementById('fmExcel'));

			$.ajax({
				url: "<c:url value='/userInfo/excelUserSave.do' />",
				data: formData,
				dataType:'json',
				processData: false,
				contentType: false,
				type: "POST",
				success: function(response){
					location.reload();
				},
				error: function (jqXHR){
					alert(jqXHR.responseText);
				}
			});

		}

		function fnResnChk (pchk) {
			var downresn = pchk.val();
			if(fnIsEmpty(downresn) || downresn.length < 3) {
				alert("다운로드 사유를 3자 이상 입력하여 주십시오.");
				pchk.focus();
				return false;
			}
			$("#fdownresn").val(downresn);
			return true;
		}

		function excelFormDown() {
			location.href = "/userInfo/excelFormDown.do";
		}

		function timeZoneSave() {
			var fuidCheckedCnt = $("input[name=fuidCheck]:checked").length;
			console.log(fuidCheckedCnt);
			if(fuidCheckedCnt == 0 ){
				alert("먼저 타임존을 등록할 출입자를 선택하세요.");
				return;
			}
			if(fuidCheckedCnt > 1){
				alert("타임존은 한명씩 등록이 가능합니다.");
				return;
			}
			if(fuidCheckedCnt == 1){
				//openPopup('/userInfo/userAddPopup.do','출입자정보',1000,670)
			}
		}

		function zipDownload () {
			if(!fnResnChk($("#fzipdownresn"))) return;
			f = document.frmSearch;
			cancel();
			f.action = "/userInfo/getZipImageDown.do";
			f.submit();
		}

		function fnTableData () {
			var tdArr = new Array();
			var checkbox = $("input[name=checkbx]:checked");
			checkbox.each(function(i) {
				var tr = checkbox.parent().parent().eq(i);
				var td = tr.children();
				tdArr.push({"fuid":td.eq(1).text(),"fcarno":td.eq(4).text(), "fpartcd1":td.eq(10).text()});
			});
			return tdArr;
		}

		//체크된 사용자 수정
		function fnChkUserSave () {
			var tdArr = new Array();
			var checkbox = $("input[name=checkbx]:checked");
			if(checkbox.length < 1) {
				alert("수정할 사용자를 선택하여 주십시오.");
				return;
			}
			var tdArr = fnTableData ();
			if(fnIsEmpty(tdArr)) {
				alert("수정할 사용자가 확인되지 않습니다.");
				return;
			}
			var frmPop = document.frmPopup;
			var url = "/userInfo/chkUserEditPopup.do";
			window.open("","popupView", "scrollbars=yes, toolbar=no, menubar=no, resizable=no, status=no, location=no, width=700, height=440, left=" + (screen.availWidth/2-230) +", top="+(screen.availHeight/2-335));
			frmPop.action = url;
			frmPop.target = "popupView";
			frmPop.fuLst.value = JSON.stringify(tdArr);
			frmPop.submit();
		}

		function fnDownLogPop () {
			openPopup('/logInfo/downloadLogPopup.do','다운로드이력목록', 900, 640);
		}

		function selZipDownload () {
			//선택된 사용자 이미지 다운로드 준비
			var tdArr = new Array();
			var checkbox = $("input[name=checkbx]:checked");
			if(checkbox.length < 1) {
				alert("이미지를 다운로드 할 사용자를 선택하여 주십시오.");
				return;
			}
			var tdArr = fnTableData ();
			if(fnIsEmpty(tdArr)) {
				alert("이미지를 다운로드 할 사용자가 확인되지 않습니다.");
				return;
			}
			showLoading();
			$.ajax({
				url: "<c:url value='/userInfo/getSelZipImageDown.do' />",
				data: {"chkArr":JSON.stringify(tdArr)},
				dataType:'json',
				processData: false,
				contentType: false,
				type: "POST",
				success: function(response){
					location.reload();
				},
				error: function (jqXHR){
					alert(jqXHR.responseText);
				}
			});
		}

		function selectExcelDownload(pty) {
			var divObj = null;
			if(pty=="A") {
				divObj = $("#excelAllDownloadPopup");
			} else {
				divObj = $("#excelSelDownloadPopup");
			}
			if(!fnResnChk(divObj.find("input[name=fexdownresn]"))) return;

			f = document.frmSearch;
			var chkValueArray= "";
			var chkTextArray= "";
			divObj.find ("input[name=excelColumn]:checked").each(function(i) {
				var chkValue = $(this).val();
				var chkText = $(this).parent().text();

				if(i==0) {
					chkValueArray = chkValueArray + chkValue + " as CELL" + (i+1);
					chkTextArray = chkTextArray + chkText;
				}else{
					chkValueArray = chkValueArray + "," + chkValue + " as CELL" + (i+1);
					chkTextArray = chkTextArray + "," + chkText;
				}
			});
			if(pty=="A" && chkValueArray.length == 0) {
				alert("한개 이상 체크를 하셔야 엑셀로 다운로드가 가능합니다.");
				return;
			} else {
				$("#excelDown"+pty).attr("data-dismiss","modal");
				$("#excelDown"+pty).attr("aria-label","Close");
			}
			$("#chkValueArray").val(chkValueArray);
			$("#chkTextArray").val(chkTextArray);
			console.log($("#chkValueArray").val());
			console.log($("#chkTextArray").val());

			//이미지 여부
			if(divObj.find("input[name=excelImgYn]").prop("checked")) $("#hidExcelImgYn").val("Y");
			else $("#hidExcelImgYn").val("");

			//전체 체크 여부
			if(pty=="A") {
				$("#hidAllLst").val("Y");
			} else {
				$("#hidAllLst").val("");
				var tdArr = fnTableData ();		//checkbox get
				if(fnIsEmpty(tdArr)) {
					alert("다운로드 될 출입자를 체크하여 주십시오.");
					return;
				} else {
					$("#hidFuLst").val(JSON.stringify(tdArr));
				}
			}
			exportExcel("frmSearch", "ifm", "<c:url value='/userInfo/excelDownload.do'/>");
			//cancel();
		}

		/*********start test**********/
		var excelUploadProgress = function () {
			//count를 0으로 초기화 해야하는가?
		    $.ajax({
		        url: "/userInfo/excelUploadProgress.do",
		        data: {},
		        dataType :"json",
		        processData: false,
				contentType: false,
				async: false,
				type: "POST",
		        success : function(resultData) {
		        	if(!fnIsEmpty(resultData.rstcnt)) {
		        		var rstDt = resultData.rstcnt;
		        		if( rstDt == 100 ) {
		        			clearInterval(threadRefresh);
		        			excelUploadProgressClear();
		        		}
		        		$("#excelUploadingState").html("<font size = 3><b>"+ rstDt +"% </b></font>");
		 	            document.getElementById('progress').style.width = (rstDt*3.65).toString() +'px';
		        	} else {
		        		clearInterval(threadRefresh);
		 	            excelUploadProgressClear();
		        	}
		        },
		        error: function(e)	{
		        	clearInterval(threadRefresh);
	 	            excelUploadProgressClear();
		        	alert("ERROR excelUploadProgress");
		        }
		    });
		}
		function excelUploadProgressClear() {
			console.log("excelUploadProgressClear");
			$.ajax({
		        url: "/userInfo/excelUploadProgressClear.do",
		        data: {},
		        dataType :"json",
		        processData: false,
				contentType: false,
				type: "POST",
		        success : function(resultData) {
		        	$("#excelUploadingState").html("");
		        	$("#progress").html("");
		        	$("#progress_info").hide();
		        	pageSearch('${pagination.curPage}');
		        },
		        error: function(e) {
		        	$("#excelUploadingState").html("");
		        	$("#progress").html("");
		        	$("#progress_info").hide();
		        	pageSearch('${pagination.curPage}');
		        }
		    });
		}
		function exportExcel(formId, ifmId, url) {
			var objForm = document.getElementById(formId);
			var objIfm = document.getElementById(ifmId);
			var defaultTarget = fnIsEmpty(objForm.getAttribute("target"))?'':objForm.getAttribute("target").replace(/^\s+|\s+$/gm,'');

			var strTarget = "_self";
			if(!fnIsEmpty(defaultTarget))
				strTarget = defaultTarget;
			objForm.target = strTarget;

			objForm.target = objIfm.getAttribute("name");
			objForm.action = url;
			objForm.method = "post";
			objForm.submit()
			setTimeout(function() {
				threadRefresh = setInterval(excelUploadProgress, 100);
			},500);
			$("#progress_info").show();

			//target reset
			var strTarget = "";
			objForm.target = strTarget;
		}
		/*********end test**********/
	</script>
	<iframe name="ifm" id="ifm" frameborder="0" height="0" width="0" scrolling="no"></iframe>
	<!-- progress bar -->
    <div id="progress_info" class="progress_info" style="display: none;">
        <div id="progress" class="progress" style="background-color: blue; width: 0px;"></div>
        <p id= "excelUploadingState" class="excelUploadingState"></p>
    </div>
	<!--검색박스 -->
	<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
		<input type="hidden" id="chkValueArray" name="chkValueArray" value=""/>
		<input type="hidden" id="chkTextArray" name="chkTextArray" value="" >
		<input type="hidden" id="fdownresn" name="fdownresn" value=""/>
		<input type="hidden" id="hidExcelImgYn" name="hidExcelImgYn" value=""/>
		<input type="hidden" id="hidAllLst" name="hidAllLst" value=""/>
		<input type="hidden" id="hidFuLst" name="hidFuLst"/>
		<!-- sort -->
		<input type="hidden" id="hidSortName" name="hidSortName" value="${userInfoVO.hidSortName}"/>
		<input type="hidden" id="hidSortNum" name="hidSortNum" value="${userInfoVO.hidSortNum}"/>
		<div class="search_box mb_20">
			<div class="search_in_bline">
				<div class="comm_search mr_20">
					<select name="srchCond" id="srchCond" size="1" class="w_100px input_com">
						<c:forEach items="${centerCombo}" var="cCombo" varStatus="status">
                        	<option value='<c:out value="${cCombo.fkind3}"/>' <c:if test="${userInfoVO.srchCond eq cCombo.fkind3}">selected</c:if>><c:out value="${cCombo.fvalue}"/></option>
                        </c:forEach>
					</select>
				</div>
				<div class="ch_box  mr_20">
					<input type="checkbox" id="srchFuid" name="srchFuid" class="checkbox" value="fuid" <c:if test="${userInfoVO.srchFuid eq 'fuid'}">checked</c:if>>
					<label for="srchFuid"> FID</label>
					<input type="checkbox" id="srchFunm" name="srchFunm" class="checkbox" value="funm" <c:if test="${userInfoVO.srchFunm eq 'funm'}">checked</c:if>>
					<label for="srchFunm" class="ml_10"> 이름</label>
					<input type="checkbox" id="srchCardNum" name="srchCardNum" class="checkbox" value="cfcdno" <c:if test="${userInfoVO.srchCardNum eq 'cfcdno'}">checked</c:if>>
					<label for="srchCardNum" class="ml_10"> 카드번호</label>
				</div>
				<div class="comm_search mr_20">
					<input type="text" class="w_200px input_com" id="srchCondWord" name="srchCondWord" value='<c:out value="${userInfoVO.srchCondWord}"/>' />
				</div>
				<div class="ch_box  mr_20">
					<input type="checkbox" id="srchFexp" name="srchFexp" class="checkbox" value="N" <c:if test="${userInfoVO.srchFexp eq 'N'}">checked</c:if>>
					<label for="srchFexp" class="ml_10"> 유효기간 만료자 제외</label>
				</div>
				
			</div>
			
			<div class="search_in_bline">
				<div class="comm_search mr_20">
					<div class="title ">출입자타입</div>
					<select name="srchFutype" id="srchFutype" size="1" class="w_100px input_com">
						<option value="">전체</option>
                        <c:forEach items="${userType}" var="uType" varStatus="status">
                        	<option value='<c:out value="${uType.fvalue}"/>' <c:if test="${uType.fvalue eq userInfoVO.srchFutype}">selected</c:if>><c:out value="${uType.fkind3}"/></option>
                        </c:forEach>
					</select>
				</div>
				<div class="comm_search">
					<div class="title ">사진등록</div>
					<select name="srchFbioyn" id="srchFbioyn" size="1" class="w_100px input_com">
						<option value="ALL">전체</option>
			            <option value="Y" <c:if test="${userInfoVO.srchFbioyn eq 'Y'}">selected</c:if>>등록</option>
			            <option value="N" <c:if test="${userInfoVO.srchFbioyn eq 'N'}">selected</c:if>>미등록</option>
					</select>
				</div>
				<%-- <div class="comm_search">
					<div class="title ">카드만료여부</div>
					<select name="srchFexpireyn" id="srchFexpireyn" size="1" class="w_100px input_com">
						<option value="ALL">전체</option>
			            <option value="N" <c:if test="${userInfoVO.srchFexpireyn eq 'N'}">selected</c:if>>사용가능</option>
			            <option value="Y" <c:if test="${userInfoVO.srchFexpireyn eq 'Y'}">selected</c:if>>기한만료</option>
					</select>
				</div> --%>
				<div class="comm_search ml_60">
					<div class="search_btn2" title="검색" onclick="userSearch()"></div>
				</div>
				<div class="comm_search ml_65">
					<button type="button" class="comm_btn" id="reset" >초기화</button>
				</div>
			</div>
		</div>
		<div class="totalbox">
			<div class="txbox">
				<b class="fl mr_10">전체 : <c:out value="${pagination.totRecord}"/>건</b>
				<!-- 건수 -->
            	<select name="srchRecPerPage" id="srchRecPerPage" class="input_com w_80px">
                	<option value="30"  <c:if test="${userInfoVO.srchCnt eq '30'}">selected</c:if>>30</option>
                  	<option value="50"  <c:if test="${userInfoVO.srchCnt eq '50'}">selected</c:if>>50</option>
                  	<option value="100"  <c:if test="${userInfoVO.srchCnt eq '100'}">selected</c:if>>100</option>
                  	<option value="300"  <c:if test="${userInfoVO.srchCnt eq '300'}">selected</c:if>>300</option>
                  	<option value="500"  <c:if test="${userInfoVO.srchCnt eq '500'}">selected</c:if>>500</option>
                  	<option value="1000"  <c:if test="${userInfoVO.srchCnt eq '1000'}">selected</c:if>>1000</option>
                  	<option value="1500" <c:if test="${userInfoVO.srchCnt eq '1500'}">selected</c:if>>1500</option>
                  	<option value="2000" <c:if test="${userInfoVO.srchCnt eq '2000'}">selected</c:if>>2000</option>
                  	<option value="2500" <c:if test="${userInfoVO.srchCnt eq '2500'}">selected</c:if>>2500</option>
                  	<option value="3000" <c:if test="${userInfoVO.srchCnt eq '3000'}">selected</c:if>>3000</option>
            	</select>
			</div>
			<div class="r_btnbox  mb_10">
				<button type="button" class="btn_middle color_basic" onclick="openPopup('/userInfo/userAddPopup.do','출입자정보',900,650)">신규등록</button>
				<button type="button" class="btn_excel" data-toggle="modal" id="excelAllDownload">전체 엑셀 다운로드</button>
				<button type="button" class="btn_excel" data-toggle="modal" id="excelSelDownload">선택 엑셀 다운로드</button>
				<button type="button" class="btn_excel" data-toggle="modal" id="zipDownload">이미지다운로드</button>
				<button type="button" class="btn_middle color_basic" onclick="fnDownLogPop()">다운로드이력확인</button>
			</div>
		</div>
	</form>
	<!--//검색박스 -->
	<!--------- 목록--------->
	<div class="com_box">
		<!--테이블 시작 -->
		<div class="tb_outbox">
			<table class="tb_list">
				<col width="80px" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<col width="" />
				<thead id="thead">
					<tr id="trUserMgt">
						<th class="wd80p"><input id="checkAllLst" type="checkbox">
							<!-- &nbsp;전체 -->
						</th>
						<th id="thfuid">
							<input type="hidden" id="sortfuid" name="thsortnm" />
							FID
							<span id="spfuid" style="float: right;"></span>
						</th>
						<th id="thfunm">
							<input type="hidden" id="sortfunm" name="thsortnm" />
							이름
							<span id="spfunm" style="float: right;"></span>
						</th>
						<th id="thfbioyn">
							<input type="hidden" id="sortfbioyn" name="thsortnm" />
							사진
							<span id="spfbioyn" style="float: right;"></span>
						</th>
						<th id="thcfcdno">
							<input type="hidden" id="sortcfcdno" name="thsortnm" />
							카드번호
							<span id="spcfcdno" style="float: right;"></span>
						</th>
						<th id="thfutypenm">
							<input type="hidden" id="sortfutypenm" name="thsortnm" />
							출입자타입
							<span id="spfutypenm" style="float: right;"></span>
						</th>
						<th id="thcfstatusnm">
							<input type="hidden" id="sortcfstatusnm" name="thsortnm" />
							카드상태
							<span id="spcfstatusnm" style="float: right;"></span>
						</th>
						<th id="thfauthtypenm">
							<input type="hidden" id="sortfauthtypenm" name="thsortnm" />
							권한타입
							<span id="spfauthtypenm" style="float: right;"></span>
						</th>
						<th id="thcfsdt">
							<input type="hidden" id="sortcfsdt" name="thsortnm" />
							카드시작일
							<span id="spcfsdt" style="float: right;"></span>
						</th>
						<th id="thcfedt">
							<input type="hidden" id="sortcfedt" name="thsortnm" />
							카드만료일
							<span id="spcfedt" style="float: right;"></span>
						</th>
						<th style="display: none;">센터</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${userInfoList == null || fn:length(userInfoList) == 0}">
							<tr>
								<th class="h_35px" colspan="11">조회 목록이 없습니다.</th>
							</tr>
						</c:when>
						<c:otherwise>
							<c:forEach items="${userInfoList}" var="uList" varStatus="status">
								<tr>
									<td class="wd80p"><input name="checkbx" type="checkbox"></td>
									<td><a href="#none" onclick="openPopup('/userInfo/userEditPopup.do?fuid=<c:out value="${uList.fuid}"/>&fpartcd1=<c:out value="${uList.fpartcd1}"/>&sfcdno=<c:out value="${uList.cfcdno}"/>','출입자정보확인',900,630)"><c:out value="${uList.fuid}"/></a></td>
									<td><a href="#none" onclick="openPopup('/userInfo/userEditPopup.do?fuid=<c:out value="${uList.fuid}"/>&fpartcd1=<c:out value="${uList.fpartcd1}"/>&sfcdno=<c:out value="${uList.cfcdno}"/>','출입자정보확인',900,630)"><c:out value="${uList.funm}"/></a></td>
									<td>
									<c:choose>
										<c:when test="${uList.fbioyn eq 'Y'}">
											등록
										</c:when>
										<c:otherwise>
											-
										</c:otherwise>
									</c:choose>
									</td>
									<td><c:out value="${uList.cfcdno}"/></td>
									<td><c:out value="${uList.futypenm}"/></td>
									<td><c:out value="${uList.cfstatusnm}"/></td>
									<td><c:out value="${uList.fauthtypenm}"/></td>
									<td><c:out value="${uList.cfsdt}"/></td>
									<td><c:out value="${uList.cfedt}"/></td>
									<td style="display: none;"><c:out value="${uList.fpartcd1}"/></td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
		<!--------- //목록--------->
		<!-- paging -->
		<jsp:include page="/WEB-INF/jsp/cubox/common/pagination.jsp" flush="false"/>
	</div>

	<!-- modal : 전체 이미지 다운로드 -->
	<div id="excelAllDownloadPopup" class="example_content">
		<div class="popup_box">
			<div class="search_box_popup" style="border-bottom: 0px">
				<div class="search_in">
					<input type="checkbox" id="checkAllA" name="checkAll" class="checkbox">
					<label for="checkAllA" class="ml_10"> 전체</label>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<div class="mr_m3">
						<input type="checkbox" name="excelColumn" id="funm" value="funm" class="checkbox">
						<label for="funm" class="ml_10"> 이름</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfcdno" value="cfcdno" class="checkbox">
						<label for="cfcdno" class="ml_10"> 카드번호</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="futypenm" value="futypenm" class="checkbox">
						<label for="futypenm" class="ml_10"> 출입자타입</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfstatusnm" value="cfstatusnm" class="checkbox">
						<label for="cfstatusnm" class="ml_10"> 카드상태</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="fauthtypenm" value="fauthtypenm" class="checkbox">
						<label for="fauthtypenm" class="ml_10"> 권한타입</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfsdt" value="cfsdt" class="checkbox">
						<label for="cfsdt" class="ml_10"> 카드시작일</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfedt" value="cfedt" class="checkbox">
						<label for="cfedt" class="ml_10"> 카드만료일</label>
					</div>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<input type="checkbox" name="excelImgYn" id="excelImgYnA" value="excelImgYn" class="checkbox">
					<label for="excelImgYnA" class="ml_10"> 이미지 추가 여부</label>
				</div>
			</div>
			<div class="search_box_popup" style="border-bottom: 0px">
				<div class="search_in">
					<label style="font-size: 14px;">➠ 다운로드 사유를 입력하여 주십시오.</label>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<input type="text" id="fexdownresna" name="fexdownresn" class="w_300px input_com" maxlength="100" placeholder="">
				</div>
			</div>

			<div class="c_btnbox">
				<div style="display: inline-block;">
					<button type="button" class="comm_btn mr_5" id="excelDownA" onclick="selectExcelDownload('A');">다운로드</button>
					<button type="button" class="bk_color comm_btn" title="취소" onclick="cancel();">취소</button>
				</div>
			</div>
		</div>
	</div>


	<!-- modal : 선택 이미지 다운로드 -->
	<div id="excelSelDownloadPopup" class="example_content">
		<div class="popup_box">
			<div class="search_box_popup" style="border-bottom: 0px">
				<div class="search_in">
					<input type="checkbox" id="checkAllS" name="checkAll" class="checkbox">
					<label for="checkAllS" class="ml_10"> 전체</label>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<div class="mr_m3">
						<input type="checkbox" name="excelColumn" id="funms" value="funm" class="checkbox">
						<label for="funms" class="ml_10"> 이름</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfcdnos" value="cfcdno" class="checkbox">
						<label for="cfcdnos" class="ml_10"> 카드번호</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="futypenms" value="futypenm" class="checkbox">
						<label for="futypenms" class="ml_10"> 출입자타입</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfstatusnms" value="cfstatusnm" class="checkbox">
						<label for="cfstatusnms" class="ml_10"> 카드상태</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="fauthtypenms" value="fauthtypenm" class="checkbox">
						<label for="fauthtypenms" class="ml_10"> 권한타입</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfsdts" value="cfsdt" class="checkbox">
						<label for="cfsdts" class="ml_10"> 카드시작일</label>
					</div>
					<div class="mr_m3 mt_15">
						<input type="checkbox" name="excelColumn" id="cfedts" value="cfedt" class="checkbox">
						<label for="cfedts" class="ml_10"> 카드만료일</label>
					</div>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<input type="checkbox" name="excelImgYn" id="excelImgYnS" value="excelImgYn" class="checkbox">
					<label for="excelImgYnS" class="ml_10"> 이미지 추가 여부</label>
				</div>
			</div>
			<div class="search_box_popup" style="border-bottom: 0px">
				<div class="search_in">
					<label style="font-size: 14px;">➠ 다운로드 사유를 입력하여 주십시오.</label>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<input type="text" id="fexdownresns" name="fexdownresn" class="w_300px input_com" maxlength="100" placeholder="">
				</div>
			</div>

			<div class="c_btnbox">
				<div style="display: inline-block;">
					<button type="button" class="comm_btn mr_5" id="excelDownS" onclick="selectExcelDownload('S');">다운로드</button>
					<button type="button" class="bk_color comm_btn" title="취소" onclick="cancel();">취소</button>
				</div>
			</div>
		</div>
	</div>

	<!-- modal : zip 이미지 다운로드 -->
	<div id="zipDownloadPopup" class="example_content">
		<div class="popup_box">
			<div class="search_box_popup" style="border-bottom: 0px">
				<div class="search_in">
					<label style="font-size: 14px;">➠ 다운로드 사유를 입력하여 주십시오.</label>
				</div>
			</div>
			<div class="search_box_popup mb_20">
				<div class="search_in">
					<input type="text" id="fzipdownresn" name="fzipdownresn" class="w_300px input_com" maxlength="100" placeholder="">
				</div>
			</div>

			<div class="c_btnbox">
				<div style="display: inline-block;">
					<button type="button" class="comm_btn mr_5" id="zipDown" onclick="zipDownload();">다운로드</button>
					<button type="button" class="bk_color comm_btn" title="취소" onclick="cancel();">취소</button>
				</div>
			</div>
		</div>
	</div>

</body>
