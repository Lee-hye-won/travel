package com.travel.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.travel.Dto.ItemFormDto;
import com.travel.Dto.ItemSearchDto;
import com.travel.Dto.MainItemDto;
import com.travel.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {
	
	private final ItemService itemService;
	
	//상품 목록 불러오기
	@GetMapping(value="/item/list")
	public String itemShopList(Model model, ItemSearchDto itemSearchDto, Optional<Integer> page) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
		Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
		
		model.addAttribute("items", items);
		model.addAttribute("itemSearchDto", itemSearchDto);
		model.addAttribute("maxPage", 5);
		
		
		return "item/itemShopList";
	}
	
	//상품 상세보기
	@GetMapping(value="/item/{itemId}")
	public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
		ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
		model.addAttribute("item", itemFormDto);
		
		return "item/itemDtl";
	}
	
}
