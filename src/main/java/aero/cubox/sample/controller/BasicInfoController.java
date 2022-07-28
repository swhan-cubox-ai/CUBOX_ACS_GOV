package aero.cubox.sample.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import org.springmodules.validation.commons.DefaultBeanValidator;

import aero.cubox.util.AES256Util;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.FileUtil;
import aero.cubox.util.StringUtil;
import aero.cubox.file.service.FileService;
import aero.cubox.file.service.vo.FileVO;
import aero.cubox.sample.service.BasicInfoService;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.auth.service.vo.AuthorVO;
import aero.cubox.sample.service.vo.CenterInfoVO;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.DateTimeVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.PaginationVO;
import aero.cubox.sample.service.vo.SiteUserVO;
import aero.cubox.sample.service.vo.SiteVO;
import aero.cubox.sample.service.vo.SysLogVO;
import aero.cubox.user.service.vo.UserChgLogVO;
import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class BasicInfoController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicInfoController.class);
	
	private int recPerPage  = 10; //조회할 페이지 수
	private int curPageUnit = 10; //한번에 표시할 페이지 번호 개수

	/** basicInfoService */
	@Resource(name = "basicInfoService")
	private BasicInfoService basicInfoService;

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;
	
	/** FileService */
	@Resource(name = "FileService") private FileService fileService;
	
	/** 파일 업로드 포맷 목록 globals.properties 지정 */
	@Value("#{property['Globals.formatList'].split(',')}") private List<String> formatList;
	
	/** 게시판 파일 저장 경로 globals.properties 지정 */
	@Value("#{property['Globals.fileStorage']}") private String RESOURCE_PATH;

	/**
	 * 센터관리화면
	 * @param commandMap 파라메터전달용 commandMap
	 * @return basicInfo/center_management
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/centerMngmt.do")
	public String centerMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {

			CenterInfoVO centerInfoVO = new CenterInfoVO();
			centerInfoVO.setFkind1("code");
			centerInfoVO.setFkind2("centercd");
			List<CenterInfoVO> centerInfoList = basicInfoService.getCenterInfoList(centerInfoVO);

			model.addAttribute("centerInfoList", centerInfoList);
			model.addAttribute("menuPath", "basicInfo/center_management");

			return "cubox/cuboxSubContents";
		}else{
			return "redirect:/login.do";
		}

	}

	/**
	 * 센터추가
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/centerAddSave.do")
	public ModelAndView centerAddSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fvalue = (String) commandMap.get("fvalue");

		int addCnt = basicInfoService.centerAddSave(fvalue);

		String fdetail = "센터명 : " + fvalue;
		if(addCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "11101001", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "11101002", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("addCnt", addCnt);

		return modelAndView;
	}

	/**
	 * 센터 사용유무 편집
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/fuseynChangeSave.do")
	public ModelAndView fuseynChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fkind3 = (String) commandMap.get("fkind3");
		String fuseyn = (String) commandMap.get("fuseyn");

		CenterInfoVO centerInfoVO = new CenterInfoVO();
		centerInfoVO.setFkind3(fkind3);
		centerInfoVO.setFuseyn(fuseyn);

		int editCnt = basicInfoService.fuseynChangeSave(centerInfoVO);

		String fvalue = commonService.getCodeValue("code", "centercd", fkind3);
		String fuseynm = fuseyn.equals("Y") ? "사용중" : "사용안함" ;
		String fdetail = "센터명 : " + fvalue + ", " + fuseynm;
		if(editCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "11101101", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "11101102", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("editCnt", editCnt);

		return modelAndView;
	}

	/**
	 * 계정관리화면
	 * @param commandMap 파라메터전달용 commandMap
	 * @return basicInfo/account_management
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/accountMngmt.do")
	public String accountMngmt(ModelMap model, @RequestParam Map<String, Object> param,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {

			SiteUserVO siteUserVO = new SiteUserVO();

			int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
			String srchRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(siteUserVO.getSrchCnt()));

			// paging
			siteUserVO.setSrchPage(srchPage);
			siteUserVO.setSrchCnt(Integer.parseInt(srchRecPerPage));
			siteUserVO.autoOffset();
			
			// search
			siteUserVO.setSite_id(StringUtil.nvl(param.get("srchCond")));
			siteUserVO.setFsiteid(StringUtil.nvl(param.get("srchFsiteid")));
			siteUserVO.setFname(StringUtil.nvl(param.get("srchFname")));
			siteUserVO.setAuthor_id(StringUtil.nvl(param.get("srchAuthorId")));

			List<SiteUserVO> siteUserList = basicInfoService.getSiteUserList(siteUserVO);
			int siteUserCnt = basicInfoService.getSiteUserCnt(siteUserVO);

			//userInfoVO.setSrchCnt(srchRecPerPage==null||srchRecPerPage.equals("")?userInfoVO.getSrchCnt():srchRecPerPage.equals("0")?totalCnt:Integer.parseInt(srchRecPerPage));
			PaginationVO pageVO = new PaginationVO();
			pageVO.setCurPage(srchPage);
			pageVO.setRecPerPage(siteUserVO.getSrchCnt());
			pageVO.setTotRecord(siteUserCnt);
			pageVO.setUnitPage(siteUserVO.getCurPageUnit());
			pageVO.calcPageList();

			List<CodeVO> authorList = commonService.getAuthorList();
			List<SiteVO> siteList = commonService.getSiteCodeList();
			List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE");  //page당 record 수

			
			model.addAttribute("authorList", authorList);
			model.addAttribute("centerList", siteList);
			model.addAttribute("cntPerPage", cntPerPage);
			
			model.addAttribute("siteUserList", siteUserList); //검색목록
			model.addAttribute("siteUserVO", siteUserVO);
			model.addAttribute("pagination", pageVO);			

			return "cubox/basicInfo/account_management";
		}else{
			return "redirect:/login.do";
		}
	}
	
	/**
	 * 입력한 id 중복여부를 체크하여 사용가능여부를 확인
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return basicInfo/idDplctCnfirm
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/idDplctCnfirm.do")
	public ModelAndView checkIdDplct(@RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String checkId = (String) commandMap.get("checkId");
		//checkId = new String(checkId.getBytes("ISO-8859-1"), "UTF-8");

		int usedCnt = basicInfoService.checkIdDplct(checkId);
		modelAndView.addObject("usedCnt", usedCnt);
		modelAndView.addObject("checkId", checkId);

		return modelAndView;
	}

	/**
	 * 입력한 fgid 중복여부를 체크하여 사용가능여부를 확인
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return basicInfo/idDplctCnfirm
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/fgidDplctCnfirm.do")
	public ModelAndView checkfgidDplct(@RequestParam Map<String, Object> commandMap) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String checkId = (String) commandMap.get("checkId");
		//checkId = new String(checkId.getBytes("ISO-8859-1"), "UTF-8");

		int usedCnt = basicInfoService.checkfgidDplct(checkId);
		modelAndView.addObject("usedCnt", usedCnt);
		modelAndView.addObject("checkId", checkId);

		return modelAndView;
	}

	/**
	 * 계정추가
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/siteUserAddSave.do")
	public ModelAndView siteUserAddSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fsiteid = (String) commandMap.get("fsiteid");
		String fname = (String) commandMap.get("fname");
		String fuid = (String) commandMap.get("fuid");
		String siteId = (String) commandMap.get("siteId");
		String authorId = (String) commandMap.get("authorId");
		String fphone01 = (String) commandMap.get("fphone01");
		String fphone02 = (String) commandMap.get("fphone02");
		String fphone03 = (String) commandMap.get("fphone03");

		String fphone = "";
		if(fphone01!=null && fphone02!=null && fphone03!=null && !fphone01.equals("") && !fphone02.equals("") && !fphone03.equals("")) {
			fphone = fphone01 + "-" + fphone02 + "-" + fphone03;
		}

		SiteUserVO siteUserVO = new SiteUserVO();
		siteUserVO.setFuid(fuid);

		int addCnt = 0;
		int uidChkCnt = 0;
		if(!StringUtil.isEmpty(fuid)) {
			uidChkCnt = basicInfoService.getSelectFuidChkCnt(siteUserVO);
		}

		if(uidChkCnt > 0) {
			addCnt = -2;
		} else {
			siteUserVO.setFsiteid(fsiteid);
			siteUserVO.setFname(fname);
			siteUserVO.setFphone(fphone);
			siteUserVO.setSite_id(siteId);
			siteUserVO.setAuthor_id(authorId);

			addCnt = basicInfoService.siteUserAddSave(siteUserVO);

			if(addCnt > 0){
				commonService.sysLogSave(loginVO.getFsiteid(), "11111001", siteUserVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}else{
				commonService.sysLogSave(loginVO.getFsiteid(), "11111002", siteUserVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}
		}
		modelAndView.addObject("addCnt", addCnt);

		return modelAndView;
	}

	/**
	 * 계정편집
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/siteUserInfoChangeSave.do")
	public ModelAndView siteUserInfoChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fsiteid = (String) commandMap.get("fsiteid");
		String fname = (String) commandMap.get("fname");
		String fuid = (String) commandMap.get("fuid");
		String siteId = (String) commandMap.get("siteId");
		String authorId = (String) commandMap.get("authorId");
		String fphone01 = (String) commandMap.get("fphone01");
		String fphone02 = (String) commandMap.get("fphone02");
		String fphone03 = (String) commandMap.get("fphone03");

		String fphone = "";
		if(fphone01!=null && fphone02!=null && fphone03!=null && !fphone01.equals("") && !fphone02.equals("") && !fphone03.equals("")) {
			fphone = fphone01 + "-" + fphone02 + "-" + fphone03;
		}

		SiteUserVO siteUserVO = new SiteUserVO();
		siteUserVO.setFuid(fuid);
		siteUserVO.setFsiteid(fsiteid);

		int addCnt = 0;
		int uidChkCnt = 0;
		if(!StringUtil.isEmpty(fuid)) {
			uidChkCnt = basicInfoService.getSelectFuidChkCnt(siteUserVO);
		}

		if(uidChkCnt > 0) {
			addCnt = -2;
		} else {
			siteUserVO.setFname(fname);
			siteUserVO.setFphone(fphone);
			siteUserVO.setAuthor_id(authorId);
			siteUserVO.setSite_id(siteId);

			addCnt = basicInfoService.siteUserInfoChangeSave(siteUserVO);

			if(addCnt > 0){
				commonService.sysLogSave(loginVO.getFsiteid(), "11111101", siteUserVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}else{
				commonService.sysLogSave(loginVO.getFsiteid(), "11111102", siteUserVO.toString(), commonUtils.getIPFromRequest(request)); //로그저장
			}
		}
		modelAndView.addObject("addCnt", addCnt);

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
	@RequestMapping(value = "/basicInfo/siteUserFuseynChangeSave.do")
	public ModelAndView siteUserFuseynChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fsiteid = (String) commandMap.get("fsiteid");
		String fuseyn = (String) commandMap.get("fuseyn");

		SiteUserVO siteUserVO = new SiteUserVO();
		siteUserVO.setFsiteid(fsiteid);
		siteUserVO.setFuseyn(fuseyn);

		int addCnt = basicInfoService.siteUserFuseynChangeSave(siteUserVO);

		String fuseynm = fuseyn.equals("Y") ? "사용중" : "사용안함" ;
		String fdetail = "계정 : " + fsiteid + ", " + fuseynm;
		if(addCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "11111201", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "11111202", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("addCnt", addCnt);

		return modelAndView;
	}

	/**
	 * 계정비밀번호초기화
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/siteUserPasswdReset.do")
	public ModelAndView siteUserPasswdReset(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String fsiteid = (String) commandMap.get("fsiteid");

		SiteUserVO siteUserVO = new SiteUserVO();
		siteUserVO.setFsiteid(fsiteid);

		int addCnt = basicInfoService.siteUserPasswdReset(siteUserVO);

		String fdetail = "계정 : " + fsiteid;
		if(addCnt > 0){
			commonService.sysLogSave(loginVO.getFsiteid(), "11111301", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}else{
			commonService.sysLogSave(loginVO.getFsiteid(), "11111302", fdetail, commonUtils.getIPFromRequest(request)); //로그저장
		}

		modelAndView.addObject("addCnt", addCnt);

		return modelAndView;
	}

	/**
	 * 비밀번호변경화면
	 * @param commandMap 파라메터전달용 commandMap
	 * @return basicInfo/pw_change
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/pwChange.do")
	public String pwChangeView(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			return "cubox/basicInfo/pw_change";
		}else{
			return "redirect:/login.do";
		}
	}

	/**
	 * 비밀번호변경
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/passwdChangeSave.do")
	public ModelAndView passwdChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String currentpasswd = (String) commandMap.get("currentpasswd");
		String fpasswd = (String) commandMap.get("fpasswd");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {

			SiteUserVO siteUserVO = new SiteUserVO();
			siteUserVO.setFsiteid(loginVO.getFsiteid());
			siteUserVO.setFpasswd(currentpasswd);

			int checkPwd = basicInfoService.checkPwd(siteUserVO);

			if(checkPwd > 0){
				siteUserVO.setFpasswd(fpasswd);
				int passwdCnt = basicInfoService.passwdChangeSave(siteUserVO);
				if(passwdCnt > 0){
					commonService.sysLogSave(loginVO.getFsiteid(), "11120001", "", commonUtils.getIPFromRequest(request)); //로그저장
				}else{
					commonService.sysLogSave(loginVO.getFsiteid(), "11120002", "", commonUtils.getIPFromRequest(request)); //로그저장
				}
				modelAndView.addObject("checkPwdError", "N");

			}else{
				modelAndView.addObject("checkPwdError", "Y");
			}
		}

		return modelAndView;

	}

	/**
	 * 공통코드관리화면
	 * @param commandMap 파라메터전달용 commandMap
	 * @return basicInfo/center_management
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/commcodeMngmt.do")
	public String commcodeMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {

			String searchFkind1 = StringUtil.isNullToString(request.getParameter("searchFkind1"));
			String searchFkind2 = StringUtil.isNullToString(request.getParameter("searchFkind2"));
			String searchCode = StringUtil.isNullToString(request.getParameter("searchCode"));
			String searchCodeName = StringUtil.isNullToString(request.getParameter("searchCodeName"));
			String srchUseYn = StringUtil.isNullToString(request.getParameter("srchUseYn"));
			

			List<CodeVO> codeFkind1List = commonService.getCodeFkind1List();
			model.addAttribute("codeFkind1List", codeFkind1List);
			
			//List<CodeVO> codeFkind2List = commonService.getCodeFkind2List();			
			//model.addAttribute("codeFkind2List", codeFkind2List);

			CodeVO codevo = new CodeVO();
			codevo.setFkind1(searchFkind1);
			codevo.setFkind2(searchFkind2);
			codevo.setFkind3(searchCode);
			codevo.setFvalue(searchCodeName.trim());
			codevo.setFuseyn(srchUseYn);
			
			List<CodeVO> codeFullList = commonService.getCodeFullList(codevo);
			model.addAttribute("codeFullList", codeFullList);
			
			List<CodeVO> codeFkind2List = commonService.getCodeFkind2List2(codevo);
			model.addAttribute("codeFkind2List", codeFkind2List);

			model.addAttribute("searchFkind1", searchFkind1);
			model.addAttribute("searchFkind2", searchFkind2);
			model.addAttribute("searchCode", searchCode);
			model.addAttribute("searchCodeName", searchCodeName);
			model.addAttribute("srchUseYn", srchUseYn);
			
			return "cubox/basicInfo/code_management";
		}else{
			return "redirect:/login.do";
		}

	}
	
	@ResponseBody
	@RequestMapping(value = "/basicInfo/getCodeFkind2List2.do")
	public ModelAndView getAuthGroup(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		CodeVO codeVO = new CodeVO();
		codeVO.setFkind1(StringUtil.isNullToString(param.get("fkind1")));

		List<CodeVO> codeList = commonService.getCodeFkind2List2(codeVO);

		modelAndView.addObject("codeFkind2List", codeList);

		return modelAndView;
	}	

	/**
	 * 공통코드 추가
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/commcodeAddSave.do")
	public ModelAndView commcodeAddSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		String strfk1 = StringUtil.nvl(commandMap.get("fkind1"));
		String strfk2 = StringUtil.nvl(commandMap.get("fkind2"));
		String strfk2_txt = StringUtil.nvl(commandMap.get("fkind2_txt"));
		String strfk3 = StringUtil.nvl(commandMap.get("fkind3"));
		String fvalue = StringUtil.nvl(commandMap.get("fvalue"));
		String forder = StringUtil.nvl(commandMap.get("forder"));
		
		if(strfk2.equals("txt")) strfk2 = strfk2_txt;

		CodeVO vo = new CodeVO();
		vo.setFkind1(strfk1);
		vo.setFkind2(strfk2);
		vo.setFkind3(strfk3);
		vo.setFuseyn("Y");
		vo.setFvalue(fvalue);
		vo.setForder(forder);

		//fkind3의 중복체크
		int fkind3cnt = commonService.getFkind3Cnt (vo);
		if(fkind3cnt > 0) {
			modelAndView.addObject("codeSaveCnt", -1);
		} else {
			rtn = commonService.insertCode (vo);
			modelAndView.addObject("codeSaveCnt", rtn);
		}

		return modelAndView;
	}

	/**
	 * 공통코드 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/commcodeEditSave.do")
	public ModelAndView commcodeEditSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		String strfk1 = (String) commandMap.get("fkind1");
		String strfk2 = (String) commandMap.get("fkind2");
		String strfk3 = (String) commandMap.get("fkind3");
		String strfk3_org = (String) commandMap.get("fkind3_org");
		String fvalue = (String) commandMap.get("fvalue");
		String fuseyn = (String) commandMap.get("fuseyn");
		String forder = (String) commandMap.get("forder");

		CodeVO vo = new CodeVO();
		vo.setFkind1(strfk1);
		vo.setFkind2(strfk2);
		vo.setFkind3(strfk3);
		vo.setFkind3Org(strfk3_org);
		vo.setFuseyn(fuseyn);
		vo.setFvalue(fvalue);
		vo.setForder(forder);

		rtn = commonService.updateCode (vo);
		modelAndView.addObject("codeSaveCnt", rtn);

		return modelAndView;
	}

	/**
	 * 공통코드 사용유무 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/commCodeUseynChangeSave.do")
	public ModelAndView commCodeUseynChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		String strfk1 = (String) commandMap.get("fkind1");
		String strfk2 = (String) commandMap.get("fkind2");
		String strfk3 = (String) commandMap.get("fkind3");
		String fuseyn = (String) commandMap.get("fuseyn");

		CodeVO vo = new CodeVO();
		vo.setFkind1(strfk1);
		vo.setFkind2(strfk2);
		vo.setFkind3(strfk3);
		vo.setFuseyn(fuseyn);

		rtn = commonService.updateCodeUseYn (vo);
		modelAndView.addObject("codeSaveCnt", rtn);

		return modelAndView;
	}

	/**
	 * (사이트)권한 관리
	 * @param commandMap 파라메터전달용 commandMap
	 * @return basicInfo/auth_management
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/authMngmt.do")
	public String groupauthMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		if (loginVO != null && loginVO.getSite_id() != null && !loginVO.getSite_id().equals("")) {
			List<AuthorVO> authList = basicInfoService.getAuthList();
			model.addAttribute("authList", authList);

			return "cubox/basicInfo/auth_management";
		}else{
			return "redirect:/login.do";
		}

	}

	/**
	 * (사이트)권한 추가
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/authAddSave.do")
	public ModelAndView authAddSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		AuthorVO vo = new AuthorVO();
		vo.setAuthorNm(StringUtil.nvl(commandMap.get("txtAuthorNm")));
		vo.setAuthorDesc(StringUtil.nvl(commandMap.get("txtAuthorDesc")));
		vo.setSortOrdr(StringUtil.isNullToString(commandMap.get("txtSortOrdr")));
		vo.setUseYn(StringUtil.nvl(commandMap.get("selUseYn")));

		rtn = basicInfoService.authAddSave(vo);

		modelAndView.addObject("authSaveCnt", rtn);

		return modelAndView;
	}


	/**
	 * (사이트)권한 사용여부 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/authUseynChangeSave.do")
	public ModelAndView authUseynChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		AuthorVO vo = new AuthorVO();
		vo.setAuthorId(StringUtil.nvl(commandMap.get("authId")));
		vo.setUseYn(StringUtil.nvl(commandMap.get("useYn")));

		rtn = commonService.updateGroupAuthUseYn (vo);
		modelAndView.addObject("groupAuthCnt", rtn);

		return modelAndView;
	}

	/**
	 * (사이트)권한 정보 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/authEditSave.do")
	public ModelAndView authEditSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		AuthorVO vo = new AuthorVO();
		vo.setAuthorId(StringUtil.nvl(commandMap.get("hidAuthorId")));
		vo.setAuthorNm(StringUtil.nvl(commandMap.get("txtAuthorNm")));
		vo.setAuthorDesc(StringUtil.nvl(commandMap.get("txtAuthorDesc")));
		vo.setSortOrdr(StringUtil.nvl(commandMap.get("txtSortOrdr")));
		vo.setUseYn(StringUtil.nvl(commandMap.get("selUseYn")));
		
		rtn = commonService.updateAuth (vo);
		modelAndView.addObject("authEditCnt", rtn);

		return modelAndView;
	}


	/**
	 * 센터관리화면adminfo/authMenuMngmt.do
	 * @param commandMap 파라메터전달용 commandMap
	 * @return basicInfo/center_management
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/siteMngmt.do")
	public String siteMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String siteId = loginVO.getSite_id();

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			
			List<AuthorVO> siteList = basicInfoService.getSiteList();
			//int maxIdx = basicInfoService.getMaxIdx(siteId);
			
			model.addAttribute("siteList", siteList);
			//model.addAttribute("menuPath", "basicInfo/site_management");
			
			//return "cubox/cuboxSubContents";
			return "cubox/basicInfo/site_management";
		}else{
			return "redirect:/login.do";
		}
	}
	/**
	 * (사이트)권한 정보 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/getFloorList.do")
	public ModelAndView getFloorList(@RequestParam Map<String, Object> commandMap,
			 HttpServletRequest request) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	String siteId = (String) commandMap.get("siteId");
		FloorVO vo = new FloorVO();
		vo.setSiteId(siteId);
		
		List<FloorVO> floorList = basicInfoService.getFloorList(vo);
		
		modelAndView.addObject("floorList", floorList);
		

		return modelAndView;
	}


	/**
	 * 공통코드 사용유무 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/siteUseynChangeSave.do")
	public ModelAndView siteUseynChangeSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		String siteId = (String) commandMap.get("siteId");
		String useyn = (String) commandMap.get("useyn");

		SiteVO vo = new SiteVO();
		vo.setSiteId(siteId);
		vo.setUseyn(useyn);
		vo.setRegistId(loginVO.getFsiteid());

		rtn = commonService.updateSiteUseYn (vo);
		modelAndView.addObject("siteCnt", rtn);

		return modelAndView;
	}

	/**
	 * 공통코드 추가
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/siteAddSave.do")
	public ModelAndView siteAddSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;
		
		String registId = loginVO.getFsiteid();

		String  siteNm = StringUtil.nvl(commandMap.get("siteNm"));
		String  sortOrdr = StringUtil.nvl(commandMap.get("sortOrdr"), "99");
		String  siteDesc = StringUtil.nvl(commandMap.get("siteDesc"));

		SiteVO vo = new SiteVO();
		vo.setSiteNm(siteNm);
		vo.setSiteDesc(siteDesc);
		vo.setSortOrdr(sortOrdr);
		vo.setRegistId(registId);

		rtn = basicInfoService.siteAddSave(vo);

		modelAndView.addObject("siteSaveCnt", rtn);

		return modelAndView;
	}

	/**
	 * 공통코드 수정
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/siteEditSave.do")
	public ModelAndView siteEditSave(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		int rtn =  0;

		String siteNm = StringUtil.nvl(commandMap.get("txtEditSiteNm"));
		String siteDesc = StringUtil.nvl(commandMap.get("txtEditSiteDesc"));
		String sortOrdr = StringUtil.nvl(commandMap.get("txtEditSortOrdr"), "99");
		String siteId = StringUtil.nvl(commandMap.get("txtEditSiteId"));

		SiteVO vo = new SiteVO();
		vo.setSiteNm(siteNm);
		vo.setSiteDesc(siteDesc);
		vo.setSortOrdr(sortOrdr);
		vo.setSiteId(siteId);
		vo.setRegistId(loginVO.getFsiteid());		

		rtn = commonService.updateSite(vo);
		modelAndView.addObject("siteEditCnt", rtn);

		return modelAndView;
	}
	
	/**
	 * 도면 업로드
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/floorUpload.do")
	public ModelAndView floorUpload(ModelAndView model, @RequestParam Map<String, Object> commandMap, MultipartHttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
		
		String siteId = loginVO.getSite_id();
		
		String atchFileId = "";
		final int FILE_SIZE = 1;
		
		FloorVO vo = new FloorVO();
		
		String floor =  (String) commandMap.get("hidFloor");
		String floorNm =  StringUtil.isNullToString(commandMap.get("hidFloorNm"));
		String newFloor =  StringUtil.isNullToString(commandMap.get("hidNewFloor"));
		String chkUnder =  StringUtil.isNullToString(commandMap.get("hidChkUnder"));
		String idx =  StringUtil.isNullToString(commandMap.get("hidIndex"));
		String ordr =  StringUtil.isNullToString(commandMap.get("hidOrdr"));
		if (idx == null || "".equals(idx)){
			 idx = Integer.toString( basicInfoService.getMaxIdx(siteId) );
		}
		if(chkUnder.equals("Y")){
			floor = "-"+floor;
			newFloor = "-" + newFloor;
		}
		

		vo.setSiteId(siteId);
		vo.setFloor(floor);
		vo.setFloorNm(floorNm);
		vo.setIdx(idx);
		vo.setNewFloor(newFloor);

		int cnt = basicInfoService.getChkFloor(vo);
		
		if( cnt > 0 ) {
			modelAndView.addObject("result", -2)
						.addObject("message", "입력하신 층이 중복되었습니다.");//floor 중복 오류
			return modelAndView;
		}
		
		String chkAtchFileId = basicInfoService.getAtchFileId(vo);
		int chkFloor = basicInfoService.checkFloor(vo); // 층이 있는지 없는지 확인
		
		if( "".equals(chkAtchFileId) || "null".equals(chkAtchFileId) || chkAtchFileId == null){
			if(chkFloor < 1){
				atchFileId = fileService.getFileID();//파일 ID 생성
				vo.setAtchFileId(atchFileId);

				int saveCnt = basicInfoService.floorUpload(vo);
			}
			
		}else{
			FloorVO result = basicInfoService.getFloorDetail(vo);
			if(result != null){
				if( result.getAtchFileId() == null || "".equals(result.getAtchFileId()) || "null".equals(String.valueOf(result.getAtchFileId())) ) {//기존 파일 ID가 존재하면 그대로 사용
					atchFileId = fileService.getFileID();
				}else {//존재하지 않으면 생성
					atchFileId = result.getAtchFileId();
				}
				
				vo.setAtchFileId(atchFileId);
				int updateCnt = basicInfoService.floorUpdate(vo); 
			}
		}
		
		List<MultipartFile> files = request.getFiles("file"+ordr);
		long size = files.size();
		boolean bb = files.isEmpty();
		
		if( files.size() < 2 ){
			modelAndView.addObject("result",-3)
						.addObject("message","업로드한 파일이 없어 기존의 파일이 유지됩니다.");
						
		}

		if(files != null && !files.isEmpty() && files.size() > 0) {//업로드된 파일이 있으면
			FileVO fMap = FileUtil.parseFileInfo(
					files
					, atchFileId
					, formatList
					, RESOURCE_PATH
					//, registId//, fileTitle
					);
			int fileCount = fMap.getFileCount();
			
			if(fileCount > 0){
				int result = fileService.insertFloorFile(fMap);
				if(result == fileCount) {
					modelAndView.addObject("result", 1)
						.addObject("message", "저장되었습니다.");
					
					
				}else {
					modelAndView.addObject("result", -1)
						.addObject("message", "파일 추가 실패");
				}
			}
			
		}
    
		return modelAndView;
	}
	
	
	/**
	 * 층 삭제
	 * @param
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/floorDelete.do")
	public ModelAndView floorDelete(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		String siteId = loginVO.getSite_id();
		String idx =  (String) commandMap.get("idx");
		String floor =  (String) commandMap.get("floor");
		
		FloorVO vo = new FloorVO();
		vo.setSiteId(siteId);
		vo.setIdx(idx);
		vo.setFloor(floor);
		
		

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
    	
    	

		int chkUseDevice = basicInfoService.checkUseDevice(vo);
		if (chkUseDevice > 0){
			modelAndView.addObject("result", -1);
			return modelAndView;
		}
    	
    	String atchFileId = basicInfoService.getAtchFileId(vo);
		vo.setAtchFileId(atchFileId);
		
//		int chkUseFile = basicInfoService.checkAtchFileId(vo);
//		if (chkUseFile > 0){
//			modelAndView.addObject("result", -2);
//			return modelAndView;
//		}
//		
		

		int delCnt = basicInfoService.floorDelete(vo);
		if(delCnt > 0){
			modelAndView.addObject("result", 1);
		}
    
		return modelAndView;
	}


	/**
	 * sys로그 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return bbasicInfo/sys_log_management
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/sysLogMngmt.do")
	public String sysLogMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if( loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("") ){
			
			String authorId = loginVO.getAuthor_id();

			int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
			String startDatetime = StringUtil.isNullToString(commandMap.get("startDateTime"));
			String endDatetime = StringUtil.isNullToString(commandMap.get("endDateTime"));
			
			String fcnntip = StringUtil.isNullToString(commandMap.get("fcnntip"));
			String fcnntid = StringUtil.isNullToString(commandMap.get("fcnntid"));
			String sysLogDeptValue = StringUtil.isNullToString(commandMap.get("sysLogDeptValue"));
			String sysLogDeptParam = StringUtil.isNullToString(commandMap.get("sysLogDeptParam"));
			
			String sysLogDept1 = StringUtil.isNullToString(commandMap.get("sysLogDept1"));
			String sysLogDept2 = StringUtil.isNullToString(commandMap.get("sysLogDept2"));
			String sysLogDept3 = StringUtil.isNullToString(commandMap.get("sysLogDept3"));
			
			String srchRecPerPage = StringUtil.isNullToString(commandMap.get("srchRecPerPage"));

			DateTimeVO dateTimeVO = commonService.getDateTime();

			if(startDatetime.equals("")) {
				startDatetime = dateTimeVO.getYesterday();// + " " + "00:00:00";
			}
			if(endDatetime.equals("")) {
				endDatetime = dateTimeVO.getToday();// + " " + dateTimeVO.getCurrenttime(); 
			}

			List<String> codeDeptParam = new ArrayList<String>();
			if(!StringUtil.isEmpty(sysLogDeptParam)){
				String[] fCodeArray = sysLogDeptParam.split(",");
				for(String array : fCodeArray){
					codeDeptParam.add(array);
				}
			}else{
				codeDeptParam.add("00");
			}

			List<CodeVO> sysLogParam = basicInfoService.sysLogParam(codeDeptParam);

			List<CodeVO> sysLogDept1List = commonService.getCodeList2("code","accsyslogdept1");		//sys로그 뎁스1 가져오기
			List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수
			
			SysLogVO syslogVO = new SysLogVO();
			syslogVO.setFkind3(loginVO.getSite_id());
			syslogVO.setFsiteid(loginVO.getFsiteid());
			syslogVO.setStartDateTime(startDatetime);
			syslogVO.setEndDateTime(endDatetime);
			syslogVO.setFcnntip(fcnntip);
			syslogVO.setFcnntid(fcnntid);
			syslogVO.setSyslogdept1(sysLogDept1);
			syslogVO.setSyslogdept2(sysLogDept2);
			syslogVO.setSyslogdept3(sysLogDept3);
			syslogVO.setSysLogDeptValue(sysLogDeptValue);
			syslogVO.setAuthorId(authorId);

			int sysLogTotalCnt = basicInfoService.sysLogTotalCnt(syslogVO);
			
			syslogVO.setSrchCnt(srchRecPerPage==null||srchRecPerPage.equals("")?syslogVO.getSrchCnt():srchRecPerPage.equals("0")?sysLogTotalCnt:Integer.parseInt(srchRecPerPage));

			PaginationVO pageVO = new PaginationVO();
			pageVO.setCurPage(srchPage);
			pageVO.setRecPerPage(syslogVO.getSrchCnt());
			pageVO.setTotRecord(sysLogTotalCnt);
			pageVO.setUnitPage(syslogVO.getCurPageUnit());
			pageVO.calcPageList();

			syslogVO.setSrchPage(srchPage);
			syslogVO.autoOffset();

			List<SysLogVO> sysLogList = basicInfoService.sysLogList(syslogVO);

			model.addAttribute("totalCnt",sysLogTotalCnt);
			model.addAttribute("currentPage",srchPage);
			model.addAttribute("displayNum", syslogVO.getSrchCnt());
			model.addAttribute("sysLogParam", sysLogParam);
			model.addAttribute("dateTimeVO", dateTimeVO);
			model.addAttribute("syslogVO", syslogVO);
			model.addAttribute("sysLogDept1List", sysLogDept1List);
			model.addAttribute("cntPerPage", cntPerPage);
			model.addAttribute("pagination",pageVO);
			model.addAttribute("sysLogList", sysLogList);

			return "cubox/basicInfo/sys_log_management";
		}else{
			return "redirect:/login.do";
		}
	}

	/**
	 * sys로그 뎁2 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/sysLogDept2List.do")
	public ModelAndView sysLogDept2List(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fkind3 = StringUtil.isNullToString(commandMap.get("fkind3"));

		CodeVO codeVO = new CodeVO();
		codeVO.setFkind1("code");
		codeVO.setFkind2("accsyslogdept2");
		codeVO.setFkind3(fkind3);

		List<CodeVO> sysLogDept2 = basicInfoService.sysLogDept2List(codeVO);		//sys로그 뎁스2 가져오기

		modelAndView.addObject("sysLogDept2", sysLogDept2);

		return modelAndView;
	}

	/**
	 * sys로그 뎁3 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/sysLogDept3List.do")
	public ModelAndView sysLogDept3List(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		String fkind3 = StringUtil.isNullToString(commandMap.get("fkind3"));

		CodeVO codeVO = new CodeVO();
		codeVO.setFkind1("code");
		codeVO.setFkind2("accsyslogdept3");
		codeVO.setFkind3(fkind3);

		List<CodeVO> sysLogDept3 = basicInfoService.sysLogDept3List(codeVO);		//sys로그 뎁스3 가져오기

		modelAndView.addObject("sysLogDept3", sysLogDept3);

		return modelAndView;
	}

	/**
	 * 날짜 시간 초기화
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/resetDateTime.do")
	public ModelAndView resetDateTime(@RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		DateTimeVO dateTimeVO = commonService.getDateTime();

		modelAndView.addObject("dateTimeVO", dateTimeVO);

		return modelAndView;
	}

	/**
	 * sys로그 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@RequestMapping(value = "/basicInfo/excelDownload.do")
	public ModelAndView excelDownload(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			
			String authorId = loginVO.getAuthor_id();

			String startDatetime = StringUtil.isNullToString(commandMap.get("startDateTime"));
			String endDatetime = StringUtil.isNullToString(commandMap.get("endDateTime"));
			
			String fcnntip = StringUtil.isNullToString(commandMap.get("fcnntip"));
			String fcnntid = StringUtil.isNullToString(commandMap.get("fcnntid"));
			String sysLogDeptValue = StringUtil.isNullToString(commandMap.get("sysLogDeptValue"));
			String sysLogDeptParam = StringUtil.isNullToString(commandMap.get("sysLogDeptParam"));
			
			String sysLogDept1 = StringUtil.isNullToString(commandMap.get("sysLogDept1"));
			String sysLogDept2 = StringUtil.isNullToString(commandMap.get("sysLogDept2"));
			String sysLogDept3 = StringUtil.isNullToString(commandMap.get("sysLogDept3"));
			
			DateTimeVO dateTimeVO = commonService.getDateTime();

			if(startDatetime.equals("")) {
				startDatetime = dateTimeVO.getYesterday();// + " " + "00:00:00";
			}
			if(endDatetime.equals("")) {
				endDatetime = dateTimeVO.getToday();// + " " + dateTimeVO.getCurrenttime(); 
			}

			List<String> codeDeptParam = new ArrayList<String>();
			if(!StringUtil.isEmpty(sysLogDeptParam)){
				String[] fCodeArray = sysLogDeptParam.split(",");
				for(String array : fCodeArray){
					codeDeptParam.add(array);
				}
			}else{
				codeDeptParam.add("00");
			}
			
			String chkValueArray = StringUtil.nvl(request.getParameter("chkValueArray"));
			String chkTextArray = StringUtil.nvl(request.getParameter("chkTextArray"));			

			SysLogVO syslogVO = new SysLogVO();
			syslogVO.setFkind3(loginVO.getSite_id());
			syslogVO.setFsiteid(loginVO.getFsiteid());
			syslogVO.setStartDateTime(startDatetime);
			syslogVO.setEndDateTime(endDatetime);
			syslogVO.setFcnntip(fcnntip);
			syslogVO.setFcnntid(fcnntid);
			syslogVO.setSyslogdept1(sysLogDept1);
			syslogVO.setSyslogdept2(sysLogDept2);
			syslogVO.setSyslogdept3(sysLogDept3);
			syslogVO.setSysLogDeptValue(sysLogDeptValue);
			syslogVO.setAuthorId(authorId);
			syslogVO.setChkValueArray(chkValueArray);
			syslogVO.setChkTextArray(chkTextArray);

			LOGGER.debug("sysLogDeptValue : " + sysLogDeptValue);

			String[] chkText = chkTextArray.split(",");

			List<ExcelVO> resultCellList = basicInfoService.sysLogExcelList(syslogVO);

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", resultCellList);
			modelAndView.addObject("excelName", "시스템로그");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}

	/**
	 * syslog 상세내용 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/basicInfo/getSysLogInfo.do")
	public ModelAndView getSysLogInfo(@RequestParam Map<String, String> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map = basicInfoService.selectSysLogInfo(param);
		
		modelAndView.addObject("sysLogInfo", map);

		return modelAndView;
	}
	
	/**
	 * 출입로그 관리
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/logSetMngmt.do")
	public String logSetMngmt(ModelMap model, HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		if (loginVO != null && loginVO.getSite_id() != null && !loginVO.getSite_id().equals("")) {
			List<Map<String, Object>> list = basicInfoService.selectGateLogSettingList(param);
			model.addAttribute("setList", list);

			return "cubox/basicInfo/log_set_management";
		} else {
			return "redirect:/login.do";
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/saveLogSetting.do")
	public ModelAndView saveLogSetting(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		try {
			param.put("keep_sdt", param.get("startDate"));
			param.put("keep_edt", param.get("endDate"));
			
			if(StringUtil.nvl(param.get("use_yn")).equals("Y") || StringUtil.nvl(param.get("keep_seq")).equals("")) {
				int chk = basicInfoService.selectDupKeepDt(param);
				LOGGER.debug("###[출입이력 중복일자 체크] chk:{}, param:{}", chk, param);
				if(chk > 0) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "기준 일자가 중복됩니다. 기준 일자를 다시 선택하세요.");
					
					return modelAndView;				
				}
			}
			
			int cnt = 0;			
			if(StringUtil.nvl(param.get("keep_seq")).equals("")) {
				param.put("reg_id", loginVO.getFsiteid());
				cnt = basicInfoService.insertGateLogSettingInfo(param);
			} else {
				param.put("mod_id", loginVO.getFsiteid());
				cnt = basicInfoService.updateGateLogSettingInfo(param);
			}
			LOGGER.debug("###[출입이력 설정정보 저장] cnt:{}, param:{}", cnt, param);
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
			} else {
				modelAndView.addObject("result", "fail");
			}
		} catch(Exception e) {
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}
	
	/**
	 * 출입자 변경 이력
	 * @param model
	 * @param commandMap
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/chgLogList.do")
	public String chgLogList(ModelMap model, @RequestParam Map<String, Object> param,
			HttpServletRequest request) throws Exception {

		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");		
		DateTimeVO dateTimeVO = commonService.getDateTime();

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String srchRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(recPerPage));
		int srchCnt = Integer.parseInt(srchRecPerPage);
		
		if(StringUtil.nvl(param.get("startDate")).equals("")) {
			param.put("startDate", dateTimeVO.getYesterday());
		}
		if(StringUtil.nvl(param.get("endDate")).equals("")) {
			param.put("endDate", dateTimeVO.getToday());
		}
		
		List<CodeVO> cmnCntPerPage = commonService.getCodeList("combo", "COUNT_PER_PAGE");  //page당 record 수
		List<CodeVO> cmnChgClCd = commonService.getCodeList("code", "chg_cl_cd");  //변경구분코드

		int total = basicInfoService.selectUserChgLogListCount(param);

		// Paging
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(srchCnt);
		pageVO.setTotRecord(total);
		pageVO.setUnitPage(curPageUnit);
		pageVO.calcPageList();
		
        String sRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"));
        if(!sRecPerPage.equals("")) {
            srchCnt = Integer.parseInt(sRecPerPage);
        }
        param.put("offset", getOffset(srchPage, srchCnt));
        param.put("srchCnt", srchCnt);

		List<UserChgLogVO> list = basicInfoService.selectUserChgLogList(param);

		model.addAttribute("dateTimeVO", dateTimeVO);
		model.addAttribute("cmnCntPerPage", cmnCntPerPage);
		model.addAttribute("cmnChgClCd", cmnChgClCd);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("chgLogList", list);
		model.addAttribute("params", param);

		return "cubox/basicInfo/user_chg_log_list";
	}
	
	@ResponseBody
	@RequestMapping(value = "/basicInfo/getChgLogInfo.do")
	public ModelAndView getChgLogInfo(@RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		UserChgLogVO vo = new UserChgLogVO();
		vo = basicInfoService.selectUserChgLogInfo(param);
		
		if(vo != null) {
			modelAndView.addObject("result", "success");
			modelAndView.addObject("chgLogInfo", vo);	
		} else {
			modelAndView.addObject("result", "fail");
		}

		return modelAndView;
	}
	
	@RequestMapping(value = "/basicInfo/chgLogListExcel.do")
	public ModelAndView chgLogListExcel(@RequestParam Map<String, Object> param, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			
			DateTimeVO dateTimeVO = commonService.getDateTime();
			if(StringUtil.nvl(param.get("startDate")).equals("")) {
				param.put("startDate", dateTimeVO.getYesterday());
			}
			if(StringUtil.nvl(param.get("endDate")).equals("")) {
				param.put("endDate", dateTimeVO.getToday());
			}
			
			List<ExcelVO> list = basicInfoService.selectUserChgLogListExcel(param);
			
			String chkTextArray = StringUtil.nvl(param.get("chkTextArray"));			
			String[] chkText = chkTextArray.split(",");			

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", list);
			modelAndView.addObject("excelName", "출입자변경이력");
			modelAndView.addObject("excelHeader", chkText);
			
			commonService.sysLogSave(loginVO.getFsiteid(), "13101212", param.toString(), commonUtils.getIPFromRequest(request)); //출입자변경이력 엑셀다운로드
		}

		return modelAndView;
	}
	
	/**
	 * 출입자 변경 전 사진 조회
	 * @param fuid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/basicInfo/getChgBioInfo.do")
	public ResponseEntity<byte[]> getChgBioInfo(@RequestParam("chg_seq") String fuid) throws Exception {
		
		String key = "s8LiEwT3if89Yq3i90hIo3HepqPfOhVd";
		AES256Util aes256 = new AES256Util();
	
		Map<String, Object> map = new HashMap<String, Object>();
		map = basicInfoService.selectUserChgBioInfo(fuid);
		
		byte[] imageContent = null;
		if(map != null && StringUtil.nvl(map.get("fimg")).length() != 0){
			imageContent = aes256.byteArrDecodenopaddingImg(StringUtil.nvl(map.get("fimg")), key);
		}
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
	}	
	
	private int getOffset(int srchPage, int srchCnt) {
		int offset = (srchPage - 1) * srchCnt;
		if(offset < 0) offset = 0;
    	return offset;
    }	
}
