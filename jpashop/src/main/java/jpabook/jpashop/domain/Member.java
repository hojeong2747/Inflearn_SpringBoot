package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

//    @NotEmpty // presentation 계층을 위한 검증 로직이 엔티티에 있는 것 -> api 마다 다른 조건이 될 수 있음. & 엔티티 변경 시 api 스펙이 바뀜
    private String name;

    @Embedded
    private Address address;

    // 회원 입장에서, 한 명의 회원이 여러 개의 상품 주문 가능 -> 일대다 관계
    @OneToMany(mappedBy = "member") // Order/member 필드에 의해서 매핑 되었다. 연관관계의 거울 ! 읽기 전용.
    private List<Order> orders = new ArrayList<>();
    // nullPointerException 발생할 일 없다. 컬렉션 필드 초기화 최선의 방법임. 그리고 컬렉션은 변경하지 말기.

}
