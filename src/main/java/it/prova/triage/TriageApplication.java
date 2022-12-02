package it.prova.triage;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.prova.triage.model.Paziente;
import it.prova.triage.model.Ruolo;
import it.prova.triage.model.Utente;
import it.prova.triage.service.paziente.PazienteService;
import it.prova.triage.service.ruolo.RuoloService;
import it.prova.triage.service.utente.UtenteService;

@SpringBootApplication
public class TriageApplication implements CommandLineRunner {

	@Autowired
	private RuoloService ruoloServiceInstance;
	@Autowired
	private UtenteService utenteServiceInstance;
	@Autowired
	private PazienteService pazienteServiceInstance;

	public static void main(String[] args) {
		SpringApplication.run(TriageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", Ruolo.ROLE_ADMIN));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", Ruolo.ROLE_SUB_OPERATOR) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", Ruolo.ROLE_SUB_OPERATOR));
		}

		if (utenteServiceInstance.findByUsername("admin") == null) {
			Utente admin = new Utente("admin", "admin", "Mario", "Rossi", new Date());
			admin.setEmail("a.admin@prova.it");
			admin.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN));
			utenteServiceInstance.inserisciNuovo(admin);
			utenteServiceInstance.changeUserAbilitation(admin.getId());
		}

		if (utenteServiceInstance.findByUsername("user") == null) {
			Utente classicUser = new Utente("user", "user", "Antonio", "Verdi", new Date());
			classicUser.setEmail("u.user@prova.it");
			classicUser.getRuoli()
					.add(ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", Ruolo.ROLE_SUB_OPERATOR));
			utenteServiceInstance.inserisciNuovo(classicUser);
			utenteServiceInstance.changeUserAbilitation(classicUser.getId());
		}
		
		Paziente paziente1 = new Paziente("sono", "malato", "ABC123", null);
		pazienteServiceInstance.inserisciNuovo(paziente1);
	}

}
