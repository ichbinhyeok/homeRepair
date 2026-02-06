<style>
    .header-container {
        display: flex; 
        align-items: center; 
        justify-content: space-between;
        max-width: 1280px; 
        margin: 0 auto; 
        padding: 0 24px; 
        height: 64px;
    }
    .nav-links {
        display: flex; 
        align-items: center; 
        gap: 24px;
    }
    
    @media (max-width: 768px) {
        .header-container {
            /* Keep logical flow but reduce padding */
            padding: 0 16px;
            height: 56px; /* Slimmer header */
        }
        .nav-links a:not(.mobile-keep) {
            display: none !important;
        }
        .nav-links {
            gap: 12px;
        }
    }
</style>

<header style="position: sticky; top: 0; z-index: 40; width: 100%; background: rgba(255,255,255,0.9); backdrop-filter: blur(10px); border-bottom: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
    <div class="header-container">
        <a href="/home-repair" style="display: flex; align-items: center; gap: 8px; font-weight: 700; color: #1e293b; font-size: 1.125rem; text-decoration: none; transition: opacity 0.2s;">
            <span>🏠</span> LivingCost<span style="color: #3b82f6;">Check</span>
        </a>
        
        <nav class="nav-links">
            <a href="/home-repair" class="mobile-keep" style="font-size: 0.875rem; font-weight: 500; color: #64748b; text-decoration: none; transition: color 0.2s;" onmouseover="this.style.color='#3b82f6'" onmouseout="this.style.color='#64748b'">Home</a>
            <a href="/home-repair/methodology" style="font-size: 0.875rem; font-weight: 500; color: #64748b; text-decoration: none; transition: color 0.2s;" onmouseover="this.style.color='#3b82f6'" onmouseout="this.style.color='#64748b'">Methodology</a>
            <a href="/home-repair/about" style="font-size: 0.875rem; font-weight: 500; color: #64748b; text-decoration: none; transition: color 0.2s;" onmouseover="this.style.color='#3b82f6'" onmouseout="this.style.color='#64748b'">About</a>
            <a href="/home-repair/editorial-policy" style="font-size: 0.875rem; font-weight: 500; color: #64748b; text-decoration: none; transition: color 0.2s;" onmouseover="this.style.color='#3b82f6'" onmouseout="this.style.color='#64748b'">Editorial Policy</a>
            <a href="/home-repair/verdicts/states/tx.html" style="font-size: 0.875rem; font-weight: 600; color: #3b82f6; text-decoration: none; transition: color 0.2s;" onmouseover="this.style.color='#2563eb'" onmouseout="this.style.color='#3b82f6'">Browse by State</a>
        </nav>
    </div>
</header>
