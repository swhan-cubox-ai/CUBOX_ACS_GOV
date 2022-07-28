package aero.cubox.util;

import aero.cubox.cmmn.service.FileImageZipService;
import aero.cubox.sample.service.LogInfoService;
import aero.cubox.sample.service.vo.ExcelVO;
import aero.cubox.sample.service.vo.LogBioRealInfoVO;
import aero.cubox.user.service.vo.UserBioInfoVO;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.AbstractView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("excelDownloadView")
public class ExcelDownloadView extends AbstractView {

	@Resource(name="FileImageZipService")
	private FileImageZipService fileImageZipService;

	@Resource(name = "logInfoService")
	private LogInfoService logInfoService;
	
	private String GLOBAL_AES256_KEY = CuboxProperties.getProperty("Globals.aes256.key");	
    private static final String CONTENT_TYPE = "application/vnd.ms-excel"; // Content Type 설정
    
    public ExcelDownloadView() {
        setContentType(CONTENT_TYPE);
    }

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDownloadView.class);
   
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, 
    			HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	ServletOutputStream out = null;
    	ModuleUtil moduleUtil = null;
        try {
        	moduleUtil = ModuleUtil.getInstance();
        	String excelName = StringUtil.nvl(model.get("excelName"));
        	String strImgYn = StringUtil.nvl(model.get("strImgYn"), "N"); 
        	String strImgGb = StringUtil.nvl(model.get("strImgGb"), "F"); //F:출입자이미지, L:출입실시간이미지 (strImgYn=Y인 경우만 유의미함.)
        	        	
            // 파일 이름 설정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c1 = Calendar.getInstance();
            String yyyymmdd = sdf.format(c1.getTime());
            String fileName = excelName + "_" + yyyymmdd +".xlsx";
            fileName = URLEncoder.encode(fileName,"UTF-8"); // UTF-8로 인코딩
            
            // 다운로드 되는 파일명 설정
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
           
            // SXSSFWorkbook 생성
            XSSFWorkbook workbook = new XSSFWorkbook();
            //workbook.setCompressTempFiles(true);
 
            // SXSSFSheet 생성
            XSSFSheet sheet = (XSSFSheet) workbook.createSheet();
            //sheet.setRandomAccessWindowSize(100); // 메모리 행 100개로 제한, 초과 시 Disk로 flush
           
            // 엑셀에 출력할 List
            List<ExcelVO> resultList = (List<ExcelVO>) model.get("resultList");
            
            // Cell 스타일 값
            sheet.setDefaultColumnWidth(12);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("맑은 고딕");
            font.setFontHeightInPoints((short) 8);
            font.setColor(HSSFColor.BLACK.index);
            style.setFont(font);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  
            style.setWrapText(true);            
            
            ///////////헤더줄////////////////////
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setFontName("맑은 고딕");
            headerFont.setFontHeightInPoints((short) 9);
            headerFont.setColor(HSSFColor.BLACK.index);
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
            headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            headerStyle.setFillForegroundColor(HSSFColor.GOLD.index);
            headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            ////////////////////////////////////////
                  
            // header 생성
            String headerList[] = (String[])model.get("excelHeader");
            XSSFRow header = (XSSFRow) sheet.createRow(0);
            String imgHeaderList[] = new String[headerList.length+1];
            imgHeaderList[0] = "이미지";            
            System.arraycopy(headerList, 0, imgHeaderList, 1, headerList.length);
            sheet.setColumnWidth(0, 2100);
            if(strImgYn.equals("Y")) {
            	setHeaderCellValue(header, imgHeaderList, headerStyle); // 헤더 칼럼명 설정 
            } else {
            	setHeaderCellValue(header, headerList, headerStyle); // 헤더 칼럼명 설정 
            } 
            // 행 데이터 생성
            int rowCount = 1;
            moduleUtil.setCurrentStateCount(0);
            if(resultList != null) {
            	moduleUtil.setTotalRowCount(resultList.size());              	
		        for (ExcelVO cellVO : resultList) {	
		        	XSSFRow aRow = (XSSFRow) sheet.createRow(rowCount++);
		            setEachRow(aRow, cellVO, style, strImgYn);	
		            if(strImgYn.equals("Y")) {
		            	if(strImgGb.equals("F") || strImgGb.equals("L")) {
		            		HashMap<String, String> param = new HashMap<String, String>();
		            		param.put("sImgGb", strImgGb);
		            		param.put("pFuid", StringUtil.nvl(cellVO.getFuid()));
		            		param.put("pFevttm", StringUtil.nvl(cellVO.getFevttm()));
		            		setImgRow(param, sheet, workbook, aRow);
		            	}
		            }
		            //if(strImgYn!=null && strImgYn.equals("Y")) setImgRow("VISIT_18082", sheet, workbook, aRow); //이미지 추가	
		            moduleUtil.setCurrentStateCount(moduleUtil.getCurrentStateCount()+1);
		        }
	        } else {
	        	moduleUtil.setTotalRowCount(0);
	        }                       
            out = response.getOutputStream();
            workbook.write(out);            
            moduleUtil.setCurrentState("C");
            LOGGER.debug("Excel 완료");            
        } catch (Exception e) {
        	moduleUtil.setCurrentState("C");
            throw e;
        } finally {
        	if (out != null) out.close();        	
        	moduleUtil.resetModuleUtil();
		}
    }
   
    private void setHeaderCellValue(XSSFRow header, String[] dataInfo, CellStyle headerStyle) {
        for(int i=0; i < dataInfo.length; i++){
        	Cell cell = header.createCell(i);
        	cell.setCellValue(dataInfo[i]);
        	cell.setCellStyle(headerStyle);
        }
    }
   
   
    private void setEachRow(XSSFRow aRow, ExcelVO excelVO, CellStyle style, String strImgYn) {
    	int i = 0;
    	if(strImgYn.equals("Y")) i = 1;
    	
    	Cell cell00 = aRow.createCell(i++);
    	cell00.setCellValue(excelVO.getCELL1());
    	cell00.setCellStyle(style);
    	
    	Cell cell01 = aRow.createCell(i++);
    	cell01.setCellValue(excelVO.getCELL2());
    	cell01.setCellStyle(style);
    	
    	Cell cell02 = aRow.createCell(i++);
    	cell02.setCellValue(excelVO.getCELL3());
    	cell02.setCellStyle(style);
    	
    	Cell cell03 = aRow.createCell(i++);
    	cell03.setCellValue(excelVO.getCELL4());
    	cell03.setCellStyle(style);
    	
    	Cell cell04 = aRow.createCell(i++);
    	cell04.setCellValue(excelVO.getCELL5());
    	cell04.setCellStyle(style);
    	
    	Cell cell05 = aRow.createCell(i++);
    	cell05.setCellValue(excelVO.getCELL6());
    	cell05.setCellStyle(style);
    	
    	Cell cell06 = aRow.createCell(i++);
    	cell06.setCellValue(excelVO.getCELL7());
    	cell06.setCellStyle(style);
    	
    	Cell cell07 = aRow.createCell(i++);
    	cell07.setCellValue(excelVO.getCELL8());
    	cell07.setCellStyle(style);
    	
    	Cell cell08 = aRow.createCell(i++);
    	cell08.setCellValue(excelVO.getCELL9());
    	cell08.setCellStyle(style);
    	
    	Cell cell09 = aRow.createCell(i++);
    	cell09.setCellValue(excelVO.getCELL10());
    	cell09.setCellStyle(style);
    	
    	Cell cell10 = aRow.createCell(i++);
    	cell10.setCellValue(excelVO.getCELL11());
    	cell10.setCellStyle(style);
    	
    	Cell cell11 = aRow.createCell(i++);
    	cell11.setCellValue(excelVO.getCELL12());
    	cell11.setCellStyle(style);
    	
    	Cell cell12 = aRow.createCell(i++);
    	cell12.setCellValue(excelVO.getCELL13());
    	cell12.setCellStyle(style);
    	
    	Cell cell13 = aRow.createCell(i++);
    	cell13.setCellValue(excelVO.getCELL14());
    	cell13.setCellStyle(style);
    	
    	Cell cell14 = aRow.createCell(i++);
    	cell14.setCellValue(excelVO.getCELL15());
    	cell14.setCellStyle(style);
    	
    	Cell cell15 = aRow.createCell(i++);
    	cell15.setCellValue(excelVO.getCELL16());
    	cell15.setCellStyle(style);
    	
    	Cell cell16 = aRow.createCell(i++);
    	cell16.setCellValue(excelVO.getCELL17());
    	cell16.setCellStyle(style);
    	
    	Cell cell17 = aRow.createCell(i++);
    	cell17.setCellValue(excelVO.getCELL18());
    	cell17.setCellStyle(style);
    	
    	Cell cell18 = aRow.createCell(i++);
    	cell18.setCellValue(excelVO.getCELL19());
    	cell18.setCellStyle(style);
    	
    	Cell cell19 = aRow.createCell(i++);
    	cell19.setCellValue(excelVO.getCELL20());
    	cell19.setCellStyle(style);
    	
    	Cell cell20 = aRow.createCell(i++);
    	cell20.setCellValue(excelVO.getCELL21());
    	cell20.setCellStyle(style);
    	
    	Cell cell21 = aRow.createCell(i++);
    	cell21.setCellValue(excelVO.getCELL22());
    	cell21.setCellStyle(style);
    	
    	Cell cell22 = aRow.createCell(i++);
    	cell22.setCellValue(excelVO.getCELL23());
    	cell22.setCellStyle(style);
    	
    	Cell cell23 = aRow.createCell(i++);
    	cell23.setCellValue(excelVO.getCELL24());
    	cell23.setCellStyle(style);
    	
    	Cell cell24 = aRow.createCell(i++);
    	cell24.setCellValue(excelVO.getCELL25());
    	cell24.setCellStyle(style);
    	
    	Cell cell25 = aRow.createCell(i++);
    	cell25.setCellValue(excelVO.getCELL26());
    	cell25.setCellStyle(style);
    	
    	Cell cell26 = aRow.createCell(i++);
    	cell26.setCellValue(excelVO.getCELL27());
    	cell26.setCellStyle(style);
    	
    	Cell cell27 = aRow.createCell(i++);
    	cell27.setCellValue(excelVO.getCELL28());
    	cell27.setCellStyle(style);
    	
    	Cell cell28 = aRow.createCell(i++);
    	cell28.setCellValue(excelVO.getCELL29());
    	cell28.setCellStyle(style);
    	
    	Cell cell29 = aRow.createCell(i++);
    	cell29.setCellValue(excelVO.getCELL30());
    	cell29.setCellStyle(style);   
    }
    
    /**
     * 엑셀 이미지 추가
     * @param param
     *        sImgGb : 이미지구분 (F:출입자 등록이미지, L:출입이력 이미지)
     *        pFuid : 출입자 FUID (F, L 모두 사용) 
     *        pFevttm : 출입시각   (L만 사용)
     * @param sheet
     * @param workbook
     * @param aRow
     */
    private void setImgRow(HashMap<String, String> param, XSSFSheet sheet, XSSFWorkbook workbook, XSSFRow aRow) {     	
    	AES256Util aes256 = null;
    	byte[] imageContent = null;	
    	try {    
    		String strImg = "";
    		if(param.get("sImgGb").equals("F")) {
	    		UserBioInfoVO uv = new UserBioInfoVO();
	        	uv.setFuid(param.get("pFuid"));
				UserBioInfoVO dvo = fileImageZipService.getUserBioInfo(uv);
				if(dvo != null) {
					strImg = dvo.getFimg();
				}
    		} else if(param.get("sImgGb").equals("L")) {
    			LogBioRealInfoVO userBioInfo = new LogBioRealInfoVO();
    			userBioInfo.setFuid(param.get("pFuid"));
    			userBioInfo.setFevttm(param.get("pFevttm"));
    			LogBioRealInfoVO bvo = logInfoService.getLogBioRealInfo(userBioInfo);
    			if(bvo != null) {
    				strImg = bvo.getFcimg();
    			}
    		}
			if(!StringUtil.nvl(strImg).equals("")) {
				aes256 = new AES256Util();
		    	imageContent = aes256.byteArrDecodenopaddingImg(strImg, GLOBAL_AES256_KEY); 
		    	LOGGER.debug("imageContent.length >>>> "+imageContent.length);
		    	//String fileId = dvo.getFuid();
		    	
		    	if(imageContent.length > 0) {
		    		aRow.setHeight((short) 1300);		    	
		            int pictureIdx = workbook.addPicture(imageContent, XSSFWorkbook.PICTURE_TYPE_JPEG);	            
		            LOGGER.debug("pictureIdx >>>" + pictureIdx);		    	
			    	XSSFCreationHelper helper = workbook.getCreationHelper();
		            XSSFDrawing drawing = sheet.createDrawingPatriarch();
		            XSSFClientAnchor anchor = helper.createClientAnchor();
		            anchor.setCol1( 0 ); //정확한 값 기준은 모르겠지만 합병된 셀 행, 열값으로 입력함. 잘나오네 
		            anchor.setRow1( aRow.getRowNum() ); // same row is okay 
		            anchor.setCol2( 0+1 ); 
		            anchor.setRow2( aRow.getRowNum()+1 );            
		            
		            anchor.setDx1(0); 
		            anchor.setDx2(1000); //정확한 값 기준은 모르겠지만 Dx2, Dy2 1000으로 하면 셀 크기에 딱맞게 줄여져서 나옴. 하단에 pict.resize()는 하면 안됨. 
		            anchor.setDy1(0); 
		            anchor.setDy2(1000);
		            XSSFPicture pict = drawing.createPicture(anchor, pictureIdx);
		         	//이미지 사이즈 비율 설정
		            //pict.resize();
		    	}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			aes256 = null;
			imageContent = null;
		}
    }    
}
