package it.prova.triage.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;

public class PazienteDTO {

	private Long id;

	@NotBlank(message = "{nome.notblank}")
	private String nome;

	@NotBlank(message = "{cognome.notblank}")
	private String cognome;

	@NotBlank(message = "{codiceFiscale.notblank}")
	private String codiceFiscale;

	private Date dataRegistrazione;

	private StatoPaziente stato;

	private String codiceDottore;

	public PazienteDTO() {

	}

	public PazienteDTO(Long id, String nome, String cognome, String codiceFiscale, Date dataRegistrazione,
			StatoPaziente stato, String codiceDottore) {
		super();
		this.id = id;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.dataRegistrazione = dataRegistrazione;
		this.stato = stato;
		this.codiceDottore = codiceDottore;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Date getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(Date dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public StatoPaziente getStato() {
		return stato;
	}

	public void setStato(StatoPaziente stato) {
		this.stato = stato;
	}

	public String getCodiceDottore() {
		return codiceDottore;
	}

	public void setCodiceDottore(String codiceDottore) {
		this.codiceDottore = codiceDottore;
	}

	public Paziente buildPazienteModel() {
		Paziente result = new Paziente(this.id, this.nome, this.cognome, this.codiceFiscale, this.dataRegistrazione,
				this.stato, this.codiceDottore);
		return result;
	}

	public static PazienteDTO buildPazienteDTOFromModel(Paziente model) {
		PazienteDTO result = new PazienteDTO(model.getId(), model.getNome(), model.getCognome(),
				model.getCodiceFiscale(), model.getDataRegistrazione(), model.getStato(), model.getCodiceDottore());
		return result;
	}

	public static List<PazienteDTO> createPazienteDTOListFromModelList(List<Paziente> modelListInput) {
		return modelListInput.stream().map(p -> PazienteDTO.buildPazienteDTOFromModel(p)).collect(Collectors.toList());
	}

}
