package aero.cubox.sample.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.GateInfoService;
import aero.cubox.sample.service.TerminalInfoService;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.CuboxProperties;
import aero.cubox.util.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.commons.DefaultBeanValidator;

import aero.cubox.sample.service.vo.DateTimeVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.GateOperationVO;
import aero.cubox.sample.service.vo.GateShTypeVO;
import aero.cubox.sample.service.vo.GateVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.PaginationVO;
import aero.cubox.sample.service.vo.SiteVO;
import aero.cubox.sample.service.vo.TcmdMainVO;
import aero.cubox.sample.service.vo.TerminalSchLogVO;
import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class TerminalInfoController {

	/** memberService */
	@Resource(name = "terminalInfoService")
	private TerminalInfoService terminalInfoService;

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

	/** gateInfoService */
	@Resource(name = "gateInfoService")
	private GateInfoService gateInfoService;

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminalInfoController.class);

	/**
	 * 단말기스케줄관리
	 * @param commandMap 파라메터전달용 commandMap
	 * @return userInfo/user_management
	 * @throws Exception
	 */
	@RequestMapping(value="/terminalInfo/terminalScheduleMngmt.do")
	public String userMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String siteId = loginVO.getSite_id();

		return "cubox/terminalInfo/terminal_schedule_management";

	}
	
	/**
	 * 스케줄관리팝업
	 * @param commandMap 파라메터전달용 commandMap
	 * @return userInfo/user_management
	 * @throws Exception
	 */
	@RequestMapping(value="/terminalInfo/scheduleMngmt.do")
	public String userAddPopup(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		String srchShLst = StringUtil.isNullToString(request.getParameter("arg1"));

		//LOGGER.debug("파라미터 확인 >> "+srchShLst);

		//값 조회
		GateShTypeVO rvo = null;
		if(!srchShLst.trim().equals("")) {
			GateShTypeVO vo = new GateShTypeVO ();
			vo.setFsidx(srchShLst);
			List<GateShTypeVO> gateShTypeList = terminalInfoService.getGateShType (vo);
			if(gateShTypeList != null && gateShTypeList.size() > 0) rvo = gateShTypeList.get(0);
		}
		if(rvo == null ) {
			rvo = new GateShTypeVO ();
			String strn = "-:--:--:--:--|-:--:--:--:--|-:--:--:--:--|-:--:--:--:--|-:--:--:--:--|-:--:--:--:--|";
			rvo.setFmo(strn);
			rvo.setFtu(strn);
			rvo.setFwe(strn);
			rvo.setFth(strn);
			rvo.setFfr(strn);
			rvo.setFsa(strn);
			rvo.setFsu(strn);
			rvo.setFf1(strn);
			rvo.setFf2(strn);
			rvo.setFf3(strn);
		}
		model.addAttribute("gateShTypeList",rvo);

		return "cubox/terminalInfo/schedule_popup";
	}

	/**
	 * 게이트 스케줄 타입 추가
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/scheduleSave.do")
	public ModelAndView scheduleSave(@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String fsidx = StringUtil.isNullToString(commandMap.get("fsidx")).trim();
    	String fnm = StringUtil.isNullToString(commandMap.get("fnm")).trim();

    	String[] arr = {"fmo","ftu","fwe","fth","ffr","fsa","fsu","ff1","ff2","ff3"};
    	Map<String, String> map = new HashMap<String, String> ();

    	for(String str:arr) {
    		String s1 = StringUtil.isNullToString(commandMap.get(str+"1_1"));
    		String s2 = StringUtil.isNullToString(commandMap.get(str+"1_2"));
    		String s3 = StringUtil.isNullToString(commandMap.get(str+"2_1"));
    		String s4 = StringUtil.isNullToString(commandMap.get(str+"2_2"));
    		s1 = s1.trim().equals("")?"--:--":s1.trim()+":";
    		s2 = s2.trim().equals("")?"--:--":s2.trim();
    		s3 = s3.trim().equals("")?"--:--":s3.trim()+":";
    		s4 = s4.trim().equals("")?"--:--":s4.trim();

    		String tot1 = ((s1+s2).equals("--:--:--:--")?"-:":"1:")+s1+s2+"|-:--:--:--:--|-:--:--:--:--|";
    		String tot2 = ((s3+s4).equals("--:--:--:--")?"-:":"1:")+s3+s4+"|-:--:--:--:--|-:--:--:--:--|";

    		map.put(str, tot1+tot2);
    	}

    	GateShTypeVO vo = new GateShTypeVO();
    	vo.setFsidx(fsidx);
    	vo.setFnm(fnm);
    	vo.setFmo(map.get("fmo"));
    	vo.setFtu(map.get("ftu"));
    	vo.setFwe(map.get("fwe"));
    	vo.setFth(map.get("fth"));
    	vo.setFfr(map.get("ffr"));
    	vo.setFsa(map.get("fsa"));
    	vo.setFsu(map.get("fsu"));
    	vo.setFf1(map.get("ff1"));
    	vo.setFf2(map.get("ff2"));
    	vo.setFf3(map.get("ff3"));

    	if(fsidx.equals("0")) {
    		//getKey
    		String fcode = "tgateshtype";
    		commonService.tcsidxSave(fcode);
    		vo.setFsidx(commonService.getFsidx(fcode));
    		rtn = terminalInfoService.insertGateSchedule (vo);
    	} else {
    		rtn = terminalInfoService.updateGateSchedule (vo);
    	}
		modelAndView.addObject("saveCnt", rtn);

		return modelAndView;
	}

	/**
	 * 게이트 스케줄 타입 이름 중복 확인
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/duplScheduleNmCheck.do")
	public ModelAndView duplScheduleNmCheck(@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String fnm = StringUtil.isNullToString(commandMap.get("fnm")).trim();
    	GateShTypeVO vo = new GateShTypeVO();
    	vo.setFnm(fnm);

    	rtn = terminalInfoService.duplScheduleNmCheck (vo);
		modelAndView.addObject("duplCnt", rtn);

		return modelAndView;
	}

	/**
	 * 게이트 스케줄 타입 삭제
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/scheduleDelete.do")
	public ModelAndView scheduleDelete(@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String fsidx = StringUtil.isNullToString(commandMap.get("fsidx")).trim();
    	GateShTypeVO vo = new GateShTypeVO();
    	vo.setFsidx(fsidx);

    	rtn = terminalInfoService.deleteGateScheduleType (vo);
		modelAndView.addObject("rstCnt", rtn);

		return modelAndView;
	}

	/**
	 * 게이트 단말기 스케줄 전송
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/terminalShSend.do")
	public ModelAndView terminalShSend (@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String fsidx = StringUtil.isNullToString(commandMap.get("fsidx")).trim();

    	//String test = "MTI0AAAAAAAAAAAAAAAAAAAAAAA1NgAAAAAAAAAAAAAAAAAAAAAAAFNjaGVkdWxlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADMzAAAAAAAAAAAAAAAAAAAAAAAAMjIzAAAAAAAAAAAAAAAAAAAAAABpZHgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAyMDE5LTEyLTIyIDIyOjA1OjI4LjE1M18wMDI5MzMzNTQ=";

    	byte[] by = {49,50,52,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,53,54,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,83,99,104,101,100,117,108,101,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,51,51,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,50,50,51,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,105,100,120,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};
    	byte[] by2 = fsidx.getBytes("UTF-8");
    	byte[] byt = new byte[by.length + by2.length];
    	System.arraycopy(by, 0, byt, 0, by.length);
    	System.arraycopy(by2, 0, byt, by.length, by2.length);

    	/*for(byte b : byt) {
    		LOGGER.debug("b:::"+b);
    	}*/

    	int rtncnt = 0;
    	if(!fsidx.equals("")) {
    		String olst = StringUtil.isNullToString(commandMap.get("tdArr")).trim();
    		Encoder encoder = Base64.getEncoder();
            String encodedString = encoder.encodeToString(byt);
            //LOGGER.debug(encodedString);

        	JSONParser parser = new JSONParser();
        	Object obj = parser.parse( olst );
        	JSONArray jsonArray = (JSONArray) obj;

        	List<Map<String, Object>> lst = CommonUtils.getListMapFromJsonArray (jsonArray);

        	for(Map m : lst) {
        		//LOGGER.debug("dddd>>>"+m);
        		String fgid = StringUtil.isNullToString(m.get("fgid"));
        		if(!fgid.equals("")) {
        			TcmdMainVO vo = new TcmdMainVO ();
        			String strFs = "ttcmd_main";
            		commonService.tcsidxSave(strFs);
            		vo.setFsidx(commonService.getFsidx(strFs));
            		vo.setFgid(fgid);
            		vo.setFtx("CMD_SET_TERMINAL_OPTION");
            		vo.setFex("Schedule;");
            		vo.setFvalue(encodedString);
            		if (fgid.startsWith("11")) rtn = terminalInfoService.terminalShSend11 (vo);
            		else if (fgid.startsWith("12")) rtn = terminalInfoService.terminalShSend12 (vo);
            		if(rtn > 0) rtncnt++;
        		}
        	}
    	}
    	//LOGGER.debug("dasdaskdasd:::"+rtn);
		modelAndView.addObject("rstCnt", rtncnt);

		return modelAndView;
	}

	/**
	 * 게이트 단말기 메세지 전송
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/terminalShMsgSend.do")
	public ModelAndView terminalShMsgSend (@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String fex = StringUtil.isNullToString(commandMap.get("fex")).trim();
    	String fvalue = StringUtil.isNullToString(commandMap.get("fvalue")).trim();

    	String olst = StringUtil.isNullToString(commandMap.get("tdArr")).trim();
    	JSONParser parser = new JSONParser();
    	Object obj = parser.parse( olst );
    	JSONArray jsonArray = (JSONArray) obj;

    	List<Map<String, Object>> lst = CommonUtils.getListMapFromJsonArray (jsonArray);

    	int rtncnt = 0;
    	for(Map m : lst) {
    		String fgid = StringUtil.isNullToString(m.get("fgid"));
    		if(!fgid.equals("")) {
    			TcmdMainVO vo = new TcmdMainVO ();
    			String strFs = "ttcmd_main";
        		commonService.tcsidxSave(strFs);
        		vo.setFsidx(commonService.getFsidx(strFs));
        		vo.setFgid(fgid);
        		vo.setFtx("CMD_SEND_MESSAGE");
        		vo.setFex(fex);
        		vo.setFvalue(fvalue);
        		if (fgid.startsWith("11")) rtn = terminalInfoService.terminalShSend11 (vo);
        		else if (fgid.startsWith("12")) rtn = terminalInfoService.terminalShSend12 (vo);
        		if(rtn > 0) rtncnt++;
    		}
    	}
		modelAndView.addObject("rstCnt", rtncnt);

		return modelAndView;
	}

	/**
	 * 단말기스케줄 로그
	 * @param commandMap 파라메터전달용 commandMap
	 * @return userInfo/user_management
	 * @throws Exception
	 */
	@RequestMapping(value="/terminalInfo/terminalLogMngmt.do")
	public String terminalLogMngmt (ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String startDate = StringUtil.isNullToString(commandMap.get("startDate"));
		String startTime = StringUtil.isNullToString(commandMap.get("startTime"));
		String endDate = StringUtil.isNullToString(commandMap.get("endDate"));
		String endTime = StringUtil.isNullToString(commandMap.get("endTime"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("stDateTime"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("edDateTime"));
		String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchFtx = StringUtil.isNullToString(commandMap.get("srchFtx"));
		String srchFlnam = StringUtil.isNullToString(commandMap.get("srchFlnam"));
		String srchFstat = StringUtil.isNullToString(commandMap.get("srchFstat"));
		String srchFtxnm = StringUtil.isNullToString(commandMap.get("srchFtxnm"));

		String searchItemArea = StringUtil.isNullToString(request.getParameter("search_item_area"));
		String searchItemFloor = StringUtil.isNullToString(request.getParameter("search_item_floor"));

		//LOGGER.debug("dasdkadlskdad:::"+srchFtx);

		DateTimeVO dateTimeVO = commonService.getDateTime();

		if(StringUtil.isEmpty(startDate)) startDate = dateTimeVO.getYesterday();
		if(StringUtil.isEmpty(startTime)) startTime = "00:00:00";
		if(StringUtil.isEmpty(endDate)) endDate = dateTimeVO.getToday();
		if(StringUtil.isEmpty(endTime)) endTime = dateTimeVO.getCurrenttime();

		stDateTime = startDate + " " + startTime;
		edDateTime = endDate + " " + endTime;

		String strFtid = "";
		if(!StringUtil.isEmpty(searchItemFloor)) strFtid= searchItemFloor;
		else if (!StringUtil.isEmpty(searchItemArea)) strFtid= searchItemArea;	/*건물*/

		//센터 코드가져오기
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());

		if(srchCond.isEmpty()) srchCond = loginVO.getSite_id();

		TerminalSchLogVO terSchvo = new TerminalSchLogVO ();
		terSchvo.setStartDate(startDate);
		terSchvo.setStartTime(startTime);
		terSchvo.setStartDateTime(startDate + " " + startTime);
		terSchvo.setEndDate(endDate);
		terSchvo.setEndTime(endTime);
		terSchvo.setEndDateTime(endDate + " " + endTime);
		terSchvo.setSrchCond(srchCond);
		terSchvo.setStartDateTime(stDateTime);
		terSchvo.setEndDateTime(edDateTime);
		terSchvo.setSrchFtx(srchFtx);
		terSchvo.setSrchFlnam(srchFlnam);
		terSchvo.setSrchFstat(srchFstat);
		terSchvo.setSearchItemArea(searchItemArea);
		terSchvo.setSearchItemFloor(searchItemFloor);
		terSchvo.setFtid(strFtid);
		terSchvo.setSrchFtxnm(srchFtxnm);

		//단말기 스케줄 전체 목록 대전
		int totalCnt = terminalInfoService.getTerminalLogTotalCnt(terSchvo);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(terSchvo.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(terSchvo.getCurPageUnit());
		pageVO.calcPageList();

		terSchvo.setSrchPage(srchPage);
		terSchvo.autoOffset();

		//카드별 출입실패이력
		List<TerminalSchLogVO> terminalLogInfo = terminalInfoService.getTerminalLogList(terSchvo);

		model.addAttribute("centerCombo", centerCombo);
		model.addAttribute("terSchvo", terSchvo);
		model.addAttribute("pagination",pageVO);
		model.addAttribute("terminalLogInfo", terminalLogInfo);
		//model.addAttribute("searchItemFloor",searchItemFloor);
		//model.addAttribute("searchItemArea",searchItemArea);
		//model.addAttribute("searchItemCenter",fgId);
		//model.addAttribute("srchCondWord",srchCondWord);

		return "cubox/terminalInfo/terminal_sch_log_management";
	}

	/**
	 * 출입이력 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "terminalInfo/terminalLogExcelDownload.do")
	public ModelAndView terminalLogExcelDownload (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		String rowText = StringUtil.isNullToString(commandMap.get("rowTextArr"));
		String rowData = StringUtil.isNullToString(commandMap.get("rowDataArr"));

		String startDate = StringUtil.isNullToString(commandMap.get("startDate"));
		String startTime = StringUtil.isNullToString(commandMap.get("startTime"));
		String endDate = StringUtil.isNullToString(commandMap.get("endDate"));
		String endTime = StringUtil.isNullToString(commandMap.get("endTime"));
		String stDateTime = StringUtil.isNullToString(commandMap.get("stDateTime"));
		String edDateTime = StringUtil.isNullToString(commandMap.get("edDateTime"));
		String srchCond = StringUtil.isNullToString(commandMap.get("srchCond"));
		String srchFtx = StringUtil.isNullToString(commandMap.get("srchFtx"));
		String srchFlnam = StringUtil.isNullToString(commandMap.get("srchFlnam"));
		String srchFstat = StringUtil.isNullToString(commandMap.get("srchFstat"));
		String srchFtxnm = StringUtil.isNullToString(commandMap.get("srchFtxnm"));

		String searchItemArea = StringUtil.isNullToString(request.getParameter("search_item_area"));
		String searchItemFloor = StringUtil.isNullToString(request.getParameter("search_item_floor"));

		DateTimeVO dateTimeVO = commonService.getDateTime();

		if(StringUtil.isEmpty(startDate)) startDate = dateTimeVO.getYesterday();
		if(StringUtil.isEmpty(startTime)) startTime = "00:00:00";
		if(StringUtil.isEmpty(endDate)) endDate = dateTimeVO.getToday();
		if(StringUtil.isEmpty(endTime)) endTime = dateTimeVO.getCurrenttime();

		stDateTime = startDate + " " + startTime;
		edDateTime = endDate + " " + endTime;

		String strFtid = "";
		if(!StringUtil.isEmpty(searchItemFloor)) strFtid= searchItemFloor;
		else if (!StringUtil.isEmpty(searchItemArea)) strFtid= searchItemArea;	/*건물*/

		if(srchCond.isEmpty()) srchCond = loginVO.getSite_id();

		String[] rsText = rowText.split(",");

		TerminalSchLogVO terSchvo = new TerminalSchLogVO ();
		terSchvo.setStartDate(startDate);
		terSchvo.setStartTime(startTime);
		terSchvo.setStartDateTime(startDate + " " + startTime);
		terSchvo.setEndDate(endDate);
		terSchvo.setEndTime(endTime);
		terSchvo.setEndDateTime(endDate + " " + endTime);
		terSchvo.setSrchCond(srchCond);
		terSchvo.setStartDateTime(stDateTime);
		terSchvo.setEndDateTime(edDateTime);
		terSchvo.setSrchFtx(srchFtx);
		terSchvo.setSrchFlnam(srchFlnam);
		terSchvo.setSrchFstat(srchFstat);
		terSchvo.setRowData(rowData);
		terSchvo.setRowText(rowText);
		terSchvo.setSearchItemArea(searchItemArea);
		terSchvo.setSearchItemFloor(searchItemFloor);
		terSchvo.setFtid(strFtid);
		terSchvo.setSrchFtxnm(srchFtxnm);

		List<ExcelVO> resultCellList = terminalInfoService.getTerminalLogCellList(terSchvo);

		modelAndView.setViewName("excelDownloadView");
        modelAndView.addObject("resultList", resultCellList);
        modelAndView.addObject("excelName", "단말기제어이력");
        modelAndView.addObject("excelHeader", rsText);

		return modelAndView;
    }

	/**
	 * 게이트 단말기 제어 정보 전송
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/terminalOperationSend.do")
	public ModelAndView terminalOperationSend (@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String opCd = StringUtil.isNullToString(commandMap.get("operationCode")).trim();
    	String fvalue = StringUtil.isNullToString(commandMap.get("fvalue")).trim();

    	int rtncnt = -1;

    	if(!StringUtil.isEmpty(opCd) && !StringUtil.isEmpty(fvalue)) {
    		rtncnt = 0;

	    	String olst = StringUtil.isNullToString(commandMap.get("tdArr")).trim();
	    	JSONParser parser = new JSONParser();
	    	Object obj = parser.parse( olst );
	    	JSONArray jsonArray = (JSONArray) obj;

	    	List<Map<String, Object>> lst = CommonUtils.getListMapFromJsonArray (jsonArray);

	    	for(Map m : lst) {
	    		String fgid = StringUtil.isNullToString(m.get("fgid"));
	    		if(!fgid.equals("")) {
	    			GateOperationVO vo = new GateOperationVO ();
	    			String strFs = "ttcmd_main";
	        		commonService.tcsidxSave(strFs);
	        		vo.setSidx(commonService.getFsidx(strFs));
	        		vo.setGid(fgid);
	        		vo.setProcid("MAIN");
	        		vo.setTx(opCd);
	        		vo.setValue(fvalue);
	        		if (fgid.startsWith("11")) rtn = gateInfoService.gateOperationFor11 (vo);
	        		else if (fgid.startsWith("12")) rtn = gateInfoService.gateOperationFor12 (vo);
	        		if(rtn > 0) rtncnt++;
	    		}
	    	}
    	}
		modelAndView.addObject("rstCnt", rtncnt);

		return modelAndView;
	}


	/**
	 * 시스템, 단말기 초기화
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/terminalOperationReset.do")
	public ModelAndView terminalOperationReset (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	GateVO gVo = new GateVO();
    	List<GateOperationVO> wList = new ArrayList<GateOperationVO>();
    	List<GateVO> gList = terminalInfoService.getTerminalFillList(gVo);

    	for(GateVO m : gList) {
    		String fgid = m.getFgid();
    		if(!fgid.equals("")) {
    			GateOperationVO vo = new GateOperationVO ();
    			String strFs = "ttcmd_main";
        		commonService.tcsidxSave(strFs);
        		vo.setSidx(commonService.getFsidx(strFs));
        		vo.setGid(fgid);
        		vo.setProcid("MAIN");
        		vo.setTx("CMD_TERMINAL_RESET");
        		vo.setValue("0");
        		wList.add(vo);
    		}
    	}
    	gateInfoService.allSystemReset(wList);
		modelAndView.addObject("rstCnt", rtn);

		return modelAndView;
	}



	/**
	 * 단말기 얼굴매칭 타입 변경
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/terminalMatchTypeChange.do")
	public ModelAndView terminalMatchTypeChange (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

		String gid = (String) commandMap.get("gid");
		String value = (String) commandMap.get("value");

		HashMap sMap = new HashMap();
		//Server 서버매칭, Client 단말매칭
		if(value.equals("Server") || value.equals("Client")){
			String strFs = "ttcmd_main";
			commonService.tcsidxSave(strFs);
			sMap.put("fsidx", commonService.getFsidx(strFs));
			sMap.put("flh", value);
			sMap.put("fgid", gid);
	    	gateInfoService.gateMatchTypeChange(sMap);
			rtn =  1;
		}else{
			rtn =  -1;
		}

		modelAndView.addObject("rstCnt", rtn);

		return modelAndView;
	}
	
	/**
	 * 단말기 출입인증방식 변경
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/terminalAuthTypeChange.do")
	public ModelAndView terminalAuthTypeChange (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

		String gid = (String) commandMap.get("gid");
		String value = (String) commandMap.get("value");

		HashMap sMap = new HashMap();
		//Server 서버매칭, Client 단말매칭
		if(value.equals("C") || value.equals("CF") || value.equals("F") || value.equals("FC")){
			String strFs = "ttcmd_main";
			commonService.tcsidxSave(strFs);
			sMap.put("fsidx", commonService.getFsidx(strFs));
			sMap.put("fli", value);
			sMap.put("fgid", gid);
	    	gateInfoService.gateAuthTypeChange(sMap);
			rtn =  1;
		}else{
			rtn =  -1;
		}

		modelAndView.addObject("rstCnt", rtn);

		return modelAndView;
	}
	
	
	/**
	 * 이미지 압축파일 비밀번호 설정
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@RequestMapping(value = "/terminalInfo/fileImgZipSetPw.do")
	public ModelAndView fileImgZipSetPw (@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

    	String setpassword = StringUtil.isNullToString(commandMap.get("setpassword")).trim();
    	HashMap mbfpw = terminalInfoService.getZipSetPw ();
    	String bfpw = "";

    	if(mbfpw!=null) {
    		bfpw = (String) mbfpw.get("FPW");
    	}

    	try {
	    	HashMap<String, String> m = new HashMap<String, String>();
			m.put("fip", commonUtils.getIPFromRequest(request));
			m.put("fpw", setpassword);	//reason
			rtn = terminalInfoService.insertFileImgZipPassword (m);
			LOGGER.debug("mmmm >> "+m);

	    	if(rtn > 0) {
	    		String stridx = StringUtil.isNullToString(m.get("fdwnidx"));
	    		terminalInfoService.zipFilePasswordReset (bfpw, setpassword, stridx);
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
			rtn = -1;
		}
    	modelAndView.addObject("rst", rtn);
		return modelAndView;
	}

	/**
	 * 이미지 압축파일 비밀번호 설정
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/getZipSetPw.do")
	public ModelAndView getZipSetPw (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	String ZIP_PW = CuboxProperties.getProperty("Globals.zippassword");
		try {
	    	HashMap mbfpw = terminalInfoService.getZipSetPw ();
	    	modelAndView.addObject("pw", mbfpw!=null&&mbfpw.get("FPW") == null ? ZIP_PW : mbfpw.get("FPW").toString() );
	    	modelAndView.addObject("uDt", mbfpw!=null&&mbfpw.get("FREGDT") == null ? "없음" : mbfpw.get("FREGDT").toString().substring(0, 19) );
	    } catch (Exception e) {
			modelAndView.addObject("pw", ZIP_PW);
		}
		return modelAndView;
	}
	

	/**
	 * 단말기 저장
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/gateAddSave.do")
	public ModelAndView gateAddSave (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String flname = (String) commandMap.get("flname");
		String fip01 = (String) commandMap.get("fip01");
		String fip02 = (String) commandMap.get("fip02");
		String fip03 = (String) commandMap.get("fip03");
		String fip04 = (String) commandMap.get("fip04");
		String fauthtype = (String) commandMap.get("fauthtype2");
		String ftmtype = (String) commandMap.get("ftmtype");
		String fvname = (String) commandMap.get("fvname");
		String fsiteId = (String) commandMap.get("fsiteId");
		
		String fip = "";
		if(fip01!=null && fip02!=null && fip03!=null && fip04!=null && !fip01.equals("") && !fip02.equals("") && !fip03.equals("") && !fip04.equals("")) {
			fip = fip01 + "." + fip02 + "." + fip03 + "." + fip04;
		}
		
		GateVO vo = new GateVO();
		
		vo.setFlname(flname);
		vo.setFip(fip);
		vo.setFli(fauthtype);
		vo.setFlh(ftmtype);
		vo.setFpartcd1(fsiteId);
		vo.setFvname(fvname);
		
		//사이트 아이디 GID 생성 절차 GID 10자리 앞3자리는 사이트코드 +7자리 
		//1.처음 시작일때 값이 있는지 체크
		//2-1.없으면  0000000으로 시작
		//2-2.있으면 사이트 아이디를 제외하고 나머지 숫자 MAX +1

		int addCnt = 0;
		try {
			addCnt = terminalInfoService.gateAddSave(vo);	
			modelAndView.addObject("result", "success");
		} catch(Exception e) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}
		
		return modelAndView;
	}
	
	/**
	 * 
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/fnGateChangeSave.do")
	public ModelAndView fnGateChangeSave (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fpartcd1 = loginVO.getSite_id();

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String fgid = (String) commandMap.get("fgid");
		String flname = (String) commandMap.get("flname");
		String fip01 = (String) commandMap.get("fip01");
		String fip02 = (String) commandMap.get("fip02");
		String fip03 = (String) commandMap.get("fip03");
		String fip04 = (String) commandMap.get("fip04");
		String fauthtype = (String) commandMap.get("fauthtype2");
		String ftmtype = (String) commandMap.get("ftmtype");
		String fvname = (String) commandMap.get("fvname");
		
		String fip = "";
		if(fip01!=null && fip02!=null && fip03!=null && fip04!=null && !fip01.equals("") && !fip02.equals("") && !fip03.equals("") && !fip04.equals("")) {
			fip = fip01 + "." + fip02 + "." + fip03 + "." + fip04;
		}
		
		GateVO vo = new GateVO();
		vo.setFgid(fgid);
		vo.setFlname(flname);
		vo.setFip(fip);
		vo.setFli(fauthtype);
		vo.setFlh(ftmtype);
		vo.setFpartcd1(fpartcd1);
		vo.setFvname(fvname);

		int addCnt = 0;
		addCnt = terminalInfoService.gateChangeSave(vo);
		
		return modelAndView;
	}
	
	/**
	 * 계정사용유무변경
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/gateFuseynChangeSave.do")
	public ModelAndView gateFuseynChangeSave(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String fgid = (String) commandMap.get("fgid");
		String fuseyn = (String) commandMap.get("fuseyn");

		GateVO vo = new GateVO();
		vo.setFgid(fgid);
		vo.setFuseyn(fuseyn);


		int addCnt = terminalInfoService.gateFuseynChangeSave(vo);

		String fuseynm = fuseyn.equals("Y") ? "사용중" : "사용안함" ;
		String fdetail = "GID : " + fgid + ", " + fuseynm;
		if(addCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "11111201", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "11111202", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("addCnt", addCnt);

		return modelAndView;
	}
	
	@ResponseBody
	@RequestMapping(value = "/terminalInfo/getGateList.do")
	public ModelAndView getTotalAuthorGroup (@RequestParam Map<String, Object> param, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	//단말기목록조회
    	GateVO vo = new GateVO();
    	vo.setFpartcd1(StringUtil.nvl(param.get("srchPartCd1"), loginVO.getSite_id()));
    	vo.setFlname(StringUtil.isNullToString(param.get("srchGateName")));
    	List<GateVO> list = terminalInfoService.getGateList(vo);
		modelAndView.addObject("list", list);

		return modelAndView;
	}
}
