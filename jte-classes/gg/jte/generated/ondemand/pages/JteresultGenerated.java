package gg.jte.generated.ondemand.pages;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.Verdict;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
public final class JteresultGenerated {
	public static final String JTE_NAME = "pages/result.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,5,5,5,5,8,10,10,13,13,13,14,14,17,17,17,18,18,21,21,21,22,22,25,33,33,34,34,34,34,37,37,37,38,38,38,40,40,40,41,41,43,43,46,46,46,48,48,49,49,53,53,55,55,56,56,56,59,59,60,60,64,64,68,78,78,80,80,83,83,83,85,85,87,87,89,89,93,93,95,95,98,98,98,100,100,102,102,108,120,120,122,122,122,123,123,123,124,124,124,126,126,127,127,131,131,141,141,141,142,142,142,2,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Verdict verdict, VerdictHistory history) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8\">\r\n        \r\n        ");
				jteOutput.writeContent("\r\n        <div class=\"text-center mb-10\">\r\n            ");
				if ("APPROVED".equals(verdict.getTier())) {
					jteOutput.writeContent("\r\n                <div class=\"inline-block bg-green-100 text-green-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-green-200\">VERDICT: APPROVED</div>\r\n                <h1 class=\"text-4xl font-extrabold text-slate-900 tracking-tight mb-2\">Green Light</h1>\r\n                <p class=\"text-lg text-green-700 font-medium\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(verdict.getHeadline());
					jteOutput.writeContent("</p>\r\n            ");
				} else if ("WARNING".equals(verdict.getTier())) {
					jteOutput.writeContent("\r\n                 <div class=\"inline-block bg-yellow-100 text-yellow-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-yellow-200\">VERDICT: WARNING</div>\r\n                <h1 class=\"text-4xl font-extrabold text-slate-900 tracking-tight mb-2\">Caution Required</h1>\r\n                <p class=\"text-lg text-yellow-700 font-medium\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(verdict.getHeadline());
					jteOutput.writeContent("</p>\r\n            ");
				} else {
					jteOutput.writeContent("\r\n                 <div class=\"inline-block bg-red-100 text-red-800 font-black text-xs px-3 py-1 rounded-full mb-4 tracking-widest border border-red-200\">VERDICT: DENIED</div>\r\n                <h1 class=\"text-4xl font-extrabold text-slate-900 tracking-tight mb-2\">Insufficient Budget</h1>\r\n                <p class=\"text-lg text-red-700 font-medium\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(verdict.getHeadline());
					jteOutput.writeContent("</p>\r\n            ");
				}
				jteOutput.writeContent("\r\n        </div>\r\n\r\n        ");
				jteOutput.writeContent("\r\n        <div class=\"mb-12\">\r\n            <h2 class=\"text-2xl font-bold text-slate-900 border-b-2 border-slate-200 pb-3 mb-6 flex items-center\">\r\n                <svg class=\"w-6 h-6 text-red-600 mr-2\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z\"/></svg>\r\n                CRITICAL / MUST DO\r\n            </h2>\r\n            \r\n            <div class=\"grid gap-6 md:grid-cols-2\">\r\n                ");
				for (var item : verdict.getPlan().getMustDo()) {
					jteOutput.writeContent("\r\n                    <div class=\"bg-white rounded-xl shadow-md overflow-hidden border-l-4 ");
					jteOutput.setContext("div", "class");
					jteOutput.writeUserContent(item.isMandatory() ? "border-red-600" : "border-slate-800");
					jteOutput.setContext("div", null);
					jteOutput.writeContent("\">\r\n                        <div class=\"p-6\">\r\n                            <div class=\"flex justify-between items-start mb-4\">\r\n                                <span class=\"bg-slate-100 text-slate-600 text-xs font-bold px-2 py-1 rounded uppercase tracking-wider\">");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(item.getCategory());
					jteOutput.writeContent("</span>\r\n                                <span class=\"text-lg font-bold text-slate-900\">$");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(String.format("%,.0f", item.getAdjustedCost()));
					jteOutput.writeContent("</span>\r\n                            </div>\r\n                            <h3 class=\"font-bold text-lg text-slate-800 mb-2 leading-snug\">");
					jteOutput.setContext("h3", null);
					jteOutput.writeUserContent(item.getPrettyName());
					jteOutput.writeContent("</h3>\r\n                            ");
					if (item.getRiskFlags() != null && !item.getRiskFlags().isEmpty()) {
						jteOutput.writeContent("\r\n                                <div class=\"space-y-1 mb-3\">\r\n                                    ");
						for (var flag : item.getRiskFlags()) {
							jteOutput.writeContent("\r\n                                        <div class=\"text-xs text-red-600 font-semibold flex items-center\">\r\n                                            <svg class=\"w-3 h-3 mr-1\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path fill-rule=\"evenodd\" d=\"M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z\" clip-rule=\"evenodd\"/></svg>\r\n                                            ");
							jteOutput.setContext("div", null);
							jteOutput.writeUserContent(flag);
							jteOutput.writeContent("\r\n                                        </div>\r\n                                    ");
						}
						jteOutput.writeContent("\r\n                                    ");
						if (item.getRiskFlags().contains("HISTORY_RECHECK")) {
							jteOutput.writeContent("\r\n                                        <p class=\"text-xs text-blue-600 italic mt-1\">\r\n                                            * Diagnostic check only based on provided repair history.\r\n                                        </p>\r\n                                    ");
						}
						jteOutput.writeContent("\r\n                                </div>\r\n                            ");
					}
					jteOutput.writeContent("\r\n                            <p class=\"text-sm text-slate-500\">");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(item.getExplanation());
					jteOutput.writeContent("</p>\r\n                        </div>\r\n                    </div>\r\n                ");
				}
				jteOutput.writeContent("\r\n                ");
				if (verdict.getPlan().getMustDo().isEmpty()) {
					jteOutput.writeContent("\r\n                    <div class=\"md:col-span-2 bg-green-50 p-6 rounded-lg text-center border border-green-100\">\r\n                        <p class=\"text-green-700 font-medium\">No critical safety items detected based on your inputs.</p>\r\n                    </div>\r\n                ");
				}
				jteOutput.writeContent("\r\n            </div>\r\n        </div>\r\n\r\n        ");
				jteOutput.writeContent("\r\n        <div class=\"bg-slate-900 rounded-xl shadow-xl overflow-hidden mb-12\">\r\n            <div class=\"px-6 py-4 border-b border-slate-700 flex items-center bg-slate-800\">\r\n                <svg class=\"w-5 h-5 text-blue-400 mr-2\" fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z\"/></svg>\r\n                <h2 class=\"text-white font-bold tracking-wide uppercase text-sm\">Expert Strategic Advice</h2>\r\n            </div>\r\n            <div class=\"p-6 text-slate-300\">\r\n                <div class=\"grid md:grid-cols-2 gap-8\">\r\n                    <div>\r\n                        <h4 class=\"text-blue-400 font-bold text-xs uppercase mb-3 tracking-wider\">Future Cost Advisory</h4>\r\n                        ");
				if (!verdict.getFutureCostWarning().isEmpty()) {
					jteOutput.writeContent("\r\n                            <ul class=\"space-y-3\">\r\n                                ");
					for (var warning : verdict.getFutureCostWarning()) {
						jteOutput.writeContent("\r\n                                    <li class=\"flex items-start text-sm\">\r\n                                        <span class=\"text-yellow-500 mr-2\">⚠</span>\r\n                                        ");
						jteOutput.setContext("li", null);
						jteOutput.writeUserContent(warning);
						jteOutput.writeContent("\r\n                                    </li>\r\n                                ");
					}
					jteOutput.writeContent("\r\n                            </ul>\r\n                        ");
				} else {
					jteOutput.writeContent("\r\n                            <p class=\"text-sm text-slate-500 italic\">No immediate deferred maintenance risks detected.</p>\r\n                        ");
				}
				jteOutput.writeContent("\r\n                    </div>\r\n                    <div>\r\n                        <h4 class=\"text-blue-400 font-bold text-xs uppercase mb-3 tracking-wider\">Upgrade Opportunities</h4>\r\n                         ");
				if (verdict.getUpgradeScenario() != null && !verdict.getUpgradeScenario().isEmpty()) {
					jteOutput.writeContent("\r\n                             <ul class=\"space-y-3\">\r\n                                ");
					for (var opp : verdict.getUpgradeScenario()) {
						jteOutput.writeContent("\r\n                                    <li class=\"flex items-start text-sm\">\r\n                                        <span class=\"text-green-500 mr-2\">↗</span>\r\n                                        ");
						jteOutput.setContext("li", null);
						jteOutput.writeUserContent(opp);
						jteOutput.writeContent("\r\n                                    </li>\r\n                                ");
					}
					jteOutput.writeContent("\r\n                            </ul>\r\n                        ");
				}
				jteOutput.writeContent("\r\n                    </div>\r\n                </div>\r\n            </div>\r\n        </div>\r\n\r\n        ");
				jteOutput.writeContent("\r\n        <h2 class=\"text-xl font-bold text-slate-800 border-b border-slate-200 pb-2 mb-6\">Recommended (If Budget Allows)</h2>\r\n        <div class=\"bg-white shadow rounded-lg overflow-hidden border border-slate-200 mb-12\">\r\n            <table class=\"min-w-full divide-y divide-gray-200\">\r\n                <thead class=\"bg-gray-50\">\r\n                    <tr>\r\n                        <th class=\"px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider\">Item</th>\r\n                        <th class=\"px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider\">Category</th>\r\n                        <th class=\"px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider\">Est. Cost</th>\r\n                    </tr>\r\n                </thead>\r\n                <tbody class=\"bg-white divide-y divide-gray-200\">\r\n                    ");
				for (var item : verdict.getPlan().getShouldDo()) {
					jteOutput.writeContent("\r\n                        <tr>\r\n                            <td class=\"px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(item.getPrettyName());
					jteOutput.writeContent("</td>\r\n                            <td class=\"px-6 py-4 whitespace-nowrap text-sm text-gray-500\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(item.getCategory());
					jteOutput.writeContent("</td>\r\n                            <td class=\"px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right\">$");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(String.format("%,.0f", item.getAdjustedCost()));
					jteOutput.writeContent("</td>\r\n                        </tr>\r\n                    ");
				}
				jteOutput.writeContent("\r\n                     ");
				if (verdict.getPlan().getShouldDo().isEmpty()) {
					jteOutput.writeContent("\r\n                        <tr>\r\n                            <td colspan=\"3\" class=\"px-6 py-4 text-center text-sm text-gray-500 italic\">No optional items suggested.</td>\r\n                        </tr>\r\n                    ");
				}
				jteOutput.writeContent("\r\n                </tbody>\r\n            </table>\r\n        </div>\r\n\r\n        <div class=\"text-center\">\r\n            <a href=\"/home-repair\" class=\"text-blue-600 hover:text-blue-800 font-medium text-sm\">Start New Calculation &rarr;</a>\r\n        </div>\r\n\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Verdict verdict = (Verdict)params.get("verdict");
		VerdictHistory history = (VerdictHistory)params.get("history");
		render(jteOutput, jteHtmlInterceptor, verdict, history);
	}
}
