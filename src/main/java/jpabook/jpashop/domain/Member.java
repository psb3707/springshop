package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter // Setter의 경우 가능하면 변경 지점이 명확하도록 변경을 위한 비즈니스 로직을 별도로 제공해야 함. 막 열어두면 엔티티의 데이터가 어디서 어떻게 바뀐것인지 알수 없음
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //@Embeddable 둘 중 하나만 있어도 동작
    private Address address;

    @OneToMany(mappedBy = "member") //Order 테이블에 있는 member 속성과 매핑(FK) 읽기 전용
    private List<Order> orders = new ArrayList<>(); // 컬렉션은 필드에서 바로 초기화!

}
