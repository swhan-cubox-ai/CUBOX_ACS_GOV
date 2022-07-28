package aero.cubox.sample.service.impl;

import java.util.List;

import aero.cubox.sample.service.vo.FloorVO;
import aero.cubox.sample.service.vo.GateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("deviceInfoDAO")
public class DeviceInfoDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInfoDAO.class);

	private static final String sqlNameSpace = "deviceInfo.";
	
	/**
	 * 단말기 가져오기 device_info_tb
	 * @param GateVO
	 * @return GateVO
	 * @throws Exception
	 */
	public List<GateVO> getDeviceInfoList(GateVO vo) {
		return selectList(sqlNameSpace+"selectDeviceInfoList", vo);
	}

	public int saveDeviceInfo(GateVO vo) {
		return insert(sqlNameSpace+"insertDeviceInfo", vo);
	}


	public int updateDeviceInfoUseYN(GateVO vo) {
		return insert(sqlNameSpace+"updateDeviceInfoUseYN", vo);
	}

	/**
	 * 단말기 가져오기 device_info_tb cnt
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public int getDeviceInfoListCnt(GateVO vo) {
		return selectOne(sqlNameSpace+"selectDeviceInfoListCnt", vo);
	}

	/**
	 * 단말기 정보 수정 device_info_tb
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public int updateDeviceInfo(GateVO vo) {
		return update(sqlNameSpace+"updateDeviceInfo",vo);
	}

	public String getFloorIdx(FloorVO fvo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getFloorIdx",fvo);
	}

}
