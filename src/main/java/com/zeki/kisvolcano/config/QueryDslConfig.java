package com.zeki.kisvolcano.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    private final EntityManagerFactory entityManagerFactory;

    public QueryDslConfig(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return new JPAQueryFactory(entityManager);
    }

}
