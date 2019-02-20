package com.uniovi.services;

import com.uniovi.entities.Mark;
import com.uniovi.repositories.MarksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MarksService {
	@Autowired
	private MarksRepository marksRepository;
	@Autowired
	private HttpSession httpSession;


	public List<Mark> getMarks() {
		List<Mark> marks = new ArrayList<Mark>();
		marksRepository.findAll().forEach(marks::add);
		return marks;
	}

	public Mark getMark(Long id) {
		Set<Mark> consultedList = (Set<Mark>) httpSession.getAttribute("consultedList");
		if(consultedList == null) {
			consultedList = new HashSet<Mark>();
		}
		Mark markObtained = marksRepository.findById(id).get();
		consultedList.add(markObtained);
		httpSession.setAttribute("consultedList", consultedList);
		return markObtained;
	}

	public void addMark(Mark mark) {
		// Si en Id es null le asignamos el ultimo + 1 de la lista
		marksRepository.save(mark);
	}

	public void deleteMark(Long id) {
		marksRepository.deleteById(id);
	}
}
