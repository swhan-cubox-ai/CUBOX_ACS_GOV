package aero.cubox.auth.service.impl;

import aero.cubox.auth.service.AuthInfoService;
import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("authInfoService")
public class AuthInfoServiceImpl extends EgovAbstractServiceImpl implements AuthInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthInfoServiceImpl.class);
	
	/** memberDAO */
	@Resource(name = "authInfoDAO")
	private AuthInfoDAO authInfoDAO;

	/**
	 * 사용자정보가져오기 
	 * @param fuid
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public UserInfoVO getUserInfo(String fuid) throws Exception {
		// TODO Auto-generated method stub
		return  authInfoDAO.getUserInfo(fuid);
	}
	
	/**
	 * 사용자정보가져오기 
	 * @param vo
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public UserInfoVO getUserInfo2(UserInfoVO vo) throws Exception {
		// TODO Auto-generated method stub
		return  authInfoDAO.getUserInfo2(vo);
	}
	

	/**
	 * 카드번호 가져오기
	 * @param fuid
	 * @return List<CardInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<CardInfoVO> getCardInfoList(String fuid) throws Exception {
		// TODO Auto-generated method stub
		return  authInfoDAO.getCardInfoList(fuid);
	}


}
