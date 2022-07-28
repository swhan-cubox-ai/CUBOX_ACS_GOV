package aero.cubox.cmmn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import aero.cubox.menu.service.MenuService;
import aero.cubox.menu.vo.MenuDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springmodules.validation.commons.DefaultBeanValidator;

import aero.cubox.util.AuthorManager;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.vo.BoardVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.MainStatusVO;
import egovframework.rte.fdl.property.EgovPropertyService;


@Controller
public class CommonController {

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "commonUtils")
	private CommonUtils commonUtils;
	
	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;
	
	@Resource(name = "MenuService")
	private MenuService menuService;
	
	/*
	@Value("#{property['Globals.site.main.id']}") 
	private String SITE_MAIN_ID;
	@Value("#{property['Globals.site.main.name']}") 
	private String SITE_MAIN_NAME;
	*/

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);

	@RequestMapping(value="/login.do")
	public String login(ModelMap model, @RequestParam Map<String, Object> commandMap, RedirectAttributes redirectAttributes) throws Exception {
		
		return "cubox/common/login";
	}

	@RequestMapping(value="/common/loginProc.do")
	public String actionLogin(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		String fsiteid = (String) commandMap.get("fsiteid");
		String fpasswd = (String) commandMap.get("fpasswd");

		LoginVO loginVO = new LoginVO();
		loginVO.setFsiteid(fsiteid);
		loginVO.setFpasswd(fpasswd);

		LoginVO resultVO = commonService.actionLogin(loginVO);
		resultVO.setFlastaccip(commonUtils.getIPFromRequest(request));
		resultVO.setFlastaccdt(commonUtils.getToday("yyyyMMddHHmmss"));
		//resultVO.setSite_main_id( SITE_MAIN_ID );

		LOGGER.debug("[LAST_ACC_IP] :" + resultVO.getFlastaccip());
		LOGGER.debug("[LAST_ACC_DE] :" + resultVO.getFlastaccdt());

		if (resultVO != null && resultVO.getFsiteid() != null && !resultVO.getFsiteid().equals("")) {
			commonService.lastConnect(resultVO); //로그인시 마지막 접속일 변경

			request.getSession().setAttribute("loginVO", resultVO);
			commonService.sysLogSave(fsiteid, "10100001", "", commonUtils.getIPFromRequest(request)); //로그저장
			//return "redirect:/index.do";
			return "redirect:/main.do";
		} else {
			model.addAttribute("resultMsg", "loginError");
			commonService.sysLogSave(fsiteid, "10100002", "", commonUtils.getIPFromRequest(request)); //로그저장
			return "cubox/common/login";
		}
	}

	@SuppressWarnings("serial")
	@RequestMapping(value="/main.do")
	public String main(ModelMap model, @RequestParam Map<String, Object> commandMap, RedirectAttributes redirectAttributes, HttpSession session) throws Exception {
		//자동새로고침때문에 추가
		String reloadYn = StringUtil.isNullToString(commandMap.get("reloadYn")).matches("Y") ? StringUtil.isNullToString(commandMap.get("reloadYn")) : "N";
		String intervalSecond = StringUtil.isNullToString(commandMap.get("intervalSecond")).matches("(^[0-9]+$)") ? StringUtil.isNullToString(commandMap.get("intervalSecond")) : "5";

		//model.addAttribute("menuPath", "common/login");

		model.addAttribute("reloadYn", reloadYn);
		model.addAttribute("intervalSecond", intervalSecond);
		
		String ssAuthorId = ((LoginVO)session.getAttribute("loginVO")).getAuthor_id();
		
		//사용자가 해당 메뉴(근태관리)에 접근 권한이 있는지 조회
		List<MenuDetailVO> result =  menuService.getUserMenuList(new HashMap<String, Object>() {
			{
				put("author_id", ssAuthorId);
				put("menu_cl_code", "00009");//근태관리 메뉴 코드
			}
		});
		
		model.addAttribute("isMenu", (result != null && result.size() > 0) ? true : false );
		
		if(ssAuthorId.equals("00008")) { //출입자등록 
			return "redirect:/userInfo/userAddPopup2.do";
		} else {
			return "cubox/common/main";
		}
	}

	/**
	 * main 출입이력 카운트
	 * @param
	 * @return ModelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/main/getMainLogCnt.do")
	public ModelAndView getMainLogCnt (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("ssAuthorId", loginVO.getAuthor_id());
		map.put("ssFuid", loginVO.getFuid());
		map.put("ssSiteId", loginVO.getSite_id());

		//일일 출입 현황
		int dayCnt = commonService.getMainLogTotCnt(map);
		//일일출입 인원
		int dayUserCnt = commonService.getMainLogTotUserCnt();
		//단말기 장애
		
		//재실자
		int inmateCnt = commonService.getInmateCnt();

		modelAndView.addObject("dayCnt", dayCnt);
		modelAndView.addObject("dayUserCnt", dayUserCnt);
		modelAndView.addObject("inmateCnt", inmateCnt);
		return modelAndView;
	}

	/**
	 * main 그래프
	 * @param
	 * @return ModelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/main/getMainStatus.do")
	public ModelAndView getMainStatus(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");
		
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		Map<String, String> param = new HashMap<String, String>();
		if(loginVO.getAuthor_id().equals("00009")) { // 일반사용자
			param.put("ssFuid", StringUtil.nvl(loginVO.getFuid(), "XXXXXXXX"));
		}
		List<MainStatusVO> statLit = commonService.selectMainLogGraph(param);
		
		modelAndView.addObject("statLit", statLit);
		return modelAndView;
	}

	/**
	 * main 출입이력
	 * @param
	 * @return ModelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/main/getMainLogList.do")
	public ModelAndView getMainLogList (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName("jsonView");
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		
		Map<String, String> param = new HashMap<String, String>();
		if(loginVO.getAuthor_id().equals("00009")) { // 일반사용자
			param.put("ssFuid", StringUtil.nvl(loginVO.getFuid(), "XXXXXXXX"));
		}
		List<LogInfoVO> logInfoList = commonService.getMainLogList(param);
		
		modelAndView.addObject("logInfoList", logInfoList);
		return modelAndView;
	}
	
	/**
	 * main 출입이력
	 * @param
	 * @return ModelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/main/getMainNoticeList.do")
	public ModelAndView getMainNoticeList (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		//출입이력
		List<BoardVO> noticeList = commonService.getMainNoticeList();
		modelAndView.addObject("noticeList", noticeList);
		return modelAndView;
	}
	
	/**
	 * main 출입이력
	 * @param
	 * @return ModelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/main/getMainQaList.do")
	public ModelAndView getMainQaList (@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jsonView");

		//출입이력
		List<BoardVO> qaList = commonService.getMainQaList();
		modelAndView.addObject("qaList", qaList);
		return modelAndView;
	}
	
	@RequestMapping(value = "/logout.do")
	public String actionLogout(HttpServletRequest request, ModelMap model) throws Exception {

		HttpSession session = request.getSession();
		LoginVO loginVO = (LoginVO) session.getAttribute("loginVO");
		session.setAttribute("loginVO", null);
		session.invalidate();
		AuthorManager.getInstance().clear();

		if (loginVO != null) {
			commonService.sysLogSave(loginVO.getFsiteid(), "10110001", "", commonUtils.getIPFromRequest(request)); //로그저장
		}
		return "redirect:/login.do";
	}

	/**
	 * 모니터링
	 * @param commandMap 파라메터전달용 commandMap
	 * @return common/index
	 * @throws Exception
	 */
	@RequestMapping(value="/index.do")
	public String index(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			model.addAttribute("menuPath", "common/index");
			return "cubox/cuboxSubContents";
		}else{
			return "redirect:/login.do";
		}
	}
}
