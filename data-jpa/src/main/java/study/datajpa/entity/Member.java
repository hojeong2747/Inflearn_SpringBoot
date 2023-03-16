package study.datajpa.entity;

import lombok.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자
@ToString(of = {"id", "username", "age"}) // 출력 위함. 연관 관계 있는 team 은 빼고!
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
) // JPA NamedQuery -> 실무에서 거의 안 쓴다.
// m.username 잘못 치면? 앱 로딩 시점에 파싱한다. JPQL -> SQL 과정을 거치면서 버그를 잡을 수 있다! 오류 잡을 수 있는 게 NamedQuery 의 가장 큰 장점이다!
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) // 잘 안 쓰긴 하는데 소개
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) // member 와 team 은 다대일, jpa 모든 연관관계는 모두 지연 로딩 세팅! 즉시로딩이면 성능 최적화 어려움.
    @JoinColumn(name = "team_id") // foreign key 명. '다' 쪽에 들어감
    private Team team;

    // 지연로딩 : member 조회할 때는 딱 member 만 조회하는 것. team 은 가짜 객체로 갖고 있다가 값을 실제 사용할 때나 볼 때 그때 DB 에서 쿼리로 해온다.

//    // entity 는 기본 생성자 하나 있어야 하고 protected 까지 열어놔야 함. -> lombok
//    protected Member() {
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public void changeTeam(Team team) {
        this.team = team; // member team 변경
        team.getMembers().add(this); // team member 도 변경!
    }
}