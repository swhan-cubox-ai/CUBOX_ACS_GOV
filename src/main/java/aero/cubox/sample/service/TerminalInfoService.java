package aero.cubox.sample.service;

import java.util.HashMap;
import java.util.List;

import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.GateShTypeVO;
import aero.cubox.sample.service.vo.GateVO;
import aero.cubox.sample.service.vo.TcmdMainVO;
import aero.cubox.sample.service.vo.TerminalSchLogVO;

public interface TerminalInfoService {

	/**
	 * 단말기 정보 개수 조회
	 * @param GateVO
	 * @return
	 * @throws Exception
	 */
	public int getTerminalFillListTotalCnt(GateVO vo) throws Exception;
	
	/**
	 * 단말기 정보 조회
	 * @param GateVO
	 * @return List<GateVO>
	 * @throws Exception
	 */
	public List<GateVO> getTerminalFillList(GateVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 조회
	 * @param GateShTypeVO
	 * @return List<GateShTypeVO>
	 * @throws Exception
	 */
	public List<GateShTypeVO> getGateShType(GateShTypeVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 수정
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int updateGateSchedule(GateShTypeVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 추가
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int insertGateSchedule(GateShTypeVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 이름 중복 확인
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int duplScheduleNmCheck(GateShTypeVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 삭제
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int deleteGateScheduleType(GateShTypeVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 단말기 전송 대전
	 * @param TcmdMainVO
	 * @return int
	 * @throws Exception
	 */
	public int terminalShSend11 (TcmdMainVO vo) throws Exception;

	/**
	 * 게이트 스케줄 타입 단말기 전송 광주
	 * @param TcmdMainVO
	 * @return int
	 * @throws Exception
	 */
	public int terminalShSend12 (TcmdMainVO vo) throws Exception;

	/**
	 * 단말기 스케줄 로그 cnt
	 * @param TerminalSchLogVO
	 * @return int
	 * @throws Exception
	 */
	public int getTerminalLogTotalCnt(TerminalSchLogVO vo) throws Exception;

	/**
	 * 단말기 스케줄 로그 목록
	 * @param TerminalSchLogVO
	 * @return List<TerminalSchLogVO>
	 * @throws Exception
	 */
	public List<TerminalSchLogVO> getTerminalLogList(TerminalSchLogVO vo) throws Exception;

	/**
	 * 단말기 제어 이력 엑셀 목록
	 * @param TerminalSchLogVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public List<ExcelVO> getTerminalLogCellList(TerminalSchLogVO vo) throws Exception;

	/**
	 * 이미지 zip password 설정
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public int insertFileImgZipPassword(HashMap map) throws Exception;

	/**
	 * 이미지 zip password get
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public HashMap getZipSetPw() throws Exception;

	/**
	 * 이미지 zip password set
	 * @param UserInfoVO
	 * @return void
	 * @throws Exception
	 */
	public void zipFilePasswordReset(String bfpw, String setpassword, String pid) throws Exception;

	/**
	 * 단말기 신규 등록
	 * @param UserInfoVO
	 * @return void
	 * @throws Exception
	 */
	public int gateAddSave(GateVO vo) throws Exception;

	
	/**
	 * 단말기 신규 등록
	 * @param UserInfoVO
	 * @return void
	 * @throws Exception
	 */
	public int gateChangeSave(GateVO vo) throws Exception;

	

	public int gateFuseynChangeSave(GateVO vo) throws Exception;


	public String getOriGid(String fpartcd1) throws Exception;

	public List<GateVO> getGateList(GateVO vo) throws Exception;

}
