package aero.cubox.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aero.cubox.auth.service.vo.AuthGroupVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.sample.service.vo.DownloadLogVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import org.springframework.stereotype.Repository;

import aero.cubox.util.StringUtil;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("userInfoDAO")
public class UserInfoDAO extends EgovAbstractMapper {

	private static final String sqlNameSpace = "userInfo.";

	public List<UserInfoVO> selectUserList(UserInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"selectUserList", vo);
    }
	
	public int selectUserListCount(UserInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"selectUserListCount", vo);
    }
	
	public List<ExcelVO> selectUserListExcel(UserInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"selectUserListExcel", vo);
    }
	
	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	/*public List<UserInfoVO> getUserInfoList(UserInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"getUserInfoList2", vo);
    }*/

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> getUserInfoList3(UserInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"getUserInfoList3", vo);
    }

	/**
	 * 사용자전체cnt
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int getUserTotalCnt3(UserInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"getUserTotalCnt3", vo);
    }

	/**
	 * 사용자정보가져오기
	 * @param String
	 * @return UserInfoVO
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo(String fuid) throws Exception {
        return selectOne(sqlNameSpace+"getUserInfo", fuid);
    }

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return UserInfoVO
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo2(UserInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"getUserInfo2", vo);
    }

	/**
	 * DB이미지가져오기
	 * @param String
	 * @return UserBioInfoVO
	 * @throws Exception
	 */
	public UserBioInfoVO getUserBioInfo(String fuid) throws Exception {
        return selectOne(sqlNameSpace+"getUserBioInfo", fuid);
    }

	/**
	 * 카드번호 가져오기
	 * @param String
	 * @return List<CardInfoVO>
	 * @throws Exception
	 */
	public List<CardInfoVO> getCardInfoList(String fuid) throws Exception{
		return selectList(sqlNameSpace+"getCardInfoList", fuid);
	}

	/**
	 * 유저권한그룹 가져오기(대전)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getUserAuthGroupList11(AuthGroupVO vo) throws Exception{
		return selectList(sqlNameSpace+"getUserAuthGroupList11", vo);
	}

	/**
	 * 유저권한그룹 가져오기(광주)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getUserAuthGroupList12(AuthGroupVO vo) throws Exception{
		return selectList(sqlNameSpace+"getUserAuthGroupList12", vo);
	}

	/**
	 * 전체권한그룹 가져오기(대전)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getTotalAuthGroupList11(AuthGroupVO vo) throws Exception{
		return selectList(sqlNameSpace+"getTotalAuthGroupList11", vo);
	}

	/**
	 * 전체권한그룹 가져오기(광주)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getTotalAuthGroupList12(AuthGroupVO vo) throws Exception{
		return selectList(sqlNameSpace+"getTotalAuthGroupList12", vo);
	}

	/**
	 * 카드정보가져오기
	 * @param String
	 * @return CardInfoVO
	 * @throws Exception
	 */
	public CardInfoVO getCardInfo(CardInfoVO vo) throws Exception{
		return selectOne(sqlNameSpace+"getCardInfo", vo);
	}

	/**
	 * 카드정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int cardInfoSave(CardInfoVO vo) throws Exception{
		return update(sqlNameSpace+"cardInfoSave", vo);
	}

	/**
	 * 카드중복체크
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getCdnoCnt(CardInfoVO vo) throws Exception{
		return selectOne(sqlNameSpace+"getCdnoCnt", vo);
	}

	/**
	 * 카드추가
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int addUserCdno(CardInfoVO vo) throws Exception{
		return insert(sqlNameSpace+"addUserCdno", vo);
	}

	/**
	 * 유저fsstatus변경
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userFsstatusChange(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"userFsstatusChange", vo);
	}

	/**
	 * tauthtogate_main테이블 fsstatus변경
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int tauthtogateMainFsstatusChange(CardInfoVO vo) throws Exception{
		return update(sqlNameSpace+"tauthtogateMainFsstatusChange", vo);
	}

	/**
	 * 사용자정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoSave(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"userInfoSave", vo);
	}

	/**
	 * 사용자의 그룹권한 수정 후 사용자 상태(Q) update
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int userInfoSaveSync(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"userInfoSaveSync", vo);
	}
	
	/**
	 * 출입자 개인정보 수정
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int updateUserInfo(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"updateUserInfo", vo);
	}
	
	/**
	 * 사용자정보-전화번호 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoPhoneUpdate(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"userInfoPhoneUpdate", vo);
	}

	/**
	 * 사용자정보-차번호 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoCarnoUpdate(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"userInfoCarnoUpdate", vo);
	}

	/**
	 * 사용자정보- tcmlinfo - fvname = 'nirscar11'  수정 / 광주는 주차없음
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoCarnoCmlUpdate() throws Exception{
		return update(sqlNameSpace+"userInfoCarnoCmlUpdate");
	}

	/**
	 * 사용자정보-비고수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoEtcUpdate(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"userInfoEtcUpdate", vo);
	}

	/**
	 * 권한그룹코드 가져오기(ftid)
	 * @param
	 * @return String
	 * @throws Exception
	 */
	public String getAuthGroupFtid(String authGroupNm) throws Exception{
		return selectOne(sqlNameSpace+"getAuthGroupFtid", authGroupNm);
	}
	
	public String getNewFuid() throws Exception {
		return selectOne(sqlNameSpace+"selectNewFuid");
	}

	/**
	 * 신규 사용자 정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int newUserInfoSave(UserInfoVO vo) throws Exception{
		return insert(sqlNameSpace+"newUserInfoSave", vo);
	}
	
	/**
	 * 출입자 권한그룹 적용
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int insertUserAuthGroup(AuthorGroupVO vo) throws Exception{
		return insert(sqlNameSpace+"insertUserAuthGroup", vo);
	}	
	
	/**
	 * 임직원 기본 권한그룹 적용
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int insertUserAuthGroupForNewEmp(AuthorGroupVO vo) throws Exception{
		return insert(sqlNameSpace+"insertUserAuthGroupForNewEmp", vo);
	}	

	/**
	 * fuid, funm 중복체크
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int getFuidFunmCnt(UserInfoVO vo) throws Exception{
		return selectOne(sqlNameSpace+"getFuidFunmCnt", vo);
	}

	/**
	 * 사용자 엑셀 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int excelUserInfoUpdate(UserInfoVO vo) throws Exception{
		return update(sqlNameSpace+"excelUserInfoUpdate", vo);
	}

	/**
	 * 카드 엑셀 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int excelCardInfoUpdate(CardInfoVO vo) throws Exception{
		return update(sqlNameSpace+"excelCardInfoUpdate", vo);
	}

	/**
	 * 사용자 이미지 업로드(수정)
	 * @param UserBioInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int imgUserInfoUpload(UserBioInfoVO vo) throws Exception{
		return update(sqlNameSpace+"imgUserInfoUpload", vo);
	}

	/**
	 * 사용자 이미지 업로드(신규)
	 * @param UserBioInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int imgUserInfoSave(UserBioInfoVO vo) throws Exception{
		return insert(sqlNameSpace+"imgUserInfoSave", vo);
	}

	/**
	 * 사진중복체크
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getBioCnt(String fuid) throws Exception{
		return selectOne(sqlNameSpace+"getBioCnt", fuid);
	}


	/**
	 * 사진중복체크
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getBioLinkCnt(String fuid) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getBioLinkCnt", fuid);
	}

	/**
	 * 엑셀다운로드
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public List<ExcelVO> getUserExcelList(UserInfoVO vo) throws Exception{
		return selectList(sqlNameSpace+"getUserExcelList", vo);
	}

	/**
	 * 타센터유저정보리스트 가져오기(대전)
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> getOtherCenterUserInfoList11(String srchFunmWord) throws Exception{
		return selectList(sqlNameSpace+"getOtherCenterUserInfoList11", srchFunmWord);
	}

	/**
	 * 타센터유저정보리스트 가져오기(광주)
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> getOtherCenterUserInfoList12(String srchFunmWord) throws Exception{
		return selectList(sqlNameSpace+"getOtherCenterUserInfoList12", srchFunmWord);
	}

	/**
	 * 출입권한목록 조회수 가져오기
	 * @param LogInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int getUsrListConnTotalCnt(LogInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getUsrListConnTotalCnt", vo);
	}

	/**
	 * 출입권한목록 목록 가져오기
	 * @param LogInfoVO
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	public List<LogInfoVO> getUsrListConnPop(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getUsrListConnPop", vo);
	}

	/**
	 * 이미지 있는 사용자 목록 전체 조회
	 * @param LogInfoVO
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	public List<UserBioInfoVO> getUserInfoAllBioList(UserInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getUserInfoAllBioList", vo);
	}

	/**
	 * 사용자 이미지 bioLink 업로드(수정)
	 * @param UserBioInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int imgUserInfoLinkUpload(UserBioInfoVO vo) throws Exception {
		return update(sqlNameSpace+"imgUserInfoLinkUpload", vo);
	}

	/**
	 * 사용자 이미지 bioLink 업로드(신규)
	 * @param UserBioInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int imgUserInfoLinkSave(UserBioInfoVO vo) throws Exception {
		return insert(sqlNameSpace+"imgUserInfoLinkSave", vo);
	}

	/**
	 * 사용자 수정 시 카드 정보 동시 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int cardInfoUpdate(UserInfoVO vo) throws Exception {
		return update(sqlNameSpace+"cardInfoUpdate", vo);
	}
	
	/**
	 * 출입자 정보 수정 시 카드 수정
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int updateCardInfo(CardInfoVO vo) throws Exception {
		return update(sqlNameSpace+"updateCardInfo", vo);
	}

	/**
	 * 체크된 사용자  카드 정보 일괄 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int chkCardInfoLstUpdate(UserInfoVO vo) throws Exception {
		return update(sqlNameSpace+"chkCardInfoLstUpdate", vo);
	}

	/**
	 * 체크된 사용자  정보 일괄 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int chkUserInfoLstSave(UserInfoVO vo) throws Exception {
		return update(sqlNameSpace+"chkUserInfoLstSave", vo);
	}

	/**
	 * 체크된 사용자  카드 정보 일괄 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int allCardInfoLstUpdate(UserInfoVO vo) throws Exception {
		return update(sqlNameSpace+"allCardInfoLstUpdate", vo);
	}

	/**
	 * 체크된 사용자  정보 일괄 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int allUserInfoLstSave(UserInfoVO vo) throws Exception {
		return update(sqlNameSpace+"allUserInfoLstSave", vo);
	}

	/**
	 * 다운로드 이력 사유 저장
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int setDownLoadResnLog(Map<String, String> m) throws Exception {
		return insert(sqlNameSpace+"setDownLoadResnLog", m);
	}

	/**
	 * 다운로드 이력 사유 전체 건수
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getDownloadLogCnt(UserInfoVO vo) {
		return selectOne(sqlNameSpace+"getDownloadLogCnt", vo);
	}

	/**
	 * 다운로드 이력 사유 목록
	 * @param
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	public List<DownloadLogVO> selectDownloadLogList(UserInfoVO vo) {
		return selectList(sqlNameSpace+"selectDownloadLogList", vo);
	}

	public void updateBioExpire(HashMap map) throws Exception{
		update(sqlNameSpace+"updateBioExpire", map);
	}
	public void updateBiolinkExpire(HashMap map) throws Exception{
		update(sqlNameSpace+"updateBiolinkExpire", map);
	}
	public int updateCardExpire(HashMap map) throws Exception{
		return update(sqlNameSpace+"updateCardExpire", map);
	}
	public int updateUserExpire(HashMap map) throws Exception{
		return update(sqlNameSpace+"updateUserExpire", map);
	}
	
	/**
	 * 출입자 사용안함 처리
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int updateCardNotUsed(HashMap map) throws Exception{
		return update(sqlNameSpace+"updateCardNotUsed", map);
	}
	
	/**
	 * 출입자 사용안함 처리 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int updateUserNotUsed(HashMap map) throws Exception{
		return update(sqlNameSpace+"updateUserNotUsed", map);
	}
	
	/**
	 * 출입자 복원
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int updateCardReco(HashMap map) throws Exception{
		return update(sqlNameSpace+"updateCardReco", map);
	}
	
	/**
	 * 출입자 복원 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int updateUserReco(HashMap map) throws Exception{
		return update(sqlNameSpace+"updateUserReco", map);
	}	

	/**
	 * fuid 중복 오류 확인
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int selectChkFuid(UserInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"selectChkFuid", vo);
	}
	
	/**
	 * fcdno 중복 오류 확인
	 * @param vo
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public Map<String, Object> getChkFcdno(CardInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace + "selectChkFcdno", vo);
	}
	
	/**
	 * 방문객카드번호 중복 체크
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectChkFvisitnum(Map<String, Object> param) throws Exception {
		return selectOne(sqlNameSpace+"selectChkFvisitnum", param);
	}		
	
	/**
	 * 출입자 상태 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> selectUserStatus(Map<String, String> param) throws Exception {
		return selectOne(sqlNameSpace + "selectUserStatus", param);
	}
	
	/**
	 * 권한그룹별 출입자 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectUserListByAuthCount(Map<String, Object> param) throws Exception {
		return selectOne(sqlNameSpace + "selectUserListByAuthCount", param);
	}
	
	/**
	 * 권한그룹별 출입자 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectUserListByAuth(Map<String, Object> param) throws Exception {
		return selectList(sqlNameSpace + "selectUserListByAuth", param);
	}	
	
	/**
	 * 권한그룹별 출입자 목록 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */	
	public List<ExcelVO> selectUserListByAuthExcel(Map<String, Object> param) throws Exception {
		return selectList(sqlNameSpace + "selectUserListByAuthExcel", param);
	}	
	
	/**
	 * 단말기별 출입자 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectUserListByGateCount(Map<String, Object> param) throws Exception {
		return selectOne(sqlNameSpace + "selectUserListByGateCount", param);
	}
	
	/**
	 * 단말기별 출입자 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> selectUserListByGate(Map<String, Object> param) throws Exception {
		return selectList(sqlNameSpace + "selectUserListByGate", param);
	}	
	
	/**
	 * 단말기별 출입자 목록 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */	
	public List<ExcelVO> selectUserListByGateExcel(Map<String, Object> param) throws Exception {
		return selectList(sqlNameSpace + "selectUserListByGateExcel", param);
	}
	
	/**
	 * 출입자 영구삭제 (1/4)tbiolink
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int deleteBiolinkInfo(Map<String, Object> param) throws Exception {
		return delete(sqlNameSpace + "deleteBiolinkInfo", param);
	}
	
	/**
	 * 출입자 영구삭제 (2/4)tbio_main
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int deleteBioInfo(Map<String, Object> param) throws Exception {
		return delete(sqlNameSpace + "deleteBioInfo", param);
	}
	
	/**
	 * 출입자 영구삭제 (3/4)tcard_main
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int deleteCardInfo(Map<String, Object> param) throws Exception {
		return delete(sqlNameSpace + "deleteCardInfo", param);
	}
	
	/**
	 * 출입자 영구삭제 (4/4)tuserinfo_main
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int deleteUserInfo(Map<String, Object> param) throws Exception {
		return delete(sqlNameSpace + "deleteUserInfo", param);
	}
	
	/**
	 * 외부에서 출입자 등록, 엑셀 저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertUserAllInfo(Map<String, Object> param) throws Exception{
		insert(sqlNameSpace+"insertUserRest", param);
		insert(sqlNameSpace+"insertCardRest", param);
		insert(sqlNameSpace+"insertAuthRest", param);
		insert(sqlNameSpace+"insertSyncDeviceRest", param);
		int cnt = insert(sqlNameSpace+"insertFaceRest", param);
		//insert(sqlNameSpace+"insertFaceSyncRest", param);
		if(StringUtil.nvl(param.get("syslogYn")).equals("Y")) {
			insert(sqlNameSpace+"insertLogRest", param);
		}
		return cnt;
	}	
	
}
