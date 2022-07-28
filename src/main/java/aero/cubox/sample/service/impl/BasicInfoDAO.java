package aero.cubox.sample.service.impl;

import java.util.List;
import java.util.Map;

import aero.cubox.auth.service.vo.AuthorVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import aero.cubox.sample.service.vo.CenterInfoVO;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.SiteUserVO;
import aero.cubox.sample.service.vo.SiteVO;
import aero.cubox.sample.service.vo.SysLogVO;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("basicInfoDAO")
public class BasicInfoDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicInfoDAO.class);

	private static final String sqlNameSpace = "basicInfo.";


	/**
	 * 센터정보가져오기
	 * @param CenterInfoVO
	 * @return List<CenterInfoVO>
	 * @throws Exception
	 */
	public List<CenterInfoVO> getCenterInfoList(CenterInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"getCenterInfoList", vo);
    }

	/**
	 * 센터추가
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int centerAddSave(String fvalue) throws Exception {
        return insert(sqlNameSpace+"centerAddSave", fvalue);
    }

	/**
	 * 센터 사용유무 편집
	 * @param CenterInfoVO
	 * @return int
	 * @throws Exception
	 */
	public int fuseynChangeSave(CenterInfoVO vo) throws Exception {
        return update(sqlNameSpace+"fuseynChangeSave", vo);
    }

	/**
	 * 사이트유저가져오기
	 * @param siteUserVO 
	 * @param
	 * @return List<SiteUserVO>
	 * @throws Exception
	 */
	public List<SiteUserVO> getSiteUserList(SiteUserVO siteUserVO) throws Exception {
        return selectList(sqlNameSpace+"getSiteUserList", siteUserVO);
    }

	/**
     * 입력한 아이디의 중복여부를 체크하여 사용가능여부를 확인
     * @param fsiteuser 중복체크대상 아이디
     * @return int 사용가능여부(아이디 사용회수 )
     */
    public int checkIdDplct(String checkId){
        return (Integer)selectOne(sqlNameSpace+"checkIdDplct", checkId);
    }

    /**
	 * 계정추가
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserAddSave(SiteUserVO vo) throws Exception {
        return insert(sqlNameSpace+"siteUserAddSave", vo);
    }

	/**
	 * 계정편집
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserInfoChangeSave(SiteUserVO vo) throws Exception {
        return insert(sqlNameSpace+"siteUserInfoChangeSave", vo);
    }

	/**
	 * 계정사용유무변경
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserFuseynChangeSave(SiteUserVO vo) throws Exception {
        return insert(sqlNameSpace+"siteUserFuseynChangeSave", vo);
    }

	/**
	 * 계정비밀번호초기화
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int siteUserPasswdReset(SiteUserVO vo) throws Exception {
        return insert(sqlNameSpace+"siteUserPasswdReset", vo);
    }

	/**
	 * 비밀번호체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
    public int checkPwd(SiteUserVO vo){
        return (Integer)selectOne(sqlNameSpace+"checkPwd", vo);
    }

    /**
	 * 계정비밀번호변경저장
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int passwdChangeSave(SiteUserVO vo) throws Exception {
        return insert(sqlNameSpace+"passwdChangeSave", vo);
    }

	/**
	 * sys로그 리스트
	 * @param SysLogVO
	 * @return List<SysLogVO>
	 * @throws Exception
	 */
	public List<SysLogVO> sysLogList(SysLogVO vo) throws Exception {
        return selectList(sqlNameSpace+"sysLogList", vo);
    }

	/**
	 * sys로그 전체cnt
	 * @param SysLogVO
	 * @return int
	 * @throws Exception
	 */
	public int sysLogTotalCnt(SysLogVO vo) throws Exception {
        return selectOne(sqlNameSpace+"sysLogTotalCnt", vo);
    }

	/**
	 * sys로그 뎁2 가져오기
	 * @param CodeVO
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	public List<CodeVO> sysLogDept2List(CodeVO vo) throws Exception{
		return selectList(sqlNameSpace+"sysLogDept2List", vo);
	}

	/**
	 * sys로그 뎁3 가져오기
	 * @param CodeVO
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	public List<CodeVO> sysLogDept3List(CodeVO vo) throws Exception{
		return selectList(sqlNameSpace+"sysLogDept3List", vo);
	}

	/**
	 * sys로그 엑셀리스트
	 * @param SysLogVO
	 * @return List<SysLogVO>
	 * @throws Exception
	 */
	public List<ExcelVO> sysLogExcelList(SysLogVO vo) throws Exception{
		return selectList(sqlNameSpace+"sysLogExcelList", vo);
	}

	/**
	 * sys코드 fkind3, fvalue가져오기
	 * @param List<String>
	 * @return List<CodeVO>
	 * @throws Exception
	 */
	public List<CodeVO> sysLogParam(List<String> codeDeptParam) throws Exception{
		return selectList(sqlNameSpace+"sysLogParam", codeDeptParam);
	}

	/**
	 * tsiteuser fuid 사용  체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int getSelectFuidChkCnt(SiteUserVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getSelectFuidChkCnt", vo);
	}
	
	/**
	 * 권한 가져오기
	 * @param
	 * @return AuthorVO
	 * @throws Exception
	 */
	public List<AuthorVO> getAuthList() throws Exception {
		return selectList(sqlNameSpace+"getAuthList");
	}
	
	/**
	 * 권한 추가
	 * @param AuthorVO
	 * @return int
	 * @throws Exception
	 */
	public int authAddSave(AuthorVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"authAddSave",vo);
	}
	
	/**
	 *  사이트 가져오기
	 * @param 
	 * @return AuthorVO
	 * @throws Exception
	 */
	public List<AuthorVO> getSiteList() {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace+"getSiteList");
	}

	/**
	 * 사이트 유저 추가
	 * @param SiteVO
	 * @return int
	 * @throws Exception
	 */
	public int siteAddSave(SiteVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"siteAddSave",vo);
	}

	/**
	 * 사이트 유저 중복체크
	 * @param SiteUserVO
	 * @return int
	 * @throws Exception
	 */
	public int getSiteUserCnt(SiteUserVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getSiteUserCnt",vo);
	}

	/**
	 * gid 중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	public int checkfgidDplct(String checkId) {
		// TODO Auto-generated method stub
		return (Integer)selectOne(sqlNameSpace+"checkfgidDplct", checkId);
	}


	/**
	 * 사이트관리 층수 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return List<FloorVO>
	 * @throws Exception
	 */
	public List<FloorVO> getFloorList(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace+"getFloorList",vo);
	}

	/**
	 * 사이트관리 층수 도면 업로드
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int floorUpload(FloorVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"floorUpload",vo);
	}

	/**
	 * floor max idx
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int getMaxIdx(String siteId) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getMaxIdx", siteId);
	}

	/**
	 * floor 삭제
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int floorDelete(FloorVO vo) {
		// TODO Auto-generated method stub
		return update(sqlNameSpace+"floorDelete",vo);
	}

	/**
	 * file 삭제
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int fileDelete(FloorVO vo) {
		// TODO Auto-generated method stub
		return update(sqlNameSpace+"fileDelete",vo);
	}

	/**
	 * file detail 삭제
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int fileDetailDelete(FloorVO vo) {
		// TODO Auto-generated method stub
		return delete(sqlNameSpace+"fileDetailDelete",vo);
	}
	
	/**
	 * file id 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return String 
	 * @throws Exception
	 */
	public String getAtchFileId(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getAtchFileId",vo);
	}

	
	/**
	 * atch_file_Id 있는지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return String 
	 * @throws Exception
	 */
	public int checkAtchFileId(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"checkAtchFileId",vo);
	}

	/**
	 * 층 정보 가져오기
	 * @param vo 
	 * @param FloorVO
	 * @return FloorVO 
	 * @throws Exception
	 */
	public FloorVO getFloorDetail(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getFloorDetail",vo);
	}

	/**
	 * 층 정보 업데이트
	 * @param vo 
	 * @param FloorVO
	 * @return int 
	 * @throws Exception
	 */
	public int floorUpdate(FloorVO vo) {
		// TODO Auto-generated method stub
		return update(sqlNameSpace+"floorUpdate",vo);
	}

	/**
	 * 중복 층 체크
	 * @param vo 
	 * @param FloorVO
	 * @return map
	 * @throws Exception
	 */
	public int getChkFloor(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getChkFloor",vo);
	}

	/**
	 * 삭제할 때 층에 걸려있는 단말기 있는 지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int checkUseDevice(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"checkUseDevice", vo);
	}

	/**
	 * 층이 있는지 없는지 확인
	 * @param vo 
	 * @param FloorVO
	 * @return int
	 * @throws Exception
	 */
	public int checkFloor(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"checkFloor",vo);
	}


    /**
     * syslog 상세내용 조회
     * @param param
     * @return
     */
    public Map<String, Object> selectSysLogInfo(Map<String, String> param) {
        return selectOne(sqlNameSpace+"selectSysLogInfo", param);
    }
    
    /**
     * 출입이력 설정정보 조회
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectGateLogSettingList(Map<String, Object> param) {
    	return selectList(sqlNameSpace+"selectGateLogSettingList", param);
    }
    
    /**
     * 출입이력 보관기간 중복 체크
     * @param param
     * @return
     */
    public int selectDupKeepDt(Map<String, Object> param) {
    	return (Integer)selectOne(sqlNameSpace+"selectDupKeepDt", param);
    }    
    
    /**
     * 출입이력 설정정보 신규저장
     * @param param
     * @return
     */
    public int insertGateLogSettingInfo(Map<String, Object> param) {
    	return insert(sqlNameSpace+"insertGateLogSettingInfo", param);
    }
    
    /**
     * 출입이력 설정정보 수정
     * @param param
     * @return
     */
    public int updateGateLogSettingInfo(Map<String, Object> param) {
    	insert(sqlNameSpace+"insertGateLogSettingLog", param);
    	return update(sqlNameSpace+"updateGateLogSettingInfo", param);
    }
}
