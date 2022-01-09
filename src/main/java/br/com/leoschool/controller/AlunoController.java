package br.com.leoschool.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.maps.errors.ApiException;

import br.com.leoschool.model.Aluno;
import br.com.leoschool.repository.AlunoRepository;
import br.com.leoschool.service.GeolocalizacaoService;

@Controller
public class AlunoController {
	
	@Autowired
	private AlunoRepository alunoRepository;
	
	@Autowired
	private GeolocalizacaoService geolocalizacaoService;
	
	@GetMapping("/aluno/cadastrar")
	public String cadastrar(Model model) {
		model.addAttribute("aluno", new Aluno());
		return "aluno/cadastro";
	}
	
	@PostMapping("/aluno/salvar")
	public String salvar(@ModelAttribute Aluno aluno) {
		try {
			
			List<Double> latitudeLongitude = geolocalizacaoService.obterLatitudeLongitude(aluno.getContato());
			
			aluno.getContato().setCoordinates(latitudeLongitude);
			
			alunoRepository.salvar(aluno);
			
		} catch (Exception e) {
			System.out.println("Endereço não localizado.");
			e.printStackTrace();
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/aluno/listar")
	public String listar(Model model) {
		List<Aluno> alunos = this.alunoRepository.listar();
		model.addAttribute("alunos", alunos);
		return "aluno/listar";
	}
	
	@GetMapping("/aluno/visualizar/{id}")
	public String visualizar(@PathVariable String id, Model model) {
		Aluno aluno = this.alunoRepository.findById(id);
		model.addAttribute("aluno", aluno);
		return "aluno/visualizar";
	}
	
	@GetMapping("/aluno/pesquisar")
	public String pesquisar() {
		return "aluno/pesquisar";
	}
	
	@GetMapping("/aluno/buscar")
	public String buscar(@RequestParam("nome") String nome, Model model) {
		List<Aluno> alunos = alunoRepository.findByName(nome);
		model.addAttribute("alunos", alunos);
		return "aluno/pesquisar";
	}
	
}
