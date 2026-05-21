---
title: AI-Powered Virtual Symptom Triage
summary: Before browsing drugs or consulting a doctor, patients can use a symptom
  triage tool. They describe their symptoms in na
status: open
complexity: medium
disposition: issue
priority: 7
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# AI-Powered Virtual Symptom Triage

## Description

Before browsing drugs or consulting a doctor, patients can use a symptom triage tool. They describe their symptoms in natural language (e.g., "headache, fever, sore throat for 2 days"). The Baichuan AI asks structured follow-up questions (duration, severity, existing conditions, current medications). It then produces: (a) possible conditions ranked by likelihood, (b) a recommended action: self-care / OTC drug suggestion / see pharmacist / see doctor urgently, (c) relevant OTC drug categories linked to the catalog, and (d) a mandatory disclaimer that this is not a medical diagnosis. The triage result can be shared with a doctor if the patient initiates a consultation.

## Rationale

DrugMall has AI (Baichuan) and IM but no guided pre-consultation triage. Patients often don't know whether they need OTC or Rx, which doctor specialization to pick, or how urgent their condition is. This reduces inappropriate self-medication and improves consultation efficiency.

