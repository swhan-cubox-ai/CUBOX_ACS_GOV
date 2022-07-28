package aero.cubox.sample.service;

import java.util.HashMap;
import java.util.List;

import aero.cubox.user.service.vo.UserInfoVO;
import aero.cubox.sample.service.vo.ComboVO;
import aero.cubox.sample.service.vo.DeviceInfoVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateInfoVO;
import aero.cubox.sample.service.vo.GateOperationVO;
import aero.cubox.sample.service.vo.GatePositionVO;

public interface GateInfoService {
	
	/**
	 * Center에 따른 Area 가져오기
	 * params CenterID
	 * @return List<ComboVO>
	 * @throws Exception
	 */
	public List<ComboVO> getAreaListToCenter(String fptid) throws Exception;
	
	
	/**
	 * Area에 따른 Floor 가져오기
	 * params AreaID
	 * @return List<ComboVO>
	 * @throws Exception
	 */
	public List<ComboVO> getFloorListToArea(String fptid) throws Exception;	
	
	
	/**
	 * 단말기 리스트 정보 가져오기 (층에 따른 단말기 조회)
	 * params FloorID
	 * @return List<GateInfoVO>
	 * @throws Exception
	 */
	public List<GateInfoVO> getGateListToFloor(String fptid) throws Exception;
	
	/**
	 * 단말기 리스트 정보 가져오기 (층에 따른 단말기 조회) - 전체포함
	 * params FloorID
	 * @return List<GateInfoVO>
	 * @throws Exception
	 */
	public List<GateInfoVO> getGateListToFloor2(GateInfoVO vo) throws Exception;
	
	/**
	 * 단말기 제어 (대전 단말기)
	 * params GateList
	 * @return 
	 * 
	 * @throws Exception
	 */
	public int gateOperationFor11(GateOperationVO gateList) throws Exception;
	
	/**
	 * 단말기 제어 (광주 단말기)
	 * params GateList
	 * 
	 * @throws Exception
	 */
	public int gateOperationFor12(GateOperationVO gateList) throws Exception;
	
	/**
	 * 단말기 위치 변경
	 * params GatePositionList
	 * 
	 * @throws Exception
	 */
	public void updateGatePosition(GatePositionVO gatePositionList) throws Exception;
	
	
	/**
	 * 단말기 별 사용자 (대전 단말기)
	 * params GateID
	 * 
	 * @throws Exception
	 */
	public List<UserInfoVO> getUserListToGateFor11(GateInfoVO gateInfo) throws Exception;
	
	/**
	 * 단말기 별 사용자 (광주 단말기)
	 * params GateID
	 * 
	 * @throws Exception
	 */
	public List<UserInfoVO> getUserListToGateFor12(GateInfoVO gateInfo) throws Exception;
	
	/**
	 * 단말기 별 사용자 수(대전 단말기)
	 * params GateID
	 * 
	 * @throws Exception
	 */
	public int getUserListToGateTotalCountFor11(GateInfoVO gateInfo) throws Exception;
	
	/**
	 * 단말기 별 사용자 수(광주 단말기)
	 * params GateID
	 * 
	 * @throws Exception
	 */
	public int getUserListToGateTotalCountFor12(GateInfoVO gateInfo) throws Exception;
	
	/**
	 * 단말기 명 가져오기
	 * params GateID
	 * 
	 * @throws Exception
	 */
	public String getGateName(String gid) throws Exception;
	
	/**
	 * 단말기별 사용자 Excel Export (대전 단말기)
	 * params GateInfoVO
	 * 
	 * @throws Exception
	 */
	public List<ExcelVO> getExcelUserListToGateFor11(GateInfoVO gateInfo) throws Exception;
	
	/**
	 * 단말기별 사용자 Excel Export (광주 단말기)
	 * params GateInfoVO
	 * 
	 * @throws Exception
	 */
	public List<ExcelVO> getExcelUserListToGateFor12(GateInfoVO gateInfo) throws Exception;


	public void allSystemReset(List<GateOperationVO> glist) throws Exception;


	public void gateMatchTypeChange(HashMap map) throws Exception;
	
	/**
	 * 단말기 출입인증방식 변경
	 * params HashMap
	 * 
	 * @throws Exception
	 */
	public void gateAuthTypeChange(HashMap map) throws Exception;

	public HashMap selectLocationGate(HashMap map) throws Exception;


	/**
	 * 단말기 출입인증방식 변경
	 * params HashMap
	 * 
	 * @throws Exception
	 */
	public void newGateAuthTypeChange(HashMap sMap) throws Exception;


	/**
	 * 단말기 얼굴매칭 타입 변경
	 * params HashMap
	 * 
	 * @throws Exception
	 */
	public void newGateMatchTypeChange(HashMap sMap) throws Exception;


	
	public List<ComboVO> getFloorListToCenter(String siteId) throws Exception;


	/**
	 * 단말기 가져오기
	 * params FloorVO
	 * 
	 * @throws Exception
	 */
	public List<DeviceInfoVO> getNewGateListToFloor(FloorVO vo) throws Exception;

	

	/**
	 * 단말기 위치 변경
	 * @param DeviceInfoVO 
	 * @return 
	 * @throws Exception
	 */
	public void updateNewGatePosition(DeviceInfoVO devicePositionVO) throws Exception;


	/**
	 * 단말기 위치 초기화
	 * @param DeviceInfoVO 
	 * @return 
	 * @throws Exception
	 */
	public void resetGatePosition(DeviceInfoVO vo) throws Exception;
}
