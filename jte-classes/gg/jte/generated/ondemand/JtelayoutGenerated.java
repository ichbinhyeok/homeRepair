package gg.jte.generated.ondemand;
import gg.jte.Content;
public final class JtelayoutGenerated {
	public static final String JTE_NAME = "layout.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,11,11,17,23,23,23,32,32,32,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Content content) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"en\" class=\"bg-gray-100\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>Home Repair Verdict</title>\r\n    <script src=\"https://cdn.tailwindcss.com\"></script>\r\n    <style>\r\n        ");
		jteOutput.writeContent("\r\n        body { font-family: 'Inter', sans-serif; }\r\n    </style>\r\n</head>\r\n<body class=\"antialiased min-h-screen flex flex-col items-center justify-center p-4\">\r\n\r\n    ");
		jteOutput.writeContent("\r\n    <div class=\"w-full max-w-4xl mx-auto mb-4 bg-gray-200 h-24 flex items-center justify-center text-gray-400 text-sm border-2 border-dashed border-gray-300\">\r\n        AD_SLOT_TOP (Placeholder)\r\n    </div>\r\n\r\n    <main class=\"w-full max-w-md bg-white rounded-xl shadow-2xl overflow-hidden border border-gray-100\">\r\n        ");
		jteOutput.setContext("main", null);
		jteOutput.writeUserContent(content);
		jteOutput.writeContent("\r\n    </main>\r\n\r\n    <footer class=\"mt-8 text-center text-gray-400 text-xs\">\r\n        <p>&copy; 2025 LivingCostCheck.com</p>\r\n    </footer>\r\n\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Content content = (Content)params.get("content");
		render(jteOutput, jteHtmlInterceptor, content);
	}
}
