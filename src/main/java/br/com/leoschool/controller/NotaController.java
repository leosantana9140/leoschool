package br.com.leoschool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.leoschool.model.Aluno;
import br.com.leoschool.model.Nota;
import br.com.leoschool.repository.AlunoRepository;

@Controller
public class NotaController {
	
	@Autowired
	private AlunoRepository alunoRepository;
	
	@GetMapping("/nota/cadastrar/{id}")
	public String cadastar(@PathVariable String id, Model model) {
		Aluno aluno = this.alunoRepository.findById(id);
		model.addAttribute("aluno", aluno);
		model.addAttribute("nota", new Nota());
		
		return "nota/cadastro";
	}
	
	@PostMapping("/nota/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Nota nota) {
		Aluno aluno = alunoRepository.findById(id);
		this.alunoRepository.salvar(aluno.adicionarNota(aluno, nota));
		
		return "redirect:/aluno/listar";
	}
	
}
