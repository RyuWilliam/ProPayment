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

test("rejects invalid json with 400", async () => {
  const response = await handlePayment("{ invalid json }");

  assert.equal(response.statusCode, 400);
  const body = JSON.parse(response.body);
  assert.equal(body.provider, "visa");
  assert.equal(body.decision, "rejected");
  assert.equal(body.reason_code, "INVALID_JSON");
});

test("rejects blocked visa card with 403", async () => {
  const response = await handlePayment(
    JSON.stringify({
      card_number: "4000000000000002",
      expiry_month: 10,
      expiry_year: 2028,
      cvv: "321",
      amount: 19,
      currency: "USD"
    })
  );

  assert.equal(response.statusCode, 403);
  const body = JSON.parse(response.body);
  assert.equal(body.reason_code, "CARD_BLOCKED");
});
