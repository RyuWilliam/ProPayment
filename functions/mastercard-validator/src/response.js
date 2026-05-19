"use strict";

const { randomUUID } = require("crypto");

function nowIso() {
  return new Date().toISOString();
}

function buildJsonResponse(statusCode, payload) {
  return {
    statusCode,
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  };
}

function buildRejectedResponse(provider, statusCode, reasonCode, currency = "N/A") {
  return buildJsonResponse(statusCode, {
    transaction_id: randomUUID(),
    provider,
    decision: "rejected",
    reason_code: reasonCode,
    authorized_amount: 0,
    currency,
    timestamp: nowIso()
  });
}

function buildDecisionResponse(provider, statusCode, decision, reasonCode, amount, currency) {
  return buildJsonResponse(statusCode, {
    transaction_id: randomUUID(),
    provider,
    decision,
    reason_code: reasonCode,
    authorized_amount: decision === "approved" ? amount : 0,
    currency,
    timestamp: nowIso()
  });
}

module.exports = {
  buildRejectedResponse,
  buildDecisionResponse
};
