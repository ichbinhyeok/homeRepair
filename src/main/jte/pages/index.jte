@import java.util.List

@param List<String> metros
@param List<String> eras

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LivingCostCheck | Real Estate Risk Analysis</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;800&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; }
    </style>
</head>
<body class="bg-white min-h-screen flex flex-col md:flex-row">

    <%-- LEFT PANEL: BRANDING & VALUE PROP --%>
    <div class="w-full md:w-1/2 lg:w-5/12 bg-slate-900 text-white p-8 md:p-12 flex flex-col justify-between relative overflow-hidden">
        <div class="absolute inset-0 opacity-10" style="background-image: radial-gradient(#ffffff 1px, transparent 1px); background-size: 24px 24px;"></div>
        <div class="absolute bottom-0 right-0 w-64 h-64 bg-blue-600 rounded-full blur-[100px] opacity-20 -mr-16 -mb-16"></div>
        
        <div class="relative z-10">
            <h1 class="text-2xl font-bold flex items-center gap-2 mb-8">
                <span>🏠</span> LivingCost<span class="text-blue-400">Check</span>
            </h1>
            <h2 class="text-4xl md:text-5xl font-black tracking-tight leading-tight mb-6">
                Don't buy a <span class="text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">Money Pit.</span>
            </h2>
            <p class="text-slate-400 text-lg font-medium leading-relaxed mb-8">
                The only forensic-grade verification tool for unexpected home repair costs. 
                <br><br>
                We analyze 200+ historical data points to expose the hidden financial liabilities of any property.
            </p>
        </div>

        <div class="relative z-10 space-y-6">
             <div class="flex items-center gap-4 p-4 bg-white/5 rounded-xl border border-white/10 backdrop-blur-sm">
                <div class="text-2xl">🛡️</div>
                <div>
                    <h3 class="font-bold text-white text-sm">Forensic Context Logic</h3>
                    <p class="text-slate-400 text-xs">Era-specific risk detection (e.g. 1920s Knob & Tube)</p>
                </div>
            </div>
             <div class="flex items-center gap-4 p-4 bg-white/5 rounded-xl border border-white/10 backdrop-blur-sm">
                <div class="text-2xl">📉</div>
                <div>
                    <h3 class="font-bold text-white text-sm">Negotiation Leverage</h3>
                    <p class="text-slate-400 text-xs">Get a licensed-contractor grade estimate to negotiate price.</p>
                </div>
            </div>
        </div>
        
        <div class="mt-8 text-xs text-slate-500 font-mono">
            v2026.1.2-BETA
        </div>
    </div>

    <%-- RIGHT PANEL: INPUT FORM --%>
    <div class="w-full md:w-1/2 lg:w-7/12 bg-slate-50 flex items-center justify-center p-6 md:p-12 overflow-y-auto">
        <div class="max-w-md w-full">
            
            <div class="mb-8 md:hidden">
                 <h2 class="text-2xl font-black text-slate-900 tracking-tight">Start Analysis</h2>
            </div>

            <form action="/home-repair/step-2" method="POST" class="space-y-6">
                
                <%-- STEP INDICATOR --%>
                <div class="flex items-center gap-2 mb-8">
                    <span class="bg-slate-900 text-white w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">1</span>
                    <span class="text-xs font-bold text-slate-500 uppercase tracking-widest">Property Basics</span>
                    <div class="flex-1 h-px bg-slate-200"></div>
                     <span class="bg-slate-200 text-slate-400 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">2</span>
                     <span class="bg-slate-200 text-slate-400 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">3</span>
                </div>

                <div class="space-y-6 bg-white p-8 rounded-2xl shadow-sm border border-slate-200">
                    
                    <%-- METRO AREA --%>
                    <div>
                        <label for="metroCode" class="block text-sm font-bold text-slate-700 mb-2">Metro Area</label>
                        <select id="metroCode" name="metroCode" class="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all bg-white" required>
                            <option value="" disabled selected>Select Property Location...</option>
                            @for(String metro : metros)
                                <option value="${metro}">${metro.replace("_", " ")}</option>
                            @endfor
                        </select>
                    </div>

                    <%-- ERA --%>
                    <div>
                        <label for="era" class="block text-sm font-bold text-slate-700 mb-2">Year Built (Era)</label>
                        <select id="era" name="era" class="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all bg-white" required>
                             <option value="" disabled selected>Select Construction Era...</option>
                            @for(String era : eras)
                                <option value="${era}">${era.replace("PRE_", "< ").replace("_PRESENT", "+").replace("_", "-")}</option>
                            @endfor
                        </select>
                        <p class="text-[10px] text-slate-400 mt-2 ml-1">
                            * We use eras to determine likely building materials and code standards.
                        </p>
                    </div>

                    <%-- RELATIONSHIP --%>
                    <div>
                        <label for="relationship" class="block text-sm font-bold text-slate-700 mb-2">My Goal</label>
                        <select id="relationship" name="relationship" class="w-full px-4 py-3 rounded-lg border border-slate-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all bg-white" required>
                            <option value="" disabled selected>Why are you here?</option>
                            <option value="LIVING">Living Here (Owner)</option>
                            <option value="BUYING">Buying (Inspection)</option>
                            <option value="INVESTING">Investing (Flip/Rent)</option>
                        </select>
                    </div>

                    <button type="submit" class="w-full bg-slate-900 hover:bg-slate-800 text-white font-bold py-4 rounded-xl shadow-lg hover:shadow-xl transition-all transform active:scale-[0.98] mt-4 flex items-center justify-center gap-2 group">
                        <span>Continue to Forensic Details</span>
                        <span class="group-hover:translate-x-1 transition-transform">→</span>
                    </button>

                </div>
                
                <p class="text-center text-xs text-slate-400 mt-6">
                    By continuing, you agree to our Terms of Service. 
                    <br>This tool provides estimates, not guarantees.
                </p>

            </form>
        </div>
    </div>

</body>
</html>
