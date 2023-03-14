package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {

    @PersistenceContext // jpa 의 entityManager 를 injection 해주는 인젝션.
    private EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class).getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class).getSingleResult();
    }

    // update 는 안 만들어도 됨. 자바 컬렉션. 자바 순수 객체 세상과 동일한 방식으로 객체 업뎃을 할 수 있다.
    // jpa 에서 변경은 변경감지로 변경됨! 변경만 하면 됨.
}