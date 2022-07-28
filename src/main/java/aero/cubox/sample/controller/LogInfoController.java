package aero.cubox.sample.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.LogInfoService;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.rte.fdl.property.EgovPropertyService;
import aero.cubox.util.AES256Util;
import aero.cubox.sample.service.vo.LogBioRealInfoVO;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.PaginationVO;
import aero.cubox.sample.service.vo.SiteVO;

@Controller
public class LogInfoController {

	@Resource(name = "logInfoService")
	private LogInfoService logInfoService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;

	private int srchCnt     = 20; //조회할 페이지 수
	private int curPageUnit = 10; //한번에 표시할 페이지 번호 개수
	
	@Value("#{property['Globals.aes256.key']}")
	private String GLOBAL_AES256_KEY;

	private static final Logger LOGGER = LoggerFactory.getLogger(LogInfoController.class);

	public LogInfoVO setParamSearchLog(LoginVO loginVO, Map<String, Object> param) {
		
		LogInfoVO logInfoVO = new LogInfoVO();

		LOGGER.debug("###[setParamSearchLog]param:"+param);
		
		// paging
		int srchPage = String.valueOf(param.get("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(String.valueOf(param.get("srchPage"))) : 1;
		//String srchPage = StringUtil.nvl(param.get("srchPage"), "1");
		String srchRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(logInfoVO.getSrchCnt()));
		// search
		String srchCond = StringUtil.nvl(param.get("srchCond"), loginVO.getSite_id());
		String srchCondWord = StringUtil.nvl(param.get("srchCondWord"));
		String srchStartDate = StringUtil.nvl(param.get("srchStartDate"));
		String srchExpireDate = StringUtil.nvl(param.get("srchExpireDate"));
		String srchSuccess = StringUtil.nvl(param.get("srchSuccess"));
		String srchFail = StringUtil.nvl(param.get("srchFail"));
		/*String fsActive = StringUtil.nvl(param.get("fsActive"));
		String fsDelete = StringUtil.nvl(param.get("fsDelete"));
		String fsExpired = StringUtil.nvl(param.get("fsExpired"));
		String fsLost = StringUtil.nvl(param.get("fsLost"));
		String fsWait = StringUtil.nvl(param.get("fsWait"));*/
		String srchCardStatus = StringUtil.nvl(param.get("srchCardStatus"));
		String fsNone = StringUtil.nvl(param.get("fsNone"));
		String flname = StringUtil.nvl(param.get("flname"));	//단말기명
		String fuid = StringUtil.nvl(param.get("fid"));			//fuid
		String funm = StringUtil.nvl(param.get("funm"));		//이름
		String fcno = StringUtil.nvl(param.get("srchCardNum"));	//카드번호
		String srchFutype = StringUtil.nvl(param.get("srchFutype"));
		String srchAuthType   = StringUtil.nvl(param.get("srchAuthType"));
		String srchCdNo = StringUtil.nvl(param.get("srchCdNo"));
		String srchFpartnm1 = StringUtil.nvl(param.get("srchFpartnm1"));
		String srchFpartnm2 = StringUtil.nvl(param.get("srchFpartnm2"));
		String srchPartWord = StringUtil.nvl(param.get("srchPartWord"));
		String srchUserType = StringUtil.nvl(param.get("srchUserType"));
		String srchFuid = "";
		
		
		// excel
		String rowText = StringUtil.nvl(param.get("rowTextArr"));
		String rowData = StringUtil.nvl(param.get("rowDataArr"));
		// sort
		String hidSortName = StringUtil.nvl(param.get("hidSortName"));
		String hidSortNum = StringUtil.nvl(param.get("hidSortNum"));
		
		if(StringUtil.nvl(param.get("pagegb")).equals("L") && !param.containsKey("srchStartDate")) {
			funm = "funm";
			srchCardStatus = "Y";
		}
		
		// 2021-04-02 일반사용자인 경우, 본인의 출입이력만 조회할 수 있도록 
		if(loginVO.getAuthor_id().equals("00009")) {
			srchFuid = loginVO.getFuid();
		}		
		
		try {
			//어제, 오늘 날짜
			String tdDt = logInfoService.getTodayDt();
			String ytDt = logInfoService.getYesterDt();
			//0시0분
			String defaultTime = logInfoService.getDefaultTime();
			//현재시간
			String curTime = logInfoService.getCurTime();
			
			if(srchStartDate.equals("")) {
				srchStartDate = ytDt + " " + defaultTime;
			} 
			if(srchExpireDate.equals("")) {
				srchExpireDate = tdDt + " " + curTime;
			}
			
			//카드상태
			/*List<String> fsList = new ArrayList<String>();
			if(fsActive.equals("Y")) fsList.add(fsActive);
			if(fsDelete.equals("D")) fsList.add(fsDelete);
			if(fsExpired.equals("E")) fsList.add(fsExpired);
			if(fsLost.equals("L")) fsList.add(fsLost);
			if(fsWait.equals("W")) fsList.add(fsWait);*/
			String[] arrCardStatus = null;
			if(!srchCardStatus.equals("")) {
				arrCardStatus = StringUtils.split(srchCardStatus, ",");	
			}
			
			//체크박스 검색조건
			List<String> srchColChk = new ArrayList<String>();
			String colChkQuery = "";
			if(!loginVO.getAuthor_id().equals("00009")) {
				
				if(flname.equals("flname")) srchColChk.add("d.device_nm");
				if(fuid.equals("fuid")) srchColChk.add("l.fuid");
				if(funm.equals("funm")) srchColChk.add("l.funm");
				if(fcno.equals("fcdno")) srchColChk.add("l.fcdno");

				if(!srchCondWord.equals("")){
					if(srchColChk.size() == 0) {
						srchColChk.add("d.device_nm");
						srchColChk.add("l.fuid");
						srchColChk.add("l.funm");
						srchColChk.add("l.fcdno");
					}
					
					for(int i = 0 ; i < srchColChk.size() ; i++){
						if(i > 0) {
							colChkQuery += " or ";
						}
						colChkQuery += srchColChk.get(i) + " like '%" + srchCondWord + "%' ";
					}
					if(srchColChk.size() > 1) {
						colChkQuery = "( " + colChkQuery + ")";
					}
				}
			}
			
			List<String> srchColChk2 = new ArrayList<String>();
			String colChkQuery2 = "";
			if(!loginVO.getAuthor_id().equals("00009")) {
				
				if(srchFpartnm1.equals("fpartnm1")) srchColChk2.add("l.fpartnm1");
				if(srchFpartnm2.equals("fpartnm2")) srchColChk2.add("l.fpartnm2");
		
				if(!srchPartWord.equals("")){
					if(srchColChk2.size() == 0) {
						srchColChk2.add("l.fpartnm1");
						srchColChk2.add("l.fpartnm2");
					}					
					
					for(int i = 0 ; i < srchColChk2.size() ; i++){
						if(i > 0) {
							colChkQuery2 += " or ";
						}
						colChkQuery2 += srchColChk2.get(i) + " like '%" + srchPartWord + "%' ";
					}
					if(srchColChk2.size() > 1) {
						colChkQuery2 = "(" + colChkQuery2 + ")";
					}
				}
			}
			
			// paging
			logInfoVO.setSrchPage(srchPage);
			logInfoVO.setSrchCnt(Integer.parseInt(srchRecPerPage));
			logInfoVO.autoOffset();
			// search
			logInfoVO.setSrchCond(srchCond);
			logInfoVO.setSrchCondWord(srchCondWord);
			logInfoVO.setSrchStartDate(srchStartDate);
			logInfoVO.setSrchExpireDate(srchExpireDate);
			logInfoVO.setSrchSuccess(srchSuccess);
			logInfoVO.setSrchFail(srchFail);
			/*logInfoVO.setFsActive(fsActive);
			logInfoVO.setFsDelete(fsDelete);
			logInfoVO.setFsExpired(fsExpired);
			logInfoVO.setFsLost(fsLost);
			logInfoVO.setFsWait(fsWait);
			logInfoVO.setFsList(fsList);*/
			logInfoVO.setSrchCardStatus(srchCardStatus);
			logInfoVO.setArrCardStatus(arrCardStatus);
			logInfoVO.setFsNone(fsNone);
			logInfoVO.setFlname(flname);
			logInfoVO.setFuid(fuid);
			logInfoVO.setFunm(funm);
			logInfoVO.setSrchCardNum(fcno);
			logInfoVO.setSrchColChk(srchColChk);
			logInfoVO.setColChkQuery(colChkQuery);
			logInfoVO.setSrchColChk2(srchColChk2);
			logInfoVO.setColChkQuery2(colChkQuery2);
			logInfoVO.setStDateTime(srchStartDate);
			logInfoVO.setEdDateTime(srchExpireDate);
			logInfoVO.setFutype(srchFutype);
			logInfoVO.setFcdno(srchCdNo);
			logInfoVO.setSrchAuthType(srchAuthType);
			logInfoVO.setSrchFpartnm1(srchFpartnm1);
			logInfoVO.setSrchFpartnm2(srchFpartnm2);
			logInfoVO.setSrchPartWord(srchPartWord);
			logInfoVO.setSrchFuid(srchFuid);
			logInfoVO.setSrchUserType(srchUserType);
			// excel
			logInfoVO.setRowData(rowData);
			logInfoVO.setRowText(rowText);
			// sort
			logInfoVO.setHidSortName(hidSortName);
			logInfoVO.setHidSortNum(hidSortNum);
		
		} catch(Exception e) {e.printStackTrace();}
		
		return logInfoVO;
	}
	
	/**
	 * 출입이력
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management_new
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/logMngmt2.do")
	public String logMngmt2(@RequestParam Map<String, Object> commandMap,
						   HttpServletRequest request, ModelMap model) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		commandMap.put("pagegb", "L");
		LogInfoVO logInfoVO = setParamSearchLog(loginVO, commandMap);
		
		//자동새로고침때문에 추가
		String reloadYn = StringUtil.isNullToString(commandMap.get("reloadYn")).matches("Y") ? StringUtil.isNullToString(commandMap.get("reloadYn")) : "N";
		String intervalSecond = StringUtil.isNullToString(commandMap.get("intervalSecond")).matches("(^[0-9]+$)") ? StringUtil.isNullToString(commandMap.get("intervalSecond")) : "5";
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//센터 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		//List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType"); 		//권한타입
		List<CodeVO> authType = commonService.getCodeList("code","evttype"); 		//권한타입
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//출입자타입
		
		LOGGER.debug("logInfoVO : {}", logInfoVO);

		//출입이력 전체 갯수
		int totalCnt = logInfoService.getGateLogListCount(logInfoVO);
		if(logInfoVO.getSrchCnt() == 0) logInfoVO.setSrchCnt(totalCnt);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(logInfoVO.getSrchPage());
		pageVO.setRecPerPage(logInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(logInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		//출입이력 리스트
		List<LogInfoVO> logInfoList = logInfoService.getGateLogList(logInfoVO);
				
		model.addAttribute("totalCnt", totalCnt);
		model.addAttribute("currentPage", logInfoVO.getSrchPage());
		model.addAttribute("displayNum", logInfoVO.getSrchCnt());
		model.addAttribute("reloadYn", reloadYn);
		model.addAttribute("intervalSecond", intervalSecond);
		model.addAttribute("centerCombo", centerCombo);
		model.addAttribute("cntPerPage", cntPerPage);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("logInfoVO", logInfoVO);
		model.addAttribute("logInfoList", logInfoList);

		return "cubox/logInfo/log_management_new";
	}	
	
	/**
	 * 출입이력
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/logMngmt.do")
	public String logMngmt(@RequestParam Map<String, Object> commandMap,
						   HttpServletRequest request, ModelMap model) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		commandMap.put("pagegb", "L");
		LogInfoVO logInfoVO = setParamSearchLog(loginVO, commandMap);
		
		//자동새로고침때문에 추가
		String reloadYn = StringUtil.isNullToString(commandMap.get("reloadYn")).matches("Y") ? StringUtil.isNullToString(commandMap.get("reloadYn")) : "N";
		String intervalSecond = StringUtil.isNullToString(commandMap.get("intervalSecond")).matches("(^[0-9]+$)") ? StringUtil.isNullToString(commandMap.get("intervalSecond")) : "5";
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//센터 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수			
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		
		//출입이력 전체 갯수
		int totalCnt = logInfoService.getGateLogListCount(logInfoVO);
		if(logInfoVO.getSrchCnt() == 0) logInfoVO.setSrchCnt(totalCnt);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(logInfoVO.getSrchPage());
		pageVO.setRecPerPage(logInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(logInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		//출입이력 리스트
		List<LogInfoVO> logInfoList = logInfoService.getGateLogList(logInfoVO);
				
		model.addAttribute("totalCnt", totalCnt);
		model.addAttribute("currentPage", logInfoVO.getSrchPage());
		model.addAttribute("displayNum", logInfoVO.getSrchCnt());
		model.addAttribute("reloadYn", reloadYn);
		model.addAttribute("intervalSecond", intervalSecond);
		model.addAttribute("centerCombo", centerCombo);
		model.addAttribute("cntPerPage", cntPerPage);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("logInfoVO", logInfoVO);
		model.addAttribute("logInfoList", logInfoList);

		return "cubox/logInfo/log_management";
	}

	/**
	 * 단말기별 출입이력
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/gateLogViewPopup.do")
	public String gateLogInfoPopup(@RequestParam Map<String, Object> commandMap, HttpServletRequest request, ModelMap model) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;

		String fgid = StringUtil.isNullToString(commandMap.get("fgid"));
		String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));
		String srchSuccess = StringUtil.isNullToString(commandMap.get("srchSuccess"));
		String srchFail = StringUtil.isNullToString(commandMap.get("srchFail"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("srchStartDate"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("srchExpireDate"));
		String fsActive = StringUtil.isNullToString(commandMap.get("fsActive"));
		String fsDelete = StringUtil.isNullToString(commandMap.get("fsDelete"));
		String fsExpired = StringUtil.isNullToString(commandMap.get("fsExpired"));
		String fsLost = StringUtil.isNullToString(commandMap.get("fsLost"));
		String fsWait = StringUtil.isNullToString(commandMap.get("fsWait"));
		String fsNone = StringUtil.isNullToString(commandMap.get("fsNone"));

		if(srchCond.isEmpty()){
			srchCond = loginVO.getSite_id();
		}

		//카드타입
		List<String> fsList = new ArrayList<String>();
		if(fsActive.equals("Y")){
			fsList.add(fsActive);
		}
		if(fsDelete.equals("D")){
			fsList.add(fsDelete);
		}
		if(fsExpired.equals("E")){
			fsList.add(fsExpired);
		}
		if(fsLost.equals("L")){
			fsList.add(fsLost);
		}
		if(fsWait.equals("W")){
			fsList.add(fsWait);
		}

		//검색조건
		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFgid(fgid);
		logInfoVO.setSrchCond(srchCond);
		logInfoVO.setSrchCondWord(srchCondWord);
		logInfoVO.setSrchSuccess(srchSuccess);
		logInfoVO.setSrchFail(srchFail);
		logInfoVO.setFsDelete(fsDelete);
		logInfoVO.setFsActive(fsActive);
		logInfoVO.setFsExpired(fsExpired);
		logInfoVO.setFsLost(fsLost);
		logInfoVO.setFsWait(fsWait);
		logInfoVO.setFsNone(fsNone);
		logInfoVO.setFsList(fsList);
		logInfoVO.setStDateTime(stDateTime);
		logInfoVO.setEdDateTime(edDateTime);
		logInfoVO.setSrchCnt(15); //2022-05-03
		if(loginVO.getAuthor_id().equals("00009")) {
			logInfoVO.setFuid(loginVO.getFuid()); //2021-04-05
		}		

		//단말기별 출입이력 전체 갯수
		int totalCnt = logInfoService.getGateLogTotalCnt(logInfoVO);

		//단말기명 타이틀
		String title = logInfoService.getGateTitle(fgid);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(logInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(logInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		logInfoVO.setSrchPage(srchPage);
		logInfoVO.autoOffset();

		//단말기 팝업에서 조회된 출입이력
		List<LogInfoVO> gateLogInfoList = logInfoService.getGateLogInfoPop(logInfoVO);

		model.addAttribute("gateLogInfoList", gateLogInfoList);				//단말기별 사용자 출입이력
		model.addAttribute("logInfoVO", logInfoVO);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("title", title);

		return "cubox/logInfo/gateLog_view_popup";

	}

	/**
	 * 단말기별 출입자 개인정보
	 * @param
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/logInfo/gateLogUserInfo.do")
	public ModelAndView gateLoguserInfo(@RequestParam("fuid") String fuid
									  , @RequestParam("fcdno") String cfcdno) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setFuid(fuid);
		userInfo.setCfcdno(cfcdno);
		userInfo = logInfoService.getGateUserInfo(userInfo);

		modelAndView.addObject("userInfo", userInfo);

		return modelAndView;
	}

	/**
	 * 사용자별 출입이력
	 * @param
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	@RequestMapping(value={"/logInfo/usrLogViewPopup.do", "/logInfo/usrLogViewPopup2.do", "/logInfo/usrLogViewPopup3.do"
			              ,"/logInfo/usrFailLogViewPopup.do", "/logInfo/usrFailLogViewPopup2.do", "/logInfo/usrFailLogViewPopup3.do"})
	public String userLogInfoPopup(@RequestParam Map<String, Object> commandMap,
								   HttpServletRequest request, ModelMap model) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String getReqPath = request.getServletPath();
		String strTempPath = "/logInfo/usrLogViewPopup2.do";
		String strVisitPath = "/logInfo/usrLogViewPopup3.do";
		String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("srchStartDate"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("srchExpireDate"));
		String srchColCond = StringUtil.isNullToString(commandMap.get("srchColCond"));
		String srchSuccess = StringUtil.isNullToString(commandMap.get("srchSuccess"));
		String srchFail = StringUtil.isNullToString(commandMap.get("srchFail"));

		if(srchCond.isEmpty()){
			srchCond = loginVO.getSite_id();
		}
		
		String title = "";
		//일반
		if( getReqPath.contains("Fail") ){
			title = "출입자별 출입실패이력";
			if( !"Y".equals(srchSuccess)){ srchSuccess="N";}
		} else {
			title = "출입자별 출입이력";
		} 
		
		
		//검색조건
		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFuid(fuid);
		logInfoVO.setSrchCond(srchCond);
		logInfoVO.setSrchCondWord(srchCondWord);
		logInfoVO.setSrchColCond(srchColCond);
		logInfoVO.setSrchSuccess(srchSuccess);
		logInfoVO.setSrchFail(srchFail);
		logInfoVO.setStDateTime(stDateTime);
		logInfoVO.setEdDateTime(edDateTime);
		logInfoVO.setSrchCnt(15); //2022-05-03

		// 2021-02-05 khlee
		//사용자 바이오정보(사진)
		//UserBioInfoVO userBioInfo = new UserBioInfoVO();
		//userBioInfo = logInfoService.getUserBioInfo(fuid);

		//사용자 개인정보 가져오기
		UserInfoVO userInfo = new UserInfoVO();
		userInfo = logInfoService.getUserInfo(fuid);
		
		
		//사용자별 출입이력 전체 갯수
		int totalCnt = logInfoService.getUsrLogTotalCnt(logInfoVO);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(logInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(logInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		logInfoVO.setSrchPage(srchPage);
		logInfoVO.autoOffset();

		//사용자별 팝업에서 조회된 출입이력
		List<LogInfoVO> userlogInfo = logInfoService.getUsrLogInfoPop(logInfoVO);

		//model.addAttribute("userBioInfo", userBioInfo);					//사용자 바이오정보(사진)
		model.addAttribute("userInfo", userInfo);						//사용자 개인정보 가져오기
		model.addAttribute("userlogInfo", userlogInfo);					//사용자별 출입이력
		model.addAttribute("pagination", pageVO);						//페이징
		model.addAttribute("logInfoVO", logInfoVO);						//검색조건
		model.addAttribute("title", title);								//사용자명 타이틀

		if(getReqPath.contains("Popup2")) {
			return "cubox/logInfo/usrLog_view_popup2"; //임시
		}else if(getReqPath.equals("Popup3")) {
			return "cubox/logInfo/usrLog_view_popup3"; //방문
		}else {
			return "cubox/logInfo/usrLog_view_popup";
		}

	}

	/**
	 * source 이미지가져오기
	 * @param
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/getByteImage.do")
	public ResponseEntity<byte[]> getByteImage(@RequestParam("fuid") String fuid) throws Exception {

		AES256Util aes256 = new AES256Util();

		UserBioInfoVO userBioInfo = new UserBioInfoVO();
		userBioInfo = logInfoService.getUserBioInfo(fuid);

		byte[] imageContent = null;
		if(userBioInfo != null && userBioInfo.getFimg().length() != 0) {
			try {
				imageContent = aes256.byteArrDecodenopaddingImg(userBioInfo.getFimg(), GLOBAL_AES256_KEY);
			} catch(Exception e) {
				LOGGER.error("### [getByteImage] 등록이미지 error : {}", e.getMessage());
				e.printStackTrace();
			}
		} else {
			LOGGER.error("### [getByteImage] 등록이미지 userBioInfo is null!");
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
	}

	/**
	 * real 이미지가져오기
	 * @param
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/getByteRealImage.do")
	public ResponseEntity<byte[]> getByteRealImage(@RequestParam("fuid") String fuid
												 , @RequestParam("fevttm") String fevttm) throws Exception {

		AES256Util aes256 = new AES256Util();

		LogBioRealInfoVO userBioInfo = new LogBioRealInfoVO();
		userBioInfo.setFuid(fuid);
		userBioInfo.setFevttm(fevttm);
		userBioInfo = logInfoService.getLogBioRealInfo(userBioInfo);

		byte[] imageContent = null;
		if(userBioInfo != null && userBioInfo.getFcimg().length() != 0) {
			try {
				imageContent = aes256.byteArrDecodenopaddingImg(userBioInfo.getFcimg(), GLOBAL_AES256_KEY);
			} catch(Exception e) {
				LOGGER.error("### [getByteRealImage] 실시간이미지 error : {}", e.getMessage());
				e.printStackTrace();
			}
		} else {
			LOGGER.error("### [getByteRealImage] 실시간이미지 userBioInfo is null!");
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
	}

	/**
	 * 출입이력 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/excelDownload.do")
	public ModelAndView excelDownload(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		LOGGER.debug("출입이력 엑셀다운로드 commandMap:{}", commandMap);
		LogInfoVO logInfoVO = setParamSearchLog(loginVO, commandMap);

		String[] rsText = null;
		List<ExcelVO> resultCellList = new ArrayList<ExcelVO>();
		
		String sImgYn = StringUtil.nvl(commandMap.get("hidImgYn"), "N");
		
		try {
			rsText = logInfoVO.getRowText().split(",");
			resultCellList = logInfoService.getGateLogListExcel(logInfoVO);
		} catch(Exception e) {
			LOGGER.error("출입이력 엑셀다운로드 에러 발생!! {}", e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			String fsyscode = "13101202";  //출입이력
			if(sImgYn.equals("Y")) {
				fsyscode = "13101213";  //출입이력(이미지포함)
			}
			commonService.sysLogSave(loginVO.getFsiteid(), fsyscode, "commandMap:"+commandMap.toString()+",colChkQuery:"+logInfoVO.getColChkQuery(), commonUtils.getIPFromRequest(request)); //로그저장		
		}

		modelAndView.setViewName("excelDownloadView");
		modelAndView.addObject("resultList", resultCellList);
		modelAndView.addObject("excelName", "출입이력");
		modelAndView.addObject("excelHeader", rsText);
		modelAndView.addObject("strImgYn", sImgYn);
		modelAndView.addObject("strImgGb", "L");

		return modelAndView;
    }

	/**
	 * 단말기 팝업 엑셀 다운로드
	 * @param
	 * @return logInfo/gatePopExcelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/gatePopExcelDownload.do")
	public ModelAndView gatePopExcelDownload(HttpServletRequest request, @RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		String rowText = StringUtil.isNullToString(commandMap.get("rowTextArr"));
		String rowData = StringUtil.isNullToString(commandMap.get("rowDataArr"));
		String fgid = StringUtil.isNullToString(commandMap.get("fgid"));
		//String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));
		String srchSuccess = StringUtil.isNullToString(commandMap.get("srchSuccess"));
		String srchFail = StringUtil.isNullToString(commandMap.get("srchFail"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("srchStartDate"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("srchExpireDate"));
		String fsActive = StringUtil.isNullToString(commandMap.get("fsActive"));
		String fsDelete = StringUtil.isNullToString(commandMap.get("fsDelete"));
		String fsExpired = StringUtil.isNullToString(commandMap.get("fsExpired"));
		String fsLost = StringUtil.isNullToString(commandMap.get("fsLost"));
		String fsWait = StringUtil.isNullToString(commandMap.get("fsWait"));
		String fsNone = StringUtil.isNullToString(commandMap.get("fsNone"));
		String[] rsText = rowText.split(",");

		//카드타입
		List<String> fsList = new ArrayList<String>();
		if(fsActive.equals("Y")){
			fsList.add(fsActive);
		}
		if(fsDelete.equals("D")){
			fsList.add(fsDelete);
		}
		if(fsExpired.equals("E")){
			fsList.add(fsExpired);
		}
		if(fsLost.equals("L")){
			fsList.add(fsLost);
		}
		if(fsWait.equals("W")){
			fsList.add(fsWait);
		}

		//검색조건
		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFgid(fgid);
		logInfoVO.setSrchCondWord(srchCondWord);
		logInfoVO.setSrchSuccess(srchSuccess);
		logInfoVO.setSrchFail(srchFail);
		logInfoVO.setStDateTime(stDateTime);
		logInfoVO.setEdDateTime(edDateTime);
		logInfoVO.setFsDelete(fsDelete);
		logInfoVO.setFsActive(fsActive);
		logInfoVO.setFsExpired(fsExpired);
		logInfoVO.setFsLost(fsLost);
		logInfoVO.setFsWait(fsWait);
		logInfoVO.setFsNone(fsNone);
		logInfoVO.setFsList(fsList);
		logInfoVO.setRowData(rowData);
		logInfoVO.setRowText(rowText);
		if(loginVO.getAuthor_id().equals("00009")) {
			logInfoVO.setFuid(loginVO.getFuid());  //2021-04-05
		}		

		List<ExcelVO> resultCellList = logInfoService.getGatePopCellList(logInfoVO);
		
		commonService.sysLogSave(loginVO.getFsiteid(), "13101208", "commandMap:"+commandMap.toString(), commonUtils.getIPFromRequest(request)); //로그저장

		modelAndView.setViewName("excelDownloadView");
		modelAndView.addObject("resultList", resultCellList);
		modelAndView.addObject("excelName", "단말기별출입이력");
		modelAndView.addObject("excelHeader", rsText);

		return modelAndView;
	}

	/**
	 * 사용자 팝업 엑셀 다운로드
	 * @param
	 * @return logInfo/usrLogExcelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/usrPopExcelDownload.do")
	public ModelAndView usrPopExcelDownload(HttpServletRequest request, @RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		String rowText = StringUtil.isNullToString(commandMap.get("rowTextArr"));
		String rowData = StringUtil.isNullToString(commandMap.get("rowDataArr"));
		String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		//String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("srchStartDate"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("srchExpireDate"));
		String srchSuccess = StringUtil.isNullToString(commandMap.get("srchSuccess"));
		String srchFail = StringUtil.isNullToString(commandMap.get("srchFail"));		

		String[] rsText = rowText.split(",");

		//검색조건
		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFuid(fuid);
		logInfoVO.setSrchCondWord(srchCondWord);
		logInfoVO.setSrchSuccess(srchSuccess);
		logInfoVO.setSrchFail(srchFail);
		logInfoVO.setStDateTime(stDateTime);
		logInfoVO.setEdDateTime(edDateTime);
		logInfoVO.setRowData(rowData);
		logInfoVO.setRowText(rowText);

		List<ExcelVO> resultCellList = logInfoService.getUsrPopCellList(logInfoVO);
		
		commonService.sysLogSave(loginVO.getFsiteid(), "13101207", "commandMap:"+commandMap.toString(), commonUtils.getIPFromRequest(request)); //로그저장        		

		modelAndView.setViewName("excelDownloadView");
		modelAndView.addObject("resultList", resultCellList);
		modelAndView.addObject("excelName", "사용자별출입이력");
		modelAndView.addObject("excelHeader", rsText);

		return modelAndView;
	}

	/**
	 * 날짜, 시간 초기화
	 * @param
	 * @return logInfo/getTime
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/getTime.do")
	public ModelAndView getTime(@RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		String toTime = StringUtil.isNullToString(commandMap.get("toTime"));

		String fromTime = logInfoService.getDefaultTime();
		String curTime = logInfoService.getCurTime();
		String tdDt = logInfoService.getTodayDt();
		String ytDt = logInfoService.getYesterDt();

		if(StringUtil.isEmpty(toTime)){
			toTime = curTime;
		}

		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFromTime(fromTime);
		logInfoVO.setToTime(toTime);

		modelAndView.setViewName("jsonView");
		modelAndView.addObject("fromTime", fromTime);
		modelAndView.addObject("toTime", toTime);
		modelAndView.addObject("tdDt", tdDt);
		modelAndView.addObject("ytDt", ytDt);

		return modelAndView;
	}

	/**
	 * 단말기 팝업 새로고침
	 * @param
	 * @return logInfo/gateLogViewPopup
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/gatePopReload.do")
	public ModelAndView gatePopReload(@RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		String toTime = StringUtil.isNullToString(commandMap.get("toTime"));

		String fromTime = logInfoService.getDefaultTime();
		String curTime = logInfoService.getCurTime();
		String tdDt = logInfoService.getTodayDt();
		String ytDt = logInfoService.getYesterDt();

		if(StringUtil.isEmpty(toTime)){
			toTime = curTime;
		}

		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFromTime(fromTime);
		logInfoVO.setToTime(toTime);

		modelAndView.setViewName("jsonView");
		modelAndView.addObject("fromTime", fromTime);
		modelAndView.addObject("toTime", toTime);
		modelAndView.addObject("tdDt", tdDt);
		modelAndView.addObject("ytDt", ytDt);

		return modelAndView;

	}

	/**
	 * 사용자 팝업 새로고침
	 * @param
	 * @return logInfo/usrLogViewPopup
	 * @throws Exception
	 */
	@RequestMapping(value={"/logInfo/usrPopReload.do", "/logInfo/usrPopReload2.do", "/logInfo/usrPopReload3.do"})
	public ModelAndView usrPopReload(@RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		String toTime = StringUtil.isNullToString(commandMap.get("toTime"));

		String fromTime = logInfoService.getDefaultTime();
		String curTime = logInfoService.getCurTime();
		String tdDt = logInfoService.getTodayDt();
		String ytDt = logInfoService.getYesterDt();

		if(StringUtil.isEmpty(toTime)){
			toTime = curTime;
		}

		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFromTime(fromTime);
		logInfoVO.setToTime(toTime);

		modelAndView.setViewName("jsonView");
		modelAndView.addObject("fromTime", fromTime);
		modelAndView.addObject("toTime", toTime);
		modelAndView.addObject("tdDt", tdDt);
		modelAndView.addObject("ytDt", ytDt);

		return modelAndView;

	}

	/**
	 * 출입실패이력
	 * @param commandMap
	 * @param request
	 * @param model
	 * @return log_failmanagement
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/logFailMngmt.do")
	public String logFailMngmt(@RequestParam Map<String, Object> commandMap,
						   HttpServletRequest request, ModelMap model) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		commandMap.put("pagegb", "LF");
		LogInfoVO logInfoVO = setParamSearchLog(loginVO, commandMap);		

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//센터 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//출입자타입 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수	
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		

		//출입이력 전체 갯수
		int totalCnt = logInfoService.getFailLogTotalCnt(logInfoVO);
		if(logInfoVO.getSrchCnt() == 0) logInfoVO.setSrchCnt(totalCnt);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(logInfoVO.getSrchCnt());
		pageVO.setRecPerPage(logInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(logInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		//출입실패이력 리스트
		List<LogInfoVO> logInfoList = logInfoService.getFailLogInfoList(logInfoVO);
		model.addAttribute("totalCnt",totalCnt);
		model.addAttribute("currentPage",logInfoVO.getSrchPage());
		model.addAttribute("displayNum", logInfoVO.getSrchCnt());
		model.addAttribute("centerCombo", centerCombo);
		model.addAttribute("userType", userType);
		model.addAttribute("cntPerPage", cntPerPage);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("logInfoVO", logInfoVO);
		model.addAttribute("logInfoList", logInfoList);	
		model.addAttribute("cardStatus", cardStatus);	

		return "cubox/logInfo/log_failmanagement";
	}

	/**
	 * 출입이력 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/failExcelDownload.do")
	public ModelAndView failExcelDownload (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		LogInfoVO logInfoVO = setParamSearchLog(loginVO, commandMap);
		
		String[] rsText = null;
		List<ExcelVO> resultCellList = new ArrayList<ExcelVO>();
		
		try {
			rsText = logInfoVO.getRowText().split(",");
			resultCellList = logInfoService.getFailLogCellList(logInfoVO);
		} catch(Exception e) {
			LOGGER.error("출입실패이력 엑셀다운로드 에러 발생!! commandMap:{}", commandMap);
			e.printStackTrace();
		} finally {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101206", "commandMap:"+commandMap.toString(), commonUtils.getIPFromRequest(request)); //출입실패이력
		}		
		
		modelAndView.setViewName("excelDownloadView");
		modelAndView.addObject("resultList", resultCellList);
		modelAndView.addObject("excelName", "출입실패이력");
		modelAndView.addObject("excelHeader", rsText);

		return modelAndView;
	}


	/**
	 * 카드별 출입실패이력
	 * @param
	 * @return logInfo/usrLog_fail_popup
	 * @throws Exception
	 */
	@RequestMapping(value={"/logInfo/cardFailLogViewPopup.do"})
	public String cardFailLogViewPopup(@RequestParam Map<String, Object> commandMap,HttpServletRequest request, ModelMap model) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		String funm = StringUtil.isNullToString(commandMap.get("funm"));//new String(.getBytes("8859_1"),"UTF-8");
		String fcdno = StringUtil.isNullToString(commandMap.get("fcdno"));
		String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchStartDate = StringUtil.isNullToString(commandMap.get("srchStartDate"));
		String srchExpireDate = StringUtil.isNullToString(commandMap.get("srchExpireDate"));
		//String fromTime = StringUtil.isNullToString(commandMap.get("fromTime"));
		//String toTime = StringUtil.isNullToString(commandMap.get("toTime"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("stDateTime"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("edDateTime"));
		String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));
		String fvisitnm = StringUtil.isNullToString(commandMap.get("fvisitnm"));
		
		if(srchCond.isEmpty()){
			srchCond = loginVO.getSite_id();
		}		

		/*
		//0시0분
		String defaultTime = logInfoService.getDefaultTime();

		//현재시간
		String curTime = logInfoService.getCurTime();

		if(StringUtil.isEmpty(fromTime)){
			fromTime = defaultTime;
		}
		if(StringUtil.isEmpty(toTime)){
			toTime = curTime;
		}

		stDateTime = srchStartDate + " " + fromTime;
		edDateTime = srchExpireDate + " " + toTime;
		*/

		//검색조건
		LogInfoVO logInfoVO = new LogInfoVO();
		logInfoVO.setFuid(fuid);
		logInfoVO.setFcdno(fcdno);
		logInfoVO.setSrchCond(srchCond);
		logInfoVO.setSrchStartDate(srchStartDate);
		logInfoVO.setSrchExpireDate(srchExpireDate);
		//logInfoVO.setFromTime(fromTime);
		//logInfoVO.setToTime(toTime);
		logInfoVO.setStDateTime(StringUtil.nvl(stDateTime, srchStartDate));
		logInfoVO.setEdDateTime(StringUtil.nvl(edDateTime, srchExpireDate));
		logInfoVO.setFunm(funm);

		//카드별 출입실패이력 전체 갯수
		int totalCnt = logInfoService.getUsrFailLogTotalCnt(logInfoVO);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(logInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(logInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		logInfoVO.setSrchPage(srchPage);
		logInfoVO.autoOffset();

		//카드별 출입실패이력
		List<LogInfoVO> userlogInfo = logInfoService.getUsrFailLogInfoPop(logInfoVO);

		model.addAttribute("userlogInfo", userlogInfo);					//카드별 출입실패이력
		model.addAttribute("pagination", pageVO);						//페이징
		model.addAttribute("logInfoVO", logInfoVO);						//검색조건
		model.addAttribute("title", fcdno+" "+(fvisitnm.equals("")?"":"("+fvisitnm+")"));				//카드 타이틀

		return "cubox/logInfo/usrLog_fail_popup";
	}

	/**
	 * 동기화 이력 조회 화면
	 * @return logInfo/syncLog_management
	 * @throws Exception
	 */
	@RequestMapping(value="/logInfo/syncLogMngmt.do")
	public String syncLogMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		String startDate = StringUtil.isNullToString(commandMap.get("startDate"));
		String endDate = StringUtil.isNullToString(commandMap.get("endDate"));
		String code = StringUtil.isNullToString(commandMap.get("fjobcd"));
		String result = StringUtil.isNullToString(commandMap.get("frsltyn"));
		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;

		PaginationVO pageVO = new PaginationVO();

		HashMap map = new HashMap();
		map.put("fsdt",startDate);
		map.put("fedt",endDate);
		map.put("code", code);
		map.put("result", result);

		int schdulCnt = logInfoService.getSchdulTotalCnt(map);

		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(srchCnt);
		pageVO.calcRecordIndex();

		pageVO.setTotRecord(schdulCnt);
		pageVO.setUnitPage(curPageUnit);
		pageVO.calcPageList();
		map.put("srchCnt", pageVO.getRecPerPage());
		map.put("offset", pageVO.getFirstRecordIndex());
		List<HashMap> schdulList = logInfoService.getSchdulList(map);

		model.addAttribute("map", map);
		model.addAttribute("schdulList", schdulList);
		model.addAttribute("pagination", pageVO);

		return "cubox/logInfo/syncLog_management";

	}
	
	/**
	 * 출입자 영구삭제 전에 출입이력 있는지 확인
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/getGateLogTotCntByUser.do")
	public ModelAndView getGateLogTotCntByUser(@RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		int cnt = -1;
		
		try {
			cnt = logInfoService.getGateLogTotCntByUser(param);
		} catch(Exception e) {
			LOGGER.error(e.getMessage() + param);
		}

		modelAndView.setViewName("jsonView");
		modelAndView.addObject("cnt", cnt);

		return modelAndView;
	}	

}
