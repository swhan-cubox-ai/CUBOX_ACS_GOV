package aero.cubox.user.controller;

import aero.cubox.auth.service.vo.AuthGroupVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.*;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoSearchVO;
import aero.cubox.user.service.vo.UserInfoVO;
import aero.cubox.util.*;
import aero.cubox.auth.service.AuthorGroupService;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.TerminalInfoService;
import aero.cubox.user.service.UserInfoService;
import egovframework.rte.fdl.property.EgovPropertyService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
@Controller
public class UserInfoController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoController.class);

	private String GLOBAL_AES256_KEY = CuboxProperties.getProperty("Globals.aes256.key");
	//private String GLOBAL_FILESTORAGE = CuboxProperties.getProperty("Globals.fileStorage");
	private String GLOBAL_DEFAULT_AUTHTYPE = CuboxProperties.getProperty("Globals.default.authtype");
	
	@Value("#{property['Globals.user.imagePath']}")
	private String gbUserImagePath;
	
	@Value("#{property['Globals.user.fpartnm1']}")
	private String gbUserFpartnm1;

	/** memberService */
	@Resource(name = "userInfoService")
	private UserInfoService userInfoService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "terminalInfoService")
	private TerminalInfoService terminalInfoService;

	@Resource(name = "authorGroupService")
	private AuthorGroupService authorGroupService;
	
	/* ???????????? ?????? ????????? */
	@Value("#{property['Globals.cardDigit']}")
	private String cardDigit;
	
	/* ???????????? ?????? ????????? */
	@Value("#{property['Globals.fidDigit']}")
	private String fidDigit;
	
	/* ????????? ???????????? */
	@Value("#{property['Globals.fillChar']}")
	private String fillChar;

	/* ????????? ?????? */
	@Value("#{property['Globals.fillDirection']}")
	private String fillDirection;
	
	
	public UserInfoVO setParamSearchUser(LoginVO loginVO, Map<String, Object> param) {
		
		UserInfoVO userInfoVO = new UserInfoVO();
		
		// paging
		String srchPage       = StringUtil.nvl(param.get("srchPage"), "1");
		String srchRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(userInfoVO.getSrchCnt()));
		// search
		String srchCond       = StringUtil.nvl(param.get("srchCond"), loginVO.getSite_id());
		String srchCondWord   = StringUtil.nvl(param.get("srchCondWord"));
		String srchFuid       = StringUtil.nvl(param.get("srchFuid"));    //column name
		String srchFunm       = StringUtil.nvl(param.get("srchFunm"));    //column name
		String srchCardNum    = StringUtil.nvl(param.get("srchCardNum")); //column name
		String srchFutype     = StringUtil.nvl(param.get("srchFutype"));
		String srchFbioyn     = StringUtil.nvl(param.get("srchFbioyn"));
		String srchFexpireyn  = StringUtil.nvl(param.get("srchFexpireyn"));
		String srchFustatus   = StringUtil.nvl(param.get("srchFustatus"));
		String srchFcardStatus= StringUtil.nvl(param.get("srchFcardStatus"));
		String srchCfsdt      = StringUtil.nvl(param.get("srchCfsdt"));
		String srchCfedt      = StringUtil.nvl(param.get("srchCfedt"));
		String srchAuthType   = StringUtil.nvl(param.get("srchAuthType"));
		String srchFpartnm1   = StringUtil.nvl(param.get("srchFpartnm1"));
		String srchFpartnm2   = StringUtil.nvl(param.get("srchFpartnm2"));
		String srchPartWord   = StringUtil.nvl(param.get("srchPartWord"));
		// sort
		String hidSortName    = StringUtil.nvl(param.get("hidSortName"));
		String hidSortNum     = StringUtil.nvl(param.get("hidSortNum"));
		// excel
		String chkValueArray  = StringUtil.nvl(param.get("chkValueArray"));
		String chkTextArray   = StringUtil.nvl(param.get("chkTextArray"));
		String hidAllLst      = StringUtil.nvl(param.get("hidAllLst"));
		
		// ?????? ????????? default ??? ??????
		if(StringUtil.nvl(param.get("pagegb")).equals("U") && !param.containsKey("srchFustatus")) { //???????????????
			srchFunm = "funm";
			srchFexpireyn = "N";
			srchFustatus = "Y";
		} else if(StringUtil.nvl(param.get("pagegb")).equals("UE") && !param.containsKey("srchFexpireyn")) { //?????????????????????
			srchFunm = "funm";
			srchFexpireyn = "Y";
		}
		
		List<String> srchColChk = new ArrayList<String>();
		String colChkQuery = "";
		
		List<String> srchColChk2 = new ArrayList<String>();
		String colChkQuery2 = "";
		
		try {
			// (????????????1)FID, ??????, ????????????
			if(!srchFuid.equals("")) srchColChk.add("u.fuid");
			if(!srchFunm.equals("")) srchColChk.add("u.funm");
			if(!srchCardNum.equals("")) srchColChk.add("c.fcdno");
			
			if(!srchCondWord.equals("") && srchColChk.size() == 0) {
				srchColChk.add("u.fuid");
				srchColChk.add("u.funm");
				srchColChk.add("c.fcdno");
			}
	
			if(!srchCondWord.equals("")) {
				
				
				if(srchCondWord.contains("'")){
					srchCondWord = srchCondWord.replaceAll("'", "''");
				}
				for(int i = 0 ; i < srchColChk.size() ; i++) {
					if(i == 0) {
						colChkQuery = srchColChk.get(i) + " like '%" + srchCondWord + "%' ";
					} else {
						colChkQuery += " OR " + srchColChk.get(i) + " like '%" + srchCondWord + "%' ";
					}
				}
				if(srchColChk.size() > 1) {
					colChkQuery = "(" + colChkQuery + ")";
				}
			}
			
			LOGGER.debug("colChkQuery:{}", colChkQuery);
			
			// (????????????2)??????, ??????
			if(!srchFpartnm1.equals("")) srchColChk2.add("u.fpartnm1");
			if(!srchFpartnm2.equals("")) srchColChk2.add("u.fpartnm2");
			
			if(!srchPartWord.equals("") && srchColChk2.size() == 0) {
				srchColChk2.add("u.fpartnm1");
				srchColChk2.add("u.fpartnm2");
			}
			
			if(!srchPartWord.equals("")) {
				if(srchPartWord.contains("'")){
					srchPartWord = srchPartWord.replaceAll("'", "''");
				}
				for(int i = 0 ; i < srchColChk2.size() ; i++) {
					if(i == 0) {
						colChkQuery2 = srchColChk2.get(i) + " like '%" + srchPartWord + "%' ";
					} else {
						colChkQuery2 += " OR " + srchColChk2.get(i) + " like '%" + srchPartWord + "%' ";
					}
				}
				if(srchColChk2.size() > 1) {
					colChkQuery2 = "(" + colChkQuery2 + ")";
				}
			}
			
			LOGGER.debug("colChkQuery2:{}", colChkQuery2);
			
			// ?????? ?????? ??????
			if(!hidAllLst.equals("Y")) {
				String fuLst = StringUtil.nvl(param.get("hidFuLst"));
				if(!fuLst.equals("")) {
					JSONParser parser = new JSONParser();
					Object obj = parser.parse(fuLst);
					JSONArray jsonArray = (JSONArray) obj;
					List<Map<String, Object>> lst = JsonUtil.getListMapFromJsonArray(jsonArray);
					userInfoVO.setFuids(lst);
				}
			}
		} catch(Exception e) {e.printStackTrace();}
		
		// paging
		userInfoVO.setSrchPage(Integer.parseInt(srchPage));
		userInfoVO.setSrchCnt(Integer.parseInt(srchRecPerPage));
		userInfoVO.autoOffset();
		// search
		userInfoVO.setSrchCond(srchCond);
		userInfoVO.setSrchCondWord(srchCondWord);
		userInfoVO.setSrchFuid(srchFuid);
		userInfoVO.setSrchFunm(srchFunm);
		userInfoVO.setSrchCardNum(srchCardNum);
		userInfoVO.setSrchFutype(srchFutype);
		userInfoVO.setSrchFbioyn(srchFbioyn);
		userInfoVO.setSrchFexpireyn(srchFexpireyn);
		userInfoVO.setSrchFustatus(srchFustatus);
		userInfoVO.setSrchFcardStatus(srchFcardStatus);
		userInfoVO.setSrchColChk(srchColChk);
		userInfoVO.setSrchColChk2(srchColChk2);
		userInfoVO.setColChkQuery(colChkQuery);
		userInfoVO.setColChkQuery2(colChkQuery2);
		userInfoVO.setSrchAuthType(srchAuthType);
		userInfoVO.setSrchFpartnm1(srchFpartnm1);
		userInfoVO.setSrchFpartnm2(srchFpartnm2);
		userInfoVO.setSrchPartWord(srchPartWord);
		userInfoVO.setSrchCfsdt(srchCfsdt);
		userInfoVO.setSrchCfedt(srchCfedt);
		// excel
		userInfoVO.setChkValueArray(chkValueArray);
		userInfoVO.setChkTextArray(chkTextArray);
		// sort
		userInfoVO.setHidSortName(hidSortName);
		userInfoVO.setHidSortNum(hidSortNum);
		
		return userInfoVO;
	}
	
	/**
	 * ????????? ?????? ?????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_management
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userMngmt.do")
	public String userMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		commandMap.put("pagegb", "U");
		UserInfoVO userInfoVO = setParamSearchUser(loginVO, commandMap);

		int totalCnt = userInfoService.selectUserListCount(userInfoVO);
		if(userInfoVO.getSrchCnt() == 0) userInfoVO.setSrchCnt(totalCnt);

		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(userInfoVO.getSrchPage());
		pageVO.setRecPerPage(userInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(userInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());  //?????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype");         //??????????????? ??????????????????
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");     //page??? record ???
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_Fustatus");     //???????????????

		List<UserInfoVO> userInfoList = userInfoService.selectUserList(userInfoVO);

		model.addAttribute("centerCombo", centerCombo);
		model.addAttribute("userType", userType);
		model.addAttribute("cntPerPage", cntPerPage);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("userInfoVO", userInfoVO);
		model.addAttribute("userInfoList", userInfoList);

		return "cubox/userInfo/user_management";
	}
	
	/**
	 * ????????? ?????? ?????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_management_new
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userMngmt2.do")
	public String userMngmt2(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		commandMap.put("pagegb", "U");
		UserInfoVO userInfoVO = setParamSearchUser(loginVO, commandMap);

		int totalCnt = userInfoService.selectUserListCount(userInfoVO);
		if(userInfoVO.getSrchCnt() == 0) userInfoVO.setSrchCnt(totalCnt);

		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(userInfoVO.getSrchPage());
		pageVO.setRecPerPage(userInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(userInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());  //?????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype");         //??????????????? ??????????????????
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");     //page??? record ???
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_Fustatus");     //???????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus");  //????????????
		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");      //????????????

		List<UserInfoVO> userInfoList = userInfoService.selectUserList(userInfoVO);

		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("userType",userType);
		model.addAttribute("authType",authType);
		model.addAttribute("cntPerPage",cntPerPage);
		model.addAttribute("userStatus",userStatus);
		model.addAttribute("cardStatus",cardStatus);
		model.addAttribute("pagination",pageVO);
		model.addAttribute("userInfoVO",userInfoVO);
		model.addAttribute("userInfoList", userInfoList);

		return "cubox/userInfo/user_management_new";
	}	

	/**
	 * ?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_expire_management
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userExpireMngmt.do")
	public String userExpireMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		commandMap.put("pagegb", "UE");
		UserInfoVO userInfoVO = setParamSearchUser(loginVO, commandMap);

		int totalCnt = userInfoService.selectUserListCount(userInfoVO);
		if(userInfoVO.getSrchCnt() == 0) userInfoVO.setSrchCnt(totalCnt);

		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(userInfoVO.getSrchPage());
		pageVO.setRecPerPage(userInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(userInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());  //?????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype");         //??????????????? ??????????????????
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");     //page??? record ???
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus");  //????????????
		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");      //????????????


		List<UserInfoVO> userInfoList = userInfoService.selectUserList(userInfoVO);

		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("userType", userType);
		model.addAttribute("authType", authType);
		model.addAttribute("cardStatus",cardStatus);
		model.addAttribute("cntPerPage",cntPerPage);
		model.addAttribute("pagination",pageVO);
		model.addAttribute("userInfoVO", userInfoVO);
		model.addAttribute("userInfoList", userInfoList);

		return "cubox/userInfo/user_expire_management";
	}

	/**
	 * ?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return 
	 * @throws Exception
	 */
	/*
	@RequestMapping(value="/userInfo/userAddPopup.do")
	public String userAddPopup(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//////////////////////////////////////////
		//???????????? ?????? ????????????
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);
		////////////////////////////////////////////////

		AuthGroupVO authGroupVO = new AuthGroupVO();

		List<AuthGroupVO> totalAuthGroup = null;
		if(loginVO.getFkind3().equals("11")){
			totalAuthGroup = userInfoService.getTotalAuthGroupList11(authGroupVO);
		}else if(loginVO.getFkind3().equals("12")){
			totalAuthGroup = userInfoService.getTotalAuthGroupList12(authGroupVO);
		}

		model.addAttribute("totalAuthGroupList", totalAuthGroup);

		model.addAttribute("menuPath", "userInfo/user_add_popup");

		return "cubox/cuboxPopupContents";
	}
	*/

	/**
	 * ?????????????????? ??????(????????????)
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/newTotalAuthGroup.do")
	public ModelAndView newTotalAuthGroup(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));

    	AuthGroupVO authGroupVO = new AuthGroupVO();
		authGroupVO.setSrchCondWord(srchCondWord);



		return modelAndView;
	}

	/**
	 * ??????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/getCdnoCnt.do")
	public ModelAndView getCdnoCnt(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String addfcdno = StringUtil.isNullToString(commandMap.get("addfcdno"));

    	CardInfoVO cardInfoVO = new CardInfoVO();
		cardInfoVO.setFpartcd1(loginVO.getSite_id());
		cardInfoVO.setFcdno(addfcdno);

		int cdnoCnt = userInfoService.getCdnoCnt(cardInfoVO);

		modelAndView.addObject("cdnoCnt", cdnoCnt);

		return modelAndView;
	}

	/**
	 * ????????? ????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/newUserInfoSave.do")
    public ModelAndView newUserInfoSave(MultipartHttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		//?????????????????? canvas?????? ??????
        byte[] imgByte= null;
		if (StringUtil.nvl(request.getParameter("imageType")).equals("file")) {
			MultipartFile file = null;
			Iterator<String> iterator = request.getFileNames();

			if (iterator.hasNext()) {
				file = request.getFile(iterator.next());
			}
			if (file != null) {
				imgByte = file.getBytes();
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, ??? ?????? ????????? ?????? ??????
			imgByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array??? ??????
		}

        String funm = request.getParameter("funm");
        String futype = request.getParameter("futype");
        String fvisitnum = request.getParameter("fvisitnum");
        String fauthtype = request.getParameter("fauthtype");
        String fustatus = request.getParameter("fustatus");
        String fpartnm1 = request.getParameter("fpartnm1");
        String fpartnm2 = request.getParameter("fpartnm2");
        String fpartnm3 = request.getParameter("fpartnm3");
        String fpartcd2 = request.getParameter("fpartcd2");
        String ftel = request.getParameter("ftel");
        String fpartcd3 = request.getParameter("fpartcd3");
        String fcarno = request.getParameter("fcarno");
        String fusdt = request.getParameter("fusdt");
        String fuedt = request.getParameter("fuedt");
        String addfcdno = request.getParameter("addfcdno");
        String cfcdnum = request.getParameter("cfcdnum");
        String cfusdt = request.getParameter("cfusdt");
        String cfuedt = request.getParameter("cfuedt");
        String authArray = request.getParameter("authArray");

        String fpartcd1 = loginVO.getSite_id();
        String fuid = fpartcd1 + "_" + addfcdno;
        String authGroupFtidText = loginVO.getSite_id() + "2," + authArray;

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setFuid(fuid);
		userInfoVO.setFunm(funm);
		userInfoVO.setFutype(futype);
		userInfoVO.setFvisitnum(fvisitnum);
		userInfoVO.setFauthtype(fauthtype);
		userInfoVO.setFustatus(fustatus);
		userInfoVO.setFpartnm1(fpartnm1);
		userInfoVO.setFpartnm2(fpartnm2);
		userInfoVO.setFpartnm3(fpartnm3);
		userInfoVO.setFtel(ftel);
		userInfoVO.setFpartcd1(fpartcd1);
		userInfoVO.setFpartcd2(fpartcd2);
		userInfoVO.setFpartcd3(fpartcd3);
		userInfoVO.setFcarno(fcarno);
		userInfoVO.setFusdt(fusdt);
		userInfoVO.setFuedt(fuedt);
		commonService.tcsidxSave("tuserinfo"); //tcsidx????????? fidx??????
		userInfoVO.setFsidx(commonService.getFsidx("tuserinfo")); //fsidx????????????
		userInfoVO.setFgroupid(authGroupFtidText);
		userInfoVO.setFmobilefg("Q");
		userInfoVO.setFsstatus("Q");

		int userSaveCnt = userInfoService.newUserInfoSave(userInfoVO); //????????? ?????? ??????

		int cardSaveCnt = 0;

		if(userSaveCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "13101101", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????

			commonService.tcsidxSave("tcard"); //tcsidx????????? fidx??????
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFuid(userInfoVO.getFuid());
			cardInfoVO.setFcdno(addfcdno);
			cardInfoVO.setFsdt(cfusdt);
			cardInfoVO.setFedt(cfuedt);
			cardInfoVO.setFcdnum(cfcdnum);
			cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx????????????

			cardSaveCnt = userInfoService.addUserCdno(cardInfoVO); //?????? ?????? ??????

			if(cardSaveCnt > 0){
				commonService.sysLogSave(loginVO.getFsiteid(), "13101102", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
				userInfoService.userFsstatusChange(userInfoVO);

				UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
				userBioInfoVO.setFuid(fuid);
				int imgUserSave = 0;
				if(imgByte != null && imgByte.length > 0) {
					userBioInfoVO.setImgByte(imgByte);
					imgUserSave = userInfoService.imgUserInfoSave(userBioInfoVO);
					if(imgUserSave > 0){
		        		commonService.sysLogSave(loginVO.getFsiteid(), "13101103", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		        	}else{
		        		commonService.sysLogSave(loginVO.getFsiteid(), "13101106", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		        	}
				}else{
					commonService.sysLogSave(loginVO.getFsiteid(), "13101107", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
				}
			}else{
				commonService.sysLogSave(loginVO.getFsiteid(), "13101105", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			}

		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101104", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}


        ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	modelAndView.addObject("cardSaveCnt", cardSaveCnt);
        return modelAndView;
    }

	/**
	 * ?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_edit_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userEditPopup.do")
	public String userEditPopup(ModelMap model, @RequestParam("fuid") String fuid, @RequestParam("fpartcd1") String fpartcd1,
			@RequestParam(value="sfcdno", required=false) String sfcdno, HttpServletRequest request) throws Exception {

		UserBioInfoVO userBioInfo = new UserBioInfoVO();
		userBioInfo = userInfoService.getUserBioInfo(fuid);

		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setFuid(fuid);
		userInfo.setFpartcd1(fpartcd1);

		UserInfoVO userInfo2 = new UserInfoVO();
		userInfo2 = userInfoService.getUserInfo2(userInfo);

		List<CardInfoVO> cardInfoList = userInfoService.getCardInfoList(fuid);

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//////////////////////////////////////////
		//???????????? ?????? ????????????
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);
		////////////////////////////////////////////////

		AuthGroupVO authGroupVO = new AuthGroupVO();
		authGroupVO.setFuid(fuid);


		model.addAttribute("sfcdno", sfcdno);
		model.addAttribute("sfutype",userInfo2.getFutype());
		model.addAttribute("userBioInfo", userBioInfo);					//????????? ???????????????(??????)
		model.addAttribute("userInfo", userInfo2);						//????????? ????????????
		model.addAttribute("cardInfoList", cardInfoList);				//?????? ?????????

		return "cubox/userInfo/user_edit_popup";
	}

	/**
	 * ????????? ?????? ??????
	 * @param model
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userEditPopup2.do")
	public String userEditPopup2(ModelMap model, HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fuid = StringUtil.nvl(request.getParameter("fuid"));
		String fpartcd1 = StringUtil.nvl(request.getParameter("fpartcd1"));
		String fcdno = StringUtil.nvl(request.getParameter("sfcdno"));
		
		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setFuid(fuid);
		userInfo.setFpartcd1(fpartcd1);
		userInfo.setCfcdno(fcdno);
		
		// ?????????
		userInfo = userInfoService.getUserInfo2(userInfo);

		// ?????? (?????????:??????=1:1 ??????????????? ????????????)
		//List<CardInfoVO> cardInfoList = userInfoService.getCardInfoList(fuid);
		
		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//???????????? ?????? ????????????
		//model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);

		model.addAttribute("fcdno", fcdno);          //????????? ????????????
		model.addAttribute("userInfo", userInfo);          //????????? ????????????
		//model.addAttribute("cardInfoList", cardInfoList);  //?????? ?????????

		return "cubox/userInfo/user_edit_popup_new";
	}	
	
	/**
	 * ?????????????????? ??????(???????????????)
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/totalAuthGroup.do")
	public ModelAndView totalGateSearch(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String srchCondWord = StringUtil.isNullToString(commandMap.get("srchCondWord"));
    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fcdno = StringUtil.isNullToString(commandMap.get("fcdno"));

    	UserInfoVO userInfo = new UserInfoVO();
		userInfo = userInfoService.getUserInfo(fuid);

    	AuthGroupVO authGroupVO = new AuthGroupVO();
		authGroupVO.setFuid(fuid);
		authGroupVO.setSrchCondWord(srchCondWord);
		authGroupVO.setFcdno(fcdno);




		return modelAndView;
	}

	/**
	 * ????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/getAuthGroup.do")
	public ModelAndView getAuthGroup(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		String fcdno = StringUtil.isNullToString(commandMap.get("fcdno"));
		String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));

		AuthGroupVO authGroupVO = new AuthGroupVO();
		authGroupVO.setFuid(fuid);
		authGroupVO.setFcdno(fcdno);

		List<AuthGroupVO> userAuthGroup = null;
		List<AuthGroupVO> totalAuthGroup = null;

		if(fpartcd1.equals("11")){
			userAuthGroup = userInfoService.getUserAuthGroupList11(authGroupVO);
		}else if(fpartcd1.equals("12")){
			userAuthGroup = userInfoService.getUserAuthGroupList12(authGroupVO);
		}

		modelAndView.addObject("userAuthGroup", userAuthGroup);

		return modelAndView;
	}

	/**
	 * ????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/getCardInfo.do")
	public ModelAndView getCardInfo(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String fcdno = StringUtil.isNullToString(commandMap.get("fcdno"));
		String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));

		CardInfoVO cardInfo = new CardInfoVO();
		cardInfo.setFcdno(fcdno);
		cardInfo.setFuid(fuid);
		cardInfo.setFpartcd1(fpartcd1);

		CardInfoVO cardInfoVO = new CardInfoVO();

		cardInfoVO = userInfoService.getCardInfo(cardInfo);
		modelAndView.addObject("cardInfoVO", cardInfoVO);

		return modelAndView;
	}

	/**
	 * ????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/cardInfoSave.do")
	public ModelAndView cardInfoSave(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		String cfcdno = StringUtil.isNullToString(commandMap.get("cfcdno"));
    	String cfstatus = StringUtil.isNullToString(commandMap.get("cfstatus"));
    	String cfcdnum = StringUtil.isNullToString(commandMap.get("cfcdnum"));
    	String srchStartDate2 = StringUtil.isNullToString(commandMap.get("srchStartDate2"));
    	String srchExpireDate2 = StringUtil.isNullToString(commandMap.get("srchExpireDate2"));

    	UserInfoVO userInfo = new UserInfoVO();
		userInfo = userInfoService.getUserInfo(fuid);

		CardInfoVO cardInfoVO = new CardInfoVO();
		cardInfoVO.setFcdno(cfcdno);
		cardInfoVO.setFuid(fuid);
		cardInfoVO.setFstatus(cfstatus);
		cardInfoVO.setFcdnum(cfcdnum);
		//cardInfoVO.setFsdt(srchStartDate2 + " 00:00:00.000");
		//cardInfoVO.setFedt(srchExpireDate2 + " 00:00:00.000");
		cardInfoVO.setFsdt(srchStartDate2);		//?????? ????????????
		cardInfoVO.setFedt(srchExpireDate2);		//?????? ????????????
		cardInfoVO.setFpartcd1(userInfo.getFpartcd1());   //tauthtogate_main 11????????? 12?????? ??????
		if(cfstatus.equals("D") || cfstatus.equals("E") || cfstatus.equals("L") || cfstatus.equals("W")){
			cardInfoVO.setFsstatus("K");
		}else{
			cardInfoVO.setFsstatus("U");
		}

		LOGGER.debug("fuid : " + fuid);

		int uCnt = userInfoService.cardInfoSave(cardInfoVO);
		if(uCnt > 0){
			//userInfoService.tauthtogateMainFsstatusChange(cardInfoVO);
			if(cfstatus.equals("D") || cfstatus.equals("E") || cfstatus.equals("L") || cfstatus.equals("W")){
				//userInfo.setFsstatus("R");
				//userInfoService.userFsstatusChange(userInfo);
			}else{
				userInfo.setFsstatus("Q");
				userInfoService.userFsstatusChange(userInfo);
			}
			//userInfo.setFsstatus("K");
			//userInfoService.userFsstatusChange(userInfo);
			commonService.sysLogSave(loginVO.getFsiteid(), "13101405", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101406", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}

		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}

	/**
	 * ??????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/addUserCdno.do")
	public ModelAndView addUserCdno(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
		String addfcdno = StringUtil.isNullToString(commandMap.get("addfcdno"));

		CardInfoVO cardInfoVO = new CardInfoVO();
		cardInfoVO.setFuid(fuid);
		cardInfoVO.setFpartcd1(fpartcd1);
		cardInfoVO.setFcdno(addfcdno);

		int cdnoCnt = userInfoService.getCdnoCnt(cardInfoVO);

		int addCdnoCnt = 0;
		if(cdnoCnt == 0){

			UserInfoVO userInfo = new UserInfoVO();
			userInfo = userInfoService.getUserInfo(fuid);

			commonService.tcsidxSave("tcard"); //tcsidx????????? fidx??????

			cardInfoVO.setFsdt(userInfo.getFusdt());
			cardInfoVO.setFedt(userInfo.getFuedt());
			cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx????????????
			addCdnoCnt = userInfoService.addUserCdno(cardInfoVO);
			if(addCdnoCnt > 0){
				userInfo.setFsstatus("Q");
				userInfoService.userFsstatusChange(userInfo);
				commonService.sysLogSave(loginVO.getFsiteid(), "13101401", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			}else{
				commonService.sysLogSave(loginVO.getFsiteid(), "13101402", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			}
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101403", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}

		LOGGER.debug("cdnoCnt : " + cdnoCnt);
		LOGGER.debug("fuid : " + fuid);


		modelAndView.addObject("cdnoCnt", cdnoCnt);
		modelAndView.addObject("addCdnoCnt", addCdnoCnt);

		return modelAndView;
	}

	/**
	 * ???????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoSave.do")
	public ModelAndView userInfoSave(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
		String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
		String funm = StringUtil.isNullToString(commandMap.get("funm"));
		String futype = StringUtil.isNullToString(commandMap.get("futype"));
		String fvisitnum = StringUtil.isNullToString(commandMap.get("fvisitnum"));		
		String fcdno = StringUtil.isNullToString(commandMap.get("fcdno"));
		String startDate = StringUtil.isNullToString(commandMap.get("startDate"));
		String expireDate = StringUtil.isNullToString(commandMap.get("expireDate"));
		String cfstatus = StringUtil.isNullToString(commandMap.get("cfstatus"));
		String newFcdno = StringUtil.isNullToString(commandMap.get("newFcdno"));
		String isFcdnoChange = StringUtil.isNullToString(commandMap.get("isFcdnoChange"));
		String fpartnm2 = StringUtil.isNullToString(commandMap.get("fpartnm2"));
		
		/* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? */
		//cardno
		String addfcdno = "";//"00"+fuid;
		
		if( !"".equals(newFcdno) ) {//??????????????? ?????????(??? ?????? ????????? ????????? ????????? ????????? view??????)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), newFcdno).replace(" ", fillChar);
		}else if( "".equals(newFcdno) ){//??????????????? ???????????? ??????????????? ????????? ???????????? ??????
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}
		/* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? END */
		
		/* ???????????? ?????? ?????? */
		if("Y".equals(isFcdnoChange)) {
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFcdno(addfcdno);
			
			Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
			int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
			
			if( cnt > 0 ) {//???????????? ?????? ????????? ?????? ??????
				modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno ?????? ??????
				return modelAndView;
			}
		}
		/* ???????????? ?????? ?????? END */
		
		// 2021-04-13 ????????????????????? ?????? ??????
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);  //????????????????????? ??????
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("fuid", fuid);
				param.put("workgb", "M");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);  //????????????????????? ??????
					return modelAndView;
				}
			}
		}

		int uCnt = 0;
		UserInfoVO userInfoVO = new UserInfoVO();
		
		try {
			userInfoVO.setFuid(fuid);
			userInfoVO.setFpartcd1(fpartcd1);
			//userInfoVO.setFauthtype(fauthtype);
			userInfoVO.setFauthtype(GLOBAL_DEFAULT_AUTHTYPE);
			userInfoVO.setFsstatus("R");	//userInfoVO.setFsstatus("K");
			userInfoVO.setCfcdno(fcdno);
			userInfoVO.setCfstatus(cfstatus);								//????????????
			//userInfoVO.setFusdt2(srchStartDate2 	+ " 00:00:00.000");		//?????? ????????????
			//userInfoVO.setFuedt2(srchExpireDate2 	+ " 00:00:00.000");		//?????? ????????????
			userInfoVO.setFusdt2(startDate);		//?????? ????????????
			userInfoVO.setFuedt2(expireDate);		//?????? ????????????
			userInfoVO.setFutype(futype);
			userInfoVO.setFvisitnum(fvisitnum);
			userInfoVO.setFunm(funm);
			userInfoVO.setNewFcdno(addfcdno);
			userInfoVO.setFpartnm2(fpartnm2);
			userInfoVO.setFmodid(loginVO.getFsiteid());
			
			uCnt = userInfoService.userInfoSave(userInfoVO);
			if(uCnt > 0) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101305", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			}
		} catch(Exception e) {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			modelAndView.addObject("message", e.getMessage());			
		}
		
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}
	
	
	private byte[] getImageByte(MultipartHttpServletRequest request) {
		
		byte[] imageByte = null;
		
		try {
			//?????????????????? canvas?????? ??????
			if (StringUtil.nvl(request.getParameter("imageType")).equals("file")) {
				MultipartFile file = null;
				Iterator<String> iterator = request.getFileNames();

				if (iterator.hasNext()) {
					file = request.getFile(iterator.next());
				}
				if (file != null) {
					imageByte = file.getBytes();
				} else {
					throw new RuntimeException("??????????????? ????????????.");
				}
			} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
				String imgbase64 = (String) request.getParameter("imgUpload");
				String[] base64Arr = imgbase64.split(","); // image/png;base64, ??? ?????? ????????? ?????? ??????
				imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array??? ??????
			}	
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("???????????? ????????????.");
		}
				
		return imageByte;
	}
	
	/**
	 * 2021-04-20 ??????????????? ????????????, ??????,??????????????? ????????? ?????????.
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoSaveNew.do")
	public ModelAndView userInfoSaveNew(MultipartHttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		// ????????? ??????
		String fpartcd1 = StringUtil.isNullToString(request.getParameter("fpartcd1"));
		String fuid = StringUtil.isNullToString(request.getParameter("fuid"));
		String funm = StringUtil.isNullToString(request.getParameter("funm"));
		String futype = StringUtil.isNullToString(request.getParameter("futype"));
		String fauthtype = StringUtil.isNullToString(request.getParameter("fauthtype"));
		String fvisitnum = StringUtil.isNullToString(request.getParameter("fvisitnum"));
		String fpartnm2 = StringUtil.isNullToString(request.getParameter("fpartnm2"));
		// ?????? ??????
		String fcdno = StringUtil.isNullToString(request.getParameter("fcdno"));
		String newFcdno = StringUtil.isNullToString(request.getParameter("newFcdno"));
		String isFcdnoChange = StringUtil.isNullToString(request.getParameter("isFcdnoChange"));
		String startDate = StringUtil.isNullToString(request.getParameter("startDate"));
		String expireDate = StringUtil.isNullToString(request.getParameter("expireDate"));
		String fstatus = StringUtil.isNullToString(request.getParameter("cfstatus"));

		/* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? */
		//cardno
		String addfcdno = "";//"00"+fuid;
		if( !"".equals(newFcdno) ) {//??????????????? ?????????(??? ?????? ????????? ????????? ????????? ????????? view??????)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), newFcdno).replace(" ", fillChar);
		}else if( "".equals(newFcdno) ){//??????????????? ???????????? ??????????????? ????????? ???????????? ??????
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}
		/* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? END */
		
		/* ???????????? ?????? ?????? */
		if("Y".equals(isFcdnoChange)) {
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFcdno(addfcdno);
			
			Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
			int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
			
			if( cnt > 0 ) {//???????????? ?????? ????????? ?????? ??????
				modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno ?????? ??????
				return modelAndView;
			}
		}
		/* ???????????? ?????? ?????? END */
		
		// 2021-04-13 ????????????????????? ?????? ??????
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);  //????????????????????? ??????
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("fuid", fuid);
				param.put("workgb", "M");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);  //????????????????????? ??????
					return modelAndView;
				}
			}
		}

		int uCnt = 0;
		UserInfoVO userInfoVO = new UserInfoVO();
		
		try {
			// ????????????
			userInfoVO.setFpartcd1(fpartcd1);
			userInfoVO.setFuid(fuid);
			userInfoVO.setFunm(funm);
			userInfoVO.setFutype(futype);
			userInfoVO.setFauthtype(fauthtype);
			userInfoVO.setFvisitnum(fvisitnum);
			userInfoVO.setFpartnm2(fpartnm2);
			userInfoVO.setFmodid(loginVO.getFsiteid());

			// bio??????
			byte[] imageByte = getImageByte(request);
			UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
			if(imageByte != null && imageByte.length > 0) {
				userInfoVO.setFbioyn("Y");
				userBioInfoVO.setFuid(fuid);
				userBioInfoVO.setImgByte(imageByte);
			} else {
				userInfoVO.setFbioyn("N");
			}
			
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFuid(fuid);
			cardInfoVO.setFpartcd1(fpartcd1);
			cardInfoVO.setFsdt(startDate);
			cardInfoVO.setFedt(expireDate);
			cardInfoVO.setFstatus(fstatus);
			cardInfoVO.setFcdno(fcdno);
			if("Y".equals(isFcdnoChange)) cardInfoVO.setFnewcdno(newFcdno);			
			
			uCnt = userInfoService.userInfoSaveNew(userInfoVO, userBioInfoVO, cardInfoVO);
			
			if(uCnt > 0) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101305", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????????????????
				if(userInfoVO.getFbioyn().equals("Y")) {
					commonService.sysLogSave(loginVO.getFsiteid(), "13101301", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //??????????????????
				}
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????????????????
			}
		} catch(Exception e) {
			modelAndView.addObject("message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????????????????
		}
		
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}	

	/**
	 * ???????????????-???????????? ??????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoPhoneUpdate.do")
	public ModelAndView userInfoPhoneUpdate(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
    	String fpartcd2 = StringUtil.isNullToString(commandMap.get("fpartcd2"));
    	String ftel = StringUtil.isNullToString(commandMap.get("ftel"));

    	UserInfoVO userInfoVO = new UserInfoVO();
    	userInfoVO.setFuid(fuid);
    	userInfoVO.setFpartcd1(fpartcd1);
    	userInfoVO.setFpartcd2(fpartcd2);
    	userInfoVO.setFtel(ftel);
    	userInfoVO.setFmobilefg("Q");
    	userInfoVO.setFsstatus("K");

		LOGGER.debug("fuid : " + fuid);

		int uCnt = userInfoService.userInfoPhoneUpdate(userInfoVO);

		if(uCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "13101307", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		} else {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101308", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}

	/**
	 * ???????????????-????????? ??????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoCarnoUpdate.do")
	public ModelAndView userInfoCarnoUpdate(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
    	String fpartcd3 = StringUtil.isNullToString(commandMap.get("fpartcd3"));
    	String fcarno = StringUtil.isNullToString(commandMap.get("fcarno"));

    	UserInfoVO userInfoVO = new UserInfoVO();
    	userInfoVO.setFuid(fuid);
    	userInfoVO.setFpartcd1(fpartcd1);
    	userInfoVO.setFpartcd3(fpartcd3);
    	userInfoVO.setFcarno(fcarno);
    	userInfoVO.setFsstatus("K");

		LOGGER.debug("fuid : " + fuid);

		int uCnt = userInfoService.userInfoCarnoUpdate(userInfoVO);

		if(uCnt > 0){
			userInfoService.userInfoCarnoCmlUpdate();
			commonService.sysLogSave(loginVO.getFsiteid(), "13101309", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101310", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}

		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}

	/**
	 * ???????????????-?????? ??????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoEtcUpdate.do")
	public ModelAndView userInfoEtcUpdate(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
    	String fetc1 = StringUtil.isNullToString(commandMap.get("fetc1"));
    	String fetc2 = StringUtil.isNullToString(commandMap.get("fetc2"));

    	UserInfoVO userInfoVO = new UserInfoVO();
    	userInfoVO.setFuid(fuid);
    	userInfoVO.setFpartcd1(fpartcd1);
    	userInfoVO.setFetc1(fetc1);
    	userInfoVO.setFetc2(fetc2);

		int uCnt = userInfoService.userInfoEtcUpdate(userInfoVO);

		if(uCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "13101311", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101312", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}
	
	/**
	 * ????????? ??????
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoDel.do")
	public ModelAndView userInfoDel(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		int cnt = 0;
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	UserInfoVO userInfoVO = new UserInfoVO();

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
    	
    	if(fuid.equals("") || fpartcd1.equals("")) {
    		modelAndView.addObject("result", "fail");
    		modelAndView.addObject("message", "????????? ????????? ????????? ???????????? ????????????.");
    	} else {
    		try {
            	userInfoVO.setFuid(fuid);
            	userInfoVO.setFpartcd1(fpartcd1);
            	userInfoVO.setFmodid(loginVO.getFsiteid());
            	
        		// ?????? ??????
        		Map<String, Object> param = setUserChgLogParam("D", request, loginVO);

            	cnt = userInfoService.userInfoDel(userInfoVO, param);
        		if(cnt > 0) {
        			modelAndView.addObject("result", "success");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101313", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ??????
        		} else {
        			modelAndView.addObject("result", "fail");
        			modelAndView.addObject("message", "????????? ?????? ??? ?????? ??????????????????.");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101314", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ??????
        		}
        	} catch(Exception e) {
        		modelAndView.addObject("result", "fail");
        		modelAndView.addObject("message", e.getMessage());
        		commonService.sysLogSave(loginVO.getFsiteid(), "13101314", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ??????
        	}
    	}
    	
		return modelAndView;
	}	

	/**
	 * ????????? ??????
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoReco.do")
	public ModelAndView userInfoReco(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		int cnt = 0;
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	UserInfoVO userInfoVO = new UserInfoVO();

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
    	String futype = StringUtil.isNullToString(commandMap.get("futype"));
    	
    	if(fuid.equals("") || fpartcd1.equals("")) {
    		modelAndView.addObject("result", "fail");
    		modelAndView.addObject("message", "????????? ????????? ????????? ???????????? ????????????.");
    	} else {
    		try {
            	userInfoVO.setFuid(fuid);
            	userInfoVO.setFpartcd1(fpartcd1);
            	userInfoVO.setFutype(futype);
            	userInfoVO.setFmodid(loginVO.getFsiteid());
            	
        		// ?????? ??????
        		Map<String, Object> param = setUserChgLogParam("R", request, loginVO);

            	cnt = userInfoService.userInfoReco(userInfoVO, param);
        		if(cnt > 0) {
        			modelAndView.addObject("result", "success");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101315", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ??????
        		} else {
        			modelAndView.addObject("result", "fail");
        			modelAndView.addObject("message", "????????? ?????? ??? ?????? ??????????????????.");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101316", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ??????
        		}
        	} catch(Exception e) {
        		modelAndView.addObject("result", "fail");
        		modelAndView.addObject("message", e.getMessage());
        		commonService.sysLogSave(loginVO.getFsiteid(), "13101316", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ??????
        	}
    	}
    	
		return modelAndView;
	}
	
	/**
	 * ????????? ???????????? (DB??????)
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userInfoDrop.do")
	public ModelAndView userInfoDrop(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		int cnt = 0;
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	UserInfoVO userInfoVO = new UserInfoVO();

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fpartcd1 = StringUtil.isNullToString(commandMap.get("fpartcd1"));
    	
    	if(fuid.equals("") || fpartcd1.equals("")) {
    		modelAndView.addObject("result", "fail");
    		modelAndView.addObject("message", "????????? ????????? ????????? ???????????? ????????????.");
    	} else {
    		try {
            	userInfoVO.setFuid(fuid);
            	userInfoVO.setFpartcd1(fpartcd1);
            	userInfoVO.setFmodid(loginVO.getFsiteid());
            	
        		// ?????? ??????
        		Map<String, Object> param = setUserChgLogParam("X", request, loginVO);

            	cnt = userInfoService.userInfoDrop(userInfoVO, param);
        		if(cnt > 0) {
        			modelAndView.addObject("result", "success");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101317", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ???????????? ??????
        		} else {
        			modelAndView.addObject("result", "fail");
        			modelAndView.addObject("message", "????????? ???????????? ??? ?????? ??????????????????.");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101318", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ???????????? ??????
        		}
        	} catch(Exception e) {
        		modelAndView.addObject("result", "fail");
        		modelAndView.addObject("message", e.getMessage());
        		commonService.sysLogSave(loginVO.getFsiteid(), "13101318", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????? ???????????? ??????
        	}
    	}
    	
		return modelAndView;
	}	
	
	/**
	 * ????????? ????????? ??????
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userListDel.do")
	public ModelAndView userListDel(String[] ids, HttpServletRequest request) throws Exception {
		int cnt = 0;
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		UserInfoVO userInfoVO = new UserInfoVO();

		String fpartcd1 = loginVO.getSite_id();
		String fmodid = loginVO.getFsiteid();    	
		//tring chgResn = StringUtil.nvl(request.getParameter("chg_resn"), "????????? ????????????(??????)");//2022-04-14 ?????? ??????
		String cntnIp = commonUtils.getIPFromRequest(request);
		
		if(!fmodid.equals("dev")) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "????????? ????????? ????????????.");		
		} else if(ids.length == 0) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "????????? ????????? ????????? ???????????? ????????????.");
		} else {
			try {
				userInfoVO.setFpartcd1(fpartcd1);
				userInfoVO.setFmodid(fmodid);
				
				// ?????? ??????
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("chg_resn", "");
				param.put("cntn_ip", cntnIp);
				param.put("reg_id", fmodid);
				
				for(String str : ids) {
					userInfoVO.setFuid(str);

					int a_del = userInfoService.userInfoDel(userInfoVO, param);
					LOGGER.debug("[###][userListDel] a_del : {}", a_del);
					cnt++;
					LOGGER.debug("[###][userListDel] ????????? ?????????... ids/cnt : {}/{}", ids.length, cnt);
				}
				LOGGER.debug("[###][userListDel] ????????? ???????????? ids/cnt : {}/{}", ids.length, cnt);

				if(cnt > 0) {
					modelAndView.addObject("result", "success");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101313", String.format("%s ?????? %d ???", userInfoVO.toString(), cnt), cntnIp); //????????? ?????? ??????
				} else {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "????????? ?????? ??? ?????? ??????????????????.");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101314", String.format("%s ?????? %d ???", userInfoVO.toString(), cnt), cntnIp); //????????? ?????? ??????
				}
			} catch(Exception e) {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", e.getMessage());
				commonService.sysLogSave(loginVO.getFsiteid(), "13101314", String.format("%s ?????? %d ???", userInfoVO.toString(), cnt), cntnIp); //????????? ?????? ??????
				e.printStackTrace();
			}
		}
		
		return modelAndView;
	}	
	
	/**
	 * ????????? ????????? ???????????? (DB??????)
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/userListDrop.do")
	public ModelAndView userListDrop(String[] ids, HttpServletRequest request) throws Exception {
		int cnt = 0;
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		UserInfoVO userInfoVO = new UserInfoVO();

		String fpartcd1 = loginVO.getSite_id();
		String fmodid = loginVO.getFsiteid();    	
		//tring chgResn = StringUtil.nvl(request.getParameter("chg_resn"), "????????? ????????????(??????)");//2022-04-14 ?????? ??????
		String cntnIp = commonUtils.getIPFromRequest(request);
		
		if(!fmodid.equals("dev")) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "????????? ????????? ????????????.");		
		} else if(ids.length == 0) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "????????? ????????? ????????? ???????????? ????????????.");
		} else {
			try {
				userInfoVO.setFpartcd1(fpartcd1);
				userInfoVO.setFmodid(fmodid);
				
				// ?????? ??????
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("chg_resn", "");
				param.put("cntn_ip", cntnIp);
				param.put("reg_id", fmodid);
				
				for(String str : ids) {
					userInfoVO.setFuid(str);

					int a_drop = userInfoService.userInfoDrop(userInfoVO, param);
					LOGGER.debug("[###][userListDrop] a_drop : {}", a_drop);
					cnt++;
					LOGGER.debug("[###][userListDrop] ?????? ?????????... ids/cnt : {}/{}", ids.length, cnt);
				}
				LOGGER.debug("[###][userListDrop] ?????? ???????????? ids/cnt : {}/{}", ids.length, cnt);

				if(cnt > 0) {
					modelAndView.addObject("result", "success");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101317", String.format("%s ?????? %d ???", userInfoVO.toString(), cnt), cntnIp); //????????? ???????????? ??????
				} else {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "????????? ???????????? ??? ?????? ??????????????????.");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101318", String.format("%s ?????? %d ???", userInfoVO.toString(), cnt), cntnIp); //????????? ???????????? ??????
				}
			} catch(Exception e) {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", e.getMessage());
				commonService.sysLogSave(loginVO.getFsiteid(), "13101318", String.format("%s ?????? %d ???", userInfoVO.toString(), cnt), cntnIp); //????????? ???????????? ??????
			}
		}
		
		return modelAndView;
	}	

	

	/**
	 * ??????????????? ??????????????? POC
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/excelUserSave2.do")
    public ModelAndView excelUserSave2(MultipartHttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		MultipartFile file = null;
        Iterator<String> iterator = request.getFileNames();
        if(iterator.hasNext()) {
            file = request.getFile(iterator.next());
        }
        
        String cntnIp = commonUtils.getIPFromRequest(request);

        try {
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			
			AES256Util aes256 = new AES256Util();
			
			List<HashMap> list = new ArrayList<HashMap>();
			int cnt = 0;

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				// ?????? ???????????? ????????? ??????
                if(row == null) {
                    continue;
                }

                if(row.getCell(0) != null) {
                	
					String idx = getValue(row.getCell(0)).replaceAll("\n", "<br>");	//??????
					String funm = getValue(row.getCell(1)).replaceAll("\n", "<br>");	//??????
					String fpartnm2 = getValue(row.getCell(2)).replaceAll("\n", "<br>");	//??????
					String fsdt = getValue(row.getCell(3)).replaceAll("\n", "<br>");	//???????????????
					String fedt = getValue(row.getCell(4)).replaceAll("\n", "<br>");	//???????????????

	                if(funm.length() > 0) {
				    	HashMap insMap = new HashMap();
				    	insMap.put("siteId", loginVO.getSite_id());
				    	insMap.put("registId", loginVO.getFsiteid());
			    	
				    	insMap.put("funm", funm);
				    	insMap.put("fpartnm1", StringUtil.nvl(gbUserFpartnm1));
				    	insMap.put("fpartnm2", fpartnm2);
				    	insMap.put("fsdt", fsdt);
				    	insMap.put("fedt", fedt);
				    	
				    	LOGGER.debug("[????????????????????????] [???????????????] insMap : {}", insMap);

						//String image_local_file = GLOBAL_FILESTORAGE + fuid;
						String image_local_file = gbUserImagePath + idx; //D:\\cubox\\excelImages\\

				        File fileCheck_jpg = new File(image_local_file + ".jpg");
				        File fileCheck_jpeg = new File(image_local_file + ".jpeg");
				        File fileCheck_bmp = new File(image_local_file + ".bmp");
				        File fileCheck_png = new File(image_local_file + ".png");
				        File fileCheck_gif = new File(image_local_file + ".gif");

			        	byte[] imgByte = null;

						if (fileCheck_jpg.exists() && fileCheck_jpg.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_jpg);
						} else if (fileCheck_jpeg.exists() && fileCheck_jpeg.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_jpeg);
						} else if (fileCheck_bmp.exists() && fileCheck_bmp.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_bmp);
						} else if (fileCheck_png.exists() && fileCheck_png.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_png);
						} else if (fileCheck_gif.exists() && fileCheck_gif.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_gif);
						}

				        if(imgByte != null && imgByte.length > 0){
				        	String imgString = aes256.byteArrEncode(imgByte, GLOBAL_AES256_KEY);
				        	insMap.put("fimg", imgString);
				        }
				        
				        cnt =+ userInfoService.insertUserInfoForExcel(insMap);
				        
				        //list.add(insMap); //list??? ??????
	                }
                }
			}
			
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
				modelAndView.addObject("message", "success");	
			} else {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "???????????? ????????? ??????");
			}
			
			/* 2022-04-15 list??? ???????????? ??????
			if(list != null && list.size() > 0 ) {
				
				int cnt = userInfoService.insertUserListForExcel(list);
		        
				if(cnt == list.size()) {
					modelAndView.addObject("result", "success");
					modelAndView.addObject("message", "success");
					
					//??????
					commonService.sysLogSave(loginVO.getFsiteid(), "13101010", String.valueOf(cnt)+"??? ?????????", cntnIp); //???????????????
					
				} else if (cnt < list.size()) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "??????????????? ????????? ???????????? ?????? ???????????? ??????");
				} else if (cnt == 0 ) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "???????????? ????????? ??????");
				}
		        
			} else {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "??????????????? ???????????? ??????");
			}*/
        } catch (Exception e) {
        	modelAndView.addObject("result", "fail");
        	modelAndView.addObject("message", e.getMessage());
            e.printStackTrace();
        }

        return modelAndView;
    }	
	
	public static String getValue(Cell cell) {
        String value = "";

        if(cell == null) {
            value = "";
        }else{
            if( cell.getCellType() == Cell.CELL_TYPE_FORMULA ) {
                value = cell.getCellFormula();
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
            	if(DateUtil.isCellDateFormatted(cell)){
            		Date date = cell.getDateCellValue();
            		value = new SimpleDateFormat("yyyy-MM-dd").format(date);
            	}else{
            		value = String.valueOf((long)cell.getNumericCellValue());
            	}

            }
            else if( cell.getCellType() == Cell.CELL_TYPE_STRING ) {
                value = cell.getStringCellValue();
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_BOOLEAN ) {
                value = cell.getBooleanCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_ERROR ) {
                value = cell.getErrorCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_BLANK ) {
                value = "";
            }
            else {
                value = cell.getStringCellValue();
            }
        }

        return value.trim();
    }

	// ?????? ???????????? ?????? ?????????
    @RequestMapping(value = "/userInfo/excelFormDown.do")
    // get ?????? ??????????????? ????????? ?????? PK ?????? ????????? ??????
    public ModelAndView fileDown(@RequestParam Map<String, Object> commandMap,
    		HttpServletRequest request) throws Exception {

    	String filePath = request.getSession().getServletContext().getRealPath("/excel/") + "/";

    	String seq = (String)commandMap.get("seq");
		String type = (String)commandMap.get("type");


		LOGGER.debug("seq : " + seq);
		LOGGER.debug("type : " + type);
		LOGGER.debug("filePath : " + filePath);

		File downloadFile = new File(filePath + "excelupload_sample2.xlsx");;
		String fileOrigin = "???????????????_????????????.xlsx";

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fileDownloadView");
        modelAndView.addObject("downloadFile", downloadFile);
        modelAndView.addObject("fileOrigin", fileOrigin);

        return modelAndView;
    }
    
    @RequestMapping(value = "/userInfo/excelFormDown2.do")
    public ModelAndView excelFormDown2(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

    	String filePath = request.getSession().getServletContext().getRealPath("/excel/") + "/";
    	LOGGER.debug("?????????????????? filePath : {}", filePath);

		File downloadFile = new File(filePath + "excelupload_form.xlsx");;
		String fileOrigin = "???????????????_????????????.xlsx";

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fileDownloadView");
        modelAndView.addObject("downloadFile", downloadFile);
        modelAndView.addObject("fileOrigin", fileOrigin);

        return modelAndView;
    }     

    /**
	 * ????????? ?????? ????????????
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/excelDownload.do")
	public ModelAndView excelDownload(@RequestParam Map<String, Object> commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModuleUtil.getInstance().setCurrentState("B");	//?????? ????????? ?????? ??????

		ModelAndView modelAndView = new ModelAndView();
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fresn = StringUtil.nvl(request.getParameter("fdownresn"));

		//???????????? ????????????
		Map<String, String> m = new HashMap<String, String>();
		m.put("fip", commonUtils.getIPFromRequest(request));
		m.put("fdwnflg", "E");
		m.put("fresn", fresn);	//reason
		int rtnCnt = 0;
		try {
			rtnCnt = userInfoService.setDownLoadResnLog(m);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(rtnCnt > 0) {
			
			UserInfoVO userInfoVO = setParamSearchUser(loginVO, commandMap);
			
			// ????????? ?????? ??????
			String strImgYn = StringUtil.nvl(commandMap.get("hidExcelImgYn"), "N");			

			String[] chkText = null;
			List<ExcelVO> resultCellList = null;
			
			try {
				// ?????? ?????? ??????
				chkText = userInfoVO.getChkTextArray().split(",");				
				
				//2021-06-18//resultCellList = userInfoService.getUserExcelList(userInfoVO);
				resultCellList = userInfoService.selectUserListExcel(userInfoVO);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				ModuleUtil moduleUtil = ModuleUtil.getInstance();
				moduleUtil.setTotalRowCount(0);
				moduleUtil.setCurrentStateCount(0);
				moduleUtil.setCurrentState("C");
			} finally {
				if(strImgYn.equals("Y")) { //???????????????(???????????????)
					commonService.sysLogSave(loginVO.getFsiteid(), "13101211", "commandMap:"+commandMap+",colChkQuery:"+userInfoVO.getColChkQuery(), commonUtils.getIPFromRequest(request)); //????????????
				} else {  //???????????????
					commonService.sysLogSave(loginVO.getFsiteid(), "13101201", "commandMap:"+commandMap+",colChkQuery:"+userInfoVO.getColChkQuery(), commonUtils.getIPFromRequest(request)); //????????????	
				}
			}

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", resultCellList);
			modelAndView.addObject("excelName", "??????????????????");
			modelAndView.addObject("excelHeader", chkText);
			modelAndView.addObject("strImgYn", strImgYn);
            modelAndView.addObject("strImgGb", "F");
		}
		
		return modelAndView;
    }

	/**
	 * ?????????????????????????????? ????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_other_center_add_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/otherCenterUserImpPopup.do")
	public String otherCenterUserImpPopup(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		AuthGroupVO authGroupVO = new AuthGroupVO();
		List<AuthGroupVO> totalAuthGroup = null;


		return "cubox/userInfo/user_other_center_add_popup";
	}

	/**
	 * ?????????????????? ??????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/otherCenterUserInfoSearch.do")
	public ModelAndView otherCenterUserInfoSearch(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	String srchFunmWord = StringUtil.isNullToString(commandMap.get("srchFunmWord"));

    	List<UserInfoVO> userInfoList = null;
		if(loginVO.getSite_id().equals("11")){
			userInfoList = userInfoService.getOtherCenterUserInfoList11(srchFunmWord);
		}else if(loginVO.getSite_id().equals("12")){
			userInfoList = userInfoService.getOtherCenterUserInfoList12(srchFunmWord);
		}

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
		modelAndView.addObject("userInfoList", userInfoList);

		return modelAndView;
	}



	/**
	 * DB?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/getByteImage.do")
	public ResponseEntity<byte[]> getByteImage(@RequestParam("fuid") String fuid) throws Exception {

		AES256Util aes256 = new AES256Util();

		LOGGER.debug("fuid : " + fuid);
		UserBioInfoVO userBioInfo = new UserBioInfoVO();
		userBioInfo = userInfoService.getUserBioInfo(fuid);
		byte[] imageContent = null;
		if(userBioInfo != null && userBioInfo.getFimg().length() != 0){
			imageContent = aes256.byteArrDecodenopaddingImg(userBioInfo.getFimg(), GLOBAL_AES256_KEY);
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
	}

	/**
	 * DB?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/getByteImageDown.do")
    // get ?????? ??????????????? ????????? ?????? PK ?????? ????????? ??????
    public void getByteImageDown(@RequestParam Map<String, Object> commandMap,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {

    	String fuid = StringUtil.isNullToString(commandMap.get("fuid"));
    	String fileOrigin = "";

    	LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
    	if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
    		UserInfoVO userInfo = new UserInfoVO();
    		userInfo = userInfoService.getUserInfo(fuid);

    		AES256Util aes256 = new AES256Util();

    		LOGGER.debug("fuid : " + fuid);
    		UserBioInfoVO userBioInfo = new UserBioInfoVO();
    		userBioInfo = userInfoService.getUserBioInfo(fuid);
    		byte[] imageContent = null;
    		if(userBioInfo != null && userBioInfo.getFimg().length() != 0){
    			imageContent = aes256.byteArrDecodenopaddingImg(userBioInfo.getFimg(), GLOBAL_AES256_KEY);
    		}

			fileOrigin = userInfo.getFunm();

			String header = request.getHeader("User-Agent");
			boolean b = header.indexOf("MSIE") > -1;
		    String fileName = null;

	        if (b) {
	            fileName = URLEncoder.encode(fileOrigin, "utf-8").replaceAll( "\\+", "%20" );
	        } else {
	        	fileName = URLEncoder.encode(fileOrigin, "utf-8").replaceAll( "\\+", "%20" );
	        }

			response.setContentType("application/download; utf-8");
			response.setContentLength((int) imageContent.length);
			response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName+".jpg" + "\";");
	        response.setHeader("Content-Transter-Encoding", "binary");

	        try{
		        OutputStream out = response.getOutputStream();
		        out.write(imageContent);
		        out.close();
	        }catch(Throwable e){
				e.printStackTrace();
			}
		}
    }



    /**
	 * ??????????????? ??????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/imgUserSave.do")
	public ModelAndView imgUserSave(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
    	int imgUserSave = 0;

		String fuid = request.getParameter("fuid");
		
        UserBioInfoVO userBioInfo = new UserBioInfoVO();
        userBioInfo.setFuid(fuid);
		
		try {
			//?????????????????? canvas?????? ??????
			byte[] imageByte = null;
			if (StringUtil.nvl(request.getParameter("imageType")).equals("file")) {
				MultipartFile file = null;
				Iterator<String> iterator = request.getFileNames();
	
				if (iterator.hasNext()) {
					file = request.getFile(iterator.next());
					LOGGER.debug("###[imgUserSave][file] iterator : " + file.getSize());
					LOGGER.debug("###[imgUserSave][file] filename : " + file.getOriginalFilename());
				}
				if (file != null) {
					imageByte = file.getBytes();
				} else {
					throw new RuntimeException("???????????? ??????!!");
				}
			} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
				String imgbase64 = (String) request.getParameter("imgUpload");
				String[] base64Arr = imgbase64.split(","); // image/png;base64, ??? ?????? ????????? ?????? ??????
				imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array??? ??????
				//LOGGER.debug("========= [addVisit][canvas] imageByte ==================");
				//LOGGER.debug(imageByte);
			} else {
				//throw new RuntimeException("?????? ????????? ??????!!");
				LOGGER.debug("?????? ????????? ??????!!");
				modelAndView.addObject("rst", -1);
				return modelAndView;
			}
			LOGGER.debug(">>> imageByte >> "+imageByte);
			LOGGER.debug("fuid : " + fuid);
	
	        userBioInfo.setImgByte(imageByte);
	        imgUserSave = userInfoService.imgUserInfoSave(userBioInfo);
	        
	        if(userBioInfo.getBioCnt().equals("0")) {
	        	if(imgUserSave > 0) {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101303", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //????????????	
	        	} else {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101304", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //????????????
	        	}
	        } else {
	        	if(imgUserSave > 0) {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101301", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //????????????
	        	} else {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101302", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //????????????
	        	}
	        }
		} catch(Exception e) {
			e.printStackTrace();
			commonService.sysLogSave(loginVO.getFsiteid(), "13101302", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}
    	modelAndView.addObject("rst", imgUserSave);
        return modelAndView;
    }

	/**
	 * ????????? ?????? ??????
	 * @param
	 * @return userInfo/tuserListPopup.do
	 * @throws Exception
	 */
	@RequestMapping(value={"/userInfo/tuserListPopup.do"})
	public String tuserListPopup(@RequestParam Map<String, Object> commandMap,HttpServletRequest request, ModelMap model) throws Exception {

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String fuid = StringUtil.isNullToString(commandMap.get("srchFid"));
		String funm = StringUtil.isNullToString(commandMap.get("srchFnm"));//new String(.getBytes("8859_1"),"UTF-8");
		String fcdno = StringUtil.isNullToString(commandMap.get("srchCdno"));

		//????????????
		LogInfoVO usrInfoVO = new LogInfoVO();
		usrInfoVO.setFuid(fuid);
		usrInfoVO.setFunm(funm);
		usrInfoVO.setFcdno(fcdno);

		//??????????????? ?????? ??????
		int totalCnt = userInfoService.getUsrListConnTotalCnt(usrInfoVO);

		//?????????
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(usrInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(usrInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		usrInfoVO.setSrchPage(srchPage);
		usrInfoVO.autoOffset();

		//?????????????????????
		List<LogInfoVO> userInfo = userInfoService.getUsrListConnPop(usrInfoVO);

		model.addAttribute("userInfo", userInfo);						//??????
		model.addAttribute("pagination", pageVO);						//?????????
		model.addAttribute("usrInfoVO", usrInfoVO);						//????????????

		return "cubox/basicInfo/tuser_list_popup";
	}

	/*????????? ?????? ???????????? zip ?????? test*/
	@RequestMapping(value = "/userInfo/getZipImageDown.do")
    // get ?????? ??????????????? ????????? ?????? PK ?????? ????????? ??????
    public ModelAndView getZipImageDown (@RequestParam Map<String, Object> commandMap,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		String fresn = StringUtil.nvl(request.getParameter("fdownresn"));
		String pwfuflg = null;

		//???????????? ????????????
		Map<String, String> m = new HashMap<String, String>();
		m.put("fip", commonUtils.getIPFromRequest(request));
		m.put("fdwnflg", "Z");
		m.put("fresn", fresn);	//reason
		LOGGER.debug("m >>>>> "+m);
		int rtnCnt = 0;
		File df = null;
		ModelAndView modelAndView = new ModelAndView();

		try {
			rtnCnt = userInfoService.setDownLoadResnLog(m);
			HashMap mpw = terminalInfoService.getZipSetPw ();
			pwfuflg = (mpw!=null)?(String) mpw.get("FUFLG"):null;

		/*	if(pwfuflg == null || !pwfuflg.trim().equals("Y")) {
				PrintWriter writer = response.getWriter();
				writer.println("<script type='text/javascript'>");
				writer.println("alert('???????????? ???????????? ????????? ????????????. ?????? ??? ?????? ???????????? ????????????.');");
				writer.println("history.back();");
				writer.println("</script>");
				writer.flush();
			} else {*/
		if(true) {
				if(rtnCnt > 0) {
					df = new File(CuboxProperties.getProperty("Globals.zipfilepath")+CuboxProperties.getProperty("Globals.zipfilename"));
				}
				modelAndView.setViewName("fileDownloadView");
				modelAndView.addObject("downloadFile", 	df);
		        modelAndView.addObject("fileOrigin", 	CuboxProperties.getProperty("Globals.zipfilename"));
		        
		        //2021-07-12 ??????????????? ?????????
		        commonService.sysLogSave(loginVO.getFsiteid(), "13101501", m.toString(), commonUtils.getIPFromRequest(request)); //?????????????????? ??????
			}
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.println("<script type='text/javascript'>");
			writer.println("alert('?????? ???????????? ??? ????????? ?????????????????????.');");
			writer.println("history.back();");
			writer.println("</script>");
			writer.flush();
		}
        return modelAndView;
    }

	/**
	 * ????????????????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/chk_user_edit_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/chkUserEditPopup.do")
	public String chkUserEditPopup (@RequestParam Map<String, Object> commandMap, HttpServletRequest request, ModelMap model) throws Exception {

		String fuLst = StringUtil.nullConvert(commandMap.get("fuLst"));

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//////////////////////////////////////////
		//???????????? ?????? ????????????
		model.addAttribute("fuLst", fuLst);
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);
		////////////////////////////////////////////////

		return "cubox/userInfo/chk_user_edit_popup";
	}

	/**
	 * ??????????????????????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/chkUserInfoSave.do")
	public ModelAndView chkUserInfoSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int uCnt = 0;
			String fauthtype = StringUtil.isNullToString(commandMap.get("fauthtype"));
	    	String fcdno = StringUtil.isNullToString(commandMap.get("fcdno"));
	    	String srchStartDate2 = StringUtil.isNullToString(commandMap.get("srchStartDate2"));
	    	String srchExpireDate2 = StringUtil.isNullToString(commandMap.get("srchExpireDate2"));
	    	String cfstatus = StringUtil.isNullToString(commandMap.get("cfstatus"));
	    	String fsvtype = StringUtil.isNullToString(commandMap.get("fsvtype"));

	    	UserInfoVO userInfoVO = new UserInfoVO();
	    	userInfoVO.setFpartcd1(loginVO.getSite_id());
	    	//userInfoVO.setFauthtype(fauthtype);
	    	userInfoVO.setFauthtype(GLOBAL_DEFAULT_AUTHTYPE);
	    	userInfoVO.setFsstatus("R");	//userInfoVO.setFsstatus("K");
	    	userInfoVO.setCfcdno(fcdno);
	    	userInfoVO.setCfstatus(cfstatus);								//????????????
	    	//userInfoVO.setFusdt2(srchStartDate2 	+ " 00:00:00.000");		//?????? ????????????
	    	//userInfoVO.setFuedt2(srchExpireDate2 	+ " 00:00:00.000");		//?????? ????????????
	    	userInfoVO.setFusdt2(srchStartDate2);		//?????? ????????????
	    	userInfoVO.setFuedt2(srchExpireDate2);		//?????? ????????????
	    	userInfoVO.setFsvtype(fsvtype);

	    try {
	    	String olst = StringUtil.isNullToString(commandMap.get("fuList")).trim();
	    	JSONParser parser = new JSONParser();
	    	Object obj = parser.parse( olst );
	    	JSONArray jsonArray = (JSONArray) obj;
	    	List<Map<String, Object>> lst = CommonUtils.getListMapFromJsonArray (jsonArray);
	    	userInfoVO.setFuids(lst);

	    	if(fsvtype != null && fsvtype.equals("A")) { //?????? ????????? ??????
	    		uCnt = userInfoService.allUserInfoLstSave(userInfoVO);
	    	} else {
	    		uCnt = userInfoService.chkUserInfoLstSave(userInfoVO);
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
		if(uCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "13101320", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101321", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
		}
		modelAndView.addObject("uCnt", 1);
		return modelAndView;
	}


	/**
	 * ????????? ????????? ?????? ?????????
	 * @param commandMap
	 * @param fuidList
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/fileImgZipSync.do")
	public ModelAndView fileImgZipSync(@RequestParam Map<String, Object> commandMap, String[] fuidList, HttpServletRequest request) throws Exception {

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	// fileImageZip.createImgfileZip();

		modelAndView.addObject("result", true);
		return modelAndView;
	}


	/**
	 * ???????????? ?????? ??????
	 * @param
	 * @return logInfo/downLog_popup
	 * @throws Exception
	 */
	@RequestMapping(value = "/logInfo/downloadLogPopup.do")
	public String downloadLogPopup(@RequestParam Map<String, Object> commandMap,HttpServletRequest request, ModelMap model) throws Exception {

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;

		UserInfoVO userInfoVO = new UserInfoVO();
		userInfoVO.setSrchPage(srchPage);
		userInfoVO.autoOffset();

		//?????????
		int totalCnt = userInfoService.getDownloadLogCnt(userInfoVO);

		//LOGGER.debug("srchPage >>> "+srchPage);

		//?????????
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(userInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(userInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		//????????? ??????????????????
		List<DownloadLogVO> downloadLogVO = userInfoService.getDownloadLogList(userInfoVO);

		model.addAttribute("downloadLogVO", downloadLogVO);				//????????? ??????????????????
		model.addAttribute("pagination", pageVO);						//?????????
		model.addAttribute("userInfoVO", userInfoVO);					//????????????
		return "cubox/userInfo/downLog_popup";
	}
	
	/**
	 * ????????? ????????? ?????? ??????
	 * @param commandMap
	 * @param userArr
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/chkUserExpire.do")
	public ModelAndView chkUserExpire(@RequestParam Map<String, Object> commandMap, String[] userArr, HttpServletRequest request) throws Exception {

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	int sCnt = 0;
    	for (String userStr : userArr) {
    		String[] temp = userStr.split("\\^");

	    	HashMap map = new HashMap();
	    	map.put("fuid", temp[0]);
	    	map.put("fcdno", temp[1]);

	    	try{
	        	userInfoService.setUserExpire(map);
	    		sCnt += 1;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}

    	String successYn = "N";
    	if(sCnt > 0){
    		successYn = "Y";
    	}else{
    		successYn = "N";
    	}
		modelAndView.addObject("result", successYn);
		return modelAndView;
	}

	/**
	 * ??????????????????????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/detectCallImg.do")
	public ModelAndView detectCallImg (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
		LOGGER.debug(">>>>>>>>>>> /userInfo/detectCallImg.do >>>>>>>>>>>>>");

		final String GLOBAL_DTAPI_IP = CuboxProperties.getProperty("Globals.detectApi.ip");
		final String GLOBAL_DTAPI_PORT = CuboxProperties.getProperty("Globals.detectApi.port");

		String url = "http://" + GLOBAL_DTAPI_IP + ":" + GLOBAL_DTAPI_PORT +"/v1/reqSVC";
    	String InputLine = "";
    	String responseBody = "";
    	HashMap<String, Object> result = new HashMap<String, Object>();
		int rst = 0;
		String imageString = null;
    	HttpURLConnection detectConn = null;

    	try{
    		URL Url = new URL(url);
    		detectConn = (HttpURLConnection) Url.openConnection();

    		JSONObject json = new JSONObject();
    		json.put("tx", "FACE_DETECT");
    		String reqBody = json.toJSONString();

    		detectConn.setDoOutput(true);
    		detectConn.setRequestMethod("POST");
    		detectConn.setRequestProperty("Content-Type", "application/json");
    		detectConn.setRequestProperty("Accept-Charset", "UTF-8");
    		detectConn.setConnectTimeout(20000);
    		detectConn.setReadTimeout(20000);

			OutputStream matchOs = detectConn.getOutputStream();
			matchOs.write(reqBody.getBytes("UTF-8"));
			matchOs.flush();

		    int responseCode = detectConn.getResponseCode();

		    LOGGER.debug(">>>> responseCode >>>>>"+responseCode);

			// ????????? ?????? ??????
			InputStreamReader isr = null;
		    if(responseCode == HttpURLConnection.HTTP_OK) {		// 200
				isr = new InputStreamReader(detectConn.getInputStream(), "UTF-8");
		    }else{
				isr = new InputStreamReader(detectConn.getErrorStream(), "UTF-8");
		    }
			BufferedReader matchIn = new BufferedReader(isr);
	        while ((InputLine = matchIn.readLine()) != null) {
	        	responseBody += InputLine;
	        }

	        JSONParser parser = new JSONParser();
			Object obj = parser.parse(responseBody);
			result = (HashMap<String, Object>) CommonUtils.getMapFromJsonObject((JSONObject) obj);
			//LOGGER.debug("123456 >>>> "+result);
			Object o = result.get("datas");
			List<HashMap<String, Object>> m = (List) o;
			imageString = m.get(0).get("buffimage").toString();
			boolean isBase64 = Base64.isBase64(imageString);
			if(isBase64) {
				imageString = "data:image/jpeg;base64,"+imageString;
				rst = 1;
			} else {
				rst = -1;
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		rst = -2;
    	} finally {
    		if(detectConn!=null) detectConn.disconnect();
		}
    	modelAndView.addObject("imageString", imageString);
    	modelAndView.addObject("rst", rst);
		return modelAndView;
	}

	/**
	 * ????????? ????????? zip ?????? ???????????? 
	 * @param commandMap
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/getSelZipImageDown.do")
    public ModelAndView getSelZipImageDown (@RequestParam Map<String, Object> commandMap,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {

		String fresn = StringUtil.nvl(request.getParameter("fdownresn"));

		//???????????? ????????????
		Map<String, String> m = new HashMap<String, String>();
		m.put("fip", commonUtils.getIPFromRequest(request));
		m.put("fdwnflg", "Z");
		m.put("fresn", fresn);	//reason
		LOGGER.debug("m >>>>> "+m);
		int rtnCnt = 0;
		File df = null;
		try {
			rtnCnt = userInfoService.setDownLoadResnLog(m);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if(rtnCnt > 0) {
			df = new File(CuboxProperties.getProperty("Globals.zipfilepath")+CuboxProperties.getProperty("Globals.zipfilename"));
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("fileDownloadView");
		modelAndView.addObject("downloadFile", 	df);
        modelAndView.addObject("fileOrigin", 	CuboxProperties.getProperty("Globals.zipfilename"));
        return modelAndView;
    }

	/**
	 * ?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_add_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userAddPopup.do")
	public String userAddPopup(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//////////////////////////////////////////
		//???????????? ?????? ????????????
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);
		////////////////////////////////////////////////

//		AuthGroupVO authGroupVO = new AuthGroupVO();
//
//		List<AuthGroupVO> totalAuthGroup = null;
//		if(loginVO.getSite_id().equals("11")){
//			totalAuthGroup = userInfoService.getTotalAuthGroupList11(authGroupVO);
//		}else if(loginVO.getSite_id().equals("12")){
//			totalAuthGroup = userInfoService.getTotalAuthGroupList12(authGroupVO);
//		}
//
//		model.addAttribute("totalAuthGroupList", totalAuthGroup);

		return "cubox/userInfo/user_add_popup";
	}
	
	/**
	 * ?????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_add_popup_new
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userAddPopup2.do")
	public String userAddPopup2(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		AuthorGroupVO vo = new AuthorGroupVO ();
		vo.setSiteId(loginVO.getFsiteid());
		vo.setUseYn("Y");
		List<AuthorGroupVO> authList = authorGroupService.getAuthorGroupList(vo);		

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//???????????? ?????? ????????????
		model.addAttribute("authList", authList);
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);

		return "cubox/userInfo/user_add_popup_new";
	}	

	/**
	 * ????????? ????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/addUserInfoSave.do")
    public ModelAndView addUserInfoSave(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fregid = loginVO.getFsiteid();

		//?????????????????? canvas?????? ??????
		byte[] imageByte = null;
		
		if( StringUtil.nvl(request.getParameter("imageType")).equals("file") ){
			MultipartFile file = null;
			Iterator<String> iterator = request.getFileNames();

			if(iterator.hasNext()) {
				file = request.getFile(iterator.next());
			}
			if(file != null) {
				imageByte = file.getBytes();
			} else {
				throw new RuntimeException("???????????? ??????!!");
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, ??? ?????? ????????? ?????? ??????
			imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array??? ??????
		}

		String fuid = StringUtil.nvl(request.getParameter("fuid"));
        String funm = StringUtil.nvl(request.getParameter("funm"));
        String futype = StringUtil.nvl(request.getParameter("futype"));
        String fvisitnum = StringUtil.nvl(request.getParameter("fvisitnum"));  //2021-04-14 ?????????????????????
        String cfstatus = StringUtil.nvl(request.getParameter("cfstatus"));						//????????????
        String srchStartDate2 = StringUtil.nvl(request.getParameter("srchStartDate2"));			//?????? ?????????
        String srchExpireDate2 = StringUtil.nvl(request.getParameter("srchExpireDate2"));		//?????? ?????????
        String fcdno = StringUtil.nvl(request.getParameter("fcdno"));							//????????????
        String fpartnm2 = StringUtil.nvl(request.getParameter("fpartnm2"));						//??????
        
        /* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? */
        //cardno
        String addfcdno = "";//"00"+fuid;
        
        if( !"".equals(fcdno) ) {//??????????????? ?????????(??? ?????? ????????? ????????? ????????? ????????? view??????)
        	addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fcdno).replace(" ", fillChar);
        }else if( "".equals(fcdno) ){//??????????????? ???????????? ??????????????? ????????? ???????????? ??????
        	addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
        }
        /* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? END */
        
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setFuid(fuid);

        if(fuid.length() != Integer.parseInt(fidDigit) || !fuid.matches("(^[0-9]{" + fidDigit + "}$)")) {
        	modelAndView.addObject("cardSaveCnt", -1);	//fuid ?????? ??????
            return modelAndView;
        } else {
        	int chk = userInfoService.getChkFuid(userInfoVO);
        	if(chk > 0) {
        		modelAndView.addObject("cardSaveCnt", -2);	//fuid ?????? ??????
                return modelAndView;
        	}
        }
        
        //2021-04-14 ?????????????????????
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);	//????????????????????? ??????
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("workgb", "A");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);	//????????????????????? ??????
					return modelAndView;
				}
			}
		}        

        userInfoVO.setFunm(funm);
        userInfoVO.setFutype(futype);
        userInfoVO.setFvisitnum(fvisitnum);
        userInfoVO.setFauthtype(GLOBAL_DEFAULT_AUTHTYPE);
        commonService.tcsidxSave("tuserinfo"); //tcsidx????????? fidx??????
		userInfoVO.setFsidx(commonService.getFsidx("tuserinfo")); //fsidx????????????
		userInfoVO.setFgroupid("112,11200001");
		userInfoVO.setFpartcd1(loginVO.getSite_id());
		userInfoVO.setFusdt(srchStartDate2);
		userInfoVO.setFuedt(srchExpireDate2);
        userInfoVO.setFcarno(addfcdno);
        userInfoVO.setFpartnm2(fpartnm2);
		userInfoVO.setFmobilefg("Q");
		userInfoVO.setFsstatus("Q");
		userInfoVO.setFregid(fregid);

		CardInfoVO cardInfoVO = new CardInfoVO();
		cardInfoVO.setFuid(userInfoVO.getFuid());
		cardInfoVO.setFcdno(addfcdno);
		commonService.tcsidxSave("tcard"); //tcsidx????????? fidx??????
		cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx????????????
		cardInfoVO.setFsdt(srchStartDate2);
		cardInfoVO.setFedt(srchExpireDate2);
		
		/* ???????????? ?????? ?????? */
		Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
		int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
		
		if( cnt > 0 ) {//???????????? ?????? ????????? ?????? ??????
			modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno ?????? ??????
			return modelAndView;
		}
		/* ???????????? ?????? ?????? END */
		
		int cardSaveCnt = 0;
		try {
			cardSaveCnt = userInfoService.addUserInfoSave (userInfoVO, cardInfoVO); //????????? ?????? ?????? (????????? ??????, ??????)
		} catch (Exception e) {
			cardSaveCnt = -3;
			e.printStackTrace();
		}

		if(cardSaveCnt == 2) {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101101", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //???????????? ?????? ?????? ??????
			commonService.sysLogSave(loginVO.getFsiteid(), "13101102", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //?????? ?????? ?????? ?????? ??????
			if(StringUtil.nvl(userInfoVO.getFutype()).equals("1")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101001", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //?????? ?????? ?????? ?????? ??????
			}
			
			//bio??????
			UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
			userBioInfoVO.setFuid(fuid);
			int imgUserSave = 0;
			if(imageByte != null && imageByte.length > 0) {
				userBioInfoVO.setImgByte(imageByte);
				imgUserSave = userInfoService.imgUserInfoSave(userBioInfoVO);
				if(imgUserSave > 0) {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101103", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
	        	} else {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101106", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
	        	}
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101107", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			}

		} else {
			//?????? ?????? ??? ?????? ?????? ?????? ?????? ??????
			commonService.sysLogSave(loginVO.getFsiteid(), "13101104", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			commonService.sysLogSave(loginVO.getFsiteid(), "13101105", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????
			if(StringUtil.nvl(userInfoVO.getFutype()).equals("1")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //?????? ?????? ?????? ?????? ??????
			}
		}
    	modelAndView.addObject("cardSaveCnt", cardSaveCnt);
        return modelAndView;
    }

	/**
	 * ????????? ???????????? (????????????,????????????,??????)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/addUserInfoNew.do")
	public ModelAndView addUserInfoNew(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		//?????????????????? canvas?????? ??????
		byte[] imageByte = null;
		if (StringUtil.nvl(request.getParameter("imageType")).equals("file")) {
			MultipartFile file = null;
			Iterator<String> iterator = request.getFileNames();

			if (iterator.hasNext()) {
				file = request.getFile(iterator.next());
			}
			if (file != null) {
				imageByte = file.getBytes();
			} else {
				throw new RuntimeException("???????????? ??????!!");
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, ??? ?????? ????????? ?????? ??????
			imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array??? ??????
		}
		
		// fuid ??????
		UserInfoVO userInfoVO = new UserInfoVO();
		
		int a = -1;
		do {
			a = userInfoService.getNewFuid(userInfoVO);
		} while (a > 0);
		String fuid = userInfoVO.getFuid();
		
		// ????????? ??????
		//String fuid = StringUtil.isNullToString(request.getParameter("fuid"));  //fuid ???????????? ??????
		String funm = StringUtil.isNullToString(request.getParameter("funm"));
		String futype = StringUtil.isNullToString(request.getParameter("futype"));
		String fvisitnum = StringUtil.isNullToString(request.getParameter("fvisitnum")); //2021-04-13 ?????????????????????
		String fpartnm1 = StringUtil.isNullToString(request.getParameter("fpartnm1"));
		String fpartnm2 = StringUtil.isNullToString(request.getParameter("fpartnm2"));
		String fpartnm3 = StringUtil.isNullToString(request.getParameter("fpartnm3"));
		String fcarno = StringUtil.isNullToString(request.getParameter("fcarno"));
		String ftel = StringUtil.isNullToString(request.getParameter("ftel"));
		String hpNo = StringUtil.isNullToString(request.getParameter("hp_no"));
		String fetc1 = StringUtil.isNullToString(request.getParameter("fetc1"));
		String fetc2 = StringUtil.isNullToString(request.getParameter("fetc2"));
		// ?????? ??????
		String fcdno = StringUtil.isNullToString(request.getParameter("fcdno"));
		String fstatus = StringUtil.nvl(request.getParameter("fstatus"), "Y");
		String fsdt = StringUtil.isNullToString(request.getParameter("fsdt"));
		String fedt = StringUtil.isNullToString(request.getParameter("fedt"));
		// ??????
		String auths = StringUtil.nvl(request.getParameter("arraryGroup"));
		String[] arrAuth = null;
		if(!auths.equals("")) {
			arrAuth = StringUtil.split(auths, ",");	
		}		
		
		/* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? */
		//cdno
		String addfcdno = "";//"00"+fuid;
		
		if( !"".equals(fcdno) ) {//??????????????? ?????????(??? ?????? ????????? ????????? ????????? ????????? view??????)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fcdno).replace(" ", fillChar);
		}else if( "".equals(fcdno) ){//??????????????? ???????????? ??????????????? ????????? ???????????? ??????
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}
		/* ??????????????? ????????? ??????????????? ????????? ???????????? ???????????? END */
		
		//UserInfoVO userInfoVO = new UserInfoVO(); //fuid ???????????? ??????
		//userInfoVO.setFuid(fuid);  //fuid ???????????? ??????

		if(fuid.length() != Integer.parseInt(fidDigit) || !fuid.matches("(^[0-9]{" + fidDigit + "}$)")) {
			modelAndView.addObject("cardSaveCnt", -1);	//fuid ?????? ??????
			return modelAndView;
		} else {
			int chk = userInfoService.getChkFuid(userInfoVO);
			if(chk > 0) {
				modelAndView.addObject("cardSaveCnt", -2);	//fuid ?????? ??????
				return modelAndView;
			}
		}

		// 2021-04-13 ????????????????????? ?????? ??????
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);	//????????????????????? ??????
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("workgb", "A");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);	//????????????????????? ??????
					return modelAndView;
				}
			}
		}
		
		userInfoVO.setFunm(funm);
		userInfoVO.setFutype(futype);
		userInfoVO.setFvisitnum(fvisitnum);
		userInfoVO.setFauthtype(GLOBAL_DEFAULT_AUTHTYPE);
		commonService.tcsidxSave("tuserinfo"); //tcsidx????????? fidx??????
		userInfoVO.setFsidx(commonService.getFsidx("tuserinfo")); //fsidx????????????
		//userInfoVO.setFgroupid("112,11200001");
		userInfoVO.setFpartcd1(loginVO.getSite_id());
		userInfoVO.setFusdt(fsdt);
		userInfoVO.setFuedt(fedt);
		userInfoVO.setFcarno(fcarno);
		userInfoVO.setFpartnm1(fpartnm1);
		userInfoVO.setFpartnm2(fpartnm2);
		userInfoVO.setFpartnm3(fpartnm3);
		userInfoVO.setFtel(ftel);
		userInfoVO.setHpNo(hpNo);
		userInfoVO.setFetc1(fetc1);
		userInfoVO.setFetc2(fetc2);
		userInfoVO.setFmobilefg("Q");
		userInfoVO.setFsstatus("Q");
		userInfoVO.setFregid(loginVO.getFsiteid());

		CardInfoVO cardInfoVO = new CardInfoVO();
		cardInfoVO.setFuid(userInfoVO.getFuid());
		cardInfoVO.setFcdno(addfcdno);
		commonService.tcsidxSave("tcard"); //tcsidx????????? fidx??????
		cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx????????????
		cardInfoVO.setFstatus(fstatus);
		cardInfoVO.setFsdt(fsdt);
		cardInfoVO.setFedt(fedt);
		
		AuthorGroupVO authVO = new AuthorGroupVO ();
		authVO.setFuid(fuid);
		authVO.setSiteId(loginVO.getSite_id());
		authVO.setGroupArray(arrAuth);
		authVO.setRegistId(loginVO.getFsiteid());
		authVO.setModifyId(loginVO.getFsiteid());
		
		/* ???????????? ?????? ?????? */
		Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
		int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
		
		if( cnt > 0 ) {//???????????? ?????? ????????? ?????? ??????
			modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno ?????? ??????
			return modelAndView;
		}
		/* ???????????? ?????? ?????? END */
		
		int cardSaveCnt = 0;
		try {
			cardSaveCnt = userInfoService.addUserInfoNew (userInfoVO, cardInfoVO, authVO); //????????? ???????????? (????????????,????????????,??????)
			LOGGER.error("###[addUserInfoNew] ???????????? (1)cardSaveCnt : {}", cardSaveCnt);
		} catch (Exception e) {
			LOGGER.error("###[addUserInfoNew] ???????????? ???????????? : {}", e.getMessage());
			cardSaveCnt = -3;
			e.printStackTrace();
		}

		if(cardSaveCnt > 0) {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101101", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????????????????
			commonService.sysLogSave(loginVO.getFsiteid(), "13101102", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //??????????????????
			if(arrAuth != null) commonService.sysLogSave(loginVO.getFsiteid(), "12101001", authVO.toString(), commonUtils.getIPFromRequest(request)); //???????????? ?????? ??????
			
			//bio??????
			UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
			userBioInfoVO.setFuid(fuid);
			int imgUserSave = 0;
			if(imageByte != null && imageByte.length > 0) {
				userBioInfoVO.setImgByte(imageByte);
				imgUserSave = userInfoService.imgUserInfoSave(userBioInfoVO);
				LOGGER.error("###[addUserInfoNew] ???????????? (2)imgUserSave : {}", imgUserSave);
				if(imgUserSave > 0) {
					commonService.sysLogSave(loginVO.getFsiteid(), "13101103", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //?????????????????????
				} else {
					commonService.sysLogSave(loginVO.getFsiteid(), "13101106", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //?????????????????????
				}
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101107", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????????????????
			}

		} else {
			//?????? ?????? ??? ?????? ?????? ?????? ?????? ??????
			commonService.sysLogSave(loginVO.getFsiteid(), "13101104", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //????????????????????????
			commonService.sysLogSave(loginVO.getFsiteid(), "13101105", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //??????????????????
			if(arrAuth != null) commonService.sysLogSave(loginVO.getFsiteid(), "12101002", authVO.toString(), commonUtils.getIPFromRequest(request)); //???????????? ?????? ??????
		}
		modelAndView.addObject("cardSaveCnt", cardSaveCnt);
		return modelAndView;
	}
	
	/**
	 * ????????? ?????? (????????????,?????????)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/modUserInfoNew.do")
	public ModelAndView modUserInfoNew(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		// ?????????????????? canvas?????? ??????
		/* byte[] imageByte = null; 
		if (StringUtil.nvl(request.getParameter("imageType")).equals("file")) {
			MultipartFile file = null;
			Iterator<String> iterator = request.getFileNames();

			if (iterator.hasNext()) {
				file = request.getFile(iterator.next());
			}
			if (file != null) {
				imageByte = file.getBytes();
			} else {
				throw new RuntimeException("???????????? ??????!!");
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, ??? ?????? ????????? ?????? ??????
			imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array??? ??????
		}*/		
		
		// ????????? ??????
		String fpartcd1 = StringUtil.isNullToString(request.getParameter("fpartcd1"));
		String fuid = StringUtil.isNullToString(request.getParameter("fuid"));
		String funm = StringUtil.isNullToString(request.getParameter("funm"));
		String futype = StringUtil.isNullToString(request.getParameter("futype"));
		String fvisitnum = StringUtil.isNullToString(request.getParameter("fvisitnum"));
		String fauthtype = StringUtil.isNullToString(request.getParameter("fauthtype"));
		String fpartnm1 = StringUtil.isNullToString(request.getParameter("fpartnm1"));
		String fpartnm2 = StringUtil.isNullToString(request.getParameter("fpartnm2"));
		String fpartnm3 = StringUtil.isNullToString(request.getParameter("fpartnm3"));
		String fcarno = StringUtil.isNullToString(request.getParameter("fcarno"));
		String ftel = StringUtil.isNullToString(request.getParameter("ftel"));
		String hpNo = StringUtil.isNullToString(request.getParameter("hp_no"));
		String fetc1 = StringUtil.isNullToString(request.getParameter("fetc1"));
		String fetc2 = StringUtil.isNullToString(request.getParameter("fetc2"));
		String cntnIp = commonUtils.getIPFromRequest(request);
		
		UserInfoVO userInfoVO = new UserInfoVO();
		userInfoVO.setFpartcd1(fpartcd1);
		userInfoVO.setFuid(fuid);
		userInfoVO.setFunm(funm);
		userInfoVO.setFutype(futype);
		userInfoVO.setFvisitnum(fvisitnum);
		userInfoVO.setFauthtype(fauthtype);
		userInfoVO.setFpartnm1(fpartnm1);
		userInfoVO.setFpartnm2(fpartnm2);
		userInfoVO.setFpartnm3(fpartnm3);
		userInfoVO.setFcarno(fcarno);
		userInfoVO.setFtel(ftel);
		userInfoVO.setHpNo(hpNo);
		userInfoVO.setFetc1(fetc1);
		userInfoVO.setFetc2(fetc2);
		userInfoVO.setFmodid(loginVO.getFsiteid());
		
		// 2021-04-13 ????????????????????? ?????? ??????
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "???????????????????????? ???????????? ????????????.");	//????????????????????? ??????
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("fuid", fuid);
				param.put("workgb", "M");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "???????????????????????? ?????????????????????. ?????? ???????????? ????????????.");	//????????????????????? ??????
					return modelAndView;
				}
			}
		}
		
		byte[] imageByte = null;
		try {
			imageByte = getImageByte(request);
		} catch(Exception e) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
			return modelAndView;
		}
		
		// bio??????
		UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
		if(imageByte != null && imageByte.length > 0) {
			userInfoVO.setFbioyn("Y");
			userBioInfoVO.setFuid(fuid);
			userBioInfoVO.setImgByte(imageByte);
		} else {
			userInfoVO.setFbioyn("N");
		}
		
		// ?????? ??????
		Map<String, Object> param = setUserChgLogParam("U", request, loginVO);

		try {
			int cnt = userInfoService.modUserInfoNew(userInfoVO, userBioInfoVO, param);
			commonService.sysLogSave(loginVO.getFsiteid(), "13101305", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //????????????????????????
			if(userInfoVO.getFbioyn().equals("Y")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101301", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //??????????????????	
			}
			modelAndView.addObject("result", "success");
			LOGGER.debug("###[modUserInfoNew] ????????? ???????????? ?????? ?????? cnt:{}, userInfoVO:{}, userBioInfoVO:{}", cnt, userInfoVO, userBioInfoVO);
		} catch(Exception e) {
			e.printStackTrace();
			commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //????????????????????????
			if(userInfoVO.getFbioyn().equals("Y")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101302", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //??????????????????	
			}
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
			LOGGER.error("###[modUserInfoNew] ????????? ???????????? ?????? ?????? userInfoVO:{}, userBioInfoVO:{}", userInfoVO, userBioInfoVO);			
		}
		
		return modelAndView;
	}	
	
	/**
	 * ????????? ?????? ??? ???????????? ??????
	 * @param commandMap
	 * @param groupArray
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/saveUserCardAuthInfo.do")
	public ModelAndView saveUserCardAuthInfo (@RequestParam Map<String, Object> param, String[] groupArray, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		String fuid = StringUtil.isNullToString(param.get("fuid"));
		String fpartcd1 = StringUtil.isNullToString(param.get("fpartcd1"));
		String fcdno = StringUtil.isNullToString(param.get("fcdno"));
		String newFcdno = StringUtil.isNullToString(param.get("newFcdno"));
		String isFcdnoChange = StringUtil.isNullToString(param.get("isFcdnoChange"));
		String fsdt = StringUtil.isNullToString(param.get("fsdt"));
		String fedt = StringUtil.isNullToString(param.get("fedt"));
		String fstatus = StringUtil.isNullToString(param.get("fstatus"));
		String cntnIp = commonUtils.getIPFromRequest(request);

		String addfcdno = "";//"00"+fuid;
		if( !"".equals(newFcdno) ) {//??????????????? ?????????(??? ?????? ????????? ????????? ????????? ????????? view??????)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), newFcdno).replace(" ", fillChar);
		}else if( "".equals(newFcdno) ){//??????????????? ???????????? ??????????????? ????????? ???????????? ??????
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}

		/* ???????????? ?????? ?????? */
		if("Y".equals(isFcdnoChange)) {
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFcdno(addfcdno);
			
			Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
			int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
			
			if( cnt > 0 ) {//???????????? ?????? ????????? ?????? ??????
				modelAndView.addObject("result", "fail"); //fcdno ?????? ??????
				modelAndView.addObject("message", "??????????????? ?????????????????????. ?????? ???????????? ????????????.\\n?????? " + StringUtil.nvl(result.get("funm")) + "???(???) ??????????????????.");
				return modelAndView;
			}
		}

		CardInfoVO cardInfoVO = new CardInfoVO();
		cardInfoVO.setFuid(fuid);
		cardInfoVO.setFpartcd1(fpartcd1);
		cardInfoVO.setFsdt(fsdt);
		cardInfoVO.setFedt(fedt);
		cardInfoVO.setFstatus(fstatus);
		cardInfoVO.setFcdno(fcdno);
		if("Y".equals(isFcdnoChange)) cardInfoVO.setFnewcdno(newFcdno);
		
		AuthorGroupVO authorGroupVO = new AuthorGroupVO();
		authorGroupVO.setFuid(fuid);
		authorGroupVO.setSiteId(fpartcd1);
		authorGroupVO.setGroupArray(groupArray);
		authorGroupVO.setRegistId(loginVO.getFsiteid());
		authorGroupVO.setModifyId(loginVO.getFsiteid());
		
		// ?????? ??????
		Map<String, Object> paramLog = new HashMap<String, Object>();
		
		int cntChgAuth = -1;
		String cfstatus = "";
		
		try {
			//?????? ??????
			cfstatus = StringUtil.nvl(cardInfoVO.getFstatus());
			//?????? ?????? ?????? ??????
			if(cfstatus.equals("Y") || cfstatus.equals("W")) {
				cntChgAuth = userInfoService.selectUserAuthGroupForChange(authorGroupVO);				
			}
			LOGGER.debug("##### [saveUserCardAuthInfo] ?????????(??????)??????????????????  cntChgAuth:{}, cfstatus:{}", cntChgAuth, cfstatus);			
			
			int cnt = 0;
			//cnt = authorGroupService.saveUserAuthGroup(vo);

			if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
				paramLog = setUserChgLogParam("A", request, loginVO);  //??????+?????? ??????
			} else {
				paramLog = setUserChgLogParam("C", request, loginVO);  //?????? ??????
			}
			
			cnt = userInfoService.saveUserCardAuthInfo(cardInfoVO, authorGroupVO, paramLog);
			LOGGER.debug("##### [saveUserCardAuthInfo] ?????? ??????  cnt:{}", cnt);
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
				commonService.sysLogSave(loginVO.getFsiteid(), "13101405", cardInfoVO.toString(), cntnIp); //????????????????????????
				if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
					commonService.sysLogSave(loginVO.getFsiteid(), "12111001", authorGroupVO.toString(), cntnIp); //????????? ?????? ?????? ??????	
				}
			} else {
				modelAndView.addObject("result", "fail");
				commonService.sysLogSave(loginVO.getFsiteid(), "13101406", cardInfoVO.toString(), cntnIp); //????????????????????????
				if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
					commonService.sysLogSave(loginVO.getFsiteid(), "12101002", authorGroupVO.toString(), cntnIp); //????????? ?????? ?????? ??????
				}
			}
			
		} catch(Exception e) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "13101406", cardInfoVO.toString(), cntnIp); //????????????????????????
			if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", authorGroupVO.toString(), cntnIp); //????????? ?????? ?????? ??????
			}
			e.printStackTrace();
		}		

		modelAndView.addObject("fuid", fuid);
		modelAndView.addObject("siteId", fpartcd1);
		return modelAndView;
	}
	
	/**
	 * ???????????????????????????????????? ??????
	 * @param commandMap ????????????????????? commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/selZipDownloadPop.do")
	public String selZipDownloadPop (@RequestParam Map<String, Object> commandMap, HttpServletRequest request, ModelMap model) throws Exception {

		String fuLst = StringUtil.nullConvert(commandMap.get("fuLst"));

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//???????????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//??????????????? ??????????????????
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//???????????? ??????????????????
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//???????????? ??????????????????

		//////////////////////////////////////////
		//???????????? ?????? ????????????
		model.addAttribute("fuLst", fuLst);
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);
		////////////////////////////////////////////////

		return "cubox/userInfo/zip_download_popup";
	}

	/**
	 * ?????? ???????????? ?????????
	 * @param commandMap ????????????????????? commandMap
	 * @return /userInfo/excelUploadProgress.do
	 * @throws Exception
	 */
    @RequestMapping(value = "/userInfo/excelUploadProgress.do")
    public ModelAndView excelUploadProgress(HttpServletRequest request) throws Exception {
        int resultData = 0;
        ModuleUtil moduleUtil = null;
        String status = null;
        try {
        	moduleUtil = ModuleUtil.getInstance();
        	status = moduleUtil.getCurrentState();
        	LOGGER.debug("excelUploadProgress status >>> "+status);
        	if(status != null) {
        		if(status.equals("B")) {
             		int a =moduleUtil.getCurrentStateCount();
                     int b =moduleUtil.getTotalRowCount();
                     //ModuleServiceImpl.getCurrentStateCount()/ModuleServiceImpl.getTotalRowCount()*100
                     //LOGGER.debug ("a>>>>>>>>>>"+a);
                     //LOGGER.debug ("b>>>>>>>>>>"+b);

                     BigDecimal aGd = new BigDecimal(a);
                     BigDecimal bGd = new BigDecimal(b);

                     if(bGd != null && bGd.compareTo(BigDecimal.ZERO) > 0) {
                    	 resultData = aGd.divide(bGd, 2, BigDecimal.ROUND_CEILING).multiply(new BigDecimal(100)).intValue();
                     }
     	        } else if(status.equals("C")) {
     	        	resultData = 100;
     	        }
        	} else {
        		resultData = 100;
        	}
        } catch (Exception e) {
        	resultData = 100;
			e.printStackTrace();
		}
        //LOGGER.debug("resultData >>> "+resultData);
        //LOGGER.debug("status >>> "+status);

        ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
        modelAndView.addObject("rstcnt", resultData);
        return modelAndView;
    }

    @RequestMapping(value = "/userInfo/excelUploadProgressClear.do")
    public ModelAndView excelUploadProgressClear(HttpServletRequest request) throws Exception {
    	int rst = 0;
    	try {
    		ModuleUtil.getInstance().resetModuleUtil();
    	} catch (Exception e) {
			e.printStackTrace();
			rst = -1;
		}
    	rst = 1;
    	LOGGER.debug("rst : " + rst);
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
        modelAndView.addObject("rstcnt", rst);
        return modelAndView;
    }

    /**
	 * ???????????????????????????
	 * @param commandMap ????????????????????? commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userAuthorGroupPopup.do")
	public String userAuthorGroupPopup(ModelMap model, @RequestParam("fuid") String fuid, @RequestParam("fpartcd1") String fpartcd1,
			@RequestParam(value="sfcdno", required=false) String sfcdno, HttpServletRequest request) throws Exception {

		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setFuid(fuid);
		userInfo.setFpartcd1(fpartcd1);

		//?????? ?????? ??????
		//AuthorGroupVO vo = new AuthorGroupVO ();
		//vo.setUseYn("Y");
		//List<AuthorGroupVO> athorGroupList = authorGroupService.getAuthorGroupList(vo);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("fuid", fuid);
		param.put("fpartcd1", fpartcd1);
		param.put("fcdno", sfcdno);
		
		//Map<String, String> map = new HashMap<String, String>();
		param = userInfoService.getUserStatus(param);
		
		model.addAttribute("userInfo", param);
		
		//model.addAttribute("sfcdno", sfcdno);
		//model.addAttribute("fuid", fuid);
		//model.addAttribute("siteId", fpartcd1);
		//model.addAttribute("athorGroupList", athorGroupList);

		return "cubox/userInfo/user_author_group_popup";
	}
	
	/**
	 * ????????? ?????? ?????? ??????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/saveUserAuthGroup.do")
	public ModelAndView saveUserAuthGroup (@RequestParam Map<String, Object> commandMap, String[] groupArray,  HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		int cnt = 0;
		String fuid= commandMap.get("fuid").toString();
		String siteId= commandMap.get("siteId").toString();
		AuthorGroupVO vo = new AuthorGroupVO ();
		vo.setFuid(fuid);
		vo.setSiteId(siteId);
		vo.setGroupArray(groupArray);
		vo.setRegistId(loginVO.getFsiteid());
		vo.setModifyId(loginVO.getFsiteid());	

		try {
			cnt = authorGroupService.saveUserAuthGroup(vo);
			
			if(cnt > 0) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12111001", vo.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ?????? ??????
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", vo.toString(), commonUtils.getIPFromRequest(request)); //????????? ?????? ?????? ??????
			}
			
		} catch(Exception e) {
			commonService.sysLogSave(loginVO.getFsiteid(), "12101002", vo.toString(), commonUtils.getIPFromRequest(request));
			e.printStackTrace();
		}
		

		modelAndView.addObject("fuid", fuid);
		modelAndView.addObject("siteId", siteId);
		return modelAndView;
	}
	
	/**
	 * ???????????? ???????????? ????????????
	 * @param commandMap ????????????????????? commandMap
	 * @param model ????????????
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/userInfo/getUserAuthGroupInfo.do")
	public ModelAndView getUserAuthGroupInfo (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String fuId = StringUtil.isNullToString( commandMap.get("selFuId") );
		String siteId = StringUtil.isNullToString( commandMap.get("selSiteId") );

		List<AuthorGroupVO> totalGroupList = null; //???????????????
		List<AuthorGroupVO>  userGroupList = null; //????????? ?????? ?????????

		AuthorGroupVO vo = new AuthorGroupVO ();
		vo.setFuid(fuId);
		vo.setSiteId(siteId);
		vo.setUseYn("Y");
		totalGroupList = authorGroupService.getAuthorGroupList(vo);
		userGroupList = authorGroupService.getUserAuthorGroupList(vo);

		modelAndView.addObject( "totalGroupList", totalGroupList);
		modelAndView.addObject(  "userGroupList", userGroupList );

		return modelAndView;
	}
	
	/**
	 * ??????????????? ?????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_by_auth_list
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userListByAuth.do")
	public String userListByAuth(ModelMap model, @RequestParam Map<String, Object> param, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		UserInfoSearchVO srchVO = new UserInfoSearchVO();
		PaginationVO pageVO = new PaginationVO();
		List<Map<String, Object>> userInfoList = null;
		
		if(!CommonUtils.empty(param)) {
		
			String str = StringUtil.nvl(param.get("srchCardStatus"));
			String[] arr = StringUtils.split(str, ",");
			param.put("arrCardStatus", arr);
			
			String auth = StringUtil.nvl(param.get("srchAuthGroupCd"));
			String[] authArr = StringUtils.split(auth, ",");
			param.put("arrAuthGroupCd", authArr);
			
			
			// paging
			int srchPage       = Integer.parseInt(StringUtil.nvl(param.get("srchPage"), "1"));
			int srchRecPerPage = Integer.parseInt(StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(srchVO.getSrchCnt())));
	
			int totalCnt = userInfoService.getUserListByAuthCount(param);
			if(srchRecPerPage == 0) srchRecPerPage = totalCnt;
			
			param.put("offset", getOffset(srchPage, srchRecPerPage));
			param.put("srchCnt", srchRecPerPage);
	
			pageVO.setCurPage(srchPage);
			pageVO.setRecPerPage(srchRecPerPage);
			pageVO.setTotRecord(totalCnt);
			pageVO.setUnitPage(srchVO.getCurPageUnit());
			pageVO.calcPageList();
	
			userInfoList = userInfoService.getUserListByAuth(param);
		}
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//?????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page??? record ???
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//????????????		

		model.addAttribute("centerCombo" , centerCombo); //????????????
		model.addAttribute("userType"    , userType);
		model.addAttribute("cntPerPage"  , cntPerPage);
		model.addAttribute("cardStatus"  , cardStatus);
		
		model.addAttribute("param"       , param);        //????????????
		model.addAttribute("pagination"  , pageVO);       //paging
		model.addAttribute("userInfoList", userInfoList); //????????????

		return "cubox/userInfo/user_by_auth_list";
	}
	
	@RequestMapping(value = "/userInfo/userListByAuthExcel.do")
	public ModelAndView userListByAuthExcel(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			
			String chkTextArray = StringUtil.nvl(request.getParameter("chkTextArray"));
			String[] chkText = chkTextArray.split(",");

			String str = StringUtil.nvl(param.get("srchCardStatus"));
			String arr[] = StringUtils.split(str, ",");
			param.put("arrCardStatus", arr);
			
			String auth = StringUtil.nvl(param.get("srchAuthGroupCd"));
			String[] authArr = StringUtils.split(auth, ",");
			param.put("arrAuthGroupCd", authArr);			

			List<ExcelVO> list = userInfoService.getUserListByAuthExcel(param);

			commonService.sysLogSave(loginVO.getFsiteid(), "13101209", param.toString(), commonUtils.getIPFromRequest(request)); //????????????
		
			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", list);
			modelAndView.addObject("excelName", "??????????????????????????????");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}	
	
	/**
	 * ???????????? ?????????
	 * @param commandMap ????????????????????? commandMap
	 * @return userInfo/user_by_gate_list
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userListByGate.do")
	public String userListByGate(ModelMap model, @RequestParam Map<String, Object> param, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		UserInfoSearchVO srchVO = new UserInfoSearchVO();
		PaginationVO pageVO = new PaginationVO();
		List<Map<String, Object>> userInfoList = null;
		
		if(!CommonUtils.empty(param)) {
			String str = StringUtil.nvl(param.get("srchCardStatus"));
			String[] arr = StringUtils.split(str, ",");
			param.put("arrCardStatus", arr);
			
			String gate = StringUtil.nvl(param.get("srchGateId"));
			String[] gateArr = StringUtils.split(gate, ",");
			param.put("arrGateId", gateArr);
			
			// paging
			int srchPage       = Integer.parseInt(StringUtil.nvl(param.get("srchPage"), "1"));
			int srchRecPerPage = Integer.parseInt(StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(srchVO.getSrchCnt())));

			int totalCnt = userInfoService.getUserListByGateCount(param);
			if(srchRecPerPage == 0) srchRecPerPage = totalCnt;
			
			param.put("offset", getOffset(srchPage, srchRecPerPage));
			param.put("srchCnt", srchRecPerPage);		

			pageVO.setCurPage(srchPage);
			pageVO.setRecPerPage(srchRecPerPage);
			pageVO.setTotRecord(totalCnt);
			pageVO.setUnitPage(srchVO.getCurPageUnit());
			pageVO.calcPageList();

			userInfoList = userInfoService.getUserListByGate(param);		
		}
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//?????? ??????????????????
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//??????????????? ??????????????????
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page??? record ???
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//????????????		

		model.addAttribute("centerCombo" , centerCombo); //????????????
		model.addAttribute("userType"    , userType);
		model.addAttribute("cntPerPage"  , cntPerPage);
		model.addAttribute("cardStatus"  , cardStatus);
		
		model.addAttribute("param"       , param);        //????????????
		model.addAttribute("pagination"  , pageVO);       //paging
		model.addAttribute("userInfoList", userInfoList); //????????????

		return "cubox/userInfo/user_by_gate_list";
	}
	
	@RequestMapping(value = "/userInfo/userListByGateExcel.do")
	public ModelAndView userListByGateExcel(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			
			String chkTextArray = StringUtil.nvl(request.getParameter("chkTextArray"));
			String[] chkText = chkTextArray.split(",");

			String str = StringUtil.nvl(param.get("srchCardStatus"));
			String arr[] = StringUtils.split(str, ",");
			param.put("arrCardStatus", arr);
			
			String gate = StringUtil.nvl(param.get("srchGateId"));
			String[] gateArr = StringUtils.split(gate, ",");
			param.put("arrGateId", gateArr);

			List<ExcelVO> list = userInfoService.getUserListByGateExcel(param);

			commonService.sysLogSave(loginVO.getFsiteid(), "13101210", param.toString(), commonUtils.getIPFromRequest(request)); //????????????

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", list);
			modelAndView.addObject("excelName", "???????????????????????????");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}
	
	/**
	 * ????????? ?????? ?????? Param 
	 * @param sChgClCd
	 * @param request
	 * @param loginVO
	 * @return
	 */
	private Map<String, Object> setUserChgLogParam(String sChgClCd, HttpServletRequest request, LoginVO loginVO) {
		Map<String, Object> param = new HashMap<String, Object>();
		
		String chgResn = StringUtil.nvl(request.getParameter("chg_resn"));
		String gb = StringUtil.nvl(request.getParameter("gb"));
		String fevttm = StringUtil.nvl(request.getParameter("fevttm"));
		String fcdno = StringUtil.nvl(request.getParameter("fcdno"));
		
		if(chgResn.equals("")) {
			if(sChgClCd.equals("U")) chgResn = "??????????????????";
			else if(sChgClCd.equals("C")) chgResn = "??????????????????";
			else if(sChgClCd.equals("A")) chgResn = "???????????????????? ??????";
			else if(sChgClCd.equals("D")) chgResn = "???????????????";
			else if(sChgClCd.equals("R")) chgResn = "???????????????";
			else if(sChgClCd.equals("X")) chgResn = "?????????????????????";
			else chgResn = "?????????????????????";
		}
		
		if(gb.equals("AS")) {
			chgResn += "(???????????????)";

			param.put("gb"    , gb);
			param.put("fevttm", fevttm);
			param.put("fcdno" , fcdno);
		}
		
		param.put("chg_cl_cd", sChgClCd);
		param.put("chg_resn" , chgResn);
		param.put("cntn_ip"  , commonUtils.getIPFromRequest(request));
		param.put("reg_id"   , loginVO.getFsiteid());

		return param;
	}	
	
	private int getOffset(int srchPage, int srchCnt) {
		int offset = (srchPage - 1) * srchCnt;
		if(offset < 0) offset = 0;
		return offset;
	}
}