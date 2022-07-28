package aero.cubox.sample.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.PaginationVO;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import aero.cubox.util.ApiUtil;
import aero.cubox.util.CuboxProperties;
import aero.cubox.util.StringUtil;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.LogInfoService;

@Controller
@RequestMapping(value = "/apiInfo/")
public class ApiInfoController {
	private final String GLOBAL_API_IP = CuboxProperties.getProperty("Globals.api.ip");
	private final String GLOBAL_API_PORT = CuboxProperties.getProperty("Globals.api.port");

	private int srchCnt     = 10; //조회할 페이지 수
	private int curPageUnit = 10; //한번에 표시할 페이지 번호 개수
	
	@Resource(name = "logInfoService")
	private LogInfoService logInfoService;	

	@Resource(name = "commonService")
	private CommonService commonService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiInfoController.class);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/apiInfoMngmt.do")
	public String apiInfoMngmt(HttpServletRequest request, ModelMap model, @RequestParam Map<String, Object> param) throws Exception {

		int srchPage = String.valueOf(request.getParameter("srchPage")).matches("(^[0-9]*$)") ? Integer.valueOf(request.getParameter("srchPage")) : 1;
		String srchRecPerPage = StringUtil.nvl(param.get("srchRecPerPage"), String.valueOf(srchCnt));
		String startDate = StringUtil.isNullToString(request.getParameter("startDate"));
		String endDate = StringUtil.isNullToString(request.getParameter("expireDate"));
		
		if(startDate.equals("")) startDate = logInfoService.getYesterDt();
		if(endDate.equals("")) endDate = logInfoService.getTodayDt();

		JSONObject json = new JSONObject();

		String matchUrl = "http://" + GLOBAL_API_IP + ":" + GLOBAL_API_PORT +"/v1/faceMatchLog";
		LOGGER.debug("matchUrl >>>> "+matchUrl);

		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(srchPage);
		pageVO.setRecPerPage(Integer.parseInt(srchRecPerPage));

		//페이지에서 조회할 레코드 인덱스 생성
		pageVO.calcRecordIndex();

		json.put("paging_no", pageVO.getFirstRecordIndex());
		json.put("paging_cnt", pageVO.getRecPerPage());
		json.put("start_dt", startDate);
		json.put("end_dt", endDate);

		String reqBody = json.toJSONString();
		
		HashMap<String, Object> match = new HashMap<String, Object>();
		match = ApiUtil.getApiReq(reqBody,matchUrl);

		int tot_cnt = 0;
		List list = new ArrayList<>();
		if(match.get("tot_cnt") != null){
			tot_cnt = (int) match.get("tot_cnt");
			list = (List) match.get("list");
		}

		pageVO.setTotRecord(tot_cnt);
		pageVO.setUnitPage(curPageUnit);
		pageVO.calcPageList();
		
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수

		model.addAttribute("cntPerPage",cntPerPage);
		model.addAttribute("pagination", pageVO);
		model.addAttribute("matchLog", list);
		model.addAttribute("json", json);
		return "cubox/apiInfo/apiInfo_management";

	}


	@RequestMapping(value="/compareMonitor.do")
	public String compareMonitor(HttpServletRequest request, ModelMap model) throws Exception {

        model.addAttribute("ws_ip", GLOBAL_API_IP + ":" + GLOBAL_API_PORT);
		return "cubox/apiInfo/compare_monitor";
	}


	@RequestMapping(value="/apiInfoPopup.do")
	public String apiInfoPopup(ModelMap model, HttpServletRequest request ) throws Exception {

		String srch_id = StringUtil.isNullToString(request.getParameter("srch_id"));
		String mtch_id = StringUtil.isNullToString(request.getParameter("mtch_id"));

		model.addAttribute("srch_id", srch_id);
		model.addAttribute("mtch_id", mtch_id);
		return "cubox/apiInfo/apiInfo_popup";

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getApiImage.do")
	public ResponseEntity<byte[]> getApiImage(HttpServletRequest request) throws Exception {

		String srch_id = StringUtil.isNullToString(request.getParameter("srch_id"));
		String img_type = StringUtil.isNullToString(request.getParameter("img_type"));

		HashMap<String, Object> result = new HashMap<String, Object>();
		String matchUrl = "http://" + GLOBAL_API_IP + ":" + GLOBAL_API_PORT +"/v1/faceMatchImage";

		JSONObject json = new JSONObject();

		json.put("srch_id", srch_id);
		json.put("img_type", img_type);

		String reqBody = json.toJSONString();

		result = ApiUtil.getApiReq(reqBody,matchUrl);

		String code = String.valueOf(result.get("code"));
		byte[] imageContent = null;
		if(code.equals("0")){
			imageContent = Base64.decodeBase64(result.get("data").toString());
		}

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
	}

}
