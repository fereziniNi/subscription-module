import client from "./client";

export async function createSubscription(payload) {
  const { data } = await client.post("/subscriptions", payload);
  return data;
}

export async function getSubscriptionById(id) {
  const { data } = await client.get(`/subscriptions/${id}`);
  return data;
}

export async function listSubscriptionsByCustomerId(customerId) {
  const { data } = await client.get(`/subscriptions/customers/${customerId}`);
  return data;
}

export async function changeSubscriptionPlan(id, payload) {
  const { data } = await client.patch(`/subscriptions/${id}/plan`, payload);
  return data;
}

export async function cancelSubscriptionImmediately(id) {
  const { data } = await client.post(`/subscriptions/${id}/cancel`);
  return data;
}

export async function cancelSubscriptionAtPeriodEnd(id) {
  const { data } = await client.post(`/subscriptions/${id}/cancel-at-period-end`);
  return data;
}

export async function reverseScheduledCancellation(id) {
  const { data } = await client.post(`/subscriptions/${id}/reverse-cancellation`);
  return data;
}

export async function renewSubscription(id, payload) {
  const { data } = await client.post(`/subscriptions/${id}/renew`, payload);
  return data;
}

export async function generateInvoice(id) {
  const { data } = await client.post(`/subscriptions/${id}/invoices`);
  return data;
}