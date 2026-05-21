---
title: AI-Powered Drug Interaction & Contraindication Checker
summary: When a patient adds multiple prescription or OTC drugs to their cart, the
  system uses the existing Baichuan AI integrati
status: open
complexity: large
disposition: issue
priority: 7
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# AI-Powered Drug Interaction & Contraindication Checker

## Description

When a patient adds multiple prescription or OTC drugs to their cart, the system uses the existing Baichuan AI integration to analyze potential drug-drug interactions, contraindications with the patient's known allergies or conditions, and duplicate-ingredient warnings. The check runs automatically at checkout and during prescription review. Results are presented as a tiered warning (severe/moderate/mild) with clear "consult your doctor" disclaimers. Doctors also see this analysis during the prescription audit workflow to assist their review. The feature covers: cross-checking active ingredients across products, flagging known interactions from standard drug databases, and surfacing age/pregnancy-related contraindications based on the patient's health profile.

## Rationale

This project already has AI (Baichuan4-Turbo), prescription audit workflows (Prescription state machine), and real-name patient profiles ¡ª the key building blocks are in place. Drug safety is the #1 concern in pharma e-commerce, and this directly addresses a life-critical gap.

