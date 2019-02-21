package com.uniovi.services;

import com.uniovi.entities.User;
import com.uniovi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
	private final UsersRepository usersRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostConstruct
	public void init() {
	}

	public List<User> getUsers() {
		List<User> users = new ArrayList<>();
		usersRepository.findAll().forEach(users::add);
		return users;
	}

	public User getUser(Long id) {
		Optional<User> optional = usersRepository.findById(id);

		return optional.orElse(null);
	}

	public void addUser(User user) {
		if(user.getPassword() != null) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		usersRepository.save(user);
	}

	public User getUserByDni(String dni) {
		return usersRepository.findByDni(dni);
	}


	public void deleteUser(Long id) {
		usersRepository.deleteById(id);
	}
}
