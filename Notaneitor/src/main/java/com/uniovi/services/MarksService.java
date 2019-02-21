package com.uniovi.services;

import com.uniovi.entities.Mark;
import com.uniovi.entities.User;
import com.uniovi.repositories.MarksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

@Service
public class MarksService {
	private final MarksRepository marksRepository;
	private final HttpSession httpSession;

	@Autowired
	public MarksService(MarksRepository marksRepository, HttpSession httpSession) {
		this.marksRepository = marksRepository;
		this.httpSession = httpSession;
	}

	@SuppressWarnings("unchecked")
	public Mark getMark(Long id) {
		Set<Mark> consultedList = (Set<Mark>) httpSession.getAttribute("consultedList");

		if(consultedList == null) {
			consultedList = new HashSet<>();
		}
		Optional<Mark> optional = marksRepository.findById(id);
		if(optional.isPresent()) {
			Mark markObtained = optional.get();
			httpSession.setAttribute("consultedList", consultedList);
			return markObtained;
		}
		return null;
	}

	public void addMark(Mark mark) {
		// Si en Id es null le asignamos el ultimo + 1 de la lista
		marksRepository.save(mark);
	}

	public void deleteMark(Long id) {
		marksRepository.deleteById(id);
	}

	public void setMarkResend(boolean revised, Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String dni = auth.getName();


		Optional<Mark> object = marksRepository.findById(id);
		if(object.isPresent()) {
			Mark mark = object.get();
			if(mark.getUser().getDni().equals(dni)) {
				marksRepository.updateResend(revised, id);
			}
		}

	}

	public Page<Mark> getMarksForUser(Pageable pageable, User user) {
		Page<Mark> marks = new PageImpl<>(new LinkedList<>());
		if(user.getRole().equals("ROLE_STUDENT")) {
			marks = marksRepository.findAllByUser(pageable, user);
		}
		if(user.getRole().equals("ROLE_PROFESSOR")) {
			marks = getMarks(pageable);
		}
		return marks;
	}

	public Page<Mark> searchMarksByDescriptionAndNameForUser(Pageable pageable, String searchText, User user) {
		Page<Mark> marks = new PageImpl<>(new LinkedList<>());
		searchText = "%" + searchText + "%";
		if(user.getRole().equals("ROLE_STUDENT")) {
			marks = marksRepository.
					searchByDescriptionNameAndUser(pageable, searchText, user);
		}
		if(user.getRole().equals("ROLE_PROFESSOR")) {
			marks = marksRepository.
					searchByDescriptionAndName(pageable, searchText);
		}
		return marks;
	}

	/* AUXILIARES */
	private Page<Mark> getMarks(Pageable pageable) {
		return marksRepository.findAll(pageable);
	}
}
