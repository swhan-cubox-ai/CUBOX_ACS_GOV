package aero.cubox.user.service.impl;

import java.util.List;
import java.util.Map;

import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.user.service.vo.UserChgLogVO;
import aero.cubox.user.service.vo.UserInfoVO;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("userChgLogDAO")
public class UserChgLogDAO extends EgovAbstractMapper {

	private static final String sqlNameSpace = "userChgLog.";
	
	/**
	 * 출입자 정보 로그를 남기기 위해 출입자 정보 조회
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectUserInfoForLog(UserInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"selectUserInfoForLog", vo);
    }
	
	/**
	 * 바이오 정보
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectBioInfoForLog(UserInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"selectBioInfoForLog", vo);
    }
	
	/**
	 * 출입자 정보 로그를 남기기 위해 카드 정보 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectCardInfoForLog(CardInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"selectCardInfoForLog", vo);
    }
	
	/**
	 * 출입자 정보 로그를 남기기 위해 권한 그룹 조회
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectAuthInfoForLog(AuthorGroupVO vo) throws Exception {
        return selectOne(sqlNameSpace+"selectAuthInfoForLog", vo);
    }	
	
	/**
	 * 출입자 정보 로그를 남기기 위해 출입자 정보 조회(삭제)
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectUserDelInfoForLog(UserInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"selectUserDelInfoForLog", vo);
    }
	
	public int selectNewUserChgSeq() throws Exception {
		return selectOne(sqlNameSpace+"selectNewUserChgSeq");
	}	
	
	/**
	 * 출입자 변경 이력 저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertUserChgLog2(Map<String, Object> param) throws Exception {
		return insert(sqlNameSpace+"insertUserChgLog2", param);
	}
	
	/**
	 * 출입자 변경 이력 저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int insertUserChgLog(Map<String, Object> param) throws Exception {
		return insert(sqlNameSpace+"insertUserChgLog", param);
	}	
	
	public int insertUserChgBio(Map<String, Object> param) throws Exception {
		return insert(sqlNameSpace+"insertUserChgBio", param);
	}	

	/**
	 * 변경 내용 상세 보기
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public UserChgLogVO selectUserChgLogInfo(Map<String, Object> param) throws Exception {
        return selectOne(sqlNameSpace+"selectUserChgLogInfo", param);
    }
	
	/**
	 * 출입자 변경 이력 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<UserChgLogVO> selectUserChgLogList(Map<String, Object> param) throws Exception {
        return selectList(sqlNameSpace+"selectUserChgLogList", param);
    }
	
	/**
	 * 출입자 변경 이력 조회 개수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int selectUserChgLogListCount(Map<String, Object> param) throws Exception {
        return selectOne(sqlNameSpace+"selectUserChgLogListCount", param);
    }
	
	/**
	 * 출입자 변경 이력 조회 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> selectUserChgLogListExcel(Map<String, Object> param) throws Exception {
        return selectList(sqlNameSpace+"selectUserChgLogListExcel", param);
    }
	
	/**
	 * 출입자 사진 변경 조회
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> selectUserChgBioInfo(String str) throws Exception {
        return selectOne(sqlNameSpace+"selectUserChgBioInfo", str);
    }	
}
