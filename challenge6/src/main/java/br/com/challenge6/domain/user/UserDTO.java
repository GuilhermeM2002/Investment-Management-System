package br.com.challenge6.domain.user;

public record UserDTO(
        Long id,
        String name,
        String email,
        String password)
{}
