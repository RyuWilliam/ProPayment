package co.edu.uptc.propayment.domain.model;

import co.edu.uptc.propayment.persistence.enums.CardType;


public class Card {
    private CardType cardType;
    private String cardNumber;
    private String cardHolderName;
    private String csv;

    public String getCsv() { return csv; }
    public void setCsv(String csv) { this.csv = csv; }


    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

}
