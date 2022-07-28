package aero.cubox.file.service;

import java.util.List;
import java.util.Map;

import aero.cubox.file.service.vo.FileVO;

public interface FileService {
	public List<FileVO> getFileList(FileVO vo) throws Exception;
	public FileVO getFileDetail(FileVO vo) throws Exception;
	public FileVO getFileManage(FileVO vo) throws Exception;
	public String getFileID() throws Exception;
	public String getFileSn(FileVO vo) throws Exception;
	public int insertFile(FileVO vo) throws Exception;
	public void updateFileDetail(FileVO vo) throws Exception;
	public int deleteFile(FileVO vo) throws Exception;
	public int deleteFileDetail(FileVO vo) throws Exception;
	public boolean isFileUpload(FileVO vo) throws Exception;
	/*public String getUserTitle(Map<String, Object> map) throws Exception;
	public Map<String, Object> getBioFileDetail(Map<String, Object> map) throws Exception;*/
	public String getAtchFileId(Map<String, Object> map) throws Exception;
	public FileVO getImageFileDetail(FileVO vo) throws Exception;
	int insertFloorFile(FileVO vo) throws Exception;
	public List<FileVO> getFloorFileList(FileVO vo) throws Exception;
}
