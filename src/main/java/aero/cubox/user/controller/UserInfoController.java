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
	
	/* 카드번호 생성 자릿수 */
	@Value("#{property['Globals.cardDigit']}")
	private String cardDigit;
	
	/* 고유번호 생성 자릿수 */
	@Value("#{property['Globals.fidDigit']}")
	private String fidDigit;
	
	/* 채워질 기본문자 */
	@Value("#{property['Globals.fillChar']}")
	private String fillChar;

	/* 채워질 방향 */
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
		
		// 최초 조회시 default 값 셋팅
		if(StringUtil.nvl(param.get("pagegb")).equals("U") && !param.containsKey("srchFustatus")) { //출입자관리
			srchFunm = "funm";
			srchFexpireyn = "N";
			srchFustatus = "Y";
		} else if(StringUtil.nvl(param.get("pagegb")).equals("UE") && !param.containsKey("srchFexpireyn")) { //출입자만료관리
			srchFunm = "funm";
			srchFexpireyn = "Y";
		}
		
		List<String> srchColChk = new ArrayList<String>();
		String colChkQuery = "";
		
		List<String> srchColChk2 = new ArrayList<String>();
		String colChkQuery2 = "";
		
		try {
			// (다중조건1)FID, 이름, 카드번호
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
			
			// (다중조건2)회사, 부서
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
			
			// 선택 엑셀 저장
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
	 * 출입자 관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
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

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());  //센터 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype");         //출입자타입 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");     //page당 record 수
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_Fustatus");     //출입자상태

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
	 * 출입자 관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
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

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());  //센터 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype");         //출입자타입 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");     //page당 record 수
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_Fustatus");     //출입자상태
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus");  //카드상태
		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");      //카드상태

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
	 * 출입자만료관리
	 * @param commandMap 파라메터전달용 commandMap
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

		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());  //센터 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype");         //출입자타입 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");     //page당 record 수
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus");  //카드상태
		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");      //카드상태


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
	 * 사용자추가팝업
	 * @param commandMap 파라메터전달용 commandMap
	 * @return 
	 * @throws Exception
	 */
	/*
	@RequestMapping(value="/userInfo/userAddPopup.do")
	public String userAddPopup(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//////////////////////////////////////////
		//콤보박스 코드 가져오기
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
	 * 전체권한그룹 검색(신규등록)
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	 * 카드중복체크
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	 * 사용자 신규등록
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/newUserInfoSave.do")
    public ModelAndView newUserInfoSave(MultipartHttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		//첨부파일인지 canvas인지 확인
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
			String[] base64Arr = imgbase64.split(","); // image/png;base64, 이 부분 버리기 위한 작업
			imgByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array로 변경
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
		commonService.tcsidxSave("tuserinfo"); //tcsidx테이블 fidx추가
		userInfoVO.setFsidx(commonService.getFsidx("tuserinfo")); //fsidx가져오기
		userInfoVO.setFgroupid(authGroupFtidText);
		userInfoVO.setFmobilefg("Q");
		userInfoVO.setFsstatus("Q");

		int userSaveCnt = userInfoService.newUserInfoSave(userInfoVO); //출입자 정보 등록

		int cardSaveCnt = 0;

		if(userSaveCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "13101101", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장

			commonService.tcsidxSave("tcard"); //tcsidx테이블 fidx추가
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFuid(userInfoVO.getFuid());
			cardInfoVO.setFcdno(addfcdno);
			cardInfoVO.setFsdt(cfusdt);
			cardInfoVO.setFedt(cfuedt);
			cardInfoVO.setFcdnum(cfcdnum);
			cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx가져오기

			cardSaveCnt = userInfoService.addUserCdno(cardInfoVO); //카드 정보 등록

			if(cardSaveCnt > 0){
				commonService.sysLogSave(loginVO.getFsiteid(), "13101102", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
				userInfoService.userFsstatusChange(userInfoVO);

				UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
				userBioInfoVO.setFuid(fuid);
				int imgUserSave = 0;
				if(imgByte != null && imgByte.length > 0) {
					userBioInfoVO.setImgByte(imgByte);
					imgUserSave = userInfoService.imgUserInfoSave(userBioInfoVO);
					if(imgUserSave > 0){
		        		commonService.sysLogSave(loginVO.getFsiteid(), "13101103", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		        	}else{
		        		commonService.sysLogSave(loginVO.getFsiteid(), "13101106", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		        	}
				}else{
					commonService.sysLogSave(loginVO.getFsiteid(), "13101107", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
				}
			}else{
				commonService.sysLogSave(loginVO.getFsiteid(), "13101105", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}

		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101104", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}


        ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	modelAndView.addObject("cardSaveCnt", cardSaveCnt);
        return modelAndView;
    }

	/**
	 * 사용자수정팝업
	 * @param commandMap 파라메터전달용 commandMap
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

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//////////////////////////////////////////
		//콤보박스 코드 가져오기
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
		model.addAttribute("userBioInfo", userBioInfo);					//사용자 바이오정보(사진)
		model.addAttribute("userInfo", userInfo2);						//사용자 기본정보
		model.addAttribute("cardInfoList", cardInfoList);				//카드 리스트

		return "cubox/userInfo/user_edit_popup";
	}

	/**
	 * 출입자 수정 팝업
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
		
		// 출입자
		userInfo = userInfoService.getUserInfo2(userInfo);

		// 카드 (출입자:카드=1:1 상태에서는 필요없음)
		//List<CardInfoVO> cardInfoList = userInfoService.getCardInfoList(fuid);
		
		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//콤보박스 코드 가져오기
		//model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);

		model.addAttribute("fcdno", fcdno);          //사용자 기본정보
		model.addAttribute("userInfo", userInfo);          //사용자 기본정보
		//model.addAttribute("cardInfoList", cardInfoList);  //카드 리스트

		return "cubox/userInfo/user_edit_popup_new";
	}	
	
	/**
	 * 전체권한그룹 검색(사용자수정)
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	 * 권한그룹가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	 * 카드정보가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	 * 카드정보저장하기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
		cardInfoVO.setFsdt(srchStartDate2);		//카드 시작일자
		cardInfoVO.setFedt(srchExpireDate2);		//카드 종료일자
		cardInfoVO.setFpartcd1(userInfo.getFpartcd1());   //tauthtogate_main 11번인지 12번인 체크
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
			commonService.sysLogSave(loginVO.getFsiteid(), "13101405", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101406", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}

	/**
	 * 카드추가하기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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

			commonService.tcsidxSave("tcard"); //tcsidx테이블 fidx추가

			cardInfoVO.setFsdt(userInfo.getFusdt());
			cardInfoVO.setFedt(userInfo.getFuedt());
			cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx가져오기
			addCdnoCnt = userInfoService.addUserCdno(cardInfoVO);
			if(addCdnoCnt > 0){
				userInfo.setFsstatus("Q");
				userInfoService.userFsstatusChange(userInfo);
				commonService.sysLogSave(loginVO.getFsiteid(), "13101401", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}else{
				commonService.sysLogSave(loginVO.getFsiteid(), "13101402", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101403", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}

		LOGGER.debug("cdnoCnt : " + cdnoCnt);
		LOGGER.debug("fuid : " + fuid);


		modelAndView.addObject("cdnoCnt", cdnoCnt);
		modelAndView.addObject("addCdnoCnt", addCdnoCnt);

		return modelAndView;
	}

	/**
	 * 사용자정보저장하기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
		
		/* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 */
		//cardno
		String addfcdno = "";//"00"+fuid;
		
		if( !"".equals(newFcdno) ) {//카드번호가 있으면(이 값은 무조건 숫자만 넘어옴 체크는 view에서)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), newFcdno).replace(" ", fillChar);
		}else if( "".equals(newFcdno) ){//카드번호가 빈값이면 고유번호를 사용해 카드번호 생성
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}
		/* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 END */
		
		/* 카드번호 중복 체크 */
		if("Y".equals(isFcdnoChange)) {
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFcdno(addfcdno);
			
			Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
			int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
			
			if( cnt > 0 ) {//입력받은 카드 번호가 조회 되면
				modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno 중복 오류
				return modelAndView;
			}
		}
		/* 카드번호 중복 체크 END */
		
		// 2021-04-13 방문객카드번호 중복 체크
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);  //방문객카드번호 없음
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("fuid", fuid);
				param.put("workgb", "M");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);  //방문객카드번호 중복
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
			userInfoVO.setCfstatus(cfstatus);								//카드상태
			//userInfoVO.setFusdt2(srchStartDate2 	+ " 00:00:00.000");		//카드 시작일자
			//userInfoVO.setFuedt2(srchExpireDate2 	+ " 00:00:00.000");		//카드 종료일자
			userInfoVO.setFusdt2(startDate);		//카드 시작일자
			userInfoVO.setFuedt2(expireDate);		//카드 종료일자
			userInfoVO.setFutype(futype);
			userInfoVO.setFvisitnum(fvisitnum);
			userInfoVO.setFunm(funm);
			userInfoVO.setNewFcdno(addfcdno);
			userInfoVO.setFpartnm2(fpartnm2);
			userInfoVO.setFmodid(loginVO.getFsiteid());
			
			uCnt = userInfoService.userInfoSave(userInfoVO);
			if(uCnt > 0) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101305", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}
		} catch(Exception e) {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			modelAndView.addObject("message", e.getMessage());			
		}
		
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}
	
	
	private byte[] getImageByte(MultipartHttpServletRequest request) {
		
		byte[] imageByte = null;
		
		try {
			//첨부파일인지 canvas인지 확인
			if (StringUtil.nvl(request.getParameter("imageType")).equals("file")) {
				MultipartFile file = null;
				Iterator<String> iterator = request.getFileNames();

				if (iterator.hasNext()) {
					file = request.getFile(iterator.next());
				}
				if (file != null) {
					imageByte = file.getBytes();
				} else {
					throw new RuntimeException("첨부파일이 없습니다.");
				}
			} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
				String imgbase64 = (String) request.getParameter("imgUpload");
				String[] base64Arr = imgbase64.split(","); // image/png;base64, 이 부분 버리기 위한 작업
				imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array로 변경
			}	
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("이미지가 없습니다.");
		}
				
		return imageByte;
	}
	
	/**
	 * 2021-04-20 출입자정보 수정저장, 사진,개인정보를 한번에 저장함.
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

		// 출입자 정보
		String fpartcd1 = StringUtil.isNullToString(request.getParameter("fpartcd1"));
		String fuid = StringUtil.isNullToString(request.getParameter("fuid"));
		String funm = StringUtil.isNullToString(request.getParameter("funm"));
		String futype = StringUtil.isNullToString(request.getParameter("futype"));
		String fauthtype = StringUtil.isNullToString(request.getParameter("fauthtype"));
		String fvisitnum = StringUtil.isNullToString(request.getParameter("fvisitnum"));
		String fpartnm2 = StringUtil.isNullToString(request.getParameter("fpartnm2"));
		// 카드 정보
		String fcdno = StringUtil.isNullToString(request.getParameter("fcdno"));
		String newFcdno = StringUtil.isNullToString(request.getParameter("newFcdno"));
		String isFcdnoChange = StringUtil.isNullToString(request.getParameter("isFcdnoChange"));
		String startDate = StringUtil.isNullToString(request.getParameter("startDate"));
		String expireDate = StringUtil.isNullToString(request.getParameter("expireDate"));
		String fstatus = StringUtil.isNullToString(request.getParameter("cfstatus"));

		/* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 */
		//cardno
		String addfcdno = "";//"00"+fuid;
		if( !"".equals(newFcdno) ) {//카드번호가 있으면(이 값은 무조건 숫자만 넘어옴 체크는 view에서)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), newFcdno).replace(" ", fillChar);
		}else if( "".equals(newFcdno) ){//카드번호가 빈값이면 고유번호를 사용해 카드번호 생성
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}
		/* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 END */
		
		/* 카드번호 중복 체크 */
		if("Y".equals(isFcdnoChange)) {
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFcdno(addfcdno);
			
			Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
			int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
			
			if( cnt > 0 ) {//입력받은 카드 번호가 조회 되면
				modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno 중복 오류
				return modelAndView;
			}
		}
		/* 카드번호 중복 체크 END */
		
		// 2021-04-13 방문객카드번호 중복 체크
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);  //방문객카드번호 없음
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("fuid", fuid);
				param.put("workgb", "M");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);  //방문객카드번호 중복
					return modelAndView;
				}
			}
		}

		int uCnt = 0;
		UserInfoVO userInfoVO = new UserInfoVO();
		
		try {
			// 개인정보
			userInfoVO.setFpartcd1(fpartcd1);
			userInfoVO.setFuid(fuid);
			userInfoVO.setFunm(funm);
			userInfoVO.setFutype(futype);
			userInfoVO.setFauthtype(fauthtype);
			userInfoVO.setFvisitnum(fvisitnum);
			userInfoVO.setFpartnm2(fpartnm2);
			userInfoVO.setFmodid(loginVO.getFsiteid());

			// bio정보
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
				commonService.sysLogSave(loginVO.getFsiteid(), "13101305", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //개인정보수정성공
				if(userInfoVO.getFbioyn().equals("Y")) {
					commonService.sysLogSave(loginVO.getFsiteid(), "13101301", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //사진수정성공
				}
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //개인정보수정실패
			}
		} catch(Exception e) {
			modelAndView.addObject("message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //개인정보수정실패
		}
		
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}	

	/**
	 * 사용자정보-전화번호 수정
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
			commonService.sysLogSave(loginVO.getFsiteid(), "13101307", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		} else {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101308", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}

	/**
	 * 사용자정보-차번호 수정
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
			commonService.sysLogSave(loginVO.getFsiteid(), "13101309", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101310", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}

	/**
	 * 사용자정보-비고 수정
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
			commonService.sysLogSave(loginVO.getFsiteid(), "13101311", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101312", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}
		modelAndView.addObject("uCnt", uCnt);

		return modelAndView;
	}
	
	/**
	 * 출입자 삭제
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
    		modelAndView.addObject("message", "삭제할 출입자 정보가 정확하지 않습니다.");
    	} else {
    		try {
            	userInfoVO.setFuid(fuid);
            	userInfoVO.setFpartcd1(fpartcd1);
            	userInfoVO.setFmodid(loginVO.getFsiteid());
            	
        		// 변경 로그
        		Map<String, Object> param = setUserChgLogParam("D", request, loginVO);

            	cnt = userInfoService.userInfoDel(userInfoVO, param);
        		if(cnt > 0) {
        			modelAndView.addObject("result", "success");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101313", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 삭제 성공
        		} else {
        			modelAndView.addObject("result", "fail");
        			modelAndView.addObject("message", "출입자 삭제 중 오류 발생했습니다.");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101314", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 삭제 실패
        		}
        	} catch(Exception e) {
        		modelAndView.addObject("result", "fail");
        		modelAndView.addObject("message", e.getMessage());
        		commonService.sysLogSave(loginVO.getFsiteid(), "13101314", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 삭제 실패
        	}
    	}
    	
		return modelAndView;
	}	

	/**
	 * 출입자 복원
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
    		modelAndView.addObject("message", "복원할 출입자 정보가 정확하지 않습니다.");
    	} else {
    		try {
            	userInfoVO.setFuid(fuid);
            	userInfoVO.setFpartcd1(fpartcd1);
            	userInfoVO.setFutype(futype);
            	userInfoVO.setFmodid(loginVO.getFsiteid());
            	
        		// 변경 로그
        		Map<String, Object> param = setUserChgLogParam("R", request, loginVO);

            	cnt = userInfoService.userInfoReco(userInfoVO, param);
        		if(cnt > 0) {
        			modelAndView.addObject("result", "success");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101315", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 삭제 성공
        		} else {
        			modelAndView.addObject("result", "fail");
        			modelAndView.addObject("message", "출입자 복원 중 오류 발생했습니다.");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101316", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 삭제 실패
        		}
        	} catch(Exception e) {
        		modelAndView.addObject("result", "fail");
        		modelAndView.addObject("message", e.getMessage());
        		commonService.sysLogSave(loginVO.getFsiteid(), "13101316", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 삭제 실패
        	}
    	}
    	
		return modelAndView;
	}
	
	/**
	 * 출입자 완전삭제 (DB삭제)
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
    		modelAndView.addObject("message", "삭제할 출입자 정보가 정확하지 않습니다.");
    	} else {
    		try {
            	userInfoVO.setFuid(fuid);
            	userInfoVO.setFpartcd1(fpartcd1);
            	userInfoVO.setFmodid(loginVO.getFsiteid());
            	
        		// 변경 로그
        		Map<String, Object> param = setUserChgLogParam("X", request, loginVO);

            	cnt = userInfoService.userInfoDrop(userInfoVO, param);
        		if(cnt > 0) {
        			modelAndView.addObject("result", "success");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101317", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 영구삭제 성공
        		} else {
        			modelAndView.addObject("result", "fail");
        			modelAndView.addObject("message", "출입자 영구삭제 중 오류 발생했습니다.");
        			commonService.sysLogSave(loginVO.getFsiteid(), "13101318", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 영구삭제 실패
        		}
        	} catch(Exception e) {
        		modelAndView.addObject("result", "fail");
        		modelAndView.addObject("message", e.getMessage());
        		commonService.sysLogSave(loginVO.getFsiteid(), "13101318", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //출입자 영구삭제 실패
        	}
    	}
    	
		return modelAndView;
	}	
	
	/**
	 * 선택한 출입자 삭제
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
		//tring chgResn = StringUtil.nvl(request.getParameter("chg_resn"), "출입자 영구삭제(목록)");//2022-04-14 로그 생략
		String cntnIp = commonUtils.getIPFromRequest(request);
		
		if(!fmodid.equals("dev")) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "삭제할 권한이 없습니다.");		
		} else if(ids.length == 0) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "삭제할 출입자 정보가 정확하지 않습니다.");
		} else {
			try {
				userInfoVO.setFpartcd1(fpartcd1);
				userInfoVO.setFmodid(fmodid);
				
				// 변경 로그
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("chg_resn", "");
				param.put("cntn_ip", cntnIp);
				param.put("reg_id", fmodid);
				
				for(String str : ids) {
					userInfoVO.setFuid(str);

					int a_del = userInfoService.userInfoDel(userInfoVO, param);
					LOGGER.debug("[###][userListDel] a_del : {}", a_del);
					cnt++;
					LOGGER.debug("[###][userListDel] 출입자 삭제중... ids/cnt : {}/{}", ids.length, cnt);
				}
				LOGGER.debug("[###][userListDel] 출입자 삭제완료 ids/cnt : {}/{}", ids.length, cnt);

				if(cnt > 0) {
					modelAndView.addObject("result", "success");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101313", String.format("%s 포함 %d 건", userInfoVO.toString(), cnt), cntnIp); //출입자 삭제 성공
				} else {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "출입자 삭제 중 오류 발생했습니다.");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101314", String.format("%s 포함 %d 건", userInfoVO.toString(), cnt), cntnIp); //출입자 삭제 실패
				}
			} catch(Exception e) {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", e.getMessage());
				commonService.sysLogSave(loginVO.getFsiteid(), "13101314", String.format("%s 포함 %d 건", userInfoVO.toString(), cnt), cntnIp); //출입자 삭제 실패
				e.printStackTrace();
			}
		}
		
		return modelAndView;
	}	
	
	/**
	 * 선택한 출입자 완전삭제 (DB삭제)
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
		//tring chgResn = StringUtil.nvl(request.getParameter("chg_resn"), "출입자 영구삭제(목록)");//2022-04-14 로그 생략
		String cntnIp = commonUtils.getIPFromRequest(request);
		
		if(!fmodid.equals("dev")) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "삭제할 권한이 없습니다.");		
		} else if(ids.length == 0) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", "삭제할 출입자 정보가 정확하지 않습니다.");
		} else {
			try {
				userInfoVO.setFpartcd1(fpartcd1);
				userInfoVO.setFmodid(fmodid);
				
				// 변경 로그
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("chg_resn", "");
				param.put("cntn_ip", cntnIp);
				param.put("reg_id", fmodid);
				
				for(String str : ids) {
					userInfoVO.setFuid(str);

					int a_drop = userInfoService.userInfoDrop(userInfoVO, param);
					LOGGER.debug("[###][userListDrop] a_drop : {}", a_drop);
					cnt++;
					LOGGER.debug("[###][userListDrop] 영구 삭제중... ids/cnt : {}/{}", ids.length, cnt);
				}
				LOGGER.debug("[###][userListDrop] 영구 삭제완료 ids/cnt : {}/{}", ids.length, cnt);

				if(cnt > 0) {
					modelAndView.addObject("result", "success");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101317", String.format("%s 포함 %d 건", userInfoVO.toString(), cnt), cntnIp); //출입자 영구삭제 성공
				} else {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "출입자 영구삭제 중 오류 발생했습니다.");
					commonService.sysLogSave(loginVO.getFsiteid(), "13101318", String.format("%s 포함 %d 건", userInfoVO.toString(), cnt), cntnIp); //출입자 영구삭제 실패
				}
			} catch(Exception e) {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", e.getMessage());
				commonService.sysLogSave(loginVO.getFsiteid(), "13101318", String.format("%s 포함 %d 건", userInfoVO.toString(), cnt), cntnIp); //출입자 영구삭제 실패
			}
		}
		
		return modelAndView;
	}	

	

	/**
	 * 사용자정보 엑셀업로드 POC
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
				// 행이 존재하기 않으면 패스
                if(row == null) {
                    continue;
                }

                if(row.getCell(0) != null) {
                	
					String idx = getValue(row.getCell(0)).replaceAll("\n", "<br>");	//순번
					String funm = getValue(row.getCell(1)).replaceAll("\n", "<br>");	//이름
					String fpartnm2 = getValue(row.getCell(2)).replaceAll("\n", "<br>");	//소속
					String fsdt = getValue(row.getCell(3)).replaceAll("\n", "<br>");	//출입시작일
					String fedt = getValue(row.getCell(4)).replaceAll("\n", "<br>");	//출입만료일

	                if(funm.length() > 0) {
				    	HashMap insMap = new HashMap();
				    	insMap.put("siteId", loginVO.getSite_id());
				    	insMap.put("registId", loginVO.getFsiteid());
			    	
				    	insMap.put("funm", funm);
				    	insMap.put("fpartnm1", StringUtil.nvl(gbUserFpartnm1));
				    	insMap.put("fpartnm2", fpartnm2);
				    	insMap.put("fsdt", fsdt);
				    	insMap.put("fedt", fedt);
				    	
				    	LOGGER.debug("[출입자엑셀업로드] [출입자정보] insMap : {}", insMap);

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
				        
				        //list.add(insMap); //list로 등록
	                }
                }
			}
			
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
				modelAndView.addObject("message", "success");	
			} else {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "엑셀파일 업로드 실패");
			}
			
			/* 2022-04-15 list로 등록하는 경우
			if(list != null && list.size() > 0 ) {
				
				int cnt = userInfoService.insertUserListForExcel(list);
		        
				if(cnt == list.size()) {
					modelAndView.addObject("result", "success");
					modelAndView.addObject("message", "success");
					
					//로그
					commonService.sysLogSave(loginVO.getFsiteid(), "13101010", String.valueOf(cnt)+"건 출입자", cntnIp); //엑셀업로드
					
				} else if (cnt < list.size()) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "엑셀파일에 등록된 출입자를 모두 저장하지 못함");
				} else if (cnt == 0 ) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "엑셀파일 업로드 실패");
				}
		        
			} else {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "엑셀파일에 출입자가 없음");
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

	// 파일 다운로드 하는 메소드
    @RequestMapping(value = "/userInfo/excelFormDown.do")
    // get 방식 하이퍼링크 경로로 보낸 PK 값을 인자로 받음
    public ModelAndView fileDown(@RequestParam Map<String, Object> commandMap,
    		HttpServletRequest request) throws Exception {

    	String filePath = request.getSession().getServletContext().getRealPath("/excel/") + "/";

    	String seq = (String)commandMap.get("seq");
		String type = (String)commandMap.get("type");


		LOGGER.debug("seq : " + seq);
		LOGGER.debug("type : " + type);
		LOGGER.debug("filePath : " + filePath);

		File downloadFile = new File(filePath + "excelupload_sample2.xlsx");;
		String fileOrigin = "엑셀업로드_양식샘플.xlsx";

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fileDownloadView");
        modelAndView.addObject("downloadFile", downloadFile);
        modelAndView.addObject("fileOrigin", fileOrigin);

        return modelAndView;
    }
    
    @RequestMapping(value = "/userInfo/excelFormDown2.do")
    public ModelAndView excelFormDown2(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

    	String filePath = request.getSession().getServletContext().getRealPath("/excel/") + "/";
    	LOGGER.debug("엑셀저장양식 filePath : {}", filePath);

		File downloadFile = new File(filePath + "excelupload_form.xlsx");;
		String fileOrigin = "엑셀업로드_양식샘플.xlsx";

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fileDownloadView");
        modelAndView.addObject("downloadFile", downloadFile);
        modelAndView.addObject("fileOrigin", fileOrigin);

        return modelAndView;
    }     

    /**
	 * 출입자 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/excelDownload.do")
	public ModelAndView excelDownload(@RequestParam Map<String, Object> commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModuleUtil.getInstance().setCurrentState("B");	//먼저 시작을 위한 설정

		ModelAndView modelAndView = new ModelAndView();
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fresn = StringUtil.nvl(request.getParameter("fdownresn"));

		//다운로드 사유저장
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
			
			// 이미지 포함 여부
			String strImgYn = StringUtil.nvl(commandMap.get("hidExcelImgYn"), "N");			

			String[] chkText = null;
			List<ExcelVO> resultCellList = null;
			
			try {
				// 엑셀 컬럼 제목
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
				if(strImgYn.equals("Y")) { //출입자정보(이미지포함)
					commonService.sysLogSave(loginVO.getFsiteid(), "13101211", "commandMap:"+commandMap+",colChkQuery:"+userInfoVO.getColChkQuery(), commonUtils.getIPFromRequest(request)); //로그저장
				} else {  //출입자정보
					commonService.sysLogSave(loginVO.getFsiteid(), "13101201", "commandMap:"+commandMap+",colChkQuery:"+userInfoVO.getColChkQuery(), commonUtils.getIPFromRequest(request)); //로그저장	
				}
			}

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", resultCellList);
			modelAndView.addObject("excelName", "출입자리스트");
			modelAndView.addObject("excelHeader", chkText);
			modelAndView.addObject("strImgYn", strImgYn);
            modelAndView.addObject("strImgGb", "F");
		}
		
		return modelAndView;
    }

	/**
	 * 타센터출입자가져오기 팝업화면
	 * @param commandMap 파라메터전달용 commandMap
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
	 * 타센터출입자 검색
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	 * DB이미지가져오기
	 * @param commandMap 파라메터전달용 commandMap
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
	 * DB이미지다운로드
	 * @param commandMap 파라메터전달용 commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/getByteImageDown.do")
    // get 방식 하이퍼링크 경로로 보낸 PK 값을 인자로 받음
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
	 * 사용자정보 이미지업로드
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
			//첨부파일인지 canvas인지 확인
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
					throw new RuntimeException("첨부파일 없음!!");
				}
			} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
				String imgbase64 = (String) request.getParameter("imgUpload");
				String[] base64Arr = imgbase64.split(","); // image/png;base64, 이 부분 버리기 위한 작업
				imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array로 변경
				//LOGGER.debug("========= [addVisit][canvas] imageByte ==================");
				//LOGGER.debug(imageByte);
			} else {
				//throw new RuntimeException("얼굴 이미지 없음!!");
				LOGGER.debug("얼굴 이미지 없음!!");
				modelAndView.addObject("rst", -1);
				return modelAndView;
			}
			LOGGER.debug(">>> imageByte >> "+imageByte);
			LOGGER.debug("fuid : " + fuid);
	
	        userBioInfo.setImgByte(imageByte);
	        imgUserSave = userInfoService.imgUserInfoSave(userBioInfo);
	        
	        if(userBioInfo.getBioCnt().equals("0")) {
	        	if(imgUserSave > 0) {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101303", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장	
	        	} else {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101304", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	}
	        } else {
	        	if(imgUserSave > 0) {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101301", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	} else {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101302", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	}
	        }
		} catch(Exception e) {
			e.printStackTrace();
			commonService.sysLogSave(loginVO.getFsiteid(), "13101302", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}
    	modelAndView.addObject("rst", imgUserSave);
        return modelAndView;
    }

	/**
	 * 출입자 목록 팝업
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

		//검색조건
		LogInfoVO usrInfoVO = new LogInfoVO();
		usrInfoVO.setFuid(fuid);
		usrInfoVO.setFunm(funm);
		usrInfoVO.setFcdno(fcdno);

		//출입사용자 전체 개수
		int totalCnt = userInfoService.getUsrListConnTotalCnt(usrInfoVO);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(usrInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(usrInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		usrInfoVO.setSrchPage(srchPage);
		usrInfoVO.autoOffset();

		//출입사용자목록
		List<LogInfoVO> userInfo = userInfoService.getUsrListConnPop(usrInfoVO);

		model.addAttribute("userInfo", userInfo);						//목록
		model.addAttribute("pagination", pageVO);						//페이징
		model.addAttribute("usrInfoVO", usrInfoVO);						//검색조건

		return "cubox/basicInfo/tuser_list_popup";
	}

	/*이미지 일괄 다운로드 zip 파일 test*/
	@RequestMapping(value = "/userInfo/getZipImageDown.do")
    // get 방식 하이퍼링크 경로로 보낸 PK 값을 인자로 받음
    public ModelAndView getZipImageDown (@RequestParam Map<String, Object> commandMap,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		String fresn = StringUtil.nvl(request.getParameter("fdownresn"));
		String pwfuflg = null;

		//다운로드 사유저장
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
				writer.println("alert('압축파일 비밀번호 재설정 중입니다. 잠시 후 다시 시도하여 주십시오.');");
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
		        
		        //2021-07-12 이미지로그 남기기
		        commonService.sysLogSave(loginVO.getFsiteid(), "13101501", m.toString(), commonUtils.getIPFromRequest(request)); //출입자이미지 저장
			}
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter writer = response.getWriter();
			writer.println("<script type='text/javascript'>");
			writer.println("alert('파일 다운로드 중 오류가 발생되었습니다.');");
			writer.println("history.back();");
			writer.println("</script>");
			writer.flush();
		}
        return modelAndView;
    }

	/**
	 * 체크된사용자일괄수정팝업
	 * @param commandMap 파라메터전달용 commandMap
	 * @return userInfo/chk_user_edit_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/chkUserEditPopup.do")
	public String chkUserEditPopup (@RequestParam Map<String, Object> commandMap, HttpServletRequest request, ModelMap model) throws Exception {

		String fuLst = StringUtil.nullConvert(commandMap.get("fuLst"));

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//////////////////////////////////////////
		//콤보박스 코드 가져오기
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
	 * 체크된사용자정보일괄수정하기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
	    	userInfoVO.setCfstatus(cfstatus);								//카드상태
	    	//userInfoVO.setFusdt2(srchStartDate2 	+ " 00:00:00.000");		//카드 시작일자
	    	//userInfoVO.setFuedt2(srchExpireDate2 	+ " 00:00:00.000");		//카드 종료일자
	    	userInfoVO.setFusdt2(srchStartDate2);		//카드 시작일자
	    	userInfoVO.setFuedt2(srchExpireDate2);		//카드 종료일자
	    	userInfoVO.setFsvtype(fsvtype);

	    try {
	    	String olst = StringUtil.isNullToString(commandMap.get("fuList")).trim();
	    	JSONParser parser = new JSONParser();
	    	Object obj = parser.parse( olst );
	    	JSONArray jsonArray = (JSONArray) obj;
	    	List<Map<String, Object>> lst = CommonUtils.getListMapFromJsonArray (jsonArray);
	    	userInfoVO.setFuids(lst);

	    	if(fsvtype != null && fsvtype.equals("A")) { //전체 사용자 수정
	    		uCnt = userInfoService.allUserInfoLstSave(userInfoVO);
	    	} else {
	    		uCnt = userInfoService.chkUserInfoLstSave(userInfoVO);
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
		if(uCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "13101320", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "13101321", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		}
		modelAndView.addObject("uCnt", 1);
		return modelAndView;
	}


	/**
	 * 출입자 이미지 압축 동기화
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
	 * 다운로드 사유 이력
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

		//사용자
		int totalCnt = userInfoService.getDownloadLogCnt(userInfoVO);

		//LOGGER.debug("srchPage >>> "+srchPage);

		//페이징
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(userInfoVO.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(userInfoVO.getCurPageUnit());
		pageVO.calcPageList();

		//카드별 출입실패이력
		List<DownloadLogVO> downloadLogVO = userInfoService.getDownloadLogList(userInfoVO);

		model.addAttribute("downloadLogVO", downloadLogVO);				//카드별 출입실패이력
		model.addAttribute("pagination", pageVO);						//페이징
		model.addAttribute("userInfoVO", userInfoVO);					//검색조건
		return "cubox/userInfo/downLog_popup";
	}
	
	/**
	 * 선택된 사용자 만료 처리
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
	 * 체크된사용자정보일괄수정하기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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

			// 리턴된 결과 읽기
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
	 * 선택된 이미지 zip 파일 다운로드 
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

		//다운로드 사유저장
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
	 * 사용자추가팝업
	 * @param commandMap 파라메터전달용 commandMap
	 * @return userInfo/user_add_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userAddPopup.do")
	public String userAddPopup(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//////////////////////////////////////////
		//콤보박스 코드 가져오기
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
	 * 사용자추가팝업
	 * @param commandMap 파라메터전달용 commandMap
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

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//콤보박스 코드 가져오기
		model.addAttribute("authList", authList);
		model.addAttribute("authType", authType);
		model.addAttribute("userType", userType);
		model.addAttribute("userStatus", userStatus);
		model.addAttribute("cardStatus", cardStatus);
		model.addAttribute("cardType", cardType);

		return "cubox/userInfo/user_add_popup_new";
	}	

	/**
	 * 사용자 신규등록
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/addUserInfoSave.do")
    public ModelAndView addUserInfoSave(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fregid = loginVO.getFsiteid();

		//첨부파일인지 canvas인지 확인
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
				throw new RuntimeException("첨부파일 없음!!");
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, 이 부분 버리기 위한 작업
			imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array로 변경
		}

		String fuid = StringUtil.nvl(request.getParameter("fuid"));
        String funm = StringUtil.nvl(request.getParameter("funm"));
        String futype = StringUtil.nvl(request.getParameter("futype"));
        String fvisitnum = StringUtil.nvl(request.getParameter("fvisitnum"));  //2021-04-14 방문객카드번호
        String cfstatus = StringUtil.nvl(request.getParameter("cfstatus"));						//카드상태
        String srchStartDate2 = StringUtil.nvl(request.getParameter("srchStartDate2"));			//카드 시작일
        String srchExpireDate2 = StringUtil.nvl(request.getParameter("srchExpireDate2"));		//카드 만료일
        String fcdno = StringUtil.nvl(request.getParameter("fcdno"));							//카드번호
        String fpartnm2 = StringUtil.nvl(request.getParameter("fpartnm2"));						//부서
        
        /* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 */
        //cardno
        String addfcdno = "";//"00"+fuid;
        
        if( !"".equals(fcdno) ) {//카드번호가 있으면(이 값은 무조건 숫자만 넘어옴 체크는 view에서)
        	addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fcdno).replace(" ", fillChar);
        }else if( "".equals(fcdno) ){//카드번호가 빈값이면 고유번호를 사용해 카드번호 생성
        	addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
        }
        /* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 END */
        
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setFuid(fuid);

        if(fuid.length() != Integer.parseInt(fidDigit) || !fuid.matches("(^[0-9]{" + fidDigit + "}$)")) {
        	modelAndView.addObject("cardSaveCnt", -1);	//fuid 입력 오류
            return modelAndView;
        } else {
        	int chk = userInfoService.getChkFuid(userInfoVO);
        	if(chk > 0) {
        		modelAndView.addObject("cardSaveCnt", -2);	//fuid 중복 오류
                return modelAndView;
        	}
        }
        
        //2021-04-14 방문객카드번호
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);	//방문객카드번호 없음
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("workgb", "A");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);	//방문객카드번호 중복
					return modelAndView;
				}
			}
		}        

        userInfoVO.setFunm(funm);
        userInfoVO.setFutype(futype);
        userInfoVO.setFvisitnum(fvisitnum);
        userInfoVO.setFauthtype(GLOBAL_DEFAULT_AUTHTYPE);
        commonService.tcsidxSave("tuserinfo"); //tcsidx테이블 fidx추가
		userInfoVO.setFsidx(commonService.getFsidx("tuserinfo")); //fsidx가져오기
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
		commonService.tcsidxSave("tcard"); //tcsidx테이블 fidx추가
		cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx가져오기
		cardInfoVO.setFsdt(srchStartDate2);
		cardInfoVO.setFedt(srchExpireDate2);
		
		/* 카드번호 중복 체크 */
		Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
		int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
		
		if( cnt > 0 ) {//입력받은 카드 번호가 조회 되면
			modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno 중복 오류
			return modelAndView;
		}
		/* 카드번호 중복 체크 END */
		
		int cardSaveCnt = 0;
		try {
			cardSaveCnt = userInfoService.addUserInfoSave (userInfoVO, cardInfoVO); //출입자 정보 등록 (사용자 정보, 카드)
		} catch (Exception e) {
			cardSaveCnt = -3;
			e.printStackTrace();
		}

		if(cardSaveCnt == 2) {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101101", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //인사정보 저장 완료 로그
			commonService.sysLogSave(loginVO.getFsiteid(), "13101102", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //카드 정보 저장 완료 로그
			if(StringUtil.nvl(userInfoVO.getFutype()).equals("1")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101001", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //카드 정보 저장 완료 로그
			}
			
			//bio정보
			UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
			userBioInfoVO.setFuid(fuid);
			int imgUserSave = 0;
			if(imageByte != null && imageByte.length > 0) {
				userBioInfoVO.setImgByte(imageByte);
				imgUserSave = userInfoService.imgUserInfoSave(userBioInfoVO);
				if(imgUserSave > 0) {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101103", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	} else {
	        		commonService.sysLogSave(loginVO.getFsiteid(), "13101106", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	}
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101107", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}

		} else {
			//인사 정보 및 카드 정보 저장 실패 로그
			commonService.sysLogSave(loginVO.getFsiteid(), "13101104", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			commonService.sysLogSave(loginVO.getFsiteid(), "13101105", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			if(StringUtil.nvl(userInfoVO.getFutype()).equals("1")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //카드 정보 저장 완료 로그
			}
		}
    	modelAndView.addObject("cardSaveCnt", cardSaveCnt);
        return modelAndView;
    }

	/**
	 * 출입자 신규등록 (개인정보,카드정보,권한)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/addUserInfoNew.do")
	public ModelAndView addUserInfoNew(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		//첨부파일인지 canvas인지 확인
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
				throw new RuntimeException("첨부파일 없음!!");
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, 이 부분 버리기 위한 작업
			imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array로 변경
		}
		
		// fuid 채번
		UserInfoVO userInfoVO = new UserInfoVO();
		
		int a = -1;
		do {
			a = userInfoService.getNewFuid(userInfoVO);
		} while (a > 0);
		String fuid = userInfoVO.getFuid();
		
		// 출입자 정보
		//String fuid = StringUtil.isNullToString(request.getParameter("fuid"));  //fuid 입력받는 경우
		String funm = StringUtil.isNullToString(request.getParameter("funm"));
		String futype = StringUtil.isNullToString(request.getParameter("futype"));
		String fvisitnum = StringUtil.isNullToString(request.getParameter("fvisitnum")); //2021-04-13 방문객카드번호
		String fpartnm1 = StringUtil.isNullToString(request.getParameter("fpartnm1"));
		String fpartnm2 = StringUtil.isNullToString(request.getParameter("fpartnm2"));
		String fpartnm3 = StringUtil.isNullToString(request.getParameter("fpartnm3"));
		String fcarno = StringUtil.isNullToString(request.getParameter("fcarno"));
		String ftel = StringUtil.isNullToString(request.getParameter("ftel"));
		String hpNo = StringUtil.isNullToString(request.getParameter("hp_no"));
		String fetc1 = StringUtil.isNullToString(request.getParameter("fetc1"));
		String fetc2 = StringUtil.isNullToString(request.getParameter("fetc2"));
		// 카드 정보
		String fcdno = StringUtil.isNullToString(request.getParameter("fcdno"));
		String fstatus = StringUtil.nvl(request.getParameter("fstatus"), "Y");
		String fsdt = StringUtil.isNullToString(request.getParameter("fsdt"));
		String fedt = StringUtil.isNullToString(request.getParameter("fedt"));
		// 권한
		String auths = StringUtil.nvl(request.getParameter("arraryGroup"));
		String[] arrAuth = null;
		if(!auths.equals("")) {
			arrAuth = StringUtil.split(auths, ",");	
		}		
		
		/* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 */
		//cdno
		String addfcdno = "";//"00"+fuid;
		
		if( !"".equals(fcdno) ) {//카드번호가 있으면(이 값은 무조건 숫자만 넘어옴 체크는 view에서)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fcdno).replace(" ", fillChar);
		}else if( "".equals(fcdno) ){//카드번호가 빈값이면 고유번호를 사용해 카드번호 생성
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}
		/* 카드번호가 없을시 고유번호를 사용해 카드번호 자동생성 END */
		
		//UserInfoVO userInfoVO = new UserInfoVO(); //fuid 입력받는 경우
		//userInfoVO.setFuid(fuid);  //fuid 입력받는 경우

		if(fuid.length() != Integer.parseInt(fidDigit) || !fuid.matches("(^[0-9]{" + fidDigit + "}$)")) {
			modelAndView.addObject("cardSaveCnt", -1);	//fuid 입력 오류
			return modelAndView;
		} else {
			int chk = userInfoService.getChkFuid(userInfoVO);
			if(chk > 0) {
				modelAndView.addObject("cardSaveCnt", -2);	//fuid 중복 오류
				return modelAndView;
			}
		}

		// 2021-04-13 방문객카드번호 중복 체크
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("cardSaveCnt", -11);	//방문객카드번호 없음
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("workgb", "A");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("cardSaveCnt", -12);	//방문객카드번호 중복
					return modelAndView;
				}
			}
		}
		
		userInfoVO.setFunm(funm);
		userInfoVO.setFutype(futype);
		userInfoVO.setFvisitnum(fvisitnum);
		userInfoVO.setFauthtype(GLOBAL_DEFAULT_AUTHTYPE);
		commonService.tcsidxSave("tuserinfo"); //tcsidx테이블 fidx추가
		userInfoVO.setFsidx(commonService.getFsidx("tuserinfo")); //fsidx가져오기
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
		commonService.tcsidxSave("tcard"); //tcsidx테이블 fidx추가
		cardInfoVO.setFsidx(commonService.getFsidx("tcard")); //fsidx가져오기
		cardInfoVO.setFstatus(fstatus);
		cardInfoVO.setFsdt(fsdt);
		cardInfoVO.setFedt(fedt);
		
		AuthorGroupVO authVO = new AuthorGroupVO ();
		authVO.setFuid(fuid);
		authVO.setSiteId(loginVO.getSite_id());
		authVO.setGroupArray(arrAuth);
		authVO.setRegistId(loginVO.getFsiteid());
		authVO.setModifyId(loginVO.getFsiteid());
		
		/* 카드번호 중복 체크 */
		Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
		int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
		
		if( cnt > 0 ) {//입력받은 카드 번호가 조회 되면
			modelAndView.addObject("cardSaveCnt", -4).addObject("result", result);//fcdno 중복 오류
			return modelAndView;
		}
		/* 카드번호 중복 체크 END */
		
		int cardSaveCnt = 0;
		try {
			cardSaveCnt = userInfoService.addUserInfoNew (userInfoVO, cardInfoVO, authVO); //출입자 신규등록 (개인정보,카드정보,권한)
			LOGGER.error("###[addUserInfoNew] 신규등록 (1)cardSaveCnt : {}", cardSaveCnt);
		} catch (Exception e) {
			LOGGER.error("###[addUserInfoNew] 신규등록 에러발생 : {}", e.getMessage());
			cardSaveCnt = -3;
			e.printStackTrace();
		}

		if(cardSaveCnt > 0) {
			commonService.sysLogSave(loginVO.getFsiteid(), "13101101", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //개인정보등록성공
			commonService.sysLogSave(loginVO.getFsiteid(), "13101102", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //카드등록성공
			if(arrAuth != null) commonService.sysLogSave(loginVO.getFsiteid(), "12101001", authVO.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 추가 성공
			
			//bio정보
			UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
			userBioInfoVO.setFuid(fuid);
			int imgUserSave = 0;
			if(imageByte != null && imageByte.length > 0) {
				userBioInfoVO.setImgByte(imageByte);
				imgUserSave = userInfoService.imgUserInfoSave(userBioInfoVO);
				LOGGER.error("###[addUserInfoNew] 신규등록 (2)imgUserSave : {}", imgUserSave);
				if(imgUserSave > 0) {
					commonService.sysLogSave(loginVO.getFsiteid(), "13101103", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //바이오등록성공
				} else {
					commonService.sysLogSave(loginVO.getFsiteid(), "13101106", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //바이오등록실패
				}
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101107", userBioInfoVO.toString(), commonUtils.getIPFromRequest(request)); //바이오데이터없음
			}

		} else {
			//인사 정보 및 카드 정보 저장 실패 로그
			commonService.sysLogSave(loginVO.getFsiteid(), "13101104", userInfoVO.toString(), commonUtils.getIPFromRequest(request)); //개인정보등록실패
			commonService.sysLogSave(loginVO.getFsiteid(), "13101105", cardInfoVO.toString(), commonUtils.getIPFromRequest(request)); //카드등록실패
			if(arrAuth != null) commonService.sysLogSave(loginVO.getFsiteid(), "12101002", authVO.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 추가 실패
		}
		modelAndView.addObject("cardSaveCnt", cardSaveCnt);
		return modelAndView;
	}
	
	/**
	 * 출입자 수정 (개인정보,이미지)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfo/modUserInfoNew.do")
	public ModelAndView modUserInfoNew(MultipartHttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		// 첨부파일인지 canvas인지 확인
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
				throw new RuntimeException("첨부파일 없음!!");
			}
		} else if (StringUtil.nvl(request.getParameter("imageType")).equals("canvas")) {
			String imgbase64 = (String) request.getParameter("imgUpload");
			String[] base64Arr = imgbase64.split(","); // image/png;base64, 이 부분 버리기 위한 작업
			imageByte = Base64.decodeBase64(base64Arr[1]); // base64 to byte array로 변경
		}*/		
		
		// 출입자 정보
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
		
		// 2021-04-13 방문객카드번호 중복 체크
		if(futype.equals("3")) {
			if(fvisitnum.equals("")) {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "방문객카드번호를 입력하여 주십시오.");	//방문객카드번호 없음
				return modelAndView;
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("fvisitnum", fvisitnum);
				param.put("fuid", fuid);
				param.put("workgb", "M");
				
				int cnt = userInfoService.getChkFvisitnum(param);
				
				if(cnt > 0) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "방문객카드번호가 중복되었습니다. 다시 입력하여 주십시오.");	//방문객카드번호 중복
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
		
		// bio정보
		UserBioInfoVO userBioInfoVO = new UserBioInfoVO();
		if(imageByte != null && imageByte.length > 0) {
			userInfoVO.setFbioyn("Y");
			userBioInfoVO.setFuid(fuid);
			userBioInfoVO.setImgByte(imageByte);
		} else {
			userInfoVO.setFbioyn("N");
		}
		
		// 변경 로그
		Map<String, Object> param = setUserChgLogParam("U", request, loginVO);

		try {
			int cnt = userInfoService.modUserInfoNew(userInfoVO, userBioInfoVO, param);
			commonService.sysLogSave(loginVO.getFsiteid(), "13101305", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //개인정보수정성공
			if(userInfoVO.getFbioyn().equals("Y")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101301", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //사진수정성공	
			}
			modelAndView.addObject("result", "success");
			LOGGER.debug("###[modUserInfoNew] 출입자 개인정보 수정 성공 cnt:{}, userInfoVO:{}, userBioInfoVO:{}", cnt, userInfoVO, userBioInfoVO);
		} catch(Exception e) {
			e.printStackTrace();
			commonService.sysLogSave(loginVO.getFsiteid(), "13101306", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //개인정보수정실패
			if(userInfoVO.getFbioyn().equals("Y")) {
				commonService.sysLogSave(loginVO.getFsiteid(), "13101302", userInfoVO.toString()+","+userBioInfoVO.toString(), cntnIp); //사진수정실패	
			}
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
			LOGGER.error("###[modUserInfoNew] 출입자 개인정보 수정 실패 userInfoVO:{}, userBioInfoVO:{}", userInfoVO, userBioInfoVO);			
		}
		
		return modelAndView;
	}	
	
	/**
	 * 출입자 카드 및 권한그룹 저장
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
		if( !"".equals(newFcdno) ) {//카드번호가 있으면(이 값은 무조건 숫자만 넘어옴 체크는 view에서)
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), newFcdno).replace(" ", fillChar);
		}else if( "".equals(newFcdno) ){//카드번호가 빈값이면 고유번호를 사용해 카드번호 생성
			addfcdno = String.format("%" + fillDirection + cardDigit + "s".trim(), fuid).replace(" ", fillChar);
		}

		/* 카드번호 중복 체크 */
		if("Y".equals(isFcdnoChange)) {
			CardInfoVO cardInfoVO = new CardInfoVO();
			cardInfoVO.setFcdno(addfcdno);
			
			Map<String, Object> result = userInfoService.getChkFcdno(cardInfoVO);
			int cnt = Integer.parseInt(String.valueOf(result.get("cnt")));
			
			if( cnt > 0 ) {//입력받은 카드 번호가 조회 되면
				modelAndView.addObject("result", "fail"); //fcdno 중복 오류
				modelAndView.addObject("message", "카드번호가 중복되었습니다. 다시 입력하여 주십시오.\\n현재 " + StringUtil.nvl(result.get("funm")) + "이(가) 사용중입니다.");
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
		
		// 변경 로그
		Map<String, Object> paramLog = new HashMap<String, Object>();
		
		int cntChgAuth = -1;
		String cfstatus = "";
		
		try {
			//카드 상태
			cfstatus = StringUtil.nvl(cardInfoVO.getFstatus());
			//권한 변경 여부 확인
			if(cfstatus.equals("Y") || cfstatus.equals("W")) {
				cntChgAuth = userInfoService.selectUserAuthGroupForChange(authorGroupVO);				
			}
			LOGGER.debug("##### [saveUserCardAuthInfo] 출입자(카드)권한수정체크  cntChgAuth:{}, cfstatus:{}", cntChgAuth, cfstatus);			
			
			int cnt = 0;
			//cnt = authorGroupService.saveUserAuthGroup(vo);

			if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
				paramLog = setUserChgLogParam("A", request, loginVO);  //카드+권한 수정
			} else {
				paramLog = setUserChgLogParam("C", request, loginVO);  //카드 수정
			}
			
			cnt = userInfoService.saveUserCardAuthInfo(cardInfoVO, authorGroupVO, paramLog);
			LOGGER.debug("##### [saveUserCardAuthInfo] 카드 수정  cnt:{}", cnt);
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
				commonService.sysLogSave(loginVO.getFsiteid(), "13101405", cardInfoVO.toString(), cntnIp); //카드정보수정성공
				if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
					commonService.sysLogSave(loginVO.getFsiteid(), "12111001", authorGroupVO.toString(), cntnIp); //사용자 권한 수정 성공	
				}
			} else {
				modelAndView.addObject("result", "fail");
				commonService.sysLogSave(loginVO.getFsiteid(), "13101406", cardInfoVO.toString(), cntnIp); //카드정보수정실패
				if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
					commonService.sysLogSave(loginVO.getFsiteid(), "12101002", authorGroupVO.toString(), cntnIp); //사용자 권한 수정 실패
				}
			}
			
		} catch(Exception e) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "13101406", cardInfoVO.toString(), cntnIp); //카드정보수정실패
			if(cntChgAuth > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", authorGroupVO.toString(), cntnIp); //사용자 권한 수정 실패
			}
			e.printStackTrace();
		}		

		modelAndView.addObject("fuid", fuid);
		modelAndView.addObject("siteId", fpartcd1);
		return modelAndView;
	}
	
	/**
	 * 체크된사용자일괄다운로드 팝업
	 * @param commandMap 파라메터전달용 commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/selZipDownloadPop.do")
	public String selZipDownloadPop (@RequestParam Map<String, Object> commandMap, HttpServletRequest request, ModelMap model) throws Exception {

		String fuLst = StringUtil.nullConvert(commandMap.get("fuLst"));

		List<CodeVO> authType = commonService.getCodeList("combo","COMBO_FAuthType");		//권한타입 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//사용자타입 코드가져오기
		List<CodeVO> userStatus = commonService.getCodeList("combo","COMBO_USER_STATUS"); 	//사용자상태 코드가져오기
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태 코드가져오기
		List<CodeVO> cardType = commonService.getCodeList("combo","COMBO_FcardType"); 		//카드타입 코드가져오기

		//////////////////////////////////////////
		//콤보박스 코드 가져오기
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
	 * 엑셀 프로세스 스타트
	 * @param commandMap 파라메터전달용 commandMap
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
	 * 사용자권한그룹팝업
	 * @param commandMap 파라메터전달용 commandMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value="/userInfo/userAuthorGroupPopup.do")
	public String userAuthorGroupPopup(ModelMap model, @RequestParam("fuid") String fuid, @RequestParam("fpartcd1") String fpartcd1,
			@RequestParam(value="sfcdno", required=false) String sfcdno, HttpServletRequest request) throws Exception {

		UserInfoVO userInfo = new UserInfoVO();
		userInfo.setFuid(fuid);
		userInfo.setFpartcd1(fpartcd1);

		//권한 그룹 조회
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
	 * 사용자 권한 그룹 저장
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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
				commonService.sysLogSave(loginVO.getFsiteid(), "12111001", vo.toString(), commonUtils.getIPFromRequest(request)); //사용자 권한 수정 성공
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", vo.toString(), commonUtils.getIPFromRequest(request)); //사용자 권한 수정 실패
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
	 * 사용자별 권한그룹 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
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

		List<AuthorGroupVO> totalGroupList = null; //권한리스트
		List<AuthorGroupVO>  userGroupList = null; //사용자 권한 리스트

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
	 * 권한그룹별 출입자
	 * @param commandMap 파라메터전달용 commandMap
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
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//센터 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//출입자타입 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태		

		model.addAttribute("centerCombo" , centerCombo); //공통코드
		model.addAttribute("userType"    , userType);
		model.addAttribute("cntPerPage"  , cntPerPage);
		model.addAttribute("cardStatus"  , cardStatus);
		
		model.addAttribute("param"       , param);        //검색조건
		model.addAttribute("pagination"  , pageVO);       //paging
		model.addAttribute("userInfoList", userInfoList); //검색목록

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

			commonService.sysLogSave(loginVO.getFsiteid(), "13101209", param.toString(), commonUtils.getIPFromRequest(request)); //로그저장
		
			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", list);
			modelAndView.addObject("excelName", "권한그룹별출입자목록");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}	
	
	/**
	 * 단말기별 출입자
	 * @param commandMap 파라메터전달용 commandMap
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
		
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());	//센터 코드가져오기
		List<CodeVO> userType = commonService.getCodeList("combo","COMBO_Futype"); 			//출입자타입 코드가져오기
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수
		List<CodeVO> cardStatus = commonService.getCodeList("combo","COMBO_Fcardstatus"); 	//카드상태		

		model.addAttribute("centerCombo" , centerCombo); //공통코드
		model.addAttribute("userType"    , userType);
		model.addAttribute("cntPerPage"  , cntPerPage);
		model.addAttribute("cardStatus"  , cardStatus);
		
		model.addAttribute("param"       , param);        //검색조건
		model.addAttribute("pagination"  , pageVO);       //paging
		model.addAttribute("userInfoList", userInfoList); //검색목록

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

			commonService.sysLogSave(loginVO.getFsiteid(), "13101210", param.toString(), commonUtils.getIPFromRequest(request)); //로그저장

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", list);
			modelAndView.addObject("excelName", "단말기별출입자목록");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}
	
	/**
	 * 출입자 변경 이력 Param 
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
			if(sChgClCd.equals("U")) chgResn = "개인정보수정";
			else if(sChgClCd.equals("C")) chgResn = "카드정보수정";
			else if(sChgClCd.equals("A")) chgResn = "카드·권한그룹 수정";
			else if(sChgClCd.equals("D")) chgResn = "출입자삭제";
			else if(sChgClCd.equals("R")) chgResn = "출입자복원";
			else if(sChgClCd.equals("X")) chgResn = "출입자영구삭제";
			else chgResn = "출입자정보수정";
		}
		
		if(gb.equals("AS")) {
			chgResn += "(권한동기화)";

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