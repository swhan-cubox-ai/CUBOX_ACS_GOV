package aero.cubox.sample.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.GateInfoService;
import aero.cubox.user.service.UserInfoService;
import aero.cubox.user.service.vo.UserBioInfoVO;
import aero.cubox.user.service.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import aero.cubox.util.AES256Util;
import aero.cubox.util.JsonUtil;
import aero.cubox.util.StringUtil;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.CuboxProperties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value="/service/")
public class RestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestController.class);

	private final String GLOBAL_AES256_KEY = CuboxProperties.getProperty("Globals.aes256.key");
	private final String MOBILE_IMAGE_FLG = CuboxProperties.getProperty("Globals.mobileImageDown");

	private final String MOBILE_IMAGE_RESIZE_YN = CuboxProperties.getProperty("Globals.mobileImageResizeYn");
	
	@Value("#{property['Globals.user.fpartcd1']}")
	private String gbUserFpartcd1;
	
	@Value("#{property['Globals.user.fpartnm1']}")
	private String gbUserFpartnm1;

	@Resource(name = "userInfoService")
	private UserInfoService userInfoService;

	@Resource(name = "gateInfoService")
	private GateInfoService gateInfoService;

	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	@Resource(name = "commonService")
	private CommonService commonService;

	@RequestMapping(value="/faceRegist.do")
	public ResponseEntity faceRegist(HttpServletRequest request) {

		LOGGER.debug(">>>>>>>>>>>>>> faceRegist start >>>>>>>>>>>>>>>>>>");

        ResponseEntity entity = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        JSONObject obj = new JSONObject();
        obj.put("success", false);
        obj.put("code", -9999);
        obj.put("msg", "An unknown error has occurred.");
		try{
			//request-responsebody-json-hashmap
			HashMap<String, Object> value = JsonUtil.getJsonToMap(JsonUtil.getResponseBody(request));

			//json parse, json key, essential value check
			if(
					value != null &&
					value.containsKey("face_id") &&
					value.containsKey("face_img") &&
					value.get("face_id").toString().length() > 0 &&
					value.get("face_img").toString().length() > 0
			) {
				String face_id = (String) value.get("face_id");
				String face_img = (String) value.get("face_img");

				LOGGER.debug(">>>>>>>>>>>>>> faceRegist face_id >>>>>>>>>>>>>>>>>> "+face_id);

				//byte[] imageByte jpg format 변경
				byte[] bt_face_img = Base64.decodeBase64(face_img);

				LOGGER.debug(">>>>>>>>>>>>>> faceRegist log 저장 시작 >>>>>>>>>>>>>>>>>> ");

				//param log 저장
	        	int rst = commonService.sysMobileLogSave(face_id, face_img);	//mobile.

	        	LOGGER.debug(">>>>>>>>>>>>>> faceRegist log 저장 완료 >>>>>>>>>>>>>>>>>> ");
	        	//LOGGER.debug("rst >>>> "+rst);

	        	if(MOBILE_IMAGE_FLG != null && MOBILE_IMAGE_FLG.equals("Y")) {
	        		LOGGER.debug(">>>>>>>>>>>>>> faceRegist 이미지 다운로드 시작 >>>>>>>>>>>>>>>>>> ");

	        		//이미지 다운로드
	        		ByteArrayInputStream inputStream = null;
	        		FileOutputStream fos = null;
	        		BufferedOutputStream bos = null;
	        		try {
	        			String filePath = CuboxProperties.getProperty("Globals.mobileImageDownPath")+face_id+"."+CuboxProperties.getProperty("Globals.mobileImageDownEx");
	        			File f = new File(filePath);
		        		if(!f.getParentFile().exists()) f.getParentFile().mkdirs();

		        		fos = new FileOutputStream(filePath);
		        		fos.write(bt_face_img);

	        		} catch (Exception e) {
	        			LOGGER.debug(">>>>>>>>>>>>>>>>>>> 모바일 이미지 다운로드 오류");
	        			e.printStackTrace();
	        			commonService.sysMobileLogUpdate (rst, "E");
					} finally {
						if(bos != null) bos.close();
						if(fos != null) fos.close();
						if(inputStream != null) inputStream.close();
						commonService.sysMobileLogUpdate (rst, "Y");
					}
	        	} else {
	        		LOGGER.debug(">>>>>>>>>>>>>> faceRegist 이미지 다운로드 안함 >>>>>>>>>>>>>>>>>> ");
	        		commonService.sysMobileLogUpdate (rst, "N");
	        	}
	        	LOGGER.debug(">>>>>>>>>>>>>> faceRegist 이미지 bio table 저장 시작 >>>>>>>>>>>>>>>>>> ");

		        UserBioInfoVO userBioInfo = new UserBioInfoVO();
		        userBioInfo.setFuid(face_id);
		        userBioInfo.setImgByte(bt_face_img);
		        userBioInfo.setFrcvflg("M");
		        userBioInfo.setResizeYn(MOBILE_IMAGE_RESIZE_YN);
	        	int imgUserSave = userInfoService.imgUserInfoSave(userBioInfo);
	        	if(imgUserSave > 0) {
	        		commonService.sysLogSave("mobile", "13101303", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	}else{
	        		commonService.sysLogSave("mobile", "13101304", userBioInfo.toString(), commonUtils.getIPFromRequest(request)); //로그저장
	        	}

	        	LOGGER.debug(">>>>>>>>>>>>>> faceRegist 이미지 bio table 저장 완료 >>>>>>>>>>>>>>>>>> ");
		        obj.put("success", true);
		        obj.put("code", 0);
		        obj.put("msg", "");
			}else{
		        obj.put("success", false);
		        obj.put("code", -1000);
		        obj.put("msg", "Json request error.");
			}
		}catch(Exception e){
	        obj.put("success", false);
	        obj.put("code", -1001);
	        obj.put("msg", "Data processing error.");
		}
        entity = new ResponseEntity(obj.toString(), responseHeaders, HttpStatus.CREATED);
        return entity;
	}

	@RequestMapping(value="/faceView.do")
	public ResponseEntity faceView(HttpServletRequest request) {
        ResponseEntity entity = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        JSONObject obj = new JSONObject();
        obj.put("success", false);
        obj.put("code", -9999);
        obj.put("msg", "An unknown error has occurred.");
        obj.put("data", "");

		try{
			//request-responsebody-json-hashmap
			HashMap<String, Object> value = JsonUtil.getJsonToMap(JsonUtil.getResponseBody(request));

			//json parse, json key, essential value check
			if(
					value != null &&
					value.containsKey("face_id") &&
					value.get("face_id").toString().length() > 0
			){

				String face_id = (String) value.get("face_id");

				AES256Util aes256 = new AES256Util();

				UserBioInfoVO userBioInfo = new UserBioInfoVO();
				userBioInfo = userInfoService.getUserBioInfo(face_id);
				byte[] imageContent = null;
				if(userBioInfo != null && userBioInfo.getFimg().length() != 0){
					imageContent = aes256.byteArrDecodenopaddingImg(userBioInfo.getFimg(), GLOBAL_AES256_KEY);
					String face_image_base64 = Base64.encodeBase64String(imageContent);
					face_image_base64 = face_image_base64.replaceAll("\r\n","");
			        obj.put("success", true);
			        obj.put("code", 0);
			        obj.put("msg", "");
			        obj.put("data", face_image_base64);
				}else{
			        obj.put("success", false);
			        obj.put("code", 0);
			        obj.put("msg", "This face not exist.");
			        obj.put("data", "");
				}
			}else{
		        obj.put("success", false);
		        obj.put("code", -1000);
		        obj.put("msg", "Json request error.");
		        obj.put("data", "");
			}
		}catch(Exception e){
	        obj.put("success", false);
	        obj.put("code", -1001);
	        obj.put("msg", "Data processing error.");
	        obj.put("data", "");
		}
        entity = new ResponseEntity(obj.toString(), responseHeaders, HttpStatus.CREATED);
        return entity;
	}

	@RequestMapping(value="/gateStatus.do")
	public ResponseEntity terminalStatus(HttpServletRequest request) {
        ResponseEntity entity = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");

        String default_flh = "Client";
        String default_fli = "CF";

        JSONObject obj = new JSONObject();
        obj.put("success", false);
        obj.put("code", -9999);
        obj.put("msg", "An unknown error has occurred.");
        obj.put("flh", "");
        obj.put("fli", "");

		try{
			//request-responsebody-json-hashmap
			HashMap<String, Object> value = JsonUtil.getJsonToMap(JsonUtil.getResponseBody(request));

			//json parse, json key, essential value check
			if(
					value != null &&
					value.containsKey("gate_id") &&
					value.get("gate_id").toString().length() > 0
			){

				String gate_id = (String) value.get("gate_id");


				HashMap map = new HashMap();
				map.put("fgid", gate_id);
				HashMap result = gateInfoService.selectLocationGate(map);
				if(result != null){
			    	String flh = StringUtil.isNullToString(result.get("flh"));
			    	String fli = StringUtil.isNullToString(result.get("fli"));

			        obj.put("success", true);
			        obj.put("code", 0);
			        obj.put("msg", "");
			        obj.put("flh", flh);
			        obj.put("fli", fli);
				}else{
			        obj.put("success", true);
			        obj.put("code", 0);
			        obj.put("msg", "This gate not exist.");
			        obj.put("flh", default_flh);
			        obj.put("fli", default_fli);
				}
			}else{
		        obj.put("success", false);
		        obj.put("code", -1000);
		        obj.put("msg", "Json request error.");
		        obj.put("flh", "");
		        obj.put("fli", "");
			}
		}catch(Exception e){
	        obj.put("success", false);
	        obj.put("code", -1001);
	        obj.put("msg", "Data processing error.");
	        obj.put("flh", "");
	        obj.put("fli", "");
		}
        entity = new ResponseEntity(obj.toString(), responseHeaders, HttpStatus.CREATED);
        return entity;
	}

	/**
	 * 외부에서 출입자 등록 Test Page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/form.do")
	public String regist() throws Exception {
		return "cubox/userInfo/user_add_external";
	}	
	
	/**
	 * 외부에서 출입자 등록
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value={"/regUser.do", "/addUser.do"})
	public ResponseEntity addUser(MultipartHttpServletRequest request) throws Exception {
		LOGGER.debug("##### [addUser] start");

		ResponseEntity entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		JSONObject obj = new JSONObject();
		obj.put("success", false);
		obj.put("message", "unknown");

		try {
			//request-responsebody-json-hashmap
			//HashMap<String, Object> value = JsonUtil.getJsonToMap(JsonUtil.getResponseBody(request));

			//json parse, json key, essential value check
			if(StringUtil.nvl(request.getParameter("funm")).equals("")) {
				obj.put("success", false);
				obj.put("message", "no user name");
			} else {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("siteId", gbUserFpartcd1);
				param.put("registId", "mobile");
				param.put("fpartnm1", gbUserFpartnm1);
				param.put("syslogCode", "13101108"); //외부에서 출입자 등록
				param.put("fsdt", commonUtils.getToday("yyyy-MM-dd"));
				param.put("fedt", commonUtils.getStringDate(DateUtils.addDays(new Date(), 30), "yyyy-MM-dd"));
				param.put("syslogYn", "Y");
				
				param.put("funm", StringUtil.nvl(request.getParameter("funm"), "이름없음"));
				param.put("fcnntip", commonUtils.getIPFromRequest(request));	
				LOGGER.debug("##### [addUser] param : {}", param);
				
				AES256Util aes256 = new AES256Util();

				// 얼굴이미지 첨부파일 받음
				MultipartFile file = null;
				Iterator<String> iterator = request.getFileNames();

				if (iterator.hasNext()) {
					file = request.getFile(iterator.next());
				}
				//if (file != null) {
				if(file.getSize() == 0) {
					obj.put("success", false);
					obj.put("message", "no image file");					
				} else {
					String imgString = aes256.byteArrEncode(file.getBytes(), GLOBAL_AES256_KEY);
					param.put("fimg", imgString);
					
					String str = userInfoService.insertUserInfoForRest(param);
					LOGGER.debug("##### [addUser] fuid : {}", str);
					
					if(!StringUtil.nvl(str).equals("")) {
						obj.put("success", true);
						obj.put("message", "success");
						obj.put("fuid", str);					
					} else {
						obj.put("success", false);
						obj.put("message", "error");
					}					
				}
			}
		} catch(Exception e) {
			obj.put("success", false);
			obj.put("message", e.getMessage());
		}
		
		LOGGER.debug("##### [addUser] obj : {}", obj);
		LOGGER.debug("##### [addUser] end!!");
		entity = new ResponseEntity(obj.toString(), responseHeaders, HttpStatus.CREATED);
		return entity;
	}		

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value="/delUser.do")
	public ResponseEntity delUser(HttpServletRequest request, @RequestParam Map<String, Object> param) throws Exception {
		LOGGER.debug("##### [delUser] start param:{}", param);

		ResponseEntity entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		JSONObject obj = new JSONObject();
		obj.put("success", false);
		obj.put("message", "unknown");

		try {
			//request-responsebody-json-hashmap
			//HashMap<String, Object> value = JsonUtil.getJsonToMap(JsonUtil.getResponseBody(request));

			//json parse, json key, essential value check
			if(StringUtil.nvl(param.get("fuid")).equals("")) {
				obj.put("success", false);
				obj.put("message", "no fuid");
			} else {
				String userId = "mobile";
				String cntnIp = commonUtils.getIPFromRequest(request);				
				
				// /userInfo/userInfoDel.do : 동일 
				UserInfoVO userInfoVO = new UserInfoVO();
            	userInfoVO.setFuid(StringUtil.nvl(param.get("fuid")));
            	userInfoVO.setFpartcd1(gbUserFpartcd1);
            	userInfoVO.setFmodid(userId);
            	
        		// 변경 로그
        		param.put("chg_resn", "외부에서 삭제요청");
        		param.put("cntn_ip", cntnIp);
        		param.put("reg_id", userId);

            	int cnt = userInfoService.userInfoDel(userInfoVO, param);
				LOGGER.debug("##### [delUser] cnt : {}", cnt);
				
				if(cnt > 0) {
					commonService.sysLogSave(userId, "13101319", userInfoVO.toString(), cntnIp); //외부에서 출입자 삭제
					obj.put("success", true);
					obj.put("message", "success");
				} else {
					obj.put("success", false);
					obj.put("message", "error");
				}
			}
		} catch(Exception e) {
			obj.put("success", false);
			obj.put("message", e.getMessage());
		}
		
		LOGGER.debug("##### [delUser] obj : {}", obj);
		LOGGER.debug("##### [delUser] end!!");
		entity = new ResponseEntity(obj.toString(), responseHeaders, HttpStatus.CREATED);
		return entity;
	}		
		
}
