package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Slf4j
class ItemRepositoryTest {
  @Autowired
  ItemRepository itemRepository;

  @PersistenceContext
  EntityManager em;
  JPAQueryFactory queryFactory;

  public void createItemList(int start, int closedEnd, ItemSellStatus itemSellStatus) {
    IntStream.rangeClosed(start, closedEnd).forEach(i -> itemRepository.save(new Item().builder()
            .itemNm("테스트 상품" + i)
            .price(10000 + i)
            .itemDetail("테스트 상품 상세 설명" + i)
            .itemSellStatus(itemSellStatus)
            .stockNumber(100)
            .regTime(LocalDateTime.now())
            .updateTime(LocalDateTime.now())
        .build()));
  }

  @BeforeEach
  public void beforeEach() {
    createItemList(1, 5, ItemSellStatus.SELL);
    createItemList(6, 10, ItemSellStatus.SOLD_OUT);
    queryFactory = new JPAQueryFactory(em);
  }

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
    assertThat(savedItem).isEqualTo(item);
  }

  @Test
  @DisplayName("상품명 조회 테스트")
  public void findByItemNmTest() {
    Optional<Item> findItem = itemRepository.findByItemNm("테스트 상품1");

    assertThat(findItem).isNotEmpty();
    assertThat(findItem.get().getItemNm()).isEqualTo("테스트 상품1");
//    log.info("findItem.getItemNm(): {}", findItem.get().getItemNm());
  }

  @Test
  @DisplayName("상품명, 상품상세설명 or 테스트")
  public void findByItemNmOrItemDetailTest() {
    List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
    itemList.stream().forEach(item -> {
      assertThat(item).satisfiesAnyOf(
          i -> assertThat(i.getItemNm()).isEqualTo("테스트 상품1"),
          i -> assertThat(i.getItemDetail()).isEqualTo("테스트 상품 상세 설명5")
      );
      log.info("상품명: {}, 상품 상세 설명: {}", item.getItemNm(), item.getItemDetail());
    });
  }

  @Test
  @DisplayName("가격 LessThan 테스트")
  public void findByPriceLessThanTest() {
    List<Item> itemList = itemRepository.findByPriceLessThan(10005);
    itemList.stream().forEach(item -> {
      assertThat(item.getPrice()).isLessThan(10005);
      log.info("상품명: {}, 삼품 가격: {}", item.getItemNm(), item.getPrice());
    });
  }

  @Test
  @DisplayName("가격 내림차순 조회 테스트")
  public void findByPriceLessThanOrderByPriceDesc() {
    List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
    itemList.stream().forEach(item -> {
      assertThat(item.getPrice()).isLessThan(10005);
      log.info("상품명: {}, 삼품 가격: {}", item.getItemNm(), item.getPrice());
    });
  }

  @Test
  @DisplayName("가격 내림차순 조회 테스트")
  public void findByItemDetail() {
    List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
    itemList.stream().forEach(item -> {
      assertThat(item.getItemDetail()).contains("테스트 상품 상세 설명");
      log.info("상품명: {}, 삼품 상세 설명: {}", item.getItemNm(), item.getItemDetail());
    });
  }

  @Test
  @DisplayName("가격 내림차순 조회 테스트")
  public void findByItemDetailByNative() {
    List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
    itemList.stream().forEach(item -> {
      assertThat(item.getItemDetail()).contains("테스트 상품 상세 설명");
      log.info("상품명: {}, 삼품 상세 설명: {}", item.getItemNm(), item.getItemDetail());
    });
  }

  @Test
  @DisplayName("QueryDSL 조회 테스트1")
  public void queryDslTest() {
    QItem qItem = QItem.item;
    JPAQuery<Item> query = queryFactory.selectFrom(qItem)
        .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
        .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
        .orderBy(qItem.price.desc());

    List<Item> itemList = query.fetch();

    itemList.stream().forEach(item -> {
      assertThat(item.getItemDetail()).contains("테스트 상품 상세 설명");
      log.info("상품명: {}, 상품 상세 설명: {}", item.getItemNm(), item.getItemDetail());
    });
  }

  @Test
  @DisplayName("상품 QueryDSL 조회 테스트2")
  public void queryDslTest2() {
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    QItem item = QItem.item;
    String itemDtail = "테스트 상품 상세 설명";
    int price = 10003;
    String itemSellStat = "SELL";

    booleanBuilder.and(item.itemDetail.like("%" + itemDtail + "%"));
    booleanBuilder.and(item.price.gt(price));
    if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
      booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
    }

    Pageable pageable = PageRequest.of(0, 5);
    Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
    List<Item> itemList = itemPagingResult.getContent();

    itemList.stream().forEach(i -> {
      assertThat(i.getItemDetail()).contains("테스트 상품 상세 설명");
      log.info("상품명: {}, 상품 상세 설명: {}", i.getItemNm(), i.getItemDetail());
    });
    log.info("total elements: {}", itemPagingResult.getTotalElements());
  }
}