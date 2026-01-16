package gg.jte.generated.ondemand.seo;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink;
import java.util.List;
public final class JtestaticverdictGenerated {
	public static final String JTE_NAME = "seo/static-verdict.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,4,4,4,20,20,20,20,20,20,20,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,21,23,23,23,23,23,23,23,23,23,25,27,27,27,27,27,27,27,27,28,28,28,28,28,28,28,28,28,29,29,29,29,29,29,29,29,29,31,36,36,36,37,37,37,37,37,37,40,40,40,45,45,45,54,54,54,54,54,54,55,55,55,57,57,60,60,60,61,61,61,62,62,62,62,63,63,159,159,159,159,159,159,160,160,160,160,160,160,160,164,164,164,168,168,168,168,168,168,170,170,172,172,173,173,175,175,175,176,176,177,177,177,178,178,179,179,179,181,181,182,182,185,185,190,190,191,191,191,191,191,191,192,192,196,196,205,205,205,206,206,207,207,207,207,207,207,207,207,207,207,207,207,208,208,210,210,210,211,211,212,212,212,212,212,212,212,212,212,212,212,212,213,213,223,223,223,4,5,6,7,8,9,10,11,12,12,12,12};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String metroCode, String metroName, String era, String eraName, Verdict verdict, List<InternalLink> eraLinks, List<InternalLink> cityLinks, String baseUrl, String canonicalUrl) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    \r\n    <title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent(" ");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(eraName);
		jteOutput.writeContent(" Home Repair Cost Guide [2026 Updated] | Verdict Engine</title>\r\n    <meta name=\"description\" content=\"");
		jteOutput.setContext("meta", "content");
		jteOutput.writeUserContent(verdict.getTier());
		jteOutput.setContext("meta", null);
		jteOutput.writeContent(": ");
		jteOutput.setContext("meta", "content");
		jteOutput.writeUserContent(verdict.getHeadline());
		jteOutput.setContext("meta", null);
		jteOutput.writeContent(" Analysis of ");
		jteOutput.setContext("meta", "content");
		jteOutput.writeUserContent(eraName);
		jteOutput.setContext("meta", null);
		jteOutput.writeContent(" homes in ");
		jteOutput.setContext("meta", "content");
		jteOutput.writeUserContent(metroName);
		jteOutput.setContext("meta", null);
		jteOutput.writeContent(" with detailed cost breakdowns and critical repair priorities based on 2026 RSMeans data.\">\r\n    \r\n    <link rel=\"canonical\"");
		var __jte_html_attribute_0 = canonicalUrl;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" href=\"");
			jteOutput.setContext("link", "href");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("link", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n    \r\n    ");
		jteOutput.writeContent("\r\n    <meta property=\"og:type\" content=\"article\">\r\n    <meta property=\"og:title\" content=\"");
		jteOutput.setContext("meta", "content");
		jteOutput.writeUserContent(metroName);
		jteOutput.setContext("meta", null);
		jteOutput.writeContent(" ");
		jteOutput.setContext("meta", "content");
		jteOutput.writeUserContent(eraName);
		jteOutput.setContext("meta", null);
		jteOutput.writeContent(" Home Repair Cost [2026 Updated]\">\r\n    <meta property=\"og:description\"");
		var __jte_html_attribute_1 = verdict.getHeadline();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
			jteOutput.writeContent(" content=\"");
			jteOutput.setContext("meta", "content");
			jteOutput.writeUserContent(__jte_html_attribute_1);
			jteOutput.setContext("meta", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n    <meta property=\"og:url\"");
		var __jte_html_attribute_2 = canonicalUrl;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
			jteOutput.writeContent(" content=\"");
			jteOutput.setContext("meta", "content");
			jteOutput.writeUserContent(__jte_html_attribute_2);
			jteOutput.setContext("meta", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\r\n    \r\n    ");
		jteOutput.writeContent("\r\n    <script type=\"application/ld+json\">\r\n    {\r\n        \"@context\": \"https://schema.org\",\r\n        \"@type\": \"LocalBusiness\",\r\n        \"name\": \"Home Repair Cost Analysis - ");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent("\",\r\n        \"description\": \"Professional home repair cost analysis for ");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(eraName);
		jteOutput.writeContent(" homes in ");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent("\",\r\n        \"areaServed\": {\r\n            \"@type\": \"City\",\r\n            \"name\": \"");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent("\"\r\n        },\r\n        \"offers\": {\r\n            \"@type\": \"AggregateOffer\",\r\n            \"priceCurrency\": \"USD\",\r\n            \"offerCount\": \"");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(verdict.getPlan().getMustDo().size());
		jteOutput.writeContent("\"\r\n        }\r\n    }\r\n    </script>\r\n    \r\n    <script type=\"application/ld+json\">\r\n    {\r\n        \"@context\": \"https://schema.org\",\r\n        \"@type\": \"HowTo\",\r\n        \"name\": \"How to Budget for ");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(eraName);
		jteOutput.writeContent(" Home Repairs in ");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent("\",\r\n        \"description\": \"");
		jteOutput.setContext("script", null);
		jteOutput.writeUserContent(verdict.getHeadline());
		jteOutput.writeContent("\",\r\n        \"step\": [\r\n        ");
		for (var item : verdict.getPlan().getMustDo()) {
			jteOutput.writeContent("\r\n            {\r\n                \"@type\": \"HowToStep\",\r\n                \"name\": \"");
			jteOutput.setContext("script", null);
			jteOutput.writeUserContent(item.getPrettyName());
			jteOutput.writeContent("\",\r\n                \"text\": \"");
			jteOutput.setContext("script", null);
			jteOutput.writeUserContent(item.getExplanation() != null ? item.getExplanation().replace("\"", "'") : "");
			jteOutput.writeContent("\"\r\n            }");
			if (!item.equals(verdict.getPlan().getMustDo().get(verdict.getPlan().getMustDo().size() - 1))) {
				jteOutput.writeContent(",");
			}
			jteOutput.writeContent("\r\n        ");
		}
		jteOutput.writeContent("\r\n        ]\r\n    }\r\n    </script>\r\n    \r\n    <style>\r\n        body {\r\n            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;\r\n            max-width: 900px;\r\n            margin: 0 auto;\r\n            padding: 20px;\r\n            line-height: 1.6;\r\n            color: #333;\r\n        }\r\n        h1 {\r\n            color: #1a1a1a;\r\n            border-bottom: 3px solid #4CAF50;\r\n            padding-bottom: 10px;\r\n        }\r\n        .verdict-tier {\r\n            display: inline-block;\r\n            padding: 8px 16px;\r\n            border-radius: 4px;\r\n            font-weight: bold;\r\n            margin: 10px 0;\r\n        }\r\n        .verdict-tier.APPROVED { background: #4CAF50; color: white; }\r\n        .verdict-tier.WARNING { background: #FF9800; color: white; }\r\n        .verdict-tier.DENIED { background: #f44336; color: white; }\r\n        .headline {\r\n            font-size: 1.2em;\r\n            margin: 20px 0;\r\n            padding: 15px;\r\n            background: #f5f5f5;\r\n            border-left: 4px solid #4CAF50;\r\n        }\r\n        .must-do-item {\r\n            margin: 20px 0;\r\n            padding: 15px;\r\n            background: #fff3cd;\r\n            border-left: 4px solid #ff9800;\r\n        }\r\n        .must-do-item h3 {\r\n            margin-top: 0;\r\n            color: #d68000;\r\n        }\r\n        .should-do {\r\n            background: #e3f2fd;\r\n            padding: 15px;\r\n            margin: 20px 0;\r\n            border-left: 4px solid #2196F3;\r\n        }\r\n        .internal-links {\r\n            margin: 40px 0;\r\n            padding: 20px;\r\n            background: #f9f9f9;\r\n            border: 1px solid #ddd;\r\n        }\r\n        .internal-links h3 {\r\n            margin-top: 0;\r\n        }\r\n        .internal-links a {\r\n            display: block;\r\n            padding: 8px 0;\r\n            color: #1976D2;\r\n            text-decoration: none;\r\n        }\r\n        .internal-links a:hover {\r\n            text-decoration: underline;\r\n        }\r\n        .cta-button {\r\n            display: inline-block;\r\n            padding: 15px 30px;\r\n            background: #4CAF50;\r\n            color: white;\r\n            text-decoration: none;\r\n            border-radius: 5px;\r\n            font-size: 1.1em;\r\n            font-weight: bold;\r\n            margin: 30px 0;\r\n            text-align: center;\r\n        }\r\n        .cta-button:hover {\r\n            background: #45a049;\r\n        }\r\n        footer {\r\n            margin-top: 60px;\r\n            padding-top: 20px;\r\n            border-top: 1px solid #ddd;\r\n            color: #666;\r\n            font-size: 0.9em;\r\n        }\r\n    </style>\r\n</head>\r\n<body>\r\n    <header>\r\n        <h1>2026 ");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent(" Home Repair Cost Analysis — ");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(eraName);
		jteOutput.writeContent("</h1>\r\n        <div class=\"verdict-tier ");
		jteOutput.setContext("div", "class");
		jteOutput.writeUserContent(verdict.getTier());
		jteOutput.setContext("div", null);
		jteOutput.writeContent("\">");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(verdict.getTier());
		jteOutput.writeContent("</div>\r\n    </header>\r\n\r\n    <section class=\"verdict-summary\">\r\n        <div class=\"headline\">");
		jteOutput.setContext("div", null);
		jteOutput.writeUserContent(verdict.getHeadline());
		jteOutput.writeContent("</div>\r\n    </section>\r\n\r\n    <section id=\"must-do-repairs\">\r\n        <h2>🔴 Critical Repairs for ");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(eraName);
		jteOutput.writeContent(" Homes in ");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent("</h2>\r\n        \r\n        ");
		if (verdict.getPlan().getMustDo().isEmpty()) {
			jteOutput.writeContent("\r\n            <p>No critical repairs identified for this scenario.</p>\r\n        ");
		} else {
			jteOutput.writeContent("\r\n            ");
			for (var item : verdict.getPlan().getMustDo()) {
				jteOutput.writeContent("\r\n                <div class=\"must-do-item\">\r\n                    <h3>");
				jteOutput.setContext("h3", null);
				jteOutput.writeUserContent(item.getPrettyName());
				jteOutput.writeContent("</h3>\r\n                    ");
				if (item.getExplanation() != null && !item.getExplanation().isEmpty()) {
					jteOutput.writeContent("\r\n                        <p>");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(item.getExplanation());
					jteOutput.writeContent("</p>\r\n                    ");
				}
				jteOutput.writeContent("\r\n                    <p><strong>Estimated Cost:</strong> $");
				jteOutput.setContext("p", null);
				jteOutput.writeUserContent(String.format("%,.0f", item.getAdjustedCost()));
				jteOutput.writeContent("</p>\r\n                </div>\r\n            ");
			}
			jteOutput.writeContent("\r\n        ");
		}
		jteOutput.writeContent("\r\n    </section>\r\n\r\n    ");
		if (!verdict.getPlan().getShouldDo().isEmpty()) {
			jteOutput.writeContent("\r\n        <section id=\"recommended-repairs\">\r\n            <h2>💡 Recommended Improvements</h2>\r\n            <div class=\"should-do\">\r\n                <ul>\r\n                    ");
			for (var item : verdict.getPlan().getShouldDo()) {
				jteOutput.writeContent("\r\n                        <li>");
				jteOutput.setContext("li", null);
				jteOutput.writeUserContent(item.getPrettyName());
				jteOutput.writeContent(" — $");
				jteOutput.setContext("li", null);
				jteOutput.writeUserContent(String.format("%,.0f", item.getAdjustedCost()));
				jteOutput.writeContent("</li>\r\n                    ");
			}
			jteOutput.writeContent("\r\n                </ul>\r\n            </div>\r\n        </section>\r\n    ");
		}
		jteOutput.writeContent("\r\n\r\n    <section id=\"cta\">\r\n        <h2>Get Your Custom Analysis</h2>\r\n        <p>This page shows costs for a typical $15,000 budget scenario. Get a personalized verdict for your exact budget and situation:</p>\r\n        <a href=\"/\" class=\"cta-button\">📊 Calculate Your Custom Verdict</a>\r\n    </section>\r\n\r\n    <section class=\"internal-links\">\r\n        <h3>🏘️ Explore Other Eras in ");
		jteOutput.setContext("h3", null);
		jteOutput.writeUserContent(metroName);
		jteOutput.writeContent("</h3>\r\n        ");
		for (var link : eraLinks) {
			jteOutput.writeContent("\r\n            <a");
			var __jte_html_attribute_3 = link.getHref();
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
				jteOutput.writeContent(" href=\"");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(__jte_html_attribute_3);
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\"");
			}
			jteOutput.writeContent(">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(link.getText());
			jteOutput.writeContent("</a>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        <h3 style=\"margin-top: 30px;\">🌆 Compare with Nearby Cities (");
		jteOutput.setContext("h3", null);
		jteOutput.writeUserContent(eraName);
		jteOutput.writeContent(")</h3>\r\n        ");
		for (var link : cityLinks) {
			jteOutput.writeContent("\r\n            <a");
			var __jte_html_attribute_4 = link.getHref();
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
				jteOutput.writeContent(" href=\"");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(__jte_html_attribute_4);
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\"");
			}
			jteOutput.writeContent(">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(link.getText());
			jteOutput.writeContent("</a>\r\n        ");
		}
		jteOutput.writeContent("\r\n    </section>\r\n\r\n    <footer>\r\n        <p><strong>Data Sources:</strong> 2026 RSMeans & U.S. Bureau of Labor Statistics Combined Data</p>\r\n        <p><strong>Last Updated:</strong> January 2026</p>\r\n        <p><strong>Disclaimer:</strong> Cost estimates are based on regional averages and historical data. Actual contractor quotes may vary based on specific property conditions, access, and market dynamics. This analysis is for informational purposes only and does not constitute professional advice.</p>\r\n    </footer>\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String metroCode = (String)params.get("metroCode");
		String metroName = (String)params.get("metroName");
		String era = (String)params.get("era");
		String eraName = (String)params.get("eraName");
		Verdict verdict = (Verdict)params.get("verdict");
		List<InternalLink> eraLinks = (List<InternalLink>)params.get("eraLinks");
		List<InternalLink> cityLinks = (List<InternalLink>)params.get("cityLinks");
		String baseUrl = (String)params.get("baseUrl");
		String canonicalUrl = (String)params.get("canonicalUrl");
		render(jteOutput, jteHtmlInterceptor, metroCode, metroName, era, eraName, verdict, eraLinks, cityLinks, baseUrl, canonicalUrl);
	}
}
