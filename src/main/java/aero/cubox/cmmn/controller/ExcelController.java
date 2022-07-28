package aero.cubox.cmmn.controller;

import aero.cubox.util.AES256Util;
import aero.cubox.util.CommonUtils;
import aero.cubox.util.CuboxProperties;
import aero.cubox.util.StringUtil;
import aero.cubox.auth.service.AuthorGroupService;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.TerminalInfoService;
import aero.cubox.user.service.UserInfoService;
import egovframework.rte.fdl.property.EgovPropertyService;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ExcelController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelController.class);

	private String GLOBAL_AES256_KEY = CuboxProperties.getProperty("Globals.aes256.key");
	//private String GLOBAL_FILESTORAGE = CuboxProperties.getProperty("Globals.fileStorage");
	private String GLOBAL_DEFAULT_AUTHTYPE = CuboxProperties.getProperty("Globals.default.authtype");
	
	@Value("#{property['Globals.user.imagePath']}")
	private String gbUserImagePath;
	
	@Value("#{property['Globals.user.fpartnm1']}")
	private String gbUserFpartnm1;

	/** memberService */
	@Resource(name = "userInfoService")
	private UserInfoService userInfoService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "commonUtils")
	protected CommonUtils commonUtils;

	/** commonService */
	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "terminalInfoService")
	private TerminalInfoService terminalInfoService;

	@Resource(name = "authorGroupService")
	private AuthorGroupService authorGroupService;
	
	/* 카드번호 생성 자릿수 */
	@Value("#{property['Globals.cardDigit']}")
	private String cardDigit;
	
	/* 고유번호 생성 자릿수 */
	@Value("#{property['Globals.fidDigit']}")
	private String fidDigit;
	
	/* 채워질 기본문자 */
	@Value("#{property['Globals.fillChar']}")
	private String fillChar;

	/* 채워질 방향 */
	@Value("#{property['Globals.fillDirection']}")
	private String fillDirection;
	
	
	/**
	 * 사용자정보 엑셀업로드 POC
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return modelAndView
	 * @throws Exception
	 */
	@RequestMapping(value = "/service/excelUpload.do")
    public ModelAndView excelUpload(MultipartHttpServletRequest request) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("jsonView");

		MultipartFile file = null;
        Iterator<String> iterator = request.getFileNames();
        if(iterator.hasNext()) {
            file = request.getFile(iterator.next());
        }
        
        String cntnIp = commonUtils.getIPFromRequest(request);

        try {
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			LOGGER.warn("sheet.getLastRowNum() : "+sheet.getLastRowNum());
			
			AES256Util aes256 = new AES256Util();
			
			List<HashMap> list = new ArrayList<HashMap>();
			int cnt = 0;

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				
				LOGGER.warn("i : "+i);
				
				Row row = sheet.getRow(i);
				// 행이 존재하기 않으면 패스
                if(row == null) {
                    continue;
                }

                if(row.getCell(0) != null) {
                	
					String idx = getValue(row.getCell(0)).replaceAll("\n", "<br>");	//순번
					String funm = getValue(row.getCell(1)).replaceAll("\n", "<br>");	//이름
					String fpartnm2 = getValue(row.getCell(2)).replaceAll("\n", "<br>");	//소속
					String fsdt = getValue(row.getCell(3)).replaceAll("\n", "<br>");	//출입시작일
					String fedt = getValue(row.getCell(4)).replaceAll("\n", "<br>");	//출입만료일
					
					LOGGER.warn("idx / funm / fpartnm2 : {} / {} / {} ", idx, funm, fpartnm2 );

	                if(funm.length() > 0) {
				    	HashMap insMap = new HashMap();
				    	insMap.put("siteId", "11");
				    	insMap.put("registId", "SYSTEM");
			    	
				    	insMap.put("funm", funm);
				    	insMap.put("fpartnm1", StringUtil.nvl(gbUserFpartnm1));
				    	insMap.put("fpartnm2", fpartnm2);
				    	insMap.put("fsdt", fsdt);
				    	insMap.put("fedt", fedt);
				    	
				    	LOGGER.warn("[출입자엑셀업로드] [출입자정보] insMap : {}", insMap);

						//String image_local_file = GLOBAL_FILESTORAGE + fuid;
						String image_local_file = gbUserImagePath + idx; //D:\\cubox\\excelImages\\

				        File fileCheck_jpg = new File(image_local_file + ".jpg");
				        File fileCheck_jpeg = new File(image_local_file + ".jpeg");
				        File fileCheck_bmp = new File(image_local_file + ".bmp");
				        File fileCheck_png = new File(image_local_file + ".png");
				        File fileCheck_gif = new File(image_local_file + ".gif");

			        	byte[] imgByte = null;

						if (fileCheck_jpg.exists() && fileCheck_jpg.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_jpg);
						} else if (fileCheck_jpeg.exists() && fileCheck_jpeg.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_jpeg);
						} else if (fileCheck_bmp.exists() && fileCheck_bmp.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_bmp);
						} else if (fileCheck_png.exists() && fileCheck_png.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_png);
						} else if (fileCheck_gif.exists() && fileCheck_gif.isFile()) {
							imgByte = FileUtils.readFileToByteArray(fileCheck_gif);
						}

				        if(imgByte != null && imgByte.length > 0){
				        	LOGGER.warn("===== 이미지 있음 ===========");
				        	
				        	String imgString = aes256.byteArrEncode(imgByte, GLOBAL_AES256_KEY);
				        	insMap.put("fimg", imgString);
				        } else {
				        	LOGGER.warn("===== 이미지 없음!! ===========");
				        }
				        
				        cnt =+ userInfoService.insertUserInfoForExcel(insMap);
				        
				        
				        LOGGER.warn("insertUserInfoForExcel cnt : {}", cnt);
				        
				        //list.add(insMap); //list로 등록
	                }
                }
			}
			
			
			if(cnt > 0) {
				modelAndView.addObject("result", "success");
				modelAndView.addObject("message", "success");	
			} else {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "엑셀파일 업로드 실패");
			}
			
			/* 2022-04-15 list로 등록하는 경우
			if(list != null && list.size() > 0 ) {
				
				int cnt = userInfoService.insertUserListForExcel(list);
		        
				if(cnt == list.size()) {
					modelAndView.addObject("result", "success");
					modelAndView.addObject("message", "success");
					
					//로그
					commonService.sysLogSave(loginVO.getFsiteid(), "13101010", String.valueOf(cnt)+"건 출입자", cntnIp); //엑셀업로드
					
				} else if (cnt < list.size()) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "엑셀파일에 등록된 출입자를 모두 저장하지 못함");
				} else if (cnt == 0 ) {
					modelAndView.addObject("result", "fail");
					modelAndView.addObject("message", "엑셀파일 업로드 실패");
				}
		        
			} else {
				modelAndView.addObject("result", "fail");
				modelAndView.addObject("message", "엑셀파일에 출입자가 없음");
			}*/
        } catch (Exception e) {
        	modelAndView.addObject("result", "fail");
        	modelAndView.addObject("message", e.getMessage());
            e.printStackTrace();
        }

        return modelAndView;
    }	
	
	public static String getValue(Cell cell) {
        String value = "";

        if(cell == null) {
            value = "";
        }else{
            if( cell.getCellType() == Cell.CELL_TYPE_FORMULA ) {
                value = cell.getCellFormula();
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
            	if(DateUtil.isCellDateFormatted(cell)){
            		Date date = cell.getDateCellValue();
            		value = new SimpleDateFormat("yyyy-MM-dd").format(date);
            	}else{
            		value = String.valueOf((long)cell.getNumericCellValue());
            	}

            }
            else if( cell.getCellType() == Cell.CELL_TYPE_STRING ) {
                value = cell.getStringCellValue();
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_BOOLEAN ) {
                value = cell.getBooleanCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_ERROR ) {
                value = cell.getErrorCellValue() + "";
            }
            else if( cell.getCellType() == Cell.CELL_TYPE_BLANK ) {
                value = "";
            }
            else {
                value = cell.getStringCellValue();
            }
        }

        return value.trim();
    }	
	
}