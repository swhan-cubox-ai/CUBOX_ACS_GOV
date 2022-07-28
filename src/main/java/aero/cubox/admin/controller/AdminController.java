package aero.cubox.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import aero.cubox.auth.service.vo.AuthorVO;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.menu.service.MenuService;
import aero.cubox.menu.vo.MenuClVO;
import aero.cubox.menu.vo.MenuDetailVO;
import aero.cubox.sample.service.BasicInfoService;
import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.user.service.UserInfoService;
import aero.cubox.user.service.vo.UserInfoVO;
import aero.cubox.util.AuthorManager;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.StringUtil;
import aero.cubox.auth.service.AuthInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class AdminController {

	@Resource(name = "authInfoService")
	private AuthInfoService authInfoService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** Validator */
	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	/** memberService */
	@Resource(name = "userInfoService")
	private UserInfoService userInfoService;

	/** basicInfoService */
	@Resource(name = "basicInfoService")
	private BasicInfoService basicInfoService;

	@Resource
    private MenuService menuService;

	/**
	 * 그룹권한
	 * @param commandMap 파라메터전달용 commandMap
	 * @return member/join04
	 * @throws Exception
	 */
	@RequestMapping(value="/adminfo/menuMngmt.do")
	public String menuMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		//getMenuCl(String author)

		//totalAuthGroup = authInfoService.getAuthGroupList(authGroupVO);
		/*
		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("authGroupVO", authGroupVO);
		model.addAttribute("groupAuthList", totalAuthGroup);
		 */
		return "cubox/adminInfo/auth_management";
	}



	/**
	 * 그룹권한
	 * @param commandMap 파라메터전달용 commandMap
	 * @return member/join04
	 * @throws Exception
	 */
	@RequestMapping(value="/adminfo/authMenuMngmt.do")
	public String authMenuMngmt(ModelMap model, @RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		AuthorManager authorManager = AuthorManager.getInstance();
		String authorId = loginVO.getAuthor_id();
		//대메뉴 조회
		List<MenuClVO> menuClList = authorManager.getMenuCl (authorId);

		UserInfoVO userInfo = new UserInfoVO();

		userInfo.setFuid(authorId); //fuid
		//userInfo.setFpartcd1(fpartcd1);

		List<CardInfoVO> cardInfoList = null;

		userInfo = authInfoService.getUserInfo2(userInfo);
		cardInfoList = authInfoService.getCardInfoList(authorId); //fuid

		HashMap test = new HashMap();
		test.put("use_yn", "Y");
		List<AuthorVO> authorList = menuService.getAuthorList(test);

		//getMenuCl(String author)

		//totalAuthGroup = authInfoService.getAuthGroupList(authGroupVO);
		/*
		model.addAttribute("centerCombo",centerCombo);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("authGroupVO", authGroupVO);
		model.addAttribute("groupAuthList", totalAuthGroup);
		 */

		HashMap pMap = new HashMap();

		List<MenuDetailVO> totalMenuList = null;
		totalMenuList = menuService.getMenuList(pMap);

		model.addAttribute( "totalMenuList", totalMenuList );
		model.addAttribute( "authorList", authorList );

		return "cubox/adminInfo/auth_management";
	}


	/**
	 * 권한별 메뉴 가져오기
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/adminfo/getAuthMenuInfo.do")
	public ModelAndView getMenuInfo(@RequestParam Map<String, Object> commandMap, HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		String authorId = StringUtil.isNullToString( commandMap.get("selAuthorId") );

		List<MenuDetailVO> totalMenuList = null; //메뉴리스트
		List<MenuDetailVO>  userMenuList = null; //사용자 권한별 메뉴리스트

		HashMap pMap = new HashMap();

		List<MenuDetailVO> menuList = null;
		menuList = menuService.getMenuList(pMap);

		if( commonUtils.notEmpty(authorId) ) {

			pMap.put("author_id", authorId);

			userMenuList = menuService.getUserMenuList(pMap);

			if( userMenuList != null && userMenuList.size() > 0 ){
				totalMenuList = menuService.getMenuList(pMap);

				for( int i=0; i < userMenuList.size(); i++ ){
					for( int j=0; j<totalMenuList.size(); j++ ){
						if(	(totalMenuList.get(j).getMenu_code()).equals(userMenuList.get(i).getMenu_code()) ){
							totalMenuList.remove(j);
						}
					}
				}

			} else {
				totalMenuList = menuList;
			}

			modelAndView.addObject( "totalMenuList", totalMenuList);
			modelAndView.addObject(  "userMenuList", userMenuList );

		} else {
			modelAndView.addObject( "totalMenuList", menuList );
		}

		return modelAndView;
	}

	/**
	 * 권한그룹추가저장
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/menuInfo/saveUserAuthMenuGroup.do")
	public ModelAndView saveUserAuthMenuGroup(@RequestParam Map<String, Object> commandMap, String[] menuArray,  HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");

    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

    	UserInfoVO userInfo = new UserInfoVO();
    	int addCnt = 0;

		String authorId= commandMap.get("authorId").toString();

		HashMap pMap = new HashMap();

		pMap.put("author_id", authorId);
		pMap.put("menuArray", menuArray);

		menuService.saveAuthMenuGroup(pMap);

		modelAndView.addObject("authorId", authorId);
		return modelAndView;
	}

}
