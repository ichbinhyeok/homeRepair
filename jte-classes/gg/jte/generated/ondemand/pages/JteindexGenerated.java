package gg.jte.generated.ondemand.pages;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping.MetroMasterData;
public final class JteindexGenerated {
	public static final String JTE_NAME = "pages/index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,3,3,3,3,19,27,27,28,28,29,29,29,29,29,29,29,29,29,29,29,29,30,30,31,31,39,74,74,74,75,75,75,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, MetroMasterData metroData) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"max-w-xl mx-auto py-12 px-4 sm:px-6 lg:px-8\">\r\n        <div class=\"text-center mb-10\">\r\n            <h1 class=\"text-4xl font-extrabold text-slate-900 tracking-tight mb-2\">Verdict Engine</h1>\r\n            <p class=\"text-lg text-slate-600\">Neutral, data-driven home repair advice.</p>\r\n        </div>\r\n\r\n        <div class=\"bg-white shadow-xl rounded-2xl overflow-hidden border border-slate-100\">\r\n            <div class=\"bg-slate-900 px-6 py-4\">\r\n                <h2 class=\"text-white font-bold text-lg flex items-center\">\r\n                    <span class=\"bg-blue-500 text-xs font-bold px-2 py-1 rounded mr-3\">STEP 1</span>\r\n                    Location & Construction Era\r\n                </h2>\r\n            </div>\r\n            \r\n            <form action=\"/home-repair/step-2\" method=\"POST\" class=\"p-8 space-y-6\">\r\n                ");
				jteOutput.writeContent("\r\n                <div>\r\n                    <label class=\"block text-sm font-bold text-slate-700 mb-2\">Select Metro Area</label>\r\n                    <p class=\"text-xs text-slate-500 mb-3\">Loads 2026 localized labor & material rates.</p>\r\n                    <div class=\"relative\">\r\n                        <select name=\"metroCode\" required \r\n                                class=\"w-full pl-4 pr-10 py-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none text-slate-800 font-medium transition cursor-pointer\">\r\n                            <option value=\"\" disabled selected>Choose your location...</option>\r\n                            ");
				if (metroData != null && metroData.getData() != null) {
					jteOutput.writeContent("\r\n                                ");
					for (var entry : metroData.getData().entrySet()) {
						jteOutput.writeContent("\r\n                                    <option");
						var __jte_html_attribute_0 = entry.getKey();
						if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
							jteOutput.writeContent(" value=\"");
							jteOutput.setContext("option", "value");
							jteOutput.writeUserContent(__jte_html_attribute_0);
							jteOutput.setContext("option", null);
							jteOutput.writeContent("\"");
						}
						jteOutput.writeContent(">");
						jteOutput.setContext("option", null);
						jteOutput.writeUserContent(entry.getKey().replace("_", " "));
						jteOutput.writeContent("</option>\r\n                                ");
					}
					jteOutput.writeContent("\r\n                            ");
				}
				jteOutput.writeContent("\r\n                        </select>\r\n                        <div class=\"pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-slate-500\">\r\n                            <svg class=\"h-4 w-4 fill-current\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 20 20\"><path d=\"M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z\"/></svg>\r\n                        </div>\r\n                    </div>\r\n                </div>\r\n\r\n                ");
				jteOutput.writeContent("\r\n                <div>\r\n                    <label class=\"block text-sm font-bold text-slate-700 mb-2\">Construction Era</label>\r\n                    <p class=\"text-xs text-slate-500 mb-3\">Identifies era-specific risks (e.g., Polybutylene, Lead).</p>\r\n                    <div class=\"relative\">\r\n                        <select name=\"era\" required\r\n                                class=\"w-full pl-4 pr-10 py-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 appearance-none text-slate-800 font-medium transition cursor-pointer\">\r\n                            <option value=\"\" disabled selected>Select build period...</option>\r\n                            <option value=\"PRE_1950\">Pre-1950 (Early Century)</option>\r\n                            <option value=\"1950_1970\">1950-1970 (Mid-Century)</option>\r\n                            <option value=\"1970_1980\">1970-1980 (Energy Crisis)</option>\r\n                            <option value=\"1980_1995\">1980-1995 (Modern Testing)</option>\r\n                            <option value=\"1995_2010\">1995-2010 (Pre-Recession)</option>\r\n                            <option value=\"2010_PRESENT\">2010-Present (High Efficiency)</option>\r\n                        </select>\r\n                         <div class=\"pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-slate-500\">\r\n                            <svg class=\"h-4 w-4 fill-current\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 20 20\"><path d=\"M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z\"/></svg>\r\n                        </div>\r\n                    </div>\r\n                </div>\r\n\r\n                <button type=\"submit\" \r\n                        class=\"w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-4 rounded-xl shadow-lg hover:shadow-xl transition transform active:scale-95 duration-150 flex justify-center items-center mt-6\">\r\n                    CONTINUE\r\n                    <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"h-5 w-5 ml-2\" viewBox=\"0 0 20 20\" fill=\"currentColor\">\r\n                        <path fill-rule=\"evenodd\" d=\"M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z\" clip-rule=\"evenodd\" />\r\n                    </svg>\r\n                </button>\r\n            </form>\r\n        </div>\r\n        \r\n        <div class=\"mt-8 text-center text-xs text-slate-400\">\r\n            &copy; 2026 Verdict Engine. All data localized by market.\r\n        </div>\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		MetroMasterData metroData = (MetroMasterData)params.get("metroData");
		render(jteOutput, jteHtmlInterceptor, metroData);
	}
}
