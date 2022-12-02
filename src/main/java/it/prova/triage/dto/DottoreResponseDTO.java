package it.prova.triage.dto;

public class DottoreResponseDTO {

	private String codiceDottore;

	public DottoreResponseDTO() {

	}

	public DottoreResponseDTO(String codiceDottore) {
		super();
		this.codiceDottore = codiceDottore;
	}

	public String getCodiceDottore() {
		return codiceDottore;
	}

	public void setCodiceDottore(String codiceDottore) {
		this.codiceDottore = codiceDottore;
	}

}
