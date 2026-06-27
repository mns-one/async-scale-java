import { BASE_HTTP, BASE_WS } from "../constants";

export async function postSessionConfig({ clientId, size, interval_, target }) {
  return fetch(`${BASE_HTTP}/run`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      "clientId": clientId,
      "seedInterval": 5000,
      "processTarget": 35,
      "packetSize": 1000,
      "totalPackets": 5
    }),
  });
}

export function createSessionSocket(clientId) {
  return new WebSocket(`${BASE_WS}/telemetry?${clientId}`);
}

