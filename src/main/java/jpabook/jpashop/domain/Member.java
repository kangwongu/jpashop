package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String name;

    // 임베디드 타입
    @Embedded
    private Address address;

    // Order와 양방향 매핑
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> order = new ArrayList<>();
}
