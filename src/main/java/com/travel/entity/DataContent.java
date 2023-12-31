package com.travel.entity;

import com.travel.Dto.PlanContentDto;
import com.travel.constant.TravelDivision;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="data_content") 
@Getter
@Setter
@ToString
public class DataContent {

	@Id
	@Column(name="plan_content_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
		
	@Column(name="place_name")
	private String placeName;
	
	@Column(name="place_address")
	private String placeAddress;
	
	@Column(name="place_tel")
	private String placeTel;
	
	@Column(name="place_latitude")
	private String placeLatitude;
	
	@Column(name="place_longitude")
	private String placeLongitude;
	
	@Column(name="place_img")
	private String placeimg;
	
	@Column(name="area_code")
	private int areaCode;
	
	@Column(name="sigungu_code")
	private int sigunguCode;
	
	@Column(name="content_id")
	private int contentId;
	
	@Column(name="content_type")
	private int contentType;
			
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id")
	private Plan plan;
	

	
	
	public static DataContent createContent(PlanContentDto dataContentDto, Plan plan) {
		DataContent dataContent = new DataContent();
		dataContent.setPlaceName(dataContentDto.getPlaceName());
		dataContent.setPlaceAddress(dataContentDto.getPlaceAddress());
		dataContent.setPlaceTel(dataContentDto.getPlaceTel());
		dataContent.setPlaceLatitude(dataContentDto.getPlaceLatitude());
		dataContent.setPlaceLongitude(dataContentDto.getPlaceLongitude());
		dataContent.setPlaceimg(dataContentDto.getPlace_img());
		dataContent.setAreaCode(dataContentDto.getArea_code());
		dataContent.setSigunguCode(dataContentDto.getSigungu_code());
		dataContent.setContentId(dataContentDto.getContent_id());
		dataContent.setContentType(dataContentDto.getContent_type());
		
		return dataContent;
	}
	
	
	
	
	
}
