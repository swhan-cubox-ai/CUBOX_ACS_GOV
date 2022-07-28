package aero.cubox.auth.service;

import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;

import java.util.List;

public interface AuthInfoService {


	
	/**
	 * 사용자정보가져오기 
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	UserInfoVO getUserInfo(String fuid) throws Exception;
	
	/**
	 * 사용자정보가져오기 
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	UserInfoVO getUserInfo2(UserInfoVO vo) throws Exception;

	/**
	 * 카드번호 가져오기
	 * @param String
	 * @return List<CardInfoVO>
	 * @throws Exception
	 */
	List<CardInfoVO> getCardInfoList(String fuid) throws Exception;




}
