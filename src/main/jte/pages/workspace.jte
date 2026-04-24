@import com.livingcostcheck.home_repair.service.dto.inspection.InspectionWorkspaceSummary
@import java.util.List
@param List<InspectionWorkspaceSummary> recentWorkspaces = java.util.List.of()
@param String title = "Inspection Workspace | LifeVerdict"

@template.layout(
    title = title,
    description = "Recent inspection negotiation packets, case files, and reopenable buyer-side workspaces.",
    content = @`
        <div class="app-shell page-pad">
            <section class="tool-hero">
                <div class="hero-panel">
                    <div class="hero-eyebrow">Negotiation workspace</div>
                    <h1 class="hero-title">
                        Private case workspace is being held back until <span class="hero-accent">case ownership</span> is ready.
                    </h1>
                    <p class="hero-copy">
                        The product will eventually reopen live deal files, but public case lists are intentionally disabled during validation. Trust and case ownership come before repeat-use convenience.
                    </p>
                    <div class="mt-6">
                        <a href="/home-repair" class="button-primary">Start New Packet -></a>
                    </div>
                </div>

                <div class="tool-form-panel">
                    <div class="panel-kicker">What belongs here</div>
                    <h2 class="panel-title">Private deal files, not public recent activity.</h2>
                    <ul class="tool-panel-list">
                        <li>Property-specific packet drafts with buyer and agent context.</li>
                        <li>Access-controlled reopen flows instead of a public recent-case feed.</li>
                        <li>A workspace that only returns after private ownership and sharing rules exist.</li>
                    </ul>
                </div>
            </section>

            @if(recentWorkspaces.isEmpty())
                <section class="spotlight-grid">
                    <div class="spotlight-panel cool" style="grid-column: 1 / -1;">
                        <div class="section-kicker">Private access pending</div>
                        <h2>Public recent-case lists are disabled on purpose.</h2>
                        <p class="hero-copy">Keep using the packet flow and direct result links during validation. The shared workspace will return only after case ownership, privacy, and access rules are in place.</p>
                    </div>
                </section>
            @else
                <section class="signal-grid">
                    @for(InspectionWorkspaceSummary workspace : recentWorkspaces)
                        <a href="${workspace.detailUrl()}" class="signal-card" style="text-decoration: none; color: inherit;">
                            <div class="signal-step">${workspace.createdAtLabel()}</div>
                            <h3>${workspace.caseLabel()}</h3>
                            <p>${workspace.propertyAddress()}</p>
                            <p><strong>${workspace.clientLabel()}</strong> | ${workspace.agentLabel()}</p>
                            <p>${workspace.marketLabel()} | ${workspace.loanTypeLabel()}</p>
                            <p>${workspace.timelineLabel()}</p>
                        </a>
                    @endfor
                </section>
            @endif
        </div>
    `
)
