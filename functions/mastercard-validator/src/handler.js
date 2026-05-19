"use strict";

const { findCard } = require("./provider-store");
const { buildRejectedResponse, buildDecisionResponse } = require("./response");

function validatePayload(payload) {
  if (!payload || typeof payload !== "object") {
    return "INVALID_PAYLOAD";
  }

  const { holder_name, card_number, cvv } = payload;

  if (typeof holder_name !== "string" || holder_name.trim() === "") {
    return "INVALID_HOLDER_NAME";
  }

  if (typeof card_number !== "string" || !/^(5[1-5]|2[2-7])\d{14,17}$/.test(card_number)) {
    return "INVALID_CARD_NUMBER";
  }

  if (cvv !== undefined && (typeof cvv !== "string" || !/^\d{3,4}$/.test(cvv))) {
    return "INVALID_CVV";
  }

  return null;
}

function validateAgainstProvider(payload) {
  const card = findCard(payload.card_number);

  if (!card) {
    return { decision: "rejected", reasonCode: "CARD_NOT_FOUND", statusCode: 404 };
  }

  if (card.holderName.toLowerCase() !== payload.holder_name.trim().toLowerCase()) {
    return { decision: "rejected", reasonCode: "HOLDER_NAME_MISMATCH", statusCode: 422 };
  }

  if (card.status !== "active") {
    return { decision: "rejected", reasonCode: "CARD_BLOCKED", statusCode: 403 };
  }

  if (payload.cvv !== undefined && card.cvv !== payload.cvv) {
    return { decision: "rejected", reasonCode: "INVALID_CVV", statusCode: 401 };
  }

  return { decision: "approved", reasonCode: "APPROVED", statusCode: 200 };
}

async function handlePayment(requestBody) {
  let payload;
  try {
    payload = JSON.parse(requestBody || "{}");
  } catch {
    return buildRejectedResponse(400, "INVALID_JSON");
  }

  const payloadError = validatePayload(payload);
  if (payloadError) {
    return buildRejectedResponse(400, payloadError);
  }

  const providerDecision = validateAgainstProvider(payload);
  return buildDecisionResponse(
    providerDecision.statusCode,
    providerDecision.decision,
    providerDecision.reasonCode
  );
}

module.exports = {
  handlePayment
};
