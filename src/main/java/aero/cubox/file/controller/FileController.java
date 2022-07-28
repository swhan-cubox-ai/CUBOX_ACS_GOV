package aero.cubox.file.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aero.cubox.sample.service.vo.LoginVO;
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
import org.springframework.web.servlet.ModelAndView;

import aero.cubox.file.service.FileService;
import aero.cubox.file.service.vo.FileVO;

@Controller
@RequestMapping(value = "/file/")
public class FileController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);
	private String NO_IMAGE_NAME = "no_img.gif";
	private String NO_IMAGE_PATH = "/images/" + NO_IMAGE_NAME;

	@Resource
	private FileService fileService;

	@RequestMapping(value="/imageListPopup/")
	public String imageList(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		List<FileVO> result = fileService.getFileList(vo);

		model.addAttribute("result", result);
		return "cubox/file/imageList";
	}

	@RequestMapping(value="/fileListIframe.do")
	public String fileListIframe(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
//		HttpSession session = request.getSession();
//		String esntl_id = String.valueOf(session.getAttribute("esntl_id"));
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String ifr_id = String.valueOf(request.getParameter("ifr_id"));

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		List<FileVO> result = fileService.getFileList(vo);

		model.addAttribute("ifr_id", ifr_id);
		model.addAttribute("owner", "N");
		model.addAttribute("result", result);
		return "cubox/file/fileList";
	}
	
	@RequestMapping(value="/floorFileListIframe.do")
	public String floorFileListIframe(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
//		HttpSession session = request.getSession();
//		String esntl_id = String.valueOf(session.getAttribute("esntl_id"));
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String ifr_id = String.valueOf(request.getParameter("ifr_id"));

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		List<FileVO> result = fileService.getFloorFileList(vo);

		model.addAttribute("ifr_id", ifr_id);
		model.addAttribute("owner", "N");
		model.addAttribute("result", result);
		return "cubox/file/fileList";
	}

	@RequestMapping(value="/fileListOwnerIframe.do")
	public String fileListOwnerIframe(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		//String registId = loginVO.getSite_nm();
		String registId = loginVO.getFsiteid();
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String ifr_id = String.valueOf(request.getParameter("ifr_id"));

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);

		//본인여부
		FileVO rMap = fileService.getFileManage(vo);
		if(rMap != null){
			String writng_id = rMap.getRegistId();
			if(registId.equals(writng_id)){
				model.addAttribute("owner", "Y");
			}
		}

		List<FileVO> result = fileService.getFileList(vo);

		model.addAttribute("ifr_id", ifr_id);
		model.addAttribute("result", result);
		
		return "cubox/file/fileList";
	}


	@RequestMapping(value="/fileDetailPopup/")
	public String fileDetailPopup(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
//		HttpSession session = request.getSession();
//		String esntl_id = String.valueOf(session.getAttribute("esntl_id"));
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String fileSn = String.valueOf(request.getParameter("fileSn"));

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		vo.setFileSn(Integer.parseInt(fileSn));
		FileVO result = fileService.getFileDetail(vo);

		model.addAttribute("result", result);
		return "cubox/file/fileDetailPopup";
	}


	@RequestMapping("/deleteAjax.do")
	public ModelAndView deleteAjax(HttpServletRequest request) throws Exception {
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String fileSn = String.valueOf(request.getParameter("fileSn"));
		String result = "1";

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		vo.setFileSn(Integer.parseInt(fileSn));
		FileVO rMap = fileService.getFileDetail(vo);
		if(rMap != null){
			String sys_file_path = (String) rMap.getFileStreCours();
			File fileCheck = new File(sys_file_path);
			if(fileCheck.exists() ){
				if(fileCheck.delete()){
					//삭제성공
				}else{
					//삭제실패
				}
			}else{
				//파일없음
			}
			
			fileService.deleteFileDetail(vo);//vo.setUseAt('N');fileService.updateFileDetail(vo);
		}else{
			result = "-1";
		}

		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("result", result);

		return new ModelAndView("jsonView", "ajaxData", ret);
	}

	@RequestMapping(value="/getFileBinary.do")
	public void getFileBinary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String fileSn = String.valueOf(request.getParameter("fileSn"));

		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		vo.setFileSn(Integer.parseInt(fileSn));
		FileVO result = fileService.getFileDetail(vo);

		if(result == null){
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.println("<script type='text/javascript'>");
			writer.println("alert('파일정보를 찾을 수 없습니다.');");
			writer.println("history.back();");
			writer.println("</script>");
			writer.flush();
		}else{
			String realPath = String.valueOf(result.getFileStreCours());
			//String fileName = SimpleUtils.default_set((String) result.get("FILE_DC"));  ??? 왜?? 2019-07-01
			String fileName = String.valueOf(result.getOrignlFileNm());

			try{

				File uFile = new File(realPath);

				boolean fileCheck = true;
				if (!uFile.exists())
					fileCheck = false;

				if (!uFile.isFile())
					fileCheck = false;

				if(fileCheck){

					String browser = request.getHeader("User-Agent");
					if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
						fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
					} else {
						fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
					}

					response.setContentType("application/octer-stream");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName +"\"");
					response.setHeader("Content-Transfer-Encoding", "binary;");

					byte b[] = new byte[(int)uFile.length()];

					BufferedInputStream in = null;
					BufferedOutputStream out = null;
					// 응답객체생성
					int read = 0;

					in = new BufferedInputStream(new FileInputStream(uFile));
					out = new BufferedOutputStream(response.getOutputStream());

					while ((read = in.read(b)) != -1)
						out.write(b,0,read);

					if(out!=null)
						out.close();
					if(in!=null)
						in.close();
				}else{
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter writer = response.getWriter();
					writer.println("<script type='text/javascript'>");
					writer.println("alert('해당 파일이 존재하지 않습니다.');");
					writer.println("history.back();");
					writer.println("</script>");
					writer.flush();
					writer.close();
				}

			}catch(Exception e){
				LOGGER.debug("download error : " + e.getMessage());

				response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script type='text/javascript'>");
				writer.println("alert('다운로드중 오류가 발생했습니다.');");
				writer.println("history.back();");
				writer.println("</script>");
				writer.flush();
				writer.close();
			}
		}
		LOGGER.debug("GetFile End");
	}
	
	@RequestMapping(value="/getFileImage.do")
	public ResponseEntity<byte[]> getFileImage(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		
		ResponseEntity entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		entity = new ResponseEntity(HttpStatus.BAD_REQUEST);
		
		String siteId = String.valueOf(request.getParameter("search_item_center"));
		String idx = String.valueOf(request.getParameter("search_item_floor"));
		
//		String siteId = String.valueOf(request.getParameter("siteId"));
//		String idx = String.valueOf(request.getParameter("floor"));
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("siteId", siteId);
		map.put("idx", idx);
		
		String atchFileId = fileService.getAtchFileId(map);
		
		String noImagePath = request.getSession().getServletContext().getRealPath("/") + NO_IMAGE_PATH;
		String realPath = noImagePath;
		String fileName = NO_IMAGE_NAME;
		
		// 1. 층, 사이트 아이디로 fileId 가져오기
		// 2. file_tb에서 use_at이 Y인지 확인
		// 3. file_detail_tb 에서 file 경로로 파일 불러오기
		
		FileVO vo = new FileVO();
		vo.setAtchFileId("00000000000000000044");
		
		FileVO result = fileService.getImageFileDetail(vo);
		
		if(result != null){
			realPath = String.valueOf(result.getFileStreCours());
			fileName = String.valueOf(result.getStreFileNm());
		}
		
		FileInputStream fileStream = null;

		try {
			File getResource = new File(realPath);
			if(!getResource.exists()){
				realPath = noImagePath;
				getResource = new File(realPath);
			}
			byte byteStream[] = new byte[(int)getResource.length()];
			fileStream = new FileInputStream(getResource);
			int i = 0;
			for(int j = 0; (i = fileStream.read()) != -1; j++)
				byteStream[j] = (byte)i;

			responseHeaders.setContentType(MediaType.IMAGE_JPEG);
			responseHeaders.set("Content-Disposition", "attatchment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") +"\"");
			entity = new ResponseEntity(byteStream, responseHeaders, HttpStatus.OK);
		} catch(Exception e) {
			fileStream.close();
		} finally {
			fileStream.close();
		}
		return entity;
		
	}
	/*
	@RequestMapping(value="/getImageBinary/")
	public ResponseEntity<byte[]> getImageBinary(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		ResponseEntity entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		entity = new ResponseEntity(HttpStatus.BAD_REQUEST);

		String atchFileId = String.valueOf(request.getParameter("atchFileId"));
		String fileSn = String.valueOf(request.getParameter("fileSn"));

		String noImagePath = request.getSession().getServletContext().getRealPath("/") + NO_IMAGE_PATH;
		String realPath = noImagePath;
		String fileName = NO_IMAGE_NAME;

		
		FileVO vo = new FileVO();
		vo.setAtchFileId(atchFileId);
		vo.setFileSn(Integer.parseInt(fileSn));
		
		
		FileVO result = fileService.getFileDetail(vo);

		if(result != null){
			realPath = String.valueOf(result.get("SYS_FILE_PATH"));
			fileName = String.valueOf(result.get("FILE_NM"));
		}

		FileInputStream fileStream = null;

		try {
			File getResource = new File(realPath);
			if(!getResource.exists()){
				realPath = noImagePath;
				getResource = new File(realPath);
			}
			byte byteStream[] = new byte[(int)getResource.length()];
			fileStream = new FileInputStream(getResource);
			int i = 0;
			for(int j = 0; (i = fileStream.read()) != -1; j++)
				byteStream[j] = (byte)i;

			responseHeaders.setContentType(MediaType.IMAGE_JPEG);
			responseHeaders.set("Content-Disposition", "attatchment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") +"\"");
			entity = new ResponseEntity(byteStream, responseHeaders, HttpStatus.OK);
		} catch(Exception e) {
			fileStream.close();
		} finally {
			fileStream.close();
		}
		return entity;
	}

	/*@RequestMapping(value="/getBioFileBinary/")
	public void getBioFileBinary(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String esntl_id = String.valueOf(request.getParameter("esntl_id"));
		String file_ty = String.valueOf(request.getParameter("file_ty"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("esntl_id", esntl_id);
		map.put("file_ty", file_ty);
		Map<String, Object> result = fileService.getBioFileDetail(map);

		if(result == null){
            response.setContentType("text/html; charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.println("<script type='text/javascript'>");
			writer.println("alert('파일정보를 찾을 수 없습니다.');");
			writer.println("history.back();");
			writer.println("</script>");
			writer.flush();
		}else{
			BufferedInputStream in = null;
			BufferedOutputStream out = null;			
			try{
				Blob bfileInfo = (Blob) result.get("FILE_INFO");
				String fileName = String.valueOf(result.get("ESNTL_ID"));
				
				boolean fileCheck = true;

				if (bfileInfo == null || bfileInfo.length() < 10)
					fileCheck = false;

				if(fileCheck) {

					String browser = request.getHeader("User-Agent");
					if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
						fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
					} else {
						fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
					}
					
					fileName += ".jpg";

					response.setContentType("application/octer-stream");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName +"\"");
					response.setHeader("Content-Transfer-Encoding", "binary;");

					int blength = (int) bfileInfo.length();
					byte b[] = new byte[blength];
				
					// 응답객체생성
					int read = 0;

					in = new BufferedInputStream(bfileInfo.getBinaryStream());
					out = new BufferedOutputStream(response.getOutputStream());

					while ((read = in.read(b)) != -1) out.write(b,0,read);
				}else{
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter writer = response.getWriter();
					writer.println("<script type='text/javascript'>");
					writer.println("alert('해당 파일이 존재하지 않습니다.');");
					writer.println("history.back();");
					writer.println("</script>");
					writer.flush();
					writer.close();
				}

			}catch(Exception e){
				LOGGER.debug("download error : " + e.getMessage());

				response.setContentType("text/html; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.println("<script type='text/javascript'>");
				writer.println("alert('다운로드중 오류가 발생했습니다.');");
				writer.println("history.back();");
				writer.println("</script>");
				writer.flush();
				writer.close();
			} finally {
				if(out!=null) out.close();
				if(in!=null)in.close();
			}
		}
		LOGGER.debug("GetFile End");
	}
	
	@RequestMapping(value="/getImageBinary/")
	public ResponseEntity<byte[]> getImageBinary(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		ResponseEntity entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		entity = new ResponseEntity(HttpStatus.BAD_REQUEST);

		String file_id = String.valueOf(request.getParameter("file_id"));
		String file_sn = String.valueOf(request.getParameter("file_sn"));

		String noImagePath = request.getSession().getServletContext().getRealPath("/") + NO_IMAGE_PATH;
		String realPath = noImagePath;
		String fileName = NO_IMAGE_NAME;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("file_id", file_id);
		map.put("file_sn", file_sn);
		Map<String, Object> result = fileService.getFileDetail(map);

		if(result != null){
			realPath = String.valueOf(result.get("SYS_FILE_PATH"));
			fileName = String.valueOf(result.get("FILE_NM"));
		}

		FileInputStream fileStream = null;

		try {
			File getResource = new File(realPath);
			if(!getResource.exists()){
				realPath = noImagePath;
				getResource = new File(realPath);
			}
			byte byteStream[] = new byte[(int)getResource.length()];
			fileStream = new FileInputStream(getResource);
			int i = 0;
			for(int j = 0; (i = fileStream.read()) != -1; j++)
				byteStream[j] = (byte)i;

			responseHeaders.setContentType(MediaType.IMAGE_JPEG);
			responseHeaders.set("Content-Disposition", "attatchment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") +"\"");
			entity = new ResponseEntity(byteStream, responseHeaders, HttpStatus.OK);
		} catch(Exception e) {
			fileStream.close();
		} finally {
			fileStream.close();
		}
		return entity;
	}

	@RequestMapping(value="/getStreamBinary/")
	public void streamView(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

		//대상 동영상 파일명
		String file_id = String.valueOf(request.getParameter("file_id"));
		String file_sn = String.valueOf(request.getParameter("file_sn"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("file_id", file_id);
		map.put("file_sn", file_sn);
		Map<String, Object> result = fileService.getFileDetail(map);

		StreamView sv = new StreamView();
		if(result != null){
			String realPath = (String) result.get("SYS_FILE_PATH");
			String fileName = (String) result.get("FILE_NM");
			//스트리밍 시작
			sv.makeStream(realPath, request, response);
		}
	}*/
	
	
	@RequestMapping(value="/displayImage.do")
	public String displayImage(@RequestParam(value="siteId") String siteId, @RequestParam(value="floorIdx") String floorIdx, HttpServletRequest request, HttpServletResponse response)throws Exception{

		//DB에 저장된 파일 정보를 불러오기
		HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("siteId", siteId);
	    map.put("idx", floorIdx);
	    
	    String atchFileId = fileService.getAtchFileId(map);
	    
		String noImagePath ="";
		String    realPath = "";
		String    fileName = "";
		
		String imgpath = "";
		
		
	    if(  atchFileId == null || atchFileId == "") {
	    	
	    	 noImagePath = request.getSession().getServletContext().getRealPath("/") + "/images/cad_basic.png";
			   fileName = NO_IMAGE_NAME;
			   
			   imgpath = noImagePath;
	    } else {
	    	FileVO vo = new FileVO();
	    	vo.setAtchFileId(atchFileId);
	    	
	    	FileVO result = fileService.getImageFileDetail(vo);
	    	
	    	//파일의 경로
		    imgpath = result.getFileStreCours();
	    }
	    
		response.setContentType("image/jpg");
	    ServletOutputStream bout = response.getOutputStream();
	    
	    FileInputStream fis = new FileInputStream(imgpath);
	    int length;
	    
	    byte[] buffer = new byte[10];
	    while((length=fis.read(buffer)) != -1){
	    	bout.write(buffer,0,length);
	    }
	    return null;
	}
	
	
}
