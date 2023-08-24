package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
@Slf4j
class OrderTest {
  @Autowired
  OrderRepository orderRepository;

  @Autowired
  ItemRepository itemRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  OrderItemRepository orderItemRepository;

  @PersistenceContext
  EntityManager em;

  public Item createItem() {
    return new Item().builder()
        .itemNm("테스트 상품")
        .price(10000)
        .itemDetail("상세 설명")
        .itemSellStatus(ItemSellStatus.SELL)
        .stockNumber(100)
        .regTime(LocalDateTime.now())
        .updateTime(LocalDateTime.now())
        .build();
  }

  public Order createOrder() {
    Member member = new Member();
    memberRepository.save(member);
    Order order = new Order(member);

    IntStream.range(0, 3).forEach(i -> {
      Item item = createItem();
      itemRepository.save(item);
      OrderItem orderItem = new OrderItem();
      orderItem.setItem(item);
      orderItem.setCount(10);
      orderItem.setOrderPrice(1000);
      orderItem.setOrder(order);
      order.getOrderItems().add(orderItem);
    });

    orderRepository.save(order);
    return order;
  }

  @Test
  @DisplayName("영속성 전이 테스트")
  public void cascadeTest() {
    Order order = createOrder();

    orderRepository.saveAndFlush(order);
    em.clear();

    Order savedOrder = orderRepository.findById(order.getId())
        .orElseThrow(EntityNotFoundException::new);
    assertThat(savedOrder.getOrderItems().size()).isEqualTo(3);
  }

  @Test
  @DisplayName("고아객체 제거 테스트")
  public void orphanRemovalTest() {
    Order order = createOrder();
    order.getOrderItems().remove(0);
    em.flush();

    assertThat(orderRepository.findById(order.getId()).get().getOrderItems().size()).isEqualTo(2);
  }

  @Test
  @DisplayName("지연 로딩 테스트")
  public void lazyLoadingTest() {
    Order order = createOrder();
    Long orderItemId = order.getOrderItems().get(0).getId();
    em.flush();
    em.clear();

    OrderItem orderItem = orderItemRepository.findById(orderItemId)
        .orElseThrow(EntityNotFoundException::new);
    log.info("Order class: {}", orderItem.getOrder().getClass());
  }
}