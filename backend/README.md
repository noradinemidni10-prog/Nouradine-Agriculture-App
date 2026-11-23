# Nouradine Agriculture API

A server-side AI gateway for the Android app. The OpenAI credential stays on the server and must never be placed in the APK, Gradle files, or public repository.

## Run locally

1. Install Node.js 20 or later.
2. Copy `.env.example` to `.env` in your deployment environment.
3. Set `OPENAI_API_KEY` using your host's secret manager and choose an API model available to your account in `OPENAI_MODEL`.
4. Run `npm install`, then `npm run check`, then `npm start`.

## Routes

- `GET /health`: service readiness and whether the AI key is configured (never reveals the key).
- `POST /v1/assistant/respond`: JSON body `{ "message": "...", "previous_response_id": "resp_..." }`. Optional `image_data_url` accepts a base64 JPEG, PNG, or WebP image.

The response is `{ "answer": "...", "response_id": "resp_...", "model": "..." }`. Pass the returned response ID on the next turn to continue the conversation.

## Production requirements before public launch

Deploy behind HTTPS; set an explicit CORS allowlist for any web client; add real user authentication, per-account quotas, abuse monitoring, privacy/retention disclosures, and billing controls. The included IP rate limit is only a basic safeguard, not a substitute for authentication. Configure API spending limits and alerts in the provider dashboard. Do not expose this endpoint publicly without those controls.

The AI endpoint is not live merely because these source files exist. A hosting deployment and server-side secret configuration are still required.
