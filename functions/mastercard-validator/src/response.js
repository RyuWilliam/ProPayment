"use strict";

function buildJsonResponse(statusCode, payload) {
  return {
    statusCode,
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  };
}

function buildRejectedResponse(statusCode, reason) {
  return buildJsonResponse(statusCode, {
    status: "REJECTED",
    reason
  });
}

function buildDecisionResponse(statusCode, decision, reasonCode) {
  return buildJsonResponse(statusCode, {
    status: decision.toUpperCase(),
    reason: reasonCode
  });
}

module.exports = {
  buildRejectedResponse,
  buildDecisionResponse
};
