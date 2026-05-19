"use strict";

const test = require("node:test");
const assert = require("node:assert/strict");
const { handlePayment } = require("../src/handler");

test("approves valid mastercard card from mock store", async () => {
  const response = await handlePayment(
    JSON.stringify({
      card_number: "5555555555554444",
      expiry_month: 11,
      expiry_year: 2029,
      cvv: "123",
      amount: 125.0,
      currency: "USD"
    })
  );

  assert.equal(response.statusCode, 200);
  const body = JSON.parse(response.body);
  assert.equal(body.provider, "mastercard");
  assert.equal(body.decision, "approved");
  assert.equal(body.reason_code, "APPROVED");
});

test("rejects blocked mastercard card with 403", async () => {
  const response = await handlePayment(
    JSON.stringify({
      card_number: "5105105105105100",
      expiry_month: 9,
      expiry_year: 2028,
      cvv: "456",
      amount: 35.5,
      currency: "USD"
    })
  );

  assert.equal(response.statusCode, 403);
  const body = JSON.parse(response.body);
  assert.equal(body.decision, "rejected");
  assert.equal(body.reason_code, "CARD_BLOCKED");
});

test("rejects invalid json with 400", async () => {
  const response = await handlePayment("{ invalid json }");

  assert.equal(response.statusCode, 400);
  const body = JSON.parse(response.body);
  assert.equal(body.provider, "mastercard");
  assert.equal(body.decision, "rejected");
  assert.equal(body.reason_code, "INVALID_JSON");
});

test("rejects invalid amount with 400", async () => {
  const response = await handlePayment(
    JSON.stringify({
      card_number: "5555555555554444",
      expiry_month: 11,
      expiry_year: 2029,
      cvv: "123",
      amount: 0,
      currency: "USD"
    })
  );

  assert.equal(response.statusCode, 400);
  const body = JSON.parse(response.body);
  assert.equal(body.reason_code, "INVALID_AMOUNT");
});
