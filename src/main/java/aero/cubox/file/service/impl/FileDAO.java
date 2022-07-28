package aero.cubox.file.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import aero.cubox.file.service.vo.FileVO;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository
public class FileDAO extends EgovAbstractMapper{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileDAO.class);
	private static final String sqlNameSpace = "cmmn.file.";
	
	public List<FileVO> selectFileList(FileVO vo) throws Exception {
		return selectList(sqlNameSpace + "selectFileList", vo);
	}

	public FileVO selectFileDetail(FileVO vo) throws Exception {
		return selectOne(sqlNameSpace + "selectFileDetail", vo);
	}

	public FileVO selectFileManage(FileVO vo) throws Exception {
		return selectOne(sqlNameSpace + "selectFileManage", vo);
	}

	public String selectFileID() throws Exception {
		return selectOne(sqlNameSpace + "selectFileID");
	}
	
	public String selectFileSn(FileVO vo) throws Exception {
		return selectOne(sqlNameSpace + "selectFileSn", vo);
	}

	public void insertFileManage(FileVO vo) throws Exception {
		insert(sqlNameSpace + "insertFileManage", vo);
	}

	public int insertFileDetail(FileVO vo) throws Exception {
		return insert(sqlNameSpace + "insertFileDetail", vo);
	}

	public void updateFileDetail(FileVO vo) throws Exception {
		update(sqlNameSpace + "updateFileDetail", vo);
	}
	
	public int deleteFile(FileVO vo) throws Exception { 
		return delete(sqlNameSpace + "deleteFile", vo);
	}
	
	public int deleteFileDetail(FileVO vo) {
		return delete(sqlNameSpace + "deleteFileDetail", vo);
	}
	
	public boolean isFileUpload(FileVO vo) {
		return selectOne(sqlNameSpace + "isFileUpload", vo);
	}

	public String getAtchFileId(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace + "getAtchFileId", map);
	}

	public FileVO getImageFileDetail(FileVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace + "getImageFileDetail", vo);
	}

	public FileVO selectFloorFileManage(FileVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"selectFloorFileManage",vo);
	}

	public void updateFileManage(FileVO vo) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"updateFileManage",vo);
		
	}
	
	public int updateFloorFileDetail(FileVO vo) throws Exception {
		return update(sqlNameSpace + "updateFloorFileDetail", vo);
	}

	public List<FileVO> getFloorFileList(FileVO vo) {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace + "getFloorFileList", vo);
	}

	

	/*public String selectUserTitle(FileVO vo) throws Exception {
		return selectOne(sqlNameSpace + "selectUserTitle", vo);
	}

	public FileVO selectBioFileDetail(FileVO vo) throws Exception {
		return selectOne(sqlNameSpace + "selectBioFileDetail", vo);
	}*/
}
