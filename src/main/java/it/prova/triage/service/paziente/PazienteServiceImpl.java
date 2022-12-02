package it.prova.triage.service.paziente;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;
import it.prova.triage.repository.paziente.PazienteRepository;
import it.prova.triage.web.api.exception.IdNotNullForInsertException;
import it.prova.triage.web.api.exception.PazienteNonDimessoException;
import it.prova.triage.web.api.exception.PazienteNotFoundException;

@Service
@Transactional
public class PazienteServiceImpl implements PazienteService {

	@Autowired
	private PazienteRepository pazienteRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Paziente> listAll() {
		return (List<Paziente>) pazienteRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Paziente caricaSingoloPaziente(Long id) {
		return pazienteRepository.findById(id).orElse(null);
	}

	@Override
	public void aggiorna(Paziente pazienteInstance) {
		Paziente pazienteReloaded = pazienteRepository.findById(pazienteInstance.getId()).orElse(null);

		if (pazienteReloaded == null) {
			throw new PazienteNotFoundException("Impossibile modificare, nessun paziente trovato con questo id");
		}
		pazienteInstance.setDataRegistrazione(pazienteReloaded.getDataRegistrazione());

		pazienteRepository.save(pazienteInstance);

	}

	@Override
	public void inserisciNuovo(Paziente pazienteInstance) {

		if (pazienteInstance.getId() != null) {
			throw new IdNotNullForInsertException("Non puoi inserire un paziente con un id gia' assegnato.");
		}

		pazienteInstance.setDataRegistrazione(new Date());
		pazienteInstance.setStato(StatoPaziente.IN_ATTESA_VISITA);
		pazienteInstance.setCodiceDottore(null);
		pazienteRepository.save(pazienteInstance);

	}

	@Override
	public void rimuovi(Long idToRemove) {

		Paziente pazienteToRemove = pazienteRepository.findById(idToRemove).orElse(null);

		if (pazienteToRemove == null) {
			throw new PazienteNotFoundException("Impossibile cancellare, nessun paziente trovato con questo id");
		}

		if (pazienteToRemove.getStato().equals(StatoPaziente.DIMESSO)) {
			throw new PazienteNonDimessoException("Impossibile cancellare un paziente non dimesso.");
		}

		pazienteRepository.deleteById(idToRemove);
	}

	@Override
	public void assegnaDottore(Long idPaziente, String codiceDottore) {
		Paziente pazienteInstance = pazienteRepository.findById(idPaziente).orElse(null);

		if (pazienteInstance == null) {
			throw new PazienteNotFoundException("Impossibile modificare, nessun paziente trovato con questo id");
		}

		pazienteInstance.setCodiceDottore(codiceDottore);
		pazienteInstance.setStato(StatoPaziente.IN_VISITA);
		pazienteRepository.save(pazienteInstance);
	}

	@Override
	public void ricovera(Paziente input) {
		input.setCodiceDottore(null);
		input.setStato(StatoPaziente.RICOVERATO);
		pazienteRepository.save(input);
	}

	@Override
	public void dimetti(Paziente input) {
		input.setCodiceDottore(null);
		input.setStato(StatoPaziente.DIMESSO);
		pazienteRepository.save(input);
	}

}
