package aero.cubox.sample.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import aero.cubox.sample.service.DeviceInfoService;
import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateVO;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("deviceInfoService")
@EnableAsync
public class DeviceInfoServiceImpl extends EgovAbstractServiceImpl implements DeviceInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoServiceImpl.class);

	/** terminalInfoDAO */
	@Resource(name = "deviceInfoDAO")
	private DeviceInfoDAO deviceInfoDAO;


	/**
	 * 단말기 가져오기 device_info_tb
	 * @param GateVO
	 * @return GateVO
	 * @throws Exception
	 */
	@Override
	public List<GateVO> getDeviceInfoList(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		return deviceInfoDAO.getDeviceInfoList(vo);
	}

	@Override
	public int saveDeviceInfo(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		return deviceInfoDAO.saveDeviceInfo(vo);
	}

	/**
	 * 사용여부 수정
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateDeviceInfoUseYN(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		return deviceInfoDAO.updateDeviceInfoUseYN(vo);
	}

	/**
	 * 단말기 목록 CNT
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getDeviceInfoListCnt(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		return deviceInfoDAO.getDeviceInfoListCnt(vo);
	}

	/**
	 *  단말기 정보 수정 device_info_tb
	 */
	@Override
	public int updateDeviceInfo(GateVO vo) throws Exception {
		// TODO Auto-generated method stub
		return deviceInfoDAO.updateDeviceInfo(vo);
	}

	/**
	 * 층 위치  idx 
	 */
	@Override
	public String getFloorIdx(FloorVO fvo) throws Exception {
		// TODO Auto-generated method stub
		return deviceInfoDAO.getFloorIdx(fvo);
	}
	

}
