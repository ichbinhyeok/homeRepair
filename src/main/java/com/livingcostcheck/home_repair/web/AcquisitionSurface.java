package com.livingcostcheck.home_repair.web;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public enum AcquisitionSurface {
    LETTER(
            "letter",
            "/inspection-response-letter",
            "Response Letter",
            "Inspection response letter check before you send it | LifeVerdict",
            "Check what to ask, what to cut, and what fallback to use before sending an inspection response letter.",
            "Inspection response letter pre-send check. Free during validation.",
            "inspection response letter",
            "after a home inspection?",
            "LifeVerdict helps buyer agents turn a draft inspection response into a sendable note: what to ask for, what to cut, and what fallback to use if the seller pushes back. The core flow stays free while we verify real use.",
            "Check The Letter Free",
            "acquisition_letter",
            "Letter angle",
            "Come in asking for the letter. Leave knowing whether the ask is safe to send.",
            "This surface is for buyer agents and buyers under contract who already know they need wording fast. The product checks send posture, number basis, evidence, and fallback before producing the note.",
            List.of(
                    "inspection response letter",
                    "seller credit request after inspection",
                    "what to ask for after home inspection"),
            List.of(
                    new PromptCard(
                            "Need an inspection response letter fast?",
                            "Start with the wording question, then let the pre-send check judge the ask and draft the buyer-agent note from the inspection findings.",
                            "Check the letter ->"),
                    new PromptCard(
                            "Want the letter to cite real evidence?",
                            "Upload the report and the strongest items stay tied to report pages or OCR-backed scans instead of floating as unsupported claims.",
                            "Use report-backed evidence ->"),
                    new PromptCard(
                            "Need something your agent can actually send?",
                            "The output is meant to compress the back-and-forth into one reviewed artifact, not force a buyer to write from a blank form.",
                            "Generate reviewed language ->")),
            List.of(
                    new FaqItem(
                            "Can I use this to write an inspection response letter?",
                            "Yes. The tool is built to check the ask, fallback posture, evidence, and agent-ready response note before it is sent."),
                    new FaqItem(
                            "What should lead the first response letter?",
                            "Lead with active leaks, unsafe systems, sewer issues, structural movement, and lender-visible findings rather than cosmetic noise."),
                    new FaqItem(
                            "Does the letter include the credit ask too?",
                            "Yes. The letter surface still checks the seller-credit ask so the wording stays tied to a defensible number."))),
    CREDIT(
            "credit",
            "/seller-credit-after-home-inspection",
            "Seller Credit",
            "Seller credit request check before you send it | LifeVerdict",
            "Check what credit to ask for, what to cut, and what fallback to use before sending a seller-credit request.",
            "Seller credit request pre-send check. Free during validation.",
            "seller credit request",
            "after the inspection?",
            "LifeVerdict helps buyer agents check a seller-credit request for number basis, evidence, seller pushback, financing boundary, cut items, and fallback before the ask leaves review mode. Buyers under contract can still use the same flow directly.",
            "Check Credit Ask Free",
            "acquisition_credit",
            "Credit angle",
            "Start from the credit ask, then test whether it can survive pushback.",
            "This surface is for buyers deciding whether closing-cost credit is the cleaner move. The pre-send check still uses the same inspection, evidence, and financing logic underneath.",
            List.of(
                    "seller credit request after inspection",
                    "ask seller for credit after home inspection",
                    "repair request vs seller credit"),
            List.of(
                    new PromptCard(
                            "Should I ask for repairs or seller credit?",
                            "This route assumes the buyer is leaning credit-first. The pre-send check helps keep that ask narrow enough to defend in a short window.",
                            "Check a credit-first ask ->"),
                    new PromptCard(
                            "Need a credit number backed by the report?",
                            "The tool ties the first ask to the strongest scoped exposure instead of using the house's entire future maintenance backlog.",
                            "Check the credit number ->"),
                    new PromptCard(
                            "Worried about lender-visible items?",
                            "The packet keeps financing pressure visible so the credit request is grounded in urgency, not only in preference.",
                            "See financing-aware wording ->")),
            List.of(
                    new FaqItem(
                            "Should I ask for seller credit after an inspection?",
                            "For many buyers under contract, a narrow seller-credit request is easier to defend and easier to close around than broad pre-close repairs."),
                    new FaqItem(
                            "Can seller credits help with inspection issues?",
                            "Often yes, as long as the lender allows the structure and the request stays inside allowable closing-cost rules."),
                    new FaqItem(
                            "What if the seller pushes back on the full ask?",
                            "The pre-send check includes a fallback posture so the buyer and agent are not improvising once the negotiation tightens."))),
    CREDIT_VS_REPAIR(
            "credit_vs_repair",
            "/repair-request-vs-seller-credit-after-inspection",
            "Credit Vs Repair",
            "Repair request vs seller credit after inspection | LifeVerdict",
            "Decide whether a repair request or seller credit is the cleaner post-inspection ask, then check whether that ask is defensible.",
            "Repair request vs seller credit pre-send check. Free during validation.",
            "repair request vs seller credit",
            "after the inspection?",
            "LifeVerdict helps buyer agents compare a repair-request posture against a seller-credit posture, then turns the stronger route into a narrow, evidence-backed ask with fallback language.",
            "Check Credit Vs Repair",
            "acquisition_credit_vs_repair",
            "Decision angle",
            "Start with the credit-vs-repair decision, then test which ask can survive the file.",
            "This surface is for users who are not just asking what the defects cost. They need to decide whether seller-managed repairs, closing credit, price change, or a narrower fallback is safer for the actual transaction.",
            List.of(
                    "repair request vs seller credit",
                    "ask for repairs or credit after inspection",
                    "seller credit instead of repairs"),
            List.of(
                    new PromptCard(
                            "Not sure whether to ask for repairs or credit?",
                            "The pre-send check keeps the same inspection facts but compares the send posture, timing risk, and fallback path before the request leaves review.",
                            "Check the better ask ->"),
                    new PromptCard(
                            "Worried the seller will choose cheap repairs?",
                            "Use the tool to keep serious items in scope while testing whether a credit-first request gives the buyer more control after closing.",
                            "Test credit-first posture ->"),
                    new PromptCard(
                            "Need a fallback if the seller says no?",
                            "The output keeps a lower fallback and do-not-lead list ready so the agent is not improvising during pushback.",
                            "Build fallback posture ->")),
            List.of(
                    new FaqItem(
                            "Is it better to ask for repairs or seller credit after inspection?",
                            "It depends on timing, loan treatment, repair control, evidence, and whether the seller can complete the work cleanly before closing."),
                    new FaqItem(
                            "When is seller credit cleaner than repairs?",
                            "Credit is often cleaner when the buyer wants control after closing, when repairs are hard to supervise, or when a narrow dollar ask is easier to negotiate."),
                    new FaqItem(
                            "When should a repair request lead instead?",
                            "A repair request can lead when the item affects safety, habitability, appraisal, or lender clearance and must be resolved before closing."))),
    ASK(
            "ask",
            "/what-to-ask-for-after-home-inspection",
            "What To Ask",
            "What should I ask for after a home inspection? | LifeVerdict",
            "Figure out what to ask for after a home inspection and check whether the ask is defensible.",
            "Inspection ask pre-send helper. Free during validation.",
            "ask the seller for",
            "after a home inspection?",
            "LifeVerdict helps buyer agents decide what belongs in the first ask, what should stay out, and whether the proposed request is safe to send inside the inspection window. Buyers under contract can still use the same flow directly.",
            "Check My First Ask",
            "acquisition_ask",
            "Question angle",
            "Start from the buyer's real question: what belongs in the first ask and what should be cut?",
            "This surface is for buyers who are not yet sure whether the answer should be repairs, credit, or a reduced request. The pre-send check narrows that choice from the inspection itself.",
            List.of(
                    "what should i ask for after home inspection",
                    "repair request after inspection",
                    "inspection negotiation help"),
            List.of(
                    new PromptCard(
                            "Not sure what belongs in the first ask?",
                            "The pre-send check separates leverage items from maintenance noise so the buyer does not overreach in the first round.",
                            "Check the first ask ->"),
                    new PromptCard(
                            "Trying to decide what to leave out?",
                            "Cosmetic items, wish-list upgrades, and long-tail maintenance can weaken the negotiation if they lead the packet.",
                            "Strip out cosmetic noise ->"),
                    new PromptCard(
                            "Need one first ask and one fallback?",
                            "The output is designed for the real inspection window, where clarity matters more than having every possible issue on paper.",
                            "Generate a reviewed ask ->")),
            List.of(
                    new FaqItem(
                            "What should I ask for after a home inspection?",
                            "Lead with safety, structural, sewer, leak, and lender-visible issues. Keep cosmetic and preference items out of the first ask."),
                    new FaqItem(
                            "Should I include every issue from the report?",
                            "Usually no. A stronger first ask is narrow and tied to the items that actually change financing, livability, or near-term exposure."),
                    new FaqItem(
                            "Can this still produce the response wording?",
                            "Yes. Even when the user starts from the decision question, the tool still outputs the reviewed note and credit packet."))),
    REPAIR_REQUEST(
            "repair_request",
            "/repair-request-after-home-inspection",
            "Repair Request",
            "Repair request after home inspection: what belongs in it? | LifeVerdict",
            "Figure out what belongs in a repair request after a home inspection and check whether it is defensible.",
            "Repair request pre-send check. Free during validation.",
            "repair request",
            "after a home inspection?",
            "LifeVerdict helps buyer agents check a repair request posture, while still showing when seller credit is cleaner for timing, control, or lender reasons. Buyers under contract can still use the same flow directly.",
            "Check Repair Request Free",
            "acquisition_repair_request",
            "Repair-request angle",
            "Start in repair-request language, but test the decision against leverage.",
            "This surface is for users thinking in repair addendum terms. The tool still shows when a credit-first packet is cleaner than asking the seller to coordinate the work before closing.",
            List.of(
                    "repair request after inspection",
                    "home inspection repair request",
                    "repair addendum after home inspection"),
            List.of(
                    new PromptCard(
                            "Need to know what belongs in the repair request?",
                            "The pre-send check highlights what is urgent enough to request and what should stay out because it only weakens the negotiation.",
                            "Check a narrower repair request ->"),
                    new PromptCard(
                            "Unsure whether the seller should repair it or credit it?",
                            "The same inspection item can lead to a repair request or a credit ask depending on timing, loan pressure, and who should control the work.",
                            "Compare request posture ->"),
                    new PromptCard(
                            "Need wording for the actual follow-up?",
                            "You still leave with a reviewed note and fallback posture instead of just a list of defects.",
                            "Generate the reviewed note ->")),
            List.of(
                    new FaqItem(
                            "What belongs in a repair request after a home inspection?",
                            "Serious safety, system, structural, leak, sewer, and lender-visible items usually belong first. Cosmetic and preference items usually do not."),
                    new FaqItem(
                            "Should I always ask the seller to do the repairs?",
                            "Not always. Sometimes a credit request is cleaner because it avoids rushed pre-close work and gives the buyer control after closing."),
                    new FaqItem(
                            "Can the tool still output a seller-credit fallback?",
                            "Yes. Even on the repair-request surface, the packet keeps a fallback posture ready if the seller resists direct repairs."))),
    OBJECTION(
            "objection",
            "/inspection-objection-after-home-inspection",
            "Inspection Objection",
            "Inspection objection after home inspection | LifeVerdict",
            "Prepare an inspection objection after a home inspection by checking whether the unsatisfactory items are defensible.",
            "Inspection objection pre-send check. Free during validation.",
            "inspection objection",
            "before the deadline?",
            "LifeVerdict helps buyer agents isolate the inspection items that are truly unsatisfactory, tie them to evidence, and check the ask before the inspection resolution window closes. Buyers under contract can still use the same flow directly.",
            "Check Objection Ask Free",
            "acquisition_objection",
            "Objection angle",
            "Use the state-form language if you must, but solve the same pre-send problem underneath.",
            "This surface is for markets and agents who think in objection or notice language. The wedge stays the same: identify the strongest unsatisfactory items and check whether the ask can actually be defended.",
            List.of(
                    "inspection objection",
                    "inspection objection notice",
                    "what is unsatisfactory after home inspection"),
            List.of(
                    new PromptCard(
                            "Need to identify what is actually unsatisfactory?",
                            "The pre-send check narrows the objection to the items that can affect financing, livability, or near-term exposure instead of repeating the whole report.",
                            "Check the objection ->"),
                    new PromptCard(
                            "Working under a short inspection resolution deadline?",
                            "The tool keeps the ask, fallback, and evidence in one artifact so the agent is not stitching the objection together manually.",
                            "Check the objection packet ->"),
                    new PromptCard(
                            "Need to show the lender-relevant impact too?",
                            "The packet carries lender-visible and appraisal-sensitive items forward so the objection stays grounded in transaction reality.",
                            "See lender-aware objection logic ->")),
            List.of(
                    new FaqItem(
                            "What is an inspection objection after a home inspection?",
                            "In some states the buyer formally gives notice that certain inspection items are unsatisfactory and need resolution before the deal proceeds."),
                    new FaqItem(
                            "What should go into an inspection objection?",
                            "The strongest objection focuses on unsafe, structural, leak, sewer, or lender-visible issues rather than cosmetic or routine maintenance items."),
                    new FaqItem(
                            "Can this still turn into a seller-credit ask?",
                            "Yes. The objection surface still builds the ask, fallback, and response note so the buyer can move from notice language into actual negotiation."))),
    DEADLINE(
            "deadline",
            "/inspection-contingency-deadline-after-home-inspection",
            "Inspection Deadline",
            "Inspection contingency deadline after home inspection | LifeVerdict",
            "Check a deadline-sensitive inspection ask before the response, objection, option, or contingency window loses leverage.",
            "Inspection contingency deadline pre-send check. Free during validation.",
            "inspection contingency deadline",
            "before the response window closes?",
            "LifeVerdict helps buyer agents narrow the ask before the inspection deadline, flag missing evidence, and decide whether the file is sendable, draft-only, or unsafe to send.",
            "Check Deadline-Sensitive Ask",
            "acquisition_deadline",
            "Deadline angle",
            "Start from the live deadline, then check whether the ask is narrow enough to send.",
            "This surface is for buyers and agents who are running out of inspection time. The tool keeps urgency visible without pretending a broad unsupported request is safe just because the deadline is near.",
            List.of(
                    "inspection contingency deadline",
                    "inspection response deadline",
                    "home inspection objection deadline"),
            List.of(
                    new PromptCard(
                            "Deadline is close and the ask is still messy?",
                            "Paste the proposed request and report findings so the pre-send check can cut weak items and show what still blocks a safe send.",
                            "Check before deadline ->"),
                    new PromptCard(
                            "Need to know if the file should stay in review?",
                            "Hard gates expose missing evidence, form path, financing risk, and ownership before the team forwards anything.",
                            "Run deadline gates ->"),
                    new PromptCard(
                            "Seller pushed back and time is short?",
                            "Use the fallback posture to keep the strongest ask alive without reopening the entire inspection report.",
                            "Check fallback posture ->")),
            List.of(
                    new FaqItem(
                            "What should I do before the inspection contingency deadline?",
                            "Narrow the ask to the defensible issues, confirm the contract form path, attach evidence, and avoid adding cosmetic noise under time pressure."),
                    new FaqItem(
                            "Can I send an inspection ask without the exact deadline?",
                            "The tool will keep the file in review mode when the exact deadline is missing because timing can change the buyer's leverage."),
                    new FaqItem(
                            "Does this replace my agent or contract form?",
                            "No. It helps prepare the ask and evidence, but the actual notice, amendment, objection, or termination path must match the contract."))),
    SELLER_REFUSED(
            "seller_refused",
            "/seller-refused-repairs-after-inspection",
            "Seller Refused",
            "Seller refused repairs after inspection | LifeVerdict",
            "Check the fallback after a seller refuses repairs or credits following a home inspection.",
            "seller refused repairs",
            "after the inspection?",
            "LifeVerdict helps buyer agents turn a seller refusal into a narrower fallback, evidence-backed response, or do-not-send warning instead of restarting the entire repair wishlist.",
            "Check Seller Refusal",
            "Start from the seller's refusal, then decide whether the buyer still has leverage.",
            "This surface is for files where the first ask already came back no. The pre-send check should not draft a louder version of the same request; it should isolate the strongest remaining items, fallback number, and next workflow step.",
            List.of("seller refused repairs after inspection", "seller won't fix inspection issues", "seller refused seller credit")),
    SELLER_COUNTER(
            "seller_counter",
            "/seller-counter-offer-after-home-inspection",
            "Seller Counter",
            "Seller counter offer after home inspection | LifeVerdict",
            "Review a seller counter offer after inspection and decide whether to accept, revise, or hold a fallback.",
            "seller counter offer",
            "after the inspection?",
            "LifeVerdict helps buyer agents compare the seller's counter against the original inspection scope, then preserve the strongest fallback without adding new noise.",
            "Check Seller Counter",
            "Start from the counter, then check what should survive.",
            "This surface is for the second negotiation step. The product keeps the counter tied to the original defects, not to a fresh generic repair list.",
            List.of("seller counter offer after inspection", "respond to seller counter inspection", "inspection counter offer")),
    SELLER_WONT_NEGOTIATE(
            "seller_wont_negotiate",
            "/seller-wont-negotiate-after-inspection",
            "Won't Negotiate",
            "Seller won't negotiate after inspection | LifeVerdict",
            "Check whether a buyer has any defensible inspection leverage when the seller says they will not negotiate.",
            "seller will not negotiate",
            "after the inspection?",
            "LifeVerdict helps separate real walk-away leverage from cosmetic disappointment when a seller refuses to negotiate after inspection.",
            "Check Remaining Leverage",
            "Start with the hard no, then decide whether there is still a file-safe move.",
            "This surface is for buyers and agents facing a flat refusal. The output should make the buyer's remaining leverage, fallback, and do-not-send boundaries explicit.",
            List.of("seller won't negotiate after inspection", "seller refuses to negotiate repairs", "inspection no negotiation")),
    FALLBACK(
            "fallback",
            "/inspection-negotiation-fallback-after-home-inspection",
            "Fallback Ask",
            "Inspection negotiation fallback after home inspection | LifeVerdict",
            "Build a fallback inspection ask after the first repair or credit request gets pushback.",
            "inspection negotiation fallback",
            "after seller pushback?",
            "LifeVerdict helps buyer agents reduce a broad ask into a defensible fallback tied only to the strongest inspection findings.",
            "Check Fallback Ask",
            "Start with the fallback, not the original wishlist.",
            "This surface is for files where the first ask is too broad or already challenged. The useful artifact is a smaller number, tighter scope, and cleaner reason to hold it.",
            List.of("inspection negotiation fallback", "seller credit fallback", "repair request fallback")),
    REDUCE_ASK(
            "reduce_ask",
            "/reduce-seller-credit-request-after-inspection",
            "Reduce Ask",
            "Reduce seller credit request after inspection | LifeVerdict",
            "Check how to reduce an overbroad seller-credit request without giving up the strongest inspection leverage.",
            "reduce seller credit request",
            "after inspection pushback?",
            "LifeVerdict helps cut weak items from a credit request while preserving serious safety, system, water, structural, and financing-sensitive leverage.",
            "Check Reduced Ask",
            "Start from the bloated ask and cut until it is defensible.",
            "This surface is for agents who know the first ask is too much. The pre-send check should produce a more credible number and explain what got removed.",
            List.of("reduce seller credit request after inspection", "lower inspection credit ask", "too much seller credit request")),
    REPAIR_ADDENDUM_REJECTED(
            "repair_addendum_rejected",
            "/seller-rejected-repair-addendum-after-inspection",
            "Rejected Addendum",
            "Seller rejected repair addendum after inspection | LifeVerdict",
            "Review the next move after a seller rejects the buyer's inspection repair addendum.",
            "seller rejected repair addendum",
            "after inspection?",
            "LifeVerdict helps convert a rejected repair addendum into a tighter credit posture, fallback, or review-only packet.",
            "Check Rejected Addendum",
            "Start from the rejected addendum and decide what still belongs.",
            "This surface is for a real transaction fork: keep repair language, switch to credit, counter with a smaller scope, or stop because the deadline/form path is wrong.",
            List.of("seller rejected repair addendum", "repair addendum rejected", "inspection addendum rejected")),
    RESPONSE_TO_COUNTER(
            "response_to_counter",
            "/response-to-seller-inspection-counter",
            "Counter Response",
            "Response to seller inspection counter | LifeVerdict",
            "Draft a defensible response to a seller inspection counter without reopening the entire report.",
            "response to seller inspection counter",
            "after pushback?",
            "LifeVerdict helps buyer agents answer a seller counter with a narrow reply, fallback amount, and evidence list.",
            "Check Counter Response",
            "Start from the seller counter, then keep the response inside the strongest items.",
            "This surface is for counter-response drafting. It should protect the buyer from adding new weak demands during a compressed deadline.",
            List.of("response to seller inspection counter", "reply to seller repair counter", "inspection counter response")),
    REASONABLE_REQUESTS(
            "reasonable_requests",
            "/reasonable-requests-after-home-inspection",
            "Reasonable Requests",
            "Reasonable requests after home inspection | LifeVerdict",
            "Check whether the buyer's post-inspection requests are reasonable, too broad, or unsafe to send.",
            "reasonable requests",
            "after a home inspection?",
            "LifeVerdict helps buyers and agents separate reasonable leverage from cosmetic or maintenance noise before the request goes out.",
            "Check Reasonable Requests",
            "Start from reasonableness, then test the ask against evidence and leverage.",
            "This surface captures high-intent buyers who are afraid of overreaching. The product should answer with exclusions and send posture, not generic advice.",
            List.of("reasonable requests after home inspection", "what are reasonable repair requests", "reasonable seller credit after inspection")),
    WHAT_NOT_TO_ASK(
            "what_not_to_ask",
            "/what-not-to-ask-for-after-home-inspection",
            "What Not To Ask",
            "What not to ask for after home inspection | LifeVerdict",
            "Find what to leave out of a home inspection request before it weakens the negotiation.",
            "what not to ask for",
            "after a home inspection?",
            "LifeVerdict helps identify cosmetic, preference, and long-tail maintenance items that should not lead the inspection ask.",
            "Check What To Cut",
            "Start with exclusions, then build the ask from what remains.",
            "This surface is deliberately anti-wishlist. It should make the product feel experienced by showing what not to send.",
            List.of("what not to ask for after home inspection", "home inspection requests not to make", "inspection repair request too much")),
    HOW_MUCH_CREDIT(
            "how_much_credit",
            "/how-much-seller-credit-to-ask-after-inspection",
            "Credit Amount",
            "How much seller credit to ask after inspection | LifeVerdict",
            "Check a seller-credit amount after inspection and label whether it is evidence-backed, estimate-only, or too broad.",
            "how much seller credit",
            "after inspection?",
            "LifeVerdict helps size the credit ask from scoped inspection exposure instead of the full repair backlog.",
            "Check Credit Amount",
            "Start with the dollar amount, then check whether the number can be defended.",
            "This surface is for the money question. The product should keep the number conservative unless evidence and quote support are strong.",
            List.of("how much seller credit to ask after inspection", "seller credit amount after inspection", "inspection credit calculator")),
    NEGOTIATION_CHECKLIST(
            "negotiation_checklist",
            "/home-inspection-negotiation-checklist",
            "Negotiation Checklist",
            "Home inspection negotiation checklist | LifeVerdict",
            "Run a post-inspection negotiation checklist before sending the buyer's repair or credit request.",
            "home inspection negotiation checklist",
            "before sending?",
            "LifeVerdict turns the checklist into a sendability review: deadline, evidence, form path, financing boundary, fallback, and excluded items.",
            "Run Negotiation Checklist",
            "Start from the checklist, then produce the packet.",
            "This surface is for users who need structure before they draft. The checklist should end in a tool output, not a static article.",
            List.of("home inspection negotiation checklist", "inspection negotiation checklist", "post inspection checklist")),
    REQUEST_LIST(
            "request_list",
            "/buyer-repair-request-list-after-inspection",
            "Request List",
            "Buyer repair request list after inspection | LifeVerdict",
            "Turn a buyer repair request list into a narrower inspection ask with exclusions and fallback.",
            "buyer repair request list",
            "after inspection?",
            "LifeVerdict helps convert a raw repair list into a defensible first ask instead of forwarding the entire inspection report.",
            "Check Request List",
            "Start from the list, then remove weak items.",
            "This surface competes against simple request-list builders by adding judgment: what survives, what gets cut, and what evidence is missing.",
            List.of("buyer repair request list after inspection", "inspection repair request list", "home inspection request list")),
    REPORT_NEGOTIATION_TOOL(
            "report_negotiation_tool",
            "/inspection-report-negotiation-tool",
            "Report Negotiation Tool",
            "Inspection report negotiation tool | LifeVerdict",
            "Paste inspection report findings and turn them into a defensible negotiation packet.",
            "inspection report negotiation tool",
            "for the buyer response?",
            "LifeVerdict is a tool-first surface for users who already have report findings and need a negotiation artifact, not another blog post.",
            "Open Negotiation Tool",
            "Start from the report, then build the buyer-side negotiation packet.",
            "This surface names the product category directly for searchers looking for a tool. The page should prove there is an actual workflow behind the claim.",
            List.of("inspection report negotiation tool", "home inspection negotiation tool", "inspection report to repair request")),
    FHA_REPAIRS(
            "fha_repairs",
            "/fha-inspection-repairs-seller-credit",
            "FHA Repairs",
            "FHA inspection repairs and seller credit | LifeVerdict",
            "Check an FHA-sensitive inspection repair or credit request before it creates closing risk.",
            "FHA inspection repairs",
            "and seller credit?",
            "LifeVerdict helps buyers and agents keep FHA-sensitive defects, credit language, and lender-confirmation warnings separated.",
            "Check FHA Ask",
            "Start from FHA financing, then avoid pretending credit always cures repair risk.",
            "This surface is for FHA files where safety, habitability, and appraisal-sensitive issues may need more than a simple closing credit.",
            List.of("FHA inspection repairs seller credit", "FHA required repairs after inspection", "FHA seller credit repairs")),
    VA_REPAIRS(
            "va_repairs",
            "/va-inspection-repairs-seller-credit",
            "VA Repairs",
            "VA inspection repairs and seller credit | LifeVerdict",
            "Check a VA-sensitive inspection repair or seller-credit request before it is sent.",
            "VA inspection repairs",
            "and seller credit?",
            "LifeVerdict helps agents keep VA safety and property-condition concerns visible without acting like a lender ruling.",
            "Check VA Ask",
            "Start from VA financing, then label what needs lender confirmation.",
            "This surface is for VA deals where the buyer needs a narrow ask and clear warning that credits and required repairs are not always interchangeable.",
            List.of("VA inspection repairs seller credit", "VA required repairs after inspection", "VA seller credit repairs")),
    LENDER_REQUIRED_REPAIRS(
            "lender_required_repairs",
            "/lender-required-repairs-after-inspection",
            "Lender Repairs",
            "Lender-required repairs after inspection | LifeVerdict",
            "Check whether inspection findings should be treated as lender-sensitive before asking for credit or repairs.",
            "lender-required repairs",
            "after inspection?",
            "LifeVerdict flags lender-visible inspection issues and keeps the buyer from framing them as ordinary cosmetic concessions.",
            "Check Lender-Sensitive Ask",
            "Start from lender risk, then separate repair path from credit path.",
            "This surface is for financed files where a wrong credit-only ask can create closing friction. The product should force confirmation language.",
            List.of("lender required repairs after inspection", "lender required repairs seller credit", "inspection repairs lender")),
    APPRAISAL_REQUIRED_REPAIRS(
            "appraisal_required_repairs",
            "/appraisal-required-repairs-after-inspection",
            "Appraisal Repairs",
            "Appraisal-required repairs after inspection | LifeVerdict",
            "Check an appraisal-sensitive repair issue before folding it into a seller-credit ask.",
            "appraisal-required repairs",
            "after inspection?",
            "LifeVerdict helps keep inspection negotiation and appraisal-sensitive repair treatment distinct enough for lender review.",
            "Check Appraisal-Sensitive Ask",
            "Start from appraisal risk, then avoid overclaiming certainty.",
            "This surface is for buyers worried that a defect may affect appraisal or underwriting. The tool should label risk and require confirmation.",
            List.of("appraisal required repairs after inspection", "appraisal required repairs seller credit", "appraisal repairs inspection")),
    SELLER_CREDIT_LIMITS(
            "seller_credit_limits",
            "/seller-credit-limits-after-home-inspection",
            "Credit Limits",
            "Seller credit limits after home inspection | LifeVerdict",
            "Check whether a post-inspection seller-credit request may need lender or closing-cost review.",
            "seller credit limits",
            "after inspection?",
            "LifeVerdict helps buyers avoid treating every repair issue as an unlimited credit request by keeping loan and closing-cost review visible.",
            "Check Credit Limit Risk",
            "Start from credit-limit risk, then label the ask as review-needed where appropriate.",
            "This surface should be conservative. It is not a closing-cost law page; it is a warning layer inside an inspection ask pre-send check.",
            List.of("seller credit limits after home inspection", "seller credit limit inspection repairs", "closing cost credit inspection")),
    REPAIR_ADDENDUM(
            "repair_addendum",
            "/home-inspection-repair-addendum",
            "Repair Addendum",
            "Home inspection repair addendum | LifeVerdict",
            "Check what belongs in a home inspection repair addendum before turning findings into contract language.",
            "home inspection repair addendum",
            "before it is sent?",
            "LifeVerdict helps narrow repair-addendum scope and keep the final form path visible for agent or broker review.",
            "Check Repair Addendum",
            "Start from addendum language, then check if the scope is too broad.",
            "This surface is for form-adjacent users. The product should support drafting without pretending to complete the legal form.",
            List.of("home inspection repair addendum", "inspection repair addendum", "repair addendum after inspection")),
    INSPECTION_AMENDMENT(
            "inspection_amendment",
            "/home-inspection-repair-amendment",
            "Repair Amendment",
            "Home inspection repair amendment | LifeVerdict",
            "Check an inspection repair or credit amendment before it leaves review.",
            "home inspection repair amendment",
            "after inspection?",
            "LifeVerdict helps translate inspection findings into a narrower amendment posture with evidence, fallback, and review gates.",
            "Check Repair Amendment",
            "Start from amendment posture, then verify the ask is defensible.",
            "This surface is for users who think in amendment language. It should keep contract review visible and avoid free-form overreach.",
            List.of("home inspection repair amendment", "inspection amendment after repairs", "seller credit amendment after inspection")),
    CONTINGENCY_REMOVAL(
            "contingency_removal",
            "/inspection-contingency-removal-after-repairs",
            "Contingency Removal",
            "Inspection contingency removal after repairs | LifeVerdict",
            "Check whether the inspection ask is settled enough before contingency removal language is considered.",
            "inspection contingency removal",
            "after repairs?",
            "LifeVerdict helps buyers keep repair, credit, evidence, and deadline status visible before the file moves toward contingency removal.",
            "Check Before Removal",
            "Start from contingency removal risk, then confirm the repair or credit terms are actually settled.",
            "This surface is for a high-risk timing moment. The product should warn when the ask is not resolved enough to treat the issue as closed.",
            List.of("inspection contingency removal after repairs", "remove inspection contingency repairs", "inspection contingency removal")),
    RESOLUTION_DEADLINE(
            "resolution_deadline",
            "/inspection-resolution-deadline",
            "Resolution Deadline",
            "Inspection resolution deadline | LifeVerdict",
            "Check an inspection-resolution deadline packet before the buyer loses leverage.",
            "inspection resolution deadline",
            "before it closes?",
            "LifeVerdict helps agents keep objection, resolution, counter, and fallback posture visible when the resolution deadline is close.",
            "Check Resolution Deadline",
            "Start from the resolution deadline, then decide what can still be sent.",
            "This surface is for markets where objection and resolution are separate timing events. The packet should show what is still live.",
            List.of("inspection resolution deadline", "inspection objection resolution deadline", "home inspection resolution deadline")),
    OBJECTION_NOTICE(
            "objection_notice",
            "/inspection-objection-notice",
            "Objection Notice",
            "Inspection objection notice | LifeVerdict",
            "Prepare the scope for an inspection objection notice without turning the report into a wishlist.",
            "inspection objection notice",
            "before the deadline?",
            "LifeVerdict helps isolate unsatisfactory items, evidence, and fallback posture before the objection notice is drafted.",
            "Check Objection Notice",
            "Start from notice language, then keep the objection narrow.",
            "This surface is for objection-notice searchers. The product supports scope and evidence, not official form completion.",
            List.of("inspection objection notice", "home inspection objection notice", "objection notice after inspection")),
    ROOF_CREDIT(
            "roof_credit",
            "/roof-repair-credit-after-inspection",
            "Roof Credit",
            "Roof repair credit after inspection | LifeVerdict",
            "Check a roof repair credit request after inspection before the buyer asks too much or too little.",
            "roof repair credit",
            "after inspection?",
            "LifeVerdict helps decide whether the roof issue should lead, needs a quote, or should stay as verification instead of a full replacement demand.",
            "Check Roof Credit",
            "Start from the roof issue, then check evidence and quote-needed status.",
            "This surface is issue-specific but still inside the same transaction wedge: scope the ask, label evidence, and avoid overclaiming.",
            List.of("roof repair credit after inspection", "roof issue seller credit", "roof replacement credit inspection")),
    SEWER_SCOPE_CREDIT(
            "sewer_scope_credit",
            "/sewer-scope-seller-credit-after-inspection",
            "Sewer Credit",
            "Sewer scope seller credit after inspection | LifeVerdict",
            "Check a sewer-scope seller-credit request after inspection with evidence and fallback boundaries.",
            "sewer scope seller credit",
            "after inspection?",
            "LifeVerdict helps keep sewer findings in the lead scope when the evidence supports real backup, blockage, or near-term failure risk.",
            "Check Sewer Credit",
            "Start from the sewer scope, then tie the ask to evidence.",
            "This surface targets a common high-leverage inspection issue while avoiding broad plumbing-cost content.",
            List.of("sewer scope seller credit after inspection", "sewer repair credit inspection", "sewer scope negotiation")),
    ELECTRICAL_REQUEST(
            "electrical_request",
            "/electrical-repair-request-after-inspection",
            "Electrical Request",
            "Electrical repair request after inspection | LifeVerdict",
            "Check an electrical repair request after inspection before it is framed as cosmetic or routine.",
            "electrical repair request",
            "after inspection?",
            "LifeVerdict helps buyers separate safety-relevant electrical findings from minor maintenance and attach the right evidence.",
            "Check Electrical Request",
            "Start from the electrical issue, then decide if it belongs in the lead ask.",
            "This surface is for safety/system leverage, not generic electrical-cost research.",
            List.of("electrical repair request after inspection", "seller credit electrical inspection", "electrical issues home inspection")),
    FOUNDATION_CREDIT(
            "foundation_credit",
            "/foundation-repair-credit-after-inspection",
            "Foundation Credit",
            "Foundation repair credit after inspection | LifeVerdict",
            "Check a foundation repair credit request after inspection before sending a high-stakes ask.",
            "foundation repair credit",
            "after inspection?",
            "LifeVerdict helps keep foundation concerns evidence-heavy, quote-aware, and narrow enough to defend.",
            "Check Foundation Credit",
            "Start from the foundation concern, then test if the ask is supportable.",
            "This surface is for serious structural negotiation. The output should warn when specialist evidence is missing.",
            List.of("foundation repair credit after inspection", "foundation issue seller credit", "foundation inspection negotiation")),
    MOLD_CREDIT(
            "mold_credit",
            "/mold-found-during-home-inspection-seller-credit",
            "Mold Credit",
            "Mold found during home inspection: seller credit check | LifeVerdict",
            "Check how to frame a mold-related seller-credit or repair request after a home inspection.",
            "mold found during home inspection",
            "seller credit?",
            "LifeVerdict helps keep mold-like findings cautious, evidence-based, and tied to specialist confirmation instead of overclaiming.",
            "Check Mold Ask",
            "Start from the mold concern, then label what needs confirmation.",
            "This surface is high-intent but trust-sensitive. The product should avoid diagnosis language and keep review gates visible.",
            List.of("mold found during home inspection seller credit", "mold inspection seller credit", "mold repair request after inspection")),
    HVAC_CREDIT(
            "hvac_credit",
            "/hvac-repair-credit-after-inspection",
            "HVAC Credit",
            "HVAC repair credit after inspection | LifeVerdict",
            "Check an HVAC repair credit request after inspection before sending the seller ask.",
            "HVAC repair credit",
            "after inspection?",
            "LifeVerdict helps decide whether HVAC findings are lead leverage, verify-next items, or too speculative without a quote.",
            "Check HVAC Credit",
            "Start from the HVAC issue, then check age, failure, quote, and fallback.",
            "This surface captures mechanical-system intent without drifting into homeowner maintenance content.",
            List.of("HVAC repair credit after inspection", "HVAC seller credit inspection", "air conditioner credit after inspection")),
    PLUMBING_LEAK_CREDIT(
            "plumbing_leak_credit",
            "/plumbing-leak-seller-credit-after-inspection",
            "Plumbing Leak Credit",
            "Plumbing leak seller credit after inspection | LifeVerdict",
            "Check a plumbing leak seller-credit request after inspection and keep the ask evidence-backed.",
            "plumbing leak seller credit",
            "after inspection?",
            "LifeVerdict helps keep active leak findings in scope while cutting unrelated plumbing wish-list items.",
            "Check Plumbing Leak",
            "Start from the active leak, then separate lead scope from maintenance.",
            "This surface focuses on active water risk, not broad plumbing budgeting.",
            List.of("plumbing leak seller credit after inspection", "plumbing repair credit inspection", "active leak seller credit")),
    WATER_INTRUSION_CREDIT(
            "water_intrusion_credit",
            "/water-intrusion-seller-credit-after-inspection",
            "Water Intrusion",
            "Water intrusion seller credit after inspection | LifeVerdict",
            "Check a water-intrusion seller-credit request after inspection before the ask is sent.",
            "water intrusion seller credit",
            "after inspection?",
            "LifeVerdict helps distinguish active water intrusion from cosmetic staining and mark what evidence is needed.",
            "Check Water Intrusion",
            "Start from water evidence, then decide what belongs in the ask.",
            "This surface is issue-specific and evidence-heavy, which is the right kind of narrow SEO expansion for the wedge.",
            List.of("water intrusion seller credit after inspection", "water damage credit home inspection", "water intrusion repair request")),
    POLYBUTYLENE_CREDIT(
            "polybutylene_credit",
            "/polybutylene-pipes-seller-credit-after-inspection",
            "Polybutylene Credit",
            "Polybutylene pipes seller credit after inspection | LifeVerdict",
            "Check a polybutylene-pipe seller-credit request after inspection before framing the buyer ask.",
            "polybutylene pipes seller credit",
            "after inspection?",
            "LifeVerdict helps treat polybutylene as a transaction risk signal that needs scope, evidence, and review rather than a vague plumbing complaint.",
            "Check Polybutylene Ask",
            "Start from the pipe risk, then label evidence and fallback.",
            "This surface targets a known inspection red flag without rebuilding broad plumbing pSEO.",
            List.of("polybutylene pipes seller credit after inspection", "polybutylene inspection negotiation", "poly b seller credit")),
    FPE_PANEL_CREDIT(
            "fpe_panel_credit",
            "/federal-pacific-panel-seller-credit-after-inspection",
            "FPE Panel Credit",
            "Federal Pacific panel seller credit after inspection | LifeVerdict",
            "Check a Federal Pacific panel seller-credit or repair request after inspection.",
            "Federal Pacific panel seller credit",
            "after inspection?",
            "LifeVerdict helps keep FPE or Stab-Lok panel findings framed as safety/system leverage with evidence and lender review where needed.",
            "Check FPE Panel Ask",
            "Start from the panel finding, then check safety, evidence, and financing risk.",
            "This surface is a narrow red-flag page: high intent, strong transaction relevance, and no drift into general electrical SEO.",
            List.of("Federal Pacific panel seller credit after inspection", "FPE panel seller credit", "Stab-Lok panel inspection credit"));

    private static final AcquisitionSurface DEFAULT_FALLBACK = LETTER;
    private static final List<AcquisitionSurface> INDEXABLE_SURFACES = List.of(values());

    private final String code;
    private final String path;
    private final String navLabel;
    private final String pageTitle;
    private final String pageDescription;
    private final String heroEyebrow;
    private final String heroAccent;
    private final String heroQuestionSuffix;
    private final String heroLead;
    private final String primaryCtaLabel;
    private final String primaryAnalyticsLabel;
    private final String panelKicker;
    private final String panelTitle;
    private final String panelNote;
    private final List<String> intentChips;
    private final List<PromptCard> promptCards;
    private final List<FaqItem> faqItems;

    AcquisitionSurface(
            String code,
            String path,
            String navLabel,
            String pageTitle,
            String pageDescription,
            String heroEyebrow,
            String heroAccent,
            String heroQuestionSuffix,
            String heroLead,
            String primaryCtaLabel,
            String primaryAnalyticsLabel,
            String panelKicker,
            String panelTitle,
            String panelNote,
            List<String> intentChips,
            List<PromptCard> promptCards,
            List<FaqItem> faqItems) {
        this.code = code;
        this.path = path;
        this.navLabel = navLabel;
        this.pageTitle = pageTitle;
        this.pageDescription = pageDescription;
        this.heroEyebrow = heroEyebrow;
        this.heroAccent = heroAccent;
        this.heroQuestionSuffix = heroQuestionSuffix;
        this.heroLead = heroLead;
        this.primaryCtaLabel = primaryCtaLabel;
        this.primaryAnalyticsLabel = primaryAnalyticsLabel;
        this.panelKicker = panelKicker;
        this.panelTitle = panelTitle;
        this.panelNote = panelNote;
        this.intentChips = intentChips;
        this.promptCards = promptCards;
        this.faqItems = faqItems;
    }

    AcquisitionSurface(
            String code,
            String path,
            String navLabel,
            String pageTitle,
            String pageDescription,
            String heroAccent,
            String heroQuestionSuffix,
            String heroLead,
            String primaryCtaLabel,
            String panelTitle,
            String panelNote,
            List<String> intentChips) {
        this(
                code,
                path,
                navLabel,
                pageTitle,
                pageDescription,
                navLabel + " pre-send check. Free during validation.",
                heroAccent,
                heroQuestionSuffix,
                heroLead,
                primaryCtaLabel,
                "acquisition_" + code,
                "Decision angle",
                panelTitle,
                panelNote,
                intentChips,
                promptCardsFor(navLabel, heroAccent, panelNote),
                faqItemsFor(heroAccent, panelNote));
    }

    public static AcquisitionSurface defaultSurface(String configuredCode) {
        for (AcquisitionSurface surface : values()) {
            if (surface.code.equalsIgnoreCase(configuredCode == null ? "" : configuredCode.trim())) {
                return surface;
            }
        }
        return DEFAULT_FALLBACK;
    }

    public static AcquisitionSurface fromCode(String rawCode, String defaultCode) {
        if (rawCode == null || rawCode.isBlank()) {
            return defaultSurface(defaultCode);
        }
        String normalized = normalize(rawCode);
        for (AcquisitionSurface surface : values()) {
            if (surface.code.equals(normalized)) {
                return surface;
            }
        }
        return defaultSurface(defaultCode);
    }

    public static AcquisitionSurface fromPath(String rawPath, String defaultCode) {
        if (rawPath == null || rawPath.isBlank()) {
            return defaultSurface(defaultCode);
        }
        String normalizedPath = rawPath.trim();
        int queryIndex = normalizedPath.indexOf('?');
        if (queryIndex >= 0) {
            normalizedPath = normalizedPath.substring(0, queryIndex);
        }
        for (AcquisitionSurface surface : values()) {
            if (surface.path.equals(normalizedPath)) {
                return surface;
            }
        }
        return defaultSurface(defaultCode);
    }

    public static boolean isSurfaceCode(String rawCode) {
        String normalized = normalize(rawCode);
        for (AcquisitionSurface surface : values()) {
            if (surface.code.equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    public static AcquisitionSurface fromLegacyVariant(String rawCode, String defaultCode) {
        if (rawCode == null || rawCode.isBlank()) {
            return defaultSurface(defaultCode);
        }
        String normalized = normalize(rawCode);
        return switch (normalized) {
            case "letter" -> LETTER;
            case "credit" -> CREDIT;
            case "ask" -> ASK;
            default -> defaultSurface(defaultCode);
        };
    }

    public static List<AcquisitionSurface> indexableSurfaces() {
        return INDEXABLE_SURFACES;
    }

    public static List<AcquisitionSurface> relatedSurfaces(AcquisitionSurface current) {
        if (current == null) {
            return INDEXABLE_SURFACES.stream().limit(10).toList();
        }

        LinkedHashSet<AcquisitionSurface> related = new LinkedHashSet<>();
        related.add(current);
        for (AcquisitionSurface surface : INDEXABLE_SURFACES) {
            if (surface != current && surface.family() == current.family()) {
                related.add(surface);
            }
        }
        for (AcquisitionSurface surface : List.of(LETTER, CREDIT, CREDIT_VS_REPAIR, ASK, REPAIR_REQUEST, OBJECTION, DEADLINE)) {
            related.add(surface);
        }
        for (AcquisitionSurface surface : INDEXABLE_SURFACES) {
            if (related.size() >= 10) {
                break;
            }
            related.add(surface);
        }
        return new ArrayList<>(related).stream().limit(10).toList();
    }

    public SurfaceFamily family() {
        return switch (this) {
            case SELLER_REFUSED, SELLER_COUNTER, SELLER_WONT_NEGOTIATE, FALLBACK, REDUCE_ASK,
                    REPAIR_ADDENDUM_REJECTED, RESPONSE_TO_COUNTER -> SurfaceFamily.COUNTER;
            case REASONABLE_REQUESTS, WHAT_NOT_TO_ASK, HOW_MUCH_CREDIT, NEGOTIATION_CHECKLIST, REQUEST_LIST,
                    REPORT_NEGOTIATION_TOOL -> SurfaceFamily.SCOPE;
            case FHA_REPAIRS, VA_REPAIRS, LENDER_REQUIRED_REPAIRS, APPRAISAL_REQUIRED_REPAIRS,
                    SELLER_CREDIT_LIMITS -> SurfaceFamily.FINANCING;
            case REPAIR_ADDENDUM, INSPECTION_AMENDMENT, CONTINGENCY_REMOVAL, RESOLUTION_DEADLINE,
                    OBJECTION_NOTICE -> SurfaceFamily.FORM;
            case ROOF_CREDIT, SEWER_SCOPE_CREDIT, ELECTRICAL_REQUEST, FOUNDATION_CREDIT, MOLD_CREDIT, HVAC_CREDIT,
                    PLUMBING_LEAK_CREDIT, WATER_INTRUSION_CREDIT, POLYBUTYLENE_CREDIT,
                    FPE_PANEL_CREDIT -> SurfaceFamily.SYSTEM;
            default -> SurfaceFamily.CORE;
        };
    }

    private static String normalize(String rawCode) {
        return rawCode == null ? "" : rawCode.trim().toLowerCase().replace('-', '_');
    }

    public String code() {
        return code;
    }

    public String path() {
        return path;
    }

    public String navLabel() {
        return navLabel;
    }

    public String pageTitle() {
        return pageTitle;
    }

    public String pageDescription() {
        return pageDescription;
    }

    public String heroEyebrow() {
        return heroEyebrow;
    }

    public String heroAccent() {
        return heroAccent;
    }

    public String heroQuestionSuffix() {
        return heroQuestionSuffix;
    }

    public String heroLead() {
        return heroLead;
    }

    public String primaryCtaLabel() {
        return primaryCtaLabel;
    }

    public String primaryAnalyticsLabel() {
        return primaryAnalyticsLabel;
    }

    public String panelKicker() {
        return panelKicker;
    }

    public String panelTitle() {
        return panelTitle;
    }

    public String panelNote() {
        return panelNote;
    }

    public List<String> intentChips() {
        return intentChips;
    }

    public List<PromptCard> promptCards() {
        return promptCards;
    }

    public List<FaqItem> faqItems() {
        return faqItems;
    }

    public boolean hasDedicatedSampleCase() {
        return switch (this) {
            case LETTER, CREDIT, CREDIT_VS_REPAIR, ASK, REPAIR_REQUEST, OBJECTION, DEADLINE, SELLER_REFUSED,
                    SELLER_COUNTER, FHA_REPAIRS, VA_REPAIRS, LENDER_REQUIRED_REPAIRS, SELLER_CREDIT_LIMITS,
                    ROOF_CREDIT -> true;
            default -> false;
        };
    }

    public SampleCase sampleCase() {
        return switch (this) {
            case LETTER -> new SampleCase(
                    "Response letter before the deadline",
                    "Buyer agent has a 4 p.m. inspection-response deadline, one roof leak, one unsafe panel note, and a buyer draft asking for every cosmetic item too.",
                    "$35,500 credit plus seller to repair all report items before closing.",
                    "Revise before send",
                    "$28,000 credit tied to active roof leak, panel safety review, and documented water staining. Remove cosmetic paint and loose hardware.",
                    "$22,000 credit or seller-paid licensed roof evaluation plus panel electrician quote before resolution deadline.",
                    "Keep only report-backed safety and active moisture findings in the lead paragraph.",
                    "Do not attach broad wish-list language without page references and quote status.");
            case CREDIT -> new SampleCase(
                    "Credit request with weak number support",
                    "Buyer wants a closing-cost credit after the report flags roof age, active plumbing leak, and an aging HVAC system with no contractor quote yet.",
                    "$42,000 seller credit for roof, plumbing, HVAC, paint, and appliance age.",
                    "Revise before send",
                    "$24,500 seller credit focused on active leak repair, roof evaluation, and HVAC service verification.",
                    "$18,000 credit if seller provides licensed roof and HVAC evaluation before closing.",
                    "Credit posture is cleaner than seller-managed repairs, but the dollar amount needs quote or estimate labels.",
                    "Confirm lender credit limits before using closing-cost credit language.");
            case CREDIT_VS_REPAIR -> new SampleCase(
                    "Repair request vs credit decision",
                    "The buyer agent is deciding whether to demand seller repairs or ask for a credit after sewer and electrical findings appear in the report.",
                    "Seller to repair sewer line, replace unsafe outlets, service HVAC, and repaint damaged trim before closing.",
                    "Send credit-first with repair fallback",
                    "$19,500 seller credit for sewer scope follow-up and licensed electrical correction, with seller repair only for lender-required items.",
                    "Seller completes licensed sewer cleanout and electrical correction before closing, buyer drops paint and trim items.",
                    "Credit gives the buyer more control when repair quality and timing are uncertain.",
                    "If FHA/VA/appraisal flags an item, repair may need to lead instead of credit.");
            case ASK -> new SampleCase(
                    "First ask from a messy report",
                    "Buyer copied twelve findings from the inspection report and wants to know what belongs in the first negotiation packet.",
                    "Ask seller to fix all twelve report findings and give $20,000 for future maintenance.",
                    "Revise before send",
                    "$15,000 credit for active leak, electrical safety correction, and sewer backup risk. Keep maintenance and cosmetic items out.",
                    "$10,000 credit plus seller-paid specialist evaluations for sewer and electrical findings.",
                    "The first ask gets stronger when it cuts ordinary maintenance before the seller sees it.",
                    "Do not lead with old-but-working systems unless failure, safety, or lender impact is documented.");
            case REPAIR_REQUEST -> new SampleCase(
                    "Repair addendum that is too broad",
                    "Buyer wants seller to complete repairs before closing, but the list mixes safety findings with preference upgrades.",
                    "Seller to repair roof, replace HVAC, fix GFCI outlets, repaint hallway, replace dishwasher, and clean gutters.",
                    "Revise before send",
                    "Seller to correct active roof leak and GFCI safety items with licensed receipts; request credit fallback for HVAC verification.",
                    "$12,500 credit if seller will not complete licensed roof and electrical repairs before closing.",
                    "Repair language should be narrow, licensed, receipt-backed, and tied to the report.",
                    "Do not ask seller to manage cosmetic or preference work unless the contract strategy supports it.");
            case OBJECTION -> new SampleCase(
                    "Inspection objection with form risk",
                    "Agent needs objection language but the buyer's draft reads like a general complaint list instead of unsatisfactory material findings.",
                    "Buyer objects to all defects in the inspection report and requests seller to make the house acceptable.",
                    "Do not send as written",
                    "Object to specific unsatisfactory items: active roof leak, unsafe electrical panel condition, and sewer backup evidence.",
                    "Request extension or seller response limited to specialist evaluation and credit negotiation.",
                    "Objection posture needs specific items, evidence references, and deadline control.",
                    "State forms and attorney-review markets can change the required wording. Treat this as packet prep, not legal language.");
            case DEADLINE -> new SampleCase(
                    "Short inspection window",
                    "The inspection deadline expires tomorrow and the buyer has serious findings but no complete contractor quotes.",
                    "$31,000 credit for roof, electrical, sewer, and water intrusion; seller must answer today.",
                    "Revise before send",
                    "$21,000 evidence-backed ask with explicit quote-needed labels and a narrow extension request for sewer/roof evaluation.",
                    "$15,000 credit or signed extension preserving inspection rights while quotes are collected.",
                    "Deadline pressure makes the fallback and extension path as important as the opening number.",
                    "Do not let urgency turn estimate-only numbers into quote-backed claims.");
            case SELLER_REFUSED -> new SampleCase(
                    "Seller already said no",
                    "The listing side rejected the first repair addendum and the buyer agent needs a narrower counter without looking desperate.",
                    "Resubmit the same $35,000 request and say the buyer will cancel unless seller agrees.",
                    "Revise before counter",
                    "$24,000 counter focused on active roof leak, panel safety, and sewer risk with cosmetic items removed.",
                    "$18,000 credit or seller-paid licensed evaluation for the two highest-risk systems.",
                    "After a refusal, leverage comes from narrowing and evidencing the ask, not repeating the first list louder.",
                    "Do not threaten cancellation unless the buyer is actually ready to use that leverage.");
            case SELLER_COUNTER -> new SampleCase(
                    "Seller countered far below the ask",
                    "Buyer asked for a $12,000 credit after inspection and the seller countered at $3,000 with no repair commitment.",
                    "Reject the counter and demand the original $12,000 again.",
                    "Revise before response",
                    "$8,500 counter tied to the two report-backed items still carrying near-term risk.",
                    "$6,000 credit or seller-paid licensed evaluation before deadline, with buyer dropping low-leverage items.",
                    "A counter-response needs a defensible middle number and a walk-forward fallback, not just the original ask repeated.",
                    "Do not counter without confirming the buyer's minimum acceptable outcome.");
            case FHA_REPAIRS -> new SampleCase(
                    "FHA-sensitive repair request",
                    "Buyer has FHA financing and the inspection mentions peeling paint, handrail issue, and active leak that may become lender-visible.",
                    "$18,000 seller credit for FHA repairs and general inspection issues.",
                    "Revise before send",
                    "Separate lender-clearance repairs from negotiable credit: ask seller to address FHA-visible safety items and request credit for buyer-controlled work.",
                    "$9,500 credit after lender-required repairs are confirmed, or seller completes documented safety repairs before appraisal clearance.",
                    "FHA-sensitive files need different buckets for lender-required repairs and negotiable credits.",
                    "Confirm lender treatment before promising that a credit can replace a required repair.");
            case VA_REPAIRS -> new SampleCase(
                    "VA repair gate before credit language",
                    "Buyer has VA financing and the report flags safety handrail, active leak evidence, and roof-life concern before appraisal review.",
                    "$16,000 seller credit for VA inspection repairs.",
                    "Revise before send",
                    "Ask seller to clear VA-visible safety and habitability items, then request a separate buyer-controlled credit only where allowed.",
                    "$7,500 credit after required repairs are separated from negotiable items.",
                    "VA-sensitive asks make money-near sense because the wrong credit language can create closing friction.",
                    "Confirm lender and appraisal treatment before treating a repair requirement as a credit request.");
            case LENDER_REQUIRED_REPAIRS -> new SampleCase(
                    "Lender-required repair risk",
                    "The agent suspects some inspection items may become lender conditions, but the buyer's draft treats everything as a negotiable credit.",
                    "$14,000 credit for all repairs so the buyer can handle them after closing.",
                    "Do not send as written",
                    "Separate possible lender-required repairs from buyer-controlled credit items and mark lender confirmation needed.",
                    "Seller completes required repairs before closing; buyer requests credit only for non-required follow-up work.",
                    "The value is preventing a clean negotiation ask from becoming an underwriting problem.",
                    "Do not promise post-close repair handling until the lender-required bucket is cleared.");
            case SELLER_CREDIT_LIMITS -> new SampleCase(
                    "Credit request may exceed usable limits",
                    "Buyer wants an $18,000 seller credit, but estimated allowable closing costs are closer to $11,000.",
                    "$18,000 seller credit at closing for inspection issues.",
                    "Revise before send",
                    "$11,000 seller credit within likely usable closing-cost room, plus price reduction or repair alternative for the remaining exposure.",
                    "$9,000 credit plus seller-paid licensed repair for the highest-risk item.",
                    "This is money-nearest because agents lose time when a negotiated credit cannot actually be used.",
                    "Confirm loan program, closing costs, and concession limits before relying on the credit amount.");
            case ROOF_CREDIT -> new SampleCase(
                    "Roof credit without overclaiming replacement",
                    "Inspection notes active staining and damaged shingles, but there is no roofing contractor quote yet.",
                    "$35,000 credit for full roof replacement because the roof is old.",
                    "Revise before send",
                    "$16,500 roof credit request tied to active leak evidence, shingle damage, and quote-needed roof evaluation.",
                    "$10,000 credit plus seller-paid licensed roofer evaluation before the inspection deadline.",
                    "Roof asks get stronger when they distinguish active failure from age and future maintenance.",
                    "Do not call for full replacement unless the report, quote, or specialist supports it.");
            default -> familySampleCase();
        };
    }

    public ProofSection proofSection() {
        return switch (family()) {
            case CORE -> new ProofSection(
                    "Core decision proof",
                    "The page is useful only if it helps the visitor decide what to send next, not if it explains inspections in general.",
                    List.of(
                            new ProofCard("Decision", "Choose credit, repair, objection, or narrower first ask before drafting."),
                            new ProofCard("Scope", "Cut maintenance, cosmetic, and old-but-working items before they weaken the packet."),
                            new ProofCard("Output", "Leave with revised wording, fallback posture, and evidence checklist.")));
            case COUNTER -> new ProofSection(
                    "Counter-move proof",
                    "These pages are for files where the first ask already met resistance and the next move must preserve leverage.",
                    List.of(
                            new ProofCard("Refusal state", "Capture whether the seller rejected, countered, delayed, or forced a reduction."),
                            new ProofCard("Narrower ask", "Rebuild the packet around the two or three items still defensible."),
                            new ProofCard("Fallback", "Prepare the buyer's next acceptable outcome before the agent responds.")));
            case SCOPE -> new ProofSection(
                    "Scope-control proof",
                    "These pages keep the user from turning a real issue into a bloated wish list.",
                    List.of(
                            new ProofCard("Keep", "Safety, active leaks, sewer, structural, and lender-visible items stay eligible."),
                            new ProofCard("Cut", "Cosmetic preference, normal wear, old-but-working systems, and vague future maintenance move out."),
                            new ProofCard("Label", "Each number is quote-backed, evidence-backed, or estimate-only.")));
            case FINANCING -> new ProofSection(
                    "Financing proof",
                    "These pages are for asks where loan treatment can change whether credit, repair, or delay is safe.",
                    List.of(
                            new ProofCard("Loan gate", "Flag FHA, VA, appraisal, and seller-credit-limit concerns early."),
                            new ProofCard("Bucket", "Separate lender-required repairs from negotiable buyer credit."),
                            new ProofCard("Caveat", "Push lender confirmation when the tool cannot safely decide treatment.")));
            case FORM -> new ProofSection(
                    "Form and deadline proof",
                    "These pages keep the packet aligned with the response window without pretending to be legal advice.",
                    List.of(
                            new ProofCard("Deadline", "Surface response, objection, resolution, removal, and extension timing risk."),
                            new ProofCard("Form fit", "Translate the packet into repair addendum, amendment, or objection-notice intent."),
                            new ProofCard("Boundary", "Keep attorney, broker, or state-form review visible where needed.")));
            case SYSTEM -> new ProofSection(
                    "System issue proof",
                    "These pages are for high-stakes defects where evidence quality matters more than generic repair advice.",
                    List.of(
                            new ProofCard("Evidence", "Separate active failure, safety risk, and specialist-confirmed findings from age alone."),
                            new ProofCard("Quote status", "Show whether the ask is quote-backed, inspection-backed, or still estimate-only."),
                            new ProofCard("Do not overclaim", "Avoid diagnosis language when roof, mold, foundation, or panel evidence is incomplete.")));
        };
    }

    public String primaryCtaHref() {
        return "/home-repair?entry=" + code + "#packet-builder";
    }

    private static List<PromptCard> promptCardsFor(String navLabel, String heroAccent, String panelNote) {
        return List.of(
                new PromptCard(
                        "Need to decide the next move?",
                        "Paste the proposed ask, seller response, or report findings so the pre-send check can judge the " + heroAccent + " posture before anything gets sent.",
                        "Run the pre-send check ->"),
                new PromptCard(
                        "Need a narrower packet?",
                        "The output keeps the strongest items, cuts weak leverage, and labels whether evidence, quote support, deadline, financing, or form review is still missing.",
                        "Build the narrow packet ->"),
                new PromptCard(
                        "Need something the buyer agent can use?",
                        panelNote,
                        "Open " + navLabel + " intake ->"));
    }

    private static List<FaqItem> faqItemsFor(String heroAccent, String panelNote) {
        return List.of(
                new FaqItem(
                        "Can this help with " + heroAccent + "?",
                        "Yes. It starts from that search intent, then runs the same pre-send inspection request check: scope, evidence, number basis, cut list, fallback, lender/form warnings, and send posture."),
                new FaqItem(
                        "Does this replace my agent, lender, contractor, or contract form?",
                        "No. It prepares the negotiation packet and review gates, but the final form, legal path, lender treatment, and repair pricing still need the appropriate professional review."),
                new FaqItem(
                        "What makes this different from a generic article?",
                        panelNote));
    }

    public enum SurfaceFamily {
        CORE,
        COUNTER,
        SCOPE,
        FINANCING,
        FORM,
        SYSTEM
    }

    public record PromptCard(String title, String body, String ctaLabel) {
    }

    public record FaqItem(String question, String answer) {
    }

    public record SampleCase(
            String title,
            String situation,
            String proposedAsk,
            String verdict,
            String revisedAsk,
            String fallback,
            String whyItWorks,
            String trustWarning) {
    }

    public record ProofSection(String kicker, String summary, List<ProofCard> cards) {
    }

    public record ProofCard(String title, String body) {
    }

    private SampleCase familySampleCase() {
        return switch (family()) {
            case COUNTER -> new SampleCase(
                    "Counter-stage negotiation check",
                    "The first ask has been rejected, countered, or delayed and the buyer agent needs a defensible next move.",
                    "Push the original request again without changing scope.",
                    "Revise before response",
                    "Narrow the counter to the highest-risk report-backed items and attach the cleanest evidence.",
                    "Accept a smaller credit, licensed evaluation, or deadline extension only if it preserves buyer leverage.",
                    "Counter-stage packets need a smaller ask, clearer evidence, and a fallback before the agent replies.",
                    "Do not escalate tone unless the buyer is ready to cancel or renegotiate hard.");
            case SCOPE -> new SampleCase(
                    "Reasonableness and scope check",
                    "The buyer has a long report list and needs to know what belongs in the first packet.",
                    "Ask the seller to fix every inspection finding and fund future maintenance.",
                    "Revise before send",
                    "Lead with serious safety, leak, sewer, structural, or lender-visible issues and move weak items out.",
                    "Use a lower credit or evaluation request if quote support is missing.",
                    "A scoped ask is more defensible than a complete defect inventory.",
                    "Do not confuse normal wear or cosmetic preference with negotiation leverage.");
            case FINANCING -> new SampleCase(
                    "Loan-sensitive ask check",
                    "The buyer has financing constraints and some findings may affect lender, appraisal, or seller-credit treatment.",
                    "Use one seller credit to solve every inspection and lender issue.",
                    "Revise before send",
                    "Separate required repairs from negotiable credits and flag anything that needs lender confirmation.",
                    "Request seller completion for lender-visible items and credit only for buyer-controlled work.",
                    "Loan-sensitive asks need the right bucket before the language is drafted.",
                    "Do not assume a credit can replace a repair that underwriting or appraisal may require.");
            case FORM -> new SampleCase(
                    "Form and deadline check",
                    "The buyer needs to move from negotiation language into the correct addendum, amendment, objection, or removal step.",
                    "Send a broad email and treat it like the formal inspection response.",
                    "Do not send as written",
                    "Convert the request into a narrow packet that can be reviewed against the correct form path.",
                    "Ask for an extension or broker/form review if the deadline or state form is unclear.",
                    "The output is useful because it separates the negotiation packet from the final form execution.",
                    "This is not legal advice. State form and attorney-review requirements still need professional review.");
            case SYSTEM -> new SampleCase(
                    "High-stakes defect check",
                    "The buyer has one serious system finding and needs to avoid overclaiming before the specialist evidence is complete.",
                    "Ask for a full replacement credit based on age and concern alone.",
                    "Revise before send",
                    "Tie the ask to active failure, safety risk, report language, and quote-needed status.",
                    "Request a smaller credit or seller-paid specialist evaluation if the report does not support replacement.",
                    "System pages work when they label evidence quality instead of inflating the issue.",
                    "Do not diagnose mold, structural failure, or full replacement need without specialist support.");
            default -> new SampleCase(
                    "Inspection ask pre-send check",
                    "The buyer or buyer agent needs to turn report findings into one defensible next ask.",
                    "Ask the seller to address every item in the inspection report.",
                    "Revise before send",
                    "Lead with the few findings that affect safety, function, financing, or near-term exposure.",
                    "Use a lower credit, seller evaluation, or deadline extension when evidence is incomplete.",
                    "The packet is useful because it compresses scope, wording, fallback, and evidence into one artifact.",
                    "Do not send unsupported numbers or broad repair lists without review.");
        };
    }
}
