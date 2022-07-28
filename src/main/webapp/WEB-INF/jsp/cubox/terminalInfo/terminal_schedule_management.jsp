<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:include page="/WEB-INF/jsp/cubox/common/checkPasswd.jsp" flush="false"/>
<script type="text/javascript">
$(function() {
	$(".title_tx").html("단말기  관리");
	$("#checkAll").click(function(){
		if($("#checkAll").prop("checked")){
			$("input[name=checkbx]").prop("checked", true);
		}else{
			$("input[name=checkbx]").prop("checked", false);
		}
	});

	$("#btnGate").on("click", function(event){
		$("#fgid").val("");
		$("#flname").val("");
		$("#fvname").val("");
		$("#fip01").val("");
		$("#fip02").val("");
		$("#fip03").val("");
		$("#fip04").val("");
		$("#fauthtype2").val("");
		$("#ftmtype").val("");
		$("#fsiteId").val("");

        $("#add-gate-modal").PopupWindow("open");
    });

	modalPopup ("add-gate-modal", "단말기 추가", 600, 600);
	modalPopup ("edit-gate-modal", "단말기 편집", 600, 400);

	$("#btnFileZipPw").on("click", function(event) {
        $("#center-zippw-modal").PopupWindow("open");
        $("#fzipdownpw").focus();
    });

	$("#btnAddClose").click(function(){
		$("#add-gate-modal").PopupWindow("close");
	});

	$("#btnEditClose").click(function(){
		$("#txtEditSiteNm").val("");
		$("#txtEditSiteDesc").val("");
		$("#txtEditSortOrdr").val("");
		$("#txtEditSiteId").val("");

		$("#edit-gate-modal").PopupWindow("close");
	});
});
</script>

<main>
    <div class="sub_box">
        <!--타이틀 공통 --><!--타이틀 공통 -->
        <div class="title_box">
            <div class="title_tx">단말기 관리</div>
            <div class="title_time"><em>2018-02-06</em> Recent Updates </div>
            <div class="route">
                <div class="icon"><img src="/img/icon_home.png" alt=""></div>
                HOME  &gt; 단말기 제어 관리 &gt; <em>단말기 관리</em> </div>
        </div>
        <!--//타이틀 공통 -->

        <!--검색박스 -->
        <div class="search_box mb_20">
            <div class="search_in">
                <div class="comm_search">
                    <select name="" size="1" class="w_100px input_com mr_5">
                        <option selected="">마곡 </option>
                        <option>마곡</option>
                    </select>
                    <select name="" size="1" class="w_100px input_com  mr_5">
                        <option selected="">선택 </option>
                        <option>선택 </option>
                    </select>
                    <select name="" size="1" class="w_100px input_com  mr_5">
                        <option selected="">지역 </option>
                        <option>지역</option>
                    </select>
                    <input type="text" class="w_250px input_com l_radius_no" placeholder="검색어를 입력해 주세요">
                    <div class="search_btn"></div>
                </div>
            </div>
        </div>
        <!--//검색박스 -->
        <!--------- 목록--------->
        <div class="com_box ">
            <div class="totalbox">
                <div class="r_btnbox  mb_10">
                    <button type="button" class="btn_middle color_color1">KeepOpen</button>
                    <button type="button" class="btn_middle color_basic">Open</button>
                    <button type="button" class="btn_middle color_gray">Close</button>
                </div>
            </div>
            <!--버튼 -->

            <!--//버튼  -->
            <!--테이블 시작 -->
            <div class="tb_outbox">
                <table class="tb_list">
                    <colgroup><col width="5%">
                        <col width="12%">
                        <col width="15%">
                        <col width="15%">
                        <col width="">
                    </colgroup><thead>
                <tr>
                    <th><input type="checkbox" name="" class="mt_5">
                    </th>
                    <th>GID</th>
                    <th>단말기명 </th>
                    <th>IP</th>
                    <th>펌웨어버전 </th>
                </tr>
                </thead>
                    <tbody>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" name="" class="mt_5"></td>
                        <td>11100000</td>
                        <td>데스트-더미리더기 </td>
                        <td>192.168.20.30</td>
                        <td>AC1100 0.0-1.00-040.100/1.0.3/NFC6K-V3.90/Firmware BLE40 V1.02</td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <!--------- //목록--------->
            <!-- 페이징 -->
            <div class="pagebox "> <span class="ar1"><img src="/img/al_icon3.png" alt=""></span> <span class="ar"><img src="/img/al_icon2.png" alt=""></span> <span class="um on"> 1 </span> <span class="um">2</span> <span class="um">3</span> <span class="ar"><img src="/img/ar_icon2.png" alt=""></span> <span class="ar1"><img src="/img/ar_icon3.png" alt=""></span> </div>
            <!-- /페이징 -->
            <!--//본문시작 -->

        </div>
    </div></main>