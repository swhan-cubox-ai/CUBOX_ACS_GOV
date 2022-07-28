package aero.cubox.sample.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import aero.cubox.sample.service.vo.GateShTypeVO;
import aero.cubox.sample.service.vo.GateVO;
import aero.cubox.sample.service.vo.TcmdMainVO;
import aero.cubox.sample.service.vo.TerminalSchLogVO;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.util.Zip4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

import net.lingala.zip4j.model.ZipParameters;
import aero.cubox.util.CuboxProperties;
import aero.cubox.sample.service.TerminalInfoService;
import aero.cubox.sample.service.vo.ExcelVO;

@Service("terminalInfoService")
@EnableAsync
public class TerminalInfoServiceImpl extends EgovAbstractServiceImpl implements TerminalInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TerminalInfoServiceImpl.class);

	/** terminalInfoDAO */
	@Resource(name = "terminalInfoDAO")
	private TerminalInfoDAO terminalInfoDAO;

	/**
	 * 단말기 정보 개수 조회
	 * @param GateVO
	 * @return List<GateVO>
	 * @throws Exception
	 */
	@Override
	public int getTerminalFillListTotalCnt(GateVO vo) throws Exception {
		return (int)terminalInfoDAO.getTerminalFillListTotalCnt(vo);
	}

	/**
	 * 단말기 정보 조회
	 * @param GateVO
	 * @return List<GateVO>
	 * @throws Exception
	 */
	@Override
	public List<GateVO> getTerminalFillList(GateVO vo) throws Exception {
		return terminalInfoDAO.getTerminalFillList(vo);
	}

	/**
	 * 게이트 스케줄 타입 조회
	 * @param GateShTypeVO
	 * @return List<GateShTypeVO>
	 * @throws Exception
	 */
	@Override
	public List<GateShTypeVO> getGateShType(GateShTypeVO vo) throws Exception {
		return terminalInfoDAO.getGateShType(vo);
	}

	/**
	 * 게이트 스케줄 타입 수정
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateGateSchedule(GateShTypeVO vo) throws Exception {
		return terminalInfoDAO.updateGateSchedule(vo);
	}

	/**
	 * 게이트 스케줄 타입 추가
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int insertGateSchedule(GateShTypeVO vo) throws Exception {
		return terminalInfoDAO.insertGateSchedule(vo);
	}

	/**
	 * 게이트 스케줄 타입 이름 중복 확인
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int duplScheduleNmCheck(GateShTypeVO vo) throws Exception {
		return terminalInfoDAO.duplScheduleNmCheck(vo);
	}

	/**
	 * 게이트 스케줄 타입 삭제
	 * @param GateShTypeVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int deleteGateScheduleType(GateShTypeVO vo) throws Exception {
		return terminalInfoDAO.deleteGateScheduleType(vo);
	}

	/**
	 * 게이트 스케줄 타입 단말기 전송 대전
	 * @param TcmdMainVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int terminalShSend11(TcmdMainVO vo) throws Exception {
		return terminalInfoDAO.terminalShSend11(vo);
	}

	/**
	 * 게이트 스케줄 타입 단말기 전송 광주
	 * @param TcmdMainVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int terminalShSend12(TcmdMainVO vo) throws Exception {
		return terminalInfoDAO.terminalShSend12(vo);
	}

	/**
	 * 단말기 스케줄 로그 cnt 대전
	 * @param TerminalSchLogVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getTerminalLogTotalCnt(TerminalSchLogVO vo) throws Exception {
		return terminalInfoDAO.getTerminalLogTotalCnt(vo);
	}

	/**
	 * 단말기 스케줄 로그 목록 대전
	 * @param TerminalSchLogVO
	 * @return List<TerminalSchLogVO>
	 * @throws Exception
	 */
	@Override
	public List<TerminalSchLogVO> getTerminalLogList(TerminalSchLogVO vo) throws Exception {
		return terminalInfoDAO.getTerminalLogList(vo);
	}

	/**
	 * 단말기 제어 이력 엑셀 목록
	 * @param TerminalSchLogVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> getTerminalLogCellList(TerminalSchLogVO vo) throws Exception {
		return terminalInfoDAO.getTerminalLogCellList(vo);
	}

	/**
	 * 이미지 zip password 설정
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	@Override
	public int insertFileImgZipPassword(HashMap vo) throws Exception {
		return terminalInfoDAO.insertFileImgZipPassword(vo);
	}

	/**
	 * 이미지 zip password get
	 * @param UserInfoVO
	 * @return HashMap
	 * @throws Exception
	 */
	@Override
	public HashMap getZipSetPw() throws Exception {
		return terminalInfoDAO.selectZipSetPw();
	}
	
	/**
	 * 이미지 zip password set
	 * @param UserInfoVO
	 * @return void
	 * @throws Exception
	 */
	@Override
	@Async
	public void zipFilePasswordReset(String bfpw, String setpassword, String pid) {
		final String IMG_STR_PATH = CuboxProperties.getProperty("Globals.zipfilepath");
		final String ZIP_FILE_PAHT = IMG_STR_PATH + CuboxProperties.getProperty("Globals.zipfilename");	
		int rst = 0;
		
		File f = new File(ZIP_FILE_PAHT);
		ZipFile rwzipFile = null;
		try {
			if(f != null) {
				rwzipFile = new ZipFile(ZIP_FILE_PAHT);
				if(rwzipFile!=null && rwzipFile.isValidZipFile()) {
					if(rwzipFile.isEncrypted()) rwzipFile.setPassword(bfpw);
					rwzipFile.extractAll(IMG_STR_PATH); 
				}
				f.delete();
				Thread.sleep(2000);
				File rootDir = new File(IMG_STR_PATH);
				File[] allFiles = rootDir.listFiles();
				LOGGER.debug("allFiles.length >>>"+allFiles.length);
				if(allFiles != null && allFiles.length > 1) {    					
					rwzipFile = null;
					rwzipFile = new ZipFile(ZIP_FILE_PAHT);		//zip 파일 초기화    					
					ZipParameters parameters = new ZipParameters();
					parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
					parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);		
					parameters.setEncryptFiles(true); 	// 암호화 활성
					parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES); // 암호화 방법
					parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256); // 암호길이
					parameters.setPassword(setpassword); 	// 암호설정
					rwzipFile.addFiles(new ArrayList<>(Arrays.asList(allFiles)), parameters);
					
					//파일 삭제				
					if(allFiles != null && allFiles.length > 1) {
						for(File sf:allFiles) {
							if(!(sf.isFile() && sf.getName().matches("^.*.zip$"))) sf.delete();
						}
					}
				} 
				rst = 1;
			}
		} catch (Exception e) {			
			rst = -1;
			LOGGER.debug(">>>> zip파일 password 오류 발생 >>>> ");
			e.printStackTrace();
		} finally {
			rwzipFile = null;
			if(rst > 0) {
				try {
					LOGGER.debug("pid >>>>"+pid);
					HashMap sm = new HashMap();
					sm.put("fdwnidx", pid);
					terminalInfoDAO.updateFileImgZipPassword (sm);					
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.debug(">>>> zip파일 password 상태값 변경 오류 발생 >>>> ");
				}
			}			
		}
	}

	@Override
	public int gateAddSave(GateVO vo) throws Exception {
		int cnt = terminalInfoDAO.gateDupChk(vo);
		if(cnt > 0) {
			throw new RuntimeException("단말기 코드가 중복됩니다. 새로 입력하세요.");
		}

		int rtn = 0;
		rtn += terminalInfoDAO.gateAddSave(vo);
		rtn += terminalInfoDAO.gateSidxAddSave(vo); 
		rtn += terminalInfoDAO.locationgateAddSave(vo);
		return rtn;
	}

	@Override
	public int gateChangeSave(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		int rtn = 0;
		rtn = terminalInfoDAO.gateChangeSave(vo);
		return rtn;
	}

	@Override
	public int gateFuseynChangeSave(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		return terminalInfoDAO.gateFuseynChangeSave(vo);
	}

	@Override
	public String getOriGid(String fpartcd1) throws Exception {
		// TODO Auto-generated method stub
		return terminalInfoDAO.getOriGid(fpartcd1);
	}

	
	public List<GateVO> getGateList(GateVO vo) throws Exception {
		return terminalInfoDAO.selectGateList(vo);
	}
	

}
