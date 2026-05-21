---
title: Pharmacy Store Locator with Real-Time Drug Availability
summary: Patients can search for nearby physical pharmacies that have a specific drug
  in stock. The feature shows a map (with the
status: open
complexity: large
disposition: issue
priority: 13
boldness: bold
generated_by: openai/deepseek-v4-pro
created: '2026-05-19T04:39:54Z'
---

# Pharmacy Store Locator with Real-Time Drug Availability

## Description

Patients can search for nearby physical pharmacies that have a specific drug in stock. The feature shows a map (with the patient's consent for location) with pharmacy pins colored by stock status (green = in stock, yellow = low stock, red = out of stock). Each pharmacy card displays: address, distance, business hours, whether it accepts medical insurance (医保定点), and estimated pickup time. Patients can reserve a drug for in-store pickup, which locks inventory for a configurable time window. For the admin backend, a store management module lets operators manage pharmacy locations, inventory feeds, and pickup order fulfillment.

## Rationale

This bridges online-to-offline (O2O), which is critical for urgent medication needs. Many patients want same-day access to drugs. The existing real-name verification and prescription workflow can seamlessly support "online prescribe → nearby pickup" as a fulfillment option alongside delivery.

