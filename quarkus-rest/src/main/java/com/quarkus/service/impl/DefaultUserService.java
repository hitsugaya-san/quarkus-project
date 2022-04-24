package com.quarkus.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarkus.entity.User;
import com.quarkus.exception.UserNotFoundException;
import com.quarkus.repository.UserRepository;
import com.quarkus.service.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Inject
    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Inject
    HttpService httpService;

    @Override
    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findByIdOptional(id).orElseThrow(() -> new UserNotFoundException("There user doesn't exist"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.listAll();
    }

    @Transactional
    @Override
    public User updateUser(long id, User user) throws UserNotFoundException {
        User existingUser = getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        userRepository.persist(existingUser);
        return existingUser;
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        userRepository.persistAndFlush(user);
        return user;
    }

    @Transactional
    @Override
    public void deleteUser(long id) throws UserNotFoundException {
        userRepository.delete(getUserById(id));
    }

    public Map<String, Object> getDataGempa() throws IOException, InterruptedException, ParseException {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> object = httpService.fetchPosts("https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> Infogempa = objectMapper.convertValue(object.get("Infogempa"), Map.class);
        Map<String, Object> gempa = objectMapper.convertValue(Infogempa.get("gempa"), Map.class);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");//2022-04-24T11:03:56
        Date parsedDate = dateFormat.parse(gempa.get("DateTime").toString().substring(0,19));
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

        result.put("Tanggal", gempa.get("Tanggal"));
        result.put("Jam", gempa.get("Jam"));
        result.put("DateTime", timestamp.toString());
        result.put("Coordinate", gempa.get("Coordinates"));
        result.put("Potensi", gempa.get("Potensi"));

        return result;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.
                newBuilder().version(HttpClient.Version.HTTP_2).build();

        HttpRequest request = HttpRequest.newBuilder().GET().
                uri(URI.create("https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json")).header("Accept",
                "application/json").build();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.
                        BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> object = objectMapper.readValue(response.body(), Map.class);
        Map<String, Object> object2 = objectMapper.convertValue(object.get("Infogempa"), Map.class);
        System.out.println("response : "+object2.get("gempa"));
    }
}
