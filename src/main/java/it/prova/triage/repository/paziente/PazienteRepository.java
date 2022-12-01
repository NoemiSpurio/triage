package it.prova.triage.repository.paziente;

import org.springframework.data.repository.CrudRepository;

import it.prova.triage.model.Paziente;

public interface PazienteRepository extends CrudRepository<Paziente, Long> {

}
