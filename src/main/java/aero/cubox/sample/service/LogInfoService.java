package aero.cubox.sample.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogBioRealInfoVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;

public interface LogInfoService {
 	
	/**
	 * 출입이력 검색목록
	 * @param vo
	 * @return
	 * @throws Exception
	 */	
	public List<LogInfoVO> getGateLogList(LogInfoVO vo) throws Exception;
	
	/**
	 * 출입이력 검색목록 전체건수
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int getGateLogListCount(LogInfoVO vo) throws Exception;
	
	/**
	 * 출입이력 검색목록 엑셀저장
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public List<ExcelVO> getGateLogListExcel(LogInfoVO vo) throws Exception;
	
	/**
	 * 단말기별 출입이력
	 * @param 
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	public List<LogInfoVO> getGateLogInfoPop(LogInfoVO vo) throws Exception;
	
	/**
	 * 단말기별 출입이력 전체 갯수 
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	public int getGateLogTotalCnt(LogInfoVO vo) throws Exception;
	
	/**
	 * 단말기별 출입자 개인정보
	 * @param UserInfoVO
	 * @return logInfo/gateLog_view_popup
	 * @throws Exception
	 */
	public UserInfoVO getGateUserInfo(UserInfoVO vo) throws Exception;

	/**
	 * 사용자별 출입이력
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	public List<LogInfoVO> getUsrLogInfoPop(LogInfoVO vo) throws Exception;
	
	/**
	 * 사용자별 출입이력 전체 갯수
	 * @param 
	 * @return int
	 * @throws Exception
	 */
	public int getUsrLogTotalCnt(LogInfoVO vo) throws Exception;
	
	/**
	 * 사용자 개인정보 가져오기
	 * @param 
	 * @return UserInfoVO
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo(String fuid) throws Exception;
	
	/**
	 * source 이미지가져오기
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	public UserBioInfoVO getUserBioInfo(String fuid) throws Exception;

	/**
	 * real 이미지가져오기 
	 * @param 
	 * @return logInfo/usrLog_view_popup
	 * @throws Exception
	 */
	public LogBioRealInfoVO getLogBioRealInfo(LogBioRealInfoVO vo) throws Exception;
	
	/* 오늘날짜 */
	public String getTodayDt() throws Exception;
	
	/* 어제날짜 */
	public String getYesterDt() throws Exception;
	
	/* 0시0분 */
	public String getDefaultTime() throws Exception;
	
	/* 현재시간 */
	public String getCurTime() throws Exception;

	/* 단말기명 타이틀 */
	public String getGateTitle(String fgid) throws Exception;
	
	/* 일반 */
	public String getTitle(String fuid) throws Exception;
	
	/* 임시증 */
	public String getTempTitle(String fuid) throws Exception;
	
	/* 방문증 */
	public String getVisitTitle(String fuid) throws Exception;
	
	/**
	 * 단말기 팝업 엑셀 다운로드
	 * @param 
	 * @return logInfo/gatePopExcelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getGatePopCellList(LogInfoVO vo) throws Exception;
	
	/**
	 * 사용자 팝업 엑셀 다운로드
	 * @param 
	 * @return logInfo/usrLogExcelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getUsrPopCellList(LogInfoVO vo) throws Exception;

	/**
	 * 출입실패 로그관리 리스트 전체 count
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getFailLogTotalCnt(LogInfoVO vo) throws Exception;

	/**
	 * 출입실패 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	public List<LogInfoVO> getFailLogInfoList(LogInfoVO vo) throws Exception;

	/**
	 * 출입실패이력 엑셀 다운로드
	 * @param
	 * @return logInfo/excelDownload
	 * @throws Exception
	 */
	public List<ExcelVO> getFailLogCellList(LogInfoVO vo) throws Exception;

	/**
	 * 카드별 출입실패 로그관리 리스트 count
	 * @param
	 * @return int
	 * @throws Exception
	 */
	public int getUsrFailLogTotalCnt(LogInfoVO vo) throws Exception;

	/**
	 * 카드별 출입실패 로그관리 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	public List<LogInfoVO> getUsrFailLogInfoPop(LogInfoVO vo) throws Exception;

	/**
	 * 동기화 이력 리스트
	 * @param commandMap 파라메터전달용 commandMap
	 * @return logInfo/log_management
	 * @throws Exception
	 */
	public List<HashMap> getSchdulList(HashMap map) throws Exception;

	public int getSchdulTotalCnt(HashMap map) throws Exception;
	
	/**
	 * 출입자 출입이력 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public int getGateLogTotCntByUser(Map<String, Object> param) throws Exception;
	
}
