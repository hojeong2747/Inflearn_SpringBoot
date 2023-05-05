package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // 숫자로 들어가는데, 중간 다른 상태가 생기면 망함. ORDINAL 절대 쓰지 말고 STRING 으로 쓰기 !
    private DeliveryStatus status; // READY, CAMP
}
