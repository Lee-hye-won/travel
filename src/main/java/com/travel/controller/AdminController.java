 package com.travel.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nimbusds.oauth2.sdk.Role;
import com.travel.Dto.AskFormDto;
import com.travel.Dto.AskResponseFormDto;
import com.travel.Dto.AskSearchDto;
import com.travel.Dto.InfoFormDto;
import com.travel.Dto.InfoSearchDto;
import com.travel.Dto.ItemFormDto;
import com.travel.Dto.ItemSearchDto;
import com.travel.Dto.MainAskDto;
import com.travel.Dto.MemberFormDto;
import com.travel.Dto.TourFormDto;
import com.travel.Dto.TourSearchDto;
import com.travel.auth.PrincipalDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.travel.entity.AskBoard;
import com.travel.entity.InfoBoard;
import com.travel.entity.Item;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.travel.Dto.MemberFormDto;

import com.travel.entity.Member;
import com.travel.entity.Tourist;
import com.travel.service.AskResponseService;
import com.travel.service.AskService;
import com.travel.service.InfoService;
import com.travel.service.ItemService;
import com.travel.service.MemberService;
import com.travel.service.TourService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class AdminController {

	private final ItemService itemService;
	private final TourService tourService;
	private final InfoService infoService;
	private final AskService askService;
	private final AskResponseService askResponseService;
	private final MemberService memberService;

	@GetMapping(value = "/admin")
	public String admin(Authentication authentication) {
		PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
		
		return "admin/adminMain";
	}
	
	


	// 회원 리스트
	@GetMapping(value = { "/admin/list", "/admin/list/{page}" })
	public String memberManage(@PathVariable("page") Optional<Integer> page, Model model) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

		Page<Member> travel = memberService.getAdminlistPage(pageable);
		model.addAttribute("travel", travel);
		model.addAttribute("maxPage", 5);

		return "admin/memberList";
	}

	//회원 리스트(회원 프로필)	
	@GetMapping(value =  {"/admin/profile" , "/admin/profile/{memberId}"})
	public String  Profilemember(@PathVariable("memberId") Long memberId, Model model) {
		MemberFormDto memberFormDto = memberService.getmemberDtl(memberId);
		
		model.addAttribute("member", memberFormDto);
		return "admin/profile";
	}

	
	// 회원 탈퇴시키기
	@DeleteMapping(value = "/admin/{memberId}/delete")
	public @ResponseBody ResponseEntity deleteMember(@RequestBody @PathVariable("memberId") Long memberId
			) {
		
		memberService.deleteMember(memberId);
		
		return new ResponseEntity<Long>(memberId, HttpStatus.OK);
	}
	
	
	// 상품
	
	
	// 쇼핑몰 상품 리스트
	@GetMapping(value = { "/adminShop", "/adminShop/{page}" })
	public String itemManage(ItemSearchDto itemSearchDto, 
			@PathVariable("page") Optional<Integer> page, Model model) {
		

			//of(조회할 페이지의 번호★[0부터 시작], 한페이지당 조회할 데이터 갯수)
			//url 경로에 페이지가 있으면 해당 페이지 번호를 조회하도록 하고 페이지 번호가 없으면 0페이지를 조회.
			Pageable pageable = PageRequest.of(page.isPresent()?page.get():0, 3); 
			
			Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
			
			model.addAttribute("items" , items);
			model.addAttribute("itemSearchDto", itemSearchDto);
			model.addAttribute("maxPage", 5); //상품관리페이지 하단에 보여줄 최대 페이지 번호
			return "admin/itemList";
			}



	// 쇼핑몰 상품 등록하기 보여주기
	@GetMapping(value = "/adminShop/new")
	public String adminShop(Model model) {
		model.addAttribute("itemFormDto", new ItemFormDto());
		return "admin/itemRegist";
	}

	// 상품, 상품이미지 등록
	@PostMapping(value = "/adminShop/new")
	public String itemNew(Authentication authentication, @Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
			@RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
		
	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String email = members.getEmail();
		
		if (bindingResult.hasErrors()) {
			return "admin/itemRegist";
		}

		// 상품등록전에 첫번째 이미지가 있는지 없는지 검사.(첫번째 이미지는 필수입력값)
		if (itemImgFileList.get(0).isEmpty()) {
			model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수입니다.");
			return "admin/itemRegist";
		}

		try {
			itemService.saveItem(email,itemFormDto, itemImgFileList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
			return "admin/itemRegist";
		}

		return "redirect:/";
	}

	
	// 상품 수정페이지 보여주기
	@GetMapping(value = "/adminShop/item/{itemId}")
	public String itemDtl(@PathVariable("itemId") Long itemId,Model model) {
		
		try {
			ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
			model.addAttribute("itemFormDto",itemFormDto);
		} catch (Exception e) {
			e.printStackTrace(); //이거 안하면 에러 안찍힘.
			model.addAttribute("errorMessage", "상품정보를 가져올 때 에러가 발생했습니다.");
			//에러발생시 비어있는 객체를 넘겨준다. 
			model.addAttribute("itemFormDto", new ItemFormDto()); 
			return "admin/itemRegist";
		}
		
		
		return "admin/itemModify";
	}
	
	//상품수정
	@PostMapping(value = "/adminShop/item/{itemId}")
		public String itemUpdate(@Valid ItemFormDto itemFormDto,Model model,
				BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
			
			if(bindingResult.hasErrors()) {
				return "admin/itemRegist";
			}
			
			//첫번째 이미지가 있는지 검사. 
			if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId()==null) {
				model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수입니다.");
				return "admin/itemRegist";
			}
			
			try {
				itemService.updateItem(itemFormDto, itemImgFileList);
				
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
				return "admin/itemRegist";
			}
			
			return "redirect:/";
		}
	
	
	// 상품 삭제
	@DeleteMapping("/adminShop/{itemId}/delete")
	public @ResponseBody ResponseEntity deletemovie(@RequestBody @PathVariable("itemId") Long itemId) {
		itemService.deleteItem(itemId);
		return new ResponseEntity<Long>(itemId, HttpStatus.OK);
	}
	
	
	
	// 공지사항
	
	
	// 공지사항 리스트
		@GetMapping(value = { "/adminInfo", "/adminInfo/{page}" })
		public String infoManage(InfoSearchDto infoSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {

			// of(조회할 페이지의 번호★[0부터 시작], 한페이지당 조회할 데이터 갯수)
			// url 경로에 페이지가 있으면 해당 페이지 번호를 조회하도록 하고 페이지 번호가 없으면 0페이지를 조회.
			Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

			Page<InfoBoard> infos = infoService.getAdminInfoPage(infoSearchDto, pageable);

			try {
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			model.addAttribute("infos", infos);
			model.addAttribute("infoSearchDto", infoSearchDto);
			model.addAttribute("maxPage", 5); // 상품관리페이지 하단에 보여줄 최대 페이지 번호

			return "admin/infoList";

		}
	
	
	// 공지사항 등록 보여주기
	@GetMapping(value = "/adminInfo/new")
		public String adminInfo(Model model) {
			model.addAttribute("infoFormDto", new InfoFormDto());
			return "admin/infoRegist";
		}

	// 공지사항, 공지사항이미지 등록
	@PostMapping(value = "/adminInfo/new")
		public String InfoNew(Authentication authentication, @Valid InfoFormDto infoFormDto, BindingResult bindingResult, 
				Model model, @RequestParam("infoImgFile") List<MultipartFile> infoImgFileList) {

	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String memberNo = members.getEmail();
		
		if(bindingResult.hasErrors()) {
			return "admin/infoRegist";
		}
		
		//상품등록전에 첫번째 이미지가 있는지 없는지 검사.(첫번째 이미지는 필수입력값)
		if(infoImgFileList.get(0).isEmpty()) {
			model.addAttribute("errorMessage", "첫번째 이미지는 필수입니다.");
			return "admin/infoRegist";
		}
		
		try {
			infoService.saveInfo(memberNo, infoFormDto, infoImgFileList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "공지 등록 중 에러가 발생했습니다.");
			return "admin/infoRegist";
		}
		
		return "redirect:/";
		}
	

	// 공지사항 상세페이지
	@GetMapping(value = "/info/{infoBoardId}")
	public String infoDtl(Model model, @PathVariable("infoBoardId") Long infoBoardId) {
		InfoFormDto infoFormDto = infoService.getInfoDtl(infoBoardId);
		model.addAttribute("info", infoFormDto);
		return "admin/infoDtl";
	}
	
	
	// 공지사항 수정페이지 보여주기
	@GetMapping(value = "/adminInfo/info/{infoBoardId}")
	public String infoModify(@PathVariable("infoBoardId") Long infoBoardId,Model model) {
		
		try {
			InfoFormDto infoFormDto = infoService.getInfoDtl(infoBoardId);
			model.addAttribute("infoFormDto",infoFormDto);
		} catch (Exception e) {
			e.printStackTrace(); //이거 안하면 에러 안찍힘.
			model.addAttribute("errorMessage", "상품정보를 가져올 때 에러가 발생했습니다.");
			//에러발생시 비어있는 객체를 넘겨준다. 
			model.addAttribute("infoFormDto", new InfoFormDto()); 
			return "admin/infoRegist";
		}
		
		
		return "admin/infoModify";
	}
	
	
	// 공지사항 수정하기
	@PostMapping(value = "/adminInfo/info/{infoBoardId}")
	public String infoUpdate(@Valid InfoFormDto infoFormDto,Model model,
			BindingResult bindingResult, @RequestParam("infoImgFile") List<MultipartFile> infoImgFileList) {
		
		if(bindingResult.hasErrors()) {
			return "admin/infoRegist";
		}
		
		//첫번째 이미지가 있는지 검사. 
		if(infoImgFileList.get(0).isEmpty() && infoFormDto.getId()==null) {
			model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수입니다.");
			return "admin/infoRegist";
		}
		
		try {
			infoService.updateInfo(infoFormDto, infoImgFileList);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
			return "admin/infoRegist";
		}
		
		return "redirect:/";
	}
	
	// 공지사항 삭제하기
	@DeleteMapping("/adminInfo/{infoBoardId}/delete")
	public @ResponseBody ResponseEntity deleteInfo(@RequestBody @PathVariable("infoBoardId") Long infoBoardId) {
		infoService.deleteInfo(infoBoardId);
		return new ResponseEntity<Long>(infoBoardId, HttpStatus.OK);
		}
	
	
	
	// 추천관광지
	
	// 추천관광지 리스트 
	@GetMapping(value = {"/adminTour/tours","/adminTour/tours/{page}"})
	public String tourManage(TourSearchDto tourSearchDto,
			@PathVariable("page") Optional<Integer> page, Model model) {
		
		Pageable pageable = PageRequest.of(page.isPresent()?page.get():0, 3); 
		
		Page<Tourist> tours = tourService.getAdminTourPage(tourSearchDto, pageable);
		
		model.addAttribute("tours" , tours);
		model.addAttribute("tourSearchDto", tourSearchDto);
		model.addAttribute("maxPage", 5); //상품관리페이지 하단에 보여줄 최대 페이지 번호
		
		return "admin/tourList";
	}
	
	
	
	// 추천관광지 등록 보여주기
	@GetMapping(value = "/adminTour/new")
	public String adminTour(Model model) {
		model.addAttribute("tourFormDto", new TourFormDto());
		return "admin/tourRegist";
	}
	
	// 추천관광지 등록
	@PostMapping(value = "/adminTour/new")
	public String tourNew(Authentication authentication, @Valid TourFormDto tourFormDto, BindingResult bindingResult,
			Model model, @RequestParam("tourImgFile") List<MultipartFile> tourImgFileList) {
	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String memberNo = members.getEmail();
		
		if(bindingResult.hasErrors()) {
			return "admin/tourRegist";
		}
		
		//상품등록전에 첫번째 이미지가 있는지 없는지 검사.(첫번째 이미지는 필수입력값)
		if(tourImgFileList.get(0).isEmpty()) {
			model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수입니다.");
			return "admin/tourRegist";
		}
		
		try {
			tourService.saveTour(memberNo, tourFormDto, tourImgFileList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
			return "admin/tourRegist";
		}
		
		return "redirect:/";
	}
	

	
	
	// 추천관광지 수정페이지 보여주기
	@GetMapping(value = "/adminTour/tour/{touristId}")
	public String tourModify(@PathVariable("touristId") Long touristId,Model model) {
		
		try {
			TourFormDto tourFormDto = tourService.getTourDtl(touristId);
			model.addAttribute("tourFormDto",tourFormDto);
		} catch (Exception e) {
			e.printStackTrace(); //이거 안하면 에러 안찍힘.
			model.addAttribute("errorMessage", "상품정보를 가져올 때 에러가 발생했습니다.");
			//에러발생시 비어있는 객체를 넘겨준다. 
			model.addAttribute("tourFormDto", new TourFormDto()); 
			return "admin/tourRegist";
		}
		
		return "admin/tourModify";
	}
	
	
	// 추천관광지 수정
	@PostMapping(value = "/adminTour/tour/{touristId}")
	public String tourUpdate(@Valid TourFormDto tourFormDto,Model model,
			BindingResult bindingResult, @RequestParam("tourImgFile") List<MultipartFile> tourImgFileList) {
		
		if(bindingResult.hasErrors()) {
			return "admin/tourRegist";
		}
		
		//첫번째 이미지가 있는지 검사. 
		if(tourImgFileList.get(0).isEmpty() && tourFormDto.getId()==null) {
			model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수입니다.");
			return "admin/tourRegist";
		}
		
		try {
			tourService.updateTour(tourFormDto, tourImgFileList);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
			return "admin/tourList";
		}
		
		return "redirect:/";
	}

	

	//추천관광지 상세 페이지
	@GetMapping(value = "/tour/{touristId}")
	public String tourDtl(Model model, @PathVariable("touristId") Long touristId) {
		TourFormDto tourFormDto = tourService.getTourDtl(touristId);
		model.addAttribute("tour", tourFormDto);
		return "admin/tourDtl";
	}
	
	
	// 추천관광지 삭제
	@DeleteMapping("/adminTour/{touristId}/delete")
	public @ResponseBody ResponseEntity deleteTour(@RequestBody @PathVariable("touristId") Long touristId) {
		tourService.deleteTour(touristId);
		return new ResponseEntity<Long>(touristId, HttpStatus.OK);
	}
	
	
	
	// 문의사항
	
	// 문의사항 리스트 보여주기 
	@GetMapping(value = { "/adminAsk/asks", "/adminAsk/asks/{page}"})
	public String askList(Model model, AskSearchDto askSearchDto,@PathVariable Optional<Integer> page) {
		Pageable pageable  = PageRequest.of(page.isPresent() ? page.get() : 0, 6);	
		Page<AskBoard> asks = askService.getAdminAskPage(askSearchDto, pageable);
		
		
		
		 // Add askStatus to each AskResponseFormDto in the list
	    //List<AskResponseFormDto> askResponseFormDtos = new ArrayList<>();
	    for (AskBoard ask : asks.getContent()) {
	        AskResponseFormDto askResponseFormDto = askResponseService.getAskResponseDtl(ask.getId());
	        if (askResponseFormDto != null) {
	            askResponseFormDto.setAskStatus(askResponseFormDto.getAskStatus());
	            model.addAttribute("askResponse", askResponseFormDto);
	        }
	        //askResponseFormDtos.add(askResponseFormDto);
	    }
		
		model.addAttribute("asks", asks);
		model.addAttribute("askSearchDto", askSearchDto);
		
		//model.addAttribute("askResponseFormDtos", askResponseFormDtos);
		model.addAttribute("maxPage", 5);
		
		return "admin/askList";
	}

	// 문의사항 등록 보여주기 - 회원
	@GetMapping(value = "/ask/new")
	public String askForm(Model model) {
		model.addAttribute("askFormDto", new AskFormDto());
		return "admin/askRegist";
	}
	
	// 문의사항 등록하기 - 회원
	@PostMapping(value = "/ask/new")
	public String askNew(Authentication authentication, @Valid AskFormDto askFormDto, BindingResult bindingResult, Model model, @RequestParam("askImgFile") List<MultipartFile> askImgFileList) {
	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String email = members.getEmail();
		if(bindingResult.hasErrors()) {
			return "admin/askRegist";
		}
		
		try {
			askService.saveAsk(email, askFormDto, askImgFileList);			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "문의사항 등록 중 에러가 발생했습니다.");
			return "admin/askRegist";
		}
		
		return "redirect:/";
	}
	
	
	// 문의사항 상세페이지
	@GetMapping(value = "/askBoard/{askBoardId}")
	public String asksDtl(Model model, @PathVariable("askBoardId") Long askBoardId, Authentication authentication) {
		AskFormDto askFormDto = askService.getAskDtl(askBoardId);
	    
		AskResponseFormDto askResponseFormDto = askResponseService.getAskResponseDtl(askBoardId);
		
		 // 현재 로그인한 사용자의 아이디를 가져옴
	    PrincipalDetails principals = (PrincipalDetails) authentication.getPrincipal();
        Member members = principals.getMember();
		String loggedInUsername = members.getEmail();
	    if (askFormDto != null) {
	    
	        // 문의사항 작성자의 아이디와 현재 로그인한 사용자의 아이디를 비교
	        if (askFormDto.getCreateBy().equals(loggedInUsername) || members.getRole().toString().equals("ROLE_ADMIN")) {
	            // 현재 로그인한 사용자가 글 작성자인 경우에만 상세 페이지 표시
	            model.addAttribute("isOwner", true);
	            model.addAttribute("ask", askFormDto);
	            if (askResponseFormDto != null) {
	                model.addAttribute("askResponse", askResponseFormDto);
	            }} 
	        else {
	            // 등록한 사람이 아닌 경우 메시지를 표시하고 상세 페이지 미표시
	            model.addAttribute("isOwner", false);
	        }
	    model.addAttribute("ask", askFormDto);
	    }
	    return "admin/askDtl";
	}
	
	// 문의사항 수정보여주기 - 회원
	@GetMapping(value = "/ask/{askBoardId}")
	public String askModify(@PathVariable("askBoardId") Long askBoardId, Model model) {
		
		try {
			AskFormDto askFormDto = askService.getAskDtl(askBoardId);
			model.addAttribute("askFormDto", askFormDto);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "문의사항을 가져올 때 에러가 발생했습니다.");
			
			// 에러발생 시 비어있는 객체를 넘겨준다.
			model.addAttribute("askFormDto", new AskFormDto());
			return "admin/askRegist";
		}
		
		return "admin/askModify";
	}
	
	// 문의사항 수정하기 - 회원 
	@PostMapping(value = "/ask/{askBoardId}")
	public String askUpdate(@Valid AskFormDto askFormDto, Model model,
			BindingResult bindingResult, @RequestParam("askImgFile") List<MultipartFile> askImgFileList) {
		
		if(bindingResult.hasErrors()) {
			return "admin/askRegist";
		}
		
		try {
			askService.updateAsk(askFormDto, askImgFileList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "문의사항 등록 중 에러가 발생했습니다.");
			return "admin/askRegist";
		}
		
		return "redirect:/";
	}
	
	
	// 문의사항 삭제하기 - 회원
		@DeleteMapping("/ask/{askBoardId}/delete")
		public @ResponseBody ResponseEntity deleteAsk(@RequestBody @PathVariable("askBoardId") Long askBoardId) {
			askService.deleteAsk(askBoardId);
			return new ResponseEntity<Long>(askBoardId, HttpStatus.OK);
		}
	
		
	// 문의사항 답변하기 보여주기 - 관리자
	@GetMapping(value = "/response/{askBoardId}")
	public String askResponseForm(Model model,  @PathVariable("askBoardId") Long askBoardId) {
		
		AskResponseFormDto arFormDto = new AskResponseFormDto();
		arFormDto.setAskBoardId(askBoardId); 
		model.addAttribute("askResponseFormDto", arFormDto);
		
		return "admin/askResponseRegist";
		}
	
	
	// 문의사항 답변하기 등록 - 관리자
	@PostMapping(value = "/response/{askBoardId}")
	public String askResponseNew(@Valid AskResponseFormDto askResponseFormDto, BindingResult bindingResult, Model model,
			 @PathVariable("askBoardId") Long askBoardId) {
		
		//AskResponseFormDto arFormDto = new AskResponseFormDto();
		//arFormDto.setAskBoardId(askBoardId); 
		//model.addAttribute("askResponseFormDto", arFormDto);
		
		 askResponseFormDto.setAskBoardId(askBoardId);
		
		
		if(bindingResult.hasErrors()) {
			return "admin/askResponseRegist";
		}
		
		try {
			askResponseService.saveAskResponse(askBoardId, askResponseFormDto);		
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "답변 등록 중 에러가 발생했습니다.");
			return "admin/askResponseRegist";
		}
		
		return "redirect:/";
	}
	
	// 문의사항 답변 수정보여주기 - 관리자
	@GetMapping(value = "/ask/response/{askBoardId}")
	public String askResponseBoardIdModify(@PathVariable("askBoardId") Long askResponseBoardId, Model model) {
		
		try {
			AskResponseFormDto askResponseFormDto = askResponseService.getAskResponseDtl(askResponseBoardId);
			model.addAttribute("askResponseFormDto", askResponseFormDto);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "문의사항을 가져올 때 에러가 발생했습니다.");
			
			// 에러발생 시 비어있는 객체를 넘겨준다.
			model.addAttribute("askResponseFormDto", new AskResponseFormDto());
			return "admin/askResponseRegist";
		}
		
		return "admin/askResponseModify";
	}
	
	
	// 문의사항 답변 수정하기 - 관리자 
	@PostMapping(value = "/ask/response/{askBoardId}")
	public String askResponseUpdate(@Valid AskResponseFormDto askResponseFormDto, Model model,
			BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			return "admin/askResponseRegist";
		}
		
		try {
	
			askResponseService.updateAskResponse(askResponseFormDto);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "답변 등록 중 에러가 발생했습니다.");
			return "admin/askResponseRegist";
		}
		
		return "redirect:/";
	}

	// 문의사항 답변 삭제 - 관리자 
	@DeleteMapping("/askResponse/{askBoardId}/delete")
	public @ResponseBody ResponseEntity deleteAskResponse(@RequestBody @PathVariable("askBoardId") Long askResponseBoardId) {
		askResponseService.deleteAskResponse(askResponseBoardId);
		return new ResponseEntity<Long>(askResponseBoardId, HttpStatus.OK);
	}
}
	 
		


