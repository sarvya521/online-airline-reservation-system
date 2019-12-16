package com.oars.service.impl;

import com.oars.constant.Role;
import com.oars.dao.UserRepository;
import com.oars.dto.UserDto;
import com.oars.entity.User;
import com.oars.modelmapper.UserMapper;
import com.oars.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final TransactionTemplate transactionTemplate;

    public UserServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<UserDto> getAllCustomers() {
        List<User> users = userRepository.findByRole(Role.CUSTOMER.name());
        return users.stream()
                .map(user -> userMapper.convertToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getAllAgents() {
        List<User> users = userRepository.findByRole(Role.CUSTOMER_REPRESENTATIVE.name());
        return users.stream()
                .map(user -> userMapper.convertToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto checkLogin(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return null;
        }
        User user = userOptional.get();
        return userMapper.convertToDto(user);
    }

    @Override
    @Transactional
    public boolean checkIfUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.convertToEntity(userDto);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                User userPersisted = userRepository.saveAndFlush(user);
                userDto.setId(userPersisted.getId());
            }
        });
        return userDto;
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        Long id = userDto.getId();
        User user = userRepository.findById(id).get();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                userRepository.saveAndFlush(user);
            }
        });
        return userDto;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).get();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                userRepository.delete(user);
            }
        });
    }
}
