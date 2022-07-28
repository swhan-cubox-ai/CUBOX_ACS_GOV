package aero.cubox.auth.service;

import java.util.List;
import java.util.Map;

import aero.cubox.auth.service.vo.AuthorGroupDetailVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.ExcelVO;

public interface AuthorGroupService {

	/**
	 * 장비 권한 그룹 리스트 가져오기
	 * @param AuthorGroupVO
	 * @return List<AuthorGroupVO>
	 * @throws Exception
	 */
	public List<AuthorGroupVO> getAuthorGroupList(AuthorGroupVO vo) throws Exception;

	/**
	 * 사용자 권한 그룹 저장
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int saveUserAuthGroup(AuthorGroupVO vo) throws Exception;

	/**
	 * 사용자 권한 그룹 조회
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public List<AuthorGroupVO> getUserAuthorGroupList(AuthorGroupVO vo) throws Exception;

	/**
	 * 권한 그룹 상세 조회
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	public List<AuthorGroupDetailVO> getAuthorGroupDetail(AuthorGroupDetailVO vo) throws Exception;

	/**
	 * 사용자(이미지 및 정보) 변경 시 SyncDevice 권한 등록
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int addSyncDeviceUpdateUser(AuthorGroupVO avo) throws Exception;

	/**
	 * 전체 장비 목록 조회
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	public List<AuthorGroupDetailVO> getTotalDeviceList(AuthorGroupDetailVO vo) throws Exception;

	/**
	 * 권한 그룹별 저장 된 장비 목록
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	public List<AuthorGroupDetailVO> getAuthorDeviceList(AuthorGroupDetailVO vo) throws Exception;

	/**
	 * 권한그룹 가져오기
	 * @param AuthorGroupVO
	 * @return AuthorGroupVO
	 * @throws Exception
	 */
	public AuthorGroupVO getAuthorGroup(AuthorGroupVO vo) throws Exception;

	/**
	 * 권한그룹 저장 및 수정
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int saveAuthorGroup(AuthorGroupVO vo) throws Exception;
	
	/**
	 * 권한그룹 사용/사용안함 처리
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int updateAuthorGroupUseYn(AuthorGroupVO vo) throws Exception;

	/**
	 * 권한그룹 상세 저장 및 수정
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public int saveAuthorGroupDetail(AuthorGroupDetailVO vo) throws Exception;
	
	/**
	 * 권한그룹별 출입자 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserListByAuth(Map<String, Object> param) throws Exception;
	
	public List<ExcelVO> getUserListByAuthExcel(Map<String, Object> param) throws Exception;
	
	/**
	 * 권한그룹별 단말기 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getGateListByAuth(Map<String, Object> param) throws Exception;
	
	public List<ExcelVO> getGateListByAuthExcel(Map<String, Object> param) throws Exception;	

}
