package aero.cubox.file.service.vo;

import java.util.Date;
import java.util.List;

public class FileVO {
	/* file_tb table */
	private String atchFileId;//첨부파일ID
	private Date creatDt;//생성일시
	private char useAt;//사용여부
	
	/* file_detail_tb table */
	private int fileSn;//파일순번
	private String fileStreCours;//파일저장경로
	private String streFileNm;//저장파일명
	private String orignlFileNm;//원파일명
	private String fileExtsn;//파일확장자
	private String fileCn;//파일내용
	private long fileSize;//파일크기
	
	private List<FileVO> fileList;
	private int fileCount;
	private String registId;//등록 ID
	private boolean isFileUpload;
	
	public String getAtchFileId() {
		return atchFileId;
	}
	public void setAtchFileId(String atchFileId) {
		this.atchFileId = atchFileId;
	}
	public Date getCreatDt() {
		return creatDt;
	}
	public void setCreatDt(Date creatDt) {
		this.creatDt = creatDt;
	}
	public char getUseAt() {
		return useAt;
	}
	public void setUseAt(char useAt) {
		this.useAt = useAt;
	}
	public int getFileSn() {
		return fileSn;
	}
	public void setFileSn(int fileSn) {
		this.fileSn = fileSn;
	}
	public String getFileStreCours() {
		return fileStreCours;
	}
	public void setFileStreCours(String fileStreCours) {
		this.fileStreCours = fileStreCours;
	}
	public String getStreFileNm() {
		return streFileNm;
	}
	public void setStreFileNm(String streFileNm) {
		this.streFileNm = streFileNm;
	}
	public String getOrignlFileNm() {
		return orignlFileNm;
	}
	public void setOrignlFileNm(String orignlFileNm) {
		this.orignlFileNm = orignlFileNm;
	}
	public String getFileExtsn() {
		return fileExtsn;
	}
	public void setFileExtsn(String fileExtsn) {
		this.fileExtsn = fileExtsn;
	}
	public String getFileCn() {
		return fileCn;
	}
	public void setFileCn(String fileCn) {
		this.fileCn = fileCn;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public List<FileVO> getFileList() {
		return fileList;
	}
	public void setFileList(List<FileVO> fileList) {
		this.fileList = fileList;
	}
	public int getFileCount() {
		return fileCount;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	
	public String getRegistId() {
		return registId;
	}
	public void setRegistId(String registId) {
		this.registId = registId;
	}
	
	public boolean isFileUpload() {
		return isFileUpload;
	}
	public void setFileUpload(boolean isFileUpload) {
		this.isFileUpload = isFileUpload;
	}
	@Override
	public String toString() {
		return "FileVO [atchFileId=" + atchFileId + ", creatDt=" + creatDt + ", useAt=" + useAt + ", fileSn=" + fileSn
				+ ", fileStreCours=" + fileStreCours + ", streFileNm=" + streFileNm + ", orignlFileNm=" + orignlFileNm
				+ ", fileExtsn=" + fileExtsn + ", fileCn=" + fileCn + ", fileSize=" + fileSize + ", fileList="
				+ fileList + ", fileCount=" + fileCount + ", registId=" + registId + ", isFileUpload=" + isFileUpload
				+ "]";
	}
}
