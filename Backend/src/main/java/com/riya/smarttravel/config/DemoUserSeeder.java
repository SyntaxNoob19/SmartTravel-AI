package com.riya.smarttravel.config;

import com.riya.smarttravel.entity.UserAccount;
import com.riya.smarttravel.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoUserSeeder implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoUserSeeder(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String demoEmail = "demo@smarttravel.com";
        if (!userAccountRepository.existsByEmailIgnoreCase(demoEmail)) {
            UserAccount demoUser = new UserAccount();
            demoUser.setName("Demo User");
            demoUser.setEmail(demoEmail);
            demoUser.setPasswordHash(passwordEncoder.encode("Demo@1234"));
            userAccountRepository.save(demoUser);
            System.out.println("=========================================");
            System.out.println("Seeded default demo user: " + demoEmail);
            System.out.println("=========================================");
        }
    }
}
