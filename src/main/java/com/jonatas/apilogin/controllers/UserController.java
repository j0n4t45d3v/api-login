package com.jonatas.apilogin.controllers;

import com.jonatas.apilogin.entities.User;
import com.jonatas.apilogin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private final UserRepository repository;
  private final PasswordEncoder crypto;

  public UserController(UserRepository repository, PasswordEncoder crypto) {
    this.repository = repository;
    this.crypto = crypto;
  }

  @GetMapping
  public ResponseEntity<List<User>> findAllUser() {
    return ResponseEntity.ok(repository.findAll());
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<Optional<User>> findById(@PathVariable(value = "id") Long id) {
    return ResponseEntity.ok(repository.findById(id));
  }

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User u) {
    u.setPassword(crypto.encode(u.getPassword()));
    return ResponseEntity.ok(repository.save(u));
  }

  @GetMapping(value = "/validaUser ")
  public ResponseEntity<Boolean> validaSenha(@RequestParam String login, @RequestParam String password) {

    Optional<User> optionalUser = repository.findByLogin(login);

    if (optionalUser.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    User usuario = optionalUser.get();
    boolean valid = crypto.matches(password, usuario.getPassword());

    HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
    return ResponseEntity.status(status).body(valid);
  }

}
