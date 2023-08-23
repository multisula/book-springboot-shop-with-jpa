package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
  @Autowired
  ItemRepository itemRepository;

  @Test
  @DisplayName("상품 저장 테스트")
  public void createItemTest() {
    Item item = new Item().builder()
        .itemNm("테스트 상품")
        .price(10000)
        .itemDetail("테스트 상품 상세 설명")
        .itemSellStatus(ItemSellStatus.SELL)
        .stockNumber(100)
        .regTime(now())
        .updateTime(now())
        .build();

    Item savedItem = itemRepository.save(item);
    System.out.println(savedItem);

  }

}