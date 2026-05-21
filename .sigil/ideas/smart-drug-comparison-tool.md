---
title: Smart Drug Comparison Tool
summary: Patients can select two or more similar drugs (e.g., two brands of amoxicillin,
  or two diabetes medications) and view a
status: open
complexity: medium
disposition: issue
priority: 1
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-20T15:37:26Z'
---

# Smart Drug Comparison Tool

## Description

Patients can select two or more similar drugs (e.g., two brands of amoxicillin, or two diabetes medications) and view a side-by-side comparison. The comparison covers: active ingredients, dosage forms, indications, contraindications, side effects, manufacturer, price per unit, insurance coverage status, user ratings, and storage requirements. The AI (Baichuan) can also generate a plain-language summary highlighting the key differences and which drug might be more suitable given the patient's known health profile (conditions, allergies). The comparison is accessible from drug detail pages and search results via a "Compare" checkbox.

## Rationale

DrugMall is a pharma e-commerce platform where patients face real medical decisions between similar products. Without comparison, they either bounce to external sites or make uninformed choices. This directly leverages the existing Baichuan AI integration and drug catalog data.

