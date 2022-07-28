package aero.cubox.sample.service.vo;

import aero.cubox.file.service.vo.FileVO;

public class BoardVO extends FileVO{
	private String nttId;
	private String bbsId;//게시판ID
	private String nttNo;
	private String nttSj;
	private String ntcrId;
	private String ntcrNm;
	private String nttCn;
	private String sortOrdr;
	private String passWord;
	private String inqireCo;
	private char useAt;//사용 여부
	private String ntceBgnde;
	private String ntceEndde;
	private String replyAt;
	private String atchFileId;
	private int atchFileCnt; //2021-04-23 첨부파일개수
	private String parnts;
	private String replyLc;
	private String registId;//등록 ID
	private String registNm;//등록자 이름	
	private String modifyId;//수정 ID
	private String registDt;//등록일시
	private String modifyDt;//수정 일시
	
	private String commentNo;
	private String writerId;
	private String writerNm;
	private String password;
	private String commentCn;
	
	/* bbs_master_tb table */
	private String bbsNm;//게시판 명
	private String bbsIntrcn;//게시판 소개
	private String bbsTyCode;//게시판 유형 코드
	private String bbsAttrbCode;//게시판 속성 코드
	private String replyPosblAt;//답장 가능 여부
	private String fileAtchPosblAt;//파일 첨부 가능 여부
	private String posblAtchFileNumber;//첨부가능 파일 숫자
	private String posblAtchFileSize;//첨부가능 파일 사이즈
	
	private int srchPage			= 1;	//조회할 페이지 번호 기본 1페이지
	private int srchCnt				= 10;	//조회할 페이지 수
	private int offset				= 0;
	private int curPage				= 1;	//조회할 페이지 번호 기본 1페이지
	private int curPageUnit			= 10;	//한번에 표시할 페이지 번호 개수
	
	
	public int getSrchPage() {
		return srchPage;
	}
	public void setSrchPage(int srchPage) {
		this.srchPage = srchPage;
	}
	public int getSrchCnt() {
		return srchCnt;
	}
	public void setSrchCnt(int srchCnt) {
		this.srchCnt = srchCnt;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getCurPage() {
		return curPage;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	public int getCurPageUnit() {
		return curPageUnit;
	}
	public void setCurPageUnit(int curPageUnit) {
		this.curPageUnit = curPageUnit;
	}
	public void autoOffset(){
		int off = (this.srchPage - 1) * this.srchCnt;
		if(off<0) off = 0;
		this.offset = off;
	}	
	
	public String getBbsNm() {
		return bbsNm;
	}
	public void setBbsNm(String bbsNm) {
		this.bbsNm = bbsNm;
	}
	public String getBbsIntrcn() {
		return bbsIntrcn;
	}
	public void setBbsIntrcn(String bbsIntrcn) {
		this.bbsIntrcn = bbsIntrcn;
	}
	public String getBbsTyCode() {
		return bbsTyCode;
	}
	public void setBbsTyCode(String bbsTyCode) {
		this.bbsTyCode = bbsTyCode;
	}
	public String getBbsAttrbCode() {
		return bbsAttrbCode;
	}
	public void setBbsAttrbCode(String bbsAttrbCode) {
		this.bbsAttrbCode = bbsAttrbCode;
	}
	public String getReplyPosblAt() {
		return replyPosblAt;
	}
	public void setReplyPosblAt(String replyPosblAt) {
		this.replyPosblAt = replyPosblAt;
	}
	public String getFileAtchPosblAt() {
		return fileAtchPosblAt;
	}
	public void setFileAtchPosblAt(String fileAtchPosblAt) {
		this.fileAtchPosblAt = fileAtchPosblAt;
	}
	public String getPosblAtchFileNumber() {
		return posblAtchFileNumber;
	}
	public void setPosblAtchFileNumber(String posblAtchFileNumber) {
		this.posblAtchFileNumber = posblAtchFileNumber;
	}
	public String getPosblAtchFileSize() {
		return posblAtchFileSize;
	}
	public void setPosblAtchFileSize(String posblAtchFileSize) {
		this.posblAtchFileSize = posblAtchFileSize;
	}
	public String getNttId() {
		return nttId;
	}
	public void setNttId(String nttId) {
		this.nttId = nttId;
	}
	public String getBbsId() {
		return bbsId;
	}
	public void setBbsId(String bbsId) {
		this.bbsId = bbsId;
	}
	public String getNttNo() {
		return nttNo;
	}
	public void setNttNo(String nttNo) {
		this.nttNo = nttNo;
	}
	public String getNttSj() {
		return nttSj;
	}
	public void setNttSj(String nttSj) {
		this.nttSj = nttSj;
	}
	public String getNtcrId() {
		return ntcrId;
	}
	public void setNtcrId(String ntcrId) {
		this.ntcrId = ntcrId;
	}
	public String getNtcrNm() {
		return ntcrNm;
	}
	public void setNtcrNm(String ntcrNm) {
		this.ntcrNm = ntcrNm;
	}
	public String getNttCn() {
		return nttCn;
	}
	public void setNttCn(String nttCn) {
		this.nttCn = nttCn;
	}
	public String getSortOrdr() {
		return sortOrdr;
	}
	public void setSortOrdr(String sortOrdr) {
		this.sortOrdr = sortOrdr;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getInqireCo() {
		return inqireCo;
	}
	public void setInqireCo(String inqireCo) {
		this.inqireCo = inqireCo;
	}
	public char getUseAt() {
		return useAt;
	}
	public void setUseAt(char useAt) {
		this.useAt = useAt;
	}
	public String getNtceBgnde() {
		return ntceBgnde;
	}
	public void setNtceBgnde(String ntceBgnde) {
		this.ntceBgnde = ntceBgnde;
	}
	public String getNtceEndde() {
		return ntceEndde;
	}
	public void setNtceEndde(String ntceEndde) {
		this.ntceEndde = ntceEndde;
	}
	public String getReplyAt() {
		return replyAt;
	}
	public void setReplyAt(String replyAt) {
		this.replyAt = replyAt;
	}
	public String getAtchFileId() {
		return atchFileId;
	}
	public void setAtchFileId(String atchFileId) {
		this.atchFileId = atchFileId;
	}
	public String getParnts() {
		return parnts;
	}
	public void setParnts(String parnts) {
		this.parnts = parnts;
	}
	public String getReplyLc() {
		return replyLc;
	}
	public void setReplyLc(String replyLc) {
		this.replyLc = replyLc;
	}
	public String getRegistId() {
		return registId;
	}
	public void setRegistId(String registId) {
		this.registId = registId;
	}
	public String getModifyId() {
		return modifyId;
	}
	public void setModifyId(String modifyId) {
		this.modifyId = modifyId;
	}
	public String getRegistDt() {
		return registDt;
	}
	public void setRegistDt(String registDt) {
		this.registDt = registDt;
	}
	public String getModifyDt() {
		return modifyDt;
	}
	public void setModifyDt(String modifyDt) {
		this.modifyDt = modifyDt;
	}
	
	public String getCommentNo() {
		return commentNo;
	}
	public void setCommentNo(String commentNo) {
		this.commentNo = commentNo;
	}
	public String getWriterId() {
		return writerId;
	}
	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}
	public String getWriterNm() {
		return writerNm;
	}
	public void setWriterNm(String writerNm) {
		this.writerNm = writerNm;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCommentCn() {
		return commentCn;
	}
	public void setCommentCn(String commentCn) {
		this.commentCn = commentCn;
	}
	public int getAtchFileCnt() {
		return atchFileCnt;
	}
	public void setAtchFileCnt(int atchFileCnt) {
		this.atchFileCnt = atchFileCnt;
	}
	public String getRegistNm() {
		return registNm;
	}
	public void setRegistNm(String registNm) {
		this.registNm = registNm;
	}	
	
	@Override
	public String toString() {
		return "BoardVO [nttId=" + nttId + ", bbsId=" + bbsId + ", nttNo=" + nttNo + ", nttSj=" + nttSj + ", ntcrId="
				+ ntcrId + ", ntcrNm=" + ntcrNm + ", nttCn=" + nttCn + ", sortOrdr=" + sortOrdr + ", passWord="
				+ passWord + ", inqireCo=" + inqireCo + ", useAt=" + useAt + ", ntceBgnde=" + ntceBgnde + ", ntceEndde="
				+ ntceEndde + ", replyAt=" + replyAt + ", atchFileId=" + atchFileId + ", atchFileCnt=" + atchFileCnt + ", parnts=" + parnts
				+ ", replyLc=" + replyLc + ", registId=" + registId + ", modifyId=" + modifyId + ", registDt="
				+ registDt + ", modifyDt=" + modifyDt + ", commentNo=" + commentNo + ", writerId=" + writerId
				+ ", writerNm=" + writerNm + ", password=" + password + ", commentCn=" + commentCn + ", bbsNm=" + bbsNm
				+ ", bbsIntrcn=" + bbsIntrcn + ", bbsTyCode=" + bbsTyCode + ", bbsAttrbCode=" + bbsAttrbCode
				+ ", replyPosblAt=" + replyPosblAt + ", fileAtchPosblAt=" + fileAtchPosblAt + ", posblAtchFileNumber="
				+ posblAtchFileNumber + ", posblAtchFileSize=" + posblAtchFileSize + "]";
	}
}
