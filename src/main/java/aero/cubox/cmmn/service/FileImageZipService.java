package aero.cubox.cmmn.service;

import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;

import java.util.HashMap;
import java.util.List;

public interface FileImageZipService {
    public List<UserBioInfoVO> getUserInfoAllList (UserInfoVO vo) throws Exception;

	public UserBioInfoVO getUserBioInfo(UserBioInfoVO uv) throws Exception;

	public HashMap getZipSetPw() throws Exception;
}
