package aero.cubox.sample.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.commons.DefaultBeanValidator;

import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.DeviceInfoService;
import aero.cubox.sample.service.GateInfoService;
import aero.cubox.sample.service.TerminalInfoService;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateShTypeVO;
import aero.cubox.sample.service.vo.GateVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.PaginationVO;
import aero.cubox.sample.service.vo.SiteVO;
import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class DeviceInfoController {

	/** deviceInfoService */
	@Resource(name = "deviceInfoService")
	private DeviceInfoService deviceInfoService;
	
	/** terminalInfoService */
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

	
	/**
	 * 단말기관리 View
	 * @param model
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deviceInfo/deviceMngmt.do")
	public String newTerminalScheduleMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String siteId = loginVO.getSite_id();

		String searchItemCenter = StringUtil.isNullToString(request.getParameter("search_item_center"));
		
		String srchCondWord = StringUtil.isNullToString(request.getParameter("srchCondWord"));
		String searchAccAuthType = StringUtil.isNullToString(request.getParameter("searchAccAuthType"));
		String searchFaceAuthType = StringUtil.isNullToString(request.getParameter("searchFaceAuthType"));
		String searchFloor = StringUtil.isNullToString(request.getParameter("searchFloor"));
		String srchUseYn = StringUtil.isNullToString(request.getParameter("srchUseYn"));
		
		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String srchRecPerPage = StringUtil.isNullToString(commandMap.get("srchRecPerPage"));

		//목록조회하기
		String deviceId = "";		/*대전*/
		
		if(!loginVO.getSite_id().equals("")) {
			deviceId = loginVO.getSite_id();
		} else if (!searchItemCenter.equals("")) {
			deviceId = searchItemCenter;
		}
		
		GateVO vo = new GateVO ();
		vo.setDeviceId(deviceId);
		
		vo.setDeviceNm(srchCondWord);
		vo.setDeviceDesc(srchCondWord);
		vo.setAccAuthType(searchAccAuthType);
		vo.setFaceAuthType(searchFaceAuthType);
		vo.setDeviceFloor(searchFloor);
		vo.setUseYn(srchUseYn);

		int totalCnt = deviceInfoService.getDeviceInfoListCnt(vo);
				
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(vo.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(vo.getCurPageUnit());
		pageVO.calcPageList();
		
		vo.setSrchPage(srchPage);
		vo.autoOffset();
		
		List<GateVO> gvo = deviceInfoService.getDeviceInfoList (vo);
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(deviceId); //센터 코드가져오기
		List<SiteVO> siteCombo = commonService.getSiteCodeList();
		List<CodeVO> authCombo = commonService.getCodeList("code","evttype"); //출입인증방식
		List<CodeVO> certCombo = commonService.getCodeList("combo","CERT_TYPE"); //얼굴인증방식		
		List<FloorVO> floorCombo = commonService.getFloorList(siteId);

		GateShTypeVO gtvo = new GateShTypeVO ();
		List<GateShTypeVO> gateShTypeList = terminalInfoService.getGateShType (gtvo); //센터 코드가져오기

		model.addAttribute("gateList",gvo);
		model.addAttribute("gateShTypeList",gateShTypeList);
		model.addAttribute("siteCombo",siteCombo);
		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("authCombo",authCombo);
		model.addAttribute("certCombo",certCombo);
		model.addAttribute("floorCombo", floorCombo);
		
		model.addAttribute("searchItemCenter",deviceId);
		model.addAttribute("srchCondWord",srchCondWord);
		
		model.addAttribute("searchAccAuthType",searchAccAuthType);
		model.addAttribute("searchFaceAuthType",searchFaceAuthType);
		model.addAttribute("searchFloor",searchFloor);
		model.addAttribute("srchUseYn",srchUseYn);
		
		model.addAttribute("pagination",pageVO);
		
		return "cubox/deviceInfo/device_management";
	}
	
	
	/**
	 * 단말기 정보 저장
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/deviceInfo/saveDeviceInfo.do")
	public ModelAndView saveDeviceInfo (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String deviceNm = (String) commandMap.get("deviceNm");
		String fip01 = (String) commandMap.get("fip01");
		String fip02 = (String) commandMap.get("fip02");
		String fip03 = (String) commandMap.get("fip03");
		String fip04 = (String) commandMap.get("fip04");
		String accAuthType = (String) commandMap.get("accAuthType");
		String faceAuthType = (String) commandMap.get("faceAuthType");
		String fvname = (String) commandMap.get("fvname");
		String siteId = (String) commandMap.get("siteId");
		String deviceFloor = (String) commandMap.get("deviceFloor");
		
		String deviceIp = "";
		if(fip01!=null && fip02!=null && fip03!=null && fip04!=null && !fip01.equals("") && !fip02.equals("") && !fip03.equals("") && !fip04.equals("")) {
			deviceIp = fip01 + "." + fip02 + "." + fip03 + "." + fip04;
		}
		
		GateVO vo = new GateVO();
		
		vo.setDeviceNm(deviceNm);
		vo.setDeviceIp(deviceIp);
		vo.setAccAuthType(accAuthType);
		vo.setFaceAuthType(faceAuthType);
		vo.setSiteId(siteId);
		vo.setFvname(fvname);
		vo.setDeviceFloor(deviceFloor);
		
		//사이트 아이디 GID 생성 절차 GID 10자리 앞3자리는 사이트코드 +7자리 
		//1.처음 시작일때 값이 있는지 체크
		//2-1.없으면  0000000으로 시작
		//2-2.있으면 사이트 아이디를 제외하고 나머지 숫자 MAX +1

		int addCnt = 0;
		try {
			addCnt = deviceInfoService.saveDeviceInfo(vo);	
			modelAndView.addObject("result", "success");
		} catch(Exception e) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}
		
		return modelAndView;
	}
	
	
	/**
	 * 단말기 정보 수정
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/deviceInfo/updateDeviceInfo.do")
	public ModelAndView updateDeviceInfo (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		 String fpartcd1 = loginVO.getSite_id();
	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
	
		String deviceId = (String) commandMap.get("deviceId");
		String flname = (String) commandMap.get("flname");
		String fip01 = (String) commandMap.get("fip01");
		String fip02 = (String) commandMap.get("fip02");
		String fip03 = (String) commandMap.get("fip03");
		String fip04 = (String) commandMap.get("fip04");
		String fauthtype = (String) commandMap.get("fauthtype2");
		String ftmtype = (String) commandMap.get("ftmtype");
		String fvname = (String) commandMap.get("fvname");
		String editDeviceDesc = (String) commandMap.get("editDeviceDesc");
		String editDeviceFloor = (String) commandMap.get("editDeviceFloor");
		
		FloorVO fvo = new FloorVO();
		fvo.setSiteId(fpartcd1);
		fvo.setFloor(editDeviceFloor);
		
		String deviceFloor = "";
		deviceFloor = deviceInfoService.getFloorIdx(fvo);
		
		String fip = "";
		if(fip01!=null && fip02!=null && fip03!=null && fip04!=null && !fip01.equals("") && !fip02.equals("") && !fip03.equals("") && !fip04.equals("")) {
			fip = fip01 + "." + fip02 + "." + fip03 + "." + fip04;
		}
		
		GateVO vo = new GateVO();
		vo.setDeviceId(deviceId);
		vo.setFlname(flname);
		vo.setDeviceIp(fip);
		vo.setFli(fauthtype);
		vo.setFlh(ftmtype);
		vo.setFpartcd1(fpartcd1);
		vo.setFvname(fvname);
		vo.setDeviceDesc(editDeviceDesc);
		vo.setDeviceFloor(deviceFloor);
				
		int addCnt = 0;
		addCnt = deviceInfoService.updateDeviceInfo(vo);
	
		return modelAndView;
	}

	/**
	 * 단말기 얼굴매칭 타입 변경
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/deviceInfo/updateDeviceMatchType.do")
	public ModelAndView updateDeviceMatchType (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

		String deviceId = (String) commandMap.get("deviceId");
		String value = (String) commandMap.get("value");

		HashMap sMap = new HashMap();
		//Server 서버매칭, Client 단말매칭
		if( value.equals("Server") || value.equals("Client") ){
			
			sMap.put("faceAuthType", value);
			sMap.put("deviceId", deviceId);
	    	gateInfoService.newGateMatchTypeChange(sMap);
			rtn =  1;
		} else {
			rtn =  -1;
		}

		modelAndView.addObject("rstCnt", rtn);

		return modelAndView;
	}
	
	
	/**
	 * 단말기 출입인증방식 변경
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/deviceInfo/updateAuthTypeChange.do")
	public ModelAndView updateAuthTypeChange (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn =  0;

		String deviceId = (String) commandMap.get("deviceId");
		String value = (String) commandMap.get("value");

		HashMap sMap = new HashMap();
		//Server 서버매칭, Client 단말매칭
		if( value.equals("C") || value.equals("CF") || value.equals("F") || value.equals("FC") ){
			
			sMap.put("accAuthType", value);
			sMap.put("deviceId", deviceId);
	    	gateInfoService.newGateAuthTypeChange(sMap);
			rtn =  1;
		} else {
			rtn =  -1;
		}

		modelAndView.addObject("rstCnt", rtn);

		return modelAndView;
	}


	/**
	 * 계정 사용유무 변경
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/deviceInfo/updateDeviceUse.do")
	public ModelAndView updateDeviceUse(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String deviceId = (String) commandMap.get("deviceId");
		String useYn = (String) commandMap.get("useYn");

		GateVO vo = new GateVO();
		vo.setDeviceId(deviceId);
		vo.setUseYn(useYn);

		int addCnt = deviceInfoService.updateDeviceInfoUseYN(vo);

		String fuseynm = useYn.equals("Y") ? "사용중" : "사용안함" ;
		String fdetail = "GID : " + deviceId + ", " + fuseynm;
		if( addCnt > 0 ){
			commonService.sysLogSave(loginVO.getFsiteid(), "11111201", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		} else {
			commonService.sysLogSave(loginVO.getFsiteid(), "11111202", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("addCnt", addCnt);

		return modelAndView;
	}
	

}
