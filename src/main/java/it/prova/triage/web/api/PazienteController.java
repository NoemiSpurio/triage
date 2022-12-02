package it.prova.triage.web.api;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage.dto.DottoreRequestDTO;
import it.prova.triage.dto.PazienteDTO;
import it.prova.triage.model.Paziente;
import it.prova.triage.service.paziente.PazienteService;
import it.prova.triage.web.api.exception.AssociaPazienteDottoreException;
import it.prova.triage.web.api.exception.IdNotNullForInsertException;
import it.prova.triage.web.api.exception.PazienteNotFoundException;
import it.prova.triage.web.api.exception.PazienteSenzaDottoreException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/paziente")
public class PazienteController {

	@Autowired
	private PazienteService pazienteService;

	@Autowired
	private WebClient webClient;

	private static final Logger LOGGER = LogManager.getLogger(PazienteController.class);

	@GetMapping
	public List<PazienteDTO> listAll() {
		return PazienteDTO.createPazienteDTOListFromModelList(pazienteService.listAll());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void createNew(@Valid @RequestBody PazienteDTO pazienteInput) {
		if (pazienteInput.getId() != null) {
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		}
		pazienteService.inserisciNuovo(pazienteInput.buildPazienteModel());
	}

	@GetMapping("/{id}")
	public PazienteDTO findById(@PathVariable(value = "id", required = true) long id) {
		Paziente paziente = pazienteService.caricaSingoloPaziente(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
		return PazienteDTO.buildPazienteDTOFromModel(paziente);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@Valid @RequestBody PazienteDTO pazienteInput, @PathVariable(required = true) Long id) {
		Paziente paziente = pazienteService.caricaSingoloPaziente(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
		pazienteInput.setId(id);
		pazienteService.aggiorna(pazienteInput.buildPazienteModel());
	}

	@PostMapping("/assegnaPaziente/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void assegnaPaziente(@RequestBody DottoreRequestDTO dottoreRequest, @PathVariable(required = true) Long id) {

		Paziente paziente = pazienteService.caricaSingoloPaziente(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}

		LOGGER.info("....invocazione servizio esterno....");
		DottoreRequestDTO result = webClient.get().uri("/verifica/" + dottoreRequest.getCodiceDottore()).retrieve()
				.onStatus(HttpStatus::is4xxClientError, response -> {
					throw new AssociaPazienteDottoreException("Impossibile procedere: il dottore richiesto non e' libero.");
				}).bodyToMono(DottoreRequestDTO.class).block();

		if (result == null) {
			throw new RuntimeException();
		}

		dottoreRequest.setCodiceFiscalePaziente(paziente.getCodiceFiscale());
		ResponseEntity<DottoreRequestDTO> response = webClient.post().uri("/impostaInVisita")
				.body(Mono.just(dottoreRequest), DottoreRequestDTO.class).retrieve()
				.onStatus(HttpStatus::is4xxClientError, response2 -> {
					throw new AssociaPazienteDottoreException("Impossibile procedere: il dottore richiesto non e' libero.");
				}).toEntity(DottoreRequestDTO.class).block();

		if (response == null) {
			throw new RuntimeException();
		}

		LOGGER.info("....invocazione servizio esterno terminata....");

		pazienteService.assegnaDottore(id, dottoreRequest.getCodiceDottore());
	}

	@GetMapping("/ricovera/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ricovera(@PathVariable(required = true) Long id) {

		Paziente paziente = pazienteService.caricaSingoloPaziente(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}

		if (paziente.getCodiceDottore() == null) {
			throw new PazienteSenzaDottoreException("Impossibile procedere perche' il paziente non ha un dottore");
		}

		DottoreRequestDTO dottoreRequest = new DottoreRequestDTO(paziente.getCodiceDottore(),
				paziente.getCodiceFiscale());

		LOGGER.info("....invocazione servizio esterno....");
		webClient.post().uri("/terminaVisita").body(Mono.just(dottoreRequest), DottoreRequestDTO.class).retrieve()
				.onStatus(HttpStatus::is4xxClientError, response -> {
					throw new AssociaPazienteDottoreException("Impossibile procedere: il dottore richiesto non e' libero.");
				}).toEntity(DottoreRequestDTO.class).block();
		LOGGER.info("....invocazione servizio esterno terminata....");

		pazienteService.ricovera(paziente);
	}

	@GetMapping("/dimetti/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void dimetti(@PathVariable(required = true) Long id) {

		Paziente paziente = pazienteService.caricaSingoloPaziente(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}

		if (paziente.getCodiceDottore() == null) {
			throw new PazienteSenzaDottoreException("Impossibile procedere perche' il paziente non ha un dottore");
		}

		DottoreRequestDTO dottoreRequest = new DottoreRequestDTO(paziente.getCodiceDottore(),
				paziente.getCodiceFiscale());

		LOGGER.info("....invocazione servizio esterno....");
		webClient.post().uri("/terminaVisita").body(Mono.just(dottoreRequest), DottoreRequestDTO.class).retrieve()
				.toEntity(DottoreRequestDTO.class).block();
		LOGGER.info("....invocazione servizio esterno terminata....");

		pazienteService.dimetti(paziente);
	}

}
