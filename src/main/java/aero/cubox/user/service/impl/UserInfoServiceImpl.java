package aero.cubox.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import aero.cubox.auth.service.AuthorGroupService;
import aero.cubox.auth.service.vo.AuthGroupVO;
import aero.cubox.auth.service.vo.AuthorGroupVO;
import aero.cubox.sample.service.vo.CardInfoVO;
import aero.cubox.sample.service.vo.DownloadLogVO;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogInfoVO;
import aero.cubox.user.service.UserInfoService;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import aero.cubox.util.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aero.cubox.util.FileConvertToJsp;
import aero.cubox.auth.service.impl.AuthorGroupDAO;
import aero.cubox.sample.service.impl.LogInfoDAO;
import aero.cubox.cmmn.service.CommonService;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("userInfoService")
public class UserInfoServiceImpl extends EgovAbstractServiceImpl implements UserInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);
	private String GLOBAL_AES256_KEY = CuboxProperties.getProperty("Globals.aes256.key");

	@Resource(name = "userInfoDAO")
	private UserInfoDAO userInfoDAO;
	
	@Resource(name = "authorGroupDAO")
	private AuthorGroupDAO authorGroupDAO;
	
	@Resource(name = "logInfoDAO")
	private LogInfoDAO logInfoDAO;

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;
	
	@Resource(name = "authorGroupService")
	private AuthorGroupService authorGroupService;
	
	@Resource(name = "userChgLogDAO")
	private UserChgLogDAO userChgLogDAO;
	
	/**
	 * 출입자관리 조회
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<UserInfoVO> selectUserList(UserInfoVO vo) throws Exception {
		return userInfoDAO.selectUserList(vo);
	}
	
	/**
	 * 출입자관리 조회 전체건수
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public int selectUserListCount(UserInfoVO vo) throws Exception {
		return userInfoDAO.selectUserListCount(vo);
	}
	
	/**
	 * 출입자관리 검색목록 엑셀저장
	 * @param UserInfoVO
	 * @return List<ExcelVO>
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> selectUserListExcel(UserInfoVO vo) throws Exception{
		return userInfoDAO.selectUserListExcel(vo);
	}

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<UserInfoVO> getUserInfoList3(UserInfoVO vo) throws Exception {
		return userInfoDAO.getUserInfoList3(vo);
	}


	/**
	 * 사용자전체cnt
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getUserTotalCnt3(UserInfoVO vo) throws Exception {
		return userInfoDAO.getUserTotalCnt3(vo);
	}

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return UserInfoVO
	 * @throws Exception
	 */
	@Override
	public UserInfoVO getUserInfo(String fuid) throws Exception {
		return userInfoDAO.getUserInfo(fuid);
	}

	/**
	 * 사용자정보가져오기
	 * @param UserInfoVO
	 * @return UserInfoVO
	 * @throws Exception
	 */
	@Override
	public UserInfoVO getUserInfo2(UserInfoVO vo) throws Exception{
		return userInfoDAO.getUserInfo2(vo);
	}

	/**
	 * DB이미지가져오기
	 * @param String
	 * @return UserBioInfoVO
	 * @throws Exception
	 */
	@Override
	public UserBioInfoVO getUserBioInfo(String fuid) throws Exception{
		return userInfoDAO.getUserBioInfo(fuid);
	}

	/**
	 * 카드번호 가져오기
	 * @param String
	 * @return List<CardInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<CardInfoVO> getCardInfoList(String fuid) throws Exception{
		return userInfoDAO.getCardInfoList(fuid);
	}

	/**
	 * 유저권한그룹 가져오기(대전)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	@Override
	public List<AuthGroupVO> getUserAuthGroupList11(AuthGroupVO vo) throws Exception{
		return userInfoDAO.getUserAuthGroupList11(vo);
	}

	/**
	 * 유저권한그룹 가져오기(광주)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	@Override
	public List<AuthGroupVO> getUserAuthGroupList12(AuthGroupVO vo) throws Exception{
		return userInfoDAO.getUserAuthGroupList12(vo);
	}

	/**
	 * 전체권한그룹 가져오기(대전)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	@Override
	public List<AuthGroupVO> getTotalAuthGroupList11(AuthGroupVO vo) throws Exception{
		return userInfoDAO.getTotalAuthGroupList11(vo);
	}

	/**
	 * 전체권한그룹 가져오기(광주)
	 * @param AuthGroupVO
	 * @return List<AuthGroupVO>
	 * @throws Exception
	 */
	@Override
	public List<AuthGroupVO> getTotalAuthGroupList12(AuthGroupVO vo) throws Exception{
		return userInfoDAO.getTotalAuthGroupList12(vo);
	}

	/**
	 * 카드정보가져오기
	 * @param String
	 * @return CardInfoVO
	 * @throws Exception
	 */
	@Override
	public CardInfoVO getCardInfo(CardInfoVO vo) throws Exception{
		return userInfoDAO.getCardInfo(vo);
	}

	/**
	 * 카드정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int cardInfoSave(CardInfoVO vo) throws Exception{
		return userInfoDAO.cardInfoSave(vo);
	}

	/**
	 * 카드중복체크
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getCdnoCnt(CardInfoVO vo) throws Exception{
		return userInfoDAO.getCdnoCnt(vo);
	}

	/**
	 * 카드추가
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addUserCdno(CardInfoVO vo) throws Exception{
		return userInfoDAO.addUserCdno(vo);
	}

	/**
	 * 유저fsstatus변경
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userFsstatusChange(UserInfoVO vo) throws Exception{
		return userInfoDAO.userFsstatusChange(vo);
	}

	/**
	 * tauthtogate_main테이블 fsstatus변경
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int tauthtogateMainFsstatusChange(CardInfoVO vo) throws Exception{
		return userInfoDAO.tauthtogateMainFsstatusChange(vo);
	}

	/**
	 * 사용자정보-전화번호 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoPhoneUpdate(UserInfoVO vo) throws Exception{
		return userInfoDAO.userInfoPhoneUpdate(vo);
	}

	/**
	 * 사용자정보-차번호 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoCarnoUpdate(UserInfoVO vo) throws Exception{
		return userInfoDAO.userInfoCarnoUpdate(vo);
	}

	/**
	 * 사용자정보- tcmlinfo - fvname = 'nirscar11' 수정 / 광주는 주차없음
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoCarnoCmlUpdate() throws Exception{
		return userInfoDAO.userInfoCarnoCmlUpdate();
	}

	/**
	 * 사용자정보-비고수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoEtcUpdate(UserInfoVO vo) throws Exception{
		return userInfoDAO.userInfoEtcUpdate(vo);
	}

	/**
	 * 권한그룹코드 가져오기(ftid)
	 * @param
	 * @return String
	 * @throws Exception
	 */
	@Override
	public String getAuthGroupFtid(String authGroupNm) throws Exception{
		return userInfoDAO.getAuthGroupFtid(authGroupNm);
	}

	/**
	 * 신규 사용자 정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int newUserInfoSave(UserInfoVO vo) throws Exception{
		return userInfoDAO.newUserInfoSave(vo);
	}

	/**
	 * fuid, funm 중복체크
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getFuidFunmCnt(UserInfoVO vo) throws Exception{
		return userInfoDAO.getFuidFunmCnt(vo);
	}

	/**
	 * 사용자 엑셀 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int excelUserInfoUpdate(UserInfoVO vo) throws Exception{
		return userInfoDAO.excelUserInfoUpdate(vo);
	}

	/**
	 * 카드 엑셀 수정
	 * @param UserInfoVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int excelCardInfoUpdate(CardInfoVO vo) throws Exception{
		return userInfoDAO.excelCardInfoUpdate(vo);
	}


	/**
	 * 사용자 이미지 업로드(신규)
	 * @param UserBioInfoVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int imgUserInfoSave(UserBioInfoVO vo) throws Exception {
		int rtn = 0;
		/******* 이미지 변환 및 암호화 시작 ********/
		byte[] bioImgbyte = vo.getImgByte();
		String strResize = vo.getResizeYn();
		if(bioImgbyte != null && bioImgbyte.length > 0) {
			if(strResize == null || !strResize.equals("N")) {
				try {
					bioImgbyte = FileConvertToJsp.getInstance().imgConvertToJsp(bioImgbyte);	//byte[] imageByte fopmat 변경
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.debug("jpg/jpeg 이미지 변환 실패");
				}
			}
			AES256Util aes256 = null;
			try {
				aes256 = new AES256Util();
				String imgString = aes256.byteArrEncode(bioImgbyte, GLOBAL_AES256_KEY);
				vo.setFimg(imgString);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				aes256 = null;
			}
		}
		/******* 이미지 변환 및 암호화 완료 ********/
		int bioCnt = getBioCnt(vo.getFuid());
		vo.setBioCnt(String.valueOf(bioCnt));
		commonService.tcsidxSave("tbio_main"); 				//tcsidx테이블 fidx추가
    	vo.setFsidx(commonService.getFsidx("tbio_main"));
        if(bioCnt > 0) {
        	rtn += userInfoDAO.imgUserInfoUpload(vo);
        } else {
        	rtn += userInfoDAO.imgUserInfoSave(vo);
        }

        int bioLinkCnt = getBioLinkCnt(vo.getFuid());
        commonService.tcsidxSave("tbio_main"); 				//tcsidx테이블 fidx추가
    	vo.setFsidx(commonService.getFsidx("tbio_main"));
        if(bioLinkCnt > 0){
        	rtn += userInfoDAO.imgUserInfoLinkUpload(vo);
        } else {
        	rtn += userInfoDAO.imgUserInfoLinkSave(vo);
        }

        if(rtn > 0) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("fuid", vo.getFuid());
        	userInfoDAO.updateUserExpire(map);
        	
        	/* 2021-02-01 usermain Q로 update 하므로 다시 실행할 필요 없음
        	//동기화 :: 이미지 변경 및 사용자 정보 변경 시 SyncDevice 권한 등록
        	AuthorGroupVO avo = new AuthorGroupVO();
        	avo.setFuid(vo.getFuid());
        	int rstSync = authorGroupService.addSyncDeviceUpdateUser(avo);
        	*/
        }
		return rtn;
	}



	/**
	 * 사진중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getBioCnt(String fuid) throws Exception{
		return userInfoDAO.getBioCnt(fuid);
	}

	/**
	 * 사진중복체크
	 * @param String
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getBioLinkCnt(String fuid) throws Exception{
		return userInfoDAO.getBioLinkCnt(fuid);
	}

	/**
	 * 타센터유저정보리스트 가져오기(대전)
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<UserInfoVO> getOtherCenterUserInfoList11(String srchFunmWord) throws Exception{
		return userInfoDAO.getOtherCenterUserInfoList11(srchFunmWord);
	}

	/**
	 * 타센터유저정보리스트 가져오기(광주)
	 * @param
	 * @return List<UserInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<UserInfoVO> getOtherCenterUserInfoList12(String srchFunmWord) throws Exception{
		return userInfoDAO.getOtherCenterUserInfoList12(srchFunmWord);
	}

	/**
	 * 출입권한목록 조회수 가져오기
	 * @param LogInfoVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getUsrListConnTotalCnt(LogInfoVO vo) throws Exception {
		return userInfoDAO.getUsrListConnTotalCnt(vo);
	}

	/**
	 * 출입권한목록 목록 가져오기
	 * @param LogInfoVO
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<LogInfoVO> getUsrListConnPop(LogInfoVO vo) throws Exception {
		return userInfoDAO.getUsrListConnPop(vo);
	}

	/**
	 * 이미지 있는 사용자 목록 전체 조회
	 * @param LogInfoVO
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<UserBioInfoVO> getUserInfoAllBioList(UserInfoVO vo) throws Exception {
		return userInfoDAO.getUserInfoAllBioList(vo);
	}

	/**
	 * 사용자정보저장하기
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoSave(UserInfoVO vo) throws Exception {
		int rnt = 0;
		//String strCf = vo.getCfstatus();
		//D:delete, E:expired, L:lost, W:wait, A:active
		//if(strCf !=null && (("DELW").contains(strCf))) vo.setFsstatus("K");
		//else vo.setFsstatus("U");
		vo.setFsstatus("U");
		rnt = userInfoDAO.cardInfoUpdate(vo);		//사용자 카드 정보 수정

		if(rnt < 1) {
			LOGGER.error("###[userInfoSave][update tcare_main] rnt:{}, vo:{}", rnt, vo);
			throw new RuntimeException("출입자 정보(카드) 변경 중 오류가 발생했습니다.");			
		}
		
		//if(strCf !=null && (("DELW").contains(strCf))) vo.setFsstatus("K");
		//else vo.setFsstatus("R");
		vo.setFsstatus("Q");
		rnt = userInfoDAO.userInfoSave(vo); 	//사용자 정보 수정

		if(rnt < 1) {
			LOGGER.error("###[userInfoSave][update tuserinfo_main(Q)] rnt:{}, vo:{}", rnt, vo);
			throw new RuntimeException("출입자 정보 변경 중 오류가 발생했습니다.");						
		}
		
		/* 2021-01-28 서버에서 동기화 프로그램이 돌고 있어서 여기에서 처리할 필요없음!(khlee) **************
		if(rnt > 0) {
			//동기화 :: 이미지 변경 및 사용자 정보 변경 시 SyncDevice 권한 등록
			AuthorGroupVO avo = new AuthorGroupVO();
        	avo.setFuid(vo.getFuid());
        	avo.setSiteId(vo.getFpartcd1());
        	int rstSync = authorGroupService.addSyncDeviceUpdateUser(avo);
		}********************************************************************/
		
		return rnt;
	}
	
	@Override
	public int userInfoSaveNew(UserInfoVO uvo, UserBioInfoVO bvo, CardInfoVO cvo) throws Exception {
		
		if(uvo.getFbioyn().equals("Y")) {
			int rnt = imgUserInfoSave(bvo);
			LOGGER.debug("###[userInfoSaveNew][바이오 수정] rnt:{}, cvo:{}", rnt, cvo);
			if(rnt < 1) {
				LOGGER.error("###[userInfoSaveNew][바이오 수정] rnt:{}, cvo:{}", rnt, cvo);
				throw new RuntimeException("출입자 정보(사진) 변경 중 오류가 발생했습니다.");			
			}
		}

		cvo.setFsstatus("U");
		int rnt2 = userInfoDAO.updateCardInfo(cvo);
		LOGGER.debug("###[userInfoSaveNew][카드 수정] rnt2:{}, cvo:{}", rnt2, cvo);
		if(rnt2 < 1) {
			LOGGER.error("###[userInfoSaveNew][카드 수정] rnt2:{}, cvo:{}", rnt2, cvo);
			throw new RuntimeException("출입자 정보(카드) 변경 중 오류가 발생했습니다.");			
		}

		uvo.setFsstatus("Q");
		int rnt3 = userInfoDAO.userInfoSave(uvo);
		LOGGER.debug("###[userInfoSaveNew][개인정보 수정] rnt3:{}, uvo:{}", rnt3, uvo);
		if(rnt3 < 1) {
			LOGGER.error("###[userInfoSaveNew][개인정보 수정] rnt3:{}, uvo:{}", rnt3, uvo);
			throw new RuntimeException("출입자 정보 변경 중 오류가 발생했습니다.");						
		}
		
		return rnt3;
	}	
	
	/**
	 * 사용자정보 삭제
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoDel(UserInfoVO vo) throws Exception {
		int rnt = 0;
		int cnt = 0;
		
		HashMap map = new HashMap();
		map.put("fuid", vo.getFuid());
		map.put("fpartcd1", vo.getFpartcd1());
		map.put("fmodid", vo.getFmodid());

		cnt = userInfoDAO.updateCardNotUsed(map);
		if(cnt > 0) {
			rnt = userInfoDAO.updateUserNotUsed(map);
			if(rnt > 0) {
				AuthorGroupVO avo = new AuthorGroupVO();
				avo.setFuid(vo.getFuid());
				avo.setSiteId(vo.getFpartcd1());
				int a = authorGroupDAO.insertUserAuthGroupLog(avo); // 로그
				int b = authorGroupDAO.deleteUserAuthGroupInfoNotUsed(map);
				if(a == b) {
					cnt = authorGroupDAO.deleteSyncDeviceNotUsed(map);
					if((a > 0 && cnt > 0) || (a == 0 && cnt == 0)) {
					} else {
						LOGGER.error("###[userInfoDel] deleteSyncDeviceNotUsed(map) : {}", map);
						throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(4)");			
					}
				} else {
					LOGGER.error("###[userInfoDel] deleteUserAuthGroupInfoNotUsed(map) : {}", map);
					throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(3)");			
				}
			} else {
				LOGGER.error("###[userInfoDel] updateUserNotUsed(map) : {}", map);
				throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(2)");				
			}
		} else {
			LOGGER.error("###[userInfoDel] updateCardNotUsed(map) : {}", map);
			throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(1)");
		}
		
		return rnt;
	}	
	
	@Override
	public int userInfoDel(UserInfoVO vo, Map<String, Object> param) throws Exception {
		int rnt = 0;
		int cnt = 0;

		//2021-07-12
		Map<String, Object> log = userChgLogDAO.selectUserDelInfoForLog(vo);
		if(StringUtil.nvl(log.get("fuid")).equals("")) {
			throw new RuntimeException(String.format("해당 출입자 정보가 존재하지 않습니다.(%s)", vo.getFuid()));
		}
		JSONObject json = JsonUtil.getJsonStringFromMap(log);
		
		//2021-08-10
		Map<String, Object> logBio = userChgLogDAO.selectBioInfoForLog(vo);
		
		HashMap map = new HashMap();
		map.put("fuid", vo.getFuid());
		map.put("fpartcd1", vo.getFpartcd1());
		map.put("fmodid", vo.getFmodid());

		cnt = userInfoDAO.updateCardNotUsed(map);
		if(cnt > 0) {
			rnt = userInfoDAO.updateUserNotUsed(map);
			if(rnt > 0) {
				AuthorGroupVO avo = new AuthorGroupVO();
				avo.setFuid(vo.getFuid());
				avo.setSiteId(vo.getFpartcd1());
				int a = authorGroupDAO.insertUserAuthGroupLog(avo); // 로그
				int b = authorGroupDAO.deleteUserAuthGroupInfoNotUsed(map);
				LOGGER.error("###[userInfoDel] 1) a = {}, b = {}", a, b);
				if(a == b) {
					cnt = authorGroupDAO.deleteSyncDeviceNotUsed(map);
					LOGGER.error("###[userInfoDel] 2) a = {}, b = {}, cnt = {}", a, b, cnt);
					if((a > 0 && cnt > 0) || (a == 0 && cnt == 0)) {
					} else {
						LOGGER.error("###[userInfoDel] deleteSyncDeviceNotUsed(map) : {}", map);
						throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(4)");
					}
				} else {
					LOGGER.error("###[userInfoDel] deleteUserAuthGroupInfoNotUsed(map) : {}", map);
					throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(3)");
				}
			} else {
				LOGGER.error("###[userInfoDel] updateUserNotUsed(map) : {}", map);
				throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(2)");
			}
		} else {
			LOGGER.error("###[userInfoDel] updateCardNotUsed(map) : {}", map);
			throw new RuntimeException("출입자 삭제 처리 중 오류가 발생했습니다.(1)");
		}

		//2021-07-12 이력남기기
		int newKey = userChgLogDAO.selectNewUserChgSeq();
		param.put("chg_seq" , newKey);
		param.put("fpartcd1", log.get("fpartcd1"));		
		param.put("fuid"    , log.get("fuid"));
		param.put("funm"    , log.get("funm"));
		param.put("fcdno"   , log.get("fcdno"));
		param.put("chg_cnts", json.toString());		
		LOGGER.debug("###[userInfoDel] 출입자삭제 로그 param : {}", param);		
		
		int iChgLogCnt = userChgLogDAO.insertUserChgLog2(param);
		LOGGER.debug("###[userInfoDel] 출입자삭제 로그 iChgLogCnt : {}", iChgLogCnt);
		
		if(StringUtil.nvl(logBio.get("bio_yn")).equals("Y")) {
			logBio.put("chg_seq", newKey);
			int iChgBioCnt = userChgLogDAO.insertUserChgBio(logBio);
			LOGGER.debug("###[userInfoDel] 출입자삭제 로그 iChgBioCnt : {}", iChgBioCnt);		
		}
		
		return rnt;
	}	
	
	/**
	 * 출입자 완전삭제
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int userInfoDrop(UserInfoVO vo) throws Exception {
		int rnt = -1;
		int cnt = -1;

		HashMap map = new HashMap();
		map.put("fuid", vo.getFuid());
		map.put("fpartcd1", vo.getFpartcd1());
		
		cnt = logInfoDAO.selectGateLogTotCntByUser(map);
		if(cnt > 0) {
			LOGGER.error("###[출입자완전삭제] (0/4)tgatelog {} cnt:{}", map, cnt);
			throw new RuntimeException("출입이력이 존재하여 영구삭제처리를 할 수 없습니다.");
		}
		
		rnt = userInfoDAO.deleteBiolinkInfo(map);
		LOGGER.error("###[출입자완전삭제] (1/4)tbiolink {} rnt:{}", map, rnt);
		rnt = userInfoDAO.deleteBioInfo(map);
		LOGGER.error("###[출입자완전삭제] (2/4)tbio_main {} rnt:{}", map, rnt);
		// 출입이력이 없는 경우, delete 처리함.
		rnt = userInfoDAO.deleteCardInfo(map);
		LOGGER.error("###[출입자완전삭제] (3/4)tcard_main {} rnt:{}", map, rnt); 
		rnt = userInfoDAO.deleteUserInfo(map);
		LOGGER.error("###[출입자완전삭제] (4/4)tuserinfo_main {} rnt:{}", map, rnt);
		
		return rnt;
	}	
	
	@Override
	public int userInfoDrop(UserInfoVO vo, Map<String, Object> param) throws Exception {
		int rnt = -1;
		int cnt = -1;
		
		//2021-07-12
		Map<String, Object> log = userChgLogDAO.selectUserDelInfoForLog(vo);
		JSONObject json = JsonUtil.getJsonStringFromMap(log);
		
		//2021-08-10
		Map<String, Object> logBio = userChgLogDAO.selectBioInfoForLog(vo);	

		HashMap map = new HashMap();
		map.put("fuid", vo.getFuid());
		map.put("fpartcd1", vo.getFpartcd1());
		
		cnt = logInfoDAO.selectGateLogTotCntByUser(map);
		if(cnt > 0) {
			LOGGER.error("###[출입자완전삭제] (0/4)tgatelog {} cnt:{}", map, cnt);
			throw new RuntimeException("출입이력이 존재하여 영구삭제처리를 할 수 없습니다.");
		}
		
		rnt = userInfoDAO.deleteBiolinkInfo(map);
		LOGGER.error("###[출입자완전삭제] (1/4)tbiolink {} rnt:{}", map, rnt);
		rnt = userInfoDAO.deleteBioInfo(map);
		LOGGER.error("###[출입자완전삭제] (2/4)tbio_main {} rnt:{}", map, rnt);
		// 출입이력이 없는 경우, delete 처리함.
		rnt = userInfoDAO.deleteCardInfo(map);
		LOGGER.error("###[출입자완전삭제] (3/4)tcard_main {} rnt:{}", map, rnt); 
		rnt = userInfoDAO.deleteUserInfo(map);
		LOGGER.error("###[출입자완전삭제] (4/4)tuserinfo_main {} rnt:{}", map, rnt);
		
		//2021-07-12 이력남기기
		int newKey = userChgLogDAO.selectNewUserChgSeq();
		param.put("chg_seq" , newKey);
		param.put("fpartcd1", log.get("fpartcd1"));		
		param.put("fuid"    , log.get("fuid"));
		param.put("funm"    , log.get("funm"));
		param.put("fcdno"   , log.get("fcdno"));
		param.put("chg_cnts", json.toString());		
		LOGGER.debug("###[userInfoDrop] 영구삭제 로그 param : {}", param);		
		int iChgLogCnt = userChgLogDAO.insertUserChgLog2(param);
		LOGGER.debug("###[userInfoDrop] 영구삭제 로그 iChgLogCnt : {}", iChgLogCnt);
		
		if(StringUtil.nvl(logBio.get("bio_yn")).equals("Y")) {
			logBio.put("chg_seq", newKey);
			int iChgBioCnt = userChgLogDAO.insertUserChgBio(logBio);
			LOGGER.debug("###[userInfoDrop] 영구삭제 로그 iChgBioCnt : {}", iChgBioCnt);
		}
		
		return rnt;
	}	
	
	/**
	 * 사용자정보 복원
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int userInfoReco(UserInfoVO vo) throws Exception {
		int rnt = 0;
		
		HashMap map = new HashMap();
		map.put("fuid", vo.getFuid());
		map.put("fpartcd1", vo.getFpartcd1());
		map.put("fmodid", vo.getFmodid());

		rnt = userInfoDAO.updateCardReco(map);
		if(rnt > 0) {
			rnt = userInfoDAO.updateUserReco(map);
			if(rnt > 0) {
				// 2021-02-02 임직원 기본권한 적용
				if(StringUtil.nvl(vo.getFutype()).equals("1")) { // 임직원인 경우
					AuthorGroupVO avo = new AuthorGroupVO();
					avo.setFuid(vo.getFuid());
					avo.setSiteId(vo.getFpartcd1());
					avo.setRegistId(vo.getFmodid());					
					int a = userInfoDAO.insertUserAuthGroupForNewEmp(avo);
					
					if(a > 0) {
						int b = authorGroupDAO.insertSyncDeviceByFuid(avo);
						if(b > 0) {
						} else {
							LOGGER.error("###[userInfoReco] insertSyncDeviceByFuid(avo) : {}", avo);
							throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(4)");	
						}
					} else {
						LOGGER.error("###[userInfoReco] insertUserAuthGroupForNewEmp(avo) : {}", avo);
						throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(3)");	
					}
				}				
			} else {
				LOGGER.error("###[userInfoReco] updateUserReco(map) : {}", map);
				throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(2)");				
			}
		} else {
			LOGGER.error("###[userInfoReco] updateCardReco(map) : {}", map);
			throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(1)");
		}
		
		return rnt;
	}
	
	@Override
	public int userInfoReco(UserInfoVO vo, Map<String, Object> param) throws Exception {
		int rnt = 0;
		
		//2021-07-12
		Map<String, Object> log = userChgLogDAO.selectUserDelInfoForLog(vo);
		JSONObject json = JsonUtil.getJsonStringFromMap(log);
		
		//2021-08-10
		Map<String, Object> logBio = userChgLogDAO.selectBioInfoForLog(vo);	
		
		HashMap map = new HashMap();
		map.put("fuid", vo.getFuid());
		map.put("fpartcd1", vo.getFpartcd1());
		map.put("fmodid", vo.getFmodid());

		rnt = userInfoDAO.updateCardReco(map);
		if(rnt > 0) {
			rnt = userInfoDAO.updateUserReco(map);
			if(rnt > 0) {
				// 2021-02-02 임직원 기본권한 적용
				if(StringUtil.nvl(vo.getFutype()).equals("1")) { // 임직원인 경우
					AuthorGroupVO avo = new AuthorGroupVO();
					avo.setFuid(vo.getFuid());
					avo.setSiteId(vo.getFpartcd1());
					avo.setRegistId(vo.getFmodid());					
					int a = userInfoDAO.insertUserAuthGroupForNewEmp(avo);
					
					if(a > 0) {
						int b = authorGroupDAO.insertSyncDeviceByFuid(avo);
						if(b > 0) {
						} else {
							LOGGER.error("###[userInfoReco] insertSyncDeviceByFuid(avo) : {}", avo);
							throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(4)");	
						}
					} else {
						LOGGER.error("###[userInfoReco] insertUserAuthGroupForNewEmp(avo) : {}", avo);
						throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(3)");	
					}
				}				
			} else {
				LOGGER.error("###[userInfoReco] updateUserReco(map) : {}", map);
				throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(2)");				
			}
		} else {
			LOGGER.error("###[userInfoReco] updateCardReco(map) : {}", map);
			throw new RuntimeException("출입자 복원 처리 중 오류가 발생했습니다.(1)");
		}
		
		//2021-07-12 이력남기기
		int newKey = userChgLogDAO.selectNewUserChgSeq();
		param.put("chg_seq" , newKey);
		param.put("fpartcd1", log.get("fpartcd1"));		
		param.put("fuid"    , log.get("fuid"));
		param.put("funm"    , log.get("funm"));
		param.put("fcdno"   , log.get("fcdno"));
		param.put("chg_cnts", json.toString());		
		LOGGER.debug("###[userInfoReco] 출입자복원 로그 param : {}", param);		
		
		int iChgLogCnt = userChgLogDAO.insertUserChgLog2(param);
		LOGGER.debug("###[userInfoReco] 출입자복원 로그 iChgLogCnt : {}", iChgLogCnt);
		
		if(StringUtil.nvl(logBio.get("bio_yn")).equals("Y")) {
			logBio.put("chg_seq", newKey);
			int iChgBioCnt = userChgLogDAO.insertUserChgBio(logBio);
			LOGGER.debug("###[userInfoReco] 출입자복원 로그 iChgBioCnt : {}", iChgBioCnt);			
		}
		
		return rnt;
	}		
	

	/**
	 * 선택된 사용자 전체 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int chkUserInfoLstSave(UserInfoVO vo) throws Exception {
		// ** 사용자 정보만 R로 변경해도 사용자정보에 따른 카드정보를 자동으로 가져간다고 함 (Q와 R, K의 경우만 기기에서 데이터를 확인함)
		// R과 K는 인사정보 tuserinfo_main와 tcard_main 만 가져감, Q의 경우 bio데이터도 가져감
		// R과 K의 경우 우선 순위 처리의 차이, 처리되는 프로세스는 동일
		int rnt = 0;
		vo.setFsstatus("U");
		rnt = userInfoDAO.chkCardInfoLstUpdate(vo);		//사용자 카드 정보 수정
		if(rnt > 0) {
			vo.setFsstatus("Q");
			rnt += userInfoDAO.chkUserInfoLstSave(vo); 	//사용자 정보 수정
		}
		
		if(rnt > 0) {
			//동기화 :: 이미지 변경 및 사용자 정보 변경 시 SyncDevice 권한 등록
			AuthorGroupVO avo = new AuthorGroupVO();
        	avo.setSiteId(vo.getFpartcd1());
        	avo.setFuids(vo.getFuids());
        	int rstSync = authorGroupService.addSyncDeviceUpdateUser(avo);
		}
		
		return rnt;
	}

	/**
	 * 전체 사용자 전체 수정
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int allUserInfoLstSave(UserInfoVO vo) throws Exception {
		// **사용자 정보만 R로 변경해도 사용자정보에 따른 카드정보를 자동으로 가져간다고 함 (Q와 R, K의 경우만 기기에서 데이터를 확인함)
		// R과 K는 인사정보 tuserinfo_main와 tcard_main 만 가져감, Q의 경우 bio데이터도 가져감
		// R과 K의 경우 우선 순위 처리의 차이, 처리되는 프로세스는 동일
		int rnt = 0;
		vo.setFsstatus("U");
		rnt = userInfoDAO.allCardInfoLstUpdate(vo);		//사용자 카드 정보 수정
		if(rnt > 0) {
			vo.setFsstatus("Q");
			rnt = userInfoDAO.allUserInfoLstSave(vo); 	//사용자 정보 수정
		}
		return rnt;
	}

	/**
	 * 다운로드 이력 사유 저장
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int setDownLoadResnLog (Map<String, String> m) throws Exception {
		return userInfoDAO.setDownLoadResnLog(m);
	}

	/**
	 * 다운로드 이력 사유 전체 건수
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getDownloadLogCnt(UserInfoVO vo) throws Exception {
		return userInfoDAO.getDownloadLogCnt(vo);
	}

	/**
	 * 다운로드 이력 사유 목록
	 * @param
	 * @return List<LogInfoVO>
	 * @throws Exception
	 */
	@Override
	public List<DownloadLogVO> getDownloadLogList(UserInfoVO vo) throws Exception {
		return userInfoDAO.selectDownloadLogList(vo);
	}

	public void setUserExpire(HashMap map) throws Exception{
		commonService.tcsidxSave("tbio_main"); 				//tcsidx테이블 fidx추가
		map.put("fsidx",commonService.getFsidx("tbio_main"));
		userInfoDAO.updateBioExpire(map);
		userInfoDAO.updateBiolinkExpire(map);
		userInfoDAO.updateUserExpire(map);
		userInfoDAO.updateCardExpire(map);
	}

	/**
	 * fuid 중복 오류 확인
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getChkFuid(UserInfoVO vo) throws Exception {
		return userInfoDAO.selectChkFuid(vo);
	}
	
	/**
	 * 방문객카드번호 중복 체크
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getChkFvisitnum(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectChkFvisitnum(param);
	}

	/**
	 * 사용자 추가 등록
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addUserInfoSave (UserInfoVO uvo, CardInfoVO cvo) throws Exception {
		int rst = 0;
		int ust = userInfoDAO.newUserInfoSave(uvo);
		int cdn = 0;
		if(ust > 0) {
			rst ++;
			cdn = userInfoDAO.addUserCdno(cvo);
			if(cdn > 0) {
				rst ++;
				userInfoDAO.userFsstatusChange(uvo);
				
				// 2021-02-02 임직원 기본권한 적용
				if(StringUtil.nvl(uvo.getFutype()).equals("1")) { // 임직원인 경우
					AuthorGroupVO avo = new AuthorGroupVO();
					avo.setFuid(uvo.getFuid());
					avo.setSiteId(uvo.getFpartcd1());
					avo.setRegistId(uvo.getFregid());
					int a = userInfoDAO.insertUserAuthGroupForNewEmp(avo);
					
					if(a > 0) {
						int b = authorGroupDAO.insertSyncDeviceByFuid(avo);
						
						if(b > 0) {
							
						} else {
							LOGGER.error("###[addUserInfoSave] insertSyncDeviceByFuid(avo) : {}", avo);
							throw new RuntimeException("[addUserInfoSave]출입자 권한그룹 장비동기화 중 오류 발생(4)");
						}
						
					} else {
						LOGGER.error("###[addUserInfoSave] insertUserAuthGroupForNewEmp(avo) : {}", avo);
						throw new RuntimeException("[addUserInfoSave]출입자 권한그룹 등록 중 오류 발생(3)");
					}
				}
			} else {
				LOGGER.error("###[addUserInfoSave] addUserCdno(cvo) : {}", cvo);
				throw new RuntimeException("[addUserInfoSave]출입자 카드 정보 등록 중 오류 발생(2)");
			}
		} else {
			LOGGER.error("###[addUserInfoSave] newUserInfoSave(uvo) : {}", uvo);
			throw new RuntimeException("[addUserInfoSave]출입자 정보 등록 중 오류 발생(1)");
		}
		return rst;
	}
	
	/**
	 * fuid 채번 및 중복 확인
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int getNewFuid(UserInfoVO vo) throws Exception {
		String newFuid = userInfoDAO.getNewFuid();
		vo.setFuid(newFuid);
		int cnt = userInfoDAO.selectChkFuid(vo);
		
		return cnt;
	}

	/**
	 * 출입자 신규등록 (개인정보,카드정보,권한)
	 * @param
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int addUserInfoNew (UserInfoVO uvo, CardInfoVO cvo, AuthorGroupVO avo) throws Exception {
		int rst = 0;
		int ust = userInfoDAO.newUserInfoSave(uvo);
		int cdn = 0;
		if(ust > 0) {
			rst++;
			cdn = userInfoDAO.addUserCdno(cvo);
			if(cdn > 0) {
				rst++;
				userInfoDAO.userFsstatusChange(uvo);
				
				// 2021-04-13 방문객카드(futype=3)인 경우, 권한이 필수아님.
				if(!CommonUtils.empty(avo.getGroupArray())) {
					// 2021-03-29 권한그룹
					int a = userInfoDAO.insertUserAuthGroup(avo);
					
					if(a > 0) {
						int b = authorGroupDAO.insertSyncDeviceByFuid(avo);
						
						if(b > 0) {
							rst++;
						} else {
							LOGGER.error("###[addUserInfoNew] insertSyncDeviceByFuid(avo) : {}", avo);
							throw new RuntimeException("[신규등록]출입자 권한그룹 장비동기화 중 오류 발생(4)");
						}
						
					} else {
						LOGGER.error("###[addUserInfoSave] insertUserAuthGroupForNewEmp(avo) : {}", avo);
						throw new RuntimeException("[신규등록]출입자 권한그룹 등록 중 오류 발생(3)");
					}					
				}
				
			} else {
				LOGGER.error("###[addUserInfoNew] addUserCdno(cvo) : {}", cvo);
				throw new RuntimeException("[신규등록]출입자 카드 정보 등록 중 오류 발생(2)");
			}
		} else {
			LOGGER.error("###[addUserInfoNew] newUserInfoSave(uvo) : {}", uvo);
			throw new RuntimeException("[신규등록]출입자 정보 등록 중 오류 발생(1)");
		}
		return rst;
	}	
	
	/**
	 * 출입자 개인정보 수정
	 * @param uvo
	 * @param bvo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int modUserInfoNew (UserInfoVO uvo, UserBioInfoVO bvo) throws Exception {
		// 출입자 개인정보
		int cnt = userInfoDAO.updateUserInfo(uvo);
		if(cnt > 0) {
			// bio 수정
			if(uvo.getFbioyn().equals("Y")) {
				cnt = imgUserInfoSave(bvo);
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("fuid", uvo.getFuid());
				userInfoDAO.updateUserExpire(map);
			}
		} else {
			LOGGER.error("###[addUserInfoSave] modUserInfoNew(uvo) : {}", uvo);
			throw new RuntimeException("[modUserInfoNew]출입자 개인정보 수정 중 오류 발생");
		} 
		
		return cnt;
	}
	
	/**
	 * 출입자 개인정보 수정 , 이력 남기기
	 * @param uvo
	 * @param bvo
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@Override
	public int modUserInfoNew (UserInfoVO uvo, UserBioInfoVO bvo, Map<String, Object> param) throws Exception {
		//2021-07-12
		Map<String, Object> log = userChgLogDAO.selectUserInfoForLog(uvo);
		JSONObject json = JsonUtil.getJsonStringFromMap(log);
		
		//2021-08-10
		Map<String, Object> logBio = userChgLogDAO.selectBioInfoForLog(uvo);	
		
		// 출입자 개인정보
		int cnt = userInfoDAO.updateUserInfo(uvo);
		if(cnt > 0) {
			// bio 수정
			if(uvo.getFbioyn().equals("Y")) {
				cnt = imgUserInfoSave(bvo);
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("fuid", uvo.getFuid());
				userInfoDAO.updateUserExpire(map);
			}
			
			//2021-07-12 이력남기기
			json.put("bio_info", (uvo.getFbioyn().equals("Y")?"사진수정함":"사진수정 안함"));
			
			int newKey = userChgLogDAO.selectNewUserChgSeq();
			param.put("chg_seq" , newKey);
			param.put("fpartcd1", log.get("fpartcd1"));		
			param.put("fuid"    , log.get("fuid"));
			param.put("funm"    , log.get("funm"));
			param.put("fcdno"   , log.get("fcdno"));
			param.put("chg_cnts", json.toString());
			LOGGER.debug("###[modUserInfoNew] 출입자수정 로그 param : {}", param);			
			
			int iChgLogCnt = userChgLogDAO.insertUserChgLog2(param);
			LOGGER.debug("###[modUserInfoNew] 출입자수정 로그 iChgLogCnt : {}", iChgLogCnt);
			
			if(StringUtil.nvl(logBio.get("bio_yn")).equals("Y")) {
				logBio.put("chg_seq", newKey);
				int iChgBioCnt = userChgLogDAO.insertUserChgBio(logBio);
				LOGGER.debug("###[modUserInfoNew] 출입자수정 로그(bio) iChgBioCnt : {}", iChgBioCnt);
			}
	
		} else {
			LOGGER.error("###[modUserInfoNew] modUserInfoNew(uvo) : {}", uvo);
			throw new RuntimeException("[modUserInfoNew]출입자 개인정보 수정 중 오류 발생");
		}
		
		return cnt;
	}
	
	/**
	 * 권한 변경 여부 확인
	 * @param authorGroupVO
	 * @return
	 * @throws Exception
	 */
	@Override
	public int selectUserAuthGroupForChange(AuthorGroupVO authorGroupVO) throws Exception {
		return authorGroupDAO.selectUserAuthGroupForChange(authorGroupVO);
	}
	
	/**
	 * 출입자 카드, 권한그룹 수정
	 * @param cardInfoVO
	 * @param authorGroupVO
	 * @return
	 * @throws Exception
	 */
	@Override
	public int saveUserCardAuthInfo(CardInfoVO cardInfoVO, AuthorGroupVO authorGroupVO) throws Exception {
		int cnt = 0;
		
		// 카드 수정
		cardInfoVO.setFsstatus("Q");
		cnt = userInfoDAO.updateCardInfo(cardInfoVO);
		LOGGER.debug("##### 카드/권한수정 cardInfoVO:{}", cardInfoVO);
		LOGGER.debug("##### 카드/권한수정 cnt:{}", cnt);
		
		if(cnt == 0) {
			LOGGER.error("##### [saveUserCardAuthInfo] 카드정보 수정 중 오류 발생!!!");
			throw new RuntimeException("카드정보 수정 중 오류가 발생했습니다.");
		}

		int cntChange = -1;
		// 카드 상태
		String cfstatus = StringUtil.nvl(cardInfoVO.getFstatus());
		// 권한그룹 수정여부 확인		
		if((cfstatus.equals("Y") || cfstatus.equals("W"))) {
			cntChange = authorGroupDAO.selectUserAuthGroupForChange(authorGroupVO);
		}
		LOGGER.debug("##### 카드/권한수정 권한수정여부 확인 cntChange:{}", cntChange);
		
		// 카드상태가 'ACTIVE' 'WAIT'인 경우만, 권한 내림
		if(cntChange > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
			// 사용자 권한그룹 log
			int cntLog = authorGroupDAO.insertUserAuthGroupLog(authorGroupVO);
			LOGGER.debug("##### 카드/권한수정 사용자 권한그룹 log cntLog:{}", cntLog);
			// 사용자 권한그룹 delete
			int cntDelete = authorGroupDAO.deleteUserAuthGroupAll(authorGroupVO);
			LOGGER.debug("##### 카드/권한수정 사용자 권한그룹 삭제 cntDelete:{}", cntDelete);
			
			// 사용자 권한그룹 insert
			int cntInsert = 0;
			String [] arr = authorGroupVO.getGroupArray();
			if(arr != null) {
				/*for(String str : arr) {
					authorGroupVO.setAuthorGroupId(str);
					cntInsert += authorGroupDAO.insertUserAuthGroup(authorGroupVO);
					LOGGER.debug("##### 카드/권한수정 권한추가 cntInsert:{}", cntInsert);
				}*/
				
				cntInsert = authorGroupDAO.insertUserAuthGroupList(authorGroupVO); 
				LOGGER.debug("##### 카드/권한수정 권한추가 (최종) cntInsert:{}", cntInsert);
				
				if(arr.length != cntInsert) {
					LOGGER.error("##### [saveUserCardAuthInfo] 카드/권한수정 권한그룹 등록 중 오류 발생!!!");
					LOGGER.error("##### [saveUserCardAuthInfo] cnt       : {}", cnt);
					LOGGER.error("##### [saveUserCardAuthInfo] cntChange : {}", cntChange);
					LOGGER.error("##### [saveUserCardAuthInfo] cntLog    : {}", cntLog);
					LOGGER.error("##### [saveUserCardAuthInfo] cntDelete : {}", cntDelete);
					LOGGER.error("##### [saveUserCardAuthInfo] cntInsert : {}", cntInsert);
					LOGGER.error("##### [saveUserCardAuthInfo] arr.length: {}", arr.length);
					throw new RuntimeException("권한그룹 저장 중 오류가 발생했습니다.");
				}
			}
			
			//2021-01-20 장비동기화 
			int cntSyncDevice = authorGroupDAO.insertSyncDeviceByFuid(authorGroupVO);
			LOGGER.debug("##### 장비동기화 cntSyncDevice:{}, authorGroupVO:{}", cntSyncDevice, authorGroupVO);
		}
		
		//2021-01-29 
		UserInfoVO userInfoVO = new UserInfoVO();
		userInfoVO.setFuid(cardInfoVO.getFuid());
		userInfoVO.setFpartcd1(cardInfoVO.getFpartcd1());
		int cntSyncUser = userInfoDAO.userInfoSaveSync(userInfoVO);
		LOGGER.debug("##### 사용자동기화 cntSyncUser:{}, userInfoVO:{}", cntSyncUser, userInfoVO);
		
		return cnt;
	}	
	
	@Override
	public int saveUserCardAuthInfo(CardInfoVO cardInfoVO, AuthorGroupVO authorGroupVO, Map<String, Object> param) throws Exception {
		int cnt = 0;

		//2021-07-12 카드
		Map<String, Object> log = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		
		if(StringUtil.nvl(param.get("chg_cl_cd")).equals("A")) {
			log = userChgLogDAO.selectAuthInfoForLog(authorGroupVO); //카드+권한
		} else {
			log = userChgLogDAO.selectCardInfoForLog(cardInfoVO); //카드
		}
		json = JsonUtil.getJsonStringFromMap(log);
	
		// 카드 수정
		cardInfoVO.setFsstatus("Q");
		cnt = userInfoDAO.updateCardInfo(cardInfoVO);
		LOGGER.debug("##### 카드/권한수정 cardInfoVO:{}", cardInfoVO);
		LOGGER.debug("##### 카드/권한수정 cnt:{}", cnt);
		
		if(cnt == 0) {
			LOGGER.error("##### [saveUserCardAuthInfo] 카드정보 수정 중 오류 발생!!!");
			throw new RuntimeException("카드정보 수정 중 오류가 발생했습니다.");
		}

		int cntChange = -1;
		// 카드 상태
		String cfstatus = StringUtil.nvl(cardInfoVO.getFstatus());
		// 권한그룹 수정여부 확인
		if((cfstatus.equals("Y") || cfstatus.equals("W"))) {
			cntChange = authorGroupDAO.selectUserAuthGroupForChange(authorGroupVO);
		}
		LOGGER.debug("##### 카드/권한수정 권한수정여부 확인 cntChange:{}", cntChange);
		
		// 카드상태가 'ACTIVE' 'WAIT'인 경우만, 권한 내림
		if(cntChange > 0 && (cfstatus.equals("Y") || cfstatus.equals("W"))) {
			// 사용자 권한그룹 log
			int cntLog = authorGroupDAO.insertUserAuthGroupLog(authorGroupVO);
			LOGGER.debug("##### 카드/권한수정 사용자 권한그룹 log cntLog:{}", cntLog);
			// 사용자 권한그룹 delete
			int cntDelete = authorGroupDAO.deleteUserAuthGroupAll(authorGroupVO);
			LOGGER.debug("##### 카드/권한수정 사용자 권한그룹 삭제 cntDelete:{}", cntDelete);
			
			// 사용자 권한그룹 insert
			int cntInsert = 0;
			String [] arr = authorGroupVO.getGroupArray();
			if(arr != null) {
				/*for(String str : arr) {
					authorGroupVO.setAuthorGroupId(str);
					cntInsert += authorGroupDAO.insertUserAuthGroup(authorGroupVO);
					LOGGER.debug("##### 카드/권한수정 권한추가 cntInsert:{}", cntInsert);
				}*/
				
				cntInsert = authorGroupDAO.insertUserAuthGroupList(authorGroupVO); 
				LOGGER.debug("##### 카드/권한수정 권한추가 (최종) cntInsert:{}", cntInsert);
				
				if(arr.length != cntInsert) {
					LOGGER.error("##### [saveUserCardAuthInfo] 카드/권한수정 권한그룹 등록 중 오류 발생!!!");
					LOGGER.error("##### [saveUserCardAuthInfo] cnt       : {}", cnt);
					LOGGER.error("##### [saveUserCardAuthInfo] cntChange : {}", cntChange);
					LOGGER.error("##### [saveUserCardAuthInfo] cntLog    : {}", cntLog);
					LOGGER.error("##### [saveUserCardAuthInfo] cntDelete : {}", cntDelete);
					LOGGER.error("##### [saveUserCardAuthInfo] cntInsert : {}", cntInsert);
					LOGGER.error("##### [saveUserCardAuthInfo] arr.length: {}", arr.length);
					throw new RuntimeException("권한그룹 저장 중 오류가 발생했습니다.");
				}
			}
			
			//2021-01-20 장비동기화 
			int cntSyncDevice = authorGroupDAO.insertSyncDeviceByFuid(authorGroupVO);
			LOGGER.debug("##### 장비동기화 cntSyncDevice:{}, authorGroupVO:{}", cntSyncDevice, authorGroupVO);
		}

		//2021-07-12 이력남기기
		param.put("fpartcd1" , log.get("fpartcd1"));
		param.put("fuid"     , log.get("fuid"));
		param.put("funm"     , log.get("funm"));
		param.put("fcdno"    , log.get("fcdno"));
		param.put("chg_cnts" , json.toString());
		LOGGER.debug("###[saveUserCardAuthInfo] 카드/권한수정 로그 param : {}", param);

		int iChgLogCnt = userChgLogDAO.insertUserChgLog(param);
		LOGGER.debug("###[saveUserCardAuthInfo] 카드수정 로그 iChgLogCnt : {}", iChgLogCnt);
		
		//2021-01-29 
		UserInfoVO userInfoVO = new UserInfoVO();
		userInfoVO.setFuid(cardInfoVO.getFuid());
		userInfoVO.setFpartcd1(cardInfoVO.getFpartcd1());
		int cntSyncUser = userInfoDAO.userInfoSaveSync(userInfoVO);
		LOGGER.debug("##### 사용자동기화 cntSyncUser:{}, userInfoVO:{}", cntSyncUser, userInfoVO);
		
		return cnt;
	}	

	/**
	 * fcdno 중복 오류 확인
	 * @param vo
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getChkFcdno(CardInfoVO vo) throws Exception {
		return userInfoDAO.getChkFcdno(vo);
	}
	
	/**
	 * 출입자 상태 조회
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, String> getUserStatus(Map<String, String> param) throws Exception {
		return userInfoDAO.selectUserStatus(param);
	}
	
	/**
	 * 권한그룹별 출입자 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getUserListByAuthCount(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectUserListByAuthCount(param);
	}
	
	/**
	 * 권한그룹별 출입자 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> getUserListByAuth(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectUserListByAuth(param);
	}	
	
	/**
	 * 권한그룹별 출입자 목록 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> getUserListByAuthExcel(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectUserListByAuthExcel(param);
	}
	
	/**
	 * 단말기별 출입자 목록 전체 건수
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getUserListByGateCount(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectUserListByGateCount(param);
	}
	
	/**
	 * 단말기별 출입자 목록
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> getUserListByGate(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectUserListByGate(param);
	}	
	
	/**
	 * 단말기별 출입자 목록 엑셀저장
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ExcelVO> getUserListByGateExcel(Map<String, Object> param) throws Exception {
		return userInfoDAO.selectUserListByGateExcel(param);
	}
	
	/**
	 * 외부에서 출입자 등록 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public String insertUserInfoForRest(Map<String, Object> param) throws Exception {
		param.put("fuid", userInfoDAO.getNewFuid());

		LOGGER.debug("[외부등록] param : {}", param);
		
		int cnt = userInfoDAO.insertUserAllInfo(param);
		
		LOGGER.debug("[외부등록] cnt : {}", cnt);
		
		return StringUtil.nvl(param.get("fuid"));
	}
	
	/**
	 * 엑셀업로드 저장(여러건)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public int insertUserListForExcel(List<HashMap> list) throws Exception {
		int cnt = 0;
		String str = "";
		
		// 출입자 등록
		for(HashMap param : list) {
			String newFuid = userInfoDAO.getNewFuid();
			param.put("fuid", newFuid);			
			userInfoDAO.insertUserAllInfo(param);
			str = StringUtil.nvl(param.get("fuid"))+"_"+StringUtil.nvl(param.get("funm"));			
			cnt++;
			LOGGER.debug("### [upload-excel-list] cnt/fuid_funm : {}/{}", cnt, str);
		}
		
		// 로그
		/*HashMap map = new HashMap();
		map = list.get(0);
		
		SysLogVO vo = new SysLogVO();
		vo.setFsiteid(StringUtil.nvl(map.get("siteId")));
		vo.setFsyscode(StringUtil.nvl(map.get("syslogCode")));
		vo.setFdetail(String.format("%s (%d/%d)", str, cnt, list.size()));
		vo.setFcnntid(StringUtil.nvl(map.get("fcnntip")));
		int log_cnt = commonDAO.sysLogSave(vo);*/
		
		LOGGER.debug("### [upload-excel-list] final cnt : {}/{}", cnt, str);
		
		return cnt;		
	}
	
	/**
	 * 엑셀업로드 저장(1건씩)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public int insertUserInfoForExcel(Map<String, Object> param) throws Exception {
		int cnt = 0;
		String str = "";
		
		// 출입자 등록
		String newFuid = userInfoDAO.getNewFuid();
		param.put("fuid", newFuid);			
		cnt = userInfoDAO.insertUserAllInfo(param);
		str = StringUtil.nvl(param.get("fuid"))+"_"+StringUtil.nvl(param.get("funm"));			
		LOGGER.warn("### [upload-excel-info] cnt/fuid_funm : {}/{}", cnt, str);
		
		// 로그
		/*HashMap map = new HashMap();
		map = list.get(0);
		
		SysLogVO vo = new SysLogVO();
		vo.setFsiteid(StringUtil.nvl(map.get("siteId")));
		vo.setFsyscode(StringUtil.nvl(map.get("syslogCode")));
		vo.setFdetail(String.format("%s (%d/%d)", str, cnt, list.size()));
		vo.setFcnntid(StringUtil.nvl(map.get("fcnntip")));
		int log_cnt = commonDAO.sysLogSave(vo);*/
		
		return cnt;		
	}		
}
