package aero.cubox.sample.service;

import java.util.List;

import aero.cubox.sample.service.vo.GateVO;
import aero.cubox.sample.service.vo.FloorVO;

public interface DeviceInfoService {

	/**
	 * 단말기 가져오기 device_info_tb
	 * @param GateVO
	 * @return GateVO
	 * @throws Exception
	 */
	public List<GateVO> getDeviceInfoList(GateVO vo) throws Exception;

	
	/**
	 * 단말기 등록 device_info_tb
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public int saveDeviceInfo(GateVO vo) throws Exception;

	/**
	 * 사용여부 수정
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public int updateDeviceInfoUseYN(GateVO vo) throws Exception;

	/**
	 * 단말기 목록 cnt
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public int getDeviceInfoListCnt(GateVO vo) throws Exception;

	/**
	 * 단말기 정보 수정 device_info_tb
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public int updateDeviceInfo(GateVO vo) throws Exception;

	/**
	 * 층 위치  idx 
	 * @param GateVO
	 * @return int
	 * @throws Exception
	 */
	public String getFloorIdx(FloorVO fvo) throws Exception; 
	
}
