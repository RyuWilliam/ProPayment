"use strict";

const test = require("node:test");
const assert = require("node:assert/strict");
const { handlePayment } = require("../src/handler");

function assertResponseShape(body) {
  assert.deepEqual(Object.keys(body).sort(), ["reason", "status"]);
}

test("approves valid visa card with matching holder name and cvv", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "John Doe",
      card_number: "4111111111111111",
      cvv: "123"
    })
  );

  assert.equal(response.statusCode, 200);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "APPROVED");
  assert.equal(body.reason, "APPROVED");
});

test("approves valid visa card without optional cvv", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "John Doe",
      card_number: "4111111111111111"
    })
  );

  assert.equal(response.statusCode, 200);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "APPROVED");
  assert.equal(body.reason, "APPROVED");
});

test("rejects card not found with 404", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "John Doe",
      card_number: "4111111111111122",
      cvv: "123"
    })
  );

  assert.equal(response.statusCode, 404);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "CARD_NOT_FOUND");
});

test("rejects mismatching holder name with 422", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "Wrong Name",
      card_number: "4111111111111111",
      cvv: "123"
    })
  );

  assert.equal(response.statusCode, 422);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "HOLDER_NAME_MISMATCH");
});

test("rejects invalid holder name type/empty with 400", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "   ",
      card_number: "4111111111111111"
    })
  );

  assert.equal(response.statusCode, 400);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "INVALID_HOLDER_NAME");
});

test("rejects invalid json with 400", async () => {
  const response = await handlePayment("{ invalid json }");

  assert.equal(response.statusCode, 400);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "INVALID_JSON");
});

test("rejects blocked visa card with 403", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "Jane Doe",
      card_number: "4000000000000002",
      cvv: "321"
    })
  );

  assert.equal(response.statusCode, 403);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "CARD_BLOCKED");
});

test("rejects incorrect cvv with 401 when provided", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "John Doe",
      card_number: "4111111111111111",
      cvv: "999"
    })
  );

  assert.equal(response.statusCode, 401);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "INVALID_CVV");
});

test("rejects malformed cvv with 400 when provided", async () => {
  const response = await handlePayment(
    JSON.stringify({
      holder_name: "John Doe",
      card_number: "4111111111111111",
      cvv: "12"
    })
  );

  assert.equal(response.statusCode, 400);
  const body = JSON.parse(response.body);
  assertResponseShape(body);
  assert.equal(body.status, "REJECTED");
  assert.equal(body.reason, "INVALID_CVV");
});
