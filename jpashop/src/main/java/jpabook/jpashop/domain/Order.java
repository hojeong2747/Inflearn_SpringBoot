package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id") // 명명 이렇게 하는 편
    private Long id;

    // 근데 양방향 연관관계라서 연관관계의 주인을 정해줘야 함. member, order 중 jpa 는 어디 값이 변경되어 있을 때 값을 변경할 것인가 ?
    // fk 값을 누가 업데이트 해야 하나 ? 둘 중 하나만 선택하게 jpa 에서 약속해놓음. 객체는 변경 포인트가 2군데인데, 테이블은 fk 하나만 변경하면 되기 때문에 쉽게 맞춘 것 !
    // 둘 중 하나를 주인의 개념으로 잡으면 됨 = 연관관계 주인. Member/orders 나 Order/member 값 중 이 값이 변경 되었을 때 값을 바꾸겠다 !
    // fk 가 가까운 곳으로 하면 됨. Orders 에 fk 가 있다. Order/member 를 바꾸면 된다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id") // fk 이름
    private Member member;

    // orderItemA, B, C 각각 있으면 각각 persist 하고 order 에 대해 최종적으로 persist 해야함.
    // 근데 cascade 옵션 쓰면 order 에 대해서만 persist 해도 됨.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // fk 를 여기에 둠. 그럼 여기를 연관관계의 주인으로 두면 됨.
    // 모든 엔티티는 갑 저장 하고 싶으면 각각 persist 호출해야 하는데, order persist 하면 member 까지 persist 됨.
    @OneToOne(fetch = LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // orderDate -> order_date 로 바꿔서 만들어줌.
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // enum 주문상태 - ORDER, CANCEL


    // 연관관계 메서드
    // 핵심적으로 control 하는 쪽에 적어주면 좋음. 양방향 연관관계는 연관관계 편의 메서드 적는 게 좋음.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this); // 여기 한 줄로 준다. 양방향 연관관계 다 걸린다.
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
