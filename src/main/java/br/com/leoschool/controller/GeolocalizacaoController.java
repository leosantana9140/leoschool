package br.com.leoschool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.leoschool.model.Aluno;
import br.com.leoschool.repository.AlunoRepository;

@Controller
public class GeolocalizacaoController {
	
	@Autowired
	private AlunoRepository alunoRepository;
	
	@GetMapping("/geolocalizacao/inicio")
	public String iniciar(Model model) {
		
		List<Aluno> alunos = alunoRepository.listar();
		
		model.addAttribute("alunos", alunos);
		
		return "geolocalizacao/pesquisar";
		
	}
	
	@GetMapping("/geolocalizacao/pesquisar")
	public String pesquisar(@RequestParam("alunoId") String alunoId, Model model) {
		
		Aluno aluno = alunoRepository.findById(alunoId);
		
		List<Aluno> alunosProximos = alunoRepository.findByLocation(aluno);
		
		model.addAttribute("alunosProximos", alunosProximos);
		
		return "geolocalizacao/pesquisar";
		
	}
	
}
