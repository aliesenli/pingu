package ch.pingu.backend.users.service;

import ch.pingu.backend.users.model.UserInfo;
import ch.pingu.backend.users.repository.UserInfoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserInfoRepository repository;

    public UserService(UserInfoRepository repository) {
        this.repository = repository;
    }

    public List<UserInfo> listAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    public Optional<UserInfo> findById(String id) {
        return repository.findById(id);
    }

    public Optional<UserInfo> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
