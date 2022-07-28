package aero.cubox.util;

import java.util.*;

import aero.cubox.menu.vo.MenuClVO;
import aero.cubox.menu.vo.MenuDetailVO;

public class AuthorManager{

	private static AuthorManager authorManager = null;
	private static LinkedHashMap<String, List<MenuDetailVO>> MENU_INFO = new LinkedHashMap<String, List<MenuDetailVO>>();
	private static LinkedHashMap<String, List<MenuClVO>> MENU_CL_INFO = new LinkedHashMap<String, List<MenuClVO>>();
	private static LinkedHashMap<String, List<MenuDetailVO>> MENU_DETAIL_INFO = new LinkedHashMap<String, List<MenuDetailVO>>();
	private static boolean AUTHOR_AT = false;

	public static synchronized AuthorManager getInstance(){
		if(authorManager == null){
			authorManager = new AuthorManager();
		}
		return authorManager;
	}

	public synchronized List<MenuClVO> getMenuCl(String author){
		return MENU_CL_INFO.get(author);
	}

	public synchronized void setMenuCl(String author, List<MenuClVO> vo){
		MENU_CL_INFO.put(author, vo);
	}


	public synchronized List<MenuDetailVO> getMenu(String author, String menuClCode){
		return MENU_INFO.get(author+"_"+menuClCode);
	}

	public synchronized void setMenu(String author, String menuClCode, List<MenuDetailVO> menuList){
		MENU_INFO.put(author+"_"+menuClCode, menuList);
	}

	public synchronized void complete() {
		AUTHOR_AT = true;
	}

	public synchronized boolean is() {
		return AUTHOR_AT;
	}

	public synchronized void clear() {
		MENU_INFO = new LinkedHashMap<String, List<MenuDetailVO>>();
		MENU_CL_INFO = new LinkedHashMap<String, List<MenuClVO>>();
		AUTHOR_AT = false;
	}

	public void setDetailMenu(String authorId, List<MenuDetailVO> menuList) {
		MENU_DETAIL_INFO.put(authorId, menuList);
	}

	public List<MenuDetailVO> getDetailMenu(String authorId) {
		return MENU_DETAIL_INFO.get(authorId);
	}

	public String getMainRedirect(String author) {
		String strMain = "/userInfo/userMngmt.do";
		String returnUrl = "";
		int i = 0;
		List<MenuDetailVO> dlist = getDetailMenu (author);
		if(dlist != null && dlist.size() > 0) {
			for(MenuDetailVO mv : dlist) {
				if(mv.getMenu_url()!=null) {
					if(mv.getMenu_url().endsWith(strMain)) {
						returnUrl = strMain;
						break;
					}
					if(i==0) {
						returnUrl = mv.getMenu_url();
						i++;
					}
				}
			}
		}
		if(returnUrl == null || returnUrl.trim().equals("")) {
			returnUrl = "/login.do";
		}
		return returnUrl;
	}
}
