package com.travel.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.travel.Dto.ItemFormDto;
import com.travel.Dto.ItemImgDto;
import com.travel.Dto.ItemSearchDto;
import com.travel.Dto.MainItemDto;
import com.travel.Repository.ItemImgRepository;
import com.travel.Repository.ItemRepository;
import com.travel.Repository.MemberRepository;
import com.travel.entity.Item;
import com.travel.entity.ItemImg;
import com.travel.entity.Member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

	private final ItemRepository itemRepository;
	private final ItemImgRepository itemImgRepository;
	private final ItemImgService itemImgService;
	private final MemberRepository memberRepository;

	// item 테이블에 상품등록(insert)
	public Long saveItem(String no, ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

		// 1. 상품등록
		Item item = itemFormDto.createItem(); // dto > entity
		
		Member member = memberRepository.findByEmail(no);
		item.setMember(member);
		
		itemRepository.save(item);

		// 2.이미지 등록 (5개)
		for (int i = 0; i < itemImgFileList.size(); i++) {
			// fk를 사용 시 부모테이블에 해당하는 entity를 먼저 넣어줘야 한다.
			ItemImg itemImg = new ItemImg();
			itemImg.setItem(item);

			// 첫번째 이미지 일 때 대표상품 이미지 지정
			if (i == 0) {
				itemImg.setRepimgYn("Y");
			} else {
				itemImg.setRepimgYn("N");
			}
			itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
		}

		return item.getId(); // 등록한 상품 id를 리턴.
	}

	// 상품 가져오기.
	@Transactional(readOnly = true) // 트랜잭션 읽기 전용 (변경감지 수행하지 않음) > 성능 향상.
	public ItemFormDto getItemDtl(Long itemId) {
		// 1. item_img 테이블의 이미지를 가져온다.
		List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

		// item_img 앤티티 객체 > ItemImgDto로 변환
		List<ItemImgDto> itemImgDtoList = new ArrayList<>();
		for (ItemImg itemImg : itemImgList) {
			ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
			itemImgDtoList.add(itemImgDto);
		}

		// 2. item테이블에 있는 데이터를 가져온다.
		Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

		// Item 엔티티 객체 > dto로 변환
		ItemFormDto itemFormDto = ItemFormDto.of(item);

		// 3. ItemFormDto에 이미지정보(itemImgDtoList)를 넣어준다.
		itemFormDto.setItemImgDtoList(itemImgDtoList);

		return itemFormDto; // 이미지 정보, 아이템 테이블에서 가져온 내용 all 들어있음.
	}

	// 수정하기
	public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

		// 1. item 엔티티 가져와서 바꾼다.
		Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

		item.updateItem(itemFormDto);

		// 2. item_img를 바꿔준다. > 5개의 레코드 전부 변경.
		List<Long> itemImgIds = itemFormDto.getItemImgIds(); // 상품 이미지 아이디 리스트 조회

		for (int i = 0; i < itemImgFileList.size(); i++) {
			itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
			
		}
			return item.getId(); // 변경한 item의 id 리턴.


}

		
		@Transactional(readOnly = true)
		public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
			
			Page<Item> itemPage = itemRepository.getAdminItemPage(itemSearchDto, pageable);
			return itemPage;
			
		}
		



	@Transactional(readOnly = true)
	public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
		Page<MainItemDto> mainItemPage = itemRepository.getMainItemPage(itemSearchDto, pageable);
		return mainItemPage;
	}


	// 상품 삭제
	public void deleteItem(Long itemId) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(EntityNotFoundException::new);
		
		itemRepository.delete(item);
	}
	
	
}
