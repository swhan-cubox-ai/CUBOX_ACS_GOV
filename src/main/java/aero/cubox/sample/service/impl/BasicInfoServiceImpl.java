package aero.cubox.sample.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aero.cubox.util.DataScrty;
import aero.cubox.user.service.impl.UserChgLogDAO;
import aero.cubox.sample.service.BasicInfoService;
import aero.cubox.auth.service.vo.AuthorVO;
import aero.cubox.sample.service.vo.CenterInfoVO;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.SiteUserVO;
import aero.cubox.sample.service.vo.SiteVO;
import aero.cubox.sample.service.vo.SysLogVO;
import aero.cubox.user.service.vo.UserChgLogVO;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("basicInfoService")
public class BasicInfoServiceImpl extends EgovAbstractServiceImpl implements BasicInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicInfoServiceImpl.class);

	@Resource(name = "basicInfoDAO")
	private BasicInfoDAO basicInfoDAO;
	
	@Resource(name = "userChgLogDAO")
	private UserChgLogDAO userChgLogDAO;

	/**
	 * 센터정보가져오기
	 * @param CenterInfoVO
	 * @return List<CenterInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<CenterInfoVO> getCenterInfoList(CenterInfoVO vo) throws Exception {
		return basicInfoDAO.getCenterInfoList(vo);
	}

	/**
	 * 센터추가
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int centerAddSave(String fvalue) throws Exception {
		return basicInfoDAO.centerAddSave(fvalue);
	}

	/**
	 * 센터 사용유무 편집
	 * @param CenterInfoVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int fuseynChangeSave(CenterInfoVO vo) throws Exception {
		return basicInfoDAO.fuseynChangeSave(vo);
	}

	/**
	 * 사이트유저가져오기
	 * @param
	 * @return List<SiteUserVO>
	 * @throws Exception
	 */
	@Override
	public List<SiteUserVO> getSiteUserList(SiteUserVO siteUserVO) throws Exception {
		return basicInfoDAO.getSiteUserList(siteUserVO);
	}

	/**
	 * 입력한 사용자아이디의 중복여부를 체크하여 사용가능여부를 확인
	 * @param fsiteuser 중복여부 확인대상 아이디
	 * @return 사용가능여부(아이디 사용회수 int)
	 * @throws Exception
	 */
	@Override
	public int checkIdDplct(String checkId) {
		return basicInfoDAO.checkIdDplct(checkId);
	}

	/**
	 * 계정추가
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int siteUserAddSave(SiteUserVO vo) throws Exception {

		String fpasswd = DataScrty.encryptPassword(vo.getFsiteid() + "1234", vo.getFsiteid());
		LOGGER.debug("vo.getFsiteid() : " + vo.getFsiteid());
		LOGGER.debug("fpasswd : " + fpasswd);
		vo.setFpasswd(fpasswd);

		return basicInfoDAO.siteUserAddSave(vo);
	}

	/**
	 * 계정편집
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int siteUserInfoChangeSave(SiteUserVO vo) throws Exception {
		return basicInfoDAO.siteUserInfoChangeSave(vo);
	}

	/**
	 * 계정사용유무변경
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int siteUserFuseynChangeSave(SiteUserVO vo) throws Exception {
		return basicInfoDAO.siteUserFuseynChangeSave(vo);
	}

	/**
	 * 계정비밀번호초기화
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int siteUserPasswdReset(SiteUserVO vo) throws Exception {

		String fpasswd = DataScrty.encryptPassword(vo.getFsiteid() + "1234", vo.getFsiteid());
		LOGGER.debug("vo.getFsiteid() : " + vo.getFsiteid());
		LOGGER.debug("fpasswd : " + fpasswd);
		vo.setFpasswd(fpasswd);

		return basicInfoDAO.siteUserPasswdReset(vo);
	}

	/**
	 * 비밀번호체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int checkPwd(SiteUserVO vo) throws Exception {

		String fpasswd = DataScrty.encryptPassword(vo.getFpasswd(), vo.getFsiteid());
		LOGGER.debug("vo.getFsiteid() : " + vo.getFsiteid());
		LOGGER.debug("fpasswd : " + fpasswd);
		vo.setFpasswd(fpasswd);

		return basicInfoDAO.checkPwd(vo);
	}

	/**
	 * 비밀번호변경저장
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int passwdChangeSave(SiteUserVO vo) throws Exception {

		String fpasswd = DataScrty.encryptPassword(vo.getFpasswd(), vo.getFsiteid());
		LOGGER.debug("vo.getFsiteid() : " + vo.getFsiteid());
		LOGGER.debug("fpasswd : " + fpasswd);
		vo.setFpasswd(fpasswd);

		return basicInfoDAO.passwdChangeSave(vo);
	}

	/**
	 * sys로그 리스트
	 * @param SysLogVO
	 * @return List<SysLogVO>
	 * @throws Exception
	 */
	@Override
	public List<SysLogVO> sysLogList(SysLogVO vo) throws Exception {
		return basicInfoDAO.sysLogList(vo);
	}

	/**
	 * sys로그 전체cnt
	 * @param SysLogVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int sysLogTotalCnt(SysLogVO vo) throws Exception{
		return basicInfoDAO.sysLogTotalCnt(vo);
	}

	/**
	 * sys로그 뎁2 가져오기
	 * @param CodeVO
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	@Override
	public List<CodeVO> sysLogDept2List(CodeVO vo) throws Exception{
		return basicInfoDAO.sysLogDept2List(vo);
	}

	/**
	 * sys로그 뎁2 가져오기
	 * @param CodeVO
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	@Override
	public List<CodeVO> sysLogDept3List(CodeVO vo) throws Exception{
		return basicInfoDAO.sysLogDept3List(vo);
	}

	/**
	 * sys로그 엑셀리스트
	 * @param SysLogVO
	 * @return List<SysLogVO>
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> sysLogExcelList(SysLogVO vo) throws Exception{
		return basicInfoDAO.sysLogExcelList(vo);
	}

	/**
	 * sys코드 fkind3, fvalue가져오기
	 * @param List<String>
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	@Override
	public List<CodeVO> sysLogParam(List<String> codeDeptParam) throws Exception{
		return basicInfoDAO.sysLogParam(codeDeptParam);
	}

	/**
	 * tsiteuser fuid 사용  체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getSelectFuidChkCnt(SiteUserVO vo) throws Exception {
		return basicInfoDAO.getSelectFuidChkCnt(vo);
	}

	/**
	 * 권한 그룹 가져오기
	 * @param
	 * @return AuthorVO
	 * @throws Exception
	 */
	@Override
	public List<AuthorVO> getAuthList() throws Exception {
		return basicInfoDAO.getAuthList();
	}

	/**
	 * 권한 추가
	 * @param AuthorVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int authAddSave(AuthorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.authAddSave(vo);
	}

	/**
	 *  사이트 가져오기
	 * @param 
	 * @return AuthorVO
	 * @throws Exception
	 */
	@Override
	public List<AuthorVO> getSiteList() throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.getSiteList();
	}

	/**
	 * 사이트 유저 추가
	 * @param SiteVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int siteAddSave(SiteVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.siteAddSave(vo);
	}

	/**
	 * 사이트 유저 중복체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getSiteUserCnt(SiteUserVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.getSiteUserCnt(vo);
	}

	/**
	 * gid 중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int checkfgidDplct(String checkId) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.checkfgidDplct(checkId);
	}

	/**
	 * 사이트관리 층수 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return List<FloorVO>
	 * @throws Exception
	 */
	@Override
	public List<FloorVO> getFloorList(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.getFloorList(vo);
	}

	/**
	 * 사이트관리 층수 도면 업로드
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int floorUpload(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.floorUpload(vo);
	}

	/**
	 * get Max Idx
	 * @param siteId
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getMaxIdx(String siteId) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.getMaxIdx(siteId);
	}

	/**
	 * 층수 삭제
	 * @param vo
	 * @param FloorVO
	 * @return 
	 * @throws Exception
	 */
	@Override
	public int floorDelete(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		
		int cnt = 0;
		cnt += basicInfoDAO.floorDelete(vo);
		cnt += basicInfoDAO.fileDelete(vo);
		cnt += basicInfoDAO.fileDetailDelete(vo);
		
		return cnt;
	}

	/**
	 * file id 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return String 
	 * @throws Exception
	 */
	@Override
	public String getAtchFileId(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.getAtchFileId(vo);
	}

	/**
	 * atch_file_Id 있는지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return String 
	 * @throws Exception
	 */
	@Override
	public int checkAtchFileId(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.checkAtchFileId(vo);
	}


	/**
	 * 층 정보 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return FloorVO 
	 * @throws Exception
	 */
	@Override
	public FloorVO getFloorDetail(FloorVO vo) {
		// TODO Auto-generated method stub
		return basicInfoDAO.getFloorDetail(vo);
	}

	/**
	 * 층 정보 업데이트
	 * @param vo 
	 * @param FloorVO
	 * @return int 
	 * @throws Exception
	 */
	@Override
	public int floorUpdate(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.floorUpdate(vo);
	}


	/**
	 * 중복 층 체크
	 * @param vo 
	 * @param FloorVO
	 * @return map
	 * @throws Exception
	 */
	@Override
	public int getChkFloor(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.getChkFloor(vo);
	}

	/**
	 * 삭제할 때 층에 걸려있는 단말기 있는 지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int checkUseDevice(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.checkUseDevice(vo);
	}

	/**
	 * 층이 있는지 없는지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int checkFloor(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return basicInfoDAO.checkFloor(vo);
	}


	
    @Override
    public Map<String, Object> selectSysLogInfo(Map<String, String> param) throws Exception {
        return basicInfoDAO.selectSysLogInfo(param);
    }	
    
	@Override
	public List<Map<String, Object>> selectGateLogSettingList(Map<String, Object> param) throws Exception {
	    return basicInfoDAO.selectGateLogSettingList(param);
	}
	
	@Override
	public int selectDupKeepDt(Map<String, Object> param) throws Exception {
	    return basicInfoDAO.selectDupKeepDt(param);
	}
	
	@Override
	public int insertGateLogSettingInfo(Map<String, Object> param) throws Exception {
	    return basicInfoDAO.insertGateLogSettingInfo(param);
	} 
	
	@Override
	public int updateGateLogSettingInfo(Map<String, Object> param) throws Exception {
	    return basicInfoDAO.updateGateLogSettingInfo(param);
	}

	@Override
	public UserChgLogVO selectUserChgLogInfo(Map<String, Object> param) throws Exception{
		return userChgLogDAO.selectUserChgLogInfo(param);
	}
	
	@Override
	public List<UserChgLogVO> selectUserChgLogList(Map<String, Object> param) throws Exception{
		return userChgLogDAO.selectUserChgLogList(param);
	}
	
	@Override
	public int selectUserChgLogListCount(Map<String, Object> param) throws Exception{
		return userChgLogDAO.selectUserChgLogListCount(param);
	}
	
	@Override
	public List<ExcelVO> selectUserChgLogListExcel(Map<String, Object> param) throws Exception{
		return userChgLogDAO.selectUserChgLogListExcel(param);
	}
	
	/**
	 * 출입자 변경 이미지 정보 조회
	 * @param str
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> selectUserChgBioInfo(String str) throws Exception {
		return userChgLogDAO.selectUserChgBioInfo(str);
	}	
}
