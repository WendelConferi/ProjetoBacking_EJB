package com.we3.web.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.google.gson.Gson;
import com.me.vo.genericos.app.vo.TimesVO;
import com.me.vo.genericos.requests.CadastroTimeRequest;
import com.mm.view.backing.AbstractBacking;
import com.we3.web.utils.PerfilGlobal;
import com.we3.web.utils.UtilWs;

@ManagedBean
@ViewScoped
public class TimesFormBacking extends AbstractBacking implements Serializable {

	private Integer numSeq;
	private String desTime;
	private Integer indTipo;
	private String desTecnico;

	@PostConstruct
	public void init() {

		// se tem alguma coisa na variavel timeXX da sessao
		if (PerfilGlobal.getSessao().getAttribute("timeXX") != null) {

			TimesVO timeSelecionadoNaTelaDEAntes = (TimesVO) PerfilGlobal.getSessao().getAttribute("timeXX");
			numSeq = timeSelecionadoNaTelaDEAntes.getNumSeq();
			desTime = timeSelecionadoNaTelaDEAntes.getDesTime();
			desTecnico = timeSelecionadoNaTelaDEAntes.getDesTecnico();
			indTipo = timeSelecionadoNaTelaDEAntes.getIndTipo();

			PerfilGlobal.getSessao().setAttribute("timeXX", null);
		}

	}

	public void actionSalvar() {

		try {
			// montar o objeto request
			CadastroTimeRequest request = new CadastroTimeRequest();
			request.setNome(desTime);
			request.setNumSeq(numSeq);
			request.setTecnico(desTecnico);
			request.setTipo(indTipo);

			// converter objeto para Json
			String jsonParEnviar = new Gson().toJson(request);
			System.out.println(jsonParEnviar);

			// enviar para o servidor
			String jsonRetornadoDoServidor = UtilWs
					.postRequest("http://localhost:8080/TesteWS/rest/financeiro/cadastrotimes", jsonParEnviar);

			// chamar a tela de listagem
			PerfilGlobal.actionNavigate("/listaTimes");

		} catch (Exception e) {
			System.out.println("erro");
		}

	}

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

	public String getDesTecnico() {
		return desTecnico;
	}

	public void setDesTecnico(String desTecnico) {
		this.desTecnico = desTecnico;
	}

}
