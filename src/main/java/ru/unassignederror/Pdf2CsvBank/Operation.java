package ru.unassignederror.Pdf2CsvBank;

public class Operation {
	private String date;
	private String authCode;
	private String category;
	private String description;
	private String amount;
	private String balance;

	public Operation() {

	}

	public Operation(String date, String authCode, String category,
			String description, String amount, String balance) {
		this.date = date;
		this.authCode = authCode;
		this.category = category;
		this.description = description;
		this.amount = amount;
		this.balance = balance;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Operation [date=" + date + ", authCode=" + authCode
				+ ", category=" + category + ", description=" + description
				+ ", amount=" + amount + ", balance=" + balance + "]";
	}

}
