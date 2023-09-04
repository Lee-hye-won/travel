package com.travel.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travel.Dto.CartDto;
import com.travel.Dto.CartItemDto;
import com.travel.Dto.CartListDto;
import com.travel.auth.PrincipalDetails;
import com.travel.entity.Member;
import com.travel.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;
	
	@PostMapping(value = "/addCart")
	public ResponseEntity<Long> addCart(@RequestBody @Valid CartDto cartDto, BindingResult bindingResult, Authentication authentication) {
	    if (bindingResult.hasErrors()) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	    System.out.println(cartDto.getItemId()+"asdasdjeybfalkwe4al;erhjoerahg");
	    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	    Member member = principalDetails.getMember();

	    try {
	        Long cartId = cartService.addToCart(cartDto, member);
	        return new ResponseEntity<>(cartId, HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}
	
	@GetMapping(value = {"/cartList", "/cartList/{page}"})
	public String cartList(@PathVariable("page") Optional<Integer>page, Authentication authentication, Model model) {
		
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String email = members.getEmail();
		Page<CartListDto> cartListDto = cartService.getCartList(email, pageable);
		System.out.println(cartListDto.getContent());
		
		model.addAttribute("carts", cartListDto);
		model.addAttribute("maxPage", 5);
		model.addAttribute("page", pageable.getPageNumber());
		
		return "/item/cart";
	}
	
	@DeleteMapping("/cartList/{cartId}/delete")
	public @ResponseBody ResponseEntity deleteCart(@PathVariable("cartId")Long cartId, Authentication authentication) {
	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String email = members.getEmail();
		if(!cartService.validateCart(cartId, email)) {
			return new ResponseEntity<String>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
			
		}
		
		cartService.deleteCart(cartId);
		
		return new ResponseEntity<Long>(cartId, HttpStatus.OK);
	}
	
}
