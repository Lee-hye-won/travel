package com.travel.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tourist_img")
public class TouristImg extends BaseEntity{

	@Id
	@Column(name = "item_img_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String imgName; // 이미지 이름
	
	private String oriImgName; //원본 이미지 
	
	private String imgUrl; //이미지 조회 경로
	
	private String repimgYn; // 대표이미지 여부
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tourist_id")
	private Tourist tourist;
	
}
