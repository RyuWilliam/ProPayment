"use strict";

const test = require("node:test");
const assert = require("node:assert/strict");
const { handlePayment } = require("../src/handler");

test("approves valid visa card from mock store", async () => {
  const response = await handlePayment(
    JSON.stringify({
      card_number: "4111111111111111",
      expiry_month: 12,
      expiry_year: 2029,
      cvv: "123",
      amount: 99.5,
      currency: "USD"
    })
  );

  assert.equal(response.statusCode, 200);
  const body = JSON.parse(response.body);
  assert.equal(body.provider, "visa");
  assert.equal(body.decision, "approved");
  assert.equal(body.reason_code, "APPROVED");
});

test("rejects card not found with 404", async () => {
  const response = await handlePayment(
    JSON.stringify({
      card_number: "4111111111111122",
      expiry_month: 12,
      expiry_year: 2029,
      cvv: "123",
      amount: 99.5,
      currency: "USD"
    })
  );

  assert.equal(response.statusCode, 404);
  const body = JSON.parse(response.body);
  assert.equal(body.decision, "rejected");
  assert.equal(body.reason_code, "CARD_NOT_FOUND");
});
