package com.share.co.kcl.security.demo.repository;

import com.share.co.kcl.security.demo.model.entity.MockUser;
import com.share.co.kcl.security.demo.model.security.JwtUserGrantedAuthority;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class MockUserAuthenticationRepository {

    private static final AtomicInteger userPrimaryId = new AtomicInteger(1);
    private static final Map<Integer, MockUser> mockRepository = new ConcurrentHashMap<>();

    public MockUser get(Integer userId) {
        return mockRepository.get(userId);
    }

    public MockUser get(String username) {
        Optional<MockUser> user = mockRepository.values().stream()
                .filter(item -> StringUtils.equals(username, item.getUsername()))
                .findFirst();
        return user.orElse(null);
    }

    public void add(String username, String password, List<JwtUserGrantedAuthority> authorities) {
        boolean isExistUsername = mockRepository.values().stream()
                .anyMatch(item -> StringUtils.equals(username, item.getUsername()));
        if (isExistUsername) {
            throw new IllegalArgumentException();
        }

        MockUser user = new MockUser();
        user.setUserId(userPrimaryId.getAndIncrement());
        user.setUsername(username);
        user.setPassword(password);
        user.setAuthorities(authorities);
        mockRepository.put(user.getUserId(), user);
    }

    public void delete(String username) {
        Optional<MockUser> user = mockRepository.values().stream()
                .filter(item -> StringUtils.equals(username, item.getUsername()))
                .findFirst();
        if (!user.isPresent()) {
            throw new IllegalArgumentException();
        }
        mockRepository.remove(user.get().getUserId());
    }


}
