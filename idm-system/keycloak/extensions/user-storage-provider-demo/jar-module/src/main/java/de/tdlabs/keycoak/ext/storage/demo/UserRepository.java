package de.tdlabs.keycoak.ext.storage.demo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class UserRepository {

  private List<User> users;

  public UserRepository() {
    users = Arrays.asList(
      new User("1", "Katie", "Washington"),
      new User("2", "Enrique", "Perkins"),
      new User("3", "Joshua", "Little"),
      new User("4", "Billie", "Newman"),
      new User("5", "Leslie", "Thompson")
    );
  }

  public List<User> getAllUsers() {
    return users;
  }

  public int getUsersCount() {
    return users.size();
  }

  public User findUserById(String id) {
    return users.stream().filter(user -> user.getId().equals(id)).findFirst().get();
  }

  public User findUserByUsernameOrEmail(String username) {
    return users.stream()
      .filter(user -> user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username))
      .findFirst().get();
  }

  public List<User> findUsers(String query) {
    return users.stream()
      .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
      .collect(Collectors.toList());
  }

  public boolean validateCredentials(String username, String password) {
    return findUserByUsernameOrEmail(username).getPassword().equals(password);
  }

  public boolean updateCredentials(String username, String password) {
    findUserByUsernameOrEmail(username).setPassword(password);
    return true;
  }

}
