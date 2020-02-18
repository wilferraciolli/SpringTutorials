package com.wiltech.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void d() {
        System.out.println(new BCryptPasswordEncoder().encode("Password123"));
    }

}
