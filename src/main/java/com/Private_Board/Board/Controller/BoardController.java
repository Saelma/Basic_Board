package com.Private_Board.Board.Controller;

import com.Private_Board.Board.Service.BoardService;
import com.Private_Board.Board.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write")
    public String boardWriteForm(){
        return "boardwrite";
    }

    @PostMapping("/board/writedo")
    public String boardWritePro(Model model, Board board, MultipartFile file) throws Exception{
        boardService.write(board, file);

        model.addAttribute("message","글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");
        return "message";
    }

    @GetMapping("/board/list")
    public String boardlist(Model model,
                            @PageableDefault(page = 0, size = 10, sort="id", direction = Sort.Direction.DESC)
                            Pageable pageable, String searchKeyword) {
        // 데이터를 담아 페이지로 보내기 위해 Model 자료형을 인자로 함
        // 검색할 때 (searchKeyword) 가 있는지 구분하며 사용

        Page<Board> list = null;

        if(searchKeyword != null){
            list = boardService.boardSearchList(searchKeyword, pageable);
        } else {
            list = boardService.boardList(pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1; // 현재 페이지를 가져옴, 0에서 시작하는 처리를 위해 + 1
        int startPage = Math.max(nowPage -4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages()); // totalPage보다 크면 안 되기에 두개중 최소값 반환


        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "boardlist";
    }

    @GetMapping("/board/view")
    public String boardview(Model model, Integer id){
        model.addAttribute("board", boardService.boardview(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Model model, Integer id){
        boardService.boardDelete(id);
        model.addAttribute("message","글 삭제가 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message"; // 삭제 처리후 list 로 이동
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("board", boardService.boardview(id));
        return "boardModify";
    }

    @GetMapping("/board/download/{id}")
    public void download(HttpServletResponse response, Board board, @PathVariable("id") Integer id) throws Exception {
        Board boardTemp = boardService.boardview(id);
        String boardpath = boardTemp.getFilepath();
        try{
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/" + boardpath; // 저장경로 지정

            File file = new File(projectPath);
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName()); // 파일 다운로드

            FileInputStream fileInputStream = new FileInputStream(projectPath);
            OutputStream out =response.getOutputStream();

            int read=0;
            byte[] buffer = new byte[1024];
            while((read = fileInputStream.read(buffer)) != -1 ){
                out.write(buffer, 0, read);
            }
        } catch (Exception e){
            throw new Exception("download error");
        }
    }


    @PostMapping("board/update/{id}")
    public String boardupdate(
            @PathVariable("id") Integer id, Board board, Model model,
    MultipartFile file)
    throws Exception{

        Board boardTemp = boardService.boardview(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        if(!file.isEmpty()) {
            boardTemp.setFilename(board.getFilename());
            boardTemp.setFilepath(board.getFilepath());
        }

        boardService.write(boardTemp, file);

        model.addAttribute("message","글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message";
    }


}
