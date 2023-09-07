package com.travel.service;

import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.travel.Repository.AskImgRepository;
import com.travel.entity.AskBoardImg;
import com.travel.entity.InfoBoardImg;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AskImgService {

	//private String askImgLocation =  "C:/travelShop/ask";
	private final FileService fileService;
	private final AskImgRepository askImgRepository;
	
	@Value("${askImgLocation}")
	private String askImgLocation;
	
	// 이미지 저장
	public void saveAskImg(AskBoardImg askBoardImg, MultipartFile askImgFile)throws Exception{
		String oriImgName = askImgFile.getOriginalFilename(); //파일 이름 > 이미지1.pg
		String imgName = "";
		String imgUrl = "";
	
	//1. 파일을 askImgLocation에 저장
	if(!StringUtils.isEmpty(oriImgName)) {
		//oriImgName이 빈문자열이 아니라면 이미지 파일 업로드.
		imgName = fileService.uploadFile(askImgLocation, oriImgName,askImgFile.getBytes());
		imgUrl = "/img/ask/" + imgName; 
	}
	
	//2. ask_Board_img테이블에 저장
	askBoardImg.updateAskImg(oriImgName, imgName, imgUrl);
	askImgRepository.save(askBoardImg); //db에 insert.
	}
	
	
	
	//이미지 업데이트 메소드
			public void updateAskImg(Long askBoardImgId, MultipartFile askImgFile) throws Exception{
				if(!askImgFile.isEmpty()) { //첨부한 이미지 파일이 있으면
					
			//해당 이미지를 가져오고
			AskBoardImg savedAskImg = askImgRepository.findById(askBoardImgId)
					                                       .orElseThrow(EntityNotFoundException::new);
					
					
			//기존 이미지 파일 폴더에서 삭제
			if(!StringUtils.isEmpty(savedAskImg.getImgName())) {
			fileService.deleteFile(askImgLocation + "/" + savedAskImg.getImgName());
			}
				
			
			//수정된 이미지 파일에 업로드
			String oriImgName = askImgFile.getOriginalFilename();
			String imgName = fileService.uploadFile(askImgLocation, oriImgName
					, askImgFile.getBytes());
			String imgUrl = "/img/ask/" + imgName;
			
			//update 쿼리문 실행. 
			/*한번 insert를 진행하면 엔티티가 영속성 컨텍스트에 저장이 되므로
			  그 이후에는 변경감지 기능이 동작하기 때문에 개발자는 update 쿼리문을 쓰지 않고
			  엔티티만 변경해주면 된다. */
			savedAskImg.updateAskImg(oriImgName, imgName, imgUrl);
					
				}
			}

}
