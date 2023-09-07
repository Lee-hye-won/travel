package com.travel.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travel.Dto.BeforeOrderDto;
import com.travel.Dto.CartDto;
import com.travel.Dto.CartItemDto;
import com.travel.Dto.OrderHistDto;
import com.travel.Dto.OrderItemDto;
import com.travel.auth.PrincipalDetails;
import com.travel.entity.Member;
import com.travel.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	
	//장바구니에서 주문하기
	@PostMapping(value= "/order/address")
	public @ResponseBody ResponseEntity getAddress(@RequestBody Map<String, Object> requestData,BindingResult bindingResult, Model model) {
	    Long[] selectedProductIds = ((List<Integer>) requestData.get("selectedProductIds"))
	            .stream()
	            .map(Long::valueOf)
	            .toArray(Long[]::new);
		if(bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			
			for(FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getDefaultMessage()); //에러메세지를 합친다.
			}
			
			return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
		}
		
	    
	    Map<String, Object> responseData = new HashMap<>();
	    responseData.put("selectedProductIds", selectedProductIds);
	    

	    return new ResponseEntity<>(responseData, HttpStatus.OK);	
	    
	}
	
	//장바구니에서 선택한 상품 주문하러 가기
	@GetMapping(value= "/order/{selectedProductIdsString}")
	public String test(@PathVariable("selectedProductIdsString") Object selectedProductIdsString,
            Model model) {
		String[] idsArray = selectedProductIdsString.toString().split(",");
        Long[] ids = new Long[idsArray.length];
        
        for (int i = 0; i < idsArray.length; i++) {
            ids[i] = Long.parseLong(idsArray[i]);
        }
        
        
        long totalPrice = 0;
        List<CartItemDto> selectedItems = orderService.findItemsByIds(ids);

        
        for (CartItemDto item : selectedItems) {
            totalPrice += item.getPrice() * item.getCount();
        }
        
        model.addAttribute("selectedItems", selectedItems);
        model.addAttribute("totalPrice", totalPrice);

        return "item/address";
	}
	
	

	//결제완료 페이지
	@GetMapping(value = "/order/success/{payId}")
	public String orderSuccess() {
		
		return "item/orderInfo";
	}
	
	
	//주문 내역 리스트
	@GetMapping(value = {"/orders", "/orders/{page}"})
	public String orderHist(@PathVariable("page") Optional<Integer>page, Principal principal, Model model) {
		Pageable pageable =  PageRequest.of(page.isPresent() ? page.get() : 0, 4);
		Page<OrderHistDto> orderHistDto = orderService.getOrderList(principal.getName(), pageable);
		
		model.addAttribute("orders", orderHistDto);
		model.addAttribute("maxPage", 5);
		model.addAttribute("page", pageable.getPageNumber());
		
		return "item/orderHist";
	}

	
	//주문 취소하기
	@PostMapping("/orders/{orderId}/cancel")
	public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
		//1. 주문취소 권한이 있는지 확인(본인확인)
		if(!orderService.validateOrder(orderId, principal.getName())) {

			return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
		
		}

		//2. 주문취소
		orderService.cancelOrder(orderId);
		return new ResponseEntity <Long>(orderId, HttpStatus.OK); //성공했을때 
	}
	
	//주문삭제하기
	@DeleteMapping("/order/{orderId}/delete")
	public @ResponseBody ResponseEntity deleteOrder(@PathVariable("orderId")Long orderId, Principal principal) {
		//★delete하기 전에 select 를 한번 해준다. -> 영속성 컨텍스트에 엔티티를 저장한 후 변경 감지를 하도록 하기 위해
		//1. 본인인증
		if(!orderService.validateOrder(orderId, principal.getName())) {
			return new ResponseEntity<String>("주문 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		
		orderService.deleteOrder(orderId);
		
		//2. 주문삭제
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}
	

	
}
