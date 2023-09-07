package com.travel.service;

import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.travel.Repository.TourImgRepository;
import com.travel.entity.ItemImg;
import com.travel.entity.TouristImg;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TourImgService {

	//private String tourImgLocation =  "C:/travelShop/tour";
	private final TourImgRepository tourImgRepository;
	private final FileService fileService;
	
	
	@Value("${tourImgLocation}")
	private String tourImgLocation;
	
	
	// 이미지 저장
	public void saveTourImg(TouristImg touristImg, MultipartFile tourImgFile)throws Exception{
		String oriImgName = tourImgFile.getOriginalFilename(); //파일 이름 > 이미지1.pg
		String imgName = "";
		String imgUrl = "";
	
	//1. 파일을 itemImgLocation에 저장
	if(!StringUtils.isEmpty(oriImgName)) {
		//oriImgName이 빈문자열이 아니라면 이미지 파일 업로드.
		imgName = fileService.uploadFile(tourImgLocation, oriImgName,tourImgFile.getBytes());
		imgUrl = "/img/tour/" + imgName; 
	}
	
	//2. item_img테이블에 저장
		touristImg.updateTourImg(oriImgName, imgName, imgUrl);
		tourImgRepository.save(touristImg); //db에 insert.
	}
	
	//이미지 업데이트 메소드
	public void updateTourImg(Long touristImgId, MultipartFile tourImgFile) throws Exception{
		if(!tourImgFile.isEmpty()) { //첨부한 이미지 파일이 있으면
			
	//해당 이미지를 가져오고
	TouristImg savedTourImg = tourImgRepository.findById(touristImgId)
			                                       .orElseThrow(EntityNotFoundException::new);
			
			
	//기존 이미지 파일 폴더에서 삭제
	if(!StringUtils.isEmpty(savedTourImg.getImgName())) {
	fileService.deleteFile(tourImgLocation + "/" + savedTourImg.getImgName());
	}
		
	
	//수정된 이미지 파일에 업로드
	String oriImgName = tourImgFile.getOriginalFilename();
	String imgName = fileService.uploadFile(tourImgLocation, oriImgName
			, tourImgFile.getBytes());
	String imgUrl = "/img/item/" + imgName;
	
	//update 쿼리문 실행. 
	/*한번 insert를 진행하면 엔티티가 영속성 컨텍스트에 저장이 되므로
	  그 이후에는 변경감지 기능이 동작하기 때문에 개발자는 update 쿼리문을 쓰지 않고
	  엔티티만 변경해주면 된다. */
	savedTourImg.updateTourImg(oriImgName, imgName, imgUrl);
			
		}
	}
}
