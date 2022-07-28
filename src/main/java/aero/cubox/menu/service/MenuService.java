package aero.cubox.menu.service;

import java.util.HashMap;
import java.util.List;

import aero.cubox.auth.service.vo.AuthorVO;
import aero.cubox.menu.vo.MenuClVO;
import aero.cubox.menu.vo.MenuDetailVO;

public interface MenuService {
	public List<MenuClVO> getAuthorMenuCl(HashMap<String, Object> map) throws Exception;
	public List<AuthorVO> getAuthorList(HashMap<String, Object> map) throws Exception;
	public List<MenuDetailVO> getAuthMenuList(HashMap<String, Object> map) throws Exception;
	public List<MenuDetailVO> getMenuList(HashMap<String, Object> map) throws Exception;
	public List<MenuDetailVO> getTotalMenuList(HashMap map) throws Exception;
	public List<MenuDetailVO> getUserMenuList(HashMap map) throws Exception;
	public void saveAuthMenuGroup(HashMap pMap) throws Exception;
}
