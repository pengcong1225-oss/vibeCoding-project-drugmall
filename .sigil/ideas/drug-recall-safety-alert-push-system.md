---
title: Drug Recall & Safety Alert Push System
summary: When a drug regulator (NMPA) issues a recall or safety warning for a specific
  drug batch, the admin can trigger a target
status: open
complexity: medium
disposition: issue
priority: 3
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Drug Recall & Safety Alert Push System

## Description

When a drug regulator (NMPA) issues a recall or safety warning for a specific drug batch, the admin can trigger a targeted notification to every patient who purchased that batch. The notification appears in-app, via SMS, and via the existing TIM IM channel. It includes: the recall reason, affected batch numbers, instructions (stop use, return to pharmacy, or dispose), and a one-click link to the return/refund workflow. Patients can acknowledge receipt. Admins track acknowledgment rates and can escalate non-responders. The system also prevents the recalled batch from being sold further.

## Rationale

DrugMall sells regulated pharmaceuticals where batch-level recalls are a real safety event. No existing feature handles this, yet it's a regulatory expectation and a critical patient safety mechanism. The existing TIM IM integration and order data make this achievable.

