# Release Checklist - Version 1.1.0

**Date**: February 13, 2026  
**Tester**: Saniya Yerzhan, Tulebaeva Marzhan 
**Build**: Release v1.1.0

---

## Installation & Setup

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 1 | App installs successfully from APK | ⬜ | |
| 2 | App icon appears correctly | ⬜ | |
| 3 | App launches without crash | ⬜ | |
| 4 | Splash screen displays properly | ⬜ | |
| 5 | Firebase connection established | ⬜ | |

---

## Authentication

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 6 | Sign up with valid email/password | ⬜ | test@example.com |
| 7 | Sign up with invalid email shows error | ⬜ | testexample.com |
| 8 | Sign up with short password shows error | ⬜ | 12345 (< 6 chars) |
| 9 | Sign up with mismatched passwords shows error | ⬜ | |
| 10 | Sign in with valid credentials | ⬜ | |
| 11 | Sign in with wrong password shows error | ⬜ | |
| 12 | Sign out returns to auth screen | ⬜ | |
| 13 | Session persists after app restart | ⬜ | Close and reopen |

---

## Recipe Details

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 14 | Recipe details load correctly | ⬜ | Mock data |
| 15 | Ingredients list displays | ⬜ | |
| 16 | Instructions display | ⬜ | |
| 17 | Favorite button toggles | ⬜ | |
| 18 | Navigate to comments works | ⬜ | |

---

## Meal Planner

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 19 | Add meal plan dialog opens | ⬜ | |
| 20 | Select day (Monday-Sunday) works | ⬜ | |
| 21 | Select meal type (Breakfast/Lunch/Dinner/Snack) | ⬜ | |
| 22 | Meal plan appears in planner after creation | ⬜ | |
| 23 | Meal plans grouped by day correctly | ⬜ | |
| 24 | Delete meal plan works | ⬜ | |
| 25 | Delete confirmation dialog appears | ⬜ | |

---

## Comments (Realtime)

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 26 | Comments load for recipe | ⬜ | |
| 27 | Add comment appears immediately | ⬜ | Realtime test |
| 28 | Cannot send empty comment | ⬜ | Button disabled |
| 29 | Edit own comment works | ⬜ | |
| 30 | Delete own comment works | ⬜ | |
| 31 | Cannot edit other user's comments | ⬜ | Multi-user test |
| 32 | Timestamp displays correctly | ⬜ | "Just now", "5m ago" |

---

## Offline Mode

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 33 | Turn off internet, app doesn't crash | ⬜ | Airplane mode |
| 34 | Show appropriate error message | ⬜ | |
| 35 | Retry button appears for failed operations | ⬜ | |
| 36 | Cached recipes still viewable (Partner A) | ⬜ | Room cache |

---

## Error Handling

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 37 | Empty meal planner shows empty state | ⬜ | New user |
| 38 | Empty comments shows empty state | ⬜ | |
| 39 | Network error shows retry option | ⬜ | |
| 40 | Firebase auth error shows user-friendly message | ⬜ | |
| 41 | Invalid input validated before submission | ⬜ | |

---

## Performance

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 42 | Comments load in < 2 seconds | ⬜ | Measure time |
| 43 | Meal planner loads in < 1 second | ⬜ | |
| 44 | UI remains responsive during operations | ⬜ | No freezing |
| 45 | Scrolling is smooth | ⬜ | 60 FPS |

---

## Navigation

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 46 | Bottom nav switches tabs correctly | ⬜ | Home/Planner/Profile |
| 47 | Back button navigation works | ⬜ | Details → Comments → Back |
| 48 | Deep linking works (if implemented) | ⬜ | |

---

## Security

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 49 | No API keys visible in APK | ⬜ | Decompile check |
| 50 | User can only see own meal plans | ⬜ | Firebase rules |
| 51 | User can only edit own comments | ⬜ | |

---

## Release Build Specific

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 52 | Debug logs disabled in release | ⬜ | Logcat check |
| 53 | ProGuard/R8 optimization successful | ⬜ | APK size |
| 54 | Signed APK verifies correctly | ⬜ | apksigner |
| 55 | Version number correct (1.1.0) | ⬜ | About screen |

---

## Summary

**Tests Passed**: ___ / 55  
**Tests Failed**: ___  
**Critical Issues**: ___  
**Build Status**: ⬜ Ready for Release | ⬜ Needs Fixes

**Tester Signature**: _______________  
**Date**: _______________