@import java.util.List

@param List<String> metros
@param List<String> eras

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LifeVerdict | Property Intelligence</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/styles.css">
    <style>
        body { font-family: var(--font-body); background-color: #000; }
        .glass-panel {
            background: rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
        .apple-input {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(0, 0, 0, 0.05);
            transition: all 0.2s ease;
        }
        .apple-input:focus {
            background: #fff;
            transform: scale(1.01);
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .hero-grain {
            background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='0.05'/%3E%3C/svg%3E");
        }
    </style>
</head>
<body class="min-h-screen text-white overflow-x-hidden selection:bg-blue-500 selection:text-white">

    <div class="fixed inset-0 z-0">
        <div class="absolute inset-0 bg-black"></div>
        <div class="absolute top-[-20%] right-[-10%] w-[80vw] h-[80vw] bg-blue-600/30 rounded-full blur-[120px] mix-blend-screen opacity-50 animate-pulse" style="animation-duration: 10s;"></div>
        <div class="absolute bottom-[-20%] left-[-10%] w-[60vw] h-[60vw] bg-purple-600/20 rounded-full blur-[100px] mix-blend-screen opacity-40"></div>
        <div class="absolute inset-0 hero-grain pointer-events-none"></div>
    </div>

    <div class="relative z-10 flex flex-col md:flex-row min-h-screen">
        
        <%-- LEFT: BRAND --%>
        <div class="w-full md:w-1/2 p-8 md:p-16 flex flex-col justify-center">
            <div class="max-w-xl mx-auto md:mx-0">
                <div class="inline-flex items-center gap-2 mb-8 px-3 py-1 rounded-full glass-panel">
                    <span class="w-2 h-2 rounded-full bg-blue-500 animate-pulse"></span>
                    <span class="text-xs font-semibold tracking-wide uppercase text-white/80">Forensic-Grade Analysis</span>
                </div>
                
                <h1 class="text-5xl md:text-7xl font-bold tracking-tight leading-tight mb-6 bg-clip-text text-transparent bg-gradient-to-br from-white to-white/60">
                    The truth about <br>that house.
                </h1>
                
                <p class="text-xl md:text-2xl text-white/60 font-medium leading-relaxed max-w-md mb-10">
                    LifeVerdict reveals the hidden financial liabilities of any property using forensic data logic.
                </p>

                <div class="grid grid-cols-2 gap-6 opacity-80">
                    <div class="pl-4 border-l border-white/20">
                        <div class="text-2xl font-bold">200+</div>
                        <div class="text-sm text-white/50">Risk Factors</div>
                    </div>
                    <div class="pl-4 border-l border-white/20">
                        <div class="text-2xl font-bold">98%</div>
                        <div class="text-sm text-white/50">Accuracy Rate</div>
                    </div>
                </div>
            </div>
        </div>

        <%-- RIGHT: ACTION --%>
        <div class="w-full md:w-1/2 flex items-center justify-center p-6 md:p-12 backdrop-blur-sm bg-white/5 md:bg-transparent">
            <div class="w-full max-w-md bg-white rounded-[32px] shadow-2xl overflow-hidden p-8 md:p-10 text-slate-900 border border-white/20 relative">
                
                <div class="absolute top-0 inset-x-0 h-1 bg-gradient-to-r from-blue-500 via-purple-500 to-blue-500"></div>

                <div class="mb-8">
                    <h2 class="text-2xl font-bold text-slate-900 tracking-tight">Run Analysis</h2>
                    <p class="text-slate-500 mt-1">Select parameters to initialize the engine.</p>
                </div>

                <form action="/home-repair/step-2" method="POST" class="space-y-5">
                    
                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Location</label>
                        <div class="relative group">
                            <select name="metroCode" class="apple-input w-full appearance-none pl-4 pr-10 py-4 rounded-xl font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 cursor-pointer" required>
                                <option value="" disabled selected>Select Metro Area</option>
                                @for(String metro : metros)
                                    <option value="${metro}">${metro.replace("_", " ")}</option>
                                @endfor
                            </select>
                            <div class="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400">
                                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                            </div>
                        </div>
                    </div>

                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Era Built</label>
                         <div class="relative group">
                            <select name="era" class="apple-input w-full appearance-none pl-4 pr-10 py-4 rounded-xl font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 cursor-pointer" required>
                                <option value="" disabled selected>Construction Period</option>
                                @for(String era : eras)
                                     <option value="${era}">${era.replace("PRE_", "< ").replace("_PRESENT", "+").replace("_", "-")}</option>
                                @endfor
                            </select>
                            <div class="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400">
                                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                            </div>
                        </div>
                    </div>

                    <div>
                        <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Objective</label>
                         <div class="relative group">
                            <select name="relationship" class="apple-input w-full appearance-none pl-4 pr-10 py-4 rounded-xl font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500/20 cursor-pointer" required>
                                <option value="" disabled selected>Your Goal</option>
                                <option value="LIVING">Owner (Living)</option>
                                <option value="BUYING">Buyer (Inspection)</option>
                                <option value="INVESTING">Investor (Flip/Rent)</option>
                            </select>
                            <div class="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400">
                                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                            </div>
                        </div>
                    </div>

                    <button type="submit" class="w-full bg-slate-900 hover:bg-black text-white font-bold py-4 rounded-xl shadow-lg hover:shadow-2xl hover:scale-[1.02] transition-all duration-300 mt-6 flex items-center justify-center gap-2">
                        Start Engine
                        <svg class="w-4 h-4 text-white/70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
                    </button>

                </form>
            </div>
        </div>
    </div>
</body>
</html>
