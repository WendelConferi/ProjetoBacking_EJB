package com.we3.web.controllers;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.google.gson.Gson;
import com.me.vo.genericos.app.response.BuscarTimeResponse;
import com.me.vo.genericos.app.vo.TimesVO;
import com.me.vo.genericos.requests.RemoverTimeRequest;
import com.mm.view.backing.AbstractBacking;
import com.we3.web.utils.PerfilGlobal;
import com.we3.web.utils.UtilWs;
import com.we3.web.utils.UtilsAddMensagem;

@ManagedBean
@ViewScoped
public class TimesBacking extends AbstractBacking implements Serializable {

	private static final long serialVersionUID = -3993025663774002613L;

	private List<TimesVO> timesParaSerMostradoNaTela;

	@PostConstruct
	public void init() {
		try {
			buscarTimes();
		} catch (Exception e) {
			e.printStackTrace();
			UtilsAddMensagem.addMessage(FacesMessage.SEVERITY_ERROR, "Erro ao buscar turmas.", null);
		}
	}

	private void buscarTimes() throws Exception {
		// Cahamando servidor sem nada pois é uma busca
		String jsonRetornadoDoServidor = UtilWs.postRequest("http://localhost:8080/TesteWS/rest/financeiro/listartimes",
				"");

		BuscarTimeResponse response = new Gson().fromJson(jsonRetornadoDoServidor, BuscarTimeResponse.class);
		timesParaSerMostradoNaTela = response.getTeams();

		System.out.println(jsonRetornadoDoServidor);
	}

	public List<TimesVO> getTimesParaSerMostradoNaTela() {
		return timesParaSerMostradoNaTela;
	}

	public void setTimesParaSerMostradoNaTela(List<TimesVO> timesParaSerMostradoNaTela) {
		this.timesParaSerMostradoNaTela = timesParaSerMostradoNaTela;
	}

	public void actionForm() {
		try {
			PerfilGlobal.actionNavigate("/formTimes");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionFormEdicao(TimesVO timeSelecionado) {
		try {
			PerfilGlobal.getSessao().setAttribute("timeXX", timeSelecionado);
			PerfilGlobal.actionNavigate("/formTimes");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void actionRemover(TimesVO timeASerRemovido) {

		try {
			// monta a request
			RemoverTimeRequest request = new RemoverTimeRequest();
			request.setNumSeq(timeASerRemovido.getNumSeq());

			// converte para json
			String jsonParaEnvio = new Gson().toJson(request);
			System.out.println(jsonParaEnvio);

			// chamar o servidor
			String jsonRetornadoDoServidor = UtilWs
					.postRequest("http://localhost:8080/TesteWS/rest/financeiro/removertimes", jsonParaEnvio);

			// recarregar a lista

			for (int i = 0; i < timesParaSerMostradoNaTela.size(); i++) {
				if (timesParaSerMostradoNaTela.get(i).getNumSeq().equals(timeASerRemovido.getNumSeq())) {

					timesParaSerMostradoNaTela.remove(i);
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("erro");
		}

	}
}
