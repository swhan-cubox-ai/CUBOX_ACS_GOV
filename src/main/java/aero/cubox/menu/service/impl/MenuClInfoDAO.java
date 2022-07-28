package aero.cubox.menu.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("menuClInfoDAO")
public class MenuClInfoDAO extends EgovAbstractMapper {
	
	/**
	 * 메뉴 분류 코드 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectMenuClList(Map<String, Object> param) throws Exception {
		return selectList("menuClInfo.selectMenuClList", param);
	}
	
	/**
	 * 메뉴 분류 코드 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectMenuClInfo(Map<String, Object> param) throws Exception {
		return selectOne("menuClInfo.selectMenuClInfo", param);
	}
	
	/**
	 * 신규 메뉴분류 코드 생성
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String selectNewMenuClCode() throws Exception {
		return selectOne("menuClInfo.selectNewMenuClCode");
	}
	
	/**
	 * 메뉴 분류 코드 등록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertMenuClInfo(Map<String, Object> param) throws Exception {
		return insert("menuClInfo.insertMenuClInfo", param);
	}
	
	/**
	 * 메뉴 분류 코드 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuClInfo(Map<String, Object> param) throws Exception {
		return update("menuClInfo.updateMenuClInfo", param);
	}
	
	/**
	 * 메뉴 분류 코드 사용여부 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuClUseYn(Map<String, Object> param) throws Exception {
		return update("menuClInfo.updateMenuClUseYn", param);
	}
	
	/**
	 * 메뉴 분류 코드 사용안함 처리시 메뉴도 동시에 사용안함 처리
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuDetailListUseYn(Map<String, Object> param) throws Exception {
		return update("menuClInfo.updateMenuDetailListUseYn", param);
	}
	
	/**
	 * 메뉴 코드 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectMenuDetailList(Map<String, Object> param) throws Exception {
		return selectList("menuClInfo.selectMenuDetailList", param);
	}
	
	/**
	 * 메뉴 코드 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectMenuDetailListCount(Map<String, Object> param) throws Exception {
		return selectOne("menuClInfo.selectMenuDetailListCount", param);
	}	
	
	/**
	 * 메뉴 코드 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectMenuDetailInfo(Map<String, Object> param) throws Exception {
		return selectOne("menuClInfo.selectMenuDetailInfo", param);
	}
	
	/**
	 * 신규 메뉴코드 생성
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String selectNewMenuCode() throws Exception {
		return selectOne("menuClInfo.selectNewMenuCode");
	}
	
	/**
	 * 메뉴 코드 등록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertMenuDetailInfo(Map<String, Object> param) throws Exception {
		return insert("menuClInfo.insertMenuDetailInfo", param);
	}
	
	/**
	 * 메뉴 코드 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuDetailInfo(Map<String, Object> param) throws Exception {
		return update("menuClInfo.updateMenuDetailInfo", param);
	}
	
	/**
	 * 메뉴 코드 사용여부 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuDetailUseYn(Map<String, Object> param) throws Exception {
		return update("menuClInfo.updateMenuDetailUseYn", param);
	}
	
	/**
	 * 메뉴 명 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String selectMenuNm(String str) throws Exception {
		return selectOne("menuClInfo.selectMenuNm", str);
	}
}
