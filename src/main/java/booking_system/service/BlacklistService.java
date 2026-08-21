package booking_system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BlacklistService {

    private final Set<String> blackListedTokens = ConcurrentHashMap.newKeySet();

    public void blackList(String token) {
        blackListedTokens.add(token);
    }

    public boolean isBlackListed(String token) {
        return blackListedTokens.contains(token);
    }

}
