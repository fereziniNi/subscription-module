import client from "./client";

export async function getAuthenticatedUserId() {
  const { data } = await client.get("/hello");
  return data.replace("Hello: ", "").trim();
}