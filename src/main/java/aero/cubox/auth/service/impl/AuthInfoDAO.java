package aero.cubox.auth.service.impl;

import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("authInfoDAO")
public class AuthInfoDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthInfoDAO.class);
	
	private static final String sqlNameSpace = "authInfo.";
	
	/**
	 * 사용자정보가져오기 
	 * @param fuid
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo(String fuid) {
		return selectOne(sqlNameSpace+"getUserInfo", fuid);
	}
	
	/**
	 * 사용자정보가져오기 
	 * @param vo
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	public UserInfoVO getUserInfo2(UserInfoVO vo) {
		return selectOne(sqlNameSpace+"getUserInfo2", vo);
	}

	/**
	 * 카드번호 가져오기
	 * @param fuid
	 * @return List<CardInfoVO>
	 * @throws Exception
	 */
	public List<CardInfoVO> getCardInfoList(String fuid) {
		return selectList(sqlNameSpace+"getCardInfoList", fuid);
	}
	

}
