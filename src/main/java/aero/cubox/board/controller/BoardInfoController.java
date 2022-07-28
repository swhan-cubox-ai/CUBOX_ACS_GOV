package aero.cubox.board.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import aero.cubox.util.FileUtil;
import aero.cubox.util.StringUtil;
import aero.cubox.file.service.FileService;
import aero.cubox.file.service.vo.FileVO;
import aero.cubox.board.service.BoardInfoService;
import aero.cubox.cmmn.service.CommonService;
import aero.cubox.sample.service.vo.BoardVO;
import aero.cubox.sample.service.vo.CodeVO;
import aero.cubox.sample.service.vo.LoginVO;
import aero.cubox.sample.service.vo.PaginationVO;

@Controller
public class BoardInfoController {
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardInfoController.class);
	
	/** memberService */
	@Resource(name = "boardInfoService")
	private BoardInfoService boardInfoService;
	
	@Resource(name = "commonService")
	private CommonService commonService;	
	
	/** 파일 업로드 포맷 목록 globals.properties 지정 */
	@Value("#{property['Globals.formatList'].split(',')}") private List<String> formatList;
	
	/** 게시판 파일 저장 경로 globals.properties 지정 */
	@Value("#{property['Globals.fileStorage']}") private String RESOURCE_PATH;
	
	/** FileService */
	@Resource(name = "FileService") private FileService fileService;
	
	/**
	 * 게시글 목록
	 * @param 
	 * @return cubox/boardInfo/list
	 * @throws Exception
	 */
	@RequestMapping(value="/boardInfo/{bbsId}/list.do")
	public String list(@PathVariable("bbsId") String bbsId, ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		BoardVO vo = new BoardVO();
		vo.setBbsId(bbsId);
		
		// paging
		String srchPage       = StringUtil.nvl(commandMap.get("srchPage"), "1");
		String srchRecPerPage = StringUtil.nvl(commandMap.get("srchRecPerPage"), String.valueOf(vo.getSrchCnt()));		
	
		vo.setSrchPage(Integer.parseInt(srchPage));
		vo.setSrchCnt(Integer.parseInt(srchRecPerPage));
		vo.autoOffset();		
		
		BoardVO option = boardInfoService.getBbsMaster(vo);
		
		int totalCnt = boardInfoService.getNoticeListCount(vo);
		List<BoardVO> noticeList = boardInfoService.getNoticeList(vo);
		
		List<CodeVO> cntPerPage = commonService.getCodeList("combo","COUNT_PER_PAGE"); 		//page당 record 수		
		
		PaginationVO pageVO = new PaginationVO();
		pageVO.setCurPage(vo.getSrchPage());
		pageVO.setRecPerPage(vo.getSrchCnt());
		pageVO.setTotRecord(totalCnt);
		pageVO.setUnitPage(vo.getCurPageUnit());
		pageVO.calcPageList();		
		
		model.addAttribute("bbsId", bbsId);
		model.addAttribute("option", option);
		model.addAttribute("noticeList", noticeList); 
		model.addAttribute("cntPerPage", cntPerPage); 
		model.addAttribute("pagination", pageVO);

		return "cubox/boardInfo/list";

	}

	/**
	 * 게시글 등록 페이지
	 * @param 
	 * @return cubox/boardInfo/add
	 * @throws Exception
	 */
	@RequestMapping(value="/boardInfo/{bbsId}/add.do")
    public String add(@PathVariable("bbsId") String bbsId, ModelMap model, @RequestParam Map<String, Object> commandMap,
		HttpServletRequest request) throws Exception {
		
		 String nttId = StringUtil.isNullToString(commandMap.get("hidNttId"));

		 BoardVO vo = new BoardVO();
		 vo.setNttId(nttId);
		 vo.setBbsId(bbsId);
		 
		 BoardVO option = boardInfoService.getBbsMaster(vo);
		 BoardVO noticeDetail = boardInfoService.getNoticeDetail(vo);
		 
		 model.addAttribute("nttId", nttId);
		 model.addAttribute("bbsId", bbsId);
		 model.addAttribute("result", noticeDetail);
		 model.addAttribute("option", option);

		 return "cubox/boardInfo/add";
	}
	
	/**
	 * 게시글 등록 기능
	 * @param 
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value="/boardInfo/addAction.do")
	public @ResponseBody ModelAndView addAction(ModelAndView model, @RequestParam Map<String, Object> commandMap, MultipartHttpServletRequest request) throws Exception {
		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String registId = loginVO.getFsiteid();
		
		model.setViewName("jsonView");

		BoardVO vo = new BoardVO();
		
		String bbsId =  StringUtil.isNullToString(commandMap.get("hidBbsId"));
		String nttId = StringUtil.isNullToString(commandMap.get("hidNttId"));
		String nttSj = StringUtil.isNullToString(commandMap.get("nttSj"));
		String txtNttCn = StringUtil.isNullToString(commandMap.get("txtNttCn"));
		
		vo.setNttSj(nttSj);
		vo.setNttCn(txtNttCn);
		vo.setRegistId(registId);
		vo.setModifyId(registId);
		vo.setBbsId(bbsId);
		
		/* 게시판 체크 */
		String atchFileId = "";
		final int FILE_SIZE = 1;
		List<MultipartFile> files = request.getFiles("txtFiles");
		
		if(nttId == null || "".equals(nttId)){//신규 작성
			int ntt = boardInfoService.chkNttId(vo);
			if (ntt == 0){
				vo.setNttId("1");
			}
			if(files != null && !files.isEmpty() && files.size() > FILE_SIZE) {//업로드된 파일이 있으면
				atchFileId = fileService.getFileID();//파일 ID 생성
				vo.setAtchFileId(atchFileId);
			}
			int saveCnt = boardInfoService.insertNotice(vo);
		}else{//수정
			vo.setNttId(nttId);
			BoardVO result = boardInfoService.getNoticeDetail(vo);
			if(result != null){
				if( result.getAtchFileId() == null || "".equals(result.getAtchFileId()) || "null".equals(String.valueOf(result.getAtchFileId())) ) {//기존 파일 ID가 존재하면 그대로 사용
					atchFileId = fileService.getFileID();
				} else {//존재하지 않으면 생성
					atchFileId = result.getAtchFileId();
				}
				
				vo.setAtchFileId(atchFileId);
				
				int updateCnt = boardInfoService.updateNotice(vo); 
			}
		}
		
		/* 파일 업로드 체크 */
		BoardVO noticeDetail = boardInfoService.getBbsMaster(vo);
		int posblAtchFileNumber = Integer.parseInt( noticeDetail.getPosblAtchFileNumber() );
		
		if(files != null && !files.isEmpty() && files.size() > FILE_SIZE) {//업로드된 파일이 있으면
			vo.setFileCount(files.size() - FILE_SIZE);
			
			boolean check = fileService.isFileUpload(vo);//파일을 더 추가할수있는지 판별
			if( files.size() > FILE_SIZE && !check ) {
				model.addObject("result", "-1")
					.addObject("message", String.format("파일을 더 이상 추가할수 없습니다.\n파일은 %d개까지 추가 가능합니다.", posblAtchFileNumber));
				
				return model;
			} else {
				if(posblAtchFileNumber > 0) {
					//파일선별 및 업로드
					//Map<String, Object> titleMap = new HashMap<String, Object>();
					//titleMap.put("esntl_id", esntl_id);
					//String fileTitle = fileService.getUserTitle(titleMap);
					FileVO fMap = FileUtil.parseFileInfo(
							files
							, atchFileId
							, formatList
							, RESOURCE_PATH
							//, registId//, fileTitle
							);
					int fileCount = fMap.getFileCount();

					//업로드 대상 유무
					if(fileCount > 0){
						int result = fileService.insertFile(fMap);
						if(result == fileCount) {
							model.addObject("result", "1")
								.addObject("message", "");
							
							return model;
						}else {
							model.addObject("result", "-1")
								.addObject("message", "파일 추가 실패");
							
							return model;
						}
					}
				}
			}
		}
		
		model.addObject("result", "1").addObject("message", "");
		
		return model;//return "redirect:/boardInfo/"+bbsId+"/list.do";
	}
	
	/**
	 * 게시글 상세 조회
	 * @param 
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value="/boardInfo/{bbsId}/detail.do")
	public String detail( @PathVariable("bbsId") String bbsId, ModelMap model, @RequestParam Map<String, Object> commandMap,
		HttpServletRequest request) throws Exception {

		LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		String fsiteId = loginVO.getFsiteid();
		
		String nttId = StringUtil.isNullToString(commandMap.get("hidNttId"));

		BoardVO vo = new BoardVO();
		vo.setNttId(nttId);
		vo.setBbsId(bbsId);
		
		BoardVO option = boardInfoService.getBbsMaster(vo);
		BoardVO noticeList = boardInfoService.getNoticeDetail(vo);
		List<BoardVO> cmntList = boardInfoService.getBbsCmntList(vo);
		
		boardInfoService.updateboardInqireCo(vo);
		
		model.addAttribute("bbsId", bbsId);
		model.addAttribute("nttId", nttId);
		model.addAttribute("fsiteId", fsiteId);
		model.addAttribute("result", noticeList); 
		model.addAttribute("cmntList", cmntList);
		model.addAttribute("option", option);

		return "cubox/boardInfo/detail";
	}
	  
	  @RequestMapping(value="/boardInfo/list.do")
	    public String list(ModelMap model, @RequestParam Map<String, Object> commandMap,
			HttpServletRequest request) throws Exception {

	        return "redirect:/boardInfo/list.do";
	   }
	  
	  @RequestMapping("/boardInfo/delAction.do")
		public ModelAndView delAction( HttpServletRequest request) throws Exception {
	       
	        LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
	        String modifyId = loginVO.getFsiteid();
	        
	        String bbsId =  StringUtil.isNullToString(request.getParameter("hidbbsId"));
			String nttId = StringUtil.isNullToString(request.getParameter("hidNttId"));
			
			ModelAndView modelAndView = new ModelAndView();
	    	modelAndView.setViewName("jsonView");
	    	
	    	try {
	    		BoardVO vo = new BoardVO();
				
				vo.setBbsId(bbsId);
				vo.setNttId(nttId);
				vo.setModifyId(modifyId);
				
				int a = boardInfoService.deleteNotice(vo);
				
				if(a > 0) {
					modelAndView.addObject( "result", "Y" );
				} else {
					modelAndView.addObject( "result", "N" );
				}
	    	} catch(Exception e) {
	    		modelAndView.addObject( "result", "N" );
	    	}
			
			return modelAndView;
		}	  
	  
	  
	  /**
		 * 댓글 조회
		 * @param 
		 * @return modelAndView
		 * @throws Exception
		 */
	  @RequestMapping(value = "/boardInfo/cmntListAjax.do")
		public ModelAndView cmntListAjax(HttpServletRequest request) throws Exception  {
	        
	    	ModelAndView modelAndView = new ModelAndView();
	    	modelAndView.setViewName("jsonView");

	    	String bbsId =  StringUtil.isNullToString(request.getParameter("hidbbsId"));
			String nttId = StringUtil.isNullToString(request.getParameter("hidNttId"));
			
	    	BoardVO vo = new BoardVO();
	    	vo.setBbsId(bbsId);
	    	vo.setNttId(nttId);
	    		
        	List<BoardVO> list = boardInfoService.getBbsCmntList(vo);
        	
        	modelAndView.addObject("list", list);
		        	
			return modelAndView;
		}
	  
	  /**
		 * 댓글 추가
		 * @param 
		 * @return modelAndView
		 * @throws Exception
		 */
	  @RequestMapping("/boardInfo/cmntAddAjax.do")
		public ModelAndView cmntAddAjax( HttpServletRequest request) throws Exception {
	       
	        LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
	        String fsiteId = loginVO.getFsiteid();
	        
	        String bbsId =  StringUtil.isNullToString(request.getParameter("hidbbsId"));
			String nttId = StringUtil.isNullToString(request.getParameter("hidNttId"));
			String txtCmntCn = StringUtil.isNullToString(request.getParameter("txtCmntCn"));
			
			BoardVO vo = new BoardVO();
			
			vo.setBbsId(bbsId);
			vo.setCommentCn(txtCmntCn);
			vo.setNttId(nttId);
			vo.setRegistId(fsiteId);
			vo.setWriterId(fsiteId);
			
			boardInfoService.insertBbsCmnt(vo);
			
	    	
			ModelAndView modelAndView = new ModelAndView();

	    	modelAndView.setViewName( "jsonView" );
	    	
			modelAndView.addObject( "result", "Y" );
			
			return modelAndView;
		}
	  
	  
	  /**
		 * 댓글 삭제
		 * @param 
		 * @return modelAndView
		 * @throws Exception
		 */
	  @RequestMapping("/boardInfo/cmntDisableAjax.do")
		public ModelAndView cmntDisableAjax( HttpServletRequest request) throws Exception {
	       
	        LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
	        String registId = loginVO.getFsiteid();
	        
	        String bbsId =  StringUtil.isNullToString(request.getParameter("hidbbsId"));
			String nttId = StringUtil.isNullToString(request.getParameter("hidNttId"));
			String txtCmntCn = StringUtil.isNullToString(request.getParameter("txtCmntCn"));
			String hidcmntNo = StringUtil.isNullToString(request.getParameter("hidcmntNo"));
			
			
			BoardVO vo = new BoardVO();
			
			vo.setBbsId(bbsId);
			vo.setCommentCn(txtCmntCn);
			vo.setNttId(nttId);
			vo.setRegistId(registId);
			vo.setCommentNo(hidcmntNo);
			
			boardInfoService.deleteBbsCmnt(vo);
			
	    	
			ModelAndView modelAndView = new ModelAndView();

	    	modelAndView.setViewName( "jsonView" );
	    	
			modelAndView.addObject( "result", "Y" );
			
			return modelAndView;
		}
	  
	  
	  /**
		 * 댓글 상세 조회
		 * @param 
		 * @return modelAndView
		 * @throws Exception
		 */
	  @RequestMapping("/boardInfo/cmntDetailAjax.do")
		public ModelAndView cmntDetailAjax( HttpServletRequest request) throws Exception {
	       
	        LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
	        String registId = loginVO.getFsiteid();
	        
	        String bbsId =  StringUtil.isNullToString(request.getParameter("hidbbsId"));
			String nttId = StringUtil.isNullToString(request.getParameter("hidNttId"));
			String txtCmntCn = StringUtil.isNullToString(request.getParameter("txtCmntCn"));
			String hidcmntNo = StringUtil.isNullToString(request.getParameter("hidcmntNo"));
			
			
			BoardVO vo = new BoardVO();
			
			vo.setBbsId(bbsId);
			vo.setCommentCn(txtCmntCn);
			vo.setNttId(nttId);
			vo.setRegistId(registId);
			vo.setCommentNo(hidcmntNo);
			
//			boardInfoService.deleteBbsCmnt(vo);
			
			BoardVO result = boardInfoService.cmntDetailAjax(vo);
			
	    	
			ModelAndView modelAndView = new ModelAndView();

	    	modelAndView.setViewName( "jsonView" );
	    	
			modelAndView.addObject( "result", "Y" );
			modelAndView.addObject( "detail", result);
			
			return modelAndView;
		}
	  
	  
	  /**
		 * 댓글 수정
		 * @param 
		 * @return modelAndView
		 * @throws Exception
		 */
	  @RequestMapping("/boardInfo/cmntUpdateAjax.do")
	  public ModelAndView cmntUpdateAjax( HttpServletRequest request) throws Exception {
		  
		  LoginVO loginVO = (LoginVO)request.getSession().getAttribute("loginVO");
		  String registId = loginVO.getFsiteid();
		  
		  String bbsId =  StringUtil.isNullToString(request.getParameter("hidbbsId"));
		  String nttId = StringUtil.isNullToString(request.getParameter("hidNttId"));
		  String txtCmntCn = StringUtil.isNullToString(request.getParameter("txtUpdateCmntCn"));
		  String hidcmntNo = StringUtil.isNullToString(request.getParameter("hidcmntNo"));
		  
		  
		  BoardVO vo = new BoardVO();
		  
		  vo.setBbsId(bbsId);
		  vo.setCommentCn(txtCmntCn);
		  vo.setNttId(nttId);
		  vo.setRegistId(registId);
		  vo.setCommentNo(hidcmntNo);
		  
		  
		  boardInfoService.cmntUpdateAjax(vo);
		  
		  BoardVO result = boardInfoService.cmntDetailAjax(vo);
		  
		  
		  ModelAndView modelAndView = new ModelAndView();
		  
		  modelAndView.setViewName( "jsonView" );
		  
		  modelAndView.addObject( "result", "Y" );
		  modelAndView.addObject( "detail", result);
		  
		  return modelAndView;
	  }
	  
	
	

}
