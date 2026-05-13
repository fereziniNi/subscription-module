import client from "./client";

export async function registerUser(payload) {
  const { data } = await client.post("/register", payload);
  return data;
}

export async function loginUser(payload) {
  const { data } = await client.post("/authenticate", payload);
  return data;
}