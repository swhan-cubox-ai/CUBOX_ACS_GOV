<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript" src="/js/charts/loader.js"></script>
<script type="text/javascript" src="/js/charts/charts.min.js"></script>
<script type="text/javascript" src="/js/charts/utils.js"></script>
<style>
canvas {
	-moz-user-select: none;
	-webkit-user-select: none;
	-ms-user-select: none;
}
</style>
<script type="text/javascript" src="/js/jquery.simple-calendar.js"></script>
<script type="text/javascript">
	$(function() {
		// 숫자만 입력가능
		$(".onlyNumber").keyup(function(event){
			if (!(event.keyCode >=37 && event.keyCode<=40)) {
				var inputVal = $(this).val();
				$(this).val(inputVal.replace(/[^0-9]/gi,''));
			}
		});	
		
		//자동새로고침 증가
		$("#btnRefreshIncrease").click(function(){
			if(parseInt($("#intervalSecond").val()) < 999) {
				$("#intervalSecond").val( parseInt($("#intervalSecond").val())+1);
			}
		});
		
		reload();			
	});
	
	var threadRefresh;

	//새로고침
	function reload() {
		//chart
		$.ajax({
			type:"GET",
			url:"<c:url value='/main/getMainStatus.do' />",
			data:{},
			dataType: "json",
			success:function(result) {
				var arrStatDt = [];
				arrStatDt.push(['Month', '출입이력', '출입실패', '출입성공' ]);
				if(result != null && result.statLit != null) {
					/* for(var i in result.statLit) {
						var arr = [result.statLit[i].exp_day, parseInt(result.statLit[i].tot_log_cnt), parseInt(result.statLit[i].fail_log_cnt), parseInt(result.statLit[i].success_log_cnt), parseInt(result.statLit[i].user_log_cnt)];
						arrStatDt.push(arr);
					}
					fnChartDraw (arrStatDt); */
					fnChartCanvasDraw (result.statLit);
				} else {

				}
			}
		});

		//log list
		$.ajax({
			type:"GET",
			url:"<c:url value='/main/getMainLogList.do' />",
			data:{},
			dataType: "json",
			success:function(result) {
				fnLogInfoListSet(result);
			}
		});

		//log cnt
		$.ajax({
			type:"GET",
			url:"<c:url value='/main/getMainLogCnt.do' />",
			data:{},
			dataType: "json",
			success:function(result) {
				fnLogInfoCntSet(result);
			}
		});
		
		//notice list
		$.ajax({
			type:"GET",
			url:"<c:url value='/main/getMainNoticeList.do' />",
			data:{},
			dataType: "json",
			success:function(result) {
				fnNoticeInfoListSet(result);
			}
		});
		
		//q&a list
		$.ajax({
			type:"GET",
			url:"<c:url value='/main/getMainQaList.do' />",
			data:{},
			dataType: "json",
			success:function(result) {
				fnQaListSet(result);
			}
		});
		
		//근태관리 달력
		$.ajax({
			type : "POST"
			, url : "/diligenceAndLazinessManagement/getWorkEventList.do"
			, data : { "nowMonth" : "" }
			, dataType : "JSON"
			, success : function (data) {
				var event = [];
				$(".today_tx").html( "<em>" + moment().format('YYYY.MM.DD') + "</em>");
				
				$.each(data.workEventList, function(index, item){
					if(moment().format('YYYYMMDD') == item.fstdde){
						$(".today_tx").html( $(".today_tx").html() + "<br>" + item.title );
					}
					
					event.push({
						startDate: new Date(moment(item.fstdde).format('YYYY-MM-DD'))
						, endDate: new Date(moment(item.fstdde).format('YYYY-MM-DD'))
						, summary: item.title
					});
				});
				
				$("#container").simpleCalendar({///참고 : https://github.com/brospars/simple-calendar
					months: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월']
					, days: ['일', '월', '화', '수', '목', '금', '토']
					, displayYear : true// 헤더에 연도 표시
					, fixedStartDay: 0// 주는 항상 월요일 또는 숫자로 설정된 요일에 시작됩니다. 0 = 일요일, 7 = 토요일, false = 월은 항상 해당 월의 첫 번째 날부터 시작
					, disableEmptyDetails: true// 빈 날짜 세부 정보 표시 활성화
					, events: event
					, onEventCreate : function ( $el ) {// HTML 이벤트가 생성 될 때 콜백이 실행 됨-$ (this) .data ( 'event') 참조 
						$el.find(".event-hour").text("");//시간 지우기
						
						var text = $el.find(".event-date").text().split("-");//시작일과 종료일 분리
						
						$el.find(".event-date").text(
							moment(text[0]).format('YYYY-MM-DD')
						);
					}
				});
			}
		});
	}
	
	function fnChartCanvasDraw (data) {
		var dtLabel = [];
		var data1 = [];
		var data2 = [];
		var data3 = [];
		var data4 = [];
		for(var i in data) {
			dtLabel.push(data[i].exp_day);
			data1.push(parseInt(data[i].tot_log_cnt));
			data2.push(parseInt(data[i].fail_log_cnt));
			data3.push(parseInt(data[i].success_log_cnt));
			data4.push(parseInt(data[i].user_log_cnt));
		}
		var color = Chart.helpers.color;
		var chartData = {
				labels: dtLabel,
				datasets: [{
					type: 'line',
					label: '출입이력',
					borderColor: '#173d93',		//window.chartColors.blue
					borderWidth: 2,
					fill: false,
					data: data1
				}, {
					type: 'bar',
					label: '출입성공',
					backgroundColor: color('#016879').alpha(0.8).rgbString(),	//window.chartColors.green
					data: data3,
					borderWidth: 2
				}, {
					type: 'bar',
					label: '출입실패',
					backgroundColor: color('#c13838').alpha(0.8).rgbString(),		//window.chartColors.red
					data: data2,
					/* borderColor: 'white', */
					borderWidth: 2
				}
		/* 		, {
					type: 'bar',
					label: '단말기출입자',
					backgroundColor: '#a7c846',	//window.chartColors.gray
					data: data4,
					borderWidth: 2
				} */
				]
			};
			//chartData.datasets = chartData;
			var ctx = document.getElementById('canvas').getContext('2d');
			var myMixedChart = new Chart(ctx, {
				type: 'bar',
				data: chartData,
				options: {
					/* indexAxis: 'y', */
					responsive: true,				/*자동크기 조정*/
					maintainAspectRatio : false, 	/*가로세로 비율*/
					elements: {
						line: {
							tension: 0.000001	/*line 곡선 조절*/
						}
					},
					legend: {
						position: 'left',
						align : "end",
						labels: {
			                fontSize : 11,
			                padding : 5,
			            }
					},
					title: {
						display: false,
						text: '출입이력현황'
					},
					tooltips: {
						mode: 'index',
						intersect: true
					},
					layout: {
			            padding: {
			                left: 0,
			                right: 0,
			                top: 0,
			                bottom: 10
			            }
			        }
				}
			});
			//myMixedChart.update();
	}

	function fnLogInfoCntSet (data) {
		if(data == null) {
			$("#visitReqCnt").html("0");
			$("#dayCnt").html("0");
			$("#dayUserCnt").html("0");
			$("#obsCnt").html("0");
			$("#inmateCnt").html("0");
		} else {
			$("#visitReqCnt").html(data.visitReqCnt==null||data.visitReqCnt==""?"0":data.visitReqCnt);
			$("#dayCnt").html(data.dayCnt==null||data.dayCnt==""?"0":data.dayCnt);
			$("#dayUserCnt").html(data.dayUserCnt==null||data.dayUserCnt==""?"0":data.dayUserCnt);
			$("#obsCnt").html(data.obsCnt==null||data.obsCnt==""?"0":data.obsCnt);
			$("#inmateCnt").html(data.inmateCnt==null||data.inmateCnt==""?"0":data.inmateCnt);
		}
	}

	function fnLogInfoListSet (data) {
		if(data == null || data.logInfoList == null) {
			$("#logListBody").html("<tr><th class='h_35px' colspan='7'>조회 목록이 없습니다.</th></tr>");
		} else {
			data = data.logInfoList;
			var str = "";
			for(var i in data) {
				str += "<tr>";
				str += "<td>"+(data[i].fevttm!=null && data[i].fevttm.length>16?data[i].fevttm.substr(0,16):data[i].fevttm)+"</td>";
				str += "<td>"+data[i].flname+"</td>";
				str += "<td>"+data[i].funm+"</td>";
				str += "<td>"+data[i].fuid+"</td>";
				//if(data[i].fcarno != null && data[i].fcarno != "") {
				//	str += "<td>"+data[i].fcarno+"</td>";
				//} else {
				//	str += "<td>"+data[i].fcdno+"</td>";
				//}
				str += "<td>"+data[i].cfstatus+"</td>";
				str += "<td><div class='tdbtnbox'>";
				if(data[i].fvalue1 != null && data[i].fvalue1 == "성공") {
					str += "<div class='st_ing1'>"+data[i].fvalue1+"</div>";
				} else {
					str += "<div class='st_ing2'>"+data[i].fvalue1+"</div>";
				}
				str += "</div></td>";
				str += "<td>"+data[i].fvalue2+"</td>";
				str += "</tr>";
			}
			$("#logListBody").html(str);
		}
	}
	
	function fnNoticeInfoListSet (data) {
		if(data == null || data.noticeList == null) {
			$("#noticeListBody").html("<tr><th class='h_35px' colspan='7'>조회 목록이 없습니다./th></tr>");
		} else {
			data = data.noticeList;
			var str2 = "";
			for(var i in data) {
				str2 += "<tr onclick='fnBoardDetail("+data[i].nttId+",00000000000000000001)'>";
				str2 += "<td>"+data[i].nttSj.substr(0,45)+(data[i].nttSj!=null && data[i].nttSj.length>45?"..":"")+"</td>";
				//str2 += "<td>"+data[i].nttCn.replace("<p>","").replace("</p>","").replace("<br>","").substr(0,25)+(data[i].nttCn!=null && data[i].nttCn.length>30?"...":"")+"</td>";
				str2 += "<td>" + data[i].nttCn.replace(/(<([^>]+)>)/ig, "").substr(0,25) + (data[i].nttCn != null && data[i].nttCn.length > 30 ? "..." : "") + "</td>";
				str2 += "<td>"+data[i].registNm+"</td>";
				str2 += "<td>"+(data[i].registDt!=null && data[i].registDt.length>16?data[i].registDt.substr(0,16):data[i].registDt)+"</td>";
				str2 += "</tr>";
			}
			
			$("#noticeListBody").html(str2);
		}
	}
	
	function fnQaListSet (data) {
		if(data == null || data.qaList == null) {
			$("#qaListBody").html("<tr><th class='h_35px' colspan='7'>조회 목록이 없습니다.</th></tr>");
		} else {
			data = data.qaList;
			var str2 = "";
			for(var i in data) {
				str2 += "<tr onclick='fnBoardDetail("+data[i].nttId+",00000000000000000002)'>";
				str2 += "<td>"+data[i].nttSj.substr(0,45)+(data[i].nttSj!=null && data[i].nttSj.length>45?"..":"")+"</td>";
				//str2 += "<td>"+data[i].nttCn.replace("<p>","").replace("</p>","").replace("<br>","").substr(0,25)+(data[i].nttCn!=null && data[i].nttCn.length>30?"...":"")+"</td>";
				str2 += "<td>" + data[i].nttCn.replace(/(<([^>]+)>)/ig, "").substr(0,25) + (data[i].nttCn != null && data[i].nttCn.length > 30 ? "..." : "") + "</td>";
				str2 += "<td>"+data[i].registNm+"</td>";
				str2 += "<td>"+(data[i].registDt!=null && data[i].registDt.length>16?data[i].registDt.substr(0,16):data[i].registDt)+"</td>";
				str2 += "</tr>";
			}
			$("#qaListBody").html(str2);
		}
	}

	function fnChartDraw (arrStatDt) {
		google.charts.load('current', {'packages':['corechart']});
		google.charts.setOnLoadCallback(function () {
			var data = google.visualization.arrayToDataTable(arrStatDt);
			var options = {
				title : '',
				vAxis: {title: ''},
				hAxis: {title: ''},
				seriesType: 'bars',
					colors: ['#173d93', '#77b8bd', '#016879', '#a7c846'],
					series: {0: {type: 'line'}}
			};
			var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
		        chart.draw(data, options);
		});
	}

	function autoRefresh(value) {
		var reloadYn = $("#reloadYn");
		if(value == "N"){
			reloadYn.val("Y");
			var sec = parseInt($("#intervalSecond").val());
			alert(sec +  "초마다 자동 새로고침이 시작됩니다.");
			$("#btnReloadYn").val("Y");
			$("#btnReloadYn").html("중지");
			threadRefresh = setInterval(function(){this.reload()}, sec * 1000);
			reload();
		} else {
			reloadYn.val("N");
			alert("자동 새로고침이 중지되었습니다.");
			$("#btnReloadYn").val("N");
			$("#btnReloadYn").html("시작");
			clearInterval(threadRefresh);
		}
	}
	
	function fnGateLog(page){
		f = document.frmSearch;
		f.action = "/logInfo/logMngmt.do?srchPage=" + page;
		f.submit();
		
	}
	
	function fnBoardList(bbsId){
		f = document.frmSearch;
		f.action = "/boardInfo/"+pad(bbsId,20)+"/list.do";
		f.submit();
		
	}
	
	
	function fnBoardDetail(nttId, bbsId){
		f = document.frmSearch;
		
		$("input:hidden[id=hidNttId]").val(nttId);
		f.action = "/boardInfo/"+pad(bbsId,20)+"/detail.do";
		f.submit();
		
	}
	function pad(n, width) {
		  n = n + '';
		  return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;

	}

</script>
<div class="main_a">
	<div class="st_box">
		<div class="icon">
			<img src="/img/main/m_icon01.png" alt="" />
		</div>
		<div class="tx1">방문자신청</div>
		<div class="tx2">
			<em id="visitReqCnt">0</em>명
		</div>
	</div>
	<div class="st_box">
		<div class="icon">
			<img src="/img/main/m_icon03.png" alt="" />
		</div>
		<div class="tx1">일일출입현황</div>
		<div class="tx2">
			<em id="dayCnt">0</em>건
		</div>
	</div>
	<div class="st_box">
		<div class="icon">
			<img src="/img/main/m_icon01.png" alt="" />
		</div>
		<div class="tx1">일일출입인원</div>
		<div class="tx2">
			<em id="dayUserCnt">0</em>명
		</div>
	</div>
	<div class="st_box">
		<div class="icon">
			<img src="/img/main/m_icon02.png" alt="" />
		</div>
		<div class="tx1">단말기장애현황</div>
		<div class="tx2">
			<em id="obsCnt">0</em>건
		</div>
	</div>
	<div class="st_box">
		<div class="icon">
			<img src="/img/main/m_icon01.png" alt="" />
		</div>
		<div class="tx1">재실인원</div>
		<div class="tx2">
			<em id="inmateCnt">0</em>명
		</div>
	</div>
	<div class="w_200px"></div>
	<form id="frmSearch" name="frmSearch" method="post" onsubmit="return false;">
	<input type="hidden" id="hidNttId" name="hidNttId">
		<input type="hidden" id="reloadYn" name="reloadYn" value='<c:out value="${reloadYn}"/>'>
		<div class="w_180px">
			<div class="comm_search mr_5">
	            <input type="text" class="w_90px input_com onlyNumber" id="intervalSecond" name="intervalSecond" value="5" maxlength="3"  placeholder="">
	            <div class="plus_btn" id="btnRefreshIncrease"></div>
	        </div>
	        <button type="button" id="btnReloadYn" value='N' onclick="autoRefresh(this.value)" class="btn_middle color_gray">시작</button>
		</div>
	</form>
	<!-- <div class="st_box">
	    <div class="icon"><img src="/img/main/m_icon04.png" alt=""/></div>
	    <div class="tx1">일간근태현황</div>
		   <div class="tx2"><em>25</em>명</div>
	  </div>
		<div class="st_box">
	    <div class="icon"><img src="/img/main/m_icon05.png" alt=""/></div>
	    <div class="tx1">월간근태현황</div>
		   <div class="tx2"><em>25</em>명</div>
	  </div>
		<div class="st_box">
	    <div class="icon"><img src="/img/main/m_icon06.png" alt=""/></div>
	    <div class="tx1">인증이력조회</div>
		   <div class="tx2"><em>25</em>명</div>
	  </div> -->
</div>
<div class="main_b">
	<div class="inbox1" style="width: 600px;">
		<div class="title">
			출입이력 현황
			<!-- <div class="more">
				<img src="/img/main/icon_more.png" alt="" />
			</div> 
			-->
		</div>
		<div class="gr">
			<!-- <div id="chart_div" style="width: 100%; height: 300px;"></div> -->
			<div style="height: 320px; margin-left: 10px;">
				<canvas id="canvas" width="" height="160" style=""></canvas>
			</div>
		</div>
	</div>
	<div class="inbox2">
		<div class="icon"><img src="/img/main/m_icon07.png" alt=""></div>
		<div class="title">단말기 제어관리</div>
		<div class="tx1">단말기별 펌웨어버전을 <br>확인하실 수 있습니다.</div>
		<div class="tx2">today : </div>
		<div class="tx3">5455 </div>
		<div class="time">updates : 2020.06.22입력</div>
	</div>
	<div class="inbox2">
		<div class="icon"><img src="/img/main/m_icon08.png" alt=""></div>
		<div class="title">출입권한관리</div>
		<div class="tx1">단말기별 펌웨어버전을 <br>확인하실 수 있습니다.</div>
		<div class="tx2">today : </div>
		<div class="tx3">5455 </div>
		<div class="time">updates : 2020.06.22입력</div>
	</div>
	<div class="inbox2 mapbg">
		<div class="icon"><img src="/img/main/m_icon09.png" alt=""></div>
		<div class="title">지역관리</div>
		<div class="tx1">단말기별 펌웨어버전을 <br>확인하실 수 있습니다.</div>
		<div class="tx2">today : </div>
		<div class="tx3">5455 </div>
		<div class="time">updates : 2020.06.22입력</div>
	</div>

	
	<div class="inbox7" style="margin-top: 40px;">
		<div class="title">
			출입이력
			<div class="more">
				<img src="/img/main/icon_more.png" alt="" onclick="fnGateLog();"/>
			</div>
		</div>
		<div class="tb_outbox">
			<table class="tb_list_main">
				<col width="15%" />
				<col width="20%" />
				<col width="15%" />
				<col width="10%" />
				<col width="10%" />
				<col width="10%" />
				<col width="20%" />
				<thead>
					<tr>
						<th>시간</th>
						<th>단말기명</th>
						<th>이름</th>
						<th>고유번호</th>
						<th>카드상태</th>
						<th>결과</th>
						<th>권한타입</th>
					</tr>
				</thead>
				<tbody id="logListBody">
					<tr>
						<th class="h_35px" colspan="7">조회 목록이 없습니다.</th>
					</tr>
				</tbody>
			</table>
		</div>
	</div>

	<c:choose>
		<c:when test="${isMenu eq true}"><%--  근태관리 접근권한이 있으면 --%>
			<div class="inbox4" style="width: 49%;">
				<div class="left_box">
					<div class="title">
						Working schedule
						<em>근무스케쥴관리</em>
					</div>
					<div class="link">
						<ul>
							<li><a href="/diligenceAndLazinessManagement/workRuleSetup.do">근무규칙설정  +</a></li>
							<li><a href="/diligenceAndLazinessManagement/restDaySetup.do">휴일설정  +</a></li>
						</ul>
					</div>
					<div class="today">
						<div class="title">today</div>
						<div class="today_tx"></div>
					</div>
					<!-- <div class="more_go">
						<a href="#">+</a>
					</div> -->
				</div>
				<div class="right_box">
					<!--메인스케쥴관리 -->
					<div id="container" class="calendar-container"></div>
					<!--//메인스케쥴관리 -->
				</div>
			</div>
		</c:when>
		<c:otherwise><%-- 근태관리 접근권한이 없으면 --%>
			<div class="inbox7" style="margin-top: 40px;">
				<div class="title">
					공지사항
					<div class="more">
						<img src="/img/main/icon_more.png" alt=""  onclick="fnBoardList(00000000000000000001);"/>
					</div>
				</div>
				<div class="tb_outbox">
					<table class="tb_list_main">
						<col width="35%" />
						<col width="35%" />
						<col width="10%" />
						<col width="20%" />
						<thead>
							<tr>
								<th>공지명</th>
								<th>공지내용</th>
								<th>작성자</th>
								<th>작성일</th>
							</tr>
						</thead>
						<tbody id="qaListBody">
							<tr>
								<th class="h_35px" colspan="7">조회 목록이 없습니다.</th>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
	
</div>

<!--메인스케쥴관리 js-->
<!-- <script src="/js/main-calendar/jquery.simple-calendar.js"></script>
<script>
  $(document).ready(function () {
    $("#container").simpleCalendar({
      fixedStartDay: 0, // begin weeks by sunday
      disableEmptyDetails: true,
      events: [
        // generate new event after tomorrow for one hour
        {
          startDate: new Date(new Date().setHours(new Date().getHours() + 24)).toDateString(),
          endDate: new Date(new Date().setHours(new Date().getHours() + 25)).toISOString(),
          summary: '일정내용이 나오는 곳'
        },
        // generate new event for yesterday at noon
        {
          startDate: new Date(new Date().setHours(new Date().getHours() - new Date().getHours() - 80, 0)).toISOString(),
          endDate: new Date(new Date().setHours(new Date().getHours() - new Date().getHours() - 11)).getTime(),
          summary: '일정내용이 나오는 곳'
        },
        // generate new event for the last two days
        {
          startDate: new Date(new Date().setHours(new Date().getHours() - 48)).toISOString(),
          endDate: new Date(new Date().setHours(new Date().getHours() - 24)).getTime(),
          summary: '일정내용이 나오는 곳'
        },
        {
          startDate: new Date(new Date()).toISOString(),
          endDate: new Date(new Date()).getTime(),
          summary: '오늘의 일정내용이 나오는 곳'
        }

      ],

    });
  });
</script> -->
<!--메인스케쥴관리 -->