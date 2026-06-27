import { BASE_HTTP, BASE_WS } from "../constants";

export async function postSessionConfig({ clientId, count, size, interval_, target }) {
  return fetch(`${BASE_HTTP}/run`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      "clientId": clientId,
      "seedInterval": interval_*1000,
      "processTarget": target,
      "packetSize": size,
      "totalPackets": count
    }),
  });
}

export function createSessionSocket(clientId) {
  return new WebSocket(`${BASE_WS}/telemetry?${clientId}`);
}

