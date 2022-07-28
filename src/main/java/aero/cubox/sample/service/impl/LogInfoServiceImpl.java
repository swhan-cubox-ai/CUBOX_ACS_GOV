package aero.cubox.sample.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aero.cubox.sample.service.LogInfoService;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogBioRealInfoVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("logInfoService")
public class LogInfoServiceImpl extends EgovAbstractServiceImpl implements LogInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogInfoServiceImpl.class);

	@Resource(name = "logInfoDAO")
	private LogInfoDAO logInfoDAO;
	
	@Override
	public List<LogInfoVO> getGateLogList(LogInfoVO vo) throws Exception {
		return logInfoDAO.getGateLogList(vo);
	}
	
	@Override
	public int getGateLogListCount(LogInfoVO vo) throws Exception {
		return logInfoDAO.getGateLogListCount(vo);
	}
	
	@Override
	public List<ExcelVO> getGateLogListExcel(LogInfoVO vo) throws Exception {
		return logInfoDAO.getGateLogListExcel(vo);
	}	
	
	/**
	 * 게이트별 출입이력
	 * @param 
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	@Override
	public List<LogInfoVO> getGateLogInfoPop(LogInfoVO vo) throws Exception {
		return logInfoDAO.getGateLogInfoPop(vo);
	}

	/**
	 * 단말기별 출입이력 전체 갯수 
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getGateLogTotalCnt(LogInfoVO vo) throws Exception {
		return logInfoDAO.getGateLogTotalCnt(vo);
	}

	/**
	 * 사용자별 출입이력
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	@Override
	public List<LogInfoVO> getUsrLogInfoPop(LogInfoVO vo) throws Exception {
		return logInfoDAO.getUsrLogInfoPop(vo);
	}
	
	/**
	 * 사용자별 출입이력 전체 갯수
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getUsrLogTotalCnt(LogInfoVO vo) throws Exception {
		return logInfoDAO.getUsrLogTotalCnt(vo);
	}
	
	/**
	 * 사용자 개인정보 가져오기
	 * @param 
	 * @return UserInfoVO
	 * @throws Exception
	 */
	@Override
	public UserInfoVO getUserInfo(String fuid) throws Exception {
		return logInfoDAO.getUserInfo(fuid);
	}
	
	/**
	 * 단말기별 출입자 개인정보
	 * @param UserInfoVO
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	@Override
	public UserInfoVO getGateUserInfo(UserInfoVO vo) throws Exception {
		return logInfoDAO.getGateUserInfo(vo);
	}

	/**
	 * source 이미지가져오기
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	@Override
	public UserBioInfoVO getUserBioInfo(String fuid) throws Exception {
		return logInfoDAO.getUserBioInfo(fuid);
	}

	/**
	 * real 이미지가져오기 
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	@Override
	public LogBioRealInfoVO getLogBioRealInfo(LogBioRealInfoVO vo) throws Exception {
		return logInfoDAO.getLogBioRealInfo(vo);
	}
	
	/* 오늘날짜 */
	@Override
	public String getTodayDt() throws Exception {
		return logInfoDAO.getTodayDt();
	}

	/* 어제날짜 */
	@Override
	public String getYesterDt() throws Exception {
		return logInfoDAO.getYesterDt();
	}
	
	//0시 0분
	@Override
	public String getDefaultTime() throws Exception {
		return logInfoDAO.getDefaultTime();
	}
	
	/* 현재시간 */
	@Override
	public String getCurTime() throws Exception {
		return logInfoDAO.getCurTime();
	}
	
	/* 단말기명 타이틀 */
	@Override
	public String getGateTitle(String fgid) throws Exception {
		return logInfoDAO.getGateTitle(fgid);
	}
	
	/* 일반 */
	@Override
	public String getTitle(String fuid) throws Exception {
		return logInfoDAO.getTitle(fuid);
	}

	/* 임시증 */
	@Override
	public String getTempTitle(String fuid) throws Exception {
		return logInfoDAO.getTempTitle(fuid);
	}
	
	/* 방문증 */
	@Override
	public String getVisitTitle(String fuid) throws Exception {
		return logInfoDAO.getVisitTitle(fuid);
	}
	
	/**
	 * 단말기 팝업 엑셀 다운로드
	 * @param 
	 * @return logInfo/gatePopExcelDownload
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> getGatePopCellList(LogInfoVO vo) throws Exception {
		return logInfoDAO.getGatePopCellList(vo);
	}
	
	/**
	 * 사용자 팝업 엑셀 다운로드
	 * @param 
	 * @return logInfo/usrLogExcelDownload
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> getUsrPopCellList(LogInfoVO vo) throws Exception {
		return logInfoDAO.getUsrPopCellList(vo);
	}

	/**
	 * 출입실패 로그관리 리스트 전체 count
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getFailLogTotalCnt(LogInfoVO vo) throws Exception {
		return logInfoDAO.getFailLogTotalCnt(vo);
	}

	/**
	 * 출입실패 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	@Override
	public List<LogInfoVO> getFailLogInfoList(LogInfoVO vo) throws Exception {
		return logInfoDAO.getFailLogInfoList(vo);
	}

	/**
	 * 출입실패이력 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> getFailLogCellList(LogInfoVO vo) throws Exception {
		return logInfoDAO.getFailLogCellList(vo);
	}

	/**
	 * 카드별 출입실패 로그관리 리스트 count
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getUsrFailLogTotalCnt(LogInfoVO vo) throws Exception {
		return logInfoDAO.getUsrFailLogTotalCnt(vo);
	}

	/**
	 * 카드별 출입실패 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	@Override
	public List<LogInfoVO> getUsrFailLogInfoPop(LogInfoVO vo) throws Exception {
		return logInfoDAO.getUsrFailLogInfoPop(vo);
	}

	@Override
	public List<HashMap> getSchdulList(HashMap map) throws Exception {
		return logInfoDAO.getSchdulList(map);
	}

	@Override
	public int getSchdulTotalCnt(HashMap map) throws Exception {
		return logInfoDAO.getSchdulTotalCnt(map);
	}

	@Override
	public int getGateLogTotCntByUser(Map<String, Object> param) throws Exception {
		return logInfoDAO.selectGateLogTotCntByUser(param);
	}


}
