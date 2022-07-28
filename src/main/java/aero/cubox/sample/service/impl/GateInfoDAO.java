package aero.cubox.sample.service.impl;

import java.util.HashMap;
import java.util.List;

import aero.cubox.user.service.vo.UserInfoVO;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import aero.cubox.sample.service.vo.ComboVO;
import aero.cubox.sample.service.vo.DeviceInfoVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateInfoVO;
import aero.cubox.sample.service.vo.GateOperationVO;
import aero.cubox.sample.service.vo.GatePositionVO;

@Repository("gateInfoDAO")
public class GateInfoDAO extends EgovAbstractMapper {

	private static final String sqlNameSpace = "gateInfo.";
	
	public List<ComboVO> getAreaListToCenter(String ftid) {
		return selectList(sqlNameSpace + "getAreaListToCenter", ftid);
	}
	
	public List<ComboVO> getFloorListToArea(String fptid) {
		return selectList(sqlNameSpace + "getFloorListToArea", fptid);
	}
	
	public List<GateInfoVO> getGateListToFloor(String fptid) {
		return selectList(sqlNameSpace + "getGateListToFloor", fptid);
	}
	
	public List<GateInfoVO> getGateListToFloor2(GateInfoVO vo) {
		return selectList(sqlNameSpace + "getGateListToFloor2", vo);
	}
	
	public int gateOperationFor11(GateOperationVO gateOperationVO) {
		return insert(sqlNameSpace + "gateOperationFor11", gateOperationVO);
	}
	
	public int gateOperationFor12(GateOperationVO gateOperationVO) {
		return insert(sqlNameSpace + "gateOperationFor12", gateOperationVO);
	}
	
	public int updateGatePosition(GatePositionVO gatePositionVO) {
		return update(sqlNameSpace + "updateGatePosition", gatePositionVO);
	}
	
	public List<UserInfoVO> getUserListToGateFor11(GateInfoVO gateInfo) {
		return selectList(sqlNameSpace + "getUserListToGateFor11", gateInfo);
	}
	
	public List<UserInfoVO> getUserListToGateFor12(GateInfoVO gateInfo) {
		return selectList(sqlNameSpace + "getUserListToGateFor12", gateInfo);
	}
	
	public int getUserListToGateTotalCoundFor11(GateInfoVO gateInfo) {
		return selectOne(sqlNameSpace + "getUserListToGateTotalCountFor11", gateInfo);
	}
	
	public int getUserListToGateTotalCoundFor12(GateInfoVO gateInfo) {
		return selectOne(sqlNameSpace + "getUserListToGateTotalCountFor12", gateInfo);
	}
	
	public String getGateName(String gid) {
		return selectOne(sqlNameSpace + "getGateName", gid);
	}
	
	public List<ExcelVO> getExcelUserListToGateFor11(GateInfoVO gateInfo) {
		return selectList(sqlNameSpace + "getExcelUserListToGateFor11", gateInfo);
	}
	
	public List<ExcelVO> getExcelUserListToGateFor12(GateInfoVO gateInfo) {
		return selectList(sqlNameSpace + "getExcelUserListToGateFor12", gateInfo);
	}


	/**
	 * 임시 일괄삭제(위험)
	 */
	public int deleteSystemReset(){
		return delete(sqlNameSpace+"deleteSystemReset");		
	}

	public int updateLocationGate(HashMap map){
		return update(sqlNameSpace+"updateLocationGate", map);		
	}


	public HashMap selectLocationGate(HashMap map){
		return selectOne(sqlNameSpace + "selectLocationGate", map);
	}

	public void newGateAuthTypeChange(HashMap sMap) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"newGateAuthTypeChange",sMap);
	}

	public void newGateMatchTypeChange(HashMap sMap) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"newGateMatchTypeChange",sMap);
	}

	public List<ComboVO> getFloorListToCenter(String siteId) {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace+"getFloorListToCenter",siteId);
	}

	public List<DeviceInfoVO> getNewGateListToFloor(FloorVO vo) {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace+"getNewGateListToFloor",vo);
	}

	public void updateNewGatePosition(DeviceInfoVO devicePositionVO) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"updateNewGatePosition",devicePositionVO);
	}

	public void resetGatePosition(DeviceInfoVO vo) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"resetGatePosition", vo);
	}
}
