package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    // 구현체가 없는데 어떻게 이 기능이 동작하지? 주입의 정체는? -> 찍어서 확인

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        // 인터페이스를 보고 스프링 데이터 jpa 가 구현 클래스를 만들어서 꽂아준 것! 구현체는 알아서 다 만들어서 주입해준다.

        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();
        // get 없으면 값이 원래 있을 수 있고 없기 때문에 optional 타입. 지금은 값이 있다 생각하고 예외 처리 생략!

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }


    // 스프링 데이터 jpa 로 테스트
    @Test
    public void basicCRUD() {
        Member member1 = new Member(("member1"));
        Member member2 = new Member(("member2"));
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // 단건 조회 검증
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

    }

    @Test
    public void findMemberDto() {

        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    // 스프링 데이터 JPA 는 유연한 반환 타입 지원
    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findListByUsername("AAA");
        System.out.println("aaa = " + result);
        // 여기서 파라미터로 없는 값을 넣어도, NULL 이 아니라 empty collection 이 반환되고, 출력하면 0 이 나온다ㅣ.

        Member findMember = memberRepository.findMemberByUsername("AAA"); // 이름 유니크하면 굳이 List 로 받을 필요 없음.
        System.out.println("findMember = " + findMember);
        // 여기서 파라미터로 없는 값을 넣으면, 결과가 매칭이 안된다 -> 결과가 NULL !
        // jpa 는 하나를 getSingleResult 라고 하는데 매칭 결과가 없으면, NoResultException 이 뜬다.
        // 스프링 데이터 jpa 는 알아서 예외를 try catch 로 잡아서 NULL 로 반환한다.

        // 실무 입장에서는 이게 낫긴 한데 더 좋아진 게 있다. 자바 8 이 나오고 나서는 Optional 로 반환하면 된다. 없을 수도 있다는 가정 하에 클라이언트에 처리를 위임함.
        // 빈 값이면 Optional.empty 라고 찍힌다.

        Optional<Member> memberOptional = memberRepository.findOptionalByUsername("AAA");
        System.out.println("memberOptional = " + memberOptional.get());

        // 결론은 DB 에 데이터가 있을 수도 있고 없을 수도 있으면 그냥 Optional 쓰는 게 맞다.
        // 근데 결과가 2개 이상이면? 단건 조회였는데 Optional 이든 아니든, 이 경우엔 IncorrectResultSizeData 어쩌구 예외 발생함.
        // NonUniqueResultException 이 터지면, 스프링 데이터 JPA 가 스프링 예외로 변환해서 반환한다.
        // 클라이언트 코드는 스프링이 추상화한 예외에 의존하면, DB 바뀌어도 클 코 바꿀 필요 없다.
    }

    // 스프링 데이터 JPA 페이징 쿼리 테스트
    @Test
    public void paging() {
        // given 이런 데이터가 있을 때
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        // 0 페이지부터 시작. 0 부터 3 페이지 가져와. Sorting 조건. 이름으로 내림차순 정렬.
        // sorting 조건이 너무 복잡하면 안 돌아가니까, 인터페이스 상에서 @Query 속에 쿼리 작성한다.

        // when 이렇게 하면

        // 특별한 반환 타입 1. Page
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // 단순히 앞에서 3개 받고 싶으면 -> findTop3ByAge(age) 하고 페이징 안 넘겨도 되긴 함.
//        long totalCount = memberRepository.totalCount(age);
//        -> 반환타입을 Page 라고 받으면, totalCount 쿼리를 알아서 같이 날린다!

        // 이걸 api 에서 그대로 반환하면 안 됨. 엔티티 노출 x, DTO 로 변환해서 반환하기!
        // DTO 로 변환 쉽게 하는 법
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        // 이건 반환해도 된다. 굉장히 유용하게 쓰임 **


        // 특별한 반환 타입 2. Slice
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        // 0번째부터 3개 가져오라고 하면 요청할 때 3개가 아니라 limit + 1 = 4개 요청한다. 그래서 전체 카운트를 가져오지 않는다.
        // Slice 는 totalCount 쿼리 안 날려서** totalElements, totalPages 다 모른다.


        // 특별한 반환 타입 3. List
//        List<Member> page = memberRepository.findByAge(age, pageRequest);
        // 페이징 쿼리는 0페이지부터 딱 3개만 찍고 가져오고 싶고 그 다음 있든 말든 상관 없어. 딱 데이터 몇 개 끊어서 가져올 때 -> 당연히 밑 메소드들 작동 안 함.



        // then
        List<Member> content = page.getContent(); // page 에서 실제 내부에 있는 데이터 3개를 꺼내고 싶으면 getContent() 로 가져옴.
        long totalElements = page.getTotalElements();// totalCount 와 같음.

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
//
//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

    }


    // 회원의 나이를 한 번에 변경하는 쿼리
    // 스프링 데이터 JPA 를 사용한 벌크성 수정 쿼리
    @Test
    public void bulkUpdate() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20); // 20 이상인 member 3개 나옴.
//        em.flush(); // 남아있는 내용 DB 반영, 근데 사실 먼저 쿼리 보내고 = flush 하고 JPQL 실행 됨.
//        em.clear(); // *여기서* 벌크 연산 이후에는 영속성 컨텍스트를 다 날린다! (근데 또 스프링 데이터 JPA 가 지원하는 게 있다 -> @Modifying 옵션)

        // 이렇게까지 설명하면 쉬운데 한 가지 문제가 있다. 순수이든 스프링 데이터든 JPA 를 쓰는 건데 JPA 에서 벌크성 업데이트는 조심해야 한다.
        // JPA 는 영속성 컨텍스트가 있어서 엔티티 관리가 돼야 하는데, 벌크 연산은 다 무시하고 DB 에 다 때려버린다! (영속성 컨텍스트는 모르고 있음 *)
        // -> 빵 때려버리면 서로 안 맞고 문제가 될 수 있음.

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        // 벌크 연산이라 지금 JPA 영속성 컨텍스트에는 40살이라고 남아있는데 DB 에는 41살로 반영이 된 것이다.
        System.out.println("member5 = " + member5); // 40 으로 찍힌다! 이게 벌크 연산에서 조심해야 할 점임.

        // 그럼 어떻게 해? -> 벌크 연산 이후에는 영속성 컨텍스트를 다 날린다!


        // then
        assertThat(resultCount).isEqualTo(3);


    }


    // @EntityGraph 이해하기 위해 fetch join 명확히 이해하기
    @Test
    public void findMemberLazy() {

        // given
        // member1 -> teamA
        // member2 -> temaB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 여기까지 하면 위처럼 연관관계 세팅 완료
        // Member Team 은 다대일인데, fetch type 을 LAZY 로 설정했음. -> Member 만 조회하면 Team 은 조회 안 하고 가짜로 갖고 있다가 실제 Team 을 조회할 때 쿼리를 날림.

        em.flush();
        em.clear(); // DB 에 남은 쿼리 다 날리고 영속성 컨텍스트 모두 CLEAR.

        // when (N + 1 문제 : 추가 쿼리가 N 번 -> 1 + N 문제라고 하는 게 이해가 더 잘 되기도.) -> fetch join 으로 해결함!
        // select Member (1)
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName()); // member.getTeam() 까지는 쿼리 안 날리는데(가짜 객체 가져옴). 근데 getName() 으로 실제 가져오면 문제.
        }
    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);

        // given
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); // 변경 안 된다고 가정하고 다 무시하고 진행
        findMember.setUsername("member2"); // 변경되면

        em.flush(); // 변경 감지 기능 작동해서 DB 에 업데이트 쿼리가 나감!
        // 치명적인 단점은? 원본이 있어야 한다! 데이터 2개를 갖고 있어야 해서 비효율적..
        // 근데 난 변경 목적이 아니라 조회만 하고 끝이라면? 근데도 갖고 오는 순간 이미 원본과 별개로 만들어둔다..
        // 그래서 100% 조회 목적으로만 사용한다면 Hibernate 가 힌트를 준다.

    }

    @Test
    public void queryLock() {
        Member member1 = new Member("member1", 10);

        // given
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }
    // 사실 쓸 내용이 많지는 않을 것이다.
    // 실시간 트래픽이 많은 상황이라면 lock 을 걸면 안 된다.

}