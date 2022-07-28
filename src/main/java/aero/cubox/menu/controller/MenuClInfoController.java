package aero.cubox.menu.controller;

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

import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.menu.service.MenuClInfoService;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.PaginationVO;

@Controller
public class MenuClInfoController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuClInfoController.class);
	
	private int recPerPage  = 10; //조회할 페이지 수
	private int curPageUnit = 10; //한번에 표시할 페이지 번호 개수	

	@Resource(name="commonService")
	private CommonService commonService;

	@Resource(name="commonUtils")
	protected CommonUtils commonUtils;
	
	@Resource(name="menuClInfoService")
	protected MenuClInfoService menuClInfoService;
	
	/**
	 * 메뉴관리 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/basicInfo/menuMngmt.do")
	public String authorGroupMngmt(ModelMap model, HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {
		
		try { 
			String menuNm = StringUtil.nvl(menuClInfoService.selectMenuNm(request.getServletPath()));
			
			int srchCnt = recPerPage;
			String sRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"));
			if(!sRecPerPage.equals("")) {
				srchCnt = Integer.parseInt(sRecPerPage);
			}	
			
			// paging
			int srchPage = Integer.parseInt(StringUtil.nvl(param.get("srchPage"), "1")); //조회할 페이지 번호 기본 1페이지
			param.put("offset", getOffset(srchPage, srchCnt));
			param.put("srchCnt", srchCnt);		
	
			List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); //page당 record 수
			List<Map<String, Object>> menuClList = menuClInfoService.selectMenuClList(null);  //메뉴분류코드
			
			int menuCnt = menuClInfoService.selectMenuDetailListCount(param);  //메뉴목록
			List<Map<String, Object>> menuList = menuClInfoService.selectMenuDetailList(param);  //메뉴목록
	
			// Paging
			PaginationVO pageVO = new PaginationVO();
			pageVO.setCurPage(srchPage);
			pageVO.setRecPerPage(srchCnt);
			pageVO.setTotRecord(menuCnt);
			pageVO.setUnitPage(curPageUnit);
			pageVO.calcPageList();
			model.addAttribute("pagination", pageVO);
	
			model.addAttribute("menuClList", menuClList);
			model.addAttribute("cntPerPage", cntPerPage);
			model.addAttribute("menuList", menuList);
			model.addAttribute("menuNm", menuNm);
			model.addAttribute("params", param);
		
		} catch(Exception e) {
			e.printStackTrace();
		}

		return "cubox/basicInfo/menu_management";
	}
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/getMenuInfo.do")
	public ModelAndView getMenuInfo(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		try { 
			Map<String, Object> detail = menuClInfoService.selectMenuDetailInfo(param);
			if(detail != null) {
				modelAndView.addObject("detail", detail);	
			} else {
				modelAndView.addObject("message", "해당 자료가 없습니다.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/saveMenuInfo.do")
	public ModelAndView addMenuInfo(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		param.put("regist_id", loginVO.getFsiteid());
		param.put("modify_id", loginVO.getFsiteid());
		
		param.put("menu_code", StringUtil.nvl(param.get("hidMenuCode")));
		param.put("menu_cl_code", StringUtil.nvl(param.get("selMenuCl")));
		param.put("menu_nm", StringUtil.nvl(param.get("txtMenuNm")));
		param.put("menu_url", StringUtil.nvl(param.get("txtMenuUrl")));
		param.put("sort_ordr", StringUtil.nvl(param.get("txtSortOrdr"), "1"));
		
		int cnt = 0;
		try {
			if(StringUtil.nvl(param.get("hidMenuCode")).equals("")) {
				cnt = menuClInfoService.insertMenuDetailInfo(param);	
			} else {
				cnt = menuClInfoService.updateMenuDetailInfo(param);
			}
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
			} else {
				modelAndView.addObject("result", "fail");
			}
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/modMenuUseYn.do")
	public ModelAndView modMenuUseYn(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		param.put("modify_id", loginVO.getFsiteid());
		
		try {
			int cnt = menuClInfoService.updateMenuDetailUseYn(param);	
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
			} else {
				modelAndView.addObject("result", "fail");
			}
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/getMenuClList.do")
	public ModelAndView getMenuClList(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		try {
			List<Map<String, Object>> list = menuClInfoService.selectMenuClList(param);
			
			modelAndView.addObject("list", list);
			
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/getMenuClInfo.do")
	public ModelAndView getMenuClInfo(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		try { 
			Map<String, Object> detail = menuClInfoService.selectMenuClInfo(param);
			if(detail != null) {
				modelAndView.addObject("detail", detail);	
			} else {
				modelAndView.addObject("message", "해당 자료가 없습니다.");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}		
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/saveMenuClInfo.do")
	public ModelAndView saveMenuClInfo(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		param.put("regist_id", loginVO.getFsiteid());
		param.put("modify_id", loginVO.getFsiteid());
		
		param.put("menu_cl_code", StringUtil.nvl(param.get("hidMenuClCode")));
		param.put("menu_cl_nm", StringUtil.nvl(param.get("txtMenuClNm")));
		param.put("icon_img", StringUtil.nvl(param.get("txtIconImg"), "left_icon1.png"));
		param.put("sort_ordr", StringUtil.nvl(param.get("txtClSortOrdr"), "1"));
		
		int cnt = 0;
		try {
			if(StringUtil.nvl(param.get("hidMenuClCode")).equals("")) {
				cnt = menuClInfoService.insertMenuClInfo(param);	
			} else {
				cnt = menuClInfoService.updateMenuClInfo(param);
			}
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
			} else {
				modelAndView.addObject("result", "fail");
			}
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}	
	
	@ResponseBody
	@RequestMapping(value="/basicInfo/modMenuClUseYn.do")
	public ModelAndView modMenuClUseYn(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		param.put("modify_id", loginVO.getFsiteid());
		
		try {
			int cnt = menuClInfoService.updateMenuClUseYn(param);	
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
			} else {
				modelAndView.addObject("result", "fail");
			}
		} catch(Exception e) {
			e.printStackTrace();
			modelAndView.addObject("result", "fail");
			modelAndView.addObject("message", e.getMessage());
		}

		return modelAndView;
	}	
	
	private int getOffset(int srchPage, int srchCnt) {
		int offset = (srchPage - 1) * srchCnt;
		if(offset < 0) offset = 0;
		return offset;
	}
	
}
