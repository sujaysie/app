# UserService OAuth Setup

## Google Cloud OAuth settings

1. Create an OAuth 2.0 Client ID in Google Cloud Console.
2. Configure the Authorized redirect URI:
   - `http://localhost:8080/login/oauth2/code/google`
3. Add your client credentials as environment variables before running the service:
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`

The service uses these values in `application.yaml` under `spring.security.oauth2.client`.
