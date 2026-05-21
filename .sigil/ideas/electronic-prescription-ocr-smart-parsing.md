---
title: Electronic Prescription OCR & Smart Parsing
summary: 'Patients can upload a photo of a paper prescription, and the system uses
  OCR with AI enhancement to extract: drug names,'
status: open
complexity: large
disposition: issue
priority: 10
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Electronic Prescription OCR & Smart Parsing

## Description

Patients can upload a photo of a paper prescription, and the system uses OCR with AI enhancement to extract: drug names, dosages, quantity, prescribing doctor, hospital stamp, and date. The parsed data auto-populates the prescription submission form. The system flags low-confidence extractions for manual review. For prescriptions with official QR codes (increasingly common in Chinese hospitals), the system scans and validates against the regional health authority's prescription database where available. Unreadable or ambiguous prescriptions are routed to a human reviewer queue in the admin backend. The feature reduces manual data entry errors and speeds up the prescription-to-order conversion funnel.

## Rationale

The project already has a prescription workflow (Prescription state machine), AI integration (Baichuan), and doctor-facing tools. OCR bridges the offline-to-online gap that's common in Chinese healthcare where many hospitals still issue paper prescriptions. Directly reduces checkout abandonment.

