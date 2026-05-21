---
title: One-Click Reorder from Purchase History
summary: From the patient's order history, any past order can be reordered with a
  single click. The system pre-fills the cart wit
status: open
complexity: small
disposition: pr
priority: 1
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# One-Click Reorder from Purchase History

## Description

From the patient's order history, any past order can be reordered with a single click. The system pre-fills the cart with the exact same drugs, quantities, and dosage instructions. Before checkout, the system checks: (a) whether a valid prescription still exists for any Rx items (and prompts re-upload if expired), (b) whether any drugs are out of stock or discontinued (and suggests alternatives via AI), and (c) whether the shipping address is still valid. The reorder flow respects any new coupons or price changes since the original order. Patients see a "Reorder" button prominently on each past order card.

## Rationale

Chronic disease patients buy the same drugs repeatedly. A manual re-purchase flow is friction-heavy. This feature directly reduces churn for DrugMall's most valuable recurring customer segment, using existing order, cart, and prescription infrastructure.

