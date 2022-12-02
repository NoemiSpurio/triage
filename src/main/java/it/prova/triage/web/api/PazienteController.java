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

import it.prova.triage.dto.PazienteDTO;
import it.prova.triage.model.Paziente;
import it.prova.triage.service.paziente.PazienteService;
import it.prova.triage.web.api.exception.IdNotNullForInsertException;
import it.prova.triage.web.api.exception.PazienteNotFoundException;

@RestController
@RequestMapping(value = "/api/paziente")
public class PazienteController {

	@Autowired
	private PazienteService pazienteService;
	
	@GetMapping
	public List<PazienteDTO> listAll(){
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
}
