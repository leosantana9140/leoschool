package br.com.leoschool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.leoschool.model.Aluno;
import br.com.leoschool.model.Habilidade;
import br.com.leoschool.repository.AlunoRepository;

@Controller
public class HabilidadeController {
	
	@Autowired
	private AlunoRepository alunoRepository;
	
	@GetMapping("/habilidade/cadastrar/{id}")
	public String cadastrar(@PathVariable String id, Model model) {
		Aluno aluno = this.alunoRepository.findById(id);
		
		model.addAttribute("aluno", aluno);
		model.addAttribute("habilidade", new Habilidade());
		
		return "habilidade/cadastro";
	}
	
	@PostMapping("/habilidade/salvar/{id}")
	public String salvar(@PathVariable String id, @ModelAttribute Habilidade habilidade) {
		Aluno aluno = this.alunoRepository.findById(id);
		
		this.alunoRepository.salvar(aluno.adicionarHabilidade(aluno, habilidade));
		
		return "redirect:/aluno/listar";
	}
	
}
