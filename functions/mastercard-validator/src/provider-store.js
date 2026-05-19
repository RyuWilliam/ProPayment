"use strict";

const cards = [
  {
    cardNumber: "5555555555554444",
    holderName: "Alice Smith",
    expiryMonth: 11,
    expiryYear: 2029,
    cvv: "123",
    status: "active"
  },
  {
    cardNumber: "5105105105105100",
    holderName: "Bob Smith",
    expiryMonth: 9,
    expiryYear: 2028,
    cvv: "456",
    status: "blocked"
  },
  {
    cardNumber: "5222222222222222",
    holderName: "Charlie Green",
    expiryMonth: 6,
    expiryYear: 2030,
    cvv: "789",
    status: "active"
  },
  {
    cardNumber: "5333333333333333",
    holderName: "David Miller",
    expiryMonth: 4,
    expiryYear: 2031,
    cvv: "111",
    status: "active"
  },
  {
    cardNumber: "5444444444444444",
    holderName: "Eva Taylor",
    expiryMonth: 10,
    expiryYear: 2027,
    cvv: "222",
    status: "blocked"
  }
];

function findCard(cardNumber) {
  return cards.find((card) => card.cardNumber === cardNumber) || null;
}

module.exports = {
  findCard
};
