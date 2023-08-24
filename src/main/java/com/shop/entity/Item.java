package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="item")
@Entity
public class Item extends BaseEntity {
  @Id
  @Column(name = "item_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;  // 상품 코드

  @Column(nullable = false, length = 50)
  private String itemNm;  // 상품명

  @Column(name = "price", nullable = false)
  private int price;  // 가격

  @Column(nullable = false)
  private int stockNumber;  // 재고수량

  @Lob
  @Column(nullable = false)
  private String itemDetail;  // 상품 상세 설명

  @Enumerated(EnumType.STRING)
  private ItemSellStatus itemSellStatus;  // 상품 판매 상태

  @Builder
  public Item(String itemNm, int price, String itemDetail, ItemSellStatus itemSellStatus, int stockNumber, LocalDateTime regTime, LocalDateTime updateTime) {
    this.itemNm = itemNm;
    this.price = price;
    this.stockNumber = stockNumber;
    this.itemDetail = itemDetail;
    this.itemSellStatus = itemSellStatus;
  }
}
