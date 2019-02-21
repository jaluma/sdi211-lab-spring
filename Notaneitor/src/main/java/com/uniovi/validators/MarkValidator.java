package com.uniovi.validators;

import com.uniovi.entities.Mark;
import com.uniovi.services.MarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class MarkValidator implements Validator {

	private final MarksService marksService;

	@Autowired
	public MarkValidator(MarksService marksService) {
		this.marksService = marksService;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Mark.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Mark mark = (Mark) target;

		if(mark.getUser() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user", "Error.empty");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "Error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "score", "Error.empty");

		if(mark.getScore() < 0 || mark.getScore() > 10) {
			errors.rejectValue("score", "Error.mark.add.score");
		}
	}
}