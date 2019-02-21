package com.uniovi.controllers;

import com.uniovi.entities.Mark;
import com.uniovi.services.MarksService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.MarkValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class MarksController {

	//Inyectar el servicio
	private final MarksService marksService;

	private final UsersService usersService;

	private final MarkValidator markValidator;

	private final HttpSession httpSession;

	@Autowired
	public MarksController(MarksService marksService, UsersService usersService, MarkValidator markValidator, HttpSession httpSession) {
		this.marksService = marksService;
		this.usersService = usersService;
		this.markValidator = markValidator;
		this.httpSession = httpSession;
	}

	@RequestMapping("/mark/list")
	public String getList(Model model) {
		model.addAttribute("markList", marksService.getMarks());
		return "mark/list";
	}

	@RequestMapping("/mark/list/update")
	public String updateList(Model model) {
		model.addAttribute("markList", marksService.getMarks());
		return "mark/list :: tableMarks";
	}

	@RequestMapping(value = "/mark/add", method = RequestMethod.GET)
	public String getMark(Model model) {
		model.addAttribute("usersList", usersService.getUsers());
		model.addAttribute("mark", new Mark());
		return "mark/add";
	}

	@RequestMapping(value = "/mark/add", method = RequestMethod.POST)
	public String setMark(@Validated Mark mark, BindingResult result, Model model) {

		markValidator.validate(mark, result);
		if(result.hasErrors()) {
			model.addAttribute("usersList", usersService.getUsers());
			return "mark/add";
		}

		marksService.addMark(mark);
		return "redirect:/mark/list";
	}

	@RequestMapping("/mark/details/{id}")
	public String getDetail(Model model, @PathVariable Long id) {
		model.addAttribute("mark", marksService.getMark(id));
		return "mark/details";
	}

	@RequestMapping("/mark/delete/{id}")
	public String deleteMark(@PathVariable Long id) {
		marksService.deleteMark(id);
		return "redirect:/mark/list";
	}

	@RequestMapping(value = "/mark/edit/{id}", method = RequestMethod.GET)
	public String getEdit(Model model, @PathVariable Long id) {
		model.addAttribute("mark", marksService.getMark(id));
		model.addAttribute("usersList", usersService.getUsers());
		return "mark/edit";
	}

	@RequestMapping(value = "/mark/edit/{id}", method = RequestMethod.POST)
	public String setEdit(@Validated Mark mark, BindingResult result, Model model, @PathVariable Long id) {
		markValidator.validate(mark, result);
		if(result.hasErrors()) {
			return "mark/edit";
		}

		Mark original = marksService.getMark(id);
		// modificar solo score y description
		original.setScore(mark.getScore());
		original.setDescription(mark.getDescription());
		marksService.addMark(original);
		return "redirect:/mark/details/" + id;
	}

	@RequestMapping(value = "/mark/{id}/resend", method = RequestMethod.GET)
	public String setResendTrue(Model model, @PathVariable Long id) {
		marksService.setMarkResend(true, id);
		return "redirect:/mark/list";
	}

	@RequestMapping(value = "/mark/{id}/noresend", method = RequestMethod.GET)
	public String setResendFalse(Model model, @PathVariable Long id) {
		marksService.setMarkResend(false, id);
		return "redirect:/mark/list";
	}

}

