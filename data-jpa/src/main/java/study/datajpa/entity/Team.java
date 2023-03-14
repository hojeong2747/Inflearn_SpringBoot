package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") // member, team 둘 다 세팅을 걸면 한 쪽에는 mappedBy 해줌. foreign key 없는 '일' 쪽에 걸어주는 게 좋음.
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}