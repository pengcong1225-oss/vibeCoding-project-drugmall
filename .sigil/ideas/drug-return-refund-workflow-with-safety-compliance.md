---
title: Drug Return & Refund Workflow with Safety Compliance
summary: 'Patients can initiate a return/refund for a drug order under specific conditions:
  wrong drug shipped, damaged packaging,'
status: open
complexity: medium
disposition: issue
priority: 10
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Drug Return & Refund Workflow with Safety Compliance

## Description

Patients can initiate a return/refund for a drug order under specific conditions: wrong drug shipped, damaged packaging, expired product, or a drug recall. The workflow enforces pharmaceutical safety rules: returned drugs are NEVER restocked (they go to a "quarantine" status for pharmacist inspection and disposal), temperature-sensitive drugs have a shorter return window, and controlled substances follow a separate regulatory return path. The patient uploads photos of the drug, selects a reason, and tracks return status. The admin/pharmacist reviews, approves/denies, and processes the refund through the existing payment system. A compliance log records every return for audit purposes.

## Rationale

Drug returns are fundamentally different from general e-commerce returns due to safety regulations. DrugMall currently has no visible return workflow, which is a gap for a pharmacy platform. This closes a critical post-purchase loop while respecting pharmaceutical compliance.

