---
title: Telemedicine Video Consultation Integration
summary: Building on the existing Tencent TIM IM integration, add real-time video
  consultation between patients and certified doc
status: open
complexity: large
disposition: issue
priority: 11
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Telemedicine Video Consultation Integration

## Description

Building on the existing Tencent TIM IM integration, add real-time video consultation between patients and certified doctors. The workflow: patient requests a consultation (selecting reason and optionally uploading medical records), the system matches available doctors by specialty, and a video room is created. During the call, the doctor can view the patient's health profile, medication history, and write an electronic prescription that flows directly into the existing Prescription state machine. Post-consultation, the doctor writes a summary note and the patient receives the e-prescription for one-click fulfillment. Consultation records are stored encrypted and linked to the patient's health timeline.

## Rationale

The project already has Tencent TIM for IM, certified doctors (Doctor entity with licenseCode), prescription workflows, and patient health profiles. Video consultation completes the telemedicine loop and creates a powerful acquisition channel ¡ª patients consult, get prescriptions, and fill them all in one platform.

