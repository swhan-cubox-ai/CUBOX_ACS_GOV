package aero.cubox.user.service;

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

public interface UserInfoService {
	
	/**
	 * 출입자관리 조회
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> selectUserList(UserInfoVO vo) throws Exception;
	
	/**
	 * 출입자관리 조회 전체건수
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int selectUserListCount(UserInfoVO vo) throws Exception;
	
	/**
	 * 출입자관리 조회 엑셀저장
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public List<ExcelVO> selectUserListExcel(UserInfoVO vo) throws Exception;

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> getUserInfoList3(UserInfoVO vo) throws Exception;

	/**
	 * 사용자전체cnt
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getUserTotalCnt3(UserInfoVO vo) throws Exception;

	/**
	 * 사용자정보가져오기
	 * @param String
	 * @return UserInfoVO
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo(String fuid) throws Exception;

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return UserInfoVO
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo2(UserInfoVO vo) throws Exception;


	/**
	 * DB이미지가져오기
	 * @param String
	 * @return UserBioInfoVO
	 * @throws Exception
	 */
	public UserBioInfoVO getUserBioInfo(String fuid) throws Exception;

	/**
	 * 카드번호 가져오기
	 * @param String
	 * @return String
	 * @throws Exception
	 */
	public List<CardInfoVO> getCardInfoList(String fuid) throws Exception;

	/**
	 * 유저권한그룹 가져오기(대전)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getUserAuthGroupList11(AuthGroupVO vo) throws Exception;

	/**
	 * 유저권한그룹 가져오기(광주)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getUserAuthGroupList12(AuthGroupVO vo) throws Exception;

	/**
	 * 전체권한그룹 가져오기(대전)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getTotalAuthGroupList11(AuthGroupVO vo) throws Exception;

	/**
	 * 전체권한그룹 가져오기(광주)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	public List<AuthGroupVO> getTotalAuthGroupList12(AuthGroupVO vo) throws Exception;

	/**
	 * 카드정보가져오기
	 * @param String
	 * @return CardInfoVO
	 * @throws Exception
	 */
	public CardInfoVO getCardInfo(CardInfoVO vo) throws Exception;

	/**
	 * 카드정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int cardInfoSave(CardInfoVO vo) throws Exception;

	/**
	 * 카드중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int getCdnoCnt(CardInfoVO vo) throws Exception;

	/**
	 * 카드추가
	 * @param CardInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int addUserCdno(CardInfoVO vo) throws Exception;

	/**
	 * 유저fsstatus변경
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userFsstatusChange(UserInfoVO vo) throws Exception;

	/**
	 * tauthtogate_main테이블 fsstatus변경
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int tauthtogateMainFsstatusChange(CardInfoVO vo) throws Exception;

	/**
	 * 사용자정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoSave(UserInfoVO vo) throws Exception;
	
	/**
	 * 2021-04-20 출입자정보 수정저장, 사진,개인정보 같이 저장함
	 * @param userInfoVO
	 * @param userBioInfoVO
	 * @param cardInfoVO
	 * @return
	 * @throws Exception
	 */
	public int userInfoSaveNew(UserInfoVO userInfoVO, UserBioInfoVO userBioInfoVO, CardInfoVO cardInfoVO) throws Exception;

	/**
	 * 사용자정보-전화번호 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoPhoneUpdate(UserInfoVO vo) throws Exception;

	/**
	 * 사용자정보-차번호 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoCarnoUpdate(UserInfoVO vo) throws Exception;

	/**
	 * 사용자정보- tcmlinfo - fvname = 'nirscar11'  수정 / 광주는 주차없음
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoCarnoCmlUpdate() throws Exception;

	/**
	 * 사용자정보-비고수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoEtcUpdate(UserInfoVO vo) throws Exception;
	
	/**
	 * 사용자정보 삭제
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoDel(UserInfoVO vo) throws Exception;
	
	/**
	 * 사용자정보 삭제, 변경이력
	 * @param vo
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int userInfoDel(UserInfoVO vo, Map<String, Object> param) throws Exception;
	
	/**
	 * 출입자 완전삭제
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int userInfoDrop(UserInfoVO vo) throws Exception;
	
	/**
	 * 출입자 완전삭제, 변경이력
	 * @param vo
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int userInfoDrop(UserInfoVO vo, Map<String, Object> map) throws Exception;
	
	/**
	 * 사용자정보 복원
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int userInfoReco(UserInfoVO vo) throws Exception;	
	
	/**
	 * 사용자정보 복원, 변경이력
	 * @param vo
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public int userInfoReco(UserInfoVO vo, Map<String, Object> map) throws Exception;	

	/**
	 * 권한그룹코드 가져오기(ftid)
	 * @param
	 * @return String
	 * @throws Exception
	 */
	public String getAuthGroupFtid(String authGroupNm) throws Exception;

	/**
	 * 신규 사용자 정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int newUserInfoSave(UserInfoVO vo) throws Exception;

	/**
	 * fuid, funm 중복체크
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int getFuidFunmCnt(UserInfoVO vo) throws Exception;

	/**
	 * 사용자 엑셀 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int excelUserInfoUpdate(UserInfoVO vo) throws Exception;

	/**
	 * 카드 엑셀 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int excelCardInfoUpdate(CardInfoVO vo) throws Exception;

	/**
	 * 사용자 이미지 업로드(신규)
	 * @param UserBioInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int imgUserInfoSave(UserBioInfoVO vo) throws Exception;

	/**
	 * 사진중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int getBioCnt(String fuid) throws Exception;
	
	/**
	 * 사진중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int getBioLinkCnt(String fuid) throws Exception;

	/**
	 * 타센터유저정보리스트 가져오기(대전)
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> getOtherCenterUserInfoList11(String srchFunmWord) throws Exception;

	/**
	 * 타센터유저정보리스트 가져오기(광주)
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public List<UserInfoVO> getOtherCenterUserInfoList12(String srchFunmWord) throws Exception;

	/**
	 * 출입권한목록 조회수 가져오기
	 * @param LogInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int getUsrListConnTotalCnt(LogInfoVO vo) throws Exception;

	/**
	 * 출입권한목록 목록 가져오기
	 * @param LogInfoVO
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	public List<LogInfoVO> getUsrListConnPop(LogInfoVO vo) throws Exception;

	/**
	 * 이미지 있는 사용자 목록 전체 조회
	 * @param LogInfoVO
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	public List<UserBioInfoVO> getUserInfoAllBioList(UserInfoVO vo) throws Exception;

	/**
	 * 선택된 사용자 전체 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int chkUserInfoLstSave(UserInfoVO vo) throws Exception;

	/**
	 * 전체 사용자 전체 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int allUserInfoLstSave(UserInfoVO vo) throws Exception;

	/**
	 * 다운로드 이력 사유 등록
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int setDownLoadResnLog(Map<String, String> m) throws Exception;

	/**
	 * 다운로드 이력 조회 전체 건수
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getDownloadLogCnt(UserInfoVO vo) throws Exception;

	/**
	 * 다운로드 이력 조회 목록
	 * @param
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	public List<DownloadLogVO> getDownloadLogList(UserInfoVO vo) throws Exception;

	

	/**
	 * 타센터유저정보 가져오기
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	//public List<UserInfoVO> getOtherCenterUserInfo(UserInfoVO vo) throws Exception;



	public void setUserExpire(HashMap map) throws Exception;

	/**
	 * fuid 중복 오류 확인
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getChkFuid(UserInfoVO vo) throws Exception;
	
	/**
	 * 방문객카드번호 중복 확인
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int getChkFvisitnum(Map<String, Object> param) throws Exception;	
	
	/**
	 * fuid 채번
	 * @return
	 * @throws Exception
	 */
	public int getNewFuid(UserInfoVO vo) throws Exception;

	/**
	 * 사용자 추가 등록
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int addUserInfoSave(UserInfoVO uvo, CardInfoVO cvo) throws Exception;
	
	/**
	 * 출입자 신규등록 (개인정보,카드정보,권한)
	 * @param uvo
	 * @param cvo
	 * @return
	 * @throws Exception
	 */
	public int addUserInfoNew(UserInfoVO uvo, CardInfoVO cvo, AuthorGroupVO avo) throws Exception;
	
	/**
	 * 출입자 개인정보 수정
	 * @param uvo
	 * @param bvo
	 * @return
	 * @throws Exception
	 */
	public int modUserInfoNew(UserInfoVO uvo, UserBioInfoVO bvo) throws Exception;
	
	/**
	 * 출입자 개인정보 수정 , 이력 남기기
	 * @param uvo
	 * @param bvo
	 * @param sChgResn
	 * @return
	 * @throws Exception
	 */
	public int modUserInfoNew(UserInfoVO uvo, UserBioInfoVO bvo, Map<String, Object> param) throws Exception;
	
	/**
	 * 권한 변경 여부 확인
	 * @param authorGroupVO
	 * @return
	 * @throws Exception
	 */
	public int selectUserAuthGroupForChange(AuthorGroupVO authorGroupVO) throws Exception;
	
	/**
	 * 출입자 카드, 권한그룹 수정
	 * @param cardInfoVO
	 * @param authorGroupVO
	 * @return
	 * @throws Exception
	 */
	public int saveUserCardAuthInfo(CardInfoVO cardInfoVO, AuthorGroupVO authorGroupVO) throws Exception;
	
	/**
	 * 출입자 카드, 권한그룹 수정, 변경이력
	 * @param cardInfoVO
	 * @param authorGroupVO
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int saveUserCardAuthInfo(CardInfoVO cardInfoVO, AuthorGroupVO authorGroupVO, Map<String, Object> param) throws Exception; 
	
	/**
	 * fcdno 중복 오류 확인
	 * @param vo
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public Map<String, Object> getChkFcdno(CardInfoVO vo) throws Exception;
	
	/**
	 * 출입자 상태 조회
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getUserStatus(Map<String, String> param) throws Exception;
	
	/**
	 * 권한그룹별 출입자 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int getUserListByAuthCount(Map<String, Object> param) throws Exception;
	
	/**
	 * 권한그룹별 출입자 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserListByAuth(Map<String, Object> param) throws Exception;
	
	/**
	 * 권한그룹별 출입자 목록 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> getUserListByAuthExcel(Map<String, Object> param) throws Exception;
	
	/**
	 * 단말기별 출입자 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int getUserListByGateCount(Map<String, Object> param) throws Exception;
	
	/**
	 * 단말기별 출입자 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserListByGate(Map<String, Object> param) throws Exception;
	
	/**
	 * 단말기별 출입자 목록 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> getUserListByGateExcel(Map<String, Object> param) throws Exception;
	
	/**
	 * 외부에서 출입자 등록 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String insertUserInfoForRest(Map<String, Object> param) throws Exception;
	
	/**
	 * 엑셀업로드 저장(여러건)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertUserListForExcel(List<HashMap> param) throws Exception;
	
	/**
	 * 엑셀업로드 저장(1건씩)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertUserInfoForExcel(Map<String, Object> param) throws Exception;		

}
