package aero.cubox.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aero.cubox.util.StringUtil;
import aero.cubox.user.service.impl.UserInfoDAO;
import aero.cubox.auth.service.AuthorGroupService;
import aero.cubox.auth.service.vo.AuthorGroupDetailVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.SyncDeviceVO;
import aero.cubox.user.service.vo.UserInfoVO;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("authorGroupService")
public class AuthorGroupServiceImpl extends EgovAbstractServiceImpl implements AuthorGroupService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorGroupServiceImpl.class);

	/** authorGroupDAO */
	@Resource(name = "authorGroupDAO")
	private AuthorGroupDAO authorGroupDAO;
	
	/** userInfoDAO */
	@Resource(name = "userInfoDAO")
	private UserInfoDAO userInfoDAO;	

	@Override
	public List<AuthorGroupVO> getAuthorGroupList(AuthorGroupVO vo) throws Exception {
		return authorGroupDAO.selectAuthorGroupList(vo);
	}

	@Override
	public int saveUserAuthGroup(AuthorGroupVO vo) throws Exception {
		int rst = 0;
		String [] strArray = vo.getGroupArray();		
		if(strArray != null && strArray.length > 0 ) {
			//ArrayList<String> addList = new ArrayList<String>();
			
			// 동기화  :: 삭제되는 권한 목록 조회
			//2021-01-20//List<SyncDeviceVO> delSyncDeviceList = authorGroupDAO.deleteSyncDeviceList(vo);
			
			/* 2021-04-14 기부여된 권한을 모두 delete 후 새로 insert 처리 
			// 사용자 권한 그룹 delete
			authorGroupDAO.deleteUserAuthGroup(vo);
			
			for(String str : strArray) {		
				if(str != null && !str.equals("")) {
					vo.setAuthorGroupId(str);
					int count = authorGroupDAO.totalUserAuthGroup (vo);	
					if(count < 1) {
						// 사용자 권한 그룹 insert
						authorGroupDAO.insertUserAuthGroup (vo);
						addList.add(str);
					} 
				}				
			}*/
			
			/* 2020-01-20 
			// 동기화  :: 추가되는 권한 목록 조회
			List<SyncDeviceVO> addSyncDeviceList = null;
			if(addList != null && addList.size() > 0) {
				vo.setGroupArray(addList.toArray(new String[addList.size()]));
				addSyncDeviceList = authorGroupDAO.addSyncDeviceList(vo);
			}
			if(addSyncDeviceList == null) addSyncDeviceList = new ArrayList<SyncDeviceVO>();
			if(delSyncDeviceList != null) addSyncDeviceList.addAll(delSyncDeviceList);
			
			for(SyncDeviceVO svo:addSyncDeviceList) {
				int syncCnt = authorGroupDAO.selectSyncDeviceCount(svo);
				if(syncCnt > 0) {
					// 사용자 권한 장비 동기화 tb insert sync_device			
					rst += authorGroupDAO.updateSyncDevice (svo);
				} else {
					rst += authorGroupDAO.insertSyncDevice (svo);
				}
			}*/			
			
			// 기존 사용자 그룹 log
			int cntLog = authorGroupDAO.insertUserAuthGroupLog(vo);			
			LOGGER.debug("###[사용자 권한그룹 변경-insertLog] cntLog:{}, vo:{}", cntLog, vo);
			
			// 사용자 권한 그룹 delete
			int cntDel = authorGroupDAO.deleteUserAuthGroupAll(vo);
			LOGGER.debug("###[사용자 권한그룹 변경-deleteAuth] cntDel:{}, vo:{}", cntDel, vo);

			// 사용자 권한 그룹 insert
			int cntAdd = authorGroupDAO.insertUserAuthGroupList(vo);
			LOGGER.debug("###[사용자 권한그룹 변경-insertAuthList] cntAdd:{}, vo:{}", cntAdd, vo);
			
			//2021-01-20 장비동기화 
			int cnt = authorGroupDAO.insertSyncDeviceByFuid(vo);
			LOGGER.debug("###[사용자 권한그룹 변경-SyncDevice] cnt:{}, vo:{}", cnt, vo);
			
			//2021-01-29 
			UserInfoVO uvo = new UserInfoVO();
			uvo.setFuid(vo.getFuid());
			uvo.setFpartcd1(vo.getSiteId());
			rst = userInfoDAO.userInfoSaveSync(uvo);
			LOGGER.debug("###[사용자 권한그룹 변경-usermain Q update] rst:{}, uvo:{}", rst, uvo);
		}
		return rst;
	}

	@Override
	public List<AuthorGroupVO> getUserAuthorGroupList(AuthorGroupVO vo) throws Exception {
		return authorGroupDAO.selectUserAuthorGroupList(vo);
	}

	@Override
	public List<AuthorGroupDetailVO> getAuthorGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		return authorGroupDAO.selectAuthorGroupDetail(vo);
	}

	/**
	 * 사용자(이미지 및 정보) 변경 시 SyncDevice 권한 등록
	 * @param AuthorGroupVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addSyncDeviceUpdateUser(AuthorGroupVO vo) throws Exception {
		int rst = 0;
		
		// 사용자 권한 조회
		List<SyncDeviceVO> userSyncDeviceList = null;
		
		List<Map<String, Object>> fuids = vo.getFuids();
		if(fuids != null && fuids.size() > 0) {
			//여러명의 사용자 동시 수정
			userSyncDeviceList = authorGroupDAO.selectUserListSyncDeviceList(vo);	
		} else {
			//한 건의 사용자 수정
			userSyncDeviceList = authorGroupDAO.selectUserSyncDeviceList(vo);	
		}	
		for(SyncDeviceVO svo:userSyncDeviceList) {
			int syncCnt = authorGroupDAO.selectSyncDeviceCount(svo);
			if(syncCnt > 0) {
				// 사용자 권한 장비 동기화 tb insert sync_device			
				rst += authorGroupDAO.updateSyncDevice (svo);
			} else {
				rst += authorGroupDAO.insertSyncDevice (svo);
			}
		}
		return rst;
	}

	/**
	 * 전체 장비 목록 조회
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	@Override
	public List<AuthorGroupDetailVO> getTotalDeviceList(AuthorGroupDetailVO vo) throws Exception {
		return authorGroupDAO.selectTotalDeviceList(vo);
	}

	/**
	 * 권한 그룹별 저장 된 장비 목록
	 * @param AuthorGroupDetailVO
	 * @return List<AuthorGroupDetailVO>
	 * @throws Exception
	 */
	@Override
	public List<AuthorGroupDetailVO> getAuthorDeviceList(AuthorGroupDetailVO vo) throws Exception {
		return authorGroupDAO.selectAuthorDeviceList(vo);
	}

	/**
	 * 권한그룹 가져오기
	 * @param AuthorGroupVO
	 * @return AuthorGroupVO
	 * @throws Exception
	 */
	@Override
	public AuthorGroupVO getAuthorGroup(AuthorGroupVO vo) throws Exception {
		return authorGroupDAO.selectAuthorGroup(vo);
	}

	/**
	 * 권한그룹 저장 및 수정
	 * @param AuthorGroupVO
	 * @return AuthorGroupVO
	 * @throws Exception
	 */
	@Override
	public int saveAuthorGroup(AuthorGroupVO vo) throws Exception {
		int rtn = 0;
		
		if(StringUtil.nvl(vo.getAuthorGroupId()).equals("")) {
			rtn = authorGroupDAO.insertAuthorGroup(vo);
		} else {
			rtn = authorGroupDAO.updateAuthorGroup(vo);
		}
		if(rtn < 1) {
			LOGGER.error("###[saveAuthorGroup] rtn:{}, vo:{}", rtn, vo);
			throw new RuntimeException("권한그룹 저장 중 오류가 발생했습니다.");
		}
		
		return rtn;		
	}

	/**
	 * 권한그룹 사용여부 수정
	 * @param AuthorGroupVO
	 * @return AuthorGroupVO
	 * @throws Exception
	 */
	@Override
	public int updateAuthorGroupUseYn(AuthorGroupVO vo) throws Exception {

		int rtn = authorGroupDAO.updateAuthorGroupUseYn(vo);
		LOGGER.debug("###[updateAuthorGroupUseYn] rtn:{}, vo:{}", rtn, vo);
		if(rtn < 1) {
			LOGGER.error("###[updateAuthorGroupUseYn] rtn:{}, vo:{}", rtn, vo);
			throw new RuntimeException("권한그룹 사용여부 수정 중 오류가 발생했습니다.");
		}
		
		// 2021-03-16 권한그룹 삭제시 적용단말기 사용안함 처리
		AuthorGroupDetailVO vo2 = new AuthorGroupDetailVO();
		vo2.setSiteId(vo.getSiteId());
		vo2.setAuthorGroupId(vo.getAuthorGroupId());
		vo2.setUseYn(vo.getUseYn());
		vo2.setModifyId(vo.getModifyId());
		int rtn2 = authorGroupDAO.updateAuthorGroupDetailUseYn(vo2);
		LOGGER.debug("###[updateAuthorGroupDetailUseYn] rtn2:{}, vo2:{}", rtn2, vo2);		

		String sAuthorGroupId = StringUtil.nvl(vo.getAuthorGroupId());
		AuthorGroupDetailVO syncVo = new AuthorGroupDetailVO();
		syncVo.setAuthorGroupId(sAuthorGroupId);
		syncVo.setSiteId(vo.getSiteId());  // 2021-03-16
		
		// 2021-01-20 사용안함 처리한 권한그룹을 소유한 사용자를 조회해서 권한 삭제 장비동기화 (sync_device)
		if(StringUtil.nvl(vo.getUseYn()).equals("N")) {
			// 1) 권한그룹을 소유한 사용자 목록 조회 
			List<AuthorGroupVO> userList = authorGroupDAO.selectUserListForAuthorGroup(syncVo);
			LOGGER.debug("###[권한그룹 사용안함 처리][{}] 삭제하는 권한그룹을 소유한 출입자목록 userList:{}", sAuthorGroupId, userList);
			
			if(userList != null) {
				for(AuthorGroupVO user : userList) {
					user.setAuthorGroupId(sAuthorGroupId);
					LOGGER.debug("###[권한그룹 사용안함 처리][{}] SyncDevice Start fuid:{}", sAuthorGroupId, user.getFuid());
					// 2) 권한그룹 log 
					authorGroupDAO.insertUserAuthGroupLog(user);
					// 3) 사용자의 권한그룹 삭제
					int a = authorGroupDAO.deleteUserAuthGroupInfo(user);
					if(a < 1) {
						LOGGER.error("###[권한그룹 사용안함 처리][{}] 사용자의 권한그룹 삭제 오류!! fuid:{}, a:{}", sAuthorGroupId, user.getFuid(), a);
						throw new RuntimeException("권한그룹 사용유무 수정 중 사용자("+user.getFuid()+") 권한그룹 삭제 오류가 발생했습니다.");
					}					
					// 4) 장비동기화
					int cnt = authorGroupDAO.insertSyncDeviceByFuid(user);
					LOGGER.debug("###[권한그룹 사용안함 처리][{}] SyncDevice End!! fuid:{}, cnt:{}", sAuthorGroupId, user.getFuid(), cnt);
				}
				//if(userList.size() != cnt) {
				//	throw new RuntimeException("권한 변경 오류 발생!!!"); 
				//}
			}
		}		
		
		return rtn;		
	}	
	
	/**
	 * 권한그룹 상세 저장 및 수정
	 * @param AuthorGroupDetailVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int saveAuthorGroupDetail(AuthorGroupDetailVO vo) throws Exception {
		int rst = 0;
		String [] strArray = vo.getDeviceIds();
		if(strArray != null && strArray.length > 0 ) {
			ArrayList<String> addList = new ArrayList<String>();
			
			// 동기화 :: 사용자 별 삭제되는 권한 조회
			//List<SyncDeviceVO> delSyncDeviceList = authorGroupDAO.deleteGroupSyncDeviceList(vo);
			
			// 권한 그룹 상세 삭제	
			int delrst = authorGroupDAO.deleteAuthGroupDetail(vo);
			LOGGER.debug("###[delete AuthorGroupDetail] delrst:{}, vo:{}", delrst, vo);
			
			int sort = 1;
			for(String str : strArray) {
				if(str != null && !str.equals("")) {
					vo.setFgid(str);
					vo.setSortOrdr(String.valueOf(sort++));
					int count = authorGroupDAO.countAuthGroupDetail (vo);
					if(count < 1) {
						// 사용자 권한 그룹 insert
						int addrst = authorGroupDAO.insertAuthGroupDetail (vo);
						addList.add(str);	//저장 한 그룹 device 추가
					} else {
						int uptrst = authorGroupDAO.updateAuthGroupDetail (vo);
					}
				}	
			}			
			
			/* 2021-01-20
			//사용자의 권한 동기화  :: syncDevice
			List<SyncDeviceVO> addSyncDeviceList = null;
			if(addList != null && addList.size() > 0) {
				vo.setDeviceIds(addList.toArray(new String[addList.size()]));
				addSyncDeviceList = authorGroupDAO.addGroupSyncDeviceList(vo);
			}
			if(addSyncDeviceList == null) addSyncDeviceList = new ArrayList<SyncDeviceVO>();
			if(delSyncDeviceList != null) addSyncDeviceList.addAll(delSyncDeviceList);
			
			//동기화 :: sync_device insert
			for(SyncDeviceVO svo:addSyncDeviceList) {
				int syncCnt = authorGroupDAO.selectSyncDeviceCount(svo);
				if(syncCnt > 0) {
					// 사용자 권한 장비 동기화 tb insert sync_device			
					rst += authorGroupDAO.updateSyncDevice (svo);
				} else {
					rst += authorGroupDAO.insertSyncDevice (svo);
				}
			}*/
			
			// 2021-01-20 권한그룹을 소유한 사용자를 조회해서 장비동기화 (sync_device)
			// 1) 권한그룹을 소유한 사용자 목록 조회
			List<AuthorGroupVO> userList = authorGroupDAO.selectUserListForAuthorGroup(vo);
			if(userList != null) {
				for(AuthorGroupVO user : userList) {
					// 2) 장비동기화
					int cnt = authorGroupDAO.insertSyncDeviceByFuid(user);
					LOGGER.debug("###[권한그룹 변경-SyncDevice] cnt:{}, user:{}", cnt, user);
				}
				//if(userList.size() != cnt) {
				//	throw new RuntimeException("권한 변경 오류 발생!!!"); 
				//}
			}
		}
		rst = 1;
		return rst;
	}
	
	/**
	 * 권한그룹별 출입자 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserListByAuth(Map<String, Object> param) throws Exception {
		return authorGroupDAO.selectUserListByAuth(param);
	}	
	
	public List<ExcelVO> getUserListByAuthExcel(Map<String, Object> param) throws Exception {
		return authorGroupDAO.selectUserListByAuthExcel(param);
	}
	
	/**
	 * 권한그룹별 단말기 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getGateListByAuth(Map<String, Object> param) throws Exception {
		return authorGroupDAO.selectGateListByAuth(param);
	}	
	
	public List<ExcelVO> getGateListByAuthExcel(Map<String, Object> param) throws Exception {
		return authorGroupDAO.selectGateListByAuthExcel(param);
	}		
}
