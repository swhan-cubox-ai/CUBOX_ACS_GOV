package aero.cubox.cmmn.service.impl;


import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class FileImageZipDAO extends EgovAbstractMapper {

	public List<UserBioInfoVO> selectUserInfoAllList(UserInfoVO vo) throws Exception {
        return selectList("schedule.selectUserInfoAllList", vo);
	}

	public UserBioInfoVO selectUserBioInfo(UserBioInfoVO vo) throws Exception {
		return selectOne("schedule.selectUserBioInfo", vo);
	}

	public HashMap getZipSetPw() throws Exception {
		return selectOne("schedule.selectZipSetPw");
	}
}
