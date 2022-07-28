package aero.cubox.menu.service;

import java.util.List;
import java.util.Map;

public interface MenuClInfoService {
	
	/**
	 * 메뉴 분류 코드 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectMenuClList(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 분류 코드 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectMenuClInfo(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 분류 코드 등록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertMenuClInfo(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 분류 코드 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuClInfo(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 분류 코드 사용여부 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuClUseYn(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 코드 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectMenuDetailList(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 코드 목록 전체건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectMenuDetailListCount(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 코드 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectMenuDetailInfo(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 코드 등록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertMenuDetailInfo(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 코드 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuDetailInfo(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 코드 사용여부 수정
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int updateMenuDetailUseYn(Map<String, Object> param) throws Exception;
	
	/**
	 * 메뉴 명 조회
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public String selectMenuNm(String str) throws Exception;
}
