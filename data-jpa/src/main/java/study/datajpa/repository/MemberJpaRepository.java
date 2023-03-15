package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    // 전체 조회 -> jpa 가 제공하는 JPQL 기술 사용해야 함. Member 가 엔티티. 테이블 대상이 아니라 객체를 대상으로 하고 모양은 sql 과 똑같음.
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList(); // JPQL, 반환 타입
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id); // id 로 Member 조회
        return Optional.ofNullable(member); // member 가 null 일 수도 있다는 것을 Optional 타입으로 한 번 감싸서 밖에 제공. java 8 기능
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult(); // single 이면 하나만 반환
    }

    // 단건 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 쿼리 메소드 기능 확인 전 순수 jpa 로 검색 조건 들어간 조회
    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
    // m.username 잘못 입력하면, 고객이 이거 누르는 순간 오류가 발생한다. 문법 오류인지 아닌지 파싱이 안 돼서 모른다.

    // JPA NamedQuery -> 구현하기 귀찮다!
    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }
}