package aero.cubox.sample.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.commons.DefaultBeanValidator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.rte.fdl.property.EgovPropertyService;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import aero.cubox.sample.service.BasicInfoService;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.GateInfoService;
import aero.cubox.sample.service.vo.CenterInfoVO;
import aero.cubox.sample.service.vo.ComboVO;
import aero.cubox.sample.service.vo.DeviceInfoVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateInfoVO;
import aero.cubox.sample.service.vo.GateOperationVO;
import aero.cubox.sample.service.vo.GatePositionVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.SiteVO;

@Controller
public class GateInfoController {
	
	/** gateInfoService */
	@Resource(name = "gateInfoService")
	private GateInfoService gateInfoService;
	
	/** basicInfoService */
	@Resource(name = "basicInfoService")
	private BasicInfoService basicInfoService;
	
	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;
		
	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;
		
	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	private static final Logger LOGGER = LoggerFactory.getLogger(GateInfoController.class);
	
	
	/**
	 * 관리 페이지 초기 화면
	 * @return gateInfo/gate_management
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/gateMngmt.do")
	public String gateMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			CenterInfoVO centerInfoVO = new CenterInfoVO();
			centerInfoVO.setFkind1("code");
			centerInfoVO.setFkind2("centercd");
			List<CenterInfoVO> centerInfoList = basicInfoService.getCenterInfoList(centerInfoVO);
			
			model.addAttribute("centerInfoList", centerInfoList);
			model.addAttribute("loginInfo", loginVO);
			model.addAttribute("menuPath", "gateInfo/gate_management");
			
			return "cubox/cuboxSubContents";
		} else {
			return "redirect:/login.do";
		}
	}
	
	/**
	 * 센터 별 지역 정보
	 * @param ftid 
	 * @return gateInfo/gateListToFloor
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/gateInfo/areaListToCenter.do")
	public ModelAndView areaListToCenter(@RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {

			ModelAndView modelAndView = new ModelAndView();
	    	modelAndView.setViewName("jsonView");
			
			List<ComboVO> areaListToCenter = gateInfoService.getAreaListToCenter((String) commandMap.get("fptid"));
						
			modelAndView.addObject("areaListToCenter", areaListToCenter);
			
			return modelAndView;
	}
	/**
	 * 센터 별 지역 정보
	 * @param ftid 
	 * @return gateInfo/gateListToFloor
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/gateInfo/floorListToCenter.do")
	public ModelAndView floorListToCenter(@RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		List<ComboVO> floorListToCenter = gateInfoService.getFloorListToCenter((String) commandMap.get("siteId"));
		
		modelAndView.addObject("floorListToCenter", floorListToCenter);
		
		return modelAndView;
	}
	
	
	/**
	 * 지역 별 층 정보
	 * @param ftid 
	 * @return gateInfo/gateListToFloor
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/gateInfo/floorListToArea.do")
	public ModelAndView floorListToArea(@RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
		
		List<ComboVO> floorListToArea = gateInfoService.getFloorListToArea((String) commandMap.get("fptid"));
					
		modelAndView.addObject("floorListToArea", floorListToArea);
		
		return modelAndView;
	}
	
	
	/**
	 * 층 별 단말기 정보
	 * @param ftid 
	 * @return gateInfo/gateListToFloor
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/gateInfo/gateListToFloor.do")
	public ModelAndView gateListToArea(@RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
    	
		String fptid = StringUtil.isNullToString(commandMap.get("fptid"));
		
		List<GateInfoVO> gateInfoList = gateInfoService.getGateListToFloor(fptid);
		
		modelAndView.setViewName("jsonView");
		modelAndView.addObject("gateInfoList", gateInfoList);
		
		return modelAndView;
	}
	
	/**
	 * 층 별 단말기 정보
	 * @param ftid 
	 * @return gateInfo/gateListToFloor
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/gateInfo/gateListToFloor2.do")
	public ModelAndView gateListToArea2(@RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
		
    	String searchItemCenter = StringUtil.isNullToString(commandMap.get("searchItemCenter"));
    	String searchItemArea = StringUtil.isNullToString(commandMap.get("searchItemArea"));
    	String searchItemFloor = StringUtil.isNullToString(commandMap.get("searchItemFloor"));
    	
    	LOGGER.debug("searchItemCenter = " + searchItemCenter);
    	LOGGER.debug("searchItemArea = " + searchItemArea);
    	LOGGER.debug("searchItemFloor = " + searchItemFloor);
    	
		GateInfoVO gateInfoVO = new GateInfoVO();
    	gateInfoVO.setSearchItemCenter(searchItemCenter);
    	gateInfoVO.setSearchItemArea(searchItemArea);
    	gateInfoVO.setSearchItemFloor(searchItemFloor);
    	
		//List<GateInfoVO> gateInfoList = gateInfoService.getGateListToFloor((String) commandMap.get("fptid"));
    	List<GateInfoVO> gateInfoList = gateInfoService.getGateListToFloor2(gateInfoVO);
    	
		modelAndView.addObject("gateInfoList", gateInfoList);
		
		return modelAndView;
	}
	
	/**
	 * 단말기 제어
	 * @param operationList 
	 * @return gateInfo/gateOperation
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/gateOperation.do")
	public ModelAndView gateOperation(@RequestParam Map<String, Object> commandMap, String[] operationList,
			HttpServletRequest request) throws Exception {
		
		String fcode = "ttcmd_main";
		String centerCode = (String)commandMap.get("centerCode");
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	for(String operation : operationList){
    		GateOperationVO gateOperationVO = new GateOperationVO();
    		commonService.tcsidxSave(fcode);
    		gateOperationVO.setSidx(commonService.getFsidx(fcode));
    		gateOperationVO.setGid(operation);
    		gateOperationVO.setProcid("MAIN");
    		gateOperationVO.setTx((String) commandMap.get("operationCode"));
    		gateOperationVO.setValue((String) commandMap.get("value"));
    		
    		if(centerCode.equals("11")){
    			gateInfoService.gateOperationFor11(gateOperationVO);
    		}
    		else {
    			gateInfoService.gateOperationFor12(gateOperationVO);
    		}    		
    	}  	
    	
    	return modelAndView;
	}
	
	/**
	 * 단말기 위치 변경
	 * @param operationList 
	 * @return gateInfo/gateOperation
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/gatePosition.do")
	public ModelAndView gatePosition(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		ModelAndView modelAndView = new ModelAndView();
		
    	modelAndView.setViewName("jsonView");

    	List<Map<String,String>> positionList = new ArrayList<Map<String, String>>();
    	positionList = mapper.readValue((String) commandMap.get("positionList"), new TypeReference<List<Map<String, String>>>(){});
    	
    	for(Map<String, String> position : positionList){
    		GatePositionVO gatePositionVO = new GatePositionVO();
    		gatePositionVO.setTid(position.get("tid"));
    		gatePositionVO.setVx(position.get("vx"));
    		gatePositionVO.setVy(position.get("vy"));
    		
    		gateInfoService.updateGatePosition(gatePositionVO);
    	}
    	
    	return modelAndView;
	}
	
	/**
	 * 단말기별 사용자 초기 화면 (사용안함)
	 * @return /gateInfo/gateUserMngmt.do
	 * @throws Exception
	 */
	/*@RequestMapping(value="/gateInfo/gateUserMngmt.do")
	public String gateUserMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			CenterInfoVO centerInfoVO = new CenterInfoVO();
			centerInfoVO.setFkind1("code");
			centerInfoVO.setFkind2("centercd");
			List<CenterInfoVO> centerInfoList = basicInfoService.getCenterInfoList(centerInfoVO);
			
			int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
			String searchItemCenter = StringUtil.isNullToString(request.getParameter("search_item_center"));
			String searchItemArea = StringUtil.isNullToString(request.getParameter("search_item_area"));
			String searchItemFloor = StringUtil.isNullToString(request.getParameter("search_item_floor"));
			String searchUserName = StringUtil.isNullToString(request.getParameter("userName"));
			String gateList = StringUtil.isNullToString(request.getParameter("gateList"));
			
			int totalCnt = 0;	
			List<ComboVO> areaListToCenter = null;
			List<ComboVO> floorListToArea = null;
			List<GateInfoVO> gateInfoList = null;
			if(!searchItemCenter.equals("0")){
				areaListToCenter = gateInfoService.getAreaListToCenter(searchItemCenter);	
				floorListToArea = gateInfoService.getFloorListToArea(searchItemArea);	
				gateInfoList = gateInfoService.getGateListToFloor(searchItemFloor);
			}
			
			List<UserInfoVO> userList = new ArrayList<UserInfoVO>();
			GateInfoVO gateInfo = new GateInfoVO();
			gateInfo.setSrchPage(srchPage);
			gateInfo.setUnm(searchUserName);
			gateInfo.autoOffset();
			
			if(gateList != ""){
				gateInfo.setGateList(gateList.split(","));
				
				if(searchItemCenter.equals("11")){
					userList = gateInfoService.getUserListToGateFor11(gateInfo);
					totalCnt = gateInfoService.getUserListToGateTotalCountFor11(gateInfo);
				}
				else {
					userList = gateInfoService.getUserListToGateFor12(gateInfo);
					totalCnt = gateInfoService.getUserListToGateTotalCountFor12(gateInfo);
				}
			}
				
				
			PaginationVO pageVO = new PaginationVO();
			pageVO.setCurPage(srchPage);
			pageVO.setRecPerPage(gateInfo.getSrchCnt());
			pageVO.setTotRecord(totalCnt);
			pageVO.setUnitPage(gateInfo.getCurPageUnit());
			pageVO.calcPageList();
			
			model.addAttribute("userList", userList);
			model.addAttribute("pagination", pageVO);
			model.addAttribute("gateInfo", gateInfo);
			model.addAttribute("searchItemCenter", searchItemCenter);
			model.addAttribute("searchItemArea", searchItemArea);
			model.addAttribute("searchItemFloor", searchItemFloor);
			model.addAttribute("gateList", gateList);
			
			model.addAttribute("areaListToCenter", areaListToCenter);
			model.addAttribute("floorListToArea", floorListToArea);
			model.addAttribute("gateInfoList", gateInfoList);
			
			model.addAttribute("centerInfoList", centerInfoList);
			model.addAttribute("loginInfo", loginVO);
			model.addAttribute("menuPath", "gateInfo/gate_user_management");
			
			return "cubox/cuboxSubContents";
		} else {
			return "redirect:/login.do";
		}
	}*/
	
    /**
	 * 출입자 엑셀 다운로드 (사용안함)
	 * @param 
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	/*@RequestMapping(value = "/gateInfo/excelDownload.do")
	public ModelAndView excelDownload(HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			String chkValueArray = StringUtil.isNullToString(request.getParameter("chkValueArray"));
			String chkTextArray = StringUtil.isNullToString(request.getParameter("chkTextArray"));
			String searchUserName = StringUtil.isNullToString(request.getParameter("userName"));
			String searchItemCenter = StringUtil.isNullToString(request.getParameter("search_item_center"));
			String gateList = StringUtil.isNullToString(request.getParameter("gateList"));
			
			List<ExcelVO> userList = new ArrayList<ExcelVO>();
			GateInfoVO gateInfo = new GateInfoVO();
			gateInfo.setGateList(gateList.split(","));
			gateInfo.setUnm(searchUserName);
			gateInfo.setChkTextArray(chkTextArray);
			gateInfo.setChkValueArray(chkValueArray);
			
			String[] chkText = chkTextArray.split(",");
			
			if(searchItemCenter.equals("11")){
				userList = gateInfoService.getExcelUserListToGateFor11(gateInfo);
			}
			else {
				userList = gateInfoService.getExcelUserListToGateFor12(gateInfo);
			}
			
			modelAndView.setViewName("excelDownloadView");
	        modelAndView.addObject("resultList", userList);
	        modelAndView.addObject("excelName", "단말기별 출입자 리스트");
	        modelAndView.addObject("excelHeader", chkText);
		}
		
		return modelAndView;
	}*/
	
	/**
	 * 관리 페이지 초기 화면
	 * @return gateInfo/gate_management
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/gate_monitor_management.do")
	public String gateMonitorMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		//getMenuCl(String author)

		//totalAuthGroup = authInfoService.getAuthGroupList(authGroupVO);
		/*
		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("authGroupVO", authGroupVO);
		model.addAttribute("groupAuthList", totalAuthGroup);
		 */
		return "cubox/gateInfo/gate_monitor_management";
	}
	
	/**
	 * 관리 페이지 초기 화면
	 * @return gateInfo/gate_management
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/new_gate_monitor_management.do")
	public String newGateMonitorMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//센터 코드가져오기
		//getMenuCl(String author)

		//totalAuthGroup = authInfoService.getAuthGroupList(authGroupVO);
		/*
		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("authGroupVO", authGroupVO);
		model.addAttribute("groupAuthList", totalAuthGroup);
		 */
		
		model.addAttribute("centerCombo",centerCombo);
		
		return "cubox/gateInfo/new_gate_monitor_management";
	}
	
	/**
	 * 층 별 단말기 정보
	 * @param ftid 
	 * @return gateInfo/gateListToFloor
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/gateInfo/newGateListToFloor.do")
	public ModelAndView newGateListToFloor(@RequestParam Map<String, Object> commandMap, 
			HttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
		
		FloorVO vo = new FloorVO();
    	
		String floor = StringUtil.isNullToString(commandMap.get("floor"));
		String siteId = StringUtil.isNullToString(commandMap.get("siteId"));
		
		vo.setFloor(floor);
		vo.setSiteId(siteId);
		
		List<DeviceInfoVO> gateInfoList = gateInfoService.getNewGateListToFloor(vo);
		
		modelAndView.setViewName("jsonView");
		modelAndView.addObject("gateInfoList", gateInfoList);
		
		return modelAndView;
	}
	
	
	/**
	 * 단말기 위치 변경
	 * @param operationList 
	 * @return gateInfo/gateOperation
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/newGatePosition.do")
	public ModelAndView newGatePosition(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		ModelAndView modelAndView = new ModelAndView();
		
    	modelAndView.setViewName("jsonView");

    	List<Map<String,String>> positionList = new ArrayList<Map<String, String>>();
    	positionList = mapper.readValue((String) commandMap.get("positionList"), new TypeReference<List<Map<String, String>>>(){});
    	
    	for(Map<String, String> position : positionList){
    		DeviceInfoVO devicePositionVO = new DeviceInfoVO();
    		devicePositionVO.setDeviceId(position.get("deviceId"));
    		devicePositionVO.setxCoordinate(position.get("xCoordinate"));
    		devicePositionVO.setyCoordinate(position.get("yCoordinate"));
    		
    		gateInfoService.updateNewGatePosition(devicePositionVO);
    	}
    	
    	return modelAndView;
	}
	/**
	 * 단말기 위치 변경
	 * @param operationList 
	 * @return gateInfo/gateOperation
	 * @throws Exception
	 */
	@RequestMapping(value="/gateInfo/resetGatePosition.do")
	public ModelAndView resetGatePosition(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
		
    	modelAndView.setViewName("jsonView");

		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		String siteId = loginVO.getSite_id();
	
		
		DeviceInfoVO vo = new DeviceInfoVO();
		
		String floor = StringUtil.isNullToString(commandMap.get("deviceFloor"));
		vo.setDeviceFloor(floor);
		vo.setSiteId(siteId);
		
		gateInfoService.resetGatePosition(vo);
		
		
		return modelAndView;
	}
	
}
