---
title: Anonymous Health Consultation Mode
summary: Patients can initiate an IM consultation with a doctor or pharmacist in anonymous
  mode. In this mode, the patient's real
status: open
complexity: small
disposition: pr
priority: 2
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Anonymous Health Consultation Mode

## Description

Patients can initiate an IM consultation with a doctor or pharmacist in anonymous mode. In this mode, the patient's real name, ID card, phone number, and health records are masked from the consulting professional. The patient uses a session-scoped alias. The professional sees only the symptoms described, age range, gender, and relevant medical history that the patient explicitly chooses to share. All other personal identifiers are hidden. The consultation transcript is encrypted and automatically deleted after 30 days unless the patient opts to save it. The anonymous mode toggle is presented before starting any new consultation.

## Rationale

Embarrassment and privacy concerns prevent many patients (especially younger demographics and those with sensitive conditions like STIs or mental health) from seeking care. DrugMall already has IM and real-name auth ¡ª this adds a privacy layer that expands the addressable market.

