"use strict";

const cards = [
  {
    cardNumber: "5555555555554444",
    expiryMonth: 11,
    expiryYear: 2029,
    cvv: "123",
    status: "active"
  },
  {
    cardNumber: "5105105105105100",
    expiryMonth: 9,
    expiryYear: 2028,
    cvv: "456",
    status: "blocked"
  }
];

function findCard(cardNumber) {
  return cards.find((card) => card.cardNumber === cardNumber) || null;
}

module.exports = {
  findCard
};
