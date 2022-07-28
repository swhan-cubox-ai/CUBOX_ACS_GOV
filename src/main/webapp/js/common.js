// 달력 datapicker
/*$(function(){

	$('.input-group.date').datepicker({
		calendarWeeks: false,
		todayHighlight: true,
		autoclose: true,
		format: "yyyy-mm-dd",
		language: "kr"
	});

	$('input.timepicker').timepicker({
        timeFormat: 'HH:mm:ss',
        interval: 1,
        dynamic: true,
        dropdown: true,
        scrollbar: true
    });
});

;(function($){
	$.fn.datepicker.dates['kr'] = {
			days: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"],
			daysShort: ["일", "월", "화", "수", "목", "금", "토", "일"],
			daysMin: ["일", "월", "화", "수", "목", "금", "토", "일"],
			months: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
			monthsShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"]
	};
}(jQuery));*/

// 팝업창 띄우기
function openPopup(url, winName, widthVal, heightVal) {
	var widthP = widthVal/2;
	var heightP = heightVal/2;
	
	/* 
	 * window를 return하도록 변경
	 * (팝업을 닫기위한 콜백함수를 사용하기 위함)
	 * by dgkim
	 */
	return window.open(url, winName, "scrollbars=yes, toolbar=no, menubar=no, resizable=no, status=no, location=no, width="+widthVal+", height="+heightVal+", left=" + (screen.availWidth/2-widthP) +", top="+(screen.availHeight/2-heightP));
}


//null check
function fnIsEmpty(inVal) {
	if (new String(inVal).valueOf() == "undefined")
		return true;
	if (inVal == null)
		return true;
	if (inVal == 'null')
		return true;

	var v_ChkStr = new String(inVal);

	if (v_ChkStr == null)
		return true;
	if (v_ChkStr.toString().length == 0)
		return true;
	return false;
}

//form 필수 체크
function fnFormValueCheck(form){
	var formObj = $("form[id="+form+"]");
	var checkObj = formObj.find("[check]");
	for(var i=0;i<checkObj.length;i++){
		var sObj = checkObj.eq(i);
		var sType = sObj.attr("check");
		var sId = sObj.attr("id");
		var sName = sObj.attr("name");
		if(sType=="text"){
			if(fnIsEmpty(checkObj.eq(i).val())){
				alert(""+checkObj.eq(i).attr("checkName")+" 은(는) 필수입력 항목입니다.");
				checkObj.eq(i).focus();
				return false;
			}
		}else if(sType=="radio"){
			if(!$('input:radio[name='+sName+']').is(':checked')){
				alert(""+checkObj.eq(i).attr("checkName")+" 은(는) 필수입력 항목입니다.");
				checkObj.eq(i).focus();
				return false;
			}
		}
	}
	return true;
}

/**
 * serializeFormJSON : 폼을 json Object로 변환한다.
 */
$.fn.serializeFormJSON = function () {

    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
    	if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};


/**
 * 숫자외에 삭제
 */
function fnRemoveChar(event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 )
		return;
	else
		event.target.value = event.target.value.replace(/[^0-9]/g, "");
}

//이메일 정규식
function isEmail(asValue) {
	var regExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
	return regExp.test(asValue); // 형식에 맞는 경우 true 리턴
}

// 날짜 정규식
function isDate(asValue) {
	//var regExp = /^(19|20)\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$/;
	var regExp = /^(19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])$/;
	return regExp.test(asValue);
}

// 핸드폰 번호 체크 정규식
function isCelluar(asValue) {
	var regExp = /^01(?:0|1|[6-9])-(?:\d{3}|\d{4})-\d{4}$/;
	return regExp.test(asValue); // 형식에 맞는 경우 true 리턴
}

// 일반 전화번호 정규식
function isCall(asValue) {
	var regExp = /^\d{2,3}-\d{3,4}-\d{4}$/;
	return regExp.test(asValue); // 형식에 맞는 경우 true 리턴
}

// 차량번호 정규식
function isCarNo(asValue) {
	var regExp1 = /\d{2,3}[가-힣ㄱ-ㅎㅏ-ㅣ\x20]\d{4}/g; // 12저1234
	var regExp2 = /[가-힣ㄱ-ㅎㅏ-ㅣ\x20]{2}\d{2}[가-힣ㄱ-ㅎㅏ-ㅣ\x20]\d{4}/g; // 서울12치1233

	if(!regExp1.test(asValue) && !regExp2.test(asValue)) {
		return false;
	} else {
		return true;
	}
}


/*
 * loading indicator show/hide
 */
function showLoading() {
	$.blockUI({
		overlayCSS:{
			backgroundColor:'#ffe',
			opacity: .5
		},
		css:{
			border:'none',
			opacity: .5,
			width:'80px',
			left:'45%'
		},
		message:"<img src='/images/loading.gif' width='80px'>"
	});
}

function hideLoading() {
	$.unblockUI();
}


/*
 * modal popup
 */
function modalPopup (popupId, popupName, width, height) {
    $("#"+popupId).PopupWindow({
        title       : popupName,
        modal       : true,
        autoOpen    : false,
		buttons : {
			close       : true,
			maximize	: true,
			minimize    : true
		},
        height      : height,
        width       : width,
        nativeDrag	: false
    });
}

/* 카드번호 자릿수 체크 */
function fnvalichk (event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) return;
	else {
		var tVal = event.target.value;
		var regx = /^[A-Fa-f0-9]*$/g;//0 ~ 9, A ~ F, a ~ f이 포함된 정규식

		if(!fnIsEmpty(tVal) && !regx.test(tVal)) {
			tVal = tVal.replace(/[^A-Fa-f0-9]/g, "");//0 ~ 9, A ~ F, a ~ f이 포함되지않은 정규식
			/*
			 * cardDigit : globals.properties의 정의된 속성을 사용
			 * 이 함수를 사용하고자하는 jsp파일에 cardDigit가 정의되어야함.
			 * */
			event.target.value = fnIsEmpty(tVal) ? "" : tVal.substring(0, cardDigit);
		}
	}
}
