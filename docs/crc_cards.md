# CRC Cards

This document lists CRC (Class–Responsibilities–Collaborators) cards for the **Cypher Event Lottery System**.

## Entrant

### ![View](https://img.shields.io/badge/View-UI-green) EntrantActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Displays entrant’s current events and waiting lists | EntrantController |
| Displays QR code and opens event details | QRScanActivity, QRController |
| Displays entrant join/leave a waiting list | WaitingListController |
| Shows notifications and invitation prompts | NotificationController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) EntrantController
| **Responsibilities** | **Collaborators** |
|---|---|
| Receive user intents (join/leave, open profile, view notifications) | EntrantActivity |
| Request model actions (e.g., WaitingList.join/leave, Profile.load) and forward results to the view | WaitingList, Profile |
| Subscribe/unsubscribe to model updates and push changes to the view | WaitingList, Profile, Notification |
| Handle navigation and user messages (success/error) | EntrantActivity |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Entrant
| **Responsibilities** | **Collaborators** |
|---|---|
| Represent entrant data (name, contact, joined events) | ProfileDB |
| Expose profile/history retrieval and update APIs | ProfileDB, EventDB |
| Notify observers when entrant-related state changes | — |

---

## Event

### ![View](https://img.shields.io/badge/View-UI-green) EventActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Displays event details, registration window, capacity | EventController |
| Join/Leave waiting list from event page | WaitingListController |
| Shows poster and QR code | PosterActivity |
| Navigate to invited/cancelled/enrolled lists | OrganizerDashboardActivity |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) EventController
| **Responsibilities** | **Collaborators** |
|---|---|
| Receive create/edit/view intents from Event/Organizer views | EventActivity, OrganizerDashboardActivity |
| Validate inputs and route to model operations (create/update/load) | — |
| Request model to persist changes and return summary state for view | Event |
| Subscribe to Event model updates and update the view | Event |
| Coordinate with WaitingList/Notification flows (no business logic) | WaitingList, Notification |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Event
| **Responsibilities** | **Collaborators** |
|---|---|
| Store event metadata (title, description, schedule, capacity) | EventDB |
| Enforce registration window invariants; maintain links to WaitingList | WaitingList |
| Generate read models for details/poster/QR | PosterDB |
| Notify observers on state change | — |

---

## WaitingList

### ![View](https://img.shields.io/badge/View-UI-green) WaitingListActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Displays entrants on the waiting list and counts | WaitingListController |
| Organizer can manage entries (cancel/remove) | OrganizerController |
| Reflect updates pushed by controller/model | WaitingListController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) WaitingListController
| **Responsibilities** | **Collaborators** |
|---|---|
| Handle add/remove intents and call model APIs | WaitingListActivity |
| Validate eligibility inputs (e.g., duplicate join) before model call | — |
| Request counts/list snapshots from model; format for view | WaitingList |
| Subscribe to WaitingList updates and forward to view | WaitingList |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) WaitingList
| **Responsibilities** | **Collaborators** |
|---|---|
| Maintain entrant membership and counts | Entrant, Event |
| Provide fair sampling interface for lottery draw | Invitation, Lottery |
| Persist membership changes and notify observers | WaitingListDB |

---

## Invitation

### ![View](https://img.shields.io/badge/View-UI-green) InvitationActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Shows invitation status with Accept/Decline actions | InvitationController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) InvitationController
| **Responsibilities** | **Collaborators** |
|---|---|
| Handle Accept/Decline intents and call model transitions | InvitationActivity |
| Forward success/error toasts; navigate back to status views | EntrantActivity |
| On Decline, ask Lottery to request a replacement draw (no logic here) | Lottery |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Invitation
| **Responsibilities** | **Collaborators** |
|---|---|
| State machine: Pending → Accepted/Declined/Expired | InvitationDB |
| Create Enrollment on Accept; emit events to notify views | Enrollment, Event |
| Trigger replacement eligibility event on Decline | Lottery |

---

## Lottery

### ![View](https://img.shields.io/badge/View-UI-green) LotteryActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Organizer chooses sample size and starts draw | LotteryController |
| Displays results (selected, replacements) | LotteryController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) LotteryController
| **Responsibilities** | **Collaborators** |
|---|---|
| Start draw requests and replacement requests based on organizer input | OrganizerDashboardActivity |
| Ask Lottery model to sample N and persist results | Lottery |
| Forward summary to Notification and roster views | Notification, EventActivity |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Lottery
| **Responsibilities** | **Collaborators** |
|---|---|
| Sample K entrants fairly from WaitingList | WaitingList, LotteryDB |
| Record selections and expose result snapshots | Invitation |
| Notify observers for UI refresh | — |

---

## Profile

### ![View](https://img.shields.io/badge/View-UI-green) ProfileActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Shows and edits entrant profile fields | ProfileController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) ProfileController
| **Responsibilities** | **Collaborators** |
|---|---|
| Handle edit/save/delete intents; validate simple fields | ProfileActivity |
| Request Profile model to load/save/delete data | Profile |
| Forward results to the view; show success/error | ProfileActivity |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Profile
| **Responsibilities** | **Collaborators** |
|---|---|
| Store entrant personal info and preferences | ProfileDB |
| Provide load/save/delete operations; track history references | EventDB |
| Notify observers when profile changes | — |

---

## Organizer

### ![View](https://img.shields.io/badge/View-UI-green) OrganizerDashboardActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Shows organizer’s events and status | OrganizerController |
| Navigates to create/edit event and roster views | EventActivity |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) OrganizerController
| **Responsibilities** | **Collaborators** |
|---|---|
| Translate dashboard actions into Event/Lottery/Notification requests | OrganizerDashboardActivity |
| Validate inputs then call models; combine summaries for view | Event, Lottery, Notification |
| No persistence/business logic; only orchestration | — |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Organizer
| **Responsibilities** | **Collaborators** |
|---|---|
| Represent organizer profile and permissions | OrganizerDB |
| Link to owned Events | Event |
| Notify on organizer-level changes | — |

---

## Admin

### ![View](https://img.shields.io/badge/View-UI-green) AdminConsoleActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Displays events, profiles, and images for moderation | AdminController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) AdminController
| **Responsibilities** | **Collaborators** |
|---|---|
| Handle remove/ban/restore intents; confirm with user | AdminConsoleActivity |
| Call models to remove events/profiles/images | Event, Profile, Poster |
| Request notification logs for review | Notification |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Admin
| **Responsibilities** | **Collaborators** |
|---|---|
| Represent administrative capabilities and audit preferences | AdminDB |
| Expose moderation operations (remove events/profiles/images) | EventDB, UserDB, PosterDB |
| Expose queries for notification logs | NotificationDB |

---

## Notification

### ![View](https://img.shields.io/badge/View-UI-green) NotificationActivity
| **Responsibilities** | **Collaborators** |
|---|---|
| Displays notifications (results, updates); allows dismiss/ack | NotificationController |

### ![Controller](https://img.shields.io/badge/Controller-Interaction-yellow) NotificationController
| **Responsibilities** | **Collaborators** |
|---|---|
| Handle send/ack/dismiss intents and call model APIs | NotificationActivity |
| Forward delivery status to the view; route cohort sends | Notification, OrganizerController |

### ![Model](https://img.shields.io/badge/Model-Data%20%26%20Logic-blue) Notification
| **Responsibilities** | **Collaborators** |
|---|---|
| Persist notifications (type, recipients, status) | NotificationDB |
| Expose queries for recipient inbox and admin logs | Profile, Admin |
| Notify observers after sends or status changes | — |
