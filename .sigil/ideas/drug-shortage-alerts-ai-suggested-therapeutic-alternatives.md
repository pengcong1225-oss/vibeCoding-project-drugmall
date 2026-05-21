---
title: Drug Shortage Alerts & AI-Suggested Therapeutic Alternatives
summary: When a drug goes out of stock, patients searching for it or with it in their
  cart see a "Temporarily Unavailable" badge
status: open
complexity: medium
disposition: issue
priority: 14
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Drug Shortage Alerts & AI-Suggested Therapeutic Alternatives

## Description

When a drug goes out of stock, patients searching for it or with it in their cart see a "Temporarily Unavailable" badge instead of a dead end. The system provides: (a) an estimated restock date (if known), (b) a "Notify Me When Available" button, and (c) AI-suggested therapeutic alternatives ¡ª drugs in the same ATC (Anatomical Therapeutic Chemical) classification with the same active ingredient or same therapeutic class, ranked by similarity, price, and stock status. The AI explanation notes why each alternative is suitable and what differs (brand vs. generic, dosage form, price). Alternatives respect the patient's known allergies and contraindications. Admin can configure alternative mappings and override AI suggestions.

## Rationale

Drug shortages are common in China due to supply chain and policy factors. A dead-end "out of stock" page means lost revenue and frustrated patients. This turns a gap into a conversion opportunity, using the existing Baichuan AI and drug catalog classification data.

