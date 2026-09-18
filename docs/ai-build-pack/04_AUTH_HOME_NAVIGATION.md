# 04 — Authentication, Launch, Home & Navigation

## Objective
Implement the app entry experience and primary navigation.

## Required flows
### Launch
- Animated launch scene.
- Asset loading/progress indication.
- Recover an existing authenticated session.
- Route to Home or Auth.

### Auth
- Username/email/password registration.
- Password minimum 8 characters.
- Email verification handling.
- Login/logout.
- Google connection.
- Discord connection.
- Recover/change password.

### Home
Landing page contains:
- Store
- Account/Profile
- Friends
- Start Game
- Settings

The persistent top area should expose Gold, Gems, and Flames where appropriate.

### Navigation
Use typed route arguments or strongly validated navigation state. Never pass sensitive data in navigation arguments.

## Error handling
Handle:
- invalid credentials;
- expired session;
- verification required;
- network unavailable;
- OAuth cancellation;
- duplicate username;
- username validation;
- server errors.

## Security
- Auth state comes from Supabase.
- Never store raw passwords.
- Use secure platform storage for any local auth/session material.
- Never expose privileged Supabase credentials.

## Deliverables
Fully navigable auth/home shell with fake content allowed temporarily, but no hard-coded final game content.

## Verification
Test fresh install, returning session, logout, failed login, offline launch, and navigation back-stack behavior.
