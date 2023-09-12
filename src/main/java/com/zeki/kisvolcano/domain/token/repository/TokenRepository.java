package com.zeki.kisvolcano.domain.token.repository;

import com.zeki.kisvolcano.domain.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
}
