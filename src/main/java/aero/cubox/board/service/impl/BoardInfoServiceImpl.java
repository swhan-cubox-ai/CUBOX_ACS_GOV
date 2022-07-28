package aero.cubox.board.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import aero.cubox.board.service.BoardInfoService;
import aero.cubox.sample.service.vo.BoardVO;

@Service("boardInfoService")
public class BoardInfoServiceImpl extends EgovAbstractServiceImpl implements BoardInfoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardInfoServiceImpl.class);

	@Resource(name = "boardInfoDAO")
	private BoardInfoDAO boardInfoDAO;

	/**
	 * 게시판 게시글 가져오기
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	@Override
	public List<BoardVO> getNoticeList(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.getNoticeList(vo);
	}
	
	public int getNoticeListCount(BoardVO vo) throws Exception {
		return boardInfoDAO.getNoticeListCount(vo);
	}

	/**
	 * 게시글 등록
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	@Override
	public int insertNotice(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.insertNotice(vo);
	}

	/**
	 * 게시글 상세조회 
	 * @param BoardVO
	 * @return BoardVO
	 * @throws Exception
	 */
	@Override
	public BoardVO getNoticeDetail(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.getNoticeDetail(vo);
	}

	/**
	 * 게시글 수정 
	 * @param BoardVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int updateNotice(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.updateNotice(vo);
	}
	
	@Override
	public int deleteNotice(BoardVO vo) throws Exception {
		return boardInfoDAO.deleteNotice(vo);
	}

	/**
	 * 
	 * @param BoardVO
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void updateboardInqireCo(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		 boardInfoDAO.updateboardInqireCo(vo);
	}

	/**
	 * 
	 * @param BoardVO
	 * @return int
	 * @throws Exception
	 */
	@Override
	public int chkNttId(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.chkNttId(vo);
	}

	/**
	 * 댓글 등록
	 * @param BoardVO
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void insertBbsCmnt(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		boardInfoDAO.insertBbsCmnt(vo);
	}

	/**
	 * 댓글 조회
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	@Override
	public List<BoardVO> getBbsCmntList(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.getBbsCmntList(vo);
	}

	/**
	 * 댓글 삭제
	 * @param BoardVO
	 * @return List<BoardVO>
	 * @throws Exception
	 */
	@Override
	public void deleteBbsCmnt(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		boardInfoDAO.deleteBbsCmnt(vo);
	}


	/**
	 * 댓글 상세조회
	 * @param BoardVO
	 * @return BoardVO
	 * @throws Exception
	 */
	@Override
	public BoardVO cmntDetailAjax(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		return boardInfoDAO.cmntDetailAjax(vo);
	}

	/**
	 * 댓글 수정
	 * @param BoardVO
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void cmntUpdateAjax(BoardVO vo) throws Exception {
		// TODO Auto-generated method stub
		boardInfoDAO.cmntUpdateAjax(vo);
	}

	/**
	 * 게시판 마스터 추가 or 수정
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int mergeBbsMaster(BoardVO vo) throws Exception {
		return boardInfoDAO.mergeBbsMaster(vo);
	}

	/**
	 * 게시판 마스터 조회
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@Override
	public BoardVO getBbsMaster(BoardVO vo) throws Exception {
		return boardInfoDAO.getBbsMaster(vo);
	}

	/**
	 * 게시판 마스터 삭제
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int deleteBbsMaster(BoardVO vo) throws Exception {
		return boardInfoDAO.deleteBbsMaster(vo);
	}
}
