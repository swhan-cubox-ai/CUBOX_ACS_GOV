package aero.cubox.sample.service.impl;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.GateShTypeVO;
import aero.cubox.sample.service.vo.GateVO;
import aero.cubox.sample.service.vo.TcmdMainVO;
import aero.cubox.sample.service.vo.TerminalSchLogVO;


@Repository("terminalInfoDAO")
public class TerminalInfoDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminalInfoDAO.class);

	private static final String sqlNameSpace = "terminalInfo.";
	
	/**
	 * 단말기 정보 개수 조회
	 * @param GateVO
	 * @return
	 * @throws Exception
	 */
	public int getTerminalFillListTotalCnt(GateVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getTerminalFillListTotalCnt", vo);
	}
	
	/**
	 * 단말기 정보 조회
	 * @param GateVO
	 * @return List<GateVO>
	 * @throws Exception
	 */
	public List<GateVO> getTerminalFillList(GateVO vo) throws Exception {
		return selectList(sqlNameSpace+"getTerminalFillList", vo);
	}

	/**
	 * 게이트 스케줄 타입 조회
	 * @param GateShTypeVO
	 * @return List<GateShTypeVO>
	 * @throws Exception
	 */
	public List<GateShTypeVO> getGateShType(GateShTypeVO vo) throws Exception {
		return selectList(sqlNameSpace+"getGateShType", vo);
	}

	/**
	 * 게이트 스케줄 타입 수정
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int updateGateSchedule(GateShTypeVO vo) throws Exception {
		return update(sqlNameSpace+"updateGateSchedule", vo);
	}

	/**
	 * 게이트 스케줄 타입 추가
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int insertGateSchedule(GateShTypeVO vo) throws Exception {
		return insert(sqlNameSpace+"insertGateSchedule", vo);
	}

	/**
	 * 게이트 스케줄 타입 이름 중복 확인
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int duplScheduleNmCheck(GateShTypeVO vo) throws Exception {
		return selectOne(sqlNameSpace+"duplScheduleNmCheck", vo);
	}

	/**
	 * 게이트 스케줄 타입 삭제
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	public int deleteGateScheduleType(GateShTypeVO vo) throws Exception {
		return delete(sqlNameSpace+"deleteGateScheduleType", vo);
	}

	/**
	 * 게이트 스케줄 타입 단말기 전송 대전
	 * @param TcmdMainVO
	 * @return int
	 * @throws Exception
	 */
	public int terminalShSend11(TcmdMainVO vo) throws Exception {
		return insert(sqlNameSpace+"terminalShSend11", vo);
	}

	/**
	 * 게이트 스케줄 타입 단말기 전송 광주
	 * @param TcmdMainVO
	 * @return int
	 * @throws Exception
	 */
	public int terminalShSend12(TcmdMainVO vo) throws Exception {
		return insert(sqlNameSpace+"terminalShSend12", vo);
	}

	/**
	 * 단말기 스케줄 로그 cnt 대전
	 * @param TerminalSchLogVO
	 * @return int
	 * @throws Exception
	 */
	public int getTerminalLogTotalCnt(TerminalSchLogVO vo) throws Exception {
		return selectOne(sqlNameSpace+"getTerminalLogTotalCnt", vo);
	}

	/**
	 * 단말기 스케줄 로그 목록
	 * @param TerminalSchLogVO
	 * @return List<TerminalSchLogVO>
	 * @throws Exception
	 */
	public List<TerminalSchLogVO> getTerminalLogList(TerminalSchLogVO vo) throws Exception {
		return selectList(sqlNameSpace+"getTerminalLogList", vo);
	}

	/**
	 * 단말기 제어 이력 엑셀 목록
	 * @param TerminalSchLogVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public List<ExcelVO> getTerminalLogCellList(TerminalSchLogVO vo) throws Exception {
		return selectList(sqlNameSpace+"getTerminalLogCellList", vo);
	}

	/**
	 * 이미지 zip password 설정
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	public int insertFileImgZipPassword(HashMap map) throws Exception {
		return insert(sqlNameSpace+"insertFileImgZipPassword", map);
	}

	/**
	 * 이미지 zip password get
	 * @param UserInfoVO
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap selectZipSetPw() throws Exception {
		return selectOne(sqlNameSpace+"selectZipSetPw");
	}

	public int updateFileImgZipPassword(HashMap map) throws Exception {
		return update (sqlNameSpace+"updateFileImgZipPassword", map);
	}
	
	/**
	 * 단말기 코드 중복 체크
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int gateDupChk(GateVO vo) throws Exception{
		return selectOne(sqlNameSpace+"gateDupChk", vo);
	}
	
	public int gateAddSave(GateVO vo) throws Exception{
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"gateAddSave", vo);
	}

	public int locationgateAddSave(GateVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"locationgateAddSave", vo);
	}

	public int gateSidxAddSave(GateVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"gateSidxAddSave",vo);
	}

	public int gateChangeSave(GateVO vo) {
		// TODO Auto-generated method stub
		return update(sqlNameSpace+"gateChangeSave",vo);
	}

	public int gateFuseynChangeSave(GateVO vo) {
		// TODO Auto-generated method stub
		return update(sqlNameSpace+"gateFuseynChangeSave",vo);
	}


	public String getOriGid(String fpartcd1) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getOriGid", fpartcd1);
	}

	/**
	 * 단말기 가져오기 device_info_tb
	 * @param GateVO
	 * @return GateVO
	 * @throws Exception
	 */
	public List<GateVO> getNewTerminalFillList(GateVO vo) {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace+"getNewTerminalFillList", vo);
	}

	public int newGateAddSave(GateVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"newGateAddSave", vo);
	}

	public List<GateVO> selectGateList(GateVO vo) {
		return selectList(sqlNameSpace+"selectGateList", vo);
	}

}
