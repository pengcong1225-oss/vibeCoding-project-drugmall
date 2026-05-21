---
title: Chronic Disease Refill Auto-Pilot & Subscription Service
summary: For patients on long-term medication for chronic conditions (hypertension,
  diabetes, etc.), the system offers a subscrip
status: open
complexity: large
disposition: issue
priority: 14
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Chronic Disease Refill Auto-Pilot & Subscription Service

## Description

For patients on long-term medication for chronic conditions (hypertension, diabetes, etc.), the system offers a subscription refill service. Based on the prescribed dosage and duration, it calculates when the patient will run out and automatically initiates a refill order at a configurable lead time. The patient receives a notification to confirm (or auto-confirms if they've opted in). For prescription drugs, the system detects when the current prescription is about to expire and prompts the patient to schedule a doctor consultation for renewal. The admin dashboard shows subscription metrics: active subscriptions, upcoming refills, churn risk, and revenue projections. Patients can pause, modify, or cancel subscriptions and view their refill history.

## Rationale

Chronic disease represents the highest lifetime value customer segment. The existing Prescription state machine, order system, and user profiles provide the foundation. This drives predictable recurring revenue and solves the real problem of patients forgetting to refill.

