---
title: 'Admin Dashboard: Drug Inventory Intelligence & Expiry Early Warning'
summary: 'The admin backend gains an inventory intelligence dashboard that provides:
  real-time stock levels across all SKUs with c'
status: open
complexity: medium
disposition: pr
priority: 4
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Admin Dashboard: Drug Inventory Intelligence & Expiry Early Warning

## Description

The admin backend gains an inventory intelligence dashboard that provides: real-time stock levels across all SKUs with color-coded status (healthy/low/critical/out-of-stock), expiry date tracking with configurable early-warning thresholds (e.g., flag drugs expiring within 3 months), automated restock recommendations based on sales velocity and lead time, batch-level traceability, and a "quarantine" workflow for suspect batches (recall, quality issue). The system sends alerts (in-app + optional SMS/email) when stock hits critical levels or when batches approach expiry. A trend view shows historical stock movements, seasonal demand patterns, and slow-moving inventory that should be discounted or discontinued.

## Rationale

Drug expiry management is both a regulatory requirement and a major cost center. The admin backend exists but lacks proactive inventory intelligence. This reduces waste, ensures availability of critical drugs, and provides operational visibility that a pharma platform must have.

