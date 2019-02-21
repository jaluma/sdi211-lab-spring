package com.uniovi.validators;

import com.uniovi.entities.User;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
	private final UsersService usersService;

	@Autowired
	public UserValidator(UsersService usersService) {
		this.usersService = usersService;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Error.empty");

		// editamos el user
		if(user.getDni() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dni", "Error.empty");
			// tamaño del DNI
			int length = user.getDni().length();
			if(length != 9 && length >= 2 && Character.isAlphabetic(user.getDni()
					.substring(length - 2, length - 1)
					.toCharArray()[0])) {
				errors.rejectValue("dni", "Error.signup.dni.length");
			}
			if(usersService.getUserByDni(user.getDni()) != null) {
				errors.rejectValue("dni", "Error.signup.dni.duplicate");
			}
		}

	}
}