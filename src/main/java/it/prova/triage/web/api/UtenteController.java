package it.prova.triage.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.triage.dto.UtenteDTO;
import it.prova.triage.model.Utente;
import it.prova.triage.service.utente.UtenteService;
import it.prova.triage.web.api.exception.IdNotNullForInsertException;
import it.prova.triage.web.api.exception.UtenteNotFoundException;

@RestController
@RequestMapping(value = "/api/utente")
public class UtenteController {

	@Autowired
	private UtenteService utenteService;

	@GetMapping
	public List<UtenteDTO> listAll() {
		return UtenteDTO.createUtenteDTOListFromModelList(utenteService.listAllUtenti());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void createNew(@Valid @RequestBody UtenteDTO utenteInput) {
		if (utenteInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");

		utenteService.inserisciNuovo(utenteInput.buildUtenteModel(true));
	}

	@GetMapping("/{id}")
	public UtenteDTO findById(@PathVariable(value = "id", required = true) long id) {
		Utente utente = utenteService.caricaSingoloUtenteConRuoli(id);

		if (utente == null)
			throw new UtenteNotFoundException("Utente not found con id: " + id);

		return UtenteDTO.buildUtenteDTOFromModel(utente);
	}

	@PutMapping("/cambiaStato/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changeUserAbilitation(@PathVariable(value = "id", required = true) long id) {
		utenteService.changeUserAbilitation(id);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@Valid @RequestBody UtenteDTO utenteInput, @PathVariable(required = true) Long id) {
		Utente utente = utenteService.caricaSingoloUtente(id);

		if (utente == null)
			throw new UtenteNotFoundException("Utente not found con id: " + id);

		utenteInput.setId(id);
		utenteService.aggiorna(utenteInput.buildUtenteModel(true));
	}
}
