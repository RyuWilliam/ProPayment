"use strict";

const cards = [
  {
    cardNumber: "4111111111111111",
    holderName: "John Doe",
    expiryMonth: 12,
    expiryYear: 2029,
    cvv: "123",
    status: "active"
  },
  {
    cardNumber: "4000000000000002",
    holderName: "Jane Doe",
    expiryMonth: 10,
    expiryYear: 2028,
    cvv: "321",
    status: "blocked"
  },
  {
    cardNumber: "4111111111111112",
    holderName: "Bob Smith",
    expiryMonth: 5,
    expiryYear: 2030,
    cvv: "456",
    status: "active"
  },
  {
    cardNumber: "4222222222222222",
    holderName: "Alice Johnson",
    expiryMonth: 8,
    expiryYear: 2031,
    cvv: "789",
    status: "active"
  },
  {
    cardNumber: "4000000000000003",
    holderName: "Charlie Brown",
    expiryMonth: 1,
    expiryYear: 2027,
    cvv: "999",
    status: "blocked"
  }
];

function findCard(cardNumber) {
  return cards.find((card) => card.cardNumber === cardNumber) || null;
}

module.exports = {
  findCard
};
