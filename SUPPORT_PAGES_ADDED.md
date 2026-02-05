# Deployment Update: Support Pages

## ✅ Issue Resolved
- **Problem**: Footer links for Privacy Policy, Terms, etc. showed "Rebuilding..." error.
- **Cause**: These pages were missing from the static generation process.
- **Fix**: Added Phase 5 to `StaticPageGenerator` and created missing templates.

## 📄 New Pages Generated
1. `/privacy-policy.html`
2. `/terms-of-service.html`
3. `/disclaimer.html`
4. `/home-repair/about.html`
5. `/home-repair/methodology.html`
6. `/home-repair/editorial-policy.html`

## 🚀 Action Required
- **Re-deploy**: Copy the updated `src/main/resources/static/` folder to your web server.
