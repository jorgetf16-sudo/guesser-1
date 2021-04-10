package rmartin.ctf.guesser;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class SuperSecureTokenManager {

    private static final Logger log = Logger.getLogger(SuperSecureTokenManager.class.getName());

    /**
     * Each client has its own state, CTF participants should be isolated
     */
    Map<String, ClientTokenState> clients = new ConcurrentHashMap<>();

    public Iterable<Integer> getUsedTokens(String clientIP){
        var usedTokens = clients.get(clientIP).usedTokens;
        log.info(String.format("Returning used tokens for client %s --> %s", clientIP, usedTokens));
        return usedTokens;
    }

    public boolean isValidToken(String clientIP, int token){
        clients.computeIfAbsent(clientIP, ClientTokenState::new);
        boolean result = clients.get(clientIP).isValidToken(token);
        log.info(String.format("Validating token for client %s got %s --> %s", clientIP, token, result));
        return result;
    }

    public void resetState(String clientIP){
        log.info(String.format("Resetting state for client %s", clientIP));
        clients.put(clientIP, new ClientTokenState(clientIP));
    }

    private static class ClientTokenState {
        final String clientIP;
        final Queue<Integer> usedTokens;
        // Each token is a random number in range [0, 1_000_000_000)
        int currentToken;
        Random random;

        ClientTokenState(String clientIP) {
            this.clientIP = clientIP;
            this.usedTokens = new ConcurrentLinkedQueue<>();
            this.random = new Random(System.currentTimeMillis());
            this.currentToken = random.nextInt(1_000_000_000);
        }

        boolean isValidToken(int token){
            boolean check = token == currentToken;
            usedTokens.add(currentToken);
            currentToken = random.nextInt(1_000_000_000);
            return check;
        }
    }
}
