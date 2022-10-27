mport static com.me.utils.MeUtils.isEmpty;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.resteasy.util.Base64;
import org.modelmapper.ModelMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.me.logger.MLogger;
import com.me.utils.BigDecimalUtil;
import com.me.utils.CommonsHelper;
import com.me.utils.DocumentosUtils;
import com.me.utils.MeEmailUtil;
import com.me.utils.MeUtils;
import com.me.validacoes.DateUtils;
import com.me.validacoes.UtilWs;
import com.me.vo.genericos.app.financeiro.response.BuscaDetalhesConveniosResponse;
import com.me.vo.genericos.app.financeiro.response.BuscaDetalhesFinanResponse;
import com.me.vo.genericos.app.financeiro.response.BuscaExtratoFinanceiroResponse;
import com.me.vo.genericos.app.financeiro.response.BuscaFilhosServicosFinanResponse;
import com.me.vo.genericos.app.financeiro.response.CadastroDetalhesFinanResponse;
import com.me.vo.genericos.app.financeiro.response.GerarMensalidadesServicosResponse;
import com.me.vo.genericos.app.response.AbstractMeResponse;
import com.me.vo.genericos.app.response.AtualizaContaProgramadaManualResponse;
import com.me.vo.genericos.app.response.BaixaManualResponse;
import com.me.vo.genericos.app.response.BuscaContasProgErrosResponse;
import com.me.vo.genericos.app.response.BuscaContasProgramadasDetalhesResponse;
import com.me.vo.genericos.app.response.BuscaContasProgramadasResponse;
import com.me.vo.genericos.app.response.BuscaContasResponse;
import com.me.vo.genericos.app.response.BuscaDepositosResponse;
import com.me.vo.genericos.app.response.CadastroContasWebResponse;
import com.me.vo.genericos.app.response.CadastroServicosResponse;
import com.me.vo.genericos.app.response.CalculoValoresResponse;
import com.me.vo.genericos.app.response.RemoveContaProgramadaResponse;
import com.me.vo.genericos.app.split.DadosFatura;
import com.me.vo.genericos.app.vo.ContaResumoVO;
import com.me.vo.genericos.app.vo.ContasProgErrosVO;
import com.me.vo.genericos.app.vo.ContasVO;
import com.me.vo.genericos.app.vo.ConveniosVO;
import com.me.vo.genericos.app.vo.DetalhesProgVO;
import com.me.vo.genericos.app.vo.FilhosServicosFinaVO;
import com.me.vo.genericos.app.vo.MesAnoFaturasVO;
import com.me.vo.genericos.app.vo.PaisFinanVO;
import com.me.vo.genericos.app.vo.ServicosVO;
import com.me.vo.genericos.app.vo.financeiro.ContasFilhosVO;
import com.me.vo.genericos.app.vo.financeiro.MeContasAPagarVO;
import com.me.vo.genericos.app.vo.financeiro.MeDespesasVO;
import com.me.vo.genericos.financeiro.requests.BuscaDepositosRequest;
import com.me.vo.genericos.financeiro.requests.BuscaDetalhesConveniosRequest;
import com.me.vo.genericos.financeiro.requests.BuscaDetalhesFinanRequest;
import com.me.vo.genericos.financeiro.requests.BuscaFilhosServicosFinanRequest;
import com.me.vo.genericos.financeiro.requests.CadastroDetalhesConveniosRequest;
import com.me.vo.genericos.financeiro.requests.CadastroDetalhesFinanRequest;
import com.me.vo.genericos.financeiro.requests.CadastroFilhosServicosFinanRequest;
import com.me.vo.genericos.financeiro.requests.GerarMensalidadesServicosRequest;
import com.me.vo.genericos.requests.AtualizaContaProgramadaManualRequest;
import com.me.vo.genericos.requests.BaixaManualRequest;
import com.me.vo.genericos.requests.BasicRequest;
import com.me.vo.genericos.requests.BuscaContasAPagarRequest;
import com.me.vo.genericos.requests.BuscaContasProgramadasRequest;
import com.me.vo.genericos.requests.BuscaContasRequest;
import com.me.vo.genericos.requests.BuscaExtratoFinanceiroRequest;
import com.me.vo.genericos.requests.BuscaPaiRequestWeb;
import com.me.vo.genericos.requests.BuscaServicosRequest;
import com.me.vo.genericos.requests.CadastroContasWebRequest;
import com.me.vo.genericos.requests.CadastroServicosRequest;
import com.me.vo.genericos.requests.EnviaContasRequest;
import com.me.vo.genericos.requests.RemoveContaGeradaRequest;
import com.me.vo.genericos.requests.RemoveContaProgramadaRequest;
import com.me.vo.genericos.requests.RemoveTurnosRequest;
import com.me.vo.genericos.web.response.BasicResponse;
import com.me.vo.genericos.web.response.BuscaContasBancariasRequest;
import com.me.vo.genericos.web.response.BuscaContasBancariasResponse;
import com.me.vo.genericos.web.response.BuscaPaiResponseWeb;
import com.me.vo.genericos.web.vo.ContasBancariasVO;
import com.me.vo.genericos.web.vo.FilhosWebVO;
import com.me.vo.genericos.web.vo.PaisWebVO;
import com.me.vo.integracoes.notas.NotasFiscaisVO;
import com.we3.common.entity.Contas;
import com.we3.common.entity.ContasBancarias;
import com.we3.common.entity.ContasEscolas;
import com.we3.common.entity.ContasExcluidas;
import com.we3.common.entity.ContasExcluidasConvenios;
import com.we3.common.entity.ContasFilhos;
import com.we3.common.entity.ContasFilhosConvenios;
import com.we3.common.entity.ContasProgramadas;
import com.we3.common.entity.Convenios;
import com.we3.common.entity.Escolas;
import com.we3.common.entity.EscolasFinan;
import com.we3.common.entity.Filhos;
import com.we3.common.entity.FilhosServicosFinan;
import com.we3.common.entity.NotasFiscais;
import com.we3.common.entity.Pais;
import com.we3.common.entity.PaisFinan;
import com.we3.common.entity.Servicos;
import com.we3.common.entity.Times;
import com.we3.common.entity.financeiro.MeCategoriasContasAPagar;
import com.we3.common.entity.financeiro.MeContasAPagar;
import com.we3.common.entity.financeiro.MeDespesas;
import com.we3.common.entity.pk.ContasProgramadasPK;
import com.we3.common.entity.pk.ConveniosPK;
import com.we3.common.entity.pk.PaisFinanPK;
import com.we3.common.entity.pk.financeiro.MeCategoriasContasAPagarPK;
import com.we3.common.entity.pk.financeiro.MeContasAPagarPK;
import com.we3.common.rest.split.BoletoRequest;
import com.we3.common.rest.split.BoletoResponse;
import com.we3.common.rest.split.DadosFinanceiros;
import com.we3.common.rest.split.SplitAPI;
import com.we3.common.rest.split.SplitUtil;

@Stateless
@LocalBean
@Path("/financeiro")
public class FinanceiroEJB {

	private static MLogger log = MLogger.getLogger(FinanceiroEJB.class);

	// @PersistenceContext(unitName = "exemplo-pu")
	@PersistenceContext(unitName = "minhaEscolaCommons")
	private EntityManager em;




@POST
	@Path("/cadastrotimes")
	@Produces("application/json")
	@Consumes("application/json")
	public CadastroTimeResponse cadastrarrrTimes(CadastroTimeRequest request) {

		CadastroTimeResponse response = new CadastroTimeResponse();

		if (request.getNumSeq() != null) {

			// edicao

			Times timejasalvo = em.find(Times.class, request.getNumSeq());

			timejasalvo.setDesTime(request.getNome());
			timejasalvo.setIndTipo(request.getTipo());
			timejasalvo.setDesTecnico(request.getTecnico());

			em.merge(timejasalvo);
			response.setNumeroTeste(22);

		} else {
			// insercao

			Times timenovo = new Times();
			timenovo.setDesTime(request.getNome());
			timenovo.setIndTipo(request.getTipo());
			timenovo.setDesTecnico(request.getTecnico());

			em.persist(timenovo);

			response.setNumeroTeste(11111);
		}
		response.setMensagemInformativa("Olá, deu tudo certo!!");
		return response;
	}

	@POST
	@Path("/removertimes")
	@Produces("application/json")
	@Consumes("application/json")
	public RemoverTimeResponse deletar(RemoverTimeRequest request) {

		Integer numSeq = request.getNumSeq();

		Times timeaserremovido = em.find(Times.class, numSeq);

		em.remove(timeaserremovido);

		RemoverTimeResponse response = new RemoverTimeResponse();
		response.setMensagemInformativa("Olá, foi removido!!");
		return response;
	}

	@POST
	@Path("/listartimes")
	@Produces("application/json")
	@Consumes("application/json")
	public ListarTimeResponse listar() {

		ListarTimeResponse response = new ListarTimeResponse();

//		Query query = em.createNativeQuery("SELECT num_seq, des_time, ind_tipo FROM times ORDER BY DES_TIME");
//
//		List resultados = query.getResultList();
//
//		if (resultados != null) {
//
//			for (Object linha : resultados) {
//
//				Object[] aux = (Object[]) linha;
//
//				TimesVO timeEncontrado = new TimesVO();
//
//				timeEncontrado.setNumSeq(Integer.valueOf(aux[0].toString()));
//				timeEncontrado.setDesTime(aux[1].toString());
//				timeEncontrado.setIndTipo(Integer.valueOf(aux[2].toString()));
//
//				response.getTeams().add(timeEncontrado);
//			}
//		}

		Query query = em.createQuery("SELECT e FROM Times e ORDER BY e.desTime");

		List<Times> resultados = query.getResultList();

		ArrayList<TimesVO> listaSeparada = new ArrayList<TimesVO>();

		if (resultados != null) {

			for (Times linha : resultados) {

				TimesVO timeEncontrado = new TimesVO();

				timeEncontrado.setNumSeq(linha.getNumSeq());
				timeEncontrado.setDesTime(linha.getDesTime());
				timeEncontrado.setIndTipo(linha.getIndTipo());
				timeEncontrado.setDesTecnico(linha.getDesTecnico());

				listaSeparada.add(timeEncontrado);
			}
		}

		response.setTeams(listaSeparada);
		return response;
	}
}
