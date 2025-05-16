package br.com.challenge6.services;

import br.com.challenge6.domain.user.User;
import br.com.challenge6.domain.user.UserDTO;
import br.com.challenge6.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;

    public UserDTO createNewUser(UserDTO dto){
        var user = mapper.map(dto, User.class);
        userRepository.save(user);

        return mapper.map(user, UserDTO.class);
    }
}
