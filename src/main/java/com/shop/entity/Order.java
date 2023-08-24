package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  private LocalDateTime orderDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
  orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  public Order(Member member){
    this.member = member;
  }
}
