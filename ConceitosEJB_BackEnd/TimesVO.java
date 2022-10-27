package com.we3.common.rest.ws;

public class TimesVO {

	private Integer numSeq;
	private String desTime;
	private Integer indTipo;

	public String getDesTecnico() {
		return desTecnico;
	}

	public void setDesTecnico(String desTecnico) {
		this.desTecnico = desTecnico;
	}

	private String desTecnico;

	public Integer getNumSeq() {
		return numSeq;
	}

	public void setNumSeq(Integer numSeq) {
		this.numSeq = numSeq;
	}

	public String getDesTime() {
		return desTime;
	}

	public void setDesTime(String desTime) {
		this.desTime = desTime;
	}

	public Integer getIndTipo() {
		return indTipo;
	}

	public void setIndTipo(Integer indTipo) {
		this.indTipo = indTipo;
	}

}
