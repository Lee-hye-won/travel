package com.travel.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travel.Dto.KakaoPayApproveDto;
import com.travel.Dto.KakaoPayReadyDto;
import com.travel.constant.OrderStatus;
import com.travel.entity.CartItem;
import com.travel.entity.Member;
import com.travel.entity.OrderItem;
import com.travel.entity.Orders;
import com.travel.entity.Pay;
import com.travel.service.CartService;
import com.travel.service.KakaoPayService;
import com.travel.service.MemberService;
import com.travel.service.OrderService;
import com.travel.service.PayService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KakaoPayController {
	private final KakaoPayService kakaoPayService;
	private final PayService payService;
	private final MemberService memberService;
	private final CartService cartService;
	private final OrderService orderService;

	//카카오페이 큐알 호출하기
	@GetMapping("/pay/ready")
	@ResponseBody
	public KakaoPayReadyDto kakaoPay(@RequestParam(value = "orderItemIds[]", required = false) Long[] orderItemIds,
	        @RequestParam("itemName") String itemName,
	        @RequestParam("totalPrice") int totalPrice,
	        @RequestParam("orderName") String orderName,
	        @RequestParam("zipCode") String zipCode,
	        @RequestParam("orderAddress") String orderAddress,
	        @RequestParam("phoneNumber") String phoneNumber,
	        Model model, HttpSession session, Principal principal) {
		
		Map<String, Object> params = new HashMap<>();
	    params.put("orderItemIds", orderItemIds);
	    params.put("totalPrice", totalPrice);
	    params.put("itemName", itemName);
	    
		int conut = 0;
		if (orderItemIds != null) {
			for (Long itemId : orderItemIds) {
				CartItem cartItem = orderService.getCartItemById(itemId);

			}
			params.put("itemCount", conut - 1);
		}
	    

		KakaoPayReadyDto res = kakaoPayService.kakaoPay(params);

		
	    // tid 값을 success 페이지로 전달하기 위해 모델에 추가
	    session.setAttribute("tid", res.getTid());
	    session.setAttribute("orderItemIds", orderItemIds);
	    session.setAttribute("item_name", itemName);
	    session.setAttribute("total_price", totalPrice);
	    session.setAttribute("orderName", orderName);
	    session.setAttribute("zipCode", zipCode);
	    session.setAttribute("orderAddress", orderAddress);
	    session.setAttribute("phoneNumber", phoneNumber);
	    
		return res;
	}
	
	//카카오페이로 결제한 정보 DB저장하기
	@GetMapping("/pay/success")
	public String success(@RequestParam("pg_token")String pgToken,HttpSession session, Principal principal, Model model) {
			String tid = (String) session.getAttribute("tid");
			String itemName = (String) session.getAttribute("item_name");
			int totalPrice = (int) session.getAttribute("total_price");
			String orderName = (String) session.getAttribute("orderName");
			String zipCode = (String) session.getAttribute("zipCode");
			String orderAddress = (String) session.getAttribute("orderAddress");
			String phoneNumber = (String) session.getAttribute("phoneNumber");

		    Long[] orderItemIds = (Long[]) session.getAttribute("orderItemIds");

			String email = principal.getName();
			Member member = memberService.findByEmail(email);
	        // 카카오 결재 요청하기
	        KakaoPayApproveDto kakaoPayApproveDto = kakaoPayService.kakaoPayApprove(tid, pgToken);
	        

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// 현재 시간을 가져옵니다.
			Date currentDate = new Date();
			String currentDateString = dateFormat.format(currentDate); // 현재 시간을 문자열로 변환합니다.
			
			Pay pay = new Pay();
			pay.setPrice(totalPrice);
			pay.setPayNo(generateRandomPayNo());
			pay.setPayDate(currentDateString);
			pay.setMember(member);
			kakaoPayService.savePay(pay);
	        
			List<OrderItem> orderItemList = new ArrayList<>();
			Orders orders = new Orders();
			orders.setOrderInfoAddress(orderAddress);
			orders.setOrderInfoName(orderName);
			orders.setOrderInfoPhone(phoneNumber);
			orders.setOrderStatus(OrderStatus.ORDER);
			orders.setTotalPrice(totalPrice);
			orders.setZipCode(zipCode);
			orders.setPay(pay);
			orders.setMember(member);
			orders.setOrderDate(pay.getPayDate());
			
			

			for (Long orderItemId : orderItemIds) {
				CartItem cartItem = orderService.getCartItemById(orderItemId);
				
				OrderItem orderItem = OrderItem.createOrderCart(cartItem);
				orderItem.setOrders(orders);
				
				orderService.setOrderItem(cartItem, orderItem);
				orderItemList.add(orderItem);
				kakaoPayService.saveOrders(orders);
				kakaoPayService.saveOrderItem(orderItem);
				cartService.deleteCart(cartItem.getId());
			}

	        // HTML에 데이터 전달
	        model.addAttribute("item_name", itemName);
	        model.addAttribute("total_price", totalPrice);
	        model.addAttribute("orderName" , orderName);
	        model.addAttribute("zipCode", zipCode);
	        model.addAttribute("orderAddress", orderAddress);
	        model.addAttribute("phoneNumber", phoneNumber);
	        model.addAttribute("pay_number", pay.getPayNo());
	        model.addAttribute("pay_id", pay.getId());

	        
	        
	        return "item/paySuccess";
	}
	
	
	private String generateRandomPayNo() {
	    Random random = new Random();
	    int randomNumber = random.nextInt(1000000000); // 0에서 999999 사이의 난수 생성
	    return String.valueOf(randomNumber); // 숫자를 문자열로 변환하여 반환
	}
	
	
	
}