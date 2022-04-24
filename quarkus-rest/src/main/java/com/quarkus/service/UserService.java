package com.quarkus.service;

import com.quarkus.entity.User;
import com.quarkus.exception.UserNotFoundException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface UserService {

    User getUserById(long id) throws UserNotFoundException;

    List<User> getAllUsers();

    User updateUser(long id, User user) throws UserNotFoundException;

    User saveUser(User user);

    void deleteUser(long id) throws UserNotFoundException;

    Map<String, Object> getDataGempa() throws IOException, InterruptedException, ParseException;
}
