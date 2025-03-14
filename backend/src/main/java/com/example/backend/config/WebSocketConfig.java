package com.example.backend.config;

import com.example.backend.service.JwtUtilService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private JwtUtilService jwtUtilService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String allowedOrigin = activeProfile.equals("dev") ? "http://localhost:1234" : "https://aaaa418.com";

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigin)
                .withSockJS()
                .setSuppressCors(false);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/chat");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtAuthInterceptor(jwtUtilService, userDetailsService));
    }

    private static class JwtAuthInterceptor implements ChannelInterceptor {
        private final JwtUtilService jwtUtilService;
        private final UserDetailsService userDetailsService;

        public JwtAuthInterceptor(JwtUtilService jwtUtilService, UserDetailsService userDetailsService) {
            this.jwtUtilService = jwtUtilService;
            this.userDetailsService = userDetailsService;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            System.out.println("received ws message");

            if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                System.out.println("processing ws connection request");
                List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");

                if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
                    System.out.println("missing Authorization header");
                    return message;
                }

                String bearerToken = authorizationHeaders.get(0);
                if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                    System.out.println("invalid Authorization header format");
                    return message;
                }

                String token = bearerToken.substring(7);
                System.out.println("got token: " + token);

                try {
                    // check if token expired
                    if (jwtUtilService.isExpired(token)) {
                        throw new RuntimeException("token expired");
                    }
                    System.out.println("got token extra claims info here:");
                    System.out.println(jwtUtilService.extractClaims(token));

                    // get email from JWT
                    System.out.println("email: " + jwtUtilService.getEmail(token));
                    String email = jwtUtilService.getEmail(token);

                    if (email == null) {
                        throw new RuntimeException("email extraction failed");
                    }

                    // get user details
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (!jwtUtilService.isValid(token, userDetails)) {
                        throw new RuntimeException("invalid JWT token");
                    }

                    // set up authentication in security context
                    System.out.println("authenticating user: " + email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(() -> jwtUtilService.extractClaims(token).get("username")); // use username as principal name

                    System.out.println("user authenticated: " + accessor.getUser().getName());
                } catch (Exception e) {
                    System.err.println("ws auth failed: " + e.getMessage());
                    e.printStackTrace();
                    throw new RuntimeException("invalid user: " + e.getMessage());
                }
            }

            return message;
        }
    }
}
