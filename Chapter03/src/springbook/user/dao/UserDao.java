package springbook.user.dao;

import springbook.user.domain.User;

import java.util.List;

public interface UserDao {
    void addUser(User user);
    User getUser(String id);
    List<User> getAllUsers();
    void deleteAll();
    int getCount();
}
