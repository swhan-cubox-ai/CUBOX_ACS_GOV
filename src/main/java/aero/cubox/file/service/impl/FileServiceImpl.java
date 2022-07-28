package aero.cubox.file.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import aero.cubox.file.service.FileService;
import aero.cubox.file.service.vo.FileVO;

@Service("FileService")
public class FileServiceImpl implements FileService {
	@Resource(name = "fileDAO")
	private FileDAO fileDAO;

	@Override
	public List<FileVO> getFileList(FileVO vo) throws Exception {
		return fileDAO.selectFileList(vo);
	}

	@Override
	public FileVO getFileDetail(FileVO vo) throws Exception {
		return fileDAO.selectFileDetail(vo);
	}

	@Override
	public FileVO getFileManage(FileVO vo) throws Exception {
		return fileDAO.selectFileManage(vo);
	}

	@Override
	public String getFileID() throws Exception {
		return fileDAO.selectFileID();
	}

	@Override
	public String getFileSn(FileVO vo) throws Exception {
		return fileDAO.selectFileSn(vo);
	}

	@Override
	public int insertFile(FileVO vo) throws Exception {
		int cnt = 0;
		
		FileVO result = fileDAO.selectFileManage(vo);
		if(result == null){
			fileDAO.insertFileManage(vo);
		}
		
		List<FileVO> fileList = vo.getFileList();
		for(FileVO sMap : fileList) {
			String fileSn = fileDAO.selectFileSn(vo);
			sMap.setFileSn(Integer.parseInt(fileSn));

			cnt += fileDAO.insertFileDetail(sMap);
		}
		
		return cnt;
	}

	@Override
	public void updateFileDetail(FileVO vo) throws Exception {
		fileDAO.updateFileDetail(vo);
	}
	
	@Override
	public int deleteFile(FileVO vo) throws Exception {
		return fileDAO.deleteFile(vo);
	}

	@Override
	public int deleteFileDetail(FileVO vo) throws Exception {
		return fileDAO.deleteFileDetail(vo);
	}

	@Override
	public boolean isFileUpload(FileVO vo) throws Exception {
		return fileDAO.isFileUpload(vo);
	}

	@Override
	public String getAtchFileId(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return fileDAO.getAtchFileId(map);
	}

	@Override
	public FileVO getImageFileDetail(FileVO vo) throws Exception {
		// TODO Auto-generated method stub
		return fileDAO.getImageFileDetail(vo);
	}
	
	/*@Override
	public String getUserTitle(Map<String, Object> map) throws Exception {
		return fileDAO.selectUserTitle(map);
	}

	@Override
	public Map<String, Object> getBioFileDetail(Map<String, Object> map) throws Exception {
		return fileDAO.selectBioFileDetail(map);
	}*/
	
	@Override
	public int insertFloorFile(FileVO vo) throws Exception {
		int cnt = 0;

		FileVO result = fileDAO.selectFloorFileManage(vo);
		if (result == null) {
			fileDAO.insertFileManage(vo);

		}

		List<FileVO> fileList = vo.getFileList();
		for (FileVO sMap : fileList) {
			String fileSn = fileDAO.selectFileSn(vo);
			sMap.setFileSn(Integer.parseInt(fileSn));

			cnt += fileDAO.insertFileDetail(sMap);

		}

		return cnt;
	}

	@Override
	public List<FileVO> getFloorFileList(FileVO vo) throws Exception {
		// TODO Auto-generated method stub
		return fileDAO.getFloorFileList(vo);
	}
}
