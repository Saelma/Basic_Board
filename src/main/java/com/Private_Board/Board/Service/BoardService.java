package com.Private_Board.Board.Service;

import com.Private_Board.Board.entity.Board;
import com.Private_Board.Board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService{
    @Autowired
    private BoardRepository boardRepository;

    public void write(Board board, MultipartFile file) throws Exception{
        String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files"; // 저장경로 지정

        UUID uuid = UUID.randomUUID(); // 파일 이름에 붙일 랜덤 이름 지정

        String filename = uuid + "_" + file.getOriginalFilename();
        // 랜덤 이름을 파일네임앞에 _ 와 같이 붙이고 원래 이름을 더해 파일 생성

        File saveFile = new File(projectPath, filename);
        // 파일을 생성하며, projectPath에 저장하고, filename 이름으로 저장됨

        file.transferTo(saveFile); // 파일을 보내는 것이며, write 함수를 사용 시 예외처리가 필요해 throws 사용

        board.setFilename(filename);
        board.setFilepath("files/" + filename);

        boardRepository.save(board);
    }

    public List<Board> boardList(){
        return boardRepository.findAll();
    }

    public Board boardview(Integer id){
        return boardRepository.findById(id).get();
    }

    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }
}
