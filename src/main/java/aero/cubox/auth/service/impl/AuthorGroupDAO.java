package aero.cubox.auth.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aero.cubox.auth.service.vo.AuthorGroupDetailVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.SyncDeviceVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;


@Repository("authorGroupDAO")
public class AuthorGroupDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorGroupDAO.class);

	private static final String sqlNameSpace = "authorGroup.";


	/**
	 * 장비 권한 리스트 가져오기
	 * @param AuthorGroupVO
	 * @return List<AuthorGroupVO>
	 * @throws Exception
	 */
	public List<AuthorGroupVO> selectAuthorGroupList(AuthorGroupVO vo) throws Exception {
		 return selectList(sqlNameSpace+"selectAuthorGroupList", vo);
	}


	/**
	 * 사용자 권한 로그 저장
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int insertUserAuthGroupLog(AuthorGroupVO vo) throws Exception {
		return insert(sqlNameSpace+"insertUserAuthGroupLog", vo);		
	}

	/**
	 * 사용자 권한그룹 삭제
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int deleteUserAuthGroupAll(AuthorGroupVO vo) throws Exception {
		return delete(sqlNameSpace+"deleteUserAuthGroupAll", vo);		
	}

	/**
	 * 사용자 권한 그룹 삭제
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int deleteUserAuthGroup(AuthorGroupVO vo) throws Exception {
		return delete(sqlNameSpace+"deleteUserAuthGroup", vo);		
	}

	/**
	 * 사용자 권한 그룹 삭제
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int deleteUserAuthGroupInfo(AuthorGroupVO vo) throws Exception {
		return delete(sqlNameSpace+"deleteUserAuthGroupInfo", vo);		
	}	
	

	/**
	 * 사용자 권한 그룹 count
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int totalUserAuthGroup(AuthorGroupVO vo) throws Exception {
		return selectOne(sqlNameSpace+"totalUserAuthGroup", vo);
	}


	/**
	 * 사용자 권한 그룹 등록
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int insertUserAuthGroup(AuthorGroupVO vo) throws Exception {
		return insert(sqlNameSpace+"insertUserAuthGroup", vo);	
	}
	
	/**
	 * 사용자 권한 그룹 등록
	 * @param AuthorGroupVO
	 * @return
	 * @throws Exception
	 */
	public int insertUserAuthGroupList(AuthorGroupVO vo) throws Exception {
		return insert(sqlNameSpace+"insertUserAuthGroupList", vo);	
	}

	/**
	 * 동기화  :: 삭제되는 권한 목록 조회
	 * @param AuthorGroupVO
	 * @return List<SyncDeviceVO>
	 * @throws Exception
	 */
	public List<SyncDeviceVO> deleteSyncDeviceList(AuthorGroupVO vo) throws Exception  {
		return selectList(sqlNameSpace+"deleteSyncDeviceList", vo);
	}

	/**
	 * 동기화  :: 추가 권한 목록 조회
	 * @param AuthorGroupVO
	 * @return List<SyncDeviceVO>
	 * @throws Exception
	 */
	public List<SyncDeviceVO> addSyncDeviceList(AuthorGroupVO vo) throws Exception  {
		return selectList(sqlNameSpace+"addSyncDeviceList", vo);
	}

	/**
	 * 동기화  :: 권한 유무 확인
	 * @param SyncDeviceVO
	 * @return int
	 * @throws Exception
	 */
	public int selectSyncDeviceCount(SyncDeviceVO vo) throws Exception {
		return selectOne(sqlNameSpace+"selectSyncDeviceCount", vo);
	}
	
	/**
	 * 동기화  :: 사용자 권한 장비 동기화 테이블 등록 
	 * @param SyncDeviceVO
	 * @return int
	 * @throws Exception
	 */
	public int insertSyncDevice(SyncDeviceVO vo) throws Exception {
		return insert(sqlNameSpace+"insertSyncDevice", vo);	
	}
	
	/**
	 * 동기화  :: 사용자 권한 장비 동기화 테이블 수정 
	 * @param SyncDeviceVO
	 * @return int
	 * @throws Exception
	 */
	public int updateSyncDevice(SyncDeviceVO vo) throws Exception {
		return update(sqlNameSpace+"updateSyncDevice", vo);	
	}

	/**
	 * 2021-01-20
	 * 동기화 :: 권한 그룹 변경시 해당 권한 그룹을 소유한 사용자 목록 조회 
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public List<AuthorGroupVO> selectUserListForAuthorGroup(AuthorGroupDetailVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectUserListForAuthorGroup", vo);
	}	
	
	/**
	 * 2021-01-20
	 * 동기화 :: 사용자 권한 변경시 장비 동기화 처리 
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int insertSyncDeviceByFuid(AuthorGroupVO vo) throws Exception {
		return insert(sqlNameSpace+"insertSyncDeviceByFuid", vo);	
	}	
	
	public List<AuthorGroupVO> selectUserAuthorGroupList(AuthorGroupVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectUserAuthorGroupList", vo);
	}


	public List<AuthorGroupDetailVO> selectAuthorGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectAuthorGroupDetail", vo);
	}


	/**
	 * 사용자(이미지 및 정보) 변경 시 사용자 별 SyncDevice 등록 할 권한 조회
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public List<SyncDeviceVO> selectUserSyncDeviceList(AuthorGroupVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectUserSyncDeviceList", vo);
	}


	/**
	 * 여러명의 사용자(정보) 동시 변경 시 사용자 별 SyncDevice 등록 할 권한 조회
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public List<SyncDeviceVO> selectUserListSyncDeviceList(AuthorGroupVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectUserListSyncDeviceList", vo);
	}

	/**
	 * 전체 장비 목록 조회
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	public List<AuthorGroupDetailVO> selectTotalDeviceList(AuthorGroupDetailVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectTotalDeviceList", vo);
	}

	/**
	 * 권한 그룹별 저장 된 장비 목록
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	public List<AuthorGroupDetailVO> selectAuthorDeviceList(AuthorGroupDetailVO vo) throws Exception {
		return selectList(sqlNameSpace+"selectAuthorDeviceList", vo);
	}

	/**
	 * 권한그룹 가져오기
	 * @param AuthorGroupVO
	 * @return AuthorGroupVO
	 * @throws Exception
	 */
	public AuthorGroupVO selectAuthorGroup(AuthorGroupVO vo) throws Exception {
		return selectOne(sqlNameSpace+"selectAuthorGroup", vo);
	}

	/**
	 * 권한그룹 신규등록
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int insertAuthorGroup(AuthorGroupVO vo) throws Exception {
		return insert (sqlNameSpace+"insertAuthorGroup", vo);
	}

	/**
	 * 권한그룹 수정
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	public int updateAuthorGroup(AuthorGroupVO vo) throws Exception {
		update (sqlNameSpace+"insertAuthorGroupLog", vo);
		return update (sqlNameSpace+"updateAuthorGroup", vo);
	}
	
	public int updateAuthorGroupUseYn(AuthorGroupVO vo) throws Exception {
		update (sqlNameSpace+"insertAuthorGroupLog", vo);
		return update (sqlNameSpace+"updateAuthorGroupUseYn", vo);
	}
	
	public int updateAuthorGroupDetailUseYn(AuthorGroupDetailVO vo) throws Exception {
		update (sqlNameSpace+"insertAuthorGroupDetailLogForDelete", vo);
		return update (sqlNameSpace+"updateAuthorGroupDetailUseYn", vo);
	}	

	/**
	 * 동기화 :: 권한 그룹 상세 삭제 목록 조회
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public List<SyncDeviceVO> deleteGroupSyncDeviceList(AuthorGroupDetailVO vo) throws Exception {
		return selectList(sqlNameSpace+"deleteGroupSyncDeviceList", vo);
	}

	/**
	 * 권한 그룹 상세 삭제
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public int deleteAuthGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		insert(sqlNameSpace+"insertAuthorGroupDetailLogForDelete", vo);
		return delete (sqlNameSpace+"deleteAuthGroupDetail", vo);
	}

	/**
	 * 권한그룹 상세 count
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public int countAuthGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		return selectOne(sqlNameSpace+"countAuthGroupDetail", vo);
	}

	/**
	 * 권한그룹 상세 등록
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public int insertAuthGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		return insert (sqlNameSpace+"insertAuthGroupDetail", vo);
	}

	/**
	 * 권한그룹 상세 수정
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public int updateAuthGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		insert(sqlNameSpace+"insertAuthorGroupDetailLogForUpdate", vo);
		return update (sqlNameSpace+"updateAuthGroupDetail", vo);
	}

	/**
	 * 동기화 :: 권한 그룹 상세 추가 목록 조회
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public List<SyncDeviceVO> addGroupSyncDeviceList(AuthorGroupDetailVO vo) throws Exception {
		return selectList(sqlNameSpace+"addGroupSyncDeviceList", vo);
	}

	/**
	 * 동기화 체크 :: 권한 그룹 사용여부 변경 체크
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	public String changeAuthorGroupUseAt(AuthorGroupVO vo) throws Exception {
		return selectOne(sqlNameSpace+"changeAuthorGroupUseAt", vo);
	}
	
	/**
	 * 출입자 사용안함 처리 - 사용자권한삭제
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int deleteUserAuthGroupInfoNotUsed(HashMap map) throws Exception {
		return delete (sqlNameSpace+"deleteUserAuthGroupInfoNotUsed", map);
	}	
	
	/**
	 * 출입자 사용안함 처리 - 장비동기화
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int deleteSyncDeviceNotUsed(HashMap map) throws Exception {
		return update (sqlNameSpace+"deleteSyncDeviceNotUsed", map);
	}
	
	/**
	 * 권한그룹별 출입자 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectUserListByAuth(Map<String, Object> map) throws Exception {
		return selectList(sqlNameSpace+"selectUserListByAuth", map);
	}
	
	/**
	 * 권한그룹별 출입자 목록 조회
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> selectUserListByAuthExcel(Map<String, Object> map) throws Exception {
		return selectList(sqlNameSpace+"selectUserListByAuthExcel", map);
	}	
	
	/**
	 * 권한그룹별 단말기 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectGateListByAuth(Map<String, Object> map) throws Exception {
		return selectList(sqlNameSpace+"selectGateListByAuth", map);
	}
	
	/**
	 * 권한그룹별 단말기 목록 조회
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> selectGateListByAuthExcel(Map<String, Object> map) throws Exception {
		return selectList(sqlNameSpace+"selectGateListByAuthExcel", map);
	}
	
	/**
	 * 출입자의 권한그룹을 변경한지 여부 확인 0:변경안함, 0보다 크면 변경함.
	 * @param authorGroupVO
	 * @return
	 * @throws Exception
	 */
	public int selectUserAuthGroupForChange(AuthorGroupVO authorGroupVO)  throws Exception {
		return (Integer)selectOne(sqlNameSpace+"selectUserAuthGroupForChange", authorGroupVO);
	}
}
