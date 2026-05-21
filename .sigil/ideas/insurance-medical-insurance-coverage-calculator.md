---
title: Insurance & Medical Insurance Coverage Calculator
summary: "For each drug product, display whether it's covered under China's national\
  \ medical insurance (\u533B\u4FDD\u76EE\u5F55) and at what reimburs"
status: open
complexity: medium
disposition: pr
priority: 5
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Insurance & Medical Insurance Coverage Calculator

## Description

For each drug product, display whether it's covered under China's national medical insurance (医保目录) and at what reimbursement tier (甲类/乙类). The patient can input their city and insurance type (职工医保/居民医保) to see an estimated out-of-pocket cost. For drugs not covered, the system shows the price and optionally suggests covered therapeutic alternatives (same active ingredient, different brand). The admin backend includes a drug-insurance mapping management tool to maintain coverage data. The checkout flow clearly separates insurance-covered amounts from self-pay amounts.

## Rationale

Medical insurance coverage is often the #1 factor in drug purchase decisions in China. Providing transparency builds trust and helps patients make informed choices. The admin backend already exists and can host the insurance mapping data management.

