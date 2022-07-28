package aero.cubox.board.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import aero.cubox.sample.service.vo.BoardVO;
import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("boardInfoDAO")
public class BoardInfoDAO extends EgovAbstractMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardInfoDAO.class);

	private static final String sqlNameSpace = "boardInfo.";

	public List<BoardVO> getNoticeList(BoardVO vo) {
		// TODO Auto-generated method stub
		 return selectList(sqlNameSpace+"getNoticeList", vo);
	}
	
	public int getNoticeListCount(BoardVO vo) {
		 return selectOne(sqlNameSpace+"getNoticeListCount", vo);
	}

	public int insertNotice(BoardVO vo) {
		// TODO Auto-generated method stub
		return insert(sqlNameSpace+"insertNotice",vo);
	}

	public BoardVO getNoticeDetail(BoardVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"getNoticeDetail", vo);
	}

	public int updateNotice(BoardVO vo) {
		// TODO Auto-generated method stub
		return update(sqlNameSpace+"updateNotice",vo);
	}
	
	public int deleteNotice(BoardVO vo) {
		return update(sqlNameSpace+"deleteNotice",vo);
	}
	

	public void updateboardInqireCo(BoardVO vo) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"updateboardInqireCo",vo);
	}

	public int chkNttId(BoardVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"chkNttId",vo);
	}

	public void insertBbsCmnt(BoardVO vo) {
		// TODO Auto-generated method stub
		insert(sqlNameSpace+"insertBbsCmnt",vo);
	}

	public List<BoardVO> getBbsCmntList(BoardVO vo) {
		// TODO Auto-generated method stub
		return selectList(sqlNameSpace+"getBbsCmntList",vo);
	}

	public void deleteBbsCmnt(BoardVO vo) {
		// TODO Auto-generated method stub
		delete(sqlNameSpace+"deleteBbsCmnt",vo);
	}



	public BoardVO cmntDetailAjax(BoardVO vo) {
		// TODO Auto-generated method stub
		return selectOne(sqlNameSpace+"cmntDetailAjax",vo);
	}

	public void cmntUpdateAjax(BoardVO vo) {
		// TODO Auto-generated method stub
		update(sqlNameSpace+"cmntUpdateAjax",vo);
	}

	/**
	 * 게시판 마스터 추가 or 수정
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int mergeBbsMaster(BoardVO vo) throws Exception {
		return insert(sqlNameSpace + "mergeBbsMaster", vo);
	}

	/**
	 * 게시판 마스터 조회
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public BoardVO getBbsMaster(BoardVO vo) throws Exception {
		return selectOne(sqlNameSpace + "getBbsMaster", vo);
	}

	/**
	 * 게시판 마스터 삭제
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int deleteBbsMaster(BoardVO vo) throws Exception {
		return delete(sqlNameSpace + "deleteBbsMaster", vo);
	}
}
