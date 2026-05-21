---
title: Family Health Account & Proxy Management
summary: A primary user can create and manage sub-profiles for family members (elderly
  parents, children, spouse) within their ac
status: open
complexity: large
disposition: issue
priority: 9
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Family Health Account & Proxy Management

## Description

A primary user can create and manage sub-profiles for family members (elderly parents, children, spouse) within their account. Each sub-profile stores the family member's health information (allergies, chronic conditions, age, weight). The primary user can: purchase prescription drugs on behalf of a family member (with the family member's verified identity), manage their medication schedules, view their order history, and receive adherence alerts. For elderly parents, the system supports a "care circle" where multiple family members can co-manage. All operations respect real-name verification requirements ¡ª each sub-profile must pass identity verification independently. The prescription review process adapts to show the prescribing doctor which family member the medication is for.

## Rationale

In China's aging society, adult children commonly manage healthcare for elderly parents. The existing User entity with realName/idCard can be extended to a family model. This dramatically expands the addressable user base without requiring each family member to be digitally literate.

