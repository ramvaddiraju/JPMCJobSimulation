package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class FetchBalanceByID {
    private final UserRepository userRepository;

    public FetchBalanceByID(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/balance")
    public Balance fetchBalanceById(@RequestParam() Long userId)
    {
        Optional<UserRecord> username = userRepository.findById(userId);
        if(username.isEmpty())
        {
            return new Balance(0);
        }
        else
        {
            UserRecord user = username.get();
            return new Balance(user.getBalance());
        }
    }
}
