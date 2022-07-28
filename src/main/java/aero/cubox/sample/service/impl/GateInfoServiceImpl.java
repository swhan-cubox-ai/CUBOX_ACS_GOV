package aero.cubox.sample.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import aero.cubox.sample.service.GateInfoService;
import aero.cubox.sample.service.vo.ComboVO;
import aero.cubox.sample.service.vo.DeviceInfoVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateInfoVO;
import aero.cubox.sample.service.vo.GateOperationVO;
import aero.cubox.sample.service.vo.GatePositionVO;
import aero.cubox.user.service.vo.UserInfoVO;

@Service("gateInfoService")
public class GateInfoServiceImpl extends EgovAbstractServiceImpl implements GateInfoService {

	/** memberDAO */
	@Resource(name = "gateInfoDAO")
	private GateInfoDAO gateInfoDAO;
	
	@Override
	public List<ComboVO> getAreaListToCenter(String fptid) throws Exception {
		return gateInfoDAO.getAreaListToCenter(fptid);
	}

	@Override
	public List<ComboVO> getFloorListToArea(String fptid) throws Exception {
		return gateInfoDAO.getFloorListToArea(fptid);
	}
	
	@Override
	public List<GateInfoVO> getGateListToFloor(String fptid) throws Exception {
		return gateInfoDAO.getGateListToFloor(fptid);
	}
	
	@Override
	public List<GateInfoVO> getGateListToFloor2(GateInfoVO vo) throws Exception {
		return gateInfoDAO.getGateListToFloor2(vo);
	}

	@Override
	public int gateOperationFor11(GateOperationVO gateList) throws Exception {
		return gateInfoDAO.gateOperationFor11(gateList);
	}

	@Override
	public int gateOperationFor12(GateOperationVO gateList) throws Exception {
		return gateInfoDAO.gateOperationFor12(gateList);
	}

	@Override
	public void updateGatePosition(GatePositionVO gatePositionList) throws Exception {
		gateInfoDAO.updateGatePosition(gatePositionList);
	}

	@Override
	public List<UserInfoVO> getUserListToGateFor11(GateInfoVO gateInfo) throws Exception {
		return gateInfoDAO.getUserListToGateFor11(gateInfo);
	}
	
	@Override
	public List<UserInfoVO> getUserListToGateFor12(GateInfoVO gateInfo) throws Exception {
		return gateInfoDAO.getUserListToGateFor12(gateInfo);
	}

	@Override
	public int getUserListToGateTotalCountFor11(GateInfoVO gateInfo) throws Exception {
		return gateInfoDAO.getUserListToGateTotalCoundFor11(gateInfo);
	}

	@Override
	public int getUserListToGateTotalCountFor12(GateInfoVO gateInfo) throws Exception {
		return gateInfoDAO.getUserListToGateTotalCoundFor12(gateInfo);
	}

	@Override
	public String getGateName(String gid) throws Exception {
		return gateInfoDAO.getGateName(gid);
	}

	@Override
	public List<ExcelVO> getExcelUserListToGateFor11(GateInfoVO gateInfo) throws Exception {
		return gateInfoDAO.getExcelUserListToGateFor11(gateInfo);
	}

	@Override
	public List<ExcelVO> getExcelUserListToGateFor12(GateInfoVO gateInfo) throws Exception {
		return gateInfoDAO.getExcelUserListToGateFor12(gateInfo);
	}


	@Override
	public void allSystemReset(List<GateOperationVO> glist) throws Exception {
    	for(GateOperationVO m : glist) {
    		gateInfoDAO.gateOperationFor11(m);
    	}
		gateInfoDAO.deleteSystemReset();		
	}


	@Override
	public void gateMatchTypeChange(HashMap map) throws Exception {

		GateOperationVO vo = new GateOperationVO ();
		vo.setSidx((String)map.get("fsidx"));
		vo.setGid((String)map.get("fgid"));
		vo.setValue((String)map.get("flh"));
		vo.setProcid("MAIN");
		vo.setTx("CMD_MATCH_STATUS");
		gateInfoDAO.gateOperationFor11(vo);
		gateInfoDAO.updateLocationGate(map);  
	}

	@Override
	public void gateAuthTypeChange(HashMap map) throws Exception {

		GateOperationVO vo = new GateOperationVO ();
		vo.setSidx((String)map.get("fsidx"));
		vo.setGid((String)map.get("fgid"));
		vo.setValue((String)map.get("fli"));
		vo.setProcid("MAIN");
		vo.setTx("CMD_AUTH_STATUS");
		gateInfoDAO.gateOperationFor11(vo);
		gateInfoDAO.updateLocationGate(map);  
	}

	
	@Override
	public HashMap selectLocationGate(HashMap map) throws Exception {
		return gateInfoDAO.selectLocationGate(map);
	}

	@Override
	public void newGateAuthTypeChange(HashMap sMap) throws Exception {
		// TODO Auto-generated method stub
		gateInfoDAO.newGateAuthTypeChange(sMap);
	}

	@Override
	public void newGateMatchTypeChange(HashMap sMap) throws Exception {
		// TODO Auto-generated method stub
		gateInfoDAO.newGateMatchTypeChange(sMap);
	}

	@Override
	public List<ComboVO> getFloorListToCenter(String siteId) throws Exception {
		// TODO Auto-generated method stub
		return gateInfoDAO.getFloorListToCenter(siteId);
	}

	@Override
	public List<DeviceInfoVO> getNewGateListToFloor(FloorVO vo) throws Exception {
		// TODO Auto-generated method stub
		return gateInfoDAO.getNewGateListToFloor(vo);
	}

	@Override
	public void updateNewGatePosition(DeviceInfoVO devicePositionVO) throws Exception {
		// TODO Auto-generated method stub
		gateInfoDAO.updateNewGatePosition(devicePositionVO);
	}

	@Override
	public void resetGatePosition(DeviceInfoVO vo) throws Exception {
		// TODO Auto-generated method stub
		gateInfoDAO.resetGatePosition(vo);
	}

}
