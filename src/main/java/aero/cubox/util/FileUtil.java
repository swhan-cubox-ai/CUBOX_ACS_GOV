package aero.cubox.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import aero.cubox.file.service.vo.FileVO;

public class FileUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	public String makeFile(String target, String contents){
		String result = "";
		if(StringUtils.isEmpty(target) || StringUtils.isEmpty(contents)){
			result = "작성할 내용이 없습니다.";
		}else{
			try{
	            
	            // 파일 객체 생성
	            File file = new File(target) ;
	            file.createNewFile();
	            
	            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()),"UTF8"));
	            // 파일안에 문자열 쓰기
	            output.write(contents);
	            output.flush();
	            // 객체 닫기
	            output.close();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
		}
		return result;
	}
	
	public List<File> subDirList(String source){
		LOGGER.debug("param source = " + source);
		File dir = new File(source); 
		LOGGER.debug("dir : " + dir);
		File[] fileList = dir.listFiles(); 
		List<File> files = new ArrayList<File>();
		try{
			for(File ff : fileList){
				if(ff.isFile()){
					// 파일이 있다면 파일 이름 출력
					LOGGER.debug("\t 파일 이름 = " + ff.getName());
					if(ff.getName().indexOf(".jsp")>0) files.add(ff);
				}else if(ff.isDirectory()){
					LOGGER.debug("디렉토리 이름 = " + ff.getName());
					// 서브디렉토리가 존재하면 재귀적 방법으로 다시 탐색
					subDirList(ff.getCanonicalPath().toString()); 
				}
			}
		}catch(IOException e){
			
		}
		return files;
	}
	
	public void makeContentsJsp(String from, String to){
		List<File> files = subDirList(from);
		File toDir = new File(to);
		if(toDir.exists() && toDir.isDirectory()){
			LOGGER.debug(to+"가 이미 존재합니다.");
		}
		else{
			toDir.mkdir();
		}
		for(File ff : files){
			if(ff.getName().indexOf(".jsp") > 0 
//						&& ff.getName().indexOf("list.jsp") < 0
					){
				try {
					Document doc = Jsoup.parse(ff,"UTF-8");
					String selector = "div.board_wrap";
					if(!doc.select(selector).isEmpty()){
						LOGGER.debug("start convert : "+ff.getPath() + " -> "+to+"/"+ff.getName());
						String content_in = "<%@ page language=\"java\" contentType=\"text/html; charset=utf-8\" pageEncoding=\"utf-8\"%>\n"+doc.select(selector).first().toString();
						makeFile(to+"/"+ff.getName(),content_in);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
		FileUtil fu = new FileUtil();
		String projectPath = "/Users/choegyeonghun/Desktop/workspace/test";
		String jspPath = "/src/main/webapp/WEB-INF/jsp";
		String targetPath = "/archive/contents";
		
//		fu.subDirList(projectPath+jspPath+targetPath);
		fu.makeContentsJsp(projectPath+jspPath+targetPath+"_design", projectPath+jspPath+targetPath);
	}
	
	public static FileVO parseFileInfo(
			List<MultipartFile> files
			, String atchFileId
			, List<String> formatList
			, String RESOURCE_PATH
			//, String esntl_id
			//, String fileTitle
		) throws Exception {
		FileVO map = new FileVO();
		map.setAtchFileId(atchFileId);//첨부파일ID
		//map.put("esntl_id", esntl_id);
		List<FileVO> fileList = new ArrayList<FileVO>();
		int i = 1;
		if (!files.isEmpty()) {
			String filePath = RESOURCE_PATH + atchFileId + File.separator;
			File pathCheck = new File(filePath);
			if(!pathCheck.exists() || !pathCheck.isDirectory())
				pathCheck.mkdirs();
			for (MultipartFile mFile : files) {
				if (mFile.isEmpty()) continue;

				String fOrgName = mFile.getOriginalFilename();
				String fFormat = "";
				if(fOrgName.length() > 0){
					fFormat = fOrgName.substring(fOrgName.lastIndexOf(".")+1).toLowerCase();
				}

				//format validation
				if(fFormat != null && formatList.contains(fFormat)) {
					FileVO fMap = new FileVO();
					fMap.setAtchFileId(atchFileId);
					String fileName = atchFileId + "_" + i + "." + fFormat;
					//String fileTitleName = fileTitle + "_" + i + "." + fFormat;
					File fileCheck = new File(filePath + fileName);
					while(fileCheck.exists() && fileCheck.isFile()){
						i++;
						fileName = atchFileId + "_" + i + "." + fFormat;
						//fileTitleName = fileTitle + "_" + i + "." + fFormat;
						fileCheck = new File(filePath + fileName);
					}

					long fSize = fileSave(mFile, filePath + fileName);

					fMap.setFileStreCours(filePath + fileName);//파일저장경로
					fMap.setStreFileNm(fileName);//저장파일명
					fMap.setOrignlFileNm(fOrgName);//원파일명
					fMap.setFileExtsn(fFormat);//파일확장자
					fMap.setFileCn(null);//파일내용
					fMap.setFileSize(fSize);//파일크기

					fileList.add(fMap);
					i++;
				}
			}
		}
		map.setFileList(fileList);
		map.setFileCount(fileList.size());
		return map;
	}
	
	public static long fileSave(MultipartFile file, String filePath) throws Exception {
		File f = new File(filePath);

		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		OutputStream os = null;
		InputStream is = null;
		final int BUFFER_SIZE = 8192;
		long size = 0;

		try {
			os = new FileOutputStream(f);

			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			is = file.getInputStream();

			while ((bytesRead = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
				size += bytesRead;
				os.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			LOGGER.debug("Error(file write) : Utility.fileSave(" + filePath + ")");
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
		}
		return size;
	}
}
