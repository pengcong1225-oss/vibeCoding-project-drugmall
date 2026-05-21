---
title: Prescription Transfer Between Partner Pharmacies
summary: A patient with an active electronic prescription on DrugMall can transfer
  it to a different partner pharmacy (e.g., one
status: open
complexity: medium
disposition: issue
priority: 8
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Prescription Transfer Between Partner Pharmacies

## Description

A patient with an active electronic prescription on DrugMall can transfer it to a different partner pharmacy (e.g., one closer to home or with better stock). The transfer flow: patient selects the prescription, browses available pharmacies showing real-time stock and price for that drug, and initiates a transfer. The originating pharmacy's pharmacist must approve the release, and the receiving pharmacy's pharmacist must accept. Both approvals happen within the existing doctor/pharmacist workflow. The patient sees transfer status (pending release → pending acceptance → ready for pickup/delivery). Transfer history is preserved in the prescription timeline.

## Rationale

In China's evolving prescription circulation policy (处方流转), patients should not be locked into a single pharmacy. DrugMall's multi-pharmacy architecture can already support this with the existing prescription state machine and pharmacy entity model.

