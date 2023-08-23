package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="item")
@Entity
public class Item {
  @Id
  @Column(name = "item_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;  // 상품 코드

  @Column(nullable = false, length = 50)
  private String itemNm;  // 상품명

  @Column(name = "price", nullable = false)
  private int price;  // 가격

  @Lob
  @Column(nullable = false)
  private int stockNumber;  // 재고수량

  @Enumerated(EnumType.STRING)
  private String itemDetail;  // 상품 상세 설명

  private ItemSellStatus itemSellStatus;  // 상품 판매 상태

  private LocalDateTime regTime;  // 등록 시간
  private LocalDateTime updateTime;  // 수정 시간
}
