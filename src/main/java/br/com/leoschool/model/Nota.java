package br.com.leoschool.model;

public class Nota {
	private double valor;
	
	public Nota() { }

	public Nota(Double valor) {
		this.valor = valor;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}
