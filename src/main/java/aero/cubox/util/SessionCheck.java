package aero.cubox.util;

import aero.cubox.auth.service.vo.AuthorVO;
import aero.cubox.menu.service.MenuService;
import aero.cubox.menu.vo.MenuClVO;
import aero.cubox.menu.vo.MenuDetailVO;
import aero.cubox.sample.service.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionCheck extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionCheck.class);

	AuthorManager authorManager = AuthorManager.getInstance();

	@Resource
    private MenuService menuService;


	/**
	 * 세션정보 체크
	 *
	 * @param request
	 * @return
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		//로그인없이 볼수 있는 페이지
		ArrayList<String> freeAccessUrls = new ArrayList<String>();

		//로그인
		freeAccessUrls.add("/login.do");
		freeAccessUrls.add("/common/loginProc.do");

		//모바일 연계
		freeAccessUrls.add("/service/faceRegist.do");
		freeAccessUrls.add("/service/faceView.do");
		freeAccessUrls.add("/service/gateStatus.do");
		freeAccessUrls.add("/service/excelUpload.do");
		
		//외부에서 출입자 등록
		freeAccessUrls.add("/service/form.do");
		freeAccessUrls.add("/service/regUser.do");
		freeAccessUrls.add("/service/addUser.do");
		freeAccessUrls.add("/service/delUser.do");
		
		//방문신청
		freeAccessUrls.add("/visitInfo/visitorPopup.do");
		freeAccessUrls.add("/visitInfo/visitorSelfPopup.do");
		freeAccessUrls.add("/visitInfo/visitorMyListPopup.do");
		freeAccessUrls.add("/visitInfo/addVisitor.do");
		freeAccessUrls.add("/visitInfo/modVisitorSelf.do");
		freeAccessUrls.add("/visitInfo/delVisitorSelf.do");
		freeAccessUrls.add("/visitInfo/userListPopup.do");

		//권한관계 없이 볼수 있는 페이지
		ArrayList<String> defaultAccessUrls = new ArrayList<String>();
		defaultAccessUrls.add("/main.do");

		String uri = request.getServletPath();

		//LOGGER.debug("uri >>>> "+uri);

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		//로그인
		if (loginVO != null && loginVO.getFsiteid() != null && !loginVO.getFsiteid().equals("")) {
			//권한 확인
			if(!authorManager.is()) setAuthorInfo();

			//공통화면 체크
			if(defaultAccessUrls.contains(uri)) { 	//index.do
				request.getSession().setAttribute("uriPath", uri);
				return true;
			} else {
				if(uri != null && (uri.endsWith("login.do") || uri.endsWith("index.do"))) {
					//String strUrlPath = authorManager.getMainRedirect(loginVO.getAuthor_id());
					//request.getSession().setAttribute("uriPath", strUrlPath);
					//response.sendRedirect(strUrlPath);
					request.getSession().setAttribute("uriPath", "/main.do");
					response.sendRedirect("/main.do");
					return false;
				} else {
					request.getSession().setAttribute("uriPath", uri);
					return true;
				}
			}
		}else{
			if(freeAccessUrls.contains(uri)) {
				return true;
			} else if (uri.contains("Popup")) {
				PrintWriter writer = response.getWriter();
				writer.println("<script>parent.location.href='/login.do';</script>");	//Popup 부모창 로그인 이동
				return false;
			} else {
				response.sendRedirect("/login.do");
				return false;
			}
		}
	}

	public static String getUserIp() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public void setAuthorInfo() throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("use_yn", "Y");
    	List<AuthorVO> authorList = menuService.getAuthorList(map);
    	for(AuthorVO avo : authorList) {
    		LOGGER.debug("result >>>> "+avo.getAuthorId());
    		String authorId = avo.getAuthorId();

    		//권한별 대메뉴 정보
        	HashMap<String, Object> sMap = new HashMap<String, Object>();
        	sMap.put("author_id", authorId);
        	List<MenuClVO> urlList = menuService.getAuthorMenuCl(sMap);
        	List<MenuClVO> chkClList = new ArrayList<MenuClVO>();

        	//권한별 상세 url 전체 정보
        	sMap.put("menu_cl_code", "");
        	List<MenuDetailVO> menuDetailList = menuService.getAuthMenuList(sMap);
	    	authorManager.setDetailMenu(authorId, menuDetailList);

    		for(MenuClVO vo : urlList) {
    			String strClCode = vo.getMenu_cl_code();
    			//권한별 sub menu 정보
    			sMap.put("menu_cl_code", strClCode);
    			List<MenuDetailVO> menuList = menuService.getAuthMenuList(sMap);
    	    	//대메뉴 정보
    	    	vo.setList(menuList);
    	    	chkClList.add(vo);
    		}
    		authorManager.setMenuCl(authorId, chkClList);
    	}
		authorManager.complete();
	}
}
