package aero.cubox.auth.controller;

import java.util.ArrayList;
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

import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import aero.cubox.auth.service.AuthorGroupService;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.auth.service.vo.AuthorGroupDetailVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.SiteVO;

@Controller
public class AuthorInfoController {
	
	@Resource(name = "authorGroupService")
	private AuthorGroupService authorGroupService;
	
	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;	
	
	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;
	
	/**
	 * 권한 그룹 상세 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/getAuthorGroupDetail.do")
	public ModelAndView getAuthorGroupDetail (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String authorGroupId = StringUtil.isNullToString( commandMap.get("selAuthorGroupId") );
		String siteId = StringUtil.isNullToString( commandMap.get("siteId") );
		
		AuthorGroupDetailVO vo = new AuthorGroupDetailVO ();
		vo.setAuthorGroupId(authorGroupId);
		vo.setSiteId(siteId);

		List<AuthorGroupDetailVO> detailList = authorGroupService.getAuthorGroupDetail(vo);

		modelAndView.addObject( "detailList", detailList);

		return modelAndView;
	}
	
	/**
	 * 권한 그룹 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/getAuthorGroup.do")
	public ModelAndView getAuthorGroup (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String authorGroupId = StringUtil.isNullToString( commandMap.get("selAuthorGroupId") );
		
		AuthorGroupVO vo = new AuthorGroupVO ();
		vo.setAuthorGroupId(authorGroupId);

		AuthorGroupVO detail = authorGroupService.getAuthorGroup(vo);

		modelAndView.addObject( "detail", detail);

		return modelAndView;
	}
	
	/**
	 * 권한 그룹 저장
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/saveAuthorGroup.do")
	public ModelAndView saveAuthorGroup (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	
		AuthorGroupVO vo = new AuthorGroupVO ();    	
    	int rtn = 0;
    	
    	try {
    		String authorGroupId = StringUtil.isNullToString( commandMap.get("authorGroupId") );
    		String authorGroupNm = StringUtil.isNullToString( commandMap.get("authorGroupNm") );
    		String authorGroupDesc = StringUtil.isNullToString( commandMap.get("authorGroupDesc") );
    		String sortOrdr = StringUtil.isNullToString( commandMap.get("sortOrdr") );
    		String empYn = StringUtil.isNullToString( commandMap.get("empYn") );
    		String visitYn = StringUtil.isNullToString( commandMap.get("visitYn") );
    		String useYn = StringUtil.isNullToString( commandMap.get("useYn") );
    		
    		vo.setAuthorGroupId(authorGroupId);
    		vo.setAuthorGroupNm(authorGroupNm);
    		vo.setAuthorGroupDesc(authorGroupDesc);
    		vo.setSortOrdr(sortOrdr);
    		vo.setEmpYn(empYn);
    		vo.setVisitYn(visitYn);
    		vo.setUseYn(useYn);
    		vo.setRegistId(loginVO.getFsiteid());
    		vo.setModifyId(loginVO.getFsiteid());

    		rtn = authorGroupService.saveAuthorGroup(vo);
    		
    		if(authorGroupId.equals("")) {  // 신규
    			if(rtn > 0) {
    				commonService.sysLogSave(loginVO.getFsiteid(), "12101001", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 추가 성공
    			} else {
    				commonService.sysLogSave(loginVO.getFsiteid(), "12101002", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 추가 실패
    			}
    		} else {  // 수정
    			if(rtn > 0) {
    				commonService.sysLogSave(loginVO.getFsiteid(), "12101101", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 수정 성공
    			} else {
    				commonService.sysLogSave(loginVO.getFsiteid(), "12101102", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 수정 실패
    			}
    		}
    		
		} catch(Exception e) {
			modelAndView.addObject("message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "12101102", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 수정 실패
    	}
		
		modelAndView.addObject( "result", rtn);

		return modelAndView;
	}
	
	/**
	 * 권한그룹관리
	 * @param commandMap 파라메터전달용 commandMap
	 * @return userInfo/user_management
	 * @throws Exception
	 */
	@RequestMapping(value="/authorInfo/authorGroupMngmt.do")
	public String authorGroupMngmt(ModelMap model, HttpServletRequest request) throws Exception {
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		//권한 그룹 조회
		AuthorGroupVO vo = new AuthorGroupVO ();
		vo.setUseYn("Y");
		List<AuthorGroupVO> athorGroupList = authorGroupService.getAuthorGroupList(vo);
		List<SiteVO> centerCombo = commonService.getCenterCodeList(loginVO.getSite_id());		//센터 코드가져오기
		
		AuthorGroupDetailVO pvo = new AuthorGroupDetailVO();
		pvo.setSiteId(loginVO.getSite_id());
		List<AuthorGroupDetailVO> totalDeviceList = authorGroupService.getTotalDeviceList(pvo);	//전체 장비

		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("athorGroupList", athorGroupList);
		model.addAttribute( "totalDeviceList", totalDeviceList);
		model.addAttribute( "loginSiteId", loginVO.getSite_id());

		return "cubox/authorInfo/author_group_management";
	}

	/**
	 * 권한그룹별 출입자 목록
	 * @param model
	 * @param request
	 * @param param
	 * @return authorInfo/author_user_list_popup
	 * @throws Exception
	 */
	@RequestMapping(value="/authorInfo/authUserListPopup.do")
	public String userListPopup(ModelMap model, HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {
		//LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		List<AuthorGroupVO> athorGroupList = null;
		List<Map<String, Object>> glist = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ulist = new ArrayList<Map<String, Object>>();

		try {
			AuthorGroupVO vo = new AuthorGroupVO ();
			vo.setUseYn("Y");
			athorGroupList = authorGroupService.getAuthorGroupList(vo);			
			
			param.put("fpartcd1", param.get("hidPartCd1"));
			param.put("author_group_id", param.get("hidAuthorGroupId"));
			
			glist = authorGroupService.getGateListByAuth(param);
			ulist = authorGroupService.getUserListByAuth(param);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(ulist != null) {
			model.addAttribute("auth", athorGroupList);
			model.addAttribute("glist", glist);
			model.addAttribute("ulist", ulist);
			model.addAttribute("param", param);
		}
		
		return "cubox/authorInfo/author_user_list_popup";
	}	
	
	/**
	 * 권한그룹별 출입자 목록 엑셀저장
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/authorInfo/authUserListExcel.do")
	public ModelAndView authUserListExcel(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {

			param.put("fpartcd1", param.get("hidPartCd1"));
			param.put("author_group_id", param.get("hidAuthorGroupId"));
			
			String authNm = StringUtil.nvl(request.getParameter("hidAuthorGroupNm"));
			String chkTextArray = StringUtil.nvl(request.getParameter("chkTextArray"));

			String[] chkText = chkTextArray.split(",");
			
			List<ExcelVO> ulist = authorGroupService.getUserListByAuthExcel(param);

			//commonService.sysLogSave(loginVO.getFsiteid(), "13101201", param.toString(), commonUtils.getIPFromRequest(request)); //로그저장

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", ulist);
			modelAndView.addObject("excelName", authNm+"_출입자목록");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}
	
	/**
	 * 권한그룹별 출입자 목록 엑셀저장
	 * @param request
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/authorInfo/authGateListExcel.do")
	public ModelAndView authGateListExcel(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {

			param.put("fpartcd1", param.get("hidPartCd1"));
			param.put("author_group_id", param.get("hidAuthorGroupId"));

			String authNm = StringUtil.nvl(request.getParameter("hidAuthorGroupNm"));
			String chkTextArray = StringUtil.nvl(request.getParameter("chkTextArray"));

			String[] chkText = chkTextArray.split(",");
			
			List<ExcelVO> list = authorGroupService.getGateListByAuthExcel(param);

			//commonService.sysLogSave(loginVO.getFsiteid(), "13101201", param.toString(), commonUtils.getIPFromRequest(request)); //로그저장

			modelAndView.setViewName("excelDownloadView");
			modelAndView.addObject("resultList", list);
			modelAndView.addObject("excelName", authNm+"_단말기목록");
			modelAndView.addObject("excelHeader", chkText);
		}

		return modelAndView;
	}	
	
	/**
	 * 권한 그룹 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/getAuthorGroupInfo.do")
	public ModelAndView getAuthorGroupInfo (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String authorId = StringUtil.isNullToString( commandMap.get("selAuthorId") );
		String partcd1 = StringUtil.isNullToString( commandMap.get("selPartcd1") );

		AuthorGroupDetailVO pvo = new AuthorGroupDetailVO();
		pvo.setAuthorGroupId(authorId);
		pvo.setSiteId(partcd1);
		List<AuthorGroupDetailVO> totalDeviceList = authorGroupService.getTotalDeviceList(pvo);	//전체 장비
		List<AuthorGroupDetailVO>  authorDeviceList = authorGroupService.getAuthorDeviceList(pvo);	//권한 그룹 별 선택 장비

		modelAndView.addObject( "totalDeviceList", totalDeviceList);
		modelAndView.addObject(  "authorDeviceList", authorDeviceList );

		return modelAndView;
	}
	
	/**
	 * 권한 그룹 상세 저장
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/saveAuthorGroupDetail.do")
	public ModelAndView saveAuthorGroupDetail (@RequestParam Map<String, Object> commandMap, String[] deviceArray, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		String authorGroupId = StringUtil.isNullToString( commandMap.get("authorGroupId") );
		String siteId = StringUtil.isNullToString( commandMap.get("siteId") );
		
		AuthorGroupDetailVO vo = new AuthorGroupDetailVO ();
		
		try {
			vo.setSiteId(siteId);
			vo.setAuthorGroupId(authorGroupId);
			vo.setDeviceIds(deviceArray);
			vo.setRegistId(loginVO.getFsiteid());
			vo.setModifyId(loginVO.getFsiteid());

			int rtn = authorGroupService.saveAuthorGroupDetail(vo);
			
			if(rtn > 0) {
				modelAndView.addObject( "result", "success");
				commonService.sysLogSave(loginVO.getFsiteid(), "12101201", vo.toString(), commonUtils.getIPFromRequest(request)); //권한그룹 장비 적용 성공
			} else {
				modelAndView.addObject( "result", "fail");
				commonService.sysLogSave(loginVO.getFsiteid(), "12101202", vo.toString(), commonUtils.getIPFromRequest(request)); //권한그룹 장비 적용 실패
			}		
		} catch(Exception e) {
			modelAndView.addObject( "result", "fail");
			modelAndView.addObject( "message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "12101202", vo.toString(), commonUtils.getIPFromRequest(request)); //권한그룹 장비 적용 실패
		}

		modelAndView.addObject( "authorGroupId", authorGroupId);

		return modelAndView;
	}
	
	/**
	 * 권한 그룹 전체 목록 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/getTotalAuthorGroup.do")
	public ModelAndView getTotalAuthorGroup (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	//권한 그룹 조회
    	AuthorGroupVO vo = new AuthorGroupVO ();
    	vo.setAuthorGroupNm(StringUtil.isNullToString(commandMap.get("authorGroupNm")));
    	List<AuthorGroupVO> athorGroupList = authorGroupService.getAuthorGroupList(vo);
		modelAndView.addObject( "list", athorGroupList);

		return modelAndView;
	}
	
	/**
	 * 권한 그룹 저장
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/authorInfo/updateAuthorGroupUseYn.do")
	public ModelAndView updateAuthorGroupUseYn (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");
    	int rtn = 0;
    	
    	LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		String siteId = StringUtil.isNullToString( commandMap.get("siteId") ); // 2021-03-16
		String authorGroupId = StringUtil.isNullToString( commandMap.get("authorGroupId") );
		String useYn = StringUtil.isNullToString( commandMap.get("useYn") );

		
		AuthorGroupVO vo = new AuthorGroupVO ();		
		
		try {
			if(authorGroupId != null && !authorGroupId.trim().equals("")) {
				vo.setSiteId(siteId);
				vo.setAuthorGroupId(authorGroupId);
				vo.setUseYn(useYn);
	    		vo.setModifyId(loginVO.getFsiteid());
				rtn = authorGroupService.updateAuthorGroupUseYn(vo);
			}
			
			if(rtn > 0) {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101101", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 수정 성공
			} else {
				commonService.sysLogSave(loginVO.getFsiteid(), "12101102", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 수정 실패
			}
			
		} catch(Exception e) {
			modelAndView.addObject("message", e.getMessage());
			commonService.sysLogSave(loginVO.getFsiteid(), "12101102", vo.toString(), commonUtils.getIPFromRequest(request)); //그룹권한 수정 실패
		}
		
		modelAndView.addObject("result", rtn);		

		return modelAndView;
	}

}
