package com.uniovi.controllers;

import com.uniovi.entities.User;
import com.uniovi.services.MarksService;
import com.uniovi.services.RolesService;
import com.uniovi.services.SecurityService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.SignUpFormValidator;
import com.uniovi.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class UsersController {
	private final UsersService usersService;
	private final MarksService marksService;
	private final SecurityService securityService;
	private final SignUpFormValidator signUpFormValidator;
	private final UserValidator userValidator;
	private final RolesService rolesService;

	@Autowired
	public UsersController(UsersService usersService, MarksService marksService, SecurityService securityService, SignUpFormValidator signUpFormValidator, UserValidator userValidator, RolesService rolesService) {
		this.usersService = usersService;
		this.marksService = marksService;
		this.securityService = securityService;
		this.signUpFormValidator = signUpFormValidator;
		this.userValidator = userValidator;
		this.rolesService = rolesService;
	}

	@RequestMapping("/user/list")
	public String getListado(Model model, Pageable pageable, Principal principal, @RequestParam(required = false) String searchText) {
		Page<User> users; // no se inicializa, se hace abajo
		if(searchText != null && !searchText.isEmpty()) {
			users = usersService.searchUsers(pageable, searchText);
		} else {
			users = usersService.getUsers(pageable);
		}
		model.addAttribute("page", users);
		model.addAttribute("usersList", users.getContent());
		return "user/list";
	}

	@RequestMapping("/user/list/update")
	public String updateList(Model model, Pageable pageable, Principal principal, @RequestParam(required = false) String searchText) {
		return getListado(model, pageable, principal, searchText) + ":: tableUsers";
	}

	@RequestMapping(value = "/user/add")
	public String getUser(Model model) {
		model.addAttribute("usersList", usersService.getUsers());
		model.addAttribute("user", new User());
		model.addAttribute("rolesList", rolesService.getRoles());
		return "user/add";
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public String setUser(@Validated User user, BindingResult result, Model model) {
		userValidator.validate(user, result);

		if(result.hasErrors()) {
			return "user/add";
		}

		usersService.addUser(user);
		return "redirect:/user/list";
	}

	@RequestMapping("/user/details/{id}")
	public String getDetail(Model model, @PathVariable Long id) {
		User user = usersService.getUser(id);
		model.addAttribute("user", user);
		model.addAttribute("markList", user.getMarks());
		return "user/details";
	}

	@RequestMapping("/user/delete/{id}")
	public String delete(@PathVariable Long id) {
		usersService.deleteUser(id);
		return "redirect:/user/list";
	}

	@RequestMapping(value = "/user/edit/{id}")
	public String getEdit(Model model, @PathVariable Long id) {
		User user = usersService.getUser(id);
		model.addAttribute("user", user);
		return "user/edit";
	}

	@RequestMapping(value = "/user/edit/{id}", method = RequestMethod.POST)
	public String setEdit(@Validated User user, BindingResult result, Model model, @PathVariable Long id) {
		userValidator.validate(user, result);

		if(result.hasErrors()) {
			return "user/edit/";
		}

		User original = usersService.getUser(id);
		// modificar solo nombre y apellido
		original.setName(user.getName());
		original.setLastName(user.getLastName());
		usersService.addUser(original);
		return "redirect:/user/details/" + id;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(@Validated User user, BindingResult result, Model model) {
		signUpFormValidator.validate(user, result);
		if(result.hasErrors()) {
			return "signup";
		}
		user.setRole(rolesService.getRoles()[0]);
		usersService.addUser(user);
		securityService.autoLogin(user.getDni(), user.getPasswordConfirm());
		return "redirect:home";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		return "login";
	}

	@RequestMapping(value = {"/home"}, method = RequestMethod.GET)
	public String home(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String dni = auth.getName();
		User activeUser = usersService.getUserByDni(dni);
		model.addAttribute("markList", activeUser.getMarks());
		return "home";
	}

}
