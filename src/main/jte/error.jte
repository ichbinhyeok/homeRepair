@import gg.jte.Content
@param String errorMessage = ""
@param String stackTrace = ""

@template.layout(content = @`
    <div class="flex flex-col items-center justify-center min-h-[50vh] text-center p-4">
        <h1 class="text-4xl font-bold text-gray-800 mb-4">Rebuilding...</h1>
        <p class="text-gray-600 mb-8">We encountered a temporary issue generating your report.</p>
        @if(!errorMessage.isEmpty())
             <!-- Development Mode Error Display: Visible for debugging, can be hidden in production -->
            <div class="bg-yellow-50 border border-yellow-200 text-yellow-800 px-4 py-3 rounded relative mb-4 text-sm max-w-xl mx-auto">
                <span class="block sm:inline">${errorMessage}</span>
            </div>
        @endif
        <a href="/home-repair" class="px-6 py-3 bg-slate-900 text-white font-bold rounded hover:bg-slate-800 transition">
            Start Fresh Calculation
        </a>
    </div>
`)
