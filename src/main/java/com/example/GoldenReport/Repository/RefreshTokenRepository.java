package com.example.GoldenReport.Repository;

import com.example.GoldenReport.Domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    List<RefreshToken> findByToken(String token);
}
