---
title: Medication Adherence & Smart Reminder System
summary: Patients can set up medication schedules for purchased drugs (dosage, frequency,
  duration, time of day). The system send
status: open
complexity: large
disposition: issue
priority: 8
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Medication Adherence & Smart Reminder System

## Description

Patients can set up medication schedules for purchased drugs (dosage, frequency, duration, time of day). The system sends multi-channel reminders (in-app notification, SMS, and optionally WeChat mini-program push) when it's time to take medication. A daily adherence dashboard shows the patient their streak and completion rate. The system detects missed doses and can optionally alert a designated family contact. For chronic disease patients, the system predicts when they'll run out of medication and prompts early reorder. Doctors can view patient adherence data during follow-up consultations.

## Rationale

Medication non-adherence is a massive problem (30-50% of chronic disease patients). This creates sticky user retention, drives refill revenue, and provides valuable adherence data to prescribing doctors on the platform ¡ª directly leveraging the existing doctor-patient relationship model.

