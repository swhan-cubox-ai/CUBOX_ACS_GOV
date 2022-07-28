package aero.cubox.cmmn.service.impl;


import aero.cubox.cmmn.service.FileImageZipService;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("FileImageZipService")
public class FileImageZipServiceImpl implements FileImageZipService {

	@Resource(name = "fileImageZipDAO")
	private FileImageZipDAO fileImageZipDAO;

	@Override
	public List<UserBioInfoVO> getUserInfoAllList(UserInfoVO vo) throws Exception {
		return fileImageZipDAO.selectUserInfoAllList(vo);
	}

	@Override
	public UserBioInfoVO getUserBioInfo(UserBioInfoVO vo) throws Exception {
		return fileImageZipDAO.selectUserBioInfo(vo);
	}

	@Override
	public HashMap getZipSetPw() throws Exception {
		return fileImageZipDAO.getZipSetPw();
	}

}
