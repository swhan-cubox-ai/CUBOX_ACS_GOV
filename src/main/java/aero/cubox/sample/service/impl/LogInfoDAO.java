package aero.cubox.sample.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogBioRealInfoVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("logInfoDAO")
public class LogInfoDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogInfoDAO.class);
	
	private static final String sqlNameSpace = "logInfo.";
	
	/**
	 * 출입이력 검색목록
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public List<LogInfoVO> getGateLogList(LogInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"getGateLogList", vo);
    }	

	/**
	 * 출입이력 검색목록 전체건수
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int getGateLogListCount(LogInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"getGateLogListCount", vo);
    }
	
	/**
	 * 출입이력 검색목록 엑셀저장
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> getGateLogListExcel(LogInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"getGateLogListExcel", vo);
    }
    
	/**
	 * 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	public List<LogInfoVO> getLogInfoList(LogInfoVO vo) throws Exception {
        return selectList(sqlNameSpace+"getLogInfoList", vo);
    }	
	
	/**
	 * 로그관리 리스트 전체 count 
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	public int getLogTotalCnt(LogInfoVO vo) throws Exception {
        return selectOne(sqlNameSpace+"getLogTotalCnt", vo);
    }
	
	/**
	 * 단말기별 출입이력
	 * @param 
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	public List<LogInfoVO> getGateLogInfoPop(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getGateLogInfoPop",vo);
	}
	
	/**
	 * 단말기별 출입이력 전체 갯수 
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	public int getGateLogTotalCnt(LogInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getGateLogTotalCnt",vo);
	}

	/**
	 * 사용자별 출입이력
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	public List<LogInfoVO> getUsrLogInfoPop(LogInfoVO vo) throws Exception{
		return selectList(sqlNameSpace+"getUsrLogInfoPop", vo);
	}
	
	/**
	 * 사용자별 출입이력 전체 갯수
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	public int getUsrLogTotalCnt(LogInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getUsrLogTotalCnt",vo);
	}
	
	
	/**
	 * 사용자 개인정보 가져오기
	 * @param 
	 * @return UserInfoVO
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo(String fuid) throws Exception{
		return selectOne(sqlNameSpace+"getUserInfo", fuid);
	}
	
	/**
	 * 단말기별 출입자 개인정보
	 * @param UserInfoVO
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	public UserInfoVO getGateUserInfo(UserInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getGateUserInfo", vo);
	}
	
	/**
	 * source 이미지가져오기
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	public UserBioInfoVO getUserBioInfo(String fuid) throws Exception{
		return selectOne(sqlNameSpace+"getUserBioInfo", fuid);
	}

	/**
	 * real 이미지가져오기 
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	public LogBioRealInfoVO getLogBioRealInfo(LogBioRealInfoVO vo) throws Exception{
		return selectOne(sqlNameSpace+"getLogBioRealInfo", vo);
	}

	/* 오늘날짜 */
	public String getTodayDt() throws Exception {
		return selectOne(sqlNameSpace+"getTodayDt");
	}

	/* 어제날짜 */
	public String getYesterDt() throws Exception {
		return selectOne(sqlNameSpace+"getYesterDt");
	}
	
	//0시0분
	public String getDefaultTime() throws Exception {
		return selectOne(sqlNameSpace+"getDefaultTime");
	}
	
	/* 현재시간 */
	public String getCurTime() throws Exception {
		return selectOne(sqlNameSpace+"getCurTime");
	}
	
	/* 단말기명 타이틀 */
	public String getGateTitle(String fgid) throws Exception {
		return selectOne(sqlNameSpace+"getGateTitle", fgid);
	}
	
	public String getTitle(String fuid) throws Exception {
		return selectOne(sqlNameSpace+"getTitle", fuid);
	}
	
	/* 임시증 */
	public String getTempTitle(String fuid) throws Exception {
		return selectOne(sqlNameSpace+"getTempTitle", fuid);
	}
	
	/* 방문증 */
	public String getVisitTitle(String fuid) throws Exception {
		return selectOne(sqlNameSpace+"getVisitTitle", fuid);
	}
	
	/**
	 * 출입이력 엑셀 다운로드
	 * @param 
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getLogCellList(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getLogCellList", vo);
	}

	/**
	 * 단말기 팝업 엑셀 다운로드
	 * @param 
	 * @return logInfo/gatePopExcelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getGatePopCellList(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getGatePopCellList", vo);
	}
	
	/**
	 * 사용자 팝업 엑셀 다운로드
	 * @param 
	 * @return logInfo/usrLogExcelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getUsrPopCellList(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getUsrPopCellList", vo);
	}

	/**
	 * 출입실패 로그관리 리스트 전체 count
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getFailLogTotalCnt(LogInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getFailLogTotalCnt", vo);
	}

	/**
	 * 출입실패 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	public List<LogInfoVO> getFailLogInfoList(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getFailLogInfoList", vo);
	}

	/**
	 * 출입실패이력 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getFailLogCellList(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getFailLogCellList", vo);
	}

	/**
	 * 카드별 출입실패 로그관리 리스트 count
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getUsrFailLogTotalCnt(LogInfoVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getUsrFailLogTotalCnt", vo);
	}

	/**
	 * 카드별 출입실패 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	public List<LogInfoVO> getUsrFailLogInfoPop(LogInfoVO vo) throws Exception {
		return selectList(sqlNameSpace+"getUsrFailLogInfoPop", vo);
	}

	public List<HashMap> getSchdulList(HashMap map) throws Exception{
		return selectList(sqlNameSpace+"getSchdulList",map);
	}

	public int getSchdulTotalCnt(HashMap map) {
		return selectOne(sqlNameSpace+"getSchdulTotalCnt",map);
	}

	/**
	 * 출입자 출입이력 전체 건수
	 * @param map
	 * @return
	 */
	public int selectGateLogTotCntByUser(Map<String, Object> param) {
		return (Integer)selectOne(sqlNameSpace+"selectGateLogTotCntByUser", param);
	}

}
