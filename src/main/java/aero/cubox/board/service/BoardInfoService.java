package aero.cubox.board.service;

import java.util.List;

import aero.cubox.sample.service.vo.BoardVO;

public interface BoardInfoService {

	/**
	 * 게시판 게시글 가져오기
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	List<BoardVO> getNoticeList(BoardVO vo) throws Exception;
	
	int getNoticeListCount(BoardVO vo) throws Exception;

	/**
	 * 게시글 등록
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	int insertNotice(BoardVO vo) throws Exception;

	/**
	 * 게시글 상세조회 
	 * @param BoardVO
	 * @return BoardVO
	 * @throws Exception
	 */
	BoardVO getNoticeDetail(BoardVO vo) throws Exception;

	/**
	 * 게시글 수정 
	 * @param BoardVO
	 * @return int
	 * @throws Exception
	 */
	int updateNotice(BoardVO vo) throws Exception;
	
	int deleteNotice(BoardVO vo) throws Exception;

	/**
	 * 
	 * @param BoardVO
	 * @return void
	 * @throws Exception
	 */
	void updateboardInqireCo(BoardVO vo) throws Exception;

	/**
	 * 
	 * @param BoardVO
	 * @return int
	 * @throws Exception
	 */
	int chkNttId(BoardVO vo) throws Exception;

	/**
	 * 댓글 등록
	 * @param BoardVO
	 * @return void
	 * @throws Exception
	 */
	void insertBbsCmnt(BoardVO vo) throws Exception;

	/**
	 * 댓글 조회
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	List<BoardVO> getBbsCmntList(BoardVO vo) throws Exception;

	/**
	 * 댓글 삭제
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	void deleteBbsCmnt(BoardVO vo) throws Exception;

	/**
	 * 댓글 상세조회
	 * @param BoardVO
	 * @return BoardVO
	 * @throws Exception
	 */
	BoardVO cmntDetailAjax(BoardVO vo) throws Exception;

	/**
	 * 댓글 수정
	 * @param BoardVO
	 * @return void
	 * @throws Exception
	 */
	void cmntUpdateAjax(BoardVO vo) throws Exception;

	/**
	 * 게시판 마스터 추가 or 수정
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	int mergeBbsMaster(BoardVO vo) throws Exception;

	/**
	 * 게시판 마스터 조회
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	BoardVO getBbsMaster(BoardVO vo) throws Exception;
	
	/**
	 * 게시판 마스터 삭제
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	int deleteBbsMaster(BoardVO vo) throws Exception;
}
