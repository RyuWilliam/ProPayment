"use strict";

const { findCard } = require("./provider-store");
const { buildRejectedResponse, buildDecisionResponse } = require("./response");

function isIntegerBetween(value, min, max) {
  return Number.isInteger(value) && value >= min && value <= max;
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
    return buildRejectedResponse("visa", 400, "INVALID_JSON");
  }

  const payloadError = validatePayload(payload);
  if (payloadError) {
    return buildRejectedResponse("visa", 400, payloadError, payload.currency || "N/A");
  }

  const providerDecision = validateAgainstProvider(payload);
  return buildDecisionResponse(
    "visa",
    providerDecision.statusCode,
    providerDecision.decision,
    providerDecision.reasonCode,
    payload.amount,
    payload.currency
  );
}

module.exports = {
  handlePayment
};
