"use strict";

const { randomUUID } = require("crypto");
const { findCard } = require("./provider-store");

function isIntegerBetween(value, min, max) {
  return Number.isInteger(value) && value >= min && value <= max;
}

function nowIso() {
  return new Date().toISOString();
}

function buildResponse(statusCode, payload) {
  return {
    statusCode,
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  };
}

function validatePayload(payload) {
  if (!payload || typeof payload !== "object") {
    return "INVALID_PAYLOAD";
  }

  const { card_number, expiry_month, expiry_year, cvv, amount, currency } = payload;

  if (typeof card_number !== "string" || !/^4\d{12,18}$/.test(card_number)) {
    return "INVALID_CARD_NUMBER";
  }

  if (!isIntegerBetween(expiry_month, 1, 12)) {
    return "INVALID_EXPIRY_MONTH";
  }

  const currentYear = new Date().getUTCFullYear();
  if (!Number.isInteger(expiry_year) || expiry_year < currentYear) {
    return "INVALID_EXPIRY_YEAR";
  }

  if (typeof cvv !== "string" || !/^\d{3,4}$/.test(cvv)) {
    return "INVALID_CVV";
  }

  if (typeof amount !== "number" || amount <= 0) {
    return "INVALID_AMOUNT";
  }

  if (typeof currency !== "string" || !/^[A-Z]{3}$/.test(currency)) {
    return "INVALID_CURRENCY";
  }

  return null;
}

function validateAgainstProvider(payload) {
  const card = findCard(payload.card_number);

  if (!card) {
    return { decision: "rejected", reasonCode: "CARD_NOT_FOUND", statusCode: 404 };
  }

  if (card.status !== "active") {
    return { decision: "rejected", reasonCode: "CARD_BLOCKED", statusCode: 403 };
  }

  if (card.expiryMonth !== payload.expiry_month || card.expiryYear !== payload.expiry_year) {
    return { decision: "rejected", reasonCode: "CARD_EXPIRED", statusCode: 422 };
  }

  if (card.cvv !== payload.cvv) {
    return { decision: "rejected", reasonCode: "INVALID_CVV", statusCode: 401 };
  }

  return { decision: "approved", reasonCode: "APPROVED", statusCode: 200 };
}

async function handlePayment(requestBody) {
  let payload;
  try {
    payload = JSON.parse(requestBody || "{}");
  } catch {
    return buildResponse(400, {
      transaction_id: randomUUID(),
      provider: "visa",
      decision: "rejected",
      reason_code: "INVALID_JSON",
      authorized_amount: 0,
      currency: "N/A",
      timestamp: nowIso()
    });
  }

  const payloadError = validatePayload(payload);
  if (payloadError) {
    return buildResponse(400, {
      transaction_id: randomUUID(),
      provider: "visa",
      decision: "rejected",
      reason_code: payloadError,
      authorized_amount: 0,
      currency: payload.currency || "N/A",
      timestamp: nowIso()
    });
  }

  const providerDecision = validateAgainstProvider(payload);
  return buildResponse(providerDecision.statusCode, {
    transaction_id: randomUUID(),
    provider: "visa",
    decision: providerDecision.decision,
    reason_code: providerDecision.reasonCode,
    authorized_amount: providerDecision.decision === "approved" ? payload.amount : 0,
    currency: payload.currency,
    timestamp: nowIso()
  });
}

module.exports = {
  handlePayment
};
