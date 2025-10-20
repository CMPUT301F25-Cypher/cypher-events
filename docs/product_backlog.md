# Product Backlog

This document summarizes all user stories for the **Cypher Event Lottery System** project.  
It corresponds to the entries in the [GitHub Project Board](https://github.com/orgs/CMPUT301F25-Cypher/projects/9).  
Each section below lists stories grouped by actor.  

---

## Summary
- Total user stories: 46
- Organized by: Entrant, Organizer, Admin
- MVP identification, risk, and priorities as per the Product Backlog document.

### Risk Level Assessment

| Risk Level | Description |
|-----------|-------------|
| ![Low](https://img.shields.io/badge/Risk-Low-green) | Straightforward implementation, clear requirements |
| ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | Some uncertainty, validation, or third‑party dependency |
| ![High](https://img.shields.io/badge/Risk-High-red) | Complex logic/algorithm, fairness, or external integration |

### Priority Level Assessment

| Priority | Description |
|---------|-------------|
| ![Must](https://img.shields.io/badge/Priority-Must-blue) | Core functionality required for MVP |
| ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | Important but not critical for MVP |
| ![Could](https://img.shields.io/badge/Priority-Could-silver) | Nice‑to‑have; later release |


---
### Entrant Stories

| User Story ID | Summary | Priority | Risk | MVP? |
|---------------|---------|----------|------|------|
| [US 01.01.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/1) | As an entrant, I want to join the waiting list for a specific event | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.01.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/2) | As an entrant, I want to leave the waiting list for a specific event | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 01.01.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/3) | As an entrant, I want to see a list of events I can join | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 01.01.04](https://github.com/CMPUT301F25-Cypher/Cypher/issues/4) | As an entrant, I want to filter events by my interests and availability | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 01.02.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/5) | As an entrant, I want to provide my personal info (name, email, optional phone) | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.02.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/6) | As an entrant, I want to update my profile info | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 01.02.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/7) | As an entrant, I want a history of my registered events | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 01.02.04](https://github.com/CMPUT301F25-Cypher/Cypher/issues/8) | As an entrant, I want to delete my profile | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 01.04.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/9) | As an entrant, I want a notification when I “win” the lottery | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.04.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/10) | As an entrant, I want a notification when I am not chosen | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 01.04.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/11) | As an entrant, I want to opt out of notifications | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.05.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/12) | As an entrant, I want another chance if a selected user declines | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![High](https://img.shields.io/badge/Risk-High-red) | ❌ |
| [US 01.05.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/13) | As an entrant, I want to accept my invitation | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.05.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/14) | As an entrant, I want to decline my invitation | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 01.05.04](https://github.com/CMPUT301F25-Cypher/Cypher/issues/15) | As an entrant, I want to know total entrants on the waiting list | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 01.05.05](https://github.com/CMPUT301F25-Cypher/Cypher/issues/16) | As an entrant, I want to view lottery selection criteria | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 01.06.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/17) | As an entrant, I want to view event details by scanning a QR code | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.06.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/18) | As an entrant, I want to sign up for an event from details view | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 01.07.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/19) | As an entrant, I want device-based identification (no login) | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![High](https://img.shields.io/badge/Risk-High-red) | ❌ |

---
### Organizer Stories

| User Story ID | Summary | Priority | Risk | MVP? |
|---------------|---------|----------|------|------|
| [US 02.01.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/20) | As an organizer, I want to create an event and generate a QR code | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 02.01.04](https://github.com/CMPUT301F25-Cypher/Cypher/issues/21) | As an organizer, I want to set a registration period | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 02.02.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/22) | As an organizer, I want to view entrants on my event’s waiting list | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 02.02.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/23) | As an organizer, I want to view on a map where entrants joined from | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![High](https://img.shields.io/badge/Risk-High-red) | ❌ |
| [US 02.02.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/24) | As an organizer, I want to enable/disable geolocation requirement | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 02.03.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/25) | As an organizer, I want to limit the number of entrants | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 02.04.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/26) | As an organizer, I want to upload an event poster | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 02.04.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/27) | As an organizer, I want to update an event poster | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 02.05.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/28) | As an organizer, I want to send notifications to chosen entrants (“winners”) | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 02.05.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/29) | As an organizer, I want to set the system to sample a specified number of attendees | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![High](https://img.shields.io/badge/Risk-High-red) | ❌ |
| [US 02.05.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/30) | As an organizer, I want to draw a replacement if someone cancels | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![High](https://img.shields.io/badge/Risk-High-red) | ❌ |
| [US 02.06.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/31) | As an organizer, I want to view a list of invited entrants | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 02.06.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/32) | As an organizer, I want to see a list of cancelled entrants | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 02.06.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/33) | As an organizer, I want to see a final list of enrolled entrants | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 02.06.04](https://github.com/CMPUT301F25-Cypher/Cypher/issues/34) | As an organizer, I want to cancel entrants that did not sign up | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 02.06.05](https://github.com/CMPUT301F25-Cypher/Cypher/issues/35) | As an organizer, I want to export a final entrant list (CSV) | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 02.07.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/36) | As an organizer, I want to send notifications to all entrants | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 02.07.02](https://github.com/CMPUT301F25-Cypher/Cypher/issues/37) | As an organizer, I want to notify selected entrants | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 02.07.03](https://github.com/CMPUT301F25-Cypher/Cypher/issues/38) | As an organizer, I want to notify cancelled entrants | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |

---
### Admin Stories

| User Story ID | Summary | Priority | Risk | MVP? |
|---------------|---------|----------|------|------|
| [US 03.01.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/39) | As an administrator, I want to remove events | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 03.02.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/40) | As an administrator, I want to remove profiles | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 03.03.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/41) | As an administrator, I want to remove images | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 03.04.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/42) | As an administrator, I want to browse events | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ✅ |
| [US 03.05.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/43) | As an administrator, I want to browse profiles | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ✅ |
| [US 03.06.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/44) | As an administrator, I want to browse uploaded images | ![Could](https://img.shields.io/badge/Priority-Could-silver) | ![Low](https://img.shields.io/badge/Risk-Low-green) | ❌ |
| [US 03.07.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/45) | As an administrator, I want to remove organizers who violate policy | ![Must](https://img.shields.io/badge/Priority-Must-blue) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |
| [US 03.08.01](https://github.com/CMPUT301F25-Cypher/Cypher/issues/46) | As an administrator, I want to review logs of notifications sent to entrants | ![Should](https://img.shields.io/badge/Priority-Should-lightgrey) | ![Medium](https://img.shields.io/badge/Risk-Medium-yellow) | ❌ |

---

> [!NOTE]
> Each story should include its **story points**, **risk level**, and **MVP flag** once finalized.
> Use this document as a quick offline reference for the GitHub Project Board.
> Keep it synchronized with the board for submission.