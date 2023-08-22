package com.travel.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travel.Dto.CartDto;
import com.travel.Dto.CartListDto;
import com.travel.service.ItemService;
import com.travel.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;
	
	@PostMapping(value = "/addCart")
	public @ResponseBody ResponseEntity cart(@RequestBody @Valid CartDto orderDto, BindingResult bindingResult, Principal principal) {
		String email = principal.getName();
		Long cartId;
		
		try {
			cartId = cartService.cart(orderDto, email);			
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Long>(cartId, HttpStatus.OK);

	}
	
	@GetMapping(value = {"/cartList", "/cartList/{page}"})
	public String cartList(@PathVariable("page") Optional<Integer>page, Principal principal, Model model) {
		
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);

		
		Page<CartListDto> cartListDto = cartService.getCartList(principal.getName(), pageable);
		
		model.addAttribute("carts", cartListDto);
		model.addAttribute("maxPage", 5);
		model.addAttribute("page", pageable.getPageNumber());
		
		return "/item/cart";
	}
	
}