"use strict";

const cards = [
  {
    cardNumber: "4111111111111111",
    expiryMonth: 12,
    expiryYear: 2029,
    cvv: "123",
    status: "active"
  },
  {
    cardNumber: "4000000000000002",
    expiryMonth: 10,
    expiryYear: 2028,
    cvv: "321",
    status: "blocked"
  }
];

function findCard(cardNumber) {
  return cards.find((card) => card.cardNumber === cardNumber) || null;
}

module.exports = {
  findCard
};
