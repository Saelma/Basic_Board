package com.Private_Board.Board.Controller;

import com.Private_Board.Board.Service.BoardService;
import com.Private_Board.Board.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@RestController
public class FileController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/{id}download")
    public void download(HttpServletResponse response, Board board, Integer id) throws Exception {
        Board boardPath = boardService.boardview(id);
        try{
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files/TestFile.jpg"; // 저장경로 지정

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
}
