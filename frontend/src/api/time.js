import client from "./client";

export async function getSimulatedTime() {
  const { data } = await client.get("/time");
  return data;
}

export async function advanceSimulatedTime(months) {
  const { data } = await client.post(`/time/advance/${months}`);
  return data;
}

export async function advanceSimulatedTimeByDays(days) {
  const { data } = await client.post(`/time/advance-days/${days}`);
  return data;
}

export async function resetSimulatedTime() {
  const { data } = await client.post("/time/reset");
  return data;
}
