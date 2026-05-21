---
title: Multi-Language & Accessibility Support for the Patient App
summary: The patient-facing app supports switching the UI language to major Chinese
  minority languages (Uyghur, Tibetan, Mongolia
status: open
complexity: large
disposition: issue
priority: 15
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Multi-Language & Accessibility Support for the Patient App

## Description

The patient-facing app supports switching the UI language to major Chinese minority languages (Uyghur, Tibetan, Mongolian, Zhuang) and English. All patient-visible text ¡ª navigation, drug information, AI consultation responses, prescription instructions, and notifications ¡ª is internationalized. Drug names display in both the selected language and Chinese for clarity in prescription contexts. The system also adds accessibility features: screen reader compatibility for visually impaired patients, high-contrast mode, and adjustable font sizes for elderly users. The admin backend includes a translation management interface for maintaining and reviewing translated content, with AI-assisted initial translation using Baichuan.

## Rationale

China has 55 officially recognized ethnic minorities, many concentrated in regions with distinct languages. The existing Vue 3 + TypeScript frontend can be i18n-enabled. This expands the addressable market to underserved populations and fulfills an accessibility and inclusivity mission aligned with healthcare.

