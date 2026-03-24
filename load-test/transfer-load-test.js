import http from "k6/http";
import { check } from "k6";
import { Counter, Trend } from "k6/metrics";

// ── CLI Arguments ───────────────────────────────────────────
// k6 run -e RATE=20000 -e DURATION=1m -e MAX_VU=200 load-test/transfer-load-test.js
const RATE = parseInt(__ENV.RATE || "20000");
const DURATION = __ENV.DURATION || "1m";
const PRE_VU = parseInt(__ENV.PRE_VU || "50");
const MAX_VU = parseInt(__ENV.MAX_VU || "200");

// ── Custom Metrics ──────────────────────────────────────────
const transferSuccess = new Counter("transfer_success");
const transferFail = new Counter("transfer_fail");
const transferDuration = new Trend("transfer_duration", true);

// ── Test Options ────────────────────────────────────────────
export const options = {
  scenarios: {
    constant_load: {
      executor: "constant-arrival-rate",
      rate: RATE,
      timeUnit: "1m",
      duration: DURATION,
      preAllocatedVUs: PRE_VU,
      maxVUs: MAX_VU,
    },
  },
  thresholds: {
    http_req_duration: ["p(95)<3000"],
    http_req_failed: ["rate<0.3"],
  },
};

// ── Constants ───────────────────────────────────────────────
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const TOTAL_ACCOUNTS = 10000;
const PASSWORD = "1234";
const TRANSFER_AMOUNT = 100;

function randomInt(max) {
  return Math.floor(Math.random() * max) + 1;
}

function accountNumber(seq) {
  return "TEST-" + String(seq).padStart(10, "0");
}

// ── Default Function (VU iteration) ────────────────────────
export default function () {
  const senderId = randomInt(TOTAL_ACCOUNTS);
  let receiverId = randomInt(TOTAL_ACCOUNTS);
  while (receiverId === senderId) {
    receiverId = randomInt(TOTAL_ACCOUNTS);
  }

  const payload = JSON.stringify({
    senderAccount: accountNumber(senderId),
    senderPassword: PASSWORD,
    receiverAccount: accountNumber(receiverId),
    transferAmount: TRANSFER_AMOUNT,
  });

  const params = {
    headers: { "Content-Type": "application/json" },
  };

  const res = http.post(`${BASE_URL}/api/transfers`, payload, params);

  transferDuration.add(res.timings.duration);

  const ok = check(res, {
    "status is 200": (r) => r.status === 200,
  });

  if (ok) {
    transferSuccess.add(1);
  } else {
    transferFail.add(1);
  }
}
