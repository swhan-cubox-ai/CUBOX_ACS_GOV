package aero.cubox.sample.service;

import java.util.List;
import java.util.Map;

import aero.cubox.auth.service.vo.AuthorVO;
import aero.cubox.user.service.vo.UserChgLogVO;
import aero.cubox.sample.service.vo.CenterInfoVO;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.SiteUserVO;
import aero.cubox.sample.service.vo.SiteVO;
import aero.cubox.sample.service.vo.SysLogVO;
import aero.cubox.sample.service.vo.FloorVO;


public interface BasicInfoService {

	/**
	 * 센터정보가져오기
	 * @param CenterInfoVO
	 * @return List<CenterInfoVO>
	 * @throws Exception
	 */
	public List<CenterInfoVO> getCenterInfoList(CenterInfoVO vo) throws Exception;

	/**
	 * 센터추가
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int centerAddSave(String fvalue) throws Exception;

	/**
	 * 센터 사용유무 편집
	 * @param CenterInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int fuseynChangeSave(CenterInfoVO vo) throws Exception;

	/**
	 * 사이트유저가져오기
	 * @param siteUserVO 
	 * @param siteUserVO
	 * @return List<SiteUserVO>
	 * @throws Exception
	 */
	public List<SiteUserVO> getSiteUserList(SiteUserVO siteUserVO) throws Exception;

	/**
	 * 입력한 아이디의 중복여부를 체크하여 사용가능여부를 확인
	 * @param fsiteuser 중복여부 확인대상 아이디
	 * @return 사용가능여부(아이디 사용회수 int)
	 * @throws Exception
	 */
	public int checkIdDplct(String checkId) throws Exception;

	/**
	 * 계정추가
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserAddSave(SiteUserVO vo) throws Exception;

	/**
	 * 계정편집
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserInfoChangeSave(SiteUserVO vo) throws Exception;

	/**
	 * 계정사용유무변경
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserFuseynChangeSave(SiteUserVO vo) throws Exception;

	/**
	 * 계정비밀번호초기화
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserPasswdReset(SiteUserVO vo) throws Exception;

	/**
	 * 비밀번호체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int checkPwd(SiteUserVO vo) throws Exception;

	/**
	 * 비밀번호변경저장
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int passwdChangeSave(SiteUserVO vo) throws Exception;

	/**
	 * sys로그 리스트
	 * @param SysLogVO
	 * @return List<SysLogVO>
	 * @throws Exception
	 */
	public List<SysLogVO> sysLogList(SysLogVO vo) throws Exception;

	/**
	 * sys로그 전체cnt
	 * @param SysLogVO
	 * @return int
	 * @throws Exception
	 */
	public int sysLogTotalCnt(SysLogVO vo) throws Exception;

	/**
	 * sys로그 뎁2 가져오기
	 * @param CodeVO
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	public List<CodeVO> sysLogDept2List(CodeVO vo) throws Exception;

	/**
	 * sys로그 뎁3 가져오기
	 * @param CodeVO
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	public List<CodeVO> sysLogDept3List(CodeVO vo) throws Exception;

	/**
	 * sys로그 엑셀리스트
	 * @param SysLogVO
	 * @return List<SysLogVO>
	 * @throws Exception
	 */
	public List<ExcelVO> sysLogExcelList(SysLogVO vo) throws Exception;

	/**
	 * sys코드 fkind3, fvalue가져오기
	 * @param List<String>
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	public List<CodeVO> sysLogParam(List<String> codeDeptParam) throws Exception;

	/**
	 * tsiteuser fuid 사용  체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int getSelectFuidChkCnt(SiteUserVO vo) throws Exception;

	/**
	 * 권한 그룹 가져오기
	 * @param
	 * @return AuthorVO
	 * @throws Exception
	 */
	public List<AuthorVO> getAuthList() throws Exception;

	/**
	 * 권한 추가
	 * @param AuthorVO
	 * @return int
	 * @throws Exception
	 */
	public int authAddSave(AuthorVO vo) throws Exception;

	/**
	 *  사이트 가져오기
	 * @param 
	 * @return AuthorVO
	 * @throws Exception
	 */
	public List<AuthorVO> getSiteList() throws Exception;

	/**
	 * 사이트 유저 추가
	 * @param SiteVO
	 * @return int
	 * @throws Exception
	 */
	public int siteAddSave(SiteVO vo) throws Exception;

	/**
	 * 사이트 유저 중복체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int getSiteUserCnt(SiteUserVO vo) throws Exception;

	/**
	 * gid 중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int checkfgidDplct(String checkId) throws Exception;


	/**
	 * 사이트관리 층수 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return List<FloorVO>
	 * @throws Exception
	 */
	public List<FloorVO> getFloorList(FloorVO vo) throws Exception;

	 /**
	 * 사이트관리 층수 도면 업로드
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int floorUpload(FloorVO vo) throws Exception;

	 /**
	 * floor max idx
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int getMaxIdx(String siteId) throws Exception;

	 /**
	 * floor 삭제
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int floorDelete(FloorVO vo) throws Exception;

	
	 /**
	 * file id 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return String 
	 * @throws Exception
	 */
	public String getAtchFileId(FloorVO vo) throws Exception;

	
	/**
	 * atch_file_Id 있는지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return String 
	 * @throws Exception
	 */
	public int checkAtchFileId(FloorVO vo) throws Exception;

	/**
	 * 층 정보 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return FloorVO 
	 * @throws Exception
	 */
	public FloorVO getFloorDetail(FloorVO vo);

	/**
	 * 층 정보 업데이트
	 * @param vo 
	 * @param FloorVO
	 * @return int 
	 * @throws Exception
	 */
	public int floorUpdate(FloorVO vo) throws Exception;

	/**
	 * 중복 층 체크
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int getChkFloor(FloorVO vo) throws Exception;

	/**
	 * 삭제할 때 층에 걸려있는 단말기 있는 지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int checkUseDevice(FloorVO vo) throws Exception;

	/**
	 * 층이 있는지 없는지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int checkFloor(FloorVO vo) throws Exception;

	
    
    /**
     * syslog 상세내용 조회
     * @param param
     * @return map
     * @throws Exception
     */
    public Map<String, Object> selectSysLogInfo(Map<String, String> param) throws Exception;
    
    /**
     * 출입이력 설정정보 조회
     * @param param
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> selectGateLogSettingList(Map<String, Object> param) throws Exception; 
    
    /**
     * 출입이력 보관기간 중복 체크
     * @param param
     * @return
     * @throws Exception
     */
    public int selectDupKeepDt(Map<String, Object> param) throws Exception; 
    
    /**
     * 출입이력 설정정보 신규저장
     * @param param
     * @return
     * @throws Exception
     */
    public int insertGateLogSettingInfo(Map<String, Object> param) throws Exception;
    
    /**
     * 출입이력 설정정보 수정
     * @param param
     * @return
     * @throws Exception
     */
    public int updateGateLogSettingInfo(Map<String, Object> param) throws Exception;
	
    /**
     * 변경 내용 상세보기
     * @param param
     * @return
     * @throws Exception
     */
    public UserChgLogVO selectUserChgLogInfo(Map<String, Object> param) throws Exception;
    
	/**
	 * 출입자 변경 이력 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<UserChgLogVO> selectUserChgLogList(Map<String, Object> param) throws Exception;

	/**
	 * 출입자 변경 이력 조회 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectUserChgLogListCount(Map<String, Object> param) throws Exception;
	
	/**
	 * 출입자 변경 이력 조회 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> selectUserChgLogListExcel(Map<String, Object> param) throws Exception;

	
	/**
	 * 출입자 변경 이미지 정보 조회
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectUserChgBioInfo(String str) throws Exception;
}
