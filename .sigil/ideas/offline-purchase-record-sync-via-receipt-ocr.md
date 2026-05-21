---
title: Offline Purchase Record Sync via Receipt OCR
summary: Patients can upload a photo of a physical pharmacy receipt to import their
  offline drug purchases into their DrugMall he
status: open
complexity: medium
disposition: issue
priority: 15
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Offline Purchase Record Sync via Receipt OCR

## Description

Patients can upload a photo of a physical pharmacy receipt to import their offline drug purchases into their DrugMall health record. The system uses OCR to extract: drug names, quantities, purchase date, pharmacy name, and price. The extracted data is matched against the DrugMall drug catalog to link to the correct products. The imported records appear in the patient's medication timeline alongside online purchases, marked with an "Offline" badge. This gives a complete medication picture for drug interaction checking, AI health insights, and doctor consultations. If the OCR confidence is low, the patient can manually correct fields before saving. Duplicate detection prevents the same receipt from being imported twice.

## Rationale

Patients buy drugs both online and offline. DrugMall's health record and drug interaction features are only as good as the data they have. This bridges the offline gap, making every platform feature (timeline, interactions, AI insights) more complete and valuable.

