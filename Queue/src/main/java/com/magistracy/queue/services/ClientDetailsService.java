/*
package com.magistracy.queue.services;

import com.magistracy.queue.entities.Client;
import com.magistracy.queue.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service  // Додана анотація @Service
public class ClientDetailsService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Для шифрування фіктивного пароля

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Optional<Client> client = clientRepository.findByPhoneNumber(phoneNumber);
        if (client.isEmpty()) {
            throw new UsernameNotFoundException("Клієнт не знайдений");
        }

        Client cl = client.get();
        return User.builder()
                .username(cl.getPhoneNumber())
                .password(passwordEncoder.encode("dummy"))  // Вже зашифрований пароль
                .roles("CLIENT")
                .build();
    }
}
*/
