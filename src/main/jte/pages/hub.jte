@template.layout(
    title = "LifeVerdict - Independent Decision Tools",
    showHeader = false,
    content = @`
    <div class="max-w-3xl mx-auto px-6 py-20 text-center">
        <!-- 1. Hero Section -->
        <h1 class="text-4xl md:text-5xl font-bold text-gray-900 tracking-tight mb-4">
            LifeVerdict
        </h1>
        <p class="text-xl text-gray-600 font-light mb-16 leading-relaxed">
            Decision tools that help you avoid expensive life mistakes.
        </p>

        <!-- 2. Module Cards -->
        <div class="max-w-md mx-auto">
            <a href="/home-repair" class="block group bg-white rounded-2xl p-8 shadow-sm hover:shadow-md transition-all duration-300 border border-gray-100 hover:border-blue-100">
                <div class="flex items-center justify-center w-12 h-12 bg-blue-50 text-blue-600 rounded-full mx-auto mb-4 group-hover:scale-110 transition-transform duration-300">
                    <!-- Simple Home Icon -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                        <polyline points="9 22 9 12 15 12 15 22"/>
                    </svg>
                </div>
                <h2 class="text-xl font-semibold text-gray-900 mb-2 group-hover:text-blue-600 transition-colors">
                    Home Repair Verdict
                </h2>
                <p class="text-gray-500 leading-relaxed text-sm">
                    Decide whether a repair is worth it — before you spend thousands.
                </p>
                <div class="mt-6 text-blue-500 font-medium text-sm flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    Open Tool 
                    <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
                </div>
            </a>
        </div>

        <!-- 3. Trust Statement -->
        <div class="mt-20 pt-10 border-t border-gray-200">
            <p class="text-sm text-gray-400 font-medium tracking-wide uppercase">
                Independent Platform
            </p>
            <p class="text-gray-500 mt-2 text-sm">
                Independent decision tools. No contractors. No sales bias.
            </p>
        </div>
    </div>
    `
)
