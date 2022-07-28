package aero.cubox.menu.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import aero.cubox.util.StringUtil;
import aero.cubox.menu.service.MenuClInfoService;

@Service("menuClInfoService")
public class MenuClInfoServiceImpl implements MenuClInfoService {
	
	@Resource(name = "menuClInfoDAO")
	private MenuClInfoDAO menuClInfoDAO;

	@Override
	public List<Map<String, Object>> selectMenuClList(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.selectMenuClList(null);
	}

	@Override
	public Map<String, Object> selectMenuClInfo(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.selectMenuClInfo(param);
	}

	@Override
	public int insertMenuClInfo(Map<String, Object> param) throws Exception {
		param.put("menu_cl_code", menuClInfoDAO.selectNewMenuClCode());
		return menuClInfoDAO.insertMenuClInfo(param);
	}

	@Override
	public int updateMenuClInfo(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.updateMenuClInfo(param);
	}

	@Override
	public int updateMenuClUseYn(Map<String, Object> param) throws Exception {
		if(StringUtil.nvl(param.get("use_yn")).equals("N")) {
			menuClInfoDAO.updateMenuDetailListUseYn(param);
		}
		return menuClInfoDAO.updateMenuClUseYn(param);
	}

	@Override
	public List<Map<String, Object>> selectMenuDetailList(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.selectMenuDetailList(param);
	}
	
	@Override
	public int selectMenuDetailListCount(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.selectMenuDetailListCount(param);
	}

	@Override
	public Map<String, Object> selectMenuDetailInfo(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.selectMenuDetailInfo(param);
	}

	@Override
	public int insertMenuDetailInfo(Map<String, Object> param) throws Exception {
		param.put("menu_code", menuClInfoDAO.selectNewMenuCode());
		return menuClInfoDAO.insertMenuDetailInfo(param);
	}

	@Override
	public int updateMenuDetailInfo(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.updateMenuDetailInfo(param);
	}

	@Override
	public int updateMenuDetailUseYn(Map<String, Object> param) throws Exception {
		return menuClInfoDAO.updateMenuDetailUseYn(param);
	}

	@Override
	public String selectMenuNm(String str) throws Exception {
		return menuClInfoDAO.selectMenuNm(str);
	}

}
