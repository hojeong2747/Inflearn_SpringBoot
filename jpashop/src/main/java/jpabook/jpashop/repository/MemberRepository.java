package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

//    @PersistenceContext
    private final EntityManager em; // final 과 @RequiredArgsConstructor 조합으로 쓰기 (일관성 있게)

    public void save(Member member) {
        em.persist(member); // jpa 가 제공하는 메서드 사용
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); // jpa 가 제공하는 메서드 사용
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();

        // jpql 은 sql 과 기능적으로는 동일한데, 차이가 있음.
        // sql 은 테이블 대상으로 쿼리, jpql 은 엔티티 객체에 대한 쿼리이다.
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
