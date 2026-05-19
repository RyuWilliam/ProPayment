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

function buildRejectedResponse(provider, statusCode, reasonCode) {
  return buildJsonResponse(statusCode, {
    transaction_id: randomUUID(),
    provider,
    decision: "rejected",
    reason_code: reasonCode,
    timestamp: nowIso()
  });
}

function buildDecisionResponse(provider, statusCode, decision, reasonCode) {
  return buildJsonResponse(statusCode, {
    transaction_id: randomUUID(),
    provider,
    decision,
    reason_code: reasonCode,
    timestamp: nowIso()
  });
}

module.exports = {
  buildRejectedResponse,
  buildDecisionResponse
};
