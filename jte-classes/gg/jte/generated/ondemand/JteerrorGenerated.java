package gg.jte.generated.ondemand;
import gg.jte.Content;
public final class JteerrorGenerated {
	public static final String JTE_NAME = "error.jte";
	public static final int[] JTE_LINE_INFO = {0,0,2,2,2,2,2,2,2,10,10,10,11,11,11,11,11,11};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.JtelayoutGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"flex flex-col items-center justify-center min-h-[50vh] text-center p-4\">\r\n        <h1 class=\"text-4xl font-bold text-gray-800 mb-4\">Oops!</h1>\r\n        <p class=\"text-gray-600 mb-8\">Something went wrong. Don't worry, it's not your fault.</p>\r\n        <a href=\"/home-repair\" class=\"px-6 py-3 bg-blue-600 text-white font-bold rounded hover:bg-blue-700 transition\">\r\n            Go Back Home\r\n        </a>\r\n    </div>\r\n");
			}
		});
		jteOutput.writeContent("\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		render(jteOutput, jteHtmlInterceptor);
	}
}
